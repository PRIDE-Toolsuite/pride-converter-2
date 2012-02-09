package uk.ac.ebi.pride.tools.converter.dao.handler;

import uk.ac.ebi.pride.tools.converter.report.model.Identification;
import uk.ac.ebi.pride.tools.converter.report.model.Param;

/**
 * Handlers that provide information from an external
 * source. The default external source for this information
 * is an mzTab file.
 * @author jg
 *
 */
public interface ExternalHandler {

    public Identification updateIdentification(Identification identification);
    
    public Param getSampleDescriptionParams();
    
    public Param getExperimentParams();
}
