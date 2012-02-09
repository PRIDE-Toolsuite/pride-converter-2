package uk.ac.ebi.pride.tools.converter.gui.component.filefilters;

import uk.ac.ebi.pride.tools.converter.utils.FileUtils;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 08/09/11
 * Time: 10:56
 */
public class MzDataFileFilter extends FileFilter {

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        String extension = FileUtils.getExtension(file);
        if (extension != null) {
            return (extension.equalsIgnoreCase(FileUtils.mzData) || extension.equalsIgnoreCase(FileUtils.xml));
        } else return false;
    }

    @Override
    public String getDescription() {
        return "mzData Files";
    }


}
