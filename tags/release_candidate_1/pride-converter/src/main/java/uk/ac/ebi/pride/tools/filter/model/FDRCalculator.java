package uk.ac.ebi.pride.tools.filter.model;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 06/02/12
 * Time: 15:07
 * To change this template use File | Settings | File Templates.
 */
public interface FDRCalculator {

    public static enum FDRType {
        PEPTIDE,
        PROTEIN,
        BOTH
    }

    public FDRType getFDRType();

    public double getProteinFalseDiscoveryRate();

    public double getPeptideFalseDiscoveryRate();

}
