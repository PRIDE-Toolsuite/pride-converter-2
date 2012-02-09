/*
 * Created by JFormDesigner on Fri Oct 21 14:40:21 BST 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.gui.NavigationPanel;
import uk.ac.ebi.pride.tools.converter.gui.component.AddTermButton;
import uk.ac.ebi.pride.tools.converter.gui.component.table.ParamTable;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.UserParam;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Collections;

/**
 * @author User #3
 */
public class ExperimentDetailForm extends AbstractForm {

    private boolean doneOnce = false;

    public ExperimentDetailForm() {
        initComponents();
        addTermButton.setOwner(paramTable1);
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
        validationListerner.fireValidationListener(isNonNullTextField(s1) && isNonNullTextField(s2) && isNonNullTextField(s3));
    }

    private void experimentDetailsRequiredFieldFocusLost(FocusEvent e) {
        //fire super method to update component background
        validateRequiredField(e.getComponent(), null);
        //validate form and fire validationListener
        validationListerner.fireValidationListener(isNonNullTextField(projectNameInput.getText()) && isNonNullTextField(experimentTitleInput.getText()) && isNonNullTextField(shortNameInput.getText()));
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
        panel1 = new JPanel();
        scrollPane1 = new JScrollPane();
        descriptionPane = new JTextPane();
        label7 = new JLabel();
        scrollPane2 = new JScrollPane();
        paramTable1 = new ParamTable();
        addTermButton = new AddTermButton(true);
        label4 = new JLabel();
        label5 = new JLabel();
        label6 = new JLabel();

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

        //======== panel1 ========
        {
            panel1.setBorder(new TitledBorder("Description"));

            //======== scrollPane1 ========
            {
                scrollPane1.setViewportView(descriptionPane);
            }

            //---- label7 ----
            label7.setText("Additional Information");

            //======== scrollPane2 ========
            {
                scrollPane2.setViewportView(paramTable1);
            }

            //---- addTermButton ----
            addTermButton.setText("Add Param");

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                    panel1Layout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                    .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                            .addGroup(GroupLayout.Alignment.LEADING, panel1Layout.createSequentialGroup()
                                                    .addContainerGap()
                                                    .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 702, Short.MAX_VALUE))
                                            .addComponent(scrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
                                            .addGroup(panel1Layout.createSequentialGroup()
                                                    .addContainerGap()
                                                    .addComponent(label7)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 434, Short.MAX_VALUE)
                                                    .addComponent(addTermButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                                    .addContainerGap())
            );
            panel1Layout.setVerticalGroup(
                    panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                            .addComponent(addTermButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label7))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                                    .addContainerGap())
            );
        }

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

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(label3)
                                        .addComponent(label2)
                                        .addComponent(label1))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(projectNameInput, GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                                        .addComponent(experimentTitleInput, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                                        .addComponent(shortNameInput, GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(label4)
                                        .addComponent(label5)
                                        .addComponent(label6))
                                .addGap(175, 175, 175))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addGap(15, 15, 15)
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
                                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
    private JPanel panel1;
    private JScrollPane scrollPane1;
    private JTextPane descriptionPane;
    private JLabel label7;
    private JScrollPane scrollPane2;
    private ParamTable paramTable1;
    private AddTermButton addTermButton;
    private JLabel label4;
    private JLabel label5;
    private JLabel label6;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    @Override
    public Collection<ValidatorMessage> validateForm() {
        //nothing to validate
        return Collections.emptyList();
    }

    @Override
    public void clear() {
        isLoaded = false;
        projectNameInput.setText(null);
        experimentTitleInput.setText(null);
        shortNameInput.setText(null);
        descriptionPane.setText(null);
        paramTable1.removeAll();
        //inactivate next button
        validationListerner.fireValidationListener(false);
    }

    @Override
    public void save(ReportReaderDAO dao) {
        Param p = new Param();
        p.getCvParam().addAll(paramTable1.getCvParamList());
        p.getCvParam().add(new CvParam(DAOCvParams.PRIDE_PROJECT.getCv(), DAOCvParams.PRIDE_PROJECT.getAccession(), DAOCvParams.PRIDE_PROJECT.getName(), projectNameInput.getText()));
        p.getUserParam().addAll(paramTable1.getUserParamList());
        dao.setExperimentParams(p);
        dao.setExperimentShortLabel(shortNameInput.getText());
        dao.setExperimentTitle(experimentTitleInput.getText());
    }

    @Override
    public void load(ReportReaderDAO dao) {
        if (!isLoaded) {
            experimentTitleInput.setText(dao.getExperimentTitle());
            shortNameInput.setText(dao.getExperimentShortLabel());
            Param param = dao.getExperimentParams();
            for (CvParam cv : param.getCvParam()) {
                if (DAOCvParams.PRIDE_PROJECT.getAccession().equals(cv.getAccession())) {
                    projectNameInput.setText(cv.getValue());
                } else {
                    paramTable1.add(cv);
                }
            }
            for (UserParam up : param.getUserParam()) {
                paramTable1.add(up);
            }
            isLoaded = true;
        }
        //fire validation listener on load
        validationListerner.fireValidationListener(isNonNullTextField(projectNameInput.getText()) && isNonNullTextField(experimentTitleInput.getText()) && isNonNullTextField(shortNameInput.getText()));
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
    public String getHelpResource() {
        return "help.ui.experimentdetails";
    }

    @Override
    public void start() {
        //validate form and fire validationListener - required for back & forth when no changes occur
        validationListerner.fireValidationListener(isNonNullTextField(projectNameInput.getText()) && isNonNullTextField(experimentTitleInput.getText()) && isNonNullTextField(shortNameInput.getText()));
    }

    @Override
    public void finish() {
        //if there are multiple source files in the conversion, add another form in the UI
        //to deal with this data
        if (ConverterData.getInstance().getInputFiles().size() > 1 && !doneOnce) {
            NavigationPanel.getInstance().registerFormAfter(new ExperimentDetailMultipleDataForm(), this);
            doneOnce = true;
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.add(new ExperimentDetailForm());
        f.pack();
        f.setVisible(true);
    }
}
