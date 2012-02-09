/*
 * Created by JFormDesigner on Fri Oct 21 16:10:57 BST 2011
 */

package uk.ac.ebi.pride.tools.converter.gui;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.border.DropShadowBorder;
import org.jdesktop.swingx.error.ErrorLevel;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.ProgressDialog;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.TabbedValidationDialog;
import uk.ac.ebi.pride.tools.converter.gui.interfaces.ConverterForm;
import uk.ac.ebi.pride.tools.converter.gui.interfaces.ValidationListener;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.gui.model.GUIException;
import uk.ac.ebi.pride.tools.converter.gui.util.error.ErrorDialogHandler;
import uk.ac.ebi.pride.tools.converter.gui.util.template.TemplateUtilities;
import uk.ac.ebi.pride.tools.converter.report.validator.ReportObjectValidator;
import uk.ac.ebi.pride.tools.converter.utils.config.Configurator;
import uk.ac.ebi.pride.tools.converter.utils.xml.validation.ValidatorFactory;
import uk.ac.ebi.pride.validator.PrideXmlValidator;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * @author User #3
 */
public class NavigationPanel extends JFrame implements ValidationListener {

    private static final Logger logger = Logger.getLogger(NavigationPanel.class);

    private static NavigationPanel instance = new NavigationPanel();

    //variables to keep track of registered forms and navigation
    private List<String> formNames = new ArrayList<String>();
    private List<ConverterForm> forms = new ArrayList<ConverterForm>();
    private int selectedIndex = 0;

    //variables to keep track of validation status and messages
    private Map<String, Collection<ValidatorMessage>> validationMessages = new LinkedHashMap<String, Collection<ValidatorMessage>>();
    private int errorMessageCount = 0;
    private int warningMessageCount = 0;
    private int infoMessageCount = 0;

    //help subsystem
    private HelpBroker mainHelpBroker;
    private HelpSet mainHelpSet;

    //user notification
    ProgressDialog progressDialog = null;

    public static NavigationPanel getInstance() {
        return instance;
    }

    private NavigationPanel() {

        try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.mint.MintLookAndFeel");

            //fix copy/paste shortcut in mac
            if (Configurator.getOSName().toLowerCase().contains("mac")) {
                //textfield
                InputMap im = (InputMap) UIManager.get("TextField.focusInputMap");
                im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), DefaultEditorKit.copyAction);
                im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
                im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);
                //textarea
                im = (InputMap) UIManager.get("TextArea.focusInputMap");
                im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), DefaultEditorKit.copyAction);
                im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
                im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\nName of the OS: " + Configurator.getOSName());
        System.out.println("Version of the OS: " + Configurator.getOSVersion());
        System.out.println("Architecture of The OS: " + Configurator.getOSArch());
        System.out.println("User Home: " + Configurator.getUserHome());

        initComponents();

        //update the jlist selection model so that only the system-selected value is highlighted
        panelList.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, index == selectedIndex, index == selectedIndex);
                return this;
            }
        });
        panelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        try {
            ClassLoader cl = NavigationPanel.class.getClassLoader();
            URL url = HelpSet.findHelpSet(cl, "help/MainHelp.hs");
            mainHelpSet = new HelpSet(cl, url);
            mainHelpBroker = mainHelpSet.createHelpBroker();
        } catch (HelpSetException e) {
            logger.error("Failed to initialize help documents", e);
        }

        //init templates
        TemplateUtilities.initTemplateFolders();
        initValidation();

    }

    private void initValidation() {

        //load validators in background
        final SwingWorker sw = new SwingWorker() {
            /**
             * Computes a result, or throws an exception if unable to do so.
             * Note that this method is executed only once.
             * Note: this method is executed in a background thread.
             *
             * @return the computed result
             * @throws Exception if unable to compute a result
             */
            @Override
            protected Object doInBackground() throws Exception {

                try {
                    //run in background
                    ReportObjectValidator repValidator = ValidatorFactory.getInstance().getReportValidator();
                    PrideXmlValidator prideValidator = ValidatorFactory.getInstance().getPrideXmlValidator();
                    System.out.println("Validator Loaded");
                    return null;
                } catch (Exception e) {
                    return e;
                }

            }
        };
        //add a listener to the swingworker so that the navigation flow can continue
        //once the background thread is done
        sw.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
                    try {

                        //if there is no error, we can navigate to the next frame
                        Exception value = (Exception) sw.get();
                        if (value != null) {
                            logger.error("Execution error: " + value.getMessage(), value);
                            ErrorDialogHandler.showErrorDialog(instance, ErrorLevel.FATAL, "Error executing background job", "An wrror occurred while processing the conversion", "NAVIGATOR_PANEL", value);
                        }
                    } catch (Exception e) {
                        logger.error("Execution error: " + e.getMessage(), e);
                        ErrorDialogHandler.showErrorDialog(instance, ErrorLevel.FATAL, "Error executing background job", "An wrror occurred while processing the conversion", "NAVIGATOR_PANEL", e);
                    }
                }

            }
        });
        sw.execute();

    }

    /**
     * Add a form to the main navigation panel. It is assumed that all ConverterForms implementations are JPanels
     *
     * @param form
     */
    public void registerForm(ConverterForm form) {
        if (!(form instanceof JPanel)) {
            throw new IllegalArgumentException("All ConverterForm implementations must be JPanels");
        }
        //add to card layout
        contentPanel.add((JPanel) form, form.getFormName());
        formNames.add(form.getFormName());
        forms.add(form);

        //register listener
        form.addValidationListener(this);

        //add to panel list display
        ((DefaultListModel) panelList.getModel()).addElement(form.getFormName());
    }

    /**
     * Add a form to the main navigation panel after the current form. It is assumed that all ConverterForms implementations are JPanels
     *
     * @param formToRegister - the form to add
     * @param currentForm    - the form to add the element after
     */
    public void registerFormAfter(ConverterForm formToRegister, ConverterForm currentForm) {

        if (!(formToRegister instanceof JPanel)) {
            throw new IllegalArgumentException("All ConverterForm implementations must be JPanels");
        }
        //get index
        int index = -1;
        for (ConverterForm form : forms) {
            index++;
            if (form == currentForm) {
                break;
            }
        }

        if (index < 0) {
            throw new IllegalArgumentException("Could not find form in forms list: " + currentForm.getFormName());
        }

        //add to card layout
        contentPanel.add((JPanel) formToRegister, formToRegister.getFormName(), index + 1);
        formNames.add(index + 1, formToRegister.getFormName());
        forms.add(index + 1, formToRegister);
        //revalidate card layout
        contentPanel.validate();

        //register listener
        formToRegister.addValidationListener(this);

        //add to panel list display

        ((DefaultListModel) panelList.getModel()).add(index + 1, formToRegister.getFormName());
        panelList.validate();
    }

    /**
     * Navigation method to go to next panel. Will call validateForm() from the current ConverterForm before
     * navigating to the next form.
     */
    private void next() {

        //validate current form
        ConverterForm form = forms.get(selectedIndex);
        try {
            Collection<ValidatorMessage> messages = form.validateForm();
            //remove any previously returned messages - i.e. if there were errors that were then fixed
            validationMessages.remove(form.getFormName());
            //store messages
            if (!messages.isEmpty()) {
                validationMessages.put(form.getFormName(), messages);
            }
            processValidatorMessages();
        } catch (ValidatorException e) {
            logger.error("Validation error: " + e.getMessage(), e);
            ErrorDialogHandler.showErrorDialog(instance, ErrorLevel.FATAL, "Error validating form", "An wrror occurred validating the data in the form: " + form.getFormName(), "NAVIGATOR_PANEL", e);
        }

        if (errorMessageCount == 0) {

            //if ok, go to next form
            if (selectedIndex < formNames.size()) {

                //call finish on current form - this might be a lengthy operation so run in swingworker
                final SwingWorker sw = new SwingWorker() {
                    /**
                     * Computes a result, or throws an exception if unable to do so.
                     * Note that this method is executed only once.
                     * Note: this method is executed in a background thread.
                     *
                     * @return the computed result
                     * @throws Exception if unable to compute a result
                     */
                    @Override
                    protected Object doInBackground() throws Exception {

                        try {
                            //run finish() in background
                            forms.get(selectedIndex).finish();
                            return null;
                        } catch (Exception e) {
                            return e;
                        }

                    }
                };
                //add a listener to the swingworker so that the navigation flow can continue
                //once the background thread is done
                sw.addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
                            try {

                                progressDialog.setVisible(false);
                                progressDialog.dispose();
                                progressDialog = null;

                                //if there is no error, we can navigate to the next frame
                                Exception value = (Exception) sw.get();
                                if (value == null) {

                                    //save the current data to the master report dao
                                    forms.get(selectedIndex).save(ConverterData.getInstance().getMasterDAO());

                                    //the next button will be reenabled by fireValidationListener(true)
                                    nextButton.setEnabled(false);

                                    //increment form index
                                    selectedIndex++;

                                    //call start on form to ensure that everything is ready for display
                                    //note that start() also pings the validation listener to ensure that the
                                    //'next' button is in the correct state
                                    forms.get(selectedIndex).start();

                                    //update form with values from the MasterDAO
                                    //note that load is called *after* start
                                    forms.get(selectedIndex).load(ConverterData.getInstance().getMasterDAO());

                                    //update main panel layout
                                    CardLayout cl = (CardLayout) (contentPanel.getLayout());
                                    cl.show(contentPanel, formNames.get(selectedIndex));

                                    //update description
                                    panelDescription.setText(forms.get(selectedIndex).getFormDescription());

                                    //update navigation list
                                    panelList.clearSelection();
                                    panelList.revalidate();
                                    panelList.repaint();
                                    panelList.setSelectedIndex(selectedIndex);

                                    //update progressbar
                                    progressBar1.setValue(selectedIndex + 1);

                                    //update help button action
                                    CSH.setHelpIDString(helpButton, forms.get(selectedIndex).getHelpResource());
                                    helpButton.addActionListener(new CSH.DisplayHelpFromSource(mainHelpBroker));

                                    //check to see if we're on the last form!

//                                    if (selectedIndex == forms.size() - 1) {
//                                        //we're at the last form
//                                        nextButton.setText("Quit");
//                                        for (ActionListener al : nextButton.getActionListeners()) {
//                                            nextButton.removeActionListener(al);
//                                        }
//                                        nextButton.addActionListener(new ActionListener() {
//                                            @Override
//                                            public void actionPerformed(ActionEvent e) {
//                                                quit();
//                                            }
//                                        });
//                                        nextButton.setEnabled(true);
//                                        nextButton.revalidate();
//                                        nextButton.repaint();
//                                    }

                                } else {
                                    if (value instanceof GUIException) {
                                        GUIException e = (GUIException) value;
                                        logger.error("Execution error: " + e.getMessage(), e);
                                        ErrorDialogHandler.showErrorDialog(instance, ErrorLevel.SEVERE, e.getShortMessage(), e.getDetailedMessage(), e.getComponent(), e);
                                    } else {
                                        logger.error("Execution error: " + value.getMessage(), value);
                                        ErrorDialogHandler.showErrorDialog(instance, ErrorLevel.FATAL, "Error executing background job", "An wrror occurred while processing the conversion", "NAVIGATOR_PANEL", value);
                                    }
                                }
                            } catch (Exception e) {
                                logger.error("Execution error: " + e.getMessage(), e);
                                ErrorDialogHandler.showErrorDialog(instance, ErrorLevel.FATAL, "Error executing background job", "An wrror occurred while processing the conversion", "NAVIGATOR_PANEL", e);
                            }
                        }

                    }
                });
                //create the progress dialog first so that the messages are properly shown, if any
                progressDialog = new ProgressDialog(this, sw);
                //set progress dialog to be modal so that users can't click "next" several times
                progressDialog.setModal(true);
                sw.execute();
                progressDialog.setVisible(true);

            }

        } else {
            //if not ok, show validation warning
            JOptionPane.showMessageDialog(this, "There are errors in the current form. You must correct them before you can progress to the next step.", "Error!", JOptionPane.ERROR_MESSAGE);
            showValidationMessages(form.getFormName());
        }
    }


    /**
     * Navigation method - go to previous panel
     */
    private void back() {
        if (selectedIndex > 0) {
            //remove any previously returned messages - esp if there were errors that were not fixed
            validationMessages.remove(forms.get(selectedIndex).getFormName());
            processValidatorMessages();

            //update index
            selectedIndex--;

            //update main content
            CardLayout cl = (CardLayout) (contentPanel.getLayout());
            cl.show(contentPanel, formNames.get(selectedIndex));

            //update list
            panelList.clearSelection();
            panelList.revalidate();
            panelList.repaint();
            panelList.setSelectedIndex(selectedIndex);

            //update progressbar
            progressBar1.setValue(selectedIndex + 1);

            //update description
            panelDescription.setText(forms.get(selectedIndex).getFormDescription());

            //update help button action
            CSH.setHelpIDString(helpButton, forms.get(selectedIndex).getHelpResource());
            helpButton.addActionListener(new CSH.DisplayHelpFromSource(mainHelpBroker));

            //if we can go back, we should be able to go straight forward again
            //if nothing changes in the form
            fireValidationListener(true);

            //if we were on the last form before but went back,
            //we now want to go forward again
            if ("Quit".equals(nextButton.getText())) {
                nextButton.setText("Next");
                for (ActionListener al : nextButton.getActionListeners()) {
                    nextButton.removeActionListener(al);
                }
                nextButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        next();
                    }
                });
                nextButton.setEnabled(true);
                nextButton.revalidate();
                nextButton.repaint();
            }
        }
    }

    /**
     * Clear the current ConverterForm being displayed
     */
    private void clear() {
        ConverterForm form = forms.get(selectedIndex);
        form.clear();
    }

    /**
     * clear all forms following the form given as a parameter - this is useful in the case
     * where one form regenerates source files that will then invalidate any preexisting data
     * in all subsequent forms
     *
     * @param aForm
     */
    public void clearAllFromForm(ConverterForm aForm) {
        boolean clearFromNow = false;
        for (ConverterForm form : forms) {
            if (clearFromNow) {
                form.clear();
            }
            if (aForm == form) {
                clearFromNow = true;
            }
        }
    }


    /**
     * quit the application
     */
    private void quit() {
        int value = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit this application?", "Please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (value == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /**
     * display help based on the current ConverterForm being displayed
     */
    private void help() {

//        //for now, show about dialog
//        AboutDialog about = new AboutDialog(this);
//        about.setVisible(true);

    }

    public void reset() {

        //update progress bar boundaries
        progressBar1.setMinimum(0);
        progressBar1.setMaximum(formNames.size());
        progressBar1.setValue(0);

        //the next button will be reenabled by fireValidationListener(true)
        nextButton.setEnabled(false);

        //set description
        panelDescription.setText(forms.get(0).getFormDescription());

        //update help button action
        CSH.setHelpIDString(helpButton, forms.get(0).getHelpResource());
        helpButton.addActionListener(new CSH.DisplayHelpFromSource(mainHelpBroker));

        CardLayout cl = (CardLayout) (contentPanel.getLayout());
        cl.first(contentPanel);

        //call start on first form
        selectedIndex = 0;
        ConverterForm form = forms.get(selectedIndex);
        form.start();

        pack();
        setVisible(true);

    }

    private void processValidatorMessages() {
        //reset totals
        errorMessageCount = 0;
        warningMessageCount = 0;
        infoMessageCount = 0;
        //update totals
        for (Collection<ValidatorMessage> messages : validationMessages.values()) {
            for (ValidatorMessage msg : messages) {
                if (MessageLevel.FATAL.equals(msg.getLevel())) {
                    errorMessageCount++;
                } else if (MessageLevel.ERROR.equals(msg.getLevel())) {
                    errorMessageCount++;
                } else if (MessageLevel.WARN.equals(msg.getLevel())) {
                    warningMessageCount++;
                } else if (MessageLevel.INFO.equals(msg.getLevel())) {
                    infoMessageCount++;
                } else if (MessageLevel.DEBUG.equals(msg.getLevel())) {
                    infoMessageCount++;
                }
            }
        }
        errorCountLabel.setText(errorMessageCount + " errors");
        warningCountLabel.setText(warningMessageCount + " warnings");
        infoCountLabel.setText(infoMessageCount + " messages");
        if (errorMessageCount + warningMessageCount + infoMessageCount > 0) {
            viewMessageButton.setEnabled(true);
        } else {
            viewMessageButton.setEnabled(false);
        }
    }

    public void hideValidatorMessages() {
        errorCountLabel.setVisible(false);
        warningCountLabel.setVisible(false);
        infoCountLabel.setVisible(false);
        viewMessageButton.setVisible(false);
    }

    private void showValidationMessages() {
        showValidationMessages(null);
    }

    private void showValidationMessages(String selectedForm) {
        TabbedValidationDialog dialog = new TabbedValidationDialog(this, validationMessages);
        if (selectedForm != null) {
            dialog.setSelectedTab(selectedForm);
        } else {
            dialog.selectLastTab();
        }
        dialog.setModal(false);
        dialog.setVisible(true);
    }

    @Override
    public void fireValidationListener(boolean isValid) {
        nextButton.setEnabled(isValid);
    }

    public void setWorkingMessage(String message) {
        if (progressDialog != null) {
            progressDialog.setMessage(message);
        } else {
            System.err.println("MESSGE NOT SHOWN: " + message);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        panel1 = new JPanel();
        scrollPane1 = new JScrollPane();
        panelList = new JList(new DefaultListModel());
        scrollPane2 = new JScrollPane();
        panelDescription = new JTextArea();
        progressBar1 = new JProgressBar();
        contentPanel = new JPanel();
        panel4 = new JPanel();
        helpButton = new JButton();
        nextButton = new JButton();
        backButton = new JButton();
        clearButton = new JButton();
        quitButton = new JButton();
        panel2 = new JPanel();
        viewMessageButton = new JButton();
        infoCountLabel = new JLabel();
        warningCountLabel = new JLabel();
        errorCountLabel = new JLabel();

        //======== this ========
        setTitle("PRIDE Converter");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 768));
        Container contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setBorder(new DropShadowBorder());

            //======== scrollPane1 ========
            {

                //---- panelList ----
                panelList.setBackground(null);
                panelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                scrollPane1.setViewportView(panelList);
            }

            //======== scrollPane2 ========
            {

                //---- panelDescription ----
                panelDescription.setLineWrap(true);
                panelDescription.setEditable(false);
                panelDescription.setWrapStyleWord(true);
                scrollPane2.setViewportView(panelDescription);
            }

            //---- progressBar1 ----
            progressBar1.setForeground(new Color(0, 102, 102));

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                    panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                                            .addComponent(progressBar1, GroupLayout.PREFERRED_SIZE, 187, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(scrollPane2, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 187, GroupLayout.PREFERRED_SIZE))
                                    .addContainerGap())
            );
            panel1Layout.setVerticalGroup(
                    panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 341, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(progressBar1, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
                                    .addContainerGap())
            );
        }

        //======== contentPanel ========
        {
            contentPanel.setBorder(new DropShadowBorder());
            contentPanel.setLayout(new CardLayout());
        }

        //======== panel4 ========
        {

            //---- helpButton ----
            helpButton.setText("Help");
            helpButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    help();
                }
            });

            //---- nextButton ----
            nextButton.setText("Next");
            nextButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    next();
                }
            });

            //---- backButton ----
            backButton.setText("Back");
            backButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    back();
                }
            });

            //---- clearButton ----
            clearButton.setText("Clear");
            clearButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    clear();
                }
            });

            //---- quitButton ----
            quitButton.setText("Quit");
            quitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    quit();
                }
            });

            GroupLayout panel4Layout = new GroupLayout(panel4);
            panel4.setLayout(panel4Layout);
            panel4Layout.setHorizontalGroup(
                    panel4Layout.createParallelGroup()
                            .addGroup(panel4Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(helpButton)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 358, Short.MAX_VALUE)
                                    .addComponent(quitButton)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(clearButton)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(backButton)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(nextButton)
                                    .addContainerGap())
            );
            panel4Layout.setVerticalGroup(
                    panel4Layout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, panel4Layout.createSequentialGroup()
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(helpButton)
                                            .addComponent(nextButton)
                                            .addComponent(backButton)
                                            .addComponent(clearButton)
                                            .addComponent(quitButton))
                                    .addContainerGap())
            );
        }

        //======== panel2 ========
        {
            panel2.setBorder(new DropShadowBorder());

            //---- viewMessageButton ----
            viewMessageButton.setText("View all validation messages");
            viewMessageButton.setEnabled(false);
            viewMessageButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showValidationMessages();
                }
            });

            //---- infoCountLabel ----
            infoCountLabel.setText("0 Messages");
            infoCountLabel.setForeground(Color.blue);

            //---- warningCountLabel ----
            warningCountLabel.setText("0 Warnings");
            warningCountLabel.setForeground(new Color(255, 102, 0));

            //---- errorCountLabel ----
            errorCountLabel.setText("0 Errors");
            errorCountLabel.setForeground(Color.red);

            GroupLayout panel2Layout = new GroupLayout(panel2);
            panel2.setLayout(panel2Layout);
            panel2Layout.setHorizontalGroup(
                    panel2Layout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
                                    .addContainerGap(222, Short.MAX_VALUE)
                                    .addComponent(errorCountLabel)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(warningCountLabel)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(infoCountLabel)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(viewMessageButton)
                                    .addContainerGap())
            );
            panel2Layout.setVerticalGroup(
                    panel2Layout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
                                    .addContainerGap(12, Short.MAX_VALUE)
                                    .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(viewMessageButton)
                                            .addComponent(infoCountLabel)
                                            .addComponent(warningCountLabel)
                                            .addComponent(errorCountLabel, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE))
                                    .addContainerGap())
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(contentPanel, GroupLayout.DEFAULT_SIZE, 721, Short.MAX_VALUE)
                                        .addComponent(panel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                                .addComponent(contentPanel, GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(panel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel panel1;
    private JScrollPane scrollPane1;
    private JList panelList;
    private JScrollPane scrollPane2;
    private JTextArea panelDescription;
    private JProgressBar progressBar1;
    private JPanel contentPanel;
    private JPanel panel4;
    private JButton helpButton;
    private JButton nextButton;
    private JButton backButton;
    private JButton clearButton;
    private JButton quitButton;
    private JPanel panel2;
    private JButton viewMessageButton;
    private JLabel infoCountLabel;
    private JLabel warningCountLabel;
    private JLabel errorCountLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public HelpBroker getHelpBroker() {
        return mainHelpBroker;
    }

}
