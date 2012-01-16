package uk.ac.ebi.pride.tools.converter.gui.component.table;

import uk.ac.ebi.pride.tools.converter.gui.NavigationPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: melih
 * Date: 15/03/2011
 * Time: 17:18
 */
public class DeleteIconColumn extends AbstractCellEditor
        implements TableCellRenderer, TableCellEditor, ActionListener {

    private JButton button;
    private JTable table;
    private Action action;
    private Map<Integer, Boolean> isDeletable = new HashMap<Integer, Boolean>();
    private int clickCountToStart = 1;

    public DeleteIconColumn(JTable table, TableColumn tableColumn) {

        this.table = table;

        button = new JButton();
        button.setBorderPainted(false);
        button.addActionListener(this);
        button.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/delete.gif")));
        button.setContentAreaFilled(false);

        tableColumn.setCellRenderer(this);
        tableColumn.setCellEditor(this);

        action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                JTable table = (JTable) e.getSource();
                int modelRow = Integer.valueOf(e.getActionCommand());
                ((DefaultTableModel) table.getModel()).removeRow(modelRow);
            }
        };

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (confirm()) {
            int row = table.convertRowIndexToModel(table.getEditingRow());
            fireEditingStopped();
            ActionEvent event = new ActionEvent(
                    table,
                    ActionEvent.ACTION_PERFORMED,
                    "" + row);
            action.actionPerformed(event);
        }
    }

    private boolean confirm() {
        int i = JOptionPane.showConfirmDialog(NavigationPanel.getInstance(), " Are you sure you want to delete this record?", "Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return i == 0;
    }

    @Override
    public Object getCellEditorValue() {
        return button;
    }

    /**
     * Returns true if <code>anEvent</code> is <b>not</b> a
     * <code>MouseEvent</code>.  Otherwise, it returns true
     * if the necessary number of clicks have occurred, and
     * returns false otherwise.
     *
     * @param anEvent the event
     * @return true  if cell is ready for editing, false otherwise
     * @see #shouldSelectCell
     */
    public boolean isCellEditable(EventObject anEvent) {
        JTable table = (JTable) anEvent.getSource();
        int modelRow = table.convertRowIndexToModel(table.rowAtPoint(((MouseEvent) anEvent).getPoint()));
        return isDeletable.get(modelRow);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        //if the value is boolean(true), it represents a param that is protected and can't be deleted
        //so don't show the delete button
        Component retval = button;
        if (value != null && value instanceof Boolean && Boolean.valueOf(value.toString())) {
            retval = new JLabel("");
            isDeletable.put(row, false);
        } else {
            isDeletable.put(row, true);
        }
        return retval;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        //if the value is boolean(true), it represents a param that is protected and can't be deleted
        //so don't show the delete button
        Component retval = button;
        if (value != null && value instanceof Boolean && Boolean.valueOf(value.toString())) {
            retval = new JLabel("");
            isDeletable.put(row, false);
        } else {
            isDeletable.put(row, true);
        }
        return retval;
    }
}
