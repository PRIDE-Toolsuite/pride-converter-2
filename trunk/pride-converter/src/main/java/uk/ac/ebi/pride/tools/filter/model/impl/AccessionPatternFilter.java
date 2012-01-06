package uk.ac.ebi.pride.tools.filter.model.impl;

import uk.ac.ebi.pride.jaxb.model.Identification;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.filter.model.Filter;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 11/03/11
 * Time: 16:36
 */
public class AccessionPatternFilter implements Filter<Identification> {

    private Pattern pattern;

    public AccessionPatternFilter(String whitelistPatterm) {
        try {
            pattern = Pattern.compile(whitelistPatterm);
        } catch (PatternSyntaxException e) {
            throw new ConverterException("Improper regex pattern for allowed pattern string.", e);
        }

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
        return objectToFilter == null || pattern.matcher(objectToFilter.getAccession()).matches();
    }
}
