package uk.ac.ebi.pridemod.slimmod.model;


import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 19/07/11
 * Time: 17:12
 * To change this template use File | Settings | File Templates.
 */

/**
 * Slim Modification is an object that contains the information for slim modification.
 * Slim modifications are post-translational modifications obtained with the information
 * from PSI-MOD and Unimod database. The list of slim modifications is a short list of more
 * used post-translational modification in the field of proteomics studies. Each modification
 * contain the delta mass of the modification and the specificity sites of the modification.
 */
public class SlimModification {

    // Identificator
    private String idPsiMod = null;

    private double deltaMass = 0.0;

    private int idUnimod = 0;

    private String psiModDesc = null;

    private String shortNamePsiMod = null;

    private List<Specificity> specificityCollection = null;

    /**
     * Constructor of the Slim Modification Class using as a parameters all attributes
     * of the class.
     *
     * @param idPsiMod              PSI-Mod database identifier.
     * @param deltaMass             delta mass of the PSI-MOD modification
     * @param idUnimod              Unimod database identifier.
     * @param psiModDesc            Name in PsiMod
     * @param shortNamePsiMod       short name in PsI Mod.
     * @param specificityCollection a collection of possible specificities of the current modification.
     */
    public SlimModification(String idPsiMod, double deltaMass, int idUnimod, String psiModDesc, String shortNamePsiMod, List<Specificity> specificityCollection) {
        this.idPsiMod = idPsiMod;
        this.deltaMass = deltaMass;
        this.idUnimod = idUnimod;
        this.psiModDesc = psiModDesc;
        this.shortNamePsiMod = shortNamePsiMod;
        this.specificityCollection = specificityCollection;
    }

    /**
     * Get the PSI-Mod database identifier.
     *
     * @return
     */
    public String getIdPsiMod() {
        return idPsiMod;
    }

    /**
     * Set the PSI-Mod database identifier.
     *
     * @param idPsiMod
     */
    public void setIdPsiMod(String idPsiMod) {
        this.idPsiMod = idPsiMod;
    }

    /**
     * Get the delta Mass of the Modification
     *
     * @return
     */
    public double getDeltaMass() {
        return deltaMass;
    }

    /**
     * Set a delta Mass of the Modification
     *
     * @param deltaMass
     */
    public void setDeltaMass(double deltaMass) {
        this.deltaMass = deltaMass;
    }

    /**
     * Get Unimod database identifier.
     *
     * @return
     */
    public int getIdUnimod() {
        return idUnimod;
    }

    /**
     * Set Unimod database identifier.
     *
     * @param idUnimod
     */
    public void setIdUnimod(int idUnimod) {
        this.idUnimod = idUnimod;
    }

    /**
     * Get Name in PSI-Mod database
     *
     * @return
     */
    public String getPsiModDesc() {
        return psiModDesc;
    }

    /**
     * Set Name in PSI-Mod database
     *
     * @param psiModDesc
     */
    public void setPsiModDesc(String psiModDesc) {
        this.psiModDesc = psiModDesc;
    }

    /**
     * Get short name in PSI-Mod database
     *
     * @return
     */
    public String getShortNamePsiMod() {
        return shortNamePsiMod;
    }

    /**
     * Set short name in PSI-Mod database
     *
     * @param shortNamePsiMod
     */
    public void setShortNamePsiMod(String shortNamePsiMod) {
        this.shortNamePsiMod = shortNamePsiMod;
    }

    /**
     * Get a collection of possible specificities of the current modification.
     *
     * @return
     */
    public List<Specificity> getSpecificityCollection() {
        return specificityCollection;
    }

    /**
     * Set a collection of possible specificities of the current modification.
     *
     * @param specificityCollection
     */
    public void setSpecificityCollection(List<Specificity> specificityCollection) {
        this.specificityCollection = specificityCollection;
    }

    /**
     * Seach if the modification contains in the list of possible specificities the specificity
     * passed by paramters.
     *
     * @param specificity
     * @return
     */
    public boolean isSpecificity(Specificity.AminoAcid specificity) {
        for (int i = 0; i < specificityCollection.size(); i++) {
            return (this.specificityCollection.get(i).getName() == specificity) ? true : false;
        }
        return false;
    }

    /**
     * Seach if the modification contains in the list of possible specificities the specificity
     * passed by paramters and also filter by the delta mass of the modification.
     *
     * @param specificity
     * @param mass
     * @param difference
     * @return
     */
    public boolean isSpecificity(Specificity.AminoAcid specificity, double mass, double difference) {
        if (Math.abs(this.deltaMass - mass) < difference) {
            for (int i = 0; i < specificityCollection.size(); i++) {
                return (this.specificityCollection.get(i).getName() == specificity) ? true : false;
            }
        }
        return false;
    }
}