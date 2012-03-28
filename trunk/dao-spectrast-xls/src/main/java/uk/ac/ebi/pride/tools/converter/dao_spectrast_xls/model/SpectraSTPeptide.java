package uk.ac.ebi.pride.tools.converter.dao_spectrast_xls.model;

import uk.ac.ebi.pride.tools.converter.dao.Utils;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;
import uk.ac.ebi.pride.tools.converter.report.model.PeptidePTM;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * Its pretty much a POJO representing a SpectraSTX peptide
 * File headers are:
 *  ### Query : the title of the query
 *  Rk : the rank of the peptide identification
 *  ID : the peptide sequence and the charge in the format <sequence>/<charge>
 *  Dot : Dot gives the spectrum similarity (Dot product of query and library spectra)
 *  Delta
 *  DelRk : delta rank
 *  DBias : dot bias
 *  MzDiff : precursor mz diff (Observed – Theoretical m/z)
 *  #Cand : hits num  (probably the number of candidates)
 *  MeanDot : hits mean
 *  SDDot : hits standard deviation
 *  Fval
 *  Status
 *  Inst : instrument type (e.g. qtof=time-of-flight, it=ion-trap, ..., Unk=unknown)
 *  Spec : spectrum type (e.g. con=consensus, ..., Unk=unknown)
 *  #Pr : number of matched proteins
 *  Proteins : the proteins matched. semicolon-separated (todo: check this) not stored within the peptide object
 *  LibFileOffset
 * @author Jose A. Dianes
 * @version $Id$
 */
public class SpectraSTPeptide {


    /**
     * Members
     */
    private final String queryName;
    private final int rank;
    private final String sequence;
    private final int charge;
    private final double dot;
    private final double delta;
    private final int deltaRank;
    private final double dotBias;
    private final double precursorMzDiff;
    private final int numCand;
    private final double meanDot;
    private final double sdDot;
    private final double fval;
    private final String status;
    private final String inst;
    private final String spectrumType;
    private final int numProteins;
    private final int libFileOffset;

    public SpectraSTPeptide(String queryName, int rank, String sequence, int charge, double dot, double delta,
                            int deltaRank, double dotBias, double precursorMzDiff, int numCand, double meanDot,
                            double sdDot, double fval, String status, String inst, String spectrumType, int numProteins,
                            int libFileOffset) {
        this.queryName = queryName;
        this.rank = rank;
        this.sequence = sequence;
        this.charge = charge;
        this.dot = dot;
        this.delta = delta;
        this.deltaRank = deltaRank;
        this.dotBias = dotBias;
        this.precursorMzDiff = precursorMzDiff;
        this.numCand = numCand;
        this.meanDot = meanDot;
        this.sdDot = sdDot;
        this.fval = fval;
        this.status = status;
        this.inst = inst;
        this.spectrumType = spectrumType;
        this.numProteins = numProteins;
        this.libFileOffset = libFileOffset;

    }

    public String getQueryName() {
        return queryName;
    }

    public int getRank() {
        return rank;
    }

    public String getSequence() {
        return sequence;
    }

    public int getCharge() {
        return charge;
    }

    public double getDot() {
        return dot;
    }

    public double getDelta() {
        return delta;
    }

    public int getDeltaRank() {
        return deltaRank;
    }

    public double getDotBias() {
        return dotBias;
    }

    public double getPrecursorMzDiff() {
        return precursorMzDiff;
    }

    public int getNumCand() {
        return numCand;
    }

    public double getMeanDot() {
        return meanDot;
    }

    public double getSdDot() {
        return sdDot;
    }

    public double getFval() {
        return fval;
    }

    public String getStatus() {
        return status;
    }

    public String getInst() {
        return inst;
    }

    public String getSpectrumType() {
        return spectrumType;
    }

    public int getNumProteins() {
        return numProteins;
    }

    public int getLibFileOffset() {
        return libFileOffset;
    }


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

        result = prime * result + ((queryName == null) ? 0 : queryName.hashCode());

        result = prime * result + rank;

        result = prime * result + ((sequence == null) ? 0 : sequence.hashCode());

        result = prime * result + charge;

        long temp;
		temp = Double.doubleToLongBits(dot);
		result = prime * result + (int) (temp ^ (temp >>> 32));

        temp = Double.doubleToLongBits(delta);
        result = prime * result + (int) (temp ^ (temp >>> 32));

        result = prime * result + deltaRank;

        temp = Double.doubleToLongBits(dotBias);
        result = prime * result + (int) (temp ^ (temp >>> 32));

        temp = Double.doubleToLongBits(precursorMzDiff);
        result = prime * result + (int) (temp ^ (temp >>> 32));

        result = prime * result + numCand;

        temp = Double.doubleToLongBits(meanDot);
        result = prime * result + (int) (temp ^ (temp >>> 32));

        temp = Double.doubleToLongBits(sdDot);
        result = prime * result + (int) (temp ^ (temp >>> 32));

        temp = Double.doubleToLongBits(fval);
        result = prime * result + (int) (temp ^ (temp >>> 32));

        result = prime * result + ((status == null) ? 0 : status.hashCode());

        result = prime * result + ((inst == null) ? 0 : inst.hashCode());

        result = prime * result + ((spectrumType == null) ? 0 : spectrumType.hashCode());

        result = prime * result + numProteins;

        result = prime * result + libFileOffset;

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

        SpectraSTPeptide other = (SpectraSTPeptide) obj;

        if (!queryName.equals(other.queryName))
			return false;
        else if (rank != other.rank)
            return false;
        else if (!sequence.equals(other.sequence))
            return false;
        else if (charge != other.charge)
            return false;
        else if (Double.doubleToLongBits(dot) != Double.doubleToLongBits(other.dot))
			return false;
        else if (Double.doubleToLongBits(delta) != Double.doubleToLongBits(other.delta))
            return false;
        else if (deltaRank != other.deltaRank)
            return false;
        else if (Double.doubleToLongBits(dotBias) != Double.doubleToLongBits(other.dotBias))
            return false;
        else if (Double.doubleToLongBits(precursorMzDiff) != Double.doubleToLongBits(other.precursorMzDiff))
            return false;
        else if (numCand != other.numCand)
            return false;
        else if (Double.doubleToLongBits(meanDot) != Double.doubleToLongBits(other.meanDot))
            return false;
        else if (Double.doubleToLongBits(sdDot) != Double.doubleToLongBits(other.sdDot))
            return false;
        else if (Double.doubleToLongBits(fval) != Double.doubleToLongBits(other.fval))
            return false;
        else if (!status.equals(other.status))
            return false;
        else if (!inst.equals(other.inst))
            return false;
        else if (!spectrumType.equals(other.spectrumType))
            return false;
        else if (numProteins != other.numProteins)
            return false;
        else if (libFileOffset != other.libFileOffset)
            return false;

		return true;
	}

    /**
     * Returns a list of peptide PTMs associated with this SpectraST peptide. It includes variable and fixed PTMs (including
     * terminal modifications). It makes use of the parameter parsing result where it can find information about the
     * variable and fixed modifications defined during the search.
     *
     * It makes several checks about the existence or not in the param file of the modifications in the sequence, throwing
     * ConverterExcetion (unchecked) when wrong.
     *
     * @return A Collection of PeptidePTM associated with this Crux peptide.
     */
    public Collection<PeptidePTM> getPTMs() {

        LinkedList<PeptidePTM> result = new LinkedList<PeptidePTM>();

        Map<Integer, String> mods = Utils.getModifications(sequence);

        for (Map.Entry<Integer, String> mod: mods.entrySet()) {
            String[] modDelta = mod.getValue().split("[\\[\\]]");
            PTM ptm = new PTM();
            ptm.setFixedModification(false);
            ptm.setResidues(modDelta[0]);
            ptm.setSearchEnginePTMLabel(modDelta[1]+"@"+modDelta[0]);
            ptm.getModMonoDelta().add(modDelta[1]);
            PeptidePTM newPeptidePtm = newPeptidePtm(ptm, mod.getKey());
            result.add(newPeptidePtm);
        }

        return result;
    }

    /**
     * Using the passed map, it finds out its index. The peptide queryName member is used to find the appropriate key in
     * the Map. It has to be prefix of just one key in the Map.
     * @return The index or -1 if not available or duplicated prefix
     * */
    public int getSpectraIndex(Map<String, Integer> titleToSpectraIdMap) {

        int index = -1;
        int timesFound = 0;
        for (Map.Entry<String, Integer> entry: titleToSpectraIdMap.entrySet()) {
            if (entry.getKey().startsWith(queryName)) {
                index = entry.getValue();
                timesFound++;
            }
        }

        if (timesFound!=1)
            return -1;
        else
            return index;
    }

    private PeptidePTM newPeptidePtm(PTM ptm, long location) {
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
