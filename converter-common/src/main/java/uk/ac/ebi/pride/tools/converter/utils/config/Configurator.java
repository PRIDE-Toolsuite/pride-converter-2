package uk.ac.ebi.pride.tools.converter.utils.config;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class Configurator {

    private static Properties config = null;
    public static final String CONFIG_FILE = "converter_config.properties";

    private static final String OS_NAME_PROP = "os.name";
    private static final String OS_VERSION_PROP = "os.version";
    private static final String OS_ARCH_PROP = "os.arch";
    private static final String USER_HOME_PROP = "user.home";


    String nameOS = System.getProperty(OS_NAME_PROP);
    String versionOS = System.getProperty(OS_VERSION_PROP);
    String architectureOS = System.getProperty(OS_ARCH_PROP);
    String userHome = System.getProperty(USER_HOME_PROP);

    public enum PROPERTIES {
        VERSION("version"),
        HELPSET("help.main.set");

        private String value;

        PROPERTIES(String value) {
            this.value = value;
        }
    }

    private static Properties loadProperties() {

        try {

            URL props = Configurator.class.getClassLoader().getResource(CONFIG_FILE);

            if (props != null) {
                if (config == null) {

                    //load all properties
                    config = new Properties();
                    config.load(props.openStream());

                }
            } else {
                throw new IllegalStateException("Configurator could not load configuration file!");
            }
            return config;
        } catch (IOException e) {
            throw new IllegalStateException("Configurator could not load configuration file!: " + e.getMessage());
        }

    }

    public static String getProperty(PROPERTIES propName) {
        if (config == null) {
            loadProperties();
        }
        return config.getProperty(propName.value);
    }

    public static String getVersion() {
        return "PRIDE Converter Toolsuite " + getProperty(PROPERTIES.VERSION);
    }

    public static String getOSName() {
        return System.getProperty(OS_NAME_PROP);
    }

    public static String getOSVersion() {
        return System.getProperty(OS_VERSION_PROP);
    }

    public static String getOSArch() {
        return System.getProperty(OS_ARCH_PROP);
    }

    public static String getUserHome() {
        return System.getProperty(USER_HOME_PROP);
    }

}
