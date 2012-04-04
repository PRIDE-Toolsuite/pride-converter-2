/*
 * Created by JFormDesigner on Mon May 23 13:43:17 BST 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.dao.DAOFactory;
import uk.ac.ebi.pride.tools.converter.gui.component.BrowserLauncher;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.config.Configurator;

import javax.swing.*;
import javax.swing.border.TitledBorder;
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
    private DAOFactory.DAO_FORMAT daoFormat = null;

    public DataTypeForm() {
        initComponents();
        macOsWarningLabel.setVisible(false);

        if (Configurator.getOSName().toLowerCase().contains("mac")) {
            setMacOS();
        }

        descriptionArea.setEditorKit(new HTMLEditorKit());
        descriptionArea.setText(bundle.getString("Welcome.message"));

        //add all spectrum only buttons
        spectraOnlyButtons.add(pklRadioSingle);
        spectraOnlyButtons.add(dtaRadioSingle);
        spectraOnlyButtons.add(mzMLRadio);
        spectraOnlyButtons.add(ms2Radio);
        spectraOnlyButtons.add(mgfRadio);
        spectraOnlyButtons.add(mzdataButton);
        spectraOnlyButtons.add(mzxmlButton);

    }

    public void setMacOS() {
        mascotRadio.setEnabled(false);
        mascotRadio.setToolTipText("Mascot DAT Files does not support Mac OS");
        macOsWarningLabel.setVisible(true);
    }

    private void mascotRadioActionPerformed(ActionEvent e) {
        daoFormat = DAOFactory.DAO_FORMAT.MASCOT;
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
        daoFormat = DAOFactory.DAO_FORMAT.X_TANDEM;
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
        daoFormat = DAOFactory.DAO_FORMAT.MZIDENTML;
        descriptionArea.setText("<br>" +
                "<b>MzIdentML</b><br>" +
                "<p><strong>jmzIdentML</strong> provides a portable and lightweight JAXB-based implementation of the full mzIdentML 1.1 standard format (note that the <strong>jmzIdentML</strong> version number has no relation to the mzIdentML version number), with considerable tweaks to make the processing of files memory efficient. In particular, mzIdentML files are effectively indexed on the fly and used as swap files, with only requested snippets of data loaded from a file when accessing it. Additionally, internal references in the mzIdentML XML are resolved automatically by <strong>jmzIdentML</strong>, giving you direct access in the object model to entities that are only referenced by ID in the actual XML file. </p>\n" +
                "<p><strong>jmzIdentML</strong> is written in 100% pure Java, and is made available under the permissive Apache2 open source license. </p>\n");
        descriptionArea.setCaretPosition(0);
        warnIfSpectrumOnly();
        validationListerner.fireValidationListener(true);
    }

    private void dtaRadioMultipleActionPerformed(ActionEvent e) {
        daoFormat = DAOFactory.DAO_FORMAT.DTA;
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
        daoFormat = DAOFactory.DAO_FORMAT.DTA;
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
        daoFormat = DAOFactory.DAO_FORMAT.MGF;
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
        daoFormat = DAOFactory.DAO_FORMAT.MS2;
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
        daoFormat = DAOFactory.DAO_FORMAT.MSGF;
        descriptionArea.setText("<br>" +
                "<b>MSGF files</b><br><br>" +
                "MSGF files are generated by the MSGF utility.<br>" +
                "Detailed information can be found at <a href=\"http://proteomics.ucsd.edu/Software/MSGeneratingFunction.html\">http://proteomics.ucsd.edu/Software/MSGeneratingFunction.html</a>");
        descriptionArea.setCaretPosition(0);
        warnIfSpectrumOnly();
        validationListerner.fireValidationListener(true);
    }

    private void mzMLRadioActionPerformed(ActionEvent e) {
        daoFormat = DAOFactory.DAO_FORMAT.MZML;
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
        daoFormat = DAOFactory.DAO_FORMAT.PKL;
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
        daoFormat = DAOFactory.DAO_FORMAT.PKL;
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
        daoFormat = DAOFactory.DAO_FORMAT.MZXML;
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
        daoFormat = DAOFactory.DAO_FORMAT.MZDATA;
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
        if (evt.getEventType().equals(HyperlinkEvent.EventType.ENTERED)) {
            setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        } else if (evt.getEventType().equals(HyperlinkEvent.EventType.EXITED)) {
            setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        } else if (evt.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
            if (evt.getDescription().startsWith("#")) {
                descriptionArea.scrollToReference(evt.getDescription());
            } else {
                this.setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));
                BrowserLauncher.openURL(evt.getDescription());
                this.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            }
        }
    }

    private void cruxButtonActionPerformed() {
        daoFormat = DAOFactory.DAO_FORMAT.CRUX;
        descriptionArea.setText("<br/><b>Crux</b><br/><br/>Crux is a software toolkit for tandem mass spectrometry analysis, with a focus on peptide identification. The Crux-txt file format records the matches between MS/MS spectra and a sequence database using lines of tab delimited fields." +
                "<br/><br/>" +
                "More information here: <a href=\"http://noble.gs.washington.edu/proj/crux/\">http://noble.gs.washington.edu/proj/crux/</a>");
        descriptionArea.setCaretPosition(0);
        warnIfSpectrumOnly();
        validationListerner.fireValidationListener(true);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("messages");
        scrollPane1 = new JScrollPane();
        descriptionArea = new JEditorPane();
        label1 = new JLabel();
        panel2 = new JPanel();
        panel1 = new JPanel();
        mascotRadio = new JRadioButton();
        macOsWarningLabel = new JLabel();
        msgfRadio = new JRadioButton();
        mzIdentMLRadio = new JRadioButton();
        xtandemRadio = new JRadioButton();
        cruxButton = new JRadioButton();
        panel4 = new JPanel();
        mzMLRadio = new JRadioButton();
        mzdataButton = new JRadioButton();
        mzxmlButton = new JRadioButton();
        dtaRadioSingle = new JRadioButton();
        mgfRadio = new JRadioButton();
        ms2Radio = new JRadioButton();
        pklRadioSingle = new JRadioButton();
        dataTypeGroup = new ButtonGroup();

        //======== this ========

        //======== scrollPane1 ========
        {

            //---- descriptionArea ----
            descriptionArea.setText(bundle.getString("WelcomeScreen.descriptionArea.text"));
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

        //---- label1 ----
        label1.setText(bundle.getString("WelcomeScreen.label1.text"));

        //======== panel2 ========
        {
            panel2.setBorder(new TitledBorder(bundle.getString("WelcomeScreen.panel2.border_2")));
            panel2.setLayout(new GridLayout(0, 2));

            //======== panel1 ========
            {
                panel1.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 5));

                //---- mascotRadio ----
                mascotRadio.setText(bundle.getString("WelcomeScreen.mascotRadio.text"));
                mascotRadio.setHorizontalAlignment(SwingConstants.LEFT);
                mascotRadio.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        mascotRadioActionPerformed(e);
                    }
                });
                panel1.add(mascotRadio);

                //---- macOsWarningLabel ----
                macOsWarningLabel.setText("not supported on Mac OS");
                macOsWarningLabel.setFont(new Font("Dialog", Font.ITALIC, 10));
                panel1.add(macOsWarningLabel);
            }
            panel2.add(panel1);

            //---- msgfRadio ----
            msgfRadio.setText(bundle.getString("WelcomeScreen.msgfRadio.text"));
            msgfRadio.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    msgfRadioActionPerformed(e);
                }
            });
            panel2.add(msgfRadio);

            //---- mzIdentMLRadio ----
            mzIdentMLRadio.setText(bundle.getString("WelcomeScreen.mzIdentMLRadio.text"));
            mzIdentMLRadio.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mzIdentMLRadioActionPerformed(e);
                }
            });
            panel2.add(mzIdentMLRadio);

            //---- xtandemRadio ----
            xtandemRadio.setText(bundle.getString("WelcomeScreen.xtandemRadio.text"));
            xtandemRadio.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    xtandemRadioActionPerformed(e);
                }
            });
            panel2.add(xtandemRadio);

            //---- cruxButton ----
            cruxButton.setText(bundle.getString("WelcomeScreen.cruxButton.text"));
            cruxButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cruxButtonActionPerformed();
                }
            });
            panel2.add(cruxButton);
        }

        //======== panel4 ========
        {
            panel4.setBorder(new TitledBorder(bundle.getString("WelcomeScreen.panel4.border")));
            panel4.setLayout(new GridLayout(0, 2));

            //---- mzMLRadio ----
            mzMLRadio.setText(bundle.getString("WelcomeScreen.mzMLRadio.text"));
            mzMLRadio.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mzMLRadioActionPerformed(e);
                }
            });
            panel4.add(mzMLRadio);

            //---- mzdataButton ----
            mzdataButton.setText(bundle.getString("WelcomeScreen.mzdataButton.text"));
            mzdataButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mzdataButtonActionPerformed(e);
                }
            });
            panel4.add(mzdataButton);

            //---- mzxmlButton ----
            mzxmlButton.setText(bundle.getString("WelcomeScreen.mzxmlButton.text"));
            mzxmlButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mzxmlButtonActionPerformed();
                }
            });
            panel4.add(mzxmlButton);

            //---- dtaRadioSingle ----
            dtaRadioSingle.setText(bundle.getString("WelcomeScreen.dtaRadioSingle.text"));
            dtaRadioSingle.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dtaRadioActionPerformed(e);
                }
            });
            panel4.add(dtaRadioSingle);

            //---- mgfRadio ----
            mgfRadio.setText(bundle.getString("WelcomeScreen.mgfRadio.text"));
            mgfRadio.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mgfRadioActionPerformed(e);
                }
            });
            panel4.add(mgfRadio);

            //---- ms2Radio ----
            ms2Radio.setText(bundle.getString("WelcomeScreen.ms2Radio.text"));
            ms2Radio.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ms2RadioActionPerformed(e);
                }
            });
            panel4.add(ms2Radio);

            //---- pklRadioSingle ----
            pklRadioSingle.setText(bundle.getString("WelcomeScreen.pklRadioSingle.text"));
            pklRadioSingle.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    pklRadioActionPerformed(e);
                }
            });
            panel4.add(pklRadioSingle);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(scrollPane1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 643, Short.MAX_VALUE)
                                        .addComponent(panel4, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 643, Short.MAX_VALUE)
                                        .addComponent(panel2, GroupLayout.DEFAULT_SIZE, 643, Short.MAX_VALUE)
                                        .addComponent(label1))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(label1)
                                .addGap(18, 18, 18)
                                .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addComponent(panel4, GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                                .addContainerGap())
        );

        //---- dataTypeGroup ----
        dataTypeGroup.add(mascotRadio);
        dataTypeGroup.add(msgfRadio);
        dataTypeGroup.add(mzIdentMLRadio);
        dataTypeGroup.add(xtandemRadio);
        dataTypeGroup.add(cruxButton);
        dataTypeGroup.add(mzMLRadio);
        dataTypeGroup.add(mzdataButton);
        dataTypeGroup.add(mzxmlButton);
        dataTypeGroup.add(dtaRadioSingle);
        dataTypeGroup.add(mgfRadio);
        dataTypeGroup.add(ms2Radio);
        dataTypeGroup.add(pklRadioSingle);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JScrollPane scrollPane1;
    private JEditorPane descriptionArea;
    private JLabel label1;
    private JPanel panel2;
    private JPanel panel1;
    private JRadioButton mascotRadio;
    private JLabel macOsWarningLabel;
    private JRadioButton msgfRadio;
    private JRadioButton mzIdentMLRadio;
    private JRadioButton xtandemRadio;
    private JRadioButton cruxButton;
    private JPanel panel4;
    private JRadioButton mzMLRadio;
    private JRadioButton mzdataButton;
    private JRadioButton mzxmlButton;
    private JRadioButton dtaRadioSingle;
    private JRadioButton mgfRadio;
    private JRadioButton ms2Radio;
    private JRadioButton pklRadioSingle;
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
    public Icon getFormIcon() {
        return getFormIcon("datatype.form.icon");
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

        if (daoFormat == null) {
            throw new ConverterException("No DAO Format Selected");
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
