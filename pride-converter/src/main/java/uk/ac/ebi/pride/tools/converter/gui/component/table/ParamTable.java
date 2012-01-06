package uk.ac.ebi.pride.tools.converter.gui.component.table;

import uk.ac.ebi.pride.tools.converter.gui.component.table.model.ParamTableModel;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;
import uk.ac.ebi.pride.tools.converter.report.model.UserParam;

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
        super();
        ParamTableModel model = new ParamTableModel();
        setModel(model);
        setColumnModel(model.getTableColumnModel(this));
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

}
