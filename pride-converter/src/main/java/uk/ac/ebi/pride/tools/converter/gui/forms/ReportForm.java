/*
 * Created by JFormDesigner on Wed May 25 12:06:57 BST 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.gui.NavigationPanel;
import uk.ac.ebi.pride.tools.converter.gui.component.table.LineWrapCellRenderer;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.ValidatorMessageTableModel;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * @author User #1
 * @author rcote
 */
public class ReportForm extends AbstractForm {

    public ReportForm() {
        initComponents();
    }


    private void collapseValidationMessageBoxActionPerformed(ActionEvent e) {
        refreshValidationReport();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("messages");
        panel2 = new JPanel();
        scrollPane2 = new JScrollPane();
        messageTable = new JTable();
        collapseValidationMessageBox = new JCheckBox();
        scrollPane1 = new JScrollPane();
        statusArea = new JTextArea();
        label1 = new JLabel();

        //======== this ========

        //======== panel2 ========
        {
            panel2.setBorder(new TitledBorder(bundle.getString("ReportPanel.panel2.border")));

            //======== scrollPane2 ========
            {

                //---- messageTable ----
                messageTable.setModel(new DefaultTableModel(
                        new Object[][]{
                                {null, null, null},
                                {null, null, null},
                        },
                        new String[]{
                                "Severity", "Rule ID", "Message"
                        }
                ) {
                    Class<?>[] columnTypes = new Class<?>[]{
                            String.class, String.class, Object.class
                    };
                    boolean[] columnEditable = new boolean[]{
                            false, false, false
                    };

                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }

                    @Override
                    public boolean isCellEditable(int rowIndex, int columnIndex) {
                        return columnEditable[columnIndex];
                    }
                });
                {
                    TableColumnModel cm = messageTable.getColumnModel();
                    cm.getColumn(0).setPreferredWidth(75);
                    cm.getColumn(1).setPreferredWidth(75);
                    cm.getColumn(2).setPreferredWidth(400);
                }
                scrollPane2.setViewportView(messageTable);
            }

            //---- collapseValidationMessageBox ----
            collapseValidationMessageBox.setText(bundle.getString("ReportPanel.collapseValidationMessageBox.text"));
            collapseValidationMessageBox.setSelected(true);
            collapseValidationMessageBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    collapseValidationMessageBoxActionPerformed(e);
                }
            });

            GroupLayout panel2Layout = new GroupLayout(panel2);
            panel2.setLayout(panel2Layout);
            panel2Layout.setHorizontalGroup(
                    panel2Layout.createParallelGroup()
                            .addGroup(panel2Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(panel2Layout.createParallelGroup()
                                            .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 611, Short.MAX_VALUE)
                                            .addComponent(collapseValidationMessageBox))
                                    .addContainerGap())
            );
            panel2Layout.setVerticalGroup(
                    panel2Layout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
                                    .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(collapseValidationMessageBox))
            );
        }

        //======== scrollPane1 ========
        {

            //---- statusArea ----
            statusArea.setText(bundle.getString("ReportPanel.statusArea.text"));
            scrollPane1.setViewportView(statusArea);
        }

        //---- label1 ----
        label1.setText(bundle.getString("ReportPanel.label1.text"));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(panel2, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(scrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 633, Short.MAX_VALUE)
                                        .addComponent(label1, GroupLayout.Alignment.LEADING))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(label1)
                                .addGap(11, 11, 11)
                                .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel panel2;
    private JScrollPane scrollPane2;
    private JTable messageTable;
    private JCheckBox collapseValidationMessageBox;
    private JScrollPane scrollPane1;
    private JTextArea statusArea;
    private JLabel label1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public void refreshValidationReport() {

        //get validation messages from ConverterData
        Map<String, Collection<ValidatorMessage>> validationMessages = ConverterData.getInstance().getValidationMessages();
        //make stats
        int nbFiles = validationMessages.size();
        int nbError = 0;
        int nbWarning = 0;
        for (Collection<ValidatorMessage> msgs : validationMessages.values()) {
            for (ValidatorMessage msg : msgs) {
                //FATAL > ERROR > WARN > INFO > DEBUG
                if (msg.getLevel().isSame(MessageLevel.ERROR) || msg.getLevel().isSame(MessageLevel.FATAL)) {
                    nbError++;
                }
                if (msg.getLevel().isSame(MessageLevel.WARN)) {
                    nbWarning++;
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(nbFiles).append(" files generated. ");
        sb.append(nbWarning).append(" warnings generated. ");
        sb.append(nbError).append(" errors detected.");

        statusArea.setText(sb.toString());

        ArrayList<ValidatorMessage> msgs = new ArrayList<ValidatorMessage>();
        for (Collection<ValidatorMessage> msgColl : validationMessages.values()) {
            msgs.addAll(msgColl);
        }
        messageTable.setAutoCreateRowSorter(true);
        messageTable.setModel(new ValidatorMessageTableModel(msgs, collapseValidationMessageBox.isSelected()));
        messageTable.setDefaultRenderer(String.class, new LineWrapCellRenderer());
        TableColumnModel cm = messageTable.getColumnModel();
        cm.getColumn(0).setPreferredWidth(75);
        cm.getColumn(1).setPreferredWidth(75);
        cm.getColumn(2).setPreferredWidth(400);

    }

    @Override
    public Collection<ValidatorMessage> validateForm() {
        //nothing to validate
        return Collections.emptyList();
    }

    @Override
    public void clear() {
        /* no op */
    }

    @Override
    public void save(ReportReaderDAO dao) {
        /* no op */
    }

    @Override
    public void load(ReportReaderDAO dao) {
        /* no op */
    }

    @Override
    public String getFormName() {
        return "Validation Report";
    }

    @Override
    public String getFormDescription() {
        return config.getString("report.form.description");
    }

    @Override
    public Icon getFormIcon() {
        return getFormIcon("report.form.icon");
    }

    @Override
    public String getHelpResource() {
        return "help.ui.validation";
    }

    @Override
    public void start() {
        NavigationPanel.getInstance().hideValidationIcon();
        refreshValidationReport();
        //validate form and fire validationListener - required for back & forth when no changes occur
        validationListerner.fireValidationListener(true);
    }

    @Override
    public void finish() {
        /* no op */
    }
}
