package uk.ac.ebi.pride.tools.converter.gui.component.table;

import psidev.psi.tools.validator.ValidatorException;
import uk.ac.ebi.pride.tools.converter.gui.component.table.model.BaseTableModel;
import uk.ac.ebi.pride.tools.converter.gui.dialogs.AbstractDialog;
import uk.ac.ebi.pride.tools.converter.gui.interfaces.CvUpdatable;
import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;
import uk.ac.ebi.pride.tools.converter.report.validator.ReportObjectValidator;
import uk.ac.ebi.pride.tools.converter.utils.xml.validation.ValidatorFactory;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.List;

/**
 * Base Table
 *
 * @author melih
 *         Date: 06/04/2011
 *         Time: 10:33
 */
public class BaseTable<T extends ReportObject> extends JTable implements CvUpdatable<T> {

    private static final int DOUBLE_CLICK_COUNT = 2;

    private static final Color grey = new Color(219, 213, 218);
    private static final Color lightBlue = new Color(100, 117, 245);
    private static final Color errorRed = new Color(255, 102, 102);

    private BaseTable<T> _this;
    protected int modelSelectedRow;
    protected boolean enableRowValidation = false;

    public BaseTable() {
        _this = this;

        setAutoCreateRowSorter(true);
        getTableHeader().setResizingAllowed(true);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == DOUBLE_CLICK_COUNT) {

                    //convert table selected row to underlying model row
                    modelSelectedRow = convertRowIndexToModel(getSelectedRow());
                    //get object
                    BaseTableModel<T> tableModel = (BaseTableModel<T>) getModel();
                    T objToEdit = tableModel.get(modelSelectedRow);
                    Class clazz = objToEdit.getClass();
                    //show editing dialog for object
                    AbstractDialog dialog = AbstractDialog.getInstance(_this, clazz);
                    dialog.edit(objToEdit);
                    dialog.setVisible(true);
                }
            }
        });

    }

    public void add(T t) {
        TableModel model = getModel();
        ((BaseTableModel) model).addRecord(t);
    }

    public void update(T t) {
        BaseTableModel<T> tableModel = (BaseTableModel<T>) getModel();
        tableModel.edit(modelSelectedRow, t);
    }

    public void addAll(Collection<T> collection) {
        for (T t : collection) {
            add(t);
        }
    }

    public List<T> getAll() {
        return ((BaseTableModel<T>) getModel()).getList();
    }

    public void removeAll() {
        BaseTableModel<T> model = (BaseTableModel<T>) getModel();
        model.removeAll();
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer,
                                     int rowIndex, int vColIndex) {

        Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);

        if (isCellSelected(rowIndex, vColIndex)) {
            c.setBackground(lightBlue);
        } else {

            if (enableRowValidation) {
                //get object at row
                modelSelectedRow = convertRowIndexToModel(rowIndex);
                //get object
                BaseTableModel<T> tableModel = (BaseTableModel<T>) getModel();
                T objToValidate = tableModel.get(modelSelectedRow);
                if (objToValidate instanceof ReportObject) {

                    //validate object
                    try {
                        ReportObjectValidator validator = ValidatorFactory.getInstance().getReportValidator();
                        //if object has errors
                        if (validator.hasError(objToValidate)) {
                            c.setBackground(errorRed);
                        } else {
                            //no errors - use alternate colors
                            if (rowIndex % 2 == 0) {
                                c.setBackground(grey);
                            } else {
                                c.setBackground(getBackground());
                            }
                        }

                    } catch (ValidatorException e) {
                        //eat exception at this point, because this is only for UI decoration
                        //the exception will be caught properly during the form validation
                    }

                }

            } else {
                //just alternate colours
                if (rowIndex % 2 == 0) {
                    c.setBackground(grey);
                } else {
                    c.setBackground(getBackground());
                }

            }
        }
        return c;
    }

    public void setEnableRowValidation(boolean enableRowValidation) {
        this.enableRowValidation = enableRowValidation;
    }

}
