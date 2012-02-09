package uk.ac.ebi.pride.tools.filter.model.impl;

import uk.ac.ebi.pride.jaxb.model.Identification;
import uk.ac.ebi.pride.tools.filter.model.Filter;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 11/03/11
 * Time: 16:41
 */
public class MinimumIdentificationScoreFilter implements Filter<Identification> {

    private double minScore;

    public MinimumIdentificationScoreFilter(double minScore) {
        this.minScore = minScore;
    }

    @Override
    /**
     * Filter an object based on the underlying implementation requirements. This method
     * will return true if an object is to be filtered (i.e. excluded from a given task) and
     * false if it is not to be excluded (i.e. it is a valid object, based on the implementation
     * requirements).
     *
     * @param objectToFilter
     * @return
     */
    public boolean filter(Identification objectToFilter) {
        return objectToFilter == null || minScore > objectToFilter.getScore();
    }

}
