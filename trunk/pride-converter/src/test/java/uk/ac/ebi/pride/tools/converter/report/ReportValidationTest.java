package uk.ac.ebi.pride.tools.converter.report;

import junit.framework.TestCase;
import uk.ac.ebi.pride.tools.converter.report.io.xml.utilities.ReportXMLUtilities;

import java.io.File;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: rcote
 * Date: 28/02/13
 * Time: 16:12
 * To change this template use File | Settings | File Templates.
 */
public class ReportValidationTest extends TestCase {

    public void testReportValidation() throws Exception {

        URL url = getClass().getClassLoader().getResource("minimal-report.xml");
        assertEquals("file is schematically invalid", true, ReportXMLUtilities.isValidAnnotatedReportFile(new File(url.toURI())));

        url = getClass().getClassLoader().getResource("new-generated-report.xml");
        assertEquals("file should be schematically invalid", false, ReportXMLUtilities.isValidNewReportFile(new File(url.toURI())));

    }


}
