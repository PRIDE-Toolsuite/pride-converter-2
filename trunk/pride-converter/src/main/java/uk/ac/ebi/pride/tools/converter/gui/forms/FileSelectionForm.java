/*
* Created by JFormDesigner on Wed May 25 11:03:01 BST 2011
*/

package uk.ac.ebi.pride.tools.converter.gui.forms;

import org.apache.log4j.Logger;
import psidev.psi.tools.validator.Context;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.dao.DAOFactory;
import uk.ac.ebi.pride.tools.converter.dao.DAOProperty;
import uk.ac.ebi.pride.tools.converter.dao.handler.HandlerFactory;
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
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;

/**
 * @author User #1
 * @author rcote
 */
public class FileSelectionForm extends AbstractForm implements TableModelListener {

    private static final Logger logger = Logger.getLogger(FileSelectionForm.class);

    private OutputFormat format;

    private HandlerFactory.FASTA_FORMAT fastaFormat;

    public FileSelectionForm(OutputFormat format) {
        initComponents();
        dataFileTable.getModel().addTableModelListener(this);
        mzTabFileTable.getModel().addTableModelListener(this);
        sequenceFileTable.getModel().addTableModelListener(this);
        this.format = format;
        if (format.equals(OutputFormat.MZTAB)
                || format.equals(OutputFormat.PRIDE_MERGED_XML)
                || format.equals(OutputFormat.PRIDE_FILTERED_XML)) {
            //disable tabs for fasta / mztab / spectra
            fileTabbedPane.setEnabledAt(1, false);
            fileTabbedPane.setEnabledAt(2, false);
            fileTabbedPane.setEnabledAt(3, false);
            forceRegenerationBox.setEnabled(false);
        }

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

    private void initComponents() {
//        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
// Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("messages");
        panel2 = new JPanel();
        panel8 = new JPanel();
        forceRegenerationBox = new JCheckBox();
        fileTabbedPane = new JTabbedPane();
        panel3 = new JPanel();
        scrollPane1 = new JScrollPane();
        dataFileTable = new FileTable();
        button1 = new JButton();
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
        panel1 = new JPanel();
        scrollPane4 = new JScrollPane();
        spectrumFileTable = new FileTable();
        button5 = new JButton();
        panel7 = new JPanel();
        tableScrollPane = new JScrollPane();
        parserOptionTable = new ParserOptionTable();
        parserOptionHelpButton = new JButton();

//======== this ========

//======== panel2 ========
        {
            panel2.setBorder(new TitledBorder(bundle.getString("SelecFilePanel.panel2.border")));
            panel2.setLayout(new GridLayout(2, 0));

            //======== panel8 ========
            {
                panel8.setLayout(new FlowLayout(FlowLayout.LEFT));

                //---- forceRegenerationBox ----
                forceRegenerationBox.setText(bundle.getString("SelecFilePanel.forceRegenerationBox.text"));
                panel8.add(forceRegenerationBox);
            }
            panel2.add(panel8);
        }

//======== fileTabbedPane ========
        {
            fileTabbedPane.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));

            //======== panel3 ========
            {

                //======== scrollPane1 ========
                {
                    scrollPane1.setViewportView(dataFileTable);
                }

                //---- button1 ----
                button1.setText(bundle.getString("SelecFilePanel.button1.text"));
                button1.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        loadDataFiles(e);
                    }
                });

                GroupLayout panel3Layout = new GroupLayout(panel3);
                panel3.setLayout(panel3Layout);
                panel3Layout.setHorizontalGroup(
                        panel3Layout.createParallelGroup()
                                .addGroup(panel3Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(panel3Layout.createParallelGroup()
                                                .addComponent(button1, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(scrollPane1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE))
                                        .addContainerGap())
                );
                panel3Layout.setVerticalGroup(
                        panel3Layout.createParallelGroup()
                                .addGroup(GroupLayout.Alignment.TRAILING, panel3Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button1, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap())
                );
            }
            fileTabbedPane.addTab(bundle.getString("SelecFilePanel.panel3.tab.title"), panel3);


            //======== panel4 ========
            {

                //======== scrollPane3 ========
                {
                    scrollPane3.setViewportView(sequenceFileTable);
                }

                //---- button3 ----
                button3.setText(bundle.getString("SelecFilePanel.button3.text"));
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
                                                .addComponent(scrollPane3, GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE)
                                                .addComponent(panel6, GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE))
                                        .addContainerGap())
                );
                panel4Layout.setVerticalGroup(
                        panel4Layout.createParallelGroup()
                                .addGroup(GroupLayout.Alignment.TRAILING, panel4Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(panel6, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(scrollPane3, GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button3, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap())
                );
            }
            fileTabbedPane.addTab(bundle.getString("SelecFilePanel.panel4.tab.title"), panel4);


            //======== panel5 ========
            {

                //======== scrollPane2 ========
                {
                    scrollPane2.setViewportView(mzTabFileTable);
                }

                //---- button2 ----
                button2.setText(bundle.getString("SelecFilePanel.button2.text"));
                button2.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        loadTabFiles(e);
                    }
                });

                GroupLayout panel5Layout = new GroupLayout(panel5);
                panel5.setLayout(panel5Layout);
                panel5Layout.setHorizontalGroup(
                        panel5Layout.createParallelGroup()
                                .addGroup(GroupLayout.Alignment.TRAILING, panel5Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(panel5Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                .addComponent(scrollPane2, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE)
                                                .addComponent(button2, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                                        .addContainerGap())
                );
                panel5Layout.setVerticalGroup(
                        panel5Layout.createParallelGroup()
                                .addGroup(GroupLayout.Alignment.TRAILING, panel5Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button2, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap())
                );
            }
            fileTabbedPane.addTab(bundle.getString("SelecFilePanel.panel5.tab.title"), panel5);


            //======== panel1 ========
            {

                //======== scrollPane4 ========
                {
                    scrollPane4.setViewportView(spectrumFileTable);
                }

                //---- button5 ----
                button5.setText(bundle.getString("SelecFilePanel.button5.text"));
                button5.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        loadSpectrumFiles(e);
                    }
                });

                GroupLayout panel1Layout = new GroupLayout(panel1);
                panel1.setLayout(panel1Layout);
                panel1Layout.setHorizontalGroup(
                        panel1Layout.createParallelGroup()
                                .addGroup(panel1Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(panel1Layout.createParallelGroup()
                                                .addComponent(scrollPane4, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE)
                                                .addComponent(button5, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                                        .addContainerGap())
                );
                panel1Layout.setVerticalGroup(
                        panel1Layout.createParallelGroup()
                                .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(scrollPane4, GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button5, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap())
                );
            }
            fileTabbedPane.addTab(bundle.getString("SelecFilePanel.panel1.tab.title"), panel1);

        }

//======== panel7 ========
        {
            panel7.setBorder(new TitledBorder(bundle.getString("SelecFilePanel.panel7.border")));

            //======== tableScrollPane ========
            {
                tableScrollPane.setViewportView(parserOptionTable);
            }

            //---- parserOptionHelpButton ----
            parserOptionHelpButton.setText(bundle.getString("SelecFilePanel.parserOptionHelpButton.text"));
            parserOptionHelpButton.setEnabled(false);

            GroupLayout panel7Layout = new GroupLayout(panel7);
            panel7.setLayout(panel7Layout);
            panel7Layout.setHorizontalGroup(
                    panel7Layout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, panel7Layout.createSequentialGroup()
                                    .addContainerGap(508, Short.MAX_VALUE)
                                    .addComponent(parserOptionHelpButton))
                            .addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE)
            );
            panel7Layout.setVerticalGroup(
                    panel7Layout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, panel7Layout.createSequentialGroup()
                                    .addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(parserOptionHelpButton))
            );
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(panel2, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE)
                                        .addComponent(panel7, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(fileTabbedPane, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(fileTabbedPane, GroupLayout.PREFERRED_SIZE, 238, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel7, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel2, GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                                .addContainerGap())
        );

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
    private JPanel panel2;
    private JPanel panel8;
    private JCheckBox forceRegenerationBox;
    private JTabbedPane fileTabbedPane;
    private JPanel panel3;
    private JScrollPane scrollPane1;
    private FileTable dataFileTable;
    private JButton button1;
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
    private JPanel panel1;
    private JScrollPane scrollPane4;
    private FileTable spectrumFileTable;
    private JButton button5;
    private JPanel panel7;
    private JScrollPane tableScrollPane;
    private ParserOptionTable parserOptionTable;
    private JButton parserOptionHelpButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    private Properties getOptions() {
        ParserOptionTableModel model = (ParserOptionTableModel) parserOptionTable.getModel();
        Properties props = model.getProperties();
        logger.debug("Parsed options from panel: " + props);
        return props;
    }

    @Override
    public Collection<ValidatorMessage> validateForm() {

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
    public String getHelpResource() {
        return "help.ui.file";
    }

    @Override
    public void start() {

        //update UI tabbed pane
        fileTabbedPane.setTitleAt(0, ConverterData.getInstance().getDaoFormat().getNiceName() + " files");

        //update option table
        updateOptionTable();

        //enable the spectrum file tab on request
        if (ConverterData.getInstance().getDaoFormat().isAllowExternalSpectra()) {
            fileTabbedPane.setEnabledAt(3, true);
        } else {
            fileTabbedPane.setEnabledAt(3, false);
        }


        //fire the table listener - this is required if users go back & forth without changing the
        //table content
        tableChanged(new TableModelEvent(dataFileTable.getModel()));

    }

    @Override
    public void finish() throws GUIException {

        //clear existing ConverterData info
        ConverterData.getInstance().clearPossibleStaleData();

        switch (format) {

            //generate mztab files
            case MZTAB:
                IOUtilities.generateMzTabFiles(getOptions(), dataFileTable.getFiles());
                break;

            //will be generating pride xml file in the end
            case PRIDE_XML:

                File sequenceFile = null;
                //we only have 1/0 sequence file
                if (!sequenceFileTable.getFiles().isEmpty()) {
                    //store path and fasta type
                    sequenceFile = sequenceFileTable.getFiles().iterator().next();
                    ConverterData.getInstance().setFastaFormat(fastaFormat);
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

                    //store options for next form
                    ConverterData.getInstance().setOptions(getOptions());

                    //store mztab files externally to fileBeans
                    for (File mzTabFile : mzTabFileTable.getFiles()) {
                        ConverterData.getInstance().getMztabFiles().add(mzTabFile.getAbsolutePath());
                    }

                    //register a new form to deal with the mapping
                    mzTabFileMappingForm = new MzTabFileMappingForm();
                    NavigationPanel.getInstance().registerFormAfter(mzTabFileMappingForm, this);

                } else {
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

                    //store options for next form
                    ConverterData.getInstance().setOptions(getOptions());

                    //register a new form to deal with the mapping
                    if (hasMultipleTabFiles) {
                        NavigationPanel.getInstance().registerFormAfter(new SpectrumFileMappingForm(), mzTabFileMappingForm);
                        mzTabFileMappingForm.setDeferReportFileGeneration(true);
                    } else {
                        NavigationPanel.getInstance().registerFormAfter(new SpectrumFileMappingForm(), this);
                    }

                } else {
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
                break;

            //merging pride xml files
            case PRIDE_MERGED_XML:
                IOUtilities.mergePrideXMLFiles(getOptions(), dataFileTable.getFiles());
                break;

            //filtering pride xml files
            case PRIDE_FILTERED_XML:
                //only store input files, the logic will be handled by other form
                for (File inputFile : dataFileTable.getFiles()) {
                    FileBean fileBean = new FileBean(inputFile.getAbsolutePath());
                    ConverterData.getInstance().getDataFiles().add(fileBean);
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
            gelIdentifier.setDescription("Sets the gel identifierz to be used for identifications in the generated mzTab file. This option only takes effect when generating mzTab files.");
            props.add(gelIdentifier);

            DAOProperty<String> spotIdentifier = new DAOProperty<String>(IOUtilities.SPOT_IDENTIFIER, null);
            gelIdentifier.setDescription("Sets the gel spot identifier to be used for identifications in the generated mzTab file. This option only takes effect when generating mzTab files. This option is ignore if gel_spot_regex is set.");
            props.add(spotIdentifier);

            DAOProperty<String> spotRegex = new DAOProperty<String>(IOUtilities.SPOT_REGULAR_EXPRESSION, null);
            gelIdentifier.setDescription("Used to extract the gel spot identifier based on the sourcefile's name. The first matching group in the pattern is used as a spot identifier.");
            props.add(spotRegex);
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

        parserOptionTable = new ParserOptionTable(props);
        parserOptionTable.getColumn("Property Value").setCellEditor(new ParserOptionCellEditor());
        tableScrollPane.setViewportView(parserOptionTable);
        if (ConverterData.getInstance().getDaoFormat().getHelpResource() != null) {
            parserOptionHelpButton.setEnabled(true);
            CSH.setHelpIDString(parserOptionHelpButton, ConverterData.getInstance().getDaoFormat().getHelpResource());
            parserOptionHelpButton.addActionListener(new CSH.DisplayHelpFromSource(NavigationPanel.getInstance().getHelpBroker()));
        } else {
            parserOptionHelpButton.setEnabled(false);
        }

    }

}
