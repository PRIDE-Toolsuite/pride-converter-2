package uk.ac.ebi.pride.tools.converter.gui.component.table;

import uk.ac.ebi.pride.tools.converter.gui.component.table.model.ExperimentDetailMultiTableModel;
import uk.ac.ebi.pride.tools.converter.gui.util.Colours;
import uk.ac.ebi.pride.tools.converter.utils.config.Configurator;

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

    private void initTable() {

        //update table properties
        putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        setDefaultEditor(String.class, new QuickStringCellEditor());

        setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        //register keystrokes
        if (Configurator.getOSName().toLowerCase().contains("mac")) {
            getInputMap().put(TableCopyAction.MAC_COPY_KEYSTROKE, TableCopyAction.COPY);
            getActionMap().put(TableCopyAction.COPY, new TableCopyAction(this));
            getInputMap().put(TablePasteAction.MAC_PASTE_KEYSTROKE, TablePasteAction.PASTE);
            getActionMap().put(TablePasteAction.PASTE, new TablePasteAction(this));
        } else {
            getInputMap().put(TableCopyAction.COPY_KEYSTROKE, TableCopyAction.COPY);
            getActionMap().put(TableCopyAction.COPY, new TableCopyAction(this));
            getInputMap().put(TablePasteAction.PASTE_KEYSTROKE, TablePasteAction.PASTE);
            getActionMap().put(TablePasteAction.PASTE, new TablePasteAction(this));
        }

        setRowSelectionAllowed(true);
        setColumnSelectionAllowed(true);

    }

    public ExperimentDetailMultiTable() {
        super(new ExperimentDetailMultiTableModel(new ArrayList<String>(), "", ""));
        initTable();
    }

    public ExperimentDetailMultiTable(List<String> files, String experimentTitle, String shortLabel) {
        super(new ExperimentDetailMultiTableModel(files, experimentTitle, shortLabel));
        initTable();
    }

    public TableCellRenderer getCellRenderer(int row, int column) {
        Object value = getValueAt(row, column);
        if (value != null) {
            if (column == 0) {
                return new ShortFilePathStringRenderer();
            } else {
                return getDefaultRenderer(String.class);
            }
        }
        return super.getCellRenderer(row, column);
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer,
                                     int rowIndex, int vColIndex) {

        Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);

        if (isCellSelected(rowIndex, vColIndex)) {
            c.setBackground(Colours.lightBlue);
        } else {

            //get object at row
            int modelSelectedRow = convertRowIndexToModel(rowIndex);
            //get row state
            ExperimentDetailMultiTableModel model = (ExperimentDetailMultiTableModel) getModel();
            if (model.isErrorDetectedInRow(modelSelectedRow)) {
                c.setBackground(Colours.errorRed);
            } else {
                //no errors - use alternate colors
                if (rowIndex % 2 == 0) {
                    c.setBackground(Colours.grey);
                } else {
                    c.setBackground(getBackground());
                }
            }

        }
        return c;
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
