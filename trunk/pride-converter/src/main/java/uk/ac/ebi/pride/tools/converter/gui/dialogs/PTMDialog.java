/*
 * Created by JFormDesigner on Wed Nov 02 13:55:07 GMT 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.dialogs;

import uk.ac.ebi.pride.tools.converter.gui.NavigationPanel;
import uk.ac.ebi.pride.tools.converter.gui.component.table.BaseTable;
import uk.ac.ebi.pride.tools.converter.gui.ols.OLSDialog;
import uk.ac.ebi.pride.tools.converter.gui.ols.OLSInputable;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author User #3
 */
public class PTMDialog extends AbstractDialog implements OLSInputable {

    private static final double MASS_DELTA_CUTOFF_VALUE = 1;

    private PTM ptm;
    private Map<String, String> metadata = new HashMap<String, String>();

    //decoy values
    private double modDelta = 9999;
    private double ptmDelta = 9999;
    private double modMonoDelta = 9999;
    private double modAvgDelta = 9999;

    private boolean isMassValid = true;
    private boolean isOriginValid = true;
    private String errorMessage = null;
    private boolean olsSearchDone = false;


    public PTMDialog(Frame owner, BaseTable<PTM> paramTable) {
        super(owner);
        callback = paramTable;
        initComponents();
    }

    public PTMDialog(Dialog owner, BaseTable<PTM> paramTable) {
        super(owner);
        callback = paramTable;
        initComponents();
    }

    private void olsButtonActionPerformed() {
        //try and search for specific delta if given, otherwise default to water;
        double delta = 18.0;
        if (ptm != null) {
            //get info from PTM
            try {
                if (!ptm.getModAvgDelta().isEmpty()) {
                    delta = Double.valueOf(ptm.getModAvgDelta().get(0));
                }
            } catch (NumberFormatException e) {
                delta = 18.0;
            }
            try {
                if (!ptm.getModMonoDelta().isEmpty()) {
                    delta = Double.valueOf(ptm.getModMonoDelta().get(0));
                }
            } catch (NumberFormatException e) {
                delta = 18.0;
            }
        }

        new OLSDialog(this, this, true, "", "MOD", -1, nameField.getText(), delta, 0.5, OLSDialog.OLS_DIALOG_PSI_MOD_MASS_SEARCH);
    }

    private void modSlimButtonActionPerformed() {
        JOptionPane.showConfirmDialog(this, "Not implemented yet", "Under Development", JOptionPane.OK_OPTION);
    }

    @Override
    public void edit(ReportObject object) {

        //store PTM to edit
        ptm = (PTM) object;
        //set initial values
        Param p = ptm.getAdditional();
        CvParam obj = new CvParam();
        if (p != null && !p.getCvParam().isEmpty()) {
            obj = p.getCvParam().get(0);
        }

        accessionField.setText(obj.getAccession());
        nameField.setText(obj.getName());
        valueField.setText(obj.getValue());
        cvField.setText(obj.getCvLabel());

    }

    private void cancelButtonActionPerformed() {
        setVisible(false);
        dispose();
    }

    private void okButtonActionPerformed() {

        //perform PTM validation
        if (!isValidPTM() && olsSearchDone) {
            StringBuilder msg = new StringBuilder("The Cv Param you have selected cannot be used to annotate the PTM.\n");
            if (errorMessage != null) {
                msg.append("Reason: ").append(errorMessage).append("\n");
            }
            msg.append("Please select a different one.");
            JOptionPane.showMessageDialog(this, msg.toString(), "ERROR!", JOptionPane.ERROR_MESSAGE);
        } else {
            //everything is ok
            callback.update(updatePTM());
            setVisible(false);
            dispose();
        }

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        cvField = new JTextField();
        accessionField = new JTextField();
        nameField = new JTextField();
        valueField = new JTextField();
        label1 = new JLabel();
        label2 = new JLabel();
        label3 = new JLabel();
        label4 = new JLabel();
        olsButton = new JButton();
        modSlimButton = new JButton();
        cancelButton = new JButton();
        okButton = new JButton();
        label7 = new JLabel();
        label8 = new JLabel();
        label9 = new JLabel();

        //======== this ========
        setResizable(false);
        setTitle("PSI-MOD PTM");
        Container contentPane = getContentPane();

        //---- label1 ----
        label1.setText("CV");

        //---- label2 ----
        label2.setText("Accession");

        //---- label3 ----
        label3.setText("Name");

        //---- label4 ----
        label4.setText("Value");

        //---- olsButton ----
        olsButton.setText("Search MOD in OLS");
        olsButton.setActionCommand("Search OLS");
        olsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                olsButtonActionPerformed();
            }
        });

        //---- modSlimButton ----
        modSlimButton.setText("Search MOD slim");
        modSlimButton.setActionCommand("Search PSI-MOD slim");
        modSlimButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modSlimButtonActionPerformed();
            }
        });

        //---- cancelButton ----
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelButtonActionPerformed();
            }
        });

        //---- okButton ----
        okButton.setText("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okButtonActionPerformed();
            }
        });

        //---- label7 ----
        label7.setText("*");
        label7.setForeground(Color.red);

        //---- label8 ----
        label8.setText("*");
        label8.setForeground(Color.red);

        //---- label9 ----
        label9.setText("*");
        label9.setForeground(Color.red);

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(okButton, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cancelButton))
                                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                        .addGroup(GroupLayout.Alignment.LEADING, contentPaneLayout.createSequentialGroup()
                                                                .addGap(82, 82, 82)
                                                                .addComponent(olsButton)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(modSlimButton, GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE))
                                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                                        .addComponent(label3)
                                                                        .addComponent(label4)
                                                                        .addComponent(label2)
                                                                        .addComponent(label1))
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(contentPaneLayout.createParallelGroup()
                                                                        .addComponent(valueField, GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                                                                        .addComponent(nameField, GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                                                                        .addComponent(accessionField, GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                                                                        .addComponent(cvField, GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE))))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                        .addComponent(label7, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(label8, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(label9, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE))))
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(cvField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label7)
                                        .addComponent(label1))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(accessionField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label2)
                                        .addComponent(label8))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(nameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label3)
                                        .addComponent(label9))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(valueField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label4))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(olsButton)
                                        .addComponent(modSlimButton))
                                .addGap(18, 18, 18)
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(okButton)
                                        .addComponent(cancelButton))
                                .addGap(13, 13, 13))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JTextField cvField;
    private JTextField accessionField;
    private JTextField nameField;
    private JTextField valueField;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JButton olsButton;
    private JButton modSlimButton;
    private JButton cancelButton;
    private JButton okButton;
    private JLabel label7;
    private JLabel label8;
    private JLabel label9;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    private PTM updatePTM() {

        Param p = ptm.getAdditional();
        if (p == null) {
            ptm.setAdditional(new Param());
            p = ptm.getAdditional();
        }
        if (!p.getCvParam().isEmpty()) {
            p.getCvParam().clear();
        }

        CvParam cvParam = new CvParam();
        cvParam.setCvLabel(cvField.getText());
        cvParam.setAccession(accessionField.getText());
        cvParam.setName(nameField.getText());
        cvParam.setValue(valueField.getText());

        ptm.getAdditional().getCvParam().add(cvParam);
        //in some cases, the mod deltas in the PTM will not be set
        //make sure that we set them here then
        if (modMonoDelta != 9999) {
            if (ptm.getModMonoDelta().isEmpty()) {
                ptm.getModMonoDelta().add("" + modMonoDelta);
            } else {
                if ("".equals(ptm.getModMonoDelta().get(0).trim())) {
                    ptm.getModMonoDelta().set(0, "" + modMonoDelta);
                }
            }
        }
        if (modAvgDelta != 9999) {
            if (ptm.getModAvgDelta().isEmpty()) {
                ptm.getModAvgDelta().add("" + modAvgDelta);
            } else {
                if ("".equals(ptm.getModAvgDelta().get(0).trim())) {
                    ptm.getModAvgDelta().set(0, "" + modAvgDelta);
                }
            }
        }

        //this is where we add the Origin metadata
        if (ptm.getResidues() == null) {
            String modOrigin = metadata.get("Origin");
            if (modOrigin != null && !modOrigin.equals("none")) {
                ptm.setResidues(alphaSort(modOrigin));
            }
        }

        return ptm;

    }

    public boolean isValidPTM() {

        //get info from PTM
        if (!ptm.getModAvgDelta().isEmpty()) {
            String delta = ptm.getModAvgDelta().get(0);
            if (delta != null && !"".equals(delta.trim())) {
                ptmDelta = Double.valueOf(delta);
            }
        }
        if (!ptm.getModMonoDelta().isEmpty()) {
            String delta = ptm.getModMonoDelta().get(0);
            if (delta != null && !"".equals(delta.trim())) {
                ptmDelta = Double.valueOf(delta);
            }
        }

        //get into from metadata
        if (metadata.get("DiffAvg") != null && !"Null".equals(metadata.get("DiffAvg"))) {
            modAvgDelta = Double.valueOf(metadata.get("DiffAvg"));
            modDelta = Double.valueOf(metadata.get("DiffAvg"));
        }
        if (metadata.get("DiffMono") != null && !"Null".equals(metadata.get("DiffMono"))) {
            modMonoDelta = Double.valueOf(metadata.get("DiffMono"));
            modDelta = Double.valueOf(metadata.get("DiffMono"));
        }

        //if there is no delta reported for the PTM, assume that any
        //term selected by the OLS is valid
        if (ptmDelta != 9999) {

            //compare
            if (modDelta != 9999) {
                double diff = ptmDelta - modDelta;
                if (diff < 0) {
                    diff = diff * -1;
                }
                //check mass delta and also include check for PTM origin
                isMassValid = diff < MASS_DELTA_CUTOFF_VALUE;
                if (!isMassValid) {
                    errorMessage = "The mass delta of the selected PTM does not fit with the mass delta reported by the search engine.";
                }
                return isMassValid && isPTMValidOrigin();

            } else {
                isMassValid = false;
                return isMassValid;
            }

        } else {
            //only return true if there is no mass delta in the report file PTM and
            //there is a delta value in the OLS term

            //also include check for PTM origin
            isMassValid = modDelta != 9999;
            if (!isMassValid) {
                errorMessage = "The selected PTM does not report a mass delta value.";
            }
            return isMassValid && isPTMValidOrigin();

        }

    }

    public boolean isPTMValidOrigin() {

        String ptmOrigin = ptm.getResidues();
        String modOrigin = metadata.get("Origin");

        if (ptmOrigin == null) {
            isOriginValid = true;
            return isOriginValid;
        } else {
            isOriginValid = modOrigin == null || modOrigin.equals("X") || ptmOrigin.equals(alphaSort(modOrigin));
            if (!isOriginValid) {
                errorMessage = "The selected PTM cannot be assigned to the amino acids where it has been reported by the search engine";
            }
            return isOriginValid;
        }

    }

    private String alphaSort(String modOrigin) {

        /*
       xref: Origin: "H, H, H, H, H, H, H"
       xref: Origin: "H, R"
       xref: Origin: "M, W, Y"
        */
        if (modOrigin.indexOf(",") > 0) {
            String[] tokens = modOrigin.split(",");
            TreeSet<String> sortedTokens = new TreeSet<String>();
            StringBuilder sortedString = new StringBuilder();
            for (String token : tokens) {
                sortedString.append(token.trim());
            }
            return sortedString.toString();
        } else {
            return modOrigin;
        }

    }


    @Override
    public void insertOLSResult(String field, String selectedValue, String accession,
                                String ontologyShort, String ontologyLong, int modifiedRow, String mappedTerm, Map<String, String> metadata) {
        olsSearchDone = true;
        cvField.setText(ontologyShort);
        nameField.setText(selectedValue);
        accessionField.setText(accession);
        if (metadata != null) {
            this.metadata = metadata;
        }
    }

    @Override
    public Window getWindow() {
        return NavigationPanel.getInstance();
    }

}
