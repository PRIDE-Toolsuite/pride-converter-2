package uk.ac.ebi.pride.tools.converter.dao_omssa_txt;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.dao.DAO;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.dao.DAOProperty;
import uk.ac.ebi.pride.tools.converter.dao.impl.*;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.filters.EValueFilterCriteria;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.filters.FilterCriteria;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.model.OmssaPeptide;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.model.OmssaProtein;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.parsers.OmssaIdentificationsParser;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.parsers.OmssaIdentificationsParserResult;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.properties.ScoreCriteria;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.properties.SupportedProperty;
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
public class OmssaTxtDao extends AbstractDAOImpl implements DAO {

    private enum SpectraType {
        /**
         * Use the identified peaks as spectrum
         */
        PKL,
        DTA,
        MGF,
        MZXML,
        MS2,
        MZML;
    }

    /**
	 * Logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(OmssaTxtDao.class);

    /**
	 * The input target Crux-txt file.
	 */
	private final File targetFile;

    /**
     * File header
     */
    private Map<String, Integer> header;

    /**
     * Target file index
     */
    private ArrayList<String[]> targetFileIndex;
    
	/**
	 * The proteins found in the Crux-txt target file
	 */
	private Map<String, OmssaProtein> proteins;

    /**
     * All PTMs found in the target file peptide sequences
     */
    private Collection<PTM> allPTMs;

    /**
     * User defined PTMs for this search
     */
    private Map<Character, Double> fixedPtms;
    private Map<Character, Double> variablePtms;

	/**
	 * The spectra file
	 */
	private File spectraFile;

    /**
     * The spectra file type
     */
    private SpectraType spectraFileType;
    
    /**
     * DAO used to parse the corresponding peak list
     * file.
     */
    private AbstractPeakListDAO spectraDAO;

    /**
	 * List of spectra ids that were identified
	 */
	private List<Integer> identifiedSpecIds;

    /**
	 * The search engine reported for the proteins
	 */
	private String searchEngine = "Omssa";

    /**
     * Contains all the supportedProperties of this DAO (get/setConfiguration)
     */
	private Properties supportedProperties;

    /**
     * Threshold property value: allows ignoring all identifications under/over the value (depending on scoreCriteria)
     */
    private String threshold;

    /**
     * Score criteria item property: allows ordering and further filtering of identifications
     */
    private String scoreCriteria;

    /**
     * Filter object built from supportedProperties
     */
    private FilterCriteria filter;

    /**
     * Main constructor. Will parse the result .xls file and create the proper internal data structures
     *
     * @param resultFile
     */
	public OmssaTxtDao(File resultFile) {
		this.targetFile = resultFile;

        // add default PTMs
        fixedPtms = new HashMap<Character, Double>();
        fixedPtms.put('C',57.02);
        variablePtms = new HashMap<Character, Double>();
        variablePtms.put('M',15.99);

		// parse the csv file
        OmssaIdentificationsParser parser = new OmssaIdentificationsParser();
        OmssaIdentificationsParserResult parsingResult = parser.parse(targetFile, fixedPtms, variablePtms);
        header = parsingResult.header;
        proteins = parsingResult.proteins;
        identifiedSpecIds = parsingResult.identifiedSpectraTitles;
        targetFileIndex = parsingResult.fileIndex;
        allPTMs = parsingResult.ptms.values();

        setDefaultConfiguration();
	}



    @SuppressWarnings("rawtypes")
    public static Collection<DAOProperty> getSupportedProperties() {

        List<DAOProperty> supportedProperties = new ArrayList<DAOProperty>();
        
        // Threshold
        DAOProperty<String> threshold = new DAOProperty<String>(SupportedProperty.THRESHOLD.getName(), null);
        threshold.setDescription("Allows filtering identifications.");
        supportedProperties.add(threshold);

        // Score criteria
        DAOProperty<String> scoreCriteria = new DAOProperty<String>(SupportedProperty.SCORE_CRITERIA.getName(), ScoreCriteria.E_VALUE.getName());
        scoreCriteria.setDescription("Defines the criteria for ordering and filtering identifications.");
        supportedProperties.add(scoreCriteria);

        // Fixed PTMs
        DAOProperty<String> fixedPtmsProperty = new DAOProperty<String>(SupportedProperty.FIXED_PTMS.getName(), "57.02@C");
        fixedPtmsProperty.setDescription("Comma separated list of fixed modifications in the format: mass-delta@AA");
        supportedProperties.add(fixedPtmsProperty);

        // Variable PTMs
        DAOProperty<String> variablePtmsProperty = new DAOProperty<String>(SupportedProperty.VARIABLE_PTMS.getName(), "15.99@M");
        variablePtmsProperty.setDescription("Comma separated list of variable modifications in the format: mass-delta@AA");
        supportedProperties.add(variablePtmsProperty);

        return supportedProperties;
    }

    /**
     * Sets the supportedProperties associated to this DAO
     * @param props The supportedProperties to be associated with the DAO
     */
    public void setConfiguration(Properties props) {
        supportedProperties = props;

        // set member supportedProperties here using supportedProperties object
        threshold = props.getProperty(SupportedProperty.THRESHOLD.getName());
        scoreCriteria = props.getProperty(SupportedProperty.SCORE_CRITERIA.getName());

        // Create the filter object from the supportedProperties
        if (ScoreCriteria.E_VALUE.getName().equals(scoreCriteria)) {
            filter = new EValueFilterCriteria();
            filter.setThreshold(Double.parseDouble(threshold));
        } else {   // default filter actually does nothing
            filter = new EValueFilterCriteria();
            filter.setThreshold(0.0);
        }

        // process fixed PTMs
        String fixedPtmsProperty = props.getProperty(SupportedProperty.FIXED_PTMS.getName());
        if (fixedPtmsProperty != null) {
            String [] fixedPtms = fixedPtmsProperty.split(",");
            for (String fixedPtm: fixedPtms) {
                String [] ptmTokens = fixedPtm.trim().split("@");
                this.fixedPtms.put(ptmTokens[1].charAt(0), Double.parseDouble(ptmTokens[0]));
            }
        }

        // process variable PTMs
        String variablePtmsProperty = props.getProperty(SupportedProperty.VARIABLE_PTMS.getName());
        if (variablePtmsProperty != null) {
            String [] variablePtms = variablePtmsProperty.split(",");
            for (String variablePtm: variablePtms) {
                String [] ptmTokens = variablePtm.trim().split("@");
                this.variablePtms.put(ptmTokens[1].charAt(0), Double.parseDouble(ptmTokens[0]));
            }
        }

    }

    /**
     * Set default configuration
     */
    private void setDefaultConfiguration() {
        Properties properties = new Properties();
        properties.setProperty(SupportedProperty.THRESHOLD.getName(), "0");
        properties.setProperty(SupportedProperty.SCORE_CRITERIA.getName(), ScoreCriteria.E_VALUE.getName());
        this.filter = new EValueFilterCriteria();
        this.filter.setThreshold(0.0);
        properties.setProperty(SupportedProperty.FIXED_PTMS.getName(), "57.02@C");
        properties.setProperty(SupportedProperty.VARIABLE_PTMS.getName(), "15.99@M");
        this.setConfiguration(properties);
    }
    
    /**
     * Gets the supportedProperties associated with this DAO
     * @return The supportedProperties associated with the DAO
     */
    public Properties getConfiguration() {
        return supportedProperties;
    }

    /**
     * Sets the spectra file associated with this set of files.
     * @throws ConverterException if file does not exist or not MGF format
     */
    public void setExternalSpectrumFile(String s) {
        spectraFile = new File(s);
        if (!spectraFile.exists()) throw new ConverterException("Spectra file does not exist");
        try {
            guessSpectraSourceType();
            // create the respective dao
            switch (spectraFileType) {
                case DTA:
                    spectraDAO = new DtaDAO(spectraFile);
                    break;
                case PKL:
                    spectraDAO = new PklDAO(spectraFile);
                    break;
                case MGF:
                    spectraDAO = new MgfDAO(spectraFile);
                    break;
                case MZXML:
                    spectraDAO = new MzXmlDAO(spectraFile);
                    break;
                case MZML:
                    spectraDAO = new MzmlDAO(spectraFile);
                    break;
            }
        } catch (InvalidFormatException e) {
            throw new ConverterException("Spectra file type unknown");
        }



    }

    /**
     *
     * @return
     * @throws InvalidFormatException
     */
    public Collection<PTM> getPTMs() throws InvalidFormatException { 
        return allPTMs;
    }


    /**
     *
     * @return Not defined
     * @throws InvalidFormatException
     */
	public String getExperimentTitle() throws InvalidFormatException {
		return "Unknown Omssa based experiment";
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
        params.getCvParam().add(DAOCvParams.ORIGINAL_MS_FORMAT.getParam("Omssa .csv file"));
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
        file.setFileType("Omssa .csv file");

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
        Software s = new Software();
        s.setName("Omssa");
        s.setVersion("");
		return s;
	}

    /**
     *
     * @return
     */
	public Param getProcessingMethod() {
        Param param = new Param();
		return param;
	}

    /**
     *
     * @return null for this DAO
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
    @Override
	public int getSpectrumCount(boolean onlyIdentified)
			throws InvalidFormatException {
        if (spectraFileType == null)
            guessSpectraSourceType();

        // if only identified return the identified count
        if (!onlyIdentified)
            return getSpectraDao().getSpectrumCount(false);

        // return the spectra dao's spec count
        return this.identifiedSpecIds.size();
	}

    /**
     *
     * @param onlyIdentified
     * @return
     * @throws InvalidFormatException
     */
	public Iterator<Spectrum> getSpectrumIterator(boolean onlyIdentified)
			throws InvalidFormatException {
        if (spectraFileType == null)
            guessSpectraSourceType();

        if (!onlyIdentified)
            return getSpectraDao().getSpectrumIterator(false);

        // use the special identified DAO iterator
        return new OnlyIdentifiedSpectrumIterator();
	}

    /**
     *
     * @param peptideUID
     * @return
     * @throws InvalidFormatException
     */
	public int getSpectrumReferenceForPeptideUID(String peptideUID)
			throws InvalidFormatException {
        String [] items = peptideUID.split("_");
		return Integer.parseInt(items[1]);
	}

    /**
     *
     * @param identificationUID
     * @return
     * @throws InvalidFormatException
     */
	public Identification getIdentificationByUID(String identificationUID)
			throws InvalidFormatException {
        OmssaProtein protein;
        protein = proteins.get(identificationUID);
        if (protein == null)
            throw new InvalidFormatException("Protein with UID=" + identificationUID + " does not exist");
        return convertIdentification(protein, false);
    }

    /**
     *
     * @param prescanMode
     * @return
     * @throws InvalidFormatException
     */
	public Iterator<Identification> getIdentificationIterator(
			boolean prescanMode) throws InvalidFormatException {
		return new OmssaIdentificationIterator(prescanMode);
	}


    /**
     * Redefine the iterator in order to not to keep all the Identification objects in memory (there are quite heavy
     * and can be quite a lot)
     * Because we have two different target files (target and decoy), and because we don't want to mix them due to the 
     * last-moment prefixing system we use, we need here two iterators that we made appear as one.
     */
	private class OmssaIdentificationIterator implements Iterator<Identification> {
		private final Iterator<String> accessionIterator = proteins.keySet().iterator();

        private boolean prescanMode;

        public OmssaIdentificationIterator(boolean prescanMode) {
            this.prescanMode = prescanMode;
        }

		public boolean hasNext() {
			return accessionIterator.hasNext();
		}

		public Identification next() {
            return convertIdentification(proteins.get(accessionIterator.next()), prescanMode);
		}

		public void remove() {
			// not supported
		}
	}

    /**
     * Converts from our internal OmssaProtein to the DAO representation
     * @param protein
     * @return
     */
	private Identification convertIdentification(OmssaProtein protein, boolean prescanMode) {

		Identification identification = new Identification();
		
		identification.setAccession(protein.getAccession());
		identification.setScore(0.0);
		identification.setThreshold(0.0);
		identification.setDatabase("Unknown database");
		identification.setDatabaseVersion("Unknown");
		identification.setUniqueIdentifier(protein.getAccession());
		
		identification.setSearchEngine(searchEngine);

		// process the peptides
		for (Integer cruxPeptideStringIndex : protein.getPeptides()) {
            String[] fields;
            List<String> wholeScan;

            fields = this.targetFileIndex.get(cruxPeptideStringIndex);  // split the columns
            
            // Check if the entry pass the filter. Otherwise, go for the next line
            if ( (this.filter == null) || filter.passFilter(this.header,fields) ) {
                // process the peptide
                OmssaPeptide omssaPeptide = OmssaIdentificationsParser.createOmssaPeptide(fields, this.header);

                Peptide peptide = new Peptide();

                peptide.setSequence(omssaPeptide.getPeptide());
                int peptideSpectraindex = omssaPeptide.getSpectrumNumber();

                if (peptideSpectraindex == -1)
                    throw new ConverterException("Spectrum reference does not exist or is ambiguous for peptide " + peptide.getSequence());
                else {
                    peptide.setSpectrumReference(peptideSpectraindex);
                    peptide.setUniqueIdentifier(peptideSpectraindex + "_" + omssaPeptide.getAccession());
                    peptide.setStart(0);
                    peptide.setEnd(0);

                    if (prescanMode) {
                        // add the additional info
                        Param additional = new Param();

                        additional.getCvParam().add(DAOCvParams.CHARGE_STATE.getParam(omssaPeptide.getCharge()));
                        additional.getCvParam().add(DAOCvParams.PRECURSOR_MZ.getParam(omssaPeptide.getMass()));
                        additional.getCvParam().add(DAOCvParams.CHARGE_STATE.getParam(omssaPeptide.getCharge()));
                        additional.getCvParam().add(DAOCvParams.OMSSA_E_VALUE.getParam(omssaPeptide.geteValue()));
                        additional.getCvParam().add(DAOCvParams.OMSSA_P_VALUE.getParam(omssaPeptide.getpValue()));

                        peptide.setAdditional(additional);

                        // add the PTMs
                        peptide.getPTM().addAll(omssaPeptide.getPTMs(omssaPeptide.getPeptide(),fixedPtms,variablePtms));

                    }

                    identification.getPeptide().add(peptide);
                }
            }
		}

        if (identification.getPeptide() == null || identification.getPeptide().size() <= 0)
            return null;
        else
            return identification;

	}

    /**
     *
     */
	private class OnlyIdentifiedSpectrumIterator implements Iterator<Spectrum> {
		private Iterator<Integer> specIdIterator;
		private Iterator<Spectrum> specIterator;
		
		public OnlyIdentifiedSpectrumIterator() throws InvalidFormatException {
			specIterator = spectraDAO.getSpectrumIterator(false);
            Collections.sort(identifiedSpecIds);
			specIdIterator = identifiedSpecIds.iterator();
		}

		public boolean hasNext() {
			return specIdIterator.hasNext();
		}

		public Spectrum next() {
			Integer id = specIdIterator.next();
			
			Spectrum s = specIterator.next();
			
			while (s.getId() != id) {
				s = specIterator.next();
			}
			
			return s;
		}

		public void remove() {
			// not supported			
		}
	}

    /**
     * Returns the spectraDAO to be used for the
     * given spectrum file. Makes sure only one
     * instance of the DAO is created and only
     * when it's needed.
     * @return The spectra DAO to be used to retrieve the spectra information.
     * @throws InvalidFormatException
     */
    private DAO getSpectraDao() throws InvalidFormatException {
        if (spectraDAO != null)
            return spectraDAO;

        // guess the spectra filetype
        guessSpectraSourceType();

        // create the respective dao
        switch (spectraFileType) {
            case DTA:
                spectraDAO = new DtaDAO(spectraFile);
                break;
            case PKL:
                spectraDAO = new PklDAO(spectraFile);
                break;
            case MGF:
                spectraDAO = new MgfDAO(spectraFile);
                break;
            case MZXML:
                spectraDAO = new MzXmlDAO(spectraFile);
                break;
            case MS2:
                spectraDAO = new Ms2DAO(spectraFile);
                break;
        }

        return spectraDAO;
    }

    /**
     * Guesses the type of spectra file used
     * and sets spectrFileType accordingly.
     * @throws InvalidFormatException
     */
    private void guessSpectraSourceType() throws InvalidFormatException {
        spectraFileType = null;

        // if it's a directory, expect it to contain .dta files
        if (spectraFile.isDirectory()) {
            spectraFileType = SpectraType.DTA;
            return;
        }

        // as the spectra file now exists, guess the type
        String filename = spectraFile.getName().toLowerCase().trim();

        if (filename.endsWith("dta"))
            spectraFileType = SpectraType.DTA;
        else if (filename.endsWith("pkl"))
            spectraFileType = SpectraType.PKL;
        else if (filename.endsWith("mgf"))
            spectraFileType = SpectraType.MGF;
        else if (filename.toLowerCase().endsWith("mzxml"))
            spectraFileType = SpectraType.MZXML;
        else if (filename.toLowerCase().endsWith("ms2"))
            spectraFileType = SpectraType.MS2;
        else if (filename.toLowerCase().endsWith("mzml"))
            spectraFileType = SpectraType.MZML;

        // make sure the type was set correctly
        if (spectraFileType == null)
            throw new InvalidFormatException("Unsupported spectra file type used (" + filename + ")");
    }


}