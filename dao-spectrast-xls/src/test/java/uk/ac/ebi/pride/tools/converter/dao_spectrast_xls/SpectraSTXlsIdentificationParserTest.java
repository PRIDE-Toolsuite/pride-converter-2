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

    private String targetFilePath = "src/test/resources/consensus_1.xls";
    private Map<String, Integer> header;
    private SpectraSTIdentificationsParserResult parserResults;

    public void testParseHeader() throws Exception {
        header = SpectraSTXlsIdentificationsParser.parseHeader(new File(targetFilePath));

        assertEquals(header.size(), 18);

        assertNull(header.get("fakeheadertag"));

        assertEquals(header.get("### Query"), new Integer(0));
        assertEquals(header.get("Rk"), new Integer(1));
        assertEquals(header.get("ID"), new Integer(2));
        assertEquals(header.get("Dot"), new Integer(3));
        assertEquals(header.get("Delta"), new Integer(4));
        assertEquals(header.get("DelRk"), new Integer(5));
        assertEquals(header.get("DBias"), new Integer(6));
        assertEquals(header.get("MzDiff"), new Integer(7));
        assertEquals(header.get("#Cand"), new Integer(8));
        assertEquals(header.get("MeanDot"), new Integer(9));
        assertEquals(header.get("SDDot"), new Integer(10));
        assertEquals(header.get("Fval"), new Integer(11));
        assertEquals(header.get("Status"), new Integer(12));
        assertEquals(header.get("Inst"), new Integer(13));
        assertEquals(header.get("Spec"), new Integer(14));
        assertEquals(header.get("#Pr"), new Integer(15));
        assertEquals(header.get("Proteins"), new Integer(16));
        assertEquals(header.get("LibFileOffset"), new Integer(17));
    }

    public void testParse() throws Exception {
        parserResults = SpectraSTXlsIdentificationsParser.parse(new File(targetFilePath));

        assertEquals(parserResults.proteins.size(), 79);

        assertEquals(parserResults.peptideCount, 614);

    }

}
