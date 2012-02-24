package uk.ac.ebi.pride.tools.converter.dao_crux_txt;

import junit.framework.TestCase;

import java.io.File;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class CruxTxtDaoTest extends TestCase {

    private CruxTxtDao cruxTxtDao;
    private String targetFilePath = "src/test/resources/search.target-short.txt";
    private String decoyFilePath = "src/test/resources/search.decoy-short.txt";
    private String paramsFilePath = "src/test/resources/search.params-short.txt";
    
    public void setUp() throws Exception {

        File targetFile = new File(targetFilePath);
        File decoyFile = new File(decoyFilePath);
        File paramsFile = new File(paramsFilePath);
        
        cruxTxtDao = new CruxTxtDao(targetFile, decoyFile, paramsFile);

    }

    public void testConfiguration() throws Exception {

    }
}
