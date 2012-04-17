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
import uk.ac.ebi.pride.tools.converter.gui.component.list.IconListCellRenderer;
import uk.ac.ebi.pride.tools.converter.gui.component.list.IconListModel;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.ProgressDialog;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.TabbedValidationDialog;
import uk.ac.ebi.pride.tools.converter.gui.interfaces.ConverterForm;
import uk.ac.ebi.pride.tools.converter.gui.interfaces.ValidationListener;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.gui.model.GUIException;
import uk.ac.ebi.pride.tools.converter.gui.util.PreferenceManager;
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
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * @author User #3
 */
public class NavigationPanel extends JFrame implements ValidationListener, WindowListener {

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
    private ProgressDialog progressDialog = null;
    private boolean exitToApplicationSelector = false;

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

        initComponents();

        //update the jlist selection model so that only the system-selected value is highlighted
        panelList.setCellRenderer(new IconListCellRenderer());
        panelList.setModel(new IconListModel());
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
        TemplateUtilities.initTemplates();
        initValidation();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                //write user preference file
                PreferenceManager.getInstance().writePreferencesToFile();
                System.out.println("Stored user preferences.");
            }
        });

        //this is to ensure that the navigation window always asks to confirm
        //before exiting
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(this);

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
                            ErrorDialogHandler.showErrorDialog(instance, ErrorLevel.FATAL, "Error executing background job", "An error occurred while processing the conversion", "NAVIGATOR_PANEL", value);
                        }
                    } catch (Exception e) {
                        logger.error("Execution error: " + e.getMessage(), e);
                        ErrorDialogHandler.showErrorDialog(instance, ErrorLevel.FATAL, "Error executing background job", "An error occurred while processing the conversion", "NAVIGATOR_PANEL", e);
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
        ((IconListModel) panelList.getModel()).addElement(form.getFormName(), form.getFormIcon());
    }

    /**
     * Remove a form to the main navigation panel. It is assumed that all ConverterForms implementations are JPanels
     *
     * @param form
     */
    public void deregisterForm(ConverterForm form) {
        if (!(form instanceof JPanel)) {
            throw new IllegalArgumentException("All ConverterForm implementations must be JPanels");
        }
        //add to card layout
        contentPanel.remove((JPanel) form);
        formNames.remove(form.getFormName());
        forms.remove(form);

        //add to panel list display
        ((IconListModel) panelList.getModel()).removeElement(form.getFormName(), form.getFormIcon());
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

        //check to ensure that we're not registering the same form twice!
        if (index + 1 <= forms.size()) {
            if (forms.get(index + 1).getClass().equals(formToRegister.getClass())) {
                logger.info(formToRegister.getFormName() + " already registered. Ignoring.");
                return;
            }
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

        ((IconListModel) panelList.getModel()).add(index + 1, formToRegister.getFormName(), formToRegister.getFormIcon());
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
                                    formNameLabel.setText(forms.get(selectedIndex).getFormName());

                                    //update navigation list
                                    panelList.clearSelection();
                                    panelList.revalidate();
                                    panelList.repaint();
                                    panelList.setSelectedIndex(selectedIndex);

                                    //update help button action
                                    CSH.setHelpIDString(helpButton, forms.get(selectedIndex).getHelpResource());
                                    helpButton.addActionListener(new CSH.DisplayHelpFromSource(mainHelpBroker));

                                    //check to see if we're on the last form!

                                    if (selectedIndex == forms.size() - 1) {
                                        //we're at the last form
                                        nextButton.setText("Finish");
                                        for (ActionListener al : nextButton.getActionListeners()) {
                                            nextButton.removeActionListener(al);
                                        }
                                        nextButton.addActionListener(new ActionListener() {
                                            @Override
                                            public void actionPerformed(ActionEvent e) {
                                                quitNoPrompt();
                                            }
                                        });
                                        nextButton.setEnabled(true);
                                        nextButton.revalidate();
                                        nextButton.repaint();
                                    }

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

            //update description
            panelDescription.setText(forms.get(selectedIndex).getFormDescription());
            formNameLabel.setText(forms.get(selectedIndex).getFormName());

            //update help button action
            CSH.setHelpIDString(helpButton, forms.get(selectedIndex).getHelpResource());
            helpButton.addActionListener(new CSH.DisplayHelpFromSource(mainHelpBroker));

            //if we can go back, we should be able to go straight forward again
            //if nothing changes in the form
            fireValidationListener(true);

            //if we were on the last form before but went back,
            //we now want to go forward again
            if ("Finish".equals(nextButton.getText())) {
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
        } else {
            quitNoPrompt();
        }
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
            if (exitToApplicationSelector) {
                setVisible(false);
                ConverterApplicationSelector.main(new String[]{});
                dispose();
            } else {
                System.exit(0);
            }
        }
    }

    /**
     * quit the application
     */
    private void quitNoPrompt() {
        if (exitToApplicationSelector) {
            setVisible(false);
            ConverterApplicationSelector.main(new String[]{});
            dispose();
        } else {
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

        //the next button will be reenabled by fireValidationListener(true)
        nextButton.setEnabled(false);

        //set description
        panelDescription.setText(forms.get(0).getFormDescription());
        formNameLabel.setText(forms.get(0).getFormName());

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
        //make sure it's visible
        validationStatus.setVisible(true);
        if (errorMessageCount > 0) {
            validationStatus.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/error.png")));
            validationStatus.repaint();
        } else if (warningMessageCount > 0) {
            validationStatus.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/warning.png")));
            validationStatus.repaint();
        } else if (infoMessageCount > 0) {
            validationStatus.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/information.png")));
            validationStatus.repaint();
        } else {
            validationStatus.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/ok.png")));
            validationStatus.repaint();
        }
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

    public void hideValidationIcon() {
        validationStatus.setVisible(false);
    }

    @Override
    public void fireValidationListener(boolean isValid) {
        nextButton.setEnabled(isValid);
    }

    public void setWorkingMessage(String message) {
        if (progressDialog != null) {
            progressDialog.setMessage(message);
        } else {
            System.err.println("MESSAGE NOT SHOWN: " + message);
        }
    }

    private void validationStatusMouseClicked() {
        showValidationMessages(null);
    }

    private void validationStatusMouseEntered() {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void validationStatusMouseExited() {
        setCursor(Cursor.getDefaultCursor());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        panel1 = new JPanel();
        scrollPane1 = new JScrollPane();
        panelList = new JList(new DefaultListModel());
        scrollPane2 = new JScrollPane();
        panelDescription = new JTextArea();
        label1 = new JLabel();
        contentPanel = new JPanel();
        panel4 = new JPanel();
        nextButton = new JButton();
        backButton = new JButton();
        quitButton = new JButton();
        validationStatus = new JLabel();
        helpButton = new JButton();
        formNameLabel = new JLabel();

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

            //---- label1 ----
            label1.setText("Progress");
            label1.setFont(new Font("Dialog", Font.BOLD, 20));

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                    panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(scrollPane1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                                            .addGroup(panel1Layout.createParallelGroup()
                                                    .addComponent(scrollPane2, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 187, GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(label1)))
                                    .addContainerGap())
            );
            panel1Layout.setVerticalGroup(
                    panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(label1)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 325, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
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

            //---- quitButton ----
            quitButton.setText("Quit");
            quitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    quit();
                }
            });

            //---- validationStatus ----
            validationStatus.setIcon(new ImageIcon(getClass().getResource("/images/ok.png")));
            validationStatus.setHorizontalAlignment(SwingConstants.CENTER);
            validationStatus.setToolTipText("Click to view validation messages");
            validationStatus.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    validationStatusMouseClicked();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    validationStatusMouseEntered();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    validationStatusMouseExited();
                }
            });

            GroupLayout panel4Layout = new GroupLayout(panel4);
            panel4.setLayout(panel4Layout);
            panel4Layout.setHorizontalGroup(
                    panel4Layout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, panel4Layout.createSequentialGroup()
                                    .addContainerGap(479, Short.MAX_VALUE)
                                    .addComponent(quitButton)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(backButton)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(nextButton)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(validationStatus, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                                    .addGap(4, 4, 4))
            );
            panel4Layout.setVerticalGroup(
                    panel4Layout.createParallelGroup()
                            .addGroup(panel4Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(panel4Layout.createParallelGroup()
                                            .addComponent(validationStatus, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                                            .addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(nextButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(backButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(quitButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        //---- helpButton ----
        helpButton.setIcon(new ImageIcon(getClass().getResource("/images/help.png")));
        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                help();
            }
        });

        //---- formNameLabel ----
        formNameLabel.setText("text");
        formNameLabel.setFont(new Font("Dialog", Font.BOLD, 20));

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
                                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                                .addComponent(formNameLabel, GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE)
                                                .addGap(18, 18, 18)
                                                .addComponent(helpButton)))
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(helpButton)
                                                        .addComponent(formNameLabel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(contentPanel, GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
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
    private JLabel label1;
    private JPanel contentPanel;
    private JPanel panel4;
    private JButton nextButton;
    private JButton backButton;
    private JButton quitButton;
    private JLabel validationStatus;
    private JButton helpButton;
    private JLabel formNameLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public HelpBroker getHelpBroker() {
        return mainHelpBroker;
    }

    public void setExitToApplicationSelector(boolean exitToApplicationSelector) {
        this.exitToApplicationSelector = exitToApplicationSelector;
    }

    public int getErrorMessageCount() {
        return errorMessageCount;
    }

    public int getWarningMessageCount() {
        return warningMessageCount;
    }

    public int getInfoMessageCount() {
        return infoMessageCount;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    //////////////////////////////////////////////////////////////// WindowListener Interface
    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        quit();
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

}
