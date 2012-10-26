package uk.ac.ebi.pride.tools.converter.dao_omssa_txt;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.properties.ScoreCriteria;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.properties.SupportedProperty;
import uk.ac.ebi.pride.tools.converter.report.model.Identification;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;

import java.io.File;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class OmssaDaoTest {

    private static final String RESULT_FILE = "2780_results.csv";
    private static final String SPECTRA_FILE = "2780_spectrum.mgf";
    private static final String MODS_FILE = "mods.xml";
    private static final String SCORE_CRITERIA = ScoreCriteria.E_VALUE.getName();
    private static final String THRESHOLD = "0.0";
    private static final int NUM_IDENTIFICATIONS = 790;
    private static final int NUM_PEPTIDES = 4726;
    private static final int NUM_PTMS = 5;

    private static final int NUM_SPECTRA = 4706;

    private static OmssaTxtDao omssaTxtDao;
    private Map<Character, Double> fixedPtms;

    @Before
    public void setUp() throws Exception {

        omssaTxtDao = new OmssaTxtDao(new File(getClass().getClassLoader().getResource(RESULT_FILE).toURI()));

        // add PTMs
        fixedPtms = new HashMap<Character, Double>();
        fixedPtms.put('C', 57.02);

        Properties props = new Properties();
        props.setProperty(SupportedProperty.SCORE_CRITERIA.getName(), SCORE_CRITERIA);
        props.setProperty(SupportedProperty.THRESHOLD.getName(), THRESHOLD);
        props.setProperty(SupportedProperty.MOD_FILE.getName(), new File(getClass().getClassLoader().getResource(MODS_FILE).toURI()).getAbsolutePath());

        omssaTxtDao.setConfiguration(props);

        omssaTxtDao.setExternalSpectrumFile(new File(getClass().getClassLoader().getResource(SPECTRA_FILE).toURI()).getAbsolutePath());

    }

    @Test
    public void testIdentificationsIterator() throws Exception {
        Iterator<Identification> identificationsIt = omssaTxtDao.getIdentificationIterator(false);
        int identificationsCount = 0;
        int peptideCount = 0;
        while (identificationsIt.hasNext()) {
            Identification newIdentification = identificationsIt.next();
            identificationsCount++;
            peptideCount += newIdentification.getPeptide().size();
        }
        assertThat(identificationsCount, is(NUM_IDENTIFICATIONS));
        assertThat(peptideCount, is(NUM_PEPTIDES));
    }

    @Test
    public void testPTMs() throws Exception {
        Collection<PTM> ptms = omssaTxtDao.getPTMs();

        assertThat(ptms.size(), is(NUM_PTMS));
    }

    @Test
    public void testSpectraIterator() throws Exception {
        int spectraCountAll = omssaTxtDao.getSpectrumCount(false);
        Iterator<Spectrum> specAllIt = omssaTxtDao.getSpectrumIterator(false);
        int count = 0;
        while (specAllIt.hasNext()) {
            Spectrum newSpectrum = specAllIt.next();
            count++;
        }

        assertEquals(spectraCountAll, count);
        assertEquals(spectraCountAll, NUM_SPECTRA);

    }

    @Test
    public void testSpectraIteratorIdentified() throws Exception {
        int spectraCountOnlyIdentified = omssaTxtDao.getSpectrumCount(true);
        Iterator<Spectrum> specIdIt = omssaTxtDao.getSpectrumIterator(true);
        int countId = 0;
        while (specIdIt.hasNext()) {
            Spectrum newSpectrum = specIdIt.next();
            countId++;
//            System.out.println("Obtained spectrum: " + newSpectrum.getId());
        }

        assertEquals(spectraCountOnlyIdentified, countId);

    }

}
