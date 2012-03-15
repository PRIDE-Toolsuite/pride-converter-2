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
        scrollPane2 = new JScrollPane();
        messageArea = new JTextArea();
        label1 = new JLabel();

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

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(scrollPane2, GroupLayout.Alignment.LEADING)
                                        .addComponent(scrollPane1)
                                        .addComponent(label1, GroupLayout.Alignment.LEADING))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 68, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label1)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JScrollPane scrollPane1;
    private JTextArea textArea1;
    private JScrollPane scrollPane2;
    private JTextArea messageArea;
    private JLabel label1;
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

            //copy metadata over to other report files, if any
            //only need to copy metadata if we have several input files at once
            if (ConverterData.getInstance().getMasterFile() != null && ConverterData.getInstance().getDataFiles().size() > 1) {
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
