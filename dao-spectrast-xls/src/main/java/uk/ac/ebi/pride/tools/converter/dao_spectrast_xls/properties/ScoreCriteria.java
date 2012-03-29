package uk.ac.ebi.pride.tools.converter.dao_spectrast_xls.properties;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public enum ScoreCriteria {
    FVAL("fval");

    private String name;

    private ScoreCriteria(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
