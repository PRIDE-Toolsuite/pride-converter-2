package uk.ac.ebi.pride.tools.converter.gui.forms;

import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.gui.interfaces.ConverterForm;
import uk.ac.ebi.pride.tools.converter.gui.interfaces.ValidationListener;
import uk.ac.ebi.pride.tools.converter.gui.model.GUIException;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 08/11/11
 * Time: 17:43
 * To change this template use File | Settings | File Templates.
 */
public class MzTabReportForm implements ConverterForm {
    @Override
    public Collection<ValidatorMessage> validateForm() throws ValidatorException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void clear() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void save(ReportReaderDAO dao) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void load(ReportReaderDAO dao) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void loadTemplate(String templateName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getFormName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getFormDescription() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getHelpResource() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addValidationListener(ValidationListener validationListerner) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void start() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void finish() throws GUIException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
