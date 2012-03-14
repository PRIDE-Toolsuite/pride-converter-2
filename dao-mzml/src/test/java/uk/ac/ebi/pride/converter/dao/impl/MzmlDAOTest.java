package uk.ac.ebi.pride.converter.dao.impl;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;
import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.dao.impl.MzmlDAO;
import uk.ac.ebi.pride.tools.converter.report.model.CV;
import uk.ac.ebi.pride.tools.converter.report.model.Contact;
import uk.ac.ebi.pride.tools.converter.report.model.InstrumentDescription;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.SearchResultIdentifier;
import uk.ac.ebi.pride.tools.converter.report.model.SourceFile;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;

public class MzmlDAOTest extends TestCase {
	private static File sourceFile;
	private static MzmlDAO dao;

	protected void setUp() throws Exception {
		if (dao != null) {
			assertNotNull(dao);
			return;
		}
		
		// create the dao
		try {
			URL testFile = getClass().getClassLoader().getResource(
					"dta_example.mzML");
			assertNotNull("Error loading dta test file", testFile);
			sourceFile = new File(testFile.toURI());
			dao = new MzmlDAO(sourceFile);
		} catch (ConverterException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetExperimentTitle() {
		assertEquals("", dao.getExperimentTitle());
	}

	public void testGetExperimentShortLabel() {
		assertEquals(null, dao.getExperimentShortLabel());
	}

	public void testGetExperimentParams() {
		Param param = dao.getExperimentParams();

		assertEquals(34, param.getCvParam().size());
		assertEquals(1, param.getUserParam().size());
	}

	public void testGetSampleName() {
		String sampleName = dao.getSampleName();

		assertEquals("", sampleName);
	}

	public void testGetSampleComment() {
		assertEquals(null, dao.getSampleComment());
	}

	public void testGetSampleParams() {
		assertEquals(0, dao.getSampleParams().getUserParam().size());
		assertEquals(0, dao.getSampleParams().getCvParam().size());
	}

	public void testGetSourceFile() {
		SourceFile file = dao.getSourceFile();

		assertEquals(sourceFile.getAbsolutePath(), file.getPathToFile());
		assertEquals(sourceFile.getName(), file.getNameOfFile());
		assertEquals("mzML", file.getFileType());
	}

	public void testGetContacts() {
		Collection<Contact> contacts = dao.getContacts();

		assertEquals("[]", contacts.toString());
	}

	public void testGetInstrument() {
		InstrumentDescription descr;

		descr = dao.getInstrument();

		assertEquals(1, descr.getAnalyzerList().getCount());
		assertEquals(1, descr.getSource().getCvParam().size());
		assertEquals(1, descr.getDetector().getCvParam().size());
		assertEquals(1, descr.getAnalyzerList().getAnalyzer().get(0)
				.getCvParam().size());
	}

	public void testGetSoftware() {
		assertEquals("Unknown generic (mzML format)", dao.getSoftware()
				.getName());
	}

	public void testGetProcessingMethod() {
		Param processingMethod = dao.getProcessingMethod();

		assertEquals(2, processingMethod.getCvParam().size());
		assertEquals(7, processingMethod.getUserParam().size());
	}

	public void testGetProtocol() {
		assertEquals(null, dao.getProtocol());
	}

	public void testGetReferences() {
		assertEquals(null, dao.getReferences());
	}

	public void testGetSearchDatabaseName() {
		assertEquals("", dao.getSearchDatabaseName());
	}

	public void testGetSearchDatabaseVersion() {
		assertEquals("", dao.getSearchDatabaseVersion());
	}

	public void testGetPTMs() {
		assertEquals(0, dao.getPTMs().size());
	}

	public void testGetSearchResultIdentifier() {
		// get the identifier
		SearchResultIdentifier identifier = dao.getSearchResultIdentifier();

		assertEquals("c3182c2ec860f48a04cf9ce2be2d13b0", identifier.getHash());
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
		assertEquals(10, dao.getSpectrumCount(false));
	}

	public void testGetSpectrumIterator() {
		Iterator<Spectrum> it = dao.getSpectrumIterator(false);

		int nSpecCount = 0;

		while (it.hasNext()) {
			assertNotNull(it.next());

			nSpecCount++;
		}

		assertEquals(10, nSpecCount);
	}

}
