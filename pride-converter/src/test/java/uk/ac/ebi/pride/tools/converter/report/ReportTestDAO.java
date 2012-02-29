package uk.ac.ebi.pride.tools.converter.report;

import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.dao.DAO;
import uk.ac.ebi.pride.tools.converter.report.model.*;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 06/01/11
 * Time: 16:57
 * To change this template use File | Settings | File Templates.
 */
public class ReportTestDAO implements DAO {

    public String getExperimentTitle() {
        return "COFRADIC methionine proteome of unstimulated human blood platelets";
    }

    public String getExperimentShortLabel() {
        return "Platelets MetOx";  //To change body of implemented methods use File | Settings | File Templates.
    }
    
    @Override
	public void setExternalSpectrumFile(String filename) {
		// not applicable
	}

	public Param getExperimentParams() {
        Param p = new Param();
        p.getCvParam().add(new CvParam("PRIDE", "PRIDE:0000097", "Project", "COFRADIC proteome of unstimulated human blood platelets"));
        p.getUserParam().add(new UserParam("COFRADICType", "MethionineCOFRADIC"));
        return p;
    }

    public String getSampleName() {
        return "Unstimulated human blood platelets";
    }

    public String getSampleComment() {
        return "Unstimulated human blood platelets";
    }

    public Param getSampleParams() {
        Param p = new Param();
        p.getCvParam().add(new CvParam("CL", "CL:0000233", "platelet", null));
        p.getCvParam().add(new CvParam("NEWT", "9606", "Homo Sapiens", null));
        p.getUserParam().add(new UserParam("MeSH", "blood_platelets"));
        return p;
    }

    public SourceFile getSourceFile() {
        SourceFile s = new SourceFile();
        s.setFileType("XML");
        s.setNameOfFile("testFile1.xml");
        s.setPathToFile("http://www.corp.edu/quae/ventos");
        return s;
    }

    public Collection<Contact> getContacts() {
        ArrayList<Contact> c = new ArrayList<Contact>();
        c.add(new Contact("Lennart Martens", "Department of Medical Protein Research (GE07, VIB09), Faculty of Medicine and Health Sciences, Ghent University and Flanders Interuniversitary Institute for Biotechnology (VIB)", "lennart.martens@UGent.be"));
        return c;
    }

    public InstrumentDescription getInstrument() {
        InstrumentDescription i = new InstrumentDescription();
        i.setInstrumentName("Micromass Q-TOF I");

        Param s = new Param();
        s.getCvParam().add(new CvParam("PSI-MS", "PSI:1000008", "Ionization Type", "ESI"));
        i.setSource(s);

        InstrumentDescription.AnalyzerList a = new InstrumentDescription.AnalyzerList();
        Param ap = new Param();
        ap.getCvParam().add(new CvParam("PSI-MS", "PSI:1000010", "Analyzer Type", "Quadrupole-TOF"));
        ap.getCvParam().add(new CvParam("PSI-MS", "PSI:1000014", "Accuracy", "0.3"));
        a.getAnalyzer().add(ap);
        a.setCount(a.getAnalyzer().size());
        i.setAnalyzerList(a);

        Param d = new Param();
        d.getCvParam().add(new CvParam("PSI-MS", "PSI:1000026", "Detector Type", "MultiChannelPlate"));
        i.setDetector(d);
        return i;
    }

    public Software getSoftware() {
        Software s = new Software();
        s.setName("MassLynx");
        s.setComments("N/A");
        s.setVersion("3.5");
        s.setCompletionTime("2018-11-01T05:36:46+00:00");
        return s;
    }

    public Param getProcessingMethod() {
        Param p = new Param();
        p.getCvParam().add(new CvParam("PSI-MS", "PSI:1000034", "Charge Deconvolution", "false"));
        p.getCvParam().add(new CvParam("PSI-MS", "PSI:1000035", "Peak Processing", "CentroidMassSpectrum"));
        return p;
    }

    public String getSearchDatabaseName() {
        return "UniProt Human";
    }

    public String getSearchDatabaseVersion() {
        return "105";
    }

    public Collection<PTM> getPTMs() {
        ArrayList<PTM> ptm = new ArrayList<PTM>();

        PTM p = new PTM();
        p.setFixedModification(false);
        p.setModAccession("MOD:00048");
        p.setModDatabase("PSI-MOD");
        p.setSearchEnginePTMLabel("#");
        p.getModAvgDelta().add("243.15");
        p.getModMonoDelta().add("243.029659");
        p.setAdditional(new Param());
        p.getAdditional().getCvParam().add(new CvParam("test", "test", "test", null));
        ptm.add(p);

        p = new PTM();
        p.setFixedModification(false);
        p.setModAccession("MOD:00684");
        p.setModDatabase("PSI-MOD");
        p.setSearchEnginePTMLabel("*");
        p.getModAvgDelta().add("115.09");
        p.getModMonoDelta().add("115.026943");
        p.setResidues("X");
        p.setAdditional(new Param());
        ptm.add(p);

        return ptm;
    }

    public SearchResultIdentifier getSearchResultIdentifier() {
        SearchResultIdentifier s = new SearchResultIdentifier();
        s.setHash("LSKJDFGHLSDFKJGHSDLFHLSKDJF");
        s.setSourceFilePath("/home/rcote/testfile.xml");
        s.setTimeCreated("2008-09-29T02:49:45");
        return s;
    }

    @Override
    public Collection<CV> getCvLookup() {
        List<CV> cv = new ArrayList<CV>();
        cv.add(new CV("DOID", "Human Disease", "1.0", "http://obo.cvs.sourceforge.net/obo/obo/ontology/phenotype/human_disease.obo"));
        cv.add(new CV("NEWT", "Uniprot Taxonomy", "1.0", "http://www.ebi.ac.uk/newt/"));
        return cv;
    }

    public Protocol getProtocol() {
        Protocol p = new Protocol();
        p.setProtocolName("Cofradic");
        Protocol.ProtocolSteps steps = new Protocol.ProtocolSteps();
        Param s1 = new Param();
        s1.getCvParam().add(new CvParam("PRIDE", "PRIDE:0001", "HPLC", null));
        s1.getUserParam().add(new UserParam("Step1", "Separation"));
        Param s2 = new Param();
        s2.getCvParam().add(new CvParam("PRIDE", "PRIDE:0002", "Tandem MS/MS", null));
        s2.getUserParam().add(new UserParam("Step2", "MS"));
        steps.getStepDescription().add(s1);
        steps.getStepDescription().add(s2);
        p.setProtocolSteps(steps);
        return p;
    }

    public Collection<Reference> getReferences() {
        ArrayList<Reference> r = new ArrayList<Reference>();
        Reference ref = new Reference("Martens L, Van Damme P, Van Damme J, Staes A, Timmerman E, Ghesquiere B, Thomas GR, Vandekerckhove J, Gevaert K (2005), ?The human platelet proteome mapped by peptide-centric proteomics: a functional protein profile?, Proteomics, 5, 3193-3204.",
                new Param());
        ref.getAdditional().getCvParam().add(new CvParam("PubMed", "16038019", "16038019", null));
        ref.getAdditional().getUserParam().add(new UserParam("DOI", "PMID/16038019"));
        r.add(ref);
        return r;
    }

    public Iterator<Identification> getIdentificationIterator(boolean prescanMode) {
        ArrayList<Identification> ids = new ArrayList<Identification>();
        Identification id = new Identification();
        id.setAccession("P29375");
        id.setAccessionVersion("1");
        id.setCuratedAccession("P29375");
        id.setDatabase("SWISSPROT");
        id.setDatabaseVersion("105");
        id.setScore(0.95);
        id.setSearchEngine("Mascot");
        id.setUniqueIdentifier("1");
        ids.add(id);
        return ids.iterator();
    }

    /**
     * the following methods aren't needed for the test DAO
     */

    public int getSpectrumReferenceForPeptideUID(String peptideUID) {
        return 0;
    }

    public void setConfiguration(Properties props) {
    }

    public Iterator<Spectrum> getSpectrumIterator(boolean includeAll) {
        return null;
    }

    @Override
    public Identification getIdentificationByUID(String identificationUID) {
        return null;
    }

    public int getSpectrumCount(boolean onlyIdentified) {
        return 0;
    }

    @Override
    public Properties getConfiguration() {
        Properties props = new Properties();
        props.setProperty("TestDAO", "true");
        props.setProperty("Foo", "Bar");
        props.setProperty("Baz", "Flubu!");
        return props;
    }

    @Override
    public Collection<DatabaseMapping> getDatabaseMappings() {
        Collection<DatabaseMapping> retval = new ArrayList<DatabaseMapping>();
        DatabaseMapping map = new DatabaseMapping();
        map.setSearchEngineDatabaseName("SWISSPROT");
        map.setSearchEngineDatabaseVersion("105");
        retval.add(map);
        return retval;
    }
}
