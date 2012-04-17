/*
* Created by JFormDesigner on Wed May 25 11:03:01 BST 2011
*/

package uk.ac.ebi.pride.tools.converter.gui.forms;

import org.apache.log4j.Logger;
import psidev.psi.tools.validator.Context;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorMessage;
import psidev.psi.tools.validator.rules.Rule;
import uk.ac.ebi.pride.tools.converter.dao.DAOFactory;
import uk.ac.ebi.pride.tools.converter.dao.DAOProperty;
import uk.ac.ebi.pride.tools.converter.dao.handler.HandlerFactory;
import uk.ac.ebi.pride.tools.converter.dao.handler.impl.MzTabHandler;
import uk.ac.ebi.pride.tools.converter.gui.NavigationPanel;
import uk.ac.ebi.pride.tools.converter.gui.component.filefilters.FastaFileFilter;
import uk.ac.ebi.pride.tools.converter.gui.component.filefilters.MzTabFileFilter;
import uk.ac.ebi.pride.tools.converter.gui.component.table.FileTable;
import uk.ac.ebi.pride.tools.converter.gui.component.table.ParserOptionTable;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.ParserOptionCellEditor;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.ParserOptionTableModel;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.MultipleFormEditingWarningDialog;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.gui.model.FileBean;
import uk.ac.ebi.pride.tools.converter.gui.model.GUIException;
import uk.ac.ebi.pride.tools.converter.gui.model.OutputFormat;
import uk.ac.ebi.pride.tools.converter.gui.util.IOUtilities;
import uk.ac.ebi.pride.tools.converter.gui.util.PreferenceManager;
import uk.ac.ebi.pride.tools.converter.gui.validator.rules.PrideMergerFileRule;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;

import javax.help.CSH;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

/**
 * @author User #1
 * @author rcote
 */
public class FileSelectionForm extends AbstractForm implements TableModelListener {

    private static final Logger logger = Logger.getLogger(FileSelectionForm.class);

    private OutputFormat format;
    private boolean singleFileSelectionMode = true;

    //default
    private HandlerFactory.FASTA_FORMAT fastaFormat = HandlerFactory.FASTA_FORMAT.FULL;

    private ParserOptionTable allOptionTable = new ParserOptionTable();
    private ParserOptionTable noAdvancedOptionTable = new ParserOptionTable();

    public FileSelectionForm(OutputFormat format) {
        initComponents();
        dataFileTable.getModel().addTableModelListener(this);
        mzTabFileTable.getModel().addTableModelListener(this);
        sequenceFileTable.getModel().addTableModelListener(this);
        this.format = format;
        //enable tabs based on format
        if (format.equals(OutputFormat.MZTAB)
                || format.equals(OutputFormat.PRIDE_MERGED_XML)
                || format.equals(OutputFormat.PRIDE_FILTERED_XML)) {
            //disable tabs for fasta / mztab / spectra
            fileTabbedPane.setEnabledAt(1, false);
            fileTabbedPane.setEnabledAt(2, false);
            fileTabbedPane.setEnabledAt(3, false);

            singleFastaFile.setEnabled(false);
            browseFastaFileButton.setEnabled(false);
            fastaFormatList.setEnabled(false);
            singleMzTabFile.setEnabled(false);
            browseMzTabButton.setEnabled(false);
            singleSpectrumFile.setEnabled(false);
            browseSpectrumFileButton.setEnabled(false);
            forceRegenerationBox.setEnabled(false);
        }

        //hide advanced options
        advancedOptionPanel.setVisible(false);

        //for merging xml files, always need more than one file
        if (format.equals(OutputFormat.PRIDE_MERGED_XML)) {
            showAdvancedFileSelection();
            singleModeLabel.setVisible(false);
        }

        //update fasta format values
        Vector<String> formats = new Vector<String>();
        for (HandlerFactory.FASTA_FORMAT fmt : HandlerFactory.FASTA_FORMAT.values()) {
            formats.add(fmt.getDisplayString());
        }
        fastaFormatList.setModel(new DefaultComboBoxModel(formats));
        fastaFormatList.setSelectedItem(HandlerFactory.FASTA_FORMAT.FULL.getDisplayString());

    }

    private Collection<File> chooseFiles(boolean allowMultipleSelection, boolean allowDirectory, FileFilter filter) {

        JFileChooser chooser = new JFileChooser();
        if (filter != null) {
            chooser.setFileFilter(filter);
        }
        chooser.setMultiSelectionEnabled(allowMultipleSelection);
        String lastUsedDirectory = ConverterData.getInstance().getLastSelectedDirectory();
        if (lastUsedDirectory != null) {
            chooser.setCurrentDirectory(new File(lastUsedDirectory));
        }

        if (allowDirectory) {
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        } else {
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        }
        int result = chooser.showOpenDialog(this);

        ArrayList<File> files = new ArrayList<File>();
        switch (result) {
            case JFileChooser.APPROVE_OPTION:
                ConverterData.getInstance().setLastSelectedDirectory(chooser.getCurrentDirectory().getAbsolutePath());
                if (allowMultipleSelection) {
                    files.addAll(Arrays.asList(chooser.getSelectedFiles()));
                } else {
                    files.add(chooser.getSelectedFile());
                }
                break;
            case JFileChooser.CANCEL_OPTION:
                break;
            case JFileChooser.ERROR_OPTION:
                break;
        }
        return files;

    }

    private void loadDataFiles(ActionEvent e) {
        Set<File> tableFiles = new TreeSet<File>();
        tableFiles.addAll(dataFileTable.getFiles());
        tableFiles.addAll(chooseFiles(true, ConverterData.getInstance().getDaoFormat().isAllowDirectory(), ConverterData.getInstance().getDaoFormat().getFilter()));
        dataFileTable.clearFiles();
        dataFileTable.addFiles(tableFiles);

        if (format.equals(OutputFormat.PRIDE_XML) && !Boolean.valueOf(PreferenceManager.getInstance().getProperty(PreferenceManager.PREFERENCE.IGNORE_MULTIPLE_FILE_EDITING)) && dataFileTable.getFiles().size() > 1) {
            MultipleFormEditingWarningDialog dialog = new MultipleFormEditingWarningDialog(NavigationPanel.getInstance());
            dialog.setVisible(true);
        }

    }

    private void loadSpectrumFiles(ActionEvent e) {
        Set<File> tableFiles = new HashSet<File>();
        tableFiles.addAll(chooseFiles(true, true, null));
        spectrumFileTable.clearFiles();
        spectrumFileTable.addFiles(tableFiles);
    }

    private void loadSequenceFiles(ActionEvent e) {
        Set<File> tableFiles = new HashSet<File>();
        //only 1 sequence file allowed per conversion
        //tableFiles.addAll(sequenceFileTable.getFiles());
        tableFiles.addAll(chooseFiles(false, false, new FastaFileFilter()));
        sequenceFileTable.clearFiles();
        sequenceFileTable.addFiles(tableFiles);
    }

    private void loadTabFiles(ActionEvent e) {
        Set<File> tableFiles = new TreeSet<File>();
        tableFiles.addAll(mzTabFileTable.getFiles());
        tableFiles.addAll(chooseFiles(true, false, new MzTabFileFilter()));
        mzTabFileTable.clearFiles();
        mzTabFileTable.addFiles(tableFiles);

        //if there are multiple tab files, report file generation is always forced
        if (mzTabFileTable.getFiles().size() > 1) {
            forceRegenerationBox.setSelected(true);
            forceRegenerationBox.setEnabled(false);
        } else {
            forceRegenerationBox.setSelected(false);
            forceRegenerationBox.setEnabled(true);
        }
    }

    private void fullIdButtonActionPerformed(ActionEvent e) {
        fastaFormat = HandlerFactory.FASTA_FORMAT.FULL;
    }

    private void firstWordButtonActionPerformed(ActionEvent e) {
        fastaFormat = HandlerFactory.FASTA_FORMAT.FIRST_WORD;
    }

    private void uniprotIdButtonActionPerformed(ActionEvent e) {
        fastaFormat = HandlerFactory.FASTA_FORMAT.UNIPROT_MATCH_ID;
    }

    private void uniprotAcButtonActionPerformed(ActionEvent e) {
        fastaFormat = HandlerFactory.FASTA_FORMAT.UNIPROT_MATCH_AC;
    }

    private void showHandCursor() {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void showDefaultCursor() {
        setCursor(Cursor.getDefaultCursor());
    }

    private void showSimpleFileSelection() {
        //empty out values
        dataFileTable.clearFiles();
        sequenceFileTable.clearFiles();
        mzTabFileTable.clearFiles();
        spectrumFileTable.clearFiles();

        //update validation listener
        if (validationListerner != null) {
            validationListerner.fireValidationListener(false);
        }

        //update UI
        remove(advancedFileSelectionPanel);
        add(singleFilePanel, BorderLayout.NORTH);
        singleFilePanel.revalidate();
        singleFilePanel.repaint();
        revalidate();
        repaint();

        singleFileSelectionMode = true;
    }

    private void showAdvancedFileSelection() {
        //empty out values
        singleFastaFile.setText(null);
        singleMzTabFile.setText(null);
        singleSourceFile.setText(null);
        singleSpectrumFile.setText(null);

        //update validation listener
        if (validationListerner != null) {
            validationListerner.fireValidationListener(false);
        }

        //update UI
        remove(singleFilePanel);
        add(advancedFileSelectionPanel, BorderLayout.NORTH);
        advancedFileSelectionPanel.revalidate();
        advancedFileSelectionPanel.repaint();
        revalidate();
        repaint();

        singleFileSelectionMode = false;
    }

    private void showHideLabelMouseClicked(MouseEvent e) {

        //get current state
        boolean isVisible = advancedOptionPanel.isVisible();

        //flip visibility state to panel and table options
        advancedOptionPanel.setVisible(!isVisible);
        if (isVisible) {
            //set viewport - switch to simple optiosn
            tableScrollPane.setViewportView(noAdvancedOptionTable);
            tableScrollPane.setColumnHeader(null);
        } else {
            //set viewport - switch to all optiosn
            tableScrollPane.setViewportView(allOptionTable);
            tableScrollPane.setColumnHeader(null);
        }

    }

    private void browseSpectrumFileButtonActionPerformed() {
        Collection<File> files = chooseFiles(false, false, null);
        if (!files.isEmpty()) {
            singleSpectrumFile.setText(files.iterator().next().getAbsolutePath());
        }
    }

    private void browseMzTabButtonActionPerformed() {
        boolean oldState = forceRegenerationBox.isSelected();
        Collection<File> files = chooseFiles(false, false, new MzTabFileFilter());
        if (!files.isEmpty()) {
            singleMzTabFile.setText(files.iterator().next().getAbsolutePath());
            forceRegenerationBox.setSelected(true);
            forceRegenerationBox.setEnabled(false);
        } else {
            forceRegenerationBox.setSelected(oldState);
            forceRegenerationBox.setEnabled(true);
        }
    }

    private void browseFastaFileButtonActionPerformed() {
        Collection<File> files = chooseFiles(false, false, new FastaFileFilter());
        if (!files.isEmpty()) {
            singleFastaFile.setText(files.iterator().next().getAbsolutePath());
        }
    }

    private void browseDataFileButtonActionPerformed() {
        Collection<File> files = chooseFiles(false, ConverterData.getInstance().getDaoFormat().isAllowDirectory(), ConverterData.getInstance().getDaoFormat().getFilter());
        if (!files.isEmpty()) {
            singleSourceFile.setText(files.iterator().next().getAbsolutePath());
            validationListerner.fireValidationListener(singleSourceFile.getText() != null && !"".equals(singleSourceFile.getText().trim()));
        }
    }

    private void singleSourceFileActionPerformed() {
        validationListerner.fireValidationListener(singleSourceFile.getText() != null && !"".equals(singleSourceFile.getText().trim()) && new File(singleSourceFile.getText().trim()).exists());
    }

    private void singleSourceFileFocusLost() {
        validationListerner.fireValidationListener(singleSourceFile.getText() != null && !"".equals(singleSourceFile.getText().trim()) && new File(singleSourceFile.getText().trim()).exists());
    }

    private void fastaFormatListItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            //update fasta format
            if (fastaFormatList.getSelectedItem() != null) {
                fastaFormat = HandlerFactory.FASTA_FORMAT.getFastaFormatByDisplayString(fastaFormatList.getSelectedItem().toString());
            } else {
                fastaFormat = HandlerFactory.FASTA_FORMAT.FULL;
            }
        }
    }

    //this will be used to configure the AccessionResolver in the report writer
    private void useHybridSearchDatabaseStateChanged() {
        ConverterData.getInstance().setUseHybridSearchDatabase(useHybridSearchDatabase.isSelected());
    }

    private void clearSourceFileTable() {
        dataFileTable.clearFiles();
    }

    private void clearMzTabFileTable() {
        mzTabFileTable.clearFiles();
    }

    private void clearSpectraFileTable() {
        spectrumFileTable.clearFiles();
    }

    private void initComponents() {
//        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
// Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("messages");
        singleFilePanel = new JPanel();
        dataFileLabel = new JLabel();
        singleSourceFile = new JTextField();
        browseDataFileButton = new JButton();
        label2 = new JLabel();
        singleFastaFile = new JTextField();
        browseFastaFileButton = new JButton();
        label3 = new JLabel();
        singleMzTabFile = new JTextField();
        browseMzTabButton = new JButton();
        label4 = new JLabel();
        singleSpectrumFile = new JTextField();
        browseSpectrumFileButton = new JButton();
        multipleModeLabel = new JLabel();
        fastaFormatList = new JComboBox();
        label1 = new JLabel();
        parserOptionPanel = new JPanel();
        tableScrollPane = new JScrollPane();
        parserOptionTable = new ParserOptionTable();
        showHideLabel = new JLabel();
        advancedOptionPanel = new JPanel();
        gridOptionPanel = new JPanel();
        forceRegenerationBox = new JCheckBox();
        useHybridSearchDatabase = new JCheckBox();
        parserOptionHelpButton = new JButton();
        label5 = new JLabel();
        advancedFileSelectionPanel = new JPanel();
        fileTabbedPane = new JTabbedPane();
        panel3 = new JPanel();
        scrollPane1 = new JScrollPane();
        dataFileTable = new FileTable();
        button1 = new JButton();
        button4 = new JButton();
        panel4 = new JPanel();
        scrollPane3 = new JScrollPane();
        sequenceFileTable = new FileTable();
        button3 = new JButton();
        panel6 = new JPanel();
        fullIdButton = new JRadioButton();
        firstWordButton = new JRadioButton();
        uniprotIdButton = new JRadioButton();
        uniprotAcButton = new JRadioButton();
        panel5 = new JPanel();
        scrollPane2 = new JScrollPane();
        mzTabFileTable = new FileTable();
        button2 = new JButton();
        button7 = new JButton();
        panel1 = new JPanel();
        scrollPane4 = new JScrollPane();
        spectrumFileTable = new FileTable();
        button5 = new JButton();
        button6 = new JButton();
        singleModeLabel = new JLabel();

//======== this ========
        setLayout(new BorderLayout());

//======== singleFilePanel ========
        {
            singleFilePanel.setBorder(null);
            singleFilePanel.setMinimumSize(new Dimension(555, 301));

            //---- dataFileLabel ----
            dataFileLabel.setText(bundle.getString("SelecFilePanel.dataFileLabel.text"));
            dataFileLabel.setToolTipText(bundle.getString("SelecFilePanel.dataFileLabel.toolTipText"));

            //---- singleSourceFile ----
            singleSourceFile.setToolTipText(bundle.getString("SelecFilePanel.singleSourceFile.toolTipText"));
            singleSourceFile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    singleSourceFileActionPerformed();
                }
            });
            singleSourceFile.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    singleSourceFileFocusLost();
                }
            });

            //---- browseDataFileButton ----
            browseDataFileButton.setText(bundle.getString("SelecFilePanel.browseDataFileButton.text"));
            browseDataFileButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    browseDataFileButtonActionPerformed();
                }
            });

            //---- label2 ----
            label2.setText(bundle.getString("SelecFilePanel.label2.text"));
            label2.setToolTipText(bundle.getString("SelecFilePanel.label2.toolTipText"));

            //---- singleFastaFile ----
            singleFastaFile.setToolTipText(bundle.getString("SelecFilePanel.singleFastaFile.toolTipText"));

            //---- browseFastaFileButton ----
            browseFastaFileButton.setText(bundle.getString("SelecFilePanel.browseFastaFileButton.text"));
            browseFastaFileButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    browseFastaFileButtonActionPerformed();
                }
            });

            //---- label3 ----
            label3.setText(bundle.getString("SelecFilePanel.label3.text"));
            label3.setToolTipText(bundle.getString("SelecFilePanel.label3.toolTipText"));

            //---- singleMzTabFile ----
            singleMzTabFile.setToolTipText(bundle.getString("SelecFilePanel.singleMzTabFile.toolTipText"));

            //---- browseMzTabButton ----
            browseMzTabButton.setText(bundle.getString("SelecFilePanel.browseMzTabButton.text"));
            browseMzTabButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    browseMzTabButtonActionPerformed();
                }
            });

            //---- label4 ----
            label4.setText(bundle.getString("SelecFilePanel.label4.text"));
            label4.setToolTipText(bundle.getString("SelecFilePanel.label4.toolTipText"));

            //---- singleSpectrumFile ----
            singleSpectrumFile.setToolTipText(bundle.getString("SelecFilePanel.singleSpectrumFile.toolTipText"));

            //---- browseSpectrumFileButton ----
            browseSpectrumFileButton.setText(bundle.getString("SelecFilePanel.browseSpectrumFileButton.text"));
            browseSpectrumFileButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    browseSpectrumFileButtonActionPerformed();
                }
            });

            //---- multipleModeLabel ----
            multipleModeLabel.setText(bundle.getString("SelecFilePanel.multipleModeLabel.text"));
            multipleModeLabel.setForeground(new Color(51, 51, 255));
            multipleModeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
            multipleModeLabel.setHorizontalAlignment(SwingConstants.LEFT);
            multipleModeLabel.setToolTipText(bundle.getString("SelecFilePanel.multipleModeLabel.toolTipText"));
            multipleModeLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showAdvancedFileSelection();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    showHandCursor();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    showDefaultCursor();
                }
            });

            //---- fastaFormatList ----
            fastaFormatList.setModel(new DefaultComboBoxModel(new String[]{
                    "Match Full ID Line",
                    "Match First Word",
                    "Match Uniprot AC",
                    "Match Uniprot ID"
            }));
            fastaFormatList.setToolTipText(bundle.getString("SelecFilePanel.fastaFormatList.toolTipText"));
            fastaFormatList.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    fastaFormatListItemStateChanged(e);
                }
            });

            //---- label1 ----
            label1.setText(bundle.getString("SelecFilePanel.label1.text"));
            label1.setForeground(Color.red);

            GroupLayout singleFilePanelLayout = new GroupLayout(singleFilePanel);
            singleFilePanel.setLayout(singleFilePanelLayout);
            singleFilePanelLayout.setHorizontalGroup(
                    singleFilePanelLayout.createParallelGroup()
                            .addGroup(singleFilePanelLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(singleFilePanelLayout.createParallelGroup()
                                            .addGroup(singleFilePanelLayout.createSequentialGroup()
                                                    .addGroup(singleFilePanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                            .addComponent(label3)
                                                            .addComponent(label2)
                                                            .addComponent(label4)
                                                            .addComponent(dataFileLabel))
                                                    .addGap(12, 12, 12)
                                                    .addGroup(singleFilePanelLayout.createParallelGroup()
                                                            .addComponent(singleMzTabFile, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE)
                                                            .addComponent(singleSpectrumFile, GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE)
                                                            .addGroup(singleFilePanelLayout.createSequentialGroup()
                                                                    .addComponent(singleFastaFile, GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addComponent(fastaFormatList, 0, 124, Short.MAX_VALUE))
                                                            .addGroup(singleFilePanelLayout.createSequentialGroup()
                                                                    .addComponent(singleSourceFile, GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE)
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addComponent(label1, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)))
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(singleFilePanelLayout.createParallelGroup()
                                                            .addComponent(browseDataFileButton)
                                                            .addComponent(browseFastaFileButton)
                                                            .addComponent(browseMzTabButton)
                                                            .addComponent(browseSpectrumFileButton))
                                                    .addGap(6, 6, 6))
                                            .addGroup(GroupLayout.Alignment.TRAILING, singleFilePanelLayout.createSequentialGroup()
                                                    .addComponent(multipleModeLabel)
                                                    .addContainerGap())))
            );
            singleFilePanelLayout.setVerticalGroup(
                    singleFilePanelLayout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, singleFilePanelLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(multipleModeLabel)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                                    .addGroup(singleFilePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(singleSourceFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(browseDataFileButton)
                                            .addComponent(dataFileLabel)
                                            .addComponent(label1))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(singleFilePanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                            .addGroup(singleFilePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(singleFastaFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(browseFastaFileButton)
                                                    .addComponent(label2))
                                            .addComponent(fastaFormatList, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(singleFilePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(singleMzTabFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(browseMzTabButton)
                                            .addComponent(label3))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(singleFilePanelLayout.createParallelGroup()
                                            .addGroup(singleFilePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(singleSpectrumFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(label4))
                                            .addComponent(browseSpectrumFileButton))
                                    .addContainerGap())
            );
        }
        add(singleFilePanel, BorderLayout.NORTH);

//======== parserOptionPanel ========
        {
            parserOptionPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

            //======== tableScrollPane ========
            {
                tableScrollPane.setViewportView(parserOptionTable);
            }

            //---- showHideLabel ----
            showHideLabel.setText(bundle.getString("SelecFilePanel.showHideLabel.text"));
            showHideLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
            showHideLabel.setForeground(new Color(51, 51, 255));
            showHideLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showHideLabelMouseClicked(e);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    showHandCursor();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    showDefaultCursor();
                }
            });

            //======== advancedOptionPanel ========
            {
                advancedOptionPanel.setBorder(null);

                //======== gridOptionPanel ========
                {
                    gridOptionPanel.setLayout(new GridLayout(2, 0));

                    //---- forceRegenerationBox ----
                    forceRegenerationBox.setText(bundle.getString("SelecFilePanel.forceRegenerationBox.text"));
                    forceRegenerationBox.setHorizontalAlignment(SwingConstants.LEFT);
                    gridOptionPanel.add(forceRegenerationBox);

                    //---- useHybridSearchDatabase ----
                    useHybridSearchDatabase.setText(bundle.getString("SelecFilePanel.useHybridSearchDatabase.text"));
                    useHybridSearchDatabase.setSelected(true);
                    useHybridSearchDatabase.setHorizontalAlignment(SwingConstants.LEFT);
                    useHybridSearchDatabase.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            useHybridSearchDatabaseStateChanged();
                        }
                    });
                    gridOptionPanel.add(useHybridSearchDatabase);
                }

                GroupLayout advancedOptionPanelLayout = new GroupLayout(advancedOptionPanel);
                advancedOptionPanel.setLayout(advancedOptionPanelLayout);
                advancedOptionPanelLayout.setHorizontalGroup(
                        advancedOptionPanelLayout.createParallelGroup()
                                .addGroup(advancedOptionPanelLayout.createSequentialGroup()
                                        .addComponent(gridOptionPanel, GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
                                        .addContainerGap())
                );
                advancedOptionPanelLayout.setVerticalGroup(
                        advancedOptionPanelLayout.createParallelGroup()
                                .addGroup(advancedOptionPanelLayout.createSequentialGroup()
                                        .addComponent(gridOptionPanel, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(13, Short.MAX_VALUE))
                );
            }

            //---- parserOptionHelpButton ----
            parserOptionHelpButton.setText(bundle.getString("SelecFilePanel.parserOptionHelpButton.text"));
            parserOptionHelpButton.setEnabled(false);

            //---- label5 ----
            label5.setText(bundle.getString("SelecFilePanel.label5.text"));

            GroupLayout parserOptionPanelLayout = new GroupLayout(parserOptionPanel);
            parserOptionPanel.setLayout(parserOptionPanelLayout);
            parserOptionPanelLayout.setHorizontalGroup(
                    parserOptionPanelLayout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, parserOptionPanelLayout.createSequentialGroup()
                                    .addGroup(parserOptionPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                            .addGroup(parserOptionPanelLayout.createSequentialGroup()
                                                    .addContainerGap()
                                                    .addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE))
                                            .addGroup(parserOptionPanelLayout.createSequentialGroup()
                                                    .addGap(1, 1, 1)
                                                    .addComponent(advancedOptionPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(parserOptionHelpButton, GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE))
                                            .addGroup(parserOptionPanelLayout.createSequentialGroup()
                                                    .addContainerGap()
                                                    .addComponent(label5)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 360, Short.MAX_VALUE)
                                                    .addComponent(showHideLabel)))
                                    .addContainerGap())
            );
            parserOptionPanelLayout.setVerticalGroup(
                    parserOptionPanelLayout.createParallelGroup()
                            .addGroup(parserOptionPanelLayout.createSequentialGroup()
                                    .addGroup(parserOptionPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(showHideLabel)
                                            .addComponent(label5))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(parserOptionPanelLayout.createParallelGroup()
                                            .addComponent(parserOptionHelpButton)
                                            .addComponent(advancedOptionPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
            );
        }
        add(parserOptionPanel, BorderLayout.CENTER);

//======== advancedFileSelectionPanel ========
        {
            advancedFileSelectionPanel.setBorder(null);
            advancedFileSelectionPanel.setForeground(new Color(255, 0, 51));

            //======== fileTabbedPane ========
            {

                //======== panel3 ========
                {

                    //======== scrollPane1 ========
                    {

                        //---- dataFileTable ----
                        dataFileTable.setToolTipText(bundle.getString("SelecFilePanel.dataFileTable.toolTipText"));
                        scrollPane1.setViewportView(dataFileTable);
                    }

                    //---- button1 ----
                    button1.setText(bundle.getString("SelecFilePanel.button1.text"));
                    button1.setToolTipText(bundle.getString("SelecFilePanel.button1.toolTipText"));
                    button1.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            loadDataFiles(e);
                        }
                    });

                    //---- button4 ----
                    button4.setText(bundle.getString("SelecFilePanel.button4.text"));
                    button4.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            clearSourceFileTable();
                        }
                    });

                    GroupLayout panel3Layout = new GroupLayout(panel3);
                    panel3.setLayout(panel3Layout);
                    panel3Layout.setHorizontalGroup(
                            panel3Layout.createParallelGroup()
                                    .addGroup(GroupLayout.Alignment.TRAILING, panel3Layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                    .addGroup(panel3Layout.createSequentialGroup()
                                                            .addComponent(button4)
                                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(button1, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                                                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 622, Short.MAX_VALUE))
                                            .addContainerGap())
                    );
                    panel3Layout.setVerticalGroup(
                            panel3Layout.createParallelGroup()
                                    .addGroup(panel3Layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(button1)
                                                    .addComponent(button4))
                                            .addContainerGap())
                    );
                }
                fileTabbedPane.addTab(bundle.getString("SelecFilePanel.panel3.tab.title"), panel3);


                //======== panel4 ========
                {

                    //======== scrollPane3 ========
                    {

                        //---- sequenceFileTable ----
                        sequenceFileTable.setToolTipText(bundle.getString("SelecFilePanel.sequenceFileTable.toolTipText"));
                        scrollPane3.setViewportView(sequenceFileTable);
                    }

                    //---- button3 ----
                    button3.setText(bundle.getString("SelecFilePanel.button3.text"));
                    button3.setToolTipText(bundle.getString("SelecFilePanel.button3.toolTipText"));
                    button3.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            loadSequenceFiles(e);
                        }
                    });

                    //======== panel6 ========
                    {
                        panel6.setBorder(new TitledBorder(bundle.getString("SelecFilePanel.panel6.border")));
                        panel6.setLayout(new FlowLayout());

                        //---- fullIdButton ----
                        fullIdButton.setText(bundle.getString("SelecFilePanel.fullIdButton.text"));
                        fullIdButton.setSelected(true);
                        fullIdButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                fullIdButtonActionPerformed(e);
                            }
                        });
                        panel6.add(fullIdButton);

                        //---- firstWordButton ----
                        firstWordButton.setText(bundle.getString("SelecFilePanel.firstWordButton.text"));
                        firstWordButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                firstWordButtonActionPerformed(e);
                            }
                        });
                        panel6.add(firstWordButton);

                        //---- uniprotIdButton ----
                        uniprotIdButton.setText(bundle.getString("SelecFilePanel.uniprotIdButton.text"));
                        uniprotIdButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                uniprotIdButtonActionPerformed(e);
                            }
                        });
                        panel6.add(uniprotIdButton);

                        //---- uniprotAcButton ----
                        uniprotAcButton.setText(bundle.getString("SelecFilePanel.uniprotAcButton.text"));
                        uniprotAcButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                uniprotAcButtonActionPerformed(e);
                            }
                        });
                        panel6.add(uniprotAcButton);
                    }

                    GroupLayout panel4Layout = new GroupLayout(panel4);
                    panel4.setLayout(panel4Layout);
                    panel4Layout.setHorizontalGroup(
                            panel4Layout.createParallelGroup()
                                    .addGroup(panel4Layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(panel4Layout.createParallelGroup()
                                                    .addComponent(button3, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(scrollPane3, GroupLayout.DEFAULT_SIZE, 622, Short.MAX_VALUE)
                                                    .addComponent(panel6, GroupLayout.DEFAULT_SIZE, 622, Short.MAX_VALUE))
                                            .addContainerGap())
                    );
                    panel4Layout.setVerticalGroup(
                            panel4Layout.createParallelGroup()
                                    .addGroup(GroupLayout.Alignment.TRAILING, panel4Layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addComponent(panel6, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(scrollPane3, GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(button3)
                                            .addContainerGap())
                    );
                }
                fileTabbedPane.addTab(bundle.getString("SelecFilePanel.panel4.tab.title"), panel4);


                //======== panel5 ========
                {

                    //======== scrollPane2 ========
                    {

                        //---- mzTabFileTable ----
                        mzTabFileTable.setToolTipText(bundle.getString("SelecFilePanel.mzTabFileTable.toolTipText"));
                        scrollPane2.setViewportView(mzTabFileTable);
                    }

                    //---- button2 ----
                    button2.setText(bundle.getString("SelecFilePanel.button2.text"));
                    button2.setToolTipText(bundle.getString("SelecFilePanel.button2.toolTipText"));
                    button2.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            loadTabFiles(e);
                        }
                    });

                    //---- button7 ----
                    button7.setText(bundle.getString("SelecFilePanel.button7.text"));
                    button7.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            clearMzTabFileTable();
                        }
                    });

                    GroupLayout panel5Layout = new GroupLayout(panel5);
                    panel5.setLayout(panel5Layout);
                    panel5Layout.setHorizontalGroup(
                            panel5Layout.createParallelGroup()
                                    .addGroup(GroupLayout.Alignment.TRAILING, panel5Layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(panel5Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                    .addComponent(scrollPane2, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 622, Short.MAX_VALUE)
                                                    .addGroup(panel5Layout.createSequentialGroup()
                                                            .addComponent(button7)
                                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(button2, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)))
                                            .addContainerGap())
                    );
                    panel5Layout.setVerticalGroup(
                            panel5Layout.createParallelGroup()
                                    .addGroup(GroupLayout.Alignment.TRAILING, panel5Layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(panel5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(button2)
                                                    .addComponent(button7))
                                            .addContainerGap())
                    );
                }
                fileTabbedPane.addTab(bundle.getString("SelecFilePanel.panel5.tab.title"), panel5);


                //======== panel1 ========
                {

                    //======== scrollPane4 ========
                    {

                        //---- spectrumFileTable ----
                        spectrumFileTable.setToolTipText(bundle.getString("SelecFilePanel.spectrumFileTable.toolTipText"));
                        scrollPane4.setViewportView(spectrumFileTable);
                    }

                    //---- button5 ----
                    button5.setText(bundle.getString("SelecFilePanel.button5.text"));
                    button5.setToolTipText(bundle.getString("SelecFilePanel.button5.toolTipText"));
                    button5.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            loadSpectrumFiles(e);
                        }
                    });

                    //---- button6 ----
                    button6.setText(bundle.getString("SelecFilePanel.button6.text"));
                    button6.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            clearSpectraFileTable();
                        }
                    });

                    GroupLayout panel1Layout = new GroupLayout(panel1);
                    panel1.setLayout(panel1Layout);
                    panel1Layout.setHorizontalGroup(
                            panel1Layout.createParallelGroup()
                                    .addGroup(panel1Layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(panel1Layout.createParallelGroup()
                                                    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                                            .addComponent(button6)
                                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(button5, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                                                    .addComponent(scrollPane4, GroupLayout.DEFAULT_SIZE, 622, Short.MAX_VALUE))
                                            .addContainerGap())
                    );
                    panel1Layout.setVerticalGroup(
                            panel1Layout.createParallelGroup()
                                    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addComponent(scrollPane4, GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(button5)
                                                    .addComponent(button6))
                                            .addContainerGap())
                    );
                }
                fileTabbedPane.addTab(bundle.getString("SelecFilePanel.panel1.tab.title"), panel1);

            }

            //---- singleModeLabel ----
            singleModeLabel.setText(bundle.getString("SelecFilePanel.singleModeLabel.text"));
            singleModeLabel.setForeground(new Color(51, 51, 255));
            singleModeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
            singleModeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            singleModeLabel.setToolTipText(bundle.getString("SelecFilePanel.singleModeLabel.toolTipText"));
            singleModeLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showSimpleFileSelection();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    showHandCursor();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    showDefaultCursor();
                }
            });

            GroupLayout advancedFileSelectionPanelLayout = new GroupLayout(advancedFileSelectionPanel);
            advancedFileSelectionPanel.setLayout(advancedFileSelectionPanelLayout);
            advancedFileSelectionPanelLayout.setHorizontalGroup(
                    advancedFileSelectionPanelLayout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, advancedFileSelectionPanelLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(advancedFileSelectionPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                            .addComponent(fileTabbedPane, GroupLayout.DEFAULT_SIZE, 651, Short.MAX_VALUE)
                                            .addComponent(singleModeLabel))
                                    .addContainerGap())
            );
            advancedFileSelectionPanelLayout.setVerticalGroup(
                    advancedFileSelectionPanelLayout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, advancedFileSelectionPanelLayout.createSequentialGroup()
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(singleModeLabel)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(fileTabbedPane, GroupLayout.PREFERRED_SIZE, 216, GroupLayout.PREFERRED_SIZE)
                                    .addContainerGap())
            );
        }

//---- buttonGroup2 ----
        ButtonGroup buttonGroup2 = new ButtonGroup();
        buttonGroup2.add(fullIdButton);
        buttonGroup2.add(firstWordButton);
        buttonGroup2.add(uniprotIdButton);
        buttonGroup2.add(uniprotAcButton);
//        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel singleFilePanel;
    private JLabel dataFileLabel;
    private JTextField singleSourceFile;
    private JButton browseDataFileButton;
    private JLabel label2;
    private JTextField singleFastaFile;
    private JButton browseFastaFileButton;
    private JLabel label3;
    private JTextField singleMzTabFile;
    private JButton browseMzTabButton;
    private JLabel label4;
    private JTextField singleSpectrumFile;
    private JButton browseSpectrumFileButton;
    private JLabel multipleModeLabel;
    private JComboBox fastaFormatList;
    private JLabel label1;
    private JPanel parserOptionPanel;
    private JScrollPane tableScrollPane;
    private ParserOptionTable parserOptionTable;
    private JLabel showHideLabel;
    private JPanel advancedOptionPanel;
    private JPanel gridOptionPanel;
    private JCheckBox forceRegenerationBox;
    private JCheckBox useHybridSearchDatabase;
    private JButton parserOptionHelpButton;
    private JLabel label5;
    private JPanel advancedFileSelectionPanel;
    private JTabbedPane fileTabbedPane;
    private JPanel panel3;
    private JScrollPane scrollPane1;
    private FileTable dataFileTable;
    private JButton button1;
    private JButton button4;
    private JPanel panel4;
    private JScrollPane scrollPane3;
    private FileTable sequenceFileTable;
    private JButton button3;
    private JPanel panel6;
    private JRadioButton fullIdButton;
    private JRadioButton firstWordButton;
    private JRadioButton uniprotIdButton;
    private JRadioButton uniprotAcButton;
    private JPanel panel5;
    private JScrollPane scrollPane2;
    private FileTable mzTabFileTable;
    private JButton button2;
    private JButton button7;
    private JPanel panel1;
    private JScrollPane scrollPane4;
    private FileTable spectrumFileTable;
    private JButton button5;
    private JButton button6;
    private JLabel singleModeLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    private Properties getOptions() {
        ParserOptionTableModel model = (ParserOptionTableModel) allOptionTable.getModel();
        Properties props = model.getProperties();
        logger.debug("Parsed options from panel: " + props);
        return props;
    }

    @Override
    public Collection<ValidatorMessage> validateForm() {

//        System.err.println(getOptions());

        //if we're running the merger
        if (format.equals(OutputFormat.PRIDE_MERGED_XML)) {
            //check to see that we have at least two files
            if (dataFileTable.getFiles().size() < 2) {
                Collection<ValidatorMessage> msgs = new ArrayList<ValidatorMessage>();
                ValidatorMessage error = new ValidatorMessage("You must provide at least two files to merge", MessageLevel.ERROR, new Context("Pride Merger"), new PrideMergerFileRule());
                msgs.add(error);
                return msgs;
            }
        }

        if (format.equals(OutputFormat.PRIDE_XML)) {
            //check that possible mztab files are generated with the same options

            if (singleFileSelectionMode) {
                //if we have tab files, we need to check to see if the DAO options are consistently set
                if (singleMzTabFile.getText() != null && !"".equals(singleMzTabFile.getText())) {
                    Properties tabOptions = MzTabHandler.readDaoConfigrationFromFile(new File(singleMzTabFile.getText()));
                    if (tabOptions != null && !tabOptions.equals(getOptions())) {
                        return Collections.singleton(new ValidatorMessage("mzTab files generated with a different set of DAO options than those set for XML conversion", MessageLevel.ERROR, new Context("DAO Options"), new DAOOptionRule()));
                    }
                }

            } else {
                //if we have tab files, we need to check to see if the DAO options are consistently set
                for (File tabFile : mzTabFileTable.getFiles()) {
                    Properties tabOptions = MzTabHandler.readDaoConfigrationFromFile(tabFile);
                    if (tabOptions != null && !tabOptions.equals(getOptions())) {
                        return Collections.singleton(new ValidatorMessage("mzTab files generated with a different set of DAO options than those set for XML conversion", MessageLevel.ERROR, new Context("DAO Options"), new DAOOptionRule()));
                    }
                }
            }
        }

        //otherwise nothing to validate
        return Collections.emptyList();
    }

    @Override
    public void clear() {
        dataFileTable.clearFiles();
        mzTabFileTable.clearFiles();
        sequenceFileTable.clearFiles();

        //reset DAO options
        updateOptionTable();

        revalidate();
        repaint();

        //inactivate next button
        //validationListerner.fireValidationListener(false);
        //the table listener will fire the validation listener
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
        return "File Selection";
    }

    @Override
    public String getFormDescription() {
        return config.getString("fileselection.form.description");
    }

    @Override
    public Icon getFormIcon() {
        return getFormIcon("fileselection.form.icon");
    }

    @Override
    public String getHelpResource() {
        return "help.ui.file";
    }

    @Override
    public void start() {

        //update UI tabbed pane
        fileTabbedPane.setTitleAt(0, ConverterData.getInstance().getDaoFormat().getNiceName() + " files");
        dataFileLabel.setText(ConverterData.getInstance().getDaoFormat().getNiceName() + " file");

        //update option table
        updateOptionTable();

        //enable the spectrum file tab on request
        if (ConverterData.getInstance().getDaoFormat().isAllowExternalSpectra()) {
            fileTabbedPane.setEnabledAt(3, true);
            singleSpectrumFile.setEnabled(true);
            browseSpectrumFileButton.setEnabled(true);
        } else {
            fileTabbedPane.setEnabledAt(3, false);
            singleSpectrumFile.setEnabled(false);
            browseSpectrumFileButton.setEnabled(false);
        }

        if (singleFileSelectionMode) {
            //fire the validation listener based on if there is a file selected
            validationListerner.fireValidationListener(singleSourceFile.getText() != null && !"".equals(singleSourceFile.getText().trim()) && new File(singleSourceFile.getText().trim()).exists());
        } else {
            //fire the table listener - this is required if users go back & forth without changing the
            //table content
            tableChanged(new TableModelEvent(dataFileTable.getModel()));
        }

    }

    @Override
    public void finish() throws GUIException {

        //clear existing ConverterData info
        ConverterData.getInstance().clearPossibleStaleData();

        switch (format) {

            //generate mztab files
            case MZTAB:
                if (singleFileSelectionMode) {
                    IOUtilities.generateMzTabFiles(getOptions(), Collections.singleton(new File(singleSourceFile.getText().trim())));
                } else {
                    IOUtilities.generateMzTabFiles(getOptions(), dataFileTable.getFiles());
                }
                break;

            //will be generating pride xml file in the end
            case PRIDE_XML:

                //store options for later use
                ConverterData.getInstance().setOptions(getOptions());
                ConverterData.getInstance().setFastaFormat(fastaFormat);

                if (singleFileSelectionMode) {

                    //create filebean with source file
                    FileBean fileBean = new FileBean(new File(singleSourceFile.getText()).getAbsolutePath());
                    //update sequence file
                    if (singleFastaFile.getText() != null && !"".equals(singleFastaFile.getText().trim())) {
                        File fasta = new File(singleFastaFile.getText());
                        if (fasta.exists()) {
                            fileBean.setSequenceFile(fasta.getAbsolutePath());
                        }
                    }
                    //update tab file
                    if (singleMzTabFile.getText() != null && !"".equals(singleMzTabFile.getText().trim())) {
                        File tab = new File(singleMzTabFile.getText());
                        if (tab.exists()) {
                            fileBean.setMzTabFile(tab.getAbsolutePath());
                            logger.warn("Tab file selected, new report file generation turned on");
                            forceRegenerationBox.setSelected(true);
                            forceRegenerationBox.setEnabled(false);
                        }
                    }
                    //update spectrum file
                    if (singleSpectrumFile.getText() != null && !"".equals(singleSpectrumFile.getText().trim())) {
                        File spectrum = new File(singleSpectrumFile.getText());
                        if (spectrum.exists()) {
                            fileBean.setSpectrumFile(spectrum.getAbsolutePath());
                        }
                    }
                    ConverterData.getInstance().getDataFiles().add(fileBean);

                    //because of back/forth navigation, we might have multiple forms that we don't need anymore
                    //ensure that they're cleared out
                    NavigationPanel.getInstance().deregisterForm(new MzTabFileMappingForm());
                    NavigationPanel.getInstance().deregisterForm(new SpectrumFileMappingForm());
                    NavigationPanel.getInstance().deregisterForm(new ExperimentDetailMultipleDataForm());

                    //convert single file
                    IOUtilities.generateReportFiles(getOptions(), ConverterData.getInstance().getDataFiles(), forceRegenerationBox.isSelected(), true);

                } else {

                    File sequenceFile = null;
                    //we only have 1/0 sequence file
                    if (!sequenceFileTable.getFiles().isEmpty()) {
                        //store path and fasta type
                        sequenceFile = sequenceFileTable.getFiles().iterator().next();
                    }

                    //if we have several mztab files, don't generate the report files now as there will be a subsequent
                    //step where the user must confirm the input file/mztab file assignment. The report generation will
                    //be done in the next step
                    boolean hasMultipleTabFiles = false;
                    MzTabFileMappingForm mzTabFileMappingForm = null;
                    if (mzTabFileTable.getFiles().size() > 1) {

                        //update flag
                        hasMultipleTabFiles = true;

                        //only store input files, the logic will be handled by other form
                        for (File inputFile : dataFileTable.getFiles()) {
                            FileBean fileBean = new FileBean(inputFile.getAbsolutePath());
                            if (sequenceFile != null) {
                                fileBean.setSequenceFile(sequenceFile.getAbsolutePath());
                            }
                            ConverterData.getInstance().getDataFiles().add(fileBean);
                        }

                        //store mztab files externally to fileBeans
                        for (File mzTabFile : mzTabFileTable.getFiles()) {
                            ConverterData.getInstance().getMztabFiles().add(mzTabFile.getAbsolutePath());
                            logger.warn("Tab file selected, new report file generation turned on");
                            forceRegenerationBox.setSelected(true);
                        }

                        //register a new form to deal with the mapping
                        mzTabFileMappingForm = new MzTabFileMappingForm();
                        NavigationPanel.getInstance().registerFormAfter(mzTabFileMappingForm, this);

                    } else {

                        //depending on form navigation, we might have a mztab mapping form - deregister it!
                        NavigationPanel.getInstance().deregisterForm(new MzTabFileMappingForm());

                        File mzTabFile = null;
                        //we only have 1/0 mztab file
                        if (!mzTabFileTable.getFiles().isEmpty()) {
                            mzTabFile = mzTabFileTable.getFiles().iterator().next();
                        }
                        //if there is only one mztab file, it will be added to all input files
                        for (File inputFile : dataFileTable.getFiles()) {
                            FileBean fileBean = new FileBean(inputFile.getAbsolutePath());
                            if (sequenceFile != null) {
                                fileBean.setSequenceFile(sequenceFile.getAbsolutePath());
                            }
                            if (mzTabFile != null) {
                                fileBean.setMzTabFile(mzTabFile.getAbsolutePath());
                                logger.warn("Tab file selected, new report file generation turned on");
                                forceRegenerationBox.setSelected(true);
                            }
                            ConverterData.getInstance().getDataFiles().add(fileBean);
                        }

                    }

                    //if we have several spectrum files, don't generate the report files now as there will be a subsequent
                    //step where the user must confirm the input file/spectrum file assignment. The report generation will
                    //be done in the next step
                    boolean hasMultipleSpectrumFiles = false;
                    if (spectrumFileTable.getFiles().size() > 1) {

                        //update flag
                        hasMultipleSpectrumFiles = true;

                        //only store input files, the logic will be handled by other form
                        //only store the input files if they haven't already been stored in the mztab block above
                        if (ConverterData.getInstance().getDataFiles().isEmpty()) {
                            for (File inputFile : dataFileTable.getFiles()) {
                                FileBean fileBean = new FileBean(inputFile.getAbsolutePath());
                                if (sequenceFile != null) {
                                    fileBean.setSequenceFile(sequenceFile.getAbsolutePath());
                                }
                                ConverterData.getInstance().getDataFiles().add(fileBean);
                            }
                        }

                        //store spectra files externally to fileBeans
                        for (File spectrumFile : spectrumFileTable.getFiles()) {
                            ConverterData.getInstance().getSpectrumFiles().add(spectrumFile.getAbsolutePath());
                        }

                        //register a new form to deal with the mapping
                        if (hasMultipleTabFiles) {
                            NavigationPanel.getInstance().registerFormAfter(new SpectrumFileMappingForm(), mzTabFileMappingForm);
                            mzTabFileMappingForm.setDeferReportFileGeneration(true);
                        } else {
                            NavigationPanel.getInstance().registerFormAfter(new SpectrumFileMappingForm(), this);
                        }

                    } else {

                        //depending on form navigation, we might have a spectrum mapping form - deregister it!
                        NavigationPanel.getInstance().deregisterForm(new SpectrumFileMappingForm());

                        File spectrumFile = null;
                        //we only have 1/0 mztab file
                        if (!spectrumFileTable.getFiles().isEmpty()) {
                            spectrumFile = spectrumFileTable.getFiles().iterator().next();
                        }

                        //if there is only one spectrum file, it will be added to all input files
                        if (ConverterData.getInstance().getDataFiles().isEmpty()) {
                            //if beans haven't been stored yet, do so now
                            for (File inputFile : dataFileTable.getFiles()) {
                                FileBean fileBean = new FileBean(inputFile.getAbsolutePath());
                                if (sequenceFile != null) {
                                    fileBean.setSequenceFile(sequenceFile.getAbsolutePath());
                                }
                                if (spectrumFile != null) {
                                    fileBean.setSpectrumFile(spectrumFile.getAbsolutePath());
                                }
                                ConverterData.getInstance().getDataFiles().add(fileBean);
                            }
                        } else {
                            //otherwise just update the existing beans
                            if (spectrumFile != null) {
                                for (FileBean fileBean : ConverterData.getInstance().getDataFiles()) {
                                    fileBean.setSpectrumFile(spectrumFile.getAbsolutePath());
                                }
                            }
                        }

                    }

                    if (!hasMultipleTabFiles && !hasMultipleSpectrumFiles) {
                        IOUtilities.generateReportFiles(getOptions(), ConverterData.getInstance().getDataFiles(), forceRegenerationBox.isSelected(), true);
                    }

                }
                break;

            //merging pride xml files
            case PRIDE_MERGED_XML:
                //merging will be done in next form
                ConverterData.getInstance().setOptions(getOptions());
                for (File inputFile : dataFileTable.getFiles()) {
                    FileBean fileBean = new FileBean(inputFile.getAbsolutePath());
                    ConverterData.getInstance().getDataFiles().add(fileBean);
                }

                break;

            //filtering pride xml files
            case PRIDE_FILTERED_XML:
                //only store input files, the logic will be handled by other form
                if (singleFileSelectionMode) {
                    FileBean fileBean = new FileBean(new File(singleSourceFile.getText()).getAbsolutePath());
                } else {
                    for (File inputFile : dataFileTable.getFiles()) {
                        FileBean fileBean = new FileBean(inputFile.getAbsolutePath());
                        ConverterData.getInstance().getDataFiles().add(fileBean);
                    }
                }
                break;
            default:
                throw new UnsupportedOperationException("No handler defined for " + format);
        }

        //make sure there is no stale form data in subsequent forms
        NavigationPanel.getInstance().clearAllFromForm(this);

    }

    /**
     * This fine grain notification tells listeners the exact range
     * of cells, rows, or columns that changed.
     */
    @Override
    public void tableChanged(TableModelEvent e) {
        //will be called by each file table but we only care about the data files for now
        if (e.getSource().equals(dataFileTable.getModel())) {
            validationListerner.fireValidationListener(dataFileTable.getFiles().size() > 0);
        }
    }

    private void updateOptionTable() {

        ArrayList<DAOProperty> props = new ArrayList<DAOProperty>();

        //if we're exporting to mztab, add those properties at the start of the list
        if (format.equals(OutputFormat.MZTAB)) {
            //add mztab-specific options
            DAOProperty<String> gelIdentifier = new DAOProperty<String>(IOUtilities.GEL_IDENTIFIER, null);
            gelIdentifier.setDescription("Sets the gel identifier to be used for identifications in the generated mzTab file. This option only takes effect when generating mzTab files.");
            props.add(gelIdentifier);

            DAOProperty<String> spotIdentifier = new DAOProperty<String>(IOUtilities.SPOT_IDENTIFIER, null);
            spotIdentifier.setDescription("Sets the gel spot identifier to be used for identifications in the generated mzTab file. This option only takes effect when generating mzTab files. This option is ignored if gel_spot_regex is set.");
            props.add(spotIdentifier);

            DAOProperty<String> spotRegex = new DAOProperty<String>(IOUtilities.SPOT_REGULAR_EXPRESSION, null);
            spotRegex.setDescription("Used to extract the gel spot identifier based on the sourcefile's name. The first matching group in the pattern is used as a spot identifier.");
            props.add(spotRegex);

            DAOProperty<String> generateQuantFields = new DAOProperty<String>(IOUtilities.GENERATE_QUANT_FIELDS, "0");
            generateQuantFields.setDescription("Adds (empty) quantitative fields to the generated mzTab file for the number of specified reagents.");
            props.add(generateQuantFields);
        }

        //if we're merging XML, add those properties at the start of the list
        if (format.equals(OutputFormat.PRIDE_MERGED_XML)) {
            //add mztab-specific options
            DAOProperty<Boolean> compress = new DAOProperty<Boolean>(IOUtilities.COMPRESS, false);
            compress.setDescription("Turn on gzip compression for output file.");
            props.add(compress);
        }

        //add dao-specific properties
        if (DAOFactory.getInstance().getSupportedProperties(ConverterData.getInstance().getDaoFormat()) != null) {
            props.addAll(DAOFactory.getInstance().getSupportedProperties(ConverterData.getInstance().getDaoFormat()));
        }


        //create two views on the same properties
        allOptionTable = new ParserOptionTable(props, true);
        noAdvancedOptionTable = new ParserOptionTable(props, false);

        //update cell editor
        allOptionTable.getColumn("Property Value").setCellEditor(new ParserOptionCellEditor());
        noAdvancedOptionTable.getColumn("Property Value").setCellEditor(new ParserOptionCellEditor());

        //don't show table headers
        allOptionTable.setTableHeader(null);
        noAdvancedOptionTable.setTableHeader(null);

        //backend will now have same value map so that the values will be consistently shared across both tables
        ParserOptionTableModel allOptionTableModel = (ParserOptionTableModel) allOptionTable.getModel();
        ParserOptionTableModel noAdvancedOptionTableModel = (ParserOptionTableModel) noAdvancedOptionTable.getModel();
        noAdvancedOptionTableModel.setValues(allOptionTableModel.getValues());

        //set viewport - no advanced options by default
        tableScrollPane.setViewportView(noAdvancedOptionTable);
        tableScrollPane.setColumnHeader(null);

        //update action for "explain option" button
        boolean explainOptionsActive = false;
        String helpResourceID = null;

        if (format.equals(OutputFormat.PRIDE_MERGED_XML)) {
            explainOptionsActive = true;
            helpResourceID = "help.ui.merger.options";
        } else if (format.equals(OutputFormat.MZTAB)) {
            explainOptionsActive = true;
            if (ConverterData.getInstance().getDaoFormat().getHelpResource() != null) {
                helpResourceID = ConverterData.getInstance().getDaoFormat().getHelpResource();
            } else {
                helpResourceID = "help.ui.mztab.options";
            }
        } else if (format.equals(OutputFormat.PRIDE_FILTERED_XML)) {
            explainOptionsActive = false;
        } else {
            explainOptionsActive = (ConverterData.getInstance().getDaoFormat().getHelpResource() != null);
            helpResourceID = ConverterData.getInstance().getDaoFormat().getHelpResource();
        }

        if (explainOptionsActive) {
            parserOptionHelpButton.setEnabled(true);
            CSH.setHelpIDString(parserOptionHelpButton, helpResourceID);
            parserOptionHelpButton.addActionListener(new CSH.DisplayHelpFromSource(NavigationPanel.getInstance().getHelpBroker()));
        } else {
            parserOptionHelpButton.setEnabled(false);
        }

    }

    private class DAOOptionRule implements Rule {
        @Override
        public String getId() {
            return "DAO Options";
        }

        @Override
        public String getName() {
            return "Inconsistent DAO options rule";
        }

        @Override
        public String getDescription() {
            return "This rule checks for inconsistent use of DAO options between PRIDE Converter toolsuite tools";
        }

        @Override
        public Collection<String> getHowToFixTips() {
            return null;
        }
    }
}
