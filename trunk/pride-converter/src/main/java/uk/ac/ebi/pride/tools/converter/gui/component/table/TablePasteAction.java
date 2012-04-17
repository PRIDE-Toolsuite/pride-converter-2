package uk.ac.ebi.pride.tools.converter.gui.component.table;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.util.StringTokenizer;

public class TablePasteAction extends AbstractAction {

    public static final String PASTE = "Paste";
    public static final KeyStroke MAC_PASTE_KEYSTROKE = KeyStroke.getKeyStroke("meta V");
    public static final KeyStroke PASTE_KEYSTROKE = KeyStroke.getKeyStroke("ctrl V");

    private JTable mappingTable;
    private Clipboard system;

    public TablePasteAction(JTable mappingTable) {
        this.mappingTable = mappingTable;
        system = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        int startRow = (mappingTable.getSelectedRows())[0];
        int startCol = (mappingTable.getSelectedColumns())[0];
        try {
            String trstring = (String) (system.getContents(this).getTransferData(DataFlavor.stringFlavor));
            StringTokenizer st1 = new StringTokenizer(trstring, "\n");
            for (int i = 0; st1.hasMoreTokens(); i++) {
                String rowstring = st1.nextToken();
                StringTokenizer st2 = new StringTokenizer(rowstring, "\t");
                for (int j = 0; st2.hasMoreTokens(); j++) {
                    String value = st2.nextToken();
                    if (startRow + i < mappingTable.getRowCount() && startCol + j < mappingTable.getColumnCount()) {
                        if (mappingTable.isCellEditable(startRow + i, startCol + j)) {
                            mappingTable.setValueAt(value, startRow + i, startCol + j);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
