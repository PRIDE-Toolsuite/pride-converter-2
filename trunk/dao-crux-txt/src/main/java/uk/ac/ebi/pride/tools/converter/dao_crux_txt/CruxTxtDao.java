package uk.ac.ebi.pride.tools.converter.dao_crux_txt;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.dao.DAO;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.dao.DAOProperty;
import uk.ac.ebi.pride.tools.converter.dao.impl.*;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.filters.DeltaCnFilterCriteria;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.filters.FilterCriteria;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.filters.XcorrRankFilterCriteria;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.filters.XcorrScoreFilterCriteria;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.model.CruxPeptide;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.model.CruxProtein;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.parsers.CruxIdentificationsParserResult;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.parsers.CruxParametersParserResult;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.parsers.CruxTxtIdentificationsParser;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.parsers.CruxTxtParamsParser;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.properties.ScoreCriteria;
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

    private enum SpectraType {
        /**
         * Use the identified peaks as spectrum
         */
        PKL,
        DTA,
        MGF,
        MZXML,
        MS2;
    }

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
     * File header
     */
    private Map<String, Integer> header;

    /**
     * Target file index
     */
    private ArrayList<String> targetFileIndex;

    /**
     * Decoy file index 
     */
    private ArrayList<String> decoyFileIndex;

    
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
     *
     */
    private SpectraType spectraFileType;

    /**
     *
     */
    private AbstractPeakListDAO spectraDAO;


    /**
     * The decoy identifications prefix
     */
    private String decoyPrefix = "";

    /**
	 * DAO used to parse the corresponding peak list
	 * file.
	 */
	private AbstractPeakListDAO peakListDao;   // todo: use this properly

    /**
	 * Peptides found in the Crux-txt file.
	 */
//	private int peptideCount = 0;

//    /**
//	 * The spectra ids in the peak list format
//	 */
//	private List<String> specIds; // todo: use this properly

    /**
	 * List of spectra ids that were identified
	 */
	private List<Integer> identifiedSpecIds;

    /**
	 * The search engine reported for the proteins
	 */
	private String searchEngine = "Crux"; // todo: check this

    /**
     * Contains all the properties of this DAO (get/setConfiguration)
     */
	private Properties properties;

    /**
     * Built up from the search parameter file
     */
    private CruxParametersParserResult params;

    /**
     * Threshold property value: allows ignoring all identifications under/over the value (depending on scoreCriteria)
     */
    private String threshold;

    /**
     * get highest scored item property: allows retrieving just the highest ranked identification based on scoreCriteria
     */
    private boolean getHighest;

    /**
     * Score criteria item property: allows ordering and further filtering of identifications
     */
    private String scoreCriteria;

    /**
     * Filter object built from properties
     */
    private FilterCriteria filter;

    /**
     * Main constructor. Will parse three files:
     *  - Crux txt target identifications file
     *  - Crux txt decoy identifications file
     *  - Crux parameters file
     *  And create the proper internal data structures
     *
     * @param resultFolder
     */
	public CruxTxtDao(File resultFolder) {
		this.targetFile = new File(resultFolder.getAbsolutePath()+ System.getProperty("file.separator") + "search.target.txt");
		this.propertiesFile = new File(resultFolder.getAbsolutePath()+ System.getProperty("file.separator") + "search.params.txt");
        this.decoyFile = new File(resultFolder.getAbsolutePath()+ System.getProperty("file.separator") + "search.decoy.txt");

		// parse the Crux files
        CruxTxtIdentificationsParser parser = new CruxTxtIdentificationsParser();
        // parse target file
        CruxIdentificationsParserResult resTarget = parser.parse(targetFile);
        header = resTarget.header;
        proteins = resTarget.proteins;
        identifiedSpecIds = resTarget.identifiedSpecIds;
//        peptideCount = resTarget.peptideCount;
        targetFileIndex = resTarget.fileIndex;
        // parse decoy file
        CruxIdentificationsParserResult resDecoy = parser.parse(decoyFile);
        proteinsDecoy = resDecoy.proteins;
//        peptideCount = peptideCount + resDecoy.peptideCount;
        decoyFileIndex = resDecoy.fileIndex;
        // parse parameter file
        params = CruxTxtParamsParser.parse(propertiesFile);
	}

    @SuppressWarnings("rawtypes")
    public static Collection<DAOProperty> getSupportedProperties() {

        List<DAOProperty> supportedProperties = new ArrayList<DAOProperty>();

        // Spectra file property
        DAOProperty<String> spectrumFilePath = new DAOProperty<String>(SupportedProperty.SPECTRUM_FILE.getName(), null);
        spectrumFilePath.setDescription("Allows to manually set the path to the spectrum source file. This should be mandatory since, in Crux, spectra files are not references within the identification or parameter files.");
        supportedProperties.add(spectrumFilePath);

        // we need the one to prefix decoy identifications
        DAOProperty<String> decoyPrefix = new DAOProperty<String>(SupportedProperty.DECOY_PREFIX.getName(), null);
        decoyPrefix.setDescription("Allows adding a prefix to decoy file identifications.");
        supportedProperties.add(decoyPrefix);
        
        // Threshold
        DAOProperty<String> threshold = new DAOProperty<String>(SupportedProperty.THRESHOLD.getName(), null);
        threshold.setDescription("Allows filtering identifications. Default value is 1.0 so all identifications pass the cut.");
        supportedProperties.add(threshold);

        // Score criteria
        DAOProperty<String> scoreCriteria = new DAOProperty<String>(SupportedProperty.SCORE_CRITERIA.getName(), ScoreCriteria.XCORR_RANK.getName());
        scoreCriteria.setDescription("Defines the criteria for ordering and filtering identifications.");
        supportedProperties.add(scoreCriteria);

        // Get just highest ranked item
        DAOProperty<String> getMaxScoreItem = new DAOProperty<String>(SupportedProperty.GET_HIGHEST_SCORE_ITEM.getName(), "true");
        getMaxScoreItem.setDescription("Defines if this DAO retrieves just the highest ranked identification for each " +
                "scan, based on the defined score_criteria. If there are several identifications with the same score, " +
                "all of them will be returned. True by default");
        supportedProperties.add(getMaxScoreItem);

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
        decoyPrefix = props.getProperty(SupportedProperty.DECOY_PREFIX.getName());
        threshold = props.getProperty(SupportedProperty.THRESHOLD.getName());
        if (props.getProperty(SupportedProperty.GET_HIGHEST_SCORE_ITEM.getName()) == null) { // todo: I couldn't make defaults to work
            getHighest = true;
        } else {
            getHighest = Boolean.parseBoolean(props.getProperty(SupportedProperty.GET_HIGHEST_SCORE_ITEM.getName()));
        }
        scoreCriteria = props.getProperty(SupportedProperty.SCORE_CRITERIA.getName());

        // Create the filter object from the properties
        if (ScoreCriteria.XCORR_RANK.getName().equals(scoreCriteria)) {
            filter = new XcorrRankFilterCriteria();
            filter.setThreshold(Integer.parseInt(threshold));
        } else if (ScoreCriteria.XCORR_SCORE.getName().equals(scoreCriteria)) {
            filter = new XcorrScoreFilterCriteria();
            filter.setThreshold(Double.parseDouble(threshold));
        } else if (ScoreCriteria.DELTA_CN.getName().equals(scoreCriteria)) {
            filter = new DeltaCnFilterCriteria();
            filter.setThreshold(Double.parseDouble(threshold));
        }

    }

    /**
     * Gets the properties associated with this DAO
     * @return The properties associated with the DAO
     */
    public Properties getConfiguration() {
        return properties;
    }

    /**
     * Sets the spectra file associated with this set of files.
     * @throws ConverterException if file does not exist.
     */
    public void setExternalSpectrumFile(String s) {
        spectraFile = new File(s);
        if (!spectraFile.exists()) throw new ConverterException("Spectra file does not exist");
        try {
            guessSpectraSourceType();
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
        return params.ptms;
    }


    /**
     *
     * @return
     * @throws InvalidFormatException
     */
	public String getExperimentTitle() throws InvalidFormatException {
		return "Unknown Crux based experiment";
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
        params.getCvParam().add(DAOCvParams.ORIGINAL_MS_FORMAT.getParam("Crux text file"));
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
        file.setFileType("Crux file");

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
        s.setName("Crux");
        s.setVersion("");
		return s;
	}

    /**
     *
     * @return
     */
	public Param getProcessingMethod() {    // todo: check this with JG
        Param param = new Param();
        DAOCvParams cvParam = DAOCvParams.SEARCH_SETTING_PARENT_MASS_TOLERANCE;
        cvParam.setValue(this.properties.getProperty("precursor-window") + " " + this.properties.getProperty("precursor-window-type"));
        param.getCvParam().add(cvParam.getParam());
		return param;
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

        // if only identified return the xtdanem identified count
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
        CruxProtein protein;
        String[] items = identificationUID.split("_",2);
        if ("t".equals(items[0])) { // non decoy protein
		    protein = proteins.get(items[1]);
            if (protein == null)
                throw new InvalidFormatException("Protein with UID=" + identificationUID + " does not exist");
            return convertIdentification(protein, "t_", "", false);
        }
        else { // decoy protein
            protein = proteinsDecoy.get(items[1]);
            if (protein == null)
                throw new InvalidFormatException("Protein with UID=" + identificationUID + " does not exist");
            return convertIdentification(protein, "d_", decoyPrefix, false);
        }

	}

    /**
     *
     * @param prescanMode
     * @return
     * @throws InvalidFormatException
     */
	public Iterator<Identification> getIdentificationIterator(
			boolean prescanMode) throws InvalidFormatException {
		return new CruxIdentificationIterator(prescanMode);
	}


    /**
     * Redefine the iterator in order to not to keep all the Identification objects in memory (there are quite heavy
     * and can be quite a lot)
     * Because we have two different target files (target and decoy), and because we don't want to mix them due to the 
     * last-moment prefixing system we use, we need here two iterators that we made appear as one.
     */
	private class CruxIdentificationIterator implements Iterator<Identification> {
		private final Iterator<String> accessionIterator = proteins.keySet().iterator();
        private final Iterator<String> accessionIteratorDecoy = proteinsDecoy.keySet().iterator();
        private boolean prescanMode;

        public CruxIdentificationIterator(boolean prescanMode) {
            this.prescanMode = prescanMode;
        }

		public boolean hasNext() {
			return accessionIterator.hasNext() || accessionIteratorDecoy.hasNext();
		}

		public Identification next() {
            if (accessionIterator.hasNext())
			    return convertIdentification(proteins.get(accessionIterator.next()), "t_", "", prescanMode);
            else 
                return convertIdentification(proteinsDecoy.get(accessionIteratorDecoy.next()), "d_", decoyPrefix, prescanMode);
		}

		public void remove() {
			// not supported
		}
	}

    /**
     * Converts from our internal CruxProtein to the DAO representation
     * @param protein
     * @return
     */
	private Identification convertIdentification(CruxProtein protein, String uidPrefix, String publicPrefix, boolean prescanMode) {

		Identification identification = new Identification();
		
		identification.setAccession(publicPrefix + protein.getAccession());
		identification.setScore(0.0);
		identification.setThreshold(0.0);
		identification.setDatabase("Unknown database");
		identification.setDatabaseVersion("Unknown");
		identification.setUniqueIdentifier(uidPrefix + protein.getAccession());
		
		identification.setSearchEngine(searchEngine);

		// process the peptides
		for (Integer cruxPeptideStringIndex : protein.getPeptides()) {
            String[] fields;
            List<String> wholeScan;
            ArrayList<String> currentIndex;
            if ("t_".equals(uidPrefix)) { // target protein
                currentIndex = this.targetFileIndex;
            } else { // decoy protein
                currentIndex = this.decoyFileIndex;
            }

            fields = currentIndex.get(cruxPeptideStringIndex).split("\t");  // split the columns
            
            if (getHighest) {
                // now we get the highest score in the scan. we need this to apply the GET_HIGHEST_SCORE_ITEM PROPERTY 
                wholeScan = getWholeScan(currentIndex, cruxPeptideStringIndex); // get the whole scan lines
                Object newThreshold = this.filter.getHighestScore(header, wholeScan);
                this.filter.setThreshold(newThreshold);
            }
            
            // Check if the entry pass the filter. Otherwise, go for the next line
            // - check also if we get just the highest on per scan
            if ( (this.filter == null) || filter.passFilter(this.header,fields) ) {
                // process the peptide
                CruxPeptide cruxPeptide = CruxPeptide.createCruxPeptide(fields, this.header);

                Peptide peptide = new Peptide();

                peptide.setSequence(cruxPeptide.getSequence());
                peptide.setSpectrumReference(cruxPeptide.getScan());
                peptide.setUniqueIdentifier(uidPrefix + cruxPeptide.getScan() + "_" + cruxPeptide.getXcorrRank());
                peptide.setStart(0);
                peptide.setEnd(0);

                if (prescanMode) {
                    // add the additional info
                    Param additional = new Param();

                    if (!"*".equals(cruxPeptide.getPrevAA(protein.getAccession())))
                        additional.getCvParam().add(DAOCvParams.UPSTREAM_FLANKING_SEQUENCE.getParam(cruxPeptide.getPrevAA(protein.getAccession())));
                    if (!"*".equals(cruxPeptide.getNextAA(protein.getAccession())))
                        additional.getCvParam().add(DAOCvParams.DOWNSTREAM_FLANKING_SEQUENCE.getParam(cruxPeptide.getNextAA(protein.getAccession())));

                    additional.getCvParam().add(DAOCvParams.CHARGE_STATE.getParam(cruxPeptide.getCharge()));
                    additional.getCvParam().add(DAOCvParams.PRECURSOR_MZ.getParam(cruxPeptide.getSpecPrecursorMZ()));
                    additional.getCvParam().add(DAOCvParams.PRECURSOR_MH.getParam(cruxPeptide.getSpecNeutralMass()));
                    additional.getCvParam().add(DAOCvParams.PEPTIDE_RANK.getParam(cruxPeptide.getXcorrRank()));
                    additional.getCvParam().add(DAOCvParams.SEQUEST_DELTA_CN.getParam(cruxPeptide.getDeltaCn()));
                    additional.getCvParam().add(DAOCvParams.SEQUEST_XCORR.getParam(cruxPeptide.getXcorrScore()));

                    peptide.setAdditional(additional);

                    // add the modifications
                    peptide.getPTM().addAll(cruxPeptide.getPTMs(params));

                }

                identification.getPeptide().add(peptide);
            }
		}
		
		return identification;

	}

    /**
     * Assumption: There are 5 entries for each scan
     * @param fileIndex
     * @param i
     * @return
     */
    private List<String> getWholeScan(ArrayList<String> fileIndex, Integer i) {
        List<String> res = null;
        String[] elem = fileIndex.get(i).split("\t");
        int elemScan = Integer.parseInt(elem[header.get("scan")]);
        int elemRank = Integer.parseInt(elem[header.get("xcorr rank")]);
        int firstElemPos = i-elemRank+1;
        int lastElemPos = i;
        int lastElemRank = elemRank;
        // now we are going to move the index to the end of the scan
        boolean found = false;
        while (i<fileIndex.size() && !found) {
            String[] nextElem = fileIndex.get(lastElemPos+1).split("\t");
            int nextElemScan = Integer.parseInt(nextElem[header.get("scan")]);
            found = (elemScan != nextElemScan);
            lastElemPos++;
        }
        if (found) {
            res = fileIndex.subList(firstElemPos, lastElemPos);     
        }
        return res;
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

        // make sure the type was set correctly
        if (spectraFileType == null)
            throw new InvalidFormatException("Unsupported spectra file type used (" + filename + ")");
    }


}