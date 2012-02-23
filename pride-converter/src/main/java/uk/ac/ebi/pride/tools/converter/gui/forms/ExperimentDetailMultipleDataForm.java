/*
 * Created by JFormDesigner on Mon Jan 09 15:45:12 GMT 2012
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import psidev.psi.tools.validator.Context;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import psidev.psi.tools.validator.rules.Rule;
import uk.ac.ebi.pride.tools.converter.gui.component.table.ExperimentDetailMultiTable;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.ExperimentDetailMultiTableModel;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.gui.model.GUIException;
import uk.ac.ebi.pride.tools.converter.gui.model.ReportBean;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
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

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        scrollPane1 = new JScrollPane();
        experimentDataTable = new ExperimentDetailMultiTable();

        //======== this ========

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(experimentDataTable);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JScrollPane scrollPane1;
    private ExperimentDetailMultiTable experimentDataTable;
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

    private static class DuplicateInfoRule implements Rule {
        @Override
        public String getId() {
            return "Duplicate Experiment Information";
        }

        @Override
        public String getName() {
            return "Duplicate Experiment Information";
        }

        @Override
        public String getDescription() {
            return "Duplicate Experiment Information";
        }

        @Override
        public Collection<String> getHowToFixTips() {
            return null;
        }

        @Override
        public String toString() {
            return "Duplicate Experiment Information";
        }
    }

}

