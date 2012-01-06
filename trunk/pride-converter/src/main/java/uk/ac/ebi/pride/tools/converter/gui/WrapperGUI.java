//package uk.ac.ebi.pride.tools.converter.gui;
//
//import org.apache.log4j.Logger;
//import org.jdesktop.swingx.error.ErrorLevel;
//import uk.ac.ebi.picr.client.AccessionMapperInterface;
//import uk.ac.ebi.picr.client.AccessionMapperService;
//import uk.ac.ebi.pride.tools.converter.gui.dialogs.AboutDialog;
//import uk.ac.ebi.pride.tools.converter.gui.forms.ReportForm;
//import uk.ac.ebi.pride.tools.converter.gui.util.error.ErrorDialogHandler;
//import uk.ac.ebi.pride.tools.converter.utils.config.Configurator;
//import uk.ac.ebi.pride.tools.converter.utils.xml.validation.ValidatorFactory;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.beans.PropertyChangeEvent;
//import java.beans.PropertyChangeListener;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.ResourceBundle;
//import java.util.TreeSet;
//import java.util.logging.Level;
//
///**
// * User: melih
// *
// * @author rcote
// *         Date: 16/03/2011
// *         Time: 15:30
// */
//public class WrapperGUI extends JFrame {
//
//    public enum Panels {
//
//        DATATYPE(0),
//        FILESELECT(1),
//        REPORT(2),
//        FILEEXPORT(3),
//        VALIDATION(4);
//
//        private int index;
//
//        Panels(int index) {
//            this.index = index;
//        }
//
//        public int getIndex() {
//            return index;
//        }
//
//    }
//
//    private static final Logger logger = Logger.getLogger(WrapperGUI.class.getName());
//
//    private static WrapperGUI instance = null;
//    private static int index = 0;
//    private ResourceBundle bundle;
//
//    private java.util.List<JPanel> panelList = new LinkedList<JPanel>();
//    private TreeSet<String> databaseNames = new TreeSet<String>();
//
//    //    private DataTypePanel dataTypePanel;
////    private SelectFilePanel selectFilePanel;
////    private ReportFileGui reportFileGui;
////    private ExportFiles exportFiles;
////    private ReportPanel reportPanel;
//    private Component m_prevGlassPane;
//
//    private static boolean isMacOS = false;
//
//
//    public WrapperGUI() {
//        initComponents();
//        bundle = ResourceBundle.getBundle("messages");
//        m_prevGlassPane = getGlassPane();
//        loadPrideXmlValidator();
//        loadDatabaseNames();
//
//    }
//
//    private void loadDatabaseNames() {
//
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    AccessionMapperService service = new AccessionMapperService();
//                    AccessionMapperInterface port = service.getAccessionMapperPort();
//                    java.util.List<String> dbNames = port.getMappedDatabaseNames();
//                    if (dbNames != null) {
//                        databaseNames.addAll(dbNames);
//                    }
//                } catch (Exception e) {
//                    logger.error("load database error: " + e.getMessage(), e);
//                    ErrorDialogHandler.showErrorDialog(instance, Level.SEVERE, "Network error", "Error while retrieving database names from PICR", "WRAPPER-WS", e);
//                }
//            }
//        };
//        new Thread(runnable).start();
//
//    }
//
//    private void loadPrideXmlValidator() {
//
//        final SwingWorker sw = new SwingWorker() {
//            /**
//             * Computes a result, or throws an exception if unable to do so.
//             * Note that this method is executed only once.
//             * Note: this method is executed in a background thread.
//             *
//             * @return the computed result
//             * @throws Exception if unable to compute a result
//             */
//            @Override
//            protected Object doInBackground() throws Exception {
//                Object validator = ValidatorFactory.getInstance().getPrideXmlValidator();
//                logger.info("PRIDE XML Validator Loaded");
//                return validator;
//            }
//        };
//        sw.addPropertyChangeListener(new PropertyChangeListener() {
//            public void propertyChange(PropertyChangeEvent evt) {
//                if (sw.isDone()) {
//                    try {
//                        Object value = sw.get();
//                        if (value == null) {
//                            logger.error("PRIDE XML Validator not loaded!");
//                        }
//                    } catch (Exception e) {
//                        logger.error("validation error: " + e.getMessage(), e);
//                        ErrorDialogHandler.showErrorDialog(instance, ErrorLevel.FATAL, "Error loading PRIDE validator", "An wrror occurred while loading the PRIDE XML Validator Framework", "WRAPPER-VALIDATOR", e);
//                    }
//                }
//
//            }
//        });
//        sw.execute();
//
//    }
//
//    public TreeSet<String> getDatabaseNames() {
//        return databaseNames;
//    }
//
//    public static WrapperGUI instance() {
//        if (instance == null) instance = new WrapperGUI();
//        return instance;
//    }
//
//    public void setWorkingMessage(String message) {
//    }
//
//    private void addPanels() {
//    }
//
//    private void removePanels() {
//        for (Iterator<JPanel> it = panelList.iterator(); it.hasNext(); ) {
//            JPanel panel = it.next();
//            panel.setVisible(false);
//            it.remove();
//        }
//    }
//
//    public void showPanel(int panelIndex) {
//        if (panelIndex > panelList.size()) {
//            throw new IllegalStateException("Invalid Panel selected");
//        }
//        showPanel(panelList.get(panelIndex));
//    }
//
//    public void showPanel(JPanel panel) {
//        // If called directly from main class
//        if (panelList.size() == 0)
//            panelList.add(panel);
//        try {
//            setVisible(true);
//            setContentPane(panel);
//            pack();
//            setTitle("Pride Converter ");
//        } catch (Exception e) {
//            logger.error("show panel error: " + e.getMessage(), e);
//            ErrorDialogHandler.showErrorDialog(instance, ErrorLevel.FATAL, "Unknown Error", e.getMessage(), "WRAPPER", e);
//        }
//    }
//
//    public void start() {
//        instance.showPanel(panelList.get(index));
//        validate();
//        repaint();
//    }
//
//    public void next() {
////        try {
////            final Navigable np = (Navigable) panelList.get(index);
////            if (np.next()) {
////                stopLoading();
////                int t = index + 1;
////                if (t >= panelList.size()) {
////                    System.exit(0);
////                } else {
////                    if (index < panelList.size()) {
////                        instance.showPanel(++index);
////                    } else {
////                        quitGUI();
////                    }
////                }
////            } else {
////                stopLoading();
////            }
////        } catch (Exception e) {
////            stopLoading();
////            logger.error("Wrapper GUI next() error: " + e.getMessage(), e);
////            ErrorDialogHandler.showErrorDialog(instance, ErrorLevel.FATAL, "Unknown Error", e.getMessage(), "WRAPPER", e);
////        }
//    }
//
//    private void quitGUI() {
//        String message = bundle.getString("ReportFileGui.cancel.confirmation");
//        int result = JOptionPane.showConfirmDialog(this, message, "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
//        if (result == 0) System.exit(0);
//    }
//
//    public void back() {
////        if (panelList.size() > 1) {
////            if (index <= 0) {
////                quitGUI();
////            }
////            Navigable np = (Navigable) panelList.get(index);
////            if (index != 0 && np.back()) {
////                instance.showPanel(panelList.get(--index));
////            }
////        } else {
////            //this needs to bappen because the ReportGUI standalone runs within the
////            //franework of the Wrapper GUI - ugly as sin
////            Navigable np = (Navigable) panelList.get(index);
////            np.back();
////        }
//    }
//
//
//    public void help() {
//        //TODO open a help page
//        //for now, show about dialog
//        AboutDialog about = new AboutDialog(this);
//        about.setVisible(true);
//    }
//
//    public void updatePanel(Panels panel, JPanel jpanel) {
//        panelList.set(panel.getIndex(), jpanel);
//    }
//
//    private static void prepeareSystem() {
//
//        System.out.println("\nName of the OS: " + Configurator.getOSName());
//        System.out.println("Version of the OS: " + Configurator.getOSVersion());
//        System.out.println("Architecture of THe OS: " + Configurator.getOSArch());
//        System.out.println("User Home: " + Configurator.getUserHome());
//        if (Configurator.getOSName().toLowerCase().contains("mac")) {
//            isMacOS = true;
//        }
//    }
//
//    public static void main(String[] args) {
//        try {
//            prepeareSystem();
//            UIManager.setLookAndFeel("com.jtattoo.plaf.mint.MintLookAndFeel");
//            final WrapperGUI instance = WrapperGUI.instance();
//            instance.addPanels();
//            instance.start();
//        } catch (Exception e) {
//            System.err.println("Error starting PRIDE Converter GUI:" + e.getMessage());
//            e.printStackTrace();
//            System.exit(255);
//        }
//    }
//
//
//    public static void resetGUI() {
////        //close all open windows
////        Window[] openWindows = instance().getOwnedWindows();
////        if (openWindows != null) {
////            for (Window w : openWindows) {
////                w.setVisible(false);
////                w.dispose();
////            }
////        }
////        boolean reportGuiIsStandalone = instance.panelList.size() == 1;
////        instance.removePanels();
////        ConverterData.reset();
////        index = 0;
////        if (!reportGuiIsStandalone) {
////            instance.addPanels();
////        } else {
////        }
////        instance.start();
//    }
//
//    private void helpActionPerformed(ActionEvent e) {
//        help();
//    }
//
//    private void initComponents() {
//        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
//        // Generated using JFormDesigner non-commercial license
//        ResourceBundle bundle = ResourceBundle.getBundle("messages");
//
//        //======== this ========
//        setTitle(bundle.getString("Desktop.this.title"));
//        setResizable(false);
//        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        setIconImage(new ImageIcon(getClass().getResource("/images/converter-icon.png")).getImage());
//        Container contentPane = getContentPane();
//        contentPane.setLayout(new BorderLayout());
//        pack();
//        // JFormDesigner - End of component initialization  //GEN-END:initComponents
//    }
//
//    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
//    // Generated using JFormDesigner non-commercial license
//    // JFormDesigner - End of variables declaration  //GEN-END:variables
//
//    public void refreshValidationReport() {
//        ((ReportForm) panelList.get(Panels.VALIDATION.getIndex())).refreshValidationReport();
//    }
//
//}
