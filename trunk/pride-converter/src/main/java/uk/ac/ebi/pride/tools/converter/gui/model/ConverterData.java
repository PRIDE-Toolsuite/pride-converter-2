package uk.ac.ebi.pride.tools.converter.gui.model;

import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.dao.DAOFactory;
import uk.ac.ebi.pride.tools.converter.dao.handler.HandlerFactory;
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
    public static final String MERGED_XML = "-merged.xml";
    public static final String MZTAB = "-mztab.txt";
    public static final String PRIDE_XML = "-pride.xml";
    public static final String DEFAULT_OUTPUT_LOCATION = System.getProperty("java.io.tmpdir") + File.separator + "prideconverter";

    private TreeSet<FileBean> dataFiles = new TreeSet<FileBean>();
    private List<String> mzTabFiles = new ArrayList<String>();
    private Properties options = new Properties();
    private DAOFactory.DAO_FORMAT daoFormat = null;
    private FileBean masterFile = null;
    private String lastSelectedDirectory = null;
    private Map<String, Collection<ValidatorMessage>> validationMessages = new HashMap<String, Collection<ValidatorMessage>>();
    private DataType type;
    private HandlerFactory.FASTA_FORMAT fastaFormat;

    private Set<PTM> PTMs = new HashSet<PTM>();
    private Set<DatabaseMapping> databaseMappings = new HashSet<DatabaseMapping>();
    private ReportReaderDAO masterDAO;
    private Set<String> filesToDelete = new HashSet<String>();
    private Map<String, ReportBean> customeReportFields = new TreeMap<String, ReportBean>();

    private String mergedOutputFile = null;

    private static ConverterData instance = new ConverterData();

    public static ConverterData getInstance() {
        return instance;
    }

    private ConverterData() {
    }

    public Set<FileBean> getDataFiles() {
        return dataFiles;
    }

    public FileBean getFileBeanByInputFileName(String filename) {
        for (FileBean fb : dataFiles) {
            if (fb.getInputFile().equals(filename)) {
                return fb;
            }
        }
        return null;
    }

    /**
     * Returns a sorted set of input file paths. This set should not be modified as it is always generated based on the
     * content of the collection of FileBean objects.
     *
     * @return
     */
    public Set<String> getInputFiles() {
        Set<String> inputFiles = new TreeSet<String>();
        for (FileBean fb : dataFiles) {
            inputFiles.add(fb.getInputFile());
        }
        return inputFiles;
    }

    /**
     * Returns a sorted set of output file paths. This set should not be modified as it is always generated based on the
     * content of the collection of FileBean objects.
     *
     * @return
     */
    public Set<String> getOutputfiles() {
        Set<String> outputFiles = new TreeSet<String>();
        for (FileBean fb : dataFiles) {
            if (fb.getOutputFile() != null) {
                outputFiles.add(fb.getOutputFile());
            }
        }
        return outputFiles;
    }

    /**
     * Returns a sorted set of report file paths. This set should not be modified as it is always generated based on the
     * content of the collection of FileBean objects.
     *
     * @return
     */
    public Set<String> getReportFiles() {
        Set<String> reportFiles = new TreeSet<String>();
        for (FileBean fb : dataFiles) {
            if (fb.getReportFile() != null) {
                reportFiles.add(fb.getReportFile());
            }
        }
        return reportFiles;
    }

    public Properties getOptions() {
        return options;
    }

    public DAOFactory.DAO_FORMAT getDaoFormat() {
        return daoFormat;
    }

    public FileBean getMasterFile() {
        return masterFile;
    }

    public void setMasterFile(FileBean masterFile) {
        this.masterFile = masterFile;
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

    /**
     * Returns a list of mztab file paths. This collection is independent of the data stored in the FileBean
     * collection and should be used in the mapping of the mztab files to the proper input files.
     *
     * @return
     */
    public List<String> getMztabFiles() {
        return mzTabFiles;
    }

    public void reset() {
        dataFiles.clear();
        mzTabFiles.clear();
        validationMessages.clear();
        options = new Properties();
        daoFormat = null;
        masterFile = null;
        type = null;
        PTMs.clear();
        databaseMappings.clear();
        filesToDelete.clear();
        customeReportFields.clear();
        mergedOutputFile = null;
        fastaFormat = null;
    }

    public void clearPossibleStaleData() {
        //basically, the only possible stale data will be everything
        //except the dao format and the type, which will have been set
        //in the first screen
        fastaFormat = null;
        dataFiles.clear();
        validationMessages.clear();
        options = new Properties();
        masterFile = null;
        PTMs.clear();
        databaseMappings.clear();
        filesToDelete.clear();
        customeReportFields.clear();
        dataFiles.clear();
        mzTabFiles.clear();
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

    public Map<String, ReportBean> getCustomeReportFields() {
        return customeReportFields;
    }

    public void setCustomeReportFields(String fileName, ReportBean customFieldData) {
        customeReportFields.put(fileName, customFieldData);
    }

    public String getMergedOutputFile() {
        return mergedOutputFile;
    }

    public void setMergedOutputFile(String mergedOutputFile) {
        this.mergedOutputFile = mergedOutputFile;
    }

    public HandlerFactory.FASTA_FORMAT getFastaFormat() {
        return fastaFormat;
    }

    public void setFastaFormat(HandlerFactory.FASTA_FORMAT fastaFormat) {
        this.fastaFormat = fastaFormat;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ConverterData");
        sb.append("{dataFiles=").append(dataFiles);
        sb.append(", mzTabFiles=").append(mzTabFiles);
        sb.append(", options=").append(options);
        sb.append(", daoFormat=").append(daoFormat);
        sb.append(", masterFile=").append(masterFile);
        sb.append(", lastSelectedDirectory='").append(lastSelectedDirectory).append('\'');
        sb.append(", validationMessages=").append(validationMessages);
        sb.append(", type=").append(type);
        sb.append(", fastaFormat=").append(fastaFormat);
        sb.append(", PTMs=").append(PTMs);
        sb.append(", databaseMappings=").append(databaseMappings);
        sb.append(", masterDAO=").append(masterDAO);
        sb.append(", filesToDelete=").append(filesToDelete);
        sb.append(", customeReportFields=").append(customeReportFields);
        sb.append(", mergedOutputFile='").append(mergedOutputFile).append('\'');
        sb.append('}');
        return sb.toString();
    }
}