package uk.ac.ebi.pride.tools.converter.dao;

import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.report.model.*;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;

import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

/**
 * This is the main interface for format-specific parsing. Each implementation is responsible to support as much of
 * desired functionality as possible. It is appreciated that not all formats will make the requested data items
 * available. In such cases, the methods should return null primitives and empty collections.
 * <br>
 * If information is available and the methods are expected to return Param types, it is valid that the implementations
 * return UserParam objects for terms where the CvParam cannot be explicitely set at runtime. It will be the
 * responsibility of the user to inspect the report file generated and make certain that the information is correct
 * and, if possible, convert the UserParam data into the appropriate CvParams. In any case, the report formats will
 * undergo a validation step where missing or incorrect information will be flagged to the user before the full
 * parsing into PRIDE XML is executed.
 * <br>
 * The DAO must report all possible protein-to-peptide assignments. External tools will be available to update
 * the report file based on specific protein inference algorithms.
 * <br>Note to all DAO implementers: DAO Implementing classes must extend the AbstractDAOImpl class and override all
 * methods defined in that class. All methods declared in AbstractDAOImpl will throw an UnsupportedOperationException,
 * unless documented otherwise.
 */
public interface DAO {

    /* -------------------------------------------------------------------------------
    Helper method to set parser-specific override configuration parameters
      ------------------------------------------------------------------------------- */
    public void setConfiguration(Properties props);

    public Properties getConfiguration();

    /* -------------------------------------------------------------------------------
    Methods for REPORT FORMAT generation
      ------------------------------------------------------------------------------- */

    /**
     * MANDATORY - Must return some experiment title. In case no title
     * is provided by the search enginge's result file, a default title
     * should be returned.
     * @return 
     */
    public String getExperimentTitle() throws InvalidFormatException;

    /**
     * OPTIONAL - Should return null in case this option is not supported
     * by the search engine.
     * @return 
     */
    public String getExperimentShortLabel();

    /**
     * MANDATORY - As a minimal requirement the date of search and the original
     * MS data file format should be set.
     * @return 
     */
    public Param getExperimentParams() throws InvalidFormatException;

    /**
     * OPTIONAL - Should return null in case it's not supported by the search engine.
     * @return 
     */
    public String getSampleName();

    /**
     * OPTIONAL - Should return null in case it's not supported by the search engine.
     * @return 
     */
    public String getSampleComment();

    /**
     * MANDATORY - As a minimal requirement the sample's species should be returned.
     * @return 
     */
    public Param getSampleParams() throws InvalidFormatException;

    /**
     * MANDATORY - Represents the input file for the conversion.
     * @return 
     */
    public SourceFile getSourceFile() throws InvalidFormatException;

    /**
     * OPTIONAL - Should return null in case it's not supported by the search engine.
     * @return 
     */
    public Collection<Contact> getContacts();

    /**
     * OPTIONAL - Should return null in case it's not supported by the search engine.
     * @return 
     */
    public InstrumentDescription getInstrument();

    /**
     * MANDATORY - The search engine's name and version.
     * @return 
     */
    public Software getSoftware() throws InvalidFormatException;

    /**
     * OPTIONAL - Should return null in case it's not supported by the search engine.
     * @return 
     */
    public Param getProcessingMethod();

    /**
     * OPTIONAL - Should return null in case it's not supported by the search engine.
     * @return 
     */
    public Protocol getProtocol();

    /**
     * OPTIONAL - Should return null in case it's not supported by the search engine.
     * @return 
     */
    public Collection<Reference> getReferences();

    //these will be written to the FASTA attributes and will be used in the FASTA section
    //  if there are multiple sequence files, the search database name will be a string-delimited
    //  concatenation of all the names. Idem for version.

    /**
     * MANDATORY - these will be written to the FASTA attributes and will be used
     * in the FASTA section if there are multiple sequence files, the search database
     * name will be a string-delimited concatenation of all the names. Idem for version.
     * @return 
     */
    public String getSearchDatabaseName() throws InvalidFormatException;

    /**
     * MANDATORY - see getSearchDatabaseName
     * @return 
     */
    public String getSearchDatabaseVersion() throws InvalidFormatException;

    /**
     * MANDATORY - Should return a collection of PTMs representing all PTMs that are used
     * in this search. Collection can be empty but not null.
     * @return 
     */
    public Collection<PTM> getPTMs() throws InvalidFormatException;

    /**
     * MANDATORY - Should return a collection of DatabaseMappings that contain all search database names and versions
     * used in this search. Collection can be empty but not null.
     * @return 
     */
    public Collection<DatabaseMapping> getDatabaseMappings() throws InvalidFormatException;

    /**
     * MANDATORY - Must return a valid SearchResultIdenfier object
     * @return 
     */
    public SearchResultIdentifier getSearchResultIdentifier() throws InvalidFormatException;

    /**
     * MANDATORY - Must return a non-null list of all CV lookups used by the DAO
     * @return 
     */
    public Collection<CV> getCvLookup() throws InvalidFormatException;

    /**
     * MANDATORY - Must returns a count of the number of spectra. if onlyIdentified is true, return only count
     * of identified spectra. If false, return count of all spectra.
     * @return 
     */
    public int getSpectrumCount(boolean onlyIdentified) throws InvalidFormatException;

    /*  -------------------------------------------------------------------------------
  Methods for XML output
   ------------------------------------------------------------------------------- */

    /**
     * returns an iterator for spectra in the source file - if onlyIdentified is true, return only identified
     * spectra. If false, return all spectra
     * @return 
     */

    public Iterator<Spectrum> getSpectrumIterator(boolean onlyIdentified) throws InvalidFormatException;

    /**
     * must return -1 if no spectrum ref found
     * @return 
     */
    public int getSpectrumReferenceForPeptideUID(String peptideUID) throws InvalidFormatException;

    /**
     * This function provides random access to the identifications.
     * It is only used in scan mode!
     *
     * @param identificationUID
     * @return
     * @throws uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException
     */
    public Identification getIdentificationByUID(String identificationUID) throws InvalidFormatException;


    /* -------------------------------------------------------------------------------
  Shared methods
   ------------------------------------------------------------------------------- */

    /**
     * This method will return an iterator that will return individual identification objects.
     * In prescanMode the complete Identification and Peptide objects should be returned
     * <b>without</b> the peptide's fragment ion annotation. Peptide items have to contain
     * all the PTMs. <br>
     * In conversionMode (= !prescanMode) Peptide and Protein objects should <b>NOT</b> contain
     * any additional parameters and peptidePTMs should <b>NOT</b> be included. Peptide
     * FragmentIon annotations are mandatory (if applicable) in scanMode.
     * The identification iterator may return null for an identification
     * @return 
     */
    public Iterator<Identification> getIdentificationIterator(boolean prescanMode) throws InvalidFormatException;
    /**
     * Sets the external spectrum file to the given filename. This function
     * is ignored by DAOs that do not require external spectrum files.
     * @param filename
     */
    public void setExternalSpectrumFile(String filename);

    /*
    These handlers need to be passed to the IdentificationIterator so that the identification objects can be properly
     decorated with the requested information, if available.
     */

}
