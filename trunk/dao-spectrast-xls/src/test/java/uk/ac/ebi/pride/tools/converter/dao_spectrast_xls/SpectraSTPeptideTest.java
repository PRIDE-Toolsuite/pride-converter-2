package uk.ac.ebi.pride.tools.converter.dao_spectrast_xls;

import junit.framework.TestCase;
import uk.ac.ebi.pride.tools.converter.dao_spectrast_xls.model.SpectraSTPeptide;
import uk.ac.ebi.pride.tools.converter.report.model.PeptidePTM;

import java.util.Collection;

/**
 * Params parser is not mocked up, sorry!
 * @author Jose A. Dianes
 * @version $Id$
 */
public class SpectraSTPeptideTest extends TestCase {

    SpectraSTPeptide spectraSTPeptide;

    public void setUp() throws Exception {
        spectraSTPeptide = new SpectraSTPeptide(
                "test-query",           // query name
                1,                      // rank
                "IVGC[160]SVHK",         // sequence
                2,                      // charge
                0.446,                  // dot
                0,                      // delta
                0,                      // delta rank
                0.323,                  // dot bias
                0.359,                  // precursor Mz Diff
                91,                     // num cand
                0.165,                  // mean dot
                0.0961,                 // sd dot
                0.254,                  // fval
                "Normal",               // status
                "it",                   // instrument type
                "Con",                  // spectrum type
                2,                      // num proteins
                910721403               // lib offset
        );


    }
    
    public void testGetPTMs() throws Exception {
        Collection<PeptidePTM> ptms = spectraSTPeptide.getPTMs();
        assertEquals(ptms.size(), 1);
    }


}
		