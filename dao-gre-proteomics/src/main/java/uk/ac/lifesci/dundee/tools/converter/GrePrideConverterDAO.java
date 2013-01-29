package uk.ac.lifesci.dundee.tools.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;

import org.apache.axis.encoding.Base64;
import org.apache.log4j.Logger;

import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.dao.DAO;
import uk.ac.ebi.pride.tools.converter.dao.DAOProperty;
import uk.ac.ebi.pride.tools.converter.dao.handler.FastaHandler;
import uk.ac.ebi.pride.tools.converter.dao.impl.AbstractDAOImpl;
import uk.ac.ebi.pride.tools.converter.dao.impl.MgfDAO;
import uk.ac.ebi.pride.tools.converter.dao.impl.MzXmlDAO;
import uk.ac.ebi.pride.tools.converter.dao.impl.MzmlDAO;
import uk.ac.ebi.pride.tools.converter.report.model.CV;
import uk.ac.ebi.pride.tools.converter.report.model.Contact;
import uk.ac.ebi.pride.tools.converter.report.model.DatabaseMapping;
import uk.ac.ebi.pride.tools.converter.report.model.Identification;
import uk.ac.ebi.pride.tools.converter.report.model.InstrumentDescription;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.Protocol;
import uk.ac.ebi.pride.tools.converter.report.model.Reference;
import uk.ac.ebi.pride.tools.converter.report.model.Report;
import uk.ac.ebi.pride.tools.converter.report.model.SearchResultIdentifier;
import uk.ac.ebi.pride.tools.converter.report.model.Software;
import uk.ac.ebi.pride.tools.converter.report.model.SourceFile;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;
import uk.ac.ebi.pride.tools.utils.AccessionResolver;
import uk.ac.lifesci.dundee.tools.converter.maxquant.MaxquantParser;


public class GrePrideConverterDAO extends AbstractDAOImpl implements DAO {

    private static final Logger logger = Logger.getLogger(GrePrideConverterDAO.class);
    //constants
    public static final String CONFIGURATION_FILE_PROP = "configurationfile";
    public static final String MAXQUANT_FILES_PROP = "maxquantfiles";
    public static final String ENDPOINT_PROPERTY = "endpoint";
    public static final String SAMPLE_IDENTIFIER_PROPERTY = "sample.identifier";
    public static final String USERNAME_PROPERTY = "username";
    public static final String PASSWORD_PROPERTY = "password";
    public static final String SPECTRA_FILE_FORMAT_PROPERTY = "spectra.file.format";
    public static final String ENDPOINT_METHOD = "/experiment_metadata";
    private static final String searchEngineString = "MaxQuant";

    //configuration and properties
    private static Collection<DAOProperty> supportedProperties = new ArrayList<DAOProperty>();
    private Properties configProperties;

    //spectrum file handling and delegation
    private AbstractDAOImpl spectraDAO = null;
    private File spectraFile = null;

    //metadata holder from Peptracker
    private Report metadataReport = null;

    //maxquant parser
    private MaxquantParser maxquantParser = null;

    private static Utils<String> str_util = new Utils<String>();
    
    
    /**
     * Default Contructor
     * 
     * @param spectra
     */
    public GrePrideConverterDAO(File spectra) {
        spectraFile = spectra;
        FastaHandler fileHandler;
        AccessionResolver r;
    }

    public static Collection<DAOProperty> getSupportedProperties() {

        // Peptracker config file
        DAOProperty<Double> configFile = new DAOProperty<Double>(CONFIGURATION_FILE_PROP, null);
        configFile.setDescription("Specifies the full path to a .properties file containing the necessary information to connect to Peptracker");
        configFile.setShortDescription("Path to .properties file to connect to Peptracker");
        supportedProperties.add(configFile);

        // identification file folder
        DAOProperty<Double> maxquantFiles = new DAOProperty<Double>(MAXQUANT_FILES_PROP, null);
        maxquantFiles.setDescription("Specifies the full path to a folder containing the MaxQuant resultFile");
        maxquantFiles.setShortDescription("Path to folder containing maxquant files");
        supportedProperties.add(maxquantFiles);

        //todo - need to support spectra dao properties for all future supported spectra formats
        //todo - note that mzXML and peaklist DAOs do not currently support any properties
        for (DAOProperty property : MzmlDAO.getSupportedProperties()) {
            property.setDescription(property.getDescription() + " ONLY APPLIES IF USING MZML SPECTRA FILES");
            supportedProperties.add(property);
        }

        return supportedProperties;

    }

    @Override
    public SourceFile getSourceFile() throws InvalidFormatException {
        return spectraDAO.getSourceFile();
    }


    @Override
    public void setConfiguration(Properties props) {
        //connect to Peptracker
        Properties loadedProps = initMetadata(props);
        //initMetadata will initialize the spectra dao to avoid a NPE
        spectraDAO.setConfiguration(props);
        //keep track of properties
        configProperties = props;

        String maxquantPath = props.getProperty(MAXQUANT_FILES_PROP);
        if (maxquantPath == null) {
            throw new ConverterException("Please specify a path for the maxquant files");
        }
        String sampleId = loadedProps.getProperty(SAMPLE_IDENTIFIER_PROPERTY);
        maxquantParser = new MaxquantParser(maxquantPath, sampleId);
    }

    private Properties initMetadata(Properties props) {

        //load properties from peptracker property file
        String peptrackerConfig = props.getProperty(CONFIGURATION_FILE_PROP);
        if (peptrackerConfig == null) {
            throw new ConverterException("Please specify a Peptracker configuration file");
        }
        File peptrackerConfigFile = new File(peptrackerConfig);
        if (!peptrackerConfigFile.exists()) {
            throw new ConverterException("Submitted peptracker config file not found:" + peptrackerConfig);
        }

        Properties peptrackerProps = new Properties();
        try {
            peptrackerProps.load(new FileReader(peptrackerConfigFile));
        } catch (IOException e) {
            throw new ConverterException("Error reading peptracker property file", e);
        }

        String endPoint = peptrackerProps.getProperty(ENDPOINT_PROPERTY);
        String sampleId = peptrackerProps.getProperty(SAMPLE_IDENTIFIER_PROPERTY);
        String username = peptrackerProps.getProperty(USERNAME_PROPERTY);
        String password = peptrackerProps.getProperty(PASSWORD_PROPERTY);
        String spectraFormat = peptrackerProps.getProperty(SPECTRA_FILE_FORMAT_PROPERTY);

        logger.debug("Connecting to Peptracker");
        URL url = null;
        try {
            //build url
            StringBuilder sb = new StringBuilder(endPoint).append(sampleId).append(ENDPOINT_METHOD);
            url = new URL(sb.toString());
            
            // stuff the Authorization request header
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            String authString = new StringBuffer(username).append(":").append(password).toString();
            String encodedString = new String(Base64.encode(authString.getBytes()));
            con.setRequestProperty("Authorization", "Basic " + encodedString);

            //read XML response
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            sb = new StringBuilder();
            String oneLine;
            while ((oneLine = br.readLine()) != null) {
                sb.append(oneLine);
            }

            //build report object
            metadataReport = new MetadataExtractor(sb.toString()).getMetadataReport();
            logger.warn("Metadata retrieved");

            //select proper spectra dao
            if ("mzml".equalsIgnoreCase(spectraFormat)) {
                spectraDAO = new MzmlDAO(spectraFile);
            } else if ("mzxml".equalsIgnoreCase(spectraFormat)) {
                spectraDAO = new MzXmlDAO(spectraFile);
            } else if ("mgf".equalsIgnoreCase(spectraFormat)) {
                spectraDAO = new MgfDAO(spectraFile);
            } else {
                throw new ConverterException("Unsupported spectra file format: " + spectraFormat);
            }

        } catch (Exception e) {
            throw new ConverterException("Error obtaining metadata from Peptracker", e);
        }
        
        return peptrackerProps;

    }

    @Override
    public Properties getConfiguration() {
        return configProperties;
    }

    @Override
    public String getExperimentTitle() throws InvalidFormatException {
        return str_util.nvl(metadataReport.getMetadata().getTitle(), "");
    }

    @Override
    public String getExperimentShortLabel() {
        return str_util.nvl(metadataReport.getMetadata().getShortLabel(), "");
    }

    @Override
    public Param getExperimentParams() throws InvalidFormatException {
        return metadataReport.getMetadata().getExperimentAdditional();
    }

    @Override
    public String getSampleName() {
        return str_util.nvl(metadataReport.getMetadata().getMzDataDescription().getAdmin().getSampleName(), "");
    }

    @Override
    public String getSampleComment() {
        return str_util.nvl(metadataReport.getMetadata().getMzDataDescription().getAdmin().getSampleDescription().getComment(), "");
    }

    @Override
    public Param getSampleParams() throws InvalidFormatException {
        return metadataReport.getMetadata().getMzDataDescription().getAdmin().getSampleDescription();
    }

    @Override
    public Collection<Contact> getContacts() {
        return metadataReport.getMetadata().getMzDataDescription().getAdmin().getContact();
    }

    @Override
    public InstrumentDescription getInstrument() {
        return metadataReport.getMetadata().getMzDataDescription().getInstrument();
    }

    @Override
    public Software getSoftware() throws InvalidFormatException {
        Software software = new Software();
        software.setName(searchEngineString);
        software.setVersion(maxquantParser.getVersion());
        return software;
    }

    @Override
    public Param getProcessingMethod() {
        return maxquantParser.getProcessingMethod();
    }

    @Override
    public Protocol getProtocol() {
        return metadataReport.getMetadata().getProtocol();
    }

    @Override
    public Collection<Reference> getReferences() {
        return metadataReport.getMetadata().getReference();
    }

    @Override
    public String getSearchDatabaseName() throws InvalidFormatException {
        return maxquantParser.getSearchDatabaseName();
    }

    @Override
    public String getSearchDatabaseVersion() throws InvalidFormatException {
        return maxquantParser.getSearchDatabaseVersion();
    }

    @Override
    public Collection<PTM> getPTMs() throws InvalidFormatException {
        return maxquantParser.getPTMs();
    }

    @Override
    public Collection<DatabaseMapping> getDatabaseMappings()
            throws InvalidFormatException {
        return Collections.EMPTY_LIST;
    }

    @Override
    public SearchResultIdentifier getSearchResultIdentifier()
            throws InvalidFormatException {
        return spectraDAO.getSearchResultIdentifier();
    }

    @Override
    public Collection<CV> getCvLookup() throws InvalidFormatException {
        return spectraDAO.getCvLookup();
        //ms/
        //pride
        //psi
        //TISSUE  or Pride N/A (pride:0000442), name=Tissue not applicable to dataset
        //SPECIES From Uniprot Taxnomy
    }

    @Override
    public int getSpectrumCount(boolean onlyIdentified)
            throws InvalidFormatException {
        return spectraDAO.getSpectrumCount(onlyIdentified);
    }

    @Override
    public Iterator<Spectrum> getSpectrumIterator(boolean onlyIdentified)
            throws InvalidFormatException {
        return spectraDAO.getSpectrumIterator(onlyIdentified);
    }

    @Override
    public int getSpectrumReferenceForPeptideUID(String peptideUID)
            throws InvalidFormatException {
        return maxquantParser.getSpectrumReferenceForPeptideUID(peptideUID);
    }

    @Override
    public Identification getIdentificationByUID(String identificationUID)
            throws InvalidFormatException {
        return maxquantParser.getIdentificationByUID(identificationUID);
    }

    @Override
    public Iterator<Identification> getIdentificationIterator(
            boolean prescanMode) throws InvalidFormatException {
        return maxquantParser.getIdentificationIterator(prescanMode);
    }

    @Override
    public void setExternalSpectrumFile(String filename) {
        spectraDAO.setExternalSpectrumFile(filename);
    }

}
