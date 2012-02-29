package uk.ac.ebi.pride.tools.converter.conversion;

import org.apache.commons.cli.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.conversion.io.MzTabWriter;
import uk.ac.ebi.pride.tools.converter.conversion.io.PrideXmlWriter;
import uk.ac.ebi.pride.tools.converter.dao.DAO;
import uk.ac.ebi.pride.tools.converter.dao.DAOFactory;
import uk.ac.ebi.pride.tools.converter.dao.DAOFactory.DAO_FORMAT;
import uk.ac.ebi.pride.tools.converter.dao.DAOProperty;
import uk.ac.ebi.pride.tools.converter.dao.handler.HandlerFactory;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReader;
import uk.ac.ebi.pride.tools.converter.report.io.ReportWriter;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;
import uk.ac.ebi.pride.tools.converter.utils.config.Configurator;

import java.io.File;
import java.util.Collection;
import java.util.Properties;
import java.util.regex.Pattern;


/**
 * User: rcote
 * Date: 19/01/11
 * Time: 15:40
 * CLI for PRIDE Converter 2.0
 */
public class PrideConverter {

    //exit codes
    private static final int STATUS_OK = 0;
    private static final int STATUS_BAD_ARG = 1;
    private static final int STATUS_ERROR = 2;

    private static String reportFile;
    private static String outputFile;
    private static String sourceFile;
    private static String spectraFile;
    private static String fastaFile;
    private static String mztabFile;
    private static String engine;   //will always be set to lowercase
    private static String mode;     //will always be set to lowercase
    private static boolean debug = false;

    public static void main(String[] args) {

        // create the parser
        CommandLineParser parser = new GnuParser();

        try {
            // parse the command line arguments
            CommandLine line = parser.parse(PrideConverterCLIOptions.getOptions(), args);

            // ---------------------------------------------------------------- required parameters
            // engine - required
            if (line.hasOption(PrideConverterCLIOptions.OPTIONS.ENGINE.getValue())) {
                engine = line.getOptionValue(PrideConverterCLIOptions.OPTIONS.ENGINE.getValue()).toLowerCase();
            }

            // sourceFile - required
            if (line.hasOption(PrideConverterCLIOptions.OPTIONS.SOURCE_FILE.getValue())) {
                sourceFile = line.getOptionValue(PrideConverterCLIOptions.OPTIONS.SOURCE_FILE.getValue());
            }

            // mode - required
            if (line.hasOption(PrideConverterCLIOptions.OPTIONS.MODE.getValue())) {
                mode = line.getOptionValue(PrideConverterCLIOptions.OPTIONS.MODE.getValue()).toLowerCase();
            }
            
            // spectra file - optional
            if (line.hasOption(PrideConverterCLIOptions.OPTIONS.SPECTRA_FILE.getValue())) {
            	spectraFile = line.getOptionValue(PrideConverterCLIOptions.OPTIONS.SPECTRA_FILE.getValue());
            }

            // ---------------------------------------------------------------- help/version
            // help
            if (line.hasOption(PrideConverterCLIOptions.OPTIONS.HELP.getValue()) || args.length == 0) {
                printUsage(engine);
                System.exit(STATUS_OK);
            }
            // version
            if (line.hasOption(PrideConverterCLIOptions.OPTIONS.VERSION.getValue())) {
                printVersion();
                System.exit(STATUS_OK);
            }

            // --------------------------------------------------------------- at this point, we assume to have all
            //                                                                 required parameters
            if (mode == null || engine == null || sourceFile == null) {
                printUsage(null);
                System.exit(STATUS_BAD_ARG);
            }

            // ---------------------------------------------------------------- optional parameters
            // outputfile  - optional
            if (line.hasOption(PrideConverterCLIOptions.OPTIONS.OUTPUT_FILE.getValue())) {
                outputFile = line.getOptionValue(PrideConverterCLIOptions.OPTIONS.OUTPUT_FILE.getValue());
            } else {
                //set default output file based on sourceFile
                File tmp = new File(sourceFile);
                String tmpName = tmp.getName();
                if (!"".equals(tmpName)) {
                    outputFile = tmp.getAbsolutePath() + "-pride.xml";
                }
            }

            // reportfile - optional
            if (line.hasOption(PrideConverterCLIOptions.OPTIONS.REPORT_FILE.getValue())) {
                reportFile = line.getOptionValue(PrideConverterCLIOptions.OPTIONS.REPORT_FILE.getValue());
            } else {
                //set default output file based on sourceFile
                File tmp = new File(sourceFile);
                String tmpName = tmp.getName();
                if (!"".equals(tmpName)) {
                    reportFile = tmp.getAbsolutePath() + "-report.xml";
                }
            }

            // fastafile - optional
            if (line.hasOption(PrideConverterCLIOptions.OPTIONS.FASTA_FILE.getValue())) {
                fastaFile = line.getOptionValue(PrideConverterCLIOptions.OPTIONS.FASTA_FILE.getValue());
            }

            // mztabfile - optional
            if (line.hasOption(PrideConverterCLIOptions.OPTIONS.MZTAB_FILE.getValue())) {
                mztabFile = line.getOptionValue(PrideConverterCLIOptions.OPTIONS.MZTAB_FILE.getValue());
            }

            // debug - optional
            debug = line.hasOption(PrideConverterCLIOptions.OPTIONS.DEBUG.getValue());
            if (debug) {
                Logger baseLogger = Logger.getLogger("uk.ac.ebi.pride.tools.converter");
                if (baseLogger != null) {
                    //make it verbose, but not too verbose
                    baseLogger.setLevel(Level.INFO);
                    //also dump to console and not just to log file
                    baseLogger.setAdditivity(true);
                }
            }

            // report only identified spectra - optional
            boolean reportOnlyIdentified = line.hasOption(PrideConverterCLIOptions.OPTIONS.INCLUDE_ONLY_IDENTIFIED_SPECTRA.getValue());

            // ---------------------------------------------------------------- setup conversion/prescan

            //setup DAO
            DAOFactory.DAO_FORMAT daoFormat = DAO_FORMAT.getDAOForSearchengineOption(engine);

            if (daoFormat == null) {
                System.err.println("Unknown engine: " + engine);
                System.exit(STATUS_BAD_ARG);
            }

            DAO dao = DAOFactory.getInstance().getDAO(sourceFile, daoFormat);
            
            if (spectraFile != null)
            	dao.setExternalSpectrumFile(spectraFile);

            // run in prescan mode
            if ("prescan".equals(mode)) {

                //check to see if we have props to send to dao
                Properties props = line.getOptionProperties(PrideConverterCLIOptions.OPTIONS.D.getValue());
                if (!props.isEmpty()) {
                    if (debug) {
                        System.out.println("Calling engine with the following properties");
                        props.list(System.out);
                    }
                    dao.setConfiguration(props);
                }

                //write report
                ReportWriter writer = new ReportWriter(reportFile);
                writer.setDAO(dao);
                //the CLI in prescan mode will always automatically map the PTMs!
                writer.setAutomaticallyMapPreferredPTMs(true);


                if (fastaFile != null) {

                    String fastaFormat = "full";
                    if (line.hasOption(PrideConverterCLIOptions.OPTIONS.FASTA_FORMAT.getValue())) {
                        fastaFormat = line.getOptionValue(PrideConverterCLIOptions.OPTIONS.FASTA_FORMAT.getValue()).toLowerCase();
                    }

                    if (debug) {
                        System.out.println("Processing FASTA file: " + fastaFile);
                        System.out.println("FASTA file format: " + fastaFormat);
                    }
                    writer.setFastaHandler(HandlerFactory.getInstance().getFastaHandler(fastaFile, fastaFormat));
                }
                if (mztabFile != null) {
                    if (debug) {
                        System.out.println("Using mzTab file: " + mztabFile);
                    }
                    //currently, there's only the default external handler :)
                    writer.setExternalHandler(HandlerFactory.getInstance().getDefaultExternalHanlder(mztabFile));
                }
                if (debug) {
                    System.out.println("Writing report file to " + reportFile);
                }
                writer.writeReport();

                if (debug) {
                    System.out.println("done!");
                }

            }

            // run in convert mode
            else if ("convert".equals(mode)) {

                //create report reader
                ReportReader reader = new ReportReader(new File(reportFile));
                //update DAO configuration based on report
                dao.setConfiguration(reader.getConfigurationOptions().asProperties());
                //write xml
                PrideXmlWriter out = new PrideXmlWriter(outputFile, reader, dao, line.hasOption("compress"));
                out.setIncludeOnlyIdentifiedSpectra(reportOnlyIdentified);

                if (debug) {
                    System.out.println("Writing PRIDE XML to " + out.getOutputFilePath());
                }
                out.writeXml();

            }

            // create an mztab file
            else if ("mztab".equals(mode)) {
                //check to see if we have props to send to dao
                Properties props = line.getOptionProperties(PrideConverterCLIOptions.OPTIONS.D.getValue());
                if (!props.isEmpty()) {
                    if (debug) {
                        System.out.println("Calling engine with the following properties");
                        props.list(System.out);
                    }
                    dao.setConfiguration(props);
                }

                // check if a gel or spot identifier is present
                String gelId = null, spotId = null;
                Pattern spotPattern = null;

                if (line.hasOption(PrideConverterCLIOptions.OPTIONS.GEL_IDENTIFIER.getValue())) {
                    gelId = line.getOptionValue(PrideConverterCLIOptions.OPTIONS.GEL_IDENTIFIER.getValue());
                }
                if (line.hasOption(PrideConverterCLIOptions.OPTIONS.SPOT_IDENTIFIER.getValue())) {
                    spotId = line.getOptionValue(PrideConverterCLIOptions.OPTIONS.SPOT_IDENTIFIER.getValue());
                }
                if (line.hasOption(PrideConverterCLIOptions.OPTIONS.SPOT_REGEX.getValue())) {
                    String regex = line.getOptionValue(PrideConverterCLIOptions.OPTIONS.SPOT_REGEX.getValue());
                    spotPattern = Pattern.compile(regex);
                }

                // write the mztab file
                MzTabWriter writer;

                if (spotPattern != null)
                    writer = new MzTabWriter(dao, gelId, spotPattern);
                else
                    writer = new MzTabWriter(dao, gelId, spotId);

                // write the file
                File outfile = null;

                if (mztabFile != null)
                    outfile = new File(mztabFile);
                else {
                    File tmpFile = new File(sourceFile);
                    if (!"".equals(tmpFile.getName()))
                        outfile = new File(tmpFile.getAbsolutePath() + "-mztab.txt");
                }

                try {
                    writer.writeMzTabFile(outfile);

                    System.out.println("MzTab file successfully written to '" + outfile.getAbsolutePath() + "'.");
                } catch (Exception e) {
                    System.out.println("Error: Failed to write mzTab file.");
                    if (debug)
                        e.printStackTrace();
                }
            } else {
                System.err.println("Invalid mode '" + mode + "' set. Only 'SCAN' or 'CONVERT' allowed.");
                System.exit(STATUS_BAD_ARG);
            }

        } catch (ParseException e) {
            // oops, something went wrong
            System.err.println("Parsing failed: " + e.getMessage());
            System.exit(STATUS_ERROR);
        } catch (InvalidFormatException e) {
            // oops, something went wrong
            System.err.println("Invalid file format for input file: " + e.getMessage());
            System.exit(STATUS_ERROR);
        }

    }

    private static void printVersion() {
        System.out.println(Configurator.getVersion());
    }

    private static void printUsage(String engine) {
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("PrideConverter -converter", "Note that -mode, -engine and -sourcefile are required parameters for conversion.\n", PrideConverterCLIOptions.getOptions(), "\n\n" + Configurator.getVersion() + "\n\n", true);

        if (engine != null) {
            printEngineSpecificUsage(engine);
        }
    }

    private static void printEngineSpecificUsage(String engine) {

        if (engine == null) {
            return;
        }

        DAO_FORMAT daoFormat = DAO_FORMAT.getDAOForSearchengineOption(engine);

        if (daoFormat == null) {
            System.err.println("Unknown engine: " + engine);
            System.exit(STATUS_BAD_ARG);
        }

        Collection<DAOProperty> properties = DAOFactory.getInstance().getSupportedProperties(daoFormat);
        // create Options object
        Options options = new Options();
        for (DAOProperty prop : properties) {
            // add t option
            options.addOption(prop.getName(), false, prop.getDescription());
        }

        if (properties.size() > 0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(daoFormat.getNiceName() + " engine options", "use the -Dproperty=value syntax to use these options\n", options, null, false);
        }
    }


}
