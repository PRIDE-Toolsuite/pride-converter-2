package uk.ac.ebi.pride.tools.converter.dao.impl;

import de.proteinms.xtandemparser.interfaces.Modification;
import de.proteinms.xtandemparser.xtandem.*;
import de.proteinms.xtandemparser.xtandem.FragmentIon;
import de.proteinms.xtandemparser.xtandem.Peptide;
import org.xml.sax.SAXException;
import uk.ac.ebi.pride.jaxb.model.*;
import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.dao.DAO;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.dao.DAOProperty;
import uk.ac.ebi.pride.tools.converter.report.model.*;
import uk.ac.ebi.pride.tools.converter.report.model.Contact;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.Identification;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.Protocol;
import uk.ac.ebi.pride.tools.converter.report.model.Reference;
import uk.ac.ebi.pride.tools.converter.report.model.Software;
import uk.ac.ebi.pride.tools.converter.report.model.SourceFile;
import uk.ac.ebi.pride.tools.converter.report.model.UserParam;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.FileUtils;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This DAO converts X!Tandem xml output files into PRIDE
 * XML files using the xtandem-parser by Muth et al. <br>
 * <b>Missing features:</b> Currently, protein thresholds are
 * not supported as the used threshold is not available in the
 * X!Tandem file.
 *
 * @author jg
 */
public class XTandemDAO extends AbstractDAOImpl implements DAO {
    /**
     * DAO used to parse the spectra data
     */
    private DAO spectraDAO = null;

    public enum XTANDEM_FRAGMENT_IONS {
        // the order of the ions in the returned fragment
        // ion vector. the order of the ions is set in
        // XTandemFile.java:285 (return value of getFragmentIonsForPeptide)
//    	MH_IONS(0),
//    	MHNH3_IONS(1),
//    	MHH2O_IONS(2),
//    	A_IONS(3),
//    	AH2O_IONS(4),
//    	ANH3_IONS(5),
        B_IONS(6),
        BH2O_IONS(7),
        BNH3_IONS(8),
        //    	C_IONS(9),
//    	X_IONS(10),
        Y_IONS(11),
        YH2O_IONS(12),
        YNH3_IONS(13);

        private int index;

        private XTANDEM_FRAGMENT_IONS(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public CvParam getIonTypeParam(String position) {
            switch (this) {
                case Y_IONS:
                    return new CvParam("PRIDE", "PRIDE:0000193", "y ion", position);
                case YH2O_IONS:
                    return new CvParam("PRIDE", "PRIDE:0000197", "y ion -H2O", position);
                case YNH3_IONS:
                    return new CvParam("PRIDE", "PRIDE:0000198", "y ion -NH3", position);
                case B_IONS:
                    return new CvParam("PRIDE", "PRIDE:0000194", "b ion", position);
                case BH2O_IONS:
                    return new CvParam("PRIDE", "PRIDE:0000196", "b ion -H2O", position);
                case BNH3_IONS:
                    return new CvParam("PRIDE", "PRIDE:0000195", "b ion -NH3", position);
//    			case A_IONS:
//    				 return new CvParam("PRIDE", "PRIDE:0000233", "a ion", position);
//    			case AH2O_IONS:
//    				return new CvParam("PRIDE", "PRIDE:0000234", "a ion -H2O", position);
//    			case ANH3_IONS:
//    				return new CvParam("PRIDE", "PRIDE:0000235", "a ion -NH3", position);
//    			case C_IONS:
//    				return new CvParam("PRIDE", "PRIDE:0000236", "c ion", position);
//    			case X_IONS:
//    				return new CvParam("PRIDE", "PRIDE:0000227", "x ion", position);
                default:
                    return null; // this shouldn't happen
            }
        }

        public static XTANDEM_FRAGMENT_IONS[] getFragmentIonTypes() {
            return values();
        }
    }

    /**
     * The possible spectra source type associated
     * with a X!Tandem result
     *
     * @author jg
     */
    private enum SpectraType {
        /**
         * Use the identified peaks as spectrum
         */
        INTERNAL,
        PKL,
        DTA,
        MGF,
        MZXML,
        MZML;
    }

    ;

    /**
     * Describes the supported properties
     * from this DAO
     *
     * @author jg
     */
    public enum SupportedProperty {
        EXPECT_THRESHOLD("expect_threshold"),
        USE_INTERNAL_SPECTA("use_internal_spectra"),
        DECOY_PREFIX("decoy_prefix");

        private String name;

        private SupportedProperty(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * The type of spectra input
     */
    private SpectraType spectraFileType;
    /**
     * The file containing the spectra
     */
    private File spectraFile;
    /**
     * Instance of the xtandem parser to use
     */
    private XTandemFile xtandemFile;
    /**
     * The file to convert
     */
    private File sourcefile;
    /**
     * Stores the peptide sequences as key and the protein
     * accessions this peptide sequence was found in as values of
     * the HashSet. Thus, the size of the HashSet represent
     * the number of proteins the peptide fits in.
     */
    private HashMap<String, HashSet<String>> sequenceInAccessions = new HashMap<String, HashSet<String>>();
    /**
     * Stores the peptides per protein (accession) found in
     * the file with the protein's accession (= label) as key.
     * This HashMap forms the basis for generating
     * the identification information of the PRIDE XML file.
     */
    private HashMap<String, ArrayList<Peptide>> proteinHasPeptides = new HashMap<String, ArrayList<Peptide>>();
    /**
     * The id's (= 1-based index in the source file) of the
     * identified spectra.
     */
    private HashSet<Integer> identifiedSpectra = new HashSet<Integer>();
    /**
     * Holds the sequence source path as key and its description
     * as value.
     */
    private HashMap<String, String> sequenceSourceDescription = new HashMap<String, String>();
    /**
     * The modification map from the xtandem-parser
     */
    private ModificationMap modMap;
    /**
     * Path to the source spectrum file. If this is set the value
     * retrieved from the X!Tandem source file is being ignored.
     */
    private String manualSpectrumFilePath = null;
    /**
     * Indicates whether the internal spectra should be used
     * irrespective of whether there's an external spectrum
     * file referenced.
     */
    private boolean useInternalSpectra = false;
    /**
     * The expect threshold used above which PSMs are ignored.
     */
    private Double expectThreshold = 0.05;
    /**
     * The protein accession decoy prefix to use
     */
    private String decoyPrefix = "DECOY_";
    /**
     * The currently set user properties. This object
     * is only used to return the set properties. The
     * actual property values are stored in member
     * variables which are set in teh setConfiguration
     * method.
     */
    private Properties properties = new Properties();

    /**
     * Creates a new XTdanem DAO.
     *
     * @param sourceFile The file to convert.
     * @throws InvalidFormatException
     */
    public XTandemDAO(File sourceFile) throws InvalidFormatException {
        this.sourcefile = sourceFile;

        try {
            // create the parser
            xtandemFile = new XTandemFile(sourceFile.getAbsolutePath());

            // check if the X!Tandem file is valid
            if (!isValid(xtandemFile))
                throw new InvalidFormatException("Passed file is not an X!Tandem output file.");

            // set the modification map
            modMap = xtandemFile.getModificationMap();

            // build the hash maps
            buildHashMaps();
        } catch (SAXException e) {
            throw new InvalidFormatException("Failed to parse X!Tandem XML file.", e);
        }
    }

    @SuppressWarnings("rawtypes")
    public static Collection<DAOProperty> getSupportedProperties() {
        List<DAOProperty> supportedProperties = new ArrayList<DAOProperty>();

        DAOProperty<Double> expectThreshold = new DAOProperty<Double>(SupportedProperty.EXPECT_THRESHOLD.getName(), 0.05, 0.0, 1.0);
        expectThreshold.setDescription("the maximum X!Tandem expect value allowed for peptide hits to be reported. The default value is 0.05");
        expectThreshold.setShortDescription("Maximum X!Tandem expect value for peptide identifications.");
        supportedProperties.add(expectThreshold);

        DAOProperty<Boolean> useInternalSpectra = new DAOProperty<Boolean>(SupportedProperty.USE_INTERNAL_SPECTA.getName(), false);
        useInternalSpectra.setDescription("if this parameter is set to \"true\" the spectra stored in the X!Tandem file are used irrespective of whether an external peak list file is referenced. These spectra are highly preprocessed and do not properly represent the input spectra. This option should only be used if the original spectra are not available.");
        useInternalSpectra.setShortDescription("Use highly preprocessed internal spectra instead of original spectra (not recommended).");
        useInternalSpectra.setAdvanced(true);
        supportedProperties.add(useInternalSpectra);

        DAOProperty<String> decoyAccPrec = new DAOProperty<String>(SupportedProperty.DECOY_PREFIX.getName(), "DECOY_");
        decoyAccPrec.setDescription("An accession prefix that identifies decoy hits. Every protein with an accession starting with this precursor will be flagged as decoy hit. Furthermore, any decoy hit generated using X!Tandem's inbuilt reverse function will be converted to using this prefix as well.");
        decoyAccPrec.setShortDescription("Protein accession prefix to identify decoy hits.");
        supportedProperties.add(decoyAccPrec);

        return supportedProperties;
    }

    @Override
    public void setConfiguration(Properties props) {
        properties = props;

        // set the actual supported values
        useInternalSpectra = Boolean.parseBoolean(properties.getProperty(SupportedProperty.USE_INTERNAL_SPECTA.getName(), "false"));
        expectThreshold = Double.parseDouble(properties.getProperty(SupportedProperty.EXPECT_THRESHOLD.getName(), "0.05"));
        decoyPrefix = properties.getProperty(SupportedProperty.DECOY_PREFIX.getName(), "DECOY_");

        // reset the spectra dao
        spectraDAO = null;
    }

    @Override
    public Properties getConfiguration() {
        return properties;
    }

    @Override
    public void setExternalSpectrumFile(String filename) {
        manualSpectrumFilePath = filename;
    }

    /**
     * Returns the spectraDAO to be used for the
     * given spectrum file. Makes sure only one
     * instance of the DAO is created and only
     * when it's needed.
     *
     * @return The spectra DAO to be used to retrieve the spectra information.
     * @throws InvalidFormatException
     */
    private DAO getSpectraDao() throws InvalidFormatException {
        if (spectraDAO != null)
            return spectraDAO;

        // guess the spectra filetype
        guessSpectraSourceType();

        // create the respective dao
        switch (spectraFileType) {
            case DTA:
                spectraDAO = new DtaDAO(spectraFile);
                break;
            case PKL:
                spectraDAO = new PklDAO(spectraFile);
                break;
            case MGF:
                spectraDAO = new MgfDAO(spectraFile);
                break;
            case MZXML:
                spectraDAO = new MzXmlDAO(spectraFile);
                break;
            case MZML:
                spectraDAO = new MzmlDAO(spectraFile);
                break;
        }

        return spectraDAO;
    }

    /**
     * Checks whether the passed xtandem-parser
     * object represents a valid X!Tandem file.
     *
     * @param xtandemFile2
     * @return
     */
    private boolean isValid(XTandemFile xtandemFile2) {
        if (xtandemFile2.getInputParameters() == null)
            return false;
        // check if there are proteins
        if (xtandemFile2.getProteinMap() == null || !xtandemFile2.getProteinMap().getProteinIDIterator().hasNext())
            return false;
        // check if there are peptides
        if (xtandemFile2.getSpectraList() == null || xtandemFile2.getSpectraList().size() < 1)
            return false;

        return true;
    }

    /**
     * Populates the sequenceInAccession HashMap as well
     * as the proteinHasPeptides HashMap. For detailed
     * information about the two HashMaps see the javadoc
     * at the variable declaration.
     *
     * @throws InvalidFormatException
     */
    private void buildHashMaps() throws InvalidFormatException {
        // reset the hashmaps
        proteinHasPeptides.clear();
        sequenceInAccessions.clear();
        identifiedSpectra.clear();
        sequenceSourceDescription.clear();

        // set the sequence source descriptions
        PerformParams params = xtandemFile.getPerformParameters();

        if (params.getSequenceSource_1() != null)
            sequenceSourceDescription.put(params.getSequenceSource_1(), params.getSequenceSourceDescription_1());
        if (params.getSequenceSource_2() != null)
            sequenceSourceDescription.put(params.getSequenceSource_2(), params.getSequenceSourceDescription_2());
        if (params.getSequenceSource_3() != null)
            sequenceSourceDescription.put(params.getSequenceSource_3(), params.getSequenceSourceDescription_3());

        // get the peptides and proteins
        PeptideMap pepMap = xtandemFile.getPeptideMap();
        ProteinMap protMap = xtandemFile.getProteinMap();

        boolean onlyBestPep = true;    // only use the best scoring peptides (lowest expect value)
        // this feature should actually not be required

        // loop through all spectra
        for (int spectraIndex = 1; spectraIndex <= xtandemFile.getSpectraNumber(); spectraIndex++) {
            // get the spectrum's peptides
            ArrayList<Peptide> peptides = pepMap.getAllPeptides(spectraIndex);

            // initialize the minimal expect value for this spectrum
            Double minExpect = null;

            // process every peptide
            for (Peptide peptide : peptides) {
                // get the protein id by extracting it from the first domain (one peptide is only in one protein)
                if (peptide.getDomains().size() < 1)
                    throw new InvalidFormatException("Peptide object encountered that does not contain any domains.");

                // WARNING: The domain id and protein id are not unique! (spectra id still is)
                // save the spec id as identified (extract the id from the protein id)
                String firstDomainId = peptide.getDomains().get(0).getDomainID();
                String protId = firstDomainId.substring(0, firstDomainId.lastIndexOf('.'));
                identifiedSpectra.add(Integer.parseInt(protId.substring(0, protId.indexOf('.'))));

                // get the protein object
                Protein protein = protMap.getProtein(peptide.getDomains().get(0).getProteinKey());

                // store the peptide in the proteinHasPeptides HashMap
                if (!proteinHasPeptides.containsKey(protein.getLabel()))
                    proteinHasPeptides.put(protein.getLabel(), new ArrayList<Peptide>());
                // add the peptide
                proteinHasPeptides.get(protein.getLabel()).add(peptide);

                // process every domain in the peptide
                for (Domain domain : peptide.getDomains()) {
                    // ignore domains above the threshold
                    if (domain.getDomainExpect() > expectThreshold)
                        continue;

                    // expect the first peptide to be the highest ranking one (= smalles expect value)
                    if (minExpect == null)
                        minExpect = domain.getDomainExpect();
                        // make sure there are no peptides with a lower expect value after the first one
                    else if (domain.getDomainExpect() < minExpect)
                        throw new InvalidFormatException("Invalid peptide order in X!Tandem file. Smaller expect value encountered in proceeding peptide.");
                        // if only the best peptides hould be included, ignore every peptide with a larger expect value
                    else if (onlyBestPep && domain.getDomainExpect() > minExpect)
                        continue;

                    // store the protein accession with the peptide sequence in sequenceInAccessions
                    if (!sequenceInAccessions.containsKey(domain.getDomainSequence()))
                        sequenceInAccessions.put(domain.getDomainSequence(), new HashSet<String>());
                    // store the protein's accession
                    sequenceInAccessions.get(domain.getDomainSequence()).add(protein.getLabel());
                }
            }
        }
    }

    /**
     * Guesses the type of spectra file used
     * and sets spectrFileType accordingly.
     *
     * @throws InvalidFormatException
     */
    private void guessSpectraSourceType() throws InvalidFormatException {
        spectraFileType = null;

        if (useInternalSpectra) {
            spectraFileType = SpectraType.INTERNAL;
            return;
        }

        // get the source file - use the "manualSpectrumFilePath" if it was set instead of the one in the X!Tandem file
        String spectraPath = (manualSpectrumFilePath != null) ? manualSpectrumFilePath : xtandemFile.getInputParameters().getSpectrumPath();

        if (spectraPath == null)
            throw new InvalidFormatException("Missing spectra source information. No reference to spectra source file found in X!Tandem file.");

        // check if it's a file
        File spectraFile = new File(spectraPath);

        // if the file can't be found, try only the filename in the same directory as the source file
        if (!spectraFile.exists()) {
            spectraFile = new File(sourcefile.getParent() + File.separator + spectraFile.getName());

            // if the file still can't be found check if it's a GPMDB (only) entry
            if (!spectraFile.exists()) {
                Pattern gpmEntryPattern = Pattern.compile("^GPM\\d+$");
                Matcher matcher = gpmEntryPattern.matcher(spectraPath);

                // if it's a GPMDB entry use the internal spectra
                if (matcher.find()) {
                    spectraFileType = SpectraType.INTERNAL;
                    return;
                } else {
                    // if the file doesn't exist and can't be converted, throw an Exception
                    throw new InvalidFormatException("Spectrum file '" + spectraPath + "' could not be found.");
                }
            }
        }

        this.spectraFile = spectraFile;

        // if it's a directory, expect it to contain .dta files
        if (spectraFile.isDirectory()) {
            spectraFileType = SpectraType.DTA;
            return;
        }

        // as the spectra file now exists, guess the type
        String filename = spectraFile.getName().toLowerCase().trim();

        if (filename.endsWith("dta"))
            spectraFileType = SpectraType.DTA;
        else if (filename.endsWith("pkl"))
            spectraFileType = SpectraType.PKL;
        else if (filename.endsWith("mgf"))
            spectraFileType = SpectraType.MGF;
        else if (filename.toLowerCase().endsWith("mzxml"))
            spectraFileType = SpectraType.MZXML;
        else if (filename.toLowerCase().endsWith("mzml"))
            spectraFileType = SpectraType.MZML;

        // make sure the type was set correctly
        if (spectraFileType == null)
            throw new InvalidFormatException("Unsupported spectra file type used (" + spectraPath + ")");
    }

    @Override
    public String getExperimentTitle() throws InvalidFormatException {
        // make sure the spectrum file exists - this function
        // is called at this point as the getExperimentTitle()
        // will be called in pre-scan mode after the spectrum
        // source could have been manually set via the
        // setConfiguration function.
        guessSpectraSourceType();

        // not supported
        return "";
    }

    @Override
    public String getExperimentShortLabel() {
        // not supported
        return null;
    }

    @Override
    public Param getExperimentParams() {
        // initialize the collection to hold the params
        Param params = new Param();

        // get the performance params
        PerformParams perfParams = xtandemFile.getPerformParameters();

        // date of search
        params.getCvParam().add(DAOCvParams.DATE_OF_SEARCH.getParam(perfParams.getProcStartTime()));
        // file format
        params.getCvParam().add(DAOCvParams.ORIGINAL_MS_FORMAT.getParam("X!Tandem"));

        return params;
    }

    @Override
    public String getSampleName() {
        // not supported
        return null;
    }

    @Override
    public String getSampleComment() {
        // not supported
        return null;
    }

    @Override
    public Param getSampleParams() {
        // SampleParams cannot be extracted from X!Tandem files
        return new Param();
    }

    @Override
    public SourceFile getSourceFile() {
        // initialize the return variable
        SourceFile file = new SourceFile();

        file.setPathToFile(sourcefile.getAbsolutePath());
        file.setNameOfFile(sourcefile.getName());
        file.setFileType("X!Tandem XML file");

        return file;
    }

    @Override
    public Collection<Contact> getContacts() {
        // not supported
        return null;
    }

    @Override
    public InstrumentDescription getInstrument() {
        // not supported
        return null;
    }

    @Override
    public Software getSoftware() {
        Software software = new Software();

        software.setName("X! Tandem");
        software.setVersion(xtandemFile.getPerformParameters().getProcVersion().replace("x! tandem", "").trim());

        return software;
    }

    @Override
    public Param getProcessingMethod() {
        Param params = new Param();

        InputParams inputParams = xtandemFile.getInputParameters();

        // set the fragment error
        Double fragmentError = inputParams.getSpectrumMonoIsoMassError();
        if (fragmentError != null && fragmentError > 0)
            params.getCvParam().add(DAOCvParams.SEARCH_SETTING_FRAGMENT_MASS_TOLERANCE.getParam(fragmentError));

        // set the parent error
        Double minusValue = inputParams.getSpectrumParentMonoIsoMassErrorMinus();
        if (minusValue != null && minusValue > 0)
            params.getCvParam().add(DAOCvParams.SEARCH_SETTING_TOLERANCE_MINUS_VALUE.getParam(minusValue));

        Double plusValue = inputParams.getSpectrumParentMonoIsoMassErrorPlus();
        if (plusValue != null && plusValue > 0)
            params.getCvParam().add(DAOCvParams.SEARCH_SETTING_TOLERANCE_PLUS_VALUE.getParam(plusValue));

        // maximum missed cleavages
        Integer missedCleavages = inputParams.getScoringMissCleavageSites();
        if (missedCleavages != null)
            params.getCvParam().add(DAOCvParams.SEARCH_SETTING_MISSED_CLEAVAGES.getParam(missedCleavages));

        // check whether k-score was used
        if (xtandemFile.getXTandemParser().getInputParamMap().containsKey("SCORING_ALGORITHM")) {
            params.getUserParam().add(
                    new UserParam("X!Tandem scoring algorithm",
                            xtandemFile.getXTandemParser().getInputParamMap().get("SCORING_ALGORITHM"))
            );
        }

        return params;
    }

    @Override
    public Protocol getProtocol() {
        // not supported
        return null;
    }

    @Override
    public Collection<Reference> getReferences() {
        // not supported
        return null;
    }

    @Override
    public String getSearchDatabaseName() {
        String database = "";

        for (String source : sequenceSourceDescription.values())
            database += ((database.length() > 0) ? ", " : "") + source;

        return database;
    }

    @Override
    public String getSearchDatabaseVersion() {
        // cannot be extracted
        return "";
    }

    @Override
    public Collection<DatabaseMapping> getDatabaseMappings() {
        ArrayList<DatabaseMapping> mappings = new ArrayList<DatabaseMapping>();

        // in some cases there are no descriptions of the used search databases
        // in these cases the paths to the used databases is used as name.
        for (String path : sequenceSourceDescription.keySet()) {
            String source = sequenceSourceDescription.get(path);

            DatabaseMapping mapping = new DatabaseMapping();

            // don't use the description as it might not be unique
//          mapping.setSearchEngineDatabaseName(source != null ? source : path);
            mapping.setSearchEngineDatabaseName(path + (source != null ? " (" + source + ")" : ""));
            mapping.setSearchEngineDatabaseVersion("");

            mappings.add(mapping);
        }

        return mappings;
    }

    @Override
    public Collection<PTM> getPTMs() throws InvalidFormatException {
        // intialize the PTM collection
        ArrayList<PTM> ptms = new ArrayList<PTM>();

        ptms.addAll(convertModificationArray(modMap.getAllFixedModifications(), true));
        ptms.addAll(convertModificationArray(modMap.getAllVariableModifications(), false));

        return ptms;
    }

    /**
     * Converts the given ArrayList of xtandem-parser
     * Modifications into a Collection of PRIDE Converte
     * PTMs.
     *
     * @param modifications       An ArrayList of xtandem-parser Modifications to convert.
     * @param isFixedModification Indicates whether the given modifications are fixed
     * @return A Collection of PRIDE Converter PTMs
     * @throws InvalidFormatException
     */
    private Collection<PTM> convertModificationArray(ArrayList<Modification> modifications, boolean isFixedModification) throws InvalidFormatException {
        HashSet<String> ptmLabels = new HashSet<String>();

        // convert the modifications
        for (Modification m : modifications) {
            ptmLabels.add(m.getName());
        }

        // convert the labels to ptms
        ArrayList<PTM> ptms = new ArrayList<PTM>();

        for (String label : ptmLabels) {
            PTM p = new PTM();
            p.setSearchEnginePTMLabel(label);
            p.setFixedModification(isFixedModification);

            // set the residue
            String residue = label.substring(label.indexOf('@') + 1);

            if ("[".equals(residue))
                p.setResidues("0");
            else if ("]".equals(residue))
                p.setResidues("1");
            else
                p.setResidues(residue);

            // add the delta if it wasn't added before
            if (p.getModMonoDelta().size() < 1)
                p.getModMonoDelta().add(label.substring(0, label.indexOf('@')));

            ptms.add(p);
        }

        return ptms;
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
        // just create a set containing the 2 cvLookups used here
        ArrayList<CV> cvs = new ArrayList<CV>();

        cvs.add(new CV("MS", "PSI Mass Spectrometry Ontology", "1.2", "http://psidev.cvs.sourceforge.net/viewvc/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo"));
        cvs.add(new CV("PRIDE", "PRIDE Controlled Vocabulary", "1.101", "http://ebi-pride.googlecode.com/svn/trunk/pride-core/schema/pride_cv.obo"));

        return cvs;
    }

    @Override
    public int getSpectrumCount(boolean onlyIdentified) throws InvalidFormatException {
        if (spectraFileType == null)
            guessSpectraSourceType();

        // if only identified return the xtdanem identified count
        if (onlyIdentified || spectraFileType == SpectraType.INTERNAL)
            return xtandemFile.getSpectraNumber();

        // return the spectra dao's spec count
        return getSpectraDao().getSpectrumCount(false);
    }

    @Override
    public Iterator<Spectrum> getSpectrumIterator(boolean onlyIdentified) throws InvalidFormatException {
        if (spectraFileType == null)
            guessSpectraSourceType();

        // xtandem xml files don't support unidentified spectra
        if (spectraFileType == SpectraType.INTERNAL)
            return new XTandemDaoSpectrumIterator();

        if (!onlyIdentified)
            return getSpectraDao().getSpectrumIterator(false);

        // use the special identified DAO iterator
        return new IdentifiedDAOSpectrumIterator();
    }

    private class IdentifiedDAOSpectrumIterator implements Iterator<Spectrum>, Iterable<Spectrum> {
        /**
         * The identified spectra as array list
         */
        private ArrayList<Integer> identifiedSpectraArray;
        /**
         * Index in the identified spectra array list
         */
        private int identifiedSpecIndex = 0;
        /**
         * The DAO's iterator
         */
        private Iterator<Spectrum> daoIterator;

        public IdentifiedDAOSpectrumIterator() throws InvalidFormatException {
            daoIterator = getSpectraDao().getSpectrumIterator(false);

            // create the identified spectra array
            identifiedSpectraArray = new ArrayList<Integer>(identifiedSpectra);

            Collections.sort(identifiedSpectraArray);
        }

        @Override
        public Iterator<Spectrum> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            return identifiedSpecIndex < identifiedSpectraArray.size();
        }

        @Override
        public Spectrum next() {
            // get the 1-based index of the current spectra
            int currentSpecIndex = identifiedSpectraArray.get(identifiedSpecIndex++);

            Spectrum s = daoIterator.next();

            while (s.getId() < currentSpecIndex && daoIterator.hasNext())
                s = daoIterator.next();

            // make sure the spec was found
            if (s.getId() != currentSpecIndex)
                throw new ConverterException("Spectrum " + currentSpecIndex + " referenced in X!Tandem XML file does not exist in spectrum source file.");

            // return the spectrum            
            return s;
        }

        @Override
        public void remove() {
            // not supported
        }

    }

    /**
     * A wrapper class for xtandem-parser's spectrum iterator that
     * automatically converts the xtandem-spectra into PRIDE jaxb
     * spectra.
     *
     * @author jg
     */
    private class XTandemDaoSpectrumIterator implements Iterator<Spectrum>, Iterable<Spectrum> {
        /**
         * The xtandem-parser's spectrum iterator.
         */
        @SuppressWarnings("unchecked")
        private Iterator<de.proteinms.xtandemparser.xtandem.Spectrum> it = xtandemFile.getSpectraIterator();


        @Override
        public Iterator<Spectrum> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public Spectrum next() {
            // return the converted spectrum
            return convertXTandemSpectrum(it.next());
        }

        @Override
        public void remove() {
            // not supported
        }

    }

    /**
     * Converts a xtandem-parser spectrum into a PRIDE jaxb
     * spectrum. The PRIDE jaxb spectrum is given the same
     * id as the xtandem spectrum.
     *
     * @param xtandemSpec The xtandem-parser spectrum to convert
     * @return
     */
    private Spectrum convertXTandemSpectrum(de.proteinms.xtandemparser.xtandem.Spectrum xtandemSpec) {
        // create the spectrum
        Spectrum spectrum = new Spectrum();

        // get the supporting information
        SupportData supportData = xtandemFile.getSupportData(xtandemSpec.getSpectrumNumber());

        // convert the peak list to the required byte arrays
        ArrayList<Double> masses = supportData.getXValuesFragIonMass2Charge();
        ArrayList<Double> intensities = supportData.getYValuesFragIonMass2Charge();

        // create the byte arrays
        byte[] massesBytes = doubleCollectionToByteArray(masses);
        byte[] intenBytes = doubleCollectionToByteArray(intensities);

        // create the intensity array
        Data intenData = new Data();
        intenData.setEndian("little");
        intenData.setLength(intenBytes.length);
        intenData.setPrecision("64"); // doubles are 64 bit in java
        intenData.setValue(intenBytes);

        IntenArrayBinary intenArrayBin = new IntenArrayBinary();
        intenArrayBin.setData(intenData);

        // create the mass data array
        Data massData = new Data();
        massData.setEndian("little");
        massData.setLength(massesBytes.length);
        massData.setPrecision("64");
        massData.setValue(massesBytes);

        MzArrayBinary massArrayBinary = new MzArrayBinary();
        massArrayBinary.setData(massData);

        // store the mz and intensities in the spectrum
        spectrum.setIntenArrayBinary(intenArrayBin);
        spectrum.setMzArrayBinary(massArrayBinary);

        // initialize the spectrum description
        SpectrumDesc description = new SpectrumDesc();

        // create the spectrumSettings/spectrumInstrument (mzRangeStop, mzRangeStart, msLevel)
        SpectrumSettings settings = new SpectrumSettings();
        SpectrumInstrument instrument = new SpectrumInstrument();

        // don't set the MS level if internal spectra are being used as the MS level
        // cannot be reliably detected

        // sort the masses to get the minimum and max
        Collections.sort(masses);
        Float rangeStart = new Float(xtandemFile.getInputParameters().getSpectrumMinFragMz());
        Float rangeStop = new Float(masses.get(masses.size() - 1));

        instrument.setMzRangeStart(rangeStart);
        instrument.setMzRangeStop(rangeStop);

        // set the spectrum settings
        settings.setSpectrumInstrument(instrument);
        description.setSpectrumSettings(settings);

        // create the precursor list
        PrecursorList precList = new PrecursorList();

        // currently, there's only one precursor supported
        precList.setCount(1);

        Precursor prec = new Precursor();
        prec.setMsLevel(1);

        Spectrum spec = new Spectrum(); // the precursor spectrum (ref)
        spec.setId(0);
        prec.setSpectrum(spec);

        uk.ac.ebi.pride.jaxb.model.Param ionSelection = new uk.ac.ebi.pride.jaxb.model.Param();

        // add the different precursor parameters if they are available
        // calculate the actual m/z
        if (xtandemSpec.getPrecursorMh() != 0.0 && xtandemSpec.getPrecursorCharge() != 0) {
            Double charge = (double) xtandemSpec.getPrecursorCharge();
            Double mz = (xtandemSpec.getPrecursorMh() - 1 + charge) / charge;

            ionSelection.getCvParam().add(DAOCvParams.PRECURSOR_MZ.getJaxbParam(mz));
        }
        if (xtandemSpec.getPrecursorCharge() != 0)
            ionSelection.getCvParam().add(DAOCvParams.CHARGE_STATE.getJaxbParam(xtandemSpec.getPrecursorCharge()));

        // save the ionselection
        prec.setIonSelection(ionSelection);

        // no activation parameters supported in xtandem xml format
        prec.setActivation(new uk.ac.ebi.pride.jaxb.model.Param());

        // add the (only) precursor to the precursor list and save it in the description item
        precList.getPrecursor().add(prec);
        description.setPrecursorList(precList);

        spectrum.setSpectrumDesc(description);

        // set the spectrum's id
        spectrum.setId(xtandemSpec.getSpectrumId());

        return spectrum;
    }

    @Override
    public int getSpectrumReferenceForPeptideUID(String peptideUID) throws InvalidFormatException {
        //peptideUID is the [spec id] + "." + [domain key]

        // return the spectrum id (everything before the first ".")
        int index = peptideUID.indexOf('.');

        if (index == -1)
            throw new InvalidFormatException("Invalid peptideUID '" + peptideUID + "'");

        return Integer.parseInt(peptideUID.substring(0, index));
    }

    @Override
    public Identification getIdentificationByUID(String identificationUID) throws InvalidFormatException {
        return convertProteinToIdentification(identificationUID, false);
    }

    @Override
    public Iterator<Identification> getIdentificationIterator(
            boolean prescanMode) {
        return new XTandemIdentificationIterator(prescanMode);
    }

    /**
     * Iterator of all Identifications in the X!Tandem file.
     *
     * @author jg
     */
    private class XTandemIdentificationIterator implements Iterator<Identification>, Iterable<Identification> {
        ArrayList<String> proteinLabels;

        Iterator<String> proteinLabelIterator;

        private boolean prescanMode;

        public XTandemIdentificationIterator(boolean prescanMode) {
            this.prescanMode = prescanMode;

            // create the ArrayList of proteinIds
            proteinLabels = new ArrayList<String>(proteinHasPeptides.keySet());
            // sort them
            Collections.sort(proteinLabels);

            // set the iterator
            proteinLabelIterator = proteinLabels.iterator();
        }

        @Override
        public Iterator<Identification> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            return proteinLabelIterator.hasNext();
        }

        @Override
        public Identification next() {
            try {
                return convertProteinToIdentification(proteinLabelIterator.next(), prescanMode);
            } catch (InvalidFormatException e) {
                throw new ConverterException(e);
            }
        }

        @Override
        public void remove() {
            // not supported
        }

    }

    /**
     * Converts the protein identified by the given proteinLabel
     * into a PrideConverter Identification object.
     *
     * @param proteinLabel Identifies the protein to convert.
     * @param prescanMode  Indicates whether in prescan mode.
     * @return The created PRIDE Converter Identification object.
     * @throws InvalidFormatException
     */
    private Identification convertProteinToIdentification(String proteinLabel, boolean prescanMode) throws InvalidFormatException {
        // get the protein's peptides
        ArrayList<Peptide> peptides = proteinHasPeptides.get(proteinLabel);

        if (peptides == null)
            throw new InvalidFormatException("Protein '" + proteinLabel + "' does not contain any peptides.");

        // get the protein object
        Protein protein = xtandemFile.getProteinMap().getProtein(peptides.get(0).getDomains().get(0).getProteinKey());

        if (protein == null)
            throw new InvalidFormatException("Invalid protein id passed (" + peptides.get(0).getDomains().get(0).getProteinKey() + ")");

        // create the identification object
        Identification identification = new Identification();
        String accession = proteinLabel;
        // check if the proteinLabel contains the ":reversed" tag
        if (decoyPrefix.length() > 0 && accession.endsWith(":reversed")) {
            accession = decoyPrefix + accession.substring(0, proteinLabel.length() - 9);
        }
        // set the accession        
        identification.setAccession(accession);
        // set the unique identifier
        identification.setUniqueIdentifier(proteinLabel);

        // set the database by using the first peptide's sequence source
        String sourcePath = peptides.get(0).getFastaFilePath();
        if (!sequenceSourceDescription.containsKey(sourcePath))
            throw new InvalidFormatException("Unknown sequence source path encountered ('" + sourcePath + "'). Can't determine used sequence search database.");

        // don't use the description as it might not be unique
//      identification.setDatabase(sequenceSourceDescription.get(sourcePath) != null ? sequenceSourceDescription.get(sourcePath) : sourcePath);
        identification.setDatabase(sourcePath + (sequenceSourceDescription.get(sourcePath) != null ? " (" + sequenceSourceDescription.get(sourcePath) + ")" : ""));
        identification.setDatabaseVersion("");

        identification.setScore(0.0);
        identification.setThreshold(0.0);

        identification.setSearchEngine("X!Tandem");

        // add the peptides
        boolean isDecoy = decoyPrefix.length() > 0 && identification.getAccession().startsWith(decoyPrefix);
        List<uk.ac.ebi.pride.tools.converter.report.model.Peptide> convertedPeptides = convertPeptides(peptides, prescanMode, isDecoy);
        // if the protein has no peptides (threshold) return null
        if (convertedPeptides.size() < 1)
            return null;
        identification.getPeptide().addAll(convertedPeptides);

        // add the additional information (only in prescan mode)
        if (prescanMode) {
            Param additional = new Param();
//            additional.getCvParam().add(DAOCvParams.XTANDEM_EXPECT.getParam(protein.getExpectValue()));

            if (decoyPrefix.length() > 0 && identification.getAccession().startsWith(decoyPrefix))
                additional.getCvParam().add(DAOCvParams.DECOY_HIT.getParam());

            identification.setAdditional(additional);
        }

        return identification;
    }

    /**
     * Converts the given Collection of xtandem-parser peptides
     * into an ArrayList of PRIDE Converter peptides.
     *
     * @param peptides    The xtandem-parser peptides to convert.
     * @param prescanMode Indicates whether in prescan mode.
     * @return An ArrayList of PrideConverter peptides.
     */
    private List<uk.ac.ebi.pride.tools.converter.report.model.Peptide> convertPeptides(Collection<Peptide> peptides, boolean prescanMode, boolean isDecoy) {
        // intialize the return variable
        ArrayList<uk.ac.ebi.pride.tools.converter.report.model.Peptide> convertedPeptides = new ArrayList<uk.ac.ebi.pride.tools.converter.report.model.Peptide>();

        // process the peptides
        for (Peptide peptide : peptides) {
            // process the different domains
            for (Domain domain : peptide.getDomains()) {
                // make sure the domain is below the threshold
                if (domain.getDomainExpect() > expectThreshold)
                    continue;

                // create the converted peptide object
                uk.ac.ebi.pride.tools.converter.report.model.Peptide convertedPeptide = new uk.ac.ebi.pride.tools.converter.report.model.Peptide();

                // get the spectrum reference
                Long specRef = Long.parseLong(domain.getDomainID().substring(0, domain.getDomainID().indexOf('.')));

                // set the standard values
                convertedPeptide.setUniqueIdentifier(specRef + "." + domain.getDomainKey());
                convertedPeptide.setSequence(domain.getDomainSequence());
                convertedPeptide.setStart(domain.getDomainStart());
                convertedPeptide.setEnd(domain.getDomainEnd());

                // use the spectrum id as spectrum reference (extract from the domain id)
                convertedPeptide.setSpectrumReference(specRef);

                // check whether the peptide is unique (by checking whether it only fits one protein
                convertedPeptide.setIsSpecific(sequenceInAccessions.get(domain.getDomainSequence()).size() == 1);

                if (prescanMode) {
                    // add the PTMs
                    convertedPeptide.getPTM().addAll(getPeptidePTMs(domain));

                    // add the additional info
                    Param additional = new Param();
                    additional.getCvParam().add(DAOCvParams.CHARGE_STATE.getParam(xtandemFile.getSpectrum(peptide.getSpectrumNumber()).getPrecursorCharge()));
                    additional.getCvParam().add(DAOCvParams.XTANDEM_EXPECT.getParam(domain.getDomainExpect()));
                    additional.getCvParam().add(DAOCvParams.XTANDEM_HYPERSCORE.getParam(domain.getDomainHyperScore()));
                    additional.getCvParam().add(DAOCvParams.PRECURSOR_MH.getParam(domain.getDomainMh()));
                    if (domain.getUpFlankSequence() != null && !domain.getUpFlankSequence().contains("[") && !domain.getUpFlankSequence().contains("]"))
                        additional.getCvParam().add(DAOCvParams.UPSTREAM_FLANKING_SEQUENCE.getParam(domain.getUpFlankSequence()));
                    if (domain.getDownFlankSequence() != null && !domain.getDownFlankSequence().contains("[") && !domain.getDownFlankSequence().contains("]"))
                        additional.getCvParam().add(DAOCvParams.DOWNSTREAM_FLANKING_SEQUENCE.getParam(domain.getDownFlankSequence()));
                    additional.getCvParam().add(DAOCvParams.XTANDEM_DELTASCORE.getParam(domain.getDomainDeltaMh()));
                    if (isDecoy)
                        additional.getCvParam().add(DAOCvParams.DECOY_HIT.getParam());

                    convertedPeptide.setAdditional(additional);
                } else {
                    // add the fragment ions
                    List<uk.ac.ebi.pride.tools.converter.report.model.FragmentIon> fragmentIons = getPeptideFragmentIons(peptide, domain);

                    if (fragmentIons != null && fragmentIons.size() > 0)
                        convertedPeptide.getFragmentIon().addAll(fragmentIons);
                }

                convertedPeptides.add(convertedPeptide);
            }
        }

        return convertedPeptides;
    }

    /**
     * Returns a peptide's fragment ions as a List of
     * report model FragmentIons.
     *
     * @param peptide
     * @param domain
     * @return
     */
    private List<uk.ac.ebi.pride.tools.converter.report.model.FragmentIon> getPeptideFragmentIons(Peptide peptide, Domain domain) {
        // get the fragment ions
        @SuppressWarnings("unchecked")
        Vector<FragmentIon[]> ions = xtandemFile.getFragmentIonsForPeptide(peptide, domain);
        List<uk.ac.ebi.pride.tools.converter.report.model.FragmentIon> prideIons =
                new ArrayList<uk.ac.ebi.pride.tools.converter.report.model.FragmentIon>();

        // iterate over all ion types
        for (XTANDEM_FRAGMENT_IONS ionType : XTANDEM_FRAGMENT_IONS.getFragmentIonTypes()) {
            // get the array
            FragmentIon[] fragmentIons = ions.get(ionType.getIndex());

            if (fragmentIons == null)
                continue;

            // iterate over the found fragment ions
            for (FragmentIon fragmentIon : fragmentIons) {
                uk.ac.ebi.pride.tools.converter.report.model.FragmentIon prideIon =
                        new uk.ac.ebi.pride.tools.converter.report.model.FragmentIon();

                prideIon.getCvParam().add(DAOCvParams.PRODUCT_ION_CHARGE.getParam(new Double(fragmentIon.getCharge()).intValue()));
                // intensity - the intensity can not be properly set since the intensity stored in the
                // X!Tandem file is changed and does not correspond to the original intensity
                // to get the actual intensity one would have to load the original spectrum, parse
                // the peak list etc.
                if (useInternalSpectra)
                    prideIon.getCvParam().add(DAOCvParams.PRODUCT_ION_INTENSITY.getParam(fragmentIon.getIntensity()));
                else
                    prideIon.getCvParam().add(DAOCvParams.PRODUCT_ION_INTENSITY.getParam(0));
                // m/z
                double mz = fragmentIon.getMZ() + fragmentIon.getTheoreticalExperimentalMassError();
                prideIon.getCvParam().add(DAOCvParams.PRODUCT_ION_MZ.getParam(mz));
                // mass error
                prideIon.getCvParam().add(DAOCvParams.PRODUCT_ION_MASS_ERROR.getParam(
                        fragmentIon.getTheoreticalExperimentalMassError()));

                // set the name
                CvParam name = ionType.getIonTypeParam("" + fragmentIon.getNumber());
                if (name != null) prideIon.getCvParam().add(name);

                prideIons.add(prideIon);
            }
        }

        return prideIons;
    }

    /**
     * Returns an ArrayList of PeptidePTMs for the given
     * domain Id.
     *
     * @param peptide The peptide to convert the modifications for.
     * @return An ArrayList of PTMs
     */
    private ArrayList<PeptidePTM> getPeptidePTMs(Domain domain) {
        // initialize the PTMs
        ArrayList<PeptidePTM> ptms = new ArrayList<PeptidePTM>();

        // process the fixed modifications
        ArrayList<Modification> fixedMods = modMap.getFixedModifications(domain.getDomainKey());

        for (Modification mod : fixedMods)
            ptms.add(convertModification(domain, mod, true));

        // process the variable modifications
        ArrayList<Modification> varMods = modMap.getVariableModifications(domain.getDomainKey());

        for (Modification mod : varMods)
            ptms.add(convertModification(domain, mod, false));

        return ptms;
    }

    /**
     * Converts a xtandem-parser Modification into a
     * PeptidePTM object.
     *
     * @param domain       The peptide the modification is from.
     * @param modification The modification object to convert.
     * @param isFixed      Indicates whether it's a fixed moficiation
     * @return The converted PeptidePTM object
     */
    private PeptidePTM convertModification(Domain domain, Modification modification, boolean isFixed) {
        PeptidePTM ptm = new PeptidePTM();

        ptm.setSearchEnginePTMLabel(modification.getName());
        ptm.setFixedModification(isFixed);
        // modification positions are set relative to the protein sequence
        ptm.setModLocation(Long.parseLong(modification.getLocation()) - domain.getDomainStart() + 1);
        // set the mass always as monoisotopic mass
        ptm.getModMonoDelta().add(new Double(modification.getMass()).toString());

        return ptm;
    }
}
