package uk.ac.ebi.pride.tools.converter.dao_crux_txt;


import junit.framework.TestCase;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.parsers.CruxTxtParamsParser;

import java.io.File;
import java.util.Properties;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class CruxTxtParamsParserTest extends TestCase {

    private String paramsFilePath = "src/test/resources/search.params-short.txt";
    Properties properties;
    
    public void setUp() throws Exception {

    }

    public void testParse() throws Exception {
        properties = CruxTxtParamsParser.parse(new File(paramsFilePath));
        
        assertEquals(properties.size(), 5);

        assertEquals(properties.getProperty("comparison").compareTo("eq"), 0);

        assertFalse(properties.getProperty("comparison").compareTo("wrong-value") == 0);

    }
}
