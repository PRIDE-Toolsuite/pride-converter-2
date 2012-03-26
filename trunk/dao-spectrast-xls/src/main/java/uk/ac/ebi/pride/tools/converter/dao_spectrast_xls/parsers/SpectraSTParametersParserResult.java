package uk.ac.ebi.pride.tools.converter.dao_spectrast_xls.parsers;

import uk.ac.ebi.pride.tools.converter.report.model.PTM;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class SpectraSTParametersParserResult {
    public Properties properties;
    public Collection<PTM> ptms;
    public Map<String, PTM> aaToFixedPtm;
    public Map<String, PTM> aaToPtm;
    public PTM ntermPTM;
    public PTM ctermPTM;
    public PTM ntermFixedPTM;
    public PTM ctermFixedPTM;
}
