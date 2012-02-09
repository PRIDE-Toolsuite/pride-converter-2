package uk.ac.ebi.pride.tools.converter.gui.component.table.model;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.gui.component.table.RowNumberRenderer;
import uk.ac.ebi.pride.tools.converter.report.model.DatabaseMapping;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author melih
 *         Date: 14/06/2011
 *         Time: 11:10
 */
public class DatabaseMappingTableModel extends BaseTableModel<DatabaseMapping> {

    private static final Logger logger = Logger.getLogger(DatabaseMappingTableModel.class);

    public DatabaseMappingTableModel() {

        String searchEngineDatabaseName = resourceBundle.getString("DBMappings.searchEngineDatabaseName");
        String searchEngineDatabaseVersion = resourceBundle.getString("DBMappings.searchEngineDatabaseVersion");
        String curratedDatabase = resourceBundle.getString("DBMappings.curratedDatabase");
        String curratedDatabaseVersion = resourceBundle.getString("DBMappings.curratedDatabaseVersion");

        /**
         * column name, editable
         * 0 [ row number, false ]
         * 1 [ searchEngineDatabaseName, false ]
         * 2 [ searchEngineDatabaseVersion, false ]
         * 3 [ curratedDatabase, false ]
         * 4 [ curratedDatabaseVersion, false ]
         * 5 [ data, false]
         */
        columnNames = new String[]{"", searchEngineDatabaseName, searchEngineDatabaseVersion, curratedDatabase, curratedDatabaseVersion, ""};
        columnEditable = new boolean[]{false, false, false, false, false, false};
        columnTypes = new Class<?>[]{
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                DatabaseMapping.class
        };
        dataColumnIndex = 5;
    }

    @Override
    protected void constructTableColumnModel(JTable table) {
        tableColumnModel.getColumn(0).setMaxWidth(WIDTH);
        tableColumnModel.getColumn(0).setMinWidth(WIDTH);
        tableColumnModel.getColumn(0).setPreferredWidth(WIDTH);
        tableColumnModel.getColumn(0).setCellRenderer(new RowNumberRenderer());

        //last column will contain the object itself
        tableColumnModel.getColumn(getColumnCount() - 1).setMaxWidth(0);
        tableColumnModel.getColumn(getColumnCount() - 1).setMinWidth(0);
        tableColumnModel.getColumn(getColumnCount() - 1).setPreferredWidth(0);
    }

    @Override
    public void edit(int row, DatabaseMapping db) {
        removeRow(row);
        insertRow(row, getRowObjectArray(db));
    }

    @Override
    protected Object[] getRowObjectArray(DatabaseMapping object) {
        DatabaseMapping dbMapping = (DatabaseMapping) object;

        return new Object[]{"",
                dbMapping.getSearchEngineDatabaseName(),
                dbMapping.getSearchEngineDatabaseVersion(),
                dbMapping.getCuratedDatabaseName(),
                dbMapping.getCuratedDatabaseVersion(),
                dbMapping
        };
    }
}
