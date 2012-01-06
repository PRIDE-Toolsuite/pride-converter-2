package uk.ac.ebi.pride.tools.converter.gui.component.table.model;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 13/12/11
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
public class ParserOptionCellEditor extends DefaultCellEditor {

    private JComponent component = new JTextField();

    public ParserOptionCellEditor() {
        super(new JTextField());
        setClickCountToStart(1);
    }

    @Override
    public Object getCellEditorValue() {
        if (component instanceof JCheckBox) {
            return ((JCheckBox) component).isSelected();
        } else if (component instanceof JComboBox) {
            return ((JComboBox) component).getSelectedItem();
        } else {
            return ((JTextField) component).getText();
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value instanceof Boolean) {
            component = new JCheckBox();
            ((JCheckBox) component).setSelected(Boolean.valueOf(value.toString()));
            ((JCheckBox) component).setHorizontalAlignment(JLabel.CENTER);
        } else {
            component = new JTextField(value.toString());
        }
        return component;
    }

}
