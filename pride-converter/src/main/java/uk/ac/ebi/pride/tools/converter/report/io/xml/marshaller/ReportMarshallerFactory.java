package uk.ac.ebi.pride.tools.converter.report.io.xml.marshaller;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.report.io.xml.util.EscapingXMLStreamWriter;
import uk.ac.ebi.pride.tools.converter.report.model.ModelConstants;
import uk.ac.ebi.pride.tools.converter.report.model.Report;
import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 15-Dec-2010
 * Time: 15:02:34
 * To change this template use File | Settings | File Templates.
 */
public class ReportMarshallerFactory {

    private static final Logger logger = Logger.getLogger(ReportMarshallerFactory.class);

    private static ReportMarshallerFactory instance = new ReportMarshallerFactory();
    private static JAXBContext jc = null;

    private ReportMarshallerFactory() {
    }

    public static ReportMarshallerFactory getInstance() {
        return instance;
    }

    public ReportMarshaller initializeMarshaller() {

        try {
            // Lazy caching of the JAXB Context.
            if (jc == null) {
                jc = JAXBContext.newInstance(ModelConstants.MODEL_PKG);
            }

            //create unmarshaller
            ReportMarshaller pm = new ReportMarshallerImpl();
            logger.info("Marshaller Initialized");

            return pm;

        } catch (JAXBException e) {
            logger.error("PrideXmlMarshaller.initializeMarshaller", e);
            throw new IllegalStateException("Could not initialize marshaller", e);
        }
    }

    private class ReportMarshallerImpl implements ReportMarshaller {

        private Marshaller marshaller = null;

        private ReportMarshallerImpl() throws JAXBException {
            marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        }

        public <T extends ReportObject> String marshall(T object) {
            StringWriter sw = new StringWriter();
            this.marshall(object, sw);
            return sw.toString();
        }

        public <T extends ReportObject> void marshall(T object, OutputStream os) {
            this.marshall(object, new OutputStreamWriter(os));
        }

        public <T extends ReportObject> void marshall(T object, Writer out) {

            if (object == null) {
                throw new IllegalArgumentException("Cannot marshall a NULL object");
            }

            try {

                // Set JAXB_FRAGMENT_PROPERTY to true for all objects that do not have
                // a @XmlRootElement annotation
                if (!(object instanceof Report)) {
                    marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                } else {
                    marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
                }

                QName aQName = ModelConstants.getQNameForClass(object.getClass());

                // before marshalling out, wrap in a Custom XMLStreamWriter
                // to fix a JAXB bug: http://java.net/jira/browse/JAXB-614
                // also wrapping in IndentingXMLStreamWriter to generate formatted XML
                XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
                IndentingXMLStreamWriter writer = new IndentingXMLStreamWriter(new EscapingXMLStreamWriter(xmlStreamWriter));
                marshaller.marshal(new JAXBElement(aQName, object.getClass(), object), writer);

            } catch (JAXBException e) {
                logger.error("ReportMarshaller.marshall", e);
                throw new IllegalStateException("Error while marshalling object:" + object.toString());
            } catch (XMLStreamException e) {
                logger.error("ReportMarshaller.marshall", e);
                throw new IllegalStateException("Error while marshalling object:" + object.toString());
            }

        }

    }

}
