/**
 *
 */
package uk.ac.ebi.pride.tools.converter.dao.impl;

import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.dao.DAOProperty;
import uk.ac.ebi.pride.tools.converter.report.model.CV;
import uk.ac.ebi.pride.tools.converter.report.model.Contact;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.DatabaseMapping;
import uk.ac.ebi.pride.tools.converter.report.model.Identification;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.Peptide;
import uk.ac.ebi.pride.tools.converter.report.model.SearchResultIdentifier;
import uk.ac.ebi.pride.tools.converter.report.model.Software;
import uk.ac.ebi.pride.tools.converter.report.model.SourceFile;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;

/**
 * @author jg
 */
public class MascotDAOTest extends TestCase {

    private static MascotDAO mascotDao;
    private static File sourceFile;


    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {   
    	if (mascotDao != null)
    		return;
    	
    	/*
    	 * IMPORTANT: this test case needs to reload
    	 * the mascot file every time.
    	 */
        // create the mascot dao
        try {
            URL testFile = getClass().getClassLoader().getResource("F001240.dat");
            assertNotNull("Error loading mascot test file", testFile);
            sourceFile = new File(testFile.toURI());
            mascotDao = new MascotDAO(sourceFile);
        } catch (ConverterException ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MascotDAO#getExperimentTitle()}.
     */
    @Test
    public void testGetExperimentTitle() {
        try {
            String title;
			try {
				title = mascotDao.getExperimentTitle();
				assertEquals("2780 PRIDE exepriment", title);
			} catch (InvalidFormatException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
        } catch (IllegalStateException ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MascotDAO#getExperimentShortLabel()}.
     */
    @Test
    public void testGetExperimentShortLabel() {
        // test the function - always returns null
        assertEquals(null, mascotDao.getExperimentShortLabel());
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MascotDAO#getExperimentParams()}.
     */
    @Test
    public void testGetExperimentParams() {
        /**
         * These parameters should contain:
         *    - the version of the used DAO(?)
         *    - date of search (PRIDE:0000219 - Date of search)
         *    - PRIDE:0000218 - Original MS data file format
         */
        // get the parameters
        Param param = mascotDao.getExperimentParams();

        // make sure there are 2 cvParams and 0 userParams
        assertEquals(3, param.getCvParam().size());
        assertEquals(0, param.getUserParam().size());

        boolean searchDate = false;
        boolean fileType = false;

        // test the params
        for (CvParam cv : param.getCvParam()) {
            // check the searchDate
            if (cv.getAccession().equals("PRIDE:0000219")) {
                searchDate = true;

                assertEquals("Date of search", cv.getName());

                Date date = new Date(1284642487000L);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                assertEquals(formatter.format(date), cv.getValue()); // Warning: this result may change due to different locales (always uses the current one)
            }

            // check the file type
            if (cv.getAccession().equals("PRIDE:0000218")) {
                fileType = true;

                assertEquals("Original MS data file format", cv.getName());
                assertEquals("Mascot dat file", cv.getValue());
            }
        }

        // make sure both parameters were there
        assertEquals(true, searchDate);
        assertEquals(true, fileType);
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MascotDAO#getSampleName()}.
     */
    @Test
    public void testGetSampleName() {
        assertEquals(null, mascotDao.getSampleName());
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MascotDAO#getSampleComment()}.
     */
    @Test
    public void testGetSampleComment() {
        assertEquals(null, mascotDao.getSampleComment());
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MascotDAO#getSampleParams()}.
     */
    @Test
    public void testGetSampleParams() {
        Param sampleParams = mascotDao.getSampleParams();

        // species should be 9606 human
        assertEquals(1, sampleParams.getCvParam().size());
        assertEquals(0, sampleParams.getUserParam().size());

        // check the cvParam
        for (CvParam cv : sampleParams.getCvParam()) {
            assertEquals("NEWT", cv.getCvLabel());
            assertEquals("9606", cv.getAccession());
            assertEquals("Homo sapiens (human)", cv.getName());
        }
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MascotDAO#getSourceFile()}.
     */
    @Test
    public void testGetSourceFile() {
        SourceFile file = mascotDao.getSourceFile();

        assertEquals(sourceFile.getAbsolutePath(), file.getPathToFile());
        assertEquals("F001240.dat", file.getNameOfFile());
        assertEquals("Mascot dat file", file.getFileType());
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MascotDAO#getContacts()}.
     */
    @Test
    public void testGetContacts() {
        Collection<Contact> contacts = mascotDao.getContacts();

        // make sure only one contact is returned
        assertEquals(1, contacts.size());

        // make sure the fields of the contact are correct
        for (Contact c : contacts) {
            // this loop will only run once
            assertEquals("David Ovelleiro", c.getName());
            assertEquals("dovelleiro@gmail.com", c.getContactInfo());
            assertEquals("", c.getInstitution());
        }
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MascotDAO#getInstrument()}.
     */
    @Test
    public void testGetInstrument() {
        /*
          InstrumentDescription instrument = mascotDao.getInstrument();

          assertEquals("ESI-TRAP", instrument.getInstrumentName());

          // check the source param
          Param sourceParam = instrument.getSource();

          // should only be 1 cvParam
          assertEquals(1, sourceParam.getCvParam().size());
          assertEquals(0, sourceParam.getUserParam().size());

          for (CvParam cv : sourceParam.getCvParam()) {
              // this loop only runs once
              assertEquals("PSI", cv.getCvLabel());
              assertEquals("PSI:1000073", cv.getAccession());
              assertEquals("Electrospray Ionization", cv.getName());
          }
          */
        assertEquals(null, mascotDao.getInstrument());
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MascotDAO#getSoftware()}.
     */
    @Test
    public void testGetSoftware() {
        Software software = mascotDao.getSoftware();

        assertEquals("Matrix Science Mascot", software.getName());
        assertEquals("2.3.01", software.getVersion());
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MascotDAO#getProcessingMethod()}.
     */
    @Test
    public void testGetProcessingMethod() {
        assertEquals("Param{cvParam=[CvParam{cvLabel='PRIDE', accession='PRIDE:0000161', name='Fragment mass tolerance setting', value='0.5'}, CvParam{cvLabel='PRIDE', accession='PRIDE:0000078', name='Peptide mass tolerance setting', value='2.0'}, CvParam{cvLabel='PRIDE', accession='PRIDE:0000162', name='Allowed missed cleavages', value='1'}, CvParam{cvLabel='MS', accession='MS:1001316', name='Mascot:SigThreshold', value='0.05'}, CvParam{cvLabel='MS', accession='MS:1001758', name='Mascot:SigThresholdType', value='identity'}], userParam=null}", mascotDao.getProcessingMethod().toString());
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MascotDAO#getSearchDatabaseName()}.
     */
    @Test
    public void testGetSearchDatabaseName() {
        assertEquals("SwissProt", mascotDao.getSearchDatabaseName());
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MascotDAO#getSearchDatabaseVersion()}.
     */
    @Test
    public void testGetSearchDatabaseVersion() {
        assertEquals("SwissProt_57.15.fasta", mascotDao.getSearchDatabaseVersion());
    }

    @Test
    public void testGetDatabaseMappingss() {
        Collection<DatabaseMapping> mappings = mascotDao.getDatabaseMappings();

        assertEquals(1, mappings.size());

        for (DatabaseMapping mapping : mappings) {
            assertEquals("SwissProt", mapping.getSearchEngineDatabaseName());
            assertEquals("SwissProt_57.15.fasta", mapping.getSearchEngineDatabaseVersion());
        }
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MascotDAO#getPTMs()}.
     */
    @Test
    public void testGetPTMs() {
        Collection<PTM> ptms = mascotDao.getPTMs();

        // make sure there are two modifications returned
        assertEquals(2, ptms.size());

        boolean fixed = false, var = false;

        for (PTM ptm : ptms) {
            // there is one fixed and one variable mod set
            if (ptm.isFixedModification()) {
                fixed = true;
                assertEquals("Carbamidomethyl (C)", ptm.getSearchEnginePTMLabel());
                assertEquals("C", ptm.getResidues());
            } else {
                var = true;
                assertEquals("Oxidation (M)", ptm.getSearchEnginePTMLabel());
                assertEquals("M", ptm.getResidues());
            }
        }

        // make sure both modifications were found
        assertEquals(true, fixed);
        assertEquals(true, var);
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MascotDAO#getSearchResultIdentifier()}.
     */
    @Test
    public void testGetSearchResultIdentifier() {
        // get the identifier
        SearchResultIdentifier identifier = mascotDao.getSearchResultIdentifier();

        assertEquals("0cf854a929ea2c0dee60ff937df54bdc", identifier.getHash());
        assertEquals(sourceFile.getAbsolutePath(), identifier.getSourceFilePath());
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MascotDAO#getSpectrumIterator(boolean)}.
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

        assertEquals(4706, nSpectra);
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MascotDAO#getSpectrumIterator(boolean)}.
     */
    @Test
    public void testGetSpectrumIteratorOnlyIdentified() {
        Iterator<Spectrum> spectra = mascotDao.getSpectrumIterator(true);

        // make sure no spectrum is returned twice
        HashSet<Integer> specIds = new HashSet<Integer>();

        int nSpectra = 0;

        // check the intensities and masses of the first spectrum
        byte[] intensities = null;
        byte[] masses = null;

        while (spectra.hasNext()) {
            Spectrum s = spectra.next();

            if (s == null)
                fail("Spectrum could not be retrieved");

            if (specIds.contains(s.getId()))
                fail("Spectrum returned twice");

            specIds.add(s.getId());

            // get the masses and intensities of the 1st spectrum
            if (s.getId() == 1) {
                intensities = s.getIntenArrayBinary().getData().getValue();
                masses = s.getMzArrayBinary().getData().getValue();
            }

            nSpectra++;
        }

        // check the number of spectra
        assertEquals(4700, nSpectra);

        // convert the intensBytes to a byte buffer
        ByteBuffer intensBuffer = ByteBuffer.wrap(intensities);
        ByteBuffer mzBuffer = ByteBuffer.wrap(masses);

        intensBuffer.order(ByteOrder.LITTLE_ENDIAN);
        mzBuffer.order(ByteOrder.LITTLE_ENDIAN);

        // check some of the numbers
        assertEquals(173.0918, mzBuffer.getDouble(0), 0);
        assertEquals(348.3387, mzBuffer.getDouble(1440), 0);
        assertEquals(579.4765, mzBuffer.getDouble(2208), 0);
        assertEquals(646.6163, mzBuffer.getDouble(2304), 0);

        assertEquals(2187.0, intensBuffer.getDouble(0), 0);
        assertEquals(69.12, intensBuffer.getDouble(1440), 0);
        assertEquals(16.37, intensBuffer.getDouble(2208), 0);
        assertEquals(10.71, intensBuffer.getDouble(2304), 0);
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MascotDAO#getSpectrumCount(boolean)
     */
    @Test
    public void testGetSpectrumCount() {
        assertEquals(4700, mascotDao.getSpectrumCount(true));
        assertEquals(4706, mascotDao.getSpectrumCount(false));
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MascotDAO#getSpectrumReferenceForPeptideUID(java.lang.String)}.
     */
    @Test
    public void testGetSpectrumReferenceForPeptideUID() {
        // get the first peptide
        Iterator<Identification> it = mascotDao.getIdentificationIterator(true);

        Identification firstIdentification = it.next();

        Peptide pep = firstIdentification.getPeptide().get(0);

        assertEquals(2116, mascotDao.getSpectrumReferenceForPeptideUID(pep.getUniqueIdentifier()));
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MascotDAO#getIdentificationIterator(boolean)}.
     */
    @Test
    public void testGetIdentificationIterator() {
        // get the iterator
        Properties props = new Properties();
        props.setProperty("ignore_below_ions_score", "0.99");
        mascotDao.setConfiguration(props);
        Iterator<Identification> it = mascotDao.getIdentificationIterator(false);

        // just count the identifications as a beginning
        int nIds = 0;

        while (it.hasNext()) {
            Identification id = it.next();

            // identifications may be null
            if (id == null)
                continue;

            // make sure the identification contains peptides
            if (id.getPeptide().size() < 1)
                fail("Identification " + id.getAccession() + " does not contain any peptides");

            nIds++;
        }

        assertEquals(262, nIds); // 262 with ignore_below_score 0.99 | 294 hits with the default settings (grouping true, only significant true)
    }

    @Test
    public void testGetIdentificationIteratorAll() {
        // set the engineoptions
        Properties props = new Properties();
        props.setProperty("only_significant", "false");
        props.setProperty("ignore_below_ions_score", "0.99");
        mascotDao.setConfiguration(props);

        // get the iterator
        Iterator<Identification> it = mascotDao.getIdentificationIterator(true);

        // just count the identifications as a begining
        int nIds = 0;
        int nNonSignificant = 0;

        while (it.hasNext()) {
            Identification id = it.next();

            // identifications may be null
            if (id == null)
                fail("NULL returned for identification.");

            // make sure the identification contains peptides
            if (id.getPeptide().size() < 1)
                fail("Identification " + id.getAccession() + " does not contain any peptides");

            // fail if it's a non-significant hit
            for (CvParam param : id.getAdditional().getCvParam())
                if (param.getAccession().equals("PRIDE:0000301"))
                    nNonSignificant++;

            nIds++;
        }

        assertEquals(88, nNonSignificant); // 88 non-significant hits
        assertEquals(350, nIds); // 250 hits with a ignore_below_thresh of 0.99 | 432 hits including non-significant hits (same as the Mascot result)
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MascotDAO#jCvParam(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)}.
     */
    @Test
    public void testJCvParam() {
        uk.ac.ebi.pride.jaxb.model.CvParam param = MascotDAO.jCvParam("PRIDE", "PRIDE:000001", "Test param", "My value");

        assertEquals("PRIDE", param.getCvLabel());
        assertEquals("PRIDE:000001", param.getAccession());
        assertEquals("Test param", param.getName());
        assertEquals("My value", param.getValue());
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MascotDAO#getCvLookup()}.
     */
    @Test
    public void testgetCvLookup() {
        Collection<CV> lookups = mascotDao.getCvLookup();

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
    }

    @Test
    public void testGetSupportedProperties() {
        Collection<DAOProperty> properties = mascotDao.getSupportedProperties();

        assertEquals(12, properties.size());
    }

    @Test
    public void testGetIdentificationByUID() {
        // get the first protein
        Iterator<Identification> it = mascotDao.getIdentificationIterator(false);

        Identification firstIdentification = it.next();

        // get it again using the getIdentificationByUID function
        Identification againIdentification;
		try {
			againIdentification = mascotDao.getIdentificationByUID(firstIdentification.getUniqueIdentifier());
		

	        assertEquals(firstIdentification.getAccession(), againIdentification.getAccession());
	        assertEquals(firstIdentification.getPeptide().size(), againIdentification.getPeptide().size());
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
    }
}
