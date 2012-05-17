package uk.ac.ebi.pride.tools.converter.dao_omssa_txt.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the identification protein accession and a list of associated peptides
 * @author Jose A. Dianes
 * @version $Id$
 */
public class OmssaProtein {
	private final String accession;
    /**
     * The list of peptides references the position in the file index (line number inside the file starting in 0)
     */
	private List<Integer> peptides;
	
	public OmssaProtein(String accession) {
		this.accession = accession;
		this.peptides = new ArrayList<Integer>();
	}
	
	public void addPeptide(Integer peptide) {
		peptides.add(peptide);
	}
	
	public void removePeptide(OmssaPeptide peptide) {
		peptides.remove(peptide);
	}
	
	public String getAccession() {
		return accession;
	}
	
	public List<Integer> getPeptides() {
		return new ArrayList<Integer>(peptides);
	}
}
