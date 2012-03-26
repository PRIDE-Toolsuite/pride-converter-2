package uk.ac.ebi.pride.tools.converter.dao_spectrast_xls.properties;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public enum SupportedProperty {
    SPECTRUM_FILE("spectrum_file"),
    THRESHOLD("threshold"),
    DECOY_PREFIX("decoy_prefix"),
    GET_HIGHEST_SCORE_ITEM("get_highest_score_item"),
    SCORE_CRITERIA("score_criteria");

    private String name;

    private SupportedProperty(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
