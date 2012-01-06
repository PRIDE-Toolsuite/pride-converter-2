package uk.ac.ebi.pride.tools.converter.dao_msgf_impl.model;

public class MsgfPeptide {
	private final int scan;
	private final String sequence;
	private final String prevAA;
	private final String nextAA;
	private final int charge;
	private final double mqScore;
	private final double length;
	private final double totalPrmScore;
	private final double medianPrmScore;
	private final double fractionY;
	private final double fractionB;
	private final double intensity;
	private final int ntt;
	private final double pValue;
	private final double fScore;
	private final double deltaScore;
	private final double deltaScoreOther;
	private final int recordNumber;
	private final int dbFilePos;
	private final int specFilePos;
	private final double specProb;
	
	public MsgfPeptide(int scan, String sequence, String prevAA, String nextAA,
			int charge, double mqScore, double length, double totalPrmScore,
			double medianPrmScore, double fractionY, double fractionB,
			double intensity, int ntt, double pValue, double fScore,
			double deltaScore, double deltaScoreOther, int recordNumber,
			int dbFilePos, int specFilePos, double specProb) {
		this.scan = scan;
		this.sequence = sequence;
		this.prevAA = prevAA;
		this.nextAA = nextAA;
		this.charge = charge;
		this.mqScore = mqScore;
		this.length = length;
		this.totalPrmScore = totalPrmScore;
		this.medianPrmScore = medianPrmScore;
		this.fractionY = fractionY;
		this.fractionB = fractionB;
		this.intensity = intensity;
		this.ntt = ntt;
		this.pValue = pValue;
		this.fScore = fScore;
		this.deltaScore = deltaScore;
		this.deltaScoreOther = deltaScoreOther;
		this.recordNumber = recordNumber;
		this.dbFilePos = dbFilePos;
		this.specFilePos = specFilePos;
		this.specProb = specProb;
	}

	public int getScan() {
		return scan;
	}

	public String getSequence() {
		return sequence;
	}

	public String getPrevAA() {
		return prevAA;
	}

	public String getNextAA() {
		return nextAA;
	}

	public int getCharge() {
		return charge;
	}

	public double getMqScore() {
		return mqScore;
	}

	public double getLength() {
		return length;
	}

	public double getTotalPrmScore() {
		return totalPrmScore;
	}

	public double getMedianPrmScore() {
		return medianPrmScore;
	}

	public double getFractionY() {
		return fractionY;
	}

	public double getFractionB() {
		return fractionB;
	}

	public double getIntensity() {
		return intensity;
	}

	public int getNtt() {
		return ntt;
	}

	public double getpValue() {
		return pValue;
	}

	public double getfScore() {
		return fScore;
	}

	public double getDeltaScore() {
		return deltaScore;
	}

	public double getDeltaScoreOther() {
		return deltaScoreOther;
	}

	public int getRecordNumber() {
		return recordNumber;
	}

	public int getDbFilePos() {
		return dbFilePos;
	}

	public int getSpecFilePos() {
		return specFilePos;
	}

	public double getSpecProb() {
		return specProb;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + charge;
		result = prime * result + dbFilePos;
		long temp;
		temp = Double.doubleToLongBits(deltaScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(deltaScoreOther);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(fScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(fractionB);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(fractionY);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(intensity);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(length);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(medianPrmScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(mqScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((nextAA == null) ? 0 : nextAA.hashCode());
		result = prime * result + ntt;
		temp = Double.doubleToLongBits(pValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((prevAA == null) ? 0 : prevAA.hashCode());
		result = prime * result + recordNumber;
		result = prime * result + scan;
		result = prime * result
				+ ((sequence == null) ? 0 : sequence.hashCode());
		result = prime * result + specFilePos;
		temp = Double.doubleToLongBits(specProb);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(totalPrmScore);
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
		MsgfPeptide other = (MsgfPeptide) obj;
		if (charge != other.charge)
			return false;
		if (dbFilePos != other.dbFilePos)
			return false;
		if (Double.doubleToLongBits(deltaScore) != Double
				.doubleToLongBits(other.deltaScore))
			return false;
		if (Double.doubleToLongBits(deltaScoreOther) != Double
				.doubleToLongBits(other.deltaScoreOther))
			return false;
		if (Double.doubleToLongBits(fScore) != Double
				.doubleToLongBits(other.fScore))
			return false;
		if (Double.doubleToLongBits(fractionB) != Double
				.doubleToLongBits(other.fractionB))
			return false;
		if (Double.doubleToLongBits(fractionY) != Double
				.doubleToLongBits(other.fractionY))
			return false;
		if (Double.doubleToLongBits(intensity) != Double
				.doubleToLongBits(other.intensity))
			return false;
		if (Double.doubleToLongBits(length) != Double
				.doubleToLongBits(other.length))
			return false;
		if (Double.doubleToLongBits(medianPrmScore) != Double
				.doubleToLongBits(other.medianPrmScore))
			return false;
		if (Double.doubleToLongBits(mqScore) != Double
				.doubleToLongBits(other.mqScore))
			return false;
		if (nextAA == null) {
			if (other.nextAA != null)
				return false;
		} else if (!nextAA.equals(other.nextAA))
			return false;
		if (ntt != other.ntt)
			return false;
		if (Double.doubleToLongBits(pValue) != Double
				.doubleToLongBits(other.pValue))
			return false;
		if (prevAA == null) {
			if (other.prevAA != null)
				return false;
		} else if (!prevAA.equals(other.prevAA))
			return false;
		if (recordNumber != other.recordNumber)
			return false;
		if (scan != other.scan)
			return false;
		if (sequence == null) {
			if (other.sequence != null)
				return false;
		} else if (!sequence.equals(other.sequence))
			return false;
		if (specFilePos != other.specFilePos)
			return false;
		if (Double.doubleToLongBits(specProb) != Double
				.doubleToLongBits(other.specProb))
			return false;
		if (Double.doubleToLongBits(totalPrmScore) != Double
				.doubleToLongBits(other.totalPrmScore))
			return false;
		return true;
	}
}
