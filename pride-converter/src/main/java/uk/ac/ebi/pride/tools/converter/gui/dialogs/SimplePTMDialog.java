/*
 * Created by JFormDesigner on Tue Feb 07 13:53:17 GMT 2012
 */

package uk.ac.ebi.pride.tools.converter.gui.dialogs;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import uk.ac.ebi.pride.toolsuite.ols.dialog.OLSDialog;;
import uk.ac.ebi.pride.toolsuite.ols.dialog.OLSInputable;
import uk.ac.ebi.pride.tools.converter.gui.NavigationPanel;
import uk.ac.ebi.pride.tools.converter.gui.component.table.BaseTable;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;
import uk.ac.ebi.pride.tools.converter.utils.ModUtils;
import uk.ac.ebi.pride.utilities.ols.web.service.model.Term;
import uk.ac.ebi.pridemod.slimmod.model.SlimModCollection;
import uk.ac.ebi.pridemod.slimmod.model.SlimModification;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author User #3
 */
public class SimplePTMDialog extends AbstractDialog implements OLSInputable {

    private static final double MASS_DELTA_CUTOFF_VALUE = 0.5;

    private PTM ptm;

    private Map<String, String> metadata = new HashMap<String, String>();
    private Map<String, SlimModification> slimModificationMap = new HashMap<String, SlimModification>();

    //decoy values
    private double modDelta = 9999;
    private double ptmDelta = 9999;
    private double modMonoDelta = 9999;
    private double modAvgDelta = 9999;

    private String errorMessage = null;
    private boolean olsSearchDone = false;

    private String accession, name;

    public SimplePTMDialog(Frame owner, BaseTable<PTM> paramTable) {
        super(owner);
        callback = paramTable;
        initComponents();
    }

    public SimplePTMDialog(Dialog owner, BaseTable<PTM> paramTable) {
        super(owner);
        callback = paramTable;
        initComponents();
    }

    private void ptmListMouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            if (ptmList.getSelectedValue() != null) {
                validatePTM((String) ptmList.getSelectedValue());
            }
        }
    }

    private void validatePTM(String ptmListSelectedValue) {

        if (olsSearchDone) {
            //check metadata
            if (!isValidPTM()) {
                StringBuilder msg = new StringBuilder("The Cv Param you have selected cannot be used to annotate the PTM.\n");
                if (errorMessage != null) {
                    msg.append("Reason: ").append(errorMessage).append("\n");
                }
                msg.append("Please select a different one.");
                JOptionPane.showMessageDialog(this, msg.toString(), "ERROR!", JOptionPane.ERROR_MESSAGE);
            } else {
                //everything is ok
                callback.update(updatePTM(), modelRowIndex);
                setVisible(false);
                dispose();
            }
        } else {

            //set values from simple mod
            SlimModification mod = slimModificationMap.get(ptmListSelectedValue);
            name = mod.getPsiModDesc();
            accession = mod.getIdPsiMod();
            modMonoDelta = mod.getDeltaMass();

            //todo - do we need to check origin at this point?
            callback.update(updatePTM(), modelRowIndex);
            setVisible(false);
            dispose();
        }

    }

    private void okButtonActionPerformed() {
        if (ptmList.getSelectedValue() != null) {
            validatePTM((String) ptmList.getSelectedValue());
        }
    }

    private void cancelButtonActionPerformed() {
        setVisible(false);
        dispose();
    }

    private void olsButtonActionPerformed() {
        showOLSDialog();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        cancelButton = new JButton();
        okButton = new JButton();
        scrollPane1 = new JScrollPane();
        ptmList = new JList();
        label1 = new JLabel();
        label2 = new JLabel();
        massDeltaTextField = new JTextField();
        ptmLabelTextField = new JTextField();
        label3 = new JLabel();
        olsButton = new JButton();

        //======== this ========
        setTitle("PTM Assignment");
        Container contentPane = getContentPane();

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

        //======== scrollPane1 ========
        {

            //---- ptmList ----
            ptmList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            ptmList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ptmListMouseClicked(e);
                }
            });
            scrollPane1.setViewportView(ptmList);
        }

        //---- label1 ----
        label1.setText("Label");

        //---- label2 ----
        label2.setText("Mass Delta");

        //---- massDeltaTextField ----
        massDeltaTextField.setEditable(false);

        //---- ptmLabelTextField ----
        ptmLabelTextField.setEditable(false);

        //---- label3 ----
        label3.setText("Suggested PTMs:");

        //---- olsButton ----
        olsButton.setText("Search OLS");
        olsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                olsButtonActionPerformed();
            }
        });

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(scrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addComponent(olsButton)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 94, Short.MAX_VALUE)
                                                .addComponent(okButton, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cancelButton))
                                        .addGroup(GroupLayout.Alignment.LEADING, contentPaneLayout.createSequentialGroup()
                                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                        .addComponent(label1)
                                                        .addComponent(label2))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(contentPaneLayout.createParallelGroup()
                                                        .addComponent(ptmLabelTextField, GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                                                        .addComponent(massDeltaTextField, GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)))
                                        .addComponent(label3, GroupLayout.Alignment.LEADING))
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(ptmLabelTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label1))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(label2)
                                        .addComponent(massDeltaTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(label3)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(cancelButton)
                                        .addComponent(okButton)
                                        .addComponent(olsButton))
                                .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JButton cancelButton;
    private JButton okButton;
    private JScrollPane scrollPane1;
    private JList ptmList;
    private JLabel label1;
    private JLabel label2;
    private JTextField massDeltaTextField;
    private JTextField ptmLabelTextField;
    private JLabel label3;
    private JButton olsButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    @Override
    public void edit(ReportObject object, int modelRowIndex) {

        //store index
        this.modelRowIndex = modelRowIndex;

        //store PTM to edit
        ptm = (PTM) object;

        SlimModCollection preferredMods = ModUtils.getPreferredModifications();

        double delta = Double.NaN;

        if (ptm.getModMonoDelta() != null && !ptm.getModMonoDelta().isEmpty()) {
            delta = Double.valueOf(ptm.getModMonoDelta().get(0));
        } else if (ptm.getModAvgDelta() != null && !ptm.getModAvgDelta().isEmpty()) {
            delta = Double.valueOf(ptm.getModAvgDelta().get(0));
        }

        ptmLabelTextField.setText(ptm.getSearchEnginePTMLabel());

        //if no delta annotated in ptm
        if (delta == Double.NaN) {
            massDeltaTextField.setText("N/A");
            //show OLS Dialog
            showOLSDialog();

        } else {

            massDeltaTextField.setText("" + delta);

            //map by delta
            SlimModCollection filteredMods = preferredMods.getbyDelta(delta, ModUtils.LOW_PRECISION);
            if (filteredMods.size() > 0) {
                //update list data
                java.util.List<String> ptmLabels = new ArrayList<String>();
                for (SlimModification mod : filteredMods) {
                    String label = mod.getIdPsiMod() + " [" + mod.getShortNamePsiMod() + "]";
                    ptmLabels.add(label);
                    slimModificationMap.put(label, mod);
                }
                ptmList.setListData(ptmLabels.toArray());
            } else {
                //show OLS Dialog
                showOLSDialog();
            }

        }

    }

    private void showOLSDialog() {
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

        new OLSDialog(this, this, true, "", "MOD", -1, null, delta, MASS_DELTA_CUTOFF_VALUE, OLSDialog.OLS_DIALOG_PSI_MOD_MASS_SEARCH);
    }

    private PTM updatePTM() {

        ptm.setModDatabase(ModUtils.MOD_DATABASE);
        ptm.setModDatabaseVersion(ModUtils.MOD_VERSION);

        Param p = ptm.getAdditional();
        if (p == null) {
            ptm.setAdditional(new Param());
            p = ptm.getAdditional();
        }
        if (!p.getCvParam().isEmpty()) {
            p.getCvParam().clear();
        }

        //update PTM information
        ptm.setModName(name);
        ptm.setModAccession(accession);

        //store additional param
        CvParam cvParam = new CvParam();
        cvParam.setCvLabel("MOD");
        cvParam.setAccession(accession);
        cvParam.setName(name);
        cvParam.setValue(null);

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
        if (ptm.getResidues() == null && metadata != null) {
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
        boolean massValid = true;
        if (ptmDelta != 9999) {

            //compare
            if (modDelta != 9999) {
                double diff = ptmDelta - modDelta;
                if (diff < 0) {
                    diff = diff * -1;
                }
                //check mass delta and also include check for PTM origin
                massValid = diff < MASS_DELTA_CUTOFF_VALUE;
                if (!massValid) {
                    errorMessage = "The mass delta of the selected PTM does not fit with the mass delta reported by the search engine.";
                }
                return massValid && isPTMValidOrigin();

            } else {
                massValid = false;
                return massValid;
            }

        } else {
            //only return true if there is no mass delta in the report file PTM and
            //there is a delta value in the OLS term

            //also include check for PTM origin
            massValid = modDelta != 9999;
            if (!massValid) {
                errorMessage = "The selected PTM does not report a mass delta value.";
            }
            return massValid && isPTMValidOrigin();

        }

    }

    public boolean isPTMValidOrigin() {

        String ptmOrigin = ptm.getResidues();
        String modOrigin = metadata.get("Origin");

        //todo - sepaarte origin with termspec as currently reported by the DAOs
        boolean originValid = true;
//        if (ptmOrigin == null) {
//            originValid = true;
//        } else {
//            originValid = modOrigin == null || modOrigin.equals("X") || ptmOrigin.equals(alphaSort(modOrigin));
//            if (!originValid) {
//                errorMessage = "The selected PTM cannot be assigned to the amino acids where it has been reported by the search engine";
//            }
//        }
        return originValid;

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

/*    @Override
    public void insertOLSResult(String field, String selectedValue, String accession,
                                String ontologyShort, String ontologyLong, int modifiedRow, String mappedTerm, Map<String, String> metadata) {
        this.olsSearchDone = true;
        this.name = selectedValue;
        this.accession = accession;
        if (metadata != null) {
            this.metadata = metadata;
        }

        //if using OLS, override existing list data
        ptmList.setListData(new String[]{accession + " [" + name + "]"});
        ptmList.setSelectedIndex(0);
    }*/


    /**
     * Inserts the selected cv term into the parent frame or dialog. If the
     * frame (or dialog) contains more than one OLS term, the field label can be
     * used to separate between the two. Modified row is used if the cv terms
     * are in a table and one of them are altered.
     *
     * @param field the name of the field where the CV term will be inserted
     * @param selectedValue the value to search for
     * @param accession the accession number to search for
     * @param ontologyShort short name of the ontology to search in, e.g., GO or
     * MOD
     * @param ontologyLong long ontology name, e.g., Gene Ontology [GO]
     * @param modifiedRow if the CV terms is going to be inserted into a table,
     * the row number can be provided here, use -1 if inserting a new row
     * @param mappedTerm the name of the previously mapped term, can be null
     * @param metadata the metadata associated with the current term (can be
     * null or empty)
     */
    @Override
    public void insertOLSResult(String field, Term selectedValue, Term accession,
                                String ontologyShort, String ontologyLong, int modifiedRow, String mappedTerm, java.util.List<String> metadata) {
        this.olsSearchDone = true;
        this.name = accession.getLabel();
        this.accession = accession.getOntologyName().equalsIgnoreCase("ncbitaxon")
                ? accession.getTermOBOId().getIdentifier().substring(accession.getTermOBOId().getIdentifier().indexOf(':')+1)
                : accession.getTermOBOId().getIdentifier();
        if (accession.getOboXRefs() != null) {
            this.metadata = new HashMap<>();
            Arrays.stream(accession.getOboXRefs()).parallel().forEach(oboxRef -> this.metadata.put(oboxRef.getId().substring(0, oboxRef.getId().indexOf(':')), oboxRef.getDatabase()));
        }
        ptmList.setListData(new String[]{this.accession + " [" + name + "]"});
        ptmList.setSelectedIndex(0);
    }

    @Override
    public Window getWindow() {
        return NavigationPanel.getInstance();
    }

}
