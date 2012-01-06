package uk.ac.ebi.pride.tools.converter.report.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/**
 * Parameters from a controlled vocbulary.
 * <p/>
 * <p>Java class for cvParamType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="cvParamType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="cvLabel" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="accession" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cvParamType")
public class CvParam
        implements Serializable, ReportObject {

    private final static long serialVersionUID = 100L;
    @XmlAttribute(required = true)
    protected String cvLabel;
    @XmlAttribute(required = true)
    protected String accession;
    @XmlAttribute(required = true)
    protected String name;
    @XmlAttribute
    protected String value;

    public CvParam() {
    }

    public CvParam(String cvLabel, String accession, String name, String value) {
        this.cvLabel = cvLabel;
        this.accession = accession;
        this.name = name;
        this.value = value;
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
     * Gets the value of the accession property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getAccession() {
        return accession;
    }

    /**
     * Sets the value of the accession property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setAccession(String value) {
        this.accession = value;
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
     * Gets the value of the value property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CvParam");
        sb.append("{cvLabel='").append(cvLabel).append('\'');
        sb.append(", accession='").append(accession).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", value='").append(value).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        uk.ac.ebi.pride.tools.converter.report.model.CvParam cvParam = (uk.ac.ebi.pride.tools.converter.report.model.CvParam) o;

        if (!accession.equals(cvParam.accession)) return false;
        if (!cvLabel.equals(cvParam.cvLabel)) return false;
        if (value != null ? !value.equals(cvParam.value) : cvParam.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = cvLabel.hashCode();
        result = 31 * result + accession.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
