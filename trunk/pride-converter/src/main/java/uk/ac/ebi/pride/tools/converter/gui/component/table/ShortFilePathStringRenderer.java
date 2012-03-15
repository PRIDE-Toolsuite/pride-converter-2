package uk.ac.ebi.pride.tools.converter.gui.component.table;

import uk.ac.ebi.pride.tools.converter.gui.util.IOUtilities;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 15/03/12
 * Time: 13:47
 */
public class ShortFilePathStringRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        String shortPath = null;
        if (value != null) {
            shortPath = IOUtilities.getShortSourceFilePath(value.toString());
        }
        return super.getTableCellRendererComponent(table, shortPath, isSelected, hasFocus, row, column);

    }
}
