package uk.ac.ebi.pride.tools.converter.gui.component.table.model;

import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 13/12/11
 * Time: 15:49
 */
public class ParserOptionCellEditor extends DefaultCellEditor implements ActionListener {

    private JComponent component = new JTextField();
    private File selectedFile = null;
    private JFileChooser chooser;

    public ParserOptionCellEditor() {
        super(new JTextField());
        setClickCountToStart(1);
    }

    @Override
    public Object getCellEditorValue() {
        if (component instanceof JCheckBox) {
            return ((JCheckBox) component).isSelected();
        } else if (component instanceof JComboBox) {
            return ((JComboBox) component).getSelectedItem();
        } else if (component instanceof JButton) {
            return selectedFile;
        } else {
            return ((JTextField) component).getText();
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

        if (value instanceof Boolean) {

            component = new JCheckBox();
            //default is unchecked
            if (value == null) {
                value = "false";
            }
            ((JCheckBox) component).setSelected(Boolean.valueOf(value.toString()));
            ((JCheckBox) component).setHorizontalAlignment(JLabel.CENTER);

        } else if (value instanceof File) {

            //Set up the editor (from the table's point of view),
            //which is a button.
            //This button brings up the file chooser dialog,
            //which is the editor from the user's point of view.
            component = new JButton();
            ((JButton) component).setActionCommand("edit");
            ((JButton) component).addActionListener(this);
            ((JButton) component).setBorderPainted(false);

            //Set up the dialog that the button brings up.
            selectedFile = new File(".");
            if (value != null) {
                selectedFile = (File) value;
            }
            chooser = new JFileChooser();
            chooser = new JFileChooser(ConverterData.getInstance().getLastSelectedDirectory());
            chooser.setSelectedFile(selectedFile);
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.addActionListener(this);

        } else {
            //default is empty
            if (value == null) {
                value = "";
            }
            component = new JTextField(value.toString());
        }
        return component;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //The user has clicked the cell, so
        //bring up the dialog.
        if ("edit".equals(e.getActionCommand())) {

            chooser.showOpenDialog((Component) e.getSource());

        } else {
            //at this point, we've pressed OK/CANCEL on the file selection dialog
            if (JFileChooser.APPROVE_SELECTION.equals(e.getActionCommand()) && chooser.getSelectedFile() != null) {
                selectedFile = chooser.getSelectedFile();
                ConverterData.getInstance().setLastSelectedDirectory(selectedFile.getParentFile().getAbsolutePath());
            }
            stopCellEditing();
        }

    }
}
