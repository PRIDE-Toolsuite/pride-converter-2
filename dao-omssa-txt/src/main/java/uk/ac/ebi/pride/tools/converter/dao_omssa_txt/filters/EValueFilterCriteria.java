package uk.ac.ebi.pride.tools.converter.dao_omssa_txt.filters;

import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.parsers.OmssaIdentificationsParser;

import java.util.List;
import java.util.Map;

/**
 * Entries with E-value score values higher than threshold will pass the filter.
 * @author Jose A. Dianes
 * @version $Id$
 */
public class EValueFilterCriteria extends FilterCriteria {

    @Override
    public boolean passFilter(Map<String, Integer> header, String[] values) {
        double threshold = (Double)(this.getThreshold());
        double score = Double.parseDouble(values[header.get(OmssaIdentificationsParser.E_VALUE_HEADER)]);
        return (score >= threshold);
    }

    @Override
    public Object getHighestScore(Map<String, Integer> header, List<String> values) {
        
        if ((values == null) || (values.size() == 0)) return null;

        Double highest = 0.0;

        for (String value :  values) {
            String[] columns = value.split("\t");
            highest = Math.max(highest, Double.parseDouble(columns[header.get(OmssaIdentificationsParser.E_VALUE_HEADER)]));
        }

        return highest;

    }
}
