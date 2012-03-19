package uk.ac.ebi.pride.tools.converter.gui.component.table.model;

import uk.ac.ebi.pride.tools.converter.dao.handler.QuantitationCvParams;
import uk.ac.ebi.pride.tools.converter.gui.model.ProtectedCvParam;
import uk.ac.ebi.pride.tools.converter.gui.model.ProtectedUserParam;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;
import uk.ac.ebi.pride.tools.converter.report.model.UserParam;

import java.util.ArrayList;
import java.util.HashSet;
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

    private static HashSet<String> protectedUserParams;

    static {
        protectedUserParams = new HashSet<String>();
        //currently required by PRIDE Inspector
        protectedUserParams.add("Available protein quantitation fields");
        protectedUserParams.add("Available peptide quantitation fields");
    }

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

        columnTypes = new Class<?>[]{String.class, String.class, String.class, String.class, String.class, Boolean.class, ReportObject.class};

        dataColumnIndex = 6;

    }

    @Override
    protected Object[] getRowObjectArray(ReportObject param) {
        if (param instanceof CvParam) return getRowObjectArray((CvParam) param);
        else if (param instanceof UserParam) return getRowObjectArray((UserParam) param);
        else throw new IllegalArgumentException("Object must be a CvParam or UserParam");
    }

    private Object[] getRowObjectArray(UserParam userParam) {
        boolean protectedParam = isUserParamProtected(userParam) || userParam instanceof ProtectedUserParam;
        return new Object[]{"", "", "", userParam.getName(), userParam.getValue(), protectedParam, userParam};
    }

    private boolean isUserParamProtected(UserParam userParam) {
        return protectedUserParams.contains(userParam.getName());
    }

    private Object[] getRowObjectArray(CvParam cvParam) {
        boolean protectedParam = isCvParamProtected(cvParam) || cvParam instanceof ProtectedCvParam;
        return new Object[]{"", cvParam.getCvLabel(), cvParam.getAccession(), cvParam.getName(), cvParam.getValue(), protectedParam, cvParam};
    }

    private boolean isCvParamProtected(CvParam cvParam) {
        //protect all quantitation params
        if (QuantitationCvParams.isAQuantificationParam(cvParam.getAccession())) {
            return true;
        }
        return false;
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

    /**
     * Indicates whether a row should be protected (i.e. not editable) even though the rest
     * of the table might be editable
     *
     * @param rowNumber
     * @return
     */
    @Override
    public boolean isRowProtected(int rowNumber) {
        Object obj = getValueAt(rowNumber, dataColumnIndex);
        if (obj instanceof ProtectedCvParam || obj instanceof ProtectedCvParam) {
            return true;
        }
        if (obj instanceof UserParam) {
            return isUserParamProtected((UserParam) obj);
        }
        if (obj instanceof CvParam) {
            return isCvParamProtected((CvParam) obj);
        }
        return false;
    }

}
