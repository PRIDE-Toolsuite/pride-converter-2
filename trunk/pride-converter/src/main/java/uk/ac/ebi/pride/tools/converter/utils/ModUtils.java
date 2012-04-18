package uk.ac.ebi.pride.tools.converter.utils;

import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;
import uk.ac.ebi.pridemod.PrideModController;
import uk.ac.ebi.pridemod.slimmod.model.SlimModCollection;
import uk.ac.ebi.pridemod.slimmod.model.SlimModification;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 25/01/12
 * Time: 12:19
 */
public class ModUtils {

    public static final String TXT_MOD_FILE = "modifications.txt";
    public static final String XML_MOD_FILE = "pride_mods.xml";
    public static final double LOW_PRECISION = 0.1d;
    public static final double HIGH_PRECISION = 0.01d;

    public static final String MOD_VERSION = ResourceBundle.getBundle("gui-settings").getString("mod.database.version");
    public static final String MOD_DATABASE = "MOD";

    private static SlimModCollection preferredModifications = null;

    public static SlimModCollection getPreferredModifications() {

        //lazy load
        if (preferredModifications == null) {
            URL url = ModUtils.class.getClassLoader().getResource(XML_MOD_FILE);
            //URL url = ModUtils.class.getClassLoader().getResource(TXT_MOD_FILE);
            if (url != null) {
                preferredModifications = PrideModController.parseSlimModCollection(url);
            } else {
                throw new IllegalStateException("Could not find preferred modification file");
            }
        }
        return preferredModifications;

    }

    public static Collection<PTM> mapPreferredModifications(Collection<PTM> ptms) {

        Collection<PTM> retval = new HashSet<PTM>();
        if (ptms != null) {

            SlimModCollection preferredMods = getPreferredModifications();
            for (PTM ptm : ptms) {

                double delta = Double.NaN;
                boolean canMap = false;
                SlimModification mod = null;

                if (ptm.getModMonoDelta() != null && !ptm.getModMonoDelta().isEmpty()) {
                    delta = Double.valueOf(ptm.getModMonoDelta().get(0));
                } else if (ptm.getModAvgDelta() != null && !ptm.getModAvgDelta().isEmpty()) {
                    delta = Double.valueOf(ptm.getModAvgDelta().get(0));
                }

                //if no delta annotated in ptm
                if (delta == Double.NaN) {
                    //can't map
                    canMap = false;
                } else {

                    //map by delta
                    SlimModCollection filteredMods = preferredMods.getbyDelta(delta, LOW_PRECISION);

                    //if we have one and only one mod
                    if (filteredMods.size() == 1) {

                        canMap = true;
                        mod = filteredMods.get(0);

                    } else if (filteredMods.size() > 1) { /* more than 1 mod */

                        //check to see if we have more than 1 possible PTM
                        filteredMods = preferredMods.getbyDelta(delta, HIGH_PRECISION);
                        if (filteredMods.size() == 1) {

                            canMap = true;
                            mod = filteredMods.get(0);

                        } else {
                            //if still have more than 1 mod, can't assign mapping
                            canMap = false;
                        }

                    } else { /* no mod at all */
                        canMap = false;
                    }
                }

                if (canMap && mod != null) {

                    ptm.setModAccession(mod.getIdPsiMod());
                    ptm.setModName(mod.getShortNamePsiMod());
                    ptm.setModDatabase(MOD_DATABASE);
                    ptm.setModDatabaseVersion(MOD_VERSION);
                    ptm.getAdditional().getCvParam().clear();
                    ptm.getAdditional().getCvParam().add(new CvParam(MOD_DATABASE, mod.getIdPsiMod(), mod.getPsiModDesc(), null));

                } else {
                    //return mod unmapped
                    ptm.setModAccession(null);
                    ptm.setModName(null);
                    ptm.setModDatabase(null);
                    ptm.setModDatabaseVersion(null);
                    ptm.getAdditional().getCvParam().clear();

                }
                retval.add(ptm);
            }

        }
        return retval;

    }

    public static boolean canMapToMultiplePreferredMods(PTM ptm) {


        double delta = Double.NaN;
        boolean canMap = false;

        if (ptm.getModMonoDelta() != null && !ptm.getModMonoDelta().isEmpty()) {
            delta = Double.valueOf(ptm.getModMonoDelta().get(0));
        } else if (ptm.getModAvgDelta() != null && !ptm.getModAvgDelta().isEmpty()) {
            delta = Double.valueOf(ptm.getModAvgDelta().get(0));
        }

        //if no delta annotated in ptm
        if (delta != Double.NaN) {

            //map by delta
            SlimModCollection filteredMods = getPreferredModifications().getbyDelta(delta, LOW_PRECISION);

            //if we have one and only one mod
            if (filteredMods.size() > 1) {
                canMap = true;
            }
        }

        return canMap;

    }

}
