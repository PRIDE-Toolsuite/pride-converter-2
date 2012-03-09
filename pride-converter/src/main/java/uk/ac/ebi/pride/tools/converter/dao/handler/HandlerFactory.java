package uk.ac.ebi.pride.tools.converter.dao.handler;

import uk.ac.ebi.pride.tools.converter.dao.handler.impl.FastaHandlerImpl;
import uk.ac.ebi.pride.tools.converter.dao.handler.impl.MzTabHandler;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 06-Dec-2010
 * Time: 16:25:14
 * To change this template use File | Settings | File Templates.
 */
public class
        HandlerFactory {

    public enum FASTA_FORMAT {
        FULL("Match Full ID Line"),
        UNIPROT_MATCH_ID("Match Uniprot ID"),
        UNIPROT_MATCH_AC("Match Uniprot AC"),
        FIRST_WORD("Match First Word");

        private String displayString;

        FASTA_FORMAT(String displayString) {
            this.displayString = displayString;
        }

        public String getDisplayString() {
            return displayString;
        }

        public static FASTA_FORMAT getFastaFormatByDisplayString(String displayString) {
            for (FASTA_FORMAT format : values()) {
                if (format.getDisplayString().equals(displayString)) {
                    return format;
                }
            }
            return null;
        }
    }

    public enum EXTERNAL_HANDLER {
        MZTAB
    }

    private static HandlerFactory instance = new HandlerFactory();

    public static HandlerFactory getInstance() {
        return instance;
    }

    public FastaHandler getFastaHandler(String fastaFilePath, FASTA_FORMAT format) {
        return new FastaHandlerImpl(fastaFilePath, format);
    }

    public FastaHandler getFastaHandler(String fastaFilePath, String format) {

        if (format != null) {

            FASTA_FORMAT fastaFormat = null;
            if ("full".equals(format)) {
                fastaFormat = FASTA_FORMAT.FULL;
            } else if ("uniprot_match_id".equals(format)) {
                fastaFormat = FASTA_FORMAT.UNIPROT_MATCH_ID;
            } else if ("uniprot_match_ac".equals(format)) {
                fastaFormat = FASTA_FORMAT.UNIPROT_MATCH_AC;
            } else if ("first_word".equals(format)) {
                fastaFormat = FASTA_FORMAT.FIRST_WORD;
            } else {
                throw new IllegalArgumentException("Unknown FASTA format: " + format);
            }
            return new FastaHandlerImpl(fastaFilePath, fastaFormat);

        } else {
            throw new IllegalArgumentException("FASTA format must not be null");
        }
    }

    public ExternalHandler getExternalHandler(String filePath, EXTERNAL_HANDLER type) {
        switch (type) {
            case MZTAB:
                return new MzTabHandler(filePath);
            default:
                throw new ConverterException("No external hanlder defined for type: " + type);
        }
    }

    public ExternalHandler getDefaultExternalHanlder(String filePath) {
        return getExternalHandler(filePath, EXTERNAL_HANDLER.MZTAB);
    }
}
