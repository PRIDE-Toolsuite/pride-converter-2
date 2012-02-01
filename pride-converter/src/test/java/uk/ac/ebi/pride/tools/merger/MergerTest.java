package uk.ac.ebi.pride.tools.merger;

import junit.framework.TestCase;
import uk.ac.ebi.pride.jaxb.model.Identification;
import uk.ac.ebi.pride.jaxb.model.PeptideItem;
import uk.ac.ebi.pride.jaxb.xml.PrideXmlReader;
import uk.ac.ebi.pride.tools.merger.io.PrideXmlMerger;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 21/03/11
 * Time: 16:39
 * To change this template use File | Settings | File Templates.
 */
public class MergerTest extends TestCase {


    public void testMerge() throws Exception {

        URL ufile1 = getClass().getClassLoader().getResource("merge-file1.xml");
        File file1 = new File(ufile1.toURI());
        URL ufile2 = getClass().getClassLoader().getResource("merge-file2.xml");
        File file2 = new File(ufile2.toURI());

        ArrayList<String> toMerge = new ArrayList<String>();
        toMerge.add(file1.getAbsolutePath());
        toMerge.add(file2.getAbsolutePath());
        PrideXmlMerger m = new PrideXmlMerger(toMerge, "merge-test.xml", false, false);
        m.mergeXml();

        PrideXmlReader in = new PrideXmlReader(new File("merge-test.xml"));
        assertEquals("There should be 4 merged spectra", 4, in.getSpectrumIds().size());
        assertEquals("There should be 4 merged identifications", 4, in.getIdentIds().size());

        /*
        <GelFreeIdentification>
            <Accession>IPI00185600</Accession>
            <AccessionVersion>1</AccessionVersion>
            <Database>IPI human</Database>
            <DatabaseVersion>2.31</DatabaseVersion>
            <PeptideItem>
                <Sequence></Sequence>
                <Start>351</Start>
                <End>363</End>
                <SpectrumReference>4</SpectrumReference>
                <additional/>
            </PeptideItem>
            <PeptideItem>
                <Sequence>MSLAQR</Sequence>
                <Start>351</Start>
                <End>363</End>
                <additional/>
            </PeptideItem>
        */

        for (String id : in.getIdentIds()) {
            Identification ident = in.getIdentById(id);
            if (ident.getAccession().equals("IPI00185600")) {
                for (PeptideItem pep : ident.getPeptideItem()) {
                    if (pep.getSequence().equals("DESTNVDMSLAQR")) {
                        assertEquals("Spectrum improperly merged - badly mapped!", 4, pep.getSpectrum().getId());
                    }
                    if (pep.getSequence().equals("MSLAQR")) {
                        assertNull("Spectrum improperly merged - invalid reference not removed!", pep.getSpectrum());
                    }
                }
            }
        }


    }


}
