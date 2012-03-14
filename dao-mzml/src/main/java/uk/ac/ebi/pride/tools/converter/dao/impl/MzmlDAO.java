package uk.ac.ebi.pride.tools.converter.dao.impl;

import org.apache.log4j.Logger;
import uk.ac.ebi.jmzml.model.mzml.*;
import uk.ac.ebi.jmzml.model.mzml.DataProcessing;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;
import uk.ac.ebi.pride.jaxb.model.*;
import uk.ac.ebi.pride.jaxb.model.Precursor;
import uk.ac.ebi.pride.jaxb.model.PrecursorList;
import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.dao.DAO;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.dao.DAOProperty;
import uk.ac.ebi.pride.tools.converter.dao.handler.QuantitationCvParams;
import uk.ac.ebi.pride.tools.converter.report.model.CV;
import uk.ac.ebi.pride.tools.converter.report.model.Contact;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.*;
import uk.ac.ebi.pride.tools.converter.report.model.Identification;
import uk.ac.ebi.pride.tools.converter.report.model.InstrumentDescription.AnalyzerList;
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

/**
 * Converts mzML files into PRIDE XML files. Since mzML
 * can hold certain types of data that cannot be included
 * in PRIDE XML files there are several limitations to this
 * DAO:<br>
 * - The file must only specify one instrument<br>
 * - There must only be one source and analyzer specified for this instrument <br>
 * - The different processing methods (mzML supports multiple) are all merged into the one present in PRIDE XML <br>
 *
 * @author jg
 */
public class MzmlDAO extends AbstractPeakListDAO implements DAO {
    /**
     * Logger instance to use.
     */
    private Logger logger = Logger.getLogger(MzmlDAO.class);
    /**
     * An ArrayList of all the spectrum ids found in
     * the mzML file. The 1-based position of an
     * id in the Array is used as its id in the
     * PRIDE XML file.
     */
    private ArrayList<String> spectrumIds;
    /**
     * The unmarshaller used to unmarshal the mzML
     * file.
     */
    private MzMLUnmarshaller unmarshaller;
    /**
     * The mzML source file
     */
    private File sourcefile;
    /**
     * Indicates whether a thorough scan of
     * the file is performed to check which instrument
     * was actually used in case multiple instrument
     * configurations are being reported.
     */
    private boolean scanOnMultipleInstruments = false;
    /**
     * The properties currently used.
     */
    private Properties properties = new Properties();

    public MzmlDAO(File sourcefile) throws InvalidFormatException {
        // save the source file
        this.sourcefile = sourcefile;

        // unmarshall the file
        try {
            unmarshaller = new MzMLUnmarshaller(sourcefile);

            // save the spectra ids
            spectrumIds = new ArrayList<String>(unmarshaller.getSpectrumIDs());
        } catch (RuntimeException e) {
            throw new InvalidFormatException("Failed to parse mzML file.", e);
        }
    }

    @SuppressWarnings("rawtypes")
    public static Collection<DAOProperty> getSupportedProperties() {
        ArrayList<DAOProperty> supportedProperties = new ArrayList<DAOProperty>();

        // add the allow-identifications-only option
        DAOProperty<Boolean> scanOnMultipleInstruments = new DAOProperty<Boolean>("scan_on_multiple_instruments", false);
        scanOnMultipleInstruments.setDescription("PRIDE XML can only report one instrument configuration. If this parameter is set to \"True\" the converter checks if all the instruments were actually used and if only one was used, reports this configuration in the report file.");
        scanOnMultipleInstruments.setShortDescription("Check if all reported instrument configurations are used.");
        scanOnMultipleInstruments.setAdvanced(true);
        supportedProperties.add(scanOnMultipleInstruments);

        return supportedProperties;
    }

    @Override
    public List<String> getSpectraIds() {
        return spectrumIds;
    }

    @Override
    public void setConfiguration(Properties props) {
        properties = props;

        scanOnMultipleInstruments = Boolean.parseBoolean(properties.getProperty("scan_on_multiple_instruments", "false"));
    }

    @Override
    public Properties getConfiguration() {
        return properties;
    }

    @Override
	public void setExternalSpectrumFile(String filename) {
		// not applicable
	}

	@Override
    public String getExperimentTitle() {
        // not supported
        return "Unknown mzML experiment";
    }

    @Override
    public String getExperimentShortLabel() {
        // not supported
        return null;
    }

    @Override
    public Param getExperimentParams() {
        Param params = new Param();

        params.getCvParam().add(DAOCvParams.ORIGINAL_MS_FORMAT.getParam("mzML"));

        // copy the input file parameters
        FileDescription description = unmarshaller.unmarshalFromXpath("/fileDescription", FileDescription.class);
        SourceFileList sourceFileList = description.getSourceFileList();

        if (sourceFileList != null) {
            // add the number of source files
            params.getUserParam().add(new UserParam("sourceFileCount", sourceFileList.getCount().toString()));

            // loop through the source files and copy the params
            for (uk.ac.ebi.jmzml.model.mzml.SourceFile sourceFile : sourceFileList.getSourceFile()) {
                // copy the cvParams
                for (CVParam param : sourceFile.getCvParam()) {
                    params.getCvParam().add(convertCvParam(param));
                }
                // copy the userParams
                for (uk.ac.ebi.jmzml.model.mzml.UserParam param : sourceFile.getUserParam()) {
                    params.getUserParam().add(convertUserParam(param));
                }
            }
        }

        // don't add the run params as this would cause the whole run object to be unmarshalled

        return params;
    }

    /**
     * Returns the concatenated name of all samples present in the
     * mzML file. The different names are joined together by
     * " and ".
     */
    @Override
    public String getSampleName() {
        // get the sample list
        String sampleName = "";

        SampleList sampleList = unmarshaller.unmarshalFromXpath("/sampleList", SampleList.class);

        if (sampleList != null) {
            for (Sample sample : sampleList.getSample()) {
                String theName = sample.getName();

                if (theName != null)
                    sampleName += ((sampleName.length() > 0) ? " and " : "") + theName;
            }
        }

        return sampleName;
    }

    @Override
    public String getSampleComment() {
        // not supported
        return null;
    }

    @Override
    public Param getSampleParams() {
        Param params = new Param();

        // add all sample params
        SampleList sampleList = unmarshaller.unmarshalFromXpath("/sampleList", SampleList.class);

        // if there is no sample list just return the empty params
        if (sampleList == null)
            return params;

        Integer sampleCount = 1;
        Integer totalSampleCount = sampleList.getCount().intValue();

        // indicate that there are multiple subsamples if necessary
        if (totalSampleCount > 1)
            params.getCvParam().add(QuantitationCvParams.CONTAINS_MULTIPLE_SUBSAMPLES.getParam());

        for (Sample sample : sampleList.getSample()) {
            for (CVParam param : sample.getCvParam()) {
                // set the subsample count if necessary
                if (totalSampleCount > 1 && (param.getValue() == null || param.getValue().equals("")))
                    param.setValue("subsample " + sampleCount);

                // add the param
                params.getCvParam().add(convertCvParam(param));
            }

            // add the userParams
            for (uk.ac.ebi.jmzml.model.mzml.UserParam param : sample.getUserParam()) {
                if (totalSampleCount > 1 && (param.getValue() == null || param.getValue().equals("")))
                    param.setValue("subsample " + sampleCount);

                // add the param
                params.getUserParam().add(convertUserParam(param));
            }

            sampleCount++;
        }

        return params;
    }

    @Override
    public SourceFile getSourceFile() {
        // initialize the return variable
        SourceFile file = new SourceFile();

        file.setPathToFile(sourcefile.getAbsolutePath());
        file.setNameOfFile(sourcefile.getName());
        file.setFileType("mzML");

        return file;
    }

    @Override
    public Collection<Contact> getContacts() {
        ArrayList<Contact> contacts = new ArrayList<Contact>();

        // get the contacts
        FileDescription fileDescription = unmarshaller.unmarshalFromXpath("/fileDescription", FileDescription.class);

        for (ParamGroup contact : fileDescription.getContact()) {
            // create the new contact
            Contact converterContact = new Contact();

            // set the name
            CvParam name = getCvParamFromGroup("MS:1000586", contact);

            if (name != null)
                converterContact.setName(name.getValue());
            else
                converterContact.setName("Unknown");

            // set the institute
            CvParam organization = getCvParamFromGroup("MS:1000590", contact);

            if (organization != null)
                converterContact.setInstitution(organization.getValue());
            else
                converterContact.setInstitution("Unknown");

            // set the email address
            CvParam mail = getCvParamFromGroup("MS:1000589", contact);

            if (mail != null)
                converterContact.setContactInfo(mail.getValue());
            else
                converterContact.setContactInfo("");

            // add the contact
            contacts.add(converterContact);
        }

        return contacts;
    }

    @Override
    public InstrumentDescription getInstrument() {
        InstrumentDescription instrumentDescription = new InstrumentDescription();

        // get the unstrument configuration
        InstrumentConfigurationList instrumentConfigurations = unmarshaller.unmarshalFromXpath("/instrumentConfigurationList", InstrumentConfigurationList.class);

        // initialize the instrument configuration to report
        InstrumentConfiguration configuration = null;

        // if there's only one use that one
        if (instrumentConfigurations.getCount().intValue() == 1)
            configuration = instrumentConfigurations.getInstrumentConfiguration().get(0);

            // in case there are multiple instruments scan all spectra to determine which one was used
        else if (instrumentConfigurations.getCount().intValue() > 1 && scanOnMultipleInstruments) {
            HashSet<String> usedInstrumentIds;
            try {
                usedInstrumentIds = getUsedInstrumentIds();
            } catch (InvalidFormatException e) {
                return null;
            }

            // if there is only one used instrument id, use this one
            if (usedInstrumentIds.size() == 1) {
                String usedId = usedInstrumentIds.iterator().next();

                logger.info("Multiple instrument configurations encountered. Reporting the only used one: \"" + usedId + "\".");

                for (InstrumentConfiguration config : instrumentConfigurations.getInstrumentConfiguration()) {
                    logger.info("Checking instrument id: \"" + config.getId() + "\"");
                    if (usedId.equals(config.getId())) {
                        configuration = config;
                        break;
                    }
                }
            }
        }

        // make sure the configuration is available
        if (configuration == null)
            return null;

        // if there's no component list, return null
        if (configuration.getComponentList() == null)
            return null;

        // in case there are multiple sources, report nothing
        if (configuration.getComponentList().getSource().size() > 1)
            return null;

        // in case there are multiple detectors report nothing
        if (configuration.getComponentList().getDetector().size() > 1)
            return null;

        // process the source
        Param source = new Param();

        // get the only source from the file
        SourceComponent theSource = configuration.getComponentList().getSource().get(0);

        for (CVParam param : theSource.getCvParam())
            source.getCvParam().add(convertCvParam(param));
        for (uk.ac.ebi.jmzml.model.mzml.UserParam param : theSource.getUserParam())
            source.getUserParam().add(convertUserParam(param));

        instrumentDescription.setSource(source);

        // process the analyzers
        AnalyzerList analyzerList = new AnalyzerList();
        analyzerList.setCount(configuration.getComponentList().getAnalyzer().size());

        for (AnalyzerComponent theAnalyzer : configuration.getComponentList().getAnalyzer()) {
            Param analyzer = new Param();

            for (CVParam param : theAnalyzer.getCvParam())
                analyzer.getCvParam().add(convertCvParam(param));
            for (uk.ac.ebi.jmzml.model.mzml.UserParam param : theAnalyzer.getUserParam())
                analyzer.getUserParam().add(convertUserParam(param));

            // add the analyzer
            analyzerList.getAnalyzer().add(analyzer);
        }

        instrumentDescription.setAnalyzerList(analyzerList);

        // set the only detector
        DetectorComponent theDetector = configuration.getComponentList().getDetector().get(0);

        Param detector = new Param();

        for (CVParam param : theDetector.getCvParam())
            detector.getCvParam().add(convertCvParam(param));
        for (uk.ac.ebi.jmzml.model.mzml.UserParam param : theDetector.getUserParam())
            detector.getUserParam().add(convertUserParam(param));

        instrumentDescription.setDetector(detector);

        return instrumentDescription;
    }

    /**
     * Scans the whole file to determine the used
     * instrument configurations.
     *
     * @return
     * @throws InvalidFormatException
     */
    private HashSet<String> getUsedInstrumentIds() throws InvalidFormatException {
        HashSet<String> instrumentIds = new HashSet<String>();

        // TODO: get the default instrument setting from the run elements
        Map<String, String> runAttributes = unmarshaller.getSingleElementAttributes("/run");

        // add the default instrument ref
        instrumentIds.add(runAttributes.get("defaultInstrumentConfigurationRef"));

        // iterate over all spectra and check which instrument they refer to
        for (String specId : spectrumIds) {
            // get the spectrum
            try {
                uk.ac.ebi.jmzml.model.mzml.Spectrum spectrum = unmarshaller.getSpectrumById(specId);

                ScanList scanList = spectrum.getScanList();

                if (scanList == null)
                    continue;

                for (Scan scan : scanList.getScan()) {
                    if (scan.getInstrumentConfigurationRef() != null)
                        instrumentIds.add(scan.getInstrumentConfigurationRef());
                }
            } catch (MzMLUnmarshallerException e) {
                throw new InvalidFormatException("Failed to retrieve spectrum \"" + specId + "\" from mzML file.", e);
            }
        }

        return instrumentIds;
    }

    @Override
    public Software getSoftware() {
        // return an empty software element
        Software software = new Software();

        software.setName("Unknown generic (mzML format)");
        software.setVersion("");

        return software;
    }

    @Override
    public Param getProcessingMethod() {
        DataProcessingList processingList = unmarshaller.unmarshalFromXpath("/dataProcessingList", DataProcessingList.class);

        // add all parameters to the param
        Param processingMethod = new Param();

        // all processing methods are merged into one param list
        for (DataProcessing processing : processingList.getDataProcessing()) {
            for (ProcessingMethod theProcessingMethod : processing.getProcessingMethod()) {
                for (CVParam param : theProcessingMethod.getCvParam())
                    processingMethod.getCvParam().add(convertCvParam(param));
                for (uk.ac.ebi.jmzml.model.mzml.UserParam param : theProcessingMethod.getUserParam())
                    processingMethod.getUserParam().add(convertUserParam(param));
            }
        }

        return processingMethod;
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
        // not applicable
        return "";
    }

    @Override
    public String getSearchDatabaseVersion() {
        // not applicable
        return "";
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<DatabaseMapping> getDatabaseMappings() {
        return Collections.EMPTY_LIST;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<PTM> getPTMs() {
        // not appplicable
        return Collections.EMPTY_LIST;
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

        // add the additional cvs from the file
        CVList cvList = unmarshaller.unmarshalFromXpath("/cvList", CVList.class);

        for (uk.ac.ebi.jmzml.model.mzml.CV cv : cvList.getCv()) {
            if (cv.getId().equals("PRIDE") || cv.getId().equals("MS"))
                continue;

            // add the cv
            cvs.add(new CV(cv.getId(), cv.getFullName(), cv.getVersion(), cv.getURI()));
        }

        return cvs;
    }

    @Override
    public int getSpectrumCount(boolean onlyIdentified) {
        return (onlyIdentified) ? 0 : unmarshaller.getSpectrumIDs().size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<Spectrum> getSpectrumIterator(boolean onlyIdentified) {
        return (onlyIdentified) ? Collections.EMPTY_LIST.iterator() : new MzmlSpectrumIterator();
    }

    private class MzmlSpectrumIterator implements Iterator<Spectrum>, Iterable<Spectrum> {
        int nCurrentSpec = 0;

        @Override
        public Iterator<Spectrum> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            return nCurrentSpec < spectrumIds.size();
        }

        @Override
        public Spectrum next() {
            uk.ac.ebi.jmzml.model.mzml.Spectrum mzMLSpectrum;
            try {
                mzMLSpectrum = unmarshaller.getSpectrumById(spectrumIds.get(nCurrentSpec++));
            } catch (MzMLUnmarshallerException e) {
                throw new ConverterException("Failed to load spectrum from mzML file", e);
            }

            return convertMzMlSpectrum(mzMLSpectrum);
        }

        @Override
        public void remove() {
            // not supported
        }

    }

    /**
     * Converts a mzML spectrum into a PRIDE JAXB Spectrum.
     *
     * @param mzMLSpectrum
     * @return
     */
    private Spectrum convertMzMlSpectrum(uk.ac.ebi.jmzml.model.mzml.Spectrum mzMLSpectrum) {
        Spectrum spectrum = new Spectrum();

        // make sure the spectrum contains a m/z and an intensity array
        BinaryDataArrayList dataArrayList = mzMLSpectrum.getBinaryDataArrayList();
        BinaryDataArray mzArray = null, intenArray = null;

        for (BinaryDataArray array : dataArrayList.getBinaryDataArray()) {
            // check the cvParams
            for (CVParam param : array.getCvParam()) {
                if (param.getAccession().equals("MS:1000514")) {
                    mzArray = array;
                    break;
                }
                if (param.getAccession().equals("MS:1000515")) {
                    intenArray = array;
                    break;
                }
            }

            if (mzArray != null && intenArray != null)
                break;
        }

        // if the spectrum doesn't contain a mz and binary array return null
        if (mzArray == null || intenArray == null)
            return null;

        // set the spectrum's id
        spectrum.setId(convertSpectrumIdToIndex(mzMLSpectrum.getId()));

        // set the precursor list
        SpectrumDesc spectrumDesc = new SpectrumDesc();

        // create the spectrumSettings/spectrumInstrument (mzRangeStop, mzRangeStart, msLevel)
        SpectrumSettings settings = new SpectrumSettings();
        SpectrumInstrument instrument = new SpectrumInstrument();

        // get the ms level
        CvParam msLevel = getCvParamFromGroup("MS:1000511", mzMLSpectrum.getCvParam());

        if (msLevel != null)
            instrument.setMsLevel(Integer.parseInt(msLevel.getValue()));
        else
            instrument.setMsLevel(0);

        // sort the masses to get the minimum and max
        instrument.setMzRangeStart(getMinNumber(mzArray));
        instrument.setMzRangeStop(getMaxNumber(mzArray));

        // set the spectrum settings
        settings.setSpectrumInstrument(instrument);
        spectrumDesc.setSpectrumSettings(settings);

        spectrumDesc.setPrecursorList(convertPrecursorList(mzMLSpectrum.getPrecursorList(), instrument.getMsLevel()));

        spectrum.setSpectrumDesc(spectrumDesc);

        // set the data arrays
        IntenArrayBinary intenArrayBin = new IntenArrayBinary();
        intenArrayBin.setData(convertPeakList(intenArray));

        MzArrayBinary massArrayBinary = new MzArrayBinary();
        massArrayBinary.setData(convertPeakList(mzArray));

        spectrum.setIntenArrayBinary(intenArrayBin);
        spectrum.setMzArrayBinary(massArrayBinary);

        return spectrum;
    }

    /**
     * Converts a mzML BinaryDataArray into a PRIDE JAXB
     * Data object.
     *
     * @param mzMLData The mzML data to convert.
     * @return The converted Data array as a PRIDE JAXB Data object.
     */
    private Data convertPeakList(BinaryDataArray mzMLData) {
        // get the values as numbers
        Number values[] = mzMLData.getBinaryDataAsNumberArray();
        // create a double collection
        ArrayList<Double> doubleValues = new ArrayList<Double>(values.length);

        for (Number n : values)
            doubleValues.add(n.doubleValue());

        // create the byte array
        byte[] byteArray = doubleCollectionToByteArray(doubleValues);

        // create the data
        Data data = new Data();

        // set the data
        data.setEndian("little");
        data.setLength(values.length);
        data.setPrecision("64");
        data.setValue(byteArray);

        return data;
    }

    /**
     * Returns the minimum number from a
     * jmzml BinaryDataArray.
     *
     * @param array
     * @return
     */
    private Float getMinNumber(BinaryDataArray array) {
        Float min = new Float(10000.0); // some really high number

        for (Number n : array.getBinaryDataAsNumberArray()) {
            if (n.floatValue() < min)
                min = n.floatValue();
        }

        return min;
    }

    /**
     * Returns the maximum number as Float from a
     * jmzml BinaryDataArray.
     *
     * @param array
     * @return
     */
    private Float getMaxNumber(BinaryDataArray array) {
        Float max = new Float(0.0);

        for (Number n : array.getBinaryDataAsNumberArray()) {
            if (n.floatValue() > max)
                max = n.floatValue();
        }

        return max;
    }

    /**
     * Converts the given MzML Precursor list into a PRIDE JAXB Precursor
     * List.
     *
     * @param thePrecList
     * @param specMsLevel The msLevel of the parent spectrum. 0 if it is unknown.
     * @return
     */
    private PrecursorList convertPrecursorList(uk.ac.ebi.jmzml.model.mzml.PrecursorList thePrecList, int specMsLevel) {

        // if there's no precursor list, return null as well
        //    this fixed a bug where an empty precursor list was returned - this was schematically invalid
        if (thePrecList == null) {
            return null;
        }

        PrecursorList precList = new PrecursorList();

        // set the count
        precList.setCount(thePrecList.getCount().intValue());

        // process the precursor
        for (uk.ac.ebi.jmzml.model.mzml.Precursor thePrecursor : thePrecList.getPrecursor()) {
            Precursor prec = new Precursor();

            // set the spectrum reference
            Spectrum refSpectrum = new Spectrum();
            refSpectrum.setId(0);

            if (thePrecursor.getSpectrum() != null)
                refSpectrum.setId(convertSpectrumIdToIndex(thePrecursor.getSpectrum().getId()));

            prec.setSpectrum(refSpectrum);

            // set the ion selection
            uk.ac.ebi.pride.jaxb.model.Param ionSelection = new uk.ac.ebi.pride.jaxb.model.Param();

            for (ParamGroup selectedIon : thePrecursor.getSelectedIonList().getSelectedIon()) {
                for (CVParam param : selectedIon.getCvParam())
                    ionSelection.getCvParam().add(jCvParam(param.getCvRef(), param.getAccession(), param.getName(), param.getValue()));
                for (uk.ac.ebi.jmzml.model.mzml.UserParam param : selectedIon.getUserParam()) {
                    uk.ac.ebi.pride.jaxb.model.UserParam userParam = new uk.ac.ebi.pride.jaxb.model.UserParam();
                    userParam.setName(param.getName());
                    userParam.setValue(param.getValue());
                    ionSelection.getUserParam().add(userParam);
                }
            }

            prec.setIonSelection(ionSelection);

            // set the activation
            uk.ac.ebi.pride.jaxb.model.Param activation = new uk.ac.ebi.pride.jaxb.model.Param();

            for (CVParam param : thePrecursor.getActivation().getCvParam())
                activation.getCvParam().add(jCvParam(param.getCvRef(), param.getAccession(), param.getName(), param.getValue()));
            for (uk.ac.ebi.jmzml.model.mzml.UserParam param : thePrecursor.getActivation().getUserParam()) {
                uk.ac.ebi.pride.jaxb.model.UserParam userParam = new uk.ac.ebi.pride.jaxb.model.UserParam();
                userParam.setName(param.getName());
                userParam.setValue(param.getValue());
                activation.getUserParam().add(userParam);
            }

            prec.setActivation(activation);

            // get the ms level
            if (thePrecursor.getSpectrum() != null) {
                CvParam msLevel = getCvParamFromGroup("MS:1000511", thePrecursor.getSpectrum().getCvParam());

                if (msLevel != null)
                    prec.setMsLevel(Integer.parseInt(msLevel.getValue()));
            }

            // if no precursor ms level could be retrieved, use the spectrum's level - 1
            if (prec.getMsLevel() < 1 && specMsLevel > 1)
                prec.setMsLevel(specMsLevel - 1);

            // add the precusor
            precList.getPrecursor().add(prec);
        }

        return precList;
    }

    @Override
    public int getSpectrumReferenceForPeptideUID(String peptideUID) {
        throw new ConverterException("mzML files do not support peptide identifications.");
    }

    @Override
    public Identification getIdentificationByUID(String identificationUID) {
        throw new ConverterException("mzML files do not support peptide identifications.");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<Identification> getIdentificationIterator(
            boolean prescanMode) {
        // not supported
        return Collections.EMPTY_LIST.iterator();
    }

    /**
     * Converts the given mzML CVParam to a PRIDE Converter
     * cvParam.
     *
     * @param param The mzML CVParam
     * @return The PRIDE Converter cvParam
     */
    private CvParam convertCvParam(CVParam param) {
        CvParam converterParam = new CvParam(param.getCvRef(), param.getAccession(), param.getName(), param.getValue());

        return converterParam;
    }

    /**
     * Converts the given mzML userParam into a PRIDE
     * Converter userParam.
     *
     * @param param
     * @return
     */
    private UserParam convertUserParam(uk.ac.ebi.jmzml.model.mzml.UserParam param) {
        return new UserParam(param.getName(), param.getValue());
    }

    /**
     * Returns the parameter identified by the supplied accession
     * from the param group. In case the parameter doesn't exist
     * null is returned.
     *
     * @param accession  The accession identifying the wanted parameter.
     * @param paramGroup The paramGroup to get the parameter from.
     * @return The PRIDE Converter CvParam retrieved
     */
    private CvParam getCvParamFromGroup(String accession, ParamGroup paramGroup) {
        // loop through the group
        for (CVParam param : paramGroup.getCvParam()) {
            if (param.getAccession().equals(accession))
                return convertCvParam(param);
        }

        return null;
    }

    /**
     * Returns the parameter identified by the supplied accession
     * from the CVParam List. In case the parameter doesn't exist
     * null is returned.
     *
     * @param accession  The accession identifying the wanted parameter.
     * @param paramGroup The List<CVParam> to get the parameter from.
     * @return The PRIDE Converter CvParam retrieved
     */
    private CvParam getCvParamFromGroup(String accession, List<CVParam> paramGroup) {
        // loop through the group
        for (CVParam param : paramGroup) {
            if (param.getAccession().equals(accession))
                return convertCvParam(param);
        }

        return null;
    }

    /**
     * Converts the given spectrum id to 1-based integer id.
     *
     * @param id
     * @return
     */
    private int convertSpectrumIdToIndex(String id) {
        for (int i = 0; i < spectrumIds.size(); i++)
            if (spectrumIds.get(i).equals(id))
                return i + 1;

        return 0;
    }
}
