package uk.ac.ebi.pride.tools.converter.gui.util.template;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.report.io.xml.marshaller.ReportMarshaller;
import uk.ac.ebi.pride.tools.converter.report.io.xml.marshaller.ReportMarshallerFactory;
import uk.ac.ebi.pride.tools.converter.report.io.xml.unmarshaller.ReportUnmarshaller;
import uk.ac.ebi.pride.tools.converter.report.io.xml.unmarshaller.ReportUnmarshallerFactory;
import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 04/11/11
 * Time: 22:03
 */
public class TemplateUtilities {

    private static final Logger logger = Logger.getLogger(TemplateUtilities.class);
    private static final String BASE_TEMPLATE_PATH = "templates";

    public static final String PLEASE_SELECT = "Please select";
    public static final String PLEASE_SELECT_OR_TYPE = "Please select or type";
    public static final String SELECT_OTHER = "Select Other";

    public static Map<String, String> initMapCache(String resourceName) {
        Map<String, String> retval = new LinkedHashMap<String, String>();
        try {
            File templateFile = new File(TemplateUtilities.getUserTemplateDir(), resourceName);
            if (templateFile.exists()) {
                retval.put(PLEASE_SELECT, PLEASE_SELECT);
                BufferedReader in = new BufferedReader(new FileReader(templateFile));
                String line;
                while ((line = in.readLine()) != null) {
                    String[] tokens = line.split("\t");
                    if (tokens.length == 2) {
                        retval.put(tokens[0], tokens[1]);
                    }
                }
                retval.put(SELECT_OTHER, SELECT_OTHER);
                in.close();
            } else {
                throw new ConverterException("Template file not found: " + resourceName);
            }
        } catch (IOException e) {
            throw new ConverterException("Error reading template:" + resourceName, e);
        }
        return retval;
    }

    public static void initTemplates() {

        //make directories if they don't already exist
        File templateDir = getUserTemplateDir();
        for (TemplateType type : TemplateType.values()) {
            File subdir = new File(templateDir, type.getTemplatePath());
            if (!subdir.exists()) {
                if (!subdir.mkdir()) {
                    throw new ConverterException("Could not create template directory: " + subdir.getAbsolutePath());
                }
            }
        }

    }

    public static File getUserTemplateDir() {

        //look for the templates file in the directory where the program is started
        File templateDir = new File(".", BASE_TEMPLATE_PATH);
        if (!templateDir.exists()) {
            logger.warn("Template directory doesn't exist, creating in " + templateDir.getAbsolutePath());
            if (!templateDir.mkdir()) {
                throw new ConverterException("Could not create template directory: " + templateDir.getAbsolutePath());
            }
        }
        return templateDir;
    }

    public static void writeTemplate(File template, ReportObject templateObject) {
        try {
            ReportMarshaller marshaller = ReportMarshallerFactory.getInstance().initializeMarshaller();
            marshaller.marshall(templateObject, new FileWriter(template));
        } catch (IOException e) {
            throw new ConverterException("Error while writing template", e);
        }
    }

    public static ReportObject loadTemplate(File template, Class clz) {
        try {
            StringBuilder sb = new StringBuilder();
            String oneLine;
            BufferedReader in = new BufferedReader(new FileReader(template));
            while ((oneLine = in.readLine()) != null) {
                sb.append(oneLine);
            }
            in.close();
            ReportUnmarshaller unmarshaller = ReportUnmarshallerFactory.getInstance().initializeUnmarshaller();
            return unmarshaller.unmarshal(sb.toString(), clz);
        } catch (IOException e) {
            throw new ConverterException("Error while reading template", e);
        }
    }
}
