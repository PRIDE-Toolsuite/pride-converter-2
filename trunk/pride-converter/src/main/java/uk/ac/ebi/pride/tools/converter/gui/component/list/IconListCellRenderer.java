package uk.ac.ebi.pride.tools.converter.gui.component.list;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.gui.NavigationPanel;
import uk.ac.ebi.pride.tools.converter.gui.util.Colours;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 06/03/12
 * Time: 14:10
 * To change this template use File | Settings | File Templates.
 */
public class IconListCellRenderer extends DefaultListCellRenderer {

    private static final Logger logger = Logger.getLogger(IconListCellRenderer.class);

    public Component getListCellRendererComponent(JList list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {

        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, index == NavigationPanel.getInstance().getSelectedIndex(), index == NavigationPanel.getInstance().getSelectedIndex());
        ImageIcon icon = (ImageIcon) ((IconListModel) list.getModel()).getIconForValue(value.toString());
        if (icon != null) {
            label.setIcon(icon);
        }

        if (index < NavigationPanel.getInstance().getSelectedIndex()) {
            //we've already done this item, so grey it out a bit
//            label.setForeground(Color.white);
            label.setBackground(Colours.selectedGreen);

            try {
                //update icon with check overlay
                // create new image
                BufferedImage combined = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                //read overlay
                BufferedImage overlay = ImageIO.read(getClass().getClassLoader().getResource("images/check.png"));

                // paint both images, preserving the alpha channels
                Graphics g = combined.getGraphics();
                g.drawImage(icon.getImage(), 0, 0, null);
                g.drawImage(overlay, 10, 10, null);

                // set new icon
                label.setIcon(new ImageIcon(combined));
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }

        }

        return label;

    }

}
