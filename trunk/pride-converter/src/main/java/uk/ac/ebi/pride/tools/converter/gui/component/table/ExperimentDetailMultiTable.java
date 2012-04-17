package uk.ac.ebi.pride.tools.converter.gui.component.table;

import uk.ac.ebi.pride.tools.converter.gui.component.table.model.ExperimentDetailMultiTableModel;
import uk.ac.ebi.pride.tools.converter.gui.util.Colours;
import uk.ac.ebi.pride.tools.converter.utils.config.Configurator;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 13/12/11
 * Time: 15:03
 */
public class ExperimentDetailMultiTable extends JTable {

    private static final String COPY = "Copy";
    private static final String PASTE = "Paste";

    private Clipboard system;

    private void initTable() {

        //update table properties
        putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        setDefaultEditor(String.class, new QuickStringCellEditor());
        setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        //register keystrokes
        if (Configurator.getOSName().toLowerCase().contains("mac")) {
            getInputMap().put(KeyStroke.getKeyStroke("meta C"), COPY);
            getActionMap().put(COPY, new CopyAction());
            getInputMap().put(KeyStroke.getKeyStroke("meta V"), PASTE);
            getActionMap().put(PASTE, new PasteAction());
        } else {
            getInputMap().put(KeyStroke.getKeyStroke("ctrl C"), COPY);
            getActionMap().put(COPY, new CopyAction());
            getInputMap().put(KeyStroke.getKeyStroke("ctrl V"), PASTE);
            getActionMap().put(PASTE, new PasteAction());
        }

        system = Toolkit.getDefaultToolkit().getSystemClipboard();

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

    private class CopyAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {

            StringBuffer sbf = new StringBuffer();
            // Check to ensure we have selected only a contiguous
            // block of cells
            int numcols = getSelectedColumnCount();
            int numrows = getSelectedRowCount();
            int[] rowsselected = getSelectedRows();
            int[] colsselected = getSelectedColumns();
            if (!((numrows - 1 == rowsselected[rowsselected.length - 1] - rowsselected[0] &&
                    numrows == rowsselected.length) &&
                    (numcols - 1 == colsselected[colsselected.length - 1] - colsselected[0] &&
                            numcols == colsselected.length))) {
                JOptionPane.showMessageDialog(null, "Invalid Copy Selection", "Invalid Copy Selection", JOptionPane.ERROR_MESSAGE);
                return;
            }
            for (int i = 0; i < numrows; i++) {
                for (int j = 0; j < numcols; j++) {
                    sbf.append(getValueAt(rowsselected[i], colsselected[j]));
                    if (j < numcols - 1) {
                        sbf.append("\t");
                    }
                }
                sbf.append("\n");
            }
            StringSelection stsel = new StringSelection(sbf.toString());
            system = Toolkit.getDefaultToolkit().getSystemClipboard();
            system.setContents(stsel, stsel);

        }
    }

    private class PasteAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {

            int startRow = (getSelectedRows())[0];
            int startCol = (getSelectedColumns())[0];
            try {
                String trstring = (String) (system.getContents(this).getTransferData(DataFlavor.stringFlavor));
                StringTokenizer st1 = new StringTokenizer(trstring, "\n");
                for (int i = 0; st1.hasMoreTokens(); i++) {
                    String rowstring = st1.nextToken();
                    StringTokenizer st2 = new StringTokenizer(rowstring, "\t");
                    for (int j = 0; st2.hasMoreTokens(); j++) {
                        String value = st2.nextToken();
                        if (startRow + i < getRowCount() && startCol + j < getColumnCount()) {
                            setValueAt(value, startRow + i, startCol + j);
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
