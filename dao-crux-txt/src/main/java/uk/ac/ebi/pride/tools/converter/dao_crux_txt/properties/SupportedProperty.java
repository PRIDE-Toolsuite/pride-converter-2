package uk.ac.ebi.pride.tools.converter.dao_crux_txt.properties;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public enum SupportedProperty {
    SPECTRUM_FILE("spectrum_file"),
    THRESHOLD("threshold"),
    DECOY_PREFIX("decoy_prefix"),
    MAX_RANK("max_rank");

    private String name;

    private SupportedProperty(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
