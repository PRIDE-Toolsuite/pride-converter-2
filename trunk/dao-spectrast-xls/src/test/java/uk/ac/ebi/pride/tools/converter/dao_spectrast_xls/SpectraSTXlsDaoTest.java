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
public class SpectraSTXlsDaoTest extends TestCase {

    private static final int NUM_IDENTIFICATIONS = 614;

    private static String spectraFilePath = "src/test/resources/consensus_1.mgf";
    private static int spectraInFile = 6062;
    private static String resultFile = "src/test/resources/consensus_1.xls";

    private static String scoreCriteria = ScoreCriteria.XCORR_RANK.getName();
    private static String threshold = "5";


    private static SpectraSTXlsDao cruxXlsDao = new SpectraSTXlsDao(new File(resultFile));

    public void setUp() throws Exception {
        Properties props = new Properties();
        props.setProperty(SupportedProperty.SCORE_CRITERIA.getName(), scoreCriteria);
        props.setProperty(SupportedProperty.THRESHOLD.getName(), threshold);
//        props.setProperty(SupportedProperty.GET_HIGHEST_SCORE_ITEM.getName(), "false");

        cruxXlsDao.setConfiguration(props);
        cruxXlsDao.setExternalSpectrumFile(spectraFilePath);

    }

    public void testIdentificationsIterator() throws Exception {
        Iterator<Identification> identificationsIt = cruxXlsDao.getIdentificationIterator(false);
        int identificationsCount = 0;
        while (identificationsIt.hasNext()) {
            Identification newIdentification = identificationsIt.next();
            identificationsCount += newIdentification.getPeptide().size();
//            System.out.println(newIdentification.getAccession());
        }
//        System.out.println("Identifications*peptide count = " + identificationsCount);
        assertEquals(identificationsCount, NUM_IDENTIFICATIONS);
    }

    public void testIdentificationByUID() throws Exception {

        Identification identification = cruxXlsDao.getIdentificationByUID("sp|Q14318|FKBP8_HUMAN");
        assertNotNull(identification);

        identification = cruxXlsDao.getIdentificationByUID("sp|O60610|DIAP1_HUMAN");
        assertNotNull(identification);

        identification = cruxXlsDao.getIdentificationByUID("sp|P68402|PA1B2_HUMAN");
        assertNotNull(identification);

    }


    public void testPTMs() throws Exception {
        Collection<PTM> ptms = cruxXlsDao.getPTMs();

        assertEquals(2, ptms.size());
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

        assertEquals(spectraCountAll,spectraInFile);

    }
    
    public void testSpectraIteratorIdentified() throws Exception {
        int spectraCountOnlyIdentified = cruxXlsDao.getSpectrumCount(true);
        Iterator<Spectrum> specIdIt = cruxXlsDao.getSpectrumIterator(true);
        int countId = 0;
        while (specIdIt.hasNext()) {
            Spectrum newSpectrum = specIdIt.next();
            countId++;
        }

        assertEquals(spectraCountOnlyIdentified,countId);

    }

    public void testOtherAPIMethods() {
        Param param = cruxXlsDao.getProcessingMethod();
        assertNotNull(param);
    }

}
