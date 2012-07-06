/*
 * Created by JFormDesigner on Mon Apr 02 16:30:02 BST 2012
 */

package uk.ac.ebi.pride.tools.converter.gui.forms;

import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.gui.NavigationPanel;
import uk.ac.ebi.pride.tools.converter.gui.component.BrowserLauncher;
import uk.ac.ebi.pride.tools.converter.gui.model.GUIException;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReaderDAO;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * @author User #3
 */
public class OtherToolsForm extends AbstractForm {
    public OtherToolsForm() {
        initComponents();
        try {
            editorPane.setEditorKit(new HTMLEditorKit());
            editorPane.setContentType("text/html");
            editorPane.setPage(getClass().getResource("OtherToolsForm.html"));
        } catch (IOException e) {
            throw new IllegalStateException("Could not load page content", e);
        }
    }


    private void editorPaneHyperlinkUpdate(HyperlinkEvent evt) {
        if (evt.getEventType().equals(HyperlinkEvent.EventType.ENTERED)) {
            setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        } else if (evt.getEventType().equals(HyperlinkEvent.EventType.EXITED)) {
            setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        } else if (evt.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
            if (evt.getDescription().startsWith("#")) {
                editorPane.scrollToReference(evt.getDescription());
            } else {
                this.setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));
                BrowserLauncher.openURL(evt.getDescription());
                this.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            }
        }
    }

    private void openWebsite(MouseEvent e) {
        if (e.getClickCount() != 2) {
            return;
        }

        String url = null;
        if (e.getSource().equals(pxLabel)) {
            url = "http://www.proteomexchange.org";
        } else if (e.getSource().equals(piLabel)) {
            url = "http://code.google.com/p/pride-toolsuite/downloads/list";
        } else {
            throw new IllegalStateException("Unknown source: " + e.getSource());
        }
        this.setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));
        BrowserLauncher.openURL(url);
        this.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }

    private void showHandCursor() {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void showDefaultCursor() {
        setCursor(Cursor.getDefaultCursor());
    }

    private void openWebsite(ActionEvent e) {
        String url = null;
        if (e.getSource().equals(pxLabelButton)) {
            url = "http://www.proteomexchange.org";
        } else if (e.getSource().equals(piLabelButton)) {
            url = "http://code.google.com/p/pride-toolsuite/downloads/list";
        } else {
            throw new IllegalStateException("Unknown source: " + e.getSource());
        }
        this.setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));
        BrowserLauncher.openURL(url);
        this.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        scrollPane1 = new JScrollPane();
        editorPane = new JEditorPane();
        piLabel = new JLabel();
        pxLabel = new JLabel();
        piLabelButton = new JButton();
        pxLabelButton = new JButton();

        //======== this ========

        //======== scrollPane1 ========
        {

            //---- editorPane ----
            editorPane.setContentType("text/html");
            editorPane.setEditable(false);
            editorPane.addHyperlinkListener(new HyperlinkListener() {
                @Override
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    editorPaneHyperlinkUpdate(e);
                }
            });
            scrollPane1.setViewportView(editorPane);
        }

        //---- piLabel ----
        piLabel.setIcon(new ImageIcon(getClass().getResource("/images/pi-logo.png")));
        piLabel.setHorizontalAlignment(SwingConstants.CENTER);
        piLabel.setToolTipText("Double-click to get PRIDE Inspector");
        piLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openWebsite(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                showHandCursor();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                showDefaultCursor();
            }
        });

        //---- pxLabel ----
        pxLabel.setHorizontalAlignment(SwingConstants.CENTER);
        pxLabel.setIcon(new ImageIcon(getClass().getResource("/images/px-logo.png")));
        pxLabel.setToolTipText("Double-click to go to ProteomExchange");
        pxLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openWebsite(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                showHandCursor();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                showDefaultCursor();
            }
        });

        //---- piLabelButton ----
        piLabelButton.setText("Start PRIDE Inspector");
        piLabelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openWebsite(e);
            }
        });

        //---- pxLabelButton ----
        pxLabelButton.setText("Go to ProteomeXchange");
        pxLabelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openWebsite(e);
            }
        });

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(scrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup()
                                                        .addComponent(piLabelButton, GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                                                        .addComponent(piLabel, GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup()
                                                        .addComponent(pxLabel, GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                                                        .addComponent(pxLabelButton, GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE))))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(piLabel)
                                        .addComponent(pxLabel))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(piLabelButton)
                                        .addComponent(pxLabelButton))
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JScrollPane scrollPane1;
    private JEditorPane editorPane;
    private JLabel piLabel;
    private JLabel pxLabel;
    private JButton piLabelButton;
    private JButton pxLabelButton;
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
        return "help.ui.tools";
    }

    @Override
    public void start() {
        NavigationPanel.getInstance().hideValidationIcon();
        //validate form and fire validationListener - required for back & forth when no changes occur
        validationListerner.fireValidationListener(true);
    }

    @Override
    public void finish() throws GUIException {
        //no op
    }

}
