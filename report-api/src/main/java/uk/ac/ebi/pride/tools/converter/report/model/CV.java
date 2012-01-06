package uk.ac.ebi.pride.tools.converter.report.model;

import javax.xml.bind.annotation.*;
import java.io.Serializable;


/**
 * Information about an ontology/CV source and a short 'lookup' tag to refer to.
 * <p/>
 * <p>Java class for cvLookupType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="cvLookupType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="cvLabel" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fullName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="address" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cvLookupType")
public class CV
        implements Serializable, ReportObject {

    private final static long serialVersionUID = 100L;
    @XmlAttribute(required = true)
    protected String cvLabel = "";
    @XmlAttribute
    protected String fullName = "";
    @XmlAttribute(required = true)
    protected String version = "";
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String address = "";

    public CV() {
    }

    public CV(String cvLabel, String fullName, String version, String address) {
        this.cvLabel = cvLabel;
        this.fullName = fullName;
        this.version = version;
        this.address = address;
    }

    /**
     * Gets the value of the cvLabel property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getCvLabel() {
        return cvLabel;
    }

    /**
     * Sets the value of the cvLabel property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCvLabel(String value) {
        this.cvLabel = value;
    }

    /**
     * Gets the value of the fullName property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the value of the fullName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setFullName(String value) {
        this.fullName = value;
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
     * Gets the value of the address property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setAddress(String value) {
        this.address = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CV");
        sb.append("{cvLabel='").append(cvLabel).append('\'');
        sb.append(", fullName='").append(fullName).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", address='").append(address).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
