package uk.ac.ebi.pride.tools.converter.report.model;

import uk.ac.ebi.pride.tools.converter.report.io.xml.util.NonNegativeIntegerAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;


/**
 * <p>Java class for PeptidePTMType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="PeptidePTMType">
 *   &lt;complexContent>
 *     &lt;extension base="{}PTMType">
 *       &lt;sequence>
 *         &lt;element name="ModLocation" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PeptidePTMType", propOrder = {
        "modLocation"
})
public class PeptidePTM
        extends PTM
        implements Serializable {

    private final static long serialVersionUID = 100L;
    @XmlElement(name = "ModLocation", required = true, type = String.class)
    @XmlJavaTypeAdapter(NonNegativeIntegerAdapter.class)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected Long modLocation;

    /**
     * Gets the value of the ptmPosition property.
     *
     * @return possible object is
     *         {@link long }
     */
    public long getModLocation() {
        return modLocation;
    }

    /**
     * Sets the value of the ptmPosition property.
     *
     * @param value allowed object is
     *              {@link long }
     */
    public void setModLocation(long value) {
        this.modLocation = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("PeptidePTM");
        sb.append("{modLocation=").append(modLocation);
        sb.append(", ");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
