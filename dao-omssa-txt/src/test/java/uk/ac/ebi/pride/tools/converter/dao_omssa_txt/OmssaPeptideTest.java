package uk.ac.ebi.pride.tools.converter.dao_omssa_txt;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.model.OmssaPeptide;
import uk.ac.ebi.pride.tools.converter.report.model.PeptidePTM;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 *
 * @author Jose A. Dianes
 * @version $Id$
 */
public class OmssaPeptideTest {

    OmssaPeptide omssaPeptide;
    Map<Character, Double> fixedPtms;
    Map<Character, Double> variablePtms;

    @Before
    public void setUp() throws Exception {
        // add mods
        fixedPtms = new HashMap<Character, Double>();
        fixedPtms.put('C',57.02);
        variablePtms = new HashMap<Character, Double>();
        variablePtms.put('M',15.99);

        // create testing peptide
        omssaPeptide = new OmssaPeptide(
                264,
                "cluster_id=166108,sequence=[QADEEVPSTYR, LATVYVDVLKD, VEPISEMMLTK, VAGVVVLILALVL, LLTPYVGGDWR, GKVVTKKFSNQ, LALDLEIATYR, ILVLVVDLHTR]",
                "ICLKSmVVGWVK",
                0.951768045,
                1277.355,
                0,
                "BL_ORD_ID:27272",
                1767,
                1777,
                "tr|F5H1D6|F5H1D6_HUMAN DNA polymerase OS=Homo sapiens GN=POLE PE=3 SV=1",
                "oxidation of M:5",
                2,
                1274.74,
                5.47E-05,
                0
        );

    }

    @Test
    public void testGetPTMs() throws Exception {
        Collection<PeptidePTM> ptms = OmssaPeptide.getPTMs(omssaPeptide.getPeptide(), fixedPtms, variablePtms);
        assertThat(ptms.size(), is(2));
    }


}
		