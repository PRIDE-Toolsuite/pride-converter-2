package uk.ac.ebi.pride.tools.converter.report.model;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * This object can hold the sequences of the identified proteins.
 * Sequences can then be referenced in the Identifications and will
 * be included in the PRIDE XML files.
 * <p/>
 * <p/>
 * <p>Java class for FastaType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="FastaType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Sequence" type="{}SequenceType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="sourceDb" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sourceDbVersion" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FastaType", propOrder = {
        "sequence"
})
public class Fasta
        implements Serializable, ReportObject {

    private final static long serialVersionUID = 100L;
    @XmlElement(name = "Sequence")
    protected List<Sequence> sequence;
    @XmlAttribute
    protected String sourceDb;
    @XmlAttribute
    protected String sourceDbVersion;

    /**
     * Gets the value of the sequence property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sequence property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSequence().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link uk.ac.ebi.pride.tools.converter.report.model.Sequence }
     */
    public List<Sequence> getSequence() {
        if (sequence == null) {
            sequence = new ArrayList<Sequence>();
        }
        return this.sequence;
    }

    /**
     * Gets the value of the sourceDb property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getSourceDb() {
        return sourceDb;
    }

    /**
     * Sets the value of the sourceDb property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSourceDb(String value) {
        this.sourceDb = value;
    }

    /**
     * Gets the value of the sourceDbVersion property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getSourceDbVersion() {
        return sourceDbVersion;
    }

    /**
     * Sets the value of the sourceDbVersion property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSourceDbVersion(String value) {
        this.sourceDbVersion = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Fasta");
        sb.append("{sourceDb='").append(sourceDb).append('\'');
        sb.append(", sourceDbVersion='").append(sourceDbVersion).append('\'');
        sb.append(", number of sequences=");
        sb.append((sequence == null) ? "0" : sequence.size());
        sb.append('}');
        return sb.toString();
    }
}
