package uk.ac.ebi.pride.tools.converter.report.io;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.gui.model.FileBean;
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

        String outFileName;
        File masterFile = new File(masterFileName);
        if (masterFile.exists()) {

            logger.warn("Reading metada from master file:" + masterFile.getAbsolutePath());
            ReportReader masterReader = new ReportReader(masterFile);

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

            for (String reportFileName : reportFileNames) {

                File reportFile = new File(reportFileName);
                if (reportFile.exists()) {

                    //copySingleMetadata will override certain param collections
                    //To avoid stale params from being written to the wrong file, always read the metadata fresh
                    Metadata masterMetadata = masterReader.getMetadata();

                    //neet to update short label / experiment title
                    //check to see if we have a custom bean and safely override the data!
                    FileBean fb = ConverterData.getInstance().getFileBeanByReportFileName(reportFile.getAbsolutePath());
                    ReportBean rb = ConverterData.getInstance().getCustomeReportFields().get(fb.getInputFile());
                    if (rb != null) {
                        masterMetadata.setShortLabel(rb.getShortLabel());
                        masterMetadata.setTitle(rb.getExperimentTitle());
                        //if the sample description of the report bean is not null,
                        //it will contain all of the params that must be written out
                        //this cohesion is maintained by the SampleForm
                        if (rb.getSampleDescription() != null) {
                            masterMetadata.getMzDataDescription().getAdmin().setSampleDescription(rb.getSampleDescription());
                            masterMetadata.getMzDataDescription().getAdmin().setSampleName(rb.getSampleName());
                        } else {
                            //if there is no sample description,
                            //use the params from the master report file
                        }
                    }

                    //write file
                    outFileName = copySingleMetadata(masterMetadata, masterPTMs, masterDatabaseMappings, reportFile);

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
     * <p/>
     * DOES NOT CHANGE THE INPUT OBJECTS
     * <p/>
     * PACKAGE METHOD so that it can be used by ReportWriter
     *
     * @param masterParams
     * @param daoParams
     * @return
     */
    static Param mergeParams(Param masterParams, Param daoParams) {

        //create new return object
        Param retval = new Param();
        //add objects in original dao object
        retval.getCvParam().addAll(daoParams.getCvParam());
        retval.getUserParam().addAll(daoParams.getUserParam());

        //loop over masterParams and add those not already in dao object to retval object
        for (CvParam cv : masterParams.getCvParam()) {
            if (!containsAccession(daoParams.getCvParam(), cv.getAccession())) {
                retval.getCvParam().add(cv);
            }
        }
        //loop over masterParams and add those not already in dao object to retval object
        for (UserParam up : masterParams.getUserParam()) {
            if (!containsName(daoParams.getUserParam(), up.getName())) {
                retval.getUserParam().add(up);
            }
        }
        return retval;
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
