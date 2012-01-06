package uk.ac.ebi.pride.tools.converter.gui.component.panels.model;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 05/01/12
 * Time: 15:48
 */
public class SampleCvComboBoxModel extends DefaultComboBoxModel {

    private boolean allowMultipleValues = false;
    private String CV;

    public SampleCvComboBoxModel(String cvLabel, boolean allowMultipleValues, Object items[]) {
        super(items);
        this.CV = cvLabel;
        this.allowMultipleValues = allowMultipleValues;
    }

    public boolean isAllowMultipleValues() {
        return allowMultipleValues;
    }

    public String getCV() {
        return CV;
    }
}
