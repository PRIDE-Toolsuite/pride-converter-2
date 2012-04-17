package uk.ac.ebi.pride.tools.converter.gui.component.table;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

public class TableCopyAction extends AbstractAction {

    public static final String COPY = "Copy";
    public static final KeyStroke MAC_COPY_KEYSTROKE = KeyStroke.getKeyStroke("meta C");
    public static final KeyStroke COPY_KEYSTROKE = KeyStroke.getKeyStroke("ctrl C");

    private JTable mappingTable;
    private Clipboard system;

    public TableCopyAction(JTable mappingTable) {
        this.mappingTable = mappingTable;
        system = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        StringBuffer sbf = new StringBuffer();
        // Check to ensure we have selected only a contiguous
        // block of cells
        int numcols = mappingTable.getSelectedColumnCount();
        int numrows = mappingTable.getSelectedRowCount();
        int[] rowsselected = mappingTable.getSelectedRows();
        int[] colsselected = mappingTable.getSelectedColumns();
        if (!((numrows - 1 == rowsselected[rowsselected.length - 1] - rowsselected[0] &&
                numrows == rowsselected.length) &&
                (numcols - 1 == colsselected[colsselected.length - 1] - colsselected[0] &&
                        numcols == colsselected.length))) {
            JOptionPane.showMessageDialog(null, "Invalid Copy Selection", "Invalid Copy Selection", JOptionPane.ERROR_MESSAGE);
            return;
        }
        for (int i = 0; i < numrows; i++) {
            for (int j = 0; j < numcols; j++) {
                sbf.append(mappingTable.getValueAt(rowsselected[i], colsselected[j]));
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
