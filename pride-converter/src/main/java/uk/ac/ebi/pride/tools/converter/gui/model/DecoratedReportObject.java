package uk.ac.ebi.pride.tools.converter.gui.model;

import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 24/02/12
 * Time: 15:19
 */
public interface DecoratedReportObject<T> extends ReportObject {

    public Color getBackground();
    public void setBackground(Color color);
    public <T extends ReportObject> T getInner();

}
