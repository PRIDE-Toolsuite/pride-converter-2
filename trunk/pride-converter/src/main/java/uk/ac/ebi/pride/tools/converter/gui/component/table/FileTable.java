package uk.ac.ebi.pride.tools.converter.gui.component.table;

import uk.ac.ebi.pride.tools.converter.gui.component.table.model.FileTableModel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.File;
import java.util.Collection;

/**
 * Base Table
 *
 * @author melih
 *         Date: 06/04/2011
 *         Time: 10:33
 */
public class FileTable extends JTable {

    private static final Color grey = new Color(219, 213, 218);
    private static final Color lightBlue = new Color(100, 117, 245);

    public FileTable() {
        FileTableModel ft = new FileTableModel();
        setModel(ft);
        setColumnModel(ft.getTableColumnModel(this));
        setAutoCreateRowSorter(true);
    }

    public void addFile(File File) {
        TableModel model = getModel();
        ((FileTableModel) model).addRecord(File);
    }

    public void addFiles(Collection<File> collection) {
        for (File File : collection) {
            addFile(File);
        }
    }

    public Collection<File> getFiles() {
        return ((FileTableModel) getModel()).getList();
    }

    public void clearFiles() {
        FileTableModel model = (FileTableModel) getModel();
        model.removeAll();
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer,
                                     int rowIndex, int vColIndex) {
        Component c = super.prepareRenderer(renderer, rowIndex,
                vColIndex);
        if (isCellSelected(rowIndex, vColIndex)) {
            c.setBackground(lightBlue);
        } else {
            if (rowIndex % 2 == 0) {
                c.setBackground(grey);
            } else {
                c.setBackground(getBackground());
            }
        }
        return c;
    }

}
