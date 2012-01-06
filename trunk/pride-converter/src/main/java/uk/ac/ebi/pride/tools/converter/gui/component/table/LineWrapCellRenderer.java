package uk.ac.ebi.pride.tools.converter.gui.component.table;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class LineWrapCellRenderer extends JTextArea implements TableCellRenderer {

    private static final double PADDING_FACTOR = 1.125;

    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {

        setText((String) value);
        setWrapStyleWord(true);
        setLineWrap(true);

        int fontHeight = getFontMetrics(getFont()).getHeight();
        int textLength = getFontMetrics(getFont()).stringWidth(getText());

        int lines = textLength / table.getColumnModel().getColumn(2).getWidth() + 2; //+2, cause we need at least 1 row and some padding.
        int height = (int) Math.ceil(fontHeight * lines * PADDING_FACTOR);

        if (height > table.getRowHeight(row)) {
            table.setRowHeight(row, height);
        }

        //FATAL > ERROR > WARN > INFO > DEBUG
        if ("FATAL".equals(value)) {
            this.setBackground(new Color(51, 51, 51));
            this.setForeground(Color.WHITE);
        } else if ("ERROR".equals(value)) {
            this.setBackground(new Color(255, 51, 51));
            this.setForeground(Color.WHITE);
        } else if ("WARN".equals(value)) {
            this.setBackground(new Color(255, 255, 51));
            this.setForeground(Color.BLACK);
        } else if ("INFO".equals(value)) {
            this.setBackground(new Color(51, 51, 255));
            this.setForeground(Color.WHITE);
        } else if ("DEBUG".equals(value)) {
            this.setBackground(Color.WHITE);
            this.setForeground(Color.BLACK);
        }
        return this;
    }

}



