/*
 * Created by JFormDesigner on Fri Oct 21 14:40:21 BST 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.gui.NavigationPanel;
import uk.ac.ebi.pride.tools.converter.gui.component.table.BaseTable;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.BaseTableModel;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.ContactTableModel;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.ReferencesTableModel;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.AbstractDialog;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.ContactDialog;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.ReferenceDialog;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
import uk.ac.ebi.pride.tools.converter.report.model.Contact;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;
import uk.ac.ebi.pride.tools.converter.report.validator.ReportObjectValidator;
import uk.ac.ebi.pride.tools.converter.utils.xml.validation.ValidatorFactory;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author User #3
 */
public class ExperimentDetailForm extends AbstractForm implements TableModelListener {

    private boolean doneOnce = false;

    public ExperimentDetailForm() {
        initComponents();

        //update reference table
        referenceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ReferencesTableModel referenceTableModel = new ReferencesTableModel();
        referenceTable.setModel(referenceTableModel);
        referenceTable.setColumnModel(referenceTableModel.getTableColumnModel(referenceTable));

        //update contact table
        ContactTableModel contactTableModel = new ContactTableModel();
        contactTableModel.addTableModelListener(this);
        contactTable.setModel(contactTableModel);
        contactTable.setColumnModel(contactTableModel.getTableColumnModel(contactTable));
        contactTable.setEnableRowValidation(true);

    }

    private void experimentDetailsRequiredFieldKeyTyped(KeyEvent e) {
        //fire super method to update component background
        validateRequiredField(e.getComponent(), e);
        //validate form and fire validationListener
        String s1 = projectNameInput.getText();
        String s2 = experimentTitleInput.getText();
        String s3 = shortNameInput.getText();
        if (e.getSource().equals(projectNameInput)) {
            s1 += e.getKeyChar();
        } else if (e.getSource().equals(experimentTitleInput)) {
            s2 += e.getKeyChar();
        } else if (e.getSource().equals(shortNameInput)) {
            s3 += e.getKeyChar();
        }
        validationListerner.fireValidationListener(isNonNullTextField(s1) && isNonNullTextField(s2) && isNonNullTextField(s3) && contactTable.getAll().size() > 0);
    }

    private void experimentDetailsRequiredFieldFocusLost(FocusEvent e) {
        //fire super method to update component background
        validateRequiredField(e.getComponent(), null);
        //validate form and fire validationListener
        validationListerner.fireValidationListener(isNonNullTextField(projectNameInput.getText()) && isNonNullTextField(experimentTitleInput.getText()) && isNonNullTextField(shortNameInput.getText()) && contactTable.getAll().size() > 0);
    }

    private void addContactAction(ActionEvent e) {
        ContactDialog contactDialog = new ContactDialog(NavigationPanel.getInstance(), contactTable);
        contactDialog.setVisible(true);
    }

    private void addReferenceAction(ActionEvent e) {
        ReferenceDialog referenceDialog = new ReferenceDialog(NavigationPanel.getInstance(), referenceTable);
        referenceDialog.setVisible(true);
    }

    private void contactEditButtonActionPerformed() {
        if (contactTable.getSelectedRowCount() > 0) {
            //convert table selected row to underlying model row
            int modelSelectedRow = contactTable.convertRowIndexToModel(contactTable.getSelectedRow());
            //get object
            ReportObject objToEdit = ((BaseTableModel) contactTable.getModel()).get(modelSelectedRow);
            Class clazz = objToEdit.getClass();
            //show editing dialog for object
            AbstractDialog dialog = AbstractDialog.getInstance(contactTable, clazz);
            dialog.edit(objToEdit);
            dialog.setVisible(true);
        }
    }

    private void referenceEditButtonActionPerformed() {
        if (referenceTable.getSelectedRowCount() > 0) {
            //convert table selected row to underlying model row
            int modelSelectedRow = referenceTable.convertRowIndexToModel(referenceTable.getSelectedRow());
            //get object
            ReportObject objToEdit = ((BaseTableModel) referenceTable.getModel()).get(modelSelectedRow);
            Class clazz = objToEdit.getClass();
            //show editing dialog for object
            AbstractDialog dialog = AbstractDialog.getInstance(referenceTable, clazz);
            dialog.edit(objToEdit);
            dialog.setVisible(true);
        }

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        projectNameInput = new JTextField();
        experimentTitleInput = new JTextField();
        shortNameInput = new JTextField();
        label1 = new JLabel();
        label2 = new JLabel();
        label3 = new JLabel();
        label4 = new JLabel();
        label5 = new JLabel();
        label6 = new JLabel();
        label7 = new JLabel();
        scrollPane1 = new JScrollPane();
        descriptionPane = new JTextPane();
        scrollPane3 = new JScrollPane();
        contactTable = new BaseTable<Contact>();
        contactLabel = new JLabel();
        addContactButton = new JButton();
        label8 = new JLabel();
        addreferenceButton = new JButton();
        scrollPane2 = new JScrollPane();
        referenceTable = new BaseTable();
        label9 = new JLabel();
        contactEditButton = new JButton();
        referenceEditButton = new JButton();

        //======== this ========

        //---- projectNameInput ----
        projectNameInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                experimentDetailsRequiredFieldKeyTyped(e);
            }
        });
        projectNameInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                experimentDetailsRequiredFieldFocusLost(e);
            }
        });

        //---- experimentTitleInput ----
        experimentTitleInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                experimentDetailsRequiredFieldKeyTyped(e);
            }
        });
        experimentTitleInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                experimentDetailsRequiredFieldFocusLost(e);
            }
        });

        //---- shortNameInput ----
        shortNameInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                experimentDetailsRequiredFieldKeyTyped(e);
            }
        });
        shortNameInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                experimentDetailsRequiredFieldFocusLost(e);
            }
        });

        //---- label1 ----
        label1.setText("Project Name");

        //---- label2 ----
        label2.setText("Experiment Title");

        //---- label3 ----
        label3.setText("Short Label");

        //---- label4 ----
        label4.setText("*");
        label4.setFont(new Font("Dialog", Font.PLAIN, 12));
        label4.setForeground(Color.red);

        //---- label5 ----
        label5.setText("*");
        label5.setFont(new Font("Dialog", Font.PLAIN, 12));
        label5.setForeground(Color.red);

        //---- label6 ----
        label6.setText("*");
        label6.setFont(new Font("Dialog", Font.PLAIN, 12));
        label6.setForeground(Color.red);

        //---- label7 ----
        label7.setText("Description");

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(descriptionPane);
        }

        //======== scrollPane3 ========
        {

            //---- contactTable ----
            contactTable.setModel(new DefaultTableModel());
            scrollPane3.setViewportView(contactTable);
        }

        //---- contactLabel ----
        contactLabel.setText("Contacts");

        //---- addContactButton ----
        addContactButton.setIcon(new ImageIcon(getClass().getResource("/images/add.png")));
        addContactButton.setToolTipText("Add");
        addContactButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addContactAction(e);
            }
        });

        //---- label8 ----
        label8.setText("*");
        label8.setForeground(Color.red);

        //---- addreferenceButton ----
        addreferenceButton.setIcon(new ImageIcon(getClass().getResource("/images/add.png")));
        addreferenceButton.setToolTipText("Add");
        addreferenceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addReferenceAction(e);
            }
        });

        //======== scrollPane2 ========
        {
            scrollPane2.setViewportView(referenceTable);
        }

        //---- label9 ----
        label9.setText("References");

        //---- contactEditButton ----
        contactEditButton.setIcon(new ImageIcon(getClass().getResource("/images/edit.png")));
        contactEditButton.setToolTipText("Edit");
        contactEditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contactEditButtonActionPerformed();
            }
        });

        //---- referenceEditButton ----
        referenceEditButton.setIcon(new ImageIcon(getClass().getResource("/images/edit.png")));
        referenceEditButton.setToolTipText("Edit");
        referenceEditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                referenceEditButtonActionPerformed();
            }
        });

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 718, Short.MAX_VALUE)
                                                .addContainerGap())
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                        .addComponent(label3)
                                                        .addComponent(label2)
                                                        .addComponent(label1)
                                                        .addComponent(label7))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup()
                                                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                                                        .addComponent(projectNameInput, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                                                        .addComponent(experimentTitleInput, GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                                                        .addComponent(shortNameInput, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup()
                                                        .addComponent(label4)
                                                        .addComponent(label5)
                                                        .addComponent(label6))
                                                .addGap(175, 175, 175))
                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(contactLabel)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(label8, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 507, Short.MAX_VALUE)
                                                .addComponent(addContactButton)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(contactEditButton)
                                                .addContainerGap())
                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(scrollPane3, GroupLayout.DEFAULT_SIZE, 718, Short.MAX_VALUE)
                                                .addContainerGap())
                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(label9)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 532, Short.MAX_VALUE)
                                                .addComponent(addreferenceButton)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(referenceEditButton)
                                                .addContainerGap())))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(contactEditButton)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(projectNameInput, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(label4)
                                                        .addComponent(label1))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(experimentTitleInput, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(label5)
                                                        .addComponent(label2))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(shortNameInput, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(label6)
                                                        .addComponent(label3))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup()
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(label7)
                                                                .addGap(35, 35, 35)
                                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                                        .addComponent(addContactButton)
                                                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(contactLabel)
                                                                                .addComponent(label8))))
                                                        .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane3, GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(addreferenceButton)
                                        .addComponent(label9)
                                        .addComponent(referenceEditButton))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JTextField projectNameInput;
    private JTextField experimentTitleInput;
    private JTextField shortNameInput;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
    private JLabel label6;
    private JLabel label7;
    private JScrollPane scrollPane1;
    private JTextPane descriptionPane;
    private JScrollPane scrollPane3;
    private BaseTable<Contact> contactTable;
    private JLabel contactLabel;
    private JButton addContactButton;
    private JLabel label8;
    private JButton addreferenceButton;
    private JScrollPane scrollPane2;
    private BaseTable referenceTable;
    private JLabel label9;
    private JButton contactEditButton;
    private JButton referenceEditButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    @Override
    public Collection<ValidatorMessage> validateForm() throws ValidatorException {

        ReportObjectValidator validator = ValidatorFactory.getInstance().getReportValidator();
        Collection<ValidatorMessage> messages = new ArrayList<ValidatorMessage>();
        messages.addAll(validator.validate(contactTable.getAll()));
        messages.addAll(validator.validate(referenceTable.getAll()));
        return messages;

    }

    @Override
    public void clear() {
        isLoaded = false;

        projectNameInput.setText(null);
        experimentTitleInput.setText(null);
        shortNameInput.setText(null);
        descriptionPane.setText(null);

        referenceTable.removeAll();
        contactTable.removeAll();

        //inactivate next button
        validationListerner.fireValidationListener(false);
    }

    @Override
    public void save(ReportReaderDAO dao) {

        //save references
        dao.setReferences(referenceTable.getAll());

        //save contacts
        dao.setContacts(contactTable.getAll());

        //update pride project
        Param p = dao.getExperimentParams();
        //remove existing project param & description here
        for (Iterator<CvParam> i = p.getCvParam().iterator(); i.hasNext(); ) {
            CvParam cv = i.next();
            if (cv.getAccession().equals(DAOCvParams.PRIDE_PROJECT.getAccession())) {
                i.remove();
            }
            if (cv.getAccession().equals(DAOCvParams.EXPERIMENT_DESCRIPTION.getAccession())) {
                i.remove();
            }
        }
        //add new project tag
        p.getCvParam().add(new CvParam(DAOCvParams.PRIDE_PROJECT.getCv(), DAOCvParams.PRIDE_PROJECT.getAccession(), DAOCvParams.PRIDE_PROJECT.getName(), projectNameInput.getText()));
        p.getCvParam().add(new CvParam(DAOCvParams.EXPERIMENT_DESCRIPTION.getCv(), DAOCvParams.EXPERIMENT_DESCRIPTION.getAccession(), DAOCvParams.EXPERIMENT_DESCRIPTION.getName(), descriptionPane.getText()));
        dao.setExperimentParams(p);

        //only save the data if it's not due to be modified in the next form
        //i.e. if we have more than 1 source file
        if (ConverterData.getInstance().getInputFiles().size() == 1) {
            dao.setExperimentShortLabel(shortNameInput.getText());
            dao.setExperimentTitle(experimentTitleInput.getText());
        }
    }

    @Override
    public void load(ReportReaderDAO dao) {
        if (!isLoaded) {
            experimentTitleInput.setText(dao.getExperimentTitle());
            shortNameInput.setText(dao.getExperimentShortLabel());
            referenceTable.addAll(dao.getReferences());
            contactTable.addAll(dao.getContacts());

            Param param = dao.getExperimentParams();
            for (CvParam cv : param.getCvParam()) {
                if (DAOCvParams.PRIDE_PROJECT.getAccession().equals(cv.getAccession())) {
                    projectNameInput.setText(cv.getValue());
                }
                if (DAOCvParams.EXPERIMENT_DESCRIPTION.getAccession().equals(cv.getAccession())) {
                    descriptionPane.setText(cv.getValue());
                }
            }
            isLoaded = true;
        }

        //if there are multiple source files in the conversion, add another form in the UI
        //to deal with this data
        if (ConverterData.getInstance().getInputFiles().size() > 1 && !doneOnce) {
            experimentTitleInput.setText("will be set in next form");
            experimentTitleInput.setEditable(false);
            shortNameInput.setText("will be set in next form");
            shortNameInput.setEditable(false);
            NavigationPanel.getInstance().registerFormAfter(new ExperimentDetailMultipleDataForm(), this);
            doneOnce = true;
        }

        //fire validation listener on load
        validationListerner.fireValidationListener(isNonNullTextField(projectNameInput.getText()) && isNonNullTextField(experimentTitleInput.getText()) && isNonNullTextField(shortNameInput.getText()) && contactTable.getAll().size() > 0);
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        validationListerner.fireValidationListener(isNonNullTextField(projectNameInput.getText()) && isNonNullTextField(experimentTitleInput.getText()) && isNonNullTextField(shortNameInput.getText()) && contactTable.getAll().size() > 0);
    }

    @Override
    public String getFormName() {
        return "Experiment Details";
    }

    @Override
    public String getFormDescription() {
        return config.getString("experimentdetail.form.description");
    }

    @Override
    public Icon getFormIcon() {
        return getFormIcon("experimentdetail.form.icon");
    }

    @Override
    public String getHelpResource() {
        return "help.ui.experimentdetails";
    }

    @Override
    public void start() {
        //validate form and fire validationListener - required for back & forth when no changes occur
        validationListerner.fireValidationListener(isNonNullTextField(projectNameInput.getText()) && isNonNullTextField(experimentTitleInput.getText()) && isNonNullTextField(shortNameInput.getText()) && contactTable.getAll().size() > 0);
    }

    @Override
    public void finish() {
        /* no op */
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.add(new ExperimentDetailForm());
        f.pack();
        f.setVisible(true);
    }
}
