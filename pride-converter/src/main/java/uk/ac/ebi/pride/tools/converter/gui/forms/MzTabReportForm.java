package uk.ac.ebi.pride.tools.converter.gui.forms;

import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.gui.component.table.ShortFilePathStringRenderer;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.gui.model.FileBean;
import uk.ac.ebi.pride.tools.converter.gui.model.GUIException;
import uk.ac.ebi.pride.tools.converter.gui.util.IOUtilities;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 08/11/11
 * Time: 17:43
 */
public class MzTabReportForm extends AbstractForm {

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
    public Icon getFormIcon() {
        return getFormIcon("mztabreport.form.icon");
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

        String gelId = null;
        String spotId = null;
        Pattern spotPattern = null;
        Properties options = ConverterData.getInstance().getOptions();
        if (options.getProperty(IOUtilities.GEL_IDENTIFIER) != null && !"".equals(options.getProperty(IOUtilities.GEL_IDENTIFIER))) {
            gelId = options.getProperty(IOUtilities.GEL_IDENTIFIER);
        }
        if (options.getProperty(IOUtilities.SPOT_IDENTIFIER) != null && !"".equals(options.getProperty(IOUtilities.SPOT_IDENTIFIER))) {
            spotId = options.getProperty(IOUtilities.SPOT_IDENTIFIER);
        }
        if (options.getProperty(IOUtilities.SPOT_REGULAR_EXPRESSION) != null && !"".equals(options.getProperty(IOUtilities.SPOT_REGULAR_EXPRESSION))) {
            String regex = options.getProperty(IOUtilities.SPOT_REGULAR_EXPRESSION);
            spotPattern = Pattern.compile(regex);
        }

        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        for (int i = 0; i < inputFiles.size(); i++) {
            Vector<Object> row = new Vector<Object>();
            row.add(inputFiles.get(i));
            row.add(mzTabFiles.get(i));
            data.add(row);
            if (gelId != null) {
                row.add(gelId);
            }
            if (spotId != null || spotPattern != null) {
                if (spotPattern != null) {
                    Matcher matcher = spotPattern.matcher(new File(inputFiles.get(i)).getName());
                    if (matcher.matches()) {
                        row.add(matcher.group(1));
                    } else {
                        row.add("");
                    }
                } else {
                    row.add(spotId);
                }
            }
        }

        Vector<Object> headers = new Vector<Object>();
        headers.add("Input File");
        headers.add("MzTab File");
        if (gelId != null) {
            headers.add("Gel ID");
        }
        if (spotId != null || spotPattern != null) {
            headers.add("SPOT ID");
        }

        fileTable.setModel(new DefaultTableModel(data, headers) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });
        TableColumnModel cm = fileTable.getColumnModel();
        cm.getColumn(0).setCellRenderer(new ShortFilePathStringRenderer());
        cm.getColumn(1).setCellRenderer(new ShortFilePathStringRenderer());

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
        label1 = new JLabel();

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

        //---- label1 ----
        label1.setText("MzTab Generation Complete!");
        label1.setHorizontalAlignment(SwingConstants.CENTER);
        label1.setFont(label1.getFont().deriveFont(label1.getFont().getSize() + 2f));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(scrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                                        .addComponent(label1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                                        .addComponent(fileGeneratedLabel, GroupLayout.Alignment.LEADING))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(label1)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fileGeneratedLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JScrollPane scrollPane1;
    private JTable fileTable;
    private JLabel fileGeneratedLabel;
    private JLabel label1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
