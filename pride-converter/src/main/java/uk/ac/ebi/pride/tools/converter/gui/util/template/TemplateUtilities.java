package uk.ac.ebi.pride.tools.converter.gui.util.template;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.gui.util.IOUtilities;
import uk.ac.ebi.pride.tools.converter.gui.util.PreferenceManager;
import uk.ac.ebi.pride.tools.converter.report.io.xml.marshaller.ReportMarshaller;
import uk.ac.ebi.pride.tools.converter.report.io.xml.marshaller.ReportMarshallerFactory;
import uk.ac.ebi.pride.tools.converter.report.io.xml.unmarshaller.ReportUnmarshaller;
import uk.ac.ebi.pride.tools.converter.report.io.xml.unmarshaller.ReportUnmarshallerFactory;
import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
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
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final String BASE_TEMPLATE_PATH = PreferenceManager.PROGRAM_BASE_DIR + FILE_SEPARATOR + "template";

    public static final String PLEASE_SELECT = "Please select";
    public static final String PLEASE_SELECT_OR_TYPE = "Please select or type";
    public static final String SELECT_OTHER = "Select Other";

    public static Map<String, String> initMapCache(String resourceName) {
        Map<String, String> retval = new LinkedHashMap<String, String>();
        try {
            URL resource = TemplateUtilities.class.getResource(resourceName);
            if (resource != null) {
                retval.put(PLEASE_SELECT, PLEASE_SELECT);
                BufferedReader in = new BufferedReader(new InputStreamReader(resource.openStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    String[] tokens = line.split("\t");
                    if (tokens.length == 2) {
                        retval.put(tokens[0], tokens[1]);
                    }
                }
                retval.put(SELECT_OTHER, SELECT_OTHER);
                in.close();
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

        //copy default templates if not already there
        boolean loadDefaultTempaltes = new Boolean(PreferenceManager.getInstance().getProperty(PreferenceManager.PREFERENCE.LOAD_DEFAULT_TEMPLATES));
        if (loadDefaultTempaltes) {

            for (TemplateType type : TemplateType.values()) {

                try {
                    URL url = TemplateUtilities.class.getResource("/templates/" + type.getTemplatePath());
                    if (url != null) {
                        File file = new File(url.toURI());
                        File[] templates = file.listFiles();
                        if (templates != null) {
                            File subdir = new File(templateDir, type.getTemplatePath());
                            for (File templateFile : templates) {
                                File newTemplate = new File(subdir, templateFile.getName());
                                System.out.println("Init default template: " + newTemplate.getName());
                                IOUtilities.copyFile(templateFile, newTemplate);
                            }
                        }
                    }

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            //store preferences so that the default templates don't get written every time the conveter starts
            PreferenceManager.getInstance().setProperty(PreferenceManager.PREFERENCE.LOAD_DEFAULT_TEMPLATES, "false");
        }

    }

    public static File getUserTemplateDir() {

        String userHomeDir = System.getProperty("user.home");
        if (userHomeDir == null) {
            logger.error("User home dir not set, defaulting to temp");
            userHomeDir = System.getProperty("java.io.tmpdir");
        }

        File prideConvDir = new File(userHomeDir, PreferenceManager.PROGRAM_BASE_DIR);
        if (!prideConvDir.exists()) {
            if (!prideConvDir.mkdir()) {
                throw new ConverterException("Could not create template directory: " + prideConvDir.getAbsolutePath());
            }
        }

        File templateDir = new File(userHomeDir, BASE_TEMPLATE_PATH);
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
