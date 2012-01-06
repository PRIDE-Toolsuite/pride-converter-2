package uk.ac.ebi.pride.tools.converter.report.io;

import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.dao.DAO;
import uk.ac.ebi.pride.tools.converter.dao.handler.FastaHandler;
import uk.ac.ebi.pride.tools.converter.report.model.*;

import java.io.File;
import java.util.*;

/**
 * User: rcote
 * Date: 07/01/11
 * Time: 16:56
 * <p/>
 * This class will be used by the Converter GUI to update an existing report file
 */
public class ReportReaderDAO implements DAO, FastaHandler {

    private ReportReader reader = null;
    private List<CV> cvLookup = new ArrayList<CV>();
    private String experimentTitle;
    private String experimentShortLabel;
    private Param experimentParams;
    private String sampleName;
    private String sampleComment;
    private Param sampleParams;
    private SourceFile sourceFile;
    private List<Contact> contacts;
    private InstrumentDescription instrument;
    private Software software;
    private Param processingMethod;
    private Protocol protocol;
    private List<PTM> PTMs = new ArrayList<PTM>();
    private List<DatabaseMapping> databaseMappings = new ArrayList<DatabaseMapping>();
    private List<Reference> references = new ArrayList<Reference>();
    private String searchDatabaseName;
    private String searchDatabaseVersion;
    private SearchResultIdentifier searchResultIdentifier;
    private ConfigurationOptions configuration;

    public ReportReaderDAO(File reportFile) {
        reader = new ReportReader(reportFile);
        cvLookup = reader.getMetadata().getMzDataDescription().getCvLookup();
        experimentTitle = reader.getMetadata().getTitle();
        experimentShortLabel = reader.getMetadata().getShortLabel();
        experimentParams = reader.getMetadata().getExperimentAdditional();
        sampleName = reader.getMetadata().getMzDataDescription().getAdmin().getSampleName();
        sampleComment = reader.getMetadata().getMzDataDescription().getAdmin().getSampleDescription().getComment();
        sampleParams = new Param();
        if (reader.getMetadata().getMzDataDescription().getAdmin().getSampleDescription().getCvParam() != null) {
            sampleParams.getCvParam().addAll(reader.getMetadata().getMzDataDescription().getAdmin().getSampleDescription().getCvParam());
        }
        if (reader.getMetadata().getMzDataDescription().getAdmin().getSampleDescription().getUserParam() != null) {
            sampleParams.getUserParam().addAll(reader.getMetadata().getMzDataDescription().getAdmin().getSampleDescription().getUserParam());
        }
        sourceFile = reader.getMetadata().getMzDataDescription().getAdmin().getSourceFile();
        contacts = reader.getMetadata().getMzDataDescription().getAdmin().getContact();
        instrument = reader.getMetadata().getMzDataDescription().getInstrument();
        software = reader.getMetadata().getMzDataDescription().getDataProcessing().getSoftware();
        processingMethod = reader.getMetadata().getMzDataDescription().getDataProcessing().getProcessingMethod();
        protocol = reader.getMetadata().getProtocol();
        Iterator<PTM> iPTM = reader.getPTMIterator();
        while (iPTM.hasNext()) {
            PTMs.add(iPTM.next());
        }
        Iterator<DatabaseMapping> dbm = reader.getDatabaseMappingIterator();
        while (dbm.hasNext()) {
            databaseMappings.add(dbm.next());
        }
        references = reader.getMetadata().getReference();
        searchDatabaseName = reader.getSearchDatabaseName();
        searchDatabaseVersion = reader.getSearchDatabaseVersion();
        searchResultIdentifier = reader.getSearchResultIdentifier();
        configuration = reader.getConfigurationOptions();

    }

    public ReportReader getReader() {
        return reader;
    }

    public void setReader(ReportReader reader) {
        this.reader = reader;
    }

    public List<CV> getCvLookup() {
        return cvLookup;
    }

    public void setCvLookup(List<CV> cvLookup) {
        this.cvLookup = cvLookup;
    }

    public String getExperimentTitle() {
        return experimentTitle;
    }

    public void setExperimentTitle(String experimentTitle) {
        this.experimentTitle = experimentTitle;
    }

    public String getExperimentShortLabel() {
        return experimentShortLabel;
    }

    public void setExperimentShortLabel(String experimentShortLabel) {
        this.experimentShortLabel = experimentShortLabel;
    }

    public Param getExperimentParams() {
        return experimentParams;
    }

    public void setExperimentParams(Param experimentParams) {
        this.experimentParams = experimentParams;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public String getSampleComment() {
        return sampleComment;
    }

    public void setSampleComment(String sampleComment) {
        this.sampleComment = sampleComment;
    }

    public Param getSampleParams() {
        return sampleParams;
    }

    public void setSampleParams(Param sampleParams) {
        this.sampleParams = sampleParams;
    }

    public SourceFile getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(SourceFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public InstrumentDescription getInstrument() {
        return instrument;
    }

    public void setInstrument(InstrumentDescription instrument) {
        this.instrument = instrument;
    }

    public Software getSoftware() {
        return software;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }

    public Param getProcessingMethod() {
        return processingMethod;
    }

    public Protocol getProtocol() {
        return this.protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public Collection<Reference> getReferences() {
        return references;
    }

    public void setReferences(List<Reference> references) {
        this.references = references;
    }

    public void setProcessingMethod(Param processsingMethod) {
        this.processingMethod = processsingMethod;
    }

    public Collection<PTM> getPTMs() {
        return PTMs;
    }

    public void setPTMs(List<PTM> PTMs) {
        this.PTMs = PTMs;
    }

    public String getSearchDatabaseName() {
        return searchDatabaseName;
    }

    public void setSearchDatabaseName(String searchDatabaseName) {
        this.searchDatabaseName = searchDatabaseName;
    }

    public String getSearchDatabaseVersion() {
        return searchDatabaseVersion;
    }

    public void setSearchDatabaseVersion(String searchDatabaseVersion) {
        this.searchDatabaseVersion = searchDatabaseVersion;
    }

    public SearchResultIdentifier getSearchResultIdentifier() {
        return searchResultIdentifier;
    }

    public void setSearchResultIdentifier(SearchResultIdentifier searchResultIdentifier) {
        this.searchResultIdentifier = searchResultIdentifier;
    }

    public Iterator<Identification> getIdentificationIterator(boolean prescanMode) {
        return reader.getIdentificationIterator();
    }

    public void setConfiguration(Properties props) {
        this.configuration = new ConfigurationOptions(props);
    }

    public Properties getConfiguration() {
        return configuration.asProperties();
    }

    @Override
    public Identification getIdentificationByUID(String uniqueID) {
        return reader.getIdentification(uniqueID);
    }

    public List<DatabaseMapping> getDatabaseMappings() {
        return databaseMappings;
    }

    public void setDatabaseMappings(List<DatabaseMapping> databaseMappings) {
        this.databaseMappings = databaseMappings;
    }

//************************************************************ Unsupported operations

    public Iterator<Spectrum> getSpectrumIterator(Set<String> peptideUID) {
        throw new UnsupportedOperationException("ReportReaderDAO does not support this operation");
    }

    public int getSpectrumReferenceForPeptideUID(String peptideUID) {
        throw new UnsupportedOperationException("ReportReaderDAO does not support this operation");
    }

    public int getSpectrumCount(boolean identOnly) {
        throw new UnsupportedOperationException("ReportReaderDAO does not support this operation");
    }

    public Iterator<Spectrum> getSpectrumIterator(boolean onlyIdentified) {
        throw new UnsupportedOperationException("ReportReaderDAO does not support this operation");
    }

    //************************************************************ non-DAO operations
    public String getReportFileAbsolutePath() {
        return reader.getReportFileAbsolutePath();
    }


    /**
     * Will return the input identification without modifying it. It is assumed that, since the underlying
     * data for this DAO is a report file, that a valid FastaHandler has been used at the initial report file
     * generation.
     *
     * @param identification - the identification object to be updated
     */
    public Identification updateFastaSequenceInformation(Identification identification) {
        return identification;
    }

    /**
     * Will return an iterator over all of the Fasta Sequences (if any). It is assumed that, since the underlying
     * data for this DAO is a report file, that a valid FastaHandler has been used at the initial report file
     * generation.
     *
     * @param onlyIdentified
     * @return
     */
    public Iterator<Sequence> getIterator(boolean onlyIdentified) {
        return reader.getSequenceIterator();
    }

    public void releaseResources() {
        reader.releaseResources();
        reader = null;
    }
}
