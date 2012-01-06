package uk.ac.ebi.pride.tools.converter.dao.impl;

import junit.framework.TestCase;
import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.report.model.*;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;

import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.Iterator;

public class MgfDAOTest extends TestCase {
	private static File sourceFile;
	private static MgfDAO mgfDao;

	protected void setUp() throws Exception {
		if (mgfDao != null)
			return;
		
		// create the mascot dao
		try {
			URL testFile = getClass().getClassLoader().getResource(
					"F001257.mgf");
			assertNotNull("Error loading mgf test file", testFile);
			sourceFile = new File(testFile.toURI());
			mgfDao = new MgfDAO(sourceFile);
		} catch (ConverterException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetExperimentTitle() {
		assertEquals("First test experiment (values are not real)",
				mgfDao.getExperimentTitle());
	}

	public void testGetExperimentParams() {
		Param params = mgfDao.getExperimentParams();

		assertEquals(1, params.getCvParam().size());
		assertEquals("Mascot generic", params.getCvParam().get(0).getValue());
	}

	public void testGetSampleParams() {
		Param params = mgfDao.getSampleParams();
		assertEquals(1, params.getUserParam().size());
		assertEquals("Human 9606", params.getUserParam().get(0).getValue());
	}

	public void testGetSourceFile() {
		SourceFile file = mgfDao.getSourceFile();

		assertEquals(sourceFile.getAbsolutePath(), file.getPathToFile());
		assertEquals("F001257.mgf", file.getNameOfFile());
		assertEquals("Mascot generic", file.getFileType());
	}

	public void testGetSoftware() {
		Software software = mgfDao.getSoftware();
		assertEquals("Unknown generic (MGF file format)", software.getName());
		assertEquals("", software.getVersion());
	}

	public void testGetSearchDatabaseName() {
		assertEquals("SwissProt v57", mgfDao.getSearchDatabaseName());
	}

	public void testGetSearchDatabaseVersion() {
		assertEquals("", mgfDao.getSearchDatabaseVersion());
	}

	public void testGetSearchResultIdentifier() {
		// get the identifier
		SearchResultIdentifier identifier = mgfDao.getSearchResultIdentifier();

		assertEquals("f50e419835558962b9e918218365ba79", identifier.getHash());
		assertEquals(sourceFile.getAbsolutePath(),
				identifier.getSourceFilePath());
	}

	public void testGetCvLookup() {
		Collection<CV> lookups = mgfDao.getCvLookup();

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
	}

	public void testGetSpectrumCount() {
		assertEquals(10, mgfDao.getSpectrumCount(false));
	}

	public void testGetSpectrumIterator() {
		Iterator<Spectrum> it;
		try {
			it = mgfDao.getSpectrumIterator(false);

			int nSpecCount = 0;

			while (it.hasNext()) {
				Spectrum s = it.next();

				assertNotNull(s);

				// check the second spectrum
				if (nSpecCount == 1) {
					assertEquals(
							"406.794",
							s.getSpectrumDesc()
									.getPrecursorList()
									.getPrecursor()
									.get(0)
									.getIonSelection()
									.getCvParamByAcc(
											DAOCvParams.PRECURSOR_MZ
													.getAccession()).getValue());
					assertEquals(
							"3",
							s.getSpectrumDesc()
									.getPrecursorList()
									.getPrecursor()
									.get(0)
									.getIonSelection()
									.getCvParamByAcc(
											DAOCvParams.POSSIBLE_CHARGE_STATE
													.getAccession()).getValue());

					byte[] intensities = s.getIntenArrayBinary().getData()
							.getValue();
					byte[] masses = s.getMzArrayBinary().getData().getValue();

					// convert the intensBytes to a byte buffer
					ByteBuffer intensBuffer = ByteBuffer.wrap(intensities);
					ByteBuffer mzBuffer = ByteBuffer.wrap(masses);

					intensBuffer.order(ByteOrder.LITTLE_ENDIAN);
					mzBuffer.order(ByteOrder.LITTLE_ENDIAN);

					// check some of the numbers
					assertEquals(102.062, mzBuffer.getDouble(8 * 0));
					assertEquals(114.1092, mzBuffer.getDouble(8 * 1));
					assertEquals(115.1159, mzBuffer.getDouble(8 * 2));

					assertEquals(10.0, intensBuffer.getDouble(0));
					assertEquals(4.0, intensBuffer.getDouble(8));
					assertEquals(4.0, intensBuffer.getDouble(16));
				}

				nSpecCount++;
			}

			assertEquals(10, nSpecCount);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
