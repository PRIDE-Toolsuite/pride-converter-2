package uk.ac.ebi.pride.tools.converter.gui.component.table;

import uk.ac.ebi.pride.tools.converter.gui.component.table.model.ExperimentDetailMultiTableModel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 13/12/11
 * Time: 15:03
 */
public class ExperimentDetailMultiTable extends JTable {

    public ExperimentDetailMultiTable() {
        super(new ExperimentDetailMultiTableModel(new ArrayList<String>(), "", ""));
        putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        setDefaultEditor(String.class, new QuickStringCellEditor());
    }

    public ExperimentDetailMultiTable(List<String> files, String experimentTitle, String shortLabel) {
        super(new ExperimentDetailMultiTableModel(files, experimentTitle, shortLabel));
        putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        setDefaultEditor(String.class, new QuickStringCellEditor());
    }

    public TableCellRenderer getCellRenderer(int row, int column) {
        Object value = getValueAt(row, column);
        if (value != null) {
            return getDefaultRenderer(String.class);
        }
        return super.getCellRenderer(row, column);
    }

    private class QuickStringCellEditor extends DefaultCellEditor {

        private JTextField component = new JTextField();

        public QuickStringCellEditor() {
            super(new JTextField());
            setClickCountToStart(1);
        }

        @Override
        public Object getCellEditorValue() {
            return component.getText();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            component = new JTextField(value.toString());
            return component;
        }

    }

}
