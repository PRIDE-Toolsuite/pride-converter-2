/*
 * Created by JFormDesigner on Fri Mar 11 13:17:39 GMT 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import org.apache.log4j.Logger;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.gui.component.AddTermButton;
import uk.ac.ebi.pride.tools.converter.gui.component.table.BaseTable;
import uk.ac.ebi.pride.tools.converter.gui.component.table.ParamTable;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.BaseTableModel;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.DatabaseMappingTableModel;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.ParamTableModel;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.AbstractDialog;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
import uk.ac.ebi.pride.tools.converter.report.model.*;
import uk.ac.ebi.pride.tools.converter.report.validator.ReportObjectValidator;
import uk.ac.ebi.pride.tools.converter.utils.xml.validation.ValidatorFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 * @author Melih Birim
 * @author rcote
 */
public class SoftwareProcessingForm extends AbstractForm {

    private static final Logger logger = Logger.getLogger(SoftwareProcessingForm.class);

    public SoftwareProcessingForm() {
        initComponents();
        addTermButton.setOwner(processingTable);
        updateTermButtonCvList(addTermButton, "softwareprocessing.suggested.cv");

        //erroneous DBMs will be highlighted in table
        DatabaseMappingTableModel tableModel = new DatabaseMappingTableModel();
        databaseTable.setEnableRowValidation(true);
        databaseTable.setModel(tableModel);
        databaseTable.setColumnModel(tableModel.getTableColumnModel(databaseTable));

    }

    private void softwareRequiredFieldFocusLost(FocusEvent e) {
        //fire super method to update component background
        validateRequiredField(e.getComponent(), null);
        //validate form and fire validationListener
        validationListerner.fireValidationListener(isNonNullTextField(softwareNameField.getText()) && isNonNullTextField(softwareVersionField.getText()));
    }

    private void softwareRequiredFieldKeyTyped(KeyEvent e) {
        //fire super method to update component background
        validateRequiredField(e.getComponent(), e);
        //validate form and fire validationListener
        String s1 = softwareNameField.getText();
        String s2 = softwareVersionField.getText();
        if (e.getSource().equals(softwareNameField)) {
            s1 += e.getKeyChar();
        } else if (e.getSource().equals(softwareVersionField)) {
            s2 += e.getKeyChar();
        }
        validationListerner.fireValidationListener(isNonNullTextField(s1) && isNonNullTextField(s2));
    }

    private void editButtonActionPerformed() {

        if (processingTable.getSelectedRowCount() > 0) {
            //convert table selected row to underlying model row
            int modelSelectedRow = processingTable.convertRowIndexToModel(processingTable.getSelectedRow());
            //get object
            ReportObject objToEdit = ((ParamTableModel) processingTable.getModel()).get(modelSelectedRow);
            Class clazz = objToEdit.getClass();
            //show editing dialog for object
            AbstractDialog dialog = AbstractDialog.getInstance(processingTable, clazz);
            dialog.edit(objToEdit, modelSelectedRow);
            dialog.setVisible(true);
        }

    }

    private void editDatabaseMappingButtonActionPerformed() {
        if (databaseTable.getSelectedRowCount() > 0) {
            //convert table selected row to underlying model row
            int modelSelectedRow = databaseTable.convertRowIndexToModel(databaseTable.getSelectedRow());
            //get object
            ReportObject objToEdit = ((BaseTableModel) databaseTable.getModel()).get(modelSelectedRow);
            Class clazz = objToEdit.getClass();
            //show editing dialog for object
            AbstractDialog dialog = AbstractDialog.getInstance(databaseTable, clazz);
            dialog.edit(objToEdit, modelSelectedRow);
            dialog.setVisible(true);
        }

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("messages");
        label1 = new JLabel();
        addTermButton = new AddTermButton();
        scrollPane1 = new JScrollPane();
        processingTable = new ParamTable();
        panel1 = new JPanel();
        label2 = new JLabel();
        label3 = new JLabel();
        label4 = new JLabel();
        softwareNameField = new JTextField();
        softwareVersionField = new JTextField();
        scrollPane2 = new JScrollPane();
        softwareCommentField = new JTextArea();
        label5 = new JLabel();
        label6 = new JLabel();
        processingMethodEditButton = new JButton();
        editButton = new JButton();
        scrollPane3 = new JScrollPane();
        databaseTable = new BaseTable<DatabaseMapping>();
        label7 = new JLabel();

        //======== this ========

        //---- label1 ----
        label1.setText(bundle.getString("SoftwareProcessingForm.label1.text"));
        label1.setToolTipText(bundle.getString("SoftwareProcessingForm.label1.toolTipText"));

        //---- addTermButton ----
        addTermButton.setToolTipText(bundle.getString("SoftwareProcessingForm.addTermButton.toolTipText"));

        //======== scrollPane1 ========
        {

            //---- processingTable ----
            processingTable.setToolTipText(bundle.getString("SoftwareProcessingForm.processingTable.toolTipText"));
            scrollPane1.setViewportView(processingTable);
        }

        //======== panel1 ========
        {
            panel1.setBorder(new TitledBorder(bundle.getString("SoftwareProcessingForm.panel1.border")));

            //---- label2 ----
            label2.setText(bundle.getString("SoftwareProcessingForm.label2.text"));
            label2.setToolTipText(bundle.getString("SoftwareProcessingForm.label2.toolTipText"));

            //---- label3 ----
            label3.setText(bundle.getString("SoftwareProcessingForm.label3.text"));
            label3.setToolTipText(bundle.getString("SoftwareProcessingForm.label3.toolTipText"));

            //---- label4 ----
            label4.setText(bundle.getString("SoftwareProcessingForm.label4.text"));
            label4.setToolTipText(bundle.getString("SoftwareProcessingForm.label4.toolTipText"));

            //---- softwareNameField ----
            softwareNameField.setToolTipText(bundle.getString("SoftwareProcessingForm.softwareNameField.toolTipText"));
            softwareNameField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    softwareRequiredFieldFocusLost(e);
                }
            });
            softwareNameField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    softwareRequiredFieldKeyTyped(e);
                }
            });

            //---- softwareVersionField ----
            softwareVersionField.setToolTipText(bundle.getString("SoftwareProcessingForm.softwareVersionField.toolTipText"));
            softwareVersionField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    softwareRequiredFieldFocusLost(e);
                }
            });
            softwareVersionField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    softwareRequiredFieldKeyTyped(e);
                }
            });

            //======== scrollPane2 ========
            {

                //---- softwareCommentField ----
                softwareCommentField.setLineWrap(true);
                softwareCommentField.setWrapStyleWord(true);
                softwareCommentField.setToolTipText(bundle.getString("SoftwareProcessingForm.softwareCommentField.toolTipText"));
                scrollPane2.setViewportView(softwareCommentField);
            }

            //---- label5 ----
            label5.setText(bundle.getString("SoftwareProcessingForm.label5.text"));
            label5.setForeground(Color.red);

            //---- label6 ----
            label6.setText(bundle.getString("SoftwareProcessingForm.label6.text"));
            label6.setForeground(Color.red);

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                    panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                    .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(label4)
                                            .addGroup(panel1Layout.createSequentialGroup()
                                                    .addContainerGap()
                                                    .addComponent(label2)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(label5)))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panel1Layout.createParallelGroup()
                                            .addGroup(panel1Layout.createSequentialGroup()
                                                    .addComponent(softwareNameField, GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                                                    .addGap(18, 18, 18)
                                                    .addComponent(label3)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(label6, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(softwareVersionField, GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE))
                                            .addComponent(scrollPane2))
                                    .addContainerGap())
            );
            panel1Layout.setVerticalGroup(
                    panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                    .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(softwareNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label5)
                                            .addComponent(label2)
                                            .addComponent(label3)
                                            .addComponent(label6)
                                            .addComponent(softwareVersionField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(label4)
                                            .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE))
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        //---- processingMethodEditButton ----
        processingMethodEditButton.setIcon(new ImageIcon(getClass().getResource("/images/edit.png")));
        processingMethodEditButton.setToolTipText(bundle.getString("SoftwareProcessingForm.processingMethodEditButton.toolTipText"));
        processingMethodEditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editButtonActionPerformed();
            }
        });

        //---- editButton ----
        editButton.setIcon(new ImageIcon(getClass().getResource("/images/edit.png")));
        editButton.setToolTipText(bundle.getString("SoftwareProcessingForm.editButton.toolTipText"));
        editButton.setMaximumSize(new Dimension(50, 26));
        editButton.setMinimumSize(new Dimension(50, 26));
        editButton.setPreferredSize(new Dimension(50, 26));
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editDatabaseMappingButtonActionPerformed();
            }
        });

        //======== scrollPane3 ========
        {

            //---- databaseTable ----
            databaseTable.setModel(new DefaultTableModel());
            scrollPane3.setViewportView(databaseTable);
        }

        //---- label7 ----
        label7.setText(bundle.getString("SoftwareProcessingForm.label7.text"));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(scrollPane3, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 633, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(label7)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 441, Short.MAX_VALUE)
                                                .addComponent(editButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(panel1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(scrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 633, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(label1)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 383, Short.MAX_VALUE)
                                                .addComponent(addTermButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(processingMethodEditButton)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(addTermButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(processingMethodEditButton)
                                        .addComponent(label1))
                                .addGap(6, 6, 6)
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(editButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label7))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane3, GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private AddTermButton addTermButton;
    private JScrollPane scrollPane1;
    private ParamTable processingTable;
    private JPanel panel1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JTextField softwareNameField;
    private JTextField softwareVersionField;
    private JScrollPane scrollPane2;
    private JTextArea softwareCommentField;
    private JLabel label5;
    private JLabel label6;
    private JButton processingMethodEditButton;
    private JButton editButton;
    private JScrollPane scrollPane3;
    private BaseTable<DatabaseMapping> databaseTable;
    private JLabel label7;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    @Override
    public Collection<ValidatorMessage> validateForm() throws ValidatorException {

        ReportObjectValidator validator = ValidatorFactory.getInstance().getReportValidator();
        DataProcessing dataProcessing = new DataProcessing();
        dataProcessing.setSoftware(makeSoftware());
        dataProcessing.setProcessingMethod(makeProcessingMethod());

        Collection<ValidatorMessage> msgs = new ArrayList<ValidatorMessage>();
        msgs.addAll(validator.validate(dataProcessing));
        msgs.addAll(validator.validate(databaseTable.getAll()));
        return msgs;

    }

    @Override
    public void clear() {
        isLoaded = false;
        softwareNameField.setText(null);
        softwareCommentField.setText(null);
        softwareVersionField.setText(null);
        processingTable.removeAll();
        //inactivate next button
        validationListerner.fireValidationListener(false);
    }

    @Override
    public void save(ReportReaderDAO dao) {
        dao.setSoftware(makeSoftware());
        dao.setProcessingMethod(makeProcessingMethod());
        dao.setDatabaseMappings(databaseTable.getAll());
    }

    private Param makeProcessingMethod() {
        Param p = new Param();
        p.getCvParam().addAll(processingTable.getCvParamList());
        p.getUserParam().addAll(processingTable.getUserParamList());
        return p;
    }

    private Software makeSoftware() {
        Software sw = new Software();
        sw.setVersion(softwareVersionField.getText());
        sw.setComments(softwareCommentField.getText());
        sw.setName(softwareNameField.getText());
        return sw;
    }

    @Override
    public void load(ReportReaderDAO dao) {
        if (!isLoaded) {
            softwareNameField.setText(dao.getSoftware().getName());
            softwareCommentField.setText(dao.getSoftware().getComments());
            softwareVersionField.setText(dao.getSoftware().getVersion());
            processingTable.add(dao.getProcessingMethod());
            //note that in this instance, the data is obtained from the ConverterData singleton
            //and not the dao
            databaseTable.addAll(ConverterData.getInstance().getDatabaseMappings());
            isLoaded = true;
        }
        //fire validation listener on load
        validationListerner.fireValidationListener(isNonNullTextField(softwareNameField.getText()) && isNonNullTextField(softwareVersionField.getText()));

    }

    @Override
    public String getFormName() {
        return "Software Processing";
    }

    @Override
    public String getFormDescription() {
        return config.getString("softwareprocessing.form.description");
    }

    @Override
    public Icon getFormIcon() {
        return getFormIcon("softwareprocessing.form.icon");
    }


    @Override
    public String getHelpResource() {
        return "help.ui.software";
    }

    @Override
    public void start() {
        //for navigation
        validationListerner.fireValidationListener(isNonNullTextField(softwareNameField.getText()) && isNonNullTextField(softwareVersionField.getText()));
    }

    @Override
    public void finish() {
        /* no op */
    }
}
