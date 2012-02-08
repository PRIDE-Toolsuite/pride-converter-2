package uk.ac.ebi.pride.tools.converter.gui;

import uk.ac.ebi.pride.tools.converter.conversion.PrideConverter;
import uk.ac.ebi.pride.tools.converter.gui.forms.*;
import uk.ac.ebi.pride.tools.converter.gui.model.OutputFormat;
import uk.ac.ebi.pride.tools.filter.PrideFilter;
import uk.ac.ebi.pride.tools.merger.PrideMerger;

import javax.swing.*;
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

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        converterButton = new JButton();
        mzTabButton = new JButton();
        filterButton = new JButton();
        mergerButton = new JButton();
        exitButton = new JButton();

        //======== this ========
        Container contentPane = getContentPane();

        //---- converterButton ----
        converterButton.setText("Launch PRIDE Converter");
        converterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchTool(e);
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

        //---- mergerButton ----
        mergerButton.setText("Launch PRIDE Merger");
        mergerButton.addActionListener(new ActionListener() {
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

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(converterButton, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                                        .addComponent(mergerButton, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                                        .addComponent(filterButton, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                                        .addComponent(mzTabButton, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                                        .addComponent(exitButton, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE))
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(converterButton, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mzTabButton, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mergerButton, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(filterButton, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(exitButton, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JButton converterButton;
    private JButton mzTabButton;
    private JButton filterButton;
    private JButton mergerButton;
    private JButton exitButton;
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
            panel.reset();
        }

    }

    static class MergerLauncher {

        public static void main(String[] args) {
            NavigationPanel panel = NavigationPanel.getInstance();
            panel.registerForm(new MergerInformationForm());
            panel.registerForm(new FileSelectionForm(OutputFormat.PRIDE_MERGED_XML));
            panel.registerForm(new MergerReportForm());
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
