package uk.ac.ebi.pride.tools.converter.gui.component.table.model;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.gui.component.table.RowNumberRenderer;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;

import javax.swing.*;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author melih
 *         Date: 12/04/2011
 *         Time: 15:53
 */
public class PTMTableModel extends BaseTableModel<PTM> {

    private static final Logger logger = Logger.getLogger(PTMTableModel.class);

    public PTMTableModel() {

        String searchEngineLabel = resourceBundle.getString("PTM.searchengine.name.text");
        String accession = resourceBundle.getString("PTM.accession.text");
        String database = resourceBundle.getString("PTM.database.text");
        String databaseVersion = resourceBundle.getString("PTM.database.version.text");
        String residues = resourceBundle.getString("PTM.residues.text");
        String monoDelta = resourceBundle.getString("PTM.mono.delta.text");
        String avgDelta = resourceBundle.getString("PTM.avg.delta.text");
        String MODAccession = resourceBundle.getString("PTM.MOD.accession.text");

        /**
         * column name, editable
         * 0 [ row number, false ]
         * 1 [ searchEngineLabel, false ]
         * 2 [ accession, false ]
         * 3 [ database, false ]
         * 4 [ databaseVersion, false ]
         * 5 [ residues, false ]
         * 6 [ monoDelta, false ]
         * 7 [ avgDelta, false ]
         * 8 [ MODAccession, false ]
         * 9 [ data, false]
         */

        columnNames = new String[]{"", searchEngineLabel, accession, database, databaseVersion, residues, monoDelta, avgDelta, MODAccession, ""};
        columnEditable = new boolean[]{false, false, false, false, false, false, false, false, false, false};
        columnTypes = new Class<?>[]{
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                PTM.class
        };
        dataColumnIndex = 9;
    }

    @Override
    protected void constructTableColumnModel(JTable table) {
        tableColumnModel.getColumn(0).setMaxWidth(WIDTH);
        tableColumnModel.getColumn(0).setMinWidth(WIDTH);
        tableColumnModel.getColumn(0).setPreferredWidth(WIDTH);
        tableColumnModel.getColumn(0).setCellRenderer(new RowNumberRenderer());

        //last column will contain the object itself
        tableColumnModel.getColumn(getColumnCount() - 1).setMaxWidth(0);
        tableColumnModel.getColumn(getColumnCount() - 1).setMinWidth(0);
        tableColumnModel.getColumn(getColumnCount() - 1).setPreferredWidth(0);
    }

    @Override
    protected Object[] getRowObjectArray(PTM ptm) {

        CvParam param = new CvParam();
        if (ptm.getAdditional() != null) {
            if (!ptm.getAdditional().getCvParam().isEmpty()) {
                param = ptm.getAdditional().getCvParam().get(0);
            }
        }
        //if accession not returned by DAO, use that of the param (or null if param is empty)
        String modAccession = ptm.getModAccession();
        if (modAccession == null || "".equals(modAccession)) {
            modAccession = param.getAccession();
            ptm.setModAccession(modAccession);
        }

        //if database not returned by DAO, use that of the param (or null if param is empty)
        String modDatabase = ptm.getModDatabase();
        if (modDatabase == null || "".equals(modDatabase)) {
            modDatabase = param.getCvLabel();
            ptm.setModDatabase(modDatabase);
        }

        return new Object[]{"",
                ptm.getSearchEnginePTMLabel(),
                modAccession,
                modDatabase,
                ptm.getModDatabaseVersion(),
                updateResidues(ptm.getResidues()),
                makeString(ptm.getModMonoDelta()),
                makeString(ptm.getModAvgDelta()),
                param.getAccession(),
                ptm
        };
    }

    private String updateResidues(String residues) {
        if (residues != null) {
            return residues.replace("0", "N-term").replace("1", "C-term");
        } else {
            return "";
        }
    }

    private String makeString(List<String> deltas) {
        StringBuilder sb = new StringBuilder();
        for (Iterator<String> it = deltas.iterator(); it.hasNext(); ) {
            String s = it.next();
            sb.append(s);
            if (it.hasNext()) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

}
