/*
 * Created by JFormDesigner on Mon Nov 07 14:01:49 GMT 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.dialogs;

import uk.ac.ebi.pride.tools.converter.gui.interfaces.ConverterForm;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author User #3
 */
public class LoadTemplateDialog extends JDialog {

    private ConverterForm callbackForm;
    private AbstractDialog callbackDialog;

    public LoadTemplateDialog(ConverterForm callbackForm, Frame owner, String[] templates) {
        super(owner);
        initComponents();
        list1.setListData(templates);
        this.callbackForm = callbackForm;
    }

    public LoadTemplateDialog(ConverterForm callbackForm, Dialog owner, String[] templates) {
        super(owner);
        initComponents();
        list1.setListData(templates);
        this.callbackForm = callbackForm;
    }

    public LoadTemplateDialog(AbstractDialog callbackDialog, Frame owner, String[] templates) {
        super(owner);
        initComponents();
        list1.setListData(templates);
        this.callbackDialog = callbackDialog;
    }

    public LoadTemplateDialog(AbstractDialog callbackDialog, Dialog owner, String[] templates) {
        super(owner);
        initComponents();
        list1.setListData(templates);
        this.callbackDialog = callbackDialog;
    }

    private void okButtonActionPerformed() {
        if (list1.getSelectedIndex() > -1) {
            if (callbackForm != null) {
                callbackForm.loadTemplate(list1.getSelectedValue().toString());
            } else if (callbackDialog != null) {
                callbackDialog.loadTemplate(list1.getSelectedValue().toString());
            } else {
                throw new IllegalStateException("No Callback Object defined!");
            }
            setVisible(false);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a template to load.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelButtonActionPerformed() {
        setVisible(false);
        dispose();
    }

    private void list1ValueChanged() {
        if (list1.getSelectedValue() != null) {
            okButton.setEnabled(true);
        }
    }

    private void list1MouseClicked(MouseEvent e) {
        //if we double click on a valid list selection, perform the OK action
        if (e.getClickCount() == 2 && list1.getSelectedIndex() > -1) {
            okButtonActionPerformed();
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        scrollPane1 = new JScrollPane();
        list1 = new JList();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setTitle("Load Template");
        setResizable(false);
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

                    //---- list1 ----
                    list1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    list1.addListSelectionListener(new ListSelectionListener() {
                        @Override
                        public void valueChanged(ListSelectionEvent e) {
                            list1ValueChanged();
                        }
                    });
                    list1.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            list1MouseClicked(e);
                        }
                    });
                    scrollPane1.setViewportView(list1);
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
                okButton.setEnabled(false);
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
    private JList list1;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
