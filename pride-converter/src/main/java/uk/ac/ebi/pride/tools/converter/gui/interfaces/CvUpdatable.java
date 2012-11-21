package uk.ac.ebi.pride.tools.converter.gui.interfaces;

import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 05/01/12
 * Time: 16:24
 */
public interface CvUpdatable<T extends ReportObject> {

    public void add(T objectToAdd);

    public void update(T objectToUpdate, int objectIndex);

}
