package uk.ac.ebi.pride.tools.converter.dao_spectrast_xls;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class SpectraSTMzXmlToXlsDaoTest extends SpectraSTAbstractXlsDaoTest {

    private static int numIdentifications = 35;
    private static int numPtms = 4;
    private static String mzXmlSpectrumFilePath = "src/test/resources/spectrum_mzxml.mzXML";
    private static int spectraInMzXmlFile = 51;
    private static String resultMzXmlFile = "src/test/resources/spectrum_mzxml.xls";
    private static String proteinUID = "sp|Q5T5Y3|CAMP1_HUMAN";

    public SpectraSTMzXmlToXlsDaoTest() {
        super(mzXmlSpectrumFilePath,spectraInMzXmlFile,resultMzXmlFile,proteinUID, numIdentifications, numPtms);
    }

}
