package uk.ac.ebi.pride.tools.converter.report.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Structure allowing the use of controlled or uncontrolled vocabulary
 * <p/>
 * <p>Java class for paramType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="paramType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cvParam" type="{}cvParamType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="userParam" type="{}userParamType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "paramType", propOrder = {
        "cvParam",
        "userParam"
})
@XmlSeeAlso({
        Description.class
})
public class Param
        implements Serializable, ReportObject {

    private final static long serialVersionUID = 100L;
    protected List<CvParam> cvParam;
    protected List<UserParam> userParam;

    /**
     * Gets the value of the cvParam property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cvParam property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCvParam().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link uk.ac.ebi.pride.tools.converter.report.model.CvParam }
     */
    public List<CvParam> getCvParam() {
        if (cvParam == null) {
            cvParam = new ArrayList<CvParam>();
        }
        return this.cvParam;
    }

    /**
     * Gets the value of the userParam property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the userParam property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUserParam().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link uk.ac.ebi.pride.tools.converter.report.model.UserParam }
     */
    public List<UserParam> getUserParam() {
        if (userParam == null) {
            userParam = new ArrayList<UserParam>();
        }
        return this.userParam;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Param");
        sb.append("{cvParam=").append(cvParam);
        sb.append(", userParam=").append(userParam);
        sb.append('}');
        return sb.toString();
    }
}
