package uk.ac.ebi.pride.tools.converter.report.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/**
 * A placeholder for fragment ion parameters.
 * <p/>
 * <p>Java class for FragmentIonType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="FragmentIonType">
 *   &lt;complexContent>
 *     &lt;extension base="{}paramType">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FragmentIonType")
public class FragmentIon
        extends Param
        implements Serializable {

    private final static long serialVersionUID = 100L;

}
