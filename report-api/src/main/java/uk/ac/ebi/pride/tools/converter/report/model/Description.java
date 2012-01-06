package uk.ac.ebi.pride.tools.converter.report.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/**
 * Extension of 'paramType' with an added free-text comment attribute.
 * <p/>
 * <p>Java class for descriptionType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="descriptionType">
 *   &lt;complexContent>
 *     &lt;extension base="{}paramType">
 *       &lt;attribute name="comment" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "descriptionType")
public class Description
        extends Param
        implements Serializable {

    private final static long serialVersionUID = 100L;
    @XmlAttribute
    protected String comment;

    /**
     * Gets the value of the comment property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setComment(String value) {
        this.comment = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Description");
        sb.append("{comment='").append(comment).append('\'');
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
