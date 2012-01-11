package uk.ac.ebi.pride.tools.converter.gui.component.table.model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 13/12/11
 * Time: 14:38
 */
public class ExperimentDetailMultiTableModel extends AbstractTableModel {

    public static final String EXPERIMENT_TITLE = "Experiment Title";
    public static final String SHORT_LABEL = "Short Label";

    private String[] COLUMN_HEADERS = {"Source File", EXPERIMENT_TITLE, SHORT_LABEL};
    private boolean[] COLUMN_EDITABLE = {false, true, true};
    private List<String> fileNames;
    private List<String> experimentTitles = new ArrayList<String>();
    private List<String> shortLabels = new ArrayList<String>();

    public ExperimentDetailMultiTableModel(List<String> files, String experimentTitle, String experimentShortLabel) {
        fileNames = files;
        for (String f : files) {
            experimentTitles.add(experimentTitle);
            shortLabels.add(experimentShortLabel);
        }
    }

    /**
     * Returns the number of rows in the model. A
     * <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it
     * is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    @Override
    public int getRowCount() {
        return fileNames.size();
    }

    /**
     * Returns the number of columns in the model. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     *
     * @return the number of columns in the model
     * @see #getRowCount
     */
    @Override
    public int getColumnCount() {
        return COLUMN_HEADERS.length;
    }

    /**
     * Returns the name of the column at <code>columnIndex</code>.  This is used
     * to initialize the table's column header name.  Note: this name does
     * not need to be unique; two columns in a table can have the same name.
     *
     * @param columnIndex the index of the column
     * @return the name of the column
     */
    @Override
    public String getColumnName(int columnIndex) {
        return COLUMN_HEADERS[columnIndex];
    }

    /**
     * Returns the most specific superclass for all the cell values
     * in the column.  This is used by the <code>JTable</code> to set up a
     * default renderer and editor for the column.
     *
     * @param columnIndex the index of the column
     * @return the common ancestor class of the object values in the model.
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    /**
     * Returns true if the cell at <code>rowIndex</code> and
     * <code>columnIndex</code>
     * is editable.  Otherwise, <code>setValueAt</code> on the cell will not
     * change the value of that cell.
     *
     * @param rowIndex    the row whose value to be queried
     * @param columnIndex the column whose value to be queried
     * @return true if the cell is editable
     * @see #setValueAt
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return COLUMN_EDITABLE[columnIndex];
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>.
     *
     * @param rowIndex    the row whose value is to be queried
     * @param columnIndex the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return fileNames.get(rowIndex);
            case 1:
                return experimentTitles.get(rowIndex);
            case 2:
                return shortLabels.get(rowIndex);
            default:
                throw new IllegalStateException("No column handler defined for column index: " + columnIndex);
        }
    }

    /**
     * Sets the value in the cell at <code>columnIndex</code> and
     * <code>rowIndex</code> to <code>aValue</code>.
     *
     * @param aValue      the new value
     * @param rowIndex    the row whose value is to be changed
     * @param columnIndex the column whose value is to be changed
     * @see #getValueAt
     * @see #isCellEditable
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                break;
            case 1:
                experimentTitles.set(rowIndex, (String) aValue);
                break;
            case 2:
                shortLabels.set(rowIndex, (String) aValue);
                break;
            default:
                throw new IllegalStateException("No column handler defined for column index: " + columnIndex);
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public boolean isValid() {
        boolean allValid = true;
        for (String s : experimentTitles) {
            if (s == null || s.trim().equals("")) {
                allValid = false;
                break;
            }
        }
        if (allValid) {
            for (String s : shortLabels) {
                if (s == null || s.trim().equals("")) {
                    allValid = false;
                    break;
                }
            }
        }
        return allValid;
    }

}
