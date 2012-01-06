package uk.ac.ebi.pride.tools.converter.report.model;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;


/**
 * This identifier should uniquely identify a
 * certain search result. It is later used
 * during the conversion process to make sure
 * that a specific result file was created from
 * the passed search result.
 * <p/>
 * <p/>
 * <p>Java class for SearchResultIdentifierType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="SearchResultIdentifierType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="hash" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sourceFilePath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="timeCreated" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchResultIdentifierType", propOrder = {
        "hash",
        "sourceFilePath",
        "timeCreated"
})
public class SearchResultIdentifier
        implements Serializable, ReportObject {

    private final static long serialVersionUID = 100L;
    @XmlElement(required = true)
    protected String hash = "";
    @XmlElement(required = true)
    protected String sourceFilePath = "";
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timeCreated;

    /**
     * Gets the value of the hash property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getHash() {
        return hash;
    }

    /**
     * Sets the value of the hash property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setHash(String value) {
        this.hash = value;
    }

    /**
     * Gets the value of the sourceFilePath property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getSourceFilePath() {
        return sourceFilePath;
    }

    /**
     * Sets the value of the sourceFilePath property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSourceFilePath(String value) {
        this.sourceFilePath = value;
    }

    /**
     * Gets the value of the timeCreated property.
     *
     * @return possible object is
     *         {@link javax.xml.datatype.XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getTimeCreated() {
        return timeCreated;
    }

    /**
     * Sets the value of the timeCreated property.
     *
     * @param value allowed object is
     *              {@link javax.xml.datatype.XMLGregorianCalendar }
     */
    public void setTimeCreated(XMLGregorianCalendar value) {
        this.timeCreated = value;
    }

    /**
     * Sets the value of the timeCreated property.
     */
    public void setTimeCreated(String value) {
        try {
            this.timeCreated = DatatypeFactory.newInstance().newXMLGregorianCalendar(value);
        } catch (DatatypeConfigurationException e) {
            throw new IllegalArgumentException("Could not format completion time into proper calendar notation: " + value, e);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("SearchResultIdentifier");
        sb.append("{hash='").append(hash).append('\'');
        sb.append(", sourceFilePath='").append(sourceFilePath).append('\'');
        sb.append(", timeCreated=").append(timeCreated);
        sb.append('}');
        return sb.toString();
    }
}
