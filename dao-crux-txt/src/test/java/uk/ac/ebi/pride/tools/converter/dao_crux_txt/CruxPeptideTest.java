package uk.ac.ebi.pride.tools.converter.dao_crux_txt;

import junit.framework.TestCase;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.model.CruxPeptide;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.parsers.CruxParametersParserResult;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.parsers.CruxTxtParamsParser;
import uk.ac.ebi.pride.tools.converter.report.model.PeptidePTM;

import java.io.File;
import java.util.Collection;

/**
 * Params parser is not mocked up, sorry!
 * @author Jose A. Dianes
 * @version $Id$
 */
public class CruxPeptideTest extends TestCase {

    CruxPeptide cruxPeptide;
    CruxPeptide cruxPeptideWrong;
    CruxParametersParserResult params;
    private String paramsFilePath = "src/test/resources/crux-output/search.params.txt";

    public void setUp() throws Exception {
        cruxPeptide = new CruxPeptide(
                1,                      // scan
                2,                      // charge
                679.4421,               // spectrum precursor m/z
                1356.8768,              // spectrum neutral mass
                1356.5005,              // peptide mass
                0.50980210,             // delta_cn
                1.71443594,             // xcorr score
                1,                      // xcorr rank
                5022,                   // matches/spectrum
                "N[12.123456]M[15.994915]GQCFSGFPFEK[-21.654321]",         // sequence
                "trypsin-full-digest",   // claveage type
                "sp|P35659|DEK_HUMAN(126),tr|B4DFG0|B4DFG0_HUMAN(98),tr|B4DN37|B4DN37_HUMAN(92),tr|D6R9L5|D6R9L5_HUMAN(131),tr|D6RDA2|D6RDA2_HUMAN(59)".split(","),
                "AB,CD,FG,HI,JK".split(",")
        );

        params = CruxTxtParamsParser.parse(new File(paramsFilePath));
    }
    
    public void testGetPTMs() throws Exception {
        Collection<PeptidePTM> ptms = cruxPeptide.getPTMs(params);
        assertEquals(ptms.size(), 4);
    }

    public void testGetAA() throws Exception {
        assertTrue(cruxPeptide.getPrevAA("sp|P35659|DEK_HUMAN(126)").equals("A"));
        assertTrue(cruxPeptide.getNextAA("sp|P35659|DEK_HUMAN(126)").equals("B"));
    }

}
		