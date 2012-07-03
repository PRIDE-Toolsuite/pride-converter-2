/*
 * Created by JFormDesigner on Wed Nov 02 14:49:41 GMT 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.dialogs;

import uk.ac.ebi.pride.tools.converter.gui.component.table.BaseTable;
import uk.ac.ebi.pride.tools.converter.gui.util.template.TemplateUtilities;
import uk.ac.ebi.pride.tools.converter.report.model.DatabaseMapping;
import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author User #3
 */
public class DatabaseMappingDialog extends AbstractDialog {

    public DatabaseMappingDialog(Frame owner, BaseTable<DatabaseMapping> paramTable) {
        super(owner);
        callback = paramTable;
        initComponents();
        initDatabaseNames();
    }

    public DatabaseMappingDialog(Dialog owner, BaseTable<DatabaseMapping> paramTable) {
        super(owner);
        callback = paramTable;
        initComponents();
        initDatabaseNames();
    }

    private void initDatabaseNames() {
        Map<String, String> databases = TemplateUtilities.initMapCache("databases.txt");
        //update default "PLEASE SELECT"
        databases.put(TemplateUtilities.PLEASE_SELECT, TemplateUtilities.PLEASE_SELECT_OR_TYPE);
        //remove select_other option that is automatically added
        databases.remove(TemplateUtilities.SELECT_OTHER);
        cDbName.setModel(new DefaultComboBoxModel(databases.values().toArray()));
    }

    private void cancelButtonActionPerformed() {
        setVisible(false);
        dispose();
    }

    private void okButtonActionPerformed() {
        DatabaseMapping db = new DatabaseMapping();
        db.setSearchEngineDatabaseName(seDbName.getText());
        db.setSearchEngineDatabaseVersion(seDbVersion.getText());
        if (cDbName.getSelectedIndex() > -1 && !cDbName.getSelectedItem().equals(TemplateUtilities.PLEASE_SELECT_OR_TYPE)) {
            db.setCuratedDatabaseName(cDbName.getSelectedItem().toString());
        } else if (cDbName.getEditor().getItem() != null) {
            db.setCuratedDatabaseName(cDbName.getEditor().getItem().toString());
        }
        //sanity check
        if (TemplateUtilities.PLEASE_SELECT_OR_TYPE.equals(db.getCuratedDatabaseName())) {
            db.setCuratedDatabaseName(null);
        }
        db.setCuratedDatabaseVersion(cDbVersion.getText());
        callback.update(db);
        setVisible(false);
        dispose();
    }

    @Override
    public void edit(ReportObject object) {
        DatabaseMapping db = (DatabaseMapping) object;
        seDbName.setText(db.getSearchEngineDatabaseName());
        seDbVersion.setText(db.getSearchEngineDatabaseVersion());
        cDbName.setSelectedItem(db.getCuratedDatabaseName());
        cDbVersion.setText(db.getCuratedDatabaseVersion());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("messages");
        dialogPane = new JPanel();
        label5 = new JLabel();
        label6 = new JLabel();
        label7 = new JLabel();
        label8 = new JLabel();
        seDbName = new JTextField();
        seDbVersion = new JTextField();
        cDbName = new JComboBox();
        cDbVersion = new JTextField();
        cancelButton = new JButton();
        okButton = new JButton();

        //======== this ========
        setTitle(bundle.getString("NewDatabaseMappingDialog.this.title"));
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

            //---- label5 ----
            label5.setText(bundle.getString("NewDatabaseMappingDialog.label5.text"));
            label5.setToolTipText(bundle.getString("NewDatabaseMappingDialog.label5.toolTipText"));

            //---- label6 ----
            label6.setText(bundle.getString("NewDatabaseMappingDialog.label6.text"));
            label6.setToolTipText(bundle.getString("NewDatabaseMappingDialog.label6.toolTipText"));

            //---- label7 ----
            label7.setText(bundle.getString("NewDatabaseMappingDialog.label7.text"));
            label7.setToolTipText(bundle.getString("NewDatabaseMappingDialog.label7.toolTipText"));

            //---- label8 ----
            label8.setText(bundle.getString("NewDatabaseMappingDialog.label8.text"));
            label8.setToolTipText("Please provide a the version of the search database you\nused in your experiment (for example release-2012_02 for\nUniprotKB) or the date you generated or downloaded the\nsearch database.");

            //---- seDbName ----
            seDbName.setEditable(false);
            seDbName.setToolTipText(bundle.getString("NewDatabaseMappingDialog.seDbName.toolTipText"));

            //---- seDbVersion ----
            seDbVersion.setEditable(false);
            seDbVersion.setToolTipText(bundle.getString("NewDatabaseMappingDialog.seDbVersion.toolTipText"));

            //---- cDbName ----
            cDbName.setEditable(true);
            cDbName.setToolTipText("<html>Please select a database name from the list supplied<br>or enter a clean, concise description of the search<br>database you used in your experiment</html>");

            //---- cDbVersion ----
            cDbVersion.setToolTipText("<html>Please provide a the version of the search database you<br>used in your experiment (for example release-2012_02 for<br>UniprotKB) or the date you generated or downloaded the<br>search database.</html");

            //---- cancelButton ----
            cancelButton.setText(bundle.getString("NewDatabaseMappingDialog.cancelButton.text"));
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cancelButtonActionPerformed();
                }
            });

            //---- okButton ----
            okButton.setText(bundle.getString("NewDatabaseMappingDialog.okButton.text"));
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    okButtonActionPerformed();
                }
            });

            GroupLayout dialogPaneLayout = new GroupLayout(dialogPane);
            dialogPane.setLayout(dialogPaneLayout);
            dialogPaneLayout.setHorizontalGroup(
                    dialogPaneLayout.createParallelGroup()
                            .addGroup(dialogPaneLayout.createSequentialGroup()
                                    .addGroup(dialogPaneLayout.createParallelGroup()
                                            .addGroup(dialogPaneLayout.createSequentialGroup()
                                                    .addGroup(dialogPaneLayout.createParallelGroup()
                                                            .addComponent(label6)
                                                            .addComponent(label7)
                                                            .addComponent(label8)
                                                            .addComponent(label5))
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(dialogPaneLayout.createParallelGroup()
                                                            .addComponent(seDbName, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                                                            .addComponent(cDbName, 0, 255, Short.MAX_VALUE)
                                                            .addComponent(seDbVersion, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                                                            .addComponent(cDbVersion, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)))
                                            .addGroup(GroupLayout.Alignment.TRAILING, dialogPaneLayout.createSequentialGroup()
                                                    .addContainerGap(352, Short.MAX_VALUE)
                                                    .addComponent(okButton)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(cancelButton)))
                                    .addContainerGap())
            );
            dialogPaneLayout.setVerticalGroup(
                    dialogPaneLayout.createParallelGroup()
                            .addGroup(dialogPaneLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(dialogPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(label5)
                                            .addComponent(seDbName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(dialogPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(label6)
                                            .addComponent(seDbVersion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(dialogPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(label7)
                                            .addComponent(cDbName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(dialogPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(label8)
                                            .addComponent(cDbVersion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                                    .addGroup(dialogPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(cancelButton)
                                            .addComponent(okButton)))
            );
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel dialogPane;
    private JLabel label5;
    private JLabel label6;
    private JLabel label7;
    private JLabel label8;
    private JTextField seDbName;
    private JTextField seDbVersion;
    private JComboBox cDbName;
    private JTextField cDbVersion;
    private JButton cancelButton;
    private JButton okButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setVisible(true);
        DatabaseMappingDialog d = new DatabaseMappingDialog(f, null);
        d.setVisible(true);

    }
}
