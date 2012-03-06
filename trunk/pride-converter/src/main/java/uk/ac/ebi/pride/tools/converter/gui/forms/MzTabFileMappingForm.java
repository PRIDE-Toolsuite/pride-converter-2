/*
 * Created by JFormDesigner on Thu Feb 02 11:53:00 GMT 2012
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.gui.model.FileBean;
import uk.ac.ebi.pride.tools.converter.gui.model.GUIException;
import uk.ac.ebi.pride.tools.converter.gui.util.IOUtilities;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

/**
 * @author User #3
 */
public class MzTabFileMappingForm extends AbstractForm {

    //if set to true, will only update the mztab mappings and let another form generate the report files
    private boolean deferReportFileGeneration = false;

    public MzTabFileMappingForm() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        scrollPane1 = new JScrollPane();
        mappingTable = new JTable();

        //======== this ========

        //======== scrollPane1 ========
        {

            //---- mappingTable ----
            mappingTable.setModel(new DefaultTableModel(
                    new Object[][]{
                            {null, null},
                            {null, null},
                    },
                    new String[]{
                            "Input File", "MzTab File"
                    }
            ));
            scrollPane1.setViewportView(mappingTable);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JScrollPane scrollPane1;
    private JTable mappingTable;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    public void setDeferReportFileGeneration(boolean deferReportFileGeneration) {
        this.deferReportFileGeneration = deferReportFileGeneration;
    }

    @Override
    public Collection<ValidatorMessage> validateForm() throws ValidatorException {

        Collection<ValidatorMessage> msgs = new ArrayList<ValidatorMessage>();
        DefaultTableModel model = (DefaultTableModel) mappingTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 1) == null || "".equals(model.getValueAt(i, 1))) {
                msgs.add(new ValidatorMessage("No mzTab file set for " + model.getValueAt(i, 0).toString(), MessageLevel.WARN));
            }
        }
        return msgs;

    }

    @Override
    public void clear() {
        //reset table data model
        updateTableModel();
    }

    @Override
    public void save(ReportReaderDAO dao) {
        /* no op */
    }

    @Override
    public void load(ReportReaderDAO dao) {
        /* no op */
    }

    @Override
    public String getFormName() {
        return "File Selection - MzTab";
    }

    @Override
    public String getFormDescription() {
        return config.getString("fileselectionextra.form.description");
    }

    @Override
    public Icon getFormIcon() {
        return getFormIcon("fileselectionextra.form.icon");
    }

    @Override
    public String getHelpResource() {
        return "help.ui.fileextra";
    }

    @Override
    public void start() {
        //update table model for file mapping
        updateTableModel();
        //set initial table values
        setInitialMapping();
        //update validation listener
        validationListerner.fireValidationListener(true);
    }

    private void setInitialMapping() {

        DefaultTableModel model = (DefaultTableModel) mappingTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            String inputFile = model.getValueAt(i, 0).toString();
            String possibleMzTabFile = inputFile + ConverterData.MZTAB;
            if (ConverterData.getInstance().getMztabFiles().contains(possibleMzTabFile)) {
                model.setValueAt(possibleMzTabFile, i, 1);
            }
        }

    }

    private void updateTableModel() {

        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        for (FileBean fileBean : ConverterData.getInstance().getDataFiles()) {
            Vector<Object> row = new Vector<Object>();
            row.add(fileBean.getInputFile());
            data.add(row);
        }
        ;

        Vector<Object> headers = new Vector<Object>();
        headers.add("Input File");
        headers.add("MzTab File");

        mappingTable.setModel(new DefaultTableModel(data, headers) {
            boolean[] columnEditable = new boolean[]{
                    false, true
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnEditable[columnIndex];
            }
        });
        {
            TableColumn col = mappingTable.getColumnModel().getColumn(1);
            col.setCellEditor(new MzTabComboBoxEditor(ConverterData.getInstance().getMztabFiles()));
            // If the cell should appear like a combobox in its
            // non-editing state, also set the combobox renderer
            col.setCellRenderer(new MzTabComboBoxRenderer(ConverterData.getInstance().getMztabFiles()));
        }


    }

    @Override
    public void finish() throws GUIException {
        //update mztab file mapping
        DefaultTableModel model = (DefaultTableModel) mappingTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            String inputFile = model.getValueAt(i, 0).toString();
            String mzTabFile = (String) model.getValueAt(i, 1);
            if (mzTabFile != null && !"".equals(mzTabFile)) {
                //update file bean
                ConverterData.getInstance().getFileBeanByInputFileName(inputFile).setMzTabFile(mzTabFile);
            }
        }

        if (!deferReportFileGeneration) {
            //generate report files
            IOUtilities.generateReportFiles(ConverterData.getInstance().getOptions(), ConverterData.getInstance().getDataFiles(), true, true);
        }

    }

    private class MzTabComboBoxEditor extends DefaultCellEditor {
        public MzTabComboBoxEditor(Collection<String> mztabFiles) {
            super(new JComboBox(mztabFiles.toArray()));
            setClickCountToStart(1);
        }
    }

    private class MzTabComboBoxRenderer extends JComboBox implements TableCellRenderer {
        public MzTabComboBoxRenderer(Collection<String> mzTabFiles) {
            super(mzTabFiles.toArray());
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                super.setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }

            // Select the current value
            setSelectedItem(value);
            return this;
        }
    }
}
