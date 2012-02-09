/*
 * Created by JFormDesigner on Wed Aug 10 10:54:35 BST 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.dialogs;

import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.gui.component.table.LineWrapCellRenderer;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.ValidatorMessageTableModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author User #3
 */
public class TabbedValidationDialog extends JDialog {

    private Map<String, Collection<ValidatorMessage>> messages;
    private List<String> forms;
    private String selectedTab;

    public TabbedValidationDialog(Frame owner, Map<String, Collection<ValidatorMessage>> messages) {
        super(owner);
        initComponents();
        this.messages = messages;
        this.forms = new ArrayList<String>(messages.keySet());
        updateTabs();
    }

    private void updateTabs() {
        tabbedPane.removeAll();
        for (String formName : messages.keySet()) {
            tabbedPane.add(formName, createTable(messages.get(formName)));
        }
        if (selectedTab != null) {
            tabbedPane.setSelectedIndex(forms.indexOf(selectedTab));
        }
    }

    private JScrollPane createTable(Collection<ValidatorMessage> messages) {

        JScrollPane scrollPane = new JScrollPane();
        JTable messageTable = new JTable();
        messageTable.setModel(new ValidatorMessageTableModel(messages, collapseValidationMessagesBox.isSelected()));
        messageTable.setDefaultRenderer(String.class, new LineWrapCellRenderer());
        messageTable.setAutoCreateRowSorter(true);
        TableColumnModel cm = messageTable.getColumnModel();
        cm.getColumn(0).setPreferredWidth(75);
        cm.getColumn(1).setPreferredWidth(75);
        cm.getColumn(2).setPreferredWidth(400);
        scrollPane.setViewportView(messageTable);
        return scrollPane;

    }

    private void okButtonActionPerformed() {
        setVisible(false);
        dispose();
    }

    private void collapseValidationMessagesBoxActionPerformed(ActionEvent e) {
        //keep track of which tab we are on before rebuilding the table
        selectedTab = forms.get(tabbedPane.getSelectedIndex());
        updateTabs();
    }

    public void setSelectedTab(String selectedTab) {
        this.selectedTab = selectedTab;
        updateTabs();
    }

    public void selectLastTab() {
        if (!forms.isEmpty()) {
            this.selectedTab = forms.get(forms.size() - 1);
            updateTabs();
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        tabbedPane = new JTabbedPane();
        panel1 = new JPanel();
        scrollPane1 = new JScrollPane();
        table1 = new JTable();
        collapseValidationMessagesBox = new JCheckBox();
        buttonBar = new JPanel();
        okButton = new JButton();

        //======== this ========
        setTitle("Report Validation Messages");
        setMinimumSize(new Dimension(480, 585));
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {

                //======== tabbedPane ========
                {

                    //======== panel1 ========
                    {

                        //======== scrollPane1 ========
                        {
                            scrollPane1.setViewportView(table1);
                        }

                        GroupLayout panel1Layout = new GroupLayout(panel1);
                        panel1.setLayout(panel1Layout);
                        panel1Layout.setHorizontalGroup(
                                panel1Layout.createParallelGroup()
                                        .addGroup(panel1Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 718, Short.MAX_VALUE))
                        );
                        panel1Layout.setVerticalGroup(
                                panel1Layout.createParallelGroup()
                                        .addGroup(panel1Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE))
                        );
                    }
                    tabbedPane.addTab("formName", panel1);

                }

                //---- collapseValidationMessagesBox ----
                collapseValidationMessagesBox.setText("Collapse validation messages with identical rule IDs");
                collapseValidationMessagesBox.setSelected(true);
                collapseValidationMessagesBox.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        collapseValidationMessagesBoxActionPerformed(e);
                    }
                });

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                        contentPanelLayout.createParallelGroup()
                                .addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 729, Short.MAX_VALUE)
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(collapseValidationMessagesBox)
                                        .addContainerGap(329, Short.MAX_VALUE))
                );
                contentPanelLayout.setVerticalGroup(
                        contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                        .addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 462, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(collapseValidationMessagesBox)
                                        .addContainerGap())
                );
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

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
                        okButtonActionPerformed();
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
    private JTabbedPane tabbedPane;
    private JPanel panel1;
    private JScrollPane scrollPane1;
    private JTable table1;
    private JCheckBox collapseValidationMessagesBox;
    private JPanel buttonBar;
    private JButton okButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
