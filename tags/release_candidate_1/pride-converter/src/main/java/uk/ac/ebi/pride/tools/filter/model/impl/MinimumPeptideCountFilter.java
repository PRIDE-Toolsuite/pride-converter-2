package uk.ac.ebi.pride.tools.filter.model.impl;

import uk.ac.ebi.pride.jaxb.model.Identification;
import uk.ac.ebi.pride.tools.filter.model.Filter;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 11/03/11
 * Time: 16:43
 */
public class MinimumPeptideCountFilter implements Filter<Identification> {

    private int minPeptideCount;

    public MinimumPeptideCountFilter(int minPeptideCount) {
        this.minPeptideCount = minPeptideCount;
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
        return objectToFilter == null || minPeptideCount > objectToFilter.getPeptideItem().size();
    }
}
