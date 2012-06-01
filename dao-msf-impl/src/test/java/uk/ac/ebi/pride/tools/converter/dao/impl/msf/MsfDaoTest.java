/**
 *
 */
package uk.ac.ebi.pride.tools.converter.dao.impl.msf;

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
public class MsfDaoTest extends TestCase {

    private static MsfDao msfDao;
    private static File sourceFile;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        if (msfDao != null) {
            return;
        }

        /*
         * IMPORTANT: this test case needs to reload the mascot file every time.
         */
        // create the mascot dao
        try {
            URL testFile = getClass().getClassLoader().getResource("protmix-cid.msf");
            assertNotNull("Error loading msf test file", testFile);
            sourceFile = new File(testFile.toURI());
            msfDao = new MsfDao(sourceFile);
        } catch (ConverterException ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MsfDao#getExperimentTitle()}.
     */
    @Test
    public void testGetExperimentTitle() {
        try {
            String title;
            try {
                title = msfDao.getExperimentTitle();
                //assertEquals("2780 PRIDE exepriment", title);
                assertEquals("", title);
            } catch (InvalidFormatException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        } catch (IllegalStateException ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MsfDao#getExperimentShortLabel()}.
     */
    @Test
    public void testGetExperimentShortLabel() {
        // test the function - always returns null
        assertEquals("OR2_20120319_AP_LIM_cas_H2O_4h_02", msfDao.getExperimentShortLabel());
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MsfDao#getExperimentParams()}.
     */
    @Test
    public void testGetExperimentParams() {
        /**
         * These parameters should contain: - the version of the used DAO(?) -
         * date of search (PRIDE:0000219 - Date of search) - PRIDE:0000218 -
         * Original MS data file format
         */
        // get the parameters
        Param param = msfDao.getExperimentParams();

        // make sure there are 2 cvParams and 0 userParams
        assertEquals(2, param.getCvParam().size());
        assertEquals(0, param.getUserParam().size());

        boolean searchDate = false;
        boolean fileType = false;

        // test the params
        for (CvParam cv : param.getCvParam()) {
            // check the searchDate
            if (cv.getAccession().equals("PRIDE:0000219")) {
                searchDate = true;

                assertEquals("Date of search", cv.getName());

//                Date date = new Date(1335519436688L);
//                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//
//                assertEquals(formatter.format(date), cv.getValue()); // Warning: this result may change due to different locales (always uses the current one)
            }

            // check the file type
            if (cv.getAccession().equals("PRIDE:0000218")) {
                fileType = true;

                assertEquals("Original MS data file format", cv.getName());
                assertEquals("Proteome Discoverer .msf file", cv.getValue());
            }
        }

        // make sure both parameters were there
        assertEquals(true, searchDate);
        assertEquals(true, fileType);
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MsfDao#getSampleName()}.
     */
    @Test
    public void testGetSampleName() {
        assertEquals(null, msfDao.getSampleName());
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MsfDao#getSampleComment()}.
     */
    @Test
    public void testGetSampleComment() {
        assertEquals(null, msfDao.getSampleComment());
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MsfDao#getSampleParams()}.
     */
    @Test
    public void testGetSampleParams() {
        Param sampleParams = msfDao.getSampleParams();
        assertEquals(0, sampleParams.getUserParam().size());
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MsfDao#getSourceFile()}.
     */
    @Test
    public void testGetSourceFile() {
        SourceFile file = msfDao.getSourceFile();

        assertEquals(sourceFile.getAbsolutePath(), file.getPathToFile());
        assertEquals("protmix-cid.msf", file.getNameOfFile());
        assertEquals(MsfDao.MSF_FILE_STRING, file.getFileType());
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MsfDao#getContacts()}.
     */
    @Test
    public void testGetContacts() {
        Collection<Contact> contacts = msfDao.getContacts();

        //No contacts are returned
        assertEquals(0, contacts.size());
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MsfDao#getInstrument()}.
     */
    @Test
    public void testGetInstrument() {
        assertEquals("", msfDao.getInstrument().getInstrumentName());
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MsfDao#getSoftware()}.
     */
    @Test
    public void testGetSoftware() {
        Software software = msfDao.getSoftware();

        assertEquals("Proteome Discoverer", software.getName());
        assertEquals("1.3.0.339", software.getVersion());
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MsfDao#getProcessingMethod()}.
     */
    @Test
    public void testGetProcessingMethod() {
        //assertEquals("Param{cvParam=[CvParam{cvLabel='MS', accession='MS:1001618', name='ProteomeDiscoverer:Spectrum Selector:Unrecognized Mass Analyzer Replacements', value='Is#FourierTransform'}, CvParam{cvLabel='MS', accession='MS:1001616', name='ProteomeDiscoverer:Spectrum Selector:Unrecognized Activation Type Replacements', value='Is#HCD'}, CvParam{cvLabel='MS', accession='MS:1001622', name='ProteomeDiscoverer:Non-Fragment Filter:Mass Window Offset', value='100 Da'}, CvParam{cvLabel='MS', accession='MS:1001207', name='Mascot', value='VERSION'}, CvParam{cvLabel='MS', accession='MS:1001655', name='ProteomeDiscoverer:Fragment Mass Tolerance', value='0.05 Da'}, CvParam{cvLabel='MS', accession='MS:1001743', name='ProteomeDiscoverer:Mascot:Weight of A Ions', value='False'}, CvParam{cvLabel='MS', accession='MS:1001745', name='ProteomeDiscoverer:Mascot:Weight of C Ions', value='False'}, CvParam{cvLabel='MS', accession='MS:1001746', name='ProteomeDiscoverer:Mascot:Weight of D Ions', value='False'}, CvParam{cvLabel='MS', accession='MS:1001747', name='ProteomeDiscoverer:Mascot:Weight of V Ions', value='False'}, CvParam{cvLabel='MS', accession='MS:1001748', name='ProteomeDiscoverer:Mascot:Weight of W Ions', value='False'}, CvParam{cvLabel='MS', accession='MS:1001749', name='ProteomeDiscoverer:Mascot:Weight of X Ions', value='False'}, CvParam{cvLabel='MS', accession='MS:1001751', name='ProteomeDiscoverer:Mascot:Weight of Z Ions', value='False'}, CvParam{cvLabel='MS', accession='MS:1001661', name='ProteomeDiscoverer:Protein Database', value='BSA'}, CvParam{cvLabel='MS', accession='MS:1001654', name='ProteomeDiscoverer:Enzyme Name', value='Trypsin'}, CvParam{cvLabel='MS', accession='MS:1001657', name='ProteomeDiscoverer:Maximum Missed Cleavage Sites', value='2'}, CvParam{cvLabel='MS', accession='MS:1001665', name='ProteomeDiscoverer:Mascot:Taxonomy', value='All entries'}, CvParam{cvLabel='MS', accession='MS:1001647', name='ProteomeDiscoverer:Mascot:Error tolerant Search', value='False'}, CvParam{cvLabel='MS', accession='MS:1001681', name='ProteomeDiscoverer:Protein Relevance Threshold', value='20'}, CvParam{cvLabel='MS', accession='MS:1001662', name='ProteomeDiscoverer:Mascot:Protein Relevance Factor', value='1'}, CvParam{cvLabel='MS', accession='MS:1001649', name='ProteomeDiscoverer:Mascot:Mascot Server URL', value='http://mascotinternal.chem.uu.nl/mascot/'}, CvParam{cvLabel='MS', accession='MS:1001650', name='ProteomeDiscoverer:Mascot:Number of attempts to submit the search', value='20'}, CvParam{cvLabel='MS', accession='MS:1001652', name='ProteomeDiscoverer:Mascot:User Name', value='matheronl'}, CvParam{cvLabel='MS', accession='MS:1001652', name='ProteomeDiscoverer:Mascot:User Name', value='matheronl'}, CvParam{cvLabel='MS', accession='MS:1001659', name='ProteomeDiscoverer:Precursor Mass Tolerance', value='50 ppm'}, CvParam{cvLabel='MS', accession='MS:1001666', name='ProteomeDiscoverer:Use Average Precursor Mass', value='False'}, CvParam{cvLabel='MS', accession='MS:1001720', name='ProteomeDiscoverer:1. Dynamic Modification', value='Oxidation (M)'}, CvParam{cvLabel='MS', accession='MS:1001721', name='ProteomeDiscoverer:2. Dynamic Modification', value='Phospho_STY (STY)'}, CvParam{cvLabel='MS', accession='MS:1001682', name='ProteomeDiscoverer:Search Against Decoy Database', value='False'}, CvParam{cvLabel='MS', accession='MS:1001744', name='ProteomeDiscoverer:Mascot:Weight of B Ions', value='True'}, CvParam{cvLabel='MS', accession='MS:1001750', name='ProteomeDiscoverer:Mascot:Weight of Y Ions', value='True'}, CvParam{cvLabel='MS', accession='MS:1001720', name='ProteomeDiscoverer:1. Dynamic Modification', value='11#43'}, CvParam{cvLabel='MS', accession='MS:1001721', name='ProteomeDiscoverer:2. Dynamic Modification', value='16#17#20#705'}, CvParam{cvLabel='MS', accession='MS:1001651', name='ProteomeDiscoverer:Mascot:X Static Modification', value='77#706'}, CvParam{cvLabel='MS', accession='MS:1001618', name='ProteomeDiscoverer:Spectrum Selector:Unrecognized Mass Analyzer Replacements', value='Is#FourierTransform'}, CvParam{cvLabel='MS', accession='MS:1001616', name='ProteomeDiscoverer:Spectrum Selector:Unrecognized Activation Type Replacements', value='Is#ETD'}, CvParam{cvLabel='MS', accession='MS:1001628', name='ProteomeDiscoverer:Non-Fragment Filter:Remove Precursor Peak', value='True'}, CvParam{cvLabel='MS', accession='MS:1001622', name='ProteomeDiscoverer:Non-Fragment Filter:Mass Window Offset', value='4 Da'}, CvParam{cvLabel='MS', accession='MS:1001622', name='ProteomeDiscoverer:Non-Fragment Filter:Mass Window Offset', value='2 Da'}, CvParam{cvLabel='MS', accession='MS:1001625', name='ProteomeDiscoverer:Non-Fragment Filter:Remove Neutral Loss Peaks', value='True'}, CvParam{cvLabel='MS', accession='MS:1001622', name='ProteomeDiscoverer:Non-Fragment Filter:Mass Window Offset', value='2 Da'}, CvParam{cvLabel='MS', accession='MS:1001626', name='ProteomeDiscoverer:Non-Fragment Filter:Remove Only Known Masses', value='False'}, CvParam{cvLabel='MS', accession='MS:1001623', name='ProteomeDiscoverer:Non-Fragment Filter:Maximum Neutral Loss Mass', value='120 Da'}, CvParam{cvLabel='MS', accession='MS:1001627', name='ProteomeDiscoverer:Non-Fragment Filter:Remove Precursor Overtones', value='False'}, CvParam{cvLabel='MS', accession='MS:1001622', name='ProteomeDiscoverer:Non-Fragment Filter:Mass Window Offset', value='0.5 Da'}, CvParam{cvLabel='MS', accession='MS:1001622', name='ProteomeDiscoverer:Non-Fragment Filter:Mass Window Offset', value='100 Da'}, CvParam{cvLabel='MS', accession='MS:1001207', name='Mascot', value='VERSION'}, CvParam{cvLabel='MS', accession='MS:1001655', name='ProteomeDiscoverer:Fragment Mass Tolerance', value='0.05 Da'}, CvParam{cvLabel='MS', accession='MS:1001743', name='ProteomeDiscoverer:Mascot:Weight of A Ions', value='False'}, CvParam{cvLabel='MS', accession='MS:1001744', name='ProteomeDiscoverer:Mascot:Weight of B Ions', value='True'}, CvParam{cvLabel='MS', accession='MS:1001745', name='ProteomeDiscoverer:Mascot:Weight of C Ions', value='False'}, CvParam{cvLabel='MS', accession='MS:1001746', name='ProteomeDiscoverer:Mascot:Weight of D Ions', value='False'}, CvParam{cvLabel='MS', accession='MS:1001747', name='ProteomeDiscoverer:Mascot:Weight of V Ions', value='False'}, CvParam{cvLabel='MS', accession='MS:1001748', name='ProteomeDiscoverer:Mascot:Weight of W Ions', value='False'}, CvParam{cvLabel='MS', accession='MS:1001749', name='ProteomeDiscoverer:Mascot:Weight of X Ions', value='False'}, CvParam{cvLabel='MS', accession='MS:1001750', name='ProteomeDiscoverer:Mascot:Weight of Y Ions', value='True'}, CvParam{cvLabel='MS', accession='MS:1001751', name='ProteomeDiscoverer:Mascot:Weight of Z Ions', value='False'}, CvParam{cvLabel='MS', accession='MS:1001661', name='ProteomeDiscoverer:Protein Database', value='BSA'}, CvParam{cvLabel='MS', accession='MS:1001654', name='ProteomeDiscoverer:Enzyme Name', value='Trypsin'}, CvParam{cvLabel='MS', accession='MS:1001657', name='ProteomeDiscoverer:Maximum Missed Cleavage Sites', value='2'}, CvParam{cvLabel='MS', accession='MS:1001665', name='ProteomeDiscoverer:Mascot:Taxonomy', value='All entries'}, CvParam{cvLabel='MS', accession='MS:1001647', name='ProteomeDiscoverer:Mascot:Error tolerant Search', value='False'}, CvParam{cvLabel='MS', accession='MS:1001681', name='ProteomeDiscoverer:Protein Relevance Threshold', value='20'}, CvParam{cvLabel='MS', accession='MS:1001662', name='ProteomeDiscoverer:Mascot:Protein Relevance Factor', value='1'}, CvParam{cvLabel='MS', accession='MS:1001649', name='ProteomeDiscoverer:Mascot:Mascot Server URL', value='http://mascotinternal.chem.uu.nl/mascot/'}, CvParam{cvLabel='MS', accession='MS:1001650', name='ProteomeDiscoverer:Mascot:Number of attempts to submit the search', value='20'}, CvParam{cvLabel='MS', accession='MS:1001652', name='ProteomeDiscoverer:Mascot:User Name', value='matheronl'}, CvParam{cvLabel='MS', accession='MS:1001652', name='ProteomeDiscoverer:Mascot:User Name', value='matheronl'}, CvParam{cvLabel='MS', accession='MS:1001659', name='ProteomeDiscoverer:Precursor Mass Tolerance', value='50 ppm'}, CvParam{cvLabel='MS', accession='MS:1001666', name='ProteomeDiscoverer:Use Average Precursor Mass', value='False'}, CvParam{cvLabel='MS', accession='MS:1001720', name='ProteomeDiscoverer:1. Dynamic Modification', value='Oxidation (M)'}, CvParam{cvLabel='MS', accession='MS:1001721', name='ProteomeDiscoverer:2. Dynamic Modification', value='Phospho_STY (STY)'}, CvParam{cvLabel='MS', accession='MS:1001682', name='ProteomeDiscoverer:Search Against Decoy Database', value='False'}, CvParam{cvLabel='MS', accession='MS:1001682', name='ProteomeDiscoverer:Search Against Decoy Database', value='False'}, CvParam{cvLabel='MS', accession='MS:1001490', name='Percolator', value='VERSION'}, CvParam{cvLabel='MS', accession='MS:1001655', name='ProteomeDiscoverer:Fragment Mass Tolerance', value='0.5 Da'}, CvParam{cvLabel='MS', accession='MS:1001618', name='ProteomeDiscoverer:Spectrum Selector:Unrecognized Mass Analyzer Replacements', value='Is#IonTrap'}, CvParam{cvLabel='MS', accession='MS:1001616', name='ProteomeDiscoverer:Spectrum Selector:Unrecognized Activation Type Replacements', value='Is#ETD'}, CvParam{cvLabel='MS', accession='MS:1001628', name='ProteomeDiscoverer:Non-Fragment Filter:Remove Precursor Peak', value='True'}, CvParam{cvLabel='MS', accession='MS:1001622', name='ProteomeDiscoverer:Non-Fragment Filter:Mass Window Offset', value='4 Da'}, CvParam{cvLabel='MS', accession='MS:1001622', name='ProteomeDiscoverer:Non-Fragment Filter:Mass Window Offset', value='2 Da'}, CvParam{cvLabel='MS', accession='MS:1001625', name='ProteomeDiscoverer:Non-Fragment Filter:Remove Neutral Loss Peaks', value='True'}, CvParam{cvLabel='MS', accession='MS:1001622', name='ProteomeDiscoverer:Non-Fragment Filter:Mass Window Offset', value='2 Da'}, CvParam{cvLabel='MS', accession='MS:1001626', name='ProteomeDiscoverer:Non-Fragment Filter:Remove Only Known Masses', value='False'}, CvParam{cvLabel='MS', accession='MS:1001623', name='ProteomeDiscoverer:Non-Fragment Filter:Maximum Neutral Loss Mass', value='120 Da'}, CvParam{cvLabel='MS', accession='MS:1001627', name='ProteomeDiscoverer:Non-Fragment Filter:Remove Precursor Overtones', value='False'}, CvParam{cvLabel='MS', accession='MS:1001622', name='ProteomeDiscoverer:Non-Fragment Filter:Mass Window Offset', value='0.5 Da'}, CvParam{cvLabel='MS', accession='MS:1001622', name='ProteomeDiscoverer:Non-Fragment Filter:Mass Window Offset', value='100 Da'}, CvParam{cvLabel='MS', accession='MS:1001207', name='Mascot', value='VERSION'}, CvParam{cvLabel='MS', accession='MS:1001655', name='ProteomeDiscoverer:Fragment Mass Tolerance', value='0.6 Da'}, CvParam{cvLabel='MS', accession='MS:1001743', name='ProteomeDiscoverer:Mascot:Weight of A Ions', value='False'}, CvParam{cvLabel='MS', accession='MS:1001746', name='ProteomeDiscoverer:Mascot:Weight of D Ions', value='False'}, CvParam{cvLabel='MS', accession='MS:1001747', name='ProteomeDiscoverer:Mascot:Weight of V Ions', value='False'}, CvParam{cvLabel='MS', accession='MS:1001748', name='ProteomeDiscoverer:Mascot:Weight of W Ions', value='False'}, CvParam{cvLabel='MS', accession='MS:1001749', name='ProteomeDiscoverer:Mascot:Weight of X Ions', value='False'}, CvParam{cvLabel='MS', accession='MS:1001751', name='ProteomeDiscoverer:Mascot:Weight of Z Ions', value='False'}, CvParam{cvLabel='MS', accession='MS:1001661', name='ProteomeDiscoverer:Protein Database', value='BSA'}, CvParam{cvLabel='MS', accession='MS:1001654', name='ProteomeDiscoverer:Enzyme Name', value='Trypsin'}, CvParam{cvLabel='MS', accession='MS:1001657', name='ProteomeDiscoverer:Maximum Missed Cleavage Sites', value='2'}, CvParam{cvLabel='MS', accession='MS:1001665', name='ProteomeDiscoverer:Mascot:Taxonomy', value='All entries'}, CvParam{cvLabel='MS', accession='MS:1001647', name='ProteomeDiscoverer:Mascot:Error tolerant Search', value='False'}, CvParam{cvLabel='MS', accession='MS:1001681', name='ProteomeDiscoverer:Protein Relevance Threshold', value='20'}, CvParam{cvLabel='MS', accession='MS:1001662', name='ProteomeDiscoverer:Mascot:Protein Relevance Factor', value='1'}, CvParam{cvLabel='MS', accession='MS:1001649', name='ProteomeDiscoverer:Mascot:Mascot Server URL', value='http://mascotinternal.chem.uu.nl/mascot/'}, CvParam{cvLabel='MS', accession='MS:1001650', name='ProteomeDiscoverer:Mascot:Number of attempts to submit the search', value='20'}, CvParam{cvLabel='MS', accession='MS:1001652', name='ProteomeDiscoverer:Mascot:User Name', value='matheronl'}, CvParam{cvLabel='MS', accession='MS:1001652', name='ProteomeDiscoverer:Mascot:User Name', value='matheronl'}, CvParam{cvLabel='MS', accession='MS:1001659', name='ProteomeDiscoverer:Precursor Mass Tolerance', value='50 ppm'}, CvParam{cvLabel='MS', accession='MS:1001666', name='ProteomeDiscoverer:Use Average Precursor Mass', value='False'}, CvParam{cvLabel='MS', accession='MS:1001720', name='ProteomeDiscoverer:1. Dynamic Modification', value='Oxidation (M)'}, CvParam{cvLabel='MS', accession='MS:1001721', name='ProteomeDiscoverer:2. Dynamic Modification', value='Phospho_STY (STY)'}, CvParam{cvLabel='MS', accession='MS:1001682', name='ProteomeDiscoverer:Search Against Decoy Database', value='False'}, CvParam{cvLabel='MS', accession='MS:1001744', name='ProteomeDiscoverer:Mascot:Weight of B Ions', value='False'}, CvParam{cvLabel='MS', accession='MS:1001750', name='ProteomeDiscoverer:Mascot:Weight of Y Ions', value='True'}, CvParam{cvLabel='MS', accession='MS:1001745', name='ProteomeDiscoverer:Mascot:Weight of C Ions', value='True'}, CvParam{cvLabel='MS', accession='MS:1001720', name='ProteomeDiscoverer:1. Dynamic Modification', value='11#43'}, CvParam{cvLabel='MS', accession='MS:1001721', name='ProteomeDiscoverer:2. Dynamic Modification', value='16#17#20#705'}, CvParam{cvLabel='MS', accession='MS:1001651', name='ProteomeDiscoverer:Mascot:X Static Modification', value='77#706'}], userParam=null}", msfDao.getProcessingMethod().toString());
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MsfDao#getSearchDatabaseName()}.
     */
    @Test
    public void testGetSearchDatabaseName() {
        assertEquals("Mascot5_BSA_All entries", msfDao.getSearchDatabaseName()); 
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MsfDao#getSearchDatabaseVersion()}.
     */
    @Test
    public void testGetSearchDatabaseVersion() {
        assertEquals("Mascot5_BSA_All entries", msfDao.getSearchDatabaseVersion());
    }

    @Test
    public void testGetDatabaseMappings() {
        Collection<DatabaseMapping> mappings = msfDao.getDatabaseMappings();

        assertEquals(1, mappings.size());

        for (DatabaseMapping mapping : mappings) {
            assertEquals("Mascot5_BSA_All entries", mapping.getSearchEngineDatabaseName());
            assertEquals("Mascot5_BSA_All entries", mapping.getSearchEngineDatabaseVersion());
        }
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MsfDao#getPTMs()}.
     */
    @Test
    public void testGetPTMs() {
        Collection<PTM> ptms = msfDao.getPTMs();

        boolean fixed = false, var = false;
        int count = 0;

        for (PTM ptm : ptms) {
            // There are one fixed, and two variable modifications

            if (ptm.isFixedModification()) {
                fixed = true;
                assertEquals("Carbamidomethyl (C)", ptm.getSearchEnginePTMLabel());
                assertEquals("C", ptm.getResidues());
            } else {
                var = true;
                if (count++ == 1) {
                    assertEquals("Oxidation (M)", ptm.getSearchEnginePTMLabel());
                    assertEquals("M", ptm.getResidues());
                } else if (count == 3){
                    assertEquals("phospho_beta_elim (ST)", ptm.getSearchEnginePTMLabel());
                    assertEquals("ST", ptm.getResidues());
                } else if (count == 2) {
                    assertEquals("phospho (Y)", ptm.getSearchEnginePTMLabel());
                    assertEquals("Y", ptm.getResidues());
                }
            }
        }

        // Check number of modifications
        assertEquals(4, ptms.size());

        // make sure both modifications were found
        assertEquals(true, fixed);
        assertEquals(true, var);
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MsfDao#getSearchResultIdentifier()}.
     */
    @Test
    public void testGetSearchResultIdentifier() {
        // get the identifier
        SearchResultIdentifier identifier = msfDao.getSearchResultIdentifier();

        assertEquals("e4530035f72535ab89f6bc5da240d549", identifier.getHash());
        assertEquals(sourceFile.getAbsolutePath(), identifier.getSourceFilePath());
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MsfDao#getSpectrumIterator(boolean)}.
     */
    @Test
    public void testGetSpectrumIterator() {
        Iterator<Spectrum> spectra = msfDao.getSpectrumIterator(false);

        int nSpectra = 0;

        while (spectra.hasNext()) {
            Spectrum s = spectra.next();

            if (s == null) {
                fail("Spectrum could not be retrieved");
            }

            nSpectra++;
        }

        assertEquals(1505, nSpectra);
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MsfDao#getSpectrumIterator(boolean)}.
     */
    @Test
    public void testGetSpectrumIteratorOnlyIdentified() {
        Iterator<Spectrum> spectra = msfDao.getSpectrumIterator(true);

        // make sure no spectrum is returned twice
        HashSet<Integer> specIds = new HashSet<Integer>();

        int nSpectra = 0;

        // check the intensities and masses of the first spectrum
        byte[] intensities = null;
        byte[] masses = null;

        while (spectra.hasNext()) {
            Spectrum s = spectra.next();

            if (s == null) {
                fail("Spectrum could not be retrieved");
            }

            if (specIds.contains(s.getId())) {
                fail("Spectrum returned twice " + s.getId() );
            }

            specIds.add(s.getId());

            // get the masses and intensities of the 1st spectrum
            if (s.getId() == 1102) {
                intensities = s.getIntenArrayBinary().getData().getValue();
                masses = s.getMzArrayBinary().getData().getValue();
            }

            nSpectra++;
        }

        // check the number of spectra
        assertEquals(244, nSpectra);

        // convert the intensBytes to a byte buffer
        ByteBuffer intensBuffer = ByteBuffer.wrap(intensities);
        ByteBuffer mzBuffer = ByteBuffer.wrap(masses);

        intensBuffer.order(ByteOrder.LITTLE_ENDIAN);
        mzBuffer.order(ByteOrder.LITTLE_ENDIAN);

        // check a number
        assertEquals(188.97409, mzBuffer.getDouble(0), 0.0000001);
        assertEquals(8.135604, intensBuffer.getDouble(0), 0.0000001);

    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MsfDao#getSpectrumCount(boolean)
     */
    @Test
    public void testGetSpectrumCount() {
        assertEquals(1505, msfDao.getSpectrumCount(false));
        assertEquals(757, msfDao.getSpectrumCount(true));
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MsfDao#getSpectrumReferenceForPeptideUID(java.lang.String)}.
     */
    @Test
    public void testGetSpectrumReferenceForPeptideUID() {
        // get the first peptide
        Iterator<Identification> it = msfDao.getIdentificationIterator(true);

        Identification firstIdentification = it.next();

        Peptide pep = firstIdentification.getPeptide().get(0);

        assertEquals(562, msfDao.getSpectrumReferenceForPeptideUID(pep.getUniqueIdentifier()));
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MsfDao#getIdentificationIterator(boolean)}.
     */
    @Test
    public void testGetIdentificationIterator() {

        Iterator<Identification> it = msfDao.getIdentificationIterator(false);

        // just count the identifications as a beginning
        int nIds = 0;

        while (it.hasNext()) {
            Identification id = it.next();

            // identifications may be null
            if (id == null) {
                continue;
            }

            // make sure the identification contains peptides
            if (id.getPeptide().size() < 1) {
                fail("Identification " + id.getAccession() + " does not contain any peptides");
            }

            nIds++;
        }

        assertEquals(16, nIds);
    }

    @Test
    public void testGetIdentificationIteratorAll() {
        // get the iterator
        Iterator<Identification> it = msfDao.getIdentificationIterator(true);

        // just count the identifications as a begining
        int nIds = 0;
        int nNonSignificant = 0;

        while (it.hasNext()) {
            Identification id = it.next();

            // identifications may be null
            if (id == null) {
                fail("NULL returned for identification.");
            }

            // make sure the identification contains peptides
            if (id.getPeptide().size() < 1) {
                fail("Identification " + id.getAccession() + " does not contain any peptides");
            }

            // fail if it's a non-significant hit
            for (CvParam param : id.getAdditional().getCvParam()) {
                if (param.getAccession().equals("PRIDE:0000301")) {
                    nNonSignificant++;
                }
            }

            nIds++;
        }

        assertEquals(16, nIds); // 250 hits with a ignore_below_thresh of 0.99 | 432 hits including non-significant hits (same as the Mascot result)
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MsfDao#jCvParam(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)}.
     */
    @Test
    public void testJCvParam() {
        uk.ac.ebi.pride.jaxb.model.CvParam param = MsfDao.jCvParam("PRIDE", "PRIDE:000001", "Test param", "My value");

        assertEquals("PRIDE", param.getCvLabel());
        assertEquals("PRIDE:000001", param.getAccession());
        assertEquals("Test param", param.getName());
        assertEquals("My value", param.getValue());
    }

    /**
     * Test method for {@link uk.ac.ebi.pride.tools.converter.dao.impl.MsfDao#getCvLookup()}.
     */
    @Test
    public void testgetCvLookup() {
        Collection<CV> lookups = msfDao.getCvLookup();

        assertEquals(2, lookups.size());

        int nCount = 0;

        for (CV cv : lookups) {

            if (nCount == 0) {
                assertEquals("MS", cv.getCvLabel());
                assertEquals("PSI Mass Spectrometry Ontology", cv.getFullName());
                assertEquals("3.20.0", cv.getVersion());
                assertEquals("http://psidev.cvs.sourceforge.net/viewvc/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo", cv.getAddress());
            }

            nCount++;
        }
    }

    @Test
    public void testGetSupportedProperties() {
        Collection<DAOProperty> properties = msfDao.getSupportedProperties();

        assertEquals(1, properties.size());
    }

    @Test
    public void testGetIdentificationByUID() {
        // get the first protein
        Iterator<Identification> it = msfDao.getIdentificationIterator(false);

        Identification firstIdentification = it.next();

        // get it again using the getIdentificationByUID function
        Identification againIdentification;

        againIdentification = msfDao.getIdentificationByUID(firstIdentification.getUniqueIdentifier());

        assertEquals(firstIdentification.getAccession(), againIdentification.getAccession());
        assertEquals(firstIdentification.getPeptide().size(), againIdentification.getPeptide().size());

    }
}
