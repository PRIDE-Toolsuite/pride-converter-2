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
            editorPane.setPage(getClass().getClassLoader().getResource("help/html/usage/ui/othertools.html"));
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

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        scrollPane1 = new JScrollPane();
        editorPane = new JEditorPane();
        panel1 = new JPanel();
        piLabel = new JLabel();
        pxLabel = new JLabel();

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

        //======== panel1 ========
        {
            panel1.setLayout(new GridLayout(0, 2, 5, 5));

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
            panel1.add(piLabel);

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
            panel1.add(pxLabel);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(scrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
                                        .addComponent(panel1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel1, GroupLayout.PREFERRED_SIZE, 107, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JScrollPane scrollPane1;
    private JEditorPane editorPane;
    private JPanel panel1;
    private JLabel piLabel;
    private JLabel pxLabel;
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

    public static void main(String[] args) {
        NavigationPanel p = NavigationPanel.getInstance();
        p.registerForm(new OtherToolsForm());
        p.reset();
    }

}
