package uk.ac.ebi.pride.tools.converter.gui.component.table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: melih
 * Date: 16/03/2011
 * Time: 13:45
 */
public class RowNumberRenderer extends DefaultTableCellRenderer {
    public RowNumberRenderer() {
        setHorizontalAlignment(JLabel.CENTER);
    }

    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setFont(getFont().deriveFont(Font.BOLD));
        } else setFont(getFont().deriveFont(Font.PLAIN));
        setText(row + 1 + "");
        setBorder(BorderFactory.createEmptyBorder());

        return this;
    }
}
