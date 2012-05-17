package uk.ac.ebi.pride.tools.converter.dao_omssa_txt.properties;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public enum SupportedProperty {
    THRESHOLD("threshold"),
    SCORE_CRITERIA("score_criteria"),
    FIXED_PTMS("fixed_ptms"),
    VARIABLE_PTMS("variable_ptms");

    private String name;

    private SupportedProperty(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
