package uk.ac.ebi.pride.tools.converter.gui.forms;

import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.gui.NavigationPanel;
import uk.ac.ebi.pride.tools.converter.gui.interfaces.ConverterForm;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.gui.model.FileBean;
import uk.ac.ebi.pride.tools.converter.gui.model.GUIException;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 08/11/11
 * Time: 17:43
 * To change this template use File | Settings | File Templates.
 */
public class MzTabReportForm extends AbstractForm implements ConverterForm {

    public MzTabReportForm() {
        initComponents();
    }

    @Override
    public Collection<ValidatorMessage> validateForm() throws ValidatorException {
        return Collections.emptyList();
    }

    @Override
    public void clear() {
        //no op
    }

    @Override
    public void save(ReportReaderDAO dao) {
        //no op
    }

    @Override
    public void load(ReportReaderDAO dao) {
        //no op
    }

    @Override
    public String getFormName() {
        return "Generation Report";
    }

    @Override
    public String getFormDescription() {
        return config.getString("mztabreport.form.description");
    }

    @Override
    public String getHelpResource() {
        return "help.ui.mztab.report";
    }

    @Override
    public void start() {

        //update table model data
        Set<FileBean> files = ConverterData.getInstance().getDataFiles();
        List<String> inputFiles = new ArrayList<String>();
        List<String> mzTabFiles = new ArrayList<String>();
        for (FileBean fileBean : files) {
            inputFiles.add(fileBean.getInputFile());
            if (fileBean.getMzTabFile() != null) {
                mzTabFiles.add(fileBean.getMzTabFile());
            }
        }

        Collections.sort(inputFiles);
        Collections.sort(mzTabFiles);

        if (inputFiles.size() != mzTabFiles.size()) {
            throw new IllegalStateException("File number mismatch: number of input files does not equal number of mztab files");
        }

        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        for (int i = 0; i < inputFiles.size(); i++) {
            Vector<Object> row = new Vector<Object>();
            row.add(inputFiles.get(i));
            row.add(mzTabFiles.get(i));
            data.add(row);
        }
        Vector<Object> headers = new Vector<Object>();
        headers.add("Input File");
        headers.add("MzTab File");

        fileTable.setModel(new DefaultTableModel(data, headers) {
            boolean[] columnEditable = new boolean[]{
                    false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnEditable[columnIndex];
            }
        });
        {
            TableColumnModel cm = fileTable.getColumnModel();
            cm.getColumn(0).setResizable(false);
            cm.getColumn(1).setResizable(false);
        }

        NavigationPanel.getInstance().hideValidatorMessages();
    }

    @Override
    public void finish() throws GUIException {
        //no op
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        scrollPane1 = new JScrollPane();
        fileTable = new JTable();
        fileGeneratedLabel = new JLabel();

        //======== this ========

        //======== scrollPane1 ========
        {

            //---- fileTable ----
            fileTable.setModel(new DefaultTableModel(
                    new Object[][]{
                            {null, null},
                            {null, null},
                    },
                    new String[]{
                            "Input File", "MzTab File"
                    }
            ) {
                boolean[] columnEditable = new boolean[]{
                        false, false
                };

                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return columnEditable[columnIndex];
                }
            });
            {
                TableColumnModel cm = fileTable.getColumnModel();
                cm.getColumn(0).setResizable(false);
                cm.getColumn(1).setResizable(false);
            }
            scrollPane1.setViewportView(fileTable);
        }

        //---- fileGeneratedLabel ----
        fileGeneratedLabel.setText("Files Generated: ");

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                                        .addComponent(fileGeneratedLabel))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(fileGeneratedLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JScrollPane scrollPane1;
    private JTable fileTable;
    private JLabel fileGeneratedLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
