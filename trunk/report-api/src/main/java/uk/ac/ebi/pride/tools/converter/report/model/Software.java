package uk.ac.ebi.pride.tools.converter.report.model;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;


/**
 * Software information (the software that produced the peak list).
 * <p/>
 * <p>Java class for softwareType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="softwareType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="comments" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="completionTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "softwareType", propOrder = {
        "name",
        "version",
        "comments"
})
public class Software
        implements Serializable, ReportObject {

    private final static long serialVersionUID = 100L;
    @XmlElement(required = true)
    protected String name = "";
    @XmlElement(required = true)
    protected String version = "";
    protected String comments;
    @XmlAttribute
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar completionTime;

    /**
     * Gets the value of the name property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the version property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the comments property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getComments() {
        return comments;
    }

    /**
     * Sets the value of the comments property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setComments(String value) {
        this.comments = value;
    }

    /**
     * Gets the value of the completionTime property.
     *
     * @return possible object is
     *         {@link javax.xml.datatype.XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getCompletionTime() {
        return completionTime;
    }

    /**
     * Sets the value of the completionTime property.
     *
     * @param value allowed object is
     *              {@link javax.xml.datatype.XMLGregorianCalendar }
     */
    public void setCompletionTime(XMLGregorianCalendar value) {
        this.completionTime = value;
    }

    /**
     * Sets the value of the completionTime property.
     */
    public void setCompletionTime(String value) {

        try {
            this.completionTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(value);
        } catch (DatatypeConfigurationException e) {
            throw new IllegalArgumentException("Could not format completion time into proper calendar notation: " + value, e);
        }

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Software");
        sb.append("{name='").append(name).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", comments='").append(comments).append('\'');
        sb.append(", completionTime=").append(completionTime);
        sb.append('}');
        return sb.toString();
    }
}
