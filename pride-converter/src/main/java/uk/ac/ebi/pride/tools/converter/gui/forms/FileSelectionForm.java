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
import uk.ac.ebi.pride.tools.converter.gui.NavigationPanel;
import uk.ac.ebi.pride.tools.converter.gui.component.filefilters.FastaFileFilter;
import uk.ac.ebi.pride.tools.converter.gui.component.filefilters.MzTabFileFilter;
import uk.ac.ebi.pride.tools.converter.gui.component.table.FileTable;
import uk.ac.ebi.pride.tools.converter.gui.component.table.ParserOptionTable;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.ParserOptionCellEditor;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.ParserOptionTableModel;
import uk.ac.ebi.pride.tools.converter.gui.model.*;
import uk.ac.ebi.pride.tools.converter.gui.util.IOUtilities;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;

import javax.help.CSH;
import javax.swing.*;
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
            //disable tabs for fasta & mztab
            fileTabbedPane.setEnabledAt(1, false);
            fileTabbedPane.setEnabledAt(2, false);
            forceRegenerationBox.setEnabled(false);
        }
    }

    private Collection<File> chooseFiles(boolean allowMultipleSelection, boolean allowDirectory, FileFilter filter) {

        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(filter);
        chooser.setMultiSelectionEnabled(allowMultipleSelection);
        String lastUsedDirectory = ConverterData.getInstance().getLastSelectedDirectory();
        if (lastUsedDirectory != null) {
            chooser.setCurrentDirectory(new File(lastUsedDirectory));
        }

        if (allowDirectory) {
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
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
        tableFiles.addAll(chooseFiles(true, ConverterData.getInstance().getType().isAllowDirectory(), ConverterData.getInstance().getType().getFilter()));
        dataFileTable.clearFiles();
        dataFileTable.addFiles(tableFiles);
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
        panel7 = new JPanel();
        tableScrollPane = new JScrollPane();
        parserOptionTable = new ParserOptionTable();
        parserOptionHelpButton = new JButton();

//======== this ========

//======== panel2 ========
        {
            panel2.setBorder(new TitledBorder(bundle.getString("SelecFilePanel.panel2.border")));
            panel2.setLayout(new FlowLayout());

            //---- forceRegenerationBox ----
            forceRegenerationBox.setText(bundle.getString("SelecFilePanel.forceRegenerationBox.text"));
            panel2.add(forceRegenerationBox);
        }

//======== fileTabbedPane ========
        {

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
                                    .addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
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
                                        .addComponent(panel7, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(fileTabbedPane, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE)
                                        .addComponent(panel2, GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(fileTabbedPane, GroupLayout.PREFERRED_SIZE, 238, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel7, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
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
                ValidatorMessage error = new ValidatorMessage("You must provide at least two files to merge", MessageLevel.ERROR, new Context("Pride Merger"), new Rule() {
                    @Override
                    public String getId() {
                        return "MERGER";
                    }

                    @Override
                    public String getName() {
                        return "Merger File Number";
                    }

                    @Override
                    public String getDescription() {
                        return "You must provide at least two files to merge";
                    }

                    @Override
                    public Collection<String> getHowToFixTips() {
                        return null;
                    }
                });
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

        DataType type = ConverterData.getInstance().getType();
        DAOFactory.DAO_FORMAT daoFormat = DAOFactory.DAO_FORMAT.getDAOForSearchengineOption(type.getEngineName().toLowerCase());

        if (daoFormat == null) {
            throw new ConverterException("Invalid DAO Format: " + type.getEngineName());
        }
        ConverterData.getInstance().setDaoFormat(daoFormat);

        fileTabbedPane.setTitleAt(0, daoFormat.getNiceName() + " files");

        updateOptionTable();

        //fire the table listener - this is required if users go back & forth without changing the
        //table content
        tableChanged(new TableModelEvent(dataFileTable.getModel()));

    }

    @Override
    public void finish() throws GUIException {

        //clear existing ConverterData info
        ConverterData.getInstance().clearPossibleStaleData();

        switch (format) {
            case MZTAB:
                IOUtilities.generateMzTabFiles(getOptions(), dataFileTable.getFiles());
                break;
            case PRIDE_XML:
                IOUtilities.generateReportFiles(getOptions(), dataFileTable.getFiles(), forceRegenerationBox.isSelected());
                break;
            case PRIDE_MERGED_XML:
                IOUtilities.mergePrideXMLFiles(getOptions(), dataFileTable.getFiles());
                break;
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
            gelIdentifier.setDescription("Sets the gel identifier to be used for identifications in the generated mzTab file. This option only takes effect when generating mzTab files.");
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
        if (DAOFactory.getInstance().getSupportedPorperties(ConverterData.getInstance().getDaoFormat()) != null) {
            props.addAll(DAOFactory.getInstance().getSupportedPorperties(ConverterData.getInstance().getDaoFormat()));
        }

        parserOptionTable = new ParserOptionTable(props);
        parserOptionTable.getColumn("Property Value").setCellEditor(new ParserOptionCellEditor());
        tableScrollPane.setViewportView(parserOptionTable);
        if (!props.isEmpty() && ConverterData.getInstance().getDaoFormat().getHelpResource() != null) {
            parserOptionHelpButton.setEnabled(true);
            CSH.setHelpIDString(parserOptionHelpButton, ConverterData.getInstance().getDaoFormat().getHelpResource());
            parserOptionHelpButton.addActionListener(new CSH.DisplayHelpFromSource(NavigationPanel.getInstance().getHelpBroker()));
        } else {
            parserOptionHelpButton.setEnabled(false);
        }

    }

}
