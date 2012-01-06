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
import uk.ac.ebi.pride.tools.jmzreader.JMzReaderException;
import uk.ac.ebi.pride.tools.pkl_parser.PklFile;
import uk.ac.ebi.pride.tools.pkl_parser.model.PklSpectrum;

/**
 * This DAO converts PKL files to PRIDE XML files. It is
 * based on the pkl-parser library.
 *
 * @author jg
 */
public class PklDAO extends AbstractPeakListDAO implements DAO {
    /**
     * The source file to convert. This may either be a
     * concatenated PKL file or a directory containing
     * PKL files with one spectrum each.
     */
    private File sourceFile;
    /**
     * Instance of the pkl-parser to use
     */
    private PklFile pklFile;

    /**
     * Creates a PklDAO.
     *
     * @param sourceFile Either a concatenated PKL file or a directory containing .pkl files with one spectrum each.
     * @throws InvalidFormatException 
     */
    public PklDAO(File sourceFile) throws InvalidFormatException {
        this.sourceFile = sourceFile;

        // create the pkl parser
        try {
			pklFile = new PklFile(sourceFile);
		} catch (JMzReaderException e) {
			throw new InvalidFormatException("Failed to open PKL file.", e);
		}
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Collection<DAOProperty> getSupportedPorperties() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<String> getSpectraIds() {
        // just return the ids returned by the pkl dao
        return pklFile.getSpectraIds();
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

        params.getCvParam().add(DAOCvParams.ORIGINAL_MS_FORMAT.getParam("Micromass PKL"));

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
        file.setFileType("Micromass PKL");

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

        software.setName("Unknown generic (Micromass PKL format)");
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
    public Collection<DatabaseMapping> getDatabaseMappings() {
        return Collections.EMPTY_LIST;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<PTM> getPTMs() {
        // not supported
        return Collections.EMPTY_LIST;
    }

    @Override
    public SearchResultIdentifier getSearchResultIdentifier() {
        // intialize the search result identifier
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
        return (onlyIdentified) ? 0 : pklFile.getSpectraCount();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<Spectrum> getSpectrumIterator(boolean onlyIdentified) {
        return (onlyIdentified) ? Collections.EMPTY_LIST.iterator() : new PklFileSpectrumIterator();
    }

    private class PklFileSpectrumIterator implements Iterator<Spectrum>, Iterable<Spectrum> {
        private Iterator<PklSpectrum> iterator = pklFile.getPklSpectrumIterator();

        private int currentSpecId = 1;

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
            Spectrum spec = convertPklSpectrum(iterator.next());

            spec.setId(currentSpecId++);

            return spec;
        }

        @Override
        public void remove() {
            // not supported
        }
    }

    private Spectrum convertPklSpectrum(PklSpectrum pklSpec) {
        // create the spectrum
        Spectrum spectrum = new Spectrum();

        Map<Double, Double> peakList = pklSpec.getPeakList();
        
        // convert the peak list to the required byte arrays
        ArrayList<Double> masses = new ArrayList<Double>(peakList.keySet());
        ArrayList<Double> intensities = new ArrayList<Double>(masses.size());

        Collections.sort(masses);
        
        // set the intensities in the same order as the masses
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
        Float rangeStart = (masses.size() > 0) ? new Float(masses.get(0)) : null;
        Float rangeStop = (masses.size() > 0) ? new Float(masses.get(masses.size() - 1)) : null;

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
        ionSelection.getCvParam().add(DAOCvParams.PRECURSOR_MZ.getJaxbParam(pklSpec.getObservedMZ()));
        ionSelection.getCvParam().add(DAOCvParams.PRECURSOR_INTENSITY.getJaxbParam(pklSpec.getObservedIntensity()));
        if (pklSpec.getPrecursorCharge() != 0)
            ionSelection.getCvParam().add(DAOCvParams.CHARGE_STATE.getJaxbParam(pklSpec.getPrecursorCharge()));

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
        throw new ConverterException("PKL files do not support peptide identifications.");
    }

    @Override
    public Identification getIdentificationByUID(String identificationUID) {
        throw new ConverterException("PKL files do not support peptide identifications.");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<Identification> getIdentificationIterator(
            boolean prescanMode) {
        // not supported
        return Collections.EMPTY_LIST.iterator();
    }
}
