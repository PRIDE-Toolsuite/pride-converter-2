package uk.ac.ebi.pride.tools.converter.gui.component.table.model;

import uk.ac.ebi.pride.tools.converter.gui.component.table.DeleteIconColumn;
import uk.ac.ebi.pride.tools.converter.gui.component.table.RowNumberRenderer;
import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: melih
 * Date: 16/03/2011
 * Time: 14:22
 */
public abstract class BaseTableModel<T extends ReportObject> extends DefaultTableModel {

    // Resouce bundle to get messages, and internalization
    protected static final ResourceBundle resourceBundle = ResourceBundle.getBundle("messages");

    public static final int SMALL_WIDTH = 25;

    // type for each column
    protected Class[] columnTypes;
    // to get cell is editable or not
    protected boolean[] columnEditable;
    // column names
    protected String[] columnNames;
    //index of the column that contains the actual object - NEEDS TO BE UPDATED BY INHERITED CLASS
    protected int dataColumnIndex;

    // table column model for the parent
    protected TableColumnModel tableColumnModel;

    public List<T> getList() {
        ArrayList<T> data = new ArrayList<T>();
        for (int i = 0; i < getRowCount(); i++) {
            data.add((T) getValueAt(i, dataColumnIndex));
        }
        return data;
    }

    public T get(int index) {
        return (T) getValueAt(index, dataColumnIndex);
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
        return columnNames.length;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return this.columnEditable[columnIndex];
    }

    public void addRecord(T t) {
        Object[] tmp = getRowObjectArray(t);
        if (tmp != null) {
            addRow(tmp);
        }
    }

    public TableColumnModel getTableColumnModel(JTable table) {
        tableColumnModel = table.getColumnModel();
        constructTableColumnModel(table);
        return tableColumnModel;
    }

    protected void constructTableColumnModel(JTable table) {
        tableColumnModel.getColumn(0).setMaxWidth(SMALL_WIDTH);
        tableColumnModel.getColumn(0).setMinWidth(SMALL_WIDTH);
        tableColumnModel.getColumn(0).setPreferredWidth(SMALL_WIDTH);
        tableColumnModel.getColumn(0).setCellRenderer(new RowNumberRenderer());

        //second to last column will be the delete column
        tableColumnModel.getColumn(getColumnCount() - 2).setMaxWidth(SMALL_WIDTH);
        tableColumnModel.getColumn(getColumnCount() - 2).setMinWidth(SMALL_WIDTH);
        tableColumnModel.getColumn(getColumnCount() - 2).setPreferredWidth(SMALL_WIDTH);
        //Add delete column to table
        new DeleteIconColumn(table, tableColumnModel.getColumn(getColumnCount() - 2));

        //last column will contain the object itself
        tableColumnModel.getColumn(getColumnCount() - 1).setMaxWidth(0);
        tableColumnModel.getColumn(getColumnCount() - 1).setMinWidth(0);
        tableColumnModel.getColumn(getColumnCount() - 1).setPreferredWidth(0);
    }

    public void removeAll() {
        while (getRowCount() > 0) {
            super.removeRow(0);
        }
    }

    public void edit(int row, T objToEdit) {
        removeRow(row);
        insertRow(row, getRowObjectArray(objToEdit));
    }

    protected abstract Object[] getRowObjectArray(T t);

    /**
     * Indicates whether a row should be protected (i.e. not editable) even though the rest
     * of the table might be editable
     *
     * @param rowNumber
     * @return
     */
    public boolean isRowProtected(int rowNumber) {
        return false;
    }

}
