package uk.ac.ebi.pride.tools.converter.dao.handler.impl;

import java.net.URL;
import java.util.Properties;

import junit.framework.TestCase;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.dao.handler.QuantitationCvParams;
import uk.ac.ebi.pride.tools.converter.report.model.Identification;
import uk.ac.ebi.pride.tools.converter.report.model.Param;

public class MzTabHandlerTest extends TestCase {
	private MzTabHandler handler;

	public void setUp() throws Exception {
		URL testFile = getClass().getClassLoader().getResource("mztab_handler_test.txt");
		
		assertNotNull(testFile);
		
		handler = new MzTabHandler(testFile.getPath());
	}

	public void testUpdateIdentification() {
		// create a test identification
		Identification id = new Identification();
		id.setAccession("gi|155555");
		id.setDatabase("NCBI gi");
		
		Param additional = new Param();
		additional.getCvParam().add(DAOCvParams.INDISTINGUISHABLE_ACCESSION.getParam("gi|10181184"));
		id.setAdditional(additional);
		
		handler.updateIdentification(id);
		
		// make sure the accession was changed
		assertEquals("gi|10181184", id.getAccession());
		
		// check the gel data
		assertNull(id.getGelBasedData());
		assertEquals(13, id.getAdditional().getCvParam().size());
		assertEquals("[CvParam{cvLabel='PRIDE', accession='PRIDE:0000098', name='Indistinguishable alternative protein accession', value='gi|155555'}, CvParam{cvLabel='PRIDE', accession='PRIDE:0000354', name='Intensity subsample 1', value='1.0'}, CvParam{cvLabel='PRIDE', accession='PRIDE:0000355', name='Intensity subsample 2', value='7265.31988'}, CvParam{cvLabel='PRIDE', accession='PRIDE:0000376', name='Standard deviation subsample 2', value='6346.894427'}, CvParam{cvLabel='PRIDE', accession='PRIDE:0000356', name='Intensity subsample 3', value='7265.31988'}, CvParam{cvLabel='PRIDE', accession='PRIDE:0000377', name='Standard deviation subsample 3', value='6346.894427'}, CvParam{cvLabel='PRIDE', accession='PRIDE:0000357', name='Intensity subsample 4', value='7265.31988'}, CvParam{cvLabel='PRIDE', accession='PRIDE:0000378', name='Standard deviation subsample 4', value='6346.894427'}, CvParam{cvLabel='PRIDE', accession='PRIDE:0000313', name='iTRAQ', value=''}, CvParam{cvLabel='PRIDE', accession='PRIDE:0000395', name='Ratio', value=''}, CvParam{cvLabel='PRIDE', accession='PRIDE:0000300', name='Gel spot identifier', value='gel1'}, CvParam{cvLabel='PRIDE', accession='PRIDE:0000304', name='Gel identifier', value='A1'}, CvParam{cvLabel='PRIDE', accession='PRIDE:0000363', name='emPAI value', value='1.254'}]", id.getAdditional().getCvParam().toString());
	}

	public void testGetSampleDescriptionParams() {
		Param sampleDescription = handler.getSampleDescriptionParams();
		
		assertNotNull(sampleDescription);
		assertEquals(9, sampleDescription.getCvParam().size());
		assertEquals("[CvParam{cvLabel='PRIDE', accession='PRIDE:0000366', name='Contains multiple subsamples', value=''}, CvParam{cvLabel='PRIDE', accession='PRIDE:0000367', name='Subample 1 description', value='Healthy human liver tissue'}, CvParam{cvLabel='PRIDE', accession='PRIDE:0000114', name='iTRAQ reagent 114', value='subsample1'}, CvParam{cvLabel='PRIDE', accession='PRIDE:0000368', name='Subample 2 description', value='Human hepatocellular carcinoma sample.'}, CvParam{cvLabel='PRIDE', accession='PRIDE:0000115', name='iTRAQ reagent 115', value='subsample2'}, CvParam{cvLabel='PRIDE', accession='PRIDE:0000369', name='Subample 3 description', value='Heptitis C infected liver tissue'}, CvParam{cvLabel='PRIDE', accession='PRIDE:0000116', name='iTRAQ reagent 116', value='subsample3'}, CvParam{cvLabel='PRIDE', accession='PRIDE:0000370', name='Subample 4 description', value='Fatty liver degeneration'}, CvParam{cvLabel='PRIDE', accession='PRIDE:0000117', name='iTRAQ reagent 117', value='subsample4'}]", sampleDescription.getCvParam().toString());
		
		assertEquals(2, sampleDescription.getUserParam().size());
		assertEquals("[UserParam{name='Available protein quantitation fields', value='PRIDE:0000354,PRIDE:0000355,PRIDE:0000356,PRIDE:0000357'}, UserParam{name='Available peptide quantitation fields', value='PRIDE:0000354,PRIDE:0000355,PRIDE:0000356,PRIDE:0000357'}]", sampleDescription.getUserParam().toString());
	}

	public void testGetExperimentParams() {
		Param param = handler.getExperimentParams();
		
		assertEquals(3, param.getCvParam().size());
		assertEquals(QuantitationCvParams.ITRAQ_QUANTIFIED.getAccession(), param.getCvParam().get(0).getAccession());
		assertEquals(DAOCvParams.GEL_BASED_EXPERIMENT.getAccession(), param.getCvParam().get(1).getAccession());
		assertEquals(QuantitationCvParams.EMPAI_QUANTIFIED.getAccession(), param.getCvParam().get(2).getAccession());
	}

	public void testGetDaoConfiguration() {
		Properties properties = handler.getDaoConfiguration();
		
		assertNotNull(properties);
		assertEquals(15, properties.size());
		assertEquals("true", properties.getProperty("enable_protein_grouping"));
		assertEquals("10.0", properties.getProperty("ignore_below_ions_score"));
	}
}
