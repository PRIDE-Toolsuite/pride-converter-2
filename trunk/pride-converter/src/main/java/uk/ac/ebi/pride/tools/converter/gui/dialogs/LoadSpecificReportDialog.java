/*
 * Created by JFormDesigner on Wed Jan 11 14:19:55 GMT 2012
 */

package uk.ac.ebi.pride.tools.converter.gui.dialogs;

import uk.ac.ebi.pride.tools.converter.gui.forms.SampleForm;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.gui.model.ReportBean;
import uk.ac.ebi.pride.tools.converter.gui.util.IOUtilities;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.TreeSet;

/**
 * @author User #3
 */
public class LoadSpecificReportDialog extends JDialog {

    private SampleForm callback;

    public LoadSpecificReportDialog(Frame owner, SampleForm parent) {
        super(owner);
        initComponents();
        this.callback = parent;
        updateList();
    }

    public LoadSpecificReportDialog(Dialog owner, SampleForm parent) {
        super(owner);
        initComponents();
        this.callback = parent;
        updateList();
    }

    private void updateList() {
        TreeSet<String> files = new TreeSet<String>();
        files.addAll(ConverterData.getInstance().getInputFiles());
        sourceFileList.setListData(files.toArray());
        sourceFileList.setCellRenderer(new ReportDialogListCellRenderer());
    }

    private void sourceFileListMouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            okButtonActionPerformed();
        }
    }

    private void okButtonActionPerformed() {
        if (sourceFileList.getSelectedIndex() > -1) {
            for (Object fileName : sourceFileList.getSelectedValues()) {
                ReportBean rb = ConverterData.getInstance().getCustomeReportFields().get(fileName.toString());
                callback.addPaneForSample(fileName.toString(), rb);
            }
            setVisible(false);
        }
    }

    private void cancelButtonActionPerformed() {
        setVisible(false);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        scrollPane1 = new JScrollPane();
        sourceFileList = new JList();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setTitle("Select source file");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {

                //======== scrollPane1 ========
                {

                    //---- sourceFileList ----
                    sourceFileList.setToolTipText("Select one or more files to add custom annotations to.");
                    sourceFileList.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            sourceFileListMouseClicked(e);
                        }
                    });
                    scrollPane1.setViewportView(sourceFileList);
                }

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                        contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                                        .addContainerGap())
                );
                contentPanelLayout.setVerticalGroup(
                        contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                                        .addContainerGap())
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
                okButton.setText("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed();
                    }
                });
                buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setText("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed();
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
    private JScrollPane scrollPane1;
    private JList sourceFileList;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    private class ReportDialogListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            ReportBean rb = ConverterData.getInstance().getCustomeReportFields().get(value);
            //if there is already a report bean for this list item
            if (rb != null && rb.getSampleDescription() != null) {
                label.setFont(label.getFont().deriveFont(Font.BOLD));
            }
            label.setText(IOUtilities.getShortSourceFilePath(value.toString()));
            return label;
        }
    }
}
