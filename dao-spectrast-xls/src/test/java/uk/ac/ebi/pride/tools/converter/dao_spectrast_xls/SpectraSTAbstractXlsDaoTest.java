package uk.ac.ebi.pride.tools.converter.dao_spectrast_xls;

import junit.framework.TestCase;
import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.dao_spectrast_xls.properties.ScoreCriteria;
import uk.ac.ebi.pride.tools.converter.dao_spectrast_xls.properties.SupportedProperty;
import uk.ac.ebi.pride.tools.converter.report.model.Identification;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;
import uk.ac.ebi.pride.tools.converter.report.model.Param;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public abstract class SpectraSTAbstractXlsDaoTest extends TestCase {

    private final int numIdentifications;
    private final int numPtms;

    private final String spectrumFilePath;
    private final int spectraInFile;
    private final String resultFilePath;
    private final String proteinUID;
    
    private static String scoreCriteria = ScoreCriteria.FVAL.getName();
    private static String threshold = "0.0";


    private static SpectraSTXlsDao cruxXlsDao;

    public SpectraSTAbstractXlsDaoTest(String spectrumFilePath, int spectraInFile, String resultFilePath,
                                       String proteinUID, int numIdentifications, int numPtms) {
        this.spectrumFilePath = spectrumFilePath;
        this.spectraInFile = spectraInFile;
        this.resultFilePath = resultFilePath;
        this.proteinUID = proteinUID;
        this.numIdentifications = numIdentifications;
        this.numPtms = numPtms;
        cruxXlsDao = new SpectraSTXlsDao(new File(this.resultFilePath));
    }
    public void setUp() throws Exception {
        Properties props = new Properties();
        props.setProperty(SupportedProperty.SCORE_CRITERIA.getName(), scoreCriteria);
        props.setProperty(SupportedProperty.THRESHOLD.getName(), threshold);

        cruxXlsDao.setConfiguration(props);
        cruxXlsDao.setExternalSpectrumFile(spectrumFilePath);

    }

    public void testIdentificationsIterator() throws Exception {
        Iterator<Identification> identificationsIt = cruxXlsDao.getIdentificationIterator(false);
        int identificationsCount = 0;
        while (identificationsIt.hasNext()) {
            Identification newIdentification = identificationsIt.next();
            identificationsCount += newIdentification.getPeptide().size();
        }
        assertEquals(identificationsCount, numIdentifications);
    }

    public void testIdentificationsIteratorPrescan() throws Exception {
        Iterator<Identification> identificationsIt = cruxXlsDao.getIdentificationIterator(true);
        int identificationsCount = 0;
        while (identificationsIt.hasNext()) {
            Identification newIdentification = identificationsIt.next();
            identificationsCount += newIdentification.getPeptide().size();
        }
        assertEquals(identificationsCount, numIdentifications);
    }

    public void testIdentificationByUID() throws Exception {
        Identification identification = cruxXlsDao.getIdentificationByUID(proteinUID);
        assertNotNull(identification);
    }


    public void testPTMs() throws Exception {
        Collection<PTM> ptms = cruxXlsDao.getPTMs();

        assertEquals(numPtms, ptms.size());
    }

    public void testSpectraIterator() throws Exception {
        int spectraCountAll = cruxXlsDao.getSpectrumCount(false);        
        Iterator<Spectrum> specAllIt = cruxXlsDao.getSpectrumIterator(false);
        int count = 0;
        while (specAllIt.hasNext()) {
            Spectrum newSpectrum = specAllIt.next();
            count++;
//            System.out.println("Obtained spectrum: " + newSpectrum.getId());
        }

        assertEquals(spectraCountAll,count);

        assertEquals(spectraCountAll, spectraInFile);

    }
    
    public void testSpectraIteratorIdentified() throws Exception {
        int spectraCountOnlyIdentified = cruxXlsDao.getSpectrumCount(true);
        Iterator<Spectrum> specIdIt = cruxXlsDao.getSpectrumIterator(true);
        int countId = 0;
        while (specIdIt.hasNext()) {
            Spectrum newSpectrum = specIdIt.next();
            countId++;
//            System.out.println("Obtained spectrum: " + newSpectrum.getId());
        }

        assertEquals(spectraCountOnlyIdentified,countId);

    }

    public void testOtherAPIMethods() {
        Param param = cruxXlsDao.getProcessingMethod();
        assertNotNull(param);
    }

}
