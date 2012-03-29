package uk.ac.ebi.pride.tools.converter.dao_spectrast_xls.properties;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public enum SupportedProperty {
    THRESHOLD("threshold"),
    SCORE_CRITERIA("score_criteria");

    private String name;

    private SupportedProperty(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
