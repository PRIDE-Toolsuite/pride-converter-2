package uk.ac.ebi.pride.tools.converter.gui.component.table;

import uk.ac.ebi.pride.tools.converter.dao.DAOProperty;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.ParserOptionCellEditor;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.ParserOptionTableModel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.event.MouseEvent;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 13/12/11
 * Time: 15:03
 */
public class ParserOptionTable extends JTable {

    private void initTable() {
        //update cell editor
        getColumn("Property Value").setCellEditor(new ParserOptionCellEditor());
        putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    }

    public ParserOptionTable() {
        super(new ParserOptionTableModel(null, false));
        initTable();
    }

    public ParserOptionTable(Collection<DAOProperty> props, boolean showAdvancedProperties) {
        super(new ParserOptionTableModel(props, showAdvancedProperties));
        initTable();
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


    @Override
    public String getToolTipText(MouseEvent event) {

        int row = convertRowIndexToModel(rowAtPoint(event.getPoint()));
        DAOProperty property = ((ParserOptionTableModel) getModel()).getDAOPropertyAtRow(row);
        if (property.getDescription() != null) {
            return lineBreakToolTip(property.getDescription());
        } else {
            return super.getToolTipText(event);
        }

    }

    private String lineBreakToolTip(String description) {

        int breakpoint = 50;
        int localIndex = 0;
        boolean addBrAtNextBreak = false;
        StringBuilder sb = new StringBuilder("<html>");
        for (int i = 0; i < description.length(); i++) {
            //update tooltip
            sb.append(description.charAt(i));
            localIndex++;
            //we hit the point where we need to break
            if (localIndex >= breakpoint) {
                addBrAtNextBreak = true;
            }
            //add br
            if (description.charAt(i) == ' ' && addBrAtNextBreak) {
                sb.append("<br/>");
                addBrAtNextBreak = false;
                localIndex = 0;
            }
        }
        sb.append("</html>");
        return sb.toString();

    }

}
