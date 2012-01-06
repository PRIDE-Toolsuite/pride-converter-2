package uk.ac.ebi.pride.tools.filter.model.impl;

import uk.ac.ebi.pride.jaxb.model.CvParam;
import uk.ac.ebi.pride.jaxb.model.Identification;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.filter.model.UpdatingFilter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 22/03/11
 * Time: 12:25
 */
public class DecoyHitUpdatingFilter implements UpdatingFilter<Identification> {

    private Pattern decoyPattern;
    int totalIdentificationCount = 0;
    int decoyCount = 0;

    public DecoyHitUpdatingFilter(String decoyPatternStr) {
        try {
            decoyPattern = Pattern.compile(decoyPatternStr);
        } catch (PatternSyntaxException e) {
            throw new ConverterException("Improper regex pattern for decoy pattern string.", e);
        }
    }

    @Override
    public Identification update(Identification objToUpdate) {

        if (objToUpdate != null) {

            //update call count
            totalIdentificationCount++;

            //check for decoy hit
            Matcher match = decoyPattern.matcher(objToUpdate.getAccession());
            if (match.matches()) {
                //update decoy count
                decoyCount++;

                //set identification param
                objToUpdate.getAdditional().getCvParam().add(makeDecoyParam());
            }
        }

        return objToUpdate;
    }

    public Double getFalseDiscoveryRate() {

        if (totalIdentificationCount > 0) {
            return decoyCount / (double) totalIdentificationCount;
        } else {
            return null;
        }

    }

    private CvParam makeDecoyParam() {

        CvParam retval = new CvParam();
        retval.setCvLabel(DAOCvParams.DECOY_HIT.getCv());
        retval.setAccession(DAOCvParams.DECOY_HIT.getAccession());
        retval.setName(DAOCvParams.DECOY_HIT.getName());
        return retval;

    }

}
