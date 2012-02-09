package uk.ac.ebi.pride.tools.converter.gui.model;

import uk.ac.ebi.pride.tools.converter.gui.component.filefilters.*;

import javax.swing.filechooser.FileFilter;


/**
 * Created by IntelliJ IDEA.
 *
 * @author melih
 *         Date: 31/05/2011
 *         Time: 10:59
 */
public enum DataType {
    PRIDE_XML("PRIDE", false, new PrideFileFilter()),
    MASCOT("MASCOT", false, new MascotFileFilter()),
    XTANDEM("XTANDEM", false, new XTandemFileFilter()),
    MZIDENTML("MZIDENTML", false, new MzIdentMLFileFilter()),
    MZML("MZML", false, new MzMLFileFilter()),
    MGF("MGF", false, new MGFFileFilter()),
    DTA_SINGLE("DTA", true, new DTAFileFilter()),
    DTA_MULTIPLE("DTA", false, new DTAFileFilter()),
    PKL_SINGLE("PKL", true, new PKLFileFilter()),
    PKL_MULTIPLE("PKL", false, new PKLFileFilter()),
    MS2("MS2", false, new MS2FileFilter()),
    MZXML("mzXML", false, new MzXMLFileFilter()),
    MZDATA("mzData", false, new MzDataFileFilter()),
    MSGF("MSGF", false, new MSGFFileFilter());

    private FileFilter filter;
    private boolean allowDirectory;
    private String engineName;

    DataType(String engineName, boolean allowDirectory, FileFilter fileFilter) {
        this.filter = fileFilter;
        this.allowDirectory = allowDirectory;
        this.engineName = engineName;
    }

    public FileFilter getFilter() {
        return filter;
    }

    public boolean isAllowDirectory() {
        return allowDirectory;
    }

    public String getEngineName() {
        return engineName;
    }
}
