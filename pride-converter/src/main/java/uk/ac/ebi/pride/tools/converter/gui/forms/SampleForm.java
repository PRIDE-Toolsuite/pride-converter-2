/*
 * Created by JFormDesigner on Fri Mar 11 11:02:12 GMT 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.gui.component.panels.SamplePanel;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;
import uk.ac.ebi.pride.tools.converter.report.model.Description;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.validator.ReportObjectValidator;
import uk.ac.ebi.pride.tools.converter.utils.xml.validation.ValidatorFactory;

import javax.swing.*;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 * @author Melih Birim
 * @author rcote
 */
public class SampleForm extends AbstractForm {

    public SampleForm() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("messages");
        tabbedPane1 = new JTabbedPane();
        samplePanel1 = new SamplePanel();
        label1 = new JLabel();

        //======== this ========

        //======== tabbedPane1 ========
        {
            tabbedPane1.addTab(bundle.getString("SampleForm.samplePanel1.tab.title"), samplePanel1);

        }

        //---- label1 ----
        label1.setText(bundle.getString("SampleForm.label1.text"));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(tabbedPane1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
                                        .addComponent(label1))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(label1)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tabbedPane1, GroupLayout.DEFAULT_SIZE, 627, Short.MAX_VALUE)
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JTabbedPane tabbedPane1;
    private SamplePanel samplePanel1;
    private JLabel label1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    @Override
    public Collection<ValidatorMessage> validateForm() throws ValidatorException {
        //todo - properly handle subsamples
        ReportObjectValidator validator = ValidatorFactory.getInstance().getReportValidator();
        Description sample = new Description();
        sample.setComment(samplePanel1.getSampleComment());
        Param p = samplePanel1.getSampleParams();
        sample.getCvParam().addAll(p.getCvParam());
        sample.getUserParam().addAll(p.getUserParam());
        return validator.validate(sample);
    }

    @Override
    public void clear() {
        //todo - properly handle subsamples!
        isLoaded = false;
        samplePanel1.clear();
        //inactivate next button
        validationListerner.fireValidationListener(false);
    }

    @Override
    public void save(ReportReaderDAO dao) {
        //todo - properly handle  subsamples!
        dao.setSampleName(samplePanel1.getSampleName());
        dao.setSampleComment(samplePanel1.getSampleComment());
        dao.setSampleParams(samplePanel1.getSampleParams());
    }

    @Override
    public void load(ReportReaderDAO dao) {
        //todo - properly handle  subsamples!
        if (!isLoaded) {
            samplePanel1.setSampleName(dao.getSampleName());
            samplePanel1.setSampleComment(dao.getSampleComment());
            samplePanel1.setSampleParams(dao.getSampleParams());
            isLoaded = true;
        }
        //fire validation listener on load
        samplePanel1.fileValidationListener();
    }

    @Override
    public String getFormName() {
        return "Sample Description";
    }

    @Override
    public String getFormDescription() {
        return config.getString("sample.form.description");
    }

    @Override
    public String getHelpResource() {
        return "help.ui.sample";
    }

    @Override
    public void start() {
        //for back & forth navigation
        samplePanel1.addValidationListener(validationListerner);
        samplePanel1.fileValidationListener();
    }

    @Override
    public void finish() {
        /* no op */
    }
}
