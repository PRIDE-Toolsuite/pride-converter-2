package uk.ac.ebi.pride.tools.converter.report.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * The protocol element defines the sample processing steps that have been performed.
 * <p/>
 * <p/>
 * <p>Java class for ProtocolType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="ProtocolType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProtocolName">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ProtocolSteps" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="StepDescription" type="{}paramType" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProtocolType", propOrder = {
        "protocolName",
        "protocolSteps"
})
public class Protocol
        implements Serializable, ReportObject {

    private final static long serialVersionUID = 100L;
    @XmlElement(name = "ProtocolName", required = true)
    protected String protocolName = "";
    @XmlElement(name = "ProtocolSteps")
    protected uk.ac.ebi.pride.tools.converter.report.model.Protocol.ProtocolSteps protocolSteps;

    public Protocol() {
        protocolSteps = new ProtocolSteps();
        protocolSteps.getStepDescription();
    }

    /**
     * Gets the value of the protocolName property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getProtocolName() {
        return protocolName;
    }

    /**
     * Sets the value of the protocolName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setProtocolName(String value) {
        this.protocolName = value;
    }

    /**
     * Gets the value of the protocolSteps property.
     *
     * @return possible object is
     *         {@link uk.ac.ebi.pride.tools.converter.report.model.Protocol.ProtocolSteps }
     */
    public uk.ac.ebi.pride.tools.converter.report.model.Protocol.ProtocolSteps getProtocolSteps() {
        return protocolSteps;
    }

    /**
     * Sets the value of the protocolSteps property.
     *
     * @param value allowed object is
     *              {@link uk.ac.ebi.pride.tools.converter.report.model.Protocol.ProtocolSteps }
     */
    public void setProtocolSteps(uk.ac.ebi.pride.tools.converter.report.model.Protocol.ProtocolSteps value) {
        this.protocolSteps = value;
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
     *         &lt;element name="StepDescription" type="{}paramType" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "stepDescription"
    })
    public static class ProtocolSteps
            implements Serializable, ReportObject {

        private final static long serialVersionUID = 100L;
        @XmlElement(name = "StepDescription")
        protected List<Param> stepDescription;

        /**
         * Gets the value of the stepDescription property.
         * <p/>
         * <p/>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the stepDescription property.
         * <p/>
         * <p/>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getStepDescription().add(newItem);
         * </pre>
         * <p/>
         * <p/>
         * <p/>
         * Objects of the following type(s) are allowed in the list
         * {@link uk.ac.ebi.pride.tools.converter.report.model.Param }
         */
        public List<Param> getStepDescription() {
            if (stepDescription == null) {
                stepDescription = new ArrayList<Param>();
            }
            return this.stepDescription;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("ProtocolSteps");
            sb.append("{stepDescription=").append(stepDescription);
            sb.append('}');
            return sb.toString();
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Protocol");
        sb.append("{protocolName='").append(protocolName).append('\'');
        sb.append(", protocolSteps=").append(protocolSteps);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        uk.ac.ebi.pride.tools.converter.report.model.Protocol protocol = (uk.ac.ebi.pride.tools.converter.report.model.Protocol) o;

        if (!protocolName.equals(protocol.protocolName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return protocolName != null ? protocolName.hashCode() : 0;
    }
}
