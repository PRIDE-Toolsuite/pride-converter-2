/*
 * Created by JFormDesigner on Tue Feb 28 12:14:13 GMT 2012
 */

package uk.ac.ebi.pride.tools.converter.gui.dialogs;

import uk.ac.ebi.pride.tools.converter.gui.util.PreferenceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author User #3
 */
public class MultipleFormEditingWarningDialog extends JDialog {
    public MultipleFormEditingWarningDialog(Frame owner) {
        super(owner);
        initComponents();
    }

    public MultipleFormEditingWarningDialog(Dialog owner) {
        super(owner);
        initComponents();
    }

    private void checkBox1ActionPerformed(ActionEvent e) {
        if (checkBox1.isSelected()) {
            PreferenceManager.getInstance().setProperty(PreferenceManager.PREFERENCE.IGNORE_MULTIPLE_FILE_EDITING, Boolean.TRUE.toString());
        }
    }

    private void okButtonActionPerformed() {
        setVisible(false);
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        scrollPane1 = new JScrollPane();
        textPane1 = new JTextPane();
        okButton = new JButton();
        checkBox1 = new JCheckBox();

        //======== this ========
        setTitle("Warning - Editing multiple source files");
        Container contentPane = getContentPane();

        //======== scrollPane1 ========
        {

            //---- textPane1 ----
            textPane1.setBackground(null);
            textPane1.setEditable(false);
            textPane1.setText("You are converting multiple source files. Be aware that some information will be copied across all PRIDE XML files while some information will not be. Please refer to the PRIDE Converter Help to learn more about this feature.");
            scrollPane1.setViewportView(textPane1);
        }

        //---- okButton ----
        okButton.setText("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okButtonActionPerformed();
            }
        });

        //---- checkBox1 ----
        checkBox1.setText("Never show this warning again");
        checkBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkBox1ActionPerformed(e);
            }
        });

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(scrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addComponent(checkBox1, GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(okButton, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(okButton)
                                        .addComponent(checkBox1, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JScrollPane scrollPane1;
    private JTextPane textPane1;
    private JButton okButton;
    private JCheckBox checkBox1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
