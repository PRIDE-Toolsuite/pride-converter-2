/**
 *
 */
package uk.ac.ebi.pride.tools.converter.dao.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.report.model.Identification;

import java.io.File;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author jg
 */
public class MascotDAOStressTest {
    private MascotDAO mascotDao;
    // TODO: generate the path to the file in a dynamic way
    private File sourceFile = new File("/home/jg/Projects/TestFiles/Mascot/F009873.dat");

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        // create the mascot dao
        mascotDao = new MascotDAO(sourceFile);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MascotDAO#getSpectrumIterator()}.
     */
    @Test
    public void testGetSpectrumIterator() {
        Iterator<Spectrum> spectra = mascotDao.getSpectrumIterator(false);

        int nSpectra = 0;

        while (spectra.hasNext()) {
            Spectrum s = spectra.next();

            if (s == null)
                fail("Spectrum could not be retrieved");

            nSpectra++;

            // check if the id was set correctly
            assertEquals(nSpectra, s.getId());
        }

        assertEquals(56673, nSpectra);
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MascotDAO#getIdentificationIterator(boolean)}.
     */
    @Test
    public void testGetIdentificationIterator() {
        // get the iterator
        Iterator<Identification> it = mascotDao.getIdentificationIterator(false);

        // just count the identifications as a begining
        int nIds = 0;

        while (it.hasNext()) {
            Identification id = it.next();

            if (id == null)
                fail("Null returned for identification");

            nIds++;
        }

        assertEquals(10960, nIds);
    }

}
