package uk.ac.ebi.pride.tools.converter.dao_crux_txt.results;

import uk.ac.ebi.pride.tools.converter.dao_crux_txt.model.CruxProtein;

import java.util.List;
import java.util.Map;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class CruxParserResults {
    public List<Integer> identifiedSpecIds;
    public Map<String, CruxProtein> proteins;
    public int peptideCount;
}
