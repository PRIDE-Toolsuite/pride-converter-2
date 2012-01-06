package uk.ac.ebi.pride.tools.converter.report.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/**
 * Data type for operator identification information.
 * <p/>
 * <p>Java class for personType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="personType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="institution" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="contactInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "personType", propOrder = {
        "name",
        "institution",
        "contactInfo"
})
public class Contact
        implements Serializable, ReportObject {

    private final static long serialVersionUID = 100L;
    @XmlElement(required = true)
    protected String name = "";
    @XmlElement(required = true)
    protected String institution = "";
    protected String contactInfo = "";

    public Contact(String name, String institution, String contactInfo) {
        this.name = name;
        this.institution = institution;
        this.contactInfo = contactInfo;
    }

    public Contact() {
    }

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
     * Gets the value of the institution property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getInstitution() {
        return institution;
    }

    /**
     * Sets the value of the institution property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setInstitution(String value) {
        this.institution = value;
    }

    /**
     * Gets the value of the contactInfo property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getContactInfo() {
        return contactInfo;
    }

    /**
     * Sets the value of the contactInfo property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setContactInfo(String value) {
        this.contactInfo = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Contact");
        sb.append("{name='").append(name).append('\'');
        sb.append(", institution='").append(institution).append('\'');
        sb.append(", contactInfo='").append(contactInfo).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        uk.ac.ebi.pride.tools.converter.report.model.Contact contact = (uk.ac.ebi.pride.tools.converter.report.model.Contact) o;

        if (!contactInfo.equals(contact.contactInfo)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return contactInfo.hashCode();
    }
}
