package uk.ac.ebi.pride.tools.converter.dao.impl;

import junit.framework.TestCase;
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

public class PklDAOTest extends TestCase {
    private static File sourceFile;
    private static PklDAO dao;

    protected void setUp() throws Exception {
    	if (dao != null)
    		return;
    	
        // create the mascot dao
        try {
            URL testFile = getClass().getClassLoader().getResource("concatenated.pkl");
            assertNotNull("Error loading dta test file", testFile);
            sourceFile = new File(testFile.toURI());
            dao = new PklDAO(sourceFile);
        } catch (ConverterException ex) {
            fail(ex.getMessage());
        }
    }

    public void testGetExperimentTitle() {
        assertEquals("Unknown title", dao.getExperimentTitle());
    }

    public void testGetExperimentParams() {
        Param params = dao.getExperimentParams();

        assertEquals(1, params.getCvParam().size());
        assertEquals("Micromass PKL", params.getCvParam().get(0).getValue());
    }

    public void testGetSampleParams() {
        assertNotNull(dao.getExperimentParams());
    }

    public void testGetSourceFile() {
        SourceFile file = dao.getSourceFile();

        assertEquals(sourceFile.getAbsolutePath(), file.getPathToFile());
        assertEquals(sourceFile.getName(), file.getNameOfFile());
        assertEquals("Micromass PKL", file.getFileType());
    }

    public void testGetSoftware() {
        assertEquals("Software{name='Unknown generic (Micromass PKL format)', version='', comments='null', completionTime=null}", dao.getSoftware().toString());
    }

    public void testGetSearchDatabaseName() {
        assertEquals("", dao.getSearchDatabaseName());
    }

    public void testGetSearchDatabaseVersion() {
        assertEquals("", dao.getSearchDatabaseVersion());
    }

    public void testGetPTMs() {
        assertNotNull(dao.getPTMs());
    }

    public void testGetSearchResultIdentifier() {
        // get the identifier
        SearchResultIdentifier identifier = dao.getSearchResultIdentifier();

        assertEquals("6a9de76ff5b1c36376168125c48b47bc", identifier.getHash());
        assertEquals(sourceFile.getAbsolutePath(), identifier.getSourceFilePath());
    }

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

    public void testGetSpectrumCount() {
        assertEquals(16, dao.getSpectrumCount(false));
        assertEquals(0, dao.getSpectrumCount(true));
    }

    public void testGetSpectrumIterator() {
        Iterator<Spectrum> it = dao.getSpectrumIterator(false);

        int specCount = 0;

        while (it.hasNext()) {
            Spectrum s = it.next();

            assertNotNull(s);

            specCount++;
        }

        assertEquals(16, specCount);
    }

}
