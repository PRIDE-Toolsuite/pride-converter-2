package uk.ac.ebi.pride.tools.converter.gui.component.list;

import uk.ac.ebi.pride.tools.converter.gui.NavigationPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 06/03/12
 * Time: 14:10
 * To change this template use File | Settings | File Templates.
 */
public class IconListCellRenderer extends DefaultListCellRenderer {

    public Component getListCellRendererComponent(JList list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {

        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, index == NavigationPanel.getInstance().getSelectedIndex(), index == NavigationPanel.getInstance().getSelectedIndex());
        Icon icon = ((IconListModel) list.getModel()).getIconForValue(value.toString());
        if (icon != null) {
            label.setIcon(icon);
        }
        return label;

    }

}
