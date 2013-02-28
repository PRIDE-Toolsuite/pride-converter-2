package uk.ac.ebi.pride.tools.converter.report.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * <p>Java class for ConfigurationOptionsType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="ConfigurationOptionsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Option" type="{}OptionType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConfigurationOptionsType", propOrder = {
        "options"
})
public class ConfigurationOptions
        implements Serializable, ReportObject {

    private final static long serialVersionUID = 100L;
    @XmlElement(name = "Option")
    protected List<Option> options;

    public ConfigurationOptions() {
    }

    /**
     * Construct a ConfigurationOptions object based on a GuiProperty bundle
     *
     * @param options
     */
    public ConfigurationOptions(Properties options) {
        if (options != null) {
            for (Map.Entry entry : options.entrySet()) {
                Object key = entry.getKey();
                Object value = entry.getValue();
                //property key cannot be null, but value can.
                getOptions().add(new Option(key.toString(), (value != null) ? value.toString() : ""));
            }
        }
    }

    /**
     * Gets the value of the engineoptions property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the engineoptions property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOptions().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link uk.ac.ebi.pride.tools.converter.report.model.Option }
     */
    public List<Option> getOptions() {
        if (options == null) {
            options = new ArrayList<Option>();
        }
        return this.options;
    }

    /**
     * Returns the ConfigurationOptions as a Properties object
     *
     * @return a Properties object that contains all of the Options of this ConfigurationOptions object
     */
    public Properties asProperties() {
        Properties props = new Properties();
        for (Option o : getOptions()) {
            props.setProperty(o.getKey(), o.getValue());
        }
        return props;
    }
}
