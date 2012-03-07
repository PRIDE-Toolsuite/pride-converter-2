package uk.ac.ebi.pride.tools.converter.dao_crux_txt;

import junit.framework.TestCase;
import uk.ac.ebi.pride.tools.converter.report.model.Identification;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class CruxTxtDaoTest extends TestCase {

    private CruxTxtDao cruxTxtDao;
    private String targetFilePath = "src/test/resources/search.target-short.txt";
    private String decoyFilePath = "src/test/resources/search.decoy-short.txt";
    private String paramsFilePath = "src/test/resources/search.params.txt";
    
    public void setUp() throws Exception {

        File targetFile = new File(targetFilePath);
        File decoyFile = new File(decoyFilePath);
        File paramsFile = new File(paramsFilePath);
        
        cruxTxtDao = new CruxTxtDao(targetFile, decoyFile, paramsFile);

    }

    public void testConfiguration() throws Exception {
        Collection<PTM> ptms = cruxTxtDao.getPTMs();
        Iterator<Identification> identificationsIt = cruxTxtDao.getIdentificationIterator(false);
        while (identificationsIt.hasNext()) {
            Identification newIdentification = identificationsIt.next();

        }
    }
}
