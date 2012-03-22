/*
 * Created by JFormDesigner on Wed Aug 03 15:26:12 BST 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.component.panels;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.filter.io.PrideXmlFilter;
import uk.ac.ebi.pride.tools.filter.model.impl.AccessionBlacklistFilter;
import uk.ac.ebi.pride.tools.filter.model.impl.AccessionWhitelistFilter;
import uk.ac.ebi.pride.tools.filter.model.impl.EmptySpectrumFilter;
import uk.ac.ebi.pride.tools.filter.model.impl.MinimumPeptideCountFilter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;

/**
 * @author User #3
 */
public class FilterPanel extends JPanel {

    private static final Logger logger = Logger.getLogger(FilterPanel.class);

    boolean filterXml = false;

    public FilterPanel() {
        initComponents();
        if (ConverterData.getInstance().getLastSelectedDirectory() != null) {
            pathField.setText(ConverterData.getInstance().getLastSelectedDirectory());
        } else {
            pathField.setText(ConverterData.DEFAULT_OUTPUT_LOCATION);
        }
    }

    //make sure the information is correct when the panel is displayed
    public void paintComponent(Graphics g) {
        if (ConverterData.getInstance().getLastSelectedDirectory() != null) {
            pathField.setText(ConverterData.getInstance().getLastSelectedDirectory());
        } else {
            pathField.setText(ConverterData.DEFAULT_OUTPUT_LOCATION);
        }
    }

    private void selectPathButtonActionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        chooser.setCurrentDirectory(new File(pathField.getText()));

        int result = chooser.showOpenDialog(this);

        File file;
        switch (result) {
            case JFileChooser.APPROVE_OPTION:
                file = chooser.getSelectedFile();
                ConverterData.getInstance().setLastSelectedDirectory(file.getAbsolutePath());
            case JFileChooser.CANCEL_OPTION:
                break;
            case JFileChooser.ERROR_OPTION:
                break;
        }
        pathField.setText(ConverterData.getInstance().getLastSelectedDirectory());
    }

    private void enableFiltering() {
        filterXml = true;
    }

    private void blacklistButtonActionPerformed() {
        selectIdentificationFilterFile(blacklistTextfield, blacklistCheckbox);
    }

    private void whitelistButtonActionPerformed() {
        selectIdentificationFilterFile(whitelistTextfield, whitelistCheckbox);
    }

    private void selectIdentificationFilterFile(JTextField textfield, JCheckBox checkbox) {

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        if (ConverterData.getInstance().getLastSelectedDirectory() != null) {
            chooser.setCurrentDirectory(new File(ConverterData.getInstance().getLastSelectedDirectory()));
        }

        int result = chooser.showOpenDialog(this);

        File file;
        switch (result) {
            case JFileChooser.APPROVE_OPTION:
                file = chooser.getSelectedFile();
                textfield.setText(file.getAbsolutePath());
                checkbox.setSelected(true);
                ConverterData.getInstance().setLastSelectedDirectory(file.getParentFile().getAbsolutePath());
            case JFileChooser.CANCEL_OPTION:
                break;
            case JFileChooser.ERROR_OPTION:
                break;
        }


    }

    private void blacklistCheckboxActionPerformed() {
        if (blacklistCheckbox.isSelected()) {
            enableFiltering();
            selectIdentificationFilterFile(blacklistTextfield, blacklistCheckbox);
        } else {
            blacklistTextfield.setText(null);
        }
    }

    private void whitelistCheckboxActionPerformed() {
        if (whitelistCheckbox.isSelected()) {
            enableFiltering();
            selectIdentificationFilterFile(whitelistTextfield, whitelistCheckbox);
        } else {
            whitelistTextfield.setText(null);
        }

    }

    private void nbPeptideFilterTextFieldFocusLost() {
        if (nbPeptideFilterTextField.getText().trim().length() > 0) {
            filterIdentsWIthNbPepBox.setSelected(true);
        } else {
            filterIdentsWIthNbPepBox.setSelected(false);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        panel1 = new JPanel();
        pathField = new JTextField();
        selectPathButton = new JButton();
        gzipCheckBox = new JCheckBox();
        includeOnlyIdentifiedSpectraBox = new JCheckBox();
        removeWorkfilesBox = new JCheckBox();
        intactSubmissionBox = new JCheckBox();
        panel2 = new JPanel();
        removeEmptySpectraBox = new JCheckBox();
        filterIdentsWIthNbPepBox = new JCheckBox();
        nbPeptideFilterTextField = new JTextField();
        label1 = new JLabel();
        blacklistCheckbox = new JCheckBox();
        whitelistCheckbox = new JCheckBox();
        blacklistTextfield = new JTextField();
        blacklistButton = new JButton();
        whitelistTextfield = new JTextField();
        whitelistButton = new JButton();

        //======== this ========

        //======== panel1 ========
        {
            panel1.setBorder(new TitledBorder("Select Output Location"));

            //---- pathField ----
            pathField.setText("Path goes here");
            pathField.setToolTipText("Select the directory where the PRIDE XML files should be\nwritten to. This defaults to the directory where the search\nengine result files are located. Existing PRIDE XML files\nwill be overwritten, if present.");

            //---- selectPathButton ----
            selectPathButton.setText("Select");
            selectPathButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectPathButtonActionPerformed(e);
                }
            });

            //---- gzipCheckBox ----
            gzipCheckBox.setText("Compress output files (gzip)");
            gzipCheckBox.setSelected(true);
            gzipCheckBox.setToolTipText("If selected, the resulting PRIDE XML files will be\nautomatically compressed to save disk space.");

            //---- includeOnlyIdentifiedSpectraBox ----
            includeOnlyIdentifiedSpectraBox.setText("Include only identified spectra");
            includeOnlyIdentifiedSpectraBox.setToolTipText("All unidentified spectra will not be included in the \ngenerated PRIDE XML file.\nThis option is NOT recommended!");
            includeOnlyIdentifiedSpectraBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    enableFiltering();
                }
            });

            //---- removeWorkfilesBox ----
            removeWorkfilesBox.setText("Remove temporary work files");
            removeWorkfilesBox.setSelected(true);
            removeWorkfilesBox.setToolTipText("All temporary files generated by the conversion process\nwill be deleted when PRIDE Converter exits.");

            //---- intactSubmissionBox ----
            intactSubmissionBox.setText("Experiment contains interactions data");
            intactSubmissionBox.setToolTipText("Select this option if your data set contains interaction \ndata and you would like to be contacted by a curator for\ninclusion into IntAct.");

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                    panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(panel1Layout.createParallelGroup()
                                            .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                                    .addComponent(pathField, GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(selectPathButton))
                                            .addComponent(gzipCheckBox)
                                            .addComponent(includeOnlyIdentifiedSpectraBox)
                                            .addComponent(removeWorkfilesBox)
                                            .addComponent(intactSubmissionBox))
                                    .addContainerGap())
            );
            panel1Layout.setVerticalGroup(
                    panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                    .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(pathField, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(selectPathButton))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(gzipCheckBox)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(includeOnlyIdentifiedSpectraBox)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(removeWorkfilesBox)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(intactSubmissionBox)
                                    .addContainerGap(15, Short.MAX_VALUE))
            );
        }

        //======== panel2 ========
        {
            panel2.setBorder(new TitledBorder("Optional Filters"));

            //---- removeEmptySpectraBox ----
            removeEmptySpectraBox.setText("Remove empty spectra");
            removeEmptySpectraBox.setToolTipText("Remove spectra that contain no peaks");
            removeEmptySpectraBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    enableFiltering();
                }
            });

            //---- filterIdentsWIthNbPepBox ----
            filterIdentsWIthNbPepBox.setText("Remove identification with less than");
            filterIdentsWIthNbPepBox.setToolTipText("Remove identifications that contain less than \nthe supplied number of peptides.");
            filterIdentsWIthNbPepBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    enableFiltering();
                }
            });

            //---- nbPeptideFilterTextField ----
            nbPeptideFilterTextField.setToolTipText("Remove identifications that contain less than \nthe supplied number of peptides.");
            nbPeptideFilterTextField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    nbPeptideFilterTextFieldFocusLost();
                }
            });

            //---- label1 ----
            label1.setText("peptides");
            label1.setToolTipText("Remove identifications that contain less than \nthe supplied number of peptides.");

            //---- blacklistCheckbox ----
            blacklistCheckbox.setText("Remove selected identifications");
            blacklistCheckbox.setToolTipText("Use list of contaminants to be removed from the result set.\nSelect a file that contains one protein accession per line.");
            blacklistCheckbox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    blacklistCheckboxActionPerformed();
                }
            });

            //---- whitelistCheckbox ----
            whitelistCheckbox.setText("Use protein inference results");
            whitelistCheckbox.setToolTipText("Use protein inference result to report only the \nsupplied proteins instead of all possible combinations.\nSelect a file that contains one protein accession per line.");
            whitelistCheckbox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    whitelistCheckboxActionPerformed();
                }
            });

            //---- blacklistTextfield ----
            blacklistTextfield.setEditable(false);
            blacklistTextfield.setToolTipText("Use list of contaminants to be removed from the result set.\nSelect a file that contains one protein accession per line.");

            //---- blacklistButton ----
            blacklistButton.setText("Select File");
            blacklistButton.setToolTipText("Select contaminant list");
            blacklistButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    blacklistButtonActionPerformed();
                }
            });

            //---- whitelistTextfield ----
            whitelistTextfield.setEditable(false);
            whitelistTextfield.setToolTipText("Use protein inference result to report only the \nsupplied proteins instead of all possible combinations.\nSelect a file that contains one protein accession per line.");

            //---- whitelistButton ----
            whitelistButton.setText("Select File");
            whitelistButton.setToolTipText("Select protein inference result file");
            whitelistButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    whitelistButtonActionPerformed();
                }
            });

            GroupLayout panel2Layout = new GroupLayout(panel2);
            panel2.setLayout(panel2Layout);
            panel2Layout.setHorizontalGroup(
                    panel2Layout.createParallelGroup()
                            .addGroup(panel2Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(panel2Layout.createParallelGroup()
                                            .addGroup(panel2Layout.createSequentialGroup()
                                                    .addComponent(removeEmptySpectraBox)
                                                    .addGap(255, 255, 255))
                                            .addGroup(panel2Layout.createSequentialGroup()
                                                    .addGroup(panel2Layout.createParallelGroup()
                                                            .addGroup(panel2Layout.createSequentialGroup()
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 1, GroupLayout.PREFERRED_SIZE)
                                                                    .addComponent(filterIdentsWIthNbPepBox)
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addComponent(nbPeptideFilterTextField, GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE)
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addComponent(label1)
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE))
                                                            .addComponent(blacklistCheckbox)
                                                            .addComponent(whitelistCheckbox)
                                                            .addGroup(GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
                                                                    .addGap(20, 20, 20)
                                                                    .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                                            .addComponent(whitelistTextfield, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                                                                            .addComponent(blacklistTextfield, GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE))
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addGroup(panel2Layout.createParallelGroup()
                                                                            .addComponent(whitelistButton, GroupLayout.PREFERRED_SIZE, 123, GroupLayout.PREFERRED_SIZE)
                                                                            .addComponent(blacklistButton, GroupLayout.PREFERRED_SIZE, 123, GroupLayout.PREFERRED_SIZE))))
                                                    .addContainerGap())))
            );
            panel2Layout.setVerticalGroup(
                    panel2Layout.createParallelGroup()
                            .addGroup(panel2Layout.createSequentialGroup()
                                    .addGap(15, 15, 15)
                                    .addComponent(removeEmptySpectraBox)
                                    .addGap(18, 18, 18)
                                    .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(filterIdentsWIthNbPepBox)
                                            .addComponent(nbPeptideFilterTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label1))
                                    .addGap(18, 18, 18)
                                    .addComponent(blacklistCheckbox)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(blacklistTextfield, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(blacklistButton))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(whitelistCheckbox)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(whitelistTextfield, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(whitelistButton))
                                    .addContainerGap(17, Short.MAX_VALUE))
            );
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(panel2, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                                        .addComponent(panel1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel panel1;
    private JTextField pathField;
    private JButton selectPathButton;
    private JCheckBox gzipCheckBox;
    private JCheckBox includeOnlyIdentifiedSpectraBox;
    private JCheckBox removeWorkfilesBox;
    private JCheckBox intactSubmissionBox;
    private JPanel panel2;
    private JCheckBox removeEmptySpectraBox;
    private JCheckBox filterIdentsWIthNbPepBox;
    private JTextField nbPeptideFilterTextField;
    private JLabel label1;
    private JCheckBox blacklistCheckbox;
    private JCheckBox whitelistCheckbox;
    private JTextField blacklistTextfield;
    private JButton blacklistButton;
    private JTextField whitelistTextfield;
    private JButton whitelistButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    public boolean isFilterXml() {
        return filterXml;
    }

    public boolean isGzipped() {
        return gzipCheckBox.isSelected();
    }

    public boolean isSubmitToIntact() {
        return intactSubmissionBox.isSelected();
    }

    public boolean isIncludeOnlyIdentifiedSpectra() {
        return includeOnlyIdentifiedSpectraBox.isSelected();
    }

    public String getOutputPath() {
        return pathField.getText();
    }

    public boolean validateFilters() {

        if (filterIdentsWIthNbPepBox.isSelected()) {
            try {
                Integer.valueOf(nbPeptideFilterTextField.getText());
            } catch (NumberFormatException e) {
                //show error dialog
                return false;
            }
        }

        return true;

    }

    public PrideXmlFilter getFilter(String inputFilePath, String outputFilePath) {

        PrideXmlFilter filter = new PrideXmlFilter(outputFilePath, inputFilePath, gzipCheckBox.isSelected(), gzipCheckBox.isSelected());

        if (includeOnlyIdentifiedSpectraBox.isSelected()) {
            logger.info("Filtering out unidentified spectra");
            filter.setFilterUnidentifiedSpectra(true);
        }
        if (removeEmptySpectraBox.isSelected()) {
            logger.info("Filtering out empty spectra");
            filter.registerSpectrumFilter(new EmptySpectrumFilter());
        }
        if (filterIdentsWIthNbPepBox.isSelected()) {
            logger.info("Filtering out identifications by number of peptide");
            filter.registerIdentificationFilter(new MinimumPeptideCountFilter(Integer.valueOf(nbPeptideFilterTextField.getText())));
        }
        if (whitelistCheckbox.isSelected()) {
            logger.info("Using whitelist filter");
            filter.registerIdentificationFilter(new AccessionWhitelistFilter(whitelistTextfield.getText()));
        }
        if (blacklistCheckbox.isSelected()) {
            logger.info("Using blacklist filter");
            filter.registerIdentificationFilter(new AccessionBlacklistFilter(blacklistTextfield.getText()));
        }

        return filter;

    }

    public void reset() {
        gzipCheckBox.setSelected(true);
        includeOnlyIdentifiedSpectraBox.setSelected(false);
        removeWorkfilesBox.setSelected(true);
        removeEmptySpectraBox.setSelected(false);
        filterIdentsWIthNbPepBox.setSelected(false);
        nbPeptideFilterTextField.setText(null);
        blacklistCheckbox.setSelected(false);
        blacklistTextfield.setText(null);
        whitelistCheckbox.setSelected(false);
        whitelistTextfield.setText(null);
        intactSubmissionBox.setSelected(false);
    }

    public boolean isRemoveWorkfiles() {
        return removeWorkfilesBox.isSelected();
    }
}
