package uk.ac.ebi.pride.tools.converter.gui.model;

import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.dao.DAOFactory;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
import uk.ac.ebi.pride.tools.converter.report.model.DatabaseMapping;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;

import java.io.File;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author melih
 * @author rcote
 *         Date: 06/06/2011
 *         Time: 11:08
 */
public class ConverterData {

    public static final String REPORT_XML = "-report.xml";
    public static final String MZTAB = "-mztab.txt";
    public static final String PRIDE_XML = "-pride.xml";
    public static final String DEFAULT_OUTPUT_LOCATION = System.getProperty("java.io.tmpdir") + File.separator + "prideconverter";

    // Key = input File Path
    // Value = report file path
    private Map<String, String> inputFiles = new HashMap<String, String>();
    private List<String> outputfiles = new ArrayList<String>();
    private List<String> fastaFiles = new ArrayList<String>();
    private List<String> mztabFiles = new ArrayList<String>();
    private Properties options = new Properties();
    private DAOFactory.DAO_FORMAT daoFormat = null;
    private String masterReportFileName = "";
    private String lastSelectedDirectory = null;
    private Map<String, Collection<ValidatorMessage>> validationMessages = new HashMap<String, Collection<ValidatorMessage>>();
    private DataType type;

    private Set<PTM> PTMs = new HashSet<PTM>();
    private Set<DatabaseMapping> databaseMappings = new HashSet<DatabaseMapping>();
    private ReportReaderDAO masterDAO;
    private Set<String> filesToDelete = new HashSet<String>();

    private static ConverterData instance = new ConverterData();

    public static ConverterData getInstance() {
        return instance;
    }

    private ConverterData() {
    }

    public Map<String, String> getInputFiles() {
        return inputFiles;
    }

    public List<String> getOutputfiles() {
        return outputfiles;
    }

    public Properties getOptions() {
        return options;
    }

    public DAOFactory.DAO_FORMAT getDaoFormat() {
        return daoFormat;
    }

    public String getMasterReportFileName() {
        return masterReportFileName;
    }

    public void setMasterReportFileName(String masterReportFileName) {
        this.masterReportFileName = masterReportFileName;
    }

    public void setDaoFormat(DAOFactory.DAO_FORMAT daoFormat) {
        this.daoFormat = daoFormat;
    }

    public void setOptions(Properties options) {
        this.options = options;
    }

    public String getLastSelectedDirectory() {
        return lastSelectedDirectory;
    }

    public void setLastSelectedDirectory(String lastSelectedDirectory) {
        this.lastSelectedDirectory = lastSelectedDirectory;
    }

    public List<String> getFastaFiles() {
        return fastaFiles;
    }

    public List<String> getMztabFiles() {
        return mztabFiles;
    }

    public void reset() {
        inputFiles.clear();
        fastaFiles.clear();
        outputfiles.clear();
        mztabFiles.clear();
        validationMessages.clear();
        options = new Properties();
        daoFormat = null;
        masterReportFileName = "";
        type = null;
        PTMs.clear();
        databaseMappings.clear();
        filesToDelete.clear();
    }

    public void clearPossibleStaleData() {
        PTMs.clear();
        validationMessages.clear();
        databaseMappings.clear();
        filesToDelete.clear();
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    public Set<PTM> getPTMs() {
        return PTMs;
    }

    public Set<DatabaseMapping> getDatabaseMappings() {
        return databaseMappings;
    }

    public void setValidationMessages(String prideXmlFile, Collection<ValidatorMessage> validatorMessages) {
        validationMessages.put(prideXmlFile, validatorMessages);
    }

    public Map<String, Collection<ValidatorMessage>> getValidationMessages() {
        return validationMessages;
    }

    public ReportReaderDAO getMasterDAO() {
        return masterDAO;
    }

    public void setMasterDAO(ReportReaderDAO masterDAO) {
        this.masterDAO = masterDAO;
    }

    public Set<String> getFilesToDelete() {
        return filesToDelete;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ConverterData");
        sb.append("{inputFiles=").append(inputFiles);
        sb.append(", outputfiles=").append(outputfiles);
        sb.append(", fastaFiles=").append(fastaFiles);
        sb.append(", mztabFiles=").append(mztabFiles);
        sb.append(", options=").append(options);
        sb.append(", daoFormat=").append(daoFormat);
        sb.append(", masterReportFileName='").append(masterReportFileName).append('\'');
        sb.append(", lastSelectedDirectory='").append(lastSelectedDirectory).append('\'');
        sb.append(", validationMessages=").append(validationMessages);
        sb.append(", type=").append(type);
        sb.append(", PTMs=").append(PTMs);
        sb.append(", databaseMappings=").append(databaseMappings);
        sb.append(", masterDAO=").append(masterDAO);
        sb.append(", filesToDelete=").append(filesToDelete);
        sb.append('}');
        return sb.toString();
    }
}
