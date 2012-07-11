package uk.ac.ebi.pride.tools.converter.report.io;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.dao.DAO;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.dao.handler.ExternalHandler;
import uk.ac.ebi.pride.tools.converter.dao.handler.FastaHandler;
import uk.ac.ebi.pride.tools.converter.dao.handler.QuantitationCvParams;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.gui.model.DecoratedReportObject;
import uk.ac.ebi.pride.tools.converter.report.io.xml.marshaller.ReportMarshaller;
import uk.ac.ebi.pride.tools.converter.report.io.xml.marshaller.ReportMarshallerFactory;
import uk.ac.ebi.pride.tools.converter.report.model.*;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;
import uk.ac.ebi.pride.tools.converter.utils.ModUtils;
import uk.ac.ebi.pride.tools.converter.utils.config.Configurator;
import uk.ac.ebi.pride.tools.converter.utils.memory.MemoryUsage;
import uk.ac.ebi.pride.tools.utils.AccessionResolver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 15-Dec-2010
 * Time: 12:27:37
 */
public class ReportWriter {

    private static final Logger logger = Logger.getLogger(ReportWriter.class);

    private DAO dao;
    private String filePath;
    private ReportMarshaller marshaller;
    private FastaHandler fastaHandler = null;
    private ExternalHandler externalHandler = null;
    private boolean automaticallyMapPreferredPTMs = false;

    public ReportWriter(String filePath) {
        this.filePath = filePath;
        marshaller = ReportMarshallerFactory.getInstance().initializeMarshaller();
    }

    public void setDAO(DAO dao) {
        this.dao = dao;
        // this is a bit of an ugly hack, but to ensure that the Fasta sequences
        // are propagated during annotation when using the ReportReaderDAO, that
        // class implements the FastaHandler interface and uses the data that is
        // in the existing report file. The hack is here to remove the risk that
        // API users will forget to manually set the FastaHandler and therefore
        // cause data loss
        if (dao != null && this.dao instanceof ReportReaderDAO) {
            setFastaHandler((ReportReaderDAO) dao);
        }
    }

    public void setFastaHandler(FastaHandler fastaHandler) {
        this.fastaHandler = fastaHandler;
    }

    public void setExternalHandler(ExternalHandler handler) {
        this.externalHandler = handler;
    }

    public String writeReport() throws ConverterException, InvalidFormatException {
        Metadata meta = createMedatada();
        Collection<PTM> ptms = dao.getPTMs();

        //update PTM mappings to try and automatically assign curated PTM annotations by mass delta
        if (automaticallyMapPreferredPTMs) {
            ptms = ModUtils.mapPreferredModifications(ptms);
        }

        Collection<DatabaseMapping> databaseMappings = dao.getDatabaseMappings();
        return writeReport(meta, ptms, databaseMappings);
    }

    /**
     * PACKAGE LEVEL METHOD - this is made available to the ReportMetadataCopier class
     * If the method tries to write to a file that already exists, it will rename the output file
     * and return the full path of the file that was created.
     *
     * @param meta
     * @throws ConverterException
     */
    String writeReport(Metadata meta, Collection<PTM> ptms, Collection<DatabaseMapping> databaseMappings) throws ConverterException, InvalidFormatException {

        if (meta == null) {
            throw new ConverterException("Metadata object to marshall cannot be null");
        }

        PrintWriter out = null;
        try {

            //check to see if the DAO is a ReportReaderDAO and make sure
            //that we're not trying to overwrite a file we're reading
            File outputFile = new File(filePath);
            if (dao instanceof ReportReaderDAO) {
                if (outputFile.getAbsolutePath().equals(((ReportReaderDAO) dao).getReportFileAbsolutePath())) {
                    logger.info("Overwriting report file detected. Will write to temporary file.");
                    //write tmp output file and rename afterwards
                    outputFile = new File(filePath + ".work");
                }
            }

            logger.warn("Writing report file: " + outputFile.getAbsolutePath());

            out = new PrintWriter(new FileWriter(outputFile));

            //write xml header
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

            //write start tag
            out.println("<Report>");

            //write searchResultIdentifier
            marshall(out, dao.getSearchResultIdentifier());

            //update sample params in case they contain subsample information
            Description sample = meta.getMzDataDescription().getAdmin().getSampleDescription();
            meta.getMzDataDescription().getAdmin().setSampleDescription(updateQuantParams(sample));

            //write metadata
            marshall(out, meta);

            //write identifications
            out.println("<Identifications>");
            Iterator<Identification> iter = dao.getIdentificationIterator(true);
            int identCount = 0;
            while (iter.hasNext()) {
                Identification id = iter.next();
                if (id == null) {
                    //this can happen in some cases, where the dao returns
                    //a null identification due to various configuration
                    //settings. The DAOs might not prescan the identifications
                    //and reach a state the the current identification to be returned
                    //by the iterator has no valid peptides. In this case, the DAO
                    //will return null and the report writer needs to handle this
                    //gracefully.
                    continue;
                }

                //try and fix weirdo accessions
                AccessionResolver acr = new AccessionResolver(id.getAccession(), id.getAccessionVersion(), id.getDatabase(), ConverterData.getInstance().isUseHybridSearchDatabase());
                //if we can parse a valid accession
                if (acr.isValidAccession()) {
                    //if the curated accession is different than the submitted accession, make note of it
                    if (!acr.getAccession().equals(id.getAccession())) {
                        id.setCuratedAccession(acr.getAccession());
                        id.setAccessionVersion(acr.getVersion());//todo - should we need a curationVersion?
                    }
                } else {
                    logger.warn("Found invalid submitted protein accession: " + id.getAccession());
                }

                //call fasta handler - this will update the sequence information for the identification object
                // if DAO is ReportReaderDAO, the DAO is also a FastaHandler - this is to ensure that the Fasta
                // sequences are propagated. The fastaHandler itself will not update the identification in any way.
                if (fastaHandler != null) {
                    id = fastaHandler.updateFastaSequenceInformation(id);
                }
                //call external handler - this will update quantitation params as well as gel data for the various peptides
                if (externalHandler != null) {
                    id = externalHandler.updateIdentification(id);
                }

                marshall(out, id);

                if (identCount % 1000 == 0) {
                    //log memory usage
                    if (logger.isInfoEnabled()) {
                        logger.info("Marshalled " + identCount + " identifications");
                        logger.info(MemoryUsage.getMessage());
                    }
                }

            }
            out.println("</Identifications>");

            //write Fasta
            out.print("<Fasta");
            out.print(" sourceDb=\"" + StringEscapeUtils.escapeXml(dao.getSearchDatabaseName()) + "\"");
            out.print(" sourceDbVersion=\"" + StringEscapeUtils.escapeXml(dao.getSearchDatabaseVersion()) + "\"");
            out.println(">");

            if (fastaHandler != null) {
                Iterator<Sequence> seqIter = fastaHandler.getIterator(true);
                while (seqIter.hasNext()) {
                    marshall(out, seqIter.next());
                }
            }
            out.println("</Fasta>");

            //write PTMs
            out.println("<PTMs>");
            marshall(out, ptms);
            out.println("</PTMs>");

            //write DatabaseMappings
            out.println("<DatabaseMappings>");
            marshall(out, databaseMappings);
            out.println("</DatabaseMappings>");

            //write configuration
            marshall(out, new ConfigurationOptions(dao.getConfiguration()));

            out.println("</Report>");

            out.close();

            //return the full path of the generated file
            return outputFile.getAbsolutePath();

        } catch (IOException e) {

            throw new ConverterException("Error during report file generation: " + e.getMessage(), e);

        } finally {
            if (out != null) {
                out.close();
            }
        }

    }

    private Metadata createMedatada() throws InvalidFormatException {

        Metadata meta = new Metadata();

        //no accession on first conversion
        meta.setExperimentAccession(null);

        //title and labels
        meta.setTitle(dao.getExperimentTitle());
        if (dao.getExperimentShortLabel() != null) {
            meta.setShortLabel(dao.getExperimentShortLabel());
        } else {
            meta.setShortLabel("");
        }

        //add software version info
        Param expParam = new Param();
        //to prevent cvParam/userParam duplication
        Set<CvParam> cvParams = new HashSet<CvParam>();
        Set<UserParam> userParams = new HashSet<UserParam>();
        cvParams.add(new CvParam(DAOCvParams.XML_GENERATION_SOFTWARE.getCv(), DAOCvParams.XML_GENERATION_SOFTWARE.getAccession(), DAOCvParams.XML_GENERATION_SOFTWARE.getName(), Configurator.getVersion()));
        //add additional experiment params, if any
        if (dao.getExperimentParams() != null) {
            cvParams.addAll(dao.getExperimentParams().getCvParam());
            userParams.addAll(dao.getExperimentParams().getUserParam());
        }
        // add additional params from the external handler
        if (externalHandler != null && externalHandler.getExperimentParams() != null) {
            //merge params
            cvParams.addAll(externalHandler.getExperimentParams().getCvParam());
            userParams.addAll(externalHandler.getExperimentParams().getUserParam());
        }
        expParam.getCvParam().addAll(cvParams);
        expParam.getUserParam().addAll(userParams);
        meta.setExperimentAdditional(expParam);

        //protocol
        if (dao.getProtocol() != null) {
            meta.setProtocol(dao.getProtocol());
        } else {
            //create holder
            meta.setProtocol(new Protocol());
        }

        //references
        if (dao.getReferences() != null) {
            meta.getReference().addAll(dao.getReferences());
        } else {
// design decision to *not* put empty reference
//   ref is optional element
//            //create holder
//            meta.getReference().add(new Reference());
        }

        //create mzdata description
        Metadata.MzDataDescription mzDesc = new Metadata.MzDataDescription();

        //cv
        if (dao.getCvLookup() != null) {
            mzDesc.getCvLookup().addAll(dao.getCvLookup());
        } else {
            //create holder
            mzDesc.getCvLookup().add(new CV());
        }

        //admin
        Admin admin = new Admin();
        Description sd = new Description();
        if (dao.getSampleComment() != null) {
            sd.setComment(dao.getSampleComment());
        } else {
            sd.setComment("");
        }
        sd.getCvParam().addAll(dao.getSampleParams().getCvParam());
        sd.getUserParam().addAll(dao.getSampleParams().getUserParam());

        //add sample quantitation params
        if (externalHandler != null && externalHandler.getSampleDescriptionParams() != null) {
            //merge params
            sd.getCvParam().addAll(externalHandler.getSampleDescriptionParams().getCvParam());
            sd.getUserParam().addAll(externalHandler.getSampleDescriptionParams().getUserParam());
        }

        admin.setSampleDescription(sd);
        if (dao.getSampleName() != null) {
            admin.setSampleName(dao.getSampleName());
        } else {
            admin.setSampleName("");
        }
        admin.setSourceFile(dao.getSourceFile());
        if (dao.getContacts() != null && !dao.getContacts().isEmpty()) {
            admin.getContact().addAll(dao.getContacts());
        } else {
            //holder object
            admin.getContact().add(new Contact());
        }
        mzDesc.setAdmin(admin);

        //data processing
        DataProcessing dp = new DataProcessing();
        if (dao.getProcessingMethod() != null) {
            dp.setProcessingMethod(dao.getProcessingMethod());
        } else {
            //holder object
            dp.setProcessingMethod(new Param());
        }
        dp.setSoftware(dao.getSoftware());
        mzDesc.setDataProcessing(dp);

        //instrument
        if (dao.getInstrument() != null) {
            mzDesc.setInstrument(dao.getInstrument());
        } else {
            //holder object
            mzDesc.setInstrument(new InstrumentDescription());
        }

        meta.setMzDataDescription(mzDesc);
        return meta;

    }

    private Description updateQuantParams(Description sample) {

        //ITAQ115, subsample1
        HashMap<String, String> subsamples = new HashMap<String, String>();
        for (CvParam cv : sample.getCvParam()) {
            if (QuantitationCvParams.isQuantificationReagent(cv.getAccession())) {
                subsamples.put(cv.getName(), cv.getValue());
            }
        }
        //if we have subsamples, check the other params otherwise just return
        if (!subsamples.isEmpty()) {
            for (CvParam cv : sample.getCvParam()) {
                if (!QuantitationCvParams.isAQuantificationParam(cv.getAccession())) {
                    //if the value of the param is ITRAQ or something of the sort, need to translate back to the
                    //subsample number
                    if (subsamples.keySet().contains(cv.getValue())) {
                        cv.setValue(subsamples.get(cv.getValue()));
                    }
                }
            }
        }

        return sample;

    }


    private void marshall(PrintWriter out, ReportObject xmlObject) throws IOException {

        if (xmlObject == null) {
            throw new ConverterException("Attempting to marshall out null object");
        }

        try {
            if (xmlObject instanceof DecoratedReportObject) {
                marshaller.marshall(((DecoratedReportObject) xmlObject).getInner(), out);
            } else {
                marshaller.marshall(xmlObject, out);
            }
            out.println();
        } catch (Exception e) {
            throw new IOException("Error marshalling: " + xmlObject.getClass().getName(), e);
        }

    }

    private void marshall(PrintWriter out, Collection<? extends ReportObject> xmlObjects) throws IOException {

        String cls = null;
        try {
            for (ReportObject object : xmlObjects) {
                cls = object.getClass().getName();
                marshall(out, object);
            }
        } catch (Exception e) {
            throw new IOException("Error marshalling: " + cls, e);
        }

    }

    public void setAutomaticallyMapPreferredPTMs(boolean automaticallyMapPreferredPTMs) {
        this.automaticallyMapPreferredPTMs = automaticallyMapPreferredPTMs;
    }
}
