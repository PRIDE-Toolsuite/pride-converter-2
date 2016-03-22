package uk.ac.ebi.pride.tools.converter.dao.impl;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.pride.jaxb.model.Precursor;
import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.report.model.CV;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.DatabaseMapping;
import uk.ac.ebi.pride.tools.converter.report.model.Identification;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.Peptide;
import uk.ac.ebi.pride.tools.converter.report.model.SearchResultIdentifier;
import uk.ac.ebi.pride.tools.converter.report.model.SourceFile;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;

public class XTandemDAOTest extends TestCase {

	private static File sourceFile;
	private static XTandemDAO dao;

	@Before
	public void setUp() throws Exception {
		if (dao != null)
			return;
		
		// create the mascot dao
		try {
			URL testFile = getClass().getClassLoader().getResource(
					"GPM33000005827.xml");
			assertNotNull("Error loading xtandem test file", testFile);
			sourceFile = new File(testFile.toURI());
			dao = new XTandemDAO(sourceFile);
		} catch (ConverterException ex) {
			fail(ex.getMessage());
		}
	}

	// @Test
	// public void testSetConfiguration() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetConfiguration() {
	// fail("Not yet implemented");
	// }

	@Test
	public void testGetExperimentTitle() {
		try {
			assertEquals("", dao.getExperimentTitle());
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetExperimentParams() {
		Param params = dao.getExperimentParams();

		assertEquals(2, params.getCvParam().size());
		assertEquals("2009:11:12:19:45:48", params.getCvParam().get(0)
				.getValue());
	}

	@Test
	public void testGetSampleParams() {
		assertNotNull(dao.getExperimentParams());
	}
	
	@Test
	public void testGetProcessingMethod() {
		assertEquals("Param{cvParam=[CvParam{cvLabel='PRIDE', accession='PRIDE:0000161', name='Fragment mass tolerance setting', value='0.4'}, CvParam{cvLabel='MS', accession='MS:1001413', name='search tolerance minus value', value='0.5'}, CvParam{cvLabel='MS', accession='MS:1001412', name='search tolerance plus value', value='3.0'}, CvParam{cvLabel='PRIDE', accession='PRIDE:0000162', name='Allowed missed cleavages', value='1'}], userParam=null}", dao.getProcessingMethod().toString());
	}

	@Test
	public void testGetSourceFile() {
		SourceFile file = dao.getSourceFile();

		assertEquals(sourceFile.getAbsolutePath(), file.getPathToFile());
		assertEquals(sourceFile.getName(), file.getNameOfFile());
		assertEquals("X!Tandem XML file", file.getFileType());
	}

	@Test
	public void testGetSoftware() {
		assertEquals(
				"Software{name='X! Tandem', version='TORNADO (2009.04.01.4)', comments='null', completionTime=null}",
				dao.getSoftware().toString());
	}

	@Test
	public void testGetSearchDatabaseName() {
		assertEquals(
				"ENSEMBL GRCh37.55, cRAP 2009.05.01 with Sigma-Aldrich Universal Protein Standard sequences",
				dao.getSearchDatabaseName());
	}

	@Test
	public void testGetSearchDatabaseVersion() {
		assertEquals("", dao.getSearchDatabaseVersion());
	}

	@Test
	public void testGetDatabaseMappings() {
		Collection<DatabaseMapping> mappings = dao.getDatabaseMappings();

		assertEquals(2, mappings.size());

		int nCurDb = 0;

		for (DatabaseMapping m : mappings) {

			if (nCurDb == 0) {
				assertEquals(
						"../fasta/human_e.fasta.pro (ENSEMBL GRCh37.55)",
						m.getSearchEngineDatabaseName());
				assertEquals("", m.getSearchEngineDatabaseVersion());
			}

			nCurDb++;
		}
	}

	@Test
	public void testGetPTMs() {
		Collection<PTM> ptms;
		try {
			ptms = dao.getPTMs();

			assertEquals(42, ptms.size());

			for (PTM p : ptms) {
				String description = p.getSearchEnginePTMLabel();

				// check the residues
				if (!description.contains("@"))
					fail("Invalid description " + description);

				String residue = description
						.substring(description.indexOf('@') + 1);

				assertEquals(residue, p.getResidues());
			}

		} catch (InvalidFormatException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetSearchResultIdentifier() {
		// get the identifier
		SearchResultIdentifier identifier = dao.getSearchResultIdentifier();

		assertEquals("0a9488c8e0e31b3d442f134c92e731e2", identifier.getHash());
		assertEquals(sourceFile.getAbsolutePath(),
				identifier.getSourceFilePath());
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
				assertEquals(
						"http://psidev.cvs.sourceforge.net/viewvc/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo",
						cv.getAddress());
			}

			nCount++;
		}

		assertEquals(2, nCount);
	}

	@Test
	public void testGetSpectrumCount() {
		try {
			assertEquals(1585, dao.getSpectrumCount(false));
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetSpectrumIterator() {
		Iterator<Spectrum> it;
		try {
			it = dao.getSpectrumIterator(false);

			int nSpecCount = 0;
			Spectrum spec4 = null;

			while (it.hasNext()) {
				if (nSpecCount == 3)
					spec4 = it.next();
				else
					assertNotNull(it.next());

				nSpecCount++;
			}

			assertEquals(nSpecCount, 1585);

			assertNotNull(spec4);

			Precursor prec = spec4.getSpectrumDesc().getPrecursorList()
					.getPrecursor().get(0);
			assertEquals(1, prec.getMsLevel());
			assertEquals(2, prec.getIonSelection().getCvParam().size());
			assertEquals("651.3204585", prec.getIonSelection().getCvParam()
					.get(0).getValue());
			assertEquals("2", prec.getIonSelection().getCvParam().get(1)
					.getValue());
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetSpectrumReferenceForPeptideUID() {
		try {
			assertEquals(1234,
					dao.getSpectrumReferenceForPeptideUID("1234.51.1"));
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetIdentificationByUID() {
		Identification id;
		try {
			id = dao.getIdentificationByUID("ENSP00000216181");

			assertNotNull(id);
			assertEquals("ENSP00000216181", id.getAccession());
			assertEquals(27, id.getPeptide().size());
			
			Peptide p = id.getPeptide().get(0);
			
			assertEquals(26, p.getFragmentIon().size());
			
			id = dao.getIdentificationByUID("ENSP00000381216:reversed");

			assertNotNull(id);
			assertEquals("DECOY_ENSP00000381216", id.getAccession());
			assertEquals(1, id.getPeptide().size());
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetIdentificationIterator() {
		Iterator<Identification> it = dao.getIdentificationIterator(false);

		int nIdCount = 0;
		int nullCount = 0;

		while (it.hasNext()) {
			Identification i = it.next();

			if (i == null)
				nullCount++;

			nIdCount++;
		}

		assertEquals(77, nullCount);
		assertEquals(1732, nIdCount);
	}
}
