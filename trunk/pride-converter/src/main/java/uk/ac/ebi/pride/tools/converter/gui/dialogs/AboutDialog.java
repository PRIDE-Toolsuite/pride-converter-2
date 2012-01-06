/*
 * Created by JFormDesigner on Fri Jul 08 10:54:35 BST 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.dialogs;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

/**
 * @author rcote
 */
public class AboutDialog extends JDialog {

    private static final String copyrightInfo =

            "Copyright 2011 European Bioinformatics Institute\n" +
                    "\n" +
                    "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                    "you may not use this file except in compliance with the License.\n" +
                    "You may obtain a copy of the License at\n" +
                    "\n" +
                    "    http://www.apache.org/licenses/LICENSE-2.0\n" +
                    "\n" +
                    "Unless required by applicable law or agreed to in writing, software\n" +
                    "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                    "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                    "See the License for the specific language governing permissions and\n" +
                    "limitations under the License.";

    public AboutDialog(Frame owner) {
        super(owner);
        initComponents();
        getBuildInfo();
    }

    public AboutDialog(Dialog owner) {
        super(owner);
        initComponents();
        getBuildInfo();
    }

    public void getBuildInfo() {
        ResourceBundle bundle = ResourceBundle.getBundle("gui-settings");
        buildNumber.setText(bundle.getString("build.version"));
        buildDate.setText(bundle.getString("build.date"));
    }

    private void okButtonActionPerformed(ActionEvent e) {
        setVisible(false);
    }

    private void easterEggMouseClicked(MouseEvent e) {

        if (e.getClickCount() == 3) {
            new ImageDialog(this).setVisible(true);
        }

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label1 = new JLabel();
        label2 = new JLabel();
        buildNumber = new JTextField();
        buildDate = new JTextField();
        scrollPane1 = new JScrollPane();
        textPane1 = new JTextPane();
        iconLabel = new JLabel();
        buttonBar = new JPanel();
        okButton = new JButton();

        //======== this ========
        setTitle("About Pride Converter");
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {

                //---- label1 ----
                label1.setText("Build Number");
                label1.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        easterEggMouseClicked(e);
                    }
                });

                //---- label2 ----
                label2.setText("Build Date");

                //---- buildNumber ----
                buildNumber.setEditable(false);

                //---- buildDate ----
                buildDate.setEditable(false);

                //======== scrollPane1 ========
                {

                    //---- textPane1 ----
                    textPane1.setFont(new Font("Dialog", Font.PLAIN, 11));
                    textPane1.setText(copyrightInfo);
                    StyledDocument doc = textPane1.getStyledDocument();
                    SimpleAttributeSet center = new SimpleAttributeSet();
                    StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
                    doc.setParagraphAttributes(0, doc.getLength(), center, false);
                    scrollPane1.setViewportView(textPane1);
                }

                //---- iconLabel ----
                iconLabel.setMaximumSize(new Dimension(32, 32));
                iconLabel.setMinimumSize(new Dimension(32, 32));
                iconLabel.setPreferredSize(new Dimension(32, 32));
                iconLabel.setIcon(new ImageIcon(getClass().getResource("/images/converter-icon-32.png")));

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                        contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                        .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                .addComponent(label2)
                                                .addComponent(label1))
                                        .addGap(18, 18, 18)
                                        .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                .addComponent(buildDate)
                                                .addComponent(buildNumber, GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE))
                                        .addGap(18, 18, 18)
                                        .addComponent(iconLabel, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(31, Short.MAX_VALUE))
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
                );
                contentPanelLayout.setVerticalGroup(
                        contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                        .addGroup(contentPanelLayout.createParallelGroup()
                                                .addGroup(contentPanelLayout.createSequentialGroup()
                                                        .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                                .addComponent(buildNumber, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(label1))
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                        .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                                .addComponent(buildDate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(label2)))
                                                .addComponent(iconLabel, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                                        .addContainerGap())
                );
            }
            dialogPane.add(contentPanel, BorderLayout.WEST);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[]{0, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[]{1.0, 0.0};

                //---- okButton ----
                okButton.setText("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed(e);
                    }
                });
                buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
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
    private JLabel label1;
    private JLabel label2;
    private JTextField buildNumber;
    private JTextField buildDate;
    private JScrollPane scrollPane1;
    private JTextPane textPane1;
    private JLabel iconLabel;
    private JPanel buttonBar;
    private JButton okButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public static void main(String[] args) {
        AboutDialog ab = new AboutDialog(new JFrame());
        ab.setVisible(true);
    }
}
