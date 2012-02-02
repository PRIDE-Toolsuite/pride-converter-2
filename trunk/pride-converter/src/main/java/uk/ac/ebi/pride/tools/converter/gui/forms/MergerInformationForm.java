/*
 * Created by JFormDesigner on Wed Feb 01 14:53:07 GMT 2012
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.dao.DAOFactory;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.gui.model.DataType;
import uk.ac.ebi.pride.tools.converter.gui.model.GUIException;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;

/**
 * @author User #3
 */
public class MergerInformationForm extends AbstractForm {
    public MergerInformationForm() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        label1 = new JLabel();
        scrollPane1 = new JScrollPane();
        textArea1 = new JTextArea();

        //======== this ========

        //---- label1 ----
        label1.setText("PRIDE XML Merger");

        //======== scrollPane1 ========
        {

            //---- textArea1 ----
            textArea1.setEditable(false);
            textArea1.setLineWrap(true);
            textArea1.setWrapStyleWord(true);
            textArea1.setText("This application will allow you to select multiple PRIDE XML files and merge them into a single PRIDE XML file. \n\nIt is assumed that each individual file is valid. Files will be ordered alphabetically and the metadata from the first file will be used to generate the final, merged file. The identifications and spectra from the other files will be merged into the final output file. \n\nPlease note that the spectrum IDs from the source files will not necessarily be kept, but the internal references between peptide-spectrum_refs will be maintained.  ");
            scrollPane1.setViewportView(textArea1);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                                        .addComponent(label1))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(label1)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JScrollPane scrollPane1;
    private JTextArea textArea1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    @Override
    public Collection<ValidatorMessage> validateForm() throws ValidatorException {
        return Collections.emptyList();
    }

    @Override
    public void clear() {
        /* no op */
    }

    @Override
    public void save(ReportReaderDAO dao) {
        /* no op */
    }

    @Override
    public void load(ReportReaderDAO dao) {
        /* no op */
        validationListerner.fireValidationListener(true);
    }

    @Override
    public String getFormName() {
        return "Merger Information";
    }

    @Override
    public String getFormDescription() {
        return config.getString("mergerinformation.form.description");
    }

    @Override
    public String getHelpResource() {
        return "help.ui.merger.information";
    }

    @Override
    public void start() {
        validationListerner.fireValidationListener(true);
    }

    @Override
    public void finish() throws GUIException {
        ConverterData.getInstance().setType(DataType.PRIDE_XML);
        DAOFactory.DAO_FORMAT daoFormat = DAOFactory.DAO_FORMAT.getDAOForSearchengineOption(DataType.PRIDE_XML.getEngineName().toLowerCase());
        if (daoFormat == null) {
            throw new ConverterException("Invalid DAO Format: " + DataType.PRIDE_XML.getEngineName());
        }
        ConverterData.getInstance().setDaoFormat(daoFormat);
    }
}
