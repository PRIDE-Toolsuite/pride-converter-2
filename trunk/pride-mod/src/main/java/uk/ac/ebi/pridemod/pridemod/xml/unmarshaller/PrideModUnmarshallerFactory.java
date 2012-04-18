package uk.ac.ebi.pridemod.pridemod.xml.unmarshaller;

import org.apache.log4j.Logger;
import uk.ac.ebi.pridemod.pridemod.model.ModelConstants;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * @author Florian Reisinger
 * @author rwang
 * @since 0.1
 */
public class PrideModUnmarshallerFactory {

    private static final Logger logger = Logger.getLogger(PrideModUnmarshallerFactory.class);

    private static PrideModUnmarshallerFactory instance = new PrideModUnmarshallerFactory();

    private static JAXBContext jc = null;

    private PrideModUnmarshallerFactory() {
    }

    public static PrideModUnmarshallerFactory getInstance() {
        return instance;
    }

    public Unmarshaller initializeUnmarshaller() {

        try {
            // Lazy caching of the JAXB Context.
            if (jc == null) {
                jc = JAXBContext.newInstance(ModelConstants.MODEL_PKG);
            }

            //create unmarshaller
            Unmarshaller pum = jc.createUnmarshaller();
            logger.info("Unmarshaller Initialized");

            return pum;

        } catch (JAXBException e) {
            logger.error("UnmarshallerFactory.initializeUnmarshaller", e);
            throw new IllegalStateException("Could not initialize unmarshaller", e);
        }
    }
}
