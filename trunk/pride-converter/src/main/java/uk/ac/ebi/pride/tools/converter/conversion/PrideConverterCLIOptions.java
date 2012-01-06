package uk.ac.ebi.pride.tools.converter.conversion;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 11/02/11
 * Time: 14:58
 * To change this template use File | Settings | File Templates.
 */
public class PrideConverterCLIOptions {

    public enum OPTIONS {

        REPORT_FILE("reportfile"),
        SOURCE_FILE("sourcefile"),
        OUTPUT_FILE("outputfile"),
        FASTA_FILE("fastafile"),
        FASTA_FORMAT("fastaformat"),
        MZTAB_FILE("mztabfile"),
        ENGINE("engine"),
        MODE("mode"),
        D("D"),
        HELP("help"),
        VERSION("version"),
        DEBUG("debug"),
        COMPRESS("compress"),
        INCLUDE_ONLY_IDENTIFIED_SPECTRA("reportOnlyIdentifiedSpectra"),
        SPOT_IDENTIFIER("gel_spot_identifier"),
        SPOT_REGEX("gel_spot_regex"),
        GEL_IDENTIFIER("gel_identifier");

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

        Option reportFile = OptionBuilder.withArgName("file")
                .hasArg()
                .withDescription("full path and filename of report file. OPTIONAL. Will default to <sourcefile>-report.xml")
                .create(OPTIONS.REPORT_FILE.getValue());

        Option sourceFile = OptionBuilder.withArgName("file")
                .hasArg()
                .withDescription("full path and filename of source file. ")
                .create(OPTIONS.SOURCE_FILE.getValue());

        Option outputFile = OptionBuilder.withArgName("file")
                .hasArg()
                .withDescription("full path and filename of PRIDE XML output file. OPTIONAL. Will default to <sourcefile>.xml.gz")
                .create(OPTIONS.OUTPUT_FILE.getValue());

        Option fastaFile = OptionBuilder.withArgName("file")
                .hasArg()
                .withDescription("full path and filename of FASTA file used as a search database")
                .create(OPTIONS.FASTA_FILE.getValue());

        Option fastaFormat = OptionBuilder.withArgName("format")
                .hasArg()
                .withDescription("The format of the FASTA id line. OPTIONAL. Must be one of [FULL, UNIPROT_MATCH_ID, UNIPROT_MATCH_AC, FIRST_WORD]. Defaults to FULL")
                .create(OPTIONS.FASTA_FORMAT.getValue());

        Option mzTabFile = OptionBuilder.withArgName("file")
                .hasArg()
                .withDescription("full path and filename of mzTab file")
                .create(OPTIONS.MZTAB_FILE.getValue());

        Option engine = OptionBuilder.withArgName("engine")
                .hasArg()
                .withDescription("search engine. Must be one of the following values: [MASCOT, MGF, DTA, PKL, MS2, mzML, XTandem, mzIdentML, mzXML, mzData, MSGF]")
                .create(OPTIONS.ENGINE.getValue());

        Option mode = OptionBuilder.withArgName("mode")
                .hasArg()
                .withDescription("The mode in which to run PrideConverter. Must be one of the following values: [PRESCAN, CONVERT, MZTAB]")
                .create(OPTIONS.MODE.getValue());

        Option onlyIdentified = OptionBuilder.withArgName("reportOnlyIdentifiedSpectra")
                .withDescription("Indicates that only identified spectra should be reported in the generated PRIDE XML file.")
                .create(OPTIONS.INCLUDE_ONLY_IDENTIFIED_SPECTRA.getValue());

        Option gelIdentifier = OptionBuilder.withArgName("gel identifier")
        		.hasArg()
        		.withDescription("sets the gel identifier to be used for identifications in the generated mzTab file. This option only takes effect when generating mzTab files.")
        		.create(OPTIONS.GEL_IDENTIFIER.getValue());
        
        Option spotIdentifier = OptionBuilder.withArgName("spot identifier")
				.hasArg()
				.withDescription("sets the gel spot identifier to be used for identifications in the generated mzTab file. This option only takes effect when generating mzTab files. This option is ignore if gel_spot_regex is set.")
				.create(OPTIONS.SPOT_IDENTIFIER.getValue());
        
        Option spotRegex = OptionBuilder.withArgName("regular expression")
				.hasArg()
				.withDescription("used to extract the gel spot identifier based on the sourcefile's name. The first matching group in the pattern is used as a spot identifier.")
				.create(OPTIONS.SPOT_REGEX.getValue());
        
        Option property = OptionBuilder.withArgName("property=value")
                .hasArgs(2)
                .withValueSeparator()
                .withDescription("use value for given property. If passing engine-specific options, this should only be used with -mode=PRESCAN. In mode=SCAN, engine-specific configuration options are parsed from the report file.")
                .create(OPTIONS.D.getValue());

        options.addOption(help);
        options.addOption(version);
        options.addOption(debug);
        options.addOption(property);
        options.addOption(reportFile);
        options.addOption(sourceFile);
        options.addOption(outputFile);
        options.addOption(fastaFile);
        options.addOption(mzTabFile);
        options.addOption(engine);
        options.addOption(mode);
        options.addOption(compress);
        options.addOption(fastaFormat);
        options.addOption(gelIdentifier);
        options.addOption(spotIdentifier);
        options.addOption(spotRegex);
        options.addOption(onlyIdentified);

    }

    public static Options getOptions() {
        return options;
    }

}
