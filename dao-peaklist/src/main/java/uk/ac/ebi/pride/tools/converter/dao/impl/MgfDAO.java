/**
 *
 */
package uk.ac.ebi.pride.tools.converter.dao.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
import uk.ac.ebi.pride.tools.converter.report.model.DatabaseMapping;
import uk.ac.ebi.pride.tools.converter.report.model.Identification;
import uk.ac.ebi.pride.tools.converter.report.model.InstrumentDescription;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.Protocol;
import uk.ac.ebi.pride.tools.converter.report.model.Reference;
import uk.ac.ebi.pride.tools.converter.report.model.SearchResultIdentifier;
import uk.ac.ebi.pride.tools.converter.report.model.Software;
import uk.ac.ebi.pride.tools.converter.report.model.SourceFile;
import uk.ac.ebi.pride.tools.converter.report.model.UserParam;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.FileUtils;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;
import uk.ac.ebi.pride.tools.jmzreader.JMzReaderException;
import uk.ac.ebi.pride.tools.mgf_parser.MgfFile;
import uk.ac.ebi.pride.tools.mgf_parser.model.Ms2Query;

/**
 * This DAO converts MGF files into PRIDE XML files.
 * Warning: PMF queries are currently not supported in PRIDE XML files.
 *
 * @author jg
 */
public class MgfDAO extends AbstractPeakListDAO implements DAO {
    /**
     * The mgf parser object to parse the file from
     */
    private MgfFile mgfFile;
    /**
     * The mgf source file
     */
    private File sourceFile;

    /**
     * Creates a new MgfDAO.
     *
     * @param sourceFile The mgf file to create the DAO from.
     * @throws InvalidFormatException 
     */
    public MgfDAO(File sourceFile) throws InvalidFormatException {
        // create the mgfFile object
        try {
            mgfFile = new MgfFile(sourceFile);
        } catch (Exception e) {
            throw new InvalidFormatException("Failed to parse mgf file", e);
        }

        // save the source file
        this.sourceFile = sourceFile;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Collection<DAOProperty> getSupportedProperties() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<String> getSpectraIds() {
        // just return an array containing indexes 0..n-1
        ArrayList<String> ids = new ArrayList<String>(mgfFile.getMs2QueryCount());

        for (Integer i = 0; i < mgfFile.getMs2QueryCount(); i++)
            ids.add(i.toString());

        return ids;
    }

    @Override
    public void setConfiguration(Properties props) {
        // currently the DAO supports no properties
    }

    @Override
    public Properties getConfiguration() {
        // as the DAO supports no properties, an empty object is returned
        return new Properties();
    }

    @Override
    public String getExperimentTitle() {
        return mgfFile.getSearchTitle();
    }

    @Override
    public String getExperimentShortLabel() {
        // not supported
        return null;
    }

    @Override
    public Param getExperimentParams() {
        Param params = new Param();

        params.getCvParam().add(DAOCvParams.ORIGINAL_MS_FORMAT.getParam("Mascot generic"));

        return params;
    }

    @Override
    public String getSampleName() {
        // not available
        return null;
    }

    @Override
    public String getSampleComment() {
        // not available
        return null;
    }

    @Override
    public Param getSampleParams() {
        Param param = new Param();

        // taxonomy is returned as a user param.
        if (mgfFile.getTaxonomy() != null)
            param.getUserParam().add(new UserParam("taxonomy", mgfFile.getTaxonomy()));

        return param;
    }

    @Override
    public SourceFile getSourceFile() {
        // initialize the return variable
        SourceFile file = new SourceFile();

        file.setPathToFile(sourceFile.getAbsolutePath());
        file.setNameOfFile(sourceFile.getName());
        file.setFileType("Mascot generic");

        return file;
    }

    @Override
    public Collection<Contact> getContacts() {
        ArrayList<Contact> contacts = new ArrayList<Contact>();

        if (mgfFile.getUserName() != null && mgfFile.getUserMail() != null) {
            Contact contact = new Contact();
            contact.setName(mgfFile.getUserName());
            contact.setContactInfo(mgfFile.getUserMail());
            contact.setInstitution("");
        }

        return contacts;
    }

    @Override
    public InstrumentDescription getInstrument() {
        // not supported
        return null;
    }

    @Override
    public Software getSoftware() {
        // return an empty software element
        Software software = new Software();

        software.setName("Unknown generic (MGF file format)");
        software.setVersion("");

        return software;
    }


    @SuppressWarnings("unchecked")
    @Override
    public Collection<DatabaseMapping> getDatabaseMappings() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Param getProcessingMethod() {
        // not supported
        return null;
    }

    @Override
    public Protocol getProtocol() {
        // not supported
        return null;
    }

    @Override
    public Collection<Reference> getReferences() {
        // not supported
        return null;
    }

    @Override
    public String getSearchDatabaseName() {
        if (mgfFile.getDatabase() != null)
            return mgfFile.getDatabase();
        else
            return "";
    }

    @Override
    public String getSearchDatabaseVersion() {
        // not supported
        return "";
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<PTM> getPTMs() {
        // there are no ptms available to be reported
        return Collections.EMPTY_LIST;
    }

    @Override
    public SearchResultIdentifier getSearchResultIdentifier() {
        // initialize the search result identifier
        SearchResultIdentifier identifier = new SearchResultIdentifier();

        // format the current time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        identifier.setSourceFilePath(sourceFile.getAbsolutePath());
        identifier.setTimeCreated(formatter.format(new Date(System.currentTimeMillis())));
        identifier.setHash(FileUtils.MD5Hash(sourceFile.getAbsolutePath()));

        return identifier;
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
    public int getSpectrumCount(boolean onlyIdentified) {
        // mgf files only contain not identified spectra
        if (onlyIdentified)
            return 0;

        return mgfFile.getMs2QueryCount();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<Spectrum> getSpectrumIterator(boolean onlyIdentified) throws InvalidFormatException {
        // in case only identified spectra should be returned, return an empty list
        if (onlyIdentified)
            return Collections.EMPTY_LIST.iterator();

        return new MgfSpectrumIterator();
    }

    private class MgfSpectrumIterator implements Iterator<Spectrum> {
        /**
         * The ms2 query iterator actually used to fetch the spectra
         */
        private Iterator<Ms2Query> ms2QueryIterator;
        /**
         * Counter to create spectrum ids (1 based)
         */
        private Integer currentSpectrum = 1;
        
        public MgfSpectrumIterator() throws InvalidFormatException {
        	try {
				ms2QueryIterator =  mgfFile.getMs2QueryIterator();
			} catch (JMzReaderException e) {
				throw new InvalidFormatException(e.getMessage(), e);
			}
        }

        @Override
        public boolean hasNext() {
            return ms2QueryIterator.hasNext();
        }

        @Override
        public Spectrum next() {
            // get the ms2 query
            Ms2Query query = ms2QueryIterator.next();

            if (query == null)
                return null;

            Spectrum spectrum = createSpectrumFromMs2Query(query);

            spectrum.setId(currentSpectrum++);

            return spectrum;
        }

        @Override
        public void remove() {
            // this function is not supported
        }

    }

    /**
     * Converts a ms2Query into a PRIDE Converter spectrum object.
     *
     * @param query The Ms2Query to create the spectrum on.
     * @return The generated Spectrum object.
     */
    private Spectrum createSpectrumFromMs2Query(Ms2Query query) {
        // create the spectrum
        Spectrum spectrum = new Spectrum();

        Map<Double, Double> peakList = query.getPeakList();
        
        // convert the peak list to the required byte arrays
        List<Double> masses;
        if (peakList != null)
        	masses = new ArrayList<Double>(peakList.keySet());
        else
        	masses = Collections.emptyList();
        List<Double> intensities = new ArrayList<Double>(masses.size());
        
        Collections.sort(masses);

        // add the intensities in the correct order
        for (Double mz : masses)
        	intensities.add(peakList.get(mz));

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

        // initialize the spectrum description
        SpectrumDesc description = new SpectrumDesc();

        // create the spectrumSettings/spectrumInstrument (mzRangeStop, mzRangeStart, msLevel)
        SpectrumSettings settings = new SpectrumSettings();
        SpectrumInstrument instrument = new SpectrumInstrument();

        instrument.setMsLevel(2);

        // sort the masses to get the minimum and max
        Collections.sort(masses);
        Float rangeStart = new Float(masses.get(0));
        Float rangeStop = new Float(masses.get(masses.size() - 1));

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
        prec.setMsLevel(1);

        Spectrum spec = new Spectrum(); // the precursor spectrum (ref)
        spec.setId(0);
        prec.setSpectrum(spec);

        uk.ac.ebi.pride.jaxb.model.Param ionSelection = new uk.ac.ebi.pride.jaxb.model.Param();

        // add the different precursor parameters if they are available
        if (query.getPeptideMass() != null)
            ionSelection.getCvParam().add(DAOCvParams.PRECURSOR_MZ.getJaxbParam(query.getPeptideMass()));
        if (query.getPeptideIntensity() != null)
            ionSelection.getCvParam().add(DAOCvParams.PRECURSOR_INTENSITY.getJaxbParam(query.getPeptideIntensity()));
        if (query.getRetentionTime() != null)
            ionSelection.getCvParam().add(DAOCvParams.RETENTION_TIME.getJaxbParam(query.getRetentionTime()));
        if (query.getChargeState() != null) {
        	// ignore multiple charge states
        	if (!query.getChargeState().contains("and")) {
        		String charges[] = query.getChargeState().split(",");
        		DAOCvParams chargeParam = (charges.length > 1) ? DAOCvParams.POSSIBLE_CHARGE_STATE : DAOCvParams.CHARGE_STATE;
        		
        		for (String charge : charges) {
        			if (charge.contains("+"))
        				ionSelection.getCvParam().add(chargeParam.getJaxbParam(charge.replaceAll("\\+", "")));
        			else if (charge.contains("-"))
        				ionSelection.getCvParam().add(chargeParam.getJaxbParam("-" + charge.replaceAll("-", "")));
        		}
        	}
        }
        if (query.getScan() != null)
            ionSelection.getCvParam().add(DAOCvParams.PEAK_LIST_SCANS.getJaxbParam(query.getScan()));

        // save the ionselection
        prec.setIonSelection(ionSelection);

        // no activation parameters supported in MGF format
        prec.setActivation(new uk.ac.ebi.pride.jaxb.model.Param());

        // add the (only) precursor to the precursor list and save it in the description item
        precList.getPrecursor().add(prec);
        description.setPrecursorList(precList);

        spectrum.setSpectrumDesc(description);

        return spectrum;
    }

    @Override
    public int getSpectrumReferenceForPeptideUID(String peptideUID) {
        throw new ConverterException("MGF files do not support peptide identifications.");
    }

    @Override
    public Identification getIdentificationByUID(String identificationUID) {
        throw new ConverterException("MGF files do not support protein identifications");
    }


    @SuppressWarnings("unchecked")
    @Override
    public Iterator<Identification> getIdentificationIterator(
            boolean prescanMode) {
        return Collections.EMPTY_LIST.iterator();
    }

}
