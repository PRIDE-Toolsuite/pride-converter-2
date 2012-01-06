package uk.ac.ebi.pride.tools.converter.report;

import junit.framework.TestCase;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReader;
import uk.ac.ebi.pride.tools.converter.report.model.*;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 04/01/11
 * Time: 16:37
 * To change this template use File | Settings | File Templates.
 */
public class ReportReaderTest extends TestCase {

    public void testReportReader() throws Exception {

        URL reportFileURL = getClass().getClassLoader().getResource("testReportFile.xml");
        assertNotNull("Could not get report file to test", reportFileURL);
        ReportReader reader = new ReportReader(new File(reportFileURL.toURI()));

        SearchResultIdentifier sri = reader.getSearchResultIdentifier();
        assertEquals("Error retrieving SearchResultIdentifier data", "2008-09-29T02:49:45", sri.getTimeCreated().toString());

        Metadata meta = reader.getMetadata();
        assertEquals("Error retrieving Metadata", "COFRADIC methionine proteome of unstimulated human blood platelets", meta.getTitle());
        assertEquals("Error retrieving Metadata-References", 1, meta.getReference().size());
        assertEquals("Error retrieving Metadata-References", 1, meta.getReference().get(0).getAdditional().getCvParam().size());
        assertEquals("Error retrieving Metadata-References", 1, meta.getReference().get(0).getAdditional().getUserParam().size());
        assertEquals("Error retrieving Metadata-MzData-CV", 4, meta.getMzDataDescription().getCvLookup().size());
        assertEquals("Error retrieving Metadata-MzData-Admin", "Unstimulated human blood platelets", meta.getMzDataDescription().getAdmin().getSampleName());
        assertEquals("Error retrieving Metadata-MzData-Admin", 1, meta.getMzDataDescription().getAdmin().getContact().size());
        assertEquals("Error retrieving Metadata-MzData-Instrument", "Micromass Q-TOF I", meta.getMzDataDescription().getInstrument().getInstrumentName());
        assertEquals("Error retrieving Metadata-MzData-DataProcessing", "3.5", meta.getMzDataDescription().getDataProcessing().getSoftware().getVersion());

        Iterator<Identification> iIter = reader.getIdentificationIterator();
        int iterCount = 0;
        Identification ident = null;
        while (iIter.hasNext()) {
            iterCount++;
            ident = iIter.next();
        }
        assertEquals("Error retrieving Identification data", 2, iterCount);
        assertEquals("Error retrieving Identification data", "Q0P9X8", ident.getCuratedAccession());

        Iterator<PTM> pIter = reader.getPTMIterator();
        int ptmCount = 0;
        PTM ptm = null;
        while (pIter.hasNext()) {
            ptmCount++;
            ptm = pIter.next();
        }
        assertEquals("Error retrieving PTM data", 2, ptmCount);
        assertEquals("Error retrieving PTM data", "MOD:00684", ptm.getModAccession());

        Iterator<Sequence> sIter = reader.getSequenceIterator();
        int seqCount = 0;
        Sequence seq = null;
        while (sIter.hasNext()) {
            seqCount++;
            seq = sIter.next();
        }
        assertEquals("Error retrieving FASTA data", 3, seqCount);
        assertEquals("Error retrieving FASTA data", 3, seq.getId());
        assertEquals("Error retrieving FASTA data", "A1VZQ4", seq.getAccession());

        assertEquals("Error retrieving FASTA db name: ", "UniProt Human", reader.getSearchDatabaseName());
        assertEquals("Error retrieving FASTA db version: ", "105", reader.getSearchDatabaseVersion());

        ConfigurationOptions options = reader.getConfigurationOptions();
        assertEquals("Error retrieving configuration options", 3, options.getOptions().size());

    }

}
