package uk.ac.ebi.pride.tools.filter;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 03/08/11
 * Time: 14:51
 */
public class PrideFilterCLIOptions {

    public enum OPTIONS {

        HELP("help"),
        VERSION("version"),
        DEBUG("debug"),
        COMPRESS("compress"),
        OUTPUT_FILE("outputfile"),
        SOURCE_FILE("inputfile"),
        WHITELIST_FILE("whitelistfile"),
        BLACKLIST_FILE("blacklistfile"),
        MIN_SCORE("min_score"),
        MIN_PEPTIDE_NUMBER("min_peptide_nb"),
        ONLY_IDENTIFIED_SPECTRA("only_identified_spectra"),
        REMOVE_EMPTY_SPECTRA("no_empty_spectra"),
        LABEL_DECOY_HITS("label_decoy_hits");

        private String value;

        OPTIONS(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private static final Options options = new Options();

    static {

        Option help = new Option(OPTIONS.HELP.getValue(), "print this message");
        Option version = new Option(OPTIONS.VERSION.getValue(), "print the version information and exit");
        Option debug = new Option(OPTIONS.DEBUG.getValue(), "print debugging information");
        Option compress = new Option(OPTIONS.COMPRESS.getValue(), "turn on gzip compression for output file");

        Option files = OptionBuilder.withArgName("file")
                .hasArg()
                .withDescription("full path and filename of file to filter")
                .create(OPTIONS.SOURCE_FILE.getValue());

        Option outputFile = OptionBuilder.withArgName("file")
                .hasArg()
                .withDescription("full path and filename of PRIDE XML output file")
                .create(OPTIONS.OUTPUT_FILE.getValue());

        Option whitelist = OptionBuilder.withArgName("file")
                .hasArg()
                .withDescription("full path and filename of file containing the protein accessions to keep")
                .create(OPTIONS.WHITELIST_FILE.getValue());

        Option blacklist = OptionBuilder.withArgName("file")
                .hasArg()
                .withDescription("full path and filename of file containing the protein accessions to remove")
                .create(OPTIONS.BLACKLIST_FILE.getValue());

        Option minScore = OptionBuilder.withArgName("score")
                .hasArg()
                .withDescription("minimum identification score to retain identification [0..1]")
                .create(OPTIONS.MIN_SCORE.getValue());

        Option minPeptideNb = OptionBuilder.withArgName("peptide_count")
                .hasArg()
                .withDescription("minimum number of peptides to retain identification")
                .create(OPTIONS.MIN_PEPTIDE_NUMBER.getValue());

        Option labelDecor = OptionBuilder.withArgName("decoy_pattern")
                .hasArg()
                .withDescription("label identifications as decoy hits if the identification accession matches the pattern")
                .create(OPTIONS.LABEL_DECOY_HITS.getValue());

        Option identifiedSpectra = new Option(OPTIONS.ONLY_IDENTIFIED_SPECTRA.getValue(), "Only keep identified spectra");
        Option removeEmpty = new Option(OPTIONS.REMOVE_EMPTY_SPECTRA.getValue(), "Remove empty spectra");

        options.addOption(help);
        options.addOption(version);
        options.addOption(debug);
        options.addOption(compress);
        options.addOption(files);
        options.addOption(outputFile);
        options.addOption(whitelist);
        options.addOption(blacklist);
        options.addOption(minScore);
        options.addOption(minPeptideNb);
        options.addOption(labelDecor);
        options.addOption(identifiedSpectra);
        options.addOption(removeEmpty);

    }

    public static Options getOptions() {
        return options;
    }


}
