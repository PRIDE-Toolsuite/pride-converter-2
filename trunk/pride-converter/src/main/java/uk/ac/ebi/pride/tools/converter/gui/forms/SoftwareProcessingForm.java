/*
 * Created by JFormDesigner on Fri Mar 11 13:17:39 GMT 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import org.apache.log4j.Logger;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.gui.component.AddTermButton;
import uk.ac.ebi.pride.tools.converter.gui.component.table.ParamTable;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
import uk.ac.ebi.pride.tools.converter.report.model.DataProcessing;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.Software;
import uk.ac.ebi.pride.tools.converter.report.validator.ReportObjectValidator;
import uk.ac.ebi.pride.tools.converter.utils.xml.validation.ValidatorFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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

        //======== this ========

        //---- label1 ----
        label1.setText(bundle.getString("SoftwareProcessingForm.label1.text"));

        //---- addTermButton ----
        addTermButton.setText(bundle.getString("SoftwareProcessingForm.addTermButton.text"));

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
                                                    .addComponent(softwareNameField, GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                                                    .addGap(18, 18, 18)
                                                    .addComponent(label3)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(label6, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(softwareVersionField, GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE))
                                            .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE))
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
                                    .addContainerGap())
            );
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(panel1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(scrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 633, Short.MAX_VALUE)
                                        .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addComponent(label1)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 379, Short.MAX_VALUE)
                                                .addComponent(addTermButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(panel1, GroupLayout.PREFERRED_SIZE, 152, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(label1, GroupLayout.Alignment.TRAILING)
                                        .addComponent(addTermButton, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
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
        //inactivate next button
        validationListerner.fireValidationListener(false);
    }

    @Override
    public void save(ReportReaderDAO dao) {
        dao.setSoftware(makeSoftware());
        dao.setProcessingMethod(makeProcessingMethod());
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
