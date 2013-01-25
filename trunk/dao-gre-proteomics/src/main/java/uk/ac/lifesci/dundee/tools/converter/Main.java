package uk.ac.lifesci.dundee.tools.converter;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import uk.ac.ebi.pride.tools.converter.report.model.Identification;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;

/**
 * Ignore - just a test class for debugging
 * @author vackarafzal
 *
 */
public class Main {

	public static void main(String[] args) throws InvalidFormatException {

    	String root = "/Users/vackarafzal/Documents/AptanaStudioWorkspaceJava/dao-gre-proteomics/src/tmp/";
    	String spectraFile = root+"example.mzXML";
    	String propertiesFile = root+"test.properties";
    	String maxquantDSFolder= root+"maxquant_ds";
        
    	GrePrideConverterDAO dao = new GrePrideConverterDAO(new File(spectraFile));
        Properties props = new Properties();
        props.put(GrePrideConverterDAO.CONFIGURATION_FILE_PROP, propertiesFile);
        props.put(GrePrideConverterDAO.MAXQUANT_FILES_PROP, maxquantDSFolder);
        dao.setConfiguration(props);
        
        Iterator<Identification> iter = dao.getIdentificationIterator(true);
        int count = 0;
        while (iter.hasNext()){
        	Identification id = iter.next();
        	count+=id.getPeptide().size();
        }
        System.out.println(count);
        
    }
}
