package uk.ac.ebi.pridemod;

import uk.ac.ebi.pridemod.pridemod.xml.PrideModReader;
import uk.ac.ebi.pridemod.slimmod.model.SlimModCollection;
import uk.ac.ebi.pridemod.slimmod.tab.ReadTabSlim;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 4/14/12
 * Time: 12:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class PrideModController {


    private static final String PRIDEMOD_TXT = ".txt";
    private static final String PRIDEMOD_XML = ".xml";

    public static SlimModCollection parseSlimModCollection(URL url) {

        if (url.getPath().endsWith(PRIDEMOD_TXT)) {
            return ReadTabSlim.parseSlimModification(url);
        } else if (url.getPath().endsWith(PRIDEMOD_XML)) {
            PrideModReader modReader = new PrideModReader(url);
            return modReader.getSlimModCollection();
        } else {
            throw new RuntimeException("No handler defined to parse URL: " + url.toString());
        }

    }

}
