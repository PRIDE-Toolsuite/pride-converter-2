package uk.ac.ebi.pride.tools.converter.dao_crux_txt.filters;

import java.util.List;
import java.util.Map;

/**
 * A filter for Crux-txt identifications. The passFilter method decides whether or not a given entry pass the filter.
 *
 * @author Jose A. Dianes
 * @version $Id$
 */
public abstract class FilterCriteria {
    private Object threshold;

    public Object getThreshold() {
        return threshold;
    }

    public void setThreshold(Object threshold) {
        this.threshold = threshold;
    }

    public abstract boolean passFilter(Map<String,Integer> header, String[] values);
    
    public abstract Object getHighestScore(Map<String,Integer> header, List<String> values);

}
