package uk.ac.ebi.pride.tools.filter.model.impl;

import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.filter.model.Filter;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 07/06/11
 * Time: 16:29
 */
public class EmptySpectrumFilter implements Filter<Spectrum> {

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
    public boolean filter(Spectrum objectToFilter) {
        boolean intenArrayOk = true;
        boolean mzArrayOk = true;
        if (objectToFilter.getIntenArrayBinary() == null ||
                objectToFilter.getIntenArrayBinary().getData() == null ||
                objectToFilter.getIntenArrayBinary().getData().getLength() == 0) {
            intenArrayOk = false;
        }
        if (objectToFilter.getMzArrayBinary() == null ||
                objectToFilter.getMzArrayBinary().getData() == null ||
                objectToFilter.getMzArrayBinary().getData().getLength() == 0) {
            mzArrayOk = false;
        }
        return !mzArrayOk || !intenArrayOk;
    }
}
