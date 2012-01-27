package uk.ac.ebi.pride.tools.filter;

import org.apache.commons.cli.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.utils.config.Configurator;
import uk.ac.ebi.pride.tools.filter.io.PrideXmlFilter;
import uk.ac.ebi.pride.tools.filter.model.impl.*;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 11/03/11
 * Time: 15:50
 * To change this template use File | Settings | File Templates.
 */
public class PrideFilter {

    private static final Logger logger = Logger.getLogger(PrideFilter.class);

    private static final int STATUS_OK = 0;
    private static final int STATUS_BAD_ARG = 1;
    private static final int STATUS_ERROR = 2;

    public static void main(String[] args) {

        // create the parser
        CommandLineParser parser = new GnuParser();

        String outputFile = null;
        String inputFile = null;

        try {
            // parse the command line arguments
            CommandLine line = parser.parse(PrideFilterCLIOptions.getOptions(), args);

            // ---------------------------------------------------------------- required parameters
            // files to filer - required
            if (line.hasOption(PrideFilterCLIOptions.OPTIONS.SOURCE_FILE.getValue())) {
                inputFile = line.getOptionValue(PrideFilterCLIOptions.OPTIONS.SOURCE_FILE.getValue());
            }

            // ---------------------------------------------------------------- optional parameters
            // outputfile  - optional
            if (line.hasOption(PrideFilterCLIOptions.OPTIONS.OUTPUT_FILE.getValue())) {
                outputFile = line.getOptionValue(PrideFilterCLIOptions.OPTIONS.OUTPUT_FILE.getValue());
            }

            // ---------------------------------------------------------------- help/version
            // help
            if (line.hasOption(PrideFilterCLIOptions.OPTIONS.HELP.getValue()) || args.length == 0) {
                printUsage();
                System.exit(STATUS_OK);
            }
            // version
            if (line.hasOption(PrideFilterCLIOptions.OPTIONS.VERSION.getValue())) {
                printVersion();
                System.exit(STATUS_OK);
            }

            // --------------------------------------------------------------- at this point, we assume to have all
            //                                                                 required parameters
            if (outputFile == null || inputFile == null) {
                printUsage();
                System.exit(STATUS_BAD_ARG);
            }


            // debug - optional
            boolean debug = line.hasOption(PrideFilterCLIOptions.OPTIONS.DEBUG.getValue());
            if (debug) {
                Logger baseLogger = Logger.getLogger("uk.ac.ebi.pride.tools.filter");
                if (baseLogger != null) {
                    //make it verbose, but not too verbose
//                    baseLogger.setLevel(Level.DEBUG);
                    baseLogger.setLevel(Level.INFO);
                    //also dump to console and not just to log file
                    baseLogger.setAdditivity(true);
                }
            }

            // ---------------------------------------------------------------- run filter
            PrideXmlFilter filter = new PrideXmlFilter(outputFile, inputFile, line.hasOption("compress"), line.hasOption("compress"));

            //register filters
            if (line.hasOption(PrideFilterCLIOptions.OPTIONS.ONLY_IDENTIFIED_SPECTRA.getValue())) {
                logger.info("Filtering out unidentified spectra");
                filter.setFilterUnidentifiedSpectra(true);
            }
            if (line.hasOption(PrideFilterCLIOptions.OPTIONS.REMOVE_EMPTY_SPECTRA.getValue())) {
                logger.info("Filtering out empty spectra");
                filter.registerSpectrumFilter(new EmptySpectrumFilter());
            }
            if (line.hasOption(PrideFilterCLIOptions.OPTIONS.LABEL_DECOY_HITS.getValue())) {
                logger.info("Updating decoy identifications");
                filter.registerIdentificationUpdatingFilter(new DecoyHitUpdatingFilter(line.getOptionValue(PrideFilterCLIOptions.OPTIONS.LABEL_DECOY_HITS.getValue())));
            }
            if (line.hasOption(PrideFilterCLIOptions.OPTIONS.MIN_PEPTIDE_NUMBER.getValue())) {
                logger.info("Filtering out identifications by number of peptide");
                try {
                    filter.registerIdentificationFilter(new MinimumPeptideCountFilter(Integer.valueOf(line.getOptionValue(PrideFilterCLIOptions.OPTIONS.MIN_PEPTIDE_NUMBER.getValue()))));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid peptide number: " + line.getOptionValue(PrideFilterCLIOptions.OPTIONS.MIN_PEPTIDE_NUMBER.getValue()));
                    System.exit(STATUS_BAD_ARG);
                }
            }
            if (line.hasOption(PrideFilterCLIOptions.OPTIONS.MIN_SCORE.getValue())) {
                logger.info("Filtering out identifications by score");
                try {
                    filter.registerIdentificationFilter(new MinimumIdentificationScoreFilter(Double.valueOf(line.getOptionValue(PrideFilterCLIOptions.OPTIONS.MIN_SCORE.getValue()))));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid score: " + line.getOptionValue(PrideFilterCLIOptions.OPTIONS.MIN_SCORE.getValue()));
                    System.exit(STATUS_BAD_ARG);
                }
            }
            if (line.hasOption(PrideFilterCLIOptions.OPTIONS.WHITELIST_FILE.getValue())) {
                logger.info("Using whitelist filter");
                filter.registerIdentificationFilter(new AccessionWhitelistFilter(line.getOptionValue(PrideFilterCLIOptions.OPTIONS.WHITELIST_FILE.getValue())));
            }
            if (line.hasOption(PrideFilterCLIOptions.OPTIONS.BLACKLIST_FILE.getValue())) {
                logger.info("Using blacklist filter");
                filter.registerIdentificationFilter(new AccessionBlacklistFilter(line.getOptionValue(PrideFilterCLIOptions.OPTIONS.BLACKLIST_FILE.getValue())));
            }

            filter.writeXml();

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
        formatter.printHelp("PrideConverter -filter", "\n", PrideFilterCLIOptions.getOptions(), "\n\n" + Configurator.getVersion() + "\n\n", true);
    }

}
