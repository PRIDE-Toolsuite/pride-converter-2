package uk.ac.ebi.pride.tools.converter.dao_omssa_txt;

import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.dao.DAO;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.dao.DAOProperty;
import uk.ac.ebi.pride.tools.converter.dao.impl.*;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.filters.EValueFilterCriteria;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.filters.FilterCriteria;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.model.OmssaPeptide;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.model.OmssaProtein;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.parsers.OmssaIdentificationsParser;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.parsers.OmssaIdentificationsParserResult;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.properties.ScoreCriteria;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.properties.SupportedProperty;
import uk.ac.ebi.pride.tools.converter.report.model.*;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.FileUtils;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * NOTE (something to keep in mind):
 * Using an internal file index the DAO stores number lines for each identification in the passed result file. Doing so,
 * it doesn't keep the whole thing in memory (something that can be really big). The positions are used in the very last
 * moment to built the identifications in the convertIdentification method.
 * <p/>
 * About score criteria valid for Omssa DAO: right now we support eValue for threshold based filtering.
 *
 * @author Jose A. Dianes
 * @version $Id$
 */
public class OmssaTxtDao extends AbstractDAOImpl implements DAO {

    private static final String PLEASE_SELECT = "Please select";

    private enum SpectraType {
        /**
         * Use the identified peaks as spectrum
         */
        PKL,
        DTA,
        MGF,
        MZXML,
        MS2,
        MZML
    }

    /**
     * The input target OMSSA-txt file.
     */
    private final File targetFile;

    /**
     * The input target OMSSA mods.xml file.
     */
    private File modFile;

    /**
     * The input target OMSSA usermods.xml file.
     */
    private File usermodFile;

    /**
     * File header
     */
    private Map<String, Integer> header;

    /**
     * Target file index: here we store number lines for each identification in the result file. Doing so, we don't keep
     * the whole thing in memory (something that can be really big). The positions are used in the very last moment to
     * built the identifications in the convertIdentification method.
     */
    private ArrayList<String[]> targetFileIndex;

    /**
     * The proteins found in the OMSSA-txt target file
     */
    private Map<String, OmssaProtein> proteins;

    /**
     * The spectra file
     */
    private File spectraFile;

    /**
     * The spectra file type
     */
    private SpectraType spectraFileType;

    /**
     * DAO used to parse the corresponding peak list file.
     */
    private AbstractPeakListDAO spectraDAO;

    /**
     * List of spectra ids that were identified
     */
    private List<Integer> identifiedSpecIds;

//    /**
//     * Spectra by Omssa accession (Peptide UID) map
//     */
//    private Map<String, Integer> peptideUIDToSpectraMap;
//

    /**
     * Contains all the supportedProperties of this DAO (get/setConfiguration)
     */
    private Properties supportedProperties;

    /**
     * Filter object built from supportedProperties
     */
    private FilterCriteria filter;

    /**
     * User defined PTMs for this search
     */
    private Map<Character, Double> fixedPtms = new HashMap<Character, Double>();

    /**
     * all observed PTMs
     */
    private Map<String, PTM> allPTms;

    /**
     * for lazy initialization
     */
    private boolean isParsed = false;

    /**
     * Main constructor. Will parse the result .xls file and create the proper internal data structures
     *
     * @param resultFile
     */
    public OmssaTxtDao(File resultFile) {
        this.targetFile = resultFile;
    }

    @SuppressWarnings("rawtypes")
    public static Collection<DAOProperty> getSupportedProperties() {

        List<DAOProperty> supportedProperties = new ArrayList<DAOProperty>();

        // Threshold
        DAOProperty<String> threshold = new DAOProperty<String>(SupportedProperty.THRESHOLD.getName(), null);
        threshold.setDescription("Allows filtering identifications.");
        supportedProperties.add(threshold);

        // Score criteria
        DAOProperty<String> scoreCriteria = new DAOProperty<String>(SupportedProperty.SCORE_CRITERIA.getName(), ScoreCriteria.E_VALUE.getName());
        scoreCriteria.setDescription("Defines the criteria for ordering and filtering identifications.");
        supportedProperties.add(scoreCriteria);

        // CORE PTMs
        DAOProperty<File> modsFileProperty = new DAOProperty<File>(SupportedProperty.MOD_FILE.getName(), new File(PLEASE_SELECT));
        modsFileProperty.setDescription("The full path of the OMSSA mods.xml file");
        modsFileProperty.setRequired(true);
        supportedProperties.add(modsFileProperty);

        // User-defined PTMs
        DAOProperty<File> usermodsFileProperty = new DAOProperty<File>(SupportedProperty.USERMOD_FILE.getName(), new File(PLEASE_SELECT));
        usermodsFileProperty.setDescription("The full path of the OMSSA usermods.xml file");
        supportedProperties.add(usermodsFileProperty);

        // Fixed PTMs
        DAOProperty<String> fixedPtmsProperty = new DAOProperty<String>(SupportedProperty.FIXED_PTMS.getName(), "57.0214@C");
        fixedPtmsProperty.setDescription("Comma separated list of fixed modifications in the format: mass-delta@AA");
        supportedProperties.add(fixedPtmsProperty);

        return supportedProperties;
    }

    /**
     * Sets the supportedProperties associated to this DAO
     *
     * @param props The supportedProperties to be associated with the DAO
     */
    public void setConfiguration(Properties props) {
        supportedProperties = props;

        // set member supportedProperties here using supportedProperties object
        /*
      Threshold property value: allows ignoring all identifications under/over the value (depending on scoreCriteria)
     */
        String threshold = props.getProperty(SupportedProperty.THRESHOLD.getName());
        /*
      Score criteria item property: allows ordering and further filtering of identifications
     */
        String scoreCriteria = props.getProperty(SupportedProperty.SCORE_CRITERIA.getName());

        // Create the filter object from the supportedProperties
        if (ScoreCriteria.E_VALUE.getName().equals(scoreCriteria)) {
            filter = new EValueFilterCriteria();
            try {
                filter.setThreshold(Double.parseDouble(threshold));
            } catch (Exception e) {
                filter.setThreshold(0.0);
            }
        } else {   // default filter actually does nothing
            filter = new EValueFilterCriteria();
            filter.setThreshold(0.0);
        }

        // process fixed PTMs
        String fixedPtmsProperty = props.getProperty(SupportedProperty.FIXED_PTMS.getName());
        if (fixedPtmsProperty != null) {
            String[] fixedPtms = fixedPtmsProperty.split(",");
            for (String fixedPtm : fixedPtms) {
                String[] ptmTokens = fixedPtm.trim().split("@");
                this.fixedPtms.put(ptmTokens[1].charAt(0), Double.parseDouble(ptmTokens[0]));
            }
        }

        // set mods.xml file
        String ptmFileStr = props.getProperty(SupportedProperty.MOD_FILE.getName());
        if (ptmFileStr == null || "".equals(ptmFileStr.trim())) {
            throw new ConverterException("OMSSA PTM File not set");
        }
        modFile = new File(ptmFileStr);
        if (!modFile.exists()) {
            throw new ConverterException("OMSSA mods.xml file does not exist: " + ptmFileStr);
        }

        // set usermods.xml file
        ptmFileStr = props.getProperty(SupportedProperty.USERMOD_FILE.getName());
        if (ptmFileStr != null && !("".equals(ptmFileStr.trim()) || ptmFileStr.equals(PLEASE_SELECT))) {
            usermodFile = new File(ptmFileStr);
            if (!usermodFile.exists()) {
                throw new ConverterException("OMSSA usermods.xml does not exist: " + ptmFileStr);
            }
        }

    }


    /**
     * Gets the supportedProperties associated with this DAO
     *
     * @return The supportedProperties associated with the DAO
     */
    public Properties getConfiguration() {
        return supportedProperties;
    }

    /**
     * Sets the spectra file associated with this set of files.
     *
     * @throws ConverterException if file does not exist or not MGF format
     */
    public void setExternalSpectrumFile(String s) {
        spectraFile = new File(s);
        if (!spectraFile.exists()) throw new ConverterException("Spectra file does not exist");
        try {
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
        } catch (InvalidFormatException e) {
            throw new ConverterException("Spectra file type unknown");
        }


    }

    /**
     * @return
     * @throws InvalidFormatException
     */
    public Collection<PTM> getPTMs() throws InvalidFormatException {

        //lazy load data
        lazyInitialize();
        return allPTms.values();
    }


    /**
     * @return Not defined
     * @throws InvalidFormatException
     */
    public String getExperimentTitle() throws InvalidFormatException {
        return "Unknown Omssa based experiment";
    }

    /**
     * @return
     */
    public String getExperimentShortLabel() {
        return null;
    }

    /**
     * @return
     * @throws InvalidFormatException
     */
    public Param getExperimentParams() throws InvalidFormatException {
        // initialize the collection to hold the params
        Param params = new Param();

        // original MS format param
        params.getCvParam().add(DAOCvParams.ORIGINAL_MS_FORMAT.getParam("Omssa .csv file"));
        params.getCvParam().add(DAOCvParams.MS_MS_SEARCH.getParam());

        return params;
    }

    /**
     * @return
     */
    public String getSampleName() {
        return null;
    }

    /**
     * @return
     */
    public String getSampleComment() {
        return null;
    }

    /**
     * @return
     * @throws InvalidFormatException
     */
    public Param getSampleParams() throws InvalidFormatException {
        return new Param();
    }

    /**
     * @return
     * @throws InvalidFormatException
     */
    public SourceFile getSourceFile() throws InvalidFormatException {
        // initialize the return variable
        SourceFile file = new SourceFile();

        file.setPathToFile(targetFile.getAbsolutePath());
        file.setNameOfFile(targetFile.getName());
        file.setFileType("OMSSA .csv file");

        return file;
    }

    /**
     * @return
     */
    public Collection<Contact> getContacts() {
        return null;
    }

    /**
     * @return
     */
    public InstrumentDescription getInstrument() {
        return null;
    }

    /**
     * @return
     * @throws InvalidFormatException
     */
    public Software getSoftware() throws InvalidFormatException {
        Software s = new Software();
        s.setName("OMSSA");
        s.setVersion("unknown");
        return s;
    }

    /**
     * @return
     */
    public Param getProcessingMethod() {
        return new Param();
    }

    /**
     * @return null for this DAO
     */
    public Protocol getProtocol() {
        return null;
    }

    /**
     * @return
     */
    public Collection<Reference> getReferences() {
        return null;
    }

    /**
     * @return
     * @throws InvalidFormatException
     */
    public String getSearchDatabaseName() throws InvalidFormatException {
        return "Unknown database";
    }

    /**
     * @return
     * @throws InvalidFormatException
     */
    public String getSearchDatabaseVersion() throws InvalidFormatException {
        return "Unknown";
    }

    /**
     * @return
     * @throws InvalidFormatException
     */
    public Collection<DatabaseMapping> getDatabaseMappings()
            throws InvalidFormatException {
        ArrayList<DatabaseMapping> mappings = new ArrayList<DatabaseMapping>(1);

        DatabaseMapping mapping = new DatabaseMapping();

        mapping.setSearchEngineDatabaseName("Unknown database");
        mapping.setSearchEngineDatabaseVersion("Unknown");

        mappings.add(mapping);

        return mappings;
    }

    /**
     * @return
     * @throws InvalidFormatException
     */
    public SearchResultIdentifier getSearchResultIdentifier()
            throws InvalidFormatException {
        // intialize the search result identifier
        SearchResultIdentifier identifier = new SearchResultIdentifier();

        // format the current time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        identifier.setSourceFilePath(targetFile.getAbsolutePath());
        identifier.setTimeCreated(formatter.format(new Date(System.currentTimeMillis())));
        identifier.setHash(FileUtils.MD5Hash(targetFile.getAbsolutePath()));

        return identifier;
    }

    /**
     * @return
     * @throws InvalidFormatException
     */
    public Collection<CV> getCvLookup() throws InvalidFormatException {
        // just create a set containing the 2 cvLookups used here
        ArrayList<CV> cvs = new ArrayList<CV>();

        cvs.add(new CV("MS", "PSI Mass Spectrometry Ontology", "1.2", "http://psidev.cvs.sourceforge.net/viewvc/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo"));
        cvs.add(new CV("PRIDE", "PRIDE Controlled Vocabulary", "1.101", "http://ebi-pride.googlecode.com/svn/trunk/pride-core/schema/pride_cv.obo"));

        return cvs;
    }

    /**
     * Only load information from the source file if required
     */
    private void lazyInitialize() {

        if (!isParsed) {
            // parse the csv file
            OmssaIdentificationsParser parser = new OmssaIdentificationsParser(targetFile, modFile, usermodFile, fixedPtms);
            OmssaIdentificationsParserResult parsingResult = parser.parse();
            header = parsingResult.getHeader();
            proteins = parsingResult.getProteins();
            identifiedSpecIds = parsingResult.getIdentifiedSpectraTitles();
            targetFileIndex = parsingResult.getFileIndex();
            allPTms = parsingResult.getPtms();
            isParsed = true;

        }

    }

    /**
     * @param onlyIdentified
     * @return
     * @throws InvalidFormatException
     */
    @Override
    public int getSpectrumCount(boolean onlyIdentified)
            throws InvalidFormatException {

        //lazy load data
        lazyInitialize();

        if (spectraFileType == null)
            guessSpectraSourceType();

        // if only identified return the identified count
        if (!onlyIdentified)
            return getSpectraDao().getSpectrumCount(false);

        // return the spectra dao's spec count
        return this.identifiedSpecIds.size();
    }

    /**
     * @param onlyIdentified
     * @return
     * @throws InvalidFormatException
     */
    public Iterator<Spectrum> getSpectrumIterator(boolean onlyIdentified)
            throws InvalidFormatException {
        //lazy load data
        lazyInitialize();

        if (spectraFileType == null)
            guessSpectraSourceType();

        if (!onlyIdentified)
            return getSpectraDao().getSpectrumIterator(false);

        // use the special identified DAO iterator
        return new OnlyIdentifiedSpectrumIterator();
    }

    /**
     * @param peptideUID
     * @return
     * @throws InvalidFormatException
     */
    public int getSpectrumReferenceForPeptideUID(String peptideUID)
            throws InvalidFormatException {
        //lazy load data
        lazyInitialize();

        String[] items = peptideUID.split("_");
        return Integer.parseInt(items[0]);
    }

    /**
     * @param identificationUID
     * @return
     * @throws InvalidFormatException
     */
    public Identification getIdentificationByUID(String identificationUID)
            throws InvalidFormatException {
        //lazy load data
        lazyInitialize();

        OmssaProtein protein;
        protein = proteins.get(identificationUID);
        if (protein == null)
            throw new InvalidFormatException("Protein with UID=" + identificationUID + " does not exist");
        return convertIdentification(protein);
    }

    /**
     * @param prescanMode
     * @return
     * @throws InvalidFormatException
     */
    public Iterator<Identification> getIdentificationIterator(boolean prescanMode) throws InvalidFormatException {
        //lazy load data
        lazyInitialize();

        return new OmssaIdentificationIterator();
    }


    /**
     * Redefine the iterator in order to not to keep all the Identification objects in memory (there are quite heavy
     * and can be quite a lot)
     * Because we have two different target files (target and decoy), and because we don't want to mix them due to the
     * last-moment prefixing system we use, we need here two iterators that we made appear as one.
     */
    private class OmssaIdentificationIterator implements Iterator<Identification> {
        private final Iterator<String> accessionIterator = proteins.keySet().iterator();

        public OmssaIdentificationIterator() {
        }

        public boolean hasNext() {
            return accessionIterator.hasNext();
        }

        public Identification next() {
            return convertIdentification(proteins.get(accessionIterator.next()));
        }

        public void remove() {
            // not supported
        }
    }

    /**
     * Converts from our internal OmssaProtein to the DAO representation
     *
     * @param protein
     * @return
     */
    private Identification convertIdentification(OmssaProtein protein) {

        Identification identification = new Identification();

        identification.setAccession(protein.getAccession());
        identification.setScore(0.0);
        identification.setThreshold(0.0);
        identification.setDatabase("Unknown database");
        identification.setDatabaseVersion("Unknown");
        identification.setUniqueIdentifier(protein.getAccession());

        identification.setSearchEngine("OMSSA");

        // process the peptides
        String[] fields;
        for (Integer omssaPeptideStringIndex : protein.getPeptides()) {

            fields = this.targetFileIndex.get(omssaPeptideStringIndex);  // split the columns

            // Check if the entry pass the filter. Otherwise, go for the next line
            if ((this.filter == null) || filter.passFilter(this.header, fields)) {
                // process the peptide
                OmssaPeptide omssaPeptide = OmssaIdentificationsParser.createOmssaPeptide(fields, this.header);

                Peptide peptide = new Peptide();

                peptide.setSequence(omssaPeptide.getPeptide().toUpperCase());
                int peptideSpectraindex = omssaPeptide.getSpectrumNumber();

                if (peptideSpectraindex == -1)
                    throw new ConverterException("Spectrum reference does not exist or is ambiguous for peptide " + peptide.getSequence());
                else {
                    //TODO the spectrum offsets returned by jmzreader are off wrt those returned by omssa
                    //TODO - RC added the +1!!!
                    peptide.setSpectrumReference(peptideSpectraindex + 1);
                    //TODO - RC added the +1!!!
                    peptide.setUniqueIdentifier((peptideSpectraindex + 1) + "_" + omssaPeptide.getAccession());
                    peptide.setStart(omssaPeptide.getStart());
                    peptide.setEnd(omssaPeptide.getStop());

                    // add the additional info
                    Param additional = new Param();

                    if (omssaPeptide.getCharge() > 0) {
                        additional.getCvParam().add(DAOCvParams.CHARGE_STATE.getParam(omssaPeptide.getCharge()));
                        additional.getCvParam().add(DAOCvParams.PRECURSOR_MZ.getParam(omssaPeptide.getMass() / omssaPeptide.getCharge()));
                    }
                    additional.getCvParam().add(DAOCvParams.OMSSA_E_VALUE.getParam(omssaPeptide.geteValue()));
                    additional.getCvParam().add(DAOCvParams.OMSSA_P_VALUE.getParam(omssaPeptide.getpValue()));

                    peptide.setAdditional(additional);

                    // add the PTMs - check for variable mods
                    if (omssaPeptide.getMods() != null && !"".equals(omssaPeptide.getMods().trim())) {

                        //oxidation of M:1 ,oxidation of M:10
                        //phosphorylation of Y:7

                        String[] mods = omssaPeptide.getMods().split(",");
                        for (String modStr : mods) {

                            String modName = modStr.substring(0, modStr.indexOf(":")).trim();
                            String modPositionStr = modStr.substring(modStr.indexOf(":") + 1).trim();
                            int modPosition = -1;
                            if (!"".equals(modPositionStr)) {
                                modPosition = Integer.parseInt(modPositionStr);
                            }

                            if (modPosition < 0) {
                                throw new ConverterException("Invalid modification position for mod: " + modStr);
                            }

                            PeptidePTM pepPTM = new PeptidePTM();
                            pepPTM.setModLocation(modPosition);
                            pepPTM.setSearchEnginePTMLabel(modName);
                            pepPTM.setResidues("" + peptide.getSequence().charAt(modPosition - 1));

                            PTM predefinedPTM = allPTms.get(modName);

                            if (predefinedPTM != null) {
                                pepPTM.setAdditional(predefinedPTM.getAdditional());
                                pepPTM.getModMonoDelta().addAll(predefinedPTM.getModMonoDelta());
                                pepPTM.setModName(predefinedPTM.getModName());
                            } else {
                                throw new ConverterException("PTM not processed during parsing: " + modName);
                            }

                            peptide.getPTM().add(pepPTM);
                        }
                    }

                    // add the PTMs - check for fixed mods
                    for (int i = 0; i < peptide.getSequence().length(); i++) {
                        Character c = peptide.getSequence().charAt(i);
                        if (fixedPtms.containsKey(c)) {

                            Double massDelta = fixedPtms.get(c);

                            PeptidePTM pepPTM = new PeptidePTM();
                            pepPTM.setModLocation(i + 1);
                            pepPTM.setSearchEnginePTMLabel(massDelta.toString() + "@" + c);
                            pepPTM.setResidues(c.toString());
                            pepPTM.getModMonoDelta().add(massDelta.toString());
                            pepPTM.setModName(massDelta.toString() + "@" + c);

                            peptide.getPTM().add(pepPTM);

                        }
                    }

                    identification.getPeptide().add(peptide);
                }
            }
        }

        if (identification.getPeptide() == null || identification.getPeptide().size() <= 0)
            return null;
        else
            return identification;

    }

    /**
     *
     */
    private class OnlyIdentifiedSpectrumIterator implements Iterator<Spectrum> {
        private Iterator<Integer> specIdIterator;
        private Iterator<Spectrum> specIterator;

        public OnlyIdentifiedSpectrumIterator() throws InvalidFormatException {
            specIterator = spectraDAO.getSpectrumIterator(false);
            Collections.sort(identifiedSpecIds);
            specIdIterator = identifiedSpecIds.iterator();
        }

        public boolean hasNext() {
            return specIdIterator.hasNext();
        }

        public Spectrum next() {
            Integer id = specIdIterator.next();

            //TODO the spectrum offsets returned by jmzreader are off wrt those returned by omssa
            //TODO - RC added the +1!!!
            Integer offsetId = new Integer(id + 1);

            Spectrum s = specIterator.next();

            while (s.getId() != offsetId) {
                s = specIterator.next();
            }

            return s;
        }

        public void remove() {
            // not supported
        }
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
            case MS2:
                spectraDAO = new Ms2DAO(spectraFile);
                break;
        }

        return spectraDAO;
    }

    /**
     * Guesses the type of spectra file used
     * and sets spectrFileType accordingly.
     *
     * @throws InvalidFormatException
     */
    private void guessSpectraSourceType() throws InvalidFormatException {
        spectraFileType = null;

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
        else if (filename.toLowerCase().endsWith("ms2"))
            spectraFileType = SpectraType.MS2;
        else if (filename.toLowerCase().endsWith("mzml"))
            spectraFileType = SpectraType.MZML;

        // make sure the type was set correctly
        if (spectraFileType == null)
            throw new InvalidFormatException("Unsupported spectra file type used (" + filename + ")");
    }

}