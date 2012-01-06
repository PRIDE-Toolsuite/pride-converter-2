/*
 * Created by JFormDesigner on Wed Aug 03 16:05:54 BST 2011
 */

package uk.ac.ebi.pride.tools.converter.gui;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.error.ErrorLevel;
import uk.ac.ebi.pride.tools.converter.gui.component.filefilters.PrideFileFilter;
import uk.ac.ebi.pride.tools.converter.gui.component.panels.FilterPanel;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.gui.util.error.ErrorDialogHandler;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.filter.io.PrideXmlFilter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;

/**
 * @author User #3
 */
public class FilterGUI extends JFrame {

    private static final Logger logger = Logger.getLogger(FilterGUI.class);
    private static final int STATUS_ERROR = 1;
    private static final int STATUS_OK = 0;

    private FilterGUI localInstance;

    public FilterGUI() {
        initComponents();
        localInstance = this;
    }

    private void selectPathButtonActionPerformed(ActionEvent e) {

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileFilter(new PrideFileFilter());
        if (ConverterData.getInstance().getLastSelectedDirectory() != null) {
            chooser.setCurrentDirectory(new File(ConverterData.getInstance().getLastSelectedDirectory()));
        }

        int result = chooser.showOpenDialog(this);

        File file = null;
        switch (result) {
            case JFileChooser.APPROVE_OPTION:
                file = chooser.getSelectedFile();
                ConverterData.getInstance().setLastSelectedDirectory(file.getParentFile().getAbsolutePath());
            case JFileChooser.CANCEL_OPTION:
                break;
            case JFileChooser.ERROR_OPTION:
                break;
        }
        if (file != null) {
            inputFile.setText(file.getAbsolutePath());
        }

    }

    private void updateCursor(Cursor c) {
        this.setCursor(c);
    }

    private void filterButtonActionPerformed() {

        if (filterPanel1.isFilterXml()) {

            //check to see if file exists
            File infile = new File(inputFile.getText());
            if (infile.exists()) {

                updateCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                startFiltering();

            } else {
                //show error dialog
                ErrorDialogHandler.showErrorDialog(this, Level.WARNING, "Error while filtering XML file", "File to filter not found:\n" + inputFile.getText(), "FILTER", null);
            }


        } else {
            JOptionPane.showMessageDialog(this, "No filters selected!", "Warning", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    private void startFiltering() {

        SwingWorker sw = new SwingWorker() {
            /**
             * Computes a result, or throws an exception if unable to do so.
             * <p/>
             * <p/>
             * Note that this method is executed only once.
             * <p/>
             * <p/>
             * Note: this method is executed in a background thread.
             *
             * @return the computed result
             * @throws Exception if unable to compute a result
             */
            @Override
            protected Object doInBackground() throws Exception {
                try {

                    File infile = new File(inputFile.getText());
                    //get filter from options
                    PrideXmlFilter filter = filterPanel1.getFilter(infile.getAbsolutePath(), infile.getAbsolutePath());

                    //warn user
                    message.setText("Filtering " + infile.getName());

                    try {

                        //run filter
                        filter.writeXml();
                        message.setText("Done!");
                        updateCursor(Cursor.getDefaultCursor());

                    } catch (ConverterException e) {
                        updateCursor(Cursor.getDefaultCursor());
                        logger.error("Error while filtering: " + e.getMessage(), e);
                        //show error message
                        ErrorDialogHandler.showErrorDialog(panel1, ErrorLevel.FATAL, "Error while filtering XML file", e.getMessage(), "FILTER", e);
                    }

                } catch (Exception e) {
                    updateCursor(Cursor.getDefaultCursor());
                    message.setText("Error: " + e.getMessage());
                    ErrorDialogHandler.showErrorDialog(panel1, ErrorLevel.FATAL, "Error while filtering XML file", e.getMessage(), "FILTER", e);
                }
                return true;
            }
        };
        sw.execute();

    }

    private void exitButtonActionPerformed() {
        System.exit(STATUS_OK);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        panel1 = new JPanel();
        inputFile = new JTextField();
        selectPathButton = new JButton();
        panel2 = new JPanel();
        filterButton = new JButton();
        exitButton = new JButton();
        panel3 = new JPanel();
        message = new JTextField();
        filterPanel1 = new FilterPanel();

        //======== this ========
        setTitle("Pride Filter");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        Container contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setBorder(new TitledBorder("Select Input File"));

            //---- selectPathButton ----
            selectPathButton.setText("Select");
            selectPathButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectPathButtonActionPerformed(e);
                }
            });

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                    panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(inputFile, GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(selectPathButton)
                                    .addGap(35, 35, 35))
            );
            panel1Layout.setVerticalGroup(
                    panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                    .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(inputFile, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(selectPathButton))
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        //======== panel2 ========
        {
            panel2.setLayout(new FlowLayout());

            //---- filterButton ----
            filterButton.setText("Filter");
            filterButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    filterButtonActionPerformed();
                }
            });
            panel2.add(filterButton);

            //---- exitButton ----
            exitButton.setText("Exit");
            exitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    exitButtonActionPerformed();
                }
            });
            panel2.add(exitButton);
        }

        //======== panel3 ========
        {
            panel3.setBorder(new TitledBorder("Status Message"));

            //---- message ----
            message.setEditable(false);

            GroupLayout panel3Layout = new GroupLayout(panel3);
            panel3.setLayout(panel3Layout);
            panel3Layout.setHorizontalGroup(
                    panel3Layout.createParallelGroup()
                            .addComponent(message, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 463, Short.MAX_VALUE)
            );
            panel3Layout.setVerticalGroup(
                    panel3Layout.createParallelGroup()
                            .addGroup(panel3Layout.createSequentialGroup()
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(message, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(panel3, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(GroupLayout.Alignment.LEADING, contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                .addComponent(panel2, GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
                                                .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(filterPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addContainerGap(7, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panel1, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(filterPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(panel3, GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(panel2, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        pack();
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel panel1;
    private JTextField inputFile;
    private JButton selectPathButton;
    private JPanel panel2;
    private JButton filterButton;
    private JButton exitButton;
    private JPanel panel3;
    private JTextField message;
    private FilterPanel filterPanel1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.mint.MintLookAndFeel");
            FilterGUI filterGUI = new FilterGUI();
            filterGUI.setVisible(true);
        } catch (Exception e) {
            logger.fatal(e.getMessage(), e);
            System.exit(STATUS_ERROR);
        }

    }
}
