package uk.ac.ebi.pridemod.jaxb.xml;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pridemod.unimod.xml.UnimodReader;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 19/07/11
 * Time: 11:30
 * To change this template use File | Settings | File Templates.
 */
public class UnimodReaderTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void unimodReader() {
        URL fileName = UnimodReaderTest.class.getClassLoader().getResource("unimod.xml");
        File file = new File(fileName.getPath());
        try {
            UnimodReader unimodreader = new UnimodReader(file);
        } catch (JAXBException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }
}
