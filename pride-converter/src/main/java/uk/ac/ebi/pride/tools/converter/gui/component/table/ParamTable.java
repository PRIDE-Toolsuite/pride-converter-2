package uk.ac.ebi.pride.tools.converter.gui.component.table;

import uk.ac.ebi.pride.tools.converter.gui.component.table.model.BaseTableModel;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.ParamTableModel;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;
import uk.ac.ebi.pride.tools.converter.report.model.UserParam;

import javax.swing.table.TableColumnModel;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author melih
 *         Date: 06/04/2011
 *         Time: 15:51
 */
public class ParamTable extends BaseTable<ReportObject> {

    public ParamTable() {
        ParamTableModel model = new ParamTableModel();
        setModel(model);
        setColumnModel(model.getTableColumnModel(this));
        updateColumnWidths();
    }

    @Override
    public void add(ReportObject reportObject) {
        ParamTableModel model = (ParamTableModel) getModel();
        model.addRecord(reportObject);
    }

    public void add(Param param) {
        if (param != null) {
            Collection<CvParam> cvParams = param.getCvParam();
            for (CvParam cvParam : cvParams) {
                add(cvParam);
            }
            List<UserParam> userParams = param.getUserParam();
            for (UserParam userParam : userParams) {
                add(userParam);
            }
        }
    }

    public void addAll(Collection<ReportObject> collection) {
        for (ReportObject t : collection) {
            add(t);
        }
    }

    public void addAll(List<Param> params) {
        for (Param param : params) {
            Collection<CvParam> cvParams = param.getCvParam();
            for (CvParam cvParam : cvParams) {
                add(cvParam);
            }
            List<UserParam> userParams = param.getUserParam();
            for (UserParam userParam : userParams) {
                add(userParam);
            }
        }
    }

    public List<UserParam> getUserParamList() {
        return ((ParamTableModel) getModel()).getUserParamList();
    }

    public List<CvParam> getCvParamList() {
        return ((ParamTableModel) getModel()).getCvParamList();
    }

    public void updateColumnWidths() {

        //update table column widths
        TableColumnModel model = getColumnModel();
        //set the width of the rest of the columns
        int total = model.getTotalColumnWidth();
        //first and last row are fixed width
        total = total - (BaseTableModel.SMALL_WIDTH * 2);
        //the rest of the columns should be proportionally spaced as such
        // 1 cv 7%
        // 2 accession 17%
        // 3 name 38%
        // 4 value 38%
        model.getColumn(1).setWidth((int) Math.floor(total * 0.07));
        model.getColumn(1).setMinWidth((int) Math.floor(total * 0.07));
        model.getColumn(1).setPreferredWidth((int) Math.floor(total * 0.07));
        model.getColumn(2).setWidth((int) Math.floor(total * 0.17));
        model.getColumn(2).setMinWidth((int) Math.floor(total * 0.17));
        model.getColumn(2).setPreferredWidth((int) Math.floor(total * 0.17));
        model.getColumn(3).setWidth((int) Math.floor(total * 0.38));
        model.getColumn(3).setMinWidth((int) Math.floor(total * 0.38));
        model.getColumn(3).setPreferredWidth((int) Math.floor(total * 0.38));
        model.getColumn(4).setWidth((int) Math.floor(total * 0.38));
        model.getColumn(4).setMinWidth((int) Math.floor(total * 0.38));
        model.getColumn(4).setPreferredWidth((int) Math.floor(total * 0.38));
        setColumnModel(model);

    }

}
