package uk.ac.ebi.pride.tools.converter.gui.util;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;

import java.io.*;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 27/02/12
 * Time: 17:01
 */
public class PreferenceManager {

    private static final Logger logger = Logger.getLogger(PreferenceManager.class);
    private static final String PREFERENCE_FILE = "userprefs.properties";
    public static final String PROGRAM_BASE_DIR = ".prideconverter";
    private static PreferenceManager instance = new PreferenceManager();
    private Properties preferences = new Properties();

    public enum PREFERENCE {
        IGNORE_MULTIPLE_FILE_EDITING("ignore.multiple.file.editing");

        String propName;

        PREFERENCE(String propName) {
            this.propName = propName;
        }

        public String getPropName() {
            return propName;
        }
    }

    private PreferenceManager() {

        String userHomeDir = System.getProperty("user.home");
        if (userHomeDir == null) {
            logger.error("User home dir not set, unable to read or store user preferences");
        } else {
            File userprefs = getPreferenceFile();
            if (!userprefs.exists()) {
                //if the pref file doesn't exist, write a skeleton one
                writePreferencesToFile();
            } else {
                try {
                    preferences.load(new FileReader(userprefs));
                } catch (IOException e) {
                    logger.fatal("Error reading property file", e);
                    throw new ConverterException("Could not reading user property file: " + e.getMessage(), e);
                }
            }
        }
    }

    private File getPreferenceFile() {
        String userHomeDir = System.getProperty("user.home");
        File prideConvDir = new File(userHomeDir, PROGRAM_BASE_DIR);
        if (!prideConvDir.exists()) {
            if (!prideConvDir.mkdir()) {
                throw new ConverterException("Could not create template directory: " + prideConvDir.getAbsolutePath());
            }
        }
        return new File(prideConvDir, PREFERENCE_FILE);
    }

    public void writePreferencesToFile() {

        try {
            PrintWriter out = new PrintWriter(new FileWriter(getPreferenceFile()));
            preferences.store(out, "Pride Converter user properties");
        } catch (IOException e) {
            logger.fatal("Error writing property file", e);
            throw new ConverterException("Could not writer user property file: " + e.getMessage(), e);
        }

    }

    public static PreferenceManager getInstance() {
        return instance;
    }

    public String getProperty(PREFERENCE propertyName) {
        return (String) preferences.get(propertyName.getPropName());
    }

    public void setProperty(PREFERENCE propertyName, String propertyValue) {
        preferences.setProperty(propertyName.getPropName(), propertyValue);
    }

}
