package uk.ac.ebi.pride.tools.converter.dao;

import java.util.Collection;

/**
 * This class encapsulates a property that's supported
 * by the DAO. It is only meant to report properties,
 * not set them. Properties should still be set using the
 * DAO's setConfiguration method.
 *
 * @author jg
 * @param <TYPE>
 */
public class DAOProperty<TYPE> {
    /**
     * The property's name. This is the name to use in the Properties object passed using setConfiguration.
     */
    private final String name;
    /**
     * The property's default value.
     */
    private final TYPE defaultValue;
    /**
     * The property's minValue. Null if not applicable.
     */
    private TYPE minValue;
    /**
     * The property's maxValue. Null if not applicable.
     */
    private TYPE maxValue;
    /**
     * A String describing the property's functionality / implications.
     */
    private String description;
    /**
     * A shorter description that can be used as a tooltip.
     */
    private String shortDescription;
    /**
     * The unit used by this property. Null if not applicable
     */
    private String unit;
    /**
     * Collection of allowed values. Null if not applicable.
     */
    private Collection<TYPE> allowedValues;
    /**
     * Indicates that the given option is an advanced option.
     */
    private boolean isAdvanced = false;

    private Class clz = Object.class;

    /**
     * indicates if a property must be set for the DAO
     */
    private boolean isRequired = false;

    /**
     * The default constructor.
     *
     * @param name         The property's name.
     * @param defaultValue The property's default value. Null if not applicable.
     */
    public DAOProperty(String name, TYPE defaultValue) {
        super();
        this.name = name;
        this.defaultValue = defaultValue;
        if (defaultValue != null) {
            this.clz = defaultValue.getClass();
        }
    }

    /**
     * Constructor creating a property with min and max values.
     *
     * @param name         The property's name.
     * @param defaultValue The property's default value. Null if not applicable.
     * @param minValue     The property's min value.
     * @param maxValue     The property's max value.
     */
    public DAOProperty(String name, TYPE defaultValue,
                       TYPE minValue, TYPE maxValue) {
        super();
        this.name = name;
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        if (defaultValue != null) {
            this.clz = defaultValue.getClass();
        }
    }

    /**
     * Getter and setter functions. The returned Objects should by type cast to the respective
     * @return 
     */

    public String getName() {
        return name;
    }

    public TYPE getDefaultValue() {
        return defaultValue;
    }

    public TYPE getMinValue() {
        return minValue;
    }

    public void setMinValue(TYPE minValue) {
        this.minValue = minValue;
    }

    public TYPE getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(TYPE maxValue) {
        this.maxValue = maxValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Collection<TYPE> getAllowedValues() {
        return allowedValues;
    }

    public void setAllowedValues(Collection<TYPE> allowedValues) {
        this.allowedValues = allowedValues;
    }

    public Class getValueClass() {
        return clz;
    }

    public boolean isAdvanced() {
        return isAdvanced;
    }

    public void setAdvanced(boolean isAdvanced) {
        this.isAdvanced = isAdvanced;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }
}
