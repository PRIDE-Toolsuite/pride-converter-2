/*
 * Created by JFormDesigner on Tue Oct 25 14:47:54 BST 2011
 */

package uk.ac.ebi.pride.tools.converter.gui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author User #3
 */
public class ProgressDialog extends JDialog {

    private static final int DEFAULT_FONT_SIZE = 12;
    private SwingWorker worker;

    public ProgressDialog(Frame owner, SwingWorker worker) {
        super(owner);
        initComponents();
        this.worker = worker;
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    public ProgressDialog(Dialog owner, SwingWorker worker) {
        super(owner);
        initComponents();
        this.worker = worker;
    }

    private void button1ActionPerformed() {
        //TODO - need to catch interrrupted exception properly
        //worker.cancel(true);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        progressBar1 = new JProgressBar();
        messageLabel = new JLabel();
        panel1 = new JPanel();
        button1 = new JButton();

        //======== this ========
        setTitle("Working...");
        setResizable(false);
        Container contentPane = getContentPane();

        //---- progressBar1 ----
        progressBar1.setIndeterminate(true);

        //---- messageLabel ----
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        //======== panel1 ========
        {
            panel1.setLayout(new FlowLayout());

            //---- button1 ----
            button1.setText("Cancel");
            button1.setEnabled(false);
            button1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    button1ActionPerformed();
                }
            });
            panel1.add(button1);
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(panel1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 636, Short.MAX_VALUE)
                                        .addComponent(messageLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 636, Short.MAX_VALUE)
                                        .addComponent(progressBar1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 636, Short.MAX_VALUE))
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(progressBar1, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(messageLabel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JProgressBar progressBar1;
    private JLabel messageLabel;
    private JPanel panel1;
    private JButton button1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public void setMessage(String message) {
        messageLabel.setFont(resizeFont(message));
        messageLabel.setText(message);
        messageLabel.revalidate();
        repaint();
    }

    private Font resizeFont(String message) {

        Graphics2D g2 = (Graphics2D) getGraphics();

        //try default font first
        int fontSize = DEFAULT_FONT_SIZE;
        boolean foundFont = false;

        Font retval = new Font("TimesRoman", Font.PLAIN, fontSize);

        while (!foundFont) {

            retval = new Font("TimesRoman", Font.PLAIN, fontSize);
            g2.setFont(retval);
            FontMetrics fm = g2.getFontMetrics();
            //the message is still too wide for the panel to properly display
            if (fm.stringWidth(message) > getWidth()) {
                fontSize--;
            } else {
                foundFont = true;
            }

        }
        return retval;

    }

}
