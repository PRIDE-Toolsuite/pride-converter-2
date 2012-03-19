package uk.ac.ebi.pride.tools.converter.dao_crux_txt.filters;

import java.util.List;
import java.util.Map;

/**
 * Entries with xcorr score values higher than threshold will pass the filter.
 * @author Jose A. Dianes
 * @version $Id$
 */
public class XcorrScoreFilterCriteria extends FilterCriteria {

    @Override
    public boolean passFilter(Map<String, Integer> header, String[] values) {
        double threshold = (Double)(this.getThreshold());
        double score = Double.parseDouble(values[header.get("xcorr score")]);
        return (score >= threshold);
    }

    @Override
    public Object getHighestScore(Map<String, Integer> header, List<String> values) {
        
        if ((values == null) || (values.size() == 0)) return null;

        Double highest = 0.0;

        for (String value :  values) {
            String[] columns = value.split("\t");
            highest = Math.max(highest, Double.parseDouble(columns[header.get("xcorr score")]));
        }

        return highest;

    }
}
