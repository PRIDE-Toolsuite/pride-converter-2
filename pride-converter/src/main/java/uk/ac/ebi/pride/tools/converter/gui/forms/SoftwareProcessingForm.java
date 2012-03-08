/*
 * Created by JFormDesigner on Fri Mar 11 13:17:39 GMT 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import org.apache.log4j.Logger;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.gui.component.AddTermButton;
import uk.ac.ebi.pride.tools.converter.gui.component.table.ParamTable;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.ParamTableModel;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.AbstractDialog;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
import uk.ac.ebi.pride.tools.converter.report.model.*;
import uk.ac.ebi.pride.tools.converter.report.validator.ReportObjectValidator;
import uk.ac.ebi.pride.tools.converter.utils.xml.validation.ValidatorFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
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
        addTermButton1.setOwner(expAdditionalTable);
        updateTermButtonCvList(addTermButton, "softwareprocessing.suggested.cv");
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

    private void editButtonActionPerformed(ActionEvent e) {
        ParamTable table = null;
        if (e.getSource().equals(expAdditionalEditButton)) {
            table = expAdditionalTable;
        } else if (e.getSource().equals(processingMethodEditButton)) {
            table = processingTable;
        }
        if (table != null && table.getSelectedRowCount() > 0) {
            //convert table selected row to underlying model row
            int modelSelectedRow = table.convertRowIndexToModel(table.getSelectedRow());
            //get object
            ReportObject objToEdit = ((ParamTableModel) table.getModel()).get(modelSelectedRow);
            Class clazz = objToEdit.getClass();
            //show editing dialog for object
            AbstractDialog dialog = AbstractDialog.getInstance(table, clazz);
            dialog.edit(objToEdit);
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
        label7 = new JLabel();
        scrollPane3 = new JScrollPane();
        expAdditionalTable = new ParamTable();
        addTermButton1 = new AddTermButton();
        processingMethodEditButton = new JButton();
        expAdditionalEditButton = new JButton();

        //======== this ========

        //---- label1 ----
        label1.setText(bundle.getString("SoftwareProcessingForm.label1.text"));

        //---- addTermButton ----
        addTermButton.setToolTipText(bundle.getString("SoftwareProcessingForm.addTermButton.toolTipText"));

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(processingTable);
        }

        //======== panel1 ========
        {
            panel1.setBorder(new TitledBorder(bundle.getString("SoftwareProcessingForm.panel1.border")));

            //---- label2 ----
            label2.setText(bundle.getString("SoftwareProcessingForm.label2.text"));

            //---- label3 ----
            label3.setText(bundle.getString("SoftwareProcessingForm.label3.text"));

            //---- label4 ----
            label4.setText(bundle.getString("SoftwareProcessingForm.label4.text"));

            //---- softwareNameField ----
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

        //---- label7 ----
        label7.setText(bundle.getString("SoftwareProcessingForm.label7.text"));

        //======== scrollPane3 ========
        {
            scrollPane3.setViewportView(expAdditionalTable);
        }

        //---- addTermButton1 ----
        addTermButton1.setToolTipText(bundle.getString("SoftwareProcessingForm.addTermButton1.toolTipText"));

        //---- processingMethodEditButton ----
        processingMethodEditButton.setIcon(new ImageIcon(getClass().getResource("/images/edit.png")));
        processingMethodEditButton.setToolTipText(bundle.getString("SoftwareProcessingForm.processingMethodEditButton.toolTipText"));
        processingMethodEditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editButtonActionPerformed(e);
            }
        });

        //---- expAdditionalEditButton ----
        expAdditionalEditButton.setIcon(new ImageIcon(getClass().getResource("/images/edit.png")));
        expAdditionalEditButton.setToolTipText(bundle.getString("SoftwareProcessingForm.expAdditionalEditButton.toolTipText"));
        expAdditionalEditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editButtonActionPerformed(e);
            }
        });

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(panel1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(scrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 633, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(label1)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 383, Short.MAX_VALUE)
                                                .addComponent(addTermButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(processingMethodEditButton))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(label7)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 286, Short.MAX_VALUE)
                                                .addComponent(addTermButton1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(expAdditionalEditButton))
                                        .addComponent(scrollPane3, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 633, Short.MAX_VALUE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(panel1, GroupLayout.PREFERRED_SIZE, 152, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(addTermButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(processingMethodEditButton)
                                        .addComponent(label1))
                                .addGap(6, 6, 6)
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(addTermButton1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label7)
                                        .addComponent(expAdditionalEditButton))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane3, GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
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
    private JLabel label7;
    private JScrollPane scrollPane3;
    private ParamTable expAdditionalTable;
    private AddTermButton addTermButton1;
    private JButton processingMethodEditButton;
    private JButton expAdditionalEditButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    @Override
    public Collection<ValidatorMessage> validateForm() throws ValidatorException {
        ReportObjectValidator validator = ValidatorFactory.getInstance().getReportValidator();
        DataProcessing dataProcessing = new DataProcessing();
        dataProcessing.setSoftware(makeSoftware());
        dataProcessing.setProcessingMethod(makeProcessingMethod());
        return validator.validate(dataProcessing);
    }

    @Override
    public void clear() {
        isLoaded = false;
        softwareNameField.setText(null);
        softwareCommentField.setText(null);
        softwareVersionField.setText(null);
        processingTable.removeAll();
        expAdditionalTable.removeAll();
        //inactivate next button
        validationListerner.fireValidationListener(false);
    }

    @Override
    public void save(ReportReaderDAO dao) {
        dao.setSoftware(makeSoftware());
        dao.setProcessingMethod(makeProcessingMethod());
        saveExperimentalAdditionalParams(dao);
    }

    private void saveExperimentalAdditionalParams(ReportReaderDAO dao) {

        Param p = new Param();
        p.getCvParam().addAll(expAdditionalTable.getCvParamList());
        p.getUserParam().addAll(expAdditionalTable.getUserParamList());
        Param daoParam = dao.getExperimentParams();
        if (daoParam != null) {
            for (CvParam cv : daoParam.getCvParam()) {
                if (cv.getAccession().equals(DAOCvParams.PRIDE_PROJECT.getAccession()) ||
                        cv.getAccession().equals(DAOCvParams.EXPERIMENT_DESCRIPTION.getAccession())) {
                    p.getCvParam().add(cv);
                }
            }
        }
        dao.setExperimentParams(p);

    }

    private void loadExperimentalAdditionalParams(ReportReaderDAO dao) {

        Param p = dao.getExperimentParams();
        if (p != null) {
            for (CvParam cv : p.getCvParam()) {
                if (cv.getAccession().equals(DAOCvParams.PRIDE_PROJECT.getAccession()) ||
                        cv.getAccession().equals(DAOCvParams.EXPERIMENT_DESCRIPTION.getAccession())) {
                    continue;
                }
                expAdditionalTable.add(cv);
            }
            for (UserParam u : p.getUserParam()) {
                expAdditionalTable.add(u);
            }
        }
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
            loadExperimentalAdditionalParams(dao);
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
