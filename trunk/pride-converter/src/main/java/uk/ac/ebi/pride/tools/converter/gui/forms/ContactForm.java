/*
 * Created by JFormDesigner on Fri Oct 21 14:40:38 BST 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.gui.NavigationPanel;
import uk.ac.ebi.pride.tools.converter.gui.component.table.BaseTable;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.ContactTableModel;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.ContactDialog;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
import uk.ac.ebi.pride.tools.converter.report.model.Contact;
import uk.ac.ebi.pride.tools.converter.report.validator.ReportObjectValidator;
import uk.ac.ebi.pride.tools.converter.utils.xml.validation.ValidatorFactory;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

/**
 * @author User #3
 */
public class ContactForm extends AbstractForm implements TableModelListener {

    public ContactForm() {
        initComponents();

        ContactTableModel contactTableModel = new ContactTableModel();
        contactTableModel.addTableModelListener(this);
        contactTable.setModel(contactTableModel);
        contactTable.setColumnModel(contactTableModel.getTableColumnModel(contactTable));
        contactTable.setEnableRowValidation(true);
    }

    private void addContactAction(ActionEvent e) {
        ContactDialog contactDialog = new ContactDialog(NavigationPanel.getInstance(), contactTable);
        contactDialog.setVisible(true);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        contactPanel = new JPanel();
        scrollPane3 = new JScrollPane();
        contactTable = new BaseTable<Contact>();
        addContactButton = new JButton();
        contactLabel = new JLabel();
        label1 = new JLabel();

        //======== this ========

        //======== contactPanel ========
        {

            //======== scrollPane3 ========
            {

                //---- contactTable ----
                contactTable.setModel(new DefaultTableModel());
                scrollPane3.setViewportView(contactTable);
            }

            //---- addContactButton ----
            addContactButton.setText("Add Contact");
            addContactButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addContactAction(e);
                }
            });

            //---- contactLabel ----
            contactLabel.setText("Contacts");

            //---- label1 ----
            label1.setText("*");
            label1.setForeground(Color.red);

            GroupLayout contactPanelLayout = new GroupLayout(contactPanel);
            contactPanel.setLayout(contactPanelLayout);
            contactPanelLayout.setHorizontalGroup(
                    contactPanelLayout.createParallelGroup()
                            .addGroup(contactPanelLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(contactLabel)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(label1, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 338, Short.MAX_VALUE)
                                    .addComponent(addContactButton, GroupLayout.PREFERRED_SIZE, 128, GroupLayout.PREFERRED_SIZE)
                                    .addContainerGap())
                            .addComponent(scrollPane3, GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
            );
            contactPanelLayout.setVerticalGroup(
                    contactPanelLayout.createParallelGroup()
                            .addGroup(contactPanelLayout.createSequentialGroup()
                                    .addGroup(contactPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(contactLabel)
                                            .addComponent(addContactButton, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label1))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(scrollPane3, GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE))
            );
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(contactPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(contactPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel contactPanel;
    private JScrollPane scrollPane3;
    private BaseTable<Contact> contactTable;
    private JButton addContactButton;
    private JLabel contactLabel;
    private JLabel label1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    /**
     * This fine grain notification tells listeners the exact range
     * of cells, rows, or columns that changed.
     */
    @Override
    public void tableChanged(TableModelEvent e) {
        validationListerner.fireValidationListener(contactTable.getAll().size() > 0);
    }

    @Override
    public Collection<ValidatorMessage> validateForm() throws ValidatorException {
        ReportObjectValidator validator = ValidatorFactory.getInstance().getReportValidator();
        return validator.validate(contactTable.getAll());
    }

    @Override
    public void clear() {
        isLoaded = false;
        contactTable.removeAll();
        //inactivate next button
        //validationListerner.fireValidationListener(false);
        //the table listener will fire the validation listener
    }

    @Override
    public void save(ReportReaderDAO dao) {
        dao.setContacts(contactTable.getAll());
    }

    @Override
    public void load(ReportReaderDAO dao) {
        if (!isLoaded) {
            contactTable.addAll(dao.getContacts());
            isLoaded = true;
        }
        //the table listener will fire the validation listener
    }

    @Override
    public String getFormName() {
        return "Contacts";
    }

    @Override
    public String getFormDescription() {
        return config.getString("contact.form.description");
    }

    @Override
    public Icon getFormIcon() {
        return getFormIcon("contact.form.icon");
    }

    @Override
    public String getHelpResource() {
        return "help.ui.contacts";
    }

    @Override
    public void start() {
        //required for back & forth navigation if nothing changes
        validationListerner.fireValidationListener(contactTable.getAll().size() > 0);
    }

    @Override
    public void finish() {
        /* no op */
    }
}
