package uk.ac.ebi.pride.tools.converter.report.model;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Description of the components of the mass spectrometer used
 * <p/>
 * <p>Java class for instrumentDescriptionType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="instrumentDescriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="instrumentName">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="source" type="{}paramType"/>
 *         &lt;element name="analyzerList">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="analyzer" type="{}paramType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="count" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="detector" type="{}paramType"/>
 *         &lt;element name="additional" type="{}paramType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "instrumentDescriptionType", propOrder = {
        "instrumentName",
        "source",
        "analyzerList",
        "detector",
        "additional"
})
public class InstrumentDescription
        implements Serializable, ReportObject {

    private final static long serialVersionUID = 100L;
    @XmlElement(required = true)
    protected String instrumentName = "";
    @XmlElement(required = true)
    protected Param source = new Param();
    @XmlElement(required = true)
    protected uk.ac.ebi.pride.tools.converter.report.model.InstrumentDescription.AnalyzerList analyzerList = new AnalyzerList();
    @XmlElement(required = true)
    protected Param detector = new Param();
    protected Param additional = new Param();

    /**
     * Gets the value of the instrumentName property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getInstrumentName() {
        return instrumentName;
    }

    /**
     * Sets the value of the instrumentName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setInstrumentName(String value) {
        this.instrumentName = value;
    }

    /**
     * Gets the value of the source property.
     *
     * @return possible object is
     *         {@link uk.ac.ebi.pride.tools.converter.report.model.Param }
     */
    public Param getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     *
     * @param value allowed object is
     *              {@link uk.ac.ebi.pride.tools.converter.report.model.Param }
     */
    public void setSource(Param value) {
        this.source = value;
    }

    /**
     * Gets the value of the analyzerList property.
     *
     * @return possible object is
     *         {@link uk.ac.ebi.pride.tools.converter.report.model.InstrumentDescription.AnalyzerList }
     */
    public uk.ac.ebi.pride.tools.converter.report.model.InstrumentDescription.AnalyzerList getAnalyzerList() {
        return analyzerList;
    }

    /**
     * Sets the value of the analyzerList property.
     *
     * @param value allowed object is
     *              {@link uk.ac.ebi.pride.tools.converter.report.model.InstrumentDescription.AnalyzerList }
     */
    public void setAnalyzerList(uk.ac.ebi.pride.tools.converter.report.model.InstrumentDescription.AnalyzerList value) {
        this.analyzerList = value;
    }

    /**
     * Gets the value of the detector property.
     *
     * @return possible object is
     *         {@link uk.ac.ebi.pride.tools.converter.report.model.Param }
     */
    public Param getDetector() {
        return detector;
    }

    /**
     * Sets the value of the detector property.
     *
     * @param value allowed object is
     *              {@link uk.ac.ebi.pride.tools.converter.report.model.Param }
     */
    public void setDetector(Param value) {
        this.detector = value;
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
     * <p>Java class for anonymous complex type.
     * <p/>
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p/>
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="analyzer" type="{}paramType" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *       &lt;attribute name="count" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "analyzer"
    })
    public static class AnalyzerList
            implements Serializable, ReportObject {

        private final static long serialVersionUID = 100L;
        @XmlElement(required = true)
        protected List<Param> analyzer = new ArrayList<Param>();
        @XmlAttribute(required = true)
        protected int count = 0;

        /**
         * Gets the value of the analyzer property.
         * <p/>
         * <p/>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the analyzer property.
         * <p/>
         * <p/>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAnalyzer().add(newItem);
         * </pre>
         * <p/>
         * <p/>
         * <p/>
         * Objects of the following type(s) are allowed in the list
         * {@link uk.ac.ebi.pride.tools.converter.report.model.Param }
         */
        public List<Param> getAnalyzer() {
            if (analyzer == null) {
                analyzer = new ArrayList<Param>();
            }
            return this.analyzer;
        }

        /**
         * Gets the value of the count property.
         */
        public int getCount() {
            return count;
        }

        /**
         * Sets the value of the count property.
         */
        public void setCount(int value) {
            this.count = value;
        }

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("InstrumentDescription");
        sb.append("{instrumentName='").append(instrumentName).append('\'');
        sb.append(", source=").append(source);
        sb.append(", analyzerList=").append(analyzerList);
        sb.append(", detector=").append(detector);
        sb.append(", additional=").append(additional);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        uk.ac.ebi.pride.tools.converter.report.model.InstrumentDescription that = (uk.ac.ebi.pride.tools.converter.report.model.InstrumentDescription) o;

        if (!instrumentName.equals(that.instrumentName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return instrumentName.hashCode();
    }
}
