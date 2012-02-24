package uk.ac.ebi.pride.tools.converter.report.io;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.gui.model.ReportBean;
import uk.ac.ebi.pride.tools.converter.gui.util.IOUtilities;
import uk.ac.ebi.pride.tools.converter.report.model.*;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: rcote
 * Date: 20/06/11
 * Time: 16:09
 * To change this template use File | Settings | File Templates.
 */
public class ReportMetadataCopier {

    private static final Logger logger = Logger.getLogger(ReportMetadataCopier.class);

    public static void copyMetadata(String masterFileName, List<String> reportFileNames) throws ConverterException, InvalidFormatException {

        File masterFile = new File(masterFileName);
        if (masterFile.exists()) {

            logger.warn("Reading metada from master file:" + masterFile.getAbsolutePath());
            ReportReader masterReader = new ReportReader(masterFile);

            Metadata masterMetadata = masterReader.getMetadata();

            List<PTM> masterPTMs = new ArrayList<PTM>();

            Iterator<PTM> iPTM = masterReader.getPTMIterator();
            while (iPTM.hasNext()) {
                masterPTMs.add(iPTM.next());
            }

            List<DatabaseMapping> masterDatabaseMappings = new ArrayList<DatabaseMapping>();
            Iterator<DatabaseMapping> iDBM = masterReader.getDatabaseMappingIterator();
            while (iDBM.hasNext()) {
                masterDatabaseMappings.add(iDBM.next());
            }

            // from here we don't need to read from the masterReportFile anymore so we release the handles
            masterReader = null;
            masterFile = null;

            for (String reportFileName : reportFileNames) {
                File reportFile = new File(reportFileName);
                String outFileName;
                if (reportFile.exists()) {

                    //copySingleMetadata will override certain param collections
                    //To avoid stale params from being written to the wrong file, keep track of the original
                    //values here and reset them before the next loop in the iteration
                    Description sample = new Description();
                    sample.setComment(masterMetadata.getMzDataDescription().getAdmin().getSampleDescription().getComment());
                    sample.getCvParam().addAll(masterMetadata.getMzDataDescription().getAdmin().getSampleDescription().getCvParam());
                    sample.getUserParam().addAll(masterMetadata.getMzDataDescription().getAdmin().getSampleDescription().getUserParam());
                    String sampleName = masterMetadata.getMzDataDescription().getAdmin().getSampleName();
                    Param expAdditional = new Param();
                    expAdditional.getCvParam().addAll(masterMetadata.getExperimentAdditional().getCvParam());
                    expAdditional.getUserParam().addAll(masterMetadata.getExperimentAdditional().getUserParam());
                    Param processingMethod = new Param();
                    processingMethod.getCvParam().addAll(masterMetadata.getMzDataDescription().getDataProcessing().getProcessingMethod().getCvParam());
                    processingMethod.getUserParam().addAll(masterMetadata.getMzDataDescription().getDataProcessing().getProcessingMethod().getUserParam());

                    //write file
                    outFileName = copySingleMetadata(masterMetadata, masterPTMs, masterDatabaseMappings, reportFile);

                    //reset params
                    masterMetadata.getMzDataDescription().getAdmin().setSampleName(sampleName);
                    masterMetadata.getMzDataDescription().getAdmin().setSampleDescription(sample);
                    masterMetadata.setExperimentAdditional(expAdditional);
                    masterMetadata.getMzDataDescription().getDataProcessing().setProcessingMethod(processingMethod);

                } else {
                    throw new ConverterException("Report file does not exist: " + reportFile);
                }
                if (!outFileName.equals(reportFile.getAbsolutePath())) {
                    //we need to overwrite the old master report file
                    if (!IOUtilities.renameFile(outFileName, reportFile.getAbsolutePath())) {
                        throw new ConverterException("Could not overwrite report file: " + reportFile.getAbsolutePath());
                    }
                }
            }

        } else {
            throw new ConverterException("Invalid master report file: " + masterFile);
        }

    }

    private static String copySingleMetadata(Metadata masterMetadata, List<PTM> masterPTMs, List<DatabaseMapping> masterDatabaseMappings, File reportFile) throws InvalidFormatException {
        logger.info("Re-writing report file with master report file meta-data: " + reportFile.getAbsolutePath());
        //create writer
        ReportWriter writer = new ReportWriter(reportFile.getAbsolutePath());
        //create dao
        ReportReaderDAO dao = new ReportReaderDAO(reportFile);
        writer.setDAO(dao);
        //don't remap PTMs from the master
        writer.setAutomaticallyMapPreferredPTMs(false);

        //neet to update short label / experiment title
        ReportBean rb = ConverterData.getInstance().getCustomeReportFields().get(dao.getSourceFile().getPathToFile());
        if (rb != null) {
            masterMetadata.setShortLabel(rb.getShortLabel());
            masterMetadata.setTitle(rb.getExperimentTitle());
            if (rb.getSampleDescription() != null) {
                Description sample = rb.getSampleDescription();
                //merge custom params with master params
                sample = mergeSampleParams(sample, masterMetadata.getMzDataDescription().getAdmin().getSampleDescription(), dao.getSampleParams());
                //set params to be marshalled out
                masterMetadata.getMzDataDescription().getAdmin().setSampleDescription(sample);
                masterMetadata.getMzDataDescription().getAdmin().setSampleName(rb.getSampleName());
            }
        }

        //need to update experiment additional params
        masterMetadata.setExperimentAdditional(mergeParams(masterMetadata.getExperimentAdditional(), dao.getExperimentParams()));

        //need to update the metadata so that the source file element of the mzdata admin reflect the correct file
        masterMetadata.getMzDataDescription().getAdmin().setSourceFile(dao.getSourceFile());

        //need to update the processing method so that the information returned from the original dao is correct
        masterMetadata.getMzDataDescription().getDataProcessing().setProcessingMethod(mergeParams(masterMetadata.getMzDataDescription().getDataProcessing().getProcessingMethod(), dao.getProcessingMethod()));

        //write report
        return writer.writeReport(masterMetadata, masterPTMs, masterDatabaseMappings);
    }

    /**
     * merges two sets of params. Iterates over all of the params from the masterParam object (obtained from the
     * masterDAO) and compares them to the daoParam object (obtained from the file DAO). If the daoParam
     * does not contain a param from the master, it will be added. If it does already contain a param,
     * that param will not be overwritten.
     *
     * @param masterParams
     * @param daoParams
     * @return
     */
    private static Param mergeParams(Param masterParams, Param daoParams) {

        for (CvParam cv : masterParams.getCvParam()) {
            if (!containsAccession(daoParams.getCvParam(), cv.getAccession())) {
                daoParams.getCvParam().add(cv);
            }
        }
        for (UserParam up : masterParams.getUserParam()) {
            if (!containsName(daoParams.getUserParam(), up.getName())) {
                daoParams.getUserParam().add(up);
            }
        }
        return daoParams;
    }

    /**
     * merges three sets of params. Iterates over all of the params from the masterParam object (obtained from the
     * masterDAO) and compares them to the customDescription object (created manually). If the customDescription
     * does not contain a param from the master, it will be added. If it does already contain a param,
     * that param will not be overwritten. Does the same with params coming from the DAO
     *
     * @param masterParams
     * @param daoParams
     * @return
     */
    private static Description mergeSampleParams(Description customDescription, Param masterParams, Param daoParams) {

        //check to see if we have a newt annotation. if we do, don't copy any annotation from the master/dao.
        boolean hasNEWT = false;
        for (CvParam cv : customDescription.getCvParam()) {
            if (cv.getCvLabel().equalsIgnoreCase("NEWT")) {
                hasNEWT = true;
                break;
            }
        }

        //copy dao params to description
        for (CvParam cv : daoParams.getCvParam()) {
            //add params if:
            //label is not NEWT and accession not already present
            // OR
            // label is NEWT and not already contains NEWT accession
            //
            if (cv.getCvLabel().equalsIgnoreCase("NEWT")) {
                if (!hasNEWT) {
                    customDescription.getCvParam().add(cv);
                }
            } else {
                if (!containsAccession(customDescription.getCvParam(), cv.getAccession())) {
                    customDescription.getCvParam().add(cv);
                }
            }
        }

        //copy master params to description
        for (CvParam cv : masterParams.getCvParam()) {
            //add params if:
            //label is not NEWT and accession not already present
            // OR
            // label is NEWT and not already contains NEWT accession
            //
            if (cv.getCvLabel().equalsIgnoreCase("NEWT")) {
                if (!hasNEWT) {
                    customDescription.getCvParam().add(cv);
                }
            } else {
                if (!containsAccession(customDescription.getCvParam(), cv.getAccession())) {
                    customDescription.getCvParam().add(cv);
                }
            }
        }

        for (UserParam up : daoParams.getUserParam()) {
            if (!containsName(customDescription.getUserParam(), up.getName())) {
                customDescription.getUserParam().add(up);
            }
        }
        for (UserParam up : masterParams.getUserParam()) {
            if (!containsName(customDescription.getUserParam(), up.getName())) {
                customDescription.getUserParam().add(up);
            }
        }
        return customDescription;
    }

    private static boolean containsAccession(List<CvParam> cvParam, String accession) {
        for (CvParam cv : cvParam) {
            if (cv.getAccession().equals(accession)) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsName(List<UserParam> userParams, String name) {
        for (UserParam up : userParams) {
            if (up.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

}
