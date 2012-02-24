/*
 * Created by JFormDesigner on Fri Mar 11 13:22:26 GMT 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.gui.component.table.BaseTable;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.PTMTableModel;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;
import uk.ac.ebi.pride.tools.converter.report.validator.ReportObjectValidator;
import uk.ac.ebi.pride.tools.converter.utils.xml.validation.ValidatorFactory;

import javax.swing.*;
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

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("messages");
        label1 = new JLabel();
        scrollPane1 = new JScrollPane();
        ptmTable = new BaseTable<PTM>();

        //======== this ========

        //---- label1 ----
        label1.setText(bundle.getString("PTMDialog.label1.text"));

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(ptmTable);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                                        .addComponent(label1))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(label1)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JScrollPane scrollPane1;
    private BaseTable<PTM> ptmTable;
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
