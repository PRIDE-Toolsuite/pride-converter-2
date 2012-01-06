package uk.ac.ebi.pride.tools.converter.dao.impl;

import uk.ac.ebi.pride.tools.converter.dao.DAO;
import uk.ac.ebi.pride.tools.converter.dao.DAOProperty;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 04/02/11
 * Time: 14:08
 * <p/>
 * This is the main interface for format-specific parsing. Each implementation is responsible to support as much of
 * desired functionality as possible. It is appreciated that not all formats will make the requested data items
 * available. In such cases, the methods should return null primitives and empty collections.
 * <p/>
 * If information is available and the methods are expected to return Param types, it is valid that the implementations
 * return UserParam objects for terms where the CvParam cannot be explicitely set at runtime. It will be the
 * responsibility of the user to inspect the report file generated and make certain that the information is correct
 * and, if possible, convert the UserParam data into the appropriate CvParams. In any case, the report formats will
 * undergo a validation step where missing or incorrect information will be flagged to the user before the full
 * parsing into PRIDE XML is executed.
 * </p>
 * The DAO must report all possible protein-to-peptide assignments. External tools will be available to update
 * the report file based on specific protein inference algorithms.
 * </p
 * >Note to all DAO implementers: DAO Implementing classes must extend the AbstractDAOImpl class and override all
 * methods defined in that class. All methods declared in AbstractDAOImpl will throw an UnsupportedOperationException,
 * unless documented otherwise.
 */
public abstract class AbstractDAOImpl implements DAO {

    /**
     * Used to retrieve the list of supported properties. Properties should nevertheless
     * be set using the setConfiguration method.
     *
     * @return A collection of supported properties.
     */
    public static Collection<DAOProperty> getSupportedPorperties() {
        throw new UnsupportedOperationException("Method getSupportedPorperties is not implemented for this DAO. " +
                "Please report this to the PRIDE team.");
    }

    /**
     * Converts the given collection of doubles into a
     * byte array (uses LITTLE_ENDIAN format).
     *
     * @param doubles
     * @return
     */
    static public byte[] doubleCollectionToByteArray(Collection<Double> doubles) {
        // allocate the memory for the bytes to store
        ByteBuffer bytes = ByteBuffer.allocate(doubles.size() * 8);
        bytes.order(ByteOrder.LITTLE_ENDIAN); // save everything in LITTLE ENDIAN format as it's the standard for mzML

        int index = 0;

        for (Double d : doubles) {
            bytes.putDouble(index, d);
            index += 8; // increment the counter by 8
        }

        // convert the ByteBuffer to a byte[]
        byte[] byteArray = new byte[doubles.size() * 8]; // allocate the memory for the byte[] array
        bytes.get(byteArray);

        return byteArray;
    }

    /**
     * Creates a PRIDE jaxb model CvParam
     *
     * @param cvLabel
     * @param accession
     * @param name
     * @param value
     * @return
     */
    static public uk.ac.ebi.pride.jaxb.model.CvParam jCvParam(String cvLabel, String accession, String name, Object value) {
        uk.ac.ebi.pride.jaxb.model.CvParam cvParam = new uk.ac.ebi.pride.jaxb.model.CvParam();
        cvParam.setCvLabel(cvLabel);
        cvParam.setAccession(accession);
        cvParam.setName(name);
        if (value != null)
            cvParam.setValue(value.toString());
        else
            cvParam.setValue("");

        return cvParam;
    }
}
