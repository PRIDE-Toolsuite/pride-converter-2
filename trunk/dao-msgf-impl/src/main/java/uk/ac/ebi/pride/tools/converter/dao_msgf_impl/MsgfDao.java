package uk.ac.ebi.pride.tools.converter.dao_msgf_impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.dao.DAO;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.dao.DAOProperty;
import uk.ac.ebi.pride.tools.converter.dao.DefaultPTMs;
import uk.ac.ebi.pride.tools.converter.dao.impl.AbstractDAOImpl;
import uk.ac.ebi.pride.tools.converter.dao.impl.AbstractPeakListDAO;
import uk.ac.ebi.pride.tools.converter.dao.impl.MzXmlDAO;
import uk.ac.ebi.pride.tools.converter.dao_msgf_impl.model.MsgfPeptide;
import uk.ac.ebi.pride.tools.converter.dao_msgf_impl.model.MsgfProtein;
import uk.ac.ebi.pride.tools.converter.report.model.CV;
import uk.ac.ebi.pride.tools.converter.report.model.Contact;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.DatabaseMapping;
import uk.ac.ebi.pride.tools.converter.report.model.Identification;
import uk.ac.ebi.pride.tools.converter.report.model.InstrumentDescription;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.Peptide;
import uk.ac.ebi.pride.tools.converter.report.model.PeptidePTM;
import uk.ac.ebi.pride.tools.converter.report.model.Protocol;
import uk.ac.ebi.pride.tools.converter.report.model.Reference;
import uk.ac.ebi.pride.tools.converter.report.model.SearchResultIdentifier;
import uk.ac.ebi.pride.tools.converter.report.model.Software;
import uk.ac.ebi.pride.tools.converter.report.model.SourceFile;
import uk.ac.ebi.pride.tools.converter.report.model.UserParam;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.FileUtils;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;

public class MsgfDao extends AbstractDAOImpl implements DAO {
	/**
	 * Logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(MsgfDao.class);
	/**
	 * The input MSF file.
	 */
	private final File sourcefile;
	/**
	 * The proteins found in the MSGF
	 */
	private Map<String, MsgfProtein> proteins;
	/**
	 * The mzXML files referenced in the MSGF file.
	 */
	private Set<String> mzxmlFiles;
	/**
	 * DAO used to parse the corresponding peak list
	 * file.
	 */
	private AbstractPeakListDAO peakListDao;
	/**
	 * Peptides found in the MSGF file.
	 */
	private int peptideCount = 0;
	/**
	 * The spectra ids in the peak list format
	 */
	private List<String> specIds;
	/**
	 * List of spectra ids that were identified
	 */
	private List<Integer> identifiedSpecIds = new ArrayList<Integer>();
	/**
	 * The search engine reported for the proteins
	 */
	private String searchEngine = "MSGF";
	/**
	 * Path where the mzXML files can be found
	 */
	private String mzxmlFolderPath = null;
	/**
	 * The maximum probability a peptide may have
	 * to still be reported in the PRIDE XML file.
	 */
	private Double peptideThreshold = 0.05;
	/**
	 * Indicates whether a carbamidomehtylation should be
	 * added to every C.
	 */
	private boolean addModCarbamidomethylation = false;
	
	private Properties properties;
	
	public MsgfDao(File sourcefile) {
		this.sourcefile = sourcefile;
		
		// parse the msf file
		parseMsgfFile();
	}
	
	/**
	 * Parses the MSGF file and stores
	 * the found proteins / peptides in
	 * the proteins Map as well as the 
	 * references mzXML files.
	 */
	private void parseMsgfFile() {
		if (sourcefile == null)
			throw new ConverterException("Input file was not set.");
		
		proteins = new LinkedHashMap<String, MsgfProtein>();
		mzxmlFiles = new HashSet<String>();
		
		// open the file
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sourcefile)));
			String line;
			Map<String, Integer> header = null;
			
			while ((line = br.readLine()) != null) {
				String[] fields = line.split("\t");
				
				// read the header if it wasn't read yet
				if (header == null) {
					header = new HashMap<String, Integer>();
					for (int i = 0; i < fields.length; i++)
						header.put(fields[i], i);
					
					continue;
				}
				
				// if the protein doesn't exist yet create it
				String accession = fields[header.get("Protein")];
				
				if (!proteins.containsKey(accession))
					proteins.put(accession, new MsgfProtein(accession));
				
				// process the peptide
				MsgfPeptide peptide = createMsgfPeptide(fields, header);
				if (peptide.getpValue() > peptideThreshold)
					continue;
				
				proteins.get(accession).addPeptide(peptide);
				peptideCount++;
				
				identifiedSpecIds.add(peptide.getScan());
				
				// add the mzXML file (name)				
				mzxmlFiles.add(fields[header.get("#SpectrumFile")]);
			}
			
			// make sure only one mzxmlFile is referenced
			if (mzxmlFiles.size() > 1)
				throw new ConverterException("The MSGF DAO only supports one referenced mzXML file per MSGF file.");
			
			br.close();
		} catch (FileNotFoundException e) {
			logger.error("Failed to open input file: " + e.getMessage());
			throw new ConverterException("Could not find input file.", e);
		} catch (IOException e) {
			logger.error("Failed to read from input file: " + e.getMessage());
			throw new ConverterException("Failed to read from input file.", e);
		} 
	}
	
	/**
	 * Creats the peak list dao to load the mzXML file.
	 */
	private void createPeakListDao() {
		if (peakListDao != null)
			return;
		
		// create the mzXML DAO
		String path = mzxmlFiles.iterator().next();
		
		File peakListFile = new File(path);
		
		if (!peakListFile.exists() && mzxmlFolderPath != null)
			peakListFile = new File(new File(mzxmlFolderPath).getAbsolutePath() + File.separator + peakListFile.getName());
		if (!peakListFile.exists())
			peakListFile = new File(sourcefile.getParent() + File.separator + peakListFile.getName());
		if (!peakListFile.exists())
			peakListFile = new File(peakListFile.getName());
		if (!peakListFile.exists())
			throw new ConverterException("Referenced peak list file '" + peakListFile.getName() + "' could not be found.");
		
		try {
			peakListDao = new MzXmlDAO(peakListFile);
			
			specIds = peakListDao.getSpectraIds();
		}
		catch (InvalidFormatException e) {
			logger.error("Failed to parse input peak list file: " + e.getMessage());
			throw new ConverterException("Failed to parse peak list file: " + e.getMessage());
		}
	}

	/**
	 * Creates a new MsgfPeptide object from the passed
	 * fields and header.
	 * @param fields The fields of the line representing the peptide.
	 * @param header A Map mapping a given column name to its 0-based index.
	 * @return The MsgfPeptide object representing the line.
	 */
	private MsgfPeptide createMsgfPeptide(String[] fields,
			Map<String, Integer> header) {
		
		// process the sequence
		String annotation = fields[header.get("Annotation")];
		String prevAA = annotation.substring(0, 1);
		String sequence = annotation.substring(2, annotation.length() - 2);
		String nextAA = annotation.substring(annotation.length() - 1);
		
		MsgfPeptide peptide = new MsgfPeptide(
				Integer.parseInt(fields[header.get("Scan#")]), 
				sequence, 
				prevAA, 
				nextAA, 
				Integer.parseInt(fields[header.get("Charge")]), 
				Double.parseDouble(fields[header.get("MQScore")]), 
				Double.parseDouble(fields[header.get("Length")]), 
				Double.parseDouble(fields[header.get("TotalPRMScore")]), 
				Double.parseDouble(fields[header.get("MedianPRMScore")]), 
				Double.parseDouble(fields[header.get("FractionY")]), 
				Double.parseDouble(fields[header.get("FractionB")]), 
				Double.parseDouble(fields[header.get("Intensity")]), 
				Integer.parseInt(fields[header.get("NTT")].replace(".0", "")), 
				Double.parseDouble(fields[header.get("p-value")]), 
				Double.parseDouble(fields[header.get("F-Score")]), 
				Double.parseDouble(fields[header.get("DeltaScore")]), 
				Double.parseDouble(fields[header.get("DeltaScoreOther")]), 
				Integer.parseInt(fields[header.get("RecordNumber")]), 
				Integer.parseInt(fields[header.get("DBFilePos")]), 
				Integer.parseInt(fields[header.get("SpecFilePos")]), 
				Double.parseDouble(fields[header.get("SpecProb")]));
		
		return peptide;
	}

	@SuppressWarnings("rawtypes")
	public static Collection<DAOProperty> getSupportedProperties() {
		List<DAOProperty> properties = new ArrayList<DAOProperty>();
		
		DAOProperty<Double> peptideThreshold = new DAOProperty<Double>("peptide_threshold", 0.05, 0.0, 0.1);
		peptideThreshold.setDescription("maximum p-value a peptide may have to still be reported in the PRIDE XML file.");
		peptideThreshold.setShortDescription("Maximum p-value for peptide hits to be reported.");
		properties.add(peptideThreshold);
		
		DAOProperty<String> searchEngine = new DAOProperty<String>("search_engine", "MSGF");
		searchEngine.setDescription("MSGF files do not contain the search engine used to identify a protein. This parameter sets the given search engine. Default value is \"MSGF\"");
		searchEngine.setShortDescription("Name of the original search engine used.");
		properties.add(searchEngine);
		
		DAOProperty<Boolean> addCarbamidomethylation = new DAOProperty<Boolean>("add_carbamidomethylation", false);
		addCarbamidomethylation.setDescription("MSGF files cannot report modifications. If this parameter is set to \"true\" a Carbamidomethylation is added to every C.");
		addCarbamidomethylation.setShortDescription("Add carabamidomethylation as static modification.");
		properties.add(addCarbamidomethylation);
		
		return properties;
    }
	
	public void setConfiguration(Properties props) {
		properties = props;
		
		searchEngine = properties.getProperty("search_engine", "MSGF");
		addModCarbamidomethylation = Boolean.parseBoolean( 
				properties.getProperty("add_carbamidomethylation", "false") );
		peptideThreshold = Double.parseDouble( properties.getProperty("peptide_threshold", "0.05") );
	}

	public Properties getConfiguration() {
		// no configuration supported
		return properties;
	}

    @Override
	public void setExternalSpectrumFile(String filename) {
    	mzxmlFolderPath = filename;
	}

	public String getExperimentTitle() throws InvalidFormatException {
		return "";
	}

	public String getExperimentShortLabel() {
		return null;
	}

	public Param getExperimentParams() throws InvalidFormatException {
		// initialize the collection to hold the params
        Param params = new Param();

        // original MS format param
        params.getCvParam().add(DAOCvParams.ORIGINAL_MS_FORMAT.getParam("MSGF file"));
       	params.getCvParam().add(DAOCvParams.MS_MS_SEARCH.getParam());

        return params;
	}

	public String getSampleName() {
		return null;
	}

	public String getSampleComment() {
		return null;
	}

	public Param getSampleParams() throws InvalidFormatException {
		return new Param();
	}

	public SourceFile getSourceFile() throws InvalidFormatException {
		// initialize the return variable
        SourceFile file = new SourceFile();

        file.setPathToFile(sourcefile.getAbsolutePath());
        file.setNameOfFile(sourcefile.getName());
        file.setFileType("MSGF file");

        return file;
	}

	public Collection<Contact> getContacts() {
		return null;
	}

	public InstrumentDescription getInstrument() {
		return null;
	}

	public Software getSoftware() throws InvalidFormatException {
		return new Software();
	}

	public Param getProcessingMethod() {
		return null;
	}

	public Protocol getProtocol() {
		return null;
	}

	public Collection<Reference> getReferences() {
		return null;
	}

	public String getSearchDatabaseName() throws InvalidFormatException {
		return "Unknown database";
	}

	public String getSearchDatabaseVersion() throws InvalidFormatException {
		return "Unknown";
	}

	public Collection<PTM> getPTMs() throws InvalidFormatException {
		List<PTM> ptms = new ArrayList<PTM>();
		
		if (addModCarbamidomethylation) {
			PTM ptm = new PTM();
			
			ptm.setSearchEnginePTMLabel("carbamidomethylation");
			ptm.setModAccession(DefaultPTMs.CARBAMIDOMETHYL.getAccession());
			ptm.setModDatabase(DefaultPTMs.CARBAMIDOMETHYL.getDatabase());
			ptm.setModDatabaseVersion(DefaultPTMs.CARBAMIDOMETHYL.getDatabaseVersion());
			ptm.getModMonoDelta().add(DefaultPTMs.CARBAMIDOMETHYL.getMonoDelta().toString());
			ptm.setResidues("C");
			
			Param additional = new Param();
			additional.getCvParam().add(new CvParam("PSI", 
					DefaultPTMs.CARBAMIDOMETHYL.getAccession(), 
					DefaultPTMs.CARBAMIDOMETHYL.getPreferredName(), 
					DefaultPTMs.CARBAMIDOMETHYL.getMonoDelta().toString()));
			ptm.setAdditional(additional);
			
			ptms.add(ptm);
		}
		
		return ptms;
	}

	public Collection<DatabaseMapping> getDatabaseMappings()
			throws InvalidFormatException {
		ArrayList<DatabaseMapping> mappings = new ArrayList<DatabaseMapping>(1);

		DatabaseMapping mapping = new DatabaseMapping();

		mapping.setSearchEngineDatabaseName("Unknown database");
		mapping.setSearchEngineDatabaseVersion("Unknown");

		mappings.add(mapping);

		return mappings;
	}

	public SearchResultIdentifier getSearchResultIdentifier()
			throws InvalidFormatException {
		// intialize the search result identifier
        SearchResultIdentifier identifier = new SearchResultIdentifier();

        // format the current time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        identifier.setSourceFilePath(sourcefile.getAbsolutePath());
        identifier.setTimeCreated(formatter.format(new Date(System.currentTimeMillis())));
        identifier.setHash(FileUtils.MD5Hash(sourcefile.getAbsolutePath()));

        return identifier;
	}

	public Collection<CV> getCvLookup() throws InvalidFormatException {
		// just create a set containing the 2 cvLookups used here
        ArrayList<CV> cvs = new ArrayList<CV>();

        cvs.add(new CV("MS", "PSI Mass Spectrometry Ontology", "1.2", "http://psidev.cvs.sourceforge.net/viewvc/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo"));
        cvs.add(new CV("PRIDE", "PRIDE Controlled Vocabulary", "1.101", "http://ebi-pride.googlecode.com/svn/trunk/pride-core/schema/pride_cv.obo"));

        return cvs;
	}

	public int getSpectrumCount(boolean onlyIdentified)
			throws InvalidFormatException {
		if (peakListDao == null)
			createPeakListDao();
		
		return onlyIdentified ? peptideCount : peakListDao.getSpectrumCount(false);
	}

	public Iterator<Spectrum> getSpectrumIterator(boolean onlyIdentified)
			throws InvalidFormatException {
		if (peakListDao == null)
			createPeakListDao();
		
		if (onlyIdentified)
			return new OnlyIdentifiedSpectrumIterator();
		
		return peakListDao.getSpectrumIterator(false);
	}

	public int getSpectrumReferenceForPeptideUID(String peptideUID)
			throws InvalidFormatException {
		return getSpecRefForScan(Integer.parseInt(peptideUID));
	}

	public Identification getIdentificationByUID(String identificationUID)
			throws InvalidFormatException {
		MsgfProtein protein = proteins.get(identificationUID);
		
		if (protein == null)
			throw new InvalidFormatException("Protein with UID=" + identificationUID + " does not exist");
		
		return convertIdentification(protein);
	}

	public Iterator<Identification> getIdentificationIterator(
			boolean prescanMode) throws InvalidFormatException {
		return new MsgfIdentificationIterator();
	}
	
	private class MsgfIdentificationIterator implements Iterator<Identification> {
		private final Iterator<String> accessionIterator = proteins.keySet().iterator();
		
		public boolean hasNext() {
			return accessionIterator.hasNext();
		}

		public Identification next() {
			return convertIdentification(proteins.get(accessionIterator.next()));
		}

		public void remove() {
			// not supported
		}
	}

	private Identification convertIdentification(MsgfProtein protein) {
		if (protein.getPeptides().size() < 1)
			return null;
		
		Identification identification = new Identification();
		
		identification.setAccession(protein.getAccession());
		identification.setScore(0.0);
		identification.setThreshold(0.0);
		identification.setDatabase("Unknown database");
		identification.setDatabaseVersion("Unknown");
		identification.setUniqueIdentifier(protein.getAccession());
		
		identification.setSearchEngine(searchEngine);
		
		// process the peptides
		for (MsgfPeptide msgfPpeptide : protein.getPeptides()) {
			Peptide peptide = new Peptide();
			
			peptide.setSequence(msgfPpeptide.getSequence());
			Spectrum spec = new Spectrum();
			spec.setId(getSpecRefForScan(msgfPpeptide.getScan()));
			peptide.setSpectrumReference(getSpecRefForScan(msgfPpeptide.getScan()));
			peptide.setUniqueIdentifier(msgfPpeptide.getScan() + "");
			
			peptide.setStart(0);
			peptide.setEnd(0);
			
			// add the additional info
			Param additional = new Param();
			
			if (!"*".equals(msgfPpeptide.getPrevAA()))
				additional.getCvParam().add(DAOCvParams.UPSTREAM_FLANKING_SEQUENCE.getParam(msgfPpeptide.getPrevAA()));
			if (!"*".equals(msgfPpeptide.getNextAA()))
				additional.getCvParam().add(DAOCvParams.DOWNSTREAM_FLANKING_SEQUENCE.getParam(msgfPpeptide.getNextAA()));
			
			additional.getCvParam().add(DAOCvParams.CHARGE_STATE.getParam(msgfPpeptide.getCharge()));
			additional.getCvParam().add(DAOCvParams.PRECURSOR_INTENSITY.getParam(msgfPpeptide.getIntensity()));
			additional.getCvParam().add(DAOCvParams.PEPTIDE_P_VALUE.getParam(msgfPpeptide.getpValue()));
			
			additional.getUserParam().add(new UserParam("MQScore", ((Double) msgfPpeptide.getMqScore()).toString()));
			additional.getUserParam().add(new UserParam("TotalPRMScore", ((Double) msgfPpeptide.getTotalPrmScore()).toString()));
			additional.getUserParam().add(new UserParam("MedianPRMScore", ((Double) msgfPpeptide.getMedianPrmScore()).toString()));
			additional.getUserParam().add(new UserParam("FractionY", ((Double) msgfPpeptide.getFractionY()).toString()));
			additional.getUserParam().add(new UserParam("FractionB", ((Double) msgfPpeptide.getFractionB()).toString()));
			additional.getUserParam().add(new UserParam("NTT", msgfPpeptide.getNtt() + ""));
			additional.getUserParam().add(new UserParam("F-Score", msgfPpeptide.getfScore() + ""));
			additional.getUserParam().add(new UserParam("DeltaScore", msgfPpeptide.getDeltaScore() + ""));
			additional.getUserParam().add(new UserParam("DeltaScoreOther", msgfPpeptide.getDeltaScoreOther() + ""));
			additional.getUserParam().add(new UserParam("SpecProb", msgfPpeptide.getSpecProb() + ""));
			
			peptide.setAdditional(additional);
			
			// add the modifications
			peptide.getPTM().addAll(createPeptidePtms(peptide.getSequence()));
			
			identification.getPeptide().add(peptide);
		}
		
		return identification;
	}
	
	/**
	 * Creates a peptide's (static) modifications.
	 * @param sequence The peptide's sequence
	 * @return A list of peptide ptms
	 */
	private List<PeptidePTM> createPeptidePtms(String sequence) {
		List<PeptidePTM> mods = new ArrayList<PeptidePTM>();
		
		for (int i = 0; i < sequence.length(); i++) {
			char c = sequence.charAt(i);
			
			if (addModCarbamidomethylation && c == 'C') { 
				PeptidePTM ptm = new PeptidePTM();
				ptm.setFixedModification(true);
				ptm.setSearchEnginePTMLabel("carbamidomethylation");
				ptm.setModAccession(DefaultPTMs.CARBAMIDOMETHYL.getAccession());
				ptm.setModDatabase(DefaultPTMs.CARBAMIDOMETHYL.getDatabase());
				ptm.setModDatabaseVersion(DefaultPTMs.CARBAMIDOMETHYL.getDatabaseVersion());
				ptm.setModLocation(i + 1);
				ptm.getModMonoDelta().add(DefaultPTMs.CARBAMIDOMETHYL.getMonoDelta().toString());
				
				Param additional = new Param();
				additional.getCvParam().add(new CvParam("PSI", 
						DefaultPTMs.CARBAMIDOMETHYL.getAccession(), 
						DefaultPTMs.CARBAMIDOMETHYL.getPreferredName(), 
						DefaultPTMs.CARBAMIDOMETHYL.getMonoDelta().toString()));
				ptm.setAdditional(additional);
				
				mods.add(ptm);
			}
		}
		
		return mods;
	}

	/**
	 * Returns the PRIDE specRef for the peptide.
	 * @param scan
	 * @return
	 */
	private int getSpecRefForScan(Integer scan) {
		createPeakListDao();
		
		String scanId = scan.toString();
		
		for (int i = 0; i < specIds.size(); i++) {
			if (scanId.equals(specIds.get(i)))
				return i + 1;
		}
		
		throw new ConverterException("Could not find spectrum for scan = " + scan);
	}
	
	private class OnlyIdentifiedSpectrumIterator implements Iterator<Spectrum> {
		private Iterator<Integer> specIdIterator;
		private Iterator<Spectrum> specIterator;
		
		public OnlyIdentifiedSpectrumIterator() throws InvalidFormatException {
			specIterator = peakListDao.getSpectrumIterator(false);
			Collections.sort(identifiedSpecIds);
			specIdIterator = identifiedSpecIds.iterator();
		}

		public boolean hasNext() {
			return specIdIterator.hasNext();
		}

		public Spectrum next() {
			Integer id = specIdIterator.next();
			Integer mzDataId = getSpecRefForScan(id);
			
			Spectrum s = specIterator.next();
			
			while (s.getId() != mzDataId) {
				s = specIterator.next();
			}
			
			return s;
		}

		public void remove() {
			// not supported			
		}
	}
}
