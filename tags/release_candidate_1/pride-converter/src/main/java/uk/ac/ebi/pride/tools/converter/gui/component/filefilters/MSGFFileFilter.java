package uk.ac.ebi.pride.tools.converter.gui.component.filefilters;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import uk.ac.ebi.pride.tools.converter.utils.FileUtils;

public class MSGFFileFilter extends FileFilter {

	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
            return true;
        }
        String extension = FileUtils.getExtension(file);
        if (extension != null) {
            return (extension.equalsIgnoreCase(".msgf"));
        } else return false;
	}

	@Override
	public String getDescription() {
		return "MSGF file (.msgf)";
	}

}
