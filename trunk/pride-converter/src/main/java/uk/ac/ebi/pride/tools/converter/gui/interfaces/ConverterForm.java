package uk.ac.ebi.pride.tools.converter.gui.interfaces;

import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.gui.model.GUIException;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;

import javax.swing.*;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 21/10/11
 * Time: 16:22
 */
public interface ConverterForm {

    public Collection<ValidatorMessage> validateForm() throws ValidatorException;

    public void clear();

    public void save(ReportReaderDAO dao);

    public void load(ReportReaderDAO dao);

    public void loadTemplate(String templateName);

    public String getFormName();

    public Icon getFormIcon();

    public String getFormDescription();

    public String getHelpResource();

    public void addValidationListener(ValidationListener validationListerner);

    public void start();

    public void finish() throws GUIException;

}
