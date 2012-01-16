/*
 * Created by JFormDesigner on Fri Oct 21 14:59:42 BST 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.component.panels;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.dao.handler.QuantitationCvParams;
import uk.ac.ebi.pride.tools.converter.gui.NavigationPanel;
import uk.ac.ebi.pride.tools.converter.gui.component.AddTermButton;
import uk.ac.ebi.pride.tools.converter.gui.component.panels.model.SampleCvComboBoxModel;
import uk.ac.ebi.pride.tools.converter.gui.component.table.ParamTable;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.ComboValueCvParamDialog;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.CvParamDialog;
import uk.ac.ebi.pride.tools.converter.gui.interfaces.CvUpdatable;
import uk.ac.ebi.pride.tools.converter.gui.interfaces.ValidationListener;
import uk.ac.ebi.pride.tools.converter.gui.util.template.TemplateUtilities;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.Description;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.UserParam;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * @author User #3
 */
public class SamplePanel extends JPanel implements CvUpdatable<CvParam> {

    private static final Logger logger = Logger.getLogger(SamplePanel.class);

    private ValidationListener validationListerner;
    private Map<String, String> speciesCache;
    private Map<String, String> cellCache;
    private Map<String, String> tissueCache;
    private ResourceBundle config;
    private Map<String, String> subsamples = new HashMap<String, String>();

    private boolean isAllowMultipleValues = false;

    public SamplePanel() {
        initComponents();
        addTermButton1.setOwner(paramTable1);
        //update cv param list
        Set<String> suggestedCVs = getSuggestedOntologies("sample.suggested.cv");
        if (!suggestedCVs.isEmpty()) {
            addTermButton1.setSuggestedCVs(suggestedCVs);
        }
        //init caches
        initCaches();

    }

    private Set<String> getSuggestedOntologies(String resourceKey) {
        //update params for cv lookup
        if (config == null) {
            config = ResourceBundle.getBundle("gui-settings");
        }
        Set<String> suggestedCVs = new HashSet<String>();
        String cvList = config.getString(resourceKey);
        if (cvList != null) {
            String[] CVs = cvList.split(",");
            for (String cv : CVs) {
                suggestedCVs.add(cv.trim());
            }
        }
        return suggestedCVs;
    }

    private void initCaches() {
        speciesCache = TemplateUtilities.initMapCache("/templates/species.txt");
        speciesComboBox.setModel(new SampleCvComboBoxModel("NEWT", false, speciesCache.values().toArray()));
        cellCache = TemplateUtilities.initMapCache("/templates/cell.txt");
        cellComboBox.setModel(new SampleCvComboBoxModel("CL", true, cellCache.values().toArray()));
        tissueCache = TemplateUtilities.initMapCache("/templates/tissue.txt");
        tissueComboBox.setModel(new SampleCvComboBoxModel("BTO", true, tissueCache.values().toArray()));
    }

    public void addValidationListener(ValidationListener validationListerner) {
        this.validationListerner = validationListerner;
    }

    private boolean isNonNullTextField(String value) {
        return value != null && value.trim().length() > 0;
    }

    private void sampleNameFieldFocusLost() {
        validateSampleField(null);
        if (validationListerner != null) {
            validationListerner.fireValidationListener(isNonNullTextField(sampleNameField.getText()) && containsNEWTParam());
        }
    }

    private void sampleNameFieldKeyTyped(KeyEvent e) {
        validateSampleField(e);
        if (validationListerner != null) {
            validationListerner.fireValidationListener(isNonNullTextField(sampleNameField.getText() + e.getKeyChar()) && containsNEWTParam());
        }
    }

    private void validateSampleField(KeyEvent event) {
        //validate form and fire validationListener
        String toValidate = sampleNameField.getText();
        if (event != null) {
            toValidate += event.getKeyChar();
        }
        if (isNonNullTextField(toValidate)) {
            sampleNameField.setBackground(Color.white);
        } else {
            sampleNameField.setBackground(Color.pink);
        }
    }

    public void fireValidationListener() {
        if (validationListerner != null) {
            validationListerner.fireValidationListener(isNonNullTextField(sampleNameField.getText()) && containsNEWTParam());
        }
    }

    private void speciesComboBoxItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            otherSelectedComboBoxItemStateChanged(speciesComboBox, speciesCache, "sample.taxon.suggested.cv");
        }
    }

    private void tissueComboBoxItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            otherSelectedComboBoxItemStateChanged(tissueComboBox, tissueCache, "sample.tissue.suggested.cv");
        }
    }

    private void cellComboBoxItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            otherSelectedComboBoxItemStateChanged(cellComboBox, cellCache, "sample.cell.suggested.cv");
        }
    }

    private void otherSelectedComboBoxItemStateChanged(JComboBox comboBox, Map<String, String> cache, String resourceKey) {

        //update boolean flag for future use when adding param
        isAllowMultipleValues = ((SampleCvComboBoxModel) comboBox.getModel()).isAllowMultipleValues();

        if (comboBox.getSelectedItem() != null && TemplateUtilities.SELECT_OTHER.equals(comboBox.getSelectedItem().toString())) {

            Set<String> suggestedCVs = getSuggestedOntologies(resourceKey);
            if (!suggestedCVs.isEmpty()) {
                if (subsamples.isEmpty()) {
                    CvParamDialog cvParamDialog = new CvParamDialog(NavigationPanel.getInstance(), this, suggestedCVs);
                    cvParamDialog.setVisible(true);
                } else {
                    ComboValueCvParamDialog cvParamDialog = new ComboValueCvParamDialog(NavigationPanel.getInstance(), this, suggestedCVs, subsamples.keySet());
                    cvParamDialog.setVisible(true);
                }
            }

        } else if (comboBox.getSelectedItem() != null && !TemplateUtilities.PLEASE_SELECT.equals(comboBox.getSelectedItem().toString())) {

            //update table - create new cvparam
            CvParam cv = new CvParam();
            cv.setCvLabel(((SampleCvComboBoxModel) comboBox.getModel()).getCV());
            String accession = null;
            for (Map.Entry<String, String> entry : cache.entrySet()) {
                if (entry.getValue().equals(comboBox.getSelectedItem())) {
                    accession = entry.getKey();
                    break;
                }
            }
            if (accession != null) {
                cv.setAccession(accession);
            } else {
                throw new IllegalStateException("No accession found for value: " + comboBox.getSelectedItem());
            }
            cv.setName(comboBox.getSelectedItem().toString());

            //update the table, based on the isAllowMultipleValues of the model
            add(cv);

        }

        //reset combobox to "Please select"
        comboBox.setSelectedIndex(0);

        //update validation
        fireValidationListener();

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        label1 = new JLabel();
        sampleNameField = new JTextField();
        label2 = new JLabel();
        label5 = new JLabel();
        label6 = new JLabel();
        sampleCommentField = new JTextField();
        label3 = new JLabel();
        speciesComboBox = new JComboBox();
        label4 = new JLabel();
        tissueComboBox = new JComboBox();
        label7 = new JLabel();
        cellComboBox = new JComboBox();
        scrollPane1 = new JScrollPane();
        paramTable1 = new ParamTable();
        addTermButton1 = new AddTermButton();
        label8 = new JLabel();

        //======== this ========

        //---- label1 ----
        label1.setText("Sample Name");

        //---- sampleNameField ----
        sampleNameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                sampleNameFieldFocusLost();
            }
        });
        sampleNameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                sampleNameFieldKeyTyped(e);
            }
        });

        //---- label2 ----
        label2.setText("*");
        label2.setForeground(Color.red);

        //---- label5 ----
        label5.setText("Additional Information");

        //---- label6 ----
        label6.setText("Description");

        //---- label3 ----
        label3.setText("Species");

        //---- speciesComboBox ----
        speciesComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                speciesComboBoxItemStateChanged(e);
            }
        });

        //---- label4 ----
        label4.setText("Tissue");

        //---- tissueComboBox ----
        tissueComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                tissueComboBoxItemStateChanged(e);
            }
        });

        //---- label7 ----
        label7.setText("Cell Type");

        //---- cellComboBox ----
        cellComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cellComboBoxItemStateChanged(e);
            }
        });

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(paramTable1);
        }

        //---- addTermButton1 ----
        addTermButton1.setText("Add Param");

        //---- label8 ----
        label8.setText("*");
        label8.setForeground(Color.red);

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(scrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE)
                                        .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                        .addComponent(label3)
                                                        .addComponent(label1)
                                                        .addComponent(label6))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup()
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(sampleNameField, GroupLayout.PREFERRED_SIZE, 187, GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(label2, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(speciesComboBox, 0, 138, Short.MAX_VALUE)
                                                                .addGap(10, 10, 10)
                                                                .addComponent(label8, GroupLayout.PREFERRED_SIZE, 12, GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(label4)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(tissueComboBox, 0, 123, Short.MAX_VALUE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(label7)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(cellComboBox, 0, 122, Short.MAX_VALUE))
                                                        .addComponent(sampleCommentField, GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(label5)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 385, Short.MAX_VALUE)
                                                .addComponent(addTermButton1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(label1)
                                        .addComponent(sampleNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label2))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(label6)
                                        .addComponent(sampleCommentField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(cellComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label7)
                                        .addComponent(tissueComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label4)
                                        .addComponent(speciesComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label3)
                                        .addComponent(label8))
                                .addGap(14, 14, 14)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(addTermButton1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label5))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JTextField sampleNameField;
    private JLabel label2;
    private JLabel label5;
    private JLabel label6;
    private JTextField sampleCommentField;
    private JLabel label3;
    private JComboBox speciesComboBox;
    private JLabel label4;
    private JComboBox tissueComboBox;
    private JLabel label7;
    private JComboBox cellComboBox;
    private JScrollPane scrollPane1;
    private ParamTable paramTable1;
    private AddTermButton addTermButton1;
    private JLabel label8;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public String getSampleName() {
        return sampleNameField.getText();
    }

    public void setSampleName(String sampleName) {
        sampleNameField.setText(sampleName);
    }

    public void setSampleComment(String comment) {
        sampleCommentField.setText(comment);
    }

    public String getSampleComment() {
        return sampleCommentField.getText();
    }

    public void setSampleParams(Param param) {
        if (param != null) {

            subsamples.clear();
            for (CvParam cv : param.getCvParam()) {
                paramTable1.add(cv);
                if (QuantitationCvParams.isQuantificationReagent(cv.getAccession())) {
                    subsamples.put(cv.getName(), cv.getAccession());
                }
            }
            if (!subsamples.isEmpty()) {
                addTermButton1.setComboBoxValues(subsamples.keySet());
            }
            for (UserParam up : param.getUserParam()) {
                paramTable1.add(up);
            }

        }
    }

    public Param getSampleParams() {
        Param p = new Param();
        //get the rest of the table
        p.getCvParam().addAll(paramTable1.getCvParamList());
        p.getUserParam().addAll(paramTable1.getUserParamList());
        return p;

    }

    public void clear() {
        sampleNameField.setText(null);
        sampleCommentField.setText(null);
        paramTable1.removeAll();
        speciesComboBox.setSelectedItem(TemplateUtilities.PLEASE_SELECT);
        tissueComboBox.setSelectedItem(TemplateUtilities.PLEASE_SELECT);
        cellComboBox.setSelectedItem(TemplateUtilities.PLEASE_SELECT);
        if (validationListerner != null) {
            validationListerner.fireValidationListener(isNonNullTextField(sampleNameField.getText()));
        }
    }

    @Override
    public void add(CvParam objectToAdd) {

        //if can add multiple values, just add it
        if (isAllowMultipleValues) {
            paramTable1.add(objectToAdd);
        } else {
            //otherwise check to see if the value is already there
            java.util.List<CvParam> cvParamList = paramTable1.getCvParamList();
            java.util.List<UserParam> userParamList = paramTable1.getUserParamList();

            for (Iterator<CvParam> i = cvParamList.iterator(); i.hasNext(); ) {
                if (i.next().getCvLabel().equalsIgnoreCase(objectToAdd.getCvLabel())) {
                    i.remove();
                }
            }

            //then add all params back
            paramTable1.removeAll();
            for (CvParam p : cvParamList) {
                paramTable1.add(p);
            }
            //add new object
            paramTable1.add(objectToAdd);
            //add back all userparams, if any
            for (UserParam u : userParamList) {
                paramTable1.add(u);
            }
        }

    }

    @Override
    public void update(CvParam objectToUpdate) {
        //no op
    }

    public Description getSampleDescription() {
        Description sample = new Description();
        sample.setComment(getSampleComment());
        Param p = getSampleParams();
        sample.getCvParam().addAll(p.getCvParam());
        sample.getUserParam().addAll(p.getUserParam());
        return sample;
    }

    public boolean isSampleValid() {
        return isNonNullTextField(sampleNameField.getText()) && containsNEWTParam();
    }

    private boolean containsNEWTParam() {
        for (CvParam p : paramTable1.getCvParamList()) {
            if (p.getCvLabel().equalsIgnoreCase("NEWT")) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.add(new SamplePanel());
        f.pack();
        f.setVisible(true);
    }

}
