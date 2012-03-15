package uk.ac.ebi.pride.tools.converter.dao_crux_txt;

import junit.framework.TestCase;
import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.properties.SupportedProperty;
import uk.ac.ebi.pride.tools.converter.report.model.Identification;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class CruxTxtDaoTest extends TestCase {


    private String spectraFilePath = "src/test/resources/spectra.ms2";
    private static String resultDirectory = "src/test/resources";

    private String decoyPrefix = "DECOY_";

    private static CruxTxtDao cruxTxtDao = new CruxTxtDao(new File(resultDirectory));

    public void setUp() throws Exception {
        Properties props = new Properties();
        props.setProperty(SupportedProperty.DECOY_PREFIX.getName(), decoyPrefix);
        props.setProperty(SupportedProperty.SPECTRUM_FILE.getName(), spectraFilePath);
        cruxTxtDao.setConfiguration(props);
    }

    public void testIdentificationsIterator() throws Exception {
        Iterator<Identification> identificationsIt = cruxTxtDao.getIdentificationIterator(false);
        while (identificationsIt.hasNext()) {
            Identification newIdentification = identificationsIt.next();
//            System.out.println(newIdentification.getAccession());
        }
    }

    public void testIdentificationByUID() throws Exception {
        Identification identification = cruxTxtDao.getIdentificationByUID("t_tr|DECOY_A8MVI8|A8MVI8_HUMAN(58)");
        assertNotNull(identification);

        identification = cruxTxtDao.getIdentificationByUID("d_sp|Q04671-2|P_HUMAN(247)");
        assertNotNull(identification);
    }

    public void testPTMs() throws Exception {
        Collection<PTM> ptms = cruxTxtDao.getPTMs();

        assertNotNull(ptms);
    }
    
    public void testSpectraIterator() throws Exception {
        int spectraCountAll = cruxTxtDao.getSpectrumCount(false);
        int spectraCountOnlyIdentified = cruxTxtDao.getSpectrumCount(true);
        Iterator<Spectrum> specAllIt = cruxTxtDao.getSpectrumIterator(false);
        int count = 0;
        while (specAllIt.hasNext()) {
            Spectrum newSpectrum = specAllIt.next();
            count++;
//            System.out.println("Obtained spectrum: " + newSpectrum.getId());
        }

        assertEquals(count,spectraCountAll);

    }
}
