package uk.ac.ebi.pride.tools.converter.gui.component.list;

import uk.ac.ebi.pride.tools.converter.gui.util.IOUtilities;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 05/04/12
 * Time: 14:20
 */
public class ShortFilePathListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Object shortFilePath = value;
        if (value != null) {
            shortFilePath = IOUtilities.getShortSourceFilePath(value.toString());
        }
        return super.getListCellRendererComponent(list, shortFilePath, index, isSelected, cellHasFocus);
    }
}
