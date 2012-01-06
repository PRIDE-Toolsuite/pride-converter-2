package uk.ac.ebi.pride.tools.converter.report.io;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.gui.util.IOUtilities;
import uk.ac.ebi.pride.tools.converter.report.model.DatabaseMapping;
import uk.ac.ebi.pride.tools.converter.report.model.Metadata;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;
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
        //need to update the metadata so that the source file element of the mzdata admin reflect the correct file
        masterMetadata.getMzDataDescription().getAdmin().setSourceFile(dao.getSourceFile());
        //write report
        return writer.writeReport(masterMetadata, masterPTMs, masterDatabaseMappings);
    }

}
