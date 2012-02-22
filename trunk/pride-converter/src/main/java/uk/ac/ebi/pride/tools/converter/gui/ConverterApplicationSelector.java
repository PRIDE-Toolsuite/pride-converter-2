package uk.ac.ebi.pride.tools.converter.gui;

import java.awt.event.*;
import uk.ac.ebi.pride.tools.converter.conversion.PrideConverter;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.ImageDialog;
import uk.ac.ebi.pride.tools.converter.gui.forms.*;
import uk.ac.ebi.pride.tools.converter.gui.model.OutputFormat;
import uk.ac.ebi.pride.tools.filter.PrideFilter;
import uk.ac.ebi.pride.tools.merger.PrideMerger;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 18/08/11
 * Time: 09:26
 */
public class ConverterApplicationSelector extends JFrame {

    private static ConverterApplicationSelector selector = null;

    private enum ConverterProperties {

        JAVAHOME("java.home"),
        JVMARGS("jvm.args"),
        PROXYUSER("http.proxyUser"),
        PROXYPASSWORD("http.proxyPassword"),
        PROXYSET("http.proxySet"),
        PROXYHOST("http.proxyHost"),
        PROXYPORT("http.proxyPort");

        private String value;

        private ConverterProperties(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public ConverterApplicationSelector() {
        initComponents();
        SimpleAttributeSet attribs = new SimpleAttributeSet();
        StyleConstants.setFontSize(attribs, 10);
        StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
        textPane1.setParagraphAttributes(attribs,true);
        textPane2.setParagraphAttributes(attribs,true);
        textPane3.setParagraphAttributes(attribs,true);
        textPane4.setParagraphAttributes(attribs,true);
        textPane5.setParagraphAttributes(attribs,true);
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            try {
                UIManager.setLookAndFeel("com.jtattoo.plaf.mint.MintLookAndFeel");
                selector = new ConverterApplicationSelector();
                selector.pack();
                selector.setVisible(true);
            } catch (Exception e) {
                System.exit(1);
            }
        } else {
            // launch the CLI version
            String mode = args[0];

            // copy all arguments apart from the first one (the mode)
            // to the newArgs array.
            String newArgs[] = new String[args.length - 1];
            for (int i = 1; i < args.length; i++)
                newArgs[i - 1] = args[i];

            if ("-converter".equals(mode))
                PrideConverter.main(newArgs);
            else if ("-filter".equals(mode))
                PrideFilter.main(newArgs);
            else if ("-merger".equals(mode))
                PrideMerger.main(newArgs);
            else {
                System.out.println("Usage: java -jar pride_converter.jar [mode]\n");
                System.out.println("PRIDE Converter 2");
                System.out.println("PRIDE Converter can operate in several different modes.");
                System.out.println("To launch the graphical mode simply specify no parameters.\n");
                System.out.println("The following modes are supported by PRIDE Converter:");
                System.out.println("   -converter    Launches the PRIDE Converter in convert mode.");
                System.out.println("                 This mode allows one to convert several search");
                System.out.println("                 engine result files into the PRIDE XML format.");
                System.out.println("   -filter       Launches the PRIDE Converter in filter mode.");
                System.out.println("                 This mode allows one to remove f.e. unidentified");
                System.out.println("                 spectra or a given set of proteins from an existing");
                System.out.println("                 PRIDE XML file.");
                System.out.println("   -merger       Launches PRIDE Converter in merger mode.");
                System.out.println("                 This mode allows one to merge several PRIDE XML files");
                System.out.println("                 into a single file.\n");
                System.out.println("For mode specific help please use java -jar pride_converter.jar [mode] -help");
            }
        }

    }

    private void launchTool(ActionEvent e) {

        selector.setVisible(false);
        selector.dispose();

        //read user properties
        Properties properties = readUserProperties();
        //launch new process
        String mainClass = "";

        if (e.getSource().equals(converterButton)) {
            mainClass = ConverterLauncher.class.getName();
        } else if (e.getSource().equals(mergerButton)) {
            mainClass = MergerLauncher.class.getName();
        } else if (e.getSource().equals(filterButton)) {
            mainClass = FilterLauncher.class.getName();
        } else if (e.getSource().equals(mzTabButton)) {
            mainClass = MzTabLauncher.class.getName();
        } else {
            throw new IllegalArgumentException("No launch method defined for button: " + e.getSource());
        }

        //bootstrap process
        String javaHome = properties.getProperty(ConverterProperties.JAVAHOME.getValue());
        String jvmArgs = properties.getProperty(ConverterProperties.JVMARGS.getValue());
        String proxyHost = properties.getProperty(ConverterProperties.PROXYHOST.getValue());
        String proxyPort = properties.getProperty(ConverterProperties.PROXYPORT.getValue());
        String proxySet = properties.getProperty(ConverterProperties.PROXYSET.getValue());
        String proxyUser = properties.getProperty(ConverterProperties.PROXYUSER.getValue());
        String proxyPassword = properties.getProperty(ConverterProperties.PROXYPASSWORD.getValue());

        // create the command
        StringBuilder cmdBuffer = new StringBuilder();
        if (javaHome != null && !"".equals(javaHome.trim())) {
            cmdBuffer.append(javaHome.trim()).append(File.separatorChar);
        }
        cmdBuffer.append("java");

        if (isWindowsPlatform()) {
            cmdBuffer.append(".exe ");
        }

        cmdBuffer.append(" -cp ");
        if (isWindowsPlatform()) {
            cmdBuffer.append("\"");
        }
        //classpath
        cmdBuffer.append(System.getProperty("java.class.path"));
        if (isWindowsPlatform()) {
            cmdBuffer.append("\"");
        }

        //memory settings
        if (jvmArgs != null) {
            cmdBuffer.append(" ").append(jvmArgs);
        }

        //proxy settings
        if (proxyHost != null) {
            cmdBuffer.append(" -D").append(ConverterProperties.PROXYHOST.getValue()).append("=").append(proxyHost);
        }
        if (proxyPort != null) {
            cmdBuffer.append(" -D").append(ConverterProperties.PROXYPORT.getValue()).append("=").append(proxyPort);
        }
        if (proxySet != null) {
            cmdBuffer.append(" -D").append(ConverterProperties.PROXYSET.getValue()).append("=").append(proxySet);
        }
        if (proxyUser != null) {
            cmdBuffer.append(" -D").append(ConverterProperties.PROXYUSER.getValue()).append("=").append(proxyUser);
        }
        if (proxyPassword != null) {
            cmdBuffer.append(" -D").append(ConverterProperties.PROXYPASSWORD.getValue()).append("=").append(proxyPassword);
        }

        cmdBuffer.append(" ").append(mainClass);

        // call the command
        Process process;
        try {
            process = Runtime.getRuntime().exec(cmdBuffer.toString());
            StreamProxy errorStreamProxy = new StreamProxy(process.getErrorStream(), System.err);
            StreamProxy outStreamProxy = new StreamProxy(process.getInputStream(), System.out);
            errorStreamProxy.start();
            outStreamProxy.start();
        } catch (IOException ioe) {
            System.err.println("Error while bootstrapping the PRIDE Converter");
            ioe.printStackTrace();
            System.exit(1);
        }
    }

    private Properties readUserProperties() {

        Properties properties = new Properties();
        try {
            //check to see if there is a file called converter properties in the same directory
            //as the current executable
            File currentDir = new File(".");
            File propFile = new File(currentDir, "converter.properties");
            if (propFile.exists()) {
                System.out.println("Reading properties file: " + propFile.getAbsolutePath());
                properties.load(new FileReader(propFile));
            }
        } catch (IOException e) {
            System.err.println("Error reading properties file: " + e.getMessage());
        }
        return properties;

    }

    private void exitButtonActionPerformed() {
        System.exit(0);
    }

    private void label1MouseClicked(MouseEvent e) {
        if (e.getClickCount() == 3){
            ImageDialog about = new ImageDialog(this);
            about.setVisible(true);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        label2 = new JLabel();
        label1 = new JLabel();
        mzTabButton = new JButton();
        filterButton = new JButton();
        exitButton = new JButton();
        converterButton = new JButton();
        textPane1 = new JTextPane();
        textPane2 = new JTextPane();
        textPane3 = new JTextPane();
        mergerButton = new JButton();
        textPane4 = new JTextPane();
        textPane5 = new JTextPane();

        //======== this ========
        setResizable(false);
        setTitle("PRIDE Converter Toolsuite");
        Container contentPane = getContentPane();

        //---- label2 ----
        label2.setText("PRIDE Converter Toolsuite");
        label2.setFont(new Font("Dialog", Font.BOLD, 18));

        //---- label1 ----
        label1.setIcon(new ImageIcon(getClass().getResource("/images/workflow.png")));
        label1.setHorizontalAlignment(SwingConstants.CENTER);
        label1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                label1MouseClicked(e);
            }
        });

        //---- mzTabButton ----
        mzTabButton.setText("Launch PRIDE mzTab Generator");
        mzTabButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchTool(e);
            }
        });

        //---- filterButton ----
        filterButton.setText("Launch PRIDE Filter");
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchTool(e);
            }
        });

        //---- exitButton ----
        exitButton.setText("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitButtonActionPerformed();
            }
        });

        //---- converterButton ----
        converterButton.setText("Launch PRIDE Converter");
        converterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchTool(e);
            }
        });

        //---- textPane1 ----
        textPane1.setBackground(UIManager.getColor("Button.background"));
        textPane1.setFont(new Font("Dialog", Font.PLAIN, 10));
        textPane1.setText("Converts spectra and identifications to PRIDE XML.");

        //---- textPane2 ----
        textPane2.setBackground(UIManager.getColor("Button.background"));
        textPane2.setFont(new Font("Dialog", Font.PLAIN, 10));
        textPane2.setText("Generates skeleton mzTab files that need further editing to add quantitation and/or 2D gel information.");

        //---- textPane3 ----
        textPane3.setBackground(UIManager.getColor("Button.background"));
        textPane3.setText("Combines multiple PRIDE XML files into one single PRIDE XML file, while maintaing the links between peptides and spectra.");
        textPane3.setFont(new Font("Dialog", Font.PLAIN, 10));

        //---- mergerButton ----
        mergerButton.setText("Launch PRIDE Merger");
        mergerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchTool(e);
            }
        });

        //---- textPane4 ----
        textPane4.setBackground(UIManager.getColor("Button.background"));
        textPane4.setFont(new Font("Dialog", Font.PLAIN, 10));
        textPane4.setText("Filters PRIDE XML based on specific identification-, peptide- and/or spectrum-based criteria.");

        //---- textPane5 ----
        textPane5.setBackground(UIManager.getColor("Button.background"));
        textPane5.setFont(new Font("Dialog", Font.PLAIN, 10));
        textPane5.setText("Quit the tool suite");

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                    .addComponent(label2, GroupLayout.PREFERRED_SIZE, 626, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label1, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 632, GroupLayout.PREFERRED_SIZE))
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(textPane3, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                                        .addComponent(textPane2, GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                                        .addComponent(textPane1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                                        .addComponent(textPane4, GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(filterButton, GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                                        .addComponent(mergerButton, GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                                        .addComponent(converterButton, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                                        .addComponent(mzTabButton, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE))))
                            .addGap(8, 8, 8))
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addComponent(textPane5, GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(exitButton, GroupLayout.PREFERRED_SIZE, 259, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap())))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(label2)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(label1)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(textPane1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(converterButton, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(textPane2)
                        .addComponent(mzTabButton, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(mergerButton, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                        .addComponent(textPane3))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(filterButton, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                        .addComponent(textPane4))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(textPane5)
                        .addComponent(exitButton, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE))
                    .addContainerGap(13, Short.MAX_VALUE))
        );
        setSize(660, 570);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel label2;
    private JLabel label1;
    private JButton mzTabButton;
    private JButton filterButton;
    private JButton exitButton;
    private JButton converterButton;
    private JTextPane textPane1;
    private JTextPane textPane2;
    private JTextPane textPane3;
    private JButton mergerButton;
    private JTextPane textPane4;
    private JTextPane textPane5;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    static class ConverterLauncher {

        public static void main(String[] args) {
            NavigationPanel panel = NavigationPanel.getInstance();
            panel.registerForm(new DataTypeForm());
            panel.registerForm(new FileSelectionForm(OutputFormat.PRIDE_XML));
            panel.registerForm(new ExperimentDetailForm());
            panel.registerForm(new ContactForm());
            panel.registerForm(new ReferenceForm());
            panel.registerForm(new SampleForm());
            panel.registerForm(new ProtocolForm());
            panel.registerForm(new InstrumentForm());
            panel.registerForm(new SoftwareProcessingForm());
            panel.registerForm(new DatabaseMappingForm());
            panel.registerForm(new PTMForm());
            panel.registerForm(new AnnotationDoneForm());
            panel.registerForm(new FileExportForm(false));
            panel.registerForm(new ReportForm());
            panel.setExitToApplicationSelector(true);
            panel.reset();
        }

    }

    static class MzTabLauncher {

        public static void main(String[] args) {
            NavigationPanel panel = NavigationPanel.getInstance();
            DataTypeForm form = new DataTypeForm();
            form.setSpectrumOnlyFormatsEnabled(false);
            panel.registerForm(form);
            panel.registerForm(new FileSelectionForm(OutputFormat.MZTAB));
            panel.registerForm(new MzTabReportForm());
            panel.setExitToApplicationSelector(true);
            panel.reset();
        }

    }

    static class MergerLauncher {

        public static void main(String[] args) {
            NavigationPanel panel = NavigationPanel.getInstance();
            panel.registerForm(new MergerInformationForm());
            panel.registerForm(new FileSelectionForm(OutputFormat.PRIDE_MERGED_XML));
            panel.registerForm(new MergerReportForm());
            panel.setExitToApplicationSelector(true);
            panel.reset();
        }

    }

    static class FilterLauncher {

        public static void main(String[] args) {
            NavigationPanel panel = NavigationPanel.getInstance();
            panel.registerForm(new FilterInformationForm());
            panel.registerForm(new FileSelectionForm(OutputFormat.PRIDE_FILTERED_XML));
            panel.registerForm(new FileExportForm(true));
            panel.registerForm(new FilterReportForm());
            panel.setExitToApplicationSelector(true);
            panel.reset();
        }

    }

    /**
     * Check whether it is Windows platform
     *
     * @return boolean  true means it is running on windows
     */
    private boolean isWindowsPlatform() {
        String osName = System.getProperty("os.name");
        return osName.startsWith("Windows");
    }

    /**
     * StreamProxy redirect the output stream and error stream to screen.
     */
    private static class StreamProxy extends Thread {
        final InputStream is;
        final PrintStream os;

        StreamProxy(InputStream is, PrintStream os) {
            this.is = is;
            this.os = os;
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    os.println(line);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
    }

}
