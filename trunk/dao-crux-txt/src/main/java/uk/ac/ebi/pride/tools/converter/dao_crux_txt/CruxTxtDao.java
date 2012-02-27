package uk.ac.ebi.pride.tools.converter.dao_crux_txt;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.dao.DAO;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.dao.DAOProperty;
import uk.ac.ebi.pride.tools.converter.dao.impl.AbstractDAOImpl;
import uk.ac.ebi.pride.tools.converter.dao.impl.AbstractPeakListDAO;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.model.CruxProtein;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.parsers.CruxTxtIdentificationsParser;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.parsers.CruxTxtParamsParser;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.properties.SupportedProperty;
import uk.ac.ebi.pride.tools.converter.report.model.*;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.FileUtils;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class CruxTxtDao extends AbstractDAOImpl implements DAO {

    /**
	 * Logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(CruxTxtDao.class);

    /**
	 * The input target Crux-txt file.
	 */
	private final File targetFile;

    /**
     * The input decoy Crux-txt file.
     */
    private final File decoyFile;

    /**
     * The input properties Crux-txt file.
     */
    private final File propertiesFile;
    
	/**
	 * The proteins found in the Crux-txt target file
	 */
	private Map<String, CruxProtein> proteins;

    /**
     * The proteins found in the Crux-txt decoy file
     */
    private Map<String, CruxProtein> proteinsDecoy;

	/**
	 * The spectra file
	 */
	private File spectraFile;


    /**
	 * DAO used to parse the corresponding peak list
	 * file.
	 */
	private AbstractPeakListDAO peakListDao;   // todo: not sure if we need this one in Crux

    /**
	 * Peptides found in the Crux-txt file.
	 */
	private int peptideCount = 0; // todo: not sure if we need this one in Crux

    /**
	 * The spectra ids in the peak list format
	 */
	private List<String> specIds; // todo: not sure if we need this one in Crux

    /**
	 * List of spectra ids that were identified
	 */
	private List<Integer> identifiedSpecIds = new ArrayList<Integer>();  // todo: not sure if we need this one in Crux

    /**
	 * The search engine reported for the proteins
	 */
    // todo: is Crux the search engine?
	private String searchEngine = "MSGF"; // todo: not sure if we need this one in Crux

    /**
     * Contains all the properties of this DAO (get/setConfiguration)
     */
	private Properties properties;

    /**
     * Built up from the search parameter file
     */
    private Properties parameters;

    /**
     * Main constructor. Will parse three files:
     *  - Crux txt target identifications file
     *  - Crux txt decoy identifications file
     *  - Crux parameters file
     *  And create the proper internal data structures
     *
     * @param targetFile
     * @param decoyFile
     * @param propertiesFile
     */
	public CruxTxtDao(File targetFile, File decoyFile, File propertiesFile) {
		this.targetFile = targetFile;
		this.propertiesFile = propertiesFile;
        this.decoyFile = decoyFile;

		// parse the Crux files
        proteins = CruxTxtIdentificationsParser.parse(targetFile).proteins;
        proteinsDecoy = CruxTxtIdentificationsParser.parse(decoyFile).proteins; // todo: Right now, we do nothing with the decoy two extra columns
        parameters = CruxTxtParamsParser.parse(propertiesFile);     // todo: give proper semantics to parameters in the future
	}

    @SuppressWarnings("rawtypes")
    public static Collection<DAOProperty> getSupportedProperties() {

        List<DAOProperty> supportedProperties = new ArrayList<DAOProperty>();

        // Spectra file property
        DAOProperty<String> spectrumFilePath = new DAOProperty<String>(SupportedProperty.SPECTRUM_FILE.getName(), null);
        spectrumFilePath.setDescription("Allows to manually set the path to the spectrum source file. This should be mandatory since, in Crux, spectra files are not references within the identification or parameter files.");
        supportedProperties.add(spectrumFilePath);

        // todo: add more properties here as required/supported


        return supportedProperties;
    }

    /**
     * Sets the properties associated to this DAO
     * @param props The properties to be associated with the DAO
     */
    public void setConfiguration(Properties props) {
        properties = props;

        // set member properties here using properties object
        spectraFile = new File(props.getProperty(SupportedProperty.SPECTRUM_FILE.getName()));
    }

    /**
     * Gets the properties associated with this DAO
     * @return The properties associated with the DAO
     */
    public Properties getConfiguration() {
        return properties;
    }






    // previous DAO code starts here


//    /**
//     *
//     * @param props
//     */
//    public void setConfiguration(Properties props) {
//        properties = props;
//
//        searchEngine = properties.getProperty("search_engine", "MSGF");
//        mzxmlFolderPath = properties.getProperty("mzxml_path", null);
//        addModCarbamidomethylation = Boolean.parseBoolean(
//                properties.getProperty("add_carbamidomethylation", "false") );
//    }

//    /**
//     *
//     * @return
//     */
//    public Properties getConfiguration() {
//        // no configuration supported
//        return properties;
//    }

    // examples of setting properties

//    @SuppressWarnings("rawtypes")
//    public static Collection<DAOProperty> getSupportedProperties() {
//
//        List<DAOProperty> properties = new ArrayList<DAOProperty>();
//
//        DAOProperty<String> searchEngine = new DAOProperty<String>("search_engine", "MSGF");
//        searchEngine.setDescription("MSGF files do not contain the search engine used to identify a protein. This parameter sets the given search engine. Default value is \"MSGF\"");
//        properties.add(searchEngine);
//
//        DAOProperty<String> mzxmlPath = new DAOProperty<String>("mzxml_path", null);
//        searchEngine.setDescription("path to the folder where the mzXML files can be found. If this parameter is not set the mzXML file will be search for in the MSGF file's directory.");
//        properties.add(mzxmlPath);
//
//        DAOProperty<Boolean> addCarbamidomethylation = new DAOProperty<Boolean>("add_carbamidomethylation", false);
//        addCarbamidomethylation.setDescription("MSGF files cannot report modifications. If this parameter is set to \"true\" a Carbamidomethylation is added to every C.");
//        properties.add(addCarbamidomethylation);
//
//        return properties;
//    }

//    @SuppressWarnings("rawtypes")
//    public static Collection<DAOProperty> getSupportedProperties() {
//
//        List<DAOProperty> supportedProperties = new ArrayList<DAOProperty>();
//
//        DAOProperty<String> spectrumFilePath = new DAOProperty<String>(SupportedProperty.SPECTRUM_FILE.getName(), null);
//        spectrumFilePath.setDescription("allows to manually set the path to the spectrum source file. If this property is set any file referenced in the actual X!Tandem file will be ignored.");
//        supportedProperties.add(spectrumFilePath);
//
//        DAOProperty<Boolean> useInternalSpectra = new DAOProperty<Boolean>(SupportedProperty.USE_INTERNAL_SPECTA.getName(), false);
//        useInternalSpectra.setDescription("if this parameter is set to \"true\" the spectra stored in the X!Tandem file are used irrespective of whether an external peak list file is referenced. These spectra are highly preprocessed and do not properly represent the input spectra. This option should only be used if the original spectra are not available.");
//        supportedProperties.add(useInternalSpectra);
//
//        return supportedProperties;
//    }
//

//	/**
//	 * Creates the peak list dao to load the mzXML file.
//	 */
//	private void createPeakListDao() {
//		if (peakListDao != null)
//			return;
//
//		// create the mzXML DAO
//		String path = mzxmlFiles.iterator().next();
//
//		File peakListFile = new File(path);
//
//		if (!peakListFile.exists() && mzxmlFolderPath != null)
//			peakListFile = new File(new File(mzxmlFolderPath).getAbsolutePath() + File.separator + peakListFile.getName());
//		if (!peakListFile.exists())
//			peakListFile = new File(targetFile.getParent() + File.separator + peakListFile.getName());
//		if (!peakListFile.exists())
//			peakListFile = new File(peakListFile.getName());
//		if (!peakListFile.exists())
//			throw new ConverterException("Referenced peak list file '" + peakListFile.getName() + "' could not be found.");
//
//		try {
//			peakListDao = new MzXmlDAO(peakListFile);
//
//			specIds = peakListDao.getSpectraIds();
//		}
//		catch (InvalidFormatException e) {
//			logger.error("Failed to parse input peak list file: " + e.getMessage());
//			throw new ConverterException("Failed to parse peak list file: " + e.getMessage());
//		}
//	}

    /**
     *
     * @return
     * @throws InvalidFormatException
     */
	public String getExperimentTitle() throws InvalidFormatException {
		return "Unknown MSGF based experiment";
	}

    /**
     *
     * @return
     */
	public String getExperimentShortLabel() {
		return null;
	}

    /**
     *
     * @return
     * @throws InvalidFormatException
     */
	public Param getExperimentParams() throws InvalidFormatException {
		// initialize the collection to hold the params
        Param params = new Param();

        // original MS format param
        params.getCvParam().add(DAOCvParams.ORIGINAL_MS_FORMAT.getParam("MSGF file"));
       	params.getCvParam().add(DAOCvParams.MS_MS_SEARCH.getParam());

        return params;
	}

    /**
     *
     * @return
     */
	public String getSampleName() {
		return null;
	}

    /**
     *
     * @return
     */
	public String getSampleComment() {
		return null;
	}

    /**
     *
     * @return
     * @throws InvalidFormatException
     */
	public Param getSampleParams() throws InvalidFormatException {
		return new Param();
	}

    /**
     *
     * @return
     * @throws InvalidFormatException
     */
	public SourceFile getSourceFile() throws InvalidFormatException {
		// initialize the return variable
        SourceFile file = new SourceFile();

        file.setPathToFile(targetFile.getAbsolutePath());
        file.setNameOfFile(targetFile.getName());
        file.setFileType("MSGF file"); // FIXME: put correct thing here

        return file;
	}

    /**
     *
     * @return
     */
	public Collection<Contact> getContacts() {
		return null;
	}

    /**
     *
     * @return
     */
	public InstrumentDescription getInstrument() {
		return null;
	}

    /**
     *
     * @return
     * @throws InvalidFormatException
     */
	public Software getSoftware() throws InvalidFormatException {
		return new Software();
	}

    /**
     *
     * @return
     */
	public Param getProcessingMethod() {
		return null;
	}

    /**
     *
     * @return
     */
	public Protocol getProtocol() {
		return null;
	}

    /**
     *
     * @return
     */
	public Collection<Reference> getReferences() {
		return null;
	}

    /**
     *
     * @return
     * @throws InvalidFormatException
     */
	public String getSearchDatabaseName() throws InvalidFormatException {
		return "Unknown database";
	}

    /**
     *
     * @return
     * @throws InvalidFormatException
     */
	public String getSearchDatabaseVersion() throws InvalidFormatException {
		return "Unknown";
	}

    /**
     *
     * @return
     * @throws InvalidFormatException
     */
	public Collection<PTM> getPTMs() throws InvalidFormatException {
		List<PTM> ptms = new ArrayList<PTM>();

//		if (addModCarbamidomethylation) {
//			PTM ptm = new PTM();
//
//			ptm.setSearchEnginePTMLabel("carbamidomethylation");
//			ptm.setModAccession(DefaultPTMs.CARBAMIDOMETHYL.getAccession());
//			ptm.setModDatabase(DefaultPTMs.CARBAMIDOMETHYL.getDatabase());
//			ptm.setModDatabaseVersion(DefaultPTMs.CARBAMIDOMETHYL.getDatabaseVersion());
//			ptm.getModMonoDelta().add(DefaultPTMs.CARBAMIDOMETHYL.getMonoDelta().toString());
//			ptm.setResidues("C");
//
//			Param additional = new Param();
//			additional.getCvParam().add(new CvParam("PSI",
//					DefaultPTMs.CARBAMIDOMETHYL.getAccession(),
//					DefaultPTMs.CARBAMIDOMETHYL.getPreferredName(),
//					DefaultPTMs.CARBAMIDOMETHYL.getMonoDelta().toString()));
//			ptm.setAdditional(additional);
//
//			ptms.add(ptm);
//		}

		return ptms;
	}

    /**
     *
     * @return
     * @throws InvalidFormatException
     */
	public Collection<DatabaseMapping> getDatabaseMappings()
			throws InvalidFormatException {
		ArrayList<DatabaseMapping> mappings = new ArrayList<DatabaseMapping>(1);

		DatabaseMapping mapping = new DatabaseMapping();

		mapping.setSearchEngineDatabaseName("Unknown database");
		mapping.setSearchEngineDatabaseVersion("Unknown");

		mappings.add(mapping);

		return mappings;
	}

    /**
     *
     * @return
     * @throws InvalidFormatException
     */
	public SearchResultIdentifier getSearchResultIdentifier()
			throws InvalidFormatException {
		// intialize the search result identifier
        SearchResultIdentifier identifier = new SearchResultIdentifier();

        // format the current time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        identifier.setSourceFilePath(targetFile.getAbsolutePath());
        identifier.setTimeCreated(formatter.format(new Date(System.currentTimeMillis())));
        identifier.setHash(FileUtils.MD5Hash(targetFile.getAbsolutePath()));

        return identifier;
	}

    /**
     *
     * @return
     * @throws InvalidFormatException
     */
	public Collection<CV> getCvLookup() throws InvalidFormatException {
		// just create a set containing the 2 cvLookups used here
        ArrayList<CV> cvs = new ArrayList<CV>();

        cvs.add(new CV("MS", "PSI Mass Spectrometry Ontology", "1.2", "http://psidev.cvs.sourceforge.net/viewvc/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo"));
        cvs.add(new CV("PRIDE", "PRIDE Controlled Vocabulary", "1.101", "http://ebi-pride.googlecode.com/svn/trunk/pride-core/schema/pride_cv.obo"));

        return cvs;
	}

    /**
     *
     * @param onlyIdentified
     * @return
     * @throws InvalidFormatException
     */
	public int getSpectrumCount(boolean onlyIdentified)
			throws InvalidFormatException {
//		if (peakListDao == null)
//			createPeakListDao();
		
		return onlyIdentified ? peptideCount : peakListDao.getSpectrumCount(false);
	}

    /**
     *
     * @param onlyIdentified
     * @return
     * @throws InvalidFormatException
     */
	public Iterator<Spectrum> getSpectrumIterator(boolean onlyIdentified)
			throws InvalidFormatException {
//		if (peakListDao == null)
//			createPeakListDao();
		
		if (onlyIdentified)
			return new OnlyIdentifiedSpectrumIterator();
		
		return peakListDao.getSpectrumIterator(false);
	}

    /**
     *
     * @param peptideUID
     * @return
     * @throws InvalidFormatException
     */
	public int getSpectrumReferenceForPeptideUID(String peptideUID)
			throws InvalidFormatException {
		return getSpecRefForScan(Integer.parseInt(peptideUID));
	}

    /**
     *
     * @param identificationUID
     * @return
     * @throws InvalidFormatException
     */
	public Identification getIdentificationByUID(String identificationUID)
			throws InvalidFormatException {
		CruxProtein protein = proteins.get(identificationUID);
		
		if (protein == null)
			throw new InvalidFormatException("Protein with UID=" + identificationUID + " does not exist");
		
		return convertIdentification(protein);
	}

    /**
     *
     * @param prescanMode
     * @return
     * @throws InvalidFormatException
     */
	public Iterator<Identification> getIdentificationIterator(
			boolean prescanMode) throws InvalidFormatException {
		return new MsgfIdentificationIterator();
	}

    /**
     *
     */
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

    /**
     *
     * @param protein
     * @return
     */
	private Identification convertIdentification(CruxProtein protein) {
        /*
		Identification identification = new Identification();
		
		identification.setAccession(protein.getAccession());
		identification.setScore(0.0);
		identification.setThreshold(0.0);
		identification.setDatabase("Unknown database");
		identification.setDatabaseVersion("Unknown");
		identification.setUniqueIdentifier(protein.getAccession());
		
		identification.setSearchEngine(searchEngine);
		
		// process the peptides
		for (CruxPeptide cruxPpeptide : protein.getPeptides()) {
			Peptide peptide = new Peptide();
			
			peptide.setSequence(cruxPpeptide.getSequence());
			Spectrum spec = new Spectrum();
			spec.setId(getSpecRefForScan(cruxPpeptide.getScan()));
			peptide.setSpectrumReference(getSpecRefForScan(cruxPpeptide.getScan()));
			peptide.setUniqueIdentifier(cruxPpeptide.getScan() + "");
			
			peptide.setStart(0);
			peptide.setEnd(0);
			
			// add the additional info
			Param additional = new Param();
			
			if (!"*".equals(cruxPpeptide.getPrevAA()))
				additional.getCvParam().add(DAOCvParams.UPSTREAM_FLANKING_SEQUENCE.getParam(cruxPpeptide.getPrevAA()));
			if (!"*".equals(cruxPpeptide.getNextAA()))
				additional.getCvParam().add(DAOCvParams.DOWNSTREAM_FLANKING_SEQUENCE.getParam(cruxPpeptide.getNextAA()));
			
			additional.getCvParam().add(DAOCvParams.CHARGE_STATE.getParam(cruxPpeptide.getCharge()));
			additional.getCvParam().add(DAOCvParams.PRECURSOR_INTENSITY.getParam(cruxPpeptide.getIntensity()));
			additional.getCvParam().add(DAOCvParams.PEPTIDE_P_VALUE.getParam(cruxPpeptide.getpValue()));
			
			additional.getUserParam().add(new UserParam("MQScore", ((Double) cruxPpeptide.getMqScore()).toString()));
			additional.getUserParam().add(new UserParam("TotalPRMScore", ((Double) cruxPpeptide.getTotalPrmScore()).toString()));
			additional.getUserParam().add(new UserParam("MedianPRMScore", ((Double) cruxPpeptide.getMedianPrmScore()).toString()));
			additional.getUserParam().add(new UserParam("FractionY", ((Double) cruxPpeptide.getFractionY()).toString()));
			additional.getUserParam().add(new UserParam("FractionB", ((Double) cruxPpeptide.getFractionB()).toString()));
			additional.getUserParam().add(new UserParam("NTT", cruxPpeptide.getNtt() + ""));
			additional.getUserParam().add(new UserParam("F-Score", cruxPpeptide.getfScore() + ""));
			additional.getUserParam().add(new UserParam("DeltaScore", cruxPpeptide.getDeltaScore() + ""));
			additional.getUserParam().add(new UserParam("DeltaScoreOther", cruxPpeptide.getDeltaScoreOther() + ""));
			additional.getUserParam().add(new UserParam("SpecProb", cruxPpeptide.getSpecProb() + ""));
			
			peptide.setAdditional(additional);
			
			// add the modifications
			peptide.getPTM().addAll(createPeptidePtms(peptide.getSequence()));
			
			identification.getPeptide().add(peptide);
		}
		
		return identification;
		*/
        return null; // todo: refactor this method and remove this line
	}
	
	/**
	 * Creates a peptide's (static) modifications.
	 * @param sequence The peptide's sequence
	 * @return A list of peptide ptms
	 */
	private List<PeptidePTM> createPeptidePtms(String sequence) {
		List<PeptidePTM> mods = new ArrayList<PeptidePTM>();
		
//		for (int i = 0; i < sequence.length(); i++) {
//			char c = sequence.charAt(i);
//
//			if (addModCarbamidomethylation && c == 'C') {
//				PeptidePTM ptm = new PeptidePTM();
//				ptm.setFixedModification(true);
//				ptm.setSearchEnginePTMLabel("carbamidomethylation");
//				ptm.setModAccession(DefaultPTMs.CARBAMIDOMETHYL.getAccession());
//				ptm.setModDatabase(DefaultPTMs.CARBAMIDOMETHYL.getDatabase());
//				ptm.setModDatabaseVersion(DefaultPTMs.CARBAMIDOMETHYL.getDatabaseVersion());
//				ptm.setModLocation(i + 1);
//				ptm.getModMonoDelta().add(DefaultPTMs.CARBAMIDOMETHYL.getMonoDelta().toString());
//
//				Param additional = new Param();
//				additional.getCvParam().add(new CvParam("PSI",
//						DefaultPTMs.CARBAMIDOMETHYL.getAccession(),
//						DefaultPTMs.CARBAMIDOMETHYL.getPreferredName(),
//						DefaultPTMs.CARBAMIDOMETHYL.getMonoDelta().toString()));
//				ptm.setAdditional(additional);
//
//				mods.add(ptm);
//			}
//		}
		
		return mods;
	}

	/**
	 * Returns the PRIDE specRef for the peptide.
	 * @param scan
	 * @return
	 */
	private int getSpecRefForScan(Integer scan) {
//		createPeakListDao();
		
		String scanId = scan.toString();
		
		for (int i = 0; i < specIds.size(); i++) {
			if (scanId.equals(specIds.get(i)))
				return i + 1;
		}
		
		throw new ConverterException("Could not find spectrum for scan = " + scan);
	}

    /**
     *
     */
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
