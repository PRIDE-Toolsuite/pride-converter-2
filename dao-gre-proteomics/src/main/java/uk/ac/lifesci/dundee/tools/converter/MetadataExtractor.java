package uk.ac.lifesci.dundee.tools.converter;

import org.xml.sax.InputSource;
import uk.ac.ebi.pride.tools.converter.report.model.ModelConstants;
import uk.ac.ebi.pride.tools.converter.report.model.Report;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;
import java.io.StringReader;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 23/01/13
 * Time: 11:18
 */
public class MetadataExtractor {

    private JAXBContext jc = null;
    private Unmarshaller unmarshaller = null;
    private Report metadataReport;

    public MetadataExtractor(String reportXML) {
        try {
            jc = JAXBContext.newInstance(ModelConstants.MODEL_PKG);
            unmarshaller = jc.createUnmarshaller();
            metadataReport = unmarshal(reportXML);
        } catch (JAXBException e) {
            throw new ConverterException("Error initializing metadata reader", e);
        }
    }

    private Report unmarshal(String xmlSnippet) throws ConverterException {

        try {

            if (xmlSnippet == null) {
                return null;
            }

            @SuppressWarnings("unchecked")
            JAXBElement<Report> holder = unmarshaller.unmarshal(new SAXSource(new InputSource(new StringReader(xmlSnippet))), Report.class);
            return holder.getValue();

        } catch (JAXBException e) {
            throw new ConverterException("Error unmarshalling object: " + e.getMessage(), e);
        }

    }

    public Report getMetadataReport() {
        return metadataReport;
    }

}
