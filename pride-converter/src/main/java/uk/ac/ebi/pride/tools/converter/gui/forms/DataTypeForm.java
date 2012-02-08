/*
 * Created by JFormDesigner on Mon May 23 13:43:17 BST 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.dao.DAOFactory;
import uk.ac.ebi.pride.tools.converter.gui.component.BrowserLauncher;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.gui.model.DataType;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.config.Configurator;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * @author melih
 * @author rcote
 */
public class DataTypeForm extends AbstractForm {

    private java.util.List<JRadioButton> spectraOnlyButtons = new ArrayList<JRadioButton>();

    public DataTypeForm() {
        initComponents();
        macOsWarningLabel.setVisible(false);

        if (Configurator.getOSName().toLowerCase().contains("mac")) {
            setMacOS();
        }

        descriptionArea.setEditorKit(new HTMLEditorKit());
        descriptionArea.setText(bundle.getString("Welcome.message"));

        //add all spectrum only buttons
        spectraOnlyButtons.add(pklRadioMultiple);
        spectraOnlyButtons.add(pklRadioSingle);
        spectraOnlyButtons.add(dtaRadioMultiple);
        spectraOnlyButtons.add(dtaRadioSingle);
        spectraOnlyButtons.add(mzMLRadio);
        spectraOnlyButtons.add(ms2Radio);
        spectraOnlyButtons.add(mgfRadio);
        spectraOnlyButtons.add(mzdataButton);
        spectraOnlyButtons.add(msgfRadio);

    }

    public void setMacOS() {
        mascotRadio.setEnabled(false);
        mascotRadio.setToolTipText("Mascot DAT Files does not support Mac OS");
        macOsWarningLabel.setVisible(true);
    }

    private void mascotRadioActionPerformed(ActionEvent e) {
        ConverterData.getInstance().setType(DataType.MASCOT);
        descriptionArea.setText("<br>" +
                "<b>Mascot DAT Files</b><br><br>" +
                "Mascot DAT Files are result files " +
                "from a Mascot search. <br>Both the spectra and the identifications are included.<br><br>" +
                "File Extension: .dat<br><br>" +
                "Homepage: <a href=\"http://www.matrixscience.com\">www.matrixscience.com</a>");
        descriptionArea.setCaretPosition(0);
        warnIfSpectrumOnly();
        validationListerner.fireValidationListener(true);
    }

    private void xtandemRadioActionPerformed(ActionEvent e) {
        ConverterData.getInstance().setType(DataType.XTANDEM);
        descriptionArea.setText("<br>" +
                "<b>X!Tandem</b><br><br>" +
                "X!Tandem is open source software that matches " +
                "tandem mass spectra to peptide sequences.<br><br>" +
                "The output format is described here:<br>" +
                "<a href=\"http://www.thegpm.org/docs/X_series_output_form.pdf\">www.thegpm.org/docs/X_series_output_form.pdf</a>" +
                "<br><br>" +
                "File Extension: .xml (the identifications) and dta, mgf, pkl, mzData or mzXML (the spectra)<br><br>" +
                "Homepage: <a href=\"http://www.thegpm.org/TANDEM\">www.thegpm.org/TANDEM</a>");
        descriptionArea.setCaretPosition(0);
        warnIfSpectrumOnly();
        validationListerner.fireValidationListener(true);
    }

    private void mzIdentMLRadioActionPerformed(ActionEvent e) {
        ConverterData.getInstance().setType(DataType.MZIDENTML);
        descriptionArea.setText("<br>" +
                "<b>MzIdentML</b><br>" +
                "<p><strong>jmzIdentML</strong> provides a portable and lightweight JAXB-based implementation of the full mzIdentML 1.1 standard format (note that the <strong>jmzIdentML</strong> version number has no relation to the mzIdentML version number), with considerable tweaks to make the processing of files memory efficient. In particular, mzIdentML files are effectively indexed on the fly and used as swap files, with only requested snippets of data loaded from a file when accessing it. Additionally, internal references in the mzIdentML XML are resolved automatically by <strong>jmzIdentML</strong>, giving you direct access in the object model to entities that are only referenced by ID in the actual XML file. </p>\n" +
                "<p><strong>jmzIdentML</strong> is written in 100% pure Java, and is made available under the permissive Apache2 open source license. </p>\n");
        descriptionArea.setCaretPosition(0);
        warnIfSpectrumOnly();
        validationListerner.fireValidationListener(true);
    }

    private void dtaRadioMultipleActionPerformed(ActionEvent e) {
        ConverterData.getInstance().setType(DataType.DTA_MULTIPLE);
        descriptionArea.setText("<br>" +
                "<b>SEQUEST DTA Files</b><br><br>" +
                "SEQUEST DTA File is a simple MS/MS data format without identifications. " +
                "The first line contains the singly protonated peptide mass (MH+) and the peptide charge " +
                "state. Subsequent lines contain space separated pairs of fragment ion m/z and intensity values. " +
                "<br><br>NB: Each file contains a single MS/MS data set.<br><br>" +
                "File Extension: .dta<br><br>" +
                "More Information: <a href=\"http://www.matrixscience.com/help/data_file_help.html#DTA\">" +
                "www.matrixscience.com/help/data_file_help.html#DTA</a>");
        descriptionArea.setCaretPosition(0);
        warnIfSpectrumOnly();
        validationListerner.fireValidationListener(true);
    }

    private void dtaRadioActionPerformed(ActionEvent e) {
        ConverterData.getInstance().setType(DataType.DTA_SINGLE);
        descriptionArea.setText("<br>" +
                "<b>SEQUEST DTA Files</b><br><br>" +
                "SEQUEST DTA File is a simple MS/MS data format without identifications. " +
                "The first line contains the singly protonated peptide mass (MH+) and the peptide charge " +
                "state. Subsequent lines contain space separated pairs of fragment ion m/z and intensity values. " +
                "<br><br>NB: Each file contains a single MS/MS data set.<br><br>" +
                "File Extension: .dta<br><br>" +
                "More Information: <a href=\"http://www.matrixscience.com/help/data_file_help.html#DTA\">" +
                "www.matrixscience.com/help/data_file_help.html#DTA</a>");
        descriptionArea.setCaretPosition(0);
        warnIfSpectrumOnly();
        validationListerner.fireValidationListener(true);
    }

    private void mgfRadioActionPerformed(ActionEvent e) {
        ConverterData.getInstance().setType(DataType.MGF);
        descriptionArea.setText("<br>" +
                "<b>Mascot Generic Files</b><br><br>" +
                "The Mascot Generic File format is a generic format for submitting " +
                "data to Mascot. <br>It only contains information about the spectra.<br><br>" +
                "File Extension: .mgf<br><br>" +
                "Homepage: <a href=\"http://www.matrixscience.com\">www.matrixscience.com</a>");
        descriptionArea.setCaretPosition(0);
        warnIfSpectrumOnly();
        validationListerner.fireValidationListener(true);
    }

    private void ms2RadioActionPerformed(ActionEvent e) {
        ConverterData.getInstance().setType(DataType.MS2);
        descriptionArea.setText("<br>" +
                "<b>MS2</b><br><br>" +
                "MS2 files stores MS/MS data and can replace a folder of thousands of DTA files. " +
                "It contains all the spectral information necessary for database searching algorithms.<br><br>" +
                "File Extension: .ms2<br><br>" +
                "More Information: <a href=\"http://doi.wiley.com/10.1002/rcm.1603\">" +
                "http://doi.wiley.com/10.1002/rcm.1603</a>");
        descriptionArea.setCaretPosition(0);
        warnIfSpectrumOnly();
        validationListerner.fireValidationListener(true);
    }

    private void msgfRadioActionPerformed(ActionEvent e) {
        ConverterData.getInstance().setType(DataType.MSGF);
        descriptionArea.setText("<br>" +
                "<b>MSGF files</b><br><br>" +
                "MSGF files are generated by the MSGF utility.<br>" +
                "Detailed information can be found at <a href=\"http://proteomics.ucsd.edu/Software/MSGeneratingFunction.html\">http://proteomics.ucsd.edu/Software/MSGeneratingFunction.html</a>");
        descriptionArea.setCaretPosition(0);
        warnIfSpectrumOnly();
        validationListerner.fireValidationListener(true);
    }

    private void mzMLRadioActionPerformed(ActionEvent e) {
        ConverterData.getInstance().setType(DataType.MZML);
        descriptionArea.setText("<br>" +
                "<b>MZML</b><br>" +
                "<p><strong>jmzML</strong> provides a portable and lightweight JAXB-based implementation of the full mzML 1.1 standard format (note that the <strong>jmzML</strong> version number has no relation to the mzML version number), with considerable tweaks to make the processing of files memory efficient. In particular, mzML files are effectively indexed on the fly and used as swap files, with only requested snippets of data loaded from a file when accessing it. Additionally, internal references in the mzML XML are resolved automatically by <strong>jmzML</strong>, giving you direct access in the object model to entities that are only referenced by ID in the actual XML file. </p>\n" +
                "<p>Apart from reading indexed and non-indexed mzML files, <strong>jmzML</strong> also allows writing of non-indexed mzML files. </p>\n" +
                "<p><strong>jmzML</strong> is written in 100% pure Java, and is made available under the permissive Apache2 open source license. </p>\n" +
                "<p>A one-page guide on using the <strong>jmzML</strong> API can be found here: <a href=\"http://code.google.com/p/jmzml/wiki/HowToUseJMzML\">HowToUseJMzML</a> </p>");
        descriptionArea.setCaretPosition(0);
        warnIfSpectrumOnly();
        validationListerner.fireValidationListener(true);
    }

    private void pklRadioMultipleActionPerformed(ActionEvent e) {
        ConverterData.getInstance().setType(DataType.PKL_MULTIPLE);
        descriptionArea.setText("<br>" +
                "<b>Waters MassLynx</b><br><br>" +
                "Waters uses two formats to represent mass spectrometry data via the MassLynx program. " +
                "The .pkl format is a plain-text file containing one or more centroided, monoisotopic " +
                "peak list of a .raw/directory structure.<br><br>" +
                "File Extensions: .pkl <br><br>" +
                "Homepage: <a href=\"http://www.waters.com/waters/nav.htm?cid=513164\">" +
                "Waters MassLynx</a>");
        descriptionArea.setCaretPosition(0);
        warnIfSpectrumOnly();
        validationListerner.fireValidationListener(true);
    }

    private void pklRadioActionPerformed(ActionEvent e) {
        ConverterData.getInstance().setType(DataType.PKL_SINGLE);
        descriptionArea.setText("<br>" +
                "<b>Waters MassLynx</b><br><br>" +
                "Waters uses two formats to represent mass spectrometry data via the MassLynx program. " +
                "The .pkl format is a plain-text file containing one or more centroided, monoisotopic " +
                "peak list of a .raw/directory structure.<br><br>" +
                "File Extensions: .pkl <br><br>" +
                "Homepage: <a href=\"http://www.waters.com/waters/nav.htm?cid=513164\">" +
                "Waters MassLynx</a>");
        descriptionArea.setCaretPosition(0);
        warnIfSpectrumOnly();
        validationListerner.fireValidationListener(true);
    }

    private void mzxmlButtonActionPerformed() {
        ConverterData.getInstance().setType(DataType.MZXML);
        descriptionArea.setText("<br>" +
                "<b>mzXML</b><br><br>" +
                "mzXML is an open data format for storage and exchange of mass spectroscopy data, developed at the " +
                "Institute for Systems Biology. mzXML provides a standard container for ms and ms/ms proteomics data. " +
                "Raw, proprietary file formats from most vendors can be converted to the open mzXML format.<br><br>" +
                "Homepage: <a href=\"http://tools.proteomecenter.org/software.php\">" +
                "http://tools.proteomecenter.org/software.php</a>");
        descriptionArea.setCaretPosition(0);
        warnIfSpectrumOnly();
        validationListerner.fireValidationListener(true);
    }

    private void mzdataButtonActionPerformed(ActionEvent e) {
        ConverterData.getInstance().setType(DataType.MZDATA);
        descriptionArea.setText("<br>" +
                "<b>mzData</b><br><br>" +
                "The mzData standard, which captures mass spectrometry output data. mzData's aim is to unite the large number of current formats (pkl's, dta's, mgf's, .....) into a single format. mzData has been released and is stable at version 1.05. It is now deprecated in favor of mzML.<br><br>" +
                "Homepage: <a href=\"http://www.psidev.info/index.php?q=node/80#mzdata\">" +
                "http://www.psidev.info/index.php?q=node/80#mzdata</a>");
        descriptionArea.setCaretPosition(0);
        warnIfSpectrumOnly();
        validationListerner.fireValidationListener(true);
    }


    private void descriptionAreaHyperlinkUpdate(HyperlinkEvent evt) {
        if (evt.getEventType().toString().equalsIgnoreCase(
                javax.swing.event.HyperlinkEvent.EventType.ENTERED.toString())) {
            setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        } else if (evt.getEventType().toString().equalsIgnoreCase(
                javax.swing.event.HyperlinkEvent.EventType.EXITED.toString())) {
            setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        } else if (evt.getEventType().toString().equalsIgnoreCase(
                javax.swing.event.HyperlinkEvent.EventType.ACTIVATED.toString())) {
            if (evt.getDescription().startsWith("#")) {
                descriptionArea.scrollToReference(evt.getDescription());
            } else {
                this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
                BrowserLauncher.openURL(evt.getDescription());
                this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            }
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("messages");
        mainPanel = new JPanel();
        scrollPane1 = new JScrollPane();
        descriptionArea = new JEditorPane();
        pklRadioMultiple = new JRadioButton();
        pklRadioSingle = new JRadioButton();
        dtaRadioMultiple = new JRadioButton();
        dtaRadioSingle = new JRadioButton();
        mzIdentMLRadio = new JRadioButton();
        mzMLRadio = new JRadioButton();
        mascotRadio = new JRadioButton();
        xtandemRadio = new JRadioButton();
        mzxmlButton = new JRadioButton();
        ms2Radio = new JRadioButton();
        mgfRadio = new JRadioButton();
        macOsWarningLabel = new JLabel();
        mzdataButton = new JRadioButton();
        label1 = new JLabel();
        msgfRadio = new JRadioButton();
        dataTypeGroup = new ButtonGroup();

        //======== this ========

        //======== mainPanel ========
        {
            mainPanel.setBorder(null);

            //======== scrollPane1 ========
            {

                //---- descriptionArea ----
                descriptionArea.setText(bundle.getString("Welcome.message"));
                descriptionArea.setContentType("text/html");
                descriptionArea.setEditable(false);
                descriptionArea.setFont(new Font("Dialog", Font.ITALIC, 10));
                descriptionArea.addHyperlinkListener(new HyperlinkListener() {
                    @Override
                    public void hyperlinkUpdate(HyperlinkEvent e) {
                        descriptionAreaHyperlinkUpdate(e);
                    }
                });
                scrollPane1.setViewportView(descriptionArea);
            }

            //---- pklRadioMultiple ----
            pklRadioMultiple.setText(bundle.getString("WelcomeScreen.pklRadioMultiple.text"));
            pklRadioMultiple.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    pklRadioMultipleActionPerformed(e);
                }
            });

            //---- pklRadioSingle ----
            pklRadioSingle.setText(bundle.getString("WelcomeScreen.pklRadioSingle.text"));
            pklRadioSingle.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    pklRadioActionPerformed(e);
                }
            });

            //---- dtaRadioMultiple ----
            dtaRadioMultiple.setText(bundle.getString("WelcomeScreen.dtaRadioMultiple.text"));
            dtaRadioMultiple.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dtaRadioMultipleActionPerformed(e);
                }
            });

            //---- dtaRadioSingle ----
            dtaRadioSingle.setText(bundle.getString("WelcomeScreen.dtaRadioSingle.text"));
            dtaRadioSingle.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dtaRadioActionPerformed(e);
                }
            });

            //---- mzIdentMLRadio ----
            mzIdentMLRadio.setText(bundle.getString("WelcomeScreen.mzIdentMLRadio.text"));
            mzIdentMLRadio.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mzIdentMLRadioActionPerformed(e);
                }
            });

            //---- mzMLRadio ----
            mzMLRadio.setText(bundle.getString("WelcomeScreen.mzMLRadio.text"));
            mzMLRadio.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mzMLRadioActionPerformed(e);
                }
            });

            //---- mascotRadio ----
            mascotRadio.setText(bundle.getString("WelcomeScreen.mascotRadio.text"));
            mascotRadio.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mascotRadioActionPerformed(e);
                }
            });

            //---- xtandemRadio ----
            xtandemRadio.setText(bundle.getString("WelcomeScreen.xtandemRadio.text"));
            xtandemRadio.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    xtandemRadioActionPerformed(e);
                }
            });

            //---- mzxmlButton ----
            mzxmlButton.setText(bundle.getString("WelcomeScreen.mzxmlButton.text"));
            mzxmlButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mzxmlButtonActionPerformed();
                }
            });

            //---- ms2Radio ----
            ms2Radio.setText(bundle.getString("WelcomeScreen.ms2Radio.text"));
            ms2Radio.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ms2RadioActionPerformed(e);
                }
            });

            //---- mgfRadio ----
            mgfRadio.setText(bundle.getString("WelcomeScreen.mgfRadio.text"));
            mgfRadio.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mgfRadioActionPerformed(e);
                }
            });

            //---- macOsWarningLabel ----
            macOsWarningLabel.setText("not supported on Mac OS");
            macOsWarningLabel.setFont(new Font("Dialog", Font.ITALIC, 10));

            //---- mzdataButton ----
            mzdataButton.setText(bundle.getString("WelcomeScreen.mzdataButton.text"));
            mzdataButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mzdataButtonActionPerformed(e);
                }
            });

            //---- label1 ----
            label1.setText(bundle.getString("WelcomeScreen.label1.text"));

            //---- msgfRadio ----
            msgfRadio.setText(bundle.getString("WelcomeScreen.msgfRadio.text"));
            msgfRadio.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    msgfRadioActionPerformed(e);
                }
            });

            GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
            mainPanel.setLayout(mainPanelLayout);
            mainPanelLayout.setHorizontalGroup(
                    mainPanelLayout.createParallelGroup()
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                    .addGroup(mainPanelLayout.createParallelGroup()
                                            .addGroup(mainPanelLayout.createSequentialGroup()
                                                    .addGroup(mainPanelLayout.createParallelGroup()
                                                            .addComponent(dtaRadioSingle)
                                                            .addComponent(mzIdentMLRadio)
                                                            .addComponent(mzMLRadio)
                                                            .addGroup(mainPanelLayout.createSequentialGroup()
                                                                    .addComponent(mascotRadio)
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addComponent(macOsWarningLabel, GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE))
                                                            .addComponent(pklRadioSingle)
                                                            .addComponent(mgfRadio))
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(mainPanelLayout.createParallelGroup()
                                                            .addComponent(mzdataButton)
                                                            .addGroup(mainPanelLayout.createSequentialGroup()
                                                                    .addGap(1, 1, 1)
                                                                    .addGroup(mainPanelLayout.createParallelGroup()
                                                                            .addComponent(mzxmlButton)
                                                                            .addComponent(xtandemRadio)
                                                                            .addComponent(ms2Radio, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
                                                                            .addComponent(dtaRadioMultiple)))
                                                            .addComponent(pklRadioMultiple)))
                                            .addGroup(mainPanelLayout.createSequentialGroup()
                                                    .addContainerGap()
                                                    .addComponent(label1)))
                                    .addContainerGap())
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                    .addComponent(msgfRadio)
                                    .addContainerGap())
                            .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE)
            );
            mainPanelLayout.setVerticalGroup(
                    mainPanelLayout.createParallelGroup()
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(label1)
                                    .addGap(9, 9, 9)
                                    .addGroup(mainPanelLayout.createParallelGroup()
                                            .addGroup(mainPanelLayout.createSequentialGroup()
                                                    .addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                            .addComponent(mascotRadio)
                                                            .addComponent(macOsWarningLabel))
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(mzMLRadio)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(mzIdentMLRadio)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(dtaRadioSingle)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(pklRadioSingle)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                            .addComponent(mgfRadio)
                                                            .addComponent(mzdataButton)))
                                            .addGroup(mainPanelLayout.createSequentialGroup()
                                                    .addComponent(mzxmlButton)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(xtandemRadio)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(ms2Radio)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(dtaRadioMultiple)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(pklRadioMultiple)))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(msgfRadio)
                                    .addGap(18, 18, 18)
                                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE))
            );
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );

        //---- dataTypeGroup ----
        dataTypeGroup.add(pklRadioMultiple);
        dataTypeGroup.add(pklRadioSingle);
        dataTypeGroup.add(dtaRadioMultiple);
        dataTypeGroup.add(dtaRadioSingle);
        dataTypeGroup.add(mzIdentMLRadio);
        dataTypeGroup.add(mzMLRadio);
        dataTypeGroup.add(mascotRadio);
        dataTypeGroup.add(xtandemRadio);
        dataTypeGroup.add(mzxmlButton);
        dataTypeGroup.add(ms2Radio);
        dataTypeGroup.add(mgfRadio);
        dataTypeGroup.add(mzdataButton);
        dataTypeGroup.add(msgfRadio);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel mainPanel;
    private JScrollPane scrollPane1;
    private JEditorPane descriptionArea;
    private JRadioButton pklRadioMultiple;
    private JRadioButton pklRadioSingle;
    private JRadioButton dtaRadioMultiple;
    private JRadioButton dtaRadioSingle;
    private JRadioButton mzIdentMLRadio;
    private JRadioButton mzMLRadio;
    private JRadioButton mascotRadio;
    private JRadioButton xtandemRadio;
    private JRadioButton mzxmlButton;
    private JRadioButton ms2Radio;
    private JRadioButton mgfRadio;
    private JLabel macOsWarningLabel;
    private JRadioButton mzdataButton;
    private JLabel label1;
    private JRadioButton msgfRadio;
    private ButtonGroup dataTypeGroup;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    @Override
    public Collection<ValidatorMessage> validateForm() {
        //nothing to validate
        return Collections.emptyList();
    }

    @Override
    public void clear() {
        dataTypeGroup.clearSelection();
        descriptionArea.setText(bundle.getString("Welcome.message"));
        //inactivate next button
        validationListerner.fireValidationListener(false);
    }

    @Override
    public void save(ReportReaderDAO dao) {
        /* no op */
    }

    @Override
    public void load(ReportReaderDAO dao) {
        /* no op */
    }

    @Override
    public String getFormName() {
        return "Format Selection";
    }

    @Override
    public String getFormDescription() {
        return config.getString("datatype.form.description");
    }

    @Override
    public String getHelpResource() {
        return "help.ui.format";
    }

    @Override
    public void start() {
        /* no op */
    }

    @Override
    public void finish() {
        /* no op */
    }

    private void warnIfSpectrumOnly() {

        DataType type = ConverterData.getInstance().getType();
        DAOFactory.DAO_FORMAT daoFormat = DAOFactory.DAO_FORMAT.getDAOForSearchengineOption(type.getEngineName().toLowerCase());

        if (daoFormat == null) {
            throw new ConverterException("Invalid DAO Format: " + type.getEngineName());
        }
        ConverterData.getInstance().setDaoFormat(daoFormat);

        if (ConverterData.getInstance().getDaoFormat().isSpectrumOnly()) {
            int res = JOptionPane.showConfirmDialog(this, "You have selected a data type that contains only spectra and no identifications.\nPress OK to continue or CANCEL to clear your selection.", "Warning!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (res == JOptionPane.CANCEL_OPTION) {
                clear();
            }
        }
    }

    public void setSpectrumOnlyFormatsEnabled(boolean enabled) {
        for (JRadioButton button : spectraOnlyButtons) {
            button.setEnabled(enabled);
        }
    }
}
