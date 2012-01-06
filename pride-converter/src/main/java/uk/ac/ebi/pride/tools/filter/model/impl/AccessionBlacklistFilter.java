package uk.ac.ebi.pride.tools.filter.model.impl;

import uk.ac.ebi.pride.jaxb.model.Identification;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 11/03/11
 * Time: 16:29
 */
public class AccessionBlacklistFilter extends AbstractListFilter {

    public AccessionBlacklistFilter(Set<String> accessionsToFilter) {
        super(accessionsToFilter);
    }

    public AccessionBlacklistFilter(String filePath) {
        super(filePath);
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
        return objectToFilter == null || accessionsToFilter.contains(objectToFilter.getAccession());
    }
}
