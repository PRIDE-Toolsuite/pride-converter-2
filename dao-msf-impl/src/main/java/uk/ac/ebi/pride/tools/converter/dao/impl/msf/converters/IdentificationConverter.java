/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.pride.tools.converter.dao.impl.msf.converters;

import com.compomics.thermo_msf_parser_API.enums.MsfVersion;
import com.compomics.thermo_msf_parser_API.highmeminstance.Peptide;
import com.compomics.thermo_msf_parser_API.highmeminstance.Protein;
import com.compomics.thermo_msf_parser_API.lowmeminstance.controllers.PeptideLowMemController;
import com.compomics.thermo_msf_parser_API.lowmeminstance.controllers.ProteinGroupLowMemController;
import com.compomics.thermo_msf_parser_API.lowmeminstance.controllers.ProteinLowMemController;
import com.compomics.thermo_msf_parser_API.lowmeminstance.controllers.ProteinScoreLowMemController;
import com.compomics.thermo_msf_parser_API.lowmeminstance.model.MsfFile;
import com.compomics.thermo_msf_parser_API.lowmeminstance.model.PeptideLowMem;
import com.compomics.thermo_msf_parser_API.lowmeminstance.model.ProteinGroupLowMem;
import com.compomics.thermo_msf_parser_API.lowmeminstance.model.ProteinLowMem;
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
    
    private static ProteinLowMemController proteins = new ProteinLowMemController();
    private static ProteinScoreLowMemController proteinScores = new ProteinScoreLowMemController();
    private static PeptideLowMemController peptides = new PeptideLowMemController();
    private static ProteinGroupLowMemController proteinGroups = new ProteinGroupLowMemController();
    
    public static uk.ac.ebi.pride.tools.converter.report.model.Identification convert(
            ProteinLowMem protein,
            String databaseName,
            String databaseVersion,
            boolean prescanMode,
            Integer confidenceLevel,
            MsfFile msfFile) {

        Identification identification = new Identification();
        identification.setAccession(proteins.getAccessionFromProteinID(protein.getProteinID(), msfFile));
        if (!proteinScores.getScoresForProteinId(protein.getProteinID(), msfFile).isEmpty()) { // PD 1.2 may not give protein scores.
            identification.setScore(proteinScores.getScoresForProteinId(protein.getProteinID(), msfFile).get(0).getScore());
        } else {
            identification.setScore(0.0);
        }
        identification.setUniqueIdentifier(protein.getProteinID() + "");
        identification.setDatabase(databaseName);
        identification.setDatabaseVersion(databaseVersion);

        List<PeptideLowMem> peptidesOfProtein = peptides.getPeptidesForProtein(protein, msfFile);
        
        for (PeptideLowMem peptide : peptidesOfProtein) {
            if (peptide.getConfidenceLevel() >= confidenceLevel) {
                identification.getPeptide().add(PeptideConverter.convertWithCoordinatesInProtein(peptide, protein, msfFile));
            }
        }
        
        /**
         * Only use the following part is the version is greater than 1.2
         */
        //TODO pass on version
        if (!msfFile.getVersion().equals(MsfVersion.VERSION1_2)) {
            //TODO check how this works
            ProteinGroupLowMem proteinGroup = proteinGroups.getProteinGroupForProteinID(protein.getProteinID(), msfFile);

            List<CvParam> params = new ArrayList<CvParam>();

            // Is the protein the "anchor/reference protein"?
            if (proteins.isMasterProtein(protein.getProteinID(),msfFile)) {
                CvParam anchorProtein = new CvParam();
                anchorProtein.setAccession("MS:1001591");
                anchorProtein.setCvLabel("MS");
                anchorProtein.setName("anchor protein");
                anchorProtein.setValue(proteins.getAccessionFromProteinID(protein.getProteinID(), msfFile));
                params.add(anchorProtein);
            }

            if (proteinGroup != null) {
                // Go through the 'other' proteins in the group and establish group relationships
                for (ProteinLowMem groupProtein : proteinGroup.getProteins()) {

                    if (!groupProtein.equals(protein)) {
                        params.add(MsfCvTermReference.PRIDE_GROUP_MEMBER.getCvParam(proteins.getAccessionFromProteinID(groupProtein.getProteinID(),msfFile)));
                    }

                    
                }
            }

            identification.getAdditional().getCvParam().addAll(params);
        }
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
    private Set<String> getPeptideSequences(Protein protein, int confidenceLevel) {
        HashSet<String> result = new HashSet<String>();
        for (Peptide pep : protein.getPeptides()) {
            if (pep.getConfidenceLevel() >= confidenceLevel) {
                result.add(pep.getSequence());
            }
        }
        return result;
    }
}
