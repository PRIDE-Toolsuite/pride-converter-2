package uk.ac.ebi.pride.tools.converter.gui.component.table.model;

import uk.ac.ebi.pride.tools.converter.dao.DAOProperty;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 13/12/11
 * Time: 14:38
 */
public class ParserOptionTableModel implements TableModel {

    private List<DAOProperty> properties = new ArrayList<DAOProperty>();
    private String[] COLUMN_HEADERS = {"Property Name", "Property Value"};
    private boolean[] COLUMN_EDITABLE = {false, true};
    private List<Object> values = new ArrayList<Object>();

    public ParserOptionTableModel(Collection<DAOProperty> props) {
        if (props != null) {
            properties.addAll(props);
            initValues(properties);
        }
    }

    private void initValues(List<DAOProperty> props) {
        for (DAOProperty prop : props) {
            if (prop.getValueClass().equals(Boolean.class)) {
                values.add(prop.getDefaultValue());
            } else {
                if (prop.getAllowedValues() != null && !prop.getAllowedValues().isEmpty()) {
                    values.add(prop.getAllowedValues());
                } else {
                    if (prop.getDefaultValue() == null) {
                        values.add("");
                    } else {
                        values.add(prop.getDefaultValue());
                    }
                }
            }
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
        return properties.size();
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
        DAOProperty prop = properties.get(rowIndex);
        if (columnIndex == 0) {
            //return name
            return cleanup(prop.getName());
        } else {
            //return value
            return values.get(rowIndex);
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
        if (columnIndex == 1) {
            values.set(rowIndex, aValue);
        }
    }

    /**
     * Adds a listener to the list that is notified each time a change
     * to the data model occurs.
     *
     * @param l the TableModelListener
     */
    @Override
    public void addTableModelListener(TableModelListener l) {
        /* no op */
    }

    /**
     * Removes a listener from the list that is notified each time a
     * change to the data model occurs.
     *
     * @param l the TableModelListener
     */
    @Override
    public void removeTableModelListener(TableModelListener l) {
        /* no op */
    }

    //replace "a_property_name" with "A Property Name"
    private String cleanup(String propertyName) {

        if (propertyName == null) {
            throw new IllegalArgumentException("Property name cannot be null");
        }
        StringBuilder sb = new StringBuilder();
        String[] words = propertyName.replace('_', ' ').split("\\s");
        for (String word : words) {
            //uppercase first letter
            String s = word.substring(0, 1);
            sb.append(s.toUpperCase());
            sb.append(word.substring(1));
            sb.append(" ");
        }
        return sb.toString();

    }

    public Properties getProperties() {

        //get properties
        Properties props = new Properties();
        for (int i = 0; i < properties.size(); i++) {
            String value = values.get(i).toString();
            //filter out empty string values for properties that are not defined as strings
            //this causes errors down the line
            if (!String.class.equals(properties.get(i).getValueClass()) && "".equals(value)) {
                value = null;
            }
            props.setProperty(properties.get(i).getName(), value);
        }
        return props;
    }
}
