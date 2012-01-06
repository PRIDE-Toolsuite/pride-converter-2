package uk.ac.ebi.pride.tools.converter.utils.xml;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 07/01/11
 * Time: 14:27
 * To change this template use File | Settings | File Templates.
 */
public class XMLUtils {

    private static Logger logger = Logger.getLogger(XMLUtils.class);

    public static SchemaValidationReport validateReportFile(String reportFile) {
        return validateReportFile(new File(reportFile));
    }

    public static SchemaValidationReport validateReportFile(File reportFile) {

        XMLUtils utils = new XMLUtils();
        URL schema = ReportObject.class.getClassLoader().getResource("reportfile.xsd");
        if (schema != null) {
            return utils.validateFile(reportFile, schema);
        } else {
            throw new IllegalStateException("Could not load schema for validation");
        }

    }

    private SchemaValidationReport validateFile(File xmlFile, URL schema) {

        SchemaValidationReport report = new SchemaValidationReport();
        try {

            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);

            SchemaFactory schemaFactory =
                    SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

            factory.setSchema(schemaFactory.newSchema(
                    new Source[]{new StreamSource(schema.openStream())}));

            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();

            reader.setErrorHandler(report);
            reader.parse(new InputSource(new FileReader(xmlFile)));

        } catch (IOException e) {
            report.fatalError(e);
        } catch (SAXParseException se) {
            report.fatalError(se);
        } catch (SAXException se) {
            report.fatalError(se);
        } catch (ParserConfigurationException e) {
            report.fatalError(e);
        }
        return report;

    }

}
