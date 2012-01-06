package uk.ac.ebi.pride.tools.filter.model;

import uk.ac.ebi.pride.jaxb.model.PrideXmlObject;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 11/03/11
 * Time: 16:14
 * To change this template use File | Settings | File Templates.
 */
public interface Filter<T extends PrideXmlObject> {

    /**
     * Filter an object based on the underlying implementation requirements. This method
     * will return true if an object is to be filtered (i.e. excluded from a given task) and
     * false if it is not to be excluded (i.e. it is a valid object, based on the implementation
     * requirements).
     *
     * @param objectToFilter
     * @return
     */
    boolean filter(T objectToFilter);

}
