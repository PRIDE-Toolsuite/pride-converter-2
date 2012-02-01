package uk.ac.ebi.pride.tools.merger;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 18/03/11
 * Time: 16:28
 */
public class PrideMergerCLIOptions {

    public enum OPTIONS {

        HELP("help"),
        VERSION("version"),
        DEBUG("debug"),
        COMPRESS("compress"),
        OUTPUT_FILE("outputfile"),
        SOURCE_FILES("inputfiles");

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

        Option help = new Option(OPTIONS.HELP.getValue(), "print this message. If combined with -engine, will also output engine-specific options");
        Option version = new Option(OPTIONS.VERSION.getValue(), "print the version information and exit");
        Option debug = new Option(OPTIONS.DEBUG.getValue(), "print debugging information");
        Option compress = new Option(OPTIONS.COMPRESS.getValue(), "turn on gzip compression for output file");

        Option files = OptionBuilder.withArgName("files")
                .hasArgs()
                .withDescription("full path and filenames of files to merge")
                .create(OPTIONS.SOURCE_FILES.getValue());

        Option outputFile = OptionBuilder.withArgName("file")
                .hasArg()
                .withDescription("full path and filename of PRIDE XML output file")
                .create(OPTIONS.OUTPUT_FILE.getValue());

        options.addOption(help);
        options.addOption(version);
        options.addOption(debug);
        options.addOption(compress);
        options.addOption(files);
        options.addOption(outputFile);

    }

    public static Options getOptions() {
        return options;
    }

}
