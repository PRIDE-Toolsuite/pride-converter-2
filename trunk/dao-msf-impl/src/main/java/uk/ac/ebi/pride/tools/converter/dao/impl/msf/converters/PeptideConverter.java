/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.pride.tools.converter.dao.impl.msf.converters;

import com.compomics.thermo_msf_parser.msf.Modification;
import com.compomics.thermo_msf_parser.msf.ModificationPosition;
import java.sql.SQLException;
import java.util.Iterator;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.Peptide;
import uk.ac.ebi.pride.tools.converter.report.model.PeptidePTM;

/**
 *
 * @author toorn101
 */
public class PeptideConverter {

    /**
     *
     * @param originalPeptide
     * @return
     */
    public static Peptide convert(com.compomics.thermo_msf_parser.msf.Peptide originalPeptide) {
        Peptide converted = new Peptide();
        converted.setSequence(originalPeptide.getSequence());
        converted.setSpectrumReference(originalPeptide.getSpectrumId());
        converted.setUniqueIdentifier(originalPeptide.getPeptideId() + "");
        
        if (originalPeptide.getPhosphoRSScore() != null) {
            CvParam phosphoRSScore = new CvParam();
            phosphoRSScore.setCvLabel("MS");
            phosphoRSScore.setAccession("MS:1001969");
            phosphoRSScore.setName("ProteomeDiscoverer:phosphoRS score");
            phosphoRSScore.setValue(originalPeptide.getPhosphoRSScore().toString());
            converted.getAdditional().getCvParam().add(phosphoRSScore);
        }
        if (originalPeptide.getPhoshpoRSSequenceProbability() != null) {
            CvParam phosphoRSSequenceProbability = new CvParam();
            phosphoRSSequenceProbability.setCvLabel("MS");
            phosphoRSSequenceProbability.setAccession("MS:1001970");
            phosphoRSSequenceProbability.setName("ProteomeDiscoverer:phosphoRS sequence probability");
            phosphoRSSequenceProbability.setValue(originalPeptide.getPhoshpoRSSequenceProbability().toString());
            converted.getAdditional().getCvParam().add(phosphoRSSequenceProbability);
        }
        
        Iterator<Modification> modIterator = originalPeptide.getPeptideModifications().iterator();
        Iterator<ModificationPosition> modPositionIterator = originalPeptide.getPeptideModificationPositions().iterator();
        Iterator<Float> siteProbabilityIterator = originalPeptide.getPhosphoRSSiteProbabilities().iterator();

        while (modIterator.hasNext()) {
            Modification mod = modIterator.next();
            ModificationPosition pos = modPositionIterator.next();
            Float siteProbability = siteProbabilityIterator.next();

            PeptidePTM ptm = PTMConverter.convertToPeptidePTM(mod, pos.getPosition() + 1, siteProbability);

            converted.getPTM().add(ptm);
        }
        return converted;
    }

    public static Peptide convertWithCoordinatesInProtein(com.compomics.thermo_msf_parser.msf.Peptide originalPeptide, com.compomics.thermo_msf_parser.msf.Protein protein) {
        Peptide converted = convert(originalPeptide);
        String proteinSequence = "";
        try {
            proteinSequence = protein.getSequence();
        } catch (SQLException ex) {
        }

        int startInProtein = proteinSequence.indexOf(originalPeptide.getSequence());
        int endInProtein = startInProtein + originalPeptide.getSequence().length();
        startInProtein++;

        converted.setStart(startInProtein);
        converted.setEnd(endInProtein);
        return converted;
    }
}
