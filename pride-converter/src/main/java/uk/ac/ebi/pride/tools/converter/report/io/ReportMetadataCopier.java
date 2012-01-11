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

        //neet to update short label / experiment title
        ReportBean rb = ConverterData.getInstance().getCustomeReportFields().get(dao.getSourceFile().getPathToFile());
        if (rb != null) {
            masterMetadata.setShortLabel(rb.getShortLabel());
            masterMetadata.setTitle(rb.getExperimentTitle());
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
