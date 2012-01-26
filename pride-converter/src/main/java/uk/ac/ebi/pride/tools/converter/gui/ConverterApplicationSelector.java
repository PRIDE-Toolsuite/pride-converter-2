package uk.ac.ebi.pride.tools.converter.gui;

import uk.ac.ebi.pride.tools.converter.conversion.PrideConverter;
import uk.ac.ebi.pride.tools.converter.gui.forms.*;
import uk.ac.ebi.pride.tools.converter.gui.model.OutputFormat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 18/08/11
 * Time: 09:26
 */
public class ConverterApplicationSelector extends JFrame {

    private static ConverterApplicationSelector selector = null;

    public ConverterApplicationSelector() {
        initComponents();
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            try {
                UIManager.setLookAndFeel("com.jtattoo.plaf.mint.MintLookAndFeel");
                selector = new ConverterApplicationSelector();
                selector.pack();
                selector.setVisible(true);
            } catch (Exception e) {
                System.exit(1);
            }
        } else {

            //todo
            //-converter pass args[1..n]
            //-filter pass args[1..n]
            //-merger pass args[1..n]

            PrideConverter.main(args);
        }

    }

    private void launchTool(ActionEvent e) {

        selector.setVisible(false);
        selector.dispose();

        if (e.getSource().equals(converterButton)) {
            launchConverter();
        } else if (e.getSource().equals(mergerButton)) {
            launchMerger();
        } else if (e.getSource().equals(filterButton)) {
            launchFilter();
        } else if (e.getSource().equals(mzTabButton)) {
            launchMzTab();
        } else {
            throw new IllegalArgumentException("No launch method defined for button: " + e.getSource());
        }

    }

    private void launchMzTab() {
//        NavigationPanel panel = NavigationPanel.getInstance();
//        panel.registerForm(new DataTypeForm());
//        panel.registerForm(new FileSelectionForm(OutputFormat.MZTAB));
//        panel.registerForm(new MzTabOptionForm());
//        panel.registerForm(new MzTabReportForm());
//        panel.reset();
    }

    private void launchConverter() {
        NavigationPanel panel = NavigationPanel.getInstance();
        panel.registerForm(new DataTypeForm());
        panel.registerForm(new FileSelectionForm(OutputFormat.PRIDE_XML));
        panel.registerForm(new ExperimentDetailForm());
        panel.registerForm(new ContactForm());
        panel.registerForm(new ReferenceForm());
        panel.registerForm(new SampleForm());
        panel.registerForm(new ProtocolForm());
        panel.registerForm(new InstrumentForm());
        panel.registerForm(new SoftwareProcessingForm());
        panel.registerForm(new DatabaseMappingForm());
        panel.registerForm(new PTMForm());
        panel.registerForm(new AnnotationDoneForm());
        panel.registerForm(new FileExportForm());
        panel.registerForm(new ReportForm());
        panel.reset();
    }

    private void launchMerger() {
        System.err.println("Merger not implemented yet");
        System.exit(1);
    }

    private void launchFilter() {
        FilterGUI filterGUI = new FilterGUI();
        filterGUI.setVisible(true);
    }

    private void exitButtonActionPerformed() {
        System.exit(0);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        converterButton = new JButton();
        mzTabButton = new JButton();
        filterButton = new JButton();
        mergerButton = new JButton();
        exitButton = new JButton();

        //======== this ========
        Container contentPane = getContentPane();

        //---- converterButton ----
        converterButton.setText("Launch PRIDE Converter");
        converterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchTool(e);
            }
        });

        //---- mzTabButton ----
        mzTabButton.setText("Launch PRIDE mzTab Generator");
        mzTabButton.setEnabled(false);
        mzTabButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchTool(e);
            }
        });

        //---- filterButton ----
        filterButton.setText("Launch PRIDE Filter");
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchTool(e);
            }
        });

        //---- mergerButton ----
        mergerButton.setText("Launch PRIDE Merger");
        mergerButton.setEnabled(false);
        mergerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchTool(e);
            }
        });

        //---- exitButton ----
        exitButton.setText("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitButtonActionPerformed();
            }
        });

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(converterButton, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                                        .addComponent(mergerButton, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                                        .addComponent(filterButton, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                                        .addComponent(mzTabButton, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                                        .addComponent(exitButton, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE))
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(converterButton, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mzTabButton, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mergerButton, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(filterButton, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(exitButton, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JButton converterButton;
    private JButton mzTabButton;
    private JButton filterButton;
    private JButton mergerButton;
    private JButton exitButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
