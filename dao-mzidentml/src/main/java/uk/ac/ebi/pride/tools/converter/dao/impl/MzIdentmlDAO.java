package uk.ac.ebi.pride.tools.converter.dao.impl;

import org.apache.log4j.Logger;
import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.mzidml.*;
import uk.ac.ebi.jmzidml.xml.io.MzIdentMLUnmarshaller;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.dao.DAO;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.dao.DAOProperty;
import uk.ac.ebi.pride.tools.converter.dao.Utils;
import uk.ac.ebi.pride.tools.converter.dao.handler.QuantitationCvParams;
import uk.ac.ebi.pride.tools.converter.report.model.*;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.Peptide;
import uk.ac.ebi.pride.tools.converter.report.model.SourceFile;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.FileUtils;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Converts an mzIdentML file to PRIDE XML. For this
 * conversion only the Identification data in
 * /SequenceCollection is taken into consideration.
 *
 * @author jg
 */
public class MzIdentmlDAO extends AbstractDAOImpl implements DAO {
    /**
     * The id format used in the spectrum file.
     *
     * @author jg
     */
    private enum SpecIdFormat {
        MASCOT_QUERY_NUM, MULTI_PEAK_LIST_NATIVE_ID, SINGLE_PEAK_LIST_NATIVE_ID, MZML_ID, NONE
    }

    ;

    /**
     * An enum of the supported spectra file types
     *
     * @author jg
     */
    private enum SpecFileFormat {
        MZML, PKL, DTA, MGF, NONE
    }

    ;
    /**
     * Id separator used to combine ids in a HashMap.
     */
    private final String ID_SEPARATOR = "!|!";
    /**
     * The spec id format used in the source file(s).
     */
    private SpecIdFormat specIdFormat;
    /**
     * The fileformat the source file(s) are in.
     */
    private SpecFileFormat specFileFormat;
    /**
     * A HashMap holding all spectraFiles referenced in this mzIdentML
     * file with their id as key. All of these files must be in the
     * same file format.
     */
    private HashMap<String, File> spectraFiles = new HashMap<String, File>();
    /**
     * HashMap that contains the created parser objects for all spectra
     * files. The objects are stored by their id. The type of the object
     * is defined through the SpecFileFormat.
     */
    private HashMap<String, AbstractPeakListDAO> spectraDAOs = new HashMap<String, AbstractPeakListDAO>();
    /**
     * An array list storing the specFileIds. This is required to calculate
     * the (PRIDE) index of every spectrum.
     */
    private ArrayList<String> specFileIds = new ArrayList<String>();
    /**
     * A logger instance.
     */
    private static final Logger logger = Logger.getLogger(MzIdentmlDAO.class);
    /**
     * The MzIdentMLUnsmarshaller used to parse the mzIdentML file.
     */
    private MzIdentMLUnmarshaller unmarshaller;
    /**
     * The sourcefile.
     */
    private File sourcefile;
    /**
     * The cvs used in the parsed mzIdentML file with
     * their id as key.
     */
    private HashMap<String, Cv> cvs = new HashMap<String, Cv>();
    /**
     * The currently set properties.
     */
    private Properties properties = new Properties();
    /**
     * Stores the referenced spectra ids.
     */
    private HashSet<String> referencedSpectraIds = new HashSet<String>();
    /**
     * Mapping between the spectrum id and its index. The spectrum id
     * is create in the format [spectraDataId]_[spectrumId]
     */
    private HashMap<String, Integer> spectrumIndexMapping;
    /**
     * Mapping between the DbSequences and their associated
     * SpectrumIdentificationItems + PeptideEvidence items.
     * The value is the SII's id + ID_SEPARATOR + PE's id.
     */
    private HashMap<String, ArrayList<String>> proteinSIIMapping;
    /**
     * Mapping between the spectrum identification items (id used as key) and the
     * spectra ids (fileId_specId) they are created from.
     */
    private HashMap<String, String> sIISpecIdMapping;
    /**
     * Stores of how many proteins a peptide is part from. The peptide's
     * sequence is used as key and the DBSequence ids as values of the
     * HashSet.
     */
    private HashMap<String, HashSet<String>> peptideProteinMapping;
    /**
     * HashSet holding all accessions for fixed modifications.
     */
    private HashSet<String> fixedModifications;
    /**
     * The measure refs describing the product ion intensity.
     * Null if not available. (Value is read in in prescan file)
     */
    private HashSet<String> productIonIntensityMeasureRef = new HashSet<String>();
    /**
     * The measure refs describing the product ion intensity.
     * Null if not available. (Value is read in in prescan file)
     */
    private HashSet<String> productIonMZMeasureRef = new HashSet<String>();
    /**
     * The measure refs describing the product ion m/z delta.
     * Null if not available. (Value is read in in prescan file)
     */
    private HashSet<String> productIonMZDeltaMeasureRef = new HashSet<String>();
    /**
     * The measure refs describing the product ion m/z error.
     * Null if not available. (Value is read in in prescan file)
     */
    private HashSet<String> productIonMZErrorMeasureRef = new HashSet<String>();
    /**
     * The analysis protocol collection of the mzIdentML file.
     * This is used for creating identifications thus unmarshalling
     * for every time would be rather inefficient.
     */
    private AnalysisProtocolCollection analysisProtocolCollection;
    /**
     * Name of the software used to identify the proteins. Search results
     * from multiple softwares cannot be represented properply as PRIDE
     * XML files only support one software element.
     */
    private String proteinSoftwareName;
    /**
     * The accession of the used peptide score. This parameter is
     * used to make sure that the protein scores are all constructed
     * from the same peptide score type. If multiple types are available
     * one is randomly chosen.
     */
    private String usedPeptideScore;
    /**
     * The various configuration options supported by the DAO together
     * with their default values.
     */
    private boolean allowIdentificationsOnly = false;
    private boolean useWeightedScoring = true;
    private boolean reportAllSpectrumIdentificationItems = false;
    private String decoyAccessionPrecursor;
    /**
     * Directory where the spectra files can be found.
     */
    private String specFileDirectory;

    /**
     * Creates a new MzIdentmlDAO based on the passed
     * mzIdentML file.
     *
     * @param sourcefile The mzIdentML file to process.
     * @throws InvalidFormatException
     */
    public MzIdentmlDAO(File sourcefile) throws InvalidFormatException {
        this.sourcefile = sourcefile;

        // create the unmarshaller
        unmarshaller = new MzIdentMLUnmarshaller(sourcefile);

        // check the mzIdentML version
        if (!unmarshaller.getMzIdentMLVersion().equals("1.1.0"))
            throw new InvalidFormatException("mzIdentML version " + unmarshaller.getMzIdentMLVersion() + " is not supported.");

        // parse the spectra files
        parseSpectraFiles();

        // parse the used cvs
        parseCvs();

        // parsed the referenced spec ids
        prescanFile();
    }

    /**
     * Parses the spectra files. Checks the id formats as well
     * as the file formats. If multiple spectra files are used, all
     * of these files must be in the same format and use
     * the same id format.
     *
     * @throws InvalidFormatException In case there are different file types provided or different id formats used.
     */
    private void parseSpectraFiles() throws InvalidFormatException {
        // check the spectra files
        Iterator<SpectraData> spectraDataIt = unmarshaller.unmarshalCollectionFromXpath(MzIdentMLElement.SpectraData);

        while (spectraDataIt.hasNext()) {
            SpectraData specData = spectraDataIt.next();

            // get the id format
            uk.ac.ebi.jmzidml.model.mzidml.CvParam specIdParam = specData.getSpectrumIDFormat().getCvParam();

            // if the spec id format wasn't saved before, save it, otherwise check it's the same as before
            if (specIdFormat == null)
                specIdFormat = getSpecFileType(specIdParam);
            else {
                if (specIdFormat != getSpecFileType(specIdParam))
                    throw new InvalidFormatException("Different spectra id formats used in the spectra files.");
            }

            // get the file format
            uk.ac.ebi.jmzidml.model.mzidml.CvParam fileFormatParam = specData.getFileFormat().getCvParam();

            if (fileFormatParam == null)
                throw new InvalidFormatException("Missing file format specification for spectra data.");

            if (specFileFormat == null)
                specFileFormat = getSpecFileFormat(fileFormatParam);
            else {
                if (specFileFormat != getSpecFileFormat(fileFormatParam))
                    throw new InvalidFormatException("Different file formats passed as spectra files.");
            }

            // create the file object based on the location
            String location = specData.getLocation();
            spectraFiles.put(specData.getId(), new File(location));
        }

        // save the spec file ids
        specFileIds.clear();
        specFileIds.addAll(spectraFiles.keySet());
    }

    /**
     * "Translates" the SpectrumIDFormat cvParam into a SpecIdFormat.
     *
     * @param specIdFormat A cvParam specifying the spec id format.
     * @return The associated SpecIdFormat
     * @throws InvalidFormatException
     * @throws InvalidFormatException Thrown in case the spectrum id format is not supported.
     */
    private SpecIdFormat getSpecFileType(uk.ac.ebi.jmzidml.model.mzidml.CvParam specIdFormat) throws InvalidFormatException {
        if (specIdFormat.getAccession().equals("MS:1001528"))
            return SpecIdFormat.MASCOT_QUERY_NUM;
        if (specIdFormat.getAccession().equals("MS:1000774"))
            return SpecIdFormat.MULTI_PEAK_LIST_NATIVE_ID;
        if (specIdFormat.getAccession().equals("MS:1000775"))
            return SpecIdFormat.SINGLE_PEAK_LIST_NATIVE_ID;
        if (specIdFormat.getAccession().equals("MS:1001530"))
            return SpecIdFormat.MZML_ID;

        if (allowIdentificationsOnly)
            return SpecIdFormat.NONE;
        else
            throw new InvalidFormatException("Unsupported spectrum identifier format used (" + specIdFormat.getAccession() + ": " + specIdFormat.getName() + ")");
    }

    /**
     * "Translates" the SpectrumFileFormat into a SpecFileFormat.
     *
     * @param specFileFormat A cvParam specifying the spectrum file format.
     * @return The corresponding SpecFileFormat
     * @throws InvalidFormatException
     * @throws InvalidFormatException In case the format is not recognized / supported.
     */
    private SpecFileFormat getSpecFileFormat(uk.ac.ebi.jmzidml.model.mzidml.CvParam specFileFormat) throws InvalidFormatException {
        if (specFileFormat.getAccession().equals("MS:1000613"))
            return SpecFileFormat.DTA;
        if (specFileFormat.getAccession().equals("MS:1001062"))
            return SpecFileFormat.MGF;
        if (specFileFormat.getAccession().equals("MS:1000565"))
            return SpecFileFormat.PKL;
        if (specFileFormat.getAccession().equals("MS:1000584"))
            return SpecFileFormat.MZML;

        if (allowIdentificationsOnly)
            return SpecFileFormat.NONE;
        else
            throw new InvalidFormatException("Unsupported spectrum file format used (" + specFileFormat.getAccession() + ": " + specFileFormat.getName() + ")");
    }

    /**
     * Prases the used cvs and stores them in the
     * cvs HashMap.
     */
    private void parseCvs() {
        // get the used cvs
        Iterator<Cv> cvIt = unmarshaller.unmarshalCollectionFromXpath(MzIdentMLElement.CV);

        while (cvIt.hasNext()) {
            Cv cv = cvIt.next();

            cvs.put(cv.getId(), cv);
        }
    }

    /**
     * Tries to locate the passed spectra file in various ways:
     * First, the original path is checked, then the file is searched
     * in the current directory at last the directory the mzid file
     * is in is checked.
     *
     * @param specFile The spectra file to find.
     * @return The found spectra file as a File object or null if allowIdentificationsOnly is set to true.
     * @throws InvalidFormatException
     * @throws InvalidFormatException Thrown if the file could not be found.
     */
    private File getExistingSpecFile(File specFile) throws InvalidFormatException {
        // if the file exists, just return it
        if (specFile.exists())
            return specFile;
        // if the spec file directory is set, check there
        if (specFileDirectory != null && specFileDirectory.length() > 0) {
            File specFileDir = new File(specFileDirectory);
            if (!specFileDir.isDirectory())
                throw new InvalidFormatException("The spectrum location must only be specified as a directory. The original filenames must not be changed.");

            specFile = new File(specFileDir.getAbsolutePath() + File.separator + specFile.getName());
            if (specFile.exists())
                return specFile;
        }
        // if it doesn't exist check in the current directory
        specFile = new File(specFile.getName());
        if (specFile.exists())
            return specFile;
        // if it doesn't exist in the current directory, check in the directory of the mzid file
        specFile = new File(sourcefile.getParentFile().getAbsolutePath() + File.separator + specFile.getName());
        if (specFile.exists())
            return specFile;
        // if the file doesn't exist there, give up
        if (allowIdentificationsOnly) {
            logger.warn("Spectra file " + specFile.getName() + " does not exist.");
            return null;
        } else
            throw new InvalidFormatException("Spectra file " + specFile.getName() + " does not exist.");
    }

    /**
     * Returns the appropriate DAO for the specified spectra file.
     * In case the spectra file does not exist and allowOnlyIdentifications
     * is true null is returned.
     *
     * @param specFileId The spectra file's id.
     * @return The respective DAO - null in case the spectra file cannot be found.
     * @throws InvalidFormatException
     */
    private AbstractPeakListDAO getSpectraDAO(String specFileId) throws InvalidFormatException {
        // make sure the right file format is set
        if (specFileFormat == SpecFileFormat.NONE)
            throw new InvalidFormatException("Unknown spectrum file format.");

        // make sure the spec file exists
        if (!spectraFiles.containsKey(specFileId))
            throw new InvalidFormatException("Spectra file " + specFileId + " is not defined in the mzIdentML file.");

        // check if the dao already exists
        if (spectraDAOs.containsKey(specFileId))
            return spectraDAOs.get(specFileId);

        // create the spec file object - this automatically checks whether the file exists
        File specFile = spectraFiles.get(specFileId);
        specFile = getExistingSpecFile(specFile);

        if (specFile == null)
            return null;

        AbstractPeakListDAO dao = null;

        // create the DAO
        switch (specFileFormat) {
            case MZML:
                dao = new MzmlDAO(specFile);
                break;
            case MGF:
                dao = new MgfDAO(specFile);
                break;
            case DTA:
                dao = new DtaDAO(specFile);
                break;
            case PKL:
                dao = new PklDAO(specFile);
                break;
        }

        spectraDAOs.put(specFileId, dao);

        return dao;
    }

    /**
     * Parses all SpectrumIdentificationItems and creates the
     * referencedSpectraIds HashSet, the sIIspecIdMapping and
     * the proteinSIIMapping HashMaps.
     *
     * @throws InvalidFormatException
     */
    private void prescanFile() throws InvalidFormatException {
        referencedSpectraIds.clear();
        sIISpecIdMapping = new HashMap<String, String>();
        proteinSIIMapping = new HashMap<String, ArrayList<String>>();
        productIonIntensityMeasureRef = new HashSet<String>();
        productIonMZMeasureRef = new HashSet<String>();

        // iterator over all spectrum identifications lists
        Iterator<SpectrumIdentificationResult> itSir = unmarshaller.unmarshalCollectionFromXpath(MzIdentMLElement.SpectrumIdentificationResult);

        while (itSir.hasNext()) {
            SpectrumIdentificationResult sir = itSir.next();

            referencedSpectraIds.add(sir.getSpectraDataRef() + "_" + sir.getSpectrumID());

            for (SpectrumIdentificationItem sii : sir.getSpectrumIdentificationItem()) {
                // ignore non-ranked one SIIs if set
                if (!reportAllSpectrumIdentificationItems && sii.getRank() != 1)
                    continue;

                // add the sII to spectrum mapping
                sIISpecIdMapping.put(sii.getId(), sir.getSpectraDataRef() + "_" + sir.getSpectrumID());

                // add the protein mapping
                for (PeptideEvidenceRef peRef : sii.getPeptideEvidenceRef()) {
                    PeptideEvidence pe = peRef.getPeptideEvidence();

                    if (pe == null)
                        throw new InvalidFormatException("Failed to unmarshall PeptideEvidence object " + peRef.getPeptideEvidenceRef());

                    // save the protein
                    if (!proteinSIIMapping.containsKey(pe.getDBSequenceRef()))
                        proteinSIIMapping.put(pe.getDBSequenceRef(), new ArrayList<String>());

                    proteinSIIMapping.get(pe.getDBSequenceRef()).add(sii.getId() + ID_SEPARATOR + pe.getId());
                }
            }
        }

        // check the fragementation table and get the measure references for the various
        // fragment ion arrays
        Iterator<FragmentationTable> itFragTab = unmarshaller.unmarshalCollectionFromXpath(MzIdentMLElement.FragmentationTable);

        while (itFragTab.hasNext()) {
            FragmentationTable fragTab = itFragTab.next();

            for (Measure measure : fragTab.getMeasure()) {
                for (uk.ac.ebi.jmzidml.model.mzidml.CvParam param : measure.getCvParam()) {
                    if (param.getAccession().equals("MS:1001225")) {
                        productIonMZMeasureRef.add(measure.getId());
                        break;
                    }
                    if (param.getAccession().equals("MS:1001226")) {
                        productIonIntensityMeasureRef.add(measure.getId());
                        break;
                    }
                    if (param.getAccession().equals("MS:1000904")) {
                        productIonMZDeltaMeasureRef.add(measure.getId());
                        break;
                    }
                    if (param.getAccession().equals("MS:1001227")) {
                        productIonMZErrorMeasureRef.add(measure.getId());
                        break;
                    }
                }
            }
        }

        // create the peptide to protein mapping
        peptideProteinMapping = new HashMap<String, HashSet<String>>();

        Iterator<PeptideEvidence> itPE = unmarshaller.unmarshalCollectionFromXpath(MzIdentMLElement.PeptideEvidence);

        while (itPE.hasNext()) {
            PeptideEvidence pe = itPE.next();

            // get the sequence
            try {
                uk.ac.ebi.jmzidml.model.mzidml.Peptide peptide = unmarshaller.unmarshal(uk.ac.ebi.jmzidml.model.mzidml.Peptide.class, pe.getPeptideRef());

                // make sure the peptide already exists
                if (!peptideProteinMapping.containsKey(peptide.getPeptideSequence()))
                    peptideProteinMapping.put(peptide.getPeptideSequence(), new HashSet<String>());

                // save the DbSequence id
                peptideProteinMapping.get(peptide.getPeptideSequence()).add(pe.getDBSequenceRef());
            } catch (JAXBException e) {
                throw new InvalidFormatException("Failed to load object from mzIdentML file.", e);
            }

        }
    }

    /**
     * Creates the spectrum id to index mappings and
     * stores the results in the spectrumIndexMapping
     * HashMap.
     * In case the HashMap was already created the function
     * returns.
     *
     * @throws InvalidFormatException
     */
    private void createSpectraIdIndexes() throws InvalidFormatException {
        // only create a new index if it hasn't been created yet
        if (spectrumIndexMapping != null)
            return;

        spectrumIndexMapping = new HashMap<String, Integer>();

        // process the peak list files
        if (specIdFormat == SpecIdFormat.MULTI_PEAK_LIST_NATIVE_ID) {
            int mzDataIndex = 1;

            // loop through all files
            for (String fileId : specFileIds) {
                // get the number of spectra
                AbstractPeakListDAO dao = getSpectraDAO(fileId);

                if (dao == null)
                    continue;

                int nSpecCount = dao.getSpectrumCount(false);
                // create the ids
                for (Integer localIndex = 0; localIndex < nSpecCount; localIndex++) {
                    String localSpecId = getSpectrumFileId(fileId, localIndex.toString());
                    spectrumIndexMapping.put(localSpecId, mzDataIndex++);
                }
            }
        }

        // if files are supplied, the stored specFiles should point to directories.
        if (specIdFormat == SpecIdFormat.SINGLE_PEAK_LIST_NATIVE_ID) {
            int mzDataIndex = 1;
            // create the required file filter
            FilenameFilter filenameFilter = null;

            if (specFileFormat == SpecFileFormat.DTA)
                filenameFilter = new DtaFilenameFilter();
            else if (specFileFormat == SpecFileFormat.PKL)
                filenameFilter = new PklFilenameFilter();
            else
                throw new InvalidFormatException("File ids as spectrum references are only supported for PKL and DTA.");

            // loop through all specFiles (actually directories)
            for (String fileId : specFileIds) {
                // get the file object
                File specFile = getExistingSpecFile(spectraFiles.get(fileId));

                if (specFile == null)
                    continue;

                // make sure it's a directory
                if (!specFile.isDirectory())
                    throw new InvalidFormatException("Spectrum file path does not point to a directory even though \"single peak list nativeID format\" was specified.");

                String[] fileNames = specFile.list(filenameFilter);

                // create the indexes
                for (String filename : fileNames) {
                    String localSpecId = getSpectrumFileId(fileId, filename);
                    spectrumIndexMapping.put(localSpecId, mzDataIndex++);
                }
            }

        }

        // process the mgf index
        if (specIdFormat == SpecIdFormat.MASCOT_QUERY_NUM) {
            int mzDataIndex = 1;

            // loop through all files
            for (String fileId : specFileIds) {
                // get the number of spectra
                AbstractPeakListDAO dao = getSpectraDAO(fileId);

                if (dao == null)
                    continue;

                int nSpecCount = dao.getSpectrumCount(false);
                // create the ids
                for (Integer localIndex = 1; localIndex <= nSpecCount; localIndex++) {
                    String localSpecId = getSpectrumFileId(fileId, new Integer(localIndex - 1).toString());
                    spectrumIndexMapping.put(localSpecId, mzDataIndex++);
                }
            }
        }

        // process the mzIdentML files
        if (specIdFormat == SpecIdFormat.MZML_ID) {
            int mzDataIndex = 1;

            // loop through all files
            for (String fileId : specFileIds) {
                // get the file object
                File specFile = getExistingSpecFile(spectraFiles.get(fileId));
                // if the file doesn't exist, ignore it
                if (specFile == null)
                    continue;

                // create a mzML unmarshaller
                MzMLUnmarshaller mzmlUnm = new MzMLUnmarshaller(specFile);
                Set<String> specIds = mzmlUnm.getSpectrumIDs();

                for (String specId : specIds) {
                    String localSpecId = getSpectrumFileId(fileId, specId);
                    spectrumIndexMapping.put(localSpecId, mzDataIndex++);
                }
            }
        }
    }

    /**
     * Returns the used spectrum id as it's used in spectrumId
     * as it's used in the spectrumIndexMapping and
     * referencedSpectraIds.
     *
     * @param fileId  The spectrum file's id.
     * @param localId The local id (either the 0-based index of the spectrum in the file, the spectrum file name or the mzML id).
     * @return The spectrum id.
     * @throws InvalidFormatException
     */
    private String getSpectrumFileId(String fileId, String localId) throws InvalidFormatException {
        switch (specIdFormat) {
            case MULTI_PEAK_LIST_NATIVE_ID:
                return fileId + "_index=" + localId;
            // mascot uses 1-based query numbers
            case MASCOT_QUERY_NUM:
                return fileId + "_query=" + (Integer.parseInt(localId) + 1);
            case SINGLE_PEAK_LIST_NATIVE_ID:
                return fileId + "_file=" + localId;
            case MZML_ID:
                return fileId + "_mzMLid=" + localId;
        }

        throw new InvalidFormatException("Unsupported spectrum id format encountered.");
    }

    public class DtaFilenameFilter implements FilenameFilter {

        public boolean accept(File dir, String name) {
            return name.endsWith(".dta");
        }
    }

    public class PklFilenameFilter implements FilenameFilter {

        public boolean accept(File dir, String name) {
            return name.endsWith(".pkl");
        }
    }

    @SuppressWarnings("rawtypes")
    public static Collection<DAOProperty> getSupportedProperties() {
        ArrayList<DAOProperty> supportedProperties = new ArrayList<DAOProperty>();

        // add the allow-identifications-only option
        DAOProperty<Boolean> allowIdentificationsOnly = new DAOProperty<Boolean>("allow_identifications_only", false);
        allowIdentificationsOnly.setDescription("Allows the conversion of mzIdentML files without spectra data. Can be set to \"True\" or \"False\". The default value is \"False\".");
        allowIdentificationsOnly.setShortDescription("Allow the conversion without spectra data (not recommended).");
        allowIdentificationsOnly.setAdvanced(true);
        supportedProperties.add(allowIdentificationsOnly);

        // add the use weighted score option
        DAOProperty<Boolean> useWeightedScoring = new DAOProperty<Boolean>("use_weighted_scoring", true);
        useWeightedScoring.setDescription("As protein scores are not directly reported in mzIdentML files two different methods can be used to calculate protein from peptide scores: Either a weighted approach (default) or just adding up the peptide scores.");
        useWeightedScoring.setShortDescription("Use a weighted scoring approach to estimate protein scores.");
        useWeightedScoring.setAdvanced(true);
        supportedProperties.add(useWeightedScoring);

        // add the report all spectrum identificationfalses
        DAOProperty<Boolean> reportAllSpectrumIdentifications = new DAOProperty<Boolean>("report_all_spectrum_identifications", false);
        reportAllSpectrumIdentifications.setDescription("By default only ranked one spectrum identifications are reported. If this option is set to true all spectrum identifications irrespective of their rank are reported.");
        reportAllSpectrumIdentifications.setShortDescription("Report more than one identification per spectrum.");
        reportAllSpectrumIdentifications.setAdvanced(true);
        supportedProperties.add(reportAllSpectrumIdentifications);

        // decoy accession precursor
        DAOProperty<String> decoyAccessionPrecuros = new DAOProperty<String>("decoy_accession_precursor", null);
        decoyAccessionPrecuros.setDescription("If set any identification with the specified precursor in its identification will be flagged as decoy identification.");
        decoyAccessionPrecuros.setShortDescription("Protein accession prefix identifying decoy hits.");
        supportedProperties.add(decoyAccessionPrecuros);

        // used peptide score accession
        DAOProperty<String> peptideScoreAccession = new DAOProperty<String>("peptide_score_accession", null);
        peptideScoreAccession.setDescription("Specifies the accession of the peptide score to use to construct the protein scores. If this parameter is not set one of the available peptide scores will be randomly chosen.");
        peptideScoreAccession.setShortDescription("Accession of the primary peptide score to be reported.");
        peptideScoreAccession.setAdvanced(true);
        supportedProperties.add(peptideScoreAccession);

        return supportedProperties;
    }

    @Override
    public void setConfiguration(Properties props) {
        properties = props;

        allowIdentificationsOnly = Boolean.parseBoolean(properties.getProperty("allow_identifications_only", "false"));
        useWeightedScoring = Boolean.parseBoolean(properties.getProperty("use_weighted_scoring", "true"));
        reportAllSpectrumIdentificationItems = Boolean.parseBoolean(properties.getProperty("report_all_spectrum_identifications", "false"));
        decoyAccessionPrecursor = properties.getProperty("decoy_accession_precursor", null);
        usedPeptideScore = properties.getProperty("peptide_score_accession", null);
    }

    @Override
    public Properties getConfiguration() {
        return properties;
    }

    @Override
    public void setExternalSpectrumFile(String filename) {
        this.specFileDirectory = filename;
    }

    @Override
    public String getExperimentTitle() {
        // get the attributes
        Map<String, String> attributes = unmarshaller.getElementAttributes(unmarshaller.getMzIdentMLId(), MzIdentML.class);

        // return the name
        return (attributes.containsKey("name")) ? attributes.get("name") : "";
    }

    @Override
    public String getExperimentShortLabel() {
        // return the mzIdentML id
        return unmarshaller.getMzIdentMLId();
    }

    @Override
    public Param getExperimentParams() {
        // currently, no parameters are being supported
        return new Param();
    }

    @Override
    public String getSampleName() {
        // get the sample list
        String sampleName = "";
        boolean hasSamples = false;

        Iterator<Sample> it = unmarshaller.unmarshalCollectionFromXpath(MzIdentMLElement.Sample);

        while (it.hasNext()) {
            Sample sample = it.next();

            if (sample.getName() != null)
                sampleName += ((sampleName.length() > 0) ? " and " : "") + sample.getName();

            hasSamples = true;
        }

        if (!hasSamples)
            sampleName = "Unknown sample";

        return sampleName;
    }

    @Override
    public String getSampleComment() {
        // not supported
        return null;
    }

    @Override
    public Param getSampleParams() {
        // create the param object
        Param param = new Param();

        // get the AnalysiSampleCollection object
        AnalysisSampleCollection sampleCollection = unmarshaller.unmarshal(MzIdentMLElement.AnalysisSampleCollection);

        // check if there are any samples
        if (sampleCollection == null)
            return param;

        // process the samples
        List<Sample> sampleList = sampleCollection.getSample();

        // check if there are multiple samples specified
        boolean multipleSamples = false;
        if (sampleList.size() > 1) {
            param.getCvParam().add(QuantitationCvParams.CONTAINS_MULTIPLE_SUBSAMPLES.getParam());
            multipleSamples = true;
        }

        // add the samples
        Integer sampleIndex = 1;

        for (Sample sample : sampleList) {
            // add the cvParams
            for (uk.ac.ebi.jmzidml.model.mzidml.CvParam cvParam : sample.getCvParam()) {
                // convert the param
                CvParam convParam = new CvParam(cvParam.getCvRef(), cvParam.getAccession(), cvParam.getName(), cvParam.getValue());
                // if there are multiple samples, set the sample index as value
                if (multipleSamples)
                    convParam.setValue(sampleIndex.toString());

                // add the cvParam
                param.getCvParam().add(convParam);
            }

            // ignore any userParams

            sampleIndex++;
        }

        return param;
    }

    @Override
    public SourceFile getSourceFile() {
        // create the souce file object
        SourceFile sourceFile = new SourceFile();

        sourceFile.setFileType("mzIdentML file");
        sourceFile.setPathToFile(this.sourcefile.getAbsolutePath());
        sourceFile.setNameOfFile(this.sourcefile.getName());

        return sourceFile;
    }

    @Override
    public Collection<Contact> getContacts() {
        // create the arralist of contacts
        ArrayList<Contact> contacts = new ArrayList<Contact>();

        // get the audit collection
        AuditCollection auditCollection = unmarshaller.unmarshal(MzIdentMLElement.AuditCollection);

        if (auditCollection == null)
            return contacts;

        // get all organizations and save them in HashMap with the id as key
        HashMap<String, Organization> organizations = new HashMap<String, Organization>();

        for (Organization org : auditCollection.getOrganization())
            organizations.put(org.getId(), org);

        for (Person person : auditCollection.getPerson()) {
            // create the contact
            Contact contact = new Contact();
            contact.setName(
                    ((person.getFirstName() != null && person.getFirstName().length() > 0) ? person.getFirstName() + " " : "") +
                            ((person.getMidInitials() != null && person.getMidInitials().length() > 0) ? person.getMidInitials() + " " : "") +
                            ((person.getLastName() != null) ? person.getLastName() : "")
            );

            // make sure there was a name defined
            if (contact.getName().length() < 1)
                continue;

            // set the e-mail address as contact info
            uk.ac.ebi.jmzidml.model.mzidml.CvParam contactInfoParam = getParamFromCollection("MS:1000589", person.getCvParam());
            // try the address
            if (contactInfoParam == null)
                contactInfoParam = getParamFromCollection("MS:1000587", person.getCvParam());
            // try the url...
            if (contactInfoParam == null)
                contactInfoParam = getParamFromCollection("MS:1000588", person.getCvParam());

            if (contactInfoParam != null)
                contact.setContactInfo(contactInfoParam.getValue());

            // the default setting if there's no data
            contact.setInstitution("");

            List<Affiliation> affiliations = person.getAffiliation();

            // get the organization
            if (affiliations != null && affiliations.size() > 0) {
                // only use the first one
                Organization org = organizations.get(affiliations.get(0).getOrganizationRef());

                if (org != null && org.getName() != null)
                    contact.setInstitution(org.getName());
            }

            contacts.add(contact);
        }


        return contacts;
    }

    /**
     * Extracts the defined cvParam from the passed collection
     * or null in case the cvParam isn't present.
     *
     * @param accession
     * @param params
     * @return
     */
    private uk.ac.ebi.jmzidml.model.mzidml.CvParam getParamFromCollection(String accession, Collection<uk.ac.ebi.jmzidml.model.mzidml.CvParam> params) {
        for (uk.ac.ebi.jmzidml.model.mzidml.CvParam param : params) {
            if (param.getAccession().equals(accession))
                return param;
        }

        return null;
    }

    @Override
    public InstrumentDescription getInstrument() {
        // that's only possible with mzML and only of there only one file
        if (specFileFormat != SpecFileFormat.MZML || specFileIds.size() != 1)
            return null;

        // get the first (and only) spec file id
        String specFileId = specFileIds.get(0);
        DAO specDAO;
        try {
            specDAO = getSpectraDAO(specFileId);
        } catch (InvalidFormatException e) {
            return null;
        }

        if (specDAO == null)
            return null;
        else
            return specDAO.getInstrument();
    }

    @Override
    public Software getSoftware() {
        // get the software iterator
        Iterator<AnalysisSoftware> analysisSoftwareIt = unmarshaller.unmarshalCollectionFromXpath(MzIdentMLElement.AnalysisSoftware);

        while (analysisSoftwareIt.hasNext()) {
            AnalysisSoftware analysisSoftware = analysisSoftwareIt.next();

            String nameAcc = analysisSoftware.getSoftwareName().getCvParam().getAccession();

            // check if it's known
            // Bioworks, Mascot, Mascot Parser, MyriMatch, ProteinPilot, ProteoWizard, ProteomeDiscoverer, Scaffold, Sequest, SpectraSt, SpectrumMill, X!Tandem
            if (nameAcc.equals("MS:1000533") || nameAcc.equals("MS:1001207") || nameAcc.equals("MS:1001478") ||
                    nameAcc.equals("MS:1001585") || nameAcc.equals("MS:1001475") || nameAcc.equals("MS:1000663") ||
                    nameAcc.equals("MS:1000615") || nameAcc.equals("MS:1000650") || nameAcc.equals("MS:1001561") ||
                    nameAcc.equals("MS:1001208") || nameAcc.equals("MS:1000687") || nameAcc.equals("MS:1001476")) {
                // this software describes the search engine, so use it
                Software software = new Software();
                software.setName(analysisSoftware.getSoftwareName().getCvParam().getName());
                software.setVersion((analysisSoftware.getVersion() != null) ? analysisSoftware.getVersion() : "");
                software.setComments((analysisSoftware.getCustomizations() != null) ? analysisSoftware.getCustomizations() : "");

                return software;
            }
        }

        // if no known software was found, return null
        return null;
    }

    @Override
    public Param getProcessingMethod() {
        // only available in mzML files
        if (specFileFormat != SpecFileFormat.MZML || spectraFiles.size() != 1)
            return null;

        String specFileId = specFileIds.get(0);
        DAO specDao;
        try {
            specDao = getSpectraDAO(specFileId);
        } catch (InvalidFormatException e) {
            return null;
        }

        if (specDao == null)
            return null;
        else
            return specDao.getProcessingMethod();
    }

    @Override
    public Protocol getProtocol() {
        // not supported
        return null;
    }

    @Override
    public Collection<Reference> getReferences() {
        // create an array of references
        ArrayList<Reference> references = new ArrayList<Reference>();

        Iterator<BibliographicReference> it = unmarshaller.unmarshalCollectionFromXpath(MzIdentMLElement.BibliographicReference);

        while (it.hasNext()) {
            BibliographicReference ref = it.next();

            // build the refline
            String refLine = ((ref.getAuthors() != null) ? ref.getAuthors() + ". " : "") +
                    ((ref.getYear() != null) ? "(" + ref.getYear().toString() + "). " : "") +
                    ((ref.getTitle() != null) ? ref.getTitle() + " " : "") +
                    ((ref.getPublication() != null) ? ref.getPublication() + " " : "") +
                    ((ref.getVolume() != null) ? ref.getVolume() + "" : "") +
                    ((ref.getIssue() != null) ? "(" + ref.getIssue() + ")" : "") +
                    ((ref.getPages() != null) ? ":" + ref.getPages() + "." : "");

            // create the ref
            Reference prideRef = new Reference();
            prideRef.setRefLine(refLine);

            Param additional = new Param();
            if (ref.getDoi() != null)
                additional.getCvParam().add(new CvParam("PRIDE", "PRIDE:0000042", "DOI", ref.getDoi()));
            prideRef.setAdditional(additional);

            references.add(prideRef);
        }

        return references;
    }

    @Override
    public String getSearchDatabaseName() {
        String searchDatabaseName = "";

        // get the iterator for the search databases
        Iterator<SearchDatabase> it = unmarshaller.unmarshalCollectionFromXpath(MzIdentMLElement.SearchDatabase);

        while (it.hasNext()) {
            SearchDatabase db = it.next();

            if (db.getDatabaseName().getCvParam() != null)
                searchDatabaseName += (searchDatabaseName.length() > 0 ? ", " : "") + db.getDatabaseName().getCvParam().getName();
            else if (db.getDatabaseName().getUserParam() != null)
                searchDatabaseName += (searchDatabaseName.length() > 0 ? ", " : "") + db.getDatabaseName().getUserParam().getName();
        }

        return searchDatabaseName;
    }

    @Override
    public String getSearchDatabaseVersion() {
        String searchDatabaseVersion = "";

        // get the iterator for the search databases
        Iterator<SearchDatabase> it = unmarshaller.unmarshalCollectionFromXpath(MzIdentMLElement.SearchDatabase);

        while (it.hasNext()) {
            SearchDatabase db = it.next();

            if (db.getVersion() != null)
                searchDatabaseVersion += (searchDatabaseVersion.length() > 0 ? ", " : "") + db.getVersion();
            else if (db.getReleaseDate() != null)
                searchDatabaseVersion += (searchDatabaseVersion.length() > 0 ? ", " : "") + db.getReleaseDate();
        }

        return searchDatabaseVersion;
    }

    @Override
    public Collection<DatabaseMapping> getDatabaseMappings() {
        ArrayList<DatabaseMapping> mappings = new ArrayList<DatabaseMapping>();

        // get the iterator for the search databases
        Iterator<SearchDatabase> it = unmarshaller.unmarshalCollectionFromXpath(MzIdentMLElement.SearchDatabase);

        while (it.hasNext()) {
            SearchDatabase db = it.next();

            DatabaseMapping mapping = new DatabaseMapping();
            mapping.setSearchEngineDatabaseName(db.getName());
            mapping.setSearchEngineDatabaseVersion((db.getVersion() != null) ? db.getVersion() : "");

            mappings.add(mapping);
        }

        return mappings;
    }

    @Override
    public Collection<PTM> getPTMs() throws InvalidFormatException {
        /**
         * The modifications are not retrieved from SpectrumIdentificationProtocol/
         * ModificationParams/SearchModification as these are optional elements and
         * thus might not be present. This method is more resource intensive but
         * will for sure catch all reported modifications.
         */

        // store the encountered ptms in a hashmap with their accession as key
        HashMap<String, PTM> ptms = new HashMap<String, PTM>();

        // get an iterator over all peptides
        Iterator<uk.ac.ebi.jmzidml.model.mzidml.Peptide> it = unmarshaller.unmarshalCollectionFromXpath(MzIdentMLElement.Peptide);

        HashMap<String, HashSet<Character>> modResidues = new HashMap<String, HashSet<Character>>();

        while (it.hasNext()) {
            uk.ac.ebi.jmzidml.model.mzidml.Peptide peptide = it.next();

            // process all modifications
            for (Modification mod : peptide.getModification()) {
                PTM ptm = convertModificationToPTM(mod);

                // add the AA specificity
                if (!modResidues.containsKey(ptm.getSearchEnginePTMLabel()))
                    modResidues.put(ptm.getSearchEnginePTMLabel(), new HashSet<Character>());

                // get the residue
                Integer position = mod.getLocation();
                Character residue;

                if (position != null) {
                    if (position == 0)
                        residue = '0';
                    else if (position > peptide.getPeptideSequence().length())
                        residue = '1';
                    else
                        residue = peptide.getPeptideSequence().charAt(position - 1);

                    modResidues.get(ptm.getSearchEnginePTMLabel()).add(residue);
                }

                if (!ptms.containsKey(ptm.getSearchEnginePTMLabel()))
                    ptms.put(ptm.getSearchEnginePTMLabel(), ptm);
            }
        }

        // add the residue info
        for (String modAccession : ptms.keySet()) {
            // make sure there is some residue info available
            if (!modResidues.containsKey(modAccession))
                continue;

            // create the sorted string
            ArrayList<Character> residues = new ArrayList<Character>(modResidues.get(modAccession));
            Collections.sort(residues);

            String residueString = "";

            for (Character c : residues)
                residueString += c;

            ptms.get(modAccession).setResidues(residueString);
        }

        return ptms.values();
    }

    /**
     * Converts a Modification object into a PTM object.
     *
     * @param mod The Modification object to convert.
     * @return The converted PTM object.
     * @throws InvalidFormatException In case the required cvParam (PSI, MOD or MS:1001460) isn't found.
     */
    private PTM convertModificationToPTM(Modification mod) throws InvalidFormatException {
        // get the PSI / UNIMOD param || MS:1001460 (unknown)
        uk.ac.ebi.jmzidml.model.mzidml.CvParam modParam = null;

        for (uk.ac.ebi.jmzidml.model.mzidml.CvParam param : mod.getCvParam()) {
            if (param.getCvRef().equals("UNIMOD") || param.getCvRef().equals("MOD") || param.getAccession().equals("MS:1001460")) {
                modParam = param;
                break;
            }
        }

        if (modParam == null)
            throw new InvalidFormatException("Missing required cvParam in modification object. Every modification element must contain either a UNIMOD, MOD or the MS:1001460 parameter.");

        // create the ptm
        PTM ptm = new PTM();

        ptm.setSearchEnginePTMLabel(modParam.getAccession());
        // only use PSI-MOD accessions
        if (modParam.getAccession().startsWith("MOD:"))
            ptm.setModAccession(modParam.getAccession());

        // get the params cv
        Cv cv = cvs.get(modParam.getCvRef());

        if (cv == null)
            throw new InvalidFormatException("Missing Cv object for " + modParam.getCvRef());

        if (modParam.getAccession().startsWith("MOD:")) {
            ptm.setModDatabase(cv.getFullName());
            ptm.setModDatabaseVersion((cv.getVersion() != null) ? cv.getVersion() : "");
        }

        if (mod.getMonoisotopicMassDelta() != null)
            ptm.getModMonoDelta().add(mod.getMonoisotopicMassDelta().toString());
        if (mod.getAvgMassDelta() != null)
            ptm.getModAvgDelta().add(mod.getAvgMassDelta().toString());

        return ptm;
    }

    @Override
    public SearchResultIdentifier getSearchResultIdentifier() {
        // intialize the search result identifier
        SearchResultIdentifier identifier = new SearchResultIdentifier();

        // format the current time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        identifier.setSourceFilePath(sourcefile.getAbsolutePath());
        identifier.setTimeCreated(formatter.format(new Date(System.currentTimeMillis())));
        identifier.setHash(FileUtils.MD5Hash(sourcefile.getAbsolutePath()));

        return identifier;
    }

    @Override
    public Collection<CV> getCvLookup() {
        ArrayList<CV> usedCvs = new ArrayList<CV>();

        boolean prideAdded = false;
        boolean msAdded = false;

        // loop through the used cvs
        for (Cv cv : cvs.values()) {
            CV usedCv = new CV();
            usedCv.setCvLabel(cv.getId());
            usedCv.setFullName(cv.getFullName());
            usedCv.setVersion(cv.getVersion());
            usedCv.setAddress(cv.getUri());

            usedCvs.add(usedCv);

            // check if the PRIDE or MS cv were added
            if (cv.getId().equals("MS"))
                msAdded = true;
            if (cv.getId().equals("PRIDE"))
                prideAdded = true;
        }

        // add PRIDE and MS if they weren't added
        if (!msAdded)
            usedCvs.add(new CV("MS", "PSI Mass Spectrometry Ontology", "1.2", "http://psidev.cvs.sourceforge.net/viewvc/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo"));
        if (!prideAdded)
            usedCvs.add(new CV("PRIDE", "PRIDE Controlled Vocabulary", "1.101", "http://ebi-pride.googlecode.com/svn/trunk/pride-core/schema/pride_cv.obo"));

        return usedCvs;
    }

    @Override
    public int getSpectrumCount(boolean onlyIdentified) throws InvalidFormatException {
        int totalSpecCount = 0;

        // get the total spec count
        for (String specFileId : spectraFiles.keySet()) {
            DAO specDao = getSpectraDAO(specFileId);

            if (specDao == null)
                continue;
            else
                totalSpecCount += specDao.getSpectrumCount(false);
        }


        if (!onlyIdentified)
            return totalSpecCount;
            // check if there are spectra at all
        else if (totalSpecCount == 0)
            return 0;

        // return the number of referenced spectra
        return referencedSpectraIds.size();
    }

    @Override
    public Iterator<Spectrum> getSpectrumIterator(boolean onlyIdentified) throws InvalidFormatException {
        return new MzIdentmlSpectrumIterator(onlyIdentified);
    }

    private class MzIdentmlSpectrumIterator implements Iterator<Spectrum> {
        /**
         * The current spectra file being processed.
         */
        private int currentSpecFileIndex = 0;
        /**
         * The id of the current spec file
         */
        private String currentSpecFileId = null;
        /**
         * The spectra ids associated with the current iterator
         */
        private List<String> currentSpecIds = null;
        /**
         * Indicates whether only identified spectra should be reported
         */
        private boolean onlyIdentified;
        /**
         * The number of spectra already parsed
         */
        private int spectraParsed = 0;
        /**
         * The current spectrum iterator being used.
         */
        private Iterator<Spectrum> currentDaoIt = null;

        /**
         * Creates a new MzIdentmlSpectrumIterator
         *
         * @param onlyIdentified Indicates whether only identified spectra should be reported.
         * @throws InvalidFormatException
         */
        private MzIdentmlSpectrumIterator(boolean onlyIdentified) throws InvalidFormatException {
            super();

            this.onlyIdentified = onlyIdentified;

            // make sure the spec id mapping hashmap was created
            createSpectraIdIndexes();
        }

        @Override
        public boolean hasNext() {
            if (onlyIdentified)
                return spectraParsed < referencedSpectraIds.size();
            else
                return spectraParsed < spectrumIndexMapping.size();
        }

        @Override
        public Spectrum next() {
            try {
                // check if the iterator was already created and if the iterator
                // still has spectra left
                if (currentDaoIt == null || !currentDaoIt.hasNext()) {
                    // get the next iterator
                    if (currentSpecFileIndex >= specFileIds.size())
                        throw new ConverterException("Failed to retrieve all spectra from spectra file.");

                    currentSpecFileId = specFileIds.get(currentSpecFileIndex++);

                    currentSpecIds = getSpectraDAO(currentSpecFileId).getSpectraIds();
                    currentDaoIt = getSpectraDAO(currentSpecFileId).getSpectrumIterator(false);
                }

                // get the next spectrum
                Spectrum spec = currentDaoIt.next();
                spectraParsed++;
                // the spec id used in the various Hashes
                String localSpecId = null;

                // set the correct spectrum id
                if (specIdFormat == SpecIdFormat.MZML_ID || specIdFormat == SpecIdFormat.SINGLE_PEAK_LIST_NATIVE_ID) {
                    // get the spec ids
                    String nativeId = currentSpecIds.get(currentSpecFileIndex);

                    // create the local id
                    localSpecId = getSpectrumFileId(currentSpecFileId, nativeId);
                    // set the new id
                    Integer newId = spectrumIndexMapping.get(localSpecId);

                    if (newId == null)
                        throw new ConverterException("Failed to retrieve mapped mzData id for spectrum " + localSpecId);

                    spec.setId(newId);
                } else {
                    // all DAOs return 1-based ids in the sequence the spectra were found in the file.
                    localSpecId = getSpectrumFileId(currentSpecFileId, new Integer(spec.getId() - 1).toString());
                    // set the new id
                    Integer newId = spectrumIndexMapping.get(localSpecId);

                    if (newId == null)
                        throw new ConverterException("Failed to retrieve mapped mzData id for spectrum " + localSpecId);

                    spec.setId(newId);
                }

                // if only identified spectra should be returned, make sure this
                // one was identified
                if (onlyIdentified && !referencedSpectraIds.contains(localSpecId))
                    return next();
                else
                    return spec;
            } catch (InvalidFormatException e) {
                throw new ConverterException(e);
            }
        }

        @Override
        public void remove() {
            // not supported
        }
    }

    @Override
    public int getSpectrumReferenceForPeptideUID(String peptideUID) throws InvalidFormatException {
        // get the SII identified by the peptideUID
        try {
            createSpectraIdIndexes();

            // split the SII and PE id
            int separationIndex = peptideUID.indexOf(ID_SEPARATOR);

            if (separationIndex == -1)
                throw new InvalidFormatException("Invalid combined id passed to createPeptide.");

            String siiId = peptideUID.substring(0, separationIndex);

            // get the SII and PE objects
            SpectrumIdentificationItem sii = unmarshaller.unmarshal(SpectrumIdentificationItem.class, siiId);

            // make sure the SII is present in the HashMap
            if (!sIISpecIdMapping.containsKey(sii.getId()))
                throw new InvalidFormatException("Missing spectrum id mapping for SpectrumIdentificationItem " + sii.getId());

            // get the spec id
            String specId = sIISpecIdMapping.get(sii.getId());

            if (!spectrumIndexMapping.containsKey(specId))
                return -1;

            return spectrumIndexMapping.get(specId);

        } catch (JAXBException e) {
            throw new InvalidFormatException("Failed to retrieve SpectrumIdentificationItem from mzIdentML file.", e);
        } catch (IllegalArgumentException e) {
            throw new InvalidFormatException("Failed to retrieve SpectrumIdentificationItem from mzIdentML file.", e);
        }
    }

    @Override
    public Identification getIdentificationByUID(String identificationUID) throws InvalidFormatException {
        try {
            createSpectraIdIndexes();

            DBSequence dbsequence = unmarshaller.unmarshal(DBSequence.class, identificationUID);
            return convertIdentification(dbsequence, false);
        } catch (JAXBException e) {
            throw new InvalidFormatException("Failed to unmarshall DBSequence object " + identificationUID, e);
        }
    }

    @Override
    public Iterator<Identification> getIdentificationIterator(
            boolean prescanMode) throws InvalidFormatException {
        return new MzIdentmlIdentificationIterator(prescanMode);
    }

    private class MzIdentmlIdentificationIterator implements Iterator<Identification> {
        /**
         * All available dbSequence ids as an array list.
         */
        private ArrayList<String> dbSequenceIds = new ArrayList<String>(proteinSIIMapping.keySet());
        /**
         * The current position in the dbSequenceIds array.
         */
        private int currentIndex = 0;
        /**
         * Indicates whether it's currently in prescan mode.
         */
        private boolean inPrescanMode = false;

        private MzIdentmlIdentificationIterator(boolean inPrescanMode) throws InvalidFormatException {
            super();
            this.inPrescanMode = inPrescanMode;

            createSpectraIdIndexes();
        }

        @Override
        public boolean hasNext() {
            return currentIndex < dbSequenceIds.size();
        }

        @Override
        public Identification next() {
            // unmarshall the dbsequence object
            try {
                DBSequence dbSequence = unmarshaller.unmarshal(DBSequence.class, dbSequenceIds.get(currentIndex++));

                return convertIdentification(dbSequence, inPrescanMode);
            } catch (Exception e) {
                throw new ConverterException("Failed to unmarshall DBSequence object " + dbSequenceIds.get(currentIndex - 1));
            }

        }

        @Override
        public void remove() {
            // not supported
        }

    }

    /**
     * Converts the passed dbsequence object into an Identification object.
     *
     * @param dbsequence  The dbsequence object to convert.
     * @param prescanMode Indiciates whether in prescan mode.
     * @return The report model Identification object.
     * @throws InvalidFormatException
     */
    private Identification convertIdentification(DBSequence dbsequence, boolean prescanMode) throws InvalidFormatException {
        String dbsequenceId = dbsequence.getId();

        // make sure the identification is in the hashmap
        if (!proteinSIIMapping.containsKey(dbsequenceId))
            throw new InvalidFormatException("DBSequence object " + dbsequenceId + " is not supported through SpectrumIdentificationItems.");

        try {
            // get the db sequence object
            SearchDatabase db = unmarshaller.unmarshal(SearchDatabase.class, dbsequence.getSearchDatabaseRef());

            // create the identification object
            Identification identification = new Identification();

            identification.setAccession(dbsequence.getAccession());
            identification.setDatabase(db.getName());
            identification.setDatabaseVersion(db.getVersion());
            identification.setUniqueIdentifier(dbsequenceId);

            // set the search engine - only possible if there's only 1 SpectrumIdentificationProtocol
            if (analysisProtocolCollection == null)
                analysisProtocolCollection = unmarshaller.unmarshal(MzIdentMLElement.AnalysisProtocolCollection);
            if (proteinSoftwareName == null && analysisProtocolCollection.getSpectrumIdentificationProtocol().size() == 1) {
                String softwareRef = analysisProtocolCollection.getSpectrumIdentificationProtocol().get(0).getAnalysisSoftwareRef();
                AnalysisSoftware software = unmarshaller.unmarshal(AnalysisSoftware.class, softwareRef);

                proteinSoftwareName = software.getName();
            }

            identification.setSearchEngine((proteinSoftwareName != null) ? proteinSoftwareName : "");

            // add all peptides
            for (String combinedId : proteinSIIMapping.get(dbsequenceId))
                identification.getPeptide().add(createPeptide(combinedId, prescanMode));

            // set the score - only in prescan mode
            identification.setScore(prescanMode ? calculateProteinScore(identification) : 0);
            // setting the threshold is not possible
            identification.setThreshold(null);

            if (prescanMode) {
                // add the additional info
                Param additional = new Param();

                for (uk.ac.ebi.jmzidml.model.mzidml.CvParam param : dbsequence.getCvParam())
                    additional.getCvParam().add(new CvParam(param.getCvRef(), param.getAccession(), param.getName(), param.getValue()));

                // label decoy hits
                if (decoyAccessionPrecursor != null && identification.getAccession().startsWith(decoyAccessionPrecursor))
                    additional.getCvParam().add(DAOCvParams.DECOY_HIT.getParam());

                identification.setAdditional(additional);
            }

            return identification;
        } catch (JAXBException e) {
            throw new InvalidFormatException("Failed to retrieve object " + dbsequenceId + " from mzIdentML file.", e);
        }
    }

    /**
     * Creates a report model Peptide from a combinedId. The combined
     * id must contain an SpectrumIdentificationItem id and a
     * PeptideEvidence id separated by the ID_SEPARATOR.
     *
     * @param combinedId  A SpectrumIdentificationItem id and a PeptideEvidence id separated by the ID_SEPARATOR.
     * @param prescanMode Indicates whether a prescan object or a scan object should be created.
     * @return The Peptide object.
     * @throws InvalidFormatException
     */
    private Peptide createPeptide(String combinedId, boolean prescanMode) throws InvalidFormatException {
        try {
            // split the SII and PE id
            int separationIndex = combinedId.indexOf(ID_SEPARATOR);

            if (separationIndex == -1)
                throw new InvalidFormatException("Invalid combined id passed to createPeptide.");

            String siiId = combinedId.substring(0, separationIndex);
            String peId = combinedId.substring(separationIndex + ID_SEPARATOR.length());

            // get the SII and PE objects
            SpectrumIdentificationItem sii = unmarshaller.unmarshal(SpectrumIdentificationItem.class, siiId);
            PeptideEvidence pe = unmarshaller.unmarshal(PeptideEvidence.class, peId);
            // get the mzIdentML peptide item
            uk.ac.ebi.jmzidml.model.mzidml.Peptide mzIdPeptide = unmarshaller.unmarshal(uk.ac.ebi.jmzidml.model.mzidml.Peptide.class, pe.getPeptideRef());

            // create the peptide
            Peptide peptide = new Peptide();
            peptide.setSequence(mzIdPeptide.getPeptideSequence());
            peptide.setUniqueIdentifier(combinedId);
            peptide.setStart(pe.getStart());
            peptide.setEnd(pe.getEnd());

            // set the spectrum ref
            String specId = sIISpecIdMapping.get(sii.getId());
            if (specId == null && !allowIdentificationsOnly)
                throw new InvalidFormatException("Missing spectrum id for " + sii.getId());
            else if (specId != null) {
                // get the reference
                Integer specRef = spectrumIndexMapping.get(specId);
                peptide.setSpectrumReference(specRef == null ? -1 : specRef);
            }

            // check whether the peptide is specific
            if (!peptideProteinMapping.containsKey(peptide.getSequence()))
                throw new InvalidFormatException("Missing DBSequence link for peptide " + peptide.getSequence());
            peptide.setIsSpecific(peptideProteinMapping.get(peptide.getSequence()).size() == 1);

            if (prescanMode) {
                // add all PTMs
                peptide.getPTM().addAll(createPeptideModifications(mzIdPeptide));

                // add the additional params
                Param additional = new Param();
                // add the PeptideEvidence additional params
                for (uk.ac.ebi.jmzidml.model.mzidml.CvParam param : pe.getCvParam())
                    additional.getCvParam().add(new CvParam(param.getCvRef(), param.getAccession(), param.getName(), param.getValue()));
                // add the Peptide additional params
                for (uk.ac.ebi.jmzidml.model.mzidml.CvParam param : mzIdPeptide.getCvParam())
                    additional.getCvParam().add(new CvParam(param.getCvRef(), param.getAccession(), param.getName(), param.getValue()));
                // add the SpectrumIdentificationItem additional params
                for (uk.ac.ebi.jmzidml.model.mzidml.CvParam param : sii.getCvParam())
                    additional.getCvParam().add(new CvParam(param.getCvRef(), param.getAccession(), param.getName(), param.getValue()));

                peptide.setAdditional(additional);
            } else {
                // add the fragment ions
                peptide.getFragmentIon().addAll(createPeptideFragmentIons(sii));
            }

            return peptide;

        } catch (JAXBException e) {
            throw new InvalidFormatException("Failed to retrieve object from mzIdentML file.", e);
        }
    }

    /**
     * Converts a mzIdentML peptide's modifications into a
     * list of PeptidePTMs.<br>
     * <b>Warning:</b> Modifications without the location attribute
     * are set to the location "-1".
     *
     * @param peptide The mzIdentML peptide which modification's should be converted.
     * @return A List of PeptidePTMs.
     * @throws InvalidFormatException
     */
    private List<PeptidePTM> createPeptideModifications(uk.ac.ebi.jmzidml.model.mzidml.Peptide peptide) throws InvalidFormatException {
        // create the list of PeptidePTMs
        ArrayList<PeptidePTM> ptms = new ArrayList<PeptidePTM>();

        // iterate over the peptide's modifications
        for (Modification mod : peptide.getModification()) {
            // create the new modification
            // get the PSI / UNIMOD param || MS:1001460 (unknown)
            uk.ac.ebi.jmzidml.model.mzidml.CvParam modParam = null;

            for (uk.ac.ebi.jmzidml.model.mzidml.CvParam param : mod.getCvParam()) {
                if (param.getCvRef().equals("UNIMOD") || param.getCvRef().equals("MOD") || param.getAccession().equals("MS:1001460")) {
                    modParam = param;
                    break;
                }
            }

            if (modParam == null)
                throw new InvalidFormatException("Missing required cvParam in modification object. Every modification element must contain either a UNIMOD, MOD or the MS:1001460 parameter.");

            // create the PeptidePTM
            PeptidePTM ptm = new PeptidePTM();

            ptm.setSearchEnginePTMLabel(modParam.getAccession());
            ptm.setModAccession(modParam.getAccession());
            ptm.setFixedModification(isFixedModification(modParam.getAccession()));
            // TODO DOC - modifications without a "location" attribute are set to location -1
            ptm.setModLocation((mod.getLocation() != null) ? mod.getLocation() : -1);

            if (mod.getAvgMassDelta() != null)
                ptm.getModAvgDelta().add(mod.getAvgMassDelta().toString());
            if (mod.getMonoisotopicMassDelta() != null)
                ptm.getModMonoDelta().add(mod.getMonoisotopicMassDelta().toString());

            Param additional = new Param();

            // add all but the modification cvParam
            for (uk.ac.ebi.jmzidml.model.mzidml.CvParam param : mod.getCvParam()) {
                if (param.getAccession().equals(modParam.getAccession()))
                    continue;

                additional.getCvParam().add(new CvParam(param.getCvRef(), param.getAccession(), param.getName(), param.getValue()));
            }

            ptm.setAdditional(additional);

            ptms.add(ptm);
        }

        return ptms;
    }

    /**
     * Checks whether a given modification's accession was defined as fixed
     * modification. This function will always return false if no
     * ModificationParams are reported in the mzIdentML file.
     *
     * @param modAccession The modification's accession.
     * @return Boolean indicating whether the modification is a fixed modification.
     */
    private boolean isFixedModification(String modAccession) {
        // if the fixed modifications were already loaded, just return the saved status
        if (fixedModifications != null)
            return fixedModifications.contains(modAccession);

        fixedModifications = new HashSet<String>();

        // load the fixed modification accessions by iterating over all SearchModifications
        Iterator<SearchModification> it = unmarshaller.unmarshalCollectionFromXpath(MzIdentMLElement.SearchModification);

        while (it.hasNext()) {
            SearchModification mod = it.next();

            // iterate over all the cvParams
            for (uk.ac.ebi.jmzidml.model.mzidml.CvParam param : mod.getCvParam()) {
                // ignore all non MOD / UNIMOD params
                if (!"MOD".equals(param.getCvRef()) && !"UNIMOD".equals(param.getCvRef()))
                    continue;

                // save the params accession in the HashSet if it's a fixed mod
                if (mod.isFixedMod())
                    fixedModifications.add(param.getAccession());
            }
        }

        // return the status of this mod
        return fixedModifications.contains(modAccession);
    }

    /**
     * Creates a list of FragmentIons based on the SpectrumIdentificationItem's
     * fragmentation data.
     *
     * @param sii The SpectrumIdentificationItem to create the fragemnt ions from.
     * @return A list of FragmentIon representing the Fragmentation information of the SpectrumIdentificationItem.
     */
    private List<FragmentIon> createPeptideFragmentIons(SpectrumIdentificationItem sii) {
        // get the Fragementation
        Fragmentation fragmentation = sii.getFragmentation();

        if (fragmentation == null)
            return Collections.emptyList();

        ArrayList<FragmentIon> fragmentIons = new ArrayList<FragmentIon>();

        // iterate over the ion types - only parse a-c, x-z ions
        for (IonType iontype : fragmentation.getIonType()) {

            // ignore not supported iontypes
            CvParam ionTypeCvParam = getIonTypeParam(iontype.getCvParam().getName());

            if (ionTypeCvParam == null)
                continue;

            // ignore IonTypes with no index set
            if (iontype.getIndex() == null)
                continue;

            // iterate over the ion type indexes
            for (Integer index = 0; index < iontype.getIndex().size(); index++) {
                Integer ionIndex = iontype.getIndex().get(index);
                FragmentIon fragmentIon = new FragmentIon();

                // charge
                fragmentIon.getCvParam().add(DAOCvParams.PRODUCT_ION_CHARGE.getParam(iontype.getCharge()));
                // ion type - the ion index is set as value of the name param
                fragmentIon.getCvParam().add(new CvParam(ionTypeCvParam.getCvLabel(),
                        ionTypeCvParam.getAccession(),
                        ionTypeCvParam.getName(),
                        ionIndex.toString()));

                // check if there are measures to add
                boolean hasMz = false, hasIntensity = false, hasMassError = false;

                for (FragmentArray fragArr : iontype.getFragmentArray()) {
                    // m/z
                    if (productIonMZMeasureRef.contains(fragArr.getMeasureRef())) {
                        fragmentIon.getCvParam().add(DAOCvParams.PRODUCT_ION_MZ.getParam(fragArr.getValues().get(index)));
                        hasMz = true;
                    }
                    // intensity
                    if (productIonIntensityMeasureRef.contains(fragArr.getMeasureRef())) {
                        fragmentIon.getCvParam().add(DAOCvParams.PRODUCT_ION_INTENSITY.getParam(fragArr.getValues().get(index)));
                        hasIntensity = true;
                    }
                    // error
                    if (productIonMZErrorMeasureRef.contains(fragArr.getMeasureRef())) {
                        fragmentIon.getCvParam().add(DAOCvParams.PRODUCT_ION_MASS_ERROR.getParam(fragArr.getValues().get(index)));
                        hasMassError = true;
                    }
                    // delta
                    if (productIonMZDeltaMeasureRef.contains(fragArr.getMeasureRef()))
                        fragmentIon.getCvParam().add(DAOCvParams.PRODUCT_ION_DELTA.getParam(fragArr.getValues().get(index)));
                }

                if (!hasMz)
                    continue;
                if (!hasMassError)
                    fragmentIon.getCvParam().add(DAOCvParams.PRODUCT_ION_MASS_ERROR.getParam(0));
                if (!hasIntensity)
                    fragmentIon.getCvParam().add(DAOCvParams.PRODUCT_ION_INTENSITY.getParam(0));

                fragmentIons.add(fragmentIon);
            }
        }

        return fragmentIons;
    }

    // TODO: Once the MS ontology is adapted for the new fragment ion params, adapt this code
    // TODO DOC - only a-c, x-z ions are reported
    private CvParam getIonTypeParam(String ion) {

        if (ion == null) {
            return null;
        } else {
            ion = ion.trim();
            if (ion.length() == 0) {
                return null;
            }
        }

        // y-ions
        if (ion.equals("frag: y ion - H2O"))
            return new CvParam("PRIDE", "PRIDE:0000197", "y ion -H2O", null);
        if (ion.equals("frag: y ion - NH3"))
            return new CvParam("PRIDE", "PRIDE:0000198", "y ion -NH3", null);
        if (ion.equals("frag: y ion"))
            return new CvParam("PRIDE", "PRIDE:0000193", "y ion", null);

        // b-ions
        if (ion.equals("frag: b ion - H2O"))
            return new CvParam("PRIDE", "PRIDE:0000196", "b ion -H2O", null);
        if (ion.equals("frag: b ion - NH3"))
            return new CvParam("PRIDE", "PRIDE:0000195", "b ion -NH3", null);
        if (ion.equals("frag: b ion"))
            return new CvParam("PRIDE", "PRIDE:0000194", "b ion", null);

        // c-ions
        if (ion.equals("frag: c ion - H2O"))
            return new CvParam("PRIDE", "PRIDE:0000237", "c ion -H2O", null);
        if (ion.equals("frag: c ion - NH3"))
            return new CvParam("PRIDE", "PRIDE:0000238", "c ion -NH3", null);
        if (ion.equals("frag: c ion"))
            return new CvParam("PRIDE", "PRIDE:0000236", "c ion", null);

        // a-ions
        if (ion.equals("frag: a ion - H2O"))
            return new CvParam("PRIDE", "PRIDE:0000234", "a ion -H2O", null);
        if (ion.equals("frag: a ion - NH3"))
            return new CvParam("PRIDE", "PRIDE:0000235", "a ion -NH3", null);
        if (ion.equals("frag: a ion"))
            return new CvParam("PRIDE", "PRIDE:0000233", "a ion", null);

        // x-ions
        if (ion.equals("frag: x ion - H2O"))
            return new CvParam("PRIDE", "PRIDE:0000228", "x ion -H2O", null);
        if (ion.equals("frag: x ion - NH3"))
            return new CvParam("PRIDE", "PRIDE:0000229", "x ion -NH3", null);
        if (ion.equals("frag: x ion"))
            return new CvParam("PRIDE", "PRIDE:0000227", "x ion", null);

        // z-ions
        if (ion.equals("frag: z ion - H2O"))
            return new CvParam("PRIDE", "PRIDE:0000231", "z ion -H2O", null);
        if (ion.equals("frag: z ion - NH3"))
            return new CvParam("PRIDE", "PRIDE:0000232", "z ion -NH3", null);
        if (ion.equals("frag: z+2 ion"))
            return new CvParam("PRIDE", "PRIDE:0000281", "zHH ion", null);
        if (ion.equals("frag: z+1"))
            return new CvParam("PRIDE", "PRIDE:0000280", "zH ion", null);
        if (ion.equals("frag: z ion"))
            return new CvParam("PRIDE", "PRIDE:0000230", "z ion", null);

        logger.warn("Unrecognized ion type: " + ion);

        return null;

    }

    /**
     * Calculates the protein's score based on all the peptide scores
     * and depending on whether weightedScoring is enabled.
     *
     * @param identification The identification to calculate the score for.
     * @return The score. 0.0 if no score can be retrieved.
     */
    private Double calculateProteinScore(Identification identification) {
        Double score = 0.0;

        // get all peptide scores and process them accordingly
        for (Peptide peptide : identification.getPeptide()) {
            if (useWeightedScoring)
                score += getPeptideScore(peptide) / peptideProteinMapping.get(peptide.getSequence()).size();
            else
                score += getPeptideScore(peptide);
        }

        return score;
    }

    /**
     * Returns the peptide's search engine specific score.
     * Several search engine parameters are checked for and
     * whichever one is found first, is used.
     *
     * @param peptide The peptide to get the score for.
     * @return The score. 0.0 if no score was found.
     */
    private Double getPeptideScore(Peptide peptide) {
        // get the peptide scores
        Map<String, Double> peptideScores = Utils.extractPeptideScores(peptide);

        if (peptideScores.size() < 1)
            return 0.0;

        // if the peptide score isn't set yet, set it using the (randomly) first available first score
        if (usedPeptideScore == null)
            usedPeptideScore = peptideScores.keySet().iterator().next();

        if (peptideScores.containsKey(usedPeptideScore))
            return peptideScores.get(usedPeptideScore);
        else
            return 0.0;
    }
}
