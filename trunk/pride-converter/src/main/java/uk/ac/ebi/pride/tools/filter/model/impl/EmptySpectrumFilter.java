package uk.ac.ebi.pride.tools.filter.model.impl;

import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.filter.model.Filter;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 07/06/11
 * Time: 16:29
 */
public class EmptySpectrumFilter implements Filter<Spectrum> {

    private static final byte[] ZERO_VALUE_ARRAY = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};


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

        //to avoid breaking the submission pipeline, the pride xml writer will
        //create intensity/mz arrays that only contain one double, 0.0, should
        //the DAOs return an empty spectrum. This marshalls out as follows;
        //
        //        <intenArrayBinary>
        //        <data precision="64" endian="little" length="8">AAAAAAAAAAA=</data>
        //        </intenArrayBinary>
        //
        //and corresponds to the byte array as defined as the ZERO_VALUE_ARRAY above
        //
        //we can filter on this

        boolean intenArrayOk = true;
        boolean mzArrayOk = true;
        if (objectToFilter.getIntenArrayBinary() == null ||
                objectToFilter.getIntenArrayBinary().getData() == null ||
                objectToFilter.getIntenArrayBinary().getData().getLength() == 0 ||
                (objectToFilter.getIntenArrayBinary().getData().getLength() == 8 &&
                        Arrays.equals(ZERO_VALUE_ARRAY, objectToFilter.getIntenArrayBinary().getData().getValue()))
                ) {
            intenArrayOk = false;
            System.out.println("Empty intensity array for spectrum " + objectToFilter.getId());
        }
        if (objectToFilter.getMzArrayBinary() == null ||
                objectToFilter.getMzArrayBinary().getData() == null ||
                objectToFilter.getMzArrayBinary().getData().getLength() == 0 ||
                (objectToFilter.getMzArrayBinary().getData().getLength() == 8 &&
                        Arrays.equals(ZERO_VALUE_ARRAY, objectToFilter.getMzArrayBinary().getData().getValue()))
                ) {
            mzArrayOk = false;
            System.out.println("Empty mz array for spectrum " + objectToFilter.getId());
        }

        return !mzArrayOk || !intenArrayOk;
    }
}
