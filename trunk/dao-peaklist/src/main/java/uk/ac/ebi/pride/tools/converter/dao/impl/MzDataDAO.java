package uk.ac.ebi.pride.tools.converter.dao.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import uk.ac.ebi.pride.jaxb.model.AcqSpecification;
import uk.ac.ebi.pride.jaxb.model.Aquisition;
import uk.ac.ebi.pride.jaxb.model.Data;
import uk.ac.ebi.pride.jaxb.model.IntenArrayBinary;
import uk.ac.ebi.pride.jaxb.model.MzArrayBinary;
import uk.ac.ebi.pride.jaxb.model.Precursor;
import uk.ac.ebi.pride.jaxb.model.PrecursorList;
import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.jaxb.model.SpectrumDesc;
import uk.ac.ebi.pride.jaxb.model.SpectrumInstrument;
import uk.ac.ebi.pride.jaxb.model.SpectrumSettings;
import uk.ac.ebi.pride.jaxb.model.SupDataDesc;
import uk.ac.ebi.pride.jaxb.model.SupDesc;
import uk.ac.ebi.pride.tools.converter.dao.DAO;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.dao.DAOProperty;
import uk.ac.ebi.pride.tools.converter.report.model.CV;
import uk.ac.ebi.pride.tools.converter.report.model.Contact;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.DatabaseMapping;
import uk.ac.ebi.pride.tools.converter.report.model.Identification;
import uk.ac.ebi.pride.tools.converter.report.model.InstrumentDescription;
import uk.ac.ebi.pride.tools.converter.report.model.InstrumentDescription.AnalyzerList;
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
import uk.ac.ebi.pride.tools.mzdata_parser.MzDataFile;
import uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.CvLookup;
import uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.Description;
import uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.Person;
import uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.SpectrumSettings.AcqSpecification.Acquisition;

public class MzDataDAO extends AbstractPeakListDAO implements DAO {
	/**
	 * The sourcefile to convert.
	 */
	private final File sourcefile;
	/**
	 * The instance of the mzData parser used
	 * to read the mzData file.
	 */
	private final MzDataFile mzdataFile;
	/**
	 * The mzData file's description object.
	 */
	private uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.MzData.Description mzDataDescription;
	
	/**
	 * Creates a new instance of the MzDataDAO used
	 * to convert the given mzData file to a PRIDE
	 * XML file.
	 * @param sourcefile The mzData file to convert.
	 * @throws InvalidFormatException
	 */
	public MzDataDAO(File sourcefile) throws InvalidFormatException {
		// make sure a sourcefile was passed
		if (sourcefile == null || !sourcefile.exists())
			throw new InvalidFormatException("Failed to find mzData file.");
		if (!sourcefile.canRead())
			throw new InvalidFormatException("Missing privilege to read mzData file.");
		
		// save the sourcefile
		this.sourcefile = sourcefile;
		
		try {
			// create the mzData file object.
			mzdataFile = new MzDataFile(sourcefile);
		} catch (JMzReaderException e) {
			throw new InvalidFormatException("Failed to parse mzData file.", e);
		}
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
    public static Collection<DAOProperty> getSupportedProperties() {
        return Collections.EMPTY_LIST;
    }

	@Override
	public void setConfiguration(Properties props) {
		// no configuration supported
		
	}

	@Override
	public Properties getConfiguration() {
		// no configuration supported
		return new Properties();
	}

	@Override
	public String getExperimentTitle() throws InvalidFormatException {
		// return the sample name
		try {
			return getDescription().getAdmin().getSampleName();
		} catch (Exception e) {
			throw new InvalidFormatException("Failed to fetch sample name from mzData file.", e);
		}
	}

	@Override
	public String getExperimentShortLabel() {
		return null;
	}

	@Override
	public Param getExperimentParams() throws InvalidFormatException {
		Param params = new Param();

        params.getCvParam().add(DAOCvParams.ORIGINAL_MS_FORMAT.getParam("mzData"));

        return params;
	}

	@Override
	public String getSampleName() {
		// return the sample name
		try {
			return (getDescription().getAdmin().getSampleName());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getSampleComment() {
		// not supported
		return null;
	}

	@Override
	public Param getSampleParams() throws InvalidFormatException {
		try {
			Description sampleDescription = getDescription().getAdmin().getSampleDescription();
			
			// convert the params
			return convertParam(sampleDescription);
		} catch (JMzReaderException e) {
			return null;
		}
	}

	@Override
	public SourceFile getSourceFile() throws InvalidFormatException {
		// initialize the return variable
        SourceFile file = new SourceFile();

        file.setPathToFile(sourcefile.getAbsolutePath());
        file.setNameOfFile(sourcefile.getName());
        file.setFileType("mzData");

        return file;
	}

	@Override
	public Collection<Contact> getContacts() {
		// process the contacts
		List<Contact> contacts = new ArrayList<Contact>();
		
		try {
			for (Person p : getDescription().getAdmin().getContact()) {
				Contact c = new Contact();
				c.setName(p.getName());
				c.setInstitution(p.getInstitution());
				c.setContactInfo(p.getContactInfo());
				
				contacts.add(c);
			}
			
			return contacts;
		} catch (JMzReaderException e) {
			// ignore any problems
			return null;
		}
	}

	@Override
	public InstrumentDescription getInstrument() {
		// convert the instrument description
		InstrumentDescription description = new InstrumentDescription();
		
		try {
			uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.InstrumentDescription instrumentDescription = getDescription().getInstrument();
			
			// copy the data...
			description.setInstrumentName(instrumentDescription.getInstrumentName());
			description.setSource(convertParam(instrumentDescription.getSource()));
			description.setDetector(convertParam(instrumentDescription.getDetector()));
			description.setAdditional(convertParam(instrumentDescription.getAdditional()));
			
			// copy the analyzers
			AnalyzerList list = new AnalyzerList();
			list.setCount(instrumentDescription.getAnalyzerList().getCount());
			
			for (uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.Param analyzer : instrumentDescription.getAnalyzerList().getAnalyzer())
				list.getAnalyzer().add(convertParam(analyzer));
			
			description.setAnalyzerList(list);
			
			return description;
		} catch (JMzReaderException e) {
			// ignore any problems as this is an optional function
			return null;
		}
	}

	@Override
	public Software getSoftware() throws InvalidFormatException {
		// copy the software information
		Software software = new Software();
		
		uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.Software mzdataSoftware;
		try {
			mzdataSoftware = getDescription().getDataProcessing().getSoftware();
			
			software.setName(mzdataSoftware.getName());
			software.setVersion(mzdataSoftware.getVersion());
			software.setComments(mzdataSoftware.getComments());
			software.setCompletionTime(mzdataSoftware.getCompletionTime());
			
			return software;
		} catch (JMzReaderException e) {
			// ignore any problems as this is an optional function
			return null;
		}
	}

	@Override
	public Param getProcessingMethod() {
		try {
			return convertParam(getDescription().getDataProcessing().getProcessingMethod());
		} catch (JMzReaderException e) {
			// ignore any problems as this is an optional function
			return null;
		}
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
	public String getSearchDatabaseName() throws InvalidFormatException {
		// not supported
		return "";
	}

	@Override
	public String getSearchDatabaseVersion() throws InvalidFormatException {
		// not supported
		return "";
	}

	@Override
	public Collection<PTM> getPTMs() throws InvalidFormatException {
		// not available
		return Collections.emptyList();
	}

	@Override
	public Collection<DatabaseMapping> getDatabaseMappings()
			throws InvalidFormatException {
		// not available
		return Collections.emptyList();
	}

	@Override
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

	@Override
	public Collection<CV> getCvLookup() throws InvalidFormatException {
		// just create a set containing the 2 cvLookups used here
        ArrayList<CV> cvs = new ArrayList<CV>();
        
        boolean msAdded = false, prideAdded = false;
        
        try {
	        for (CvLookup cvLookup : mzdataFile.getCvLookups()) {
	        	cvs.add(new CV(cvLookup.getCvLabel(), cvLookup.getFullName(), cvLookup.getVersion(), cvLookup.getAddress()));
	        	
	        	if (cvLookup.getCvLabel().equals("PRIDE"))
	        		prideAdded = true;
	        	if (cvLookup.getCvLabel().equals("MS"))
	        		msAdded = true;
	        }
        }
        catch (Exception e) {
        	// just ignore any problem
        }

        if (!msAdded)
        	cvs.add(new CV("MS", "PSI Mass Spectrometry Ontology", "1.2", "http://psidev.cvs.sourceforge.net/viewvc/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo"));
        if (!prideAdded)
        	cvs.add(new CV("PRIDE", "PRIDE Controlled Vocabulary", "1.101", "http://ebi-pride.googlecode.com/svn/trunk/pride-core/schema/pride_cv.obo"));

        return cvs;
	}

	@Override
	public int getSpectrumCount(boolean onlyIdentified)
			throws InvalidFormatException {
		return onlyIdentified ? 0 : mzdataFile.getSpectraCount();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Spectrum> getSpectrumIterator(boolean onlyIdentified)
			throws InvalidFormatException {
		return onlyIdentified ? Collections.EMPTY_LIST.iterator() : new SpectrumIterator();
	}
	
	/**
	 * A simple wrapper class around the MzDataFile
	 * iterator class.
	 * @author jg
	 *
	 */
	private class SpectrumIterator implements Iterator<Spectrum> {
		private final Iterator<uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.Spectrum> it = mzdataFile.getMzDataSpectrumIterator();
		
		@Override
		public boolean hasNext() {
			return it.hasNext();
		}

		@Override
		public Spectrum next() {
			return convertSpectrum(it.next());
		}

		@Override
		public void remove() {
			// not supported			
		}
	}

	@Override
	public int getSpectrumReferenceForPeptideUID(String peptideUID)
			throws InvalidFormatException {
		throw new ConverterException("mzData files do not support identifications.");
	}

	@Override
	public Identification getIdentificationByUID(String identificationUID)
			throws InvalidFormatException {
		throw new ConverterException("mzData files do not support identifications.");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Identification> getIdentificationIterator(
			boolean prescanMode) throws InvalidFormatException {
		return Collections.EMPTY_LIST.iterator();
	}

	@Override
	public List<String> getSpectraIds() {
		return mzdataFile.getSpectraIds();
	}

	/**
	 * Converts a mzData Param object into a 
	 * report file Param object.
	 * @param mzDataParam
	 * @return
	 */
	private Param convertParam(uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.Param mzDataParam) {
		if (mzDataParam == null)
			return null;
		
		Param param = new Param();
		
		for (uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.CvParam p : mzDataParam.getCvParams())
			param.getCvParam().add(new CvParam(p.getCvLabel(), p.getAccession(), p.getName(), p.getValue()));
		
		for (uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.UserParam p : mzDataParam.getUserParams())
			param.getUserParam().add(new UserParam(p.getName(), p.getValue()));
		
		return param;
	}
	
	/**
	 * Converts a mzData Param object into a 
	 * PRIDE JAXB Param object.
	 * @param mzDataParam
	 * @return
	 */
	private uk.ac.ebi.pride.jaxb.model.Param convertJParam(uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.Param mzDataParam) {
		if (mzDataParam == null)
			return null;
		
		uk.ac.ebi.pride.jaxb.model.Param param = new uk.ac.ebi.pride.jaxb.model.Param();
		
		for (uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.CvParam p : mzDataParam.getCvParams()) {
			uk.ac.ebi.pride.jaxb.model.CvParam cvp = new uk.ac.ebi.pride.jaxb.model.CvParam();
			cvp.setCvLabel(p.getCvLabel());
			cvp.setAccession(p.getAccession());
			cvp.setName(p.getName());
			cvp.setValue(p.getValue());
			
			param.getCvParam().add(cvp);
		}
		
		for (uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.UserParam p : mzDataParam.getUserParams()) {
			uk.ac.ebi.pride.jaxb.model.UserParam up = new uk.ac.ebi.pride.jaxb.model.UserParam();
			up.setName(p.getName());
			up.setValue(p.getValue());
			
			param.getUserParam().add(up);
		}
		
		return param;
	}
	
	/**
	 * Returns the mzData file's description object.
	 * This function makes sure that the object is
	 * only unmarshalled once.
	 * @return
	 * @throws JMzReaderException
	 */
	private uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.MzData.Description getDescription() throws JMzReaderException {
		// if the description object was already unmarshalled return it
		if (mzDataDescription != null)
			return mzDataDescription;
		
		// unmarshall the description object
		mzDataDescription = mzdataFile.getDescription();
		
		return mzDataDescription;
	}
	
	/**
	 * Converts a mzData Spectrum object
	 * into a PRIDE JAXB spectrum object. These
	 * are identical therefore the data is directly
	 * copied.
	 * @param mzDataSpectrum
	 * @return
	 */
	private Spectrum convertSpectrum(uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.Spectrum mzDataSpectrum) {
		Spectrum s = new Spectrum();
		
		// copy the id
		s.setId(mzDataSpectrum.getId());
		
		// copy the spectrum description
		SpectrumDesc specDesc = new SpectrumDesc();
		
		// spectrum settings
		uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.SpectrumSettings mzDataSettings = mzDataSpectrum.getSpectrumDesc().getSpectrumSettings();
		SpectrumSettings specSettings = new SpectrumSettings();
		specDesc.setSpectrumSettings(specSettings);
		
		// acqSpecification
		if (mzDataSettings.getAcqSpecification() != null) {
			AcqSpecification acq = new AcqSpecification();
			acq.setSpectrumType(mzDataSettings.getAcqSpecification().getSpectrumType());
			acq.setMethodOfCombination(mzDataSettings.getAcqSpecification().getMethodOfCombination());
			acq.setCount(mzDataSettings.getAcqSpecification().getCount());
			
			// add the acquisitions
			for (Acquisition mzAq : mzDataSettings.getAcqSpecification().getAcquisition()) {
				Aquisition aqu = new Aquisition();
				aqu.setAcqNumber(mzAq.getAcqNumber());
				
				// copy the params...
				for (uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.CvParam p : mzAq.getCvParams()) {
					uk.ac.ebi.pride.jaxb.model.CvParam cvp = new uk.ac.ebi.pride.jaxb.model.CvParam();
					cvp.setCvLabel(p.getCvLabel());
					cvp.setAccession(p.getAccession());
					cvp.setName(p.getName());
					cvp.setValue(p.getValue());
					
					aqu.getCvParam().add(cvp);
				}
				
				for (uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.UserParam p : mzAq.getUserParams()) {
					uk.ac.ebi.pride.jaxb.model.UserParam up = new uk.ac.ebi.pride.jaxb.model.UserParam();
					up.setName(p.getName());
					up.setValue(p.getValue());
					
					aqu.getUserParam().add(up);
				}
			}
			
			specSettings.setAcqSpecification(acq);
		}
		
		// spectrum instrument
		SpectrumInstrument specInstr = new SpectrumInstrument();
		uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.SpectrumSettings.SpectrumInstrument mzSpecInstr = mzDataSettings.getSpectrumInstrument();
		
		specInstr.setMsLevel(mzSpecInstr.getMsLevel());
		specInstr.setMzRangeStart(mzSpecInstr.getMzRangeStart());
		specInstr.setMzRangeStop(mzSpecInstr.getMzRangeStop());
		
		// copy the params...
		for (uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.CvParam p : mzSpecInstr.getCvParams()) {
			uk.ac.ebi.pride.jaxb.model.CvParam cvp = new uk.ac.ebi.pride.jaxb.model.CvParam();
			cvp.setCvLabel(p.getCvLabel());
			cvp.setAccession(p.getAccession());
			cvp.setName(p.getName());
			cvp.setValue(p.getValue());
			
			specInstr.getCvParam().add(cvp);
		}
		
		for (uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.UserParam p : mzSpecInstr.getUserParams()) {
			uk.ac.ebi.pride.jaxb.model.UserParam up = new uk.ac.ebi.pride.jaxb.model.UserParam();
			up.setName(p.getName());
			up.setValue(p.getValue());
			
			specInstr.getUserParam().add(up);
		}
		
		specSettings.setSpectrumInstrument(specInstr);
		
		// precursor list
		if (mzDataSpectrum.getSpectrumDesc().getPrecursorList() != null) {
			PrecursorList precList = new PrecursorList();
			specDesc.setPrecursorList(precList);
			
			uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.SpectrumDesc.PrecursorList mzPrecList = mzDataSpectrum.getSpectrumDesc().getPrecursorList();
			
			precList.setCount(mzPrecList.getCount());
			
			for (uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.Precursor mzPrec : mzPrecList.getPrecursor()) {
				Precursor prec = new Precursor();
				
				prec.setMsLevel(mzPrec.getMsLevel());
				Spectrum specRef = new Spectrum();
				specRef.setId(mzPrec.getSpectrumRef());
				prec.setSpectrum(specRef);
				
				prec.setIonSelection(convertJParam(mzPrec.getIonSelection()));
				prec.setActivation(convertJParam(mzPrec.getActivation()));
				
				precList.getPrecursor().add(prec);
			}
		}
		
		// add the comments
		for (String mzComment : mzDataSpectrum.getSpectrumDesc().getComments())
			specDesc.getComments().add(mzComment);
		
		s.setSpectrumDesc(specDesc);
		
		// add the supDesc
		if (mzDataSpectrum.getSupDesc() != null) {
			for (uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.SupDesc mzSupDesc : mzDataSpectrum.getSupDesc()) {
				SupDesc supDesc = new SupDesc();
				supDesc.setSupDataArrayRef(mzSupDesc.getSupDataArrayRef());
				
				if (mzSupDesc.getSupDataDesc() != null) {
					SupDataDesc desc = new SupDataDesc();
					
					// copy the params...
					for (uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.CvParam p : mzSupDesc.getSupDataDesc().getCvParams()) {
						uk.ac.ebi.pride.jaxb.model.CvParam cvp = new uk.ac.ebi.pride.jaxb.model.CvParam();
						cvp.setCvLabel(p.getCvLabel());
						cvp.setAccession(p.getAccession());
						cvp.setName(p.getName());
						cvp.setValue(p.getValue());
						
						desc.getCvParam().add(cvp);
					}
					
					for (uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.UserParam p : mzSupDesc.getSupDataDesc().getUserParams()) {
						uk.ac.ebi.pride.jaxb.model.UserParam up = new uk.ac.ebi.pride.jaxb.model.UserParam();
						up.setName(p.getName());
						up.setValue(p.getValue());
						
						desc.getUserParam().add(up);
					}
					
					// copy the comment
					desc.setComment(mzSupDesc.getSupDataDesc().getComment());
					
					supDesc.setSupDataDesc(desc);
				}
				
				// add the source files
				if (mzSupDesc.getSupSourceFile() != null) {
					for (uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.SourceFile mzSourceFile : mzSupDesc.getSupSourceFile()) {
						uk.ac.ebi.pride.jaxb.model.SourceFile sourceFile = new uk.ac.ebi.pride.jaxb.model.SourceFile();
						sourceFile.setNameOfFile(mzSourceFile.getNameOfFile());
						sourceFile.setFileType(mzSourceFile.getFileType());
						sourceFile.setPathToFile(mzSourceFile.getPathToFile());
						
						supDesc.getSupSourceFile().add(sourceFile);
					}
				}
				
				s.getSupDesc().add(supDesc);
			}
		}
		
		// mzArray
		uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.PeakListBinary mzDataMzArray = mzDataSpectrum.getMzArrayBinary();
		MzArrayBinary mzArray = new MzArrayBinary();
		
		Data mzData = new Data();
		mzData.setEndian(mzDataMzArray.getData().getEndian());
		mzData.setLength(mzDataMzArray.getData().getLength());
		mzData.setPrecision(mzDataMzArray.getData().getPrecision());
		mzData.setValue(mzDataMzArray.getData().getValue());
		
		mzArray.setData(mzData);
		s.setMzArrayBinary(mzArray);
		
		// intenArray
		uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.PeakListBinary mzDataIntenArray = mzDataSpectrum.getIntenArrayBinary();
		IntenArrayBinary intenArray = new IntenArrayBinary();
		
		Data intenData = new Data();
		intenData.setEndian(mzDataIntenArray.getData().getEndian());
		intenData.setLength(mzDataIntenArray.getData().getLength());
		intenData.setPrecision(mzDataIntenArray.getData().getPrecision());
		intenData.setValue(mzDataIntenArray.getData().getValue());
		
		intenArray.setData(intenData);
		s.setIntenArrayBinary(intenArray);
		
		// TODO: add support for supData array
		// these objects just need to be copied from one
		// object model to the other. but as they've never
		// been used in PRIDE until now this wasn't implemented
		// yet.
		
		return s;
	}
}
