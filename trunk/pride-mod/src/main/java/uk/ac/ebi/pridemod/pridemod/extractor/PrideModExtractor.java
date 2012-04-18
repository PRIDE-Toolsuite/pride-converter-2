package uk.ac.ebi.pridemod.pridemod.extractor;

import org.apache.log4j.Logger;
import uk.ac.ebi.pridemod.pridemod.model.PrideMod;
import uk.ac.ebi.pridemod.pridemod.model.PrideModification;
import uk.ac.ebi.pridemod.pridemod.model.PrideModifications;

import javax.xml.bind.Unmarshaller;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 19/07/11
 * Time: 14:35
 * To change this template use File | Settings | File Templates.
 */
public class PrideModExtractor {

    private static final Logger logger = Logger.getLogger(PrideModExtractor.class.getName());

    private PrideMod prideMod = null;

    private PrideModifications modColletion = null;

    public PrideModExtractor(PrideMod prideMod) {
        this.prideMod = prideMod;
    }

    public PrideModExtractor(Unmarshaller unmarshaller) {
        this.prideMod = (PrideMod) unmarshaller;
    }

    public List<PrideModification> getModListbyMass(double mass) {
        return this.modColletion.getModbyMonoMass(mass);
    }

    public PrideModification getModbyId(int id) {
        return this.modColletion.getModbyId(id);
    }

    public List<PrideModification> getModListbySpecificity(String specificity) {
        return this.modColletion.getModListbySpecificity(specificity);
    }

    public List<PrideModification> getModListbyMassSepecificity(String specificity, double mass) {
        return this.modColletion.getListbyMassSpecificity(specificity, mass);
    }


}
