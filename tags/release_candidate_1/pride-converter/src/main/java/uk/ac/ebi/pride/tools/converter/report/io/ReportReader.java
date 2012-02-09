package uk.ac.ebi.pride.tools.converter.report.io;

import org.apache.log4j.Logger;
import psidev.psi.tools.xxindex.SimpleXmlElementExtractor;
import psidev.psi.tools.xxindex.StandardXpathAccess;
import psidev.psi.tools.xxindex.XmlElementExtractor;
import psidev.psi.tools.xxindex.index.IndexElement;
import psidev.psi.tools.xxindex.index.XpathIndex;
import uk.ac.ebi.pride.tools.converter.report.io.xml.unmarshaller.ReportUnmarshaller;
import uk.ac.ebi.pride.tools.converter.report.io.xml.unmarshaller.ReportUnmarshallerFactory;
import uk.ac.ebi.pride.tools.converter.report.io.xxindex.ReportXpath;
import uk.ac.ebi.pride.tools.converter.report.model.*;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 30-Apr-2010
 * Time: 12:13:31
 */
public class ReportReader {

    private static final Logger logger = Logger.getLogger(ReportReader.class);

    //xxindex
    private File reportFile = null;
    private ReportUnmarshaller unmarshaller = null;
    private StandardXpathAccess xpathAccess = null;
    private XmlElementExtractor xmlExtractor = null;
    private XpathIndex index = null;

    //fasta attributes
    private static final Pattern FASTA_DB_NAME_PATTERN = Pattern.compile("sourceDb\\s*=\\s*[\"']([^\"'>]*)?[\"']", Pattern.CASE_INSENSITIVE);
    private static final Pattern FASTA_DB_VERSION_PATTERN = Pattern.compile("sourceDbVersion\\s*=\\s*[\"']([^\"'>]*)?[\"']", Pattern.CASE_INSENSITIVE);
    private String fastaAttributeStr = null;

    //identification cache for random access
    private Map<String, IndexElement> identificationCache = null;

    //sequence cache for random access
    private Map<Long, IndexElement> sequenceCache = null;

    public ReportReader(File reportFile) {

        if (!reportFile.exists()) {
            throw new ConverterException("Xml file to be indexed does not exist: " + reportFile.getAbsolutePath());
        }

        try {
            // create unmarshaller
            this.unmarshaller = ReportUnmarshallerFactory.getInstance().initializeUnmarshaller();

            logger.info("Creating index: ");
            xpathAccess = new StandardXpathAccess(reportFile, ReportXpath.getXpaths());
            logger.debug("done!");

            // create xml element extractor
            xmlExtractor = new SimpleXmlElementExtractor();
            xmlExtractor.setEncoding(xmlExtractor.detectFileEncoding(reportFile.toURI().toURL()));

            // create index
            index = xpathAccess.getIndex();

            //keep file pointer for later use
            this.reportFile = reportFile;

        } catch (IOException e) {
            logger.error(e);
            throw new ConverterException("Could not initialize report reader: " + e.getMessage(), e);
        }

    }

    public String getReportFileAbsolutePath() {
        return reportFile.getAbsolutePath();
    }

    private String getXMLSnippet(String xpath) {
        Iterator<String> iter = xpathAccess.getXmlSnippetIterator(xpath);
        if (iter.hasNext()) {
            return iter.next();
        } else {
            return null;
        }
    }

    public SearchResultIdentifier getSearchResultIdentifier() {
        String xml = getXMLSnippet(ReportXpath.SEARCH_RESULT_IDENTIFIER_ELEMENT.getXpath());
        return unmarshaller.unmarshal(xml, ReportXpath.SEARCH_RESULT_IDENTIFIER_ELEMENT.getClassType());
    }

    public Metadata getMetadata() {
        String xml = getXMLSnippet(ReportXpath.METADATA_ELEMENT.getXpath());
        return unmarshaller.unmarshal(xml, ReportXpath.METADATA_ELEMENT.getClassType());
    }

    public ConfigurationOptions getConfigurationOptions() {
        String xml = getXMLSnippet(ReportXpath.CONFIGURATION_OPTIONS_ELEMENT.getXpath());
        return unmarshaller.unmarshal(xml, ReportXpath.CONFIGURATION_OPTIONS_ELEMENT.getClassType());
    }

    public Iterator<Identification> getIdentificationIterator() {

        return new ReportIterator<Identification>(
                xpathAccess.getXmlSnippetIterator(ReportXpath.IDENTIFICATION_ELEMENT.getXpath()),
                ReportXpath.IDENTIFICATION_ELEMENT.getClassType());

    }

    public Identification getIdentification(String uniqueIdentifier) {

        initIdentificationCache();
        IndexElement ndx = identificationCache.get(uniqueIdentifier);
        if (ndx != null) {
            return getIdentification(ndx);
        } else {
            throw new ConverterException("Could not find identification with unique identifier: " + uniqueIdentifier);
        }

    }

    private void initIdentificationCache() {

        if (identificationCache == null) {

            //init cache
            identificationCache = new HashMap<String, IndexElement>();

            //iterate over all index elements
            List<IndexElement> ids = index.getElements(ReportXpath.IDENTIFICATION_ELEMENT.getXpath());
            for (Iterator<IndexElement> i = ids.iterator(); i.hasNext(); ) {
                IndexElement ndx = i.next();
                Identification id = getIdentification(ndx);
                //store indexelement and UID for rapid retrieval later
                IndexElement old = identificationCache.put(id.getUniqueIdentifier(), ndx);
                if (old != null) {
                    throw new ConverterException("Identification Unique Identifier not unique for id: " + id.getUniqueIdentifier());
                }
            }

        }

    }

    public Sequence getSequenceById(long seqId) {
        initFastaSequenceCache();
        IndexElement ndx = sequenceCache.get(seqId);
        if (ndx != null) {
            return getSequence(ndx);
        } else {
            throw new ConverterException("Could not find sequence with unique identifier: " + seqId);
        }

    }

    private void initFastaSequenceCache() {

        if (sequenceCache == null) {

            //init cache
            sequenceCache = new HashMap<Long, IndexElement>();

            //iterate over all index elements
            List<IndexElement> ids = index.getElements(ReportXpath.SEQUENCE_ELEMENT.getXpath());
            for (Iterator<IndexElement> i = ids.iterator(); i.hasNext(); ) {
                IndexElement ndx = i.next();
                Sequence seq = getSequence(ndx);
                //store indexelement and UID for rapid retrieval later
                IndexElement old = sequenceCache.put(seq.getId(), ndx);
                if (old != null) {
                    throw new ConverterException("Sequence Unique Identifier not unique for id: " + seq.getId());
                }
            }

        }

    }

    private Identification getIdentification(IndexElement ndx) {

        try {
            String xml = xmlExtractor.readString(ndx.getStart(), ndx.getStop(), reportFile);
            return unmarshaller.unmarshal(xml, Identification.class);
        } catch (IOException e) {
            throw new ConverterException("Error reading identification from report file: " + e.getMessage(), e);
        }

    }

    private Sequence getSequence(IndexElement ndx) {

        try {
            String xml = xmlExtractor.readString(ndx.getStart(), ndx.getStop(), reportFile);
            return unmarshaller.unmarshal(xml, Sequence.class);
        } catch (IOException e) {
            throw new ConverterException("Error reading sequence from report file: " + e.getMessage(), e);
        }

    }

    public Iterator<PTM> getPTMIterator() {

        return new ReportIterator<PTM>(
                xpathAccess.getXmlSnippetIterator(ReportXpath.PTM_ELEMENT.getXpath()),
                ReportXpath.PTM_ELEMENT.getClassType());

    }

    public Iterator<DatabaseMapping> getDatabaseMappingIterator() {

        return new ReportIterator<DatabaseMapping>(
                xpathAccess.getXmlSnippetIterator(ReportXpath.DATABASE_MAPPING_ELEMENT.getXpath()),
                ReportXpath.DATABASE_MAPPING_ELEMENT.getClassType());

    }

    public Iterator<Sequence> getSequenceIterator() {

        return new ReportIterator<Sequence>(
                xpathAccess.getXmlSnippetIterator(ReportXpath.SEQUENCE_ELEMENT.getXpath()),
                ReportXpath.SEQUENCE_ELEMENT.getClassType());

    }

    public String getSearchDatabaseName() {

        initFastaAttributeStr();

        Matcher match = FASTA_DB_NAME_PATTERN.matcher(fastaAttributeStr);
        if (match.find()) {
            return match.group(1);
        } else {
            return null;
        }
    }

    public String getSearchDatabaseVersion() {

        initFastaAttributeStr();

        Matcher match = FASTA_DB_VERSION_PATTERN.matcher(fastaAttributeStr);
        if (match.find()) {
            return match.group(1);
        } else {
            return null;
        }
    }

    private void initFastaAttributeStr() {

        try {

            if (fastaAttributeStr == null) {

                List<IndexElement> ie = index.getElements(ReportXpath.FASTA_ELEMENT.getXpath());
                if (!ie.isEmpty()) {
                    fastaAttributeStr = xpathAccess.getStartTag(ie.get(0));
                } else {
                    fastaAttributeStr = "";
                }

            }

        } catch (IOException e) {
            throw new ConverterException("Error parsing FASTA attributes: " + e.getMessage(), e);
        }

    }

    private class ReportIterator<T extends ReportObject> implements Iterator<T> {

        Iterator<String> xmlStrIterator;
        Class cls;

        private ReportIterator(Iterator<String> xmlStrIterator, Class cls) {
            this.xmlStrIterator = xmlStrIterator;
            this.cls = cls;
        }

        public boolean hasNext() {
            return xmlStrIterator.hasNext();
        }

        @SuppressWarnings("unchecked")
        public T next() {
            String xml = xmlStrIterator.next();
            return (T) unmarshaller.unmarshal(xml, cls);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public void releaseResources() {
        xmlExtractor = null;
        xpathAccess = null;
        index = null;
        reportFile = null;
    }

}
