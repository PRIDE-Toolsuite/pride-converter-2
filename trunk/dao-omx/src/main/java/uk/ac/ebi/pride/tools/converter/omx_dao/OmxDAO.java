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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import uk.ac.ebi.pride.jaxb.model.*;
import uk.ac.ebi.pride.tools.converter.dao.DAO;
import uk.ac.ebi.pride.tools.converter.dao.DAOProperty;
import uk.ac.ebi.pride.tools.converter.dao.impl.AbstractDAOImpl;
import uk.ac.ebi.pride.tools.converter.report.model.Contact;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.Identification;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.Protocol;
import uk.ac.ebi.pride.tools.converter.report.model.Reference;
import uk.ac.ebi.pride.tools.converter.report.model.*;
import uk.ac.ebi.pride.tools.converter.report.model.Software;
import uk.ac.ebi.pride.tools.converter.report.model.SourceFile;
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
     * details in the mods.xml and usermods.xml files
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
        //params.getCvParam().add(new CvParam("PRIDE", "PRIDE:0000219", "Date of search", ??)); // @TODO: date of search not found in OMSSA omx files?

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
    public int getSpectrumReferenceForPeptideUID(String peptideUID) {

        // @TODO: Harald: not sure what this method is supposed to do...?

        // From DAO interface: must return -1 if no spectrum ref found

        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Identification getIdentificationByUID(String identificationUID) {

        // @TODO: Harald: not sure what this method is supposed to do...?

        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Iterator<Identification> getIdentificationIterator(boolean prescanMode) {

        /**
         * From DAO interface:
         *
         * This method will return an iterator that will return individual
         * identification objects. In prescanMode the complete Identification
         * and Peptide objects should be returned <b>without<b> the peptide's
         * fragment ion annotation. Peptide items have to contain all the PTMs.
         * Additionally, all handlers (QuantitationHandler, FastaHandler,
         * GelCoordinateHandler) have to be invoked (if they were set). <br> In
         * scanMode (= !prescanMode) Peptide and Protein objects should
         * <b>NOT</b> contain any additional parameters and peptidePTMs should
         * <b>NOT</b> be included. Furthermore, the different handlers should
         * also <b>NOT</b> be invoked. Peptide FragmentIon annotations are
         * mandatory (if applicable) in scanMode. The identification iterator
         * may return null for an identification
         */
        throw new UnsupportedOperationException("Not yet implemented.");
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
        int msLevel = 0;

        msLevel = 2; // note: omssa seems to only support MS/MS (i.e., not MS^3)

        // create the spectrumSettings/spectrumInstrument (mzRangeStop, mzRangeStart, msLevel)
        SpectrumSettings settings = new SpectrumSettings();
        SpectrumInstrument instrument = new SpectrumInstrument();

        instrument.setMsLevel(msLevel);

        // @TODO: the mz range has to be implemented

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
            if (omssaOmxFile.getSpectrumToHitSetMap().get(omssaSpectrum).MSHitSet_hits.MSHits.isEmpty()) {

                // @TODO: OMSSA question: possible with more than one charge per spectrum??
                // @TODO: annotate the charge on the peptide level instead??
                String chargeString = "" + omssaSpectrum.MSSpectrum_charge.MSSpectrum_charge_E.get(0);
                chargeString = chargeString.replaceFirst("\\+", "");

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

        throw new UnsupportedOperationException("Not supported yet.");
    }
}