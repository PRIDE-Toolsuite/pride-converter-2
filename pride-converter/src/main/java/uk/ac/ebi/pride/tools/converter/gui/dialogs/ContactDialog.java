/*
 * Created by JFormDesigner on Fri Oct 21 13:45:51 BST 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.dialogs;

import uk.ac.ebi.pride.tools.converter.gui.NavigationPanel;
import uk.ac.ebi.pride.tools.converter.gui.component.table.BaseTable;
import uk.ac.ebi.pride.tools.converter.gui.util.template.TemplateType;
import uk.ac.ebi.pride.tools.converter.report.model.Contact;
import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author User #3
 */
public class ContactDialog extends AbstractDialog {

    public ContactDialog(Frame owner, BaseTable parentTable) {
        super(owner);
        initComponents();
        this.callback = parentTable;
    }

    public ContactDialog(Dialog owner, BaseTable parentTable) {
        super(owner);
        initComponents();
        this.callback = parentTable;
    }

    private void okButtonActionPerformed(ActionEvent e) {
        Contact c = makeContact();
        if (!isEditing) {
            callback.add(c);
        } else {
            callback.update(c);
        }
        setVisible(false);
        dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        setVisible(false);
        dispose();
    }

    private void contactDialogFocusLost(FocusEvent e) {
        validateRequiredField(e.getComponent(), null);
        okButton.setEnabled(isNonNullTextField(nameField.getText()) && isNonNullTextField(emailField.getText()) && isNonNullTextField(institutionField.getText()));
        saveButton.setEnabled(isNonNullTextField(nameField.getText()) && isNonNullTextField(emailField.getText()) && isNonNullTextField(institutionField.getText()));
    }

    private void contactDialogKeyTyped(KeyEvent e) {
        validateRequiredField(e.getComponent(), e);
        String s1 = nameField.getText();
        String s2 = emailField.getText();
        String s3 = institutionField.getText();
        if (e.getSource().equals(nameField)) {
            s1 += e.getKeyChar();
        } else if (e.getSource().equals(emailField)) {
            s2 += e.getKeyChar();
        } else if (e.getSource().equals(institutionField)) {
            s3 += e.getKeyChar();
        }
        okButton.setEnabled(isNonNullTextField(s1) && isNonNullTextField(s2) && isNonNullTextField(s3));
        saveButton.setEnabled(isNonNullTextField(s1) && isNonNullTextField(s2) && isNonNullTextField(s3));
    }

    @Override
    public void edit(ReportObject object) {
        Contact c = (Contact) object;
        nameField.setText(c.getName());
        emailField.setText(c.getContactInfo());
        institutionField.setText(c.getInstitution());
        okButton.setEnabled(isNonNullTextField(nameField.getText()) && isNonNullTextField(emailField.getText()) && isNonNullTextField(institutionField.getText()));
        saveButton.setEnabled(isNonNullTextField(nameField.getText()) && isNonNullTextField(emailField.getText()) && isNonNullTextField(institutionField.getText()));
    }

    private Contact makeContact() {
        Contact c = new Contact();
        c.setName(nameField.getText());
        c.setContactInfo(emailField.getText());
        c.setInstitution(institutionField.getText());
        return c;
    }

    private void saveButtonActionPerformed() {
        saveTemplate(nameField.getText(), TemplateType.CONTACT, makeContact());
    }

    private void loadButtonActionPerformed() {
        String[] templates = getTemplateNames(TemplateType.CONTACT);
        LoadTemplateDialog dialog = new LoadTemplateDialog(this, NavigationPanel.getInstance(), templates);
        dialog.setVisible(true);
    }

    @Override
    public void loadTemplate(String templateName) {
        edit(loadTemplate(templateName, TemplateType.CONTACT));
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        label1 = new JLabel();
        label2 = new JLabel();
        label3 = new JLabel();
        institutionField = new JTextField();
        emailField = new JTextField();
        nameField = new JTextField();
        label5 = new JLabel();
        label4 = new JLabel();
        cancelButton = new JButton();
        okButton = new JButton();
        loadButton = new JButton();
        saveButton = new JButton();
        label6 = new JLabel();

        //======== this ========
        setResizable(false);
        setTitle("Contact");
        Container contentPane = getContentPane();

        //---- label1 ----
        label1.setText("Name");

        //---- label2 ----
        label2.setText("Email");

        //---- label3 ----
        label3.setText("Institution");

        //---- institutionField ----
        institutionField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                contactDialogFocusLost(e);
            }
        });
        institutionField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                contactDialogKeyTyped(e);
            }
        });

        //---- emailField ----
        emailField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                contactDialogFocusLost(e);
            }
        });
        emailField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                contactDialogKeyTyped(e);
            }
        });

        //---- nameField ----
        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                contactDialogFocusLost(e);
            }
        });
        nameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                contactDialogKeyTyped(e);
            }
        });

        //---- label5 ----
        label5.setText("*");
        label5.setForeground(Color.red);

        //---- label4 ----
        label4.setText("*");
        label4.setForeground(Color.red);

        //---- cancelButton ----
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelButtonActionPerformed(e);
            }
        });

        //---- okButton ----
        okButton.setText("OK");
        okButton.setEnabled(false);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okButtonActionPerformed(e);
            }
        });

        //---- loadButton ----
        loadButton.setText("Load");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadButtonActionPerformed();
            }
        });

        //---- saveButton ----
        saveButton.setText("Save");
        saveButton.setEnabled(false);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveButtonActionPerformed();
            }
        });

        //---- label6 ----
        label6.setText("*");
        label6.setForeground(Color.red);

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addGap(86, 86, 86)
                                                .addComponent(loadButton)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(saveButton)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 126, Short.MAX_VALUE)
                                                .addComponent(okButton, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cancelButton))
                                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .addGroup(contentPaneLayout.createParallelGroup()
                                                        .addComponent(label3, GroupLayout.Alignment.TRAILING)
                                                        .addComponent(label2, GroupLayout.Alignment.TRAILING)
                                                        .addComponent(label1, GroupLayout.Alignment.TRAILING))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(contentPaneLayout.createParallelGroup()
                                                        .addComponent(emailField, GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                                                        .addComponent(institutionField, GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                                                        .addComponent(nameField, GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                        .addComponent(label4, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(label5, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(label6, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE))))
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(nameField)
                                        .addComponent(label1)
                                        .addComponent(label5))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(emailField)
                                        .addComponent(label2)
                                        .addComponent(label4))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(institutionField)
                                        .addComponent(label3)
                                        .addComponent(label6))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(cancelButton)
                                        .addComponent(okButton)
                                        .addComponent(loadButton)
                                        .addComponent(saveButton))
                                .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JTextField institutionField;
    private JTextField emailField;
    private JTextField nameField;
    private JLabel label5;
    private JLabel label4;
    private JButton cancelButton;
    private JButton okButton;
    private JButton loadButton;
    private JButton saveButton;
    private JLabel label6;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
