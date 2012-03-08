/*
 * Created by JFormDesigner on Wed Jun 08 11:59:38 BST 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.gui.component.table.BaseTable;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.BaseTableModel;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.DatabaseMappingTableModel;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.AbstractDialog;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
import uk.ac.ebi.pride.tools.converter.report.model.DatabaseMapping;
import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;
import uk.ac.ebi.pride.tools.converter.report.validator.ReportObjectValidator;
import uk.ac.ebi.pride.tools.converter.utils.xml.validation.ValidatorFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author melih
 * @author rcote
 */
public class DatabaseMappingForm extends AbstractForm {

    DatabaseMappingTableModel tableModel;
    Set<DatabaseMapping> mappings;

    public DatabaseMappingForm() {
        initComponents();

        tableModel = new DatabaseMappingTableModel();
        //erroneous DBMs will be highlighted in table
        databaseTable.setEnableRowValidation(true);
        databaseTable.setModel(tableModel);
        databaseTable.setColumnModel(tableModel.getTableColumnModel(databaseTable));

        mappings = new HashSet<DatabaseMapping>();
    }

    private void editButtonActionPerformed() {
        if (databaseTable.getSelectedRowCount() > 0) {
            //convert table selected row to underlying model row
            int modelSelectedRow = databaseTable.convertRowIndexToModel(databaseTable.getSelectedRow());
            //get object
            ReportObject objToEdit = ((BaseTableModel) databaseTable.getModel()).get(modelSelectedRow);
            Class clazz = objToEdit.getClass();
            //show editing dialog for object
            AbstractDialog dialog = AbstractDialog.getInstance(databaseTable, clazz);
            dialog.edit(objToEdit);
            dialog.setVisible(true);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("messages");
        label1 = new JLabel();
        scrollPane1 = new JScrollPane();
        databaseTable = new BaseTable<DatabaseMapping>();
        editButton = new JButton();

        //======== this ========

        //---- label1 ----
        label1.setText(bundle.getString("DataBaseInformationPanel.label1.text"));

        //======== scrollPane1 ========
        {

            //---- databaseTable ----
            databaseTable.setModel(new DefaultTableModel());
            scrollPane1.setViewportView(databaseTable);
        }

        //---- editButton ----
        editButton.setIcon(new ImageIcon(getClass().getResource("/images/formatselection.png")));
        editButton.setToolTipText(bundle.getString("DataBaseInformationPanel.editButton.toolTipText"));
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editButtonActionPerformed();
            }
        });

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(label1)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 316, Short.MAX_VALUE)
                                                .addComponent(editButton)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(editButton)
                                        .addComponent(label1))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JScrollPane scrollPane1;
    private BaseTable<DatabaseMapping> databaseTable;
    private JButton editButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    @Override
    public Collection<ValidatorMessage> validateForm() throws ValidatorException {
        ReportObjectValidator validator = ValidatorFactory.getInstance().getReportValidator();
        return validator.validate(databaseTable.getAll());
    }

    @Override
    public void clear() {
        isLoaded = false;
    }

    @Override
    public void save(ReportReaderDAO dao) {
        dao.setDatabaseMappings(databaseTable.getAll());
    }

    @Override
    public void load(ReportReaderDAO dao) {
        if (!isLoaded) {
            //note that in this instance, the data is obtained from the ConverterData singleton
            //and not the dao
            databaseTable.addAll(ConverterData.getInstance().getDatabaseMappings());
            isLoaded = true;
        }
        //no need to fire the validation listener
    }

    @Override
    public String getFormName() {
        return "Database Information";
    }

    @Override
    public String getFormDescription() {
        return config.getString("databasemapping.form.description");
    }

    @Override
    public Icon getFormIcon() {
        return getFormIcon("databasemapping.form.icon");
    }

    @Override
    public String getHelpResource() {
        return "help.ui.databasemapping";
    }

    @Override
    public void start() {
        //database mappings are optional
        validationListerner.fireValidationListener(true);
    }

    @Override
    public void finish() {
        /* no op */
    }
}
