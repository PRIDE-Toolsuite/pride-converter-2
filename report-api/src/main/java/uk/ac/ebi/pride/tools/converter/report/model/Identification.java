package uk.ac.ebi.pride.tools.converter.report.model;

import uk.ac.ebi.pride.tools.converter.report.io.xml.util.NonNegativeIntegerAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>Java class for IdentificationType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="IdentificationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Accession" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CuratedAccession" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UniqueIdentifier" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AccessionVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SpliceIsoform" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Database" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DatabaseVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Peptide" type="{}PeptideType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="SpectrumReference" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="Score" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="Threshold" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="SearchEngine" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SequenceCoverage" type="{}CoverageType" minOccurs="0"/>
 *         &lt;element name="additional" type="{}paramType" minOccurs="0"/>
 *         &lt;element name="FastaSequenceReference" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="GelBasedData" type="{}GelBasedDataType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdentificationType", propOrder = {
        "accession",
        "curatedAccession",
        "uniqueIdentifier",
        "accessionVersion",
        "spliceIsoform",
        "database",
        "databaseVersion",
        "peptide",
        "spectrumReference",
        "score",
        "threshold",
        "searchEngine",
        "sequenceCoverage",
        "additional",
        "fastaSequenceReference",
        "gelBasedData"
})
public class Identification
        implements Serializable, ReportObject {

    private final static long serialVersionUID = 100L;
    @XmlElement(name = "Accession", required = true)
    protected String accession = "";
    @XmlElement(name = "CuratedAccession")
    protected String curatedAccession;
    @XmlElement(name = "UniqueIdentifier", required = true)
    protected String uniqueIdentifier = "";
    @XmlElement(name = "AccessionVersion")
    protected String accessionVersion;
    @XmlElement(name = "SpliceIsoform")
    protected String spliceIsoform;
    @XmlElement(name = "Database", required = true)
    protected String database;
    @XmlElement(name = "DatabaseVersion")
    protected String databaseVersion;
    @XmlElement(name = "Peptide")
    protected List<Peptide> peptide;
    @XmlElement(name = "SpectrumReference", type = String.class)
    @XmlJavaTypeAdapter(NonNegativeIntegerAdapter.class)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected Long spectrumReference;
    @XmlElement(name = "Score")
    protected Double score;
    @XmlElement(name = "Threshold")
    protected Double threshold;
    @XmlElement(name = "SearchEngine")
    protected String searchEngine;
    @XmlElement(name = "SequenceCoverage")
    protected Double sequenceCoverage;
    protected Param additional = new Param();
    @XmlElement(name = "FastaSequenceReference", type = String.class)
    @XmlJavaTypeAdapter(NonNegativeIntegerAdapter.class)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected Long fastaSequenceReference;
    @XmlElement(name = "GelBasedData")
    protected GelBasedData gelBasedData;

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

    /**
     * Gets the value of the curatedAccession property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getCuratedAccession() {
        return curatedAccession;
    }

    /**
     * Sets the value of the curatedAccession property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCuratedAccession(String value) {
        this.curatedAccession = value;
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
     * Gets the value of the accessionVersion property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getAccessionVersion() {
        return accessionVersion;
    }

    /**
     * Sets the value of the accessionVersion property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setAccessionVersion(String value) {
        this.accessionVersion = value;
    }

    /**
     * Gets the value of the spliceIsoform property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getSpliceIsoform() {
        return spliceIsoform;
    }

    /**
     * Sets the value of the spliceIsoform property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSpliceIsoform(String value) {
        this.spliceIsoform = value;
    }

    /**
     * Gets the value of the database property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Sets the value of the database property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDatabase(String value) {
        this.database = value;
    }

    /**
     * Gets the value of the databaseVersion property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getDatabaseVersion() {
        return databaseVersion;
    }

    /**
     * Sets the value of the databaseVersion property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDatabaseVersion(String value) {
        this.databaseVersion = value;
    }

    /**
     * Gets the value of the peptide property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the peptide property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPeptide().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link uk.ac.ebi.pride.tools.converter.report.model.Peptide }
     */
    public List<Peptide> getPeptide() {
        if (peptide == null) {
            peptide = new ArrayList<Peptide>();
        }
        return this.peptide;
    }

    /**
     * Gets the peptide with the given unique identifier from the list of peptides. Returns null if the
     * peptide is not found or if the uid is null.
     */
    public Peptide getPeptide(String uid) {

        if (uid == null) {
            return null;
        }

        Peptide retval = null;
        Iterator<Peptide> pepIt = getPeptide().iterator();
        while (pepIt.hasNext() && retval == null) {
            Peptide p = pepIt.next();
            if (uid.equals(p.getUniqueIdentifier())) {
                retval = p;
            }
        }
        return retval;

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
     * Gets the value of the score property.
     *
     * @return possible object is
     *         {@link Double }
     */
    public Double getScore() {
        return score;
    }

    /**
     * Sets the value of the score property.
     *
     * @param value allowed object is
     *              {@link Double }
     */
    public void setScore(Double value) {
        this.score = value;
    }

    /**
     * Gets the value of the threshold property.
     *
     * @return possible object is
     *         {@link Double }
     */
    public Double getThreshold() {
        return threshold;
    }

    /**
     * Sets the value of the threshold property.
     *
     * @param value allowed object is
     *              {@link Double }
     */
    public void setThreshold(Double value) {
        this.threshold = value;
    }

    /**
     * Gets the value of the searchEngine property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getSearchEngine() {
        return searchEngine;
    }

    /**
     * Sets the value of the searchEngine property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSearchEngine(String value) {
        this.searchEngine = value;
    }

    /**
     * Gets the value of the sequenceCoverage property.
     *
     * @return possible object is
     *         {@link Double }
     */
    public Double getSequenceCoverage() {
        return sequenceCoverage;
    }

    /**
     * Sets the value of the sequenceCoverage property.
     *
     * @param value allowed object is
     *              {@link Double }
     */
    public void setSequenceCoverage(Double value) {
        this.sequenceCoverage = value;
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
     * Gets the value of the fastaSequenceReference property.
     *
     * @return possible object is
     *         {@link long }
     */
    public Long getFastaSequenceReference() {
        return fastaSequenceReference;
    }

    /**
     * Sets the value of the fastaSequenceReference property.
     *
     * @param value allowed object is
     *              {@link long }
     */
    public void setFastaSequenceReference(long value) {
        this.fastaSequenceReference = value;
    }

    /**
     * Gets the value of the gelBasedData property.
     *
     * @return possible object is
     *         {@link uk.ac.ebi.pride.tools.converter.report.model.GelBasedData }
     */
    public GelBasedData getGelBasedData() {
        return gelBasedData;
    }

    /**
     * Sets the value of the gelBasedData property.
     *
     * @param value allowed object is
     *              {@link uk.ac.ebi.pride.tools.converter.report.model.GelBasedData }
     */
    public void setGelBasedData(GelBasedData value) {
        this.gelBasedData = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Identification");
        sb.append("{accession='").append(accession).append('\'');
        sb.append(", curatedAccession='").append(curatedAccession).append('\'');
        sb.append(", uniqueIdentifier='").append(uniqueIdentifier).append('\'');
        sb.append(", accessionVersion='").append(accessionVersion).append('\'');
        sb.append(", spliceIsoform='").append(spliceIsoform).append('\'');
        sb.append(", database='").append(database).append('\'');
        sb.append(", databaseVersion='").append(databaseVersion).append('\'');
        sb.append(", peptide=").append(peptide);
        sb.append(", spectrumReference=").append(spectrumReference);
        sb.append(", score=").append(score);
        sb.append(", threshold=").append(threshold);
        sb.append(", searchEngine='").append(searchEngine).append('\'');
        sb.append(", sequenceCoverage=").append(sequenceCoverage);
        sb.append(", additional=").append(additional);
        sb.append(", fastaSequenceReference=").append(fastaSequenceReference);
        sb.append(", gelBasedData=").append(gelBasedData);
        sb.append('}');
        return sb.toString();
    }
}
