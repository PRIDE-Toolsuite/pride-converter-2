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
import java.awt.event.MouseListener;

/**
 * Created by IntelliJ IDEA.
 * User: melih
 * Date: 15/03/2011
 * Time: 17:18
 */
public class DeleteIconColumn extends AbstractCellEditor
        implements TableCellRenderer, TableCellEditor, ActionListener, MouseListener {

    JButton button;
    JTable table;
    Action action;

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

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
    }

    @Override
    public Component getTableCellEditorComponent(JTable jTable, Object o, boolean b, int i, int i1) {
        return button;
    }

    @Override
    public Component getTableCellRendererComponent(JTable jTable, Object o, boolean b, boolean b1, int i, int i1) {
        return button;
    }
}
