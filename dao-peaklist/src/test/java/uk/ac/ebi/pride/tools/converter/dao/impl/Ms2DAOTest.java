package uk.ac.ebi.pride.tools.converter.dao.impl;

import junit.framework.TestCase;
import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.report.model.CV;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.SearchResultIdentifier;
import uk.ac.ebi.pride.tools.converter.report.model.SourceFile;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

public class Ms2DAOTest extends TestCase {
	private static Ms2DAO dao;
	private static File sourceFile;

	protected void setUp() throws Exception {
		if (dao != null)
			return;
		
		// create the dao
		try {
			URL testFile = getClass().getClassLoader().getResource("test.ms2");
			assertNotNull("Error loading dta test file", testFile);
			sourceFile = new File(testFile.toURI());
			dao = new Ms2DAO(sourceFile);
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
		assertEquals("ms2", params.getCvParam().get(0).getValue());

		assertEquals(10, params.getUserParam().size());
		assertEquals(
				"[UserParam{name='ScanType', value='MS2'}, UserParam{name='IsolationWindow', value=''}, UserParam{name='Comments', value='RawXtract written by John Venable, 2003'}, UserParam{name='DataType', value='Centroid'}, UserParam{name='LastScan', value='32808'}, UserParam{name='InstrumentType', value='ITMS'}, UserParam{name='AcquisitionMethod', value='Data-Dependent'}, UserParam{name='FirstScan', value='1'}, UserParam{name='MinimumNpeaks', value='30'}, UserParam{name='MinimumMass', value='700'}]",
				params.getUserParam().toString());
	}

	public void testGetSampleParams() {
		assertNotNull(dao.getSampleParams());
	}

	public void testGetSourceFile() {
		SourceFile file = dao.getSourceFile();

		assertEquals(sourceFile.getAbsolutePath(), file.getPathToFile());
		assertEquals(sourceFile.getName(), file.getNameOfFile());
		assertEquals("ms2", file.getFileType());
	}

	public void testGetSoftware() {
		assertEquals(
				"Software{name='RAWXtract', version='1.8', comments='MS2logMSzm', completionTime=null}",
				dao.getSoftware().toString());
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

		assertEquals("7d0326937ba37ded0e0111c94f038b47", identifier.getHash());
		assertEquals(sourceFile.getAbsolutePath(),
				identifier.getSourceFilePath());
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
				assertEquals(
						"http://psidev.cvs.sourceforge.net/viewvc/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo",
						cv.getAddress());
			}

			nCount++;
		}

		assertEquals(2, nCount);
	}

	public void testGetSpectrumCount() {
		assertEquals(16421, dao.getSpectrumCount(false));
		assertEquals(0, dao.getSpectrumCount(true));
	}

	public void testGetSpectrumIterator() {
		Iterator<Spectrum> it;
		try {
			it = dao.getSpectrumIterator(false);

			int nSpecCount = 0;

			while (it.hasNext()) {
				assertNotNull(it.next());

				nSpecCount++;
			}

			assertEquals(16421, nSpecCount);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

}
