package uk.ac.ebi.pride.tools.converter.dao_crux_txt;

import junit.framework.TestCase;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.parsers.CruxTxtIdentificationsParser;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.results.CruxParserResults;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;

import java.io.File;
import java.util.Map;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class CruxTxtIdentificationParserTest extends TestCase {

    private String targetFilePath = "src/test/resources/search.target-short.txt";
    private String decoyFilePath = "src/test/resources/search.decoy-short.txt";
    private String wrongFilePath = "src/test/resources/search.target-short-wrong.txt";
    private Map<String, Integer> header;
    private CruxParserResults parserResults;

    public void testParseHeader() throws Exception {
        header = CruxTxtIdentificationsParser.parseHeader(new File(targetFilePath));

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
        parserResults = CruxTxtIdentificationsParser.parse(new File(targetFilePath));

        assertEquals(parserResults.proteins.size(), 39);

        assertEquals(parserResults.peptideCount, 39);

    }

    public void testParseDecoy() throws Exception {
        parserResults = CruxTxtIdentificationsParser.parse(new File(decoyFilePath));

        assertEquals(parserResults.proteins.size(), 19);

        assertEquals(parserResults.peptideCount, 19);

    }

    public void testParseWrong() throws Exception {
         try {
             parserResults = CruxTxtIdentificationsParser.parse(new File(wrongFilePath));
             throw new Exception(); // if test OK, we should never get this point
         } catch (Exception e) {
             assertTrue(e.getClass() == ConverterException.class);
         }
    }

}
