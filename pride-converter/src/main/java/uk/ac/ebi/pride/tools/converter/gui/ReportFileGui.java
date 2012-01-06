///*
// * Created by JFormDesigner on Thu Mar 10 17:11:27 GMT 2011
// */
//
//package uk.ac.ebi.pride.tools.converter.gui;
//
//import org.apache.log4j.Logger;
//import org.jdesktop.swingx.error.ErrorLevel;
//import psidev.psi.tools.validator.ValidatorMessage;
//import uk.ac.ebi.pride.tools.converter.dao.DAO;
//import uk.ac.ebi.pride.tools.converter.gui.component.LoadingPanel;
//import uk.ac.ebi.pride.tools.converter.gui.component.NavigableTabbedPane;
//import uk.ac.ebi.pride.tools.converter.gui.component.NavigationPanel;
//import uk.ac.ebi.pride.tools.converter.gui.component.interfaces.IForm;
//import uk.ac.ebi.pride.tools.converter.gui.component.interfaces.Navigable;
//import uk.ac.ebi.pride.tools.converter.gui.dialogs.ErrorMessageDialog;
//import uk.ac.ebi.pride.tools.converter.gui.dialogs.PopupDialog;
//import uk.ac.ebi.pride.tools.converter.gui.dialogs.TabbedValidationDialog;
//import uk.ac.ebi.pride.tools.converter.gui.forms.report.*;
//import uk.ac.ebi.pride.tools.converter.gui.util.ETDUtilities;
//import uk.ac.ebi.pride.tools.converter.gui.util.Pair;
//import uk.ac.ebi.pride.tools.converter.gui.util.error.ErrorDialogHandler;
//import uk.ac.ebi.pride.tools.converter.gui.validator.ValidationManager;
//import uk.ac.ebi.pride.tools.converter.report.io.ReportMetadataCopier;
//import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
//import uk.ac.ebi.pride.tools.converter.report.io.ReportWriter;
//import uk.ac.ebi.pride.tools.converter.report.model.DatabaseMapping;
//import uk.ac.ebi.pride.tools.converter.report.model.PTM;
//import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
//import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;
//
//import javax.swing.*;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;
//import java.awt.*;
//import java.awt.event.*;
//import java.beans.PropertyChangeEvent;
//import java.beans.PropertyChangeListener;
//import java.io.File;
//import java.text.MessageFormat;
//import java.util.*;
//import java.util.List;
//import java.util.logging.Level;
//
///**
// * @author melih
// * @author rcote
// */
//public class ReportFileGui extends JPanel implements Navigable, PropertyChangeListener {
//
//    private static final Logger logger = Logger.getLogger(ReportFileGui.class);
//
//    // Instance of ReportFile
//    private static ReportFileGui instance;
//
//    //keep track of where we were
//    private int previousSelectedTabIndex = 0;
//    private JTabbedPane previousSelectedTabPane = null;
//
//    // Name of the tabbed pane
//    private static final String MAIN_TABBED_PANE_NAME = "mainTabbedPane";
//    private static final String INNER_TABBED_PANE_NAME = "innerTabbedPane";
//
//    private boolean internalTabPaneStateChange = false;
//    private boolean startupDone = false;
//
//    private ResourceBundle bundle;
//    private Integer fileCounter = 1;
//    private boolean edit = false;
//
//    private DAO reportReaderDAO = null;
//    private Set<PTM> PTMList;
//    private Set<DatabaseMapping> dbMappingList;
//
//    private JFrame parent = null;
//    private List<IForm> forms;
//    private ErrorMessageDialog tabErrorDialog = null;
//    private LoadingPanel progressPanel;
//    private Component m_prevGlassPane;
//
//    private File selectedFile;
//    private SwingWorker<Boolean, Object> reportWorker;
//    private PopupDialog popupDialog = null;
//
//    public ReportFileGui(JFrame parent) {
//
//        instance = this;
//        this.parent = parent;
//        bundle = ResourceBundle.getBundle("messages");
//
//        initComponents();
//
//        tabErrorDialog = new ErrorMessageDialog(this.parent);
//
//        scanFormValidator();
//
//        previousSelectedTabPane = mainTabbedPane;
//
//        addForms();
//
//        clearForms();
//
//        startupDone = true;
//    }
//
//    public void setPTMList(Set<PTM> PTMList) {
//        this.PTMList = PTMList;
//    }
//
//    public Set<PTM> getPTMList() {
//        return PTMList;
//    }
//
//    public void setDbMappingList(Set<DatabaseMapping> dbMappingList) {
//        this.dbMappingList = dbMappingList;
//    }
//
//    public Set<DatabaseMapping> getDbMappingList() {
//        return dbMappingList;
//    }
//
//    private void scanFormValidator() {
//        try {
//            ValidationManager.scan(this);
//        } catch (Exception e) {
//            logger.error("validation error: " + e.getMessage(), e);
//            ErrorDialogHandler.showErrorDialog(this, ErrorLevel.FATAL, "Validation error", "Error while reading form validation rules", "REPORT-VALIDATION", e);
//        }
//    }
//
//    private void addForms() {
//        forms = new ArrayList<IForm>();
//
//        forms.add(generalForm1);
//        forms.add(instrumentForm1);
//        forms.add(protocolForm1);
//        forms.add(softwareProcessingForm1);
//        forms.add(referenceExperimentForm1);
//        forms.add(sampleForm1);
//        forms.add(databaseInfoForm);
//        forms.add(pTMDialog1);
//    }
//
//    public static ReportFileGui getInstance() {
//        return instance;
//    }
//
//    public void openReportFile(File file) {
//        reportReaderDAO = new ReportReaderDAO(file);
//        ConverterData.setMasterReportFileName(file.getAbsolutePath());
//    }
//
//    public DAO getReportReaderDAO() {
//        return reportReaderDAO;
//    }
//
//    private void validateAllForms() {
//        setWorkingMessage("Validating report file data");
//        HashMap<String, Collection<ValidatorMessage>> msgs = new HashMap<String, Collection<ValidatorMessage>>();
//        for (IForm frm : forms) {
//            Collection<ValidatorMessage> messageCollection = frm.validateForm();
//            if (messageCollection.isEmpty()) {
//                frm.setErrorHighlight(false);
//            } else {
//                frm.setErrorHighlight(true);
//                msgs.put(frm.getFormName(), messageCollection);
//            }
//        }
//        if (!msgs.isEmpty()) {
//            TabbedValidationDialog tvd = new TabbedValidationDialog(parent, msgs);
//            tvd.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
//            tvd.setVisible(true);
//        }
//
//    }
//
//    private void setWorkingMessage(String msg) {
//        WrapperGUI.instance().setWorkingMessage(msg);
//    }
//
//    private void validateForm(IForm selectedForm) {
//
//        Collection<ValidatorMessage> messages = selectedForm.validateForm();
//        if (messages != null) {
//            StringBuilder buffer = new StringBuilder();
//            for (ValidatorMessage message : messages) {
//                buffer.append(message.getMessage());
//                buffer.append("\n");
//            }
//            if (messages.size() != 0) {
//                tabErrorDialog.showMessage(buffer.toString(), (JComponent) selectedForm);
//            } else {
//                tabErrorDialog.closeMessage((JComponent) selectedForm);
//            }
//        }
//
//    }
//
//    private Pair<NavigableTabbedPane, JComponent> getSelectedTabComponent() {
//        int index = mainTabbedPane.getIndex();
//        NavigableTabbedPane activeTabPane = mainTabbedPane;
//        JComponent component = (JComponent) mainTabbedPane.getComponents()[index];
//        if (component instanceof NavigableTabbedPane) {
//            activeTabPane = innerTabbedPane;
//            NavigableTabbedPane c = (NavigableTabbedPane) component;
//            component = (JComponent) c.getComponents()[c.getIndex()];
//        }
//        return new Pair<NavigableTabbedPane, JComponent>(activeTabPane, component);
//    }
//
//    public boolean next() {
//        if (mainTabbedPane.reachedEnd()) {
//
//            try {
//
//                //perform validation in a background thread, because it is very expensive
//                reportWorker = new SwingWorker<Boolean, Object>() {
//                    /**
//                     * Note that this method is executed only once.
//                     * Note: this method is executed in a background thread.
//                     *
//                     * @return the computed result
//                     * @throws Exception if unable to compute a result
//                     */
//                    @Override
//                    protected Boolean doInBackground() throws Exception {
//
//                        WrapperGUI.instance().startLoading();
//                        setWorkingMessage("Validating report file data");
//                        System.out.println("validating in background");
//                        validateAllForms();
//                        WrapperGUI.instance().stopLoading();
//
//                        return true;
//
//                    }
//                };
//                reportWorker.addPropertyChangeListener(this);
//                reportWorker.execute();
//
//            } catch (Exception e) {
//
//                WrapperGUI.instance().stopLoading();
//                logger.error(e.getMessage(), e);
//                ErrorDialogHandler.showErrorDialog(this, Level.SEVERE, "Validation error", "Error while validating report annotations", "REPORT-VALIDATION", e);
//
//            }
//
//        } else {
//            try {
//                ValidationManager.validate(getSelectedTabComponent().getSecondary());
//            } catch (Throwable e) {
//                logger.error("validation error: " + e.getMessage(), e);
//                ErrorDialogHandler.showErrorDialog(WrapperGUI.instance(), ErrorLevel.FATAL, "Validation error", "Error while validating component: " + e.getMessage(), "COMPONENT-VALIDATION", e);
//            }
//            //only move forward in the inner tabs if the GUI validation is happy
//            if (ValidationManager.validated(getSelectedTabComponent().getSecondary())) {
//                validateForm((IForm) getSelectedTabComponent().getSecondary());
//                mainTabbedPane.next();
//                if (popupDialog != null) {
//                    popupDialog.setVisible(false);
//                    popupDialog = null;
//                }
//            } else {
//                if (popupDialog == null || !popupDialog.isVisible()) {
//                    popupDialog = new PopupDialog(parent);
//                    popupDialog.showMessage("Please complete all mandatory form elements.", navigationPanel1.getNextButton());
//                }
//            }
//        }
//        return false;
//    }
//
//    /**
//     * This method gets called when a bound property is changed.
//     *
//     * @param evt A PropertyChangeEvent object describing the event source
//     *            and the property that has changed.
//     */
//    @Override
//    public void propertyChange(PropertyChangeEvent evt) {
//        try {
//
//            if (reportWorker.isDone()) {
//
//                //check to see if we have any exceptions in the swingworker
//                reportWorker.get();
//                int t = JOptionPane.showConfirmDialog(parent, "Do you want to save the current annotations?", "Metadata annotation complete.", JOptionPane.YES_NO_OPTION);
//                if (t == JOptionPane.YES_OPTION) {
//                    saveReportFileAction(false);
//                    boolean statusOK = copyMetadata();
//                    if (statusOK) {
//                        //if the report gui is running standalone, the index will be 0 - exit
//                        //if the wrapper gui is running, the index will be 1 - continue to export
//                        if (WrapperGUI.instance().getIndex() > 0) {
//                            WrapperGUI.instance().setIndex(WrapperGUI.Panels.FILEEXPORT.getIndex());
//                            WrapperGUI.instance().showPanel(WrapperGUI.Panels.FILEEXPORT.getIndex());
//                            parent.setTitle(bundle.getString("Desktop.this.title"));
//                        } else {
//                            System.exit(0);
//                        }
//                    }
//                } else {
//                    mainTabbedPane.next();
//                }
//
//            }
//        } catch (Exception e) {
//
//            WrapperGUI.instance().stopLoading();
//            logger.error(e.getMessage(), e);
//            ErrorDialogHandler.showErrorDialog(this, ErrorLevel.FATAL, "Validation error", "Error while reading form validation rules", "REPORT-VALIDATION", e);
//
//        }
//    }
//
//    private boolean copyMetadata() {
//
//        WrapperGUI.instance().startLoading();
//
//        try {
//            //only need to copy metadata if we have several input files at once
//            if (!"".equals(ConverterData.getMasterReportFileName()) && ConverterData.getInputFiles().size() > 1) {
//                File masterReportFile = new File(ConverterData.getMasterReportFileName());
//                List<File> destinationFiles = new ArrayList<File>();
//                for (String reportFile : ConverterData.getInputFiles().values()) {
//                    if (!ConverterData.getMasterReportFileName().equals(reportFile)) {
//                        destinationFiles.add(new File(reportFile));
//                    }
//                }
//                setWorkingMessage("Updating metadata from master report file");
//                ReportMetadataCopier.copyMetadata(masterReportFile, destinationFiles);
//            }
//            return true;
//
//        } catch (ConverterException e) {
//            logger.error("metadata copy error: " + e.getMessage(), e);
//            ErrorDialogHandler.showErrorDialog(this, ErrorLevel.FATAL, "Metadata copy error", "Error while copying metadata from master report file to other report files", "REPORT-METADATA", e);
//            return false;
//        } catch (InvalidFormatException e) {
//            logger.error("metadata copy error - invalid format: " + e.getMessage(), e);
//            ErrorDialogHandler.showErrorDialog(this, ErrorLevel.FATAL, "Metadata copy error", "Invalid file format detected while copying metadata from master report file to other report files", "REPORT-METADATA", e);
//            return false;
//        } finally {
//
//            WrapperGUI.instance().stopLoading();
//
//        }
//
//    }
//
//    public boolean back() {
//        //if the report gui is runnign in standalone, the index will be 0
//        if (mainTabbedPane.reachedStart()) {
//
//            if (WrapperGUI.instance().getIndex() > 1) {
//
//                String message = "Do you want to abandon all annotations and return to the previous screen?";
//                int result = JOptionPane.showConfirmDialog(this, message, "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
//                if (result == 0) {
//                    return true;
//                }
//                return false;
//
//            } else {
//
//                int t = JOptionPane.showConfirmDialog(parent, "Do you want to save the current annotations and exit?", "Metadata annotation complete.", JOptionPane.YES_NO_OPTION);
//                if (t == JOptionPane.YES_OPTION) {
//                    saveReportFileAction(false);
//                    System.exit(0);
//                }
//                return false;
//            }
//
//        } else {
//            mainTabbedPane.back();
//            return false;
//        }
//    }
//
//    public void help(ActionEvent e) {
//    }
//
//    private void tabbedPanesStateChanged(ChangeEvent e) {
//
//        // if the instance not initialized yet, return, happens only at the start of application
//        if (!startupDone) return;
//
//        if (internalTabPaneStateChange) {
//            internalTabPaneStateChange = false;
//            return;
//        }
//
//
//        // get the tabbed pane, main or inner tabbed pane
//        JTabbedPane currentSelectionTab = (JTabbedPane) e.getSource();
//
//        int currentSelectionIndex;
//
//        try {
//            ValidationManager.validate(previousSelectedTabPane.getComponent(previousSelectedTabIndex));
//        } catch (Throwable er) {
//            logger.error("validation error: " + er.getMessage(), er);
//            ErrorDialogHandler.showErrorDialog(WrapperGUI.instance(), ErrorLevel.FATAL, "Validation error", "Error while validating component: " + er.getMessage(), "COMPONENT-VALIDATION", er);
//        }
//        //only allow tab change if validation is ok
//        if (ValidationManager.validated(previousSelectedTabPane.getComponent(previousSelectedTabIndex))) {
////rc todo - fix object validation
////            validateForm((IForm) previousSelectedTabPane.getComponent(previousSelectedTabIndex));
//            if (popupDialog != null) {
//                popupDialog.setVisible(false);
//                popupDialog = null;
//            }
//            previousSelectedTabPane = currentSelectionTab;
//            previousSelectedTabIndex = currentSelectionTab.getSelectedIndex();
//            return;
//        } else {
//            internalTabPaneStateChange = true;
//            previousSelectedTabPane.setSelectedIndex(previousSelectedTabIndex);
//            if (popupDialog == null || !popupDialog.isVisible()) {
//                popupDialog = new PopupDialog(parent);
//                popupDialog.showMessage("Please complete all mandatory form elements.", navigationPanel1.getNextButton());
//            }
//        }
////        // get all opened windows, if tab changed -- PopupDialogs fo Validations  and close them
////        Window[] windows = ReportFileGui.instance.getOwnedWindows();
////        for (Window window : windows) {
////            window.setVisible(false);
////        }
////
////        // tabChanged is changed only ValidationListener, focusLost method.
////        tabErrorDialog.closeMessage(getSelectedTabComponent().getPrimary());
//
//    }
//
//    private void reportFileNameFieldMouseClicked(MouseEvent e) {
//        if (e.getClickCount() == 2) {
//            reportFileNameField.setEditable(true);
//        } else if (e.getClickCount() == 1) {
//            reportFileNameField.setEditable(false);
//        }
//    }
//
//    private void reportFileNameFieldFocusLost(FocusEvent e) {
//        reportFileNameField.setEditable(false);
//    }
//
//    private void newButtonActionPerformed(ActionEvent e) {
//        if (edit) {
//            int t = JOptionPane.showConfirmDialog(this,
//                    MessageFormat.format(bundle.getString("Edit.confirm"),
//                            reportFileNameField.getText()),
//                    bundle.getString("Warning"),
//                    JOptionPane.YES_NO_OPTION);
//            if (t == 1) return;
//        }
//        edit = false;
//        reportReaderDAO = null;
//
//        String s = new MessageFormat(bundle.getString("NewFile.text")).format(new Object[]{fileCounter++});
//        reportFileNameField.setText(s);
//        logger.debug(s);
//        clearForms();
//        parent.setTitle(bundle.getString("ReportFileGui.this.title"));
//
//    }
//
//    private void openButtonActionPerformed(ActionEvent e) {
//        edit = true;
//        WrapperGUI.instance().startLoading();
//        JFileChooser chooser = new JFileChooser();
//        int returnVal = chooser.showOpenDialog(this);
//        if (returnVal == JFileChooser.APPROVE_OPTION) {
//            refreshForms(chooser.getSelectedFile());
//        }
//        WrapperGUI.instance().stopLoading();
//    }
//
//    private void saveButtonActionPerformed(ActionEvent e) {
//        final boolean saveas = e.getSource().equals(saveAsButton);
//        if (SwingUtilities.isEventDispatchThread()) {
//            SwingWorker worker = new SwingWorker() {
//                @Override
//                protected Object doInBackground() throws Exception {
//                    saveReportFileAction(saveas);
//                    return null;
//                }
//            };
//            worker.execute();
//        } else {
//            saveReportFileAction(saveas);
//        }
//    }
//
//    private void saveReportFileAction(boolean saveas) {
//
//        WrapperGUI.instance().startLoading();
//
//        if (edit) {
//            for (IForm form : forms) {
//                try {
//                    form.collect(reportReaderDAO);
//                    form.collectTemplate();
//                } catch (Exception e) {
//                    logger.error("template collection error: " + e.getMessage(), e);
//                    ErrorDialogHandler.showErrorDialog(this, ErrorLevel.FATAL, "Template collection error", "Error while collecting templates", "REPORT-TEMPLATE", e);
//                }
//            }
//            File tmp = selectedFile;
//            if (saveas) {
//                JFileChooser chooser = new JFileChooser(ConverterData.getLastSelectedDirectory());
//                int returnVal = chooser.showSaveDialog(this);
//
//                if (returnVal == JFileChooser.APPROVE_OPTION) {
//                    tmp = chooser.getSelectedFile();
//                } else {
//                    return;
//                }
//            }
//            writeReportFile(tmp);
//        } else {
//            for (final IForm form : forms) {
//                try {
//                    form.collectTemplate();
//                } catch (Exception e) {
//                    logger.error("template collection error: " + e.getMessage(), e);
//                    ErrorDialogHandler.showErrorDialog(this, ErrorLevel.FATAL, "Template collection error", "Error while collecting templates", "REPORT-TEMPLATE", e);
//                }
//            }
//            clearForms();
//        }
//
//        WrapperGUI.instance().stopLoading();
//
//    }
//
//    public void refreshForms(File selectedFile) {
//        try {
//            edit = true;
//            this.selectedFile = selectedFile;
//            parent.setTitle(bundle.getString("ReportFileGui.this.editing.title"));
//
//            openReportFile(selectedFile);
//            reportFileNameField.setText(selectedFile.getName());
//            for (IForm form : forms) {
//                form.refresh();
//            }
//        } catch (Exception e) {
//            logger.error("form refresh error: " + e.getMessage(), e);
//            ErrorDialogHandler.showErrorDialog(this, ErrorLevel.FATAL, "Form refresh error", "Error while refreshing forms", "REPORT-TEMPLATE", e);
//        }
//    }
//
//    private void clearForms() {
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                for (IForm form : forms) {
//                    form.clear();
//                }
//            }
//        };
//        ETDUtilities.execute(runnable);
//    }
//
//    private void writeReportFile(File tmp) {
//        try {
//            ReportWriter writer = new ReportWriter(tmp.getPath());
//            writer.setDAO(reportReaderDAO);
//            setWorkingMessage("Writing report file: " + tmp.getAbsolutePath());
//            writer.writeReport();
//
//            reportFileNameField.setText(tmp.getName());
//            refreshForms(tmp);
//
//        } catch (Exception e) {
//            logger.error("error writing report file: " + e.getMessage(), e);
//            ErrorDialogHandler.showErrorDialog(this, ErrorLevel.FATAL, "Error writing report file", "An error occurred while writing the report file", "REPORT-WRITE", e);
//            WrapperGUI.instance().stopLoading();
//        }
//    }
//
//    private void initComponents() {
//        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
//        // Generated using JFormDesigner non-commercial license
//        ResourceBundle bundle = ResourceBundle.getBundle("messages");
//        annotationPanel = new JPanel();
//        newButton = new JButton();
//        openButton = new JButton();
//        saveButton = new JButton();
//        saveAsButton = new JButton();
//        label1 = new JLabel();
//        reportFileNameField = new JTextField();
//        mainTabbedPane = new NavigableTabbedPane();
//        generalForm1 = new GeneralForm();
//        referenceExperimentForm1 = new ReferenceExperimentForm();
//        innerTabbedPane = new NavigableTabbedPane();
//        sampleForm1 = new SampleForm();
//        protocolForm1 = new ProtocolForm();
//        instrumentForm1 = new InstrumentForm();
//        softwareProcessingForm1 = new SoftwareProcessingForm();
//        databaseInfoForm = new DatabaseMappingForm();
//        pTMDialog1 = new PTMDialog();
//        navigationPanel1 = new NavigationPanel();
//
//        //======== this ========
//
//        //======== annotationPanel ========
//        {
//
//            //---- newButton ----
//            newButton.setIcon(new ImageIcon(getClass().getResource("/images/new.gif")));
//            newButton.setToolTipText(bundle.getString("ReportFileGui.newButton.toolTipText"));
//            newButton.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    newButtonActionPerformed(e);
//                }
//            });
//
//            //---- openButton ----
//            openButton.setIcon(new ImageIcon(getClass().getResource("/images/open.gif")));
//            openButton.setToolTipText(bundle.getString("ReportFileGui.openButton.toolTipText"));
//            openButton.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    openButtonActionPerformed(e);
//                }
//            });
//
//            //---- saveButton ----
//            saveButton.setIcon(new ImageIcon(getClass().getResource("/images/save.gif")));
//            saveButton.setToolTipText(bundle.getString("ReportFileGui.saveButton.toolTipText"));
//            saveButton.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    saveButtonActionPerformed(e);
//                }
//            });
//
//            //---- saveAsButton ----
//            saveAsButton.setIcon(new ImageIcon(getClass().getResource("/images/saveas2.png")));
//            saveAsButton.setToolTipText(bundle.getString("ReportFileGui.saveAsButton.toolTipText"));
//            saveAsButton.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    saveButtonActionPerformed(e);
//                }
//            });
//
//            //---- label1 ----
//            label1.setText(bundle.getString("ReportFileGui.label1.text"));
//            label1.setLabelFor(reportFileNameField);
//
//            //---- reportFileNameField ----
//            reportFileNameField.setEditable(false);
//            reportFileNameField.addMouseListener(new MouseAdapter() {
//                @Override
//                public void mouseClicked(MouseEvent e) {
//                    reportFileNameFieldMouseClicked(e);
//                }
//            });
//            reportFileNameField.addFocusListener(new FocusAdapter() {
//                @Override
//                public void focusLost(FocusEvent e) {
//                    reportFileNameFieldFocusLost(e);
//                }
//            });
//
//            //======== mainTabbedPane ========
//            {
//                mainTabbedPane.addChangeListener(new ChangeListener() {
//                    @Override
//                    public void stateChanged(ChangeEvent e) {
//                        tabbedPanesStateChanged(e);
//                    }
//                });
//                mainTabbedPane.setName(MAIN_TABBED_PANE_NAME);
//                mainTabbedPane.addTab(bundle.getString("ReportFileGui.generalForm1.tab.title"), generalForm1);
//
//                mainTabbedPane.addTab(bundle.getString("ReportFileGui.referenceExperimentForm1.tab.title"), referenceExperimentForm1);
//
//
//                //======== innerTabbedPane ========
//                {
//                    innerTabbedPane.addChangeListener(new ChangeListener() {
//                        @Override
//                        public void stateChanged(ChangeEvent e) {
//                            tabbedPanesStateChanged(e);
//                        }
//                    });
//                    innerTabbedPane.setName(INNER_TABBED_PANE_NAME);
//                    innerTabbedPane.addTab(bundle.getString("ReportFileGui.sampleForm1.tab.title"), sampleForm1);
//
//                    innerTabbedPane.addTab(bundle.getString("ReportFileGui.protocolForm1.tab.title"), protocolForm1);
//
//                    innerTabbedPane.addTab(bundle.getString("ReportFileGui.instrumentForm1.tab.title"), instrumentForm1);
//
//                    innerTabbedPane.addTab(bundle.getString("ReportFileGui.softwareProcessingForm1.tab.title"), softwareProcessingForm1);
//
//                }
//                mainTabbedPane.addTab(bundle.getString("ReportFileGui.innerTabbedPane.tab.title"), innerTabbedPane);
//
//                mainTabbedPane.addTab(bundle.getString("ReportFileGui.databaseInfoForm.tab.title"), databaseInfoForm);
//
//                mainTabbedPane.addTab(bundle.getString("ReportFileGui.pTMDialog1.tab.title"), pTMDialog1);
//
//            }
//
//            GroupLayout annotationPanelLayout = new GroupLayout(annotationPanel);
//            annotationPanel.setLayout(annotationPanelLayout);
//            annotationPanelLayout.setHorizontalGroup(
//                    annotationPanelLayout.createParallelGroup()
//                            .addGroup(annotationPanelLayout.createParallelGroup()
//                                    .addGroup(annotationPanelLayout.createSequentialGroup()
//                                            .addGap(0, 0, 0)
//                                            .addGroup(annotationPanelLayout.createParallelGroup()
//                                                    .addComponent(navigationPanel1, GroupLayout.DEFAULT_SIZE, 928, Short.MAX_VALUE)
//                                                    .addGroup(annotationPanelLayout.createSequentialGroup()
//                                                            .addComponent(newButton, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
//                                                            .addGap(6, 6, 6)
//                                                            .addComponent(openButton, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
//                                                            .addGap(6, 6, 6)
//                                                            .addComponent(saveButton, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
//                                                            .addGap(6, 6, 6)
//                                                            .addComponent(saveAsButton, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
//                                                            .addGap(6, 6, 6)
//                                                            .addComponent(label1, GroupLayout.PREFERRED_SIZE, 107, GroupLayout.PREFERRED_SIZE)
//                                                            .addGap(6, 6, 6)
//                                                            .addComponent(reportFileNameField, GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE))
//                                                    .addComponent(mainTabbedPane, GroupLayout.DEFAULT_SIZE, 928, Short.MAX_VALUE))))
//                            .addGap(0, 928, Short.MAX_VALUE)
//            );
//            annotationPanelLayout.setVerticalGroup(
//                    annotationPanelLayout.createParallelGroup()
//                            .addGroup(annotationPanelLayout.createParallelGroup()
//                                    .addGroup(annotationPanelLayout.createSequentialGroup()
//                                            .addGroup(annotationPanelLayout.createParallelGroup()
//                                                    .addGroup(annotationPanelLayout.createSequentialGroup()
//                                                            .addGap(0, 0, Short.MAX_VALUE)
//                                                            .addGroup(annotationPanelLayout.createParallelGroup()
//                                                                    .addComponent(newButton)
//                                                                    .addComponent(openButton)
//                                                                    .addComponent(saveButton)
//                                                                    .addComponent(saveAsButton)
//                                                                    .addGroup(annotationPanelLayout.createSequentialGroup()
//                                                                            .addGap(4, 4, 4)
//                                                                            .addComponent(label1)))
//                                                            .addGap(12, 12, 12))
//                                                    .addGroup(annotationPanelLayout.createSequentialGroup()
//                                                            .addGap(2, 2, 2)
//                                                            .addComponent(reportFileNameField, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
//                                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)))
//                                            .addComponent(mainTabbedPane, GroupLayout.PREFERRED_SIZE, 625, GroupLayout.PREFERRED_SIZE)
//                                            .addGap(6, 6, 6)
//                                            .addComponent(navigationPanel1, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
//                                            .addContainerGap()))
//                            .addGap(0, 699, Short.MAX_VALUE)
//            );
//        }
//
//        GroupLayout layout = new GroupLayout(this);
//        setLayout(layout);
//        layout.setHorizontalGroup(
//                layout.createParallelGroup()
//                        .addGroup(layout.createSequentialGroup()
//                                .addContainerGap()
//                                .addComponent(annotationPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//                                .addContainerGap())
//        );
//        layout.setVerticalGroup(
//                layout.createParallelGroup()
//                        .addGroup(layout.createSequentialGroup()
//                                .addContainerGap()
//                                .addComponent(annotationPanel, GroupLayout.PREFERRED_SIZE, 697, GroupLayout.PREFERRED_SIZE)
//                                .addContainerGap(12, Short.MAX_VALUE))
//        );
//        // JFormDesigner - End of component initialization  //GEN-END:initComponents
//    }
//
//    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
//    // Generated using JFormDesigner non-commercial license
//    private JPanel annotationPanel;
//    private JButton newButton;
//    private JButton openButton;
//    private JButton saveButton;
//    private JButton saveAsButton;
//    private JLabel label1;
//    private JTextField reportFileNameField;
//    private NavigableTabbedPane mainTabbedPane;
//    private GeneralForm generalForm1;
//    private ReferenceExperimentForm referenceExperimentForm1;
//    private NavigableTabbedPane innerTabbedPane;
//    private SampleForm sampleForm1;
//    private ProtocolForm protocolForm1;
//    private InstrumentForm instrumentForm1;
//    private SoftwareProcessingForm softwareProcessingForm1;
//    private DatabaseMappingForm databaseInfoForm;
//    private PTMDialog pTMDialog1;
//    private NavigationPanel navigationPanel1;
//    // JFormDesigner - End of variables declaration  //GEN-END:variables
//
//
//    public static void main(String[] args) throws Exception {
//        UIManager.setLookAndFeel("com.jtattoo.plaf.mint.MintLookAndFeel");
//        WrapperGUI instance = WrapperGUI.instance();
//        ReportFileGui gui = new ReportFileGui(instance);
//        WrapperGUI.instance().showPanel(gui);
//    }
//
//    public void setEditingMode() {
//        newButton.setEnabled(false);
//        openButton.setEnabled(false);
//    }
//
//    public boolean isEditable() {
//        return edit;
//    }
//
//    public JFrame getParentFrame() {
//        return parent;
//    }
//
//}
