package uk.ac.ebi.pride.tools.converter.report.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/**
 * Defines a coordinate system for describing the position of a spot on a gel.
 * <p/>
 * <p/>
 * <p>Java class for Point complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="Point">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="XCoordinate" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="YCoordinate" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Point", propOrder = {
        "xCoordinate",
        "yCoordinate"
})
public class Point
        implements Serializable, ReportObject {

    private final static long serialVersionUID = 100L;
    @XmlElement(name = "XCoordinate")
    protected double xCoordinate = -1;
    @XmlElement(name = "YCoordinate")
    protected double yCoordinate = -1;

    /**
     * Gets the value of the xCoordinate property.
     */
    public double getXCoordinate() {
        return xCoordinate;
    }

    /**
     * Sets the value of the xCoordinate property.
     */
    public void setXCoordinate(double value) {
        this.xCoordinate = value;
    }

    /**
     * Gets the value of the yCoordinate property.
     */
    public double getYCoordinate() {
        return yCoordinate;
    }

    /**
     * Sets the value of the yCoordinate property.
     */
    public void setYCoordinate(double value) {
        this.yCoordinate = value;
    }

    public boolean isValid() {
        return (xCoordinate > 0 && yCoordinate > 0);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Point");
        sb.append("{xCoordinate=").append(xCoordinate);
        sb.append(", yCoordinate=").append(yCoordinate);
        sb.append('}');
        return sb.toString();
    }
}
