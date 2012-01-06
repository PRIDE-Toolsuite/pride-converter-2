package uk.ac.ebi.pride.tools.converter.dao.impl;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.report.model.CV;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.SearchResultIdentifier;
import uk.ac.ebi.pride.tools.converter.report.model.Software;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;

public class MzXmlDAOTest extends TestCase {
	private static MzXmlDAO dao;
	private static File sourceFile;

	protected void setUp() throws Exception {
		super.setUp();
		
		if (dao != null)
			return;
		
		 // create the mzXML dao
       try {
           URL testFile = getClass().getClassLoader().getResource("testfile.mzXML");
           assertNotNull("Error loading mzXML test file", testFile);
           sourceFile = new File(testFile.toURI());
           dao = new MzXmlDAO(sourceFile);
       } catch (Exception ex) {
    	   ex.printStackTrace();
           fail(ex.getMessage());
       }
	}

	public void testGetSpectraIds() {
		List<String> spectraIds = dao.getSpectraIds();
		
		assertNotNull(spectraIds);
		assertEquals(9181, spectraIds.size());
	}

	public void testGetExperimentTitle() {
		assertEquals("Unknown mzXML experiment.", dao.getExperimentTitle());
	}

	public void testGetSoftware() {
		Software s;
		try {
			s = dao.getSoftware();
			
			assertNotNull(s);
			assertEquals("ReAdW", s.getName());
			assertEquals("4.0.2(build Jul  1 2008 14:23:37)", s.getVersion());
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testGetProcessingMethod() {
		Param p = dao.getProcessingMethod();
		
		assertNotNull(p);
		
		assertEquals("Param{cvParam=null, userParam=null}", p.toString());
	}

	public void testGetSearchDatabaseName() {
		assertEquals("", dao.getSearchDatabaseName());
	}

	public void testGetSearchResultIdentifier() {
		// get the identifier
        SearchResultIdentifier identifier = dao.getSearchResultIdentifier();

        assertEquals("dcfa884e459122b4dddee2330194d432", identifier.getHash());
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
		assertEquals(9181, dao.getSpectrumCount(false));
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
		
		assertEquals(9181, specCount);
	}

}
