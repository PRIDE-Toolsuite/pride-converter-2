package uk.ac.ebi.pride.tools.converter.dao_spectrast_xls.parsers;

import uk.ac.ebi.pride.tools.converter.dao_spectrast_xls.model.SpectraSTProtein;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class SpectraSTIdentificationsParserResult {
    /**
     * Number of spectra references by the identifications (scans)
     */
    public List<String> identifiedSpectraTitles;
    /**
     * Number of identified proteins
     */
    public Map<String, SpectraSTProtein> proteins;
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

    /**
     * PTMs
     */
    public Map<String, PTM> ptms;

}
