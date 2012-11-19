/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.pride.tools.converter.dao.impl.msf;

import com.compomics.thermo_msf_parser.Parser;
import com.compomics.thermo_msf_parser.msf.Protein;
import java.util.Iterator;
import uk.ac.ebi.pride.tools.converter.dao.impl.msf.converters.IdentificationConverter;
import uk.ac.ebi.pride.tools.converter.report.model.Identification;

/**
 *
 * @author toorn101
 */
public class IdentificationIterator implements Iterator<Identification> {

    private Parser parser;
    private String searchDatabaseName;
    private String searchDatabaseVersion;
    private boolean preScanMode;
    private Integer confidenceLevel;
    private Iterator<Protein> proteinIterator;
    private boolean hasNext = false;
    Identification next;

    public IdentificationIterator(Parser parser, String searchDatabaseName, String searchDatabaseVersion, boolean preScanMode, Integer confidenceLevel) {
        this.parser = parser;
        this.searchDatabaseName = searchDatabaseName;
        this.searchDatabaseVersion = searchDatabaseVersion;
        this.preScanMode = preScanMode;
        this.confidenceLevel = confidenceLevel;
        this.proteinIterator = parser.getProteins().iterator();
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
            Protein nextProtein = proteinIterator.next();
            if (nextProtein != null && nextProtein.getPeptides().size() > 0) {
                next = IdentificationConverter.convert(parser, nextProtein, searchDatabaseName, searchDatabaseVersion, preScanMode, confidenceLevel);
                hasNext = true;
                break;
            }
        }
    }
}
