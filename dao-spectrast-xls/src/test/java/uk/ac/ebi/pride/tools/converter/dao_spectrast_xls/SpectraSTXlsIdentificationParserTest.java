package uk.ac.ebi.pride.tools.converter.dao_spectrast_xls;

import junit.framework.TestCase;
import uk.ac.ebi.pride.tools.converter.dao_spectrast_xls.parsers.SpectraSTIdentificationsParserResult;
import uk.ac.ebi.pride.tools.converter.dao_spectrast_xls.parsers.SpectraSTXlsIdentificationsParser;

import java.io.File;
import java.util.Map;

/**
 * @author Jose A. Dianes
 * @version 1.0
 */
public class SpectraSTXlsIdentificationParserTest extends TestCase {

    private String targetFilePath = "src/test/resources/crux-output/search.target.txt";
    private String decoyFilePath = "src/test/resources/crux-output/search.decoy.txt";
    private Map<String, Integer> header;
    private SpectraSTIdentificationsParserResult parserResults;

    public void testParseHeader() throws Exception {
        header = SpectraSTXlsIdentificationsParser.parseHeader(new File(targetFilePath));

        assertEquals(header.size(), 13);

        assertNull(header.get("fakeheadertag"));

        assertEquals(header.get("scan"), new Integer(0));
        assertEquals(header.get("charge"), new Integer(1));
        assertEquals(header.get("spectrum precursor m/z"), new Integer(2));
        assertEquals(header.get("spectrum neutral mass"), new Integer(3));
        assertEquals(header.get("peptide mass"), new Integer(4));
        assertEquals(header.get("delta_cn"), new Integer(5));
        assertEquals(header.get("xcorr score"), new Integer(6));
        assertEquals(header.get("xcorr rank"), new Integer(7));
        assertEquals(header.get("matches/spectrum"), new Integer(8));
        assertEquals(header.get("sequence"), new Integer(9));
        assertEquals(header.get("cleavage type"), new Integer(10));
        assertEquals(header.get("protein id"), new Integer(11));
        assertEquals(header.get("flanking aa"), new Integer(12));

    }

    public void testParse() throws Exception {
        parserResults = SpectraSTXlsIdentificationsParser.parse(new File(targetFilePath));

        assertEquals(parserResults.proteins.size(), 14880);

        assertEquals(parserResults.peptideCount, 25010);

    }

    public void testParseDecoy() throws Exception {
        parserResults = SpectraSTXlsIdentificationsParser.parse(new File(decoyFilePath));

        assertEquals(parserResults.proteins.size(), 19980);

        assertEquals(parserResults.peptideCount, 25005);

    }

}
