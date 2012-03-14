package uk.ac.ebi.pride.tools.converter.conversion.io;

import org.apache.commons.beanutils.ConversionException;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.jaxb.model.CvParam;
import uk.ac.ebi.pride.jaxb.model.*;
import uk.ac.ebi.pride.jaxb.model.Param;
import uk.ac.ebi.pride.jaxb.model.SimpleGel;
import uk.ac.ebi.pride.jaxb.model.UserParam;
import uk.ac.ebi.pride.jaxb.xml.marshaller.PrideXmlMarshaller;
import uk.ac.ebi.pride.jaxb.xml.marshaller.PrideXmlMarshallerFactory;
import uk.ac.ebi.pride.tools.converter.dao.DAO;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.dao.DAOFactory;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReader;
import uk.ac.ebi.pride.tools.converter.report.io.xml.marshaller.ReportMarshaller;
import uk.ac.ebi.pride.tools.converter.report.io.xml.marshaller.ReportMarshallerFactory;
import uk.ac.ebi.pride.tools.converter.report.model.*;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.FileUtils;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;
import uk.ac.ebi.pride.tools.converter.utils.memory.MemoryUsage;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.zip.GZIPOutputStream;

/**
 * User: rcote
 * Date: 19/01/11
 * Time: 15:41
 * <p/>
 * This is the main class to write PRIDE XML files. It will take a ReportReader and a DAO and
 * marshall out PRIDE XML in a memory-efficient way.
 */
public class PrideXmlWriter {

    private static final double MASS_DELTA_CUTOFF_VALUE = 1;
    private static final Logger logger = Logger.getLogger(PrideXmlWriter.class);

    private static IntenArrayBinary EMPTY_INTEN_ARRAY;
    private static MzArrayBinary EMPTY_MZ_ARRAY;

    static {
        // create the byte arrays
        // allocate the memory for the bytes to store
        ByteBuffer bytes = ByteBuffer.allocate(8);
        bytes.order(ByteOrder.LITTLE_ENDIAN); // save everything in LITTLE ENDIAN format as it's the standard
        bytes.putDouble(0, 0);
        // convert the ByteBuffer to a byte[]
        byte[] byteArray = new byte[8]; // allocate the memory for the byte[] array
        bytes.get(byteArray);

        // create the intensity array
        Data arrayData = new Data();
        arrayData.setEndian("little");
        arrayData.setLength(byteArray.length);
        arrayData.setPrecision("64");
        arrayData.setValue(byteArray);

        EMPTY_INTEN_ARRAY = new IntenArrayBinary();
        EMPTY_INTEN_ARRAY.setData(arrayData);

        EMPTY_MZ_ARRAY = new MzArrayBinary();
        EMPTY_MZ_ARRAY.setData(arrayData);
    }


    private ReportReader reader;
    private DAO dao;
    private String outputFilePath;
    private Metadata meta;
    private PrideXmlMarshaller prideJaxbMarshaller;
    private ReportMarshaller reportJaxbMarshaller;
    private boolean includeOnlyIdentifiedSpectra = false;
    private HashMap<String, PTM> ptmCache;
    private boolean gzipCompress = false;
    private HashMap<String, DatabaseMapping> databaseMappingCache;

    /**
     * Main constructor. Requires an outputfile path as well as a valid ReportReader and DAO
     *
     * @param outputFilePath - the full name and path of the file to be written. This must be a file name, and not
     *                       a directory name!
     * @param reader         - a report reader object that will parse data from an existing report file
     * @param dao            - a fully-instanciated dao object that will extract data from source files
     */
    public PrideXmlWriter(String outputFilePath, ReportReader reader, DAO dao, boolean gzipCompress) {

        this.outputFilePath = outputFilePath;
        this.reader = reader;
        this.dao = dao;
        this.gzipCompress = gzipCompress;
        meta = reader.getMetadata();
        prideJaxbMarshaller = PrideXmlMarshallerFactory.getInstance().initializeMarshaller();
        reportJaxbMarshaller = ReportMarshallerFactory.getInstance().initializeMarshaller();

        //sanity check on compression and filename suffix
        if (gzipCompress) {
            //check to see if outputFilePath ends in .gz if gzip turned on
            if (!outputFilePath.endsWith(FileUtils.gz)) {
                //update file name
                outputFilePath += FileUtils.gz;
            }
        } else {
            //check to see if outputFilePath ends if .gz with gzip turned off
            if (outputFilePath.toLowerCase().endsWith(FileUtils.gz)) {
                //automatically turn on compression
                this.gzipCompress = true;
            }
        }

    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    /**
     * Setter method to indicate if all spectra should be marshalled out (if false) or if only identified spectra
     * should be marshalled out (if true).
     *
     * @param includeOnlyIdentifiedSpectra
     */
    public void setIncludeOnlyIdentifiedSpectra(boolean includeOnlyIdentifiedSpectra) {
        this.includeOnlyIdentifiedSpectra = includeOnlyIdentifiedSpectra;
    }

    /**
     * Main logic method. If gzipCompress is set to true, the resulting file will be gzipped. Note that if
     * gzipCompress is set to true, the outputFilePath will automatically be appended with ".gz" if not already set.
     * Also, if the outputFilePath ends in ".gz", gzipCompress is automatically set to true.
     *
     * @throws ConverterException
     */
    public void writeXml() throws ConverterException, InvalidFormatException {

        PrintWriter out = null;
        try {

            //create xml file
            File xmlFile = new File(outputFilePath);

            if (gzipCompress) {
                FileOutputStream fos = new FileOutputStream(xmlFile);
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fos);
                OutputStreamWriter outWriter = new OutputStreamWriter(gzipOutputStream);
                out = new PrintWriter(outWriter);
            } else {
                out = new PrintWriter(new FileWriter(xmlFile));
            }

            logger.warn("Writing file : " + xmlFile.getAbsolutePath());

            //write header
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

            //experiment
            out.println("<ExperimentCollection version=\"2.1\">");
            out.println("<Experiment>");
            if (meta.getExperimentAccession() != null && !"".equals(meta.getExperimentAccession())) {
                out.println("<ExperimentAccession>" + meta.getExperimentAccession() + "</ExperimentAccession>");
            }
            out.println("<Title>" + meta.getTitle() + "</Title>");

            marshallReportObject(out, meta.getReference());
            out.println("<ShortLabel>" + meta.getShortLabel() + "</ShortLabel>");

            //protocol
            marshallReportObject(out, meta.getProtocol());
            out.println();

            //mzdata
            out.println("<mzData version=\"1.05\" accessionNumber=\"" + meta.getExperimentAccession() + "\">");
            marshallReportObject(out, meta.getMzDataDescription().getCvLookup());
            out.println();
            out.println("<description>");
            marshallReportObject(out, meta.getMzDataDescription().getAdmin());
            out.println();
            marshallReportObject(out, meta.getMzDataDescription().getInstrument());
            out.println();
            marshallReportObject(out, meta.getMzDataDescription().getDataProcessing());
            out.println();
            out.println("</description>");

            //spectrumlist - note dependency on includeAllSpectra
            out.println("<spectrumList count=\"" + dao.getSpectrumCount(includeOnlyIdentifiedSpectra) + "\">");
            Iterator<Spectrum> spectrumIter = dao.getSpectrumIterator(includeOnlyIdentifiedSpectra);
            int spectrumCount = 0;
            while (spectrumIter.hasNext()) {
                Spectrum spectrum = spectrumIter.next();
                //if the spectrum has no intensity array, set one properly
                if (spectrum.getIntenArrayBinary() == null || spectrum.getIntentArray() == null) {
                    spectrum.setIntenArrayBinary(EMPTY_INTEN_ARRAY);
                }
                //if the spectrum has no mz array, set one properly
                if (spectrum.getMzArrayBinary() == null || spectrum.getMzNumberArray() == null) {
                    spectrum.setMzArrayBinary(EMPTY_MZ_ARRAY);
                }
                prideJaxbMarshaller.marshall(spectrum, out);
                out.println();
                spectrumCount++;
                if (spectrumCount % 1000 == 0) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Marshalled " + spectrumCount + " spectra");
                        logger.info(MemoryUsage.getMessage());
                    }
                }
            }
            //close spectrumlist
            out.println("</spectrumList>");

            //close mzdata
            out.println("</mzData>");

            //identifications - we iterate from the reader to ensure that we capture any changes made
            //                  by external tools (e.g. protein inference)
            Iterator<uk.ac.ebi.pride.tools.converter.report.model.Identification> idIter = reader.getIdentificationIterator();
            int identCount = 0;
            while (idIter.hasNext()) {

                //since we can't assume that the dao and the report file will always return identifications in the same
                //order, the dao provides helper methods to get identifications by UUID.
                uk.ac.ebi.pride.tools.converter.report.model.Identification readerId = idIter.next();
                uk.ac.ebi.pride.tools.converter.report.model.Identification daoId = dao.getIdentificationByUID(readerId.getUniqueIdentifier());
                if (daoId == null) {
                    throw new ConversionException("Report file contains identifications not returned by DAO! Offending ID: " + readerId.getUniqueIdentifier());
                }

                uk.ac.ebi.pride.jaxb.model.Identification id = finalizeIdentification(daoId, readerId);
                if (id != null) {
                    identCount++;
                    prideJaxbMarshaller.marshall(id, out);
                    out.println();
                    if (identCount % 1000 == 0) {
                        //log memory usage
                        if (logger.isInfoEnabled()) {
                            logger.info("Marshalled " + identCount + " identifications");
                            logger.info(MemoryUsage.getMessage());
                        }
                    }
                }
            }

            //close experiment
            marshallReportObject(out, meta.getExperimentAdditional());
            out.println("</Experiment>");

            //close experimentcollection
            out.println("</ExperimentCollection>");

        } catch (IOException e) {

            logger.error("Error writing outputfile: " + e.getMessage(), e);
            throw new ConverterException("Error writing output file: " + e.getMessage(), e);

        } finally {
            if (out != null) {
                out.close();
            }
        }

    }

    /**
     * Internal method that will take the corresponding DAO identification and report identification
     * and merge fields. Returns a fully-constructed PRIDE JAXB identification object.
     *
     * @param daoId    - the identification as returned from the DAO
     * @param readerId - the identification as returned from the report
     * @return
     */
    private uk.ac.ebi.pride.jaxb.model.Identification finalizeIdentification(
            uk.ac.ebi.pride.tools.converter.report.model.Identification daoId,
            uk.ac.ebi.pride.tools.converter.report.model.Identification readerId) throws InvalidFormatException {

        uk.ac.ebi.pride.jaxb.model.Identification id = null;

        //create identification
        if (readerId.getGelBasedData() == null || readerId.getGelBasedData().getGel() == null) {
            id = new GelFreeIdentification();
        } else {
            //if we have properly formatted gel info, create gel-based identification
            id = new TwoDimensionalIdentification();

            //fill in gel-specific data elements
            GelBasedData gData = readerId.getGelBasedData();

            if (gData.getGelLocation() != null && gData.getGelLocation().isValid()) {
                GelLocation loc = new GelLocation();
                loc.setXCoordinate(gData.getGelLocation().getXCoordinate());
                loc.setYCoordinate(gData.getGelLocation().getYCoordinate());
                ((TwoDimensionalIdentification) id).setGelLocation(loc);
            }

            if (gData.getGel() != null) {
                SimpleGel gel = new SimpleGel();
                gel.setGelLink(gData.getGel().getGelLink());
                gel.setAdditional(convertParams(gData.getGel().getAdditional()));
                ((TwoDimensionalIdentification) id).setGel(gel);
            }

            ((TwoDimensionalIdentification) id).setMolecularWeight(gData.getMolecularWeight());
            ((TwoDimensionalIdentification) id).setPI(gData.getPI());

        }

        //if the curated accession is not null, return that. otherwise, return the default - submitted - accession
        id.setAccession((readerId.getCuratedAccession() != null && readerId.getCuratedAccession().length() > 0) ? readerId.getCuratedAccession() : readerId.getAccession());
        id.setAccessionVersion(readerId.getAccessionVersion());
        id.setDatabase(mapSearchDatabaseName(readerId.getDatabase(), readerId.getDatabaseVersion()));
        id.setDatabaseVersion(mapSearchDatabaseVersion(readerId.getDatabase(), readerId.getDatabaseVersion()));
        id.setScore(readerId.getScore());
        id.setSearchEngine(readerId.getSearchEngine());
        id.setSequenceCoverage(readerId.getSequenceCoverage());
        id.setSpliceIsoform(readerId.getSpliceIsoform());
        id.setThreshold(readerId.getThreshold());

        //get params
        id.setAdditional(convertParams(readerId.getAdditional()));

        //if we used the curated accession, set the original accession as a param
        if (readerId.getCuratedAccession() != null && readerId.getCuratedAccession().length() > 0) {
            CvParam cv = new CvParam();
            cv.setCvLabel(DAOCvParams.SUBMITTED_PROTEIN_ACCESSION.getCv());
            cv.setAccession(DAOCvParams.SUBMITTED_PROTEIN_ACCESSION.getAccession());
            cv.setName(DAOCvParams.SUBMITTED_PROTEIN_ACCESSION.getName());
            cv.setValue(readerId.getAccession());
            id.getAdditional().getCvParam().add(cv);
        }

        //if we have a fasta sequence, create PRIDE param for identification
        Long seqId = readerId.getFastaSequenceReference();
        if (seqId != null) {
            Sequence seq = reader.getSequenceById(seqId);
            if (seq != null) {
                CvParam cv = new CvParam();
                cv.setCvLabel(DAOCvParams.SEARCH_DATABASE_PROTEIN_SEQUENCE.getCv());
                cv.setAccession(DAOCvParams.SEARCH_DATABASE_PROTEIN_SEQUENCE.getAccession());
                cv.setName(DAOCvParams.SEARCH_DATABASE_PROTEIN_SEQUENCE.getName());
                cv.setValue(seq.getValue());
                id.getAdditional().getCvParam().add(cv);
            }
        }

        //create peptides - use peptides from report-based identification in case some peptides have been removed
        //                  during protein inference curation
        for (Peptide p : readerId.getPeptide()) {
            PeptideItem newPep = new PeptideItem();

            newPep.setAdditional(convertParams(p.getAdditional()));
            newPep.setStart(new BigInteger("" + p.getStart()));
            newPep.setEnd(new BigInteger("" + p.getEnd()));
            //if we have a curated sequence,
            newPep.setSequence((p.getCuratedSequence() != null && p.getCuratedSequence().length() > 0) ? p.getCuratedSequence() : p.getSequence());

            //assign spectrum ID for each peptide
            int sRef = dao.getSpectrumReferenceForPeptideUID(p.getUniqueIdentifier());
            if (sRef > -1) {
                //creating an empty spectrum and only setting the ref will allow the jaxb marshaller to set the
                //id/idref properly
                Spectrum s = new Spectrum();
                s.setId(sRef);
                newPep.setSpectrum(s);
            }
            //create fragment ions
            Peptide daoPep = daoId.getPeptide(p.getUniqueIdentifier());
            if (daoPep != null) {
                newPep.getFragmentIon().addAll(convertFragmentIons(daoPep.getFragmentIon()));
            } else {
                throw new ConverterException("Report object contains peptide not returned by DAO: " + p.getUniqueIdentifier());
            }

            //create modifications
            newPep.getModificationItem().addAll(convertModifications(p.getPTM()));
            id.getPeptideItem().add(newPep);

        }

        return id;

    }

    private String mapSearchDatabaseName(String database, String databaseVersion) {
        DatabaseMapping dm = getDatabaseMapping(database, databaseVersion);
        //database mapping will never be null - it will have thrown an ConverterException if not found
        if (dm.getCuratedDatabaseName() != null) {
            return dm.getCuratedDatabaseName();
        } else {
            return dm.getSearchEngineDatabaseName();
        }
    }

    private String mapSearchDatabaseVersion(String database, String databaseVersion) {
        DatabaseMapping dm = getDatabaseMapping(database, databaseVersion);
        //database mapping will never be null - it will have thrown an ConverterException if not found
        if (dm.getCuratedDatabaseVersion() != null) {
            return dm.getCuratedDatabaseVersion();
        } else {
            return dm.getSearchEngineDatabaseVersion();
        }
    }

    private DatabaseMapping getDatabaseMapping(String database, String databaseVersion) {

        initDatabaseMappings();
        DatabaseMapping retval = databaseMappingCache.get(database + databaseVersion);
        if (retval == null) {
            StringBuilder sb = new StringBuilder("Could not find valid database mapping for database >")
                    .append(database)
                    .append("< with version >")
                    .append(databaseVersion)
                    .append("<");
            throw new ConverterException(sb.toString());
        }
        return retval;

    }

    private void initDatabaseMappings() {

        if (databaseMappingCache == null) {

            databaseMappingCache = new HashMap<String, DatabaseMapping>();
            Iterator<DatabaseMapping> it = reader.getDatabaseMappingIterator();
            while (it.hasNext()) {
                DatabaseMapping dm = it.next();
                databaseMappingCache.put(dm.getSearchEngineDatabaseName() + dm.getSearchEngineDatabaseVersion(), dm);
            }

        }

    }


    /**
     * Converts report-model PTMs to JAXB PTMs. Uses cached information obtained from the PTM section of the
     * report file to set Accession/DB/DB-Version for the PTMs. The PTM location and mass deltas are taken from
     * the DAO PTMs.
     *
     * @param ptmList
     * @return
     */
    private Collection<ModificationItem> convertModifications(List<PeptidePTM> ptmList) {

        //initialize PTMs from reader
        initPTMCache();

        List<ModificationItem> newModList = new ArrayList<ModificationItem>();
        if (ptmList != null) {
            for (PeptidePTM ptm : ptmList) {

                //get PTM info from cache
                PTM reportPTM = ptmCache.get(ptm.getSearchEnginePTMLabel());
                if (reportPTM == null) {
                    throw new ConverterException("DAO returned peptide PTM not described in report file: " + ptm.getSearchEnginePTMLabel());
                }

                ModificationItem mod = new ModificationItem();
                //store curated information from report
                mod.setModAccession(reportPTM.getModAccession());
                mod.setModDatabase(reportPTM.getModDatabase());
                mod.setModDatabaseVersion(reportPTM.getModDatabaseVersion());

                //location comes from actual modification
                mod.setModLocation(new BigInteger("" + ptm.getModLocation()));

                double ptmDelta = Double.NaN;
                double reportPtmDelta = Double.NaN;

                //mod deltas come from actual DAO ptm
                if (ptm.getModAvgDelta() != null && !ptm.getModAvgDelta().isEmpty()) {
                    mod.getModAvgDelta().addAll(ptm.getModAvgDelta());
                    ptmDelta = Double.valueOf(ptm.getModAvgDelta().get(0));
                }
                if (ptm.getModMonoDelta() != null && !ptm.getModMonoDelta().isEmpty()) {
                    mod.getModMonoDelta().addAll(ptm.getModMonoDelta());
                    ptmDelta = Double.valueOf(ptm.getModMonoDelta().get(0));
                }

                //validate deltas coming from report file
                if (reportPTM.getModAvgDelta() != null && !reportPTM.getModAvgDelta().isEmpty()) {
                    reportPtmDelta = Double.valueOf(reportPTM.getModAvgDelta().get(0));
                }
                if (reportPTM.getModMonoDelta() != null && !reportPTM.getModMonoDelta().isEmpty()) {
                    reportPtmDelta = Double.valueOf(reportPTM.getModMonoDelta().get(0));
                }
                if (reportPtmDelta == Double.NaN) {
                    throw new ConverterException("No Mass Delta Annotated for report PTM: " + reportPTM);
                }

                double diff = ptmDelta - reportPtmDelta;
                if (diff < 0) {
                    diff = diff * -1;
                }
                if (diff > MASS_DELTA_CUTOFF_VALUE) {
                    throw new ConverterException("Mass delta of identified PTM doesn't match with mass delta from report file for PTM: " + reportPTM);
                }

                // make sure the mod contains additional params
                if (reportPTM.getAdditional() == null)
                    throw new ConverterException("Missing additional parameters for modification " + reportPTM.getSearchEnginePTMLabel());

                //store additional params from report PTM
                mod.setAdditional(convertParams(reportPTM.getAdditional()));
                //check to see if there are conflicting params from the dao ptm
                if (ptm.getAdditional() != null) {
                    mod.setAdditional(mergeParams(mod.getAdditional(), ptm.getAdditional()));
                }

                newModList.add(mod);

            }
        }
        return newModList;

    }

    /**
     * helper method to load the PTM information from the report file.
     */
    private void initPTMCache() {

        if (ptmCache == null) {
            ptmCache = new HashMap<String, PTM>();
            Iterator<PTM> ptms = reader.getPTMIterator();
            while (ptms.hasNext()) {
                PTM ptm = ptms.next();
                ptmCache.put(ptm.getSearchEnginePTMLabel(), ptm);
            }
        }

    }

    /**
     * convert report model fragment ions to JAXB fragment ions.
     *
     * @param ionList
     * @return
     */
    private Collection<uk.ac.ebi.pride.jaxb.model.FragmentIon> convertFragmentIons(
            List<uk.ac.ebi.pride.tools.converter.report.model.FragmentIon> ionList) {

        List<uk.ac.ebi.pride.jaxb.model.FragmentIon> newList =
                new ArrayList<uk.ac.ebi.pride.jaxb.model.FragmentIon>();

        if (ionList != null) {

            //iterate over the fragment ions and convert them to jaxb
            for (uk.ac.ebi.pride.tools.converter.report.model.FragmentIon oldIon : ionList) {

                uk.ac.ebi.pride.jaxb.model.FragmentIon newIon = new uk.ac.ebi.pride.jaxb.model.FragmentIon();
                //fragment ion extends param, so use helper method
                Param p = convertParams(oldIon);
                //fill in collections in proper object
                newIon.getCvParam().addAll(p.getCvParam());
                newIon.getUserParam().addAll(p.getUserParam());
                //add to list
                newList.add(newIon);

            }

        }

        return newList;

    }

    /**
     * convert report-model Params to JAXB-model params.
     *
     * @param param
     * @return
     */
    private Param convertParams(uk.ac.ebi.pride.tools.converter.report.model.Param param) {
        Param p = new Param();
        if (param.getCvParam() != null) {
            for (uk.ac.ebi.pride.tools.converter.report.model.CvParam old : param.getCvParam()) {
                CvParam cv = new CvParam();
                cv.setCvLabel(old.getCvLabel());
                cv.setAccession(old.getAccession());
                cv.setName(old.getName());
                cv.setValue(old.getValue());
                p.getCvParam().add(cv);
            }
        }
        if (param.getUserParam() != null) {
            for (uk.ac.ebi.pride.tools.converter.report.model.UserParam old : param.getUserParam()) {
                UserParam user = new UserParam();
                user.setName(old.getName());
                user.setValue(old.getValue());
                p.getUserParam().add(user);
            }
        }
        return p;
    }

    /**
     * helper method that merges params from an existing JAXB-model param object and a report-model param object.
     * Values from the report-model object will override the JAXB-model values.
     *
     * @param originalParam
     * @param overrideParam
     * @return
     */
    private Param mergeParams(Param originalParam, uk.ac.ebi.pride.tools.converter.report.model.Param overrideParam) {

        //check original param and keep track of current accessions
        List<String> currentAccessions = new ArrayList<String>();
        for (CvParam cv : originalParam.getCvParam()) {
            currentAccessions.add(cv.getCvLabel() + cv.getAccession());
        }
        //also store original user params
        for (UserParam user : originalParam.getUserParam()) {
            currentAccessions.add(user.getName());
        }

        //iterate over overrideParams. If there is a conflict, delete from originalParam and replace with new
        //param created from override values
        for (uk.ac.ebi.pride.tools.converter.report.model.CvParam cv : overrideParam.getCvParam()) {
            if (currentAccessions.contains(cv.getCvLabel() + cv.getAccession())) {
                deleteCvParam(originalParam, cv.getCvLabel(), cv.getAccession());

                //create replacement cv param
                CvParam newCV = new CvParam();
                newCV.setCvLabel(cv.getCvLabel());
                newCV.setAccession(cv.getAccession());
                newCV.setName(cv.getName());
                newCV.setValue(cv.getValue());
                //store it
                originalParam.getCvParam().add(newCV);
            }
        }

        //iterate over overrideParams. If there is a conflict, delete from originalParam and replace with new
        //param created from override values
        for (uk.ac.ebi.pride.tools.converter.report.model.UserParam user : overrideParam.getUserParam()) {
            if (currentAccessions.contains(user.getName())) {
                deleteUserParam(originalParam, user.getName());

                //create replacement user param
                UserParam newUP = new UserParam();
                newUP.setName(user.getName());
                newUP.setValue(user.getValue());
                //store it
                originalParam.getUserParam().add(newUP);
            }
        }

        return originalParam;

    }

    /**
     * helper method to delete a CvParam from a Param object given a cv label and accession
     *
     * @param param
     * @param cvLabel
     * @param accession
     */
    private void deleteCvParam(Param param, String cvLabel, String accession) {

        Iterator<CvParam> it = param.getCvParam().iterator();
        while (it.hasNext()) {
            CvParam cv = it.next();
            if (cv.getCvLabel().equals(cvLabel) && cv.getAccession().equals(accession)) {
                it.remove();
            }
        }

    }

    /**
     * helper method to delete a UserParam from a Param object given a param name
     *
     * @param param
     * @param name
     */
    private void deleteUserParam(Param param, String name) {

        Iterator<UserParam> it = param.getUserParam().iterator();
        while (it.hasNext()) {
            UserParam user = it.next();
            if (user.getName().equals(name)) {
                it.remove();
            }
        }

    }


    /**
     * helper method to marshall a collection of report-model object
     *
     * @param out
     * @param xmlObject
     * @param <T>
     * @throws IOException
     */
    private <T extends ReportObject> void marshallReportObject(PrintWriter out, List<T> xmlObject) throws IOException {
        if (xmlObject == null) {
            throw new ConverterException("Attempting to marshall out null list");
        }

        //iterate and marshall individual objects
        for (T obj : xmlObject) {
            if (obj == null) {
                continue;
            }
            marshallReportObject(out, obj);
            out.println();
        }

    }

    /**
     * helper method to marshall out a single report-model object
     *
     * @param out
     * @param xmlObject
     * @param <T>
     * @throws IOException
     */
    private <T extends ReportObject> void marshallReportObject(PrintWriter out, T xmlObject) throws IOException {

        if (xmlObject == null) {
            throw new ConverterException("Attempting to marshall out null object");
        }

        try {
            reportJaxbMarshaller.marshall(xmlObject, out);
        } catch (Exception e) {
            throw new IOException("Error marshalling: " + xmlObject.getClass().getName(), e);
        }

    }

    /**
     * helper method to marshall out a collection of PRIDE JAXB model objects
     *
     * @param out
     * @param xmlObject
     * @param <T>
     * @throws IOException
     */
    private <T extends PrideXmlObject> void marshallPrideObject(PrintWriter out, List<T> xmlObject) throws IOException {
        if (xmlObject == null) {
            throw new ConverterException("Attempting to marshall out null list");
        }

        //iterate and marshall individual objects
        for (T obj : xmlObject) {
            marshallPrideObject(out, obj);
            out.println();
        }

    }

    /**
     * helper method to marshall out a single PRIDE JAXB model objects
     *
     * @param out
     * @param xmlObject
     * @param <T>
     * @throws IOException
     */
    private <T extends PrideXmlObject> void marshallPrideObject(PrintWriter out, T xmlObject) throws IOException {

        if (xmlObject == null) {
            throw new ConverterException("Attempting to marshall out null object");
        }

        try {
            prideJaxbMarshaller.marshall(xmlObject, out);
        } catch (Exception e) {
            throw new IOException("Error marshalling: " + xmlObject.getClass().getName(), e);
        }

    }

    public static void main(String[] args) {

        try {
            DAO mascot = DAOFactory.getInstance().getDAO("/home/rcote/dev/cvs/pride-converter/src/test/resources/F001240.dat", DAOFactory.DAO_FORMAT.MASCOT);
            ReportReader reader = new ReportReader(new File("/home/rcote/dev/cvs/pride-converter/src/test/resources/F001240.dat-report.xml"));
            PrideXmlWriter out = new PrideXmlWriter("/home/rcote/dev/cvs/pride-converter/out.xml.gz", reader, mascot, false);
            out.writeXml();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
    }


}
