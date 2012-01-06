package uk.ac.ebi.pride.tools.converter.gui.util;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;

import java.io.File;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 21/10/11
 * Time: 13:34
 * To change this template use File | Settings | File Templates.
 */
public class IOUtilities {

    private static final Logger logger = Logger.getLogger(IOUtilities.class);

    public static String getFileNameWithoutExtension(File file) {
        String retval = null;
        if (file != null) {
            if (file.getName().lastIndexOf(".") > 0) {
                retval = file.getName().substring(0, file.getName().lastIndexOf("."));
            }
        }
        return retval;
    }

    public static boolean renameFile(String fromFile, String toFile) {

        try {

            File destinationFile = new File(toFile);
            File sourceFile = new File(fromFile);

            //delete the destination file, if it exists
            deleteFiles(toFile);

            if (sourceFile.renameTo(destinationFile)) {
                logger.info("Successfully renamed temporary file to final path: " + toFile);
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            throw new ConverterException("Error renaming file", e);
        }

    }

    private static void deleteFiles(String file) {

        logger.warn("Deleting file: " + file);

        File fileToDelete = new File(file);
        //old file exists - sanity check
        if (fileToDelete.exists()) {

            try {
                //in windows systems, there might be lock contention issues that need resolving while
                //the system clears up stale file handles
                int nbTries = 5;
                while (nbTries > 0 && !fileToDelete.delete()) {
                    System.gc();
                    Thread.sleep(2000);
                    nbTries--;
                }
                if (nbTries == 0) {
                    throw new ConverterException("Could not delete file: " + file);
                }
            } catch (InterruptedException e) {
                /* no op */
            }
        }

    }

    public static void deleteFiles(Set<String> filesToDelete) {
        for (String file : filesToDelete) {
            deleteFiles(file);
        }
    }
}
