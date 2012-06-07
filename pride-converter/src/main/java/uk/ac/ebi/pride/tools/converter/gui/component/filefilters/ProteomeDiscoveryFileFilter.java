package uk.ac.ebi.pride.tools.converter.gui.component.filefilters;

import uk.ac.ebi.pride.tools.converter.utils.FileUtils;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 07/06/12
 * Time: 17:14
 * To change this template use File | Settings | File Templates.
 */
public class ProteomeDiscoveryFileFilter extends FileFilter {

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        String extension = FileUtils.getExtension(file);
        if (extension != null) {
            return (extension.equalsIgnoreCase(FileUtils.msf));
        } else return false;
    }

    @Override
    public String getDescription() {
        return "Proteome Discoverer Files (.msf)";
    }

}
