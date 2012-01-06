package uk.ac.ebi.pride.tools.converter.gui.component.table;

import uk.ac.ebi.pride.tools.converter.dao.DAOProperty;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.ParserOptionTableModel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 13/12/11
 * Time: 15:03
 */
public class ParserOptionTable extends JTable {

    public ParserOptionTable() {
        super(new ParserOptionTableModel(null));
        putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    }

    public ParserOptionTable(Collection<DAOProperty> props) {
        super(new ParserOptionTableModel(props));
        putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    }

    public TableCellRenderer getCellRenderer(int row, int column) {
        Object value = getValueAt(row, column);
        if (value != null) {
            if (value instanceof Boolean) {
                return getDefaultRenderer(Boolean.class);
            } else {
                return getDefaultRenderer(String.class);
            }
        }
        return super.getCellRenderer(row, column);
    }


}
