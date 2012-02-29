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
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.FileUtils;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;
import uk.ac.ebi.pride.tools.dta_parser.DtaFile;
import uk.ac.ebi.pride.tools.dta_parser.model.DtaSpectrum;
import uk.ac.ebi.pride.tools.jmzreader.JMzReaderException;

/**
 * This DAO converts DTA files to PRIDE XML files. It is
 * based on the dta-parser library.
 *
 * @author jg
 */
public class DtaDAO extends AbstractPeakListDAO implements DAO {
    /**
     * The source file used
     */
    File sourceFile;
    /**
     * Instance of the dta parser used to parse the file / directory
     */
    DtaFile dtaFile;

    public DtaDAO(File sourceFile) throws InvalidFormatException {
        this.sourceFile = sourceFile;

        try {
			dtaFile = new DtaFile(sourceFile);
		} catch (JMzReaderException e) {
			throw new InvalidFormatException("Failed to open dta file.", e);
		}
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Collection<DAOProperty> getSupportedProperties() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<String> getSpectraIds() {
        // return the parsers peak spec ids
        return dtaFile.getSpectraIds();
    }

    @Override
    public void setConfiguration(Properties props) {
        // no configuration supported for this DAO
    }

    @Override
    public Properties getConfiguration() {
        // no configuration supported for this DAO
        return new Properties();
    }

    @Override
    public String getExperimentTitle() {
        // not supported
        return "Unknown title";
    }

    @Override
    public String getExperimentShortLabel() {
        // not supported
        return null;
    }

    @Override
    public Param getExperimentParams() {
        Param params = new Param();

        params.getCvParam().add(DAOCvParams.ORIGINAL_MS_FORMAT.getParam("Sequest DTA"));

        return params;
    }

    @Override
    public String getSampleName() {
        // not supported
        return null;
    }

    @Override
    public String getSampleComment() {
        // not supported
        return null;
    }

    @Override
    public Param getSampleParams() {
        // no parameters supported
        return new Param();
    }

    @Override
    public SourceFile getSourceFile() {
        // initialize the return variable
        SourceFile file = new SourceFile();

        file.setPathToFile(sourceFile.getAbsolutePath());
        file.setNameOfFile(sourceFile.getName());
        file.setFileType("Sequest DTA");

        return file;
    }

    @Override
    public Collection<Contact> getContacts() {
        // not supported
        return null;
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

        software.setName("Unknown generic (DTA file format)");
        software.setVersion("");

        return software;
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
        // not supported
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
        // not supported
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

        if (sourceFile.isFile())
            identifier.setHash(FileUtils.MD5Hash(sourceFile.getAbsolutePath()));
        else
            identifier.setHash("Directory");

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
        return (onlyIdentified) ? 0 : dtaFile.getSpectraCount();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<Spectrum> getSpectrumIterator(boolean onlyIdentified) {
        return (onlyIdentified) ? Collections.EMPTY_LIST.iterator() : new DtaSpectrumIterator();
    }

    private class DtaSpectrumIterator implements Iterator<Spectrum>, Iterable<Spectrum> {
        /**
         * The spectrum iterator used to create this iterator.
         */
        private Iterator<DtaSpectrum> iterator = dtaFile.getDtaSpectrumIterator();

        private Integer currentId = 1;

        @Override
        public Iterator<Spectrum> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Spectrum next() {
            DtaSpectrum s = iterator.next();

            Spectrum spec = convertDtaSpectrum(s);
            spec.setId(currentId++);

            return spec;
        }

        @Override
        public void remove() {
            // this function is not supported
        }
    }

    /**
     * Converts a DtaSpectrum into a PRIDE Converter Spectrum.
     *
     * @param spec
     * @return
     */
    private Spectrum convertDtaSpectrum(DtaSpectrum dtaSpec) {
        // create the spectrum
        Spectrum spectrum = new Spectrum();

        Map<Double, Double> peakList = dtaSpec.getPeakList();
        
        // convert the peak list to the required byte arrays
        List<Double> masses;
        if (peakList != null)
        	masses = new ArrayList<Double>(peakList.keySet());
        else
        	masses = Collections.emptyList();
        List<Double> intensities = new ArrayList<Double>(masses.size());

        Collections.sort(masses);
        
        // add the intensities in the same order as they are in the m/z array
        for (Double mass : masses)
        	intensities.add(peakList.get(mass));

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
        Float rangeStart = new Float(0), rangeStop = new Float(0);
        if (masses.size() > 0) {
	        Collections.sort(masses);
	        rangeStart = new Float(masses.get(0));
	        rangeStop = new Float(masses.get(masses.size() - 1));
        }

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
        if (dtaSpec.getMhMass() != null)
            ionSelection.getCvParam().add(DAOCvParams.PRECURSOR_MZ.getJaxbParam(dtaSpec.getPrecursorMZ()));
        if (dtaSpec.getPrecursorCharge() != null)
            ionSelection.getCvParam().add(DAOCvParams.CHARGE_STATE.getJaxbParam(dtaSpec.getPrecursorCharge()));

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
        throw new ConverterException("DTA files do not support peptide identifications.");
    }

    @Override
    public Identification getIdentificationByUID(String identificationUID) {
        throw new ConverterException("DTA files do not support peptide identifications.");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<Identification> getIdentificationIterator(
            boolean prescanMode) {
        // not supported
        return Collections.EMPTY_LIST.iterator();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<DatabaseMapping> getDatabaseMappings() {
        return Collections.EMPTY_LIST;
    }

}
