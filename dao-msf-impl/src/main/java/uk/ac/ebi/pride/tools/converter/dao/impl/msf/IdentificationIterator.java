/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.pride.tools.converter.dao.impl.msf;

import com.compomics.thermo_msf_parser.gui.MsfFile;
import com.compomics.thermo_msf_parser.msf.PeptideLowMemController;
import com.compomics.thermo_msf_parser.msf.ProteinLowMem;
import com.compomics.thermo_msf_parser.msf.ProteinLowMemController;
import java.util.Iterator;
import uk.ac.ebi.pride.tools.converter.dao.impl.msf.converters.IdentificationConverter;
import uk.ac.ebi.pride.tools.converter.report.model.Identification;

/**
 *
 * @author toorn101
 */
public class IdentificationIterator implements Iterator<Identification> {

    private String searchDatabaseName;
    private String searchDatabaseVersion;
    private boolean preScanMode;
    private Integer confidenceLevel;
    private Iterator<ProteinLowMem> proteinIterator;
    private boolean hasNext = false;
    private Identification next;
    private ProteinLowMemController proteins = new ProteinLowMemController();
    private PeptideLowMemController peptides = new PeptideLowMemController();
    private MsfFile msfFile;

    public IdentificationIterator(MsfFile msfFile, String searchDatabaseName, String searchDatabaseVersion, boolean preScanMode, Integer confidenceLevel) {
        this.searchDatabaseName = searchDatabaseName;
        this.searchDatabaseVersion = searchDatabaseVersion;
        this.preScanMode = preScanMode;
        this.confidenceLevel = confidenceLevel;
        //TODO switch to just proteinslist
        this.msfFile = msfFile;
        this.proteinIterator = proteins.getAllProteins(msfFile.getConnection());
        prepareNext();
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public Identification next() {
        Identification result = next;
        prepareNext();
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Don't try to remove.");
    }

    private void prepareNext() {
        hasNext = false;
        while (proteinIterator.hasNext()) {
            ProteinLowMem nextProtein = proteinIterator.next();
            if (nextProtein != null && peptides.getPeptidesForProtein(nextProtein,msfFile.getVersion(),msfFile.getAminoAcids()).size() > 0) {
                next = IdentificationConverter.convert(nextProtein, searchDatabaseName, searchDatabaseVersion, preScanMode, confidenceLevel, msfFile);
                hasNext = true;
                break;
            }
        }
    }
}
