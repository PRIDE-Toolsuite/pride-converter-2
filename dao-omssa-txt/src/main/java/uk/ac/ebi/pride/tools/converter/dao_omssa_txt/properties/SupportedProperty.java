package uk.ac.ebi.pride.tools.converter.dao_omssa_txt.properties;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public enum SupportedProperty {
    THRESHOLD("threshold"),
    SCORE_CRITERIA("score_criteria"),
    FIXED_PTMS("fixed_ptms"),
    MOD_FILE("mod_file"),
    USERMOD_FILE("usermod_file");

    private String name;

    private SupportedProperty(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
