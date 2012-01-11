package uk.ac.ebi.pride.tools.converter.gui.model;

import uk.ac.ebi.pride.tools.converter.report.model.Description;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 11/01/12
 * Time: 11:53
 * To change this template use File | Settings | File Templates.
 */
public class ReportBean {

    private String shortLabel;
    private String experimentTitle;
    private Description sampleDescription;
    private String sampleName;

    public String getShortLabel() {
        return shortLabel;
    }

    public void setShortLabel(String shortLabel) {
        this.shortLabel = shortLabel;
    }

    public String getExperimentTitle() {
        return experimentTitle;
    }

    public void setExperimentTitle(String experimentTitle) {
        this.experimentTitle = experimentTitle;
    }

    public Description getSampleDescription() {
        return sampleDescription;
    }

    public void setSampleDescription(Description sampleDescription) {
        this.sampleDescription = sampleDescription;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }
}
