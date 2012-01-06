package uk.ac.ebi.pride.tools.converter.report.model;

import uk.ac.ebi.pride.tools.converter.report.io.xml.util.NonNegativeIntegerAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for PeptideType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="PeptideType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Sequence" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CuratedSequence" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Start" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="End" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="SpectrumReference" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="isSpecific" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="UniqueIdentifier" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="additional" type="{}paramType" minOccurs="0"/>
 *         &lt;element name="PTM" type="{}PeptidePTMType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="FragmentIon" type="{}FragmentIonType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PeptideType", propOrder = {
        "sequence",
        "curatedSequence",
        "start",
        "end",
        "spectrumReference",
        "isSpecific",
        "uniqueIdentifier",
        "additional",
        "ptm",
        "fragmentIon"
})
public class Peptide
        implements Serializable, ReportObject {

    private final static long serialVersionUID = 100L;
    @XmlElement(name = "Sequence", required = true)
    protected String sequence = "";
    @XmlElement(name = "CuratedSequence", required = true)
    protected String curatedSequence = "";
    @XmlElement(name = "Start", type = String.class)
    @XmlJavaTypeAdapter(NonNegativeIntegerAdapter.class)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected Long start;
    @XmlElement(name = "End", type = String.class)
    @XmlJavaTypeAdapter(NonNegativeIntegerAdapter.class)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected Long end;
    @XmlElement(name = "SpectrumReference", type = String.class)
    @XmlJavaTypeAdapter(NonNegativeIntegerAdapter.class)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected Long spectrumReference;
    protected boolean isSpecific;
    @XmlElement(name = "UniqueIdentifier", required = true)
    protected String uniqueIdentifier = "";
    protected Param additional = new Param();
    @XmlElement(name = "PTM")
    protected List<PeptidePTM> ptm;
    @XmlElement(name = "FragmentIon")
    protected List<FragmentIon> fragmentIon;

    /**
     * Gets the value of the sequence property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Sets the value of the sequence property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSequence(String value) {
        this.sequence = value;
    }

    /**
     * Gets the value of the curatedSequence property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getCuratedSequence() {
        return curatedSequence;
    }

    /**
     * Sets the value of the curatedSequence property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCuratedSequence(String value) {
        this.curatedSequence = value;
    }

    /**
     * Gets the value of the start property.
     *
     * @return possible object is
     *         {@link long }
     */
    public long getStart() {
        return start;
    }

    /**
     * Sets the value of the start property.
     *
     * @param value allowed object is
     *              {@link long }
     */
    public void setStart(long value) {
        this.start = value;
    }

    /**
     * Gets the value of the end property.
     *
     * @return possible object is
     *         {@link long }
     */
    public long getEnd() {
        return end;
    }

    /**
     * Sets the value of the end property.
     *
     * @param value allowed object is
     *              {@link long }
     */
    public void setEnd(long value) {
        this.end = value;
    }

    /**
     * Gets the value of the spectrumReference property.
     *
     * @return possible object is
     *         {@link long }
     */
    public long getSpectrumReference() {
        return spectrumReference;
    }

    /**
     * Sets the value of the spectrumReference property.
     *
     * @param value allowed object is
     *              {@link long }
     */
    public void setSpectrumReference(long value) {
        this.spectrumReference = value;
    }

    /**
     * Gets the value of the isSpecific property.
     */
    public boolean isIsSpecific() {
        return isSpecific;
    }

    /**
     * Sets the value of the isSpecific property.
     */
    public void setIsSpecific(boolean value) {
        this.isSpecific = value;
    }

    /**
     * Gets the value of the uniqueIdentifier property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    /**
     * Sets the value of the uniqueIdentifier property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUniqueIdentifier(String value) {
        this.uniqueIdentifier = value;
    }

    /**
     * Gets the value of the additional property.
     *
     * @return possible object is
     *         {@link uk.ac.ebi.pride.tools.converter.report.model.Param }
     */
    public Param getAdditional() {
        return additional;
    }

    /**
     * Sets the value of the additional property.
     *
     * @param value allowed object is
     *              {@link uk.ac.ebi.pride.tools.converter.report.model.Param }
     */
    public void setAdditional(Param value) {
        this.additional = value;
    }

    /**
     * Gets the value of the ptm property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ptm property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPTM().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link uk.ac.ebi.pride.tools.converter.report.model.PeptidePTM }
     */
    public List<PeptidePTM> getPTM() {
        if (ptm == null) {
            ptm = new ArrayList<PeptidePTM>();
        }
        return this.ptm;
    }

    /**
     * Gets the value of the fragmentIon property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fragmentIon property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFragmentIon().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link uk.ac.ebi.pride.tools.converter.report.model.FragmentIon }
     */
    public List<FragmentIon> getFragmentIon() {
        if (fragmentIon == null) {
            fragmentIon = new ArrayList<FragmentIon>();
        }
        return this.fragmentIon;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Peptide");
        sb.append("{sequence='").append(sequence).append('\'');
        sb.append(", curatedSequence='").append(curatedSequence).append('\'');
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", spectrumReference=").append(spectrumReference);
        sb.append(", isSpecific=").append(isSpecific);
        sb.append(", uniqueIdentifier='").append(uniqueIdentifier).append('\'');
        sb.append(", additional=").append(additional);
        sb.append(", ptm=").append(ptm);
        sb.append('}');
        return sb.toString();
    }
}
