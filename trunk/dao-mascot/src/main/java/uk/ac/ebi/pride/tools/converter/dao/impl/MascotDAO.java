package uk.ac.ebi.pride.tools.converter.dao.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import matrix_science.msparser.ms_inputquery;
import matrix_science.msparser.ms_mascotresfile;
import matrix_science.msparser.ms_mascotresults;
import matrix_science.msparser.ms_peptide;
import matrix_science.msparser.ms_peptidesummary;
import matrix_science.msparser.ms_protein;
import matrix_science.msparser.ms_proteinsummary;
import matrix_science.msparser.ms_searchparams;
import matrix_science.msparser.ms_taxonomychoice;
import matrix_science.msparser.ms_taxonomyfile;
import matrix_science.msparser.vectord;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import uk.ac.ebi.pride.jaxb.model.Data;
import uk.ac.ebi.pride.jaxb.model.IntenArrayBinary;
import uk.ac.ebi.pride.jaxb.model.MzArrayBinary;
import uk.ac.ebi.pride.jaxb.model.Precursor;
import uk.ac.ebi.pride.jaxb.model.PrecursorList;
import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.jaxb.model.SpectrumDesc;
import uk.ac.ebi.pride.jaxb.model.SpectrumInstrument;
import uk.ac.ebi.pride.jaxb.model.SpectrumSettings;
import uk.ac.ebi.pride.tools.converter.dao.DAO;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.dao.DAOProperty;
import uk.ac.ebi.pride.tools.converter.report.model.CV;
import uk.ac.ebi.pride.tools.converter.report.model.Contact;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.DatabaseMapping;
import uk.ac.ebi.pride.tools.converter.report.model.FragmentIon;
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
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.FileUtils;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;
import uk.ac.ebi.pride.tools.converter.utils.config.Configurator;

/**
 * MascotDAO using the Matrix Science ms_parser
 * library to convert Mascot DAT files into PRIDE
 * XML files<br>
 * To convert Mascot results into PRIDE XML files several
 * compromises had to be taken which are as follows:<br>
 * <b>Different Ion Series:</b> Mascot supports the possibility
 * to query spectra where the ion series (b, y, rest) are
 * separated beforehand. These cases are currently not supported
 * by MascotDAO and only ionSeries 1 (as recommended in the ms_parser
 * documentation) is taken into consideration. This should work fine
 * for 99% of the cases.<br>
 * <b>Precursor Charge States:</b> There's currently only one
 * precursor supported per spectrum. Furthermore, as Mascot can
 * report multiple peptides per spectrum the charge state is only
 * reported at the petpide level and NOT at the precursor level.<br>
 * <b>Precursor retention time:</b> Mascot can return multiple retention
 * times for one precursor. The MascotDAO currently only uses the first
 * retention time. <br>
 * <b>Unsupported PRIDE XML objects:</b> The following objects are
 * currently not supported (and thus not returned) by the MascotDAO:
 * Activation parameter, spectrum acquisition parameters. <br>
 * <b>Error Tolerant Searches: </b> Only integrated error tolerant
 * searches are supported by the Mascot DAO as separate error tolerant
 * searches are not recommended by Matrix Science. <br>
 * <b>Quantitation Methods: </b>Quantitation methods are not supported
 * by the MascotDAO. These should generally not be supported by DAOs but by
 * specific QuantitationHandlers. <br>
 * <b>Protein families (Mascot >= V2.3):</b>Protein families cannot be reported
 * in PRIDE XML files. Therefore, the here presented results correspond to the
 * results seen in the older "peptide summary" view. <br>
 * <b>Protein scores in MudPIT experiments:</b> For several reasons when using
 * MudPIT scoring, proteins with only one peptide can have a lower score than
 * the threshold while still being deemed significant identifications. This is caused
 * by the fact that protein thesholds have to be determined by using the
 * average peptide threshold in the file (as recommended in the msparser
 * documentation).
 *
 * @author jg
 */
public class MascotDAO extends AbstractDAOImpl implements DAO {
    /**
     * The mascot result file object
     */
    private ms_mascotresfile mascotFile;
    /**
     * File representing the actual source file on the filesystem.
     */
    private File sourcefile;
    /**
     * File pointing to the temporary copy of the mascot library
     * in case one was created.
     */
    private File tmpMascotLibraryFile;
    /**
     * log4j logger object
     */
    private Logger logger = Logger.getLogger(MascotDAO.class);
    /**
     * String identifying the mascot search engine
     */
    private final String searchEngineString = "Matrix Science Mascot";
    /**
     * Properties object. Initially an empty object. Can be overwritten
     * by setProperties.
     */
    private Properties properties = new Properties();

    /**
     * String to map the mascot varModString to numbers. 0-9 indicate the
     * numbers 0-9, A-W the numbers 10-32
     */
    public final String mascotVarPtmString = "0123456789ABCDEFGHIJKLMNOPQRSTUVW";
    /**
     * Decimal format to use for all doubles
     */
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    /**
     * Creating a (peptide) result summary is a time consuming
     * process. To make sure it's only done once, the function
     * getPeptideResults is used by all functions requiring such
     * an object. Thus, the object only is created once.
     */
    private ms_mascotresults results;
    private ms_mascotresults decoyResults;
    /**
     * A set holding the ids (= numbers) of all empty spectra.
     * These spectra are loaded in the constructer and will be
     * ignored in any iterator.
     */
    private Set<Integer> emptySpectraIds;
    /**
     * Holds the ids (= numbers) of all identified queries. These
     * might still include spectra not identified in the generated
     * result as the significance checks are not performed there.
     */
    private Set<Integer> identifiedSpectra;

    /**
     * Collection to hold all supported properties by this DAO.
     */
    @SuppressWarnings("rawtypes")
    private static Collection<DAOProperty> supportedProperties;
    /**
     * Indicates whether the mascot library was already loaded.
     */
    private static boolean isMascotLibraryLoaded = false;

    /**
     * Just a list of supported properties to keep thing's a little cleaner.
     *
     * @author jg
     */
    private enum SupportedProperties {
        //                       NAME								DEFAULT		TYPE
        MIN_PROPABILITY("min_probability", 0.05, MascotDAO.SupportedProperties.TYPE.DOUBLE),
        USE_MUDPIT_SCORING("use_mudpit_scoring", true, MascotDAO.SupportedProperties.TYPE.BOOLEAN),
        ONLY_SIGNIFICANT("only_significant", true, MascotDAO.SupportedProperties.TYPE.BOOLEAN),
        DUPE_SAME_QUERY("remove_duplicates_same_query", true, MascotDAO.SupportedProperties.TYPE.BOOLEAN),
        DUPE_DIFF_QUERY("remove_duplicates_different_query", false, MascotDAO.SupportedProperties.TYPE.BOOLEAN),
        INCLUDE_ERR_TOL("include_error_tolerant", false, MascotDAO.SupportedProperties.TYPE.BOOLEAN),
        DECOY_PREFIX("decoy_accession_prefix", "DECOY_", MascotDAO.SupportedProperties.TYPE.STRING),
        ENABLE_GROUPING("enable_protein_grouping", true, MascotDAO.SupportedProperties.TYPE.BOOLEAN),
        IGNORE_BELOW_SCORE("ignore_below_ions_score", 0.0, MascotDAO.SupportedProperties.TYPE.DOUBLE),
        COMPATIBILITY_MODE("compatibility_mode", true, MascotDAO.SupportedProperties.TYPE.BOOLEAN),
        REMOVE_EMPTY_SPECTRA("remove_empty_spectra", true, MascotDAO.SupportedProperties.TYPE.BOOLEAN),
        USE_HOMOLOGY_THREHOLD("homology_threshold", false, MascotDAO.SupportedProperties.TYPE.BOOLEAN);

        private String name;
        private Object defaultValue;
        private TYPE type;

        public enum TYPE {
            STRING, BOOLEAN, DOUBLE;
        }

        private SupportedProperties(String name, Object defaultValue, TYPE type) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }

        public TYPE getType() {
            return type;
        }

        @Override
        public String toString() {
            return name + " (" + defaultValue.toString() + ")";
        }
    }

    /**
     * Generates the collection of supported properties if
     * it wasn't created before.
     */
    @SuppressWarnings("rawtypes")
    private static void generateSupportedProperties() {
        // if the collection was already created, return
        if (supportedProperties != null)
            return;

        // create a new collection
        supportedProperties = new ArrayList<DAOProperty>(7);

        // min_propability
        DAOProperty<Double> minProbability = new DAOProperty<Double>(SupportedProperties.MIN_PROPABILITY.getName(), (Double) SupportedProperties.MIN_PROPABILITY.getDefaultValue(), 0.0, 1.0);
        minProbability.setDescription("Specifies a cut-off point for protein scores, a cut-off for an Integrated error tolerant search and a threshold for calculating MudPIT scores. This value represents a probability threshold.");
        minProbability.setShortDescription("Probability cut-off for protein or peptide scores depending on the search (PMF or MS2).");
        supportedProperties.add(minProbability);

        // use MudPIT scoring
        DAOProperty<Boolean> useMudpit = new DAOProperty<Boolean>(SupportedProperties.USE_MUDPIT_SCORING.getName(), (Boolean) SupportedProperties.USE_MUDPIT_SCORING.getDefaultValue());
        useMudpit.setDescription("Indicates whether MudPIT or normal scoring should be used.");
        useMudpit.setShortDescription("Indicates whether MudPIT or normal scoring should be used.");
        supportedProperties.add(useMudpit);

        // only report significant peptides / PMF proteins
        DAOProperty<Boolean> onlySignificant = new DAOProperty<Boolean>(SupportedProperties.ONLY_SIGNIFICANT.getName(), (Boolean) SupportedProperties.ONLY_SIGNIFICANT.getDefaultValue());
        onlySignificant.setDescription("Indicates whether only significant peptides / (in PMF searches) proteins should be included in the generated PRIDE file.");
        onlySignificant.setShortDescription("Only report significant identifications (peptides in MS2 and proteins in PMF searches).");
        supportedProperties.add(onlySignificant);

        // remove duplicates from same query (same sequence, same query)
        DAOProperty<Boolean> dupeSameQuery = new DAOProperty<Boolean>(SupportedProperties.DUPE_SAME_QUERY.getName(), (Boolean) SupportedProperties.DUPE_SAME_QUERY.getDefaultValue());
        dupeSameQuery.setDescription("Indicates whether duplicate peptides having the same sequence and coming from the same query (= spectrum) should be removed. These peptides may have different modifications reported.");
        dupeSameQuery.setShortDescription("Remove duplicate identifications with the same sequence coming from the same spectrum.");
        dupeSameQuery.setAdvanced(true);
        supportedProperties.add(dupeSameQuery);

        // remove duplicates from different query (same sequence, different mods)
        DAOProperty<Boolean> dupeDiffQuery = new DAOProperty<Boolean>(SupportedProperties.DUPE_DIFF_QUERY.getName(), (Boolean) SupportedProperties.DUPE_DIFF_QUERY.getDefaultValue());
        dupeDiffQuery.setDescription("Indicates whether duplicate peptides having the same sequence (but maybe different modifications) coming from different queries (= spectra) should be removed.");
        dupeDiffQuery.setShortDescription("Remove duplicate peptides with the same sequence but coming from different spectra.");
        dupeDiffQuery.setAdvanced(true);
        supportedProperties.add(dupeDiffQuery);

        // error tolerant searches
        DAOProperty<Boolean> errTolSearch = new DAOProperty<Boolean>(SupportedProperties.INCLUDE_ERR_TOL.getName(), (Boolean) SupportedProperties.INCLUDE_ERR_TOL.getDefaultValue());
        errTolSearch.setDescription("Indicates whether integrated error tolerant search results should be included in the PRIDE XML support. These results are not included in the protein scores by Mascot.");
        errTolSearch.setShortDescription("Include error tolerant search results in PRIDE XML file (if present).");
        supportedProperties.add(errTolSearch);

        // decoy accession precursor
        DAOProperty<String> decoyAccPrec = new DAOProperty<String>(SupportedProperties.DECOY_PREFIX.getName(), (String) SupportedProperties.DECOY_PREFIX.getDefaultValue());
        decoyAccPrec.setDescription("An accession prefix that identifies decoy hits. Every protein with an accession starting with this precursor will be flagged as decoy hit. Furthermore, any decoy hit who's accession does not start with this prefix will be altered accordingly.");
        decoyAccPrec.setShortDescription("Protein accession prefix to identify decoy hits.");
        supportedProperties.add(decoyAccPrec);

        // protein grouping (old version)
        DAOProperty<Boolean> proteinGrouping = new DAOProperty<Boolean>(SupportedProperties.ENABLE_GROUPING.getName(), (Boolean) SupportedProperties.ENABLE_GROUPING.getDefaultValue());
        proteinGrouping.setDescription("Indicates whether the grouping mode (Occam's Razor, see Mascot documentation) should be enabled. This is the default behaviour for Mascot. This mode is not equivalent to the protein clustering introduced in Mascot 2.3.");
        proteinGrouping.setShortDescription("Enable Mascot protein grouping mode (Occam's Razor).");
        proteinGrouping.setAdvanced(true);
        supportedProperties.add(proteinGrouping);

        // ignore below ions score (0 default)
        DAOProperty<Double> ignoreIonsScore = new DAOProperty<Double>(SupportedProperties.IGNORE_BELOW_SCORE.getName(), (Double) SupportedProperties.IGNORE_BELOW_SCORE.getDefaultValue(), 0.0, 1.0);
        ignoreIonsScore.setDescription("Peptides with a lower expect ratio (of being false positives) will be ignored completely. Set to 1 to deactivate. Default value is " + SupportedProperties.IGNORE_BELOW_SCORE.getDefaultValue());
        ignoreIonsScore.setShortDescription("Ignore peptides with a lower expect ratio from any further analysis.");
        supportedProperties.add(ignoreIonsScore);

        // compatibility mode
        DAOProperty<Boolean> compMode = new DAOProperty<Boolean>(SupportedProperties.COMPATIBILITY_MODE.getName(), (Boolean) SupportedProperties.COMPATIBILITY_MODE.getDefaultValue());
        compMode.setDescription("If set to true (default) the precuror charge will also be reported at the spectrum level using the best ranked peptide's charge state. This might lead to wrong precursor charges being reported. The correct charge state is always additionally reported at the peptide level.");
        compMode.setShortDescription("Report precursor charges at the spectrum level for compatibility with older applications (can lead to wrong results).");
        compMode.setAdvanced(true);
        supportedProperties.add(compMode);
        
        // remove empty spectra
        DAOProperty<Boolean> removeEmptySpec = new DAOProperty<Boolean>(SupportedProperties.REMOVE_EMPTY_SPECTRA.getName(), (Boolean) SupportedProperties.REMOVE_EMPTY_SPECTRA.getDefaultValue());
        removeEmptySpec.setDescription("If set to true (default) spectra without any peaks are ignored and not reported in the PRIDE XML file.");
        removeEmptySpec.setShortDescription("Do not report empty spectra in the PRIDE XML file.");
        removeEmptySpec.setAdvanced(true);
        supportedProperties.add(removeEmptySpec);
        
        // use homology threshold
        DAOProperty<Boolean> homologyThreshold = new DAOProperty<Boolean>(SupportedProperties.USE_HOMOLOGY_THREHOLD.getName(), (Boolean) SupportedProperties.USE_HOMOLOGY_THREHOLD.getDefaultValue());
        homologyThreshold.setDescription("If set to true (default is \"false\" the homology instead of the identity threshold will be used to identify significant identifications.");
        homologyThreshold.setShortDescription("Use the homology threshold instead of the identity threshold.");
        supportedProperties.add(homologyThreshold);
    }

    /**
     * Returns the current value for the given property.
     *
     * @param property The property to get the current value for.
     * @return An Object representing the property's current value.
     */
    private Object getCurrentProperty(SupportedProperties property) {
        // check if the property was overwritten
        if (properties.containsKey(property.getName())) {
            // save the value
            String value = properties.getProperty(property.getName());

            // parse the property depending on its type (default is string)
            switch (property.getType()) {
                case BOOLEAN:
                    return Boolean.parseBoolean(value);
                case DOUBLE:
                    return Double.parseDouble(value);
                case STRING:
                default:
                    return value;
            }

        } else {
            return property.getDefaultValue();
        }
    }

    /**
     * Used to retrieve the list of supported properties. Properties should nevertheless
     * be set using the setConfiguration method.
     *
     * @return A collection of supported properties.
     */
    @SuppressWarnings("rawtypes")
    public static Collection<DAOProperty> getSupportedProperties() {
        // generate the supported properties (if they weren't created yet)
        generateSupportedProperties();
        return supportedProperties;
    }
    
    @Override
	public void setExternalSpectrumFile(String filename) {
		// not applicable to the dao
	}

    /**
     * Detault constructor. Expects the result file as parameter.
     *
     * @param resultFile
     * @throws InvalidFormatException
     * @throws IllegalArgumentException Thrown if the argument isn't pointing to a valid mascot file. (File is checked for validity)
     * @throws UnsatisfiedLinkError     Thrown if the mascot library could not be found at the expected location.
     */
    public MascotDAO(File resultFile) throws InvalidFormatException {
        try {
            // try to load the mascot libaray
            this.loadMascotLibrary();

            // make sure the file exists
            if (!resultFile.isFile())
                throw new FileNotFoundException();

            logger.debug("Parsing .dat file " + resultFile.getAbsolutePath());

            // create the mascot file
            mascotFile = new ms_mascotresfile(resultFile.getAbsolutePath());

            // check if the file is valid
            if (!mascotFile.isValid())
                throw new InvalidFormatException("Invalid mascot file passed");
            
            // make sure PMF and other query methods are not mixed
            if (mascotFile.isPMF() && (mascotFile.isMSMS() || mascotFile.isSQ()))
            	throw new InvalidFormatException("Cannot handle PMF result files combined with results form other query methods.");

            // save the file's params
            sourcefile = resultFile;
            
            // load the empty spectra
            prescanSpectra();
        } catch (UnsatisfiedLinkError ex) {
            logger.error(ex.getMessage());
            throw new ConverterException("Mascot library not found", ex);
        } catch (FileNotFoundException ex) {
            logger.error(ex.getMessage());
            throw new InvalidFormatException("Mascot result file not found", ex);
        }
    }

    /**
     * Scans all queries in the loaded file and
     * checks for queries without any peaks as well
     * as the ones that lead to a peptide identification.
     */
    private void prescanSpectra() {
		// initialize the empty and identified spectra set
    	emptySpectraIds = new HashSet<Integer>();
    	identifiedSpectra = new HashSet<Integer>();
		
    	// get the results
        ms_mascotresults results = getResults();

        // loop through all spectra
        for (int i = 1; i <= mascotFile.getNumQueries(); i++) {
            // try to create the peptide item for this spectrum - always just use rank = 1
            ms_peptide pep = results.getPeptide(i, 1);

            // check if the query was identified
            if (pep.getAnyMatch())
                identifiedSpectra.add(i);
            
            // check if the query is empty
            ms_inputquery query = new ms_inputquery(mascotFile, i);
            // check for empty peaks only in ion series 1
            if (query.getNumberOfPeaks(1) < 1) {
            	emptySpectraIds.add(i);
            }
        }

        // repeat the same for the decoy results
        results = getDecoyResults();

        // make sure there are any decoy results
        if (results == null)
            return;

        // loop through all spectra
        for (int i = 1; i <= mascotFile.getNumQueries(); i++) {
            // try to create the peptide item for this spectrum - always just use rank = 1
            ms_peptide pep = results.getPeptide(i, 1);

            // check if the query was identified
            if (pep.getAnyMatch())
                identifiedSpectra.add(i);
            
            // check if the query is empty
            ms_inputquery query = new ms_inputquery(mascotFile, i);
            // check for empty peaks only in ion series 1
            if (query.getNumberOfPeaks(1) < 1) {
            	emptySpectraIds.add(i);
            }
        }
	}

	@Override
    protected void finalize() throws Throwable {
        // delete the temporary mascot file if it exists
        if (tmpMascotLibraryFile != null && tmpMascotLibraryFile.exists())
            tmpMascotLibraryFile.delete();

        // call the super function
        super.finalize();
    }

    /**
     * Tries to load the mascot library.
     */
    private void loadMascotLibrary() {
    	if (isMascotLibraryLoaded)
    		return;

        try {
            // load the msparser class

            //determine OS and ARCH
            boolean isLinux = false;
            boolean isAMD64 = false;
            String mascotLibraryFile;
            if (Configurator.getOSName().toLowerCase().contains("linux")) {
                isLinux = true;
            }
            if (Configurator.getOSArch().toLowerCase().contains("amd64")) {
                isAMD64 = true;
            }
            if (isLinux) {
                if (isAMD64) {
                    mascotLibraryFile = "libmsparserj-64.so";
                } else {
                    mascotLibraryFile = "libmsparserj-32.so";
                }
            } else {
                //if not linux, default to windows
                if (isAMD64) {
                    mascotLibraryFile = "msparserj-64.dll";
                } else {
                    mascotLibraryFile = "msparserj-32.dll";
                }
            }
            logger.warn("Using: " + mascotLibraryFile);
            URL mascot_lib = MascotDAO.class.getClassLoader().getResource(mascotLibraryFile);
            if (mascot_lib != null) {

                //need to copy the mascot parser shared object to a location on the filesystem
                //because System.load can't load objects that are packaged within a jar
                logger.debug("Mascot library URL: " + mascot_lib);
                //create a temp file
                tmpMascotLibraryFile = File.createTempFile("libmascot.so.", ".tmp", new File(System.getProperty("java.io.tmpdir")));
                InputStream in = mascot_lib.openStream();
                OutputStream out = new FileOutputStream(tmpMascotLibraryFile);
                //copy file
                IOUtils.copy(in, out);
                in.close();
                out.close();
                //load library
                System.load(tmpMascotLibraryFile.getAbsolutePath());

                isMascotLibraryLoaded = true;
            } else {
                throw new ConverterException("Could not load Mascot Library for system: " + Configurator.getOSName() + Configurator.getOSArch());
            }
        } catch (IOException e) {
            throw new ConverterException("Error loading Mascot library: " + e.getMessage(), e);
        }
    }


    @Override
    public Collection<CV> getCvLookup() {
        // just create a set containing the 2 cvLookups used here
        ArrayList<CV> cvs = new ArrayList<CV>();

        cvs.add(new CV("MS", "PSI Mass Spectrometry Ontology", "1.2", "http://psidev.cvs.sourceforge.net/viewvc/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo"));
        cvs.add(new CV("PRIDE", "PRIDE Controlled Vocabulary", "1.101", "http://ebi-pride.googlecode.com/svn/trunk/pride-core/schema/pride_cv.obo"));

        return cvs;
    }

    @Override
    public String getExperimentTitle() throws InvalidFormatException {
//        // make sure the mascot file contains parameters
//        ms_searchparams params = mascotFile.params();
//
//        if (params == null)
//            throw new InvalidFormatException("Mascot file could not be parsed properly");
//
//        // return the file's search title
//        return params.getCOM();
    	
    	// these title's are generally not sensible
    	return "";
    }

    @Override
    public String getExperimentShortLabel() {
        // this function is not available for mascot files
        return null;
    }

    @Override
    public Param getExperimentParams() {
        // initialize the collection to hold the params
        Param params = new Param();

        // add the date of search
        int seconds = mascotFile.getDate();
        long msec = (long) seconds * (long) 1000;

        // convert the date into a human readable format
        Date searchDate = new Date(msec);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        // date of search
        params.getCvParam().add(DAOCvParams.DATE_OF_SEARCH.getParam(formatter.format(searchDate)));

        // original MS format param
        params.getCvParam().add(DAOCvParams.ORIGINAL_MS_FORMAT.getParam("Mascot dat file"));
        
        // PMF search
        if (mascotFile.isPMF())
        	params.getCvParam().add(DAOCvParams.PMF_SEARCH.getParam());
        if (mascotFile.isSQ())
        	params.getCvParam().add(DAOCvParams.TAG_SEARCH.getParam());
        if (mascotFile.isMSMS())
        	params.getCvParam().add(DAOCvParams.MS_MS_SEARCH.getParam());

        // add a possible FDR
        Double fdr = getFDR();

        if (fdr != null)
            params.getCvParam().add(DAOCvParams.PEPTIDE_FDR.getParam(fdr.toString()));

        return params;
    }

    @Override
    public String getSampleName() {
        // this is not supported by the mascot res file
        return null;
    }

    @Override
    public String getSampleComment() {
        // this is not supported by the mascot res file
        return null;
    }

    @Override
    public Param getSampleParams() {
        // initialize the sample params
        Param sampleParams = new Param();

        // get the taxonomies
        HashMap<Integer, String> taxids = getTaxids();

        //check to see that taxids is not null
        if (taxids != null) {
            // insert the taxids as params
            for (Integer taxid : taxids.keySet())
                sampleParams.getCvParam().add(new CvParam("NEWT", taxid.toString(), taxids.get(taxid), ""));
        }

        return sampleParams;
    }

    /**
     * Extracts the included taxonomies from the taxonomy file
     * and returns them in a HashMap with the taxid as key and the
     * name as value.
     *
     * @return A HashMap of taxonomies found in the file. NULL if the taxonomy file could not be parsed.
     */
    private HashMap<Integer, String> getTaxids() {
        // get the taxonomy file
        ms_taxonomyfile taxFile = new ms_taxonomyfile();

        // try to extract the taxonomy file and check if it's valid
        if (mascotFile.getTaxonomy(taxFile)) {
            if (taxFile.isValid()) {
                // try to parse the taxonomy file and create a set of taxids
                HashMap<Integer, String> taxids = new HashMap<Integer, String>();

                int entries = taxFile.getNumberOfEntries();

                // loop through
                for (int i = 0; i < entries; i++) {
                    ms_taxonomychoice entry = taxFile.getEntryByNumber(i);

                    // loop through the included taxids
                    for (int j = 0; j < entry.getNumberOfIncludeTaxonomies(); j++) {
                        taxids.put(entry.getIncludeTaxonomy(j), entry.getTitle().replace(".", "").trim());
                    }
                }

                return taxids;
            }
        }

        // return null in case the taxonomy file couldn't be parsed
        return null;
    }

    @Override
    public SourceFile getSourceFile() {
        // initialize the return variable
        SourceFile file = new SourceFile();

        file.setPathToFile(sourcefile.getAbsolutePath());
        file.setNameOfFile(sourcefile.getName());
        file.setFileType("Mascot dat file");

        return file;
    }

    @Override
    public Collection<Contact> getContacts() {
        Collection<Contact> contacts = new ArrayList<Contact>(1);

        // if a username is available, create a contact
        if (mascotFile.params().getUSERNAME() != null && mascotFile.params().getUSERNAME().length() > 0) {
            // create the potential one contact
            Contact contact = new Contact();
            contact.setName(mascotFile.params().getUSERNAME());

            if (mascotFile.params().getUSEREMAIL() != null && mascotFile.params().getUSEREMAIL().length() > 0)
                contact.setContactInfo(mascotFile.params().getUSEREMAIL());

            contact.setInstitution(""); // this is not supported

            contacts.add(contact);
        }

        return contacts;
    }

    @Override
    public InstrumentDescription getInstrument() {
        return null;
    }

    @Override
    public Software getSoftware() {
        // initialize the software item
        Software software = new Software();

        software.setName(searchEngineString);
        software.setVersion(mascotFile.getMascotVer());

        return software;
    }

    @Override
    public Param getProcessingMethod() {
    	// report the search engine settings here
    	Param params = new Param();
    	ms_searchparams searchParams = mascotFile.params();
    	
    	if (searchParams == null)
    		return null;
    	
    	// fragment tolerance
    	Double fragmentTolerance = searchParams.getITOL();
    	params.getCvParam().add(DAOCvParams.SEARCH_SETTING_FRAGMENT_MASS_TOLERANCE.getParam(fragmentTolerance + " " + searchParams.getITOLU()));
    	
    	// parent tolerance
    	Double parentTolerance = searchParams.getTOL();    	
    	params.getCvParam().add(DAOCvParams.SEARCH_SETTING_PARENT_MASS_TOLERANCE.getParam(parentTolerance + " " + searchParams.getTOLU()));
    	
    	// missed cleavages
    	params.getCvParam().add(DAOCvParams.SEARCH_SETTING_MISSED_CLEAVAGES.getParam(searchParams.getPFA()));
    	
    	// set the used thresholds
    	params.getCvParam().add(DAOCvParams.MASCOT_SIGNIFICANCE_THRESHOLD.getParam(getCurrentProperty(SupportedProperties.MIN_PROPABILITY)));
    	params.getCvParam().add(DAOCvParams.MASCOT_SIGNIFICANCE_THRESHOLD_TYPE.getParam((Boolean) getCurrentProperty(SupportedProperties.USE_HOMOLOGY_THREHOLD) ? "homology" : "identity"));
    	
        return params;
    }

    /**
     * Returns comma-delimited string containing the names of the used search
     * databases.
     */
    @Override
    public String getSearchDatabaseName() {
        int nDbs = mascotFile.params().getNumberOfDatabases();

        // if there was only one database used, return this database's name
        if (nDbs == 1)
            return mascotFile.params().getDB(1);

        // create the string of databases
        String dbs = "";

        for (int i = 1; i <= nDbs; i++) {
            if (i > 1) dbs += ", ";

            dbs += mascotFile.params().getDB(i);
        }

        return dbs;
    }

    /**
     * Returns a comma-delimited string containing the (fasta) versions of the used
     * search database.
     */
    @Override
    public String getSearchDatabaseVersion() {
        int nDbs = mascotFile.params().getNumberOfDatabases();

        // if there was only one database used, return this database's name
        if (nDbs == 1)
            return mascotFile.getFastaVer(1);

        // create the string of databases
        String versions = "";

        for (int i = 1; i <= nDbs; i++) {
            if (i > 1) versions += ", ";

            versions += mascotFile.getFastaVer(i);
        }

        return versions;
    }

    @Override
    public Collection<DatabaseMapping> getDatabaseMappings() {
        int nDbs = mascotFile.params().getNumberOfDatabases();
        ArrayList<DatabaseMapping> mappings = new ArrayList<DatabaseMapping>();

        for (int i = 1; i <= nDbs; i++) {
            DatabaseMapping mapping = new DatabaseMapping();

            mapping.setSearchEngineDatabaseName(mascotFile.params().getDB(i));
            mapping.setSearchEngineDatabaseVersion(mascotFile.getFastaVer(i));

            mappings.add(mapping);
        }

        return mappings;
    }

    /**
     * Returns a collection of PTMs. The PTMs only contain the
     * searchEnginePTMLabel as well as if they are fixed modifications.
     */
    @Override
    public Collection<PTM> getPTMs() {
        boolean average = mascotFile.params().getMASS().equals("Average");

        // initialize the array of ptms
        ArrayList<PTM> ptms = new ArrayList<PTM>();

        String modName = "";
        int modNumber = 1; // modifications start at one

        do {
            modName = mascotFile.params().getFixedModsName(modNumber);

            if (modName.length() > 0) {
                PTM ptm = new PTM();

                ptm.setFixedModification(true);
                ptm.setSearchEnginePTMLabel(modName);

                // set the specificity
                ptm.setResidues(mascotFile.params().getFixedModsResidues(modNumber).replace("C_term", "1").replace("N_term", "0"));

                //add delta info
                Double modDelta = mascotFile.params().getFixedModsDelta(modNumber);
                if (average) {
                    if (ptm.getModAvgDelta().size() == 0) {
                        ptm.getModAvgDelta().add(modDelta.toString());
                    }
                } else {
                    if (ptm.getModMonoDelta().size() == 0) {
                        ptm.getModMonoDelta().add(modDelta.toString());
                    }
                }

                ptms.add(ptm);
            }

            modNumber++;
        } while (modName.length() > 0);

        // process the variable modifications
        modName = "";
        modNumber = 1; // modifications start at one

        // get the var mod residues
        Map<String, String> varModResidues = getVarModResidues();

        do {
            modName = mascotFile.params().getVarModsName(modNumber);

            if (modName.length() > 0) {
                PTM ptm = new PTM();

                ptm.setFixedModification(false);
                ptm.setSearchEnginePTMLabel(modName);

                // add the residue info if available
                if (varModResidues.containsKey(modName))
                    ptm.setResidues(varModResidues.get(modName));
                else
                    logger.warn("No residue information available for modification '" + modName + "'");

                //add delta info
                Double modDelta = mascotFile.params().getVarModsDelta(modNumber);
                if (average) {
                    if (ptm.getModAvgDelta().size() == 0) {
                        ptm.getModAvgDelta().add(modDelta.toString());
                    }
                } else {
                    if (ptm.getModMonoDelta().size() == 0) {
                        ptm.getModMonoDelta().add(modDelta.toString());
                    }
                }

                ptms.add(ptm);
            }

            modNumber++;
        } while (modName.length() > 0);

        return ptms;
    }

    /**
     * Adds the amino acid specificity information
     * to the found variable modifications.
     *
     * @param ptms
     * @return
     */
    private Map<String, String> getVarModResidues() {
        // initialize the map of mod -> residues
        HashMap<String, HashSet<Character>> varModResidues = new HashMap<String, HashSet<Character>>();

        ms_mascotresults results = getResults();

        // loop through all found peptides
        for (int queryNum = 1; queryNum <= mascotFile.getNumQueries(); queryNum++) {
            // only check ranked 1 peptides
            ms_peptide peptide = results.getPeptide(queryNum, 1);

            // make sure there is an identification
            if (!peptide.getAnyMatch())
                continue;

            // get the peptide's modification string
            String modString = peptide.getVarModsStr();

            // loop through the peptide modString
            for (Integer position = 0; position < modString.length(); position++) {
                // get the character at the position and convert it into a number
                char modChar = modString.charAt(position);
                int modNumber = mascotVarPtmString.indexOf(modChar);

                // make sure the modChar could be assigned to a modification number
                if (modNumber == -1)
                    continue;

                // if the modNumber = 0, no modification was set
                if (modNumber == 0 && modChar != 'X')
                    continue;

                // get the ptm's info
                String name;

                if (modChar != 'X')
                    name = mascotFile.params().getVarModsName(modNumber);
                else
                    name = results.getErrTolModName(peptide.getQuery(), peptide.getRank());

                // make sure the modification is defined
                if (name.length() < 1)
                    continue;

                // add the residue to the string
                if (!varModResidues.containsKey(name))
                    varModResidues.put(name, new HashSet<Character>());

                // check if it's at the N-term
                if (position == 0)
                    varModResidues.get(name).add('0');
                    // check if it's at the C-term
                else if (position > peptide.getPeptideStr().length())
                    varModResidues.get(name).add('1');
                    // add the AA code from the peptide's sequence
                else
                    varModResidues.get(name).add(peptide.getPeptideStr().charAt(position - 1));
            }
        }

        // sort the string
        HashMap<String, String> varModResidueStrings = new HashMap<String, String>();

        for (String modName : varModResidues.keySet()) {
            ArrayList<Character> chars = new ArrayList<Character>(varModResidues.get(modName));
            // sort the characters
            Collections.sort(chars);

            String residueString = "";

            for (Character c : chars)
                residueString += c;

            varModResidueStrings.put(modName, residueString);
        }

        return varModResidueStrings;
    }

    @Override
    public SearchResultIdentifier getSearchResultIdentifier() {
        // intialize the search result identifier
        SearchResultIdentifier identifier = new SearchResultIdentifier();

        // format the current time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        identifier.setSourceFilePath(sourcefile.getAbsolutePath());
        identifier.setTimeCreated(formatter.format(new Date(System.currentTimeMillis())));
        identifier.setHash(FileUtils.MD5Hash(sourcefile.getAbsolutePath()));

        return identifier;
    }

    @Override
    public int getSpectrumCount(boolean onlyIdentified) {
        int count = 0;
        
        // in PMF searches all queries are returned as 1 spectrum
        if (mascotFile.isPMF())
        	return 1;

        if (onlyIdentified) {
            count = identifiedSpectra.size();
        } else {
            count = mascotFile.getNumQueries() - emptySpectraIds.size();
        }

        return count;
    }

    @Override
    public Iterator<Spectrum> getSpectrumIterator(boolean onlyIdentified) {
        // create a new iterator
        return new MascotSpectrumIterator(onlyIdentified);
    }

    private class MascotSpectrumIterator implements Iterator<Spectrum> {
        /**
         * A list of spectrum ids, used if only spectra with identifications should be returned
         */
        private ArrayList<Integer> queryIds = new ArrayList<Integer>();
        /**
         * Indicates whether only spectra with identifications or all of them should be returned
         */
        private boolean onlyIdentified = false;
        /**
         * current index, either in the queryIds array or in the mascot file
         */
        private int currentIndex = 0;
        /**
         * number of spectra in the mascot file
         */
        private final int nSpectra;
        /**
         * The difference between the actual query id
         * and the currentIndex. As Mascot queries are
         * 1-based and the currentIndex is 0-based the
         * factor is always 1. It's incremented as soon
         * as empty spectra are encountered and need to be
         * ignored.
         */
        private int correctionFactor = 1;

        /**
         * The default constructor.
         *
         * @param onlyIdentified Indicates whether all available spectra or only spectra with a peptide identification should be returned.
         */
        public MascotSpectrumIterator(boolean onlyIdentified) {
            this.onlyIdentified = onlyIdentified;
            
            // set the number of spectra ignoring the empty spectra if the option was set
            if (mascotFile.isPMF())
            	nSpectra = 1;
            else
            	nSpectra = mascotFile.getNumQueries() - ((Boolean) getCurrentProperty(SupportedProperties.REMOVE_EMPTY_SPECTRA) ? emptySpectraIds.size() : 0);

            // load the identified query ids if necessary
            if (onlyIdentified) {
            	queryIds.addAll(identifiedSpectra);
            	Collections.sort(queryIds);
            }
        }

        @Override
        public boolean hasNext() {
        	// only 1 spectrum reported in PMF queries
        	if (mascotFile.isPMF())
        		return currentIndex < 1;
        	
            // check if the current index exists
            if (onlyIdentified) {
                return currentIndex < queryIds.size();
            } else {
                return currentIndex < nSpectra;
            }
        }

        @Override
        public Spectrum next() {
        	if (mascotFile.isPMF()) {
        		currentIndex++;
        		return createPMFSpectrum();
        	}
        	
            // get the ms_inputquery for the current index
            int spectrumIndex = -1;

            // the peptide UIDs are string encoded spectrum indexes
            if (onlyIdentified) {
                spectrumIndex = queryIds.get(currentIndex);
            } else {
                // in the mascot result file queries are accessed 1-based
                spectrumIndex = currentIndex + correctionFactor;
                
                // ignore empty spectra if the option was set
                if ((Boolean) getCurrentProperty(SupportedProperties.REMOVE_EMPTY_SPECTRA)) {
	                while (emptySpectraIds.contains(spectrumIndex)) {
	                	// increment the correction factor thereby skipping the
	                	// empty spectrum's position
	                	correctionFactor++;
	                	// calculate the new spectrum index.
	                	spectrumIndex = currentIndex + correctionFactor;
	                }
                }
            }

            currentIndex++;

            // make sure the spectrumIndex points to a spectrum
            if (spectrumIndex < 1 || currentIndex > nSpectra)
                return null;

            // get the ms_inputquery
            ms_inputquery query = new ms_inputquery(mascotFile, spectrumIndex);

            try {
                return createSpectrum(query, spectrumIndex);
            } catch (InvalidFormatException e) {
                throw new ConverterException(e);
            }
        }

        /**
         * Creates a spectrum object from the passed PMF queries
         * entered by the user.
         * @return
         */
        private Spectrum createPMFSpectrum() {
        	Spectrum spectrum = new Spectrum();
        	
        	spectrum.setId(1);
        	
        	// create the peak list
            ArrayList<Double> masses = new ArrayList<Double>();
            ArrayList<Double> intensities = new ArrayList<Double>();
            
            for (int i = 1; i <= mascotFile.getNumQueries(); i++) {
            	// get the m/z value
            	Double mz = mascotFile.getObservedMass(i);
            	Double intensity = mascotFile.getObservedIntensity(i);
            	
            	// if the intensity is not available (0) set it to 1
            	if (intensity == 0)
            		intensity = 1.0;
            	
            	masses.add(mz);
            	intensities.add(intensity);
            }
            
            // create the byte arrays
            byte[] massesBytes = doubleCollectionToByteArray(masses);
            byte[] intenBytes = doubleCollectionToByteArray(intensities);

            // create the intensity array
            Data intenData = new Data();
            intenData.setEndian("little");
            intenData.setLength(intenBytes.length);
            intenData.setPrecision("64"); // doubles are 64 bit in java
            intenData.setValue(intenBytes);

            IntenArrayBinary intenArrayBin = new IntenArrayBinary();
            intenArrayBin.setData(intenData);

            // create the mass data array
            Data massData = new Data();
            massData.setEndian("little");
            massData.setLength(massesBytes.length);
            massData.setPrecision("64");
            massData.setValue(massesBytes);

            MzArrayBinary massArrayBinary = new MzArrayBinary();
            massArrayBinary.setData(massData);

            // store the mz and intensities in the spectrum
            spectrum.setIntenArrayBinary(intenArrayBin);
            spectrum.setMzArrayBinary(massArrayBinary);
            
         	// create the spectrum description
            SpectrumDesc description = new SpectrumDesc();

            // create the spectrumSettings/spectrumInstrument (mzRangeStop, mzRangeStart, msLevel)
            SpectrumSettings settings = new SpectrumSettings();
            SpectrumInstrument instrument = new SpectrumInstrument();

            instrument.setMsLevel(1);
            Float rangeStart = Collections.min(masses).floatValue();
            Float rangeStop = Collections.max(masses).floatValue();

            instrument.setMzRangeStart(rangeStart);
            instrument.setMzRangeStop(rangeStop);

            // set the spectrum settings
            settings.setSpectrumInstrument(instrument);
            description.setSpectrumSettings(settings);

            spectrum.setSpectrumDesc(description);
            
			return spectrum;
		}

		@Override
        public void remove() {
            // this function is not supported
        }

        private Spectrum createSpectrum(ms_inputquery query, int spectrumId) throws InvalidFormatException {
            // initialize the spectrum
            Spectrum spectrum = new Spectrum();

            // set the id
            spectrum.setId(spectrumId);

            // create the peak list
            ArrayList<Double> masses = new ArrayList<Double>();
            ArrayList<Double> intensities = new ArrayList<Double>();

            // currently, only ion series 1 is supported
            for (int ions = 1; ions <= 1; ions++) {
                int numPeaks = query.getNumberOfPeaks(ions);

                for (int peakNo = 1; peakNo <= numPeaks; peakNo++) {
                    masses.add(query.getPeakMass(ions, peakNo));
                    intensities.add(query.getPeakIntensity(ions, peakNo));
                }
            }

            // create the byte arrays
            byte[] massesBytes = doubleCollectionToByteArray(masses);
            byte[] intenBytes = doubleCollectionToByteArray(intensities);

            // create the intensity array
            Data intenData = new Data();
            intenData.setEndian("little");
            intenData.setLength(intenBytes.length);
            intenData.setPrecision("64"); // doubles are 64 bit in java
            intenData.setValue(intenBytes);

            IntenArrayBinary intenArrayBin = new IntenArrayBinary();
            intenArrayBin.setData(intenData);

            // create the mass data array
            Data massData = new Data();
            massData.setEndian("little");
            massData.setLength(massesBytes.length);
            massData.setPrecision("64");
            massData.setValue(massesBytes);

            MzArrayBinary massArrayBinary = new MzArrayBinary();
            massArrayBinary.setData(massData);

            // store the mz and intensities in the spectrum
            spectrum.setIntenArrayBinary(intenArrayBin);
            spectrum.setMzArrayBinary(massArrayBinary);

            // add the spectrum description
            spectrum.setSpectrumDesc(generateSpectrumDescription(query, spectrumId));

            return spectrum;
        }

        /**
         * Generates the SpectrumDesc object for the passed spectrum. <br>
         * A charge state is only reported at the peptide level
         *
         * @param query      The ms_inputquery object representing the given spectrum.
         * @param spectrumId The spectrum's id in the mascot file (1-based)
         * @return The SpectrumDesc object for the given spectrum
         */
        private SpectrumDesc generateSpectrumDescription(ms_inputquery query, int spectrumId) {
            // initialize the spectrum description
            SpectrumDesc description = new SpectrumDesc();

            // set the ms level based on the type of search performed
            int msLevel = 0;
            if (mascotFile.isMSMS())
                msLevel = 2;
            else
                logger.error("Spectrum msLevel cannot be determined for non-MS/MS experiments.");

            // create the spectrumSettings/spectrumInstrument (mzRangeStop, mzRangeStart, msLevel)
            SpectrumSettings settings = new SpectrumSettings();
            SpectrumInstrument instrument = new SpectrumInstrument();

            instrument.setMsLevel(msLevel);
            Float rangeStart = new Float((query.getMinInternalMass() != -1) ? query.getMinInternalMass() : query.getMassMin());
            Float rangeStop = new Float((query.getMaxInternalMass() != -1) ? query.getMaxInternalMass() : query.getMassMax());

            instrument.setMzRangeStart(rangeStart);
            instrument.setMzRangeStop(rangeStop);

            // set the spectrum settings
            settings.setSpectrumInstrument(instrument);
            description.setSpectrumSettings(settings);

            // create the precursor list
            PrecursorList precList = new PrecursorList();

            // currently, there's only one precursor supported
            precList.setCount(1);

            Precursor prec = new Precursor();
            prec.setMsLevel(msLevel - 1);

            Spectrum spec = new Spectrum();
            spec.setId(0);
            prec.setSpectrum(spec);

            uk.ac.ebi.pride.jaxb.model.Param ionSelection = new uk.ac.ebi.pride.jaxb.model.Param();

            if (mascotFile.getObservedIntensity(spectrumId) != 0)
                ionSelection.getCvParam().add(DAOCvParams.PRECURSOR_INTENSITY.getJaxbParam(new Double(mascotFile.getObservedIntensity(spectrumId)).toString()));
            if (mascotFile.getObservedMass(spectrumId) != 0)
                ionSelection.getCvParam().add(DAOCvParams.PRECURSOR_MZ.getJaxbParam(new Double(mascotFile.getObservedMass(spectrumId)).toString()));
            if (query.getRetentionTimes().length() > 0)
                ionSelection.getCvParam().add(DAOCvParams.RETENTION_TIME.getJaxbParam(query.getRetentionTimes()));
            // if in compatibility mode add the charge state of the first peptide
            if ((Boolean) getCurrentProperty(SupportedProperties.COMPATIBILITY_MODE)) {
                ms_mascotresults res = getResults();
                ms_peptide p = res.getPeptide(spectrumId, 1);

                // if the peptide was identified
                if (p.getAnyMatch()) {
                    ionSelection.getCvParam().add(DAOCvParams.CHARGE_STATE.getJaxbParam(new Integer(p.getCharge()).toString()));
                }
            }

            // save the ionselection
            prec.setIonSelection(ionSelection);

            // currently, no activation parameters supported
            prec.setActivation(new uk.ac.ebi.pride.jaxb.model.Param());

            // add the (only) precursor to the precursor list and save it in the description item
            precList.getPrecursor().add(prec);
            description.setPrecursorList(precList);

            return description;
        }
    }

    @Override
    public int getSpectrumReferenceForPeptideUID(String peptideUID) {
    	// in PMF queries there is always only 1 spectrum
    	if (mascotFile.isPMF())
    		return 1;
    	
        // extract the query id from the peptide UID
        int index = peptideUID.indexOf("_");

        // if the "_" could not be found the peptideUID is not valid, thus -1 is returned
        if (index == -1)
            return -1;

        // extract the queryId (everything before the "_")
        String strQueryId = peptideUID.substring(0, index);

        // return the queryId (as integer)
        return Integer.parseInt(strQueryId);
    }


    @Override
    public Identification getIdentificationByUID(String proteinUID) throws InvalidFormatException {
        boolean isDecoy = false;

        // check if it's a decoy hit
        if (proteinUID.startsWith("d_")) {
            proteinUID = proteinUID.substring(2);
            isDecoy = true;
        }

        // get the index
        Integer index = Integer.parseInt(proteinUID);

        // get the protein
        ms_mascotresults res = (isDecoy) ? getDecoyResults() : getResults();
        ms_protein p = res.getHit(index);

        if (p == null)
            throw new InvalidFormatException("Protein with uid = " + proteinUID + " could not be found (decoy = " + isDecoy + ")");

        // create and return the identification
        return createIdentification(p, index, false, isDecoy);
    }

    @Override
    public Iterator<Identification> getIdentificationIterator(
            boolean prescanMode) {
        return new MascotIdentificationIterator(prescanMode);
    }

    private class MascotIdentificationIterator implements Iterable<Identification>, Iterator<Identification> {
        /**
         * Current position in the identifications
         */
        private int index = 1;
        /**
         * Total number of identifications available
         */
        private int size;
        /**
         * Indicates whether iterator should return pre-scan or scan (= complete) objects.
         */
        private final boolean prescanMode;
        /**
         * A local representation of the ms_mascotresults. This is
         * to make sure that the results were created.
         */
        private ms_mascotresults localResults;
        /**
         * Decoy results are processed after the true results.
         */
        private ms_mascotresults localDecoyResults;
        /**
         * Indicates whether the current protein is a decoy result
         */
        private boolean isDecoyHit;
        /**
         * In PMF based experiments the proteins need to be checked
         * whether they are significant hits
         */
        private List<Integer> significantPMFIdentifications;

        /**
         * Default constructor
         *
         * @param prescanMode Boolean to indicate whether complete or pre-scan objects should be returned.
         */
        public MascotIdentificationIterator(boolean prescanMode) {
        	// set the prescan mode
            this.prescanMode = prescanMode;

            // check if there are any peptide summaries
            if (!mascotFile.anyPeptideSummaryMatches() && !mascotFile.anyPMF()) {
                size = 0;
                return;
            }
            
            // in PMF queries load the significant identifications if required
            if (mascotFile.isPMF() && (Boolean) getCurrentProperty(SupportedProperties.ONLY_SIGNIFICANT)) {
            	loadSignificantPMFHits();
            }

            // create the results
            localResults = getResults();
            localDecoyResults = getDecoyResults();

            // initialize the size
            size = localResults.getNumberOfHits();

            // add the number of decoy hits if there are any
            if (localDecoyResults != null)
                size += localDecoyResults.getNumberOfHits();
        }

        /**
         * Loads the significant PMF (protein)
         * hits into the significantPMFIdentifications
         * list.
         */
        private void loadSignificantPMFHits() {
        	// make sure it's a PMF result
        	if (!mascotFile.isPMF())
        		return;
        	
			significantPMFIdentifications = new ArrayList<Integer>();
			ms_mascotresults results = getResults();
			Double threshold = new Double(
					results.getProteinThreshold(1 / (Double) getCurrentProperty(SupportedProperties.MIN_PROPABILITY)));
			
			// check all proteins whether their score is above the threshold
			for (int i = 1; i <= results.getNumberOfHits(); i++) {
				ms_protein protein = results.getHit(i);
				
				if (protein.getScore() > threshold)
					significantPMFIdentifications.add(i);
			}
		}

		@Override
        public boolean hasNext() {
            // if the significantPMFIdentifications is not null, use them
			if (significantPMFIdentifications != null)
				return index <= significantPMFIdentifications.size();
			
			// check if the current index points to a valid entry (array is 1-based)
            return index >= 1 && index <= size;
        }

        @Override
        public Identification next() {
            ms_protein protein;
            int resultIndex = 0; // this variable represents the index of the protein as it's found in the results object

            // first check if the significant pmf hits should be used
            if (significantPMFIdentifications != null) {
            	resultIndex = significantPMFIdentifications.get(index - 1);
            	protein = localResults.getHit(resultIndex);
            	isDecoyHit = false; // no decoy hits supported for PMF based results
            }
            // load the identification from the msacot file or the decoy results, depending on the current size
            else if (index <= localResults.getNumberOfHits()) {
                protein = localResults.getHit(index);
                resultIndex = index;
                isDecoyHit = false;
            } else {
                isDecoyHit = true;

                // check if a decoy result is available
                if (localDecoyResults != null)
                    protein = localDecoyResults.getHit(index - localResults.getNumberOfHits());
                else
                    protein = null;

                resultIndex = index - localResults.getNumberOfHits();
            }

            index++;

            // make sure the protein exists
            if (protein == null)
                return null;

            // create the identification
            Identification ident = createIdentification(protein, resultIndex, prescanMode, isDecoyHit);

            // if the identification does not contain any peptides, get the next identification
            if (ident.getPeptide().size() < 1) {
                if (hasNext())
                    ident = next();
                else
                    ident = null;
            }

            return ident;
        }

        @Override
        public void remove() {
            // this function is not supported
        }

        @Override
        public Iterator<Identification> iterator() {
            return this;
        }

    }

    /**
     * Creates an Identification object based on a ms_protein
     * object. In pre-scan mode the peptide's fragment ion
     * annotations are omitted. Furthermore, the different handlers
     * (QuantitationHandler, etc.) are only called in pre-scan mode
     * as their information should be included in the report file.
     * Additional parameters (for both peptide and protein) as well as
     * peptide PTMs are only reported in pre-scan mode.
     *
     * @param protein     The ms_protein object to create the Identification object from.
     * @param preScanMode
     * @param decoyHit
     * @return The Identification object representing the ms_protein object.
     */
    private Identification createIdentification(ms_protein protein, Integer index, boolean preScanMode, boolean decoyHit) {
        // create the Identifications object
        Identification ident = new Identification();

        // set the standard values
        String decoyPrefix = (String) getCurrentProperty(SupportedProperties.DECOY_PREFIX);
        
        if (decoyHit && decoyPrefix != null && !protein.getAccession().startsWith(decoyPrefix))
        	ident.setAccession(decoyPrefix + protein.getAccession());
        else
        	ident.setAccession(protein.getAccession());
        // accession version, spliceisoform is not supported
        ident.setUniqueIdentifier(((decoyHit) ? "d_" : "") + index.toString());     // just use the index as unique identifier

        // set the search database and version from which the protein was identified
        ident.setDatabase(mascotFile.params().getDB(protein.getDB()));
        ident.setDatabaseVersion(mascotFile.getFastaVer(protein.getDB()));

        ident.setScore(roundDouble(protein.getScore()));

        // for protein summaries this call should use getProteinScoreCutoff
        // divide 1 / minProp to get the 1inX score
        ms_mascotresults res = (decoyHit) ? getDecoyResults() : getResults();
        
        // set the threshold depending on whether it's a PMF or MS/MS based query
        if (mascotFile.isPMF()) {
        	Double threshold = new Double(res.getProteinThreshold(1 / (Double) getCurrentProperty(SupportedProperties.MIN_PROPABILITY)));
        	ident.setThreshold(threshold);
        }
        else {
        	// this cannot be adapted for the homology threshold as Mascot doesn't support protein thresholds
        	Double threshold = new Double(res.getAvePeptideIdentityThreshold(1 / (Double) getCurrentProperty(SupportedProperties.MIN_PROPABILITY)));
        	
        	ident.setThreshold(threshold);
        }

        ident.setSearchEngine(searchEngineString);

        // insert the peptides
        ident.getPeptide().addAll(getProteinPeptides(protein, preScanMode, decoyHit));

        // set the additional parameter(s)
        if (preScanMode) {
            Param additional = new Param();

            // add the protein name
            String description = results.getProteinDescription(protein.getAccession());
            if (description.length() > 0)
                additional.getCvParam().add(DAOCvParams.PROTEIN_NAME.getParam(description));

            // check if there are alternative accessions for this protein and add them as secondary accessions
            int similarHitIndex = 1;
            ms_protein simProtein = res.getNextSimilarProtein(protein.getHitNumber(), similarHitIndex);

            while (simProtein != null) {
                // save the accession as secondary accession
                if (simProtein.getGrouping() == ms_protein.GROUP_COMPLETE)
                    additional.getCvParam().add(DAOCvParams.INDISTINGUISHABLE_ACCESSION.getParam(simProtein.getAccession()));

                // get the next similar hit protein
                simProtein = res.getNextSimilarProtein(protein.getHitNumber(), ++similarHitIndex);
            }

            // check for decoy hits
            if (decoyHit || (decoyPrefix != null && ident.getAccession().startsWith(decoyPrefix)))
                additional.getCvParam().add(DAOCvParams.DECOY_HIT.getParam());

            // make sure the protein contains significant peptides
            boolean signifcantPeptides = false;

            for (Peptide peptide : ident.getPeptide()) {
                boolean isSignificant = true;

                for (CvParam param : peptide.getAdditional().getCvParam()) {
                    if (param.getAccession().equals(DAOCvParams.NON_SIGNIFICANT_PEPTIDE.getAccession())) {
                        isSignificant = false;
                        break;
                    }
                }

                if (isSignificant) {
                    signifcantPeptides = true;
                    break;
                }
            }

            // label non-significant identifications
            if (!signifcantPeptides)
                additional.getCvParam().add(DAOCvParams.NON_SIGNIFICANT_PROTEIN.getParam());
            
            if (mascotFile.isPMF())
            	additional.getCvParam().add(DAOCvParams.PMF_IDENTIFICATION.getParam());
            if (mascotFile.isMSMS())
            	additional.getCvParam().add(DAOCvParams.MS_MS_IDENTIFICATION.getParam());

            ident.setAdditional(additional);
        }

        return ident;
    }

    /**
     * Round the double using the decimalFormat.
     *
     * @param d
     * @return
     */
    private Double roundDouble(Double d) {
        Double value = Double.valueOf(decimalFormat.format(d));

        return value;
    }

    /**
     * Checks whether a peptide is "valid" which means, checks whether the peptide
     * should be reported as part of an identification.
     * Duplicate peptides as well as insignificant peptides are being removed.
     *
     * @param protein       The ms_protein in which the peptide was identified.
     * @param peptideNumber The peptide's number within the protein.
     * @param isDecoyHit
     * @return
     */
    private boolean isPeptideValid(ms_protein protein, int peptideNumber, ms_peptide peptide, boolean isDecoyHit) {
    	// if the protein is a PMF hit, all peptides are "valid" as they have no scores
    	if (mascotFile.isPMF())
    		return true;
    	
        // exclude peptides that come form the same query with the same sequence
        if ((Boolean) getCurrentProperty(SupportedProperties.DUPE_SAME_QUERY) &&
                protein.getPeptideDuplicate(peptideNumber) == ms_protein.DUPE_DuplicateSameQuery) {
            logger.debug(protein.getAccession() + " - " + peptide.getPeptideStr() + " (" + peptide.getVarModsStr() + "): Duplicate Same query");
            return false;
        }

        // another peptide with different query but same sequence got higher score
        if ((Boolean) getCurrentProperty(SupportedProperties.DUPE_DIFF_QUERY) &&
                protein.getPeptideDuplicate(peptideNumber) == ms_protein.DUPE_Duplicate) {
            logger.debug(protein.getAccession() + " - " + peptide.getPeptideStr() + " (" + peptide.getVarModsStr() + "): Duplicate");
            return false;
        }

        // ignore non-significant peptides (if the option is set)
        ms_mascotresults res = (isDecoyHit) ? getDecoyResults() : getResults();
        int th = 0;
        if ( (Boolean) getCurrentProperty(SupportedProperties.USE_HOMOLOGY_THREHOLD))
        	th = res.getHomologyThreshold(peptide.getQuery(), 1 / (Double) getCurrentProperty(SupportedProperties.MIN_PROPABILITY), peptide.getRank());
        else
        	th = res.getPeptideIdentityThreshold(peptide.getQuery(), 1 / (Double) getCurrentProperty(SupportedProperties.MIN_PROPABILITY));

        if ((Boolean) getCurrentProperty(SupportedProperties.ONLY_SIGNIFICANT) && peptide.getIonsScore() < th) {
            logger.debug(protein.getAccession() + " - " + peptide.getPeptideStr() + "(" + peptide.getQuery() + " - " + peptide.getVarModsStr() + "): Not significant score: " + peptide.getIonsScore() + " < " + th);
            return false;
        }

        logger.debug(protein.getAccession() + " - " + peptide.getPeptideStr() + "(" + peptide.getQuery() + " - " + peptide.getVarModsStr() + "):--OK-- score: " + peptide.getIonsScore() + " < " + th);

        return true;
    }

    /**
     * Creates a list of peptides that are assigned to this specific
     * protein. PTMs as well as additional parameters are only
     * returned in prescan mode.
     *
     * @param protein     The ms_protein to create the peptides for.
     * @param prescanMode
     * @param decoyHit    Indicates whether the passed protein is a decoy hit.
     * @return A List of Peptides for the specific protein.
     */
    private List<Peptide> getProteinPeptides(ms_protein protein, boolean prescanMode, boolean decoyHit) {
        // initialize the list of peptides
        ArrayList<Peptide> peptides = new ArrayList<Peptide>();

        // loop through all peptides
        for (int i = 1; i <= protein.getNumPeptides(); i++) {
            // get the peptides basic indexes
            int queryId = protein.getPeptideQuery(i);
            int rank = protein.getPeptideP(i);

            // make sure the peptide is part of a query
            if (queryId == -1)
                continue;

            // get the peptide object
            ms_peptide msPep = results.getPeptide(queryId, rank);

            // make sure the peptide actually exists
            if (!msPep.getAnyMatch())
                continue;

            logger.debug("----------------------------------");

            // make sure the peptide should be displayed
            if (!isPeptideValid(protein, i, msPep, decoyHit))
                continue;

            logger.debug("Processing peptide: " + msPep.getPeptideStr());
            logger.debug("Protein Accession:  " + protein.getAccession());
            logger.debug("Query id:           " + queryId);
            logger.debug("Peaks used:         " + msPep.getPeaksUsedFromIons1());

            // create the xmlPeptide object
            Peptide peptide = new Peptide();

            peptide.setSequence(msPep.getPeptideStr()); // this can be an empty string if there was no identification
            peptide.setUniqueIdentifier(queryId + "_" + rank); // the peptide UID is created [queryId]_[rank]
            // in PMF based experiments there are no spectrum refs
            if (mascotFile.isPMF())
            	peptide.setSpectrumReference(1);
            else
            	peptide.setSpectrumReference(queryId);
            
            peptide.setIsSpecific(msPep.getNumProteins() == 1); // a peptide is set specific if it only fits one protein
            peptide.setStart(protein.getPeptideStart(i));
            peptide.setEnd(protein.getPeptideEnd(i));

            // peptide additional information

            // fields
            /*
             * quantitation method component string (getComponentStr)
             * mass delta between experimental and calculated rel mass (getDelta)
             * isErrorTolerant
             * getMissedCleavages
             * getMrCalc: the relative calculated mass for the peptide
             * getMrExperimental: the observed mz value as a relative mass
             * getNum13C: number of 13C peaks offset - Quant - some quantitation related problem
             * getNumProteins
             * getObserved: the observed mass / charge value
             * getPeaksUsedFromIons[1-3]: only support for ion series 1 - change it if we need to
             */

            // modifications
            if (prescanMode) {
                peptide.getPTM().addAll(createFixedPeptidePTMs(msPep)); // add the fixed modifications
                peptide.getPTM().addAll(createVarPeptidePTMs(msPep, prescanMode));   // add the variable modifications
            }

            // get the fragments that matched used peaks by mascot
            // only include fragment ions if it's not the pre-scan
            if (!prescanMode) {
                // fragment ions
                HashMap<String, Double> theoreticalFragments = createTheoreticalFragments(msPep.getPeptideStr(),
                        getPeptideMassChanges(msPep, prescanMode),
                        msPep.getSeriesUsedStr());

                Double tolerance = mascotFile.params().getITOL();

                // if the tolerance is specified in mmu convert it to Dalton
                if (mascotFile.params().getITOLU().equals("mmu"))
                    tolerance = tolerance / 1000;

                List<FragmentIon> fragmentIons = getMatchedFragments(theoreticalFragments,
                        new ms_inputquery(mascotFile, queryId),
                        1, // only use ionSeries 1
                        msPep.getPeaksUsedFromIons1(),
                        tolerance);

                peptide.getFragmentIon().addAll(fragmentIons);
            }

            // peptide additional params
            if (prescanMode) {
                Param additional = new Param();

                if (mascotFile.isMSMS())
                    additional.getCvParam().add(DAOCvParams.MS_MS_IDENTIFICATION.getParam());
                if (mascotFile.isPMF()) {
                    additional.getCvParam().add(DAOCvParams.PMF_IDENTIFICATION.getParam());
                    // the assigned m/z value to this peptide
                    additional.getCvParam().add(DAOCvParams.UNIT_MZ.getParam(mascotFile.getObservedMass(queryId)));
                }

                // score
                if (!mascotFile.isPMF())
                	additional.getCvParam().add(DAOCvParams.MASCOT_SCORE.getParam(new Double(msPep.getIonsScore()).toString()));
                // rank
                additional.getCvParam().add(DAOCvParams.PEPTIDE_RANK.getParam(String.format("%d", rank)));

                // up-/downstream flanking sequence
                String upStream = String.format("%c", protein.getPeptideResidueBefore(i));
                if (!"@".equals(upStream) && !"-".equals(upStream) && !"?".equals(upStream))
                    additional.getCvParam().add(DAOCvParams.UPSTREAM_FLANKING_SEQUENCE.getParam(upStream));

                String downStream = String.format("%c", protein.getPeptideResidueAfter(i));
                if (!"@".equals(downStream) && !"-".equals(downStream) && !"?".equals(downStream))
                    additional.getCvParam().add(DAOCvParams.DOWNSTREAM_FLANKING_SEQUENCE.getParam(downStream));

                // the peptide's charge state
                additional.getCvParam().add(DAOCvParams.CHARGE_STATE.getParam(String.format("%d", msPep.getCharge())));

                // make sure the peptide is a significant hit
                if (!mascotFile.isPMF()) {
	                ms_mascotresults rs = (decoyHit) ? getDecoyResults() : getResults();
	                // calculate the threshold depending on if identity threshold or homology threshold was used
	                int th = 0;
	                if ((Boolean) getCurrentProperty(SupportedProperties.USE_HOMOLOGY_THREHOLD))
	                	th = rs.getHomologyThreshold(msPep.getQuery(), 1 / (Double) getCurrentProperty(SupportedProperties.MIN_PROPABILITY), msPep.getRank());
	                else
	                	th = rs.getPeptideIdentityThreshold(msPep.getQuery(), 1 / (Double) getCurrentProperty(SupportedProperties.MIN_PROPABILITY));
	                
	                // add peptide cvParam to flag not significant peptide hits
	                if (msPep.getIonsScore() < th)
	                    additional.getCvParam().add(DAOCvParams.NON_SIGNIFICANT_PEPTIDE.getParam());
                }

                peptide.setAdditional(additional);
            }

            // save the peptide
            peptides.add(peptide);
        }

        return peptides;
    }

    /**
     * Returns a list of fixed PeptidePTMs.
     *
     * @param peptide The ms_peptide to create the ptms for.
     * @return List of PeptidePTMs
     */
    private List<PeptidePTM> createFixedPeptidePTMs(ms_peptide peptide) {
        // initialize the list of ptms
        List<PeptidePTM> ptms = new ArrayList<PeptidePTM>();

        // modification info is stored in the search result params
        ms_searchparams params = mascotFile.params();

        // check if monoisotopic of average masses are used
        boolean average = params.getMASS().equals("Average");

        // add the fixed modifications -- only way of determining if the mod exists is by getting its name
        int modNum = 1;
        String modName;

        do {
            modName = params.getFixedModsName(modNum);

            // make sure the mod exists
            if (modName.length() == 0)
                break;

            // get all possible params
            Double delta = params.getFixedModsDelta(modNum);
            Double neutralLoss = params.getFixedModsNeutralLoss(modNum);
            String residues = params.getFixedModsResidues(modNum);

            // get the position of the modification
            Integer index = -1;

            // if it's the n-terminus use 0 as index
            if (residues.equals("N_term"))
                index = 0;
            else if (residues.equals("C_term"))
                index = peptide.getPeptideStr().length() + 1;
            else
                index = peptide.getPeptideStr().indexOf(residues);

            // while new positions of the AA are found, process it            
            while (index != -1) {
                // increment the index to represent the 1-based location in the peptide sequence
                if (!residues.equals("N_term") && !residues.equals("C_term"))
                    index++;

                // create the ptm
                PeptidePTM ptm = new PeptidePTM();
                ptm.setFixedModification(true);
                ptm.setModLocation(index);
                ptm.setSearchEnginePTMLabel(modName);

                // set the average or mono delta
                if (average) {
                    ptm.getModAvgDelta().add(delta.toString());
                } else {
                    ptm.getModMonoDelta().add(delta.toString());
                }

                // set the neutral loss as additional parameter
                if (neutralLoss != 0) {
                    ptm.getAdditional().getCvParam().add(DAOCvParams.NEUTRAL_LOSS.getParam(neutralLoss.toString()));
                }

                // save the ptm in the list
                ptms.add(ptm);

                // if it's not the terminal, check for other occurrences of the residue
                if (!residues.equals("N_term") && !residues.equals("C_term"))
                    index = peptide.getPeptideStr().indexOf(residues, index);
                else
                    break;
            }

            // go to the next mod
            modNum++;
        } while (modName.length() > 0);

        return ptms;
    }

    /**
     * List of variable PTMs for the given peptide.
     *
     * @param peptide The ms_peptide to create the list for.
     * @return A list of PeptidePTMs.
     */
    private List<PeptidePTM> createVarPeptidePTMs(ms_peptide peptide, boolean isDecoyHit) {
        // initialize the list of ptms
        List<PeptidePTM> ptms = new ArrayList<PeptidePTM>();

        // modification info is stored in the search result params
        ms_searchparams params = mascotFile.params();

        // check if monoisotopic of average masses are used
        boolean average = params.getMASS().equals("Average");

        // get the peptide's modification and neutral loss string
        String modString = peptide.getVarModsStr();
        String neutralLossString = peptide.getPrimaryNlStr();

        // loop through the peptide modString
        for (Integer position = 0; position < modString.length(); position++) {
            // get the character at the position and convert it into a number
            char modChar = modString.charAt(position);
            int modNumber = mascotVarPtmString.indexOf(modChar);

            // make sure the modChar could be assigned to a modification number
            if (modNumber == -1 && modChar != 'X') {
                logger.error("Invalid variable modification char '" + modChar + "' found at " + position + " in " + peptide.getPeptideStr());
                continue;
            }

            // if the modNumber = 0, no modification was set
            if (modNumber == 0 && modChar != 'X')
                continue;

            // get the ptm's info
            String name;

            if (modChar != 'X')
                name = params.getVarModsName(modNumber);
            else
                name = (isDecoyHit) ? getDecoyResults().getErrTolModName(peptide.getQuery(), peptide.getRank()) :
                        getResults().getErrTolModName(peptide.getQuery(), peptide.getRank());

            // make sure the modification is defined
            if (name.length() < 1) {
                logger.error("Found variable modification number " + modNumber + " is not defined in the parameters");
                continue;
            }

            // initialize the ptm
            PeptidePTM ptm = new PeptidePTM();
            ptm.setFixedModification(false);
            ptm.setModLocation(position);
            ptm.setSearchEnginePTMLabel(name);
            ptm.setAdditional(new Param());

            // set the delta mass
            Double delta;

            if (modChar != 'X')
                delta = params.getVarModsDelta(modNumber);
            else
                delta = (isDecoyHit) ? getDecoyResults().getErrTolModDelta(peptide.getQuery(), peptide.getRank()) :
                        getResults().getErrTolModDelta(peptide.getQuery(), peptide.getRank());

            if (average)
                ptm.getModAvgDelta().add(delta.toString());
            else
                ptm.getModMonoDelta().add(delta.toString());

            // set the neutral loss
            if (neutralLossString.length() > 0 && modChar != 'X') {
                vectord neutralLosses = params.getVarModsNeutralLosses(modNumber);// get all neutral losses for this variable modificication
                char neutralLossChar = neutralLossString.charAt(position);        // get which neutral loss was used for this modification
                Integer neutralLossInt = mascotVarPtmString.indexOf(neutralLossChar); // convert the char to an index

                // get the used neutral loss
                Double neutralLoss = 0.0;

                if (neutralLossInt > 0)
                    neutralLoss = neutralLosses.get(neutralLossInt - 1);

                // save the neutral loss as an additional parameter if it's set
                if (neutralLoss > 0) {
                    ptm.getAdditional().getCvParam().add(DAOCvParams.NEUTRAL_LOSS.getParam(neutralLoss.toString()));
                    logger.debug("Neutral loss found: " + neutralLoss);
                }
            } else if (modChar == 'X') {
                // get the used neutral loss
                Double neutralLoss = (isDecoyHit) ? getDecoyResults().getErrTolModNeutralLoss(peptide.getQuery(), peptide.getRank()) :
                        getResults().getErrTolModNeutralLoss(peptide.getQuery(), peptide.getRank());

                // save the neutral loss as an additional parameter if it's set
                if (neutralLoss > 0) {
                    ptm.getAdditional().getCvParam().add(DAOCvParams.NEUTRAL_LOSS.getParam(neutralLoss.toString()));
                    logger.debug("Neutral loss found: " + neutralLoss);
                }
            }

            // save the ptm in the list
            ptms.add(ptm);
        }

        return ptms;
    }

    /**
     * Returns an array of doubles the size of the peptide's sequence +2
     * (length) indicating the mass change for the given AA + termini caused by
     * variable modifications.
     *
     * @param peptide The peptide to create the massChangeArray for.
     * @return Double array the length of the peptide sequence.
     */
    private double[] getPeptideMassChanges(ms_peptide peptide, boolean isDecoyHit) {
        // initialize the list of ptms
        double[] massChanges = new double[peptide.getPeptideStr().length() + 2];

        for (int i = 0; i < massChanges.length; i++)
            massChanges[i] = 0.0;

        // modification info is stored in the search result params
        ms_searchparams params = mascotFile.params();

        // get the peptide's modification and neutral loss string
        String modString = peptide.getVarModsStr();
        String neutralLossString = peptide.getPrimaryNlStr();

        // loop through the peptide modString
        for (Integer position = 0; position < modString.length(); position++) {
            // get the character at the position and convert it into a number
            char modChar = modString.charAt(position);
            int modNumber = mascotVarPtmString.indexOf(modChar);

            // check if it's an error tolerant modification
            if (modChar == 'X') {
                // get the detla for the error tolerant result
                Double delta = (isDecoyHit) ? getDecoyResults().getErrTolModDelta(peptide.getQuery(), peptide.getRank()) :
                        getResults().getErrTolModDelta(peptide.getQuery(), peptide.getRank());

                // if there was a modification found, set it
                if (delta > 0)
                    massChanges[position] = delta;

                // check if there's a neutral loss set
                Double neutralLoss = (isDecoyHit) ? getDecoyResults().getErrTolModNeutralLoss(peptide.getQuery(), peptide.getRank()) :
                        getResults().getErrTolModNeutralLoss(peptide.getQuery(), peptide.getRank());
                ;

                // subtract the potential neutral loss
                if (neutralLoss > 0)
                    massChanges[position] -= neutralLoss;

                continue;
            }

            // make sure the modChar could be assigned to a modification number
            if (modNumber == -1) {
                logger.error("Invalid variable modification char '" + modChar + "' found at " + position + " in " + peptide.getPeptideStr());
                continue;
            }

            // if the modNumber = 0, no modification was set
            if (modNumber == 0)
                continue;

            // get the ptm's info
            String name = params.getVarModsName(modNumber);

            // make sure the modification is defined
            if (name.length() < 1) {
                logger.error("Found variable modification number " + modNumber + " is not defined in the parameters");
                continue;
            }

            // set the delta mass
            massChanges[position] = params.getVarModsDelta(modNumber);

            // set the neutral loss
            if (neutralLossString.length() > 0) {
                vectord neutralLosses = params.getVarModsNeutralLosses(modNumber);// get all neutral losses for this variable modificication
                char neutralLossChar = neutralLossString.charAt(position);        // get which neutral loss was used for this modification
                Integer neutralLossInt = mascotVarPtmString.indexOf(neutralLossChar); // convert the char to an index

                // get the used neutral loss
                Double neutralLoss = 0.0;

                if (neutralLossInt > 0)
                    neutralLoss = neutralLosses.get(neutralLossInt - 1);

                // save the neutral loss as an additional parameter if it's set
                if (neutralLoss > 0) {
                    massChanges[position] -= neutralLoss;
                }
            }
        }

        return massChanges;
    }

    @Override
    public void setConfiguration(Properties props) {
        properties = props;
        
        // "reset" the peptide results
        results = null;
        decoyResults = null;
    }

    @Override
    public Properties getConfiguration() {
        return properties;
    }

    @Override
    public Protocol getProtocol() {
        // not present in any search engine result file
        return null;
    }

    @Override
    public Collection<Reference> getReferences() {
        // not present in any search engine result file
        return null;
    }

    /**
     * This functions returns a ms_mascotresults object that provides
     * access to the protein / peptide identifications. As creating
     * this object is very time-consuming it's only created once. This
     * is checked by this function.
     *
     * @return A ms_mascotresults object providing access to the peptide / protein identifications of the result file.
     */
    private ms_mascotresults getResults() {
        // check whether the results were already created
        if (results != null)
            return results;

        // return a protein summary result object in case it's a PMF query
        if (mascotFile.isPMF()) {
        	results = new ms_proteinsummary(mascotFile,
        			getProteinSummaryFlrags(),
        			(Double) getCurrentProperty(SupportedProperties.MIN_PROPABILITY),         // minProbability
                    10000000,         // maxHitsToReport (some really high number)
                    null,     // unigeneIndexFile
                    null);     // singleHit	
        }
        else {
            // create the peptide results
            results = new ms_peptidesummary(mascotFile,
                    getPeptideSummaryFlags(), // | // use Mascot's default duplication handling
                    (Double) getCurrentProperty(SupportedProperties.MIN_PROPABILITY),         // minProbability
                    10000000,         // maxHitsToReport (some really high number)
                    null,     // unigeneIndexFile
                    (Double) getCurrentProperty(SupportedProperties.IGNORE_BELOW_SCORE),         // ignoreIonsScoreBelow
                    0,         // minPepLenInPepSummary
                    null);     // singleHit	
        }

        return results;
    }

    /**
     * Returns the decoy peptide summary - if a decoy database was used.
     * If no decoy database was used, null is returned.
     * For PMF queries null is always returned.
     *
     * @return The ms_mascotresults for the decoy database results. Null if no decoy database was searched
     */
    private ms_mascotresults getDecoyResults() {
        // check whether the decoy results were already created
        if (decoyResults != null)
            return decoyResults;

        // check if a decoy database was used
        if (mascotFile.params().getDECOY() != 1)
            return null;
        
        // for PMF queries that's (probably) not sensible
        if (mascotFile.isPMF())
        	return null;

        // create the decoy results
        decoyResults = new ms_peptidesummary(mascotFile,
                getPeptideSummaryFlags() |
                        ms_mascotresults.MSRES_DECOY,
                (Double) getCurrentProperty(SupportedProperties.MIN_PROPABILITY),        // minProbability
                10000000,         // maxHitsToReport (some really high number)
                null,     // unigeneIndexFile
                (Double) getCurrentProperty(SupportedProperties.IGNORE_BELOW_SCORE),         // ignoreIonsScoreBelow
                0,         // minPepLenInPepSummary
                null);     // singleHit

        return decoyResults;
    }

    /**
     * Returns the peptide summary flags to use
     *
     * @return
     */
    private int getPeptideSummaryFlags() {
        // create the flags
        int flags = 0;

        flags = flags | ms_mascotresults.MSRES_DUPE_DEFAULT;
        //  query    sequence    modifications    position
//    	flags = flags | ms_mascotresults.MSRES_DUPE_REMOVE_A | //  same       same            same          same
//    					ms_mascotresults.MSRES_DUPE_REMOVE_B | //  same       same          different       same
//    					ms_mascotresults.MSRES_DUPE_REMOVE_C | //  same       same          different    different
//    					ms_mascotresults.MSRES_DUPE_REMOVE_D | //  same     different       different    different
//    					ms_mascotresults.MSRES_DUPE_REMOVE_E;  //different    same            same          same

        // set the MudPIT scoring
        // check if the properties were set
        if ((Boolean) getCurrentProperty(SupportedProperties.USE_MUDPIT_SCORING))
            flags = flags | ms_mascotresults.MSRES_MUDPIT_PROTEIN_SCORE;

        // check if error tolerant results should be included
        if ((Boolean) getCurrentProperty(SupportedProperties.INCLUDE_ERR_TOL))
            flags = flags | ms_mascotresults.MSRES_INTEGRATED_ERR_TOL;

        // grouping mode
        if ((Boolean) getCurrentProperty(SupportedProperties.ENABLE_GROUPING))
            flags = flags | ms_mascotresults.MSRES_GROUP_PROTEINS;

        return flags;
    }
    
    /**
     * Returns the flags for a protein summary object.
     * @return
     */
    private int getProteinSummaryFlrags() {
    	 int flags = 0;
    	 
    	// grouping mode
         if ((Boolean) getCurrentProperty(SupportedProperties.ENABLE_GROUPING))
             flags = flags | ms_mascotresults.MSRES_GROUP_PROTEINS;
         
         return flags;
    }

    /**
     * Returns the matched fragments for a given spectrum (= ms_inputquery).
     *
     * @param theoreticalFragments A HashMap holding the theoretical fragments for the given spectrum.
     * @param query                The spectrum (=ms_inputquery) used to retrieve the result.
     * @param ionSeries            The used ionSeries (generally 1)
     * @param numPeaksUsed         The number of peaks used by mascot.
     * @param tolerance            The tolerance to use in Dalton.
     * @return A List<FragmentIon> holding all the FragmentIons identified.
     */
    private List<FragmentIon> getMatchedFragments(HashMap<String, Double> theoreticalFragments, ms_inputquery query, int ionSeries, int numPeaksUsed, double tolerance) {
        // initialize the HashMap of FragmentIons - use to be able to overwrite fragment ions if multiple peaks fit
        HashMap<String, FragmentIon> fragmentIons = new HashMap<String, FragmentIon>();

        // save all already processed ions together with their intensities
        HashMap<String, Double> fragmentIntensities = new HashMap<String, Double>();

        logger.debug("\t----- Fragment Ions for Query " + query.getIndex() + " ----------");

        // loop through the peaks presumably used by Mascot and check if they match to a theoretical fragment ion
        for (int nPeak = 0; nPeak < numPeaksUsed; nPeak++) {
            // get the mz for the given ion series
            Double mz = query.getPeakMass(ionSeries, nPeak);
            Double intens = query.getPeakIntensity(ionSeries, nPeak);

            // check if this peak was found
            for (String ion : theoreticalFragments.keySet()) {
                double theoreticalMz = theoreticalFragments.get(ion);

                // if the theoretical ion matches, save it
                if (theoreticalMz >= mz - tolerance && theoreticalMz <= mz + tolerance) {
                    // ignore any potential peaks that could identify the same ion but have less intensity
                    if (fragmentIntensities.containsKey(ion) && fragmentIntensities.get(ion) > intens) {
                        continue;
                    }

                    // create and save the fragment ion
                    FragmentIon fragmentIon = new FragmentIon();

                    // charge
                    String productIonCharge = (ion.contains("++")) ? "2" : "1";
                    fragmentIon.getCvParam().add(DAOCvParams.PRODUCT_ION_CHARGE.getParam(productIonCharge));
                    // intensity
                    fragmentIon.getCvParam().add(DAOCvParams.PRODUCT_ION_INTENSITY.getParam(intens));
                    // m/z
                    fragmentIon.getCvParam().add(DAOCvParams.PRODUCT_ION_MZ.getParam(mz));
                    // mass error
                    Double massError = theoreticalMz - mz;
                    fragmentIon.getCvParam().add(DAOCvParams.PRODUCT_ION_MASS_ERROR.getParam(massError));

                    // set the name
                    CvParam name = getIonName(ion);
                    if (name != null) fragmentIon.getCvParam().add(name);

                    // save the fragment ion
                    fragmentIons.put(ion, fragmentIon);
                    fragmentIntensities.put(ion, intens);

                    logger.debug("\t" + ion + ": " + mz);
                }
            }
        }

        // convert the values of the HashMap to an ArrayList
        return new ArrayList<FragmentIon>(fragmentIons.values());
    }

    /**
     * Returns the given ion name CvParam. If the ion isn't known null
     * is returned.
     *
     * @param ion The ion as a string (f.e. y++, y°, b*++)
     * @return The name CvParam for the given ion. Null if the ion name isn't known.
     */
    private CvParam getIonName(String ion) {
        // get the position
        String position = ion.replaceAll("[^0-9]", "");

        // y-ions
        if (ion.startsWith("y°"))
            return new CvParam("PRIDE", "PRIDE:0000197", "y ion -H2O", position);
        if (ion.startsWith("y*"))
            return new CvParam("PRIDE", "PRIDE:0000198", "y ion -NH3", position);
        if (ion.startsWith("y"))
            return new CvParam("PRIDE", "PRIDE:0000193", "y ion", position);

        // b-ions
        if (ion.startsWith("b°"))
            return new CvParam("PRIDE", "PRIDE:0000196", "b ion -H2O", position);
        if (ion.startsWith("b*"))
            return new CvParam("PRIDE", "PRIDE:0000195", "b ion -NH3", position);
        if (ion.startsWith("b"))
            return new CvParam("PRIDE", "PRIDE:0000194", "b ion", position);

        // c-ions
        if (ion.startsWith("c°"))
            return new CvParam("PRIDE", "PRIDE:0000237", "c ion -H2O", position);
        if (ion.startsWith("c*"))
            return new CvParam("PRIDE", "PRIDE:0000238", "c ion -NH3", position);
        if (ion.startsWith("c"))
            return new CvParam("PRIDE", "PRIDE:0000236", "c ion", position);

        // a-ions
        if (ion.startsWith("a°"))
            return new CvParam("PRIDE", "PRIDE:0000234", "a ion -H2O", position);
        if (ion.startsWith("a*"))
            return new CvParam("PRIDE", "PRIDE:0000235", "a ion -NH3", position);
        if (ion.startsWith("a"))
            return new CvParam("PRIDE", "PRIDE:0000233", "a ion", position);

        // x-ions
        if (ion.startsWith("x°"))
            return new CvParam("PRIDE", "PRIDE:0000228", "x ion -H2O", position);
        if (ion.startsWith("x*"))
            return new CvParam("PRIDE", "PRIDE:0000229", "x ion -NH3", position);
        if (ion.startsWith("x"))
            return new CvParam("PRIDE", "PRIDE:0000227", "x ion", position);

        // z-ions
        if (ion.startsWith("z°"))
            return new CvParam("PRIDE", "PRIDE:0000231", "z ion -H2O", position);
        if (ion.startsWith("z*"))
            return new CvParam("PRIDE", "PRIDE:0000232", "z ion -NH3", position);
        if (ion.startsWith("zh++"))
            return new CvParam("PRIDE", "PRIDE:0000281", "zHH ion", position);
        if (ion.startsWith("zh"))
            return new CvParam("PRIDE", "PRIDE:0000280", "zH ion", position);
        if (ion.startsWith("z"))
            return new CvParam("PRIDE", "PRIDE:0000230", "z ion", position);

        // this case should not happen
        return null;
    }

    /**
     * Calculates the theoretical fragment masses for the given series and
     * massChanges. The massChanges should only represent variable modifications
     * as fixed modifications are automatically taken into consideration.
     *
     * @param sequence    The peptide's sequence.
     * @param massChanges An array of mass changes. This array has to have the same size as the peptide length + 2 as it has to have one value for every AA in the peptide + the termini.
     * @param seriesUsed  The string representing which ion series was used. Only scoring series are taken into consideration.
     * @return A HashMap with the fragments name (y, y++, y* - ammonia loss, y*++, y° - water loss, y°++, b++ ...) as key and the mass as value
     */
    private HashMap<String, Double> createTheoreticalFragments(String sequence, double[] massChanges, String seriesUsed) {
        // initialize the HashMap to use
        HashMap<String, Double> fragments = new HashMap<String, Double>();

        logger.debug("--CreateTheoreticalFragments");
        logger.debug("\tseriesUsed: " + seriesUsed);

        // B series
        if (seriesUsed.charAt(3) == '2')
            fragments.putAll(createTheoreticalBSeries(sequence, massChanges, seriesUsed.charAt(5) == '2'));
        // Y series
        if (seriesUsed.charAt(6) == '2')
            fragments.putAll(createTheoreticalYSeries(sequence, massChanges, seriesUsed.charAt(8) == '2'));
        // A series
        if (seriesUsed.charAt(0) == '2')
            fragments.putAll(createTheoreticalASeries(sequence, massChanges, seriesUsed.charAt(2) == '2'));
        // C series
        if (seriesUsed.charAt(9) == '2')
            fragments.putAll(createTheoreticalCSeries(sequence, massChanges, seriesUsed.charAt(10) == '2'));
        // X series
        if (seriesUsed.charAt(11) == '2')
            fragments.putAll(createTheoreticalXSeries(sequence, massChanges, seriesUsed.charAt(12) == '2'));
        // Z series
        if (seriesUsed.charAt(13) == '2')
            fragments.putAll(createTheoreticalZSeries(sequence, massChanges, seriesUsed.charAt(14) == '2'));
        // Z+H
        if (seriesUsed.charAt(15) == '2')
            fragments.putAll(createTheoreticalZHSeries(sequence, massChanges, seriesUsed.charAt(16) == '2'));

        return fragments;
    }

    /**
     * Calculates the theoretical y series masses for the given series and
     * massChanges. The massChanges should only represent variable modifications
     * as fixed modifications are automatically taken into consideration.
     *
     * @param sequence            The peptide's sequence.
     * @param massChanges         An array of mass changes. This array has to have the same size as the peptide length +2 as it has to have one value for every AA + termini in the peptide.
     * @param includeDoubleCharge Boolean indicating whether the double charged ions should be included
     * @return A HashMap with the fragments name (y, y++, y* - ammonia loss, y*++, y° - water loss, y°++) as key and the mass as value
     */
    private HashMap<String, Double> createTheoreticalYSeries(String sequence, double[] massChanges, boolean includeDoubleCharge) {
        // initialize the HashMap to use
        HashMap<String, Double> fragments = new HashMap<String, Double>();

        // get the search parameters
        ms_searchparams params = mascotFile.params();
        int nY = 1; // the current y that's being processed

        // loop through the sequence starting from the c-terminal (= the end)
        for (int position = sequence.length() - 1; position > 0; position--, nY++) {
            // build the string of residues
            String residues = sequence.substring(position);

            // calculate the mass
            double mass = 0.0;

            // add the residue masses together
            for (int i = 0; i < residues.length(); i++)
                mass += params.getResidueMass(residues.charAt(i));

            // add the masses of potential modifications
            for (int i = 0; i < residues.length(); i++)
                mass += massChanges[sequence.length() - i];

            // add the c-term mass, potential c-term modification changes, and the hydrogen mass
            mass += params.getCTermMass();
            mass += massChanges[sequence.length() + 1];
            mass += params.getHydrogenMass();

            // calculate the different y masses (single and double charged)
            fragments.put("y" + nY, mass + 1);
            if (includeDoubleCharge)
                fragments.put("y++" + nY, mass / 2 + 1);

            /*
                * Currently disabled as they are not used for scoring by mascot
                *
               // calculate y* and y*++ -> ammonia loss (NH3)
               fragments.put("y*" + nY, mass - params.getNitrogenMass() - (params.getHydrogenMass() * 3) + 1);
               if (includeDoubleCharge)
                   fragments.put("y*++" + nY, fragments.get("y*" + nY) / 2 + 0.5);

               // calculate the y° and y°++ -> water loss
               fragments.put("y°" + nY, mass - (params.getHydrogenMass() * 2) - params.getOxygenMass() + 1);
               if (includeDoubleCharge)
                   fragments.put("y°++" + nY, fragments.get("y°" + nY) / 2 + 0.5);
               */
        }

        return fragments;
    }

    private HashMap<String, Double> createTheoreticalBSeries(String sequence, double[] massChanges, boolean includeDoubleCharge) {
        // initialize the HashMap to use
        HashMap<String, Double> fragments = new HashMap<String, Double>();

        // get the search parameters
        ms_searchparams params = mascotFile.params();

        // loop through the sequence starting from the n-terminal (= the beginning)
        for (int position = 0; position < sequence.length() - 1; position++) {
            // build the string of residues
            String residues = sequence.substring(0, position + 1);

            // calculate the masses
            double mass = 0.0;

            // add the residue masses
            for (int i = 0; i < residues.length(); i++) {
                mass += params.getResidueMass(residues.charAt(i));
                // add potential mass changes through varmods
                mass += massChanges[i + 1]; // +1 as the first (0) position is used for the c-terminal
            }

            // add the n-terminal, any n-term modification and the proton
            mass += params.getNTermMass();
            mass += massChanges[0];
            mass -= params.getHydrogenMass();

            // calculate the b and b++ ions
            fragments.put("b" + (position + 1), mass + 1);
            if (includeDoubleCharge)
                fragments.put("b++" + (position + 1), mass / 2 + 1);

            /*
                * Currently disabled as they are not used for scoring by mascot
                *
               // calculate the b* and b*++ masses (ammonia loss)
               fragments.put("b*" + (position+1), mass - params.getNitrogenMass() - (params.getHydrogenMass() * 3) + 1);
               if (includeDoubleCharge)
                   fragments.put("b*++" + (position+1), fragments.get("b*" + (position+1)) / 2 + 0.5);

               // calculate the b° and b°++ masses (water loss)
               fragments.put("b°" + (position+1), mass - (params.getHydrogenMass() * 2) - params.getOxygenMass() + 1);
               if (includeDoubleCharge)
                   fragments.put("b°++" + (position+1), fragments.get("b°" + (position+1)) / 2 + 0.5);
               */
        }

        return fragments;
    }

    private HashMap<String, Double> createTheoreticalASeries(String sequence, double[] massChanges, boolean includeDoubleCharge) {
        // initialize the HashMap to use
        HashMap<String, Double> fragments = new HashMap<String, Double>();

        // get the search parameters
        ms_searchparams params = mascotFile.params();

        // loop through the sequence starting from the n-terminal (= the beginning)
        for (int position = 0; position < sequence.length() - 1; position++) {
            // build the string of residues
            String residues = sequence.substring(0, position + 1);

            // calculate the masses
            double mass = 0.0;

            // add the residue masses
            for (int i = 0; i < residues.length(); i++) {
                mass += params.getResidueMass(residues.charAt(i));
                // add potential mass changes through varmods
                mass += massChanges[i + 1]; // +1 as the first (0) position is used for the c-terminal
            }

            // add the n-terminal, any n-term modification and the proton
            mass += params.getNTermMass();
            mass += massChanges[0];
            mass -= params.getCarbonMass() - params.getHydrogenMass() - params.getOxygenMass(); // - CHO

            // calculate the b and b++ ions
            fragments.put("a" + (position + 1), mass + 1);
            if (includeDoubleCharge)
                fragments.put("a++" + (position + 1), mass / 2 + 1);

            /*
                * Currently disabled as they are not used for scoring by mascot
                *
               // calculate the a* and a*++ masses (ammonia loss)
               fragments.put("a*" + (position+1), mass - params.getNitrogenMass() - (params.getHydrogenMass() * 3) + 1);
               if (includeDoubleCharge)
                   fragments.put("a*++" + (position+1), fragments.get("a*" + (position+1)) / 2 + 0.5);

               // calculate the a° and a°++ masses (water loss)
               fragments.put("a°" + (position+1), mass - (params.getHydrogenMass() * 2) - params.getOxygenMass() + 1);
               if (includeDoubleCharge)
                   fragments.put("a°++" + (position+1), fragments.get("a°" + (position+1)) / 2 + 0.5);
               */
        }

        return fragments;
    }

    private HashMap<String, Double> createTheoreticalCSeries(String sequence, double[] massChanges, boolean includeDoubleCharge) {
        // initialize the HashMap to use
        HashMap<String, Double> fragments = new HashMap<String, Double>();

        // get the search parameters
        ms_searchparams params = mascotFile.params();

        // loop through the sequence starting from the n-terminal (= the beginning)
        for (int position = 0; position < sequence.length() - 1; position++) {
            // build the string of residues
            String residues = sequence.substring(0, position + 1);

            // calculate the masses
            double mass = 0.0;

            // add the residue masses
            for (int i = 0; i < residues.length(); i++) {
                mass += params.getResidueMass(residues.charAt(i));
                // add potential mass changes through varmods
                mass += massChanges[i + 1]; // +1 as the first (0) position is used for the c-terminal
            }

            // add the n-terminal, any n-term modification and the proton
            mass += params.getNTermMass();
            mass += massChanges[0];
            mass += params.getNitrogenMass() + params.getHydrogenMass() * 2; // + NH2

            // calculate the b and b++ ions
            fragments.put("c" + (position + 1), mass + 1);
            if (includeDoubleCharge)
                fragments.put("c++" + (position + 1), mass / 2 + 1);

            /*
                * Currently disabled as they are not used for scoring by mascot
                *
               // calculate the a* and c*++ masses (ammonia loss)
               fragments.put("c*" + (position+1), mass - params.getNitrogenMass() - (params.getHydrogenMass() * 3) + 1);
               if (includeDoubleCharge)
                   fragments.put("c*++" + (position+1), fragments.get("c*" + (position+1)) / 2 + 0.5);

               // calculate the c° and c°++ masses (water loss)
               fragments.put("c°" + (position+1), mass - (params.getHydrogenMass() * 2) - params.getOxygenMass() + 1);
               if (includeDoubleCharge)
                   fragments.put("c°++" + (position+1), fragments.get("c°" + (position+1)) / 2 + 0.5);
               */
        }

        return fragments;
    }

    private HashMap<String, Double> createTheoreticalXSeries(String sequence, double[] massChanges, boolean includeDoubleCharge) {
        // initialize the HashMap to use
        HashMap<String, Double> fragments = new HashMap<String, Double>();

        // get the search parameters
        ms_searchparams params = mascotFile.params();
        int nX = 1; // the current x that's being processed

        // loop through the sequence starting from the c-terminal (= the end)
        for (int position = sequence.length() - 1; position > 0; position--, nX++) {
            // build the string of residues
            String residues = sequence.substring(position);

            // calculate the mass
            double mass = 0.0;

            // add the residue masses together
            for (int i = 0; i < residues.length(); i++)
                mass += params.getResidueMass(residues.charAt(i));

            // add the masses of potential modifications
            for (int i = 0; i < residues.length(); i++)
                mass += massChanges[sequence.length() - i];

            // add the c-term mass, potential c-term modification changes, and the hydrogen mass
            mass += params.getCTermMass();
            mass += massChanges[sequence.length() + 1];
            mass += params.getCarbonMass() + params.getOxygenMass() - params.getHydrogenMass(); // + CO - H

            // calculate the different x masses (single and double charged)
            fragments.put("x" + nX, mass + 1);
            if (includeDoubleCharge)
                fragments.put("x++" + nX, mass / 2 + 1);

            /*
                * Currently disabled as they are not used for scoring by mascot
                *
               // calculate x* and x*++ -> ammonia loss (NH3)
               fragments.put("x*" + nX, mass - params.getNitrogenMass() - (params.getHydrogenMass() * 3) + 1);
               if (includeDoubleCharge)
                   fragments.put("x*++" + nX, fragments.get("x*" + nX) / 2 + 0.5);

               // calculate the x° and x°++ -> water loss
               fragments.put("x°" + nX, mass - (params.getHydrogenMass() * 2) - params.getOxygenMass() + 1);
               if (includeDoubleCharge)
                   fragments.put("x°++" + nX, fragments.get("x°" + nX) / 2 + 0.5);
               */
        }

        return fragments;
    }

    private HashMap<String, Double> createTheoreticalZSeries(String sequence, double[] massChanges, boolean includeDoubleCharge) {
        // initialize the HashMap to use
        HashMap<String, Double> fragments = new HashMap<String, Double>();

        // get the search parameters
        ms_searchparams params = mascotFile.params();
        int nZ = 1; // the current x that's being processed

        // loop through the sequence starting from the c-terminal (= the end)
        for (int position = sequence.length() - 1; position > 0; position--, nZ++) {
            // build the string of residues
            String residues = sequence.substring(position);

            // calculate the mass
            double mass = 0.0;

            // add the residue masses together
            for (int i = 0; i < residues.length(); i++)
                mass += params.getResidueMass(residues.charAt(i));

            // add the masses of potential modifications
            for (int i = 0; i < residues.length(); i++)
                mass += massChanges[sequence.length() - i];

            // add the c-term mass, potential c-term modification changes, and the hydrogen mass
            mass += params.getCTermMass();
            mass += massChanges[sequence.length() + 1];
            mass -= params.getNitrogenMass() - params.getHydrogenMass() * 2; // - NH2

            // calculate the different z masses (single and double charged)
            fragments.put("z" + nZ, mass + 1);
            if (includeDoubleCharge)
                fragments.put("z++" + nZ, mass / 2 + 1);

            /*
                * Currently disabled as they are not used for scoring by mascot
                *
               // calculate z* and z*++ -> ammonia loss (NH3)
               fragments.put("z*" + nZ, mass - params.getNitrogenMass() - (params.getHydrogenMass() * 3) + 1);
               if (includeDoubleCharge)
                   fragments.put("z*++" + nZ, fragments.get("z*" + nZ) / 2 + 0.5);

               // calculate the z° and z°++ -> water loss
               fragments.put("z°" + nZ, mass - (params.getHydrogenMass() * 2) - params.getOxygenMass() + 1);
               if (includeDoubleCharge)
                   fragments.put("z°++" + nZ, fragments.get("z°" + nZ) / 2 + 0.5);
               //*/
        }

        return fragments;
    }

    private HashMap<String, Double> createTheoreticalZHSeries(String sequence, double[] massChanges, boolean includeDoubleCharge) {
        // initialize the HashMap to use
        HashMap<String, Double> fragments = new HashMap<String, Double>();

        // get the search parameters
        ms_searchparams params = mascotFile.params();
        int nZ = 1; // the current x that's being processed

        // loop through the sequence starting from the c-terminal (= the end)
        for (int position = sequence.length() - 1; position > 0; position--, nZ++) {
            // build the string of residues
            String residues = sequence.substring(position);

            // calculate the mass
            double mass = 0.0;

            // add the residue masses together
            for (int i = 0; i < residues.length(); i++)
                mass += params.getResidueMass(residues.charAt(i));

            // add the masses of potential modifications
            for (int i = 0; i < residues.length(); i++)
                mass += massChanges[sequence.length() - i];

            // add the c-term mass, potential c-term modification changes, and the hydrogen mass
            mass += params.getCTermMass();
            mass += massChanges[sequence.length() + 1];
            // TODO: check the formula to calculate Z+H ions
            mass -= params.getNitrogenMass() - params.getHydrogenMass() * 2 + params.getHydrogenMass(); // - NH2 + H

            // calculate the different z masses (single and double charged)
            fragments.put("zh" + nZ, mass + 1);
            if (includeDoubleCharge)
                fragments.put("zh++" + nZ, mass / 2 + 1);

            /*
                * Currently disabled as they are not used for scoring by mascot
                *
               // calculate zh* and zh*++ -> ammonia loss (NH3)
               fragments.put("zh*" + nZ, mass - params.getNitrogenMass() - (params.getHydrogenMass() * 3) + 1);
               if (includeDoubleCharge)
                   fragments.put("zh*++" + nZ, fragments.get("zh*" + nZ) / 2 + 0.5);

               // calculate the zh° and zh°++ -> water loss
               fragments.put("zh°" + nZ, mass - (params.getHydrogenMass() * 2) - params.getOxygenMass() + 1);
               if (includeDoubleCharge)
                   fragments.put("zh°++" + nZ, fragments.get("zh°" + nZ) / 2 + 0.5);
               //*/
        }

        return fragments;
    }

    /**
     * Calculates the FDR for the used result file under the
     * set thresholds. This function only uses the identity
     * or homolgy threshold for its calculation.
     *
     * @return The FDR for the set probability. Null in case there was no decoy search performed.
     */
    private Double getFDR() {
    	if (mascotFile.isPMF())
    		return null;
    	
        // get the results
        ms_mascotresults results = getResults();

        // check if a decoy database was used
        if (mascotFile.params().getDECOY() != 1)
            return null;

        // calculate the 1 in X probability
        Double probability = (Double) getCurrentProperty(SupportedProperties.MIN_PROPABILITY);

        Double oneInXProb = (probability <= 1) ? 1 / probability : probability;

        // get the correct and decoy hits
        Double hits = 0.0;
        Double decoyHits = 0.0;
        
        if ((Boolean) getCurrentProperty(SupportedProperties.USE_HOMOLOGY_THREHOLD)) {
        	hits = (double) results.getNumHitsAboveHomology(oneInXProb);
        	decoyHits = (double) results.getNumDecoyHitsAboveHomology(oneInXProb);
        }
        else {
        	hits = (double) results.getNumHitsAboveIdentity(oneInXProb);
        	decoyHits = (double) results.getNumDecoyHitsAboveIdentity(oneInXProb);
        }

        // normally, the FDR is reported as decoy*2 / total - Mascot does it differently
        return decoyHits / hits;
    }
}
