package uk.ac.ebi.pride.tools.converter.report.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * This objects holds the required metadata of the performed search / experiment. This object
 * is expected to be completed after prescan.
 * <p/>
 * <p/>
 * <p>Java class for MetadataType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="MetadataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ExperimentAccession" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Title" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Reference" type="{}ReferenceType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ShortLabel" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Protocol" type="{}ProtocolType"/>
 *         &lt;element name="MzDataDescription">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="cvLookup" type="{}cvLookupType" maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;element name="admin" type="{}adminType"/>
 *                   &lt;element name="instrument" type="{}instrumentDescriptionType"/>
 *                   &lt;element name="dataProcessing" type="{}dataProcessingType"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ExperimentAdditional" type="{}paramType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataType", propOrder = {
        "experimentAccession",
        "title",
        "reference",
        "shortLabel",
        "protocol",
        "mzDataDescription",
        "experimentAdditional"
})
public class Metadata
        implements Serializable, ReportObject {

    private final static long serialVersionUID = 100L;
    @XmlElement(name = "ExperimentAccession")
    protected String experimentAccession;
    @XmlElement(name = "Title", required = true)
    protected String title = "";
    @XmlElement(name = "Reference")
    protected List<Reference> reference;
    @XmlElement(name = "ShortLabel", required = true)
    protected String shortLabel = "";
    @XmlElement(name = "Protocol", required = true)
    protected Protocol protocol = new Protocol();
    @XmlElement(name = "MzDataDescription", required = true)
    protected uk.ac.ebi.pride.tools.converter.report.model.Metadata.MzDataDescription mzDataDescription = new MzDataDescription();
    @XmlElement(name = "ExperimentAdditional", required = true)
    protected Param experimentAdditional = new Param();

    /**
     * Gets the value of the experimentAccession property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getExperimentAccession() {
        return experimentAccession;
    }

    /**
     * Sets the value of the experimentAccession property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setExperimentAccession(String value) {
        this.experimentAccession = value;
    }

    /**
     * Gets the value of the title property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the reference property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reference property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReference().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link uk.ac.ebi.pride.tools.converter.report.model.Reference }
     */
    public List<Reference> getReference() {
        if (reference == null) {
            reference = new ArrayList<Reference>();
        }
        return this.reference;
    }

    /**
     * Gets the value of the shortLabel property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getShortLabel() {
        return shortLabel;
    }

    /**
     * Sets the value of the shortLabel property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setShortLabel(String value) {
        this.shortLabel = value;
    }

    /**
     * Gets the value of the protocol property.
     *
     * @return possible object is
     *         {@link uk.ac.ebi.pride.tools.converter.report.model.Protocol }
     */
    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * Sets the value of the protocol property.
     *
     * @param value allowed object is
     *              {@link uk.ac.ebi.pride.tools.converter.report.model.Protocol }
     */
    public void setProtocol(Protocol value) {
        this.protocol = value;
    }

    /**
     * Gets the value of the mzDataDescription property.
     *
     * @return possible object is
     *         {@link uk.ac.ebi.pride.tools.converter.report.model.Metadata.MzDataDescription }
     */
    public uk.ac.ebi.pride.tools.converter.report.model.Metadata.MzDataDescription getMzDataDescription() {
        return mzDataDescription;
    }

    /**
     * Sets the value of the mzDataDescription property.
     *
     * @param value allowed object is
     *              {@link uk.ac.ebi.pride.tools.converter.report.model.Metadata.MzDataDescription }
     */
    public void setMzDataDescription(uk.ac.ebi.pride.tools.converter.report.model.Metadata.MzDataDescription value) {
        this.mzDataDescription = value;
    }

    /**
     * Gets the value of the experimentAdditional property.
     *
     * @return possible object is
     *         {@link uk.ac.ebi.pride.tools.converter.report.model.Param }
     */
    public Param getExperimentAdditional() {
        return experimentAdditional;
    }

    /**
     * Sets the value of the experimentAdditional property.
     *
     * @param value allowed object is
     *              {@link uk.ac.ebi.pride.tools.converter.report.model.Param }
     */
    public void setExperimentAdditional(Param value) {
        this.experimentAdditional = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * <p/>
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p/>
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="cvLookup" type="{}cvLookupType" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element name="admin" type="{}adminType"/>
     *         &lt;element name="instrument" type="{}instrumentDescriptionType"/>
     *         &lt;element name="dataProcessing" type="{}dataProcessingType"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "cvLookup",
            "admin",
            "instrument",
            "dataProcessing"
    })
    public static class MzDataDescription
            implements Serializable, ReportObject {

        private final static long serialVersionUID = 100L;
        protected List<CV> cvLookup;
        @XmlElement(required = true)
        protected Admin admin = new Admin();
        @XmlElement(required = true)
        protected InstrumentDescription instrument = new InstrumentDescription();
        @XmlElement(required = true)
        protected DataProcessing dataProcessing = new DataProcessing();

        /**
         * Gets the value of the cvLookup property.
         * <p/>
         * <p/>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the cvLookup property.
         * <p/>
         * <p/>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCvLookup().add(newItem);
         * </pre>
         * <p/>
         * <p/>
         * <p/>
         * Objects of the following type(s) are allowed in the list
         * {@link uk.ac.ebi.pride.tools.converter.report.model.CV }
         */
        public List<CV> getCvLookup() {
            if (cvLookup == null) {
                cvLookup = new ArrayList<CV>();
            }
            return this.cvLookup;
        }

        /**
         * Gets the value of the admin property.
         *
         * @return possible object is
         *         {@link uk.ac.ebi.pride.tools.converter.report.model.Admin }
         */
        public Admin getAdmin() {
            return admin;
        }

        /**
         * Sets the value of the admin property.
         *
         * @param value allowed object is
         *              {@link uk.ac.ebi.pride.tools.converter.report.model.Admin }
         */
        public void setAdmin(Admin value) {
            this.admin = value;
        }

        /**
         * Gets the value of the instrument property.
         *
         * @return possible object is
         *         {@link uk.ac.ebi.pride.tools.converter.report.model.InstrumentDescription }
         */
        public InstrumentDescription getInstrument() {
            return instrument;
        }

        /**
         * Sets the value of the instrument property.
         *
         * @param value allowed object is
         *              {@link uk.ac.ebi.pride.tools.converter.report.model.InstrumentDescription }
         */
        public void setInstrument(InstrumentDescription value) {
            this.instrument = value;
        }

        /**
         * Gets the value of the dataProcessing property.
         *
         * @return possible object is
         *         {@link uk.ac.ebi.pride.tools.converter.report.model.DataProcessing }
         */
        public DataProcessing getDataProcessing() {
            return dataProcessing;
        }

        /**
         * Sets the value of the dataProcessing property.
         *
         * @param value allowed object is
         *              {@link uk.ac.ebi.pride.tools.converter.report.model.DataProcessing }
         */
        public void setDataProcessing(DataProcessing value) {
            this.dataProcessing = value;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("MzDataDescription");
            sb.append("{cvLookup=").append(cvLookup);
            sb.append(", admin=").append(admin);
            sb.append(", instrument=").append(instrument);
            sb.append(", dataProcessing=").append(dataProcessing);
            sb.append('}');
            return sb.toString();
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Metadata");
        sb.append("{experimentAccession='").append(experimentAccession).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", reference=").append(reference);
        sb.append(", shortLabel='").append(shortLabel).append('\'');
        sb.append(", protocol=").append(protocol);
        sb.append(", mzDataDescription=").append(mzDataDescription);
        sb.append(", experimentAdditional=").append(experimentAdditional);
        sb.append('}');
        return sb.toString();
    }
}
