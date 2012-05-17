package uk.ac.ebi.pride.tools.converter.dao_omssa_txt.model;

import uk.ac.ebi.pride.tools.converter.report.model.PTM;
import uk.ac.ebi.pride.tools.converter.report.model.PeptidePTM;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * It's pretty much a POJO representing an Omssa peptide
 * File headers are (in a csv Omssa output file):
 *  Spectrum number: uniquely identifies the analised spectrum
 *  Filename/id
 *  Peptide: the AA sequence
 *  E-value: the Omssa score
 *  Mass: peptide mass
 *  gi
 *  Accession: it looks like some kind of internal Omssa accession. We don't pay too much attention to it.
 *  Start: of the peptide inside of the protein sequence.
 *  Stop: of the peptide inside of the protein sequence.
 *  Define: the protein accession
 *  Mods: modifcations, in natural language (!)
 *  Charge: precursor charge
 *  Theo Mass
 *  P-value
 *  NIST score
 *
 * @author Jose A. Dianes
 * @version $Id$
 */
public class OmssaPeptide {

    /**
     * Members
     */
    private final int spectrumNumber;
    private final String fileNameId;
    private final String peptide;
    private final double eValue;
    private final double mass;
    private final double gi;
    private final String accession;
    private final int start;
    private final int stop;
    private final String defline;
    private final String mods;
    private final int charge;
    private final double theoMass;
    private final double pValue;
    private final double nistScore;


    public OmssaPeptide(int spectrumNumber, String fileNameId, String peptide, double eValue, double mass, double gi,
                        String accession, int start, int stop, String defline, String mods, int charge, double theoMass,
                        double pValue, double nistScore) {
        this.spectrumNumber = spectrumNumber;
        this.fileNameId = fileNameId;
        this.peptide = peptide;
        this.eValue = eValue;
        this.mass = mass;
        this.gi = gi;
        this.accession = accession;
        this.start = start;
        this.stop = stop;
        this.defline = defline;
        this.mods = mods;
        this.charge = charge;
        this.theoMass = theoMass;
        this.pValue = pValue;
        this.nistScore = nistScore;
    }

    public int getSpectrumNumber() {
        return spectrumNumber;
    }

    public String getFileNameId() {
        return fileNameId;
    }

    public String getPeptide() {
        return peptide;
    }

    public double geteValue() {
        return eValue;
    }

    public double getMass() {
        return mass;
    }

    public double getGi() {
        return gi;
    }

    public String getAccession() {
        return accession;
    }

    public int getStart() {
        return start;
    }

    public int getStop() {
        return stop;
    }

    public String getDefline() {
        return defline;
    }

    public String getMods() {
        return mods;
    }

    public int getCharge() {
        return charge;
    }

    public double getTheoMass() {
        return theoMass;
    }

    public double getpValue() {
        return pValue;
    }

    public double getNistScore() {
        return nistScore;
    }

    @Override
    public int hashCode() {
		final int prime = 31;
		int result = 1;

        result = prime * result + spectrumNumber;

        result = prime * result + ((fileNameId == null) ? 0 : fileNameId.hashCode());

        result = prime * result + ((peptide == null) ? 0 : peptide.hashCode());

        long temp;
        temp = Double.doubleToLongBits(eValue);
        result = prime * result + (int) (temp ^ (temp >>> 32));

        temp = Double.doubleToLongBits(mass);
        result = prime * result + (int) (temp ^ (temp >>> 32));

        temp = Double.doubleToLongBits(gi);
        result = prime * result + (int) (temp ^ (temp >>> 32));

        result = prime * result + ((accession == null) ? 0 : accession.hashCode());

        result = prime * result + start;

        result = prime * result + stop;

        result = prime * result + ((defline == null) ? 0 : defline.hashCode());

        result = prime * result + ((mods == null) ? 0 : mods.hashCode());

        result = prime * result + charge;

        temp = Double.doubleToLongBits(theoMass);
        result = prime * result + (int) (temp ^ (temp >>> 32));

        temp = Double.doubleToLongBits(pValue);
        result = prime * result + (int) (temp ^ (temp >>> 32));

        temp = Double.doubleToLongBits(nistScore);
        result = prime * result + (int) (temp ^ (temp >>> 32));

        return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

        OmssaPeptide other = (OmssaPeptide) obj;

        if (spectrumNumber != other.spectrumNumber)
            return false;
        else if (!fileNameId.equals(other.fileNameId))
            return false;
        else if (!peptide.equals(other.peptide))
            return false;
        else if (Double.doubleToLongBits(eValue) != Double.doubleToLongBits(other.eValue))
			return false;
        else if (Double.doubleToLongBits(mass) != Double.doubleToLongBits(other.mass))
            return false;
        else if (Double.doubleToLongBits(gi) != Double.doubleToLongBits(other.gi))
            return false;
        else if (!accession.equals(other.accession))
            return false;
        else if (start != other.start)
            return false;
        else if (stop != other.stop)
            return false;
        else if (!defline.equals(other.defline))
            return false;
        else if (!mods.equals(other.mods))
            return false;
        else if (charge != other.charge)
            return false;
        else if (Double.doubleToLongBits(theoMass) != Double.doubleToLongBits(other.theoMass))
            return false;
        else if (Double.doubleToLongBits(pValue) != Double.doubleToLongBits(other.pValue))
            return false;
        else if (Double.doubleToLongBits(nistScore) != Double.doubleToLongBits(other.nistScore))
            return false;

		return true;
	}

    /**
     *
     * @return A Collection of PeptidePTM associated with this Crux peptide.
     */
    public static Collection<PeptidePTM> getPTMs(String peptideSequence, Map<Character, Double> fixedPtms, Map<Character, Double> variablePtms) {

        LinkedList<PeptidePTM> result = new LinkedList<PeptidePTM>();

        char[] aaSequence = peptideSequence.toCharArray();
        for (int i=0; i< aaSequence.length; i++) {
            char aa = aaSequence[i];
            // check for fixed PTMs
            if (fixedPtms.containsKey(aa)) {
                PTM ptm = new PTM();
                ptm.setFixedModification(true);
                ptm.setResidues(""+aa);
                ptm.setSearchEnginePTMLabel(fixedPtms.get(aa) + "@" + aa);
                ptm.getModMonoDelta().add(""+fixedPtms.get(aa));
                PeptidePTM newPeptidePtm = newPeptidePtm(ptm, i);
                result.add(newPeptidePtm);
            }
            // check for variable PTMs
            if ( Character.isLowerCase(aa)
                    && variablePtms.containsKey(Character.toUpperCase(aa))
                ) {
                char AA = Character.toUpperCase(aa);
                PTM ptm = new PTM();
                ptm.setFixedModification(false);
                ptm.setResidues(""+AA);
                ptm.setSearchEnginePTMLabel(
                        variablePtms.get(AA) +"@"+ AA
                );
                ptm.getModMonoDelta().add(""+variablePtms.get(AA));
                PeptidePTM newPeptidePtm = newPeptidePtm(ptm, i);
                result.add(newPeptidePtm);
            }
        }


        return result;
    }

//    /**
//     * Using the passed map, it finds out its index. The peptide queryName member is used to find the appropriate key in
//     * the Map. It has to be prefix of just one key in the Map.
//     * @return The index or -1 if not available or duplicated prefix
//     * */
//    public int getSpectraIndex(Map<String, Integer> titleToSpectraIdMap) {
//
//        int index = -1;
//        int timesFound = 0;
//        for (Map.Entry<String, Integer> entry: titleToSpectraIdMap.entrySet()) {
//            if (entry.getKey().startsWith(queryName)) {
//                index = entry.getValue();
//                timesFound++;
//            }
//        }
//
//        if (timesFound!=1)
//            return -1;
//        else
//            return index;
//    }

    private static PeptidePTM newPeptidePtm(PTM ptm, long location) {
        PeptidePTM newPeptidePtm = new PeptidePTM();
        newPeptidePtm.setSearchEnginePTMLabel(ptm.getSearchEnginePTMLabel());
        newPeptidePtm.setFixedModification(ptm.isFixedModification());
        newPeptidePtm.setResidues(ptm.getResidues());
        newPeptidePtm.setModLocation(location);
        for (String monoDelta: ptm.getModMonoDelta()) {
            newPeptidePtm.getModMonoDelta().add(monoDelta);
        }
        return newPeptidePtm;
    }


}
