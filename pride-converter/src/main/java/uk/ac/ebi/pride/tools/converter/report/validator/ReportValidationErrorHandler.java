package uk.ac.ebi.pride.tools.converter.report.validator;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import psidev.psi.tools.validator.Context;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorMessage;
import psidev.psi.tools.validator.rules.Rule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 26/09/11
 * Time: 10:13
 */
public class ReportValidationErrorHandler implements ErrorHandler {

    private List<ValidatorMessage> errorMessages = null;

    public ReportValidationErrorHandler() {
        super();
        errorMessages = new ArrayList<ValidatorMessage>();
    }

    public boolean noErrors() {
        // !There can be Warning messages as well as Error messages!
        // We do not want to report an error if we only have warning messages.
        // So we iterate over the messages and check if there is at least one error.
        for (ValidatorMessage message : errorMessages) {
            if (message.getLevel().isSame(MessageLevel.ERROR) || message.getLevel().isHigher(MessageLevel.ERROR)) {
                return false;
            }
        }
        return true;
    }

    public List<ValidatorMessage> getIssuesAsValidatorMessages() {
        return errorMessages;
    }

    /**
     * Receive notification of a recoverable error.
     * <p/>
     * <p>This corresponds to the definition of "error" in section 1.2
     * of the W3C XML 1.0 Recommendation.  For example, a validating
     * parser would use this callback to report the violation of a
     * validity constraint.  The default behaviour is to take no
     * action.</p>
     * <p/>
     * <p>The SAX parser must continue to provide normal parsing events
     * after invoking this method: it should still be possible for the
     * application to process the document through to the end.  If the
     * application cannot do so, then the parser should report a fatal
     * error even if the XML 1.0 recommendation does not require it to
     * do so.</p>
     * <p/>
     * <p>Filters may use this method to report other, non-XML errors
     * as well.</p>
     *
     * @param exception The error information encapsulated in a
     *                  SAX parse exception.
     * @throws org.xml.sax.SAXException Any SAX exception, possibly
     *                                  wrapping another exception.
     * @see org.xml.sax.SAXParseException
     */
    public void error(SAXParseException exception) throws SAXException {
        StringBuilder sb = new StringBuilder();
        sb.append("Non-fatal XML Parsing error detected on line ")
                .append(exception.getLineNumber())
                .append("\n  Error message: ")
                .append(exception.getMessage());
        errorMessages.add(new ValidatorMessage(sb.toString(), MessageLevel.ERROR, new Context("Schema"), schemaRule));
    }

    /**
     * Receive notification of a non-recoverable error.
     * <p/>
     * <p>This corresponds to the definition of "fatal error" in
     * section 1.2 of the W3C XML 1.0 Recommendation.  For example, a
     * parser would use this callback to report the violation of a
     * well-formedness constraint.</p>
     * <p/>
     * <p>The application must assume that the document is unusable
     * after the parser has invoked this method, and should continue
     * (if at all) only for the sake of collecting addition error
     * messages: in fact, SAX parsers are free to stop reporting any
     * other events once this method has been invoked.</p>
     *
     * @param exception The error information encapsulated in a
     *                  SAX parse exception.
     * @throws org.xml.sax.SAXException Any SAX exception, possibly
     *                                  wrapping another exception.
     * @see org.xml.sax.SAXParseException
     */
    public void fatalError(SAXParseException exception) throws SAXException {
        StringBuilder sb = new StringBuilder();
        sb.append("FATAL XML Parsing error detected on line ")
                .append(exception.getLineNumber())
                .append("\n  Fatal Error message: ")
                .append(exception.getMessage());
        errorMessages.add(new ValidatorMessage(sb.toString(), MessageLevel.FATAL, new Context("Schema"), schemaRule));
    }

    /**
     * This is a extension method to the #fatalError(SAXParseException) method to
     * allow the handling of IOExceptions.
     * Example: An FileNotFoundException is thrown by the validate method if the file
     * to be validated contains a local reference to a DTD (which can not be
     * found on the server).
     *
     * @param exception the IOException that this handler is supposed to handle.
     */
    public void fatalError(IOException exception) {
        StringBuilder sb = new StringBuilder();
        sb.append("FATAL XML Validation error. ");
        if (exception instanceof FileNotFoundException) {
            String exceptionMsg = exception.getMessage();
            // we are only interested in the file name that is referenced not the full path
            // (since the full path contains the local part not necessary only the path specified in the XML)
            String sub = exceptionMsg.substring(exceptionMsg.lastIndexOf(File.separatorChar) + 1);
            sb.append("A needed or referenced File was not found!\n")
                    .append("  ").append(sub)
                    .append("\n  Hint: Please make sure your file does not reference local schema or DTD files.");
        } else {
            sb.append("An I/O error prevented the file from being validated!\n");
        }
        errorMessages.add(new ValidatorMessage(sb.toString(), MessageLevel.FATAL, new Context("Schema"), schemaRule));
    }

    /**
     * Receive notification of a warning.
     * <p/>
     * <p>SAX parsers will use this method to report conditions that
     * are not errors or fatal errors as defined by the XML 1.0
     * recommendation.  The default behaviour is to take no action.</p>
     * <p/>
     * <p>The SAX parser must continue to provide normal parsing events
     * after invoking this method: it should still be possible for the
     * application to process the document through to the end.</p>
     * <p/>
     * <p>Filters may use this method to report other, non-XML warnings
     * as well.</p>
     *
     * @param exception The warning information encapsulated in a
     *                  SAX parse exception.
     * @throws org.xml.sax.SAXException Any SAX exception, possibly
     *                                  wrapping another exception.
     * @see org.xml.sax.SAXParseException
     */
    public void warning(SAXParseException exception) throws SAXException {
        StringBuilder sb = new StringBuilder();
        sb.append("Warning: Validation of the XMl has detected the following condition on line ")
                .append(exception.getLineNumber())
                .append("\n  Warning message: ")
                .append(exception.getMessage());
        errorMessages.add(new ValidatorMessage(sb.toString(), MessageLevel.WARN, new Context("Schema"), schemaRule));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ReportValidationErrorHandler");
        sb.append("{errorMessages=").append(errorMessages);
        sb.append('}');
        return sb.toString();
    }

    private Rule schemaRule = new Rule() {
        /**
         * @return a String that should uniquely identify the rule
         */
        public String getId() {
            return "SCHEMA";
        }

        /**
         * @return a short descriptive name for the rule.
         */
        public String getName() {
            return "Schema Validation";
        }

        /**
         * @return a description of the rule. Ideally this should contain clues as to what the rules purpose is.
         */
        public String getDescription() {
            return "Schema Validation Rule";
        }

        /**
         * @return a collection of Strings that should make it easier
         *         for the user to determine why the rule failed and
         *         tips that can help in solving the problem.
         */
        public Collection<String> getHowToFixTips() {
            return Collections.emptyList();
        }
    };


}
