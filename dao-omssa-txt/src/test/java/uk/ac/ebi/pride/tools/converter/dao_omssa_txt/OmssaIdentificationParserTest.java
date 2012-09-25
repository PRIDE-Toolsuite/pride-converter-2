package uk.ac.ebi.pride.tools.converter.dao_omssa_txt;


import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.parsers.OmssaIdentificationsParser;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.parsers.OmssaIdentificationsParserResult;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * @author Jose A. Dianes
 * @version 1.0
 */
public class OmssaIdentificationParserTest {

    private String targetFilePath = "src/test/resources/output.omssa.csv";
    private Map<String, Integer> header;
    private OmssaIdentificationsParserResult parserResults;

    Map<Character, Double> fixedPtms;
    Map<Character, Double> variablePtms;

    @Before
    public void setUp() throws Exception {
        // add mods
        fixedPtms = new HashMap<Character, Double>();
        fixedPtms.put('C', 57.02);
        variablePtms = new HashMap<Character, Double>();
        variablePtms.put('M', 15.99);
    }

    @Test
    public void testParseHeader() throws Exception {
        header = OmssaIdentificationsParser.parseHeader(new File(targetFilePath));

        assertThat(header.size(), is(15));

        assertThat(header.get("fakeheadertag"), nullValue());

        assertThat(header.get("Spectrum number"), is(0));
        assertThat(header.get("Filename/id"), is(1));
        assertThat(header.get("Peptide"), is(2));
        assertThat(header.get("E-value"), is(3));
        assertThat(header.get("Mass"), is(4));
        assertThat(header.get("gi"), is(5));
        assertThat(header.get("Accession"), is(6));
        assertThat(header.get("Start"), is(7));
        assertThat(header.get("Stop"), is(8));
        assertThat(header.get("Defline"), is(9));
        assertThat(header.get("Mods"), is(10));
        assertThat(header.get("Charge"), is(11));
        assertThat(header.get("Theo Mass"), is(12));
        assertThat(header.get("P-value"), is(13));
        assertThat(header.get("NIST score"), is(14));

    }

    @Test
    public void testParse() throws Exception {
        parserResults = OmssaIdentificationsParser.parse(new File(targetFilePath), fixedPtms, variablePtms);

        assertThat(parserResults.getProteins().size(), is(692));

        assertThat(parserResults.getPeptideCount(), is(1001));

    }

}
