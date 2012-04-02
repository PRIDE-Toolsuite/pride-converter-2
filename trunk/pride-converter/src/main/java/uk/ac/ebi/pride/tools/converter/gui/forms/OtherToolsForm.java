/*
 * Created by JFormDesigner on Mon Apr 02 16:30:02 BST 2012
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.gui.model.GUIException;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.Collections;

/**
 * @author User #3
 */
public class OtherToolsForm extends AbstractForm {
    public OtherToolsForm() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        label1 = new JLabel();
        label2 = new JLabel();
        scrollPane1 = new JScrollPane();
        textArea1 = new JTextArea();
        scrollPane2 = new JScrollPane();
        textArea2 = new JTextArea();
        scrollPane3 = new JScrollPane();
        textArea3 = new JTextArea();

        //======== this ========

        //---- label1 ----
        label1.setIcon(new ImageIcon(getClass().getResource("/images/px-logo.png")));
        label1.setBackground(Color.black);

        //---- label2 ----
        label2.setIcon(new ImageIcon(getClass().getResource("/images/pi-logo.png")));

        //======== scrollPane1 ========
        {

            //---- textArea1 ----
            textArea1.setBackground(null);
            textArea1.setEditable(false);
            textArea1.setWrapStyleWord(true);
            textArea1.setLineWrap(true);
            textArea1.setText("PRIDE Inspector is a desktop application to visualize and perform first quality assessment on Mass Spectrometry data.");
            scrollPane1.setViewportView(textArea1);
        }

        //======== scrollPane2 ========
        {

            //---- textArea2 ----
            textArea2.setBackground(null);
            textArea2.setEditable(false);
            textArea2.setLineWrap(true);
            textArea2.setWrapStyleWord(true);
            textArea2.setText("The 'ProteomeXchange' consortium has been set up to provide a single point of submission of MS proteomics data to the main existing proteomics repositories, and encourage the data exchange between them so that the community may easily benefit.");
            scrollPane2.setViewportView(textArea2);
        }

        //======== scrollPane3 ========
        {

            //---- textArea3 ----
            textArea3.setText("Your search result files have now been converted to PRIDE XML. Several tools are available to help ");
            textArea3.setEditable(false);
            textArea3.setLineWrap(true);
            textArea3.setWrapStyleWord(true);
            scrollPane3.setViewportView(textArea3);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(scrollPane3, GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(label2, GroupLayout.Alignment.LEADING)
                                                        .addComponent(label1, GroupLayout.Alignment.LEADING))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup()
                                                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                                                        .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE))))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane3, GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                                        .addComponent(label2))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                                        .addComponent(label1))
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JLabel label2;
    private JScrollPane scrollPane1;
    private JTextArea textArea1;
    private JScrollPane scrollPane2;
    private JTextArea textArea2;
    private JScrollPane scrollPane3;
    private JTextArea textArea3;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    @Override
    public Collection<ValidatorMessage> validateForm() throws ValidatorException {
        return Collections.emptyList();
    }

    @Override
    public void clear() {
        //no op
    }

    @Override
    public void save(ReportReaderDAO dao) {
        //no op
    }

    @Override
    public void load(ReportReaderDAO dao) throws GUIException {
        //no op
    }

    @Override
    public String getFormName() {
        return "Helpful Tools";
    }

    @Override
    public Icon getFormIcon() {
        return getFormIcon("othertools.form.icon");
    }

    @Override
    public String getFormDescription() {
        return config.getString("othertools.form.description");

    }

    @Override
    public String getHelpResource() {
        return "help.index";
    }

    @Override
    public void start() {
        //no op
    }

    @Override
    public void finish() throws GUIException {
        //no op
    }
}
