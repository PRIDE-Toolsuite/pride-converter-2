package uk.ac.ebi.pride.tools.converter.dao_spectrast_xls;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class SpectraSTDtaToXlsDaoTest extends SpectraSTAbstractXlsDaoTest {

    private static int numIdentifications = 1;
    private static int numPtms = 0;
    private static String dtaSpectrumFilePath = "src/test/resources/spectrum_dta.dta";
    private static int spectraInDtaFile = 1;
    private static String resultDtaFile = "src/test/resources/spectrum_dta.xls";
    private static String proteinUID = "sp|P13645|K1C10_HUMAN";

    public SpectraSTDtaToXlsDaoTest() {
        super(dtaSpectrumFilePath,spectraInDtaFile,resultDtaFile,proteinUID, numIdentifications, numPtms);
    }

}
