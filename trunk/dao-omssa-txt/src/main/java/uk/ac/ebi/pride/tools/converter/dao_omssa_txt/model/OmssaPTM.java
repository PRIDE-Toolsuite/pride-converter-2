package uk.ac.ebi.pride.tools.converter.dao_omssa_txt.model;

import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: rcote
 * Date: 24/10/12
 * Time: 14:28
 * To change this template use File | Settings | File Templates.
 */
public class OmssaPTM {


    private Integer modNumber;
    private String modName;
    private Double modMonoMass;
    private Vector<String> modResidues;
    private String psiModName;
    private String unimodAc;

    /**
     * Creates a new OmssaModification object.
     *
     * @param modNumber
     * @param modName
     * @param modMonoMass
     * @param modResidues
     */
    public OmssaPTM(Integer modNumber, String modName, Double modMonoMass, Vector<String> modResidues, String unimodAc, String psiModName) {
        this.modNumber = modNumber;
        this.modName = modName;
        this.modMonoMass = modMonoMass;
        this.modResidues = modResidues;
        this.unimodAc = unimodAc;
        this.psiModName = psiModName;
    }

    /**
     * Returns the modification number
     *
     * @return the modification number
     */
    public Integer getModNumber() {
        return modNumber;
    }

    /**
     * Returns the modification name
     *
     * @return the modification name
     */
    public String getModName() {
        return modName;
    }

    /**
     * Returns the modification mass
     *
     * @return the modification mass
     */
    public Double getModMonoMass() {
        return modMonoMass;
    }

    /**
     * Returns the modified residues
     *
     * @return the modified residues
     */
    public Vector<String> getModResidues() {
        return modResidues;
    }

    /**
     * Returns the modified residues as a String
     *
     * @return the modified residues
     */
    public String getModResiduesAsString() {

        String temp = "";

        for (int i = 0; i < modResidues.size(); i++) {
            temp += modResidues.get(i) + ", ";
        }

        // remove the ", " at the end
        if (temp.length() > 0) {
            temp = temp.substring(0, temp.length() - 2);
        }

        return temp;
    }

    public String getPsiModName() {
        return psiModName;
    }

    public String getUnimodAc() {
        return unimodAc;
    }

}


