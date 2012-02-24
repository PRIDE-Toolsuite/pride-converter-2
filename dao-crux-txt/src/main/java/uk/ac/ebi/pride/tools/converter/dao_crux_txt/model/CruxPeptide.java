package uk.ac.ebi.pride.tools.converter.dao_crux_txt.model;

/**
 * Its pretty much a POJO representing a peptide
 * @author Jose A. Dianes
 * @version $Id$
 */
public class CruxPeptide {

	//private final String protein_id;
    //private final String flanking_aa;
	private final int scan;
    private final int charge;
    private final double specPrecursorMZ;
    private final double specNeutralMass;
    private final double peptideMass;
    private final double deltaCn;
    private final double xcorrScore;
    private final int xcorrRank;
    private final int matchesSpectrum;
    private final String sequence;
    private final String claveageType;

	public CruxPeptide(int scan, int charge, double specPrecursorMZ, double specNeutralMass, 
                       double peptideMass, double deltaCn, double xcorrScore, int xcorrRank,
                       int matchesSpectrum, String sequence, String claveageType) {
        this.scan = scan;
        this.charge = charge;
        this.specPrecursorMZ = specPrecursorMZ;
        this.specNeutralMass = specNeutralMass;
        this.peptideMass = peptideMass;
        this.deltaCn = deltaCn;
        this.xcorrScore = xcorrScore;
        this.xcorrRank = xcorrRank;
        this.matchesSpectrum = matchesSpectrum;
        this.sequence = sequence;
        this.claveageType = claveageType;
	}

    public int getScan() {
        return scan;
    }

    public int getCharge() {
        return charge;
    }

    public double getSpecPrecursorMZ() {
        return specPrecursorMZ;
    }

    public double getSpecNeutralMass() {
        return specNeutralMass;
    }

    public double getPeptideMass() {
        return peptideMass;
    }

    public double getDeltaCn() {
        return deltaCn;
    }

    public double getXcorrScore() {
        return xcorrScore;
    }

    public int getXcorrRank() {
        return xcorrRank;
    }

    public int getMatchesSpectrum() {
        return matchesSpectrum;
    }

    public String getSequence() {
        return sequence;
    }

    public String getClaveageType() {
        return claveageType;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

        result = prime * result + scan;

        result = prime * result + charge;

		long temp;
		temp = Double.doubleToLongBits(specPrecursorMZ);
		result = prime * result + (int) (temp ^ (temp >>> 32));

		temp = Double.doubleToLongBits(specNeutralMass);
		result = prime * result + (int) (temp ^ (temp >>> 32));

        temp = Double.doubleToLongBits(peptideMass);
		result = prime * result + (int) (temp ^ (temp >>> 32));

        temp = Double.doubleToLongBits(deltaCn);
		result = prime * result + (int) (temp ^ (temp >>> 32));

        temp = Double.doubleToLongBits(xcorrScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));

        result = prime * result + xcorrRank;

        result = prime * result + matchesSpectrum;

        result = prime * result + ((sequence == null) ? 0 : sequence.hashCode());

		result = prime * result + ((claveageType == null) ? 0 : claveageType.hashCode());

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

        CruxPeptide other = (CruxPeptide) obj;

        if (charge != other.scan)
            return false;
        if (charge != other.charge)
            return false;
		if (Double.doubleToLongBits(specPrecursorMZ) != Double
				.doubleToLongBits(other.specPrecursorMZ))
			return false;
		if (Double.doubleToLongBits(specNeutralMass) != Double
				.doubleToLongBits(other.specNeutralMass))
			return false;
		if (Double.doubleToLongBits(peptideMass) != Double
				.doubleToLongBits(other.peptideMass))
			return false;
		if (Double.doubleToLongBits(deltaCn) != Double
				.doubleToLongBits(other.deltaCn))
			return false;
		if (Double.doubleToLongBits(xcorrScore) != Double
				.doubleToLongBits(other.xcorrScore))
			return false;
        if (xcorrRank != other.xcorrRank)
            return false;
        if (matchesSpectrum != other.matchesSpectrum)
            return false;
		if (sequence == null) {
			if (other.sequence != null)
				return false;
		} else if (!sequence.equals(other.sequence))
			return false;
		if (claveageType == null) {
			if (other.claveageType != null)
				return false;
		} else if (!claveageType.equals(other.claveageType))
			return false;

		return true;
	}
}
