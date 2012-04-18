package uk.ac.ebi.pridemod;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pridemod.slimmod.model.SlimModCollection;

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


    }

}
