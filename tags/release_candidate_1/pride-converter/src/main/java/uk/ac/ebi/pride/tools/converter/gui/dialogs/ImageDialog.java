package uk.ac.ebi.pride.tools.converter.gui.dialogs;

import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author User #3
 * @author rcote
 */
public class ImageDialog extends JDialog {

    private static final Logger logger = Logger.getLogger(ImageDialog.class);

    private JLabel label1;

    public ImageDialog(Frame owner) {
        super(owner);
        initComponents();
    }

    public ImageDialog(Dialog owner) {
        super(owner);
        initComponents();
    }

    private void initComponents() {
        label1 = new JLabel();

        //======== this ========
        setTitle("PRIDE Team - June 2011");
        Container contentPane = getContentPane();

        //---- label1 ----
        label1.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            BufferedImage wp = ImageIO.read(this.getClass().getClassLoader().getResource("images/group.jpg"));
            label1.setIcon(new ImageIcon(wp));
        } catch (IOException e) {
            logger.warn("Could not load image: " + e.getMessage(), e);
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(label1, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(label1, GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                                .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
    }


}
