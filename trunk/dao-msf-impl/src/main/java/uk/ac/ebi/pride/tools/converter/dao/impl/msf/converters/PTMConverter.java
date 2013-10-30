/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.pride.tools.converter.dao.impl.msf.converters;

import com.compomics.thermo_msf_parser_API.highmeminstance.Modification;
import uk.ac.ebi.pride.tools.converter.dao.impl.msf.terms.MsfCvTermReference;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;

import uk.ac.ebi.pride.tools.converter.report.model.PeptidePTM;

/**
 *
 * @author toorn101
 */
public class PTMConverter {

    /**
     * Convert a msf Modification to a PRIDE PTM object
     *
     * @param originalPTM
     * @return
     */
    public static PTM convert(Modification originalPTM) {
        PTM converted = new PTM();
        convert(originalPTM, converted);
        return converted;
    }

    /**
     * Convert a msf Modification to a PRIDE PeptidePTM
     *
     * @param originalPTM
     * @param position
     * @return
     */
    public static PeptidePTM convertToPeptidePTM(Modification originalPTM, long position) {
        PeptidePTM converted = new PeptidePTM();
        convert(originalPTM, converted);
        converted.setModLocation(position);

        return converted;
    }

    /**
     * convert a msf Modification to a PRIDE PeptidePTM if a siteProbability is
     * present
     *
     * @param originalPTM
     * @param position
     * @param siteProbability
     * @return
     */
    public static PeptidePTM convertToPeptidePTM(Modification originalPTM, long position, Float siteProbability) {
        PeptidePTM converted = convertToPeptidePTM(originalPTM, position);

        converted.setModLocation(position);
        
        if (siteProbability != null) {

            CvParam siteLocalization = MsfCvTermReference.PD_PHOSPHORS_SITE_PROBABILITY.getCvParam(siteProbability * 100 + "%");

            converted.getAdditional().getCvParam().add(siteLocalization);
        }
        return converted;
    }

    /**
     * Convenience function to unify inheritance, fills the PTM or subclasses
     * referenced by 'converted'
     *
     * @param originalPTM
     * @param converted
     */
    private static void convert(Modification originalPTM, PTM converted) {
        converted.setFixedModification(originalPTM.isFixedModification());
        converted.setModAccession(originalPTM.getUnimodAccession() + "");
        converted.setModDatabase("UniMod");
        converted.setModDatabaseVersion(""); //Unknown
        converted.setSearchEnginePTMLabel(originalPTM.getModificationName());

        StringBuilder rsb = new StringBuilder();

        for (com.compomics.thermo_msf_parser_API.highmeminstance.AminoAcid aa : originalPTM.getSelectedAminoAcids()) {
            rsb.append(aa.getOneLetterCode().toUpperCase());
        }
        converted.setResidues(rsb.toString());
        converted.getModMonoDelta().add(originalPTM.getDeltaMass() + "");
        converted.getModAvgDelta().add(originalPTM.getDeltaAverageMass() + "");

        converted.setSearchEnginePTMLabel(converted.getSearchEnginePTMLabel()+ " (" + converted.getResidues() + ")");
    }
}
