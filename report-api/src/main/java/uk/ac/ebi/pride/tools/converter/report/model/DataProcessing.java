package uk.ac.ebi.pride.tools.converter.report.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/**
 * Description of the software, and the way in which it was used to generate the peak list.
 * <p/>
 * <p/>
 * <p>Java class for dataProcessingType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="dataProcessingType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="software" type="{}softwareType"/>
 *         &lt;element name="processingMethod" type="{}paramType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dataProcessingType", propOrder = {
        "name",
        "software",
        "processingMethod"
})
public class DataProcessing
        implements Serializable, ReportObject {

    private final static long serialVersionUID = 100L;
    protected String name;
    @XmlElement(required = true)
    protected Software software;
    protected Param processingMethod = new Param();

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
     * Gets the value of the software property.
     *
     * @return possible object is
     *         {@link uk.ac.ebi.pride.tools.converter.report.model.Software }
     */
    public Software getSoftware() {
        return software;
    }

    /**
     * Sets the value of the software property.
     *
     * @param value allowed object is
     *              {@link uk.ac.ebi.pride.tools.converter.report.model.Software }
     */
    public void setSoftware(Software value) {
        this.software = value;
    }

    /**
     * Gets the value of the processingMethod property.
     *
     * @return possible object is
     *         {@link uk.ac.ebi.pride.tools.converter.report.model.Param }
     */
    public Param getProcessingMethod() {
        return processingMethod;
    }

    /**
     * Sets the value of the processingMethod property.
     *
     * @param value allowed object is
     *              {@link uk.ac.ebi.pride.tools.converter.report.model.Param }
     */
    public void setProcessingMethod(Param value) {
        this.processingMethod = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        uk.ac.ebi.pride.tools.converter.report.model.DataProcessing that = (uk.ac.ebi.pride.tools.converter.report.model.DataProcessing) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
