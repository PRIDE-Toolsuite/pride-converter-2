/*
 * Created by JFormDesigner on Fri Mar 11 13:22:26 GMT 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.gui.component.table.BaseTable;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.PTMTableModel;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.AbstractDialog;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.gui.util.Colours;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;
import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;
import uk.ac.ebi.pride.tools.converter.report.validator.ReportObjectValidator;
import uk.ac.ebi.pride.tools.converter.utils.xml.validation.ValidatorFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 * @author Melih Birim
 * @author rcote
 */
public class PTMForm extends AbstractForm {

    private PTMTableModel ptmTableModel;

    public PTMForm() {

        initComponents();

        ptmTableModel = new PTMTableModel();
        ptmTable.setModel(ptmTableModel);
        ptmTable.setColumnModel(ptmTableModel.getTableColumnModel(ptmTable));
        //erroneous PTMs will be highlighted in table
        ptmTable.setEnableRowValidation(true);
        //disable alternate row colors - otherwise too colourful and messy
        ptmTable.setUseAlternateRowColor(false);

        ambiguousLegend.setBackground(Colours.ambiguousYellow);
        errorLegend.setBackground(Colours.errorRed);

    }

    private void editButtonActionPerformed() {
        if (ptmTable.getSelectedRowCount() > 0) {
            //convert table selected row to underlying model row
            int modelSelectedRow = ptmTable.convertRowIndexToModel(ptmTable.getSelectedRow());
            //get object
            ReportObject objToEdit = ((PTMTableModel) ptmTable.getModel()).get(modelSelectedRow);
            Class clazz = objToEdit.getClass();
            //show editing dialog for object
            AbstractDialog dialog = AbstractDialog.getInstance(ptmTable, clazz);
            dialog.edit(objToEdit, modelSelectedRow);
            dialog.setVisible(true);
        }

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("messages");
        label1 = new JLabel();
        scrollPane1 = new JScrollPane();
        ptmTable = new BaseTable<PTM>();
        editButton = new JButton();
        label2 = new JLabel();
        errorLegend = new JTextField();
        label3 = new JLabel();
        ambiguousLegend = new JTextField();
        label4 = new JLabel();

        //======== this ========

        //---- label1 ----
        label1.setText(bundle.getString("PTMDialog.label1.text"));

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(ptmTable);
        }

        //---- editButton ----
        editButton.setIcon(new ImageIcon(getClass().getResource("/images/edit.png")));
        editButton.setToolTipText(bundle.getString("PTMDialog.editButton.toolTipText"));
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editButtonActionPerformed();
            }
        });

        //---- label2 ----
        label2.setText(bundle.getString("PTMDialog.label2.text"));

        //---- errorLegend ----
        errorLegend.setEditable(false);

        //---- label3 ----
        label3.setText(bundle.getString("PTMDialog.label3.text"));

        //---- ambiguousLegend ----
        ambiguousLegend.setEditable(false);

        //---- label4 ----
        label4.setText(bundle.getString("PTMDialog.label4.text"));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(scrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 673, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(label1)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 586, Short.MAX_VALUE)
                                                .addComponent(editButton))
                                        .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addComponent(label2)
                                                .addGap(18, 18, 18)
                                                .addComponent(errorLegend, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(label3)
                                                .addGap(18, 18, 18)
                                                .addComponent(ambiguousLegend, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(label4)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(editButton)
                                        .addComponent(label1))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(label2)
                                        .addComponent(errorLegend, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label3)
                                        .addComponent(ambiguousLegend, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label4))
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JScrollPane scrollPane1;
    private BaseTable<PTM> ptmTable;
    private JButton editButton;
    private JLabel label2;
    private JTextField errorLegend;
    private JLabel label3;
    private JTextField ambiguousLegend;
    private JLabel label4;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    @Override
    public Collection<ValidatorMessage> validateForm() throws ValidatorException {
        ReportObjectValidator validator = ValidatorFactory.getInstance().getReportValidator();
        return validator.validate(ptmTable.getAll());
    }

    @Override
    public void clear() {
        //critical information returned by the DAO
        isLoaded = false;
    }

    @Override
    public void save(ReportReaderDAO dao) {
        dao.setPTMs(ptmTable.getAll());
    }

    @Override
    public void load(ReportReaderDAO dao) {
        if (!isLoaded) {
            //note that in this instance, the data is obtained from the ConverterData singleton
            //and not the dao
            ptmTable.addAll(ConverterData.getInstance().getPTMs());
            isLoaded = true;
        }
        //no need to fire validation listener
    }

    @Override
    public String getFormName() {
        return "PTMs";
    }

    @Override
    public String getFormDescription() {
        return config.getString("ptm.form.description");
    }

    @Override
    public Icon getFormIcon() {
        return getFormIcon("ptm.form.icon");
    }

    @Override
    public String getHelpResource() {
        return "help.ui.ptms";
    }

    @Override
    public void start() {
        //PTMs are optional
        validationListerner.fireValidationListener(true);
    }

    @Override
    public void finish() {
        /* no op */
    }
}
