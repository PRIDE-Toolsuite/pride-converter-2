package uk.ac.ebi.pride.tools.converter.dao_crux_txt.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the identification protein accession and a list of associated peptides
 * @author Jose A. Dianes
 * @version $Id$
 */
public class CruxProtein {
	private final String accession;
	private List<CruxPeptide> peptides;
	
	public CruxProtein(String accession) {
		this.accession = accession;
		this.peptides = new ArrayList<CruxPeptide>();
	}
	
	public void addPeptide(CruxPeptide peptide) {
		peptides.add(peptide);
	}
	
	public void removePeptide(CruxPeptide peptide) {
		peptides.remove(peptide);
	}
	
	public String getAccession() {
		return accession;
	}
	
	public List<CruxPeptide> getPeptides() {
		return new ArrayList<CruxPeptide>(peptides);
	}
}
