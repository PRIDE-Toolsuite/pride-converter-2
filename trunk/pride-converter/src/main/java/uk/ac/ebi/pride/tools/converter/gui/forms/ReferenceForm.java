/*
 * Created by JFormDesigner on Fri Mar 11 10:46:09 GMT 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import org.apache.log4j.Logger;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.gui.NavigationPanel;
import uk.ac.ebi.pride.tools.converter.gui.component.table.BaseTable;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.ReferencesTableModel;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.ReferenceDialog;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
import uk.ac.ebi.pride.tools.converter.report.model.Reference;
import uk.ac.ebi.pride.tools.converter.report.validator.ReportObjectValidator;
import uk.ac.ebi.pride.tools.converter.utils.xml.validation.ValidatorFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 * @author Melih Birim
 * @author rcote
 */
public class ReferenceForm extends AbstractForm {

    private static final Logger logger = Logger.getLogger(ReferenceForm.class);

    public ReferenceForm() {
        initComponents();

        referenceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ReferencesTableModel referenceTableModel = new ReferencesTableModel();
        referenceTable.setModel(referenceTableModel);
        referenceTable.setColumnModel(referenceTableModel.getTableColumnModel(referenceTable));

    }

    private void addReferenceAction(ActionEvent e) {
        ReferenceDialog referenceDialog = new ReferenceDialog(NavigationPanel.getInstance(), referenceTable);
        referenceDialog.setVisible(true);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("messages");
        label1 = new JLabel();
        addreferenceButton = new JButton();
        scrollPane1 = new JScrollPane();
        referenceTable = new BaseTable<Reference>();

        //======== this ========

        //---- label1 ----
        label1.setText(bundle.getString("ReferenceExperimentForm.label1.text"));

        //---- addreferenceButton ----
        addreferenceButton.setText(bundle.getString("ReferenceExperimentForm.addreferenceButton.text"));
        addreferenceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addReferenceAction(e);
            }
        });

        //======== scrollPane1 ========
        {

            //---- referenceTable ----
            referenceTable.setModel(new DefaultTableModel());
            scrollPane1.setViewportView(referenceTable);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(scrollPane1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(label1)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 161, Short.MAX_VALUE)
                                                .addComponent(addreferenceButton)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(addreferenceButton)
                                        .addComponent(label1))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JButton addreferenceButton;
    private JScrollPane scrollPane1;
    private BaseTable<Reference> referenceTable;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    @Override
    public Collection<ValidatorMessage> validateForm() throws ValidatorException {
        ReportObjectValidator validator = ValidatorFactory.getInstance().getReportValidator();
        return validator.validate(referenceTable.getAll());
    }

    @Override
    public void clear() {
        isLoaded = false;
        referenceTable.removeAll();
        //no need to inactivate next button
        //validationListerner.fireValidationListener(false);
    }

    @Override
    public void save(ReportReaderDAO dao) {
        dao.setReferences(referenceTable.getAll());
    }

    @Override
    public void load(ReportReaderDAO dao) {
        if (!isLoaded) {
            referenceTable.addAll(dao.getReferences());
            isLoaded = true;
        }
        //no need to fire validation listener
    }

    @Override
    public String getFormName() {
        return "References";
    }

    @Override
    public String getFormDescription() {
        return config.getString("reference.form.description");
    }

    @Override
    public Icon getFormIcon() {
        return getFormIcon("reference.form.icon");
    }

    @Override
    public String getHelpResource() {
        return "help.ui.references";
    }

    @Override
    public void start() {
        //references are optional
        validationListerner.fireValidationListener(true);
    }

    @Override
    public void finish() {
        /* no op */
    }
}
