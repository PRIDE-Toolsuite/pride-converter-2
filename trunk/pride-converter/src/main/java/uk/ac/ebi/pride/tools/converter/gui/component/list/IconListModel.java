package uk.ac.ebi.pride.tools.converter.gui.component.list;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 06/03/12
 * Time: 14:10
 */
public class IconListModel extends DefaultListModel {

    Map<String, Icon> icons = new HashMap<String, Icon>();

    public void addElement(String label, Icon icon) {
        icons.put(label, icon);
        super.addElement(label);
    }

    public Icon getIconForValue(String label) {
        return icons.get(label);
    }

    public void add(int index, String label, Icon icon) {
        icons.put(label, icon);
        super.add(index, label);
    }
}
