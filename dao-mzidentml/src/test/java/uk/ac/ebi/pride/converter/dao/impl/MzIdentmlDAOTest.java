package uk.ac.ebi.pride.converter.dao.impl;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import junit.framework.TestCase;
import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.dao.impl.MzIdentmlDAO;
import uk.ac.ebi.pride.tools.converter.report.model.CV;
import uk.ac.ebi.pride.tools.converter.report.model.Contact;
import uk.ac.ebi.pride.tools.converter.report.model.DatabaseMapping;
import uk.ac.ebi.pride.tools.converter.report.model.Identification;
import uk.ac.ebi.pride.tools.converter.report.model.InstrumentDescription;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.Peptide;
import uk.ac.ebi.pride.tools.converter.report.model.Reference;
import uk.ac.ebi.pride.tools.converter.report.model.SearchResultIdentifier;
import uk.ac.ebi.pride.tools.converter.report.model.Software;
import uk.ac.ebi.pride.tools.converter.report.model.SourceFile;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;

public class MzIdentmlDAOTest extends TestCase {
    private static File sourceFile;
    private static MzIdentmlDAO dao;

    protected void setUp() throws Exception {
        super.setUp();
        
        if (dao != null)
        	return;

        // create the dao
        try {
            URL testFile = getClass().getClassLoader().getResource("55merge_mascot_full.mzid");
            assertNotNull("Error loading dta test file", testFile);
            sourceFile = new File(testFile.toURI());
            dao = new MzIdentmlDAO(sourceFile);
        } catch (ConverterException ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    public void testSetConfiguration() {
        Properties props = new Properties();
        props.setProperty("my-prop", "Hi");

        dao.setConfiguration(props);

        props = dao.getConfiguration();

        assertEquals("Hi", props.getProperty("my-prop"));
    }

    public void testGetConfiguration() {
        assertNotNull(dao.getConfiguration());
    }

    public void testGetExperimentTitle() {
        assertEquals("My experiment", dao.getExperimentTitle());
    }

    public void testGetExperimentShortLabel() {
        assertNotNull(dao.getExperimentShortLabel());
    }

    public void testGetExperimentParams() {
        assertEquals(0, dao.getExperimentParams().getCvParam().size());
    }

    public void testGetSampleName() {
        assertEquals("Unknown sample", dao.getSampleName());
    }

    public void testGetSampleComment() {
        assertNull(dao.getSampleComment());
    }

    public void testGetSampleParams() {
        Param param = dao.getSampleParams();

        assertEquals(0, param.getCvParam().size());
    }

    public void testGetSourceFile() {
        SourceFile sourceFile = dao.getSourceFile();

        assertNotNull(sourceFile);
        assertEquals("55merge_mascot_full.mzid", sourceFile.getNameOfFile());
        assertEquals("mzIdentML file", sourceFile.getFileType());
        assertEquals(this.sourceFile.getAbsolutePath(), sourceFile.getPathToFile());
    }

    public void testGetContacts() {
        Collection<Contact> contacts = dao.getContacts();

        assertEquals(1, contacts.size());

        for (Contact c : contacts) {
            assertEquals("ajones", c.getName());
            assertEquals("", c.getContactInfo());
            assertEquals("Matrix Science Limited", c.getInstitution());
        }
    }

    public void testGetInstrument() {
		InstrumentDescription i = dao.getInstrument();
        assertNull(dao.getInstrument());
    }

    public void testGetSoftware() {
        Software software = dao.getSoftware();

        assertNotNull(software);
        assertEquals("Mascot", software.getName());
        assertEquals("2.3.02", software.getVersion());
        assertEquals(" No customisations ", software.getComments());
    }

    public void testGetProcessingMethod() {
		Param m = dao.getProcessingMethod();
		assertNull(m);
    }

    public void testGetProtocol() {
        assertNull(dao.getProtocol());
    }

    public void testGetReferences() {
        Collection<Reference> refs = dao.getReferences();

        assertEquals(1, refs.size());

        for (Reference ref : refs) {
            assertEquals("David N. Perkins, Darryl J. C. Pappin, David M. Creasy, John S. Cottrell. (1999). Probability-based protein identification by searching sequence databases using mass spectrometry data Electrophoresis 20(18):3551-3567.", ref.getRefLine());
            assertEquals(1, ref.getAdditional().getCvParam().size());
            assertEquals("doi/doi", ref.getAdditional().getCvParam().get(0).getValue());
        }
    }

    public void testGetSearchDatabaseName() {
        assertEquals("Neo_rndTryp_3times.fasta", dao.getSearchDatabaseName());
    }

    public void testGetSearchDatabaseVersion() {
        assertEquals("1.0", dao.getSearchDatabaseVersion());
    }

    public void testDatabaseMappings() {
        Collection<DatabaseMapping> mappings = dao.getDatabaseMappings();

        assertEquals(1, mappings.size());

        for (DatabaseMapping m : mappings) {
            assertEquals("NeoProt_tripledecoy", m.getSearchEngineDatabaseName());
            assertEquals("1.0", m.getSearchEngineDatabaseVersion());
        }
    }

    public void testGetPTMs() {
        Collection<PTM> ptms;
		try {
			ptms = dao.getPTMs();
			
	        assertEquals(2, ptms.size());
	
	        int nPtmIndex = 0;
	
	        for (PTM ptm : ptms) {
	            // process the ptms
	            assertEquals(0, ptm.getAdditional().getCvParam().size());
	
	            if (nPtmIndex == 1) {
	                assertEquals("UNIMOD:35", ptm.getSearchEnginePTMLabel());
	                assertNull(ptm.getModDatabase());
	                assertNull(ptm.getModAccession());
	                assertEquals("MX", ptm.getResidues());
	            }
	
	            nPtmIndex++;
	        }
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
    }

    public void testGetSearchResultIdentifier() {
        // get the identifier
        SearchResultIdentifier identifier = dao.getSearchResultIdentifier();

        assertEquals("27deee9ed0ae536b45bb32c71f59fd34", identifier.getHash());
        assertEquals(sourceFile.getAbsolutePath(), identifier.getSourceFilePath());
    }

    public void testGetCvLookup() {
        ArrayList<CV> cvs = new ArrayList<CV>(dao.getCvLookup());

        assertEquals(5, cvs.size());

        assertEquals("MS", cvs.get(3).getCvLabel());
        assertEquals("PSI Mass Spectrometry Ontology", cvs.get(3).getFullName());
    }

    public void testGetSpectrumCount() {
        Properties props = new Properties();
        props.setProperty("allow_identifications_only", "true");
        dao.setConfiguration(props);

        int nSpecCount;
		try {
			nSpecCount = dao.getSpectrumCount(false);
		
	        assertEquals(0, nSpecCount);
	
	        nSpecCount = dao.getSpectrumCount(true);
	
	        assertEquals(0, nSpecCount);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
    }

    public void testGetSpectrumIterator() {
        Properties props = new Properties();
        props.setProperty("allow_identifications_only", "true");
        dao.setConfiguration(props);

        Iterator<Spectrum> it;
		try {
			it = dao.getSpectrumIterator(false);

	        int nSpecCount = 0;
	
	        while (it.hasNext()) {
	            assertNotNull(it.next());
	
	            nSpecCount++;
	        }
	
	        assertEquals(0, nSpecCount);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
    }

    public void testGetSpectrumReferenceForPeptideUID() {
        Properties props = new Properties();
        props.setProperty("allow_identifications_only", "true");
        dao.setConfiguration(props);
        int specRef;
		try {
			specRef = dao.getSpectrumReferenceForPeptideUID("SII_207_1!|!QEKPSKTADASEK_000000000000000_1_psu|NC_LIV_010760_236_248");
		
			assertEquals(-1, specRef);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
    }

    public void testGetIdentificationByUID() {
        Properties props = new Properties();
        props.setProperty("allow_identifications_only", "true");
        dao.setConfiguration(props);

        Identification id;
		try {
			id = dao.getIdentificationByUID("DBSeq_1_Rnd1psu|NC_LIV_142030");
	
	        assertNotNull(id);
	        assertEquals("Rnd1psu|NC_LIV_142030", id.getAccession());
	
	        assertEquals(1, id.getPeptide().size());
	        Peptide p = id.getPeptide().get(0);
	        assertEquals("GSGGK", p.getSequence());
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
    }

    public void testGetIdentificationIterator() {
        Properties props = new Properties();
        props.setProperty("allow_identifications_only", "true");
        dao.setConfiguration(props);

        Iterator<Identification> it;
		try {
			it = dao.getIdentificationIterator(true);

	        int nIdCount = 0;
	
	        while (it.hasNext()) {
	            Identification ident = it.next();
	
	            assertNotNull(ident);
	            assertTrue(ident.getPeptide().size() > 0);
	
	            nIdCount++;
	        }
	
	        assertEquals(246, nIdCount);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
    }

}
