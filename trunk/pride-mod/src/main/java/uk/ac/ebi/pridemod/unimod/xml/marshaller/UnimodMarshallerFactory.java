package uk.ac.ebi.pridemod.unimod.xml.marshaller;

import org.apache.log4j.Logger;
import uk.ac.ebi.pridemod.unimod.model.ModelConstants;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 13-Aug-2010
 * Time: 14:15:35
 * To change this template use File | Settings | File Templates.
 */
public class UnimodMarshallerFactory {

    private static final Logger logger = Logger.getLogger(UnimodMarshallerFactory.class);

    private static UnimodMarshallerFactory instance = new UnimodMarshallerFactory();

    private static JAXBContext jc = null;

    private UnimodMarshallerFactory() {
    }

    public static UnimodMarshallerFactory getInstance() {
        return instance;
    }

    public Marshaller initializeMarshaller() {

        try {
            // Lazy caching of the JAXB Context.
            if (jc == null) {
                jc = JAXBContext.newInstance(ModelConstants.MODEL_PKG);
            }

            //create unmarshaller
            Marshaller pm = jc.createMarshaller();
            logger.info("Marshaller Initialized");

            return pm;

        } catch (JAXBException e) {
            logger.error("UnimodMarshaller.initializeMarshaller", e);
            throw new IllegalStateException("Could not initialize marshaller", e);
        }
    }
}
