package uk.ac.ebi.pride.tools.converter.dao_omssa_txt.properties;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public enum ScoreCriteria {
    E_VALUE("E-value");

    private String name;

    private ScoreCriteria(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
