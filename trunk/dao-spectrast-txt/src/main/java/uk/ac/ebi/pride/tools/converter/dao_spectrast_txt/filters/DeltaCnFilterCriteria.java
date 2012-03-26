package uk.ac.ebi.pride.tools.converter.dao_spectrast_txt.filters;

import java.util.List;
import java.util.Map;

/**
 * Entries with deltaCn values lower than threshold will pass the filter.
 * @author Jose A. Dianes
 * @version $Id$
 */
public class DeltaCnFilterCriteria extends FilterCriteria {

    @Override
    public boolean passFilter(Map<String, Integer> header, String[] values) {
        double threshold = (Double)(this.getThreshold());
        double delta = Double.parseDouble(values[header.get("delta_cn")]);
        return (delta <= threshold);
    }

    @Override
    public Object getHighestScore(Map<String, Integer> header, List<String> values) {

        if ((values == null) || (values.size() == 0)) return null;

        Double highest = 0.0;

        for (String value :  values) {
            String[] columns = value.split("\t");
            highest = Math.min(highest, Double.parseDouble(columns[header.get("delta_cn")]));
        }

        return highest;

    }
}
