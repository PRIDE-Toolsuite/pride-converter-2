package uk.ac.ebi.pride.tools.converter.dao_crux_txt;


import junit.framework.TestCase;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.parsers.CruxParametersParserResult;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.parsers.CruxTxtParamsParser;

import java.io.File;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class CruxTxtParamsParserTest extends TestCase {

    private String paramsFilePath = "src/test/resources/search.params.txt";
    CruxParametersParserResult parserResult;
    
    public void setUp() throws Exception {

    }

    public void testParse() throws Exception {
        parserResult = CruxTxtParamsParser.parse(new File(paramsFilePath));
//        assertEquals(parserResult.properties.size(), 83);
        assertEquals(parserResult.properties.getProperty("comparison").compareTo("eq"), 0);
        assertFalse(parserResult.properties.getProperty("comparison").compareTo("wrong-value") == 0);
    }

    public void testCheckVariableIsFixed() throws Exception {

    }

    public void testCheckRepeatedAAInMod() throws Exception {

    }
}
