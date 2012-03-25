package uk.ac.ebi.pride.tools.converter.omx_dao;

import de.proteinms.omxparser.OmssaOmxFile;
import de.proteinms.omxparser.util.*;
import org.apache.log4j.Logger;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.text.SimpleDateFormat;
import java.util.*;
import uk.ac.ebi.pride.jaxb.model.*;
import uk.ac.ebi.pride.tools.converter.dao.DAO;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.dao.DAOProperty;
import uk.ac.ebi.pride.tools.converter.dao.impl.AbstractDAOImpl;
import uk.ac.ebi.pride.tools.converter.report.model.Contact;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.Identification;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.Protocol;
import uk.ac.ebi.pride.tools.converter.report.model.Reference;
import uk.ac.ebi.pride.tools.converter.report.model.*;
import uk.ac.ebi.pride.tools.converter.report.model.FragmentIon;
import uk.ac.ebi.pride.tools.converter.report.model.Software;
import uk.ac.ebi.pride.tools.converter.report.model.SourceFile;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.FileUtils;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;

/**
 * OmxDAO using the OMSSA Parser library to convert OMSSA OMX files into PRIDE
 * XML files.
 *
 * @author Harald Barsnes
 */
public class OmxDAO extends AbstractDAOImpl implements DAO {

    /**
     * log4j logger object
     */
    private Logger logger = Logger.getLogger(OmxDAO.class);
    /**
     * String identifying the mascot search engine.
     */
    private final String searchEngineString = "OMSSA";
    /**
     * The OMSSA OMX file.
     */
    private OmssaOmxFile omssaOmxFile;
    /**
     * File representing the actual source file on the filesystem.
     */
    private File sourcefile;
    /**
     * Properties object. Initially an empty object. Can be overwritten by
     * setProperties.
     */
    private Properties properties = new Properties();
    /**
     * Maps the omssa modification ids (which are integers) to the modification
     * details in the mods.xml and usermods.xml files.
     */
    HashMap<Integer, OmssaModification> omssaModificationDetails;
    /**
     * Scale to change m/z float to integer. Note: defaults to 100 if not found
     * (as described in the OMSSA xsd file).
     */
    private int omssaResponseScale;
    /**
     * The MS/MS mass tolerance.
     */
    private double ionCoverageErrorMargin;
    /**
     * Collection to hold all supported properties by this DAO.
     */
    private static Collection<DAOProperty> supportedProperties;
    /**
     * A link to the selected MSResponse object. Only one object is assumed and
     * thus selected.
     */
    private MSResponse msResponse;
    /**
     * AA link to the selected MSRequest object. Only one object is assumed and
     * thus selected.
     */
    private MSRequest msRequest;
    /**
     * The current spectrum counter. -1 if not calculated.
     */
    private int spectrumCounter = -1;
    /**
     * The protein accession decoy prefix to use.
     */
    private String decoyPrefix = "DECOY_";

    /**
     * Just a list of supported properties to keep thing's a little cleaner.
     *
     * @author jg
     */
    private enum SupportedProperties {

        // NAME	DEFAULT	TYPE
        COMPATIBILITY_MODE("compatibility_mode", true, OmxDAO.SupportedProperties.TYPE.BOOLEAN);
        private String name;
        private Object defaultValue;
        private TYPE type;

        public enum TYPE {

            STRING, BOOLEAN, DOUBLE;
        }

        private SupportedProperties(String name, Object defaultValue, TYPE type) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }

        public TYPE getType() {
            return type;
        }

        @Override
        public String toString() {
            return name + " (" + defaultValue.toString() + ")";
        }
    }

    /**
     * Generates the collection of supported properties if it wasn't created
     * before.
     */
    private static void generateSupportedProperties() {

        // if the collection was already created, return
        if (supportedProperties != null) {
            return;
        }

        // create a new collection
        supportedProperties = new ArrayList<DAOProperty>(7);

        // compatibility mode
        DAOProperty<Boolean> compMode = new DAOProperty<Boolean>(SupportedProperties.COMPATIBILITY_MODE.getName(), (Boolean) SupportedProperties.COMPATIBILITY_MODE.getDefaultValue());
        compMode.setDescription("If set to true (default) the precuror charge will also be reported at the spectrum level using the best ranked peptide's charge state. This might lead to wrong precursor charges being reported. The correct charge state is always additionally reported at the peptide level.");
        supportedProperties.add(compMode);
    }

    /**
     * Returns the current value for the given property.
     *
     * @param property The property to get the current value for.
     * @return An Object representing the property's current value.
     */
    private Object getCurrentProperty(SupportedProperties property) {
        // check if the property was overwritten
        if (properties.containsKey(property.getName())) {
            // save the value
            String value = properties.getProperty(property.getName());

            // parse the property depending on its type (default is string)
            switch (property.getType()) {
                case BOOLEAN:
                    return Boolean.parseBoolean(value);
                case DOUBLE:
                    return Double.parseDouble(value);
                case STRING:
                default:
                    return value;
            }

        } else {
            return property.getDefaultValue();
        }
    }

    /**
     * Used to retrieve the list of supported properties. Properties should
     * nevertheless be set using the setConfiguration method.
     *
     * @return A collection of supported properties.
     */
    public static Collection<DAOProperty> getSupportedPorperties() {
        // generate the supported properties (if they weren't created yet)
        generateSupportedProperties();
        return supportedProperties;
    }

    /**
     * Detault constructor. Expects the omssa result file as parameter.
     *
     * @param resultFile the OMSSA OMX file
     * @param modsFile the mods.xml file
     * @param userModsFile the usermods.xml file
     */
    public OmxDAO(File resultFile, File modsFile, File userModsFile) {

        // parse the OMSSA file
        omssaOmxFile = new OmssaOmxFile(resultFile.getAbsolutePath(), modsFile.getAbsolutePath(), userModsFile.getAbsolutePath());

        // parse the modification files
        omssaModificationDetails = omssaOmxFile.getModifications();

        // get the selected MSResponse and MSRequest objects
        if (omssaOmxFile.getParserResult().MSSearch_response.MSResponse.isEmpty()) {
            throw new IllegalArgumentException("Omx file does not contain any MSResponse objects!");
        }

        if (omssaOmxFile.getParserResult().MSSearch_request.MSRequest.isEmpty()) {
            throw new IllegalArgumentException("Omx file does not contain any MSRequest objects!");
        }

        msResponse = omssaOmxFile.getParserResult().MSSearch_response.MSResponse.get(0); // @TODO: what about more than one MSResponse??
        msRequest = omssaOmxFile.getParserResult().MSSearch_request.MSRequest.get(0); // @TODO: what about more than one MSRequest??

        // get some general ommsa parameters needed later 
        omssaResponseScale = msResponse.MSResponse_scale;
        ionCoverageErrorMargin = msRequest.MSRequest_settings.MSSearchSettings.MSSearchSettings_msmstol;
    }

    @Override
    public void setConfiguration(Properties props) {
        properties = props;
    }

    @Override
    public Properties getConfiguration() {
        return properties;
    }

    @Override
    public String getExperimentTitle() {
        // not supported
        return "Unknown OMSSA experiment";
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

        // date of search
        // @TODO: date of search not found in OMSSA omx files? perhaps the data the omx file was created can be used?
        //params.getCvParam().add(new CvParam("PRIDE", "PRIDE:0000219", "Date of search", ??));

        // set the peaks used
        params.getCvParam().add(new CvParam("PRIDE", "PRIDE:0000218", "Original MS data file format", "Unknown (OMSSA internal peaks used)"));

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
        // SampleParams cannot be extracted from OMSSA files
        return new Param();
    }

    @Override
    public SourceFile getSourceFile() {

        // initialize the return variable
        SourceFile file = new SourceFile();

        file.setPathToFile(sourcefile.getAbsolutePath());
        file.setNameOfFile(sourcefile.getName());
        file.setFileType("OMSSA OMX file");

        return file;
    }

    @Override
    public Collection<Contact> getContacts() {
        // this function is not available for omssa omx files
        return null;
    }

    @Override
    public InstrumentDescription getInstrument() {
        // this function is not available for omssa omx files 
        return null;
    }

    @Override
    public Software getSoftware() {
        // initialize the software item
        Software software = new Software();

        software.setName(searchEngineString);
        software.setVersion(msResponse.MSResponse_version);

        return software;
    }

    @Override
    public Param getProcessingMethod() {
        // this function is not available for omssa omx files
        return null;
    }

    @Override
    public Protocol getProtocol() {
        // this function is not available for omssa omx files
        return null;
    }

    @Override
    public Collection<Reference> getReferences() {
        // this function is not available for omssa omx files
        return null;
    }

    @Override
    public String getSearchDatabaseName() {
        return "" + msRequest.MSRequest_settings.MSSearchSettings.MSSearchSettings_db;
    }

    @Override
    public String getSearchDatabaseVersion() {
        return "" + msResponse.MSResponse_dbversion;
    }

    @Override
    public Collection<PTM> getPTMs() {

        // intialize the PTM collection
        ArrayList<PTM> ptms = new ArrayList<PTM>();
        ArrayList<String> allLabels = new ArrayList<String>();

        // get the list of fixed modifications
        List<Integer> fixedModifications =
                msRequest.MSRequest_settings.MSSearchSettings.MSSearchSettings_fixed.MSMod;

        // parse the fixed modifications
        for (int i = 0; i < fixedModifications.size(); i++) {

            PTM p = new PTM();
            OmssaModification omssaMod = omssaModificationDetails.get(fixedModifications.get(i));

            String label = omssaMod.getModName();

            if (!allLabels.contains(label)) {
                p.setSearchEnginePTMLabel(label);
                p.setFixedModification(true);
                p.getModMonoDelta().add(omssaMod.getModMonoMass().toString());

                int modType = omssaMod.getModType();
                String tempResidues = omssaMod.getModResiduesAsCompactString();

                switch (modType) {
                    case 0:
                        p.setResidues(tempResidues);
                        break;
                    case 1:
                    case 2:
                    case 5:
                    case 6:
                        p.setResidues("0");
                        break;
                    case 3:
                    case 4:
                    case 7:
                    case 8:
                        p.setResidues("1");
                        break;
                }

                ptms.add(p);
                allLabels.add(label);
            } else {
                throw new IllegalStateException("Duplicate fixed ptm label: " + label + "!");
            }
        }


        // get the list of fixed modifications
        List<Integer> variableModifications =
                msRequest.MSRequest_settings.MSSearchSettings.MSSearchSettings_variable.MSMod;

        // parse the variable modifications
        for (int i = 0; i < variableModifications.size(); i++) {

            PTM p = new PTM();
            OmssaModification omssaMod = omssaModificationDetails.get(variableModifications.get(i));

            String label = omssaMod.getModName();

            if (!allLabels.contains(label)) {
                p.setSearchEnginePTMLabel(label);
                p.setFixedModification(false);
                p.getModMonoDelta().add(omssaMod.getModMonoMass().toString());

                int modType = omssaMod.getModType();
                String tempResidues = omssaMod.getModResiduesAsCompactString();

                switch (modType) {
                    case 0:
                        p.setResidues(tempResidues);
                        break;
                    case 1:
                    case 2:
                    case 5:
                    case 6:
                        p.setResidues("0");
                        break;
                    case 3:
                    case 4:
                    case 7:
                    case 8:
                        p.setResidues("1");
                        break;
                }

                ptms.add(p);
                allLabels.add(label);
            } else {
                throw new IllegalStateException("Duplicate variable ptm label: " + label + "!");
            }
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
    public int getSpectrumCount(boolean onlyIdentified) {

        if (spectrumCounter < 0) {

            if (onlyIdentified) {

                // get the list of spectra
                HashMap<MSSpectrum, MSHitSet> spectraMap = omssaOmxFile.getSpectrumToHitSetMap();
                Iterator<MSSpectrum> spectrumIterator = spectraMap.keySet().iterator();

                spectrumCounter = 0;

                // iterate all the spectra
                while (spectrumIterator.hasNext()) {

                    MSSpectrum tempSpectrum = spectrumIterator.next();

                    // check if the spectrum is identified
                    boolean spectrumIsIdentified = spectraMap.get(tempSpectrum).MSHitSet_hits.MSHits.size() > 0;

                    if (spectrumIsIdentified) {
                        spectrumCounter++;
                    }
                }
            } else {
                spectrumCounter = msRequest.MSRequest_spectra.MSSpectrumset.MSSpectrum.size();
            }
        }

        return spectrumCounter;
    }

    @Override
    public Iterator<Spectrum> getSpectrumIterator(boolean onlyIdentified) {
        // create a new iterator
        return new OmssaSpectrumIterator(onlyIdentified);
    }

    @Override
    public int getSpectrumReferenceForPeptideUID(String peptideUID) throws InvalidFormatException {
        
        //@TODO: verify the implementation of this method!!
        
        // return the spectrum id (everything before the first ".")
        int index = peptideUID.indexOf('.');

        if (index == -1) {
            throw new InvalidFormatException("Invalid peptideUID '" + peptideUID + "'");
        }

        return Integer.parseInt(peptideUID.substring(0, index));
    }

    @Override
    public Identification getIdentificationByUID(String identificationUID) throws InvalidFormatException {
        return convertProteinToIdentification(identificationUID, false);
    }

    @Override
    public Iterator<Identification> getIdentificationIterator(boolean prescanMode) {
        return new OmssaIdentificationIterator(prescanMode);
    }

    /**
     * Iterator of all Identifications in the OMSSA OMX file.
     *
     * @author Harald Barsnes
     */
    private class OmssaIdentificationIterator implements Iterator<Identification>, Iterable<Identification> {

        HashMap<String, LinkedList<String>> proteinMap = omssaOmxFile.getProteinToPeptideMap();
        //ArrayList<MSSpectrum> msSpectrumList;
        Iterator<String> proteinIterator;
        private boolean prescanMode;

        public OmssaIdentificationIterator(boolean prescanMode) {
            this.prescanMode = prescanMode;

            // sort them
            //msSpectrumList = new ArrayList<String>(proteinMap.keySet());
            //Collections.sort(proteinLabels); // @TODO: possible to sort?? is sorting required??

            // set the iterator
            proteinIterator = proteinMap.keySet().iterator();
        }

        @Override
        public Iterator<Identification> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            return proteinIterator.hasNext();
        }

        @Override
        public Identification next() {
            try {
                return convertProteinToIdentification(proteinIterator.next(), prescanMode);
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
     * Converts the protein identified by the given proteinId into a
     * PrideConverter Identification object.
     *
     * @param proteinId the protein id, i.e., the protein accession number.
     * @param prescanMode Indicates whether in prescan mode.
     * @return The created PRIDE Converter Identification object.
     * @throws InvalidFormatException
     */
    private Identification convertProteinToIdentification(String proteinId, boolean prescanMode) throws InvalidFormatException {

        // create the identification object
        Identification identification = new Identification();

        // check if the proteinLabel contains the ":reversed" tag
        if (decoyPrefix.length() > 0 && proteinId.endsWith(":reversed")) { // @TODO: not sure if this is correct for omssa...
            proteinId = decoyPrefix + proteinId.substring(0, proteinId.length() - 9);
        }

        // set the protein accession
        identification.setAccession(proteinId);

        // check if it's a decoy protein
        boolean isDecoy = decoyPrefix.length() > 0 && identification.getAccession().startsWith(decoyPrefix); // @TODO: not sure if this is correct for omssa...

        // set the unique identifier
        identification.setUniqueIdentifier(proteinId); // @TODO: verify that this is ok!!

        // set the database
        identification.setDatabase(getSearchDatabaseName()); // @TODO: verify that this is correct!!
        identification.setDatabaseVersion(getSearchDatabaseVersion()); // @TODO: verify that this is correct!!

        // set the score and threshold
        identification.setScore(0.0); // @TODO: implement me??
        identification.setThreshold(0.0); // @TODO: implement me??

        // set the search engine
        identification.setSearchEngine("OMSSA");

        // add the additional protein information (only in prescan mode)
        if (prescanMode) {
            Param additional = new Param();
            // additional.getCvParam().add(DAOCvParams.XTANDEM_EXPECT.getParam(protein.getExpectValue())); // @TODO: add any omssa specific cv terms??

            if (decoyPrefix.length() > 0 && identification.getAccession().startsWith(decoyPrefix)) { // @TODO: again, not sure if this is needed??
                additional.getCvParam().add(DAOCvParams.DECOY_HIT.getParam());
            }

            identification.setAdditional(additional);
        }

        // get the protein's peptides
        HashMap<String, MSPepHit> peptideToPeptHitMap = omssaOmxFile.getPeptidesToPepHit(proteinId);

        // if the protein has no peptides (threshold) return null
        if (peptideToPeptHitMap.keySet().size() < 1) {
            return null;
        }

        // iterate the peptides
        Iterator<String> peptideIterator = peptideToPeptHitMap.keySet().iterator();

        while (peptideIterator.hasNext()) {

            String currentPeptideSequence = peptideIterator.next();
            MSPepHit msPepHit = peptideToPeptHitMap.get(currentPeptideSequence);

            LinkedList<MSSpectrum> spectrumMatches = omssaOmxFile.getPeptideToSpectrumMap().get(currentPeptideSequence);
            Iterator<MSSpectrum> spectrumIterator = spectrumMatches.iterator();

            while (spectrumIterator.hasNext()) {

                MSSpectrum currentSpectrum = spectrumIterator.next();

                if (!omssaOmxFile.getSpectrumToHitSetMap().get(currentSpectrum).MSHitSet_hits.MSHits.isEmpty()) {

                    // find (and select) the MSHit with the lowest e-value
                    List<MSHits> allMSHits = omssaOmxFile.getSpectrumToHitSetMap().get(currentSpectrum).MSHitSet_hits.MSHits;
                    Iterator<MSHits> msHitIterator = allMSHits.iterator();
                    Double lowestEValue = Double.MAX_VALUE;
                    MSHits currentMSHit = null;

                    while (msHitIterator.hasNext()) {

                        MSHits tempMSHit = msHitIterator.next();

                        if (tempMSHit.MSHits_evalue < lowestEValue) {
                            lowestEValue = tempMSHit.MSHits_evalue;
                            currentMSHit = tempMSHit;
                        }
                    }

                    if (currentPeptideSequence.equalsIgnoreCase(currentMSHit.MSHits_pepstring)) {
                        throw new IllegalStateException("Peptide sequences do not match! Please check the code!!");
                    }

                    Peptide convertedPeptide = new Peptide();

                    // set the standard values
                    convertedPeptide.setUniqueIdentifier(msPepHit.MSPepHit_accession); // @TODO: not sure if this can be used???
                    convertedPeptide.setSequence(currentPeptideSequence);
                    convertedPeptide.setStart(msPepHit.MSPepHit_start);
                    convertedPeptide.setEnd(msPepHit.MSPepHit_stop);

                    // set the spectrum reference
                    //convertedPeptide.setSpectrumReference(???); // @TODO: not sure how to implement this...

                    // check whether the peptide is unique (by checking whether it only fits one protein
                    convertedPeptide.setIsSpecific(omssaOmxFile.getPeptideToProteinMap().size() > 1);

                    if (prescanMode) {
                        // add the PTMs
                        convertedPeptide.getPTM().addAll(getPeptidePTMs(currentPeptideSequence, currentMSHit));

                        // add the additional info
                        Param additional = new Param();

                        // add the omssa e and p values
                        //additional.getCvParam().add(DAOCvParams.OMSSA_EXPECT.getParam(currentMSHit.MSHits_evalue)); // @TODO: OMSSA_EXPECT has to be added as a supported cv
                        //additional.getCvParam().add(DAOCvParams.OMSSA_PVALUE.getParam(currentMSHit.MSHits_pvalue)); // @TODO: OMSSA_PVALUE has to be added as a supported cv

                        // add the flanking residues
                        String downstreamFlankingSequence = currentMSHit.MSHits_pepstart;
                        String upstreamFlankingSequence = currentMSHit.MSHits_pepstop;

                        if (upstreamFlankingSequence != null) {
                            additional.getCvParam().add(DAOCvParams.UPSTREAM_FLANKING_SEQUENCE.getParam(currentMSHit.MSHits_pepstart));
                        }
                        if (downstreamFlankingSequence != null) {
                            additional.getCvParam().add(DAOCvParams.DOWNSTREAM_FLANKING_SEQUENCE.getParam(currentMSHit.MSHits_pepstop));
                        }

                        // add the precursor charge
                        String chargeString = "" + currentSpectrum.MSSpectrum_charge.MSSpectrum_charge_E.get(0); // @TODO: what about multiple charges?? // note: this the spectrum charge and not the peptide charge...
                        chargeString = chargeString.replaceFirst("\\+", "");
                        additional.getCvParam().add(DAOCvParams.CHARGE_STATE.getParam(chargeString));

                        // add the precursor mz
                        additional.getCvParam().add(DAOCvParams.PRECURSOR_MZ.getParam(currentSpectrum.MSSpectrum_mz)); // @TODO: MH or MZ?

                        if (isDecoy) {
                            additional.getCvParam().add(DAOCvParams.DECOY_HIT.getParam());
                        }

                        convertedPeptide.setAdditional(additional);

                    } else {
                        // add the fragment ions
                        List<uk.ac.ebi.pride.tools.converter.report.model.FragmentIon> fragmentIons =
                                getPeptideFragmentIons(currentMSHit, currentSpectrum);

                        if (fragmentIons != null && fragmentIons.size() > 0) {
                            convertedPeptide.getFragmentIon().addAll(fragmentIons);
                        }
                    }

                    identification.getPeptide().add(convertedPeptide);
                }
            }
        }

        return identification;
    }

    private class OmssaSpectrumIterator implements Iterator<Spectrum> {

        /**
         * Indicates whether only spectra with identifications or all of them
         * should be returned
         */
        private boolean onlyIdentified = false;
        /**
         * The maximum spectrum index. Note: number of spectra can _not_ be used
         * as OMSSA sometimes skips some of the indexes...
         */
        private int numberOfIdentifiedSpectra;
        /**
         * Counts the number of spectra read, used when only iterating the
         * identified spectra.
         */
        private int spectrumCounter = 0;
        /**
         * The OMSSA spectrum iterator.
         */
        private Iterator<MSSpectrum> spectrumIterator;
        /**
         * The OMSSA spectrum map.
         */
        private HashMap<MSSpectrum, MSHitSet> spectraMap;

        /**
         * The default constructor.
         *
         * @param onlyIdentified Indicates whether all available spectra or only
         * spectra with a peptide identification should be returned.
         */
        public OmssaSpectrumIterator(boolean onlyIdentified) {
            this.onlyIdentified = onlyIdentified;

            // get the list of spectra
            spectraMap = omssaOmxFile.getSpectrumToHitSetMap();
            spectrumIterator = spectraMap.keySet().iterator();

            // get the number of identified spectra
            numberOfIdentifiedSpectra = getSpectrumCount(true);
        }

        @Override
        public boolean hasNext() {
            if (onlyIdentified) {
                return spectrumCounter < numberOfIdentifiedSpectra;
            } else {
                return spectrumIterator.hasNext();
            }
        }

        @Override
        public Spectrum next() {

            if (onlyIdentified) {

                // have to find the next identified spectrum
                MSSpectrum tempSpectrum = spectrumIterator.next();

                // iterate until we find an identified spectrum
                while (spectraMap.get(tempSpectrum).MSHitSet_hits.MSHits.isEmpty()) {
                    tempSpectrum = spectrumIterator.next();
                }

                spectrumCounter++;

                // create the spectrum
                return createSpectrum(tempSpectrum);
            } else {
                // create the spectrum
                return createSpectrum(spectrumIterator.next());
            }
        }

        @Override
        public void remove() {
            // this function is not supported
        }
    }

    /**
     * Returns the OMSSA spectrum in the wanted format.
     *
     * @param omssaSpectrum the OMSSA spectrum
     * @return the OMSSA spectrum in the wanted format
     */
    private Spectrum createSpectrum(MSSpectrum omssaSpectrum) {

        // initialize the spectrum
        Spectrum spectrum = new Spectrum();

        // set the id
        spectrum.setId(omssaSpectrum.MSSpectrum_number);

        // create the peak list
        ArrayList<Double> masses = new ArrayList<Double>();
        ArrayList<Double> intensities = new ArrayList<Double>();

        // currently, only ion series 1 is supported
        List<Integer> mzValues = omssaSpectrum.MSSpectrum_mz.MSSpectrum_mz_E;
        List<Integer> intensityValues = omssaSpectrum.MSSpectrum_abundance.MSSpectrum_abundance_E;

        Double omssaAbundanceScale = omssaSpectrum.MSSpectrum_iscale;

        for (int j = 0; j < mzValues.size(); j++) {
            masses.add(mzValues.get(j).doubleValue() / omssaResponseScale);
            intensities.add(intensityValues.get(j).doubleValue() / omssaAbundanceScale);
        }

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

        // add the spectrum description
        spectrum.setSpectrumDesc(generateSpectrumDescription(omssaSpectrum));

        return spectrum;
    }

    /**
     * Generates the SpectrumDesc object for the passed spectrum. <br> A charge
     * state is only reported at the peptide level
     *
     * @param omssaSpectrum The given OMSSA spectrum.
     * @return The SpectrumDesc object for the given spectrum
     */
    private SpectrumDesc generateSpectrumDescription(MSSpectrum omssaSpectrum) {

        // initialize the spectrum description
        SpectrumDesc description = new SpectrumDesc();

        // set the ms level based on the type of search performed
        int msLevel = 2; // note: omssa seems to only support MS/MS (i.e., not MS^3)

        // create the spectrumSettings/spectrumInstrument (mzRangeStop, mzRangeStart, msLevel)
        SpectrumSettings settings = new SpectrumSettings();
        SpectrumInstrument instrument = new SpectrumInstrument();

        instrument.setMsLevel(msLevel);

        // @TODO: the mz range should be implemented?

        // TODO: PRIDE - document how the spectrumSettings/spectrumInstrument should be used. Currently, I assume that the mzStart should be the beginning of the scan range, not the min mass in the spec.
        // TODO: DOC - rangeStart / Stop is either the set parameters, or if the parameters were not set the min and max of the spectrum masses
        //Float rangeStart = new Float((query.getMinInternalMass() != -1) ? query.getMinInternalMass() : query.getMassMin());
        //Float rangeStop = new Float((query.getMaxInternalMass() != -1) ? query.getMaxInternalMass() : query.getMassMax());
        //instrument.setMzRangeStart(rangeStart);
        //instrument.setMzRangeStop(rangeStop);

        // set the spectrum settings
        settings.setSpectrumInstrument(instrument);
        description.setSpectrumSettings(settings);

        // create the precursor list
        PrecursorList precList = new PrecursorList();

        // currently, there's only one precursor supported
        precList.setCount(1);

        Precursor prec = new Precursor();
        prec.setMsLevel(msLevel - 1);

        Spectrum spec = new Spectrum();
        spec.setId(0);
        prec.setSpectrum(spec);

        uk.ac.ebi.pride.jaxb.model.Param ionSelection = new uk.ac.ebi.pride.jaxb.model.Param();

        // @TODO: precursor peak intensity not available?
        //ionSelection.getCvParam().add(jCvParam("MS", "MS:1000042", "peak intensity", mascotFile.getObservedIntensity(spectrumId)));

        // precursor m/z
        ionSelection.getCvParam().add(jCvParam("MS", "MS:1000744", "selected ion m/z", (((double) omssaSpectrum.MSSpectrum_precursormz) / omssaResponseScale)));

        // @TODO: retention time not available?
        //ionSelection.getCvParam().add(jCvParam("MS", "MS:1000894", "retention time", query.getRetentionTimes()));

        // if in compatibility mode add the charge state of the first peptide
        if ((Boolean) getCurrentProperty(SupportedProperties.COMPATIBILITY_MODE)) {

            // if the peptide was identified
            if (omssaOmxFile.getSpectrumToHitSetMap().get(omssaSpectrum).MSHitSet_hits.MSHits.isEmpty()
                    && !omssaSpectrum.MSSpectrum_charge.MSSpectrum_charge_E.isEmpty()) {

                // note: annotates the first charge only, the rest are added at the peptide level
                String chargeString = omssaSpectrum.MSSpectrum_charge.MSSpectrum_charge_E.get(0).toString();
                ionSelection.getCvParam().add(jCvParam("MS", "MS:1000041", "charge state", chargeString));
            }
        }

        // save the ion selection
        prec.setIonSelection(ionSelection);

        // currently, no activation parameters supported
        prec.setActivation(new uk.ac.ebi.pride.jaxb.model.Param());

        // add the (only) precursor to the precursor list and save it in the description item
        precList.getPrecursor().add(prec);
        description.setPrecursorList(precList);

        return description;
    }

    @Override
    public Collection<DatabaseMapping> getDatabaseMappings() throws InvalidFormatException {

        DatabaseMapping databaseMapping = new DatabaseMapping();
        databaseMapping.setSearchEngineDatabaseName(getSearchDatabaseName());
        databaseMapping.setSearchEngineDatabaseVersion(getSearchDatabaseVersion());

        ArrayList<DatabaseMapping> allDatabaseMappings = new ArrayList<DatabaseMapping>(1);
        allDatabaseMappings.add(databaseMapping);

        return allDatabaseMappings;
    }

    @Override
    public void setExternalSpectrumFile(String string) {

        // @TODO: implement me??

        throw new UnsupportedOperationException("Not supported.");
    }

    /**
     * Returns an ArrayList of PeptidePTMs for the given peptideSequence.
     *
     * @param peptideSequence The peptide to convert the modifications for.
     * @return An ArrayList of PTMs
     */
    private ArrayList<PeptidePTM> getPeptidePTMs(String peptideSequence, MSHits msHit) {
        // initialize the PTMs
        ArrayList<PeptidePTM> ptms = new ArrayList<PeptidePTM>();

        // process the fixed modifications
        getFixedModifications(peptideSequence, ptms);

        // process the variable modifications
        getVariableModifications(msHit, ptms);

        return ptms;
    }

    /**
     * Adds the fixed modifications.
     *
     * @param peptideSequence
     * @param ptms
     */
    private void getFixedModifications(String peptideSequence, ArrayList<PeptidePTM> ptms) {

        // get the list of fixed modifications
        List<Integer> fixedModifications =
                msRequest.MSRequest_settings.MSSearchSettings.MSSearchSettings_fixed.MSMod;

        // fixed modifications
        if (fixedModifications.size() > 0) {

            for (Integer fixedModification : fixedModifications) {
                Vector<String> modifiedResidues = omssaModificationDetails.get(fixedModification).getModResidues();

                for (String modifiedResidue : modifiedResidues) {

                    int index = peptideSequence.indexOf(modifiedResidue);

                    while (index != -1) {

                        PeptidePTM ptm = new PeptidePTM();

                        ptm.setSearchEnginePTMLabel(omssaModificationDetails.get(fixedModification).getModName());
                        ptm.setFixedModification(true);

                        // modification positions are set relative to the protein sequence
                        ptm.setModLocation(index);
                        // set the mass always as monoisotopic mass
                        ptm.getModMonoDelta().add(omssaModificationDetails.get(fixedModification).getModMonoMass().toString());
                        ptms.add(ptm);

                        index = peptideSequence.indexOf(modifiedResidue, index + 1);
                    }
                }
            }
        }
    }

    /**
     * Adds the variable modifications.
     *
     * @param msHit
     * @param ptms
     */
    private void getVariableModifications(MSHits msHit, ArrayList<PeptidePTM> ptms) {

        // variable modifications
        Iterator<MSModHit> modsIterator = msHit.MSHits_mods.MSModHit.iterator();

        while (modsIterator.hasNext()) {

            MSModHit currentMSModHit = modsIterator.next();

            int modType = currentMSModHit.MSModHit_modtype.MSMod;
            int modSite = currentMSModHit.MSModHit_site;

            PeptidePTM ptm = new PeptidePTM();

            ptm.setSearchEnginePTMLabel(omssaModificationDetails.get(modType).getModName());
            ptm.setFixedModification(false);

            // modification positions are set relative to the protein sequence
            ptm.setModLocation(modSite + 1);
            // set the mass always as monoisotopic mass
            ptm.getModMonoDelta().add(omssaModificationDetails.get(modType).getModMonoMass().toString());
            ptms.add(ptm);
        }
    }

    /**
     * Returns a peptide's fragment ions as a List of report model FragmentIons.
     *
     * @param currentMSHit
     * @return
     */
    private ArrayList<FragmentIon> getPeptideFragmentIons(MSHits currentMSHit, MSSpectrum currentSpectrum) {

        ArrayList<uk.ac.ebi.pride.tools.converter.report.model.FragmentIon> prideIons =
                new ArrayList<uk.ac.ebi.pride.tools.converter.report.model.FragmentIon>();

        // get the list of fragment ions for the current peptide identification
        List<MSMZHit> currentFragmentIons = currentMSHit.MSHits_mzhits.MSMZHit;

        double currentIntensityScale = currentSpectrum.MSSpectrum_iscale;

        // iterate the fragment ions, detect the type and create CV params for each of them
        for (MSMZHit currentFragmentIon : currentFragmentIons) {

            // Now we have to map the reported fragment ion to its corresponding peak.
            // Note that the values given in the OMSSA file are scaled.
            int fragmentIonMzValueUnscaled = currentFragmentIon.MSMZHit_mz;
            double fragmentIonIntensityScaled = -1;
            double observedPeakMzValue = -1;
            double fragmentIonMassError = -1;

            List<Integer> mzValues = currentSpectrum.MSSpectrum_mz.MSSpectrum_mz_E;
            List<Integer> intensityValues = currentSpectrum.MSSpectrum_abundance.MSSpectrum_abundance_E;

            // Iterate the peaks and find the values within the fragment ion error range.
            // If more than one match, use the most intense.
            for (int j = 0; j < mzValues.size(); j++) {

                // check if the fragment ion is within the mass error range
                if (Math.abs(mzValues.get(j) - fragmentIonMzValueUnscaled) <= (ionCoverageErrorMargin * omssaResponseScale)) {

                    // select this peak if it's the most intense peak within range
                    if ((intensityValues.get(j).doubleValue() / currentIntensityScale) > fragmentIonIntensityScaled) {
                        fragmentIonIntensityScaled = intensityValues.get(j).doubleValue() / currentIntensityScale;

                        // calculate the fragmet ion mass
                        fragmentIonMassError = (mzValues.get(j).doubleValue() - fragmentIonMzValueUnscaled) / omssaResponseScale; // @TODO: or the other way around?? The order decides the sign.
                        observedPeakMzValue = mzValues.get(j).doubleValue() / omssaResponseScale;
                    }
                }
            }

            // check if any peaks in the spectrum matched the fragment ion
            if (fragmentIonIntensityScaled == -1) {
                System.out.println("Unable to map the fragment ion \'"
                        + currentFragmentIon.MSMZHit_ion.MSIonType + " " + currentFragmentIon.MSMZHit_number + "\'!!");
            } else {

                // Now we have to map the reported fragment ion to its corresponding peak.
                FragmentIon fragmentIon = new uk.ac.ebi.pride.tools.converter.report.model.FragmentIon();

                // charge
                fragmentIon.getCvParam().add(DAOCvParams.PRODUCT_ION_CHARGE.getParam(currentFragmentIon.MSMZHit_charge));

                // intensity
                fragmentIon.getCvParam().add(DAOCvParams.PRODUCT_ION_INTENSITY.getParam(fragmentIonIntensityScaled));

                // m/z
                fragmentIon.getCvParam().add(DAOCvParams.PRODUCT_ION_MZ.getParam(observedPeakMzValue));

                // mass error
                fragmentIon.getCvParam().add(DAOCvParams.PRODUCT_ION_MASS_ERROR.getParam(fragmentIonMassError));


                // ion type is [0-10], 0 is a-ion, 1 is b-ion etc
                int msIonType = currentFragmentIon.MSMZHit_ion.MSIonType;
                
                // tag used for netutal loss and immonium ion type
                String msIonNeutralLossOrImmoniumIonTag = "";

                // get the omssa neutral loss type
                int msIonNeutralLossType = currentFragmentIon.MSMZHit_moreion.MSIon.MSIon_neutralloss.MSIonNeutralLoss;

                // -1 means no neutral loss reported
                if (msIonNeutralLossType != -1) {
                    msIonNeutralLossOrImmoniumIonTag += "_" + msIonNeutralLossType;
                } else {
                    // check for immonium ions
                    if (currentFragmentIon.MSMZHit_moreion.MSIon.MSIon_immonium.MSImmonium.MSImmonium_parent != null) {
                        msIonNeutralLossOrImmoniumIonTag += "_" + currentFragmentIon.MSMZHit_moreion.MSIon.MSIon_immonium.MSImmonium.MSImmonium_parent;
                    }
                }

                // get the cv mapping
                CvParam currentCvParam = getCvNameMapping("OMSSA" + "_" + msIonType + msIonNeutralLossOrImmoniumIonTag, currentFragmentIon.MSMZHit_number);

                // add the mapping if found
                if (currentCvParam != null) {
                    fragmentIon.getCvParam().add(currentCvParam);
                    prideIons.add(fragmentIon);
                }
            }
        }

        return prideIons;
    }

    /**
     * Map the OMSSA fragment ions to the correct PRIDE CV param.
     *
     * @param key
     * @param position
     * @return
     */
    private CvParam getCvNameMapping(String key, int position) {

        if (key.equalsIgnoreCase("OMSSA_0")) {
            return new CvParam("PRIDE", "PRIDE:0000233", "a ion", "" + position);
        } else if (key.equalsIgnoreCase("OMSSA_0_0")) {
            return new CvParam("PRIDE", "PRIDE:0000234", "a ion -H2O", "" + position);
        } else if (key.equalsIgnoreCase("OMSSA_0_1")) {
            return new CvParam("PRIDE", "PRIDE:0000235", "a ion -NH3", "" + position);
        } else if (key.equalsIgnoreCase("OMSSA_1")) {
            return new CvParam("PRIDE", "PRIDE:0000194", "b ion", "" + position);
        } else if (key.equalsIgnoreCase("OMSSA_1_0")) {
            return new CvParam("PRIDE", "PRIDE:0000196", "b ion -H2O", "" + position);
        } else if (key.equalsIgnoreCase("OMSSA_1_1")) {
            return new CvParam("PRIDE", "PRIDE:0000195", "b ion -NH3", "" + position);
        } else if (key.equalsIgnoreCase("OMSSA_2")) {
            return new CvParam("PRIDE", "PRIDE:0000236", "c ion", "" + position);
        } else if (key.equalsIgnoreCase("OMSSA_2_0")) {
            return new CvParam("PRIDE", "PRIDE:0000237", "c ion -H2O", "" + position);
        } else if (key.equalsIgnoreCase("OMSSA_2_1")) {
            return new CvParam("PRIDE", "PRIDE:0000238", "c ion -NH3", "" + position);
        } else if (key.equalsIgnoreCase("OMSSA_3")) {
            return new CvParam("PRIDE", "PRIDE:0000227", "x ion", "" + position);
        } else if (key.equalsIgnoreCase("OMSSA_3_0")) {
            return new CvParam("PRIDE", "PRIDE:0000228", "x ion -H2O", "" + position);
        } else if (key.equalsIgnoreCase("OMSSA_3_1")) {
            return new CvParam("PRIDE", "PRIDE:0000229", "x ion -NH3", "" + position);
        } else if (key.equalsIgnoreCase("OMSSA_4")) {
            return new CvParam("PRIDE", "PRIDE:0000193", "y ion", "" + position);
        } else if (key.equalsIgnoreCase("OMSSA_4_0")) {
            return new CvParam("PRIDE", "PRIDE:0000197", "y ion -H2O", "" + position);
        } else if (key.equalsIgnoreCase("OMSSA_4_1")) {
            return new CvParam("PRIDE", "PRIDE:0000198", "y ion -NH3", "" + position);
        } else if (key.equalsIgnoreCase("OMSSA_5")) {
            return new CvParam("PRIDE", "PRIDE:0000230", "z ion", "" + position);
        } else if (key.equalsIgnoreCase("OMSSA_5_0")) {
            return new CvParam("PRIDE", "PRIDE:0000231", "z ion -H2O", "" + position);
        } else if (key.equalsIgnoreCase("OMSSA_5_1")) {
            return new CvParam("PRIDE", "PRIDE:0000232", "z ion -NH3", "" + position);
        } else if (key.equalsIgnoreCase("OMSSA_6")) {
            return new CvParam("PRIDE", "PRIDE:0000263", "precursor ion", null);
        } else if (key.equalsIgnoreCase("OMSSA_8_A")) {
            return new CvParam("PRIDE", "PRIDE:0000240", "immonium A", null);
        } else if (key.equalsIgnoreCase("OMSSA_8_C")) {
            return new CvParam("PRIDE", "PRIDE:0000241", "immonium C", null);
        } else if (key.equalsIgnoreCase("OMSSA_8_D")) {
            return new CvParam("PRIDE", "PRIDE:0000242", "immonium D", null);
        } else if (key.equalsIgnoreCase("OMSSA_8_E")) {
            return new CvParam("PRIDE", "PRIDE:0000243", "immonium E", null);
        } else if (key.equalsIgnoreCase("OMSSA_8_F")) {
            return new CvParam("PRIDE", "PRIDE:0000244", "immonium F", null);
        } else if (key.equalsIgnoreCase("OMSSA_8_G")) {
            return new CvParam("PRIDE", "PRIDE:0000245", "immonium G", null);
        } else if (key.equalsIgnoreCase("OMSSA_8_H")) {
            return new CvParam("PRIDE", "PRIDE:0000246", "immonium H", null);
        } else if (key.equalsIgnoreCase("OMSSA_8_I")) {
            return new CvParam("PRIDE", "PRIDE:0000247", "immonium I", null);
        } else if (key.equalsIgnoreCase("OMSSA_8_K")) {
            return new CvParam("PRIDE", "PRIDE:0000248", "immonium K", null);
        } else if (key.equalsIgnoreCase("OMSSA_8_L")) {
            return new CvParam("PRIDE", "PRIDE:0000249", "immonium L", null);
        } else if (key.equalsIgnoreCase("OMSSA_8_M")) {
            return new CvParam("PRIDE", "PRIDE:0000250", "immonium M", null);
        } else if (key.equalsIgnoreCase("OMSSA_8_N")) {
            return new CvParam("PRIDE", "PRIDE:0000251", "immonium N", null);
        } else if (key.equalsIgnoreCase("OMSSA_8_P")) {
            return new CvParam("PRIDE", "PRIDE:0000252", "immonium P", null);
        } else if (key.equalsIgnoreCase("OMSSA_8_Q")) {
            return new CvParam("PRIDE", "PRIDE:0000253", "immonium Q", null);
        } else if (key.equalsIgnoreCase("OMSSA_8_R")) {
            return new CvParam("PRIDE", "PRIDE:0000254", "immonium R", null);
        } else if (key.equalsIgnoreCase("OMSSA_8_S")) {
            return new CvParam("PRIDE", "PRIDE:0000255", "immonium S", null);
        } else if (key.equalsIgnoreCase("OMSSA_8_T")) {
            return new CvParam("PRIDE", "PRIDE:0000256", "immonium T", null);
        } else if (key.equalsIgnoreCase("OMSSA_8_V")) {
            return new CvParam("PRIDE", "PRIDE:0000257", "immonium V", null);
        } else if (key.equalsIgnoreCase("OMSSA_8_W")) {
            return new CvParam("PRIDE", "PRIDE:0000258", "immonium W", null);
        } else if (key.equalsIgnoreCase("OMSSA_8_Y")) {
            return new CvParam("PRIDE", "PRIDE:0000259", "immonium Y", null);
        } else {
            return null; // unknown or unsupported ion type...
        }
    }
}