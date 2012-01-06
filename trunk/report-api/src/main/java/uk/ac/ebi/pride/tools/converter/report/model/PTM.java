package uk.ac.ebi.pride.tools.converter.report.model;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for PTMType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="PTMType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SearchEnginePTMLabel" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ModAccession" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ModDatabase" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ModDatabaseVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FixedModification" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ModMonoDelta" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ModAvgDelta" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Residues" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="additional" type="{}paramType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PTMType", propOrder = {
        "searchEnginePTMLabel",
        "modAccession",
        "modDatabase",
        "modDatabaseVersion",
        "fixedModification",
        "modMonoDelta",
        "modAvgDelta",
        "residues",
        "additional"
})
@XmlSeeAlso({
        PeptidePTM.class
})
public class PTM
        implements Serializable, ReportObject {

    private final static long serialVersionUID = 100L;
    @XmlElement(name = "SearchEnginePTMLabel", required = true)
    protected String searchEnginePTMLabel;
    @XmlElement(name = "ModAccession")
    protected String modAccession;
    @XmlElement(name = "ModDatabase")
    protected String modDatabase;
    @XmlElement(name = "ModDatabaseVersion")
    protected String modDatabaseVersion;
    @XmlElement(name = "FixedModification")
    protected Boolean fixedModification;
    @XmlElement(name = "ModMonoDelta")
    protected List<String> modMonoDelta;
    @XmlElement(name = "ModAvgDelta")
    protected List<String> modAvgDelta;
    @XmlElement(name = "Residues")
    protected String residues;
    @XmlElement(required = true)
    protected Param additional = new Param();

    /**
     * Gets the value of the searchEnginePTMLabel property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getSearchEnginePTMLabel() {
        return searchEnginePTMLabel;
    }

    /**
     * Sets the value of the searchEnginePTMLabel property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSearchEnginePTMLabel(String value) {
        this.searchEnginePTMLabel = value;
    }

    /**
     * Gets the value of the modAccession property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getModAccession() {
        return modAccession;
    }

    /**
     * Sets the value of the modAccession property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setModAccession(String value) {
        this.modAccession = value;
    }

    /**
     * Gets the value of the modDatabase property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getModDatabase() {
        return modDatabase;
    }

    /**
     * Sets the value of the modDatabase property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setModDatabase(String value) {
        this.modDatabase = value;
    }

    /**
     * Gets the value of the modDatabaseVersion property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getModDatabaseVersion() {
        return modDatabaseVersion;
    }

    /**
     * Sets the value of the modDatabaseVersion property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setModDatabaseVersion(String value) {
        this.modDatabaseVersion = value;
    }

    /**
     * Gets the value of the fixedModification property.
     *
     * @return possible object is
     *         {@link Boolean }
     */
    public Boolean isFixedModification() {
        return fixedModification;
    }

    /**
     * Sets the value of the fixedModification property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setFixedModification(Boolean value) {
        this.fixedModification = value;
    }

    /**
     * Gets the value of the modMonoDelta property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the modMonoDelta property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getModMonoDelta().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     */
    public List<String> getModMonoDelta() {
        if (modMonoDelta == null) {
            modMonoDelta = new ArrayList<String>();
        }
        return this.modMonoDelta;
    }

    /**
     * Gets the value of the modAvgDelta property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the modAvgDelta property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getModAvgDelta().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     */
    public List<String> getModAvgDelta() {
        if (modAvgDelta == null) {
            modAvgDelta = new ArrayList<String>();
        }
        return this.modAvgDelta;
    }

    /**
     * Gets the value of the residues property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getResidues() {
        return residues;
    }

    /**
     * Sets the value of the residues property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setResidues(String value) {
        this.residues = value;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        uk.ac.ebi.pride.tools.converter.report.model.PTM ptm = (uk.ac.ebi.pride.tools.converter.report.model.PTM) o;

        if (modAccession != null ? !modAccession.equals(ptm.modAccession) : ptm.modAccession != null) return false;
        if (modDatabase != null ? !modDatabase.equals(ptm.modDatabase) : ptm.modDatabase != null) return false;
        if (searchEnginePTMLabel != null ? !searchEnginePTMLabel.equals(ptm.searchEnginePTMLabel) : ptm.searchEnginePTMLabel != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = searchEnginePTMLabel != null ? searchEnginePTMLabel.hashCode() : 0;
        result = 31 * result + (modAccession != null ? modAccession.hashCode() : 0);
        result = 31 * result + (modDatabase != null ? modDatabase.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("PTM");
        sb.append("{searchEnginePTMLabel='").append(searchEnginePTMLabel).append('\'');
        sb.append(", modAccession='").append(modAccession).append('\'');
        sb.append(", modDatabase='").append(modDatabase).append('\'');
        sb.append(", modDatabaseVersion='").append(modDatabaseVersion).append('\'');
        sb.append(", fixedModification=").append(fixedModification);
        sb.append(", modMonoDelta=").append(modMonoDelta);
        sb.append(", modAvgDelta=").append(modAvgDelta);
        sb.append(", residues='").append(residues).append('\'');
        sb.append(", additional=").append(additional);
        sb.append('}');
        return sb.toString();
    }
}
