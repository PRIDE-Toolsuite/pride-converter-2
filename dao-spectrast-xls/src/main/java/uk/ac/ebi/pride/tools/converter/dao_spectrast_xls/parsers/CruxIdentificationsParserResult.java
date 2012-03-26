package uk.ac.ebi.pride.tools.converter.dao_spectrast_xls.parsers;

import uk.ac.ebi.pride.tools.converter.dao_spectrast_xls.model.CruxProtein;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class CruxIdentificationsParserResult {
    /**
     * Number of spectra references by the identifications (scans)
     */
    public List<Integer> identifiedSpecIds;
    /**
     * Number of identified proteins
     */
    public Map<String, CruxProtein> proteins;
    /**
     *  Number of peptides in the file
     */
    public int peptideCount;
    /**
     * The file header
     */
    public Map<String, Integer> header;
    /**
     * Target file index
     */
    public ArrayList<String> fileIndex;

}
