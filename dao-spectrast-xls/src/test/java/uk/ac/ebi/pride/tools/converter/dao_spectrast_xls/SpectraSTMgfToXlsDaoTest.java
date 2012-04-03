package uk.ac.ebi.pride.tools.converter.dao_spectrast_xls;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class SpectraSTMgfToXlsDaoTest extends SpectraSTAbstractXlsDaoTest {

    private static int numIdentifications = 614;
    private static int numPtms = 2;
    private static String mgfSpectrumFilePath = "src/test/resources/consensus_1.mgf";
    private static int spectraInMgfFile = 6062;
    private static String resultMgfFile = "src/test/resources/consensus_1.xls";
    private static String proteinUID = "sp|Q14318|FKBP8_HUMAN";

    public SpectraSTMgfToXlsDaoTest() {
        super(mgfSpectrumFilePath,spectraInMgfFile,resultMgfFile,proteinUID, numIdentifications, numPtms);
    }

}
