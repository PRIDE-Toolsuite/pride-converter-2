/*
 * Created by JFormDesigner on Fri Mar 11 12:04:26 GMT 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.gui.NavigationPanel;
import uk.ac.ebi.pride.tools.converter.gui.component.AddTermButton;
import uk.ac.ebi.pride.tools.converter.gui.component.table.ParamTable;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.ParamTableModel;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.AbstractDialog;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.LoadTemplateDialog;
import uk.ac.ebi.pride.tools.converter.gui.util.template.TemplateType;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
import uk.ac.ebi.pride.tools.converter.report.model.InstrumentDescription;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;
import uk.ac.ebi.pride.tools.converter.report.validator.ReportObjectValidator;
import uk.ac.ebi.pride.tools.converter.utils.xml.validation.ValidatorFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * @author Melih Birim
 * @author rcote
 */
public class InstrumentForm extends AbstractForm {

    private Vector<String> analyzers = new Vector<String>();
    private Vector<ParamTable> analyzerTables = new Vector<ParamTable>();

    private static final String ANALYZER_PREFIX = "";

    public InstrumentForm() {
        initComponents();
        addSourceButton.setOwner(sourceTable);
        updateTermButtonCvList(addSourceButton, "instrument.suggested.cv");
        addDetectorButton.setOwner(detectorTable);
        updateTermButtonCvList(addDetectorButton, "instrument.suggested.cv");
        //disable buttons until there is list content
        addTermButton.setEnabled(false);
        updateTermButtonCvList(addTermButton, "instrument.suggested.cv");
        removeAnalyzerButton.setEnabled(false);
        saveButton.setEnabled(false);

    }

    private void addAnalyzerButtonActionPerformed(ActionEvent e) {
        analyzers.add(ANALYZER_PREFIX);
        analyzerTables.add(new ParamTable());
        analyzerList.setListData(paginateAnalyzers(analyzers));
        analyzerList.setSelectedIndex(analyzers.size() - 1);
        analyzerScrollPane.setViewportView(analyzerTables.lastElement());
        addTermButton.setOwner(analyzerTables.lastElement());
        revalidate();
        repaint();
    }

    private void analyzerListValueChanged() {
        //only activate buttons if there is content in the list
        addTermButton.setEnabled(analyzerList.getModel().getSize() > 0);
        removeAnalyzerButton.setEnabled(analyzerList.getModel().getSize() > 0);

        if (analyzerList.getSelectedIndex() > -1) {
            analyzerScrollPane.setViewportView(analyzerTables.get(analyzerList.getSelectedIndex()));
            addTermButton.setOwner(analyzerTables.get(analyzerList.getSelectedIndex()));
            revalidate();
            repaint();
        }
    }

    private void removeAnalyzerButtonActionPerformed() {
        int ndx = analyzerList.getSelectedIndex();
        if (ndx > -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove this analyzer?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                analyzers.remove(ndx);
                analyzerTables.remove(ndx);
                analyzerList.setListData(paginateAnalyzers(analyzers));
            }
            analyzerList.setSelectedIndex(0);
            if (!analyzerTables.isEmpty()) {
                analyzerScrollPane.setViewportView(analyzerTables.firstElement());
                addTermButton.setOwner(analyzerTables.firstElement());
            } else {
                analyzerScrollPane.setViewportView(null);
            }
            revalidate();
            repaint();

        } else {
            JOptionPane.showMessageDialog(this, "Please select the analyzer to remove", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Vector<String> paginateAnalyzers(Vector<String> vector) {
        Vector<String> retval = new Vector<String>();
        int ndx = 1;
        for (String s : vector) {
            retval.add(s + ndx++);
        }
        return retval;
    }

    private void instrumentNameFieldKeyTyped(KeyEvent e) {
        validateRequiredField(instrumentNameField, e);
        validationListerner.fireValidationListener(isNonNullTextField(instrumentNameField.getText() + e.getKeyChar()));
        saveButton.setEnabled(isNonNullTextField(instrumentNameField.getText() + e.getKeyChar()));
    }

    private void instrumentNameFieldFocusLost(FocusEvent e) {
        validateRequiredField(instrumentNameField, null);
        validationListerner.fireValidationListener(isNonNullTextField(instrumentNameField.getText()));
        saveButton.setEnabled(isNonNullTextField(instrumentNameField.getText()));
    }

    private void loadButtonActionPerformed() {
        String[] templates = getTemplateNames(TemplateType.INSTRUMENT);
        LoadTemplateDialog dialog = new LoadTemplateDialog(this, NavigationPanel.getInstance(), templates);
        dialog.setVisible(true);
    }

    private void saveButtonActionPerformed() {
        saveTemplate(instrumentNameField.getText(), TemplateType.INSTRUMENT, makeInstrument());
    }

    private void editParam(ActionEvent e) {
        ParamTable table = null;
        if (e.getSource().equals(sourceEditButton)) {
            table = sourceTable;
        } else if (e.getSource().equals(analyzerEditButton)) {
            table = analyzerTables.get(analyzerList.getSelectedIndex());
        } else if (e.getSource().equals(detectorEditButton)) {
            table = detectorTable;
        }
        if (table != null && table.getSelectedRowCount() > 0) {
            //convert table selected row to underlying model row
            int modelSelectedRow = table.convertRowIndexToModel(table.getSelectedRow());
            //get object
            ReportObject objToEdit = ((ParamTableModel) table.getModel()).get(modelSelectedRow);
            Class clazz = objToEdit.getClass();
            //show editing dialog for object
            AbstractDialog dialog = AbstractDialog.getInstance(table, clazz);
            dialog.edit(objToEdit);
            dialog.setVisible(true);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("messages");
        label4 = new JLabel();
        instrumentNameField = new JTextField();
        loadButton = new JButton();
        saveButton = new JButton();
        label5 = new JLabel();
        label1 = new JLabel();
        scrollPane1 = new JScrollPane();
        sourceTable = new ParamTable();
        scrollPane3 = new JScrollPane();
        analyzerList = new JList();
        analyzerScrollPane = new JScrollPane();
        paramTable1 = new ParamTable();
        label2 = new JLabel();
        scrollPane2 = new JScrollPane();
        detectorTable = new ParamTable();
        addDetectorButton = new AddTermButton();
        addSourceButton = new AddTermButton();
        addTermButton = new AddTermButton();
        removeAnalyzerButton = new JButton();
        addAnalyzerButton = new JButton();
        label3 = new JLabel();
        sourceEditButton = new JButton();
        analyzerEditButton = new JButton();
        detectorEditButton = new JButton();

        //======== this ========

        //---- label4 ----
        label4.setText(bundle.getString("InstrumentForm.label4.text"));
        label4.setToolTipText(bundle.getString("InstrumentForm.label4.toolTipText"));

        //---- instrumentNameField ----
        instrumentNameField.setToolTipText(bundle.getString("InstrumentForm.instrumentNameField.toolTipText"));
        instrumentNameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                instrumentNameFieldKeyTyped(e);
            }
        });
        instrumentNameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                instrumentNameFieldFocusLost(e);
            }
        });

        //---- loadButton ----
        loadButton.setText(bundle.getString("InstrumentForm.loadButton.text"));
        loadButton.setToolTipText(bundle.getString("InstrumentForm.loadButton.toolTipText"));
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadButtonActionPerformed();
            }
        });

        //---- saveButton ----
        saveButton.setText(bundle.getString("InstrumentForm.saveButton.text"));
        saveButton.setToolTipText(bundle.getString("InstrumentForm.saveButton.toolTipText"));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveButtonActionPerformed();
            }
        });

        //---- label5 ----
        label5.setText(bundle.getString("InstrumentForm.label5.text_2"));
        label5.setForeground(Color.red);

        //---- label1 ----
        label1.setText(bundle.getString("InstrumentForm.label1.text"));
        label1.setToolTipText(bundle.getString("InstrumentForm.label1.toolTipText"));

        //======== scrollPane1 ========
        {

            //---- sourceTable ----
            sourceTable.setToolTipText(bundle.getString("InstrumentForm.sourceTable.toolTipText"));
            scrollPane1.setViewportView(sourceTable);
        }

        //======== scrollPane3 ========
        {

            //---- analyzerList ----
            analyzerList.setToolTipText(bundle.getString("InstrumentForm.analyzerList.toolTipText"));
            analyzerList.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    analyzerListValueChanged();
                }
            });
            scrollPane3.setViewportView(analyzerList);
        }

        //======== analyzerScrollPane ========
        {

            //---- paramTable1 ----
            paramTable1.setToolTipText(bundle.getString("InstrumentForm.paramTable1.toolTipText"));
            analyzerScrollPane.setViewportView(paramTable1);
        }

        //---- label2 ----
        label2.setText(bundle.getString("InstrumentForm.label2.text"));
        label2.setToolTipText(bundle.getString("InstrumentForm.label2.toolTipText"));

        //======== scrollPane2 ========
        {

            //---- detectorTable ----
            detectorTable.setToolTipText(bundle.getString("InstrumentForm.detectorTable.toolTipText"));
            scrollPane2.setViewportView(detectorTable);
        }

        //---- addDetectorButton ----
        addDetectorButton.setMargin(new Insets(1, 14, 2, 14));
        addDetectorButton.setToolTipText(bundle.getString("InstrumentForm.addDetectorButton.toolTipText"));

        //---- addSourceButton ----
        addSourceButton.setMargin(new Insets(1, 14, 2, 14));
        addSourceButton.setToolTipText(bundle.getString("InstrumentForm.addSourceButton.toolTipText"));

        //---- addTermButton ----
        addTermButton.setMargin(new Insets(1, 14, 2, 14));
        addTermButton.setToolTipText(bundle.getString("InstrumentForm.addTermButton.toolTipText"));

        //---- removeAnalyzerButton ----
        removeAnalyzerButton.setText(bundle.getString("InstrumentForm.removeAnalyzerButton.text"));
        removeAnalyzerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAnalyzerButtonActionPerformed();
            }
        });

        //---- addAnalyzerButton ----
        addAnalyzerButton.setText(bundle.getString("InstrumentForm.addAnalyzerButton.text"));
        addAnalyzerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAnalyzerButtonActionPerformed(e);
            }
        });

        //---- label3 ----
        label3.setText(bundle.getString("InstrumentForm.label3.text"));
        label3.setToolTipText(bundle.getString("InstrumentForm.label3.toolTipText"));

        //---- sourceEditButton ----
        sourceEditButton.setIcon(new ImageIcon(getClass().getResource("/images/edit.png")));
        sourceEditButton.setMargin(new Insets(1, 14, 2, 14));
        sourceEditButton.setToolTipText(bundle.getString("InstrumentForm.sourceEditButton.toolTipText"));
        sourceEditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editParam(e);
            }
        });

        //---- analyzerEditButton ----
        analyzerEditButton.setIcon(new ImageIcon(getClass().getResource("/images/edit.png")));
        analyzerEditButton.setMargin(new Insets(1, 14, 2, 14));
        analyzerEditButton.setToolTipText(bundle.getString("InstrumentForm.analyzerEditButton.toolTipText"));
        analyzerEditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editParam(e);
            }
        });

        //---- detectorEditButton ----
        detectorEditButton.setIcon(new ImageIcon(getClass().getResource("/images/edit.png")));
        detectorEditButton.setMargin(new Insets(1, 14, 2, 14));
        detectorEditButton.setToolTipText(bundle.getString("InstrumentForm.detectorEditButton.toolTipText"));
        detectorEditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editParam(e);
            }
        });

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 623, Short.MAX_VALUE)
                                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 623, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                        .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                                .addComponent(label4)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(instrumentNameField, GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(label5)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(loadButton)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(saveButton))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(label1)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 468, Short.MAX_VALUE)
                                                                .addComponent(addSourceButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(label2)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 161, Short.MAX_VALUE)
                                                                .addComponent(addAnalyzerButton)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(removeAnalyzerButton)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(addTermButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(label3)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 455, Short.MAX_VALUE)
                                                                .addComponent(addDetectorButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                        .addGroup(layout.createParallelGroup()
                                                                .addComponent(sourceEditButton)
                                                                .addComponent(analyzerEditButton))
                                                        .addComponent(detectorEditButton)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(scrollPane3, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
                                                .addGap(4, 4, 4)
                                                .addComponent(analyzerScrollPane, GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(sourceEditButton)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                                .addComponent(loadButton)
                                                                .addComponent(saveButton))
                                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                                .addComponent(instrumentNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(label5)
                                                                .addComponent(label4)))
                                                .addGap(8, 8, 8)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                        .addComponent(addSourceButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(label1))))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(addAnalyzerButton)
                                                .addComponent(removeAnalyzerButton))
                                        .addComponent(addTermButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(analyzerEditButton)
                                        .addComponent(label2))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(analyzerScrollPane, GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                                        .addComponent(scrollPane3, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(addDetectorButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label3)
                                        .addComponent(detectorEditButton))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel label4;
    private JTextField instrumentNameField;
    private JButton loadButton;
    private JButton saveButton;
    private JLabel label5;
    private JLabel label1;
    private JScrollPane scrollPane1;
    private ParamTable sourceTable;
    private JScrollPane scrollPane3;
    private JList analyzerList;
    private JScrollPane analyzerScrollPane;
    private ParamTable paramTable1;
    private JLabel label2;
    private JScrollPane scrollPane2;
    private ParamTable detectorTable;
    private AddTermButton addDetectorButton;
    private AddTermButton addSourceButton;
    private AddTermButton addTermButton;
    private JButton removeAnalyzerButton;
    private JButton addAnalyzerButton;
    private JLabel label3;
    private JButton sourceEditButton;
    private JButton analyzerEditButton;
    private JButton detectorEditButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    @Override
    public Collection<ValidatorMessage> validateForm() throws ValidatorException {
        ReportObjectValidator validator = ValidatorFactory.getInstance().getReportValidator();
        return validator.validate(makeInstrument());
    }

    @Override
    public void clear() {
        isLoaded = false;
        instrumentNameField.setText(null);
        sourceTable.removeAll();
        detectorTable.removeAll();
        analyzers.removeAllElements();
        analyzerTables.removeAllElements();
        analyzerList.setListData(analyzers);
        //set default view
        analyzerScrollPane.setViewportView(paramTable1);
        revalidate();
        repaint();
        //inactivate next button
        validationListerner.fireValidationListener(false);
        //inactivate save button
        saveButton.setEnabled(false);

    }

    @Override
    public void save(ReportReaderDAO dao) {
        dao.setInstrument(makeInstrument());
    }

    private InstrumentDescription makeInstrument() {
        InstrumentDescription instrument = new InstrumentDescription();
        instrument.setInstrumentName(instrumentNameField.getText());
        for (ParamTable pt : analyzerTables) {
            Param p = new Param();
            p.getCvParam().addAll(pt.getCvParamList());
            instrument.getAnalyzerList().getAnalyzer().add(p);
        }
        instrument.getAnalyzerList().setCount(analyzerTables.size());
        Param sourceParam = new Param();
        sourceParam.getCvParam().addAll(sourceTable.getCvParamList());
        instrument.setSource(sourceParam);
        Param detectorParam = new Param();
        detectorParam.getCvParam().addAll(detectorTable.getCvParamList());
        instrument.setDetector(detectorParam);
        return instrument;
    }

    @Override
    public void load(ReportReaderDAO dao) {
        if (!isLoaded) {
            loadInstrument(dao.getInstrument());
        }
        //fire validation listener on load
        validationListerner.fireValidationListener(isNonNullTextField(instrumentNameField.getText()));
        //update save button on load
        saveButton.setEnabled(isNonNullTextField(instrumentNameField.getText()));

    }

    private void loadInstrument(InstrumentDescription instrument) {
        instrumentNameField.setText(instrument.getInstrumentName());
        for (Param param : instrument.getAnalyzerList().getAnalyzer()) {
            analyzers.add(ANALYZER_PREFIX);
            ParamTable paramTable = new ParamTable();
            paramTable.add(param);
            analyzerTables.add(paramTable);
        }
        if (!analyzers.isEmpty()) {
            analyzerList.setListData(paginateAnalyzers(analyzers));
            analyzerList.setSelectedIndex(0);
            analyzerScrollPane.setViewportView(analyzerTables.firstElement());
            addTermButton.setOwner(analyzerTables.firstElement());
            revalidate();
            repaint();
        }
        sourceTable.add(instrument.getSource());
        detectorTable.add(instrument.getDetector());
        //to prevent multiple loads on navigation
        isLoaded = true;
    }

    @Override
    public String getFormName() {
        return "Instrument Description";
    }

    @Override
    public String getFormDescription() {
        return config.getString("instrument.form.description");
    }

    @Override
    public Icon getFormIcon() {
        return getFormIcon("instrument.form.icon");
    }

    @Override
    public String getHelpResource() {
        return "help.ui.instrument";
    }

    @Override
    public void start() {
        //for back&forth navigation
        validationListerner.fireValidationListener(isNonNullTextField(instrumentNameField.getText()));
    }

    @Override
    public void finish() {
        /* no op */
    }

    @Override
    public void loadTemplate(String templateName) {

        InstrumentDescription instrument = (InstrumentDescription) loadTemplate(templateName, TemplateType.INSTRUMENT);
        clear();
        loadInstrument(instrument);
        //update save button on load
        saveButton.setEnabled(isNonNullTextField(instrumentNameField.getText()));
        //update field if required
        validateRequiredField(instrumentNameField, null);
        //fire validation listener on load
        validationListerner.fireValidationListener(isNonNullTextField(instrumentNameField.getText()));
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.add(new InstrumentForm());
        f.pack();
        f.setVisible(true);
    }
}
