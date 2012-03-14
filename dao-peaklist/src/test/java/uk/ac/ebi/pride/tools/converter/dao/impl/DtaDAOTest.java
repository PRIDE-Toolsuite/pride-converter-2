package uk.ac.ebi.pride.tools.converter.dao.impl;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.report.model.CV;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.SearchResultIdentifier;
import uk.ac.ebi.pride.tools.converter.report.model.SourceFile;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

public class DtaDAOTest extends TestCase {
    private static File sourceFile;
    private static DtaDAO dao;

    @Before
    public void setUp() throws Exception {
    	if (dao != null) {
    		return;
    	}
    	
        // create the mascot dao
        try {
            URL testFile = getClass().getClassLoader().getResource("QSTAR1a_concat.dta");
            assertNotNull("Error loading dta test file", testFile);
            sourceFile = new File(testFile.toURI());
            dao = new DtaDAO(sourceFile);
        } catch (ConverterException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testGetExperimentTitle() {
        assertEquals("", dao.getExperimentTitle());
    }

    @Test
    public void testGetExperimentParams() {
        Param params = dao.getExperimentParams();

        assertEquals(1, params.getCvParam().size());
        assertEquals("Sequest DTA", params.getCvParam().get(0).getValue());
    }

    @Test
    public void testGetSampleParams() {
        assertNotNull(dao.getExperimentParams());
    }

    @Test
    public void testGetSourceFile() {
        SourceFile file = dao.getSourceFile();

        assertEquals(sourceFile.getAbsolutePath(), file.getPathToFile());
        assertEquals(sourceFile.getName(), file.getNameOfFile());
        assertEquals("Sequest DTA", file.getFileType());
    }

    @Test
    public void testGetSoftware() {
        assertEquals("Software{name='Unknown generic (DTA file format)', version='', comments='null', completionTime=null}", dao.getSoftware().toString());
    }

    @Test
    public void testGetSearchDatabaseName() {
        assertEquals("", dao.getSearchDatabaseName());
    }

    @Test
    public void testGetSearchDatabaseVersion() {
        assertEquals("", dao.getSearchDatabaseVersion());
    }

    @Test
    public void testGetPTMs() {
        assertNotNull(dao.getPTMs());
    }

    @Test
    public void testGetSearchResultIdentifier() {
        // get the identifier
        SearchResultIdentifier identifier = dao.getSearchResultIdentifier();

        assertEquals("1c36d1d83eb9e1c56bd3a0a0d37466ee", identifier.getHash());
        assertEquals(sourceFile.getAbsolutePath(), identifier.getSourceFilePath());
    }

    @Test
    public void testGetCvLookup() {
        Collection<CV> lookups = dao.getCvLookup();

        assertEquals(2, lookups.size());

        int nCount = 0;

        for (CV cv : lookups) {

            if (nCount == 0) {
                assertEquals("MS", cv.getCvLabel());
                assertEquals("PSI Mass Spectrometry Ontology", cv.getFullName());
                assertEquals("1.2", cv.getVersion());
                assertEquals("http://psidev.cvs.sourceforge.net/viewvc/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo", cv.getAddress());
            }

            nCount++;
        }

        assertEquals(2, nCount);
    }

    @Test
    public void testGetSpectrumCount() {
        assertEquals(2457, dao.getSpectrumCount(false));
        assertEquals(0, dao.getSpectrumCount(true));
    }

    @Test
    public void testGetSpectrumIterator() {
        Iterator<Spectrum> it = dao.getSpectrumIterator(false);

        int specCount = 0;

        while (it.hasNext()) {
            Spectrum s = it.next();

            assertNotNull(s);

            specCount++;
        }

        assertEquals(2457, specCount);
    }
}
