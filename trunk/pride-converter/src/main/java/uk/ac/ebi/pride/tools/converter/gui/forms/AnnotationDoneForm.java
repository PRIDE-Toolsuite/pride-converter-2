/*
 * Created by JFormDesigner on Wed Nov 02 15:57:47 GMT 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.error.ErrorLevel;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.gui.NavigationPanel;
import uk.ac.ebi.pride.tools.converter.gui.component.AddTermButton;
import uk.ac.ebi.pride.tools.converter.gui.component.table.ParamTable;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.ParamTableModel;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.AbstractDialog;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.gui.model.GUIException;
import uk.ac.ebi.pride.tools.converter.gui.util.IOUtilities;
import uk.ac.ebi.pride.tools.converter.gui.util.error.ErrorDialogHandler;
import uk.ac.ebi.pride.tools.converter.report.io.ReportMetadataCopier;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
import uk.ac.ebi.pride.tools.converter.report.io.ReportWriter;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;
import uk.ac.ebi.pride.tools.converter.report.model.UserParam;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author User #3
 */
public class AnnotationDoneForm extends AbstractForm {

    private static final Logger logger = Logger.getLogger(AnnotationDoneForm.class);

    public AnnotationDoneForm() {
        initComponents();
        addTermButton1.setOwner(expAdditionalTable);
    }

    private void editButtonActionPerformed(ActionEvent e) {
        if (expAdditionalTable.getSelectedRowCount() > 0) {
            //convert table selected row to underlying model row
            int modelSelectedRow = expAdditionalTable.convertRowIndexToModel(expAdditionalTable.getSelectedRow());
            //get object
            ReportObject objToEdit = ((ParamTableModel) expAdditionalTable.getModel()).get(modelSelectedRow);
            Class clazz = objToEdit.getClass();
            //show editing dialog for object
            AbstractDialog dialog = AbstractDialog.getInstance(expAdditionalTable, clazz);
            dialog.edit(objToEdit, modelSelectedRow);
            dialog.setVisible(true);
        }

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        scrollPane2 = new JScrollPane();
        messageArea = new JTextArea();
        label1 = new JLabel();
        scrollPane3 = new JScrollPane();
        expAdditionalTable = new ParamTable();
        label7 = new JLabel();
        expAdditionalEditButton = new JButton();
        addTermButton1 = new AddTermButton();

        //======== this ========

        //======== scrollPane2 ========
        {

            //---- messageArea ----
            messageArea.setBackground(null);
            messageArea.setText("You have X warnings. Please review them before proceeding.");
            messageArea.setLineWrap(true);
            messageArea.setEditable(false);
            messageArea.setWrapStyleWord(true);
            scrollPane2.setViewportView(messageArea);
        }

        //---- label1 ----
        label1.setText("Messages:");

        //======== scrollPane3 ========
        {

            //---- expAdditionalTable ----
            expAdditionalTable.setToolTipText("Additional information about the experiment\nthat was not recorded in previous screens.\nExisting values should generally not be modified.");
            scrollPane3.setViewportView(expAdditionalTable);
        }

        //---- label7 ----
        label7.setText("Experiment Additional Information");
        label7.setToolTipText("Additional information about the experiment\nthat was not recorded in previous screens.\nExisting values should generally not be modified.");

        //---- expAdditionalEditButton ----
        expAdditionalEditButton.setIcon(new ImageIcon(getClass().getResource("/images/edit.png")));
        expAdditionalEditButton.setToolTipText("Edit");
        expAdditionalEditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editButtonActionPerformed(e);
            }
        });

        //---- addTermButton1 ----
        addTermButton1.setToolTipText("Add");

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(scrollPane2)
                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(label7)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 321, Short.MAX_VALUE)
                                                .addComponent(addTermButton1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(expAdditionalEditButton))
                                        .addComponent(scrollPane3, GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
                                        .addComponent(label1))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(addTermButton1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(expAdditionalEditButton)
                                        .addComponent(label7))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane3, GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(label1)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JScrollPane scrollPane2;
    private JTextArea messageArea;
    private JLabel label1;
    private JScrollPane scrollPane3;
    private ParamTable expAdditionalTable;
    private JLabel label7;
    private JButton expAdditionalEditButton;
    private AddTermButton addTermButton1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    @Override
    public Collection<ValidatorMessage> validateForm() {
        return Collections.emptyList();
    }

    @Override
    public void clear() {
        expAdditionalTable.removeAll();
        isLoaded = false;
    }

    @Override
    public void save(ReportReaderDAO dao) {
        //create new object
        Param p = new Param();
        //fill it with table values
        p.getCvParam().addAll(expAdditionalTable.getCvParamList());
        p.getUserParam().addAll(expAdditionalTable.getUserParamList());
        //get previously stored project and exp params and add those too
        Param daoParam = dao.getExperimentParams();
        if (daoParam != null) {
            for (CvParam cv : daoParam.getCvParam()) {
                if (cv.getAccession().equals(DAOCvParams.PRIDE_PROJECT.getAccession()) ||
                        cv.getAccession().equals(DAOCvParams.EXPERIMENT_DESCRIPTION.getAccession())) {
                    p.getCvParam().add(cv);
                }
            }
        }
        //store to dao
        dao.setExperimentParams(p);
    }

    @Override
    public void load(ReportReaderDAO dao) {
        if (!isLoaded) {

            Param p = dao.getExperimentParams();
            if (p != null) {
                for (CvParam cv : p.getCvParam()) {
                    if (cv.getAccession().equals(DAOCvParams.PRIDE_PROJECT.getAccession()) ||
                            cv.getAccession().equals(DAOCvParams.EXPERIMENT_DESCRIPTION.getAccession())) {
                        continue;
                    }
                    expAdditionalTable.add(cv);
                }
                for (UserParam u : p.getUserParam()) {
                    expAdditionalTable.add(u);
                }
            }

            isLoaded = true;
        }
    }

    @Override
    public String getFormName() {
        return "Review";
    }

    @Override
    public String getFormDescription() {
        return config.getString("annotationdone.form.description");
    }

    @Override
    public Icon getFormIcon() {
        return getFormIcon("annotationdone.form.icon");
    }

    @Override
    public String getHelpResource() {
        return "help.ui.review";
    }

    @Override
    public void start() {
        //nothing to validate
        validationListerner.fireValidationListener(true);

        //update messages
        StringBuilder messages = new StringBuilder();
        if (ConverterData.getInstance().getInputFiles().size() > 1) {
            //add multiple file message
            messages.append("You are converting ")
                    .append(ConverterData.getInstance().getInputFiles().size())
                    .append(" source files. Be aware that some information will be copied across")
                    .append(" all PRIDE XML files while some information will not be. Please refer")
                    .append(" to the PRIDE Converter Help to learn more about this.\n\n");
        }
        if (NavigationPanel.getInstance().getWarningMessageCount() > 0) {
            //add warning message
            messages.append("The information you have entered in the previous forms has generated ")
                    .append(NavigationPanel.getInstance().getWarningMessageCount())
                    .append(" warning messages.")
                    .append(" Please review them before generating the PRIDE XML files.\n\n");
        }
        if (NavigationPanel.getInstance().getInfoMessageCount() > 0) {
            //add info message
            messages.append("The information you have entered in the previous forms has generated ")
                    .append(NavigationPanel.getInstance().getInfoMessageCount())
                    .append(" information messages.")
                    .append(" Please review them before generating the PRIDE XML files.\n\n");
        }
        messageArea.setText(messages.toString());

    }

    @Override
    public void finish() throws GUIException {
        try {

            //write report file currently being edited
            ReportWriter writer = new ReportWriter(ConverterData.getInstance().getMasterFile().getReportFile());
            writer.setDAO(ConverterData.getInstance().getMasterDAO());
            //we don't want to lose the annotations we've already done!
            writer.setAutomaticallyMapPreferredPTMs(false);
            NavigationPanel.getInstance().setWorkingMessage("Writing report file: " + ConverterData.getInstance().getMasterFile().getReportFile());
            String outfile = writer.writeReport();

            if (!outfile.equals(ConverterData.getInstance().getMasterFile().getReportFile())) {
                //we need to overwrite the old master report file
                if (!IOUtilities.renameFile(outfile, ConverterData.getInstance().getMasterFile().getReportFile())) {
                    throw new ConverterException("Could not overwrite master report file");
                }
            }

            //at this point, the report file has been modified and the xxindex offsets in the report reader
            //are no longer valid, so we need to update the master dao otherwise we get XML errors in back/forth navigation
            ReportReaderDAO dao = new ReportReaderDAO(new File(ConverterData.getInstance().getMasterFile().getReportFile()));
            ConverterData.getInstance().setMasterDAO(dao);

            //copy metadata over to other report files, if any
            //only need to copy metadata if we have several input files at once
            if (ConverterData.getInstance().getDataFiles().size() > 1) {
                String masterReportFileName = ConverterData.getInstance().getMasterFile().getReportFile();
                List<String> destFileNames = new ArrayList<String>(ConverterData.getInstance().getReportFiles());
                NavigationPanel.getInstance().setWorkingMessage("Updating metadata from master report file");
                ReportMetadataCopier.copyMetadata(masterReportFileName, destFileNames);
            }

        } catch (ConverterException e) {
            logger.error("report IO error: " + e.getMessage(), e);
            ErrorDialogHandler.showErrorDialog(this, ErrorLevel.FATAL, "Report IO error", "Error while writing report file", "REPORT-METADATA", e);
        } catch (InvalidFormatException e) {
            logger.error("report IO error - invalid format: " + e.getMessage(), e);
            ErrorDialogHandler.showErrorDialog(this, ErrorLevel.FATAL, "Report IO error", "Invalid file format detected while writing report file", "REPORT-METADATA", e);
        }

    }

}
