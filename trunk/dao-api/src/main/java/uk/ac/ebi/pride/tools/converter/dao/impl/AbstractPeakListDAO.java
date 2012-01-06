package uk.ac.ebi.pride.tools.converter.dao.impl;

import java.util.List;

/**
 * This class should be used as superclass
 * for all peak list DAOs. It only adds
 * the required function getSpectrumIds.
 *
 * @author jg
 */
public abstract class AbstractPeakListDAO extends AbstractDAOImpl {
    /**
     * Returns a list of spectra ids. The order of the
     * ids in the list should be the same in which the
     * spectra are returned by the spectrum iterator.
     * Furthermore, the position in the array should
     * resemble the spectrum's 1-based id.
     * For DAOs handling one spectrum per file the id
     * should be the filename. For DAOs processing
     * peak list formats with inbuilt ids (f.e. mzML)
     * the spectrum's id should be returned. For formats
     * not containing an "inbuilt" index the 0-based index
     * of the spectrum in the file should be returned.
     *
     * @return
     */
    public abstract List<String> getSpectraIds();
}
