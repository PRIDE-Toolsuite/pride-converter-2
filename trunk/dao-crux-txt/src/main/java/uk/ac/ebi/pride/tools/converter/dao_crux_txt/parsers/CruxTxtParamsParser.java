package uk.ac.ebi.pride.tools.converter.dao_crux_txt.parsers;

import uk.ac.ebi.pride.tools.converter.utils.ConverterException;

import java.io.*;
import java.util.Properties;

/**
 * Parses a Crux txt parameter file consisting of comments or parameter assignments (and also blank lines if desired)
 * @author Jose A. Dianes
 * @version $Id$
 */
public class CruxTxtParamsParser {

    /**
     * The main parsing method
     * @param file
     * @return Return a Properties data structure build up from the file
     * @throws ConverterException
     */
    public static Properties parse(File file) throws ConverterException {

        if (file == null)
            throw new ConverterException("Input properties file was not set.");

        try {
            BufferedReader br = new BufferedReader( new InputStreamReader( new FileInputStream( file ) ) );
            Properties properties = new Properties();
            String line;
            // scan the file looking for properties
            while ( (line = br.readLine()) != null ) { // while not eof
                if (!line.startsWith("#") && line.length()>0 ) { // if not a comment, its a property
                    String[] property =  line.split("=");
                    properties.setProperty(property[0],property[1]);
                }
            }

            return properties;

        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }
}
