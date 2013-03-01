package uk.ac.ebi.pride.tools.converter.report.io.xml.utilities;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.pride.tools.converter.dao.DAO;
import uk.ac.ebi.pride.tools.converter.dao.DAOFactory;
import uk.ac.ebi.pride.tools.converter.gui.model.ConverterData;
import uk.ac.ebi.pride.tools.converter.report.io.ReportReader;
import uk.ac.ebi.pride.tools.converter.report.model.SearchResultIdentifier;
import uk.ac.ebi.pride.tools.converter.report.validator.ReportSchemaValidator;
import uk.ac.ebi.pride.tools.converter.report.validator.ReportValidationErrorHandler;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 12/09/11
 * Time: 14:18
 */
public class ReportXMLUtilities {

    private static Logger logger = Logger.getLogger(ReportXMLUtilities.class);

    public static boolean isValidAnnotatedReportFile(File repFile) {
        try {
            if (repFile != null && repFile.exists()) {
                //perform schema validation on report file
                ReportSchemaValidator validator = new ReportSchemaValidator();
                ReportValidationErrorHandler errors = validator.validate(new FileReader(repFile));
                if (errors.noErrors()) {
                    return true;
                } else {
                    logger.error("Unexpected validation errror: " + errors.toString());
                    return false;
                }
            }
            return false;
        } catch (IOException e) {
            logger.error("Error validating file: " + repFile.getAbsolutePath(), e);
            return false;
        } catch (SAXException e) {
            logger.error("Error validating file: " + repFile.getAbsolutePath(), e);
            return false;
        }
    }

    public static boolean isValidNewReportFile(File repFile) {
        try {
            boolean containsUnexpectedError = false;
            if (repFile != null && repFile.exists()) {
                //perform schema validation on report file
                ReportSchemaValidator validator = new ReportSchemaValidator();
                ReportValidationErrorHandler errors = validator.validate(new FileReader(repFile));
                //a newly generated report file will have schema errors - this is normal
                //as long as those are the only errors reported!
                List<ValidatorMessage> messages = errors.getIssuesAsValidatorMessages();
                for (ValidatorMessage msg : messages) {
                    if (msg.getMessage().contains("sampleName")
                            || msg.getMessage().contains("instrumentName")
                            || msg.getMessage().contains("ProtocolName")
                            || msg.getMessage().contains("analyzerList")) {
                        // this is an expected error
                        continue;
                    } else {
                        //this is an unexpected error!
                        logger.error("Unexpected validation errror: " + msg.getMessage());
                        containsUnexpectedError = true;
                    }
                }
            }
            return !containsUnexpectedError;
        } catch (IOException e) {
            logger.error("Error validating file: " + repFile.getAbsolutePath(), e);
            return false;
        } catch (SAXException e) {
            logger.error("Error validating file: " + repFile.getAbsolutePath(), e);
            return false;
        }
    }

    public static boolean isUnmodifiedSourceForReportFile(File repFile, String reportSource) {

        try {
            if (repFile != null && repFile.exists()) {

                //read the checksum from the report and compare to the chechsum from the dao
                DAO dao = DAOFactory.getInstance().getDAO(reportSource, ConverterData.getInstance().getDaoFormat());
                SearchResultIdentifier originalSRI = dao.getSearchResultIdentifier();

                ReportReader reader = new ReportReader(repFile);
                SearchResultIdentifier reportSRI = reader.getSearchResultIdentifier();

                return reportSRI.getHash().equals(originalSRI.getHash());

            }
            return false;
        } catch (InvalidFormatException e) {
            logger.warn("Error reading report file, will overwrite: " + e.getMessage(), e);
            return false;
        }


    }
}
