package uk.ac.ebi.pride.tools.converter.gui.component.combobox;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class IgnoreKeySelectionManager implements JComboBox.KeySelectionManager, KeyListener {
//don't want to have keyboard input for combobox

    @Override
    public int selectionForKey(char aKey, ComboBoxModel aModel) {
        return -1;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        e.consume();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        e.consume();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        e.consume();
    }
}