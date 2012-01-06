package uk.ac.ebi.pride.tools.converter.report.model;

import uk.ac.ebi.pride.tools.converter.report.io.xml.util.NonNegativeIntegerAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;


/**
 * <p>Java class for SequenceType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="SequenceType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="accession" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SequenceType", propOrder = {
        "value"
})
public class Sequence
        implements Serializable, ReportObject {

    private final static long serialVersionUID = 100L;
    @XmlValue
    protected String value;
    @XmlAttribute
    @XmlJavaTypeAdapter(NonNegativeIntegerAdapter.class)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected Long id;
    @XmlAttribute
    protected String accession;

    public Sequence() {
    }

    public Sequence(String value, long i, String accession) {
        this.value = value;
        this.id = i;
        this.accession = accession;
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

    /**
     * Gets the value of the id property.
     *
     * @return possible object is
     *         {@link long }
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value allowed object is
     *              {@link long }
     */
    public void setId(long value) {
        this.id = value;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Sequence");
        sb.append("{value='").append(value).append('\'');
        sb.append(", id=").append(id);
        sb.append(", accession='").append(accession).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
