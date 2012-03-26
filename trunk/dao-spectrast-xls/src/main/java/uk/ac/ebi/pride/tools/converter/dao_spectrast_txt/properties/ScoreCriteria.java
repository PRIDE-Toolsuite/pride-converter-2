package uk.ac.ebi.pride.tools.converter.dao_spectrast_txt.properties;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public enum ScoreCriteria {
    XCORR_RANK("xcorr_rank"),
    XCORR_SCORE("xcorr_score"),
    DELTA_CN("delta_cn");

    private String name;

    private ScoreCriteria(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
