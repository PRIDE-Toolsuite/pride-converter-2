/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.pride.tools.converter.dao.impl.msf.converters;

import com.compomics.thermo_msf_parser.Parser;
import com.compomics.thermo_msf_parser.msf.Peptide;
import com.compomics.thermo_msf_parser.msf.Protein;
import com.compomics.thermo_msf_parser.msf.ProteinGroup;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import uk.ac.ebi.pride.tools.converter.dao.impl.msf.terms.MsfCvTermReference;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.Identification;

/**
 *
 * @author toorn101
 */
public class IdentificationConverter {

    /**
     * Convert a protein to an Identification
     *
     * @param parser The msf parser instance
     * @param protein an msf parser protein
     * @param prescanMode mode of operation, defines the amount of data returned
     * @see
     * @return
     */
    public static uk.ac.ebi.pride.tools.converter.report.model.Identification convert(Parser parser,
            Protein protein,
            String databaseName,
            String databaseVersion,
            boolean prescanMode,
            Integer confidenceLevel) {

        Identification identification = new Identification();
        identification.setAccession(protein.getUtilAccession());
        identification.setScore(protein.getScores().firstElement().getScore());
        identification.setUniqueIdentifier(protein.getProteinId() + "");
        identification.setDatabase(databaseName);
        identification.setDatabaseVersion(databaseVersion);

        for (Peptide peptide : protein.getPeptides()) {
            if (peptide.getConfidenceLevel() >= confidenceLevel) {
                identification.getPeptide().add(PeptideConverter.convertWithCoordinatesInProtein(peptide, protein));
            }
        }

        ProteinGroup proteinGroup = parser.getProteinGroupsMap().get(protein.getProteinGroupId());

        List<CvParam> params = new ArrayList<CvParam>();

        // Is the protein the "anchor/reference protein"?
        if (protein.getMasterProtein() == 1) {
            CvParam anchorProtein = new CvParam();
            anchorProtein.setAccession("MS:1001591");
            anchorProtein.setCvLabel("MS");
            anchorProtein.setName("anchor protein");
            anchorProtein.setValue(protein.getUtilAccession()); //TODO: I hope this is correct behavior
            params.add(anchorProtein);
        }

        // A set to collect all peptide sequences of the 'other' proteins in the same group
        Set<String> allOtherPeptideSequences = new HashSet<String>();


        // Go through the 'other' proteins in the group and establish group relationships
        for (Protein groupProtein : proteinGroup.getProteins()) {

            if (groupProtein.equals(protein)) {
                continue;
            }

            params.add(MsfCvTermReference.PRIDE_GROUP_MEMBER.getCvParam(groupProtein.getUtilAccession()));
        }

        identification.getAdditional().getCvParam().addAll(params);

        return identification;
    }

    /**
     * extract peptide sequences from a protein, at a certain confidence level
     * cutoff
     *
     * @param protein
     * @param confidenceLevel only use the given confidence level or better
     * @return
     */
    private static Set<String> getPeptideSequences(Protein protein, int confidenceLevel) {
        HashSet<String> result = new HashSet<String>();
        for (Peptide pep : protein.getPeptides()) {
            if (pep.getConfidenceLevel() >= confidenceLevel) {
                result.add(pep.getSequence());
            }
        }
        return result;
    }
}
