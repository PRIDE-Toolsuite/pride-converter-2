/*
 * Created by JFormDesigner on Thu Apr 05 14:05:44 BST 2012
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.gui.component.list.ShortFilePathListCellRenderer;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.gui.model.GUIException;
import uk.ac.ebi.pride.tools.converter.gui.util.IOUtilities;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;

/**
 * @author User #3
 */
public class MergerSelectMasterFileForm extends AbstractForm {

    public MergerSelectMasterFileForm() {
        initComponents();
        fileBox.setRenderer(new ShortFilePathListCellRenderer());
    }

    private void fileBoxItemStateChanged() {
        validationListerner.fireValidationListener(true);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        label1 = new JLabel();
        fileBox = new JComboBox();

        //======== this ========

        //---- label1 ----
        label1.setText("Please select the master file:");

        //---- fileBox ----
        fileBox.setToolTipText("Please select the master file");
        fileBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                fileBoxItemStateChanged();
            }
        });

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(fileBox, 0, 388, Short.MAX_VALUE)
                                        .addComponent(label1))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(label1)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fileBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(249, Short.MAX_VALUE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JComboBox fileBox;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    @Override
    public Collection<ValidatorMessage> validateForm() throws ValidatorException {
        return Collections.emptyList();
    }

    @Override
    public void clear() {
        fileBox.setSelectedItem(null);
    }

    @Override
    public void save(ReportReaderDAO dao) {
        // no op
    }

    @Override
    public void load(ReportReaderDAO dao) throws GUIException {
        // no op
    }

    @Override
    public String getFormName() {
        return "Master File Selection";
    }

    @Override
    public String getFormDescription() {
        return config.getString("masterfileselection.form.description");
    }

    @Override
    public Icon getFormIcon() {
        return getFormIcon("masterfileselection.form.icon");
    }

    @Override
    public String getHelpResource() {
        return "help.ui.merger.masterfile";
    }

    @Override
    public void start() {
        //for navigation
        validationListerner.fireValidationListener(fileBox.getSelectedIndex() > -1);
        //update model
        if (!isLoaded) {
            Set<String> inputFiles = ConverterData.getInstance().getInputFiles();
            fileBox.setModel(new DefaultComboBoxModel(inputFiles.toArray()));
            isLoaded = true;
        }
    }

    @Override
    public void finish() throws GUIException {

        List<String> inputFiles = new ArrayList<String>();
        //put master file at head - first file to merge will always be master file!
        String masterFile = fileBox.getModel().getSelectedItem().toString();
        inputFiles.add(masterFile);
        //then add all other files
        for (String file : ConverterData.getInstance().getInputFiles()) {
            if (!file.equals(masterFile)) {
                inputFiles.add(file);
            }
        }

        IOUtilities.mergePrideXMLFiles(ConverterData.getInstance().getOptions(), inputFiles);
    }
}
