package uk.ac.ebi.pride.tools.converter.gui.component.table.model;

import uk.ac.ebi.pride.tools.converter.gui.component.table.DeleteIconColumn;
import uk.ac.ebi.pride.tools.converter.gui.component.table.RowNumberRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author melih
 *         Date: 25/05/2011
 *         Time: 11:28
 */
public class FileTableModel extends DefaultTableModel {

    protected static final int WIDTH = 25;

    protected List<File> list;

    // type for each column
    protected Class[] columnTypes;
    // to get cell is editable or not
    protected boolean[] columnEditable;
    // column names
    protected String[] columnNames;

    // table column model for the parent
    protected TableColumnModel tableColumnModel;

    // column count
    protected int columnCount;

    public FileTableModel() {

        columnCount = 4;
        list = new ArrayList<File>();
        columnNames = new String[]{"", "Name", "Path", ""};
        columnEditable = new boolean[]{false, false, false, true};
        columnTypes = new Class<?>[]{String.class, String.class, String.class, String.class};

    }

    protected Object[] getRowObjectArray(File file) {
        if (file == null)
            return new Object[0];
        else
            return new Object[]{"", file.getName(), file.getPath(), ""};
    }

    protected void constructTableColumnModel(JTable table) {
        tableColumnModel.getColumn(0).setMaxWidth(WIDTH);
        tableColumnModel.getColumn(0).setMinWidth(WIDTH);
        tableColumnModel.getColumn(0).setPreferredWidth(WIDTH);
        tableColumnModel.getColumn(0).setCellRenderer(new RowNumberRenderer());

        tableColumnModel.getColumn(columnCount - 1).setMaxWidth(WIDTH);
        tableColumnModel.getColumn(columnCount - 1).setMinWidth(WIDTH);
        tableColumnModel.getColumn(columnCount - 1).setPreferredWidth(WIDTH);
        //Add delete column to table
        new DeleteIconColumn(table, tableColumnModel.getColumn(columnCount - 1));
    }


    public List<File> getList() {
        return list;
    }

    public File get(int index) {
        return list.get(index);
    }

    @Override
    public String getColumnName(int column) throws IllegalArgumentException {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnTypes[columnIndex];
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return this.columnEditable[columnIndex];
    }

    public void addRecord(File File) {
        Object[] tmp = getRowObjectArray(File);
        if (tmp != null) {
            //need to add file to list before calling addRow because of timing of listeners
            list.add(File);
            addRow(tmp);
        }
    }

    @Override
    public void removeRow(int index) {
        //need to remove file to list before calling addRow because of timing of listeners
        list.remove(index);
        super.removeRow(index);
    }

    public TableColumnModel getTableColumnModel(JTable table) {
        tableColumnModel = table.getColumnModel();
        constructTableColumnModel(table);
        return tableColumnModel;
    }

    public void removeAll() {
        ArrayList<File> newList = new ArrayList<File>(list);
        int index = 0;
        for (File File : newList) {
            removeRow(index);
        }
    }

}
