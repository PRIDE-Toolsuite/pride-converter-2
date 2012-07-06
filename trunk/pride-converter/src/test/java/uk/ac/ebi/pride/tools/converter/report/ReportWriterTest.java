package uk.ac.ebi.pride.tools.converter.report;

import junit.framework.TestCase;
import uk.ac.ebi.pride.tools.converter.utils.xml.SchemaValidationReport;
import uk.ac.ebi.pride.tools.converter.utils.xml.XMLUtils;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 04/01/11
 * Time: 16:37
 * To change this template use File | Settings | File Templates.
 */
public class ReportWriterTest extends TestCase {

    public void testWriteReport() throws Exception {

        boolean validXML = false;
        ReportWriterSingleton.generateTestReportFile();
        File reportFile = new File("testReportFileOut.xml");
        //validate xml
        SchemaValidationReport valid = XMLUtils.validateReportFile(reportFile);
        assertTrue("ReportWriter generated invalid XML", valid.noErrors());
        assertTrue("Error during file delete", reportFile.delete());

    }

}
