/*
 * Created by JFormDesigner on Fri Mar 11 11:36:21 GMT 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.gui.NavigationPanel;
import uk.ac.ebi.pride.tools.converter.gui.component.AddTermButton;
import uk.ac.ebi.pride.tools.converter.gui.component.table.ParamTable;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.LoadTemplateDialog;
import uk.ac.ebi.pride.tools.converter.gui.util.template.TemplateType;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.Protocol;
import uk.ac.ebi.pride.tools.converter.report.validator.ReportObjectValidator;
import uk.ac.ebi.pride.tools.converter.utils.xml.validation.ValidatorFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * @author Melih Birim
 * @author rcote
 */
public class ProtocolForm extends AbstractForm {

    private Vector<String> protocolSteps = new Vector<String>();
    private Vector<ParamTable> protocolStepTables = new Vector<ParamTable>();

    private static final String STEP_PREFIX = "Step ";

    public ProtocolForm() {
        initComponents();
        //only enable buttons when there is list content
        addTermButton.setEnabled(false);
        removeStepButton.setEnabled(false);
        saveButton.setEnabled(false);
    }

    private void list1ValueChanged() {

        //only enable buttons when there is list content
        addTermButton.setEnabled(list1.getModel().getSize() > 0);
        removeStepButton.setEnabled(list1.getModel().getSize() > 0);

        if (list1.getSelectedIndex() > -1) {
            protocolTableScrollPane.setViewportView(protocolStepTables.get(list1.getSelectedIndex()));
            addTermButton.setOwner(protocolStepTables.get(list1.getSelectedIndex()));
            revalidate();
            repaint();
        }
    }

    private void addStepButtonActionPerformed() {
        protocolSteps.add(STEP_PREFIX);
        protocolStepTables.add(new ParamTable());
        list1.setListData(paginateProtocolSteps(protocolSteps));
        list1.setSelectedIndex(protocolSteps.size() - 1);
        protocolTableScrollPane.setViewportView(protocolStepTables.lastElement());
        addTermButton.setOwner(protocolStepTables.lastElement());
        revalidate();
        repaint();
    }

    private void removeStepButtonActionPerformed() {
        int ndx = list1.getSelectedIndex();
        if (ndx > -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove this protocol step?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                protocolSteps.remove(ndx);
                protocolStepTables.remove(ndx);
                list1.setListData(paginateProtocolSteps(protocolSteps));
            }
            list1.setSelectedIndex(0);
            protocolTableScrollPane.setViewportView(protocolStepTables.firstElement());
            addTermButton.setOwner(protocolStepTables.firstElement());
            revalidate();
            repaint();

        } else {
            JOptionPane.showMessageDialog(this, "Please select a protocol step to remove", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Vector<String> paginateProtocolSteps(Vector<String> protocolSteps) {
        Vector<String> retval = new Vector<String>();
        int ndx = 1;
        for (String s : protocolSteps) {
            retval.add(s + ndx++);
        }
        return retval;
    }

    private void protocolNameKeyTyped(KeyEvent e) {
        validateRequiredField(protocolName, e);
        validationListerner.fireValidationListener(isNonNullTextField(protocolName.getText() + e.getKeyChar()));
        saveButton.setEnabled(isNonNullTextField(protocolName.getText() + e.getKeyChar()));
    }

    private void protocolNameFocusLost(FocusEvent e) {
        validateRequiredField(protocolName, null);
        validationListerner.fireValidationListener(isNonNullTextField(protocolName.getText()));
        saveButton.setEnabled(isNonNullTextField(protocolName.getText()));
    }

    private void saveButtonActionPerformed() {
        saveTemplate(protocolName.getText(), TemplateType.PROTOCOL, makeProtocol());
    }

    private void loadButtonActionPerformed() {
        String[] templates = getTemplateNames(TemplateType.PROTOCOL);
        LoadTemplateDialog dialog = new LoadTemplateDialog(this, NavigationPanel.getInstance(), templates);
        dialog.setVisible(true);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("messages");
        scrollPane1 = new JScrollPane();
        list1 = new JList();
        protocolTableScrollPane = new JScrollPane();
        paramTable1 = new ParamTable();
        label4 = new JLabel();
        protocolName = new JTextField();
        label5 = new JLabel();
        loadButton = new JButton();
        saveButton = new JButton();
        label1 = new JLabel();
        addStepButton = new JButton();
        removeStepButton = new JButton();
        addTermButton = new AddTermButton();

        //======== this ========

        //======== scrollPane1 ========
        {

            //---- list1 ----
            list1.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    list1ValueChanged();
                }
            });
            scrollPane1.setViewportView(list1);
        }

        //======== protocolTableScrollPane ========
        {
            protocolTableScrollPane.setViewportView(paramTable1);
        }

        //---- label4 ----
        label4.setText(bundle.getString("ProtocolForm.label4.text"));

        //---- protocolName ----
        protocolName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                protocolNameKeyTyped(e);
            }
        });
        protocolName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                protocolNameFocusLost(e);
            }
        });

        //---- label5 ----
        label5.setText(bundle.getString("ProtocolForm.label5.text"));
        label5.setForeground(Color.red);

        //---- loadButton ----
        loadButton.setText(bundle.getString("ProtocolForm.loadButton.text"));
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadButtonActionPerformed();
            }
        });

        //---- saveButton ----
        saveButton.setText(bundle.getString("ProtocolForm.saveButton.text"));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveButtonActionPerformed();
            }
        });

        //---- label1 ----
        label1.setText(bundle.getString("ProtocolForm.label1.text"));

        //---- addStepButton ----
        addStepButton.setText(bundle.getString("ProtocolForm.addStepButton.text"));
        addStepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStepButtonActionPerformed();
            }
        });

        //---- removeStepButton ----
        removeStepButton.setText(bundle.getString("ProtocolForm.removeStepButton.text"));
        removeStepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeStepButtonActionPerformed();
            }
        });

        //---- addTermButton ----
        addTermButton.setText(bundle.getString("ProtocolForm.addTermButton.text"));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(protocolTableScrollPane, GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(label4)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(protocolName, GroupLayout.PREFERRED_SIZE, 187, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(label5, GroupLayout.PREFERRED_SIZE, 12, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(loadButton)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(saveButton))
                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(label1)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 221, Short.MAX_VALUE)
                                                .addComponent(addStepButton)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(removeStepButton)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(addTermButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(loadButton)
                                        .addComponent(saveButton)
                                        .addComponent(label4)
                                        .addComponent(protocolName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label5))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(addStepButton)
                                        .addComponent(removeStepButton)
                                        .addComponent(addTermButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label1))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                                        .addComponent(protocolTableScrollPane, GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE))
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JScrollPane scrollPane1;
    private JList list1;
    private JScrollPane protocolTableScrollPane;
    private ParamTable paramTable1;
    private JLabel label4;
    private JTextField protocolName;
    private JLabel label5;
    private JButton loadButton;
    private JButton saveButton;
    private JLabel label1;
    private JButton addStepButton;
    private JButton removeStepButton;
    private AddTermButton addTermButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    @Override
    public Collection<ValidatorMessage> validateForm() throws ValidatorException {
        ReportObjectValidator validator = ValidatorFactory.getInstance().getReportValidator();
        return validator.validate(makeProtocol());
    }

    @Override
    public void clear() {
        isLoaded = false;
        protocolName.setText(null);
        protocolSteps.removeAllElements();
        protocolStepTables.removeAllElements();
        list1.setListData(protocolSteps);
        //set default view
        protocolTableScrollPane.setViewportView(paramTable1);
        revalidate();
        repaint();
        //inactivate next button
        validationListerner.fireValidationListener(false);
        //inactivate save button
        saveButton.setEnabled(false);
    }

    @Override
    public void save(ReportReaderDAO dao) {
        dao.setProtocol(makeProtocol());
    }

    private Protocol makeProtocol() {
        Protocol p = new Protocol();
        p.setProtocolName(protocolName.getText());
        for (ParamTable pt : protocolStepTables) {
            Param param = new Param();
            param.getCvParam().addAll(pt.getCvParamList());
            p.getProtocolSteps().getStepDescription().add(param);
        }
        return p;
    }

    @Override
    public void load(ReportReaderDAO dao) {
        if (!isLoaded) {
            loadProtocol(dao.getProtocol());
        }
        //fire validation listener on load
        validationListerner.fireValidationListener(isNonNullTextField(protocolName.getText()));
        //update save button on load
        saveButton.setEnabled(isNonNullTextField(protocolName.getText()));
    }

    private void loadProtocol(Protocol protocol) {
        protocolName.setText(protocol.getProtocolName());
        for (Param param : protocol.getProtocolSteps().getStepDescription()) {
            protocolSteps.add(STEP_PREFIX);
            ParamTable paramTable = new ParamTable();
            paramTable.add(param);
            protocolStepTables.add(paramTable);
        }
        if (!protocolSteps.isEmpty()) {
            list1.setListData(paginateProtocolSteps(protocolSteps));
            list1.setSelectedIndex(0);
            protocolTableScrollPane.setViewportView(protocolStepTables.firstElement());
            addTermButton.setOwner(protocolStepTables.firstElement());
            revalidate();
            repaint();
        }
        //to avoid multiple reloads
        isLoaded = true;
    }

    @Override
    public String getFormName() {
        return "Protocol Description";
    }

    @Override
    public String getFormDescription() {
        return config.getString("protocol.form.description");
    }

    @Override
    public String getHelpResource() {
        return "help.ui.protocol";
    }

    @Override
    public void start() {
        //for back&forth navigation
        validationListerner.fireValidationListener(isNonNullTextField(protocolName.getText()));
    }

    @Override
    public void finish() {
        /* no op */
    }

    @Override
    public void loadTemplate(String templateName) {

        Protocol protocol = (Protocol) loadTemplate(templateName, TemplateType.PROTOCOL);
        clear();
        loadProtocol(protocol);
        //update save button on load
        saveButton.setEnabled(isNonNullTextField(protocolName.getText()));
        //update field if required
        validateRequiredField(protocolName, null);
        //fire validation listener on load
        validationListerner.fireValidationListener(isNonNullTextField(protocolName.getText()));
    }
}
