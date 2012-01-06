package uk.ac.ebi.pride.tools.converter.dao_msgf_impl.model;

import java.util.ArrayList;
import java.util.List;

public class MsgfProtein {
	private final String accession;
	private List<MsgfPeptide> peptides;
	
	public MsgfProtein(String accession) {
		this.accession = accession;
		this.peptides = new ArrayList<MsgfPeptide>();
	}
	
	public void addPeptide(MsgfPeptide peptide) {
		peptides.add(peptide);
	}
	
	public void removePeptide(MsgfPeptide peptide) {
		peptides.remove(peptide);
	}
	
	public String getAccession() {
		return accession;
	}
	
	public List<MsgfPeptide> getPeptides() {
		return new ArrayList<MsgfPeptide>(peptides);
	}
}
