package uk.ac.ebi.pride.tools.converter.report.io.xml.marshaller;

import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;

import java.io.OutputStream;
import java.io.Writer;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 15-Dec-2010
 * Time: 15:01:25
 * To change this template use File | Settings | File Templates.
 */
public interface ReportMarshaller {

    public <T extends ReportObject> String marshall(T object);

    public <T extends ReportObject> void marshall(T object, OutputStream os);

    public <T extends ReportObject> void marshall(T object, Writer out);

}
