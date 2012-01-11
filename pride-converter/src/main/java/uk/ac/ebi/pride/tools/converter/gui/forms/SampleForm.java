/*
 * Created by JFormDesigner on Fri Mar 11 11:02:12 GMT 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.dao.DAO;
import uk.ac.ebi.pride.tools.converter.gui.NavigationPanel;
import uk.ac.ebi.pride.tools.converter.gui.component.ButtonTabComponent;
import uk.ac.ebi.pride.tools.converter.gui.component.panels.SamplePanel;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.LoadSpecificReportDialog;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.gui.model.ReportBean;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
import uk.ac.ebi.pride.tools.converter.report.model.Description;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.validator.ReportObjectValidator;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;
import uk.ac.ebi.pride.tools.converter.utils.xml.validation.ValidatorFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * @author Melih Birim
 * @author rcote
 */
public class SampleForm extends AbstractForm implements ActionListener {

    public SampleForm() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("messages");
        tabbedPane1 = new JTabbedPane();
        samplePanel1 = new SamplePanel();
        label1 = new JLabel();
        customSampleButton = new JButton();

        //======== this ========

        //======== tabbedPane1 ========
        {
            tabbedPane1.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    tabbedPane1StateChanged();
                }
            });
            tabbedPane1.addTab(bundle.getString("SampleForm.samplePanel1.tab.title"), samplePanel1);

        }

        //---- label1 ----
        label1.setText(bundle.getString("SampleForm.label1.text"));

        //---- customSampleButton ----
        customSampleButton.setText(bundle.getString("SampleForm.customSampleButton.text"));
        customSampleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                customSampleButtonActionPerformed();
            }
        });

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(tabbedPane1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(label1)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 471, Short.MAX_VALUE)
                                                .addComponent(customSampleButton)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(label1)
                                        .addComponent(customSampleButton))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tabbedPane1, GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JTabbedPane tabbedPane1;
    private SamplePanel samplePanel1;
    private JLabel label1;
    private JButton customSampleButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    private void customSampleButtonActionPerformed() {
        LoadSpecificReportDialog dialog = new LoadSpecificReportDialog(NavigationPanel.getInstance(), this);
        dialog.setVisible(true);
    }

    public void addPaneForSample(String sourceFile, ReportBean rb) {

        //check to see if sourcefile is already loaded
        boolean matchFound = false;
        for (int i = 0; i < tabbedPane1.getTabCount(); i++) {
            if (tabbedPane1.getTitleAt(i).equals(sourceFile)) {
                matchFound = true;
                tabbedPane1.setSelectedIndex(i);
                break;
            }
        }

        if (!matchFound) {
            SamplePanel samplePanel = new SamplePanel();
            try {
                if (rb != null && rb.getSampleDescription() != null) {
                    samplePanel1.setSampleName(rb.getSampleName());
                    samplePanel1.setSampleComment(rb.getSampleDescription().getComment());
                    Param p = new Param();
                    p.getCvParam().addAll(rb.getSampleDescription().getCvParam());
                    p.getUserParam().addAll(rb.getSampleDescription().getUserParam());
                    samplePanel1.setSampleParams(p);

                } else {
                    DAO dao = ConverterData.getInstance().getMasterDAO();
                    samplePanel1.setSampleName(dao.getSampleName());
                    samplePanel1.setSampleComment(dao.getSampleComment());
                    samplePanel1.setSampleParams(dao.getSampleParams());
                }
            } catch (InvalidFormatException e) {
                logger.warn("Error reading dao information: " + e.getMessage(), e);
            }

            samplePanel.addValidationListener(validationListerner);
            tabbedPane1.add(sourceFile, samplePanel);

            ButtonTabComponent button = new ButtonTabComponent(tabbedPane1);
            button.addActionListener(this);
            tabbedPane1.setTabComponentAt(tabbedPane1.getTabCount() - 1, button);
            tabbedPane1.setSelectedIndex(tabbedPane1.getTabCount() - 1);
        }
    }

    private void tabbedPane1StateChanged() {
        if (validationListerner != null) {
            boolean allValid = true;
            for (int i = 0; i < tabbedPane1.getTabCount(); i++) {
                SamplePanel panel = (SamplePanel) tabbedPane1.getComponentAt(i);
                allValid = allValid && panel.isSampleValid();
            }
            validationListerner.fireValidationListener(allValid);
        }
    }

    /**
     * Invoked when an action occurs.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        //we need to do this like so because the selected tab might not be the one that is clicked to be closed!
        //need to start at 1 because the first tab will not be closeable
        for (int i = 1; i < tabbedPane1.getTabCount(); i++) {
            if (((ButtonTabComponent) tabbedPane1.getTabComponentAt(i)).getButton() == e.getSource()) {

                SamplePanel panel = (SamplePanel) tabbedPane1.getComponentAt(i);
                //store description in memory for now, it will be validated later
                Description sample = panel.getSampleDescription();
                String fileName = tabbedPane1.getTitleAt(i);
                ReportBean rb = ConverterData.getInstance().getCustomeReportFields().get(fileName);
                if (rb != null) {
                    rb.setSampleName(panel.getSampleName());
                    rb.setSampleDescription(sample);
                } else {
                    throw new IllegalStateException("Could not find report bean for file: " + fileName);
                }

                //no sense looping further
                break;

            }
        }

    }

    @Override
    public Collection<ValidatorMessage> validateForm() throws ValidatorException {

        ReportObjectValidator validator = ValidatorFactory.getInstance().getReportValidator();
        Collection<ValidatorMessage> messages = new ArrayList<ValidatorMessage>();

        //validate all open tabs
        Set<String> validatedTabs = new HashSet<String>();
        for (int i = 0; i < tabbedPane1.getTabCount(); i++) {
            SamplePanel sample = (SamplePanel) tabbedPane1.getComponentAt(i);
            validatedTabs.add(tabbedPane1.getTitleAt(i));
            messages.addAll(validator.validate(sample));
        }

        //also check those that might be in memeory!
        for (String fileName : ConverterData.getInstance().getCustomeReportFields().keySet()) {
            ReportBean rb = ConverterData.getInstance().getCustomeReportFields().get(fileName);
            //don't validate sample descriptions that might already be opened as tabs
            if (rb.getSampleDescription() != null && !validatedTabs.contains(fileName)) {
                Collection<ValidatorMessage> msg = validator.validate(rb.getSampleDescription());
                //if errors, add tab back to view
                if (hasErrors(msg)) {
                    addPaneForSample(fileName, rb);
                }
                messages.addAll(msg);
            }

        }

        return messages;
    }

    private boolean hasErrors(Collection<ValidatorMessage> msg) {
        boolean hasErrors = false;
        for (ValidatorMessage m : msg) {
            if (m.getLevel().isHigher(MessageLevel.WARN)) {
                hasErrors = true;
                break;
            }
        }
        return hasErrors;
    }

    @Override
    public void clear() {

        isLoaded = false;
        samplePanel1.clear();
        if (tabbedPane1.getTabCount() > 1) {
            for (int i = 0; i < tabbedPane1.getTabCount(); i++) {
                if (!tabbedPane1.getTitleAt(i).equals("Master Sample")) {
                    tabbedPane1.removeTabAt(i);
                }
            }
        }
        //inactivate next button
        validationListerner.fireValidationListener(false);

    }

    @Override
    public void save(ReportReaderDAO dao) {

        dao.setSampleName(samplePanel1.getSampleName());
        dao.setSampleComment(samplePanel1.getSampleComment());
        dao.setSampleParams(samplePanel1.getSampleParams());

    }

    @Override
    public void load(ReportReaderDAO dao) {

        //if there are multiple files, show customize sample button
        if (ConverterData.getInstance().getInputFiles().size() > 1) {
            customSampleButton.setVisible(true);
        } else {
            customSampleButton.setVisible(false);
        }

        if (!isLoaded) {
            samplePanel1.setSampleName(dao.getSampleName());
            samplePanel1.setSampleComment(dao.getSampleComment());
            samplePanel1.setSampleParams(dao.getSampleParams());
            isLoaded = true;
        }
        //fire validation listener on load
        samplePanel1.fireValidationListener();
    }

    @Override
    public String getFormName() {
        return "Sample Description";
    }

    @Override
    public String getFormDescription() {
        return config.getString("sample.form.description");
    }

    @Override
    public String getHelpResource() {
        return "help.ui.sample";
    }

    @Override
    public void start() {
        //for back & forth navigation
        samplePanel1.addValidationListener(validationListerner);
        samplePanel1.fireValidationListener();
    }

    @Override
    public void finish() {
        /* no op */
    }

}
