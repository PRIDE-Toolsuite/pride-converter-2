package uk.ac.lifesci.dundee.tools.converter;

import org.xml.sax.InputSource;
import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.dao.DAO;
import uk.ac.ebi.pride.tools.converter.dao.DAOProperty;
import uk.ac.ebi.pride.tools.converter.dao.impl.AbstractDAOImpl;
import uk.ac.ebi.pride.tools.converter.dao.impl.MzmlDAO;
import uk.ac.ebi.pride.tools.converter.report.model.*;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class GrePrideConverterDAO extends AbstractDAOImpl implements DAO {

    public static final String CONFIGURATION_FILE_PROP = "configurationfile";
    public static final String MAXQUANT_FILES_PROP = "maxquantfiles";

    private final String searchEngineString = "MaxQuant";
    private final String searchEngineVersion = "0.1";
    private static Collection<DAOProperty> supportedProperties = new ArrayList<DAOProperty>();

    protected AbstractDAOImpl spectraDAO = null;

    protected Report metadataReport = null;

    protected AbstractDAOImpl identificationDAO = null;

    protected static Utils<String> str_util = new Utils<String>();

    protected File spectraFile = null;

    public GrePrideConverterDAO(File spectra) {
        spectraFile = spectra;
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

        //todo - need to support spectra dao properties
//        supportedProperties.addAll(spectraDAO.getSupportedProperties());

        return supportedProperties;

    }

    @Override
    public SourceFile getSourceFile() throws InvalidFormatException {
        return spectraDAO.getSourceFile();
    }


    @Override
    public void setConfiguration(Properties props) {
        //connect to Peptracker
        initMetadata(props);
//todo fix
//        identificationDAO.setConfiguration(props);
        spectraDAO.setConfiguration(props);
    }

    private void initMetadata(Properties props) {

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

        String endPoint = peptrackerProps.getProperty("endpoint");
        String sampleId = peptrackerProps.getProperty("sample.identifier");
        String username = peptrackerProps.getProperty("username");
        String password = peptrackerProps.getProperty("password");
        String spectraFormat = peptrackerProps.getProperty("spectra.file.format");

        URL url = null;
        try {
            StringBuilder sb = new StringBuilder(endPoint).append(sampleId).append("/experiment_metadata");
            url = new URL(sb.toString());
            URLConnection uc = url.openConnection();
//            String val = (new StringBuffer(username).append(":").append(password)).toString();
//            byte[] base = val.getBytes();
//            String authorizationString = "Basic " + new String(new Base64().encode(base));
//            uc.setRequestProperty ("Authorization", authorizationString);

            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            sb = new StringBuilder();
            String oneLine;
            while ((oneLine = br.readLine()) != null) {
                sb.append(oneLine);
            }
            metadataReport = new MetadataExtractor(sb.toString()).getMetadataReport();
            System.out.println("metadataReport = " + metadataReport);

            //select proper spectra dao
            if ("mzml".equalsIgnoreCase(spectraFormat)) {
                spectraDAO = new MzmlDAO(spectraFile);
            }

        } catch (Exception e) {
            throw new ConverterException("Error obtaining metadata from Peptracker", e);
        }

    }

    @Override
    public Properties getConfiguration() {
        //todo update with current config
        return identificationDAO.getConfiguration();
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
        software.setVersion(searchEngineVersion);
        return software;
    }

    @Override
    public Param getProcessingMethod() {
        return identificationDAO.getProcessingMethod();
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
        return identificationDAO.getSearchDatabaseName();
    }

    @Override
    public String getSearchDatabaseVersion() throws InvalidFormatException {
        return identificationDAO.getSearchDatabaseVersion();
    }

    @Override
    public Collection<PTM> getPTMs() throws InvalidFormatException {
        return identificationDAO.getPTMs();
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
        return identificationDAO.getSpectrumReferenceForPeptideUID(peptideUID);
    }

    @Override
    public Identification getIdentificationByUID(String identificationUID)
            throws InvalidFormatException {
        return identificationDAO.getIdentificationByUID(identificationUID);
    }

    @Override
    public Iterator<Identification> getIdentificationIterator(
            boolean prescanMode) throws InvalidFormatException {
        return identificationDAO.getIdentificationIterator(prescanMode);
    }

    @Override
    public void setExternalSpectrumFile(String filename) {
        spectraDAO.setExternalSpectrumFile(filename);

    }


    private static class MetadataExtractor {

        private JAXBContext jc = null;
        private Unmarshaller unmarshaller = null;
        private Report metadataReport;

        private MetadataExtractor(String reportXML) {
            try {
                jc = JAXBContext.newInstance(ModelConstants.MODEL_PKG);
                unmarshaller = jc.createUnmarshaller();
                metadataReport = unmarshal(reportXML);
            } catch (JAXBException e) {
                throw new ConverterException("Error initializing metadata reader", e);
            }
        }

        private Report unmarshal(String xmlSnippet) throws ConverterException {

            try {

                if (xmlSnippet == null) {
                    return null;
                }

                @SuppressWarnings("unchecked")
                JAXBElement<Report> holder = unmarshaller.unmarshal(new SAXSource(new InputSource(new StringReader(xmlSnippet))), Report.class);
                return holder.getValue();

            } catch (JAXBException e) {
                throw new ConverterException("Error unmarshalling object: " + e.getMessage(), e);
            }

        }

        public Report getMetadataReport() {
            return metadataReport;
        }
    }

}
