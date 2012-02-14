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
import uk.ac.ebi.pride.tools.mzxml_parser.MzXMLFile;
import uk.ac.ebi.pride.tools.mzxml_parser.MzXMLParsingException;
import uk.ac.ebi.pride.tools.mzxml_parser.mzxml.model.DataProcessing;
import uk.ac.ebi.pride.tools.mzxml_parser.mzxml.model.Peaks;
import uk.ac.ebi.pride.tools.mzxml_parser.mzxml.model.PrecursorMz;
import uk.ac.ebi.pride.tools.mzxml_parser.mzxml.model.Scan;

/**
 * Converts mzXML files to PRIDE XML files. This
 * DAO is based on the mzxml-parser.
 * @author jg
 *
 */
public class MzXmlDAO extends AbstractPeakListDAO {
	/**
	 * The logger to use.
	 */
	private static final Logger logger = Logger.getLogger(MzXmlDAO.class);
	/**
	 * The mzXML file object used
	 * to parse the mzXML file.
	 */
	private MzXMLFile mzxmlFile;
	/**
	 * The File object pointing to the
	 * mzXML file to convert.
	 */
	private File sourcefile;
	
	/**
	 * Creates a new MzXmlDAO to convert
	 * the passed file.
	 * @param sourcfile File object pointing to the mzXML object to convert.
	 * @throws InvalidFormatException 
	 */
	public MzXmlDAO(File sourcfile) throws InvalidFormatException {
		this.sourcefile = sourcfile;
		
		// create the mzXMl file object
		try {
			mzxmlFile = new MzXMLFile(sourcefile);
		} catch (MzXMLParsingException e) {
			throw new InvalidFormatException("Failed to parse mzXML file '" + sourcefile.getName() + "'.", e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static Collection<DAOProperty> getSupportedProperties() {
		return Collections.emptyList();
	}
	
	public void setConfiguration(Properties props) {
		// no configuration supported
	}

	public Properties getConfiguration() {
		// no configuration supported
		return new Properties();
	}

	public String getExperimentTitle() {
		return "Unknown mzXML experiment.";
	}

	public String getExperimentShortLabel() {
		// not supported
		return null;
	}

	public Param getExperimentParams() {
		Param params = new Param();

        params.getCvParam().add(DAOCvParams.ORIGINAL_MS_FORMAT.getParam("mzXML"));

        return params;
	}

	public String getSampleName() {
		// not available
		return null;
	}

	public String getSampleComment() {
		// not available
		return null;
	}

	public Param getSampleParams() {
		// not available
		return new Param();
	}
 
	public SourceFile getSourceFile() {
		// initialize the return variable
        SourceFile file = new SourceFile();

        file.setPathToFile(sourcefile.getAbsolutePath());
        file.setNameOfFile(sourcefile.getName());
        file.setFileType("mzXML");

        return file;
	}

	public Collection<Contact> getContacts() {
		// not supported
		return null;
	}

	public InstrumentDescription getInstrument() {
		// no instrument descriptions are returned as they
		// cannot be safely converted to cvParams
		return null;
	}

	public Software getSoftware() throws InvalidFormatException {
		Software software = new Software();
		
		software.setName("Unknown (generic mzXML)");
		software.setVersion("Unknown");
		
		// if there's only one dataProcessing, extract this information
		try {
			List<DataProcessing> dataProcessings = mzxmlFile.getDataProcessing();
			
			if (dataProcessings == null)
				throw new InvalidFormatException("Missing DataProcessing section in mzXML file.");
			
			if (dataProcessings.size() == 1) {
				DataProcessing processing = dataProcessings.get(0);
				
				software.setName(processing.getSoftware().getName());
				software.setVersion(processing.getSoftware().getVersion());
			}
		} catch (MzXMLParsingException e) {
			// ingore any problems
		}
		
		return software;
	}

	public Param getProcessingMethod() {
		Param param = new Param();
		
		// process the data processing
		try {
			List<DataProcessing> dataProcessings = mzxmlFile.getDataProcessing();
			
			for (DataProcessing p : dataProcessings) {
				try {
					// TODO: find a sensible cvParam for centroiding
//					if (p.isCentroided())
//						param.getCvParam().add(DAOCvParams.CENTROIDED_SPECTRUM.getParam());
					if (p.isDeisotoped())
						param.getCvParam().add(DAOCvParams.DEISOTOPING.getParam());
					if (p.isChargeDeconvoluted())
						param.getCvParam().add(DAOCvParams.CHARGE_DECONVOLUTION.getParam());
				}
				catch (NullPointerException e) {
					// ignore null pointer exceptions and just continue
				}
			}
		} catch (MzXMLParsingException e) {
			// ignore any problems
		}
		
		return param;
	}

	public Protocol getProtocol() {
		// not available
		return null;
	}

	public Collection<Reference> getReferences() {
		// not available
		return null;
	}

	public String getSearchDatabaseName() {
		// not available
		return "";
	}

	public String getSearchDatabaseVersion() {
		// not available
		return "";
	}

	public Collection<PTM> getPTMs() {
		// not available
		return Collections.emptyList();
	}

	public Collection<DatabaseMapping> getDatabaseMappings() {
		// not available
		return Collections.emptyList();
	}

	public SearchResultIdentifier getSearchResultIdentifier() {
		// initialize the search result identifier
        SearchResultIdentifier identifier = new SearchResultIdentifier();

        // format the current time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        identifier.setSourceFilePath(sourcefile.getAbsolutePath());
        identifier.setTimeCreated(formatter.format(new Date(System.currentTimeMillis())));
        identifier.setHash(FileUtils.MD5Hash(sourcefile.getAbsolutePath()));

        return identifier;
	}

	public Collection<CV> getCvLookup() {
		// just create a set containing the 2 cvLookups used here
        ArrayList<CV> cvs = new ArrayList<CV>();

        cvs.add(new CV("MS", "PSI Mass Spectrometry Ontology", "1.2", "http://psidev.cvs.sourceforge.net/viewvc/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo"));
        cvs.add(new CV("PRIDE", "PRIDE Controlled Vocabulary", "1.101", "http://ebi-pride.googlecode.com/svn/trunk/pride-core/schema/pride_cv.obo"));

        return cvs;
	}

	public int getSpectrumCount(boolean onlyIdentified) {
		// either return 0 or the number of MS1 + MS2 scans
		return (onlyIdentified) ? 0 : mzxmlFile.getMS1ScanCount() + mzxmlFile.getMS2ScanCount();
	}

	@SuppressWarnings("unchecked")
	public Iterator<Spectrum> getSpectrumIterator(boolean onlyIdentified) {
		if (onlyIdentified)
			return Collections.EMPTY_LIST.iterator();
		else
			return new MzXmlSpectraIterator();
	}

	public int getSpectrumReferenceForPeptideUID(String peptideUID) {
		// not available
		throw new ConverterException("mzXML files cannot contain peptides.");
	}

	public Identification getIdentificationByUID(String identificationUID) {
		// not available
		throw new ConverterException("mzXML files cannot contain identification.");
	}

	@SuppressWarnings("unchecked")
	public Iterator<Identification> getIdentificationIterator(
			boolean prescanMode) {
		return Collections.EMPTY_LIST.iterator();
	}

	@Override
	public List<String> getSpectraIds() {
		// convert the spectra numbers to strings
		List<Long> spectraNumbers = mzxmlFile.getScanNumbers();
		ArrayList<String> specIds = new ArrayList<String>(spectraNumbers.size());
		
		for (Long l : spectraNumbers)
			specIds.add(l.toString());
		
		return specIds;
	}

	private class MzXmlSpectraIterator implements Iterator<Spectrum>, Iterable<Spectrum> {
		/**
		 * Iterator over all the spectra.
		 */
		private Iterator<Scan> level1Iterator = mzxmlFile.getScanIterator();
		/**
		 * Index of the currently processed ms2 spectrum
		 * in the current ms1 spectrum.
		 */
		private int level2Index = 0;
		/**
		 * INdex of the currently processed ms3 spectrum
		 * in the current ms2 spectrum.
		 */
		private int level3Index = 0;
		/**
		 * The current level 1 spectrum.
		 */
		private Scan currentL1Spec;
		/**
		 * The current level 2 spectrum
		 */
		private Scan currentL2Spec;

		public Iterator<Spectrum> iterator() {
			return this;
		}

		public boolean hasNext() {
			// check if there's another spectrum to
			// process or another child spectrum left.
			return (level1Iterator.hasNext() || level2Index < currentL1Spec.getScan().size());
		}

		public Spectrum next() {
			Spectrum s = null;
			
			// get the next level 1 spectrum if there's none yet
			// or if there are no level 2 spectra left
			try {
				if (currentL1Spec == null || level2Index >= currentL1Spec.getScan().size()) {
					currentL1Spec = level1Iterator.next();
					
					// reset the ms2LevelIndex
					level2Index = 0;
					currentL2Spec = null;
					
					// convert the scan (MS1 level spectra have no precursor)
					s = convertScan(currentL1Spec, 0L);
				}
				else if (currentL2Spec == null || level3Index >= currentL2Spec.getScan().size()) {
					// get the level 2 spec
					currentL2Spec = currentL1Spec.getScan().get(level2Index++);
					
					level3Index = 0;
					
					s = convertScan(currentL2Spec, currentL1Spec.getNum());
				}
				// check if there's a valid level 3 scan
				else if (level3Index < currentL2Spec.getScan().size()) {
					// get the level 3 spec
					Scan level3Scan = currentL2Spec.getScan().get(level3Index++);
					
					s = convertScan(level3Scan, currentL2Spec.getNum());
				}
			}
			catch(InvalidFormatException e) {
				throw new ConverterException(e);
			}
			
			return s;
		}
		
		public void remove() {
			// not available			
		}
		
	}
	
	/**
	 * Converts the passed Scan object to a PRIDE
	 * JAXB spectrum object. The scan's number is
	 * used as a mzData index.
	 * @param scan
	 * @param precursorNum The precuror's number.
	 * @return
	 * @throws InvalidFormatException 
	 */
	private Spectrum convertScan(Scan scan, Long precursorNum) throws InvalidFormatException {
		Spectrum spectrum = new Spectrum();
		
		if (scan.getPeaks() == null)
			throw new InvalidFormatException("Missing required \"peaks\" element in scan object.");
		
		// get the peak list (only one is supported)
		if (scan.getPeaks().size() > 1)
			logger.error("Multiple peak lists found to spectrum " + scan.getNum() + ". Only the first one is taken into consideration");
		
		Peaks peaks = scan.getPeaks().get(0);

        // get the peak list
		Map<Double, Double> peakList;
		try {
			peakList = MzXMLFile.convertPeaksToMap(peaks);
		} catch (MzXMLParsingException e) {
			throw new InvalidFormatException("Failed to convert spectrum's peak list (num = " + scan.getNum() + ")");
		}

		// set the spectrum's id
		if (scan.getNum() == null)
			throw new InvalidFormatException("Missing required \"num\" attribute in scan object.");
		
        spectrum.setId(scan.getNum().intValue());

        // set the precursor list
        SpectrumDesc spectrumDesc = new SpectrumDesc();

        // create the spectrumSettings/spectrumInstrument (mzRangeStop, mzRangeStart, msLevel)
        SpectrumSettings settings = new SpectrumSettings();
        SpectrumInstrument instrument = new SpectrumInstrument();

        // set the ms level
        if (scan.getMsLevel() == null)
        	throw new InvalidFormatException("Scan object missing required attribute \"msLevel\"");
        
        instrument.setMsLevel(scan.getMsLevel().intValue());

        // sort the masses to get the minimum and max
        if (peakList.size() > 0) {
        	instrument.setMzRangeStart(Collections.min(peakList.keySet()).floatValue());
        	instrument.setMzRangeStop(Collections.max(peakList.keySet()).floatValue());
        }

        // set the spectrum settings
        settings.setSpectrumInstrument(instrument);
        spectrumDesc.setSpectrumSettings(settings);
        
        spectrumDesc.setPrecursorList(convertPrecursorList(scan, instrument.getMsLevel(), precursorNum));

        spectrum.setSpectrumDesc(spectrumDesc);

        // set the data arrays
        ArrayList<Double> mzValues = new ArrayList<Double>(peakList.size());
        mzValues.addAll(peakList.keySet());
        if (mzValues.size() > 0)
        	Collections.sort(mzValues);
        
        ArrayList<Double> intensities = new ArrayList<Double>(peakList.size());
        for (Double mz : mzValues)
        	intensities.add(peakList.get(mz));
        
        // create the byte arrays
        byte[] massesBytes = doubleCollectionToByteArray(mzValues);
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

        spectrum.setIntenArrayBinary(intenArrayBin);
        spectrum.setMzArrayBinary(massArrayBinary);

        return spectrum;
	}

	/**
	 * Converts the precursor information found
	 * in the passed scan into a PrecursorList object.
	 * @param scan
	 * @param msLevel The ms level of the child spectrum. The precursor's ms level is set to -1 of the child's spectrum.
	 * @param precursorNumber The precur's number.
	 * @return
	 */
	private PrecursorList convertPrecursorList(Scan scan, int msLevel, Long precursorNumber) {
		PrecursorList precursorList = new PrecursorList();
		List<PrecursorMz> precursors = scan.getPrecursorMz();
		
		if (precursors.size() < 1)
			return null;
		
		// set the count
		precursorList.setCount(precursors.size());
		
		// create the precursors
		for (PrecursorMz prec : precursors) {
			Precursor precursor = new Precursor();
			
			// set the activation method
			uk.ac.ebi.pride.jaxb.model.Param activation = new uk.ac.ebi.pride.jaxb.model.Param();
			if (prec.getActivationMethod() != null)
				activation.getUserParam().add(userParam("Activation method", prec.getActivationMethod()));
			precursor.setActivation(activation);
			
			// just set the ms level to one below the current one
			precursor.setMsLevel(msLevel - 1);
			
			// set the ion selection
			uk.ac.ebi.pride.jaxb.model.Param ionSelection = new uk.ac.ebi.pride.jaxb.model.Param();
			
			ionSelection.getCvParam().add(DAOCvParams.PRECURSOR_MZ.getJaxbParam(prec.getValue()));
			ionSelection.getCvParam().add(DAOCvParams.PRECURSOR_INTENSITY.getJaxbParam(prec.getPrecursorIntensity()));
			if (prec.getPrecursorCharge() != null)
				ionSelection.getCvParam().add(DAOCvParams.CHARGE_STATE.getJaxbParam(prec.getPrecursorCharge()));
			
			precursor.setIonSelection(ionSelection);
			
			// set the precursor spectrum id
			Spectrum precSpec = new Spectrum();
			
			if (prec.getPrecursorScanNum() != null)
				precSpec.setId(prec.getPrecursorScanNum().intValue());
			else
				precSpec.setId(precursorNumber.intValue());
			
			precursor.setSpectrum(precSpec);
			
			precursorList.getPrecursor().add(precursor);
		}
		
		return precursorList;
	}
	
	private uk.ac.ebi.pride.jaxb.model.UserParam userParam(String name, String value) {
		uk.ac.ebi.pride.jaxb.model.UserParam param = new uk.ac.ebi.pride.jaxb.model.UserParam();
		
		param.setName(name);
		param.setValue(value);
		
		return param;
	}
}
