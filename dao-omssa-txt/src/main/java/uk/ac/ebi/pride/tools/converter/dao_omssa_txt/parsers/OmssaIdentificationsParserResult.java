package uk.ac.ebi.pride.tools.converter.dao_omssa_txt.parsers;

import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.model.OmssaProtein;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class OmssaIdentificationsParserResult {
    /**
     * Number of spectra references by the identifications (scans)
     */
    private List<Integer> identifiedSpectraTitles;
    /**
     * Number of identified proteins
     */
    private Map<String, OmssaProtein> proteins;
    /**
     * Number of peptides in the file
     */
    private int peptideCount;
    /**
     * The file header
     */
    private Map<String, Integer> header;
    /**
     * Target file index
     */
    private ArrayList<String[]> fileIndex;

    /**
     * PTMs
     */
    private Map<String, PTM> ptms;

    public List<Integer> getIdentifiedSpectraTitles() {
        return identifiedSpectraTitles;
    }

    public void setIdentifiedSpectraTitles(List<Integer> identifiedSpectraTitles) {
        this.identifiedSpectraTitles = identifiedSpectraTitles;
    }

    public Map<String, OmssaProtein> getProteins() {
        return proteins;
    }

    public void setProteins(Map<String, OmssaProtein> proteins) {
        this.proteins = proteins;
    }

    public int getPeptideCount() {
        return peptideCount;
    }

    public void setPeptideCount(int peptideCount) {
        this.peptideCount = peptideCount;
    }

    public Map<String, Integer> getHeader() {
        return header;
    }

    public void setHeader(Map<String, Integer> header) {
        this.header = header;
    }

    public ArrayList<String[]> getFileIndex() {
        return fileIndex;
    }

    public void setFileIndex(ArrayList<String[]> fileIndex) {
        this.fileIndex = fileIndex;
    }

    public Map<String, PTM> getPtms() {
        return ptms;
    }

    public void setPtms(Map<String, PTM> ptms) {
        this.ptms = ptms;
    }
}
