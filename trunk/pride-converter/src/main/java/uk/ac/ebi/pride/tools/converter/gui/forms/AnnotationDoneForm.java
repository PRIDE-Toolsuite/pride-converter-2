/*
 * Created by JFormDesigner on Wed Nov 02 15:57:47 GMT 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.error.ErrorLevel;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.gui.NavigationPanel;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.gui.model.GUIException;
import uk.ac.ebi.pride.tools.converter.gui.util.IOUtilities;
import uk.ac.ebi.pride.tools.converter.gui.util.error.ErrorDialogHandler;
import uk.ac.ebi.pride.tools.converter.report.io.ReportMetadataCopier;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
import uk.ac.ebi.pride.tools.converter.report.io.ReportWriter;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;

import javax.swing.*;
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
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        scrollPane1 = new JScrollPane();
        textArea1 = new JTextArea();

        //======== this ========

        //======== scrollPane1 ========
        {

            //---- textArea1 ----
            textArea1.setBackground(null);
            textArea1.setText("All data required to generate a valid report file has been captured. Please click on the \"Next\" button to generate the report file or click on the \"Back\" button if you wish to review it.");
            textArea1.setWrapStyleWord(true);
            textArea1.setLineWrap(true);
            scrollPane1.setViewportView(textArea1);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JScrollPane scrollPane1;
    private JTextArea textArea1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    @Override
    public Collection<ValidatorMessage> validateForm() {
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
        return "Review";
    }

    @Override
    public String getFormDescription() {
        return config.getString("annotationdone.form.description");
    }

    @Override
    public String getHelpResource() {
        return "help.ui.review";
    }

    @Override
    public void start() {
        //nothing to validate
        validationListerner.fireValidationListener(true);
    }

    @Override
    public void finish() throws GUIException {
        try {

            //write report file currently being edited
            ReportWriter writer = new ReportWriter(ConverterData.getInstance().getMasterReportFileName());
            writer.setDAO(ConverterData.getInstance().getMasterDAO());
            NavigationPanel.getInstance().setWorkingMessage("Writing report file: " + ConverterData.getInstance().getMasterReportFileName());
            String outfile = writer.writeReport();

            if (!outfile.equals(ConverterData.getInstance().getMasterReportFileName())) {
                //we need to overwrite the old master report file
                if (!IOUtilities.renameFile(outfile, ConverterData.getInstance().getMasterReportFileName())) {
                    throw new ConverterException("Could not overwrite master report file");
                }
            }

            //copy metadata over to other report files, if any
            //only need to copy metadata if we have several input files at once
            if (!"".equals(ConverterData.getInstance().getMasterReportFileName()) && ConverterData.getInstance().getInputFiles().size() > 1) {
                String masterReportFileName = ConverterData.getInstance().getMasterReportFileName();
                List<String> destFileNames = new ArrayList<String>();
                for (String reportFile : ConverterData.getInstance().getInputFiles().values()) {
                    if (!ConverterData.getInstance().getMasterReportFileName().equals(reportFile)) {
                        destFileNames.add(reportFile);
                    }
                }
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
