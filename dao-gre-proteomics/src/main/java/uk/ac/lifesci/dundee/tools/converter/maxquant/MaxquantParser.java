package uk.ac.lifesci.dundee.tools.converter.maxquant;

import uk.ac.ebi.pride.tools.converter.report.model.Identification;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;
import uk.ac.ebi.pride.tools.converter.report.model.Param;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 23/01/13
 * Time: 11:11
 */
public class MaxquantParser {

    private String maxquantFilePath;

    public MaxquantParser(String maxquantFilePath) {
        this.maxquantFilePath = maxquantFilePath;
    }

    public Param getProcessingMethod() {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public String getSearchDatabaseVersion() {
        return null;
    }

    public String getSearchDatabaseName() {
        return null;
    }

    public Collection<PTM> getPTMs() {
        return Collections.emptyList();
    }

    public int getSpectrumReferenceForPeptideUID(String peptideUID) {
        return 0;
    }

    public Identification getIdentificationByUID(String identificationUID) {
        return null;
    }

    public Iterator<Identification> getIdentificationIterator(boolean prescanMode) {
        List<Identification> empty = Collections.emptyList();
        return empty.iterator();
    }

}
