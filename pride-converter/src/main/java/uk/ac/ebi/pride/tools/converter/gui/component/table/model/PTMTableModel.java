package uk.ac.ebi.pride.tools.converter.gui.component.table.model;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.gui.component.table.RowNumberRenderer;
import uk.ac.ebi.pride.tools.converter.gui.model.DecoratedReportObject;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.utils.ModUtils;

import javax.swing.*;
import java.awt.*;
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
        String name = resourceBundle.getString("PTM.name.text");
        String database = resourceBundle.getString("PTM.database.text");
        String residues = resourceBundle.getString("PTM.residues.text");
        String monoDelta = resourceBundle.getString("PTM.mono.delta.text");

        /**
         * column name, editable
         * 0 [ row number, false ]
         * 1 [ searchEngineLabel, false ]
         * 2 [ accession, false ]
         * 3 [ name, false ]
         * 4 [ database, false ]
         * 5 [ residues, false ]
         * 6 [ monoDelta, false ]
         * 7 [ data, false]
         */

        columnNames = new String[]{"", searchEngineLabel, accession, name, database, residues, monoDelta, ""};
        columnEditable = new boolean[]{false, false, false, false, false, false, false, false,};
        columnTypes = new Class<?>[]{
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                PTM.class
        };
        dataColumnIndex = 7;
    }

    @Override
    protected void constructTableColumnModel(JTable table) {
        tableColumnModel.getColumn(0).setMaxWidth(SMALL_WIDTH);
        tableColumnModel.getColumn(0).setMinWidth(SMALL_WIDTH);
        tableColumnModel.getColumn(0).setPreferredWidth(SMALL_WIDTH);
        tableColumnModel.getColumn(0).setCellRenderer(new RowNumberRenderer());

        //last column will contain the object itself
        tableColumnModel.getColumn(getColumnCount() - 1).setMaxWidth(0);
        tableColumnModel.getColumn(getColumnCount() - 1).setMinWidth(0);
        tableColumnModel.getColumn(getColumnCount() - 1).setPreferredWidth(0);
    }

    @Override
    protected Object[] getRowObjectArray(PTM ptm) {

        String modAccession = ptm.getModAccession();
        String modDatabase = ptm.getModDatabase();
        String modName = ptm.getModName();
        //check to see if we need to highlight the PTM if it has multiple possible PTMs
        DecoratedPTM decoratedPTM = new DecoratedPTM(ptm);
        if (ModUtils.canMapToMultiplePreferredMods(decoratedPTM)) {
            decoratedPTM.setBackground(new Color(255, 255, 153));
        }

        return new Object[]{"",
                ptm.getSearchEnginePTMLabel(),
                modAccession,
                modName,
                modDatabase,
                updateResidues(ptm.getResidues()),
                makeString(ptm.getModMonoDelta()),
                decoratedPTM
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

    public class DecoratedPTM extends PTM implements DecoratedReportObject<PTM> {

        private PTM ptm;
        private Color background;

        public DecoratedPTM(PTM ptm) {
            //need to avoid multiple levels of decoration
            if (ptm instanceof DecoratedPTM) {
                this.ptm = ((DecoratedPTM) ptm).getInner();
            } else {
                this.ptm = ptm;
            }
        }

        public PTM getInner() {
            return ptm;
        }

        public Color getBackground() {
            return background;
        }

        public void setBackground(Color background) {
            this.background = background;
        }

        @Override
        public String getSearchEnginePTMLabel() {
            return ptm.getSearchEnginePTMLabel();
        }

        @Override
        public void setSearchEnginePTMLabel(String value) {
            ptm.setSearchEnginePTMLabel(value);
        }

        @Override
        public String getModAccession() {
            return ptm.getModAccession();
        }

        @Override
        public void setModAccession(String value) {
            ptm.setModAccession(value);
        }

        @Override
        public String getModDatabase() {
            return ptm.getModDatabase();
        }

        @Override
        public void setModDatabase(String value) {
            ptm.setModDatabase(value);
        }

        @Override
        public String getModDatabaseVersion() {
            return ptm.getModDatabaseVersion();
        }

        @Override
        public void setModDatabaseVersion(String value) {
            ptm.setModDatabaseVersion(value);
        }

        @Override
        public Boolean isFixedModification() {
            return ptm.isFixedModification();
        }

        @Override
        public void setFixedModification(Boolean value) {
            ptm.setFixedModification(value);
        }

        @Override
        public List<String> getModMonoDelta() {
            return ptm.getModMonoDelta();
        }

        @Override
        public List<String> getModAvgDelta() {
            return ptm.getModAvgDelta();
        }

        @Override
        public String getResidues() {
            return ptm.getResidues();
        }

        @Override
        public void setResidues(String value) {
            ptm.setResidues(value);
        }

        @Override
        public Param getAdditional() {
            return ptm.getAdditional();
        }

        @Override
        public void setAdditional(Param value) {
            ptm.setAdditional(value);
        }

        @Override
        public String getModName() {
            return ptm.getModName();
        }

        @Override
        public void setModName(String modName) {
            ptm.setModName(modName);
        }

        @Override
        public boolean equals(Object o) {
            return ptm.equals(o);
        }

        @Override
        public int hashCode() {
            return ptm.hashCode();
        }

        @Override
        public String toString() {
            return ptm.toString();
        }
    }

}
