package uk.ac.ebi.pride.tools.converter.dao_omssa_txt;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.properties.ScoreCriteria;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.properties.SupportedProperty;
import uk.ac.ebi.pride.tools.converter.report.model.Identification;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;
import uk.ac.ebi.pride.tools.converter.report.model.Param;

import java.io.File;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class OmssaDaoTest {

    private static final String RESULT_FILE_PATH = "src/test/resources/output.omssa.csv";
    private static final String SCORE_CRITERIA = ScoreCriteria.E_VALUE.getName();
    private static final String THRESHOLD = "0.0";
    private static final int NUM_IDENTIFICATIONS = 1001;
    private static final int NUM_PTMS = 2;
    private static final int NUM_SPECTRA = 1957;
    private static final String PROTEIN_UID = "sp|P51649|SSDH_HUMAN Succinate-semialdehyde dehydrogenase, mitochondrial OS=Homo sapiens GN=ALDH5A1 PE=1 SV=2";

    private static OmssaTxtDao cruxXlsDao;

    Map<Character, Double> fixedPtms;
    Map<Character, Double> variablePtms;
    private static final String SPECTRA_FILE_PATH = "src/test/resources/spectra.pkl";

    @Before
    public void setUp() throws Exception {
        cruxXlsDao = new OmssaTxtDao(new File(this.RESULT_FILE_PATH));

        // add PTMs
        fixedPtms = new HashMap<Character, Double>();
        fixedPtms.put('C',57.02);
        variablePtms = new HashMap<Character, Double>();
        variablePtms.put('M',15.99);

        // TODO: set right properties here
        Properties props = new Properties();
        props.setProperty(SupportedProperty.SCORE_CRITERIA.getName(), SCORE_CRITERIA);
        props.setProperty(SupportedProperty.THRESHOLD.getName(), THRESHOLD);

        cruxXlsDao.setConfiguration(props);

        cruxXlsDao.setExternalSpectrumFile(SPECTRA_FILE_PATH);

    }

    @Test
    public void testIdentificationsIterator() throws Exception {
        Iterator<Identification> identificationsIt = cruxXlsDao.getIdentificationIterator(false);
        int identificationsCount = 0;
        while (identificationsIt.hasNext()) {
            Identification newIdentification = identificationsIt.next();
            identificationsCount += newIdentification.getPeptide().size();
        }
        assertThat(identificationsCount, is(NUM_IDENTIFICATIONS));
    }

    @Test
    public void testIdentificationsIteratorPrescan() throws Exception {
        Iterator<Identification> identificationsIt = cruxXlsDao.getIdentificationIterator(true);
        int identificationsCount = 0;
        while (identificationsIt.hasNext()) {
            Identification newIdentification = identificationsIt.next();
            identificationsCount += newIdentification.getPeptide().size();
        }
        assertThat(identificationsCount, is(NUM_IDENTIFICATIONS));
    }

    @Test
    public void testIdentificationByUID() throws Exception {
        Identification identification = cruxXlsDao.getIdentificationByUID(PROTEIN_UID);
        assertNotNull(identification);
    }

    @Test
    public void testPTMs() throws Exception {
        Collection<PTM> ptms = cruxXlsDao.getPTMs();

        assertThat(ptms.size(), is(NUM_PTMS));
    }

    @Test
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

        assertEquals(spectraCountAll, NUM_SPECTRA);

    }

    @Test
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

    @Test
    public void testOtherAPIMethods() {
        Param param = cruxXlsDao.getProcessingMethod();
        assertNotNull(param);
    }

}
