/*
 * Created by JFormDesigner on Mon Jan 09 15:45:12 GMT 2012
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import psidev.psi.tools.validator.Context;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.gui.component.table.ExperimentDetailMultiTable;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.ExperimentDetailMultiTableModel;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.gui.model.GUIException;
import uk.ac.ebi.pride.tools.converter.gui.model.ReportBean;
import uk.ac.ebi.pride.tools.converter.gui.validator.rules.DuplicateInfoRule;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author User #3
 */
public class ExperimentDetailMultipleDataForm extends AbstractForm implements TableModelListener {

    public ExperimentDetailMultipleDataForm() {
        initComponents();

    }

    private void autogenerateButtonActionPerformed() {

        StringBuilder msg = new StringBuilder()
                .append("You should only use the auto-generation function\n")
                .append("if you are going to merge the files afterwards.\n")
                .append("Otherwise, please provide meaningful annotations\n")
                .append("for these values. Continue with auto-generation?");
        int confirm = JOptionPane.showConfirmDialog(this, msg.toString(), "Warning!", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            //auto generate experiment short label and title
            String projectName = null;
            for (CvParam cv : ConverterData.getInstance().getMasterDAO().getExperimentParams().getCvParam()) {
                if (DAOCvParams.PRIDE_PROJECT.getAccession().equals(cv.getAccession())) {
                    if (cv.getValue() != null && !"".equals(cv.getValue())) {
                        projectName = cv.getValue();
                        break;
                    }
                }
            }
            //project name should never be null as it is required in the previous form
            for (int i = 0; i < experimentDataTable.getModel().getRowCount(); i++) {
                String expTitle = projectName + " - Experiment " + (i + 1);
                String shortLabel = "Exp " + (i + 1);
                experimentDataTable.setValueAt(expTitle, i, 1);
                experimentDataTable.setValueAt(shortLabel, i, 2);
            }
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        scrollPane1 = new JScrollPane();
        experimentDataTable = new ExperimentDetailMultiTable();
        scrollPane2 = new JScrollPane();
        textArea1 = new JTextArea();
        autogenerateButton = new JButton();

        //======== this ========

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(experimentDataTable);
        }

        //======== scrollPane2 ========
        {
            scrollPane2.setBorder(null);

            //---- textArea1 ----
            textArea1.setWrapStyleWord(true);
            textArea1.setLineWrap(true);
            textArea1.setText("Please enter an experiment title and short label for each source file. The combination of experiment title and short label must be unique, and neither value can be empty.");
            textArea1.setBackground(null);
            textArea1.setEditable(false);
            textArea1.setBorder(null);
            scrollPane2.setViewportView(textArea1);
        }

        //---- autogenerateButton ----
        autogenerateButton.setText("Auto-generate");
        autogenerateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autogenerateButtonActionPerformed();
            }
        });

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                                                .addComponent(autogenerateButton)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(autogenerateButton))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JScrollPane scrollPane1;
    private ExperimentDetailMultiTable experimentDataTable;
    private JScrollPane scrollPane2;
    private JTextArea textArea1;
    private JButton autogenerateButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    @Override
    public Collection<ValidatorMessage> validateForm() throws ValidatorException {

        List<ValidatorMessage> validatorMessages = new ArrayList<ValidatorMessage>();
        ExperimentDetailMultiTableModel model = (ExperimentDetailMultiTableModel) experimentDataTable.getModel();
        if (!model.isValid()) {
            //add validation error
            validatorMessages.add(new ValidatorMessage("Duplicate values for experiment title and short label", MessageLevel.ERROR, new Context("Experiment Additional Information"), new DuplicateInfoRule()));
        }
        experimentDataTable.getSelectionModel().clearSelection();
        experimentDataTable.repaint();

        return validatorMessages;
    }

    @Override
    public void clear() {
        isLoaded = false;
        ExperimentDetailMultiTableModel model = new ExperimentDetailMultiTableModel(new ArrayList<String>(ConverterData.getInstance().getInputFiles()), "", "");
        model.addTableModelListener(this);
        experimentDataTable.setModel(model);
        //inactivate next button
        validationListerner.fireValidationListener(false);
    }

    @Override
    public void save(ReportReaderDAO dao) {
        int rows = experimentDataTable.getModel().getRowCount();
        int i = 0;
        while (i < rows) {
            String file = (String) experimentDataTable.getValueAt(i, 0);
            ReportBean rb = new ReportBean();
            rb.setExperimentTitle((String) experimentDataTable.getValueAt(i, 1));
            rb.setShortLabel((String) experimentDataTable.getValueAt(i, 2));
            ConverterData.getInstance().setCustomeReportFields(file, rb);
            i++;
        }

        //also need to update the information in the master dao!
        ReportBean rb = ConverterData.getInstance().getCustomeReportFields().get(dao.getSourceFile().getPathToFile());
        if (rb != null) {
            dao.setExperimentTitle(rb.getExperimentTitle());
            dao.setExperimentShortLabel(rb.getShortLabel());
        }
    }

    @Override
    public void load(ReportReaderDAO dao) {
        if (!isLoaded) {
            ExperimentDetailMultiTableModel model = new ExperimentDetailMultiTableModel(new ArrayList<String>(ConverterData.getInstance().getInputFiles()), dao.getExperimentTitle(), dao.getExperimentShortLabel());
            model.addTableModelListener(this);
            experimentDataTable.setModel(model);
            //resize table
            int width = experimentDataTable.getWidth();
            experimentDataTable.getColumnModel().getColumn(0).setWidth((int) Math.floor(width * 0.33));
            experimentDataTable.getColumnModel().getColumn(1).setWidth((int) Math.floor(width * 0.66));
            experimentDataTable.revalidate();
            experimentDataTable.repaint();
            isLoaded = true;
        }
        //fire validation listener on load
        validationListerner.fireValidationListener(((ExperimentDetailMultiTableModel) experimentDataTable.getModel()).isValid());
    }

    @Override
    public String getFormName() {
        return "Experiment Details - Extra";
    }

    @Override
    public String getFormDescription() {
        return config.getString("experimentdetailsextra.form.description");
    }

    @Override
    public Icon getFormIcon() {
        return getFormIcon("experimentdetailsextra.form.icon");
    }

    @Override
    public String getHelpResource() {
        return "help.ui.experimentdetailsextra";
    }

    @Override
    public void start() {
        //validate form and fire validationListener - required for back & forth when no changes occur
        validationListerner.fireValidationListener(((ExperimentDetailMultiTableModel) experimentDataTable.getModel()).isValid());
    }

    @Override
    public void finish() throws GUIException {
        /* no op */
    }

    /**
     * This fine grain notification tells listeners the exact range
     * of cells, rows, or columns that changed.
     */
    @Override
    public void tableChanged(TableModelEvent e) {
        validationListerner.fireValidationListener(((ExperimentDetailMultiTableModel) experimentDataTable.getModel()).isValid());
        //calling isValid in the model will update information on which rows are erroneous
        //therefore we need to call repaint to make sure that the table is properly displayed
        experimentDataTable.repaint();
    }


}

