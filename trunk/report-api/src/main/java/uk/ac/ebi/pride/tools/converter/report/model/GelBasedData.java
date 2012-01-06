package uk.ac.ebi.pride.tools.converter.report.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/**
 * <p>Java class for GelBasedDataType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="GelBasedDataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Gel" type="{}SimpleGel"/>
 *         &lt;element name="GelLocation" type="{}Point"/>
 *         &lt;element name="MolecularWeight" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="pI" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GelBasedDataType", propOrder = {
        "gel",
        "gelLocation",
        "molecularWeight",
        "pi"
})
public class GelBasedData
        implements Serializable, ReportObject {

    private final static long serialVersionUID = 100L;
    @XmlElement(name = "Gel", required = true)
    protected SimpleGel gel;
    @XmlElement(name = "GelLocation", required = true)
    protected Point gelLocation;
    @XmlElement(name = "MolecularWeight")
    protected double molecularWeight = -1;
    @XmlElement(name = "pI")
    protected double pi = -1;

    /**
     * Gets the value of the gel property.
     *
     * @return possible object is
     *         {@link uk.ac.ebi.pride.tools.converter.report.model.SimpleGel }
     */
    public SimpleGel getGel() {
        return gel;
    }

    /**
     * Sets the value of the gel property.
     *
     * @param value allowed object is
     *              {@link uk.ac.ebi.pride.tools.converter.report.model.SimpleGel }
     */
    public void setGel(SimpleGel value) {
        this.gel = value;
    }

    /**
     * Gets the value of the gelLocation property.
     *
     * @return possible object is
     *         {@link uk.ac.ebi.pride.tools.converter.report.model.Point }
     */
    public Point getGelLocation() {
        return gelLocation;
    }

    /**
     * Sets the value of the gelLocation property.
     *
     * @param value allowed object is
     *              {@link uk.ac.ebi.pride.tools.converter.report.model.Point }
     */
    public void setGelLocation(Point value) {
        this.gelLocation = value;
    }

    /**
     * Gets the value of the molecularWeight property.
     */
    public double getMolecularWeight() {
        return molecularWeight;
    }

    /**
     * Sets the value of the molecularWeight property.
     */
    public void setMolecularWeight(double value) {
        this.molecularWeight = value;
    }

    /**
     * Gets the value of the pi property.
     */
    public double getPI() {
        return pi;
    }

    /**
     * Sets the value of the pi property.
     */
    public void setPI(double value) {
        this.pi = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("GelBasedData");
        sb.append("{gel=").append(gel);
        sb.append(", gelLocation=").append(gelLocation);
        sb.append(", molecularWeight=").append(molecularWeight);
        sb.append(", pi=").append(pi);
        sb.append('}');
        return sb.toString();
    }
}
