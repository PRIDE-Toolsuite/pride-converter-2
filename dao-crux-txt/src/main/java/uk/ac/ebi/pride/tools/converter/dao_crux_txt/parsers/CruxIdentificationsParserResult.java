package uk.ac.ebi.pride.tools.converter.dao_crux_txt.parsers;

import uk.ac.ebi.pride.tools.converter.dao_crux_txt.model.CruxProtein;

import java.util.List;
import java.util.Map;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class CruxIdentificationsParserResult {
    public List<Integer> identifiedSpecIds;
    public Map<String, CruxProtein> proteins;
    public int peptideCount;
    public Map<String, Integer> header;
}
