package uk.ac.ebi.pride.tools.converter.report.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/**
 * <p>Java class for DatabaseMappingType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="DatabaseMappingType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SearchEngineDatabaseName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SearchEngineDatabaseVersion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CuratedDatabaseName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CuratedDatabaseVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatabaseMappingType", propOrder = {
        "searchEngineDatabaseName",
        "searchEngineDatabaseVersion",
        "curatedDatabaseName",
        "curatedDatabaseVersion"
})
public class DatabaseMapping
        implements Serializable, ReportObject {

    private final static long serialVersionUID = 100L;
    @XmlElement(name = "SearchEngineDatabaseName", required = true)
    protected String searchEngineDatabaseName;
    @XmlElement(name = "SearchEngineDatabaseVersion", required = true)
    protected String searchEngineDatabaseVersion;
    @XmlElement(name = "CuratedDatabaseName")
    protected String curatedDatabaseName;
    @XmlElement(name = "CuratedDatabaseVersion")
    protected String curatedDatabaseVersion;

    /**
     * Gets the value of the searchEngineDatabaseName property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getSearchEngineDatabaseName() {
        return searchEngineDatabaseName;
    }

    /**
     * Sets the value of the searchEngineDatabaseName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSearchEngineDatabaseName(String value) {
        this.searchEngineDatabaseName = value;
    }

    /**
     * Gets the value of the searchEngineDatabaseVersion property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getSearchEngineDatabaseVersion() {
        return searchEngineDatabaseVersion;
    }

    /**
     * Sets the value of the searchEngineDatabaseVersion property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSearchEngineDatabaseVersion(String value) {
        this.searchEngineDatabaseVersion = value;
    }

    /**
     * Gets the value of the curatedDatabaseName property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getCuratedDatabaseName() {
        return curatedDatabaseName;
    }

    /**
     * Sets the value of the curatedDatabaseName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCuratedDatabaseName(String value) {
        this.curatedDatabaseName = value;
    }

    /**
     * Gets the value of the curatedDatabaseVersion property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getCuratedDatabaseVersion() {
        return curatedDatabaseVersion;
    }

    /**
     * Sets the value of the curatedDatabaseVersion property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCuratedDatabaseVersion(String value) {
        this.curatedDatabaseVersion = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        uk.ac.ebi.pride.tools.converter.report.model.DatabaseMapping that = (uk.ac.ebi.pride.tools.converter.report.model.DatabaseMapping) o;

        if (searchEngineDatabaseName != null ? !searchEngineDatabaseName.equals(that.searchEngineDatabaseName) : that.searchEngineDatabaseName != null)
            return false;
        if (searchEngineDatabaseVersion != null ? !searchEngineDatabaseVersion.equals(that.searchEngineDatabaseVersion) : that.searchEngineDatabaseVersion != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = searchEngineDatabaseName != null ? searchEngineDatabaseName.hashCode() : 0;
        result = 31 * result + (searchEngineDatabaseVersion != null ? searchEngineDatabaseVersion.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("DatabaseMapping");
        sb.append("{searchEngineDatabaseName='").append(searchEngineDatabaseName).append('\'');
        sb.append(", searchEngineDatabaseVersion='").append(searchEngineDatabaseVersion).append('\'');
        sb.append(", curatedDatabaseName='").append(curatedDatabaseName).append('\'');
        sb.append(", curatedDatabaseVersion='").append(curatedDatabaseVersion).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
