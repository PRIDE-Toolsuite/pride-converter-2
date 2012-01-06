package uk.ac.ebi.pride.tools.filter.model;

import uk.ac.ebi.pride.jaxb.model.PrideXmlObject;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 11/03/11
 * Time: 16:50
 */
public interface UpdatingFilter<T extends PrideXmlObject> {

    public T update(T objToUpdate);

}
