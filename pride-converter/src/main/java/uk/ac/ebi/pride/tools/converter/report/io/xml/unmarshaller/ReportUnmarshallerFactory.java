package uk.ac.ebi.pride.tools.converter.report.io.xml.unmarshaller;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import uk.ac.ebi.pride.tools.converter.report.io.xml.util.EscapingXMLUtilities;
import uk.ac.ebi.pride.tools.converter.report.model.ModelConstants;
import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;
import java.io.StringReader;

/**
 * @author Florian Reisinger
 * @author rwang
 * @since 0.1
 */
public class ReportUnmarshallerFactory {

    private static final Logger logger = Logger.getLogger(ReportUnmarshallerFactory.class);

    private static ReportUnmarshallerFactory instance = new ReportUnmarshallerFactory();
    private static JAXBContext jc = null;

    private ReportUnmarshallerFactory() {
    }

    public static ReportUnmarshallerFactory getInstance() {
        return instance;
    }

    public ReportUnmarshaller initializeUnmarshaller() {

        try {
            // Lazy caching of the JAXB Context.
            if (jc == null) {
                jc = JAXBContext.newInstance(ModelConstants.MODEL_PKG);
            }

            //create unmarshaller
            ReportUnmarshaller pum = new ReportUnmarshallerImpl();
            logger.info("Unmarshaller Initialized");

            return pum;

        } catch (JAXBException e) {
            logger.error("UnmarshallerFactory.initializeUnmarshaller", e);
            throw new IllegalStateException("Could not initialize unmarshaller", e);
        }
    }

    private class ReportUnmarshallerImpl implements ReportUnmarshaller {

        private Unmarshaller unmarshaller = null;

        private ReportUnmarshallerImpl() throws JAXBException {
            unmarshaller = jc.createUnmarshaller();
        }

        /**
         * Add synchronization feature, unmarshaller is not thread safe by default.
         *
         * @param xmlSnippet raw xml string
         * @param cls        class type to unmarshall to.
         * @param <T>        an instance of class type.
         * @return T    return an instance of class type.
         */
        public synchronized <T extends ReportObject> T unmarshal(String xmlSnippet, Class cls) throws ConverterException {

            T retval;
            try {

                if (xmlSnippet == null || cls == null) {
                    return null;
                }

                String cleanXML = EscapingXMLUtilities.escapeCharacters(xmlSnippet);

                @SuppressWarnings("unchecked")
                JAXBElement<T> holder = unmarshaller.unmarshal(new SAXSource(new InputSource(new StringReader(cleanXML))), cls);
                retval = holder.getValue();

            } catch (JAXBException e) {
                throw new ConverterException("Error unmarshalling object: " + e.getMessage(), e);
            }

            return retval;

        }

    }
}
