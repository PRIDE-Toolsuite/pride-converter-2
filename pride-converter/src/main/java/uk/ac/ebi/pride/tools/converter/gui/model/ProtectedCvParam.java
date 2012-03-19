package uk.ac.ebi.pride.tools.converter.gui.model;

import uk.ac.ebi.pride.tools.converter.report.model.CvParam;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 16/03/12
 * Time: 13:48
 */
public class ProtectedCvParam extends CvParam {

    public ProtectedCvParam() {
    }

    public ProtectedCvParam(String cvLabel, String accession, String name, String value) {
        super(cvLabel, accession, name, value);
    }

    public ProtectedCvParam(CvParam cv) {
        super(cv != null ? cv.getCvLabel() : null,
                cv != null ? cv.getAccession() : null,
                cv != null ? cv.getName() : null,
                cv != null ? cv.getValue() : null);
    }

}
