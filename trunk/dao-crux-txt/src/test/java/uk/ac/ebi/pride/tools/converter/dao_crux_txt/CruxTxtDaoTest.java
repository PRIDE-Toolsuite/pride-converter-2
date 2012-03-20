package uk.ac.ebi.pride.tools.converter.dao_crux_txt;

import junit.framework.TestCase;
import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.properties.ScoreCriteria;
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
    private static String resultDirectory = "src/test/resources/crux-output";

    private String decoyPrefix = "DECOY_";
    private String scoreCriteria = ScoreCriteria.XCORR_RANK.getName();
    private String threshold = "5";

    private static CruxTxtDao cruxTxtDao = new CruxTxtDao(new File(resultDirectory));

    public void setUp() throws Exception {
        Properties props = new Properties();
        props.setProperty(SupportedProperty.DECOY_PREFIX.getName(), decoyPrefix);
        props.setProperty(SupportedProperty.SPECTRUM_FILE.getName(), spectraFilePath);
        props.setProperty(SupportedProperty.SCORE_CRITERIA.getName(), scoreCriteria);
        props.setProperty(SupportedProperty.THRESHOLD.getName(), threshold);
//        props.setProperty(SupportedProperty.GET_HIGHEST_SCORE_ITEM.getName(), "false");

        cruxTxtDao.setConfiguration(props);
    }

    public void testIdentificationsIterator() throws Exception {
        Iterator<Identification> identificationsIt = cruxTxtDao.getIdentificationIterator(false);
        int identificationsCount = 0;
        while (identificationsIt.hasNext()) {
            Identification newIdentification = identificationsIt.next();
            identificationsCount += newIdentification.getPeptide().size();
//            System.out.println(newIdentification.getAccession());
        }
//        System.out.println("Identifications*peptide count = " + identificationsCount);
    }

    public void testIdentificationByUID() throws Exception {
        Identification identification = cruxTxtDao.getIdentificationByUID("t_sw|P35659|DEK_HUMAN(126)");
        assertNotNull(identification);

        identification = cruxTxtDao.getIdentificationByUID("d_sw|A6NIX2|WTIP_HUMAN(622)");
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
    }

}
