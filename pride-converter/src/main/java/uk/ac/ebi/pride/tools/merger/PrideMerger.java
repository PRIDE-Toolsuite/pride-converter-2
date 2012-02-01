package uk.ac.ebi.pride.tools.merger;

import org.apache.commons.cli.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.utils.config.Configurator;
import uk.ac.ebi.pride.tools.merger.io.PrideXmlMerger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 18/03/11
 * Time: 16:28
 */
public class PrideMerger {

    private static final int STATUS_OK = 0;
    private static final int STATUS_BAD_ARG = 1;
    private static final int STATUS_ERROR = 2;

    public static void main(String[] args) {

        // create the parser
        CommandLineParser parser = new GnuParser();

        String outputFile = null;
        List<String> inputFiles = new ArrayList<String>();

        try {
            // parse the command line arguments
            CommandLine line = parser.parse(PrideMergerCLIOptions.getOptions(), args);

            // ---------------------------------------------------------------- required parameters
            // files to merge - required
            if (line.hasOption(PrideMergerCLIOptions.OPTIONS.SOURCE_FILES.getValue())) {
                String[] infiles = line.getOptionValues(PrideMergerCLIOptions.OPTIONS.SOURCE_FILES.getValue());
                if (infiles != null && infiles.length > 0) {
                    inputFiles.addAll(Arrays.asList(infiles));
                }
            }

            // ---------------------------------------------------------------- optional parameters
            // outputfile  - optional
            if (line.hasOption(PrideMergerCLIOptions.OPTIONS.OUTPUT_FILE.getValue())) {
                outputFile = line.getOptionValue(PrideMergerCLIOptions.OPTIONS.OUTPUT_FILE.getValue());
            }

            // ---------------------------------------------------------------- help/version
            // help
            if (line.hasOption(PrideMergerCLIOptions.OPTIONS.HELP.getValue()) || args.length == 0) {
                printUsage();
                System.exit(STATUS_OK);
            }
            // version
            if (line.hasOption(PrideMergerCLIOptions.OPTIONS.VERSION.getValue())) {
                printVersion();
                System.exit(STATUS_OK);
            }

            // --------------------------------------------------------------- at this point, we assume to have all
            //                                                                 required parameters
            if (outputFile == null || inputFiles.isEmpty()) {
                printUsage();
                System.exit(STATUS_BAD_ARG);
            }


            // debug - optional
            boolean debug = line.hasOption(PrideMergerCLIOptions.OPTIONS.DEBUG.getValue());
            if (debug) {
                Logger baseLogger = Logger.getLogger("uk.ac.ebi.pride.tools.merger");
                if (baseLogger != null) {
                    //make it verbose, but not too verbose
//                    baseLogger.setLevel(Level.DEBUG);
                    baseLogger.setLevel(Level.INFO);
                    //also dump to console and not just to log file
                    baseLogger.setAdditivity(true);
                }
            }

            // ---------------------------------------------------------------- run merge
            PrideXmlMerger merger = new PrideXmlMerger(inputFiles, outputFile, line.hasOption("compress"), false);
            merger.mergeXml();

        } catch (ParseException e) {
            // oops, something went wrong
            System.err.println("Parsing failed: " + e.getMessage());
            System.exit(STATUS_ERROR);
        }

    }

    private static void printVersion() {
        System.out.println(Configurator.getVersion());
    }

    private static void printUsage() {
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("PrideConverter -merger", "\n", PrideMergerCLIOptions.getOptions(), "\n\n" + Configurator.getVersion() + "\n\n", true);
    }

}
