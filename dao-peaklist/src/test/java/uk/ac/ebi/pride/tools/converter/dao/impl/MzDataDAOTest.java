package uk.ac.ebi.pride.tools.converter.dao.impl;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import uk.ac.ebi.pride.jaxb.model.CvLookup;
import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.report.model.CV;
import uk.ac.ebi.pride.tools.converter.report.model.Contact;
import uk.ac.ebi.pride.tools.converter.report.model.InstrumentDescription;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.SearchResultIdentifier;
import uk.ac.ebi.pride.tools.converter.report.model.Software;
import uk.ac.ebi.pride.tools.converter.report.model.SourceFile;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;

public class MzDataDAOTest extends TestCase {
	private static MzDataDAO dao;
	private static File sourceFile;

	protected void setUp() throws Exception {
		super.setUp();
		
		if (dao != null)
			return;
		
		// create the dao
		try {
			URL testFile = getClass().getClassLoader().getResource("PRIDE_Exp_mzData_Ac_8869.xml");
			assertNotNull("Error loading dta test file", testFile);
			sourceFile = new File(testFile.toURI());
			dao = new MzDataDAO(sourceFile);
		} catch (ConverterException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetSpectraIds() {
		List<String> ids = dao.getSpectraIds();
		
		assertEquals(2139, ids.size());
	}

	public void testGetExperimentTitle() {
		try {
			assertEquals("MS-060322ng_DC-LPS-CYT", dao.getExperimentTitle());
		} catch (InvalidFormatException e) {
			fail(e.getMessage());
		}
	}

	public void testGetExperimentShortLabel() {
		assertNull(dao.getExperimentShortLabel());
	}

	public void testGetExperimentParams() {
		Param param;
		try {
			param = dao.getExperimentParams();

			assertNotNull(param);
			assertEquals(1, param.getCvParam().size());
		} catch (InvalidFormatException e) {
			fail(e.getMessage());
		}
	}

	public void testGetSampleName() {
		assertEquals("MS-060322ng_DC-LPS-CYT", dao.getSampleName());
	}

	public void testGetSampleComment() {
		assertNull(dao.getSampleComment());
	}

	public void testGetSampleParams() {
		Param param;
		try {
			param = dao.getSampleParams();

			assertEquals(4, param.getCvParam().size());
			assertEquals("GO:0005737", param.getCvParam().get(2).getAccession());
		} catch (InvalidFormatException e) {
			fail(e.getMessage());
		}
	}

	public void testGetSourceFile() {
		SourceFile file;
		try {
			file = dao.getSourceFile();
			
			assertEquals(sourceFile.getAbsolutePath(), file.getPathToFile());
			assertEquals(sourceFile.getName(), file.getNameOfFile());
			assertEquals("mzData", file.getFileType());
		} catch (InvalidFormatException e) {
			fail(e.getMessage());
		}
	}

	public void testGetContacts() {
		Collection<Contact> contacts = dao.getContacts();
		
		assertEquals(1, contacts.size());
		assertEquals("Christopher Gerner", contacts.iterator().next().getName());
	}

	public void testGetInstrument() {
		InstrumentDescription instrument = dao.getInstrument();
		
		assertNotNull(instrument);
		
		assertEquals(3, instrument.getSource().getCvParam().size());
	}

	public void testGetSoftware() {
		try {
			Software software = dao.getSoftware();
			
			assertNotNull(software);
			assertEquals("A.03.03", software.getVersion());
		} catch (InvalidFormatException e) {
			fail(e.getMessage());
		}
	}

	public void testGetSearchResultIdentifier() {
		// get the identifier
		SearchResultIdentifier identifier;
		try {
			identifier = dao.getSearchResultIdentifier();
			
			assertEquals("d3c3c44ebf7478760ad592538227e930", identifier.getHash());
			assertEquals(sourceFile.getAbsolutePath(),
					identifier.getSourceFilePath());
		} catch (InvalidFormatException e) {
			fail(e.getMessage());
		}		
	}

	public void testGetCvLookup() {
		Collection<CV> cvLookups;
		try {
			cvLookups = dao.getCvLookup();
			
			assertEquals(7, cvLookups.size());
		} catch (InvalidFormatException e) {
			fail(e.getMessage());
		}
	}

	public void testGetSpectrumCount() {
		try {
			assertEquals(2139, dao.getSpectrumCount(false));
		} catch (InvalidFormatException e) {
			fail(e.getMessage());
		}
	}

	public void testGetSpectrumIterator() {
		try {
			Iterator<Spectrum> it = dao.getSpectrumIterator(false);
			
			int count = 0;
			
			while(it.hasNext()) {
				Spectrum s = it.next();
				
				assertNotNull(s);
				
				count++;
			}
			
			assertEquals(2139, count);
		} catch (InvalidFormatException e) {
			fail(e.getMessage());
		}
	}
}
