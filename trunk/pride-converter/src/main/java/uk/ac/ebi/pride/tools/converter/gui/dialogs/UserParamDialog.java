/*
 * Created by JFormDesigner on Fri Oct 21 13:54:23 BST 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.dialogs;

import uk.ac.ebi.pride.tools.converter.gui.component.table.ParamTable;
import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;
import uk.ac.ebi.pride.tools.converter.report.model.UserParam;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ResourceBundle;

/**
 * @author User #3
 */
public class UserParamDialog extends AbstractDialog {

    public UserParamDialog(Frame owner, ParamTable table) {
        super(owner);
        callback = table;
        initComponents();
    }

    public UserParamDialog(Dialog owner, ParamTable table) {
        super(owner);
        callback = table;
        initComponents();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        setVisible(false);
        dispose();
    }

    private void okButtonActionPerformed(ActionEvent e) {
        if (!isEditing) {
            callback.add(new UserParam(nameTextField.getText(), valueTextField.getText()));
        } else {
            callback.update(new UserParam(nameTextField.getText(), valueTextField.getText()));
        }
        setVisible(false);
        dispose();
    }

    @Override
    public void edit(ReportObject object) {
        UserParam c = (UserParam) object;
        nameTextField.setText(c.getName());
        valueTextField.setText(c.getValue());
        okButton.setEnabled(isNonNullTextField(nameTextField.getText()));
    }

    private void nameTextFieldFocusLost() {
        validateRequiredField(nameTextField, null);
        okButton.setEnabled(isNonNullTextField(nameTextField.getText()));
    }

    private void nameTextFieldKeyTyped(KeyEvent e) {
        validateRequiredField(e.getComponent(), e);
        String s1 = nameTextField.getText();
        s1 += e.getKeyChar();
        okButton.setEnabled(isNonNullTextField(s1));

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("messages");
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        nameTextField = new JTextField();
        valueTextField = new JTextField();
        label1 = new JLabel();
        label2 = new JLabel();
        label7 = new JLabel();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setTitle(bundle.getString("NewUserParam.this.title"));
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setBorder(null);

                //---- nameTextField ----
                nameTextField.setToolTipText(bundle.getString("NewUserParam.nameTextField.toolTipText"));
                nameTextField.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        nameTextFieldFocusLost();
                    }
                });
                nameTextField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        nameTextFieldKeyTyped(e);
                    }
                });

                //---- valueTextField ----
                valueTextField.setToolTipText(bundle.getString("NewUserParam.valueTextField.toolTipText"));

                //---- label1 ----
                label1.setText(bundle.getString("NewUserParam.name.text"));

                //---- label2 ----
                label2.setText(bundle.getString("NewUserParam.value.text"));

                //---- label7 ----
                label7.setText("*");
                label7.setForeground(Color.red);

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                        contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                        .addGap(28, 28, 28)
                                        .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                .addComponent(label1)
                                                .addComponent(label2))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(contentPanelLayout.createParallelGroup()
                                                .addComponent(valueTextField, GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                                                .addComponent(nameTextField, GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label7, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap())
                );
                contentPanelLayout.setVerticalGroup(
                        contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(label1)
                                                .addComponent(label7))
                                        .addGap(18, 18, 18)
                                        .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(valueTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(label2))
                                        .addContainerGap(16, Short.MAX_VALUE))
                );
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[]{0, 85, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[]{1.0, 0.0, 0.0};

                //---- okButton ----
                okButton.setText(bundle.getString("NewUserParam.okButton.text"));
                okButton.setEnabled(false);
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed(e);
                    }
                });
                buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setText(bundle.getString("NewUserParam.cancelButton.text"));
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JTextField nameTextField;
    private JTextField valueTextField;
    private JLabel label1;
    private JLabel label2;
    private JLabel label7;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
