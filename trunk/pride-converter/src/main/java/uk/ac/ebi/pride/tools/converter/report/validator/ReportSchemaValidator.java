package uk.ac.ebi.pride.tools.converter.report.validator;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 26/09/11
 * Time: 10:11
 */
public class ReportSchemaValidator {

    /**
     * This static object is used to create the Schema object
     * used for validation.
     */
    private static final SchemaFactory SCHEMA_FACTORY = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

    private static final URL schemaURL = ReportSchemaValidator.class.getClassLoader().getResource("reportfile.xsd");

    /**
     * This method carries out the work of validating the XML file passed in through
     * 'inputStream' against the compiled XML schema 'schema'.  This method is a helper
     * method called by the implementation of this abstract class.
     *
     * @param reader being a java.io.Reader from the complete XML file being validated.
     * @param schema being a compiled schema object built from the appropriate xsd (
     *               performed by the implementing sub-class of this abstract class.)
     * @return an PrideValidationErrorHandler that can be queried for details of any
     *         parsing errors in ValidatorMessage format (convenient for Validator framework).
     * @throws org.xml.sax.SAXException in case of a SAX related error.
     */
    protected ReportValidationErrorHandler validate(Reader reader, Schema schema)
            throws SAXException {

        final ReportValidationErrorHandler reportValidationErrorHandler = new ReportValidationErrorHandler();
        Validator validator = schema.newValidator();
        validator.setErrorHandler(reportValidationErrorHandler);
        try {
            validator.validate(new SAXSource(new InputSource(reader)));
        } catch (IOException ioe) {
            reportValidationErrorHandler.fatalError(ioe);
        } catch (SAXParseException spe) {
            reportValidationErrorHandler.fatalError(spe);
        }
        return reportValidationErrorHandler;
    }

    /**
     * This method must be implemented to create a suitable Schema object for the
     * xsd file in question.
     *
     * @param reader the XML file being validated as a Stream (Reader)
     * @return an PrideValidationErrorHandler that can be queried to return all of the
     *         error in ValidatorMessage format.
     * @throws org.xml.sax.SAXException in case of SAX related error.
     */
    public ReportValidationErrorHandler validate(Reader reader) throws SAXException {
        if (schemaURL == null) {
            throw new IllegalStateException("Report Schema file not found!");
        }
        Schema schema = SCHEMA_FACTORY.newSchema(schemaURL);
        return validate(reader, schema);
    }

}
