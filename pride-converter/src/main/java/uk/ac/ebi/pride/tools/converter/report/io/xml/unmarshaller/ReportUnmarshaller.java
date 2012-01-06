package uk.ac.ebi.pride.tools.converter.report.io.xml.unmarshaller;

import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;

/**
 * PrideXMLUnmarshaller is responsible for unmarshallering raw xml strings into
 * JAXB objects.
 *
 * @author Florian Reisinger
 * @since 0.1
 */
public interface ReportUnmarshaller {

    public <T extends ReportObject> T unmarshal(String xmlSnippet, Class cls) throws ConverterException;

}
