package uk.ac.ebi.pride.tools.converter.report.model;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


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
 *         &lt;element name="SearchResultIdentifier" type="{}SearchResultIdentifierType"/>
 *         &lt;element name="Metadata" type="{}MetadataType"/>
 *         &lt;element name="Identifications">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Identification" type="{}IdentificationType" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Fasta" type="{}FastaType" minOccurs="0"/>
 *         &lt;element name="PTMs">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="PTM" type="{}PTMType" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DatabaseMappings">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="DatabaseMapping" type="{}DatabaseMappingType" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ConfigurationOptions" type="{}ConfigurationOptionsType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "searchResultIdentifier",
        "metadata",
        "identifications",
        "fasta",
        "ptMs",
        "databaseMappings",
        "configurationOptions"
})
@XmlRootElement(name = "Report")
public class Report
        implements Serializable, ReportObject {

    private final static long serialVersionUID = 100L;
    @XmlElement(name = "SearchResultIdentifier", required = true)
    protected SearchResultIdentifier searchResultIdentifier;
    @XmlElement(name = "Metadata", required = true)
    protected Metadata metadata;
    @XmlElement(name = "Identifications", required = true)
    protected uk.ac.ebi.pride.tools.converter.report.model.Report.Identifications identifications;
    @XmlElement(name = "Fasta")
    protected Fasta fasta;
    @XmlElement(name = "PTMs", required = true)
    protected uk.ac.ebi.pride.tools.converter.report.model.Report.PTMs ptMs;
    @XmlElement(name = "DatabaseMappings", required = true)
    protected uk.ac.ebi.pride.tools.converter.report.model.Report.DatabaseMappings databaseMappings;
    @XmlElement(name = "ConfigurationOptions")
    protected ConfigurationOptions configurationOptions;

    /**
     * Gets the value of the searchResultIdentifier property.
     *
     * @return possible object is
     *         {@link uk.ac.ebi.pride.tools.converter.report.model.SearchResultIdentifier }
     */
    public SearchResultIdentifier getSearchResultIdentifier() {
        return searchResultIdentifier;
    }

    /**
     * Sets the value of the searchResultIdentifier property.
     *
     * @param value allowed object is
     *              {@link uk.ac.ebi.pride.tools.converter.report.model.SearchResultIdentifier }
     */
    public void setSearchResultIdentifier(SearchResultIdentifier value) {
        this.searchResultIdentifier = value;
    }

    /**
     * Gets the value of the metadata property.
     *
     * @return possible object is
     *         {@link uk.ac.ebi.pride.tools.converter.report.model.Metadata }
     */
    public Metadata getMetadata() {
        return metadata;
    }

    /**
     * Sets the value of the metadata property.
     *
     * @param value allowed object is
     *              {@link uk.ac.ebi.pride.tools.converter.report.model.Metadata }
     */
    public void setMetadata(Metadata value) {
        this.metadata = value;
    }

    /**
     * Gets the value of the identifications property.
     *
     * @return possible object is
     *         {@link uk.ac.ebi.pride.tools.converter.report.model.Report.Identifications }
     */
    public uk.ac.ebi.pride.tools.converter.report.model.Report.Identifications getIdentifications() {
        return identifications;
    }

    /**
     * Sets the value of the identifications property.
     *
     * @param value allowed object is
     *              {@link uk.ac.ebi.pride.tools.converter.report.model.Report.Identifications }
     */
    public void setIdentifications(uk.ac.ebi.pride.tools.converter.report.model.Report.Identifications value) {
        this.identifications = value;
    }

    /**
     * Gets the value of the fasta property.
     *
     * @return possible object is
     *         {@link uk.ac.ebi.pride.tools.converter.report.model.Fasta }
     */
    public Fasta getFasta() {
        return fasta;
    }

    /**
     * Sets the value of the fasta property.
     *
     * @param value allowed object is
     *              {@link uk.ac.ebi.pride.tools.converter.report.model.Fasta }
     */
    public void setFasta(Fasta value) {
        this.fasta = value;
    }

    /**
     * Gets the value of the ptMs property.
     *
     * @return possible object is
     *         {@link uk.ac.ebi.pride.tools.converter.report.model.Report.PTMs }
     */
    public uk.ac.ebi.pride.tools.converter.report.model.Report.PTMs getPTMs() {
        return ptMs;
    }

    /**
     * Sets the value of the ptMs property.
     *
     * @param value allowed object is
     *              {@link uk.ac.ebi.pride.tools.converter.report.model.Report.PTMs }
     */
    public void setPTMs(uk.ac.ebi.pride.tools.converter.report.model.Report.PTMs value) {
        this.ptMs = value;
    }

    /**
     * Gets the value of the databaseMappings property.
     *
     * @return possible object is
     *         {@link uk.ac.ebi.pride.tools.converter.report.model.Report.DatabaseMappings }
     */
    public uk.ac.ebi.pride.tools.converter.report.model.Report.DatabaseMappings getDatabaseMappings() {
        return databaseMappings;
    }

    /**
     * Sets the value of the databaseMappings property.
     *
     * @param value allowed object is
     *              {@link uk.ac.ebi.pride.tools.converter.report.model.Report.DatabaseMappings }
     */
    public void setDatabaseMappings(uk.ac.ebi.pride.tools.converter.report.model.Report.DatabaseMappings value) {
        this.databaseMappings = value;
    }

    /**
     * Gets the value of the configurationOptions property.
     *
     * @return possible object is
     *         {@link uk.ac.ebi.pride.tools.converter.report.model.ConfigurationOptions }
     */
    public ConfigurationOptions getConfigurationOptions() {
        return configurationOptions;
    }

    /**
     * Sets the value of the configurationOptions property.
     *
     * @param value allowed object is
     *              {@link uk.ac.ebi.pride.tools.converter.report.model.ConfigurationOptions }
     */
    public void setConfigurationOptions(ConfigurationOptions value) {
        this.configurationOptions = value;
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
     *         &lt;element name="DatabaseMapping" type="{}DatabaseMappingType" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "databaseMapping"
    })
    public static class DatabaseMappings
            implements Serializable, ReportObject {

        private final static long serialVersionUID = 100L;
        @XmlElement(name = "DatabaseMapping")
        protected List<DatabaseMapping> databaseMapping;

        /**
         * Gets the value of the databaseMapping property.
         * <p/>
         * <p/>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the databaseMapping property.
         * <p/>
         * <p/>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDatabaseMapping().add(newItem);
         * </pre>
         * <p/>
         * <p/>
         * <p/>
         * Objects of the following type(s) are allowed in the list
         * {@link uk.ac.ebi.pride.tools.converter.report.model.DatabaseMapping }
         */
        public List<DatabaseMapping> getDatabaseMapping() {
            if (databaseMapping == null) {
                databaseMapping = new ArrayList<DatabaseMapping>();
            }
            return this.databaseMapping;
        }

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
     *         &lt;element name="Identification" type="{}IdentificationType" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "identification"
    })
    public static class Identifications
            implements Serializable, ReportObject {

        private final static long serialVersionUID = 100L;
        @XmlElement(name = "Identification")
        protected List<Identification> identification;

        /**
         * Gets the value of the identification property.
         * <p/>
         * <p/>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the identification property.
         * <p/>
         * <p/>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getIdentification().add(newItem);
         * </pre>
         * <p/>
         * <p/>
         * <p/>
         * Objects of the following type(s) are allowed in the list
         * {@link uk.ac.ebi.pride.tools.converter.report.model.Identification }
         */
        public List<Identification> getIdentification() {
            if (identification == null) {
                identification = new ArrayList<Identification>();
            }
            return this.identification;
        }

    }


    /**
     * The PTM's accession. This element has to
     * be set for a successful conversion.
     * <p/>
     * <p/>
     * <p>Java class for anonymous complex type.
     * <p/>
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p/>
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="PTM" type="{}PTMType" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "ptm"
    })
    public static class PTMs
            implements Serializable, ReportObject {

        private final static long serialVersionUID = 100L;
        @XmlElement(name = "PTM")
        protected List<PTM> ptm;

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
         * {@link uk.ac.ebi.pride.tools.converter.report.model.PTM }
         */
        public List<PTM> getPTM() {
            if (ptm == null) {
                ptm = new ArrayList<PTM>();
            }
            return this.ptm;
        }

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Report");
        sb.append("{searchResultIdentifier=").append(searchResultIdentifier);
        sb.append(", metadata=").append(metadata);
        sb.append(", identifications=").append(identifications);
        sb.append(", fasta=").append(fasta);
        sb.append(", ptMs=").append(ptMs);
        sb.append(", databaseMappings=").append(databaseMappings);
        sb.append(", configurationOptions=").append(configurationOptions);
        sb.append('}');
        return sb.toString();
    }

}
