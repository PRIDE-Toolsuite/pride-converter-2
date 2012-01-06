package uk.ac.ebi.pride.tools.converter.gui.component.table.model;

import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;
import uk.ac.ebi.pride.tools.converter.report.model.UserParam;

import java.util.ArrayList;
import java.util.List;

/**
 * COntains RowNuber, Accession, Name, Value columns
 * don't use add(ReportObject object) directly at super class BaseTableModel
 *
 * @author melih
 *         Date: 04/04/2011
 *         Time: 16:14
 */
public class ParamTableModel extends BaseTableModel<ReportObject> {

    public ParamTableModel() {

        String name = resourceBundle.getString("Param.name.text");
        String value = resourceBundle.getString("Param.value.text");
        String accession = resourceBundle.getString("Param.accession.text");
        String cv = resourceBundle.getString("Param.cv.text");


        /**
         * column name, editable
         * 0 [ row number, false ]
         * 1 [ cv, false ]
         * 2 [ accession,, false ]
         * 3 [ name, false ]
         * 4 [ value, false ]
         * 5 [ delete action, true ]
         * 6 [ data, false]
         */
        columnNames = new String[]{"", cv, accession, name, value, "", ""};

        columnEditable = new boolean[]{false, false, false, false, false, true, false};

        columnTypes = new Class<?>[]{String.class, String.class, String.class, String.class, String.class, String.class, ReportObject.class};

        dataColumnIndex = 6;

    }

    @Override
    protected Object[] getRowObjectArray(ReportObject param) {
        if (param instanceof CvParam) return getRowObjectArray((CvParam) param);
        else if (param instanceof UserParam) return getRowObjectArray((UserParam) param);
        else throw new IllegalArgumentException("Object must be a CvParam or UserParam");
    }

    private Object[] getRowObjectArray(UserParam userParam) {
        return new Object[]{"", "", "", userParam.getName(), userParam.getValue(), "", userParam};
    }

    private Object[] getRowObjectArray(CvParam cvParam) {
        return new Object[]{"", cvParam.getCvLabel(), cvParam.getAccession(), cvParam.getName(), cvParam.getValue(), "", cvParam};
    }

    public List<UserParam> getUserParamList() {
        ArrayList<UserParam> lst = new ArrayList<UserParam>();
        for (int i = 0; i < getRowCount(); i++) {
            Object obj = getValueAt(i, dataColumnIndex);
            if (obj instanceof UserParam) {
                lst.add((UserParam) obj);
            }
        }
        return lst;
    }

    public List<CvParam> getCvParamList() {
        ArrayList<CvParam> lst = new ArrayList<CvParam>();
        for (int i = 0; i < getRowCount(); i++) {
            Object obj = getValueAt(i, dataColumnIndex);
            if (obj instanceof CvParam) {
                lst.add((CvParam) obj);
            }
        }
        return lst;
    }
}
