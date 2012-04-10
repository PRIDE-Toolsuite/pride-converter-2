/*
 * Created by JFormDesigner on Fri Mar 11 12:04:26 GMT 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.gui.NavigationPanel;
import uk.ac.ebi.pride.tools.converter.gui.component.AddTermButton;
import uk.ac.ebi.pride.tools.converter.gui.component.table.ParamTable;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.LoadTemplateDialog;
import uk.ac.ebi.pride.tools.converter.gui.util.template.TemplateType;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
import uk.ac.ebi.pride.tools.converter.report.model.InstrumentDescription;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
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
            analyzerScrollPane.setViewportView(analyzerTables.firstElement());
            addTermButton.setOwner(analyzerTables.firstElement());
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

        //======== this ========

        //---- label4 ----
        label4.setText(bundle.getString("InstrumentForm.label4.text"));

        //---- instrumentNameField ----
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
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadButtonActionPerformed();
            }
        });

        //---- saveButton ----
        saveButton.setText(bundle.getString("InstrumentForm.saveButton.text"));
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

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(sourceTable);
        }

        //======== scrollPane3 ========
        {

            //---- analyzerList ----
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
            analyzerScrollPane.setViewportView(paramTable1);
        }

        //---- label2 ----
        label2.setText(bundle.getString("InstrumentForm.label2.text"));

        //======== scrollPane2 ========
        {
            scrollPane2.setViewportView(detectorTable);
        }

        //---- addDetectorButton ----
        addDetectorButton.setText(bundle.getString("InstrumentForm.addDetectorButton.text"));

        //---- addSourceButton ----
        addSourceButton.setText(bundle.getString("InstrumentForm.addSourceButton.text"));

        //---- addTermButton ----
        addTermButton.setText(bundle.getString("InstrumentForm.addTermButton.text"));

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

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(scrollPane2, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 623, Short.MAX_VALUE)
                                        .addComponent(scrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 623, Short.MAX_VALUE)
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
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 411, Short.MAX_VALUE)
                                                .addComponent(addSourceButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(label2)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 91, Short.MAX_VALUE)
                                                .addComponent(addAnalyzerButton)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(removeAnalyzerButton)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(addTermButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addComponent(scrollPane3, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
                                                .addGap(4, 4, 4)
                                                .addComponent(analyzerScrollPane, GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(label3)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 385, Short.MAX_VALUE)
                                                .addComponent(addDetectorButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
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
                                        .addComponent(label1))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(addTermButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(removeAnalyzerButton)
                                                        .addComponent(addAnalyzerButton))
                                                .addGap(5, 5, 5))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(label2)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)))
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(scrollPane3, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                        .addComponent(analyzerScrollPane, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(addDetectorButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label3))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
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