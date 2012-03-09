package uk.ac.ebi.pride.tools.converter.gui.util;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.conversion.io.MzTabWriter;
import uk.ac.ebi.pride.tools.converter.dao.DAO;
import uk.ac.ebi.pride.tools.converter.dao.DAOFactory;
import uk.ac.ebi.pride.tools.converter.dao.handler.HandlerFactory;
import uk.ac.ebi.pride.tools.converter.gui.NavigationPanel;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.gui.model.FileBean;
import uk.ac.ebi.pride.tools.converter.gui.model.GUIException;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
import uk.ac.ebi.pride.tools.converter.report.io.ReportWriter;
import uk.ac.ebi.pride.tools.converter.report.io.xml.utilities.ReportXMLUtilities;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;
import uk.ac.ebi.pride.tools.merger.io.PrideXmlMerger;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 21/10/11
 * Time: 13:34
 * To change this template use File | Settings | File Templates.
 */
public class IOUtilities {

    private static final Logger logger = Logger.getLogger(IOUtilities.class);
    public static final String GEL_IDENTIFIER = "Gel Identifier";
    public static final String SPOT_IDENTIFIER = "Spot Identifier";
    public static final String SPOT_REGULAR_EXPRESSION = "Spot Regular Expression";
    public static final String COMPRESS = "Compress output file";

    public static String getFileNameWithoutExtension(File file) {
        String retval = null;
        if (file != null) {
            if (file.getName().lastIndexOf(".") > 0) {
                retval = file.getName().substring(0, file.getName().lastIndexOf("."));
            }
        }
        return retval;
    }

    public static boolean renameFile(String fromFile, String toFile) {

        try {

            File destinationFile = new File(toFile);
            File sourceFile = new File(fromFile);

            //delete the destination file, if it exists
            deleteFiles(toFile);

            if (sourceFile.renameTo(destinationFile)) {
                logger.info("Successfully renamed temporary file to final path: " + toFile);
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            throw new ConverterException("Error renaming file", e);
        }

    }

    private static void deleteFiles(String file) {

        logger.warn("Deleting file: " + file);

        File fileToDelete = new File(file);
        //old file exists - sanity check
        if (fileToDelete.exists()) {

            try {
                //in windows systems, there might be lock contention issues that need resolving while
                //the system clears up stale file handles
                int nbTries = 5;
                while (nbTries > 0 && !fileToDelete.delete()) {
                    System.gc();
                    Thread.sleep(2000);
                    nbTries--;
                }
                if (nbTries == 0) {
                    throw new ConverterException("Could not delete file: " + file);
                }
            } catch (InterruptedException e) {
                /* no op */
            }
        }

    }

    public static void deleteFiles(Set<String> filesToDelete) {
        for (String file : filesToDelete) {
            deleteFiles(file);
        }
    }

    public static void generateMzTabFiles(Properties options, Collection<File> inputFiles) throws GUIException {

        NavigationPanel.getInstance().setWorkingMessage("Creating mzTab files.");

        File outputDir = new File(ConverterData.getInstance().getLastSelectedDirectory());
        ConverterData.getInstance().setOptions(options);

        for (File file : inputFiles) {
            final String absolutePath = file.getAbsolutePath();
            try {

                logger.warn("Reading = " + absolutePath);
                NavigationPanel.getInstance().setWorkingMessage("Creating mzTab file for " + absolutePath);

                FileBean fileBean = new FileBean(absolutePath);

                DAO dao = DAOFactory.getInstance().getDAO(absolutePath, ConverterData.getInstance().getDaoFormat());

                // check if a gel or spot identifier is present
                String gelId = null, spotId = null;
                Pattern spotPattern = null;

                if (options.getProperty(GEL_IDENTIFIER) != null && !"".equals(options.getProperty(GEL_IDENTIFIER))) {
                    gelId = options.getProperty(GEL_IDENTIFIER);
                    //do not pass non-dao option
                    options.remove(GEL_IDENTIFIER);
                }
                if (options.getProperty(SPOT_IDENTIFIER) != null && !"".equals(options.getProperty(SPOT_IDENTIFIER))) {
                    spotId = options.getProperty(SPOT_IDENTIFIER);
                    //do not pass non-dao option
                    options.remove(SPOT_IDENTIFIER);
                }
                if (options.getProperty(SPOT_REGULAR_EXPRESSION) != null && !"".equals(options.getProperty(SPOT_REGULAR_EXPRESSION))) {
                    String regex = options.getProperty(SPOT_REGULAR_EXPRESSION);
                    spotPattern = Pattern.compile(regex);
                    //do not pass non-dao option
                    options.remove(SPOT_REGULAR_EXPRESSION);
                }
                dao.setConfiguration(options);

                // write the mztab file
                MzTabWriter writer;

                if (spotPattern != null)
                    writer = new MzTabWriter(dao, gelId, spotPattern);
                else
                    writer = new MzTabWriter(dao, gelId, spotId);

                String tabFile = file.getName() + ConverterData.MZTAB;

                writer.writeMzTabFile(new File(outputDir, tabFile));

                //update converterdata
                fileBean.setMzTabFile(tabFile);
                ConverterData.getInstance().getDataFiles().add(fileBean);

            } catch (Exception e) {
                logger.fatal("Error in Generating MzTAB Files for input file " + absolutePath + ", error is " + e.getMessage(), e);
                GUIException gex = new GUIException(e);
                gex.setShortMessage("Error in Generating MzTAB Files for input file " + absolutePath);
                gex.setDetailedMessage(null);
                gex.setComponent(IOUtilities.class.getName());
                throw gex;
            }
        }

    }

    public static void generateReportFiles(Properties options, Collection<FileBean> dataFiles, boolean forceRegeneration, boolean automaticallyMapPreferredPTMs) throws GUIException {

        ConverterData.getInstance().setOptions(options);
        int i = 0;

        for (FileBean fileBean : dataFiles) {

            try {

                String reportFile = fileBean.getInputFile() + ConverterData.REPORT_XML;
                if (forceRegeneration) {
                    generateReportFile(fileBean, options, automaticallyMapPreferredPTMs);
                } else {

                    //try and load existing report file
                    NavigationPanel.getInstance().setWorkingMessage("Attemping to load existing report file: " + reportFile);
                    File repFile = new File(reportFile);
                    //check to see if file exists and is a valid report file
                    if (!repFile.exists() || !ReportXMLUtilities.isUnmodifiedSourceForReportFile(repFile, fileBean.getInputFile())) {
                        logger.warn("Source file modified since report generation, will recreate report file");
                        generateReportFile(fileBean, options, automaticallyMapPreferredPTMs);
                    }
                }

                fileBean.setReportFile(reportFile);
                //update datafiles collection
                ConverterData.getInstance().getDataFiles().add(fileBean);
                if (i == 0) {
                    ConverterData.getInstance().setMasterFile(fileBean);
                    ConverterData.getInstance().setMasterDAO(new ReportReaderDAO(new File(reportFile)));
                }
                // Read PTMs
                ReportReaderDAO reportReaderDAO = new ReportReaderDAO(new File(reportFile));
                ConverterData.getInstance().getPTMs().addAll(reportReaderDAO.getPTMs());
                // Read DB Mappings
                ConverterData.getInstance().getDatabaseMappings().addAll(reportReaderDAO.getDatabaseMappings());
                //increment file count
                i++;

            } catch (ConverterException e) {
                logger.fatal("Error in generating Report Files for input file " + fileBean.getInputFile() + ", error is " + e.getMessage(), e);
                GUIException gex = new GUIException(e);
                gex.setShortMessage("Error in generating Report Files for input file " + fileBean.getInputFile());
                gex.setDetailedMessage(null);
                gex.setComponent(IOUtilities.class.getName());
                throw gex;
            } catch (InvalidFormatException e) {
                logger.fatal("Invalid file format for input file " + fileBean.getInputFile() + ", error is " + e.getMessage(), e);
                GUIException gex = new GUIException(e);
                gex.setShortMessage("Invalid file format for input file " + fileBean.getInputFile() + "\nPlease select a properly formatted file and try again.");
                gex.setDetailedMessage(null);
                gex.setComponent(IOUtilities.class.getName());
                throw gex;
            }
        }

    }

    private static void generateReportFile(FileBean fileBean, Properties options, boolean automaticallyMapPreferredPTMs) throws InvalidFormatException {

        String reportFile = fileBean.getInputFile() + ConverterData.REPORT_XML;

        //create report file
        NavigationPanel.getInstance().setWorkingMessage("Creating report file for " + fileBean.getInputFile());

        logger.warn("Reading = " + fileBean.getInputFile());

        DAO dao = DAOFactory.getInstance().getDAO(fileBean.getInputFile(), ConverterData.getInstance().getDaoFormat());
        dao.setConfiguration(options);
        if (fileBean.getSpectrumFile() != null) {
            dao.setExternalSpectrumFile(fileBean.getSpectrumFile());
        }

        ReportWriter writer = new ReportWriter(reportFile);
        writer.setDAO(dao);
        writer.setAutomaticallyMapPreferredPTMs(automaticallyMapPreferredPTMs);

        if (fileBean.getSequenceFile() != null) {
            writer.setFastaHandler(HandlerFactory.getInstance().getFastaHandler(fileBean.getSequenceFile(), ConverterData.getInstance().getFastaFormat()));
        }

        if (fileBean.getMzTabFile() != null) {
            writer.setExternalHandler(HandlerFactory.getInstance().getDefaultExternalHanlder(fileBean.getMzTabFile()));
        }

        logger.warn("Writing = " + reportFile);
        writer.writeReport();

    }

    public static void mergePrideXMLFiles(Properties options, Collection<File> inputFiles) throws GUIException {

        try {

            //get compression option
            boolean compress = false;
            if (options.getProperty(COMPRESS) != null && !"".equals(options.getProperty(COMPRESS))) {
                compress = Boolean.valueOf(options.getProperty(COMPRESS));
            }

            //prep input files
            for (File inFile : inputFiles) {
                FileBean fileBean = new FileBean(inFile.getAbsolutePath());
                ConverterData.getInstance().getDataFiles().add(fileBean);
            }
            List<String> files = new ArrayList<String>(ConverterData.getInstance().getInputFiles());

            //set output file
            String outputFilePath = files.get(0) + ConverterData.MERGED_XML;

            //merge
            PrideXmlMerger merger = new PrideXmlMerger(files, outputFilePath, compress, true);
            //filename can change if compression is set to true
            outputFilePath = merger.mergeXml();
            ConverterData.getInstance().setMergedOutputFile(outputFilePath);

        } catch (ConverterException e) {
            logger.fatal("Error in merging XML: " + e.getMessage(), e);
            GUIException gex = new GUIException(e);
            gex.setShortMessage("Error in merging XML files");
            gex.setDetailedMessage(null);
            gex.setComponent(IOUtilities.class.getName());
            throw gex;
        }

    }
}
