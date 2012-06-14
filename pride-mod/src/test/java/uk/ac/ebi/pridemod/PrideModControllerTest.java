package uk.ac.ebi.pridemod;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pridemod.slimmod.model.SlimModCollection;
import uk.ac.ebi.pridemod.slimmod.model.SlimModification;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 4/14/12
 * Time: 1:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class PrideModControllerTest {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void PrideControllerReader() {
        URL fileName = PrideModControllerTest.class.getClassLoader().getResource("pride_mods.xml");
        SlimModCollection slimModCollection = PrideModController.parseSlimModCollection(fileName);
        for (SlimModification mod : slimModCollection) {
            StringBuilder sb = new StringBuilder();
            sb.append(mod.getIdPsiMod()).append(",");
            sb.append(mod.getIdUnimod()).append(",");
            sb.append(mod.getShortNamePsiMod()).append(",");
            sb.append(mod.getPsiModDesc()).append(",");
            sb.append(mod.getDeltaMass()).append(",");
            System.out.println(sb.toString());
        }


    }

}
