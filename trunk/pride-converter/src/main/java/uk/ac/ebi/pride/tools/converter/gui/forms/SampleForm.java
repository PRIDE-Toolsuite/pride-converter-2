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
import uk.ac.ebi.pride.tools.converter.gui.model.ProtectedCvParam;
import uk.ac.ebi.pride.tools.converter.gui.model.ProtectedUserParam;
import uk.ac.ebi.pride.tools.converter.gui.model.ReportBean;
import uk.ac.ebi.pride.tools.converter.gui.util.IOUtilities;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
import uk.ac.ebi.pride.tools.converter.report.model.*;
import uk.ac.ebi.pride.tools.converter.report.validator.ReportObjectValidator;
import uk.ac.ebi.pride.tools.converter.utils.xml.validation.ValidatorFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * @author Melih Birim
 * @author rcote
 */
public class SampleForm extends AbstractForm implements ActionListener, TableModelListener {

    private static final String paramSeparator = "@%%@";

    public SampleForm() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("messages");
        tabbedPane1 = new JTabbedPane();
        masterSamplePanel = new SamplePanel();
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
            tabbedPane1.addTab(bundle.getString("SampleForm.masterSamplePanel.tab.title"), masterSamplePanel);

        }

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
                                        .addComponent(tabbedPane1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 776, Short.MAX_VALUE)
                                        .addComponent(customSampleButton, GroupLayout.Alignment.TRAILING))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(customSampleButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tabbedPane1, GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JTabbedPane tabbedPane1;
    private SamplePanel masterSamplePanel;
    private JButton customSampleButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    private void customSampleButtonActionPerformed() {
        LoadSpecificReportDialog dialog = new LoadSpecificReportDialog(NavigationPanel.getInstance(), this);
        dialog.setVisible(true);
    }

    public void addPaneForSample(String sourceFile, ReportBean rb) {

        String displayLabel = IOUtilities.getShortSourceFilePath(sourceFile);

        //check to see if sourcefile is already loaded
        boolean matchFound = false;
        //start at 1 as the first tab will always be the master file
        for (int i = 1; i < tabbedPane1.getTabCount(); i++) {
            if (tabbedPane1.getTitleAt(i).equals(IOUtilities.getShortSourceFilePath(sourceFile))) {
                //sanity check!
                SamplePanel samplePane = (SamplePanel) tabbedPane1.getComponentAt(i);
                if (samplePane.getSourceFile().equals(sourceFile)) {
                    matchFound = true;
                    tabbedPane1.setSelectedIndex(i);
                    break;
                }
            }
        }

        if (!matchFound) {
            SamplePanel samplePanel = new SamplePanel();
            //for later tracking
            samplePanel.setSourceFile(sourceFile);

            //if there's already a bean with custom information, use that
            if (rb != null && rb.getSampleDescription() != null) {
                samplePanel.setSampleName(rb.getSampleName());
                samplePanel.setSampleComment(rb.getSampleDescription().getComment());
                Param p = new Param();
                p.getCvParam().addAll(rb.getSampleDescription().getCvParam());
                p.getUserParam().addAll(rb.getSampleDescription().getUserParam());
                samplePanel.setSampleParams(p);

            } else {
                //otherwise use the dao information
                DAO dao = ConverterData.getInstance().getMasterDAO();
                samplePanel.setSampleName(dao.getSampleName());
                samplePanel.setSampleComment(dao.getSampleComment());
                //add params from master dao but make sure they are protected
                Param p = new Param();
                for (CvParam cv : masterSamplePanel.getSampleDescription().getCvParam()) {
                    p.getCvParam().add(new ProtectedCvParam(cv));
                }
                for (UserParam up : masterSamplePanel.getSampleDescription().getUserParam()) {
                    p.getUserParam().add(new ProtectedUserParam(up));
                }
                samplePanel.setSampleParams(p);
            }

            samplePanel.addValidationListener(validationListerner);
            tabbedPane1.add(displayLabel, samplePanel);

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
                ReportBean rb = ConverterData.getInstance().getCustomeReportFields().get(panel.getSourceFile());
                if (rb != null) {
                    rb.setSampleName(panel.getSampleName());
                    rb.setSampleDescription(sample);
                } else {
                    throw new IllegalStateException("Could not find report bean for file: " + panel.getSourceFile());
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
            SamplePanel samplePanel = (SamplePanel) tabbedPane1.getComponentAt(i);
            validatedTabs.add(samplePanel.getSourceFile());
            messages.addAll(validator.validate(samplePanel.getSampleDescription()));
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
        masterSamplePanel.clear();
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

        dao.setSampleName(masterSamplePanel.getSampleName());
        dao.setSampleComment(masterSamplePanel.getSampleComment());
        dao.setSampleParams(masterSamplePanel.getSampleParams());

        //loop through all open tabs and close them and save them to ConverterData for later use
        //need to start at 1 because the first tab will not be closeable
        for (int i = 1; i < tabbedPane1.getTabCount(); i++) {

            SamplePanel panel = (SamplePanel) tabbedPane1.getComponentAt(i);
            Description sample = panel.getSampleDescription();
            String fileName = panel.getSourceFile();
            ReportBean rb = ConverterData.getInstance().getCustomeReportFields().get(fileName);
            if (rb != null) {
                rb.setSampleName(panel.getSampleName());
                rb.setSampleDescription(sample);
            } else {
                throw new IllegalStateException("Could not find report bean for file: " + fileName);
            }
        }
        //now close all tabs
        for (int i = 1; i < tabbedPane1.getTabCount(); i++) {
            tabbedPane1.removeTabAt(i);
        }

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

            if (ConverterData.getInstance().getDataFiles().size() > 1) {
                tabbedPane1.setTitleAt(0, "Master Record");
                masterSamplePanel.setMasterPanel(true, this);
            } else {
                tabbedPane1.setTitleAt(0, IOUtilities.getShortSourceFilePath(ConverterData.getInstance().getMasterFile().getInputFile()));
            }
            masterSamplePanel.setSampleName(dao.getSampleName());
            masterSamplePanel.setSampleComment(dao.getSampleComment());
            masterSamplePanel.setSampleParams(dao.getSampleParams(), true);
            isLoaded = true;
        }
        //fire validation listener on load
        masterSamplePanel.fireValidationListener();
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
    public Icon getFormIcon() {
        return getFormIcon("sample.form.icon");
    }

    @Override
    public String getHelpResource() {
        return "help.ui.sample";
    }

    @Override
    public void start() {
        //for back & forth navigation
        masterSamplePanel.addValidationListener(validationListerner);
        masterSamplePanel.fireValidationListener();
    }

    @Override
    public void finish() {
        /* no op */
    }

    //**************************************** TableModelListener interface

    private Set<String> paramModelEventTracker = new HashSet<String>();

    @Override
    public void tableChanged(TableModelEvent e) {

        //update all report beans for open tabs!

        //first, save all open tabs!
        for (int i = 1; i < tabbedPane1.getTabCount(); i++) {

            SamplePanel panel = (SamplePanel) tabbedPane1.getComponentAt(i);
            //store description in memory for now, it will be validated later
            Description sample = panel.getSampleDescription();
            ReportBean rb = ConverterData.getInstance().getCustomeReportFields().get(panel.getSourceFile());
            if (rb != null) {
                rb.setSampleName(panel.getSampleName());
                rb.setSampleDescription(sample);
            } else {
                throw new IllegalStateException("Could not find report bean for file: " + panel.getSourceFile());
            }

        }

        //second process table event
        switch (e.getType()) {

            case TableModelEvent.INSERT:

                for (int i = e.getFirstRow(); i <= e.getLastRow(); i++) {
                    ReportObject obj = masterSamplePanel.getParamAtIndex(i);

                    //store param in set so that we can later on track inserts/deletes
                    String valueToAdd;
                    if (obj instanceof CvParam) {
                        CvParam cv = (CvParam) obj;
                        valueToAdd = cv.getCvLabel() + paramSeparator + cv.getAccession();
                    } else {
                        UserParam up = (UserParam) obj;
                        valueToAdd = up.getName();
                    }
                    paramModelEventTracker.add(valueToAdd);

                    for (ReportBean rb : ConverterData.getInstance().getCustomeReportFields().values()) {
                        Description sample = rb.getSampleDescription();
                        if (sample == null) {
                            sample = new Description();
                        }
                        //make sure we're not adding params several times
                        Set<CvParam> allCvParams = new HashSet<CvParam>(sample.getCvParam());
                        Set<UserParam> allUserParams = new HashSet<UserParam>(sample.getUserParam());
                        if (obj instanceof CvParam) {
                            allCvParams.add((CvParam) obj);
                        } else {
                            allUserParams.add((UserParam) obj);
                        }
                        sample.getCvParam().clear();
                        sample.getCvParam().addAll(allCvParams);
                        sample.getUserParam().clear();
                        sample.getUserParam().addAll(allUserParams);
                    }
                }

                break;

            case TableModelEvent.UPDATE:

                for (int i = e.getFirstRow(); i <= e.getLastRow(); i++) {
                    ReportObject obj = masterSamplePanel.getParamAtIndex(i);

                    for (ReportBean rb : ConverterData.getInstance().getCustomeReportFields().values()) {
                        Description sample = rb.getSampleDescription();
                        if (sample == null) {
                            sample = new Description();
                        }
                        if (obj instanceof CvParam) {

                            CvParam updatedParam = (CvParam) obj;
                            //check to see if we have the identical param and, if so, update it
                            for (Iterator<CvParam> iterator = sample.getCvParam().iterator(); iterator.hasNext(); ) {
                                CvParam cv = iterator.next();
                                if (cv.getAccession().equals(updatedParam.getAccession())
                                        && cv.getCvLabel().equals(updatedParam.getCvLabel())) {
                                    cv.setName(updatedParam.getName());
                                    cv.setValue(updatedParam.getValue());
                                }
                            }

                        } else {
                            UserParam updatedParam = (UserParam) obj;
                            //check to see if we have the identical param and, if so, update it
                            for (Iterator<UserParam> iterator = sample.getUserParam().iterator(); iterator.hasNext(); ) {
                                UserParam user = iterator.next();
                                if (user.getName().equals(updatedParam.getName())) {
                                    user.setName(updatedParam.getValue());
                                }
                            }
                        }

                    }
                }

                break;

            case TableModelEvent.DELETE:

                //the table model event will tell you which rows have been deleted, but
                //by then you can't access the data back from the model because, duh, it's been deleted.
                //so we compare the content of the table with the map that we keep
                //to find which param has been deleted
                //and then propagate that deletion across all report beans
                ReportObject obj = findMissingParam();

                for (ReportBean rb : ConverterData.getInstance().getCustomeReportFields().values()) {
                    Description sample = rb.getSampleDescription();
                    if (sample == null) {
                        sample = new Description();
                    }
                    if (obj instanceof CvParam) {

                        CvParam deletedParam = (CvParam) obj;
                        //check to see if we have the identical param and, if so, remove it
                        for (Iterator<CvParam> iterator = sample.getCvParam().iterator(); iterator.hasNext(); ) {
                            CvParam cv = iterator.next();
                            if (cv.getAccession().equals(deletedParam.getAccession()) && cv.getCvLabel().equals(deletedParam.getCvLabel())) {
                                iterator.remove();
                            }
                        }

                    } else {
                        //check to see if we have the identical param and, if so, remove it
                        UserParam deletedParam = (UserParam) obj;
                        for (Iterator<UserParam> iterator = sample.getUserParam().iterator(); iterator.hasNext(); ) {
                            if (iterator.next().getName().equals(deletedParam.getName())) {
                                iterator.remove();
                            }
                        }
                    }

                }

                break;

        }

        //third, update all open tabs
        for (int i = 1; i < tabbedPane1.getTabCount(); i++) {

            SamplePanel panel = (SamplePanel) tabbedPane1.getComponentAt(i);
            //store description in memory for now, it will be validated later
            ReportBean rb = ConverterData.getInstance().getCustomeReportFields().get(panel.getSourceFile());
            if (rb != null) {
                panel.setSampleParams(rb.getSampleDescription());
            }

        }

    }

    private ReportObject findMissingParam() {

        //loop over all current cv params and build set
        Param currentParams = masterSamplePanel.getSampleParams();
        Set<String> currentParamSet = new HashSet<String>();
        for (CvParam cv : currentParams.getCvParam()) {
            currentParamSet.add(cv.getCvLabel() + paramSeparator + cv.getAccession());
        }
        for (UserParam up : currentParams.getUserParam()) {
            currentParamSet.add(up.getName());
        }
        //set set operations to calculate difference
        paramModelEventTracker.removeAll(currentParamSet);
        //delete operations should only remove one row at a time, so if there is more than
        //one element, complain vigorously.
        if (paramModelEventTracker.size() == 1) {
            //get deleted param
            String deletedParamStr = paramModelEventTracker.iterator().next();
            //update collection for later use
            paramModelEventTracker.clear();
            paramModelEventTracker.addAll(currentParamSet);
            //create valid return value
            int ndx = deletedParamStr.indexOf(paramSeparator);
            if (ndx > -1) {
                String cv = deletedParamStr.substring(0, ndx);
                String ac = deletedParamStr.substring(ndx + paramSeparator.length());
                return new CvParam(cv, ac, null, null);
            } else {
                return new UserParam(deletedParamStr, null);
            }
        } else {
            throw new IllegalStateException("Error determining deleted param!");
        }

    }

}
