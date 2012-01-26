package uk.ac.ebi.pride.tools.converter.gui;

import uk.ac.ebi.pride.tools.converter.conversion.PrideConverter;
import uk.ac.ebi.pride.tools.converter.gui.forms.*;
import uk.ac.ebi.pride.tools.converter.gui.model.OutputFormat;

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

            //todo
            //-converter pass args[1..n]
            //-filter pass args[1..n]
            //-merger pass args[1..n]

            PrideConverter.main(args);
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
            //
        } else if (e.getSource().equals(filterButton)) {
            mainClass = FilterGUI.class.getCanonicalName();
        } else if (e.getSource().equals(mzTabButton)) {
            mainClass = MzTabLauncher.class.getName();
        } else {
            throw new IllegalArgumentException("No launch method defined for button: " + e.getSource());
        }

        //bootstrap process
        String jvmArgs = properties.getProperty(ConverterProperties.JVMARGS.getValue());
        String proxyHost = properties.getProperty(ConverterProperties.PROXYHOST.getValue());
        String proxyPort = properties.getProperty(ConverterProperties.PROXYPORT.getValue());
        String proxySet = properties.getProperty(ConverterProperties.PROXYSET.getValue());
        String proxyUser = properties.getProperty(ConverterProperties.PROXYUSER.getValue());
        String proxyPassword = properties.getProperty(ConverterProperties.PROXYPASSWORD.getValue());

        // create the command
        StringBuilder cmdBuffer = new StringBuilder();
        cmdBuffer.append("java -cp ");
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
        mzTabButton.setEnabled(false);
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
        mergerButton.setEnabled(false);
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
            panel.registerForm(new FileExportForm());
            panel.registerForm(new ReportForm());
            panel.reset();
        }

    }

    static class MzTabLauncher {

        public static void main(String[] args) {
            NavigationPanel panel = NavigationPanel.getInstance();
            panel.registerForm(new DataTypeForm());
            panel.registerForm(new FileSelectionForm(OutputFormat.MZTAB));
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
