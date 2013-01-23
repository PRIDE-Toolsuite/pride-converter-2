package uk.ac.lifesci.dundee.tools.converter.maxquant;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.report.model.Identification;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.UserParam;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 23/01/13
 * Time: 11:11
 */
public class MaxquantParser {

    private static final Logger logger = Logger.getLogger(MaxquantParser.class);

    //parameter keys constants
    public static final String VERSION_PARAM = "Version";
    public static final String DATABASE_PARAM = "Database";
    public static final String VARIABLE_MOD_PARAM = "Variable modifications";
    public static final String FIXED_MOD_PARAM = "Fixed modifications";
    public static final String SEPARATOR = "/";

    //parameters parsed from file
    private Map<String, String> parameters = new HashMap<String, String>();
    //keys of parameters of interest for processing method
    private static final Set<String> parameterKeys = new HashSet<String>();

    static {
        parameterKeys.add("Peptide FDR");
        parameterKeys.add("Protein FDR");
        parameterKeys.add("Site FDR");
        parameterKeys.add("Min. peptide Length");
        parameterKeys.add("Min. unique peptides");
        parameterKeys.add("Min. peptides");
        parameterKeys.add("Reverse string");
        parameterKeys.add("Contaminant string");
    }

    //keep track of identifications
    private LinkedHashMap<String, Identification> identifications = new LinkedHashMap<String, Identification>();

    //keep track of MsMsID to Scan #
    //the key will be the MS Id and the value will be the scan number
    private LinkedHashMap<String, Integer> msIdtoScanNumber = new LinkedHashMap<String, Integer>();

    /**
     * _peptides_file = DelimitedFileValidator([
     * ['Sequence'],
     * ['Mass'],
     * ['Unique', 'Unique (Groups)'],
     * ['Protein Group IDs'],
     * ['Contaminant'],
     * ['PEP'],
     * ['Reverse']
     * ])
     * <p/>
     * _mod_peptides_file = DelimitedFileValidator([
     * ['Peptide ID'],
     * ['Raw File'],
     * ['Elution Time', 'Retention Time'],
     * ['Calibrated Elution Time', 'Calibrated Retention Time'],
     * ['Score', 'PTM Score'],
     * ['Delta Score', 'Delta score', 'PTM Delta'],
     * ['Contaminant'],
     * ['PEP'],
     * ['Reverse']
     * ])
     * <p/>
     * _evidence_file = DelimitedFileValidator([
     * ['Gel Slice', 'Fraction'],
     * ['Type'],
     * ['Charge'],
     * ['Uncalibrated - Calibrated m/z [ppm]'],
     * ['Uncalibrated Mass Error [ppm]'],
     * ['MS/MS Scan Number'],
     * ['Mod. Peptide ID'],
     * ['Contaminant'],
     * ['PEP'],
     * ['Reverse']
     * ]).include(_mod_peptides_file)
     * <p/>
     * <p/>
     */

    private String maxquantFilePath;

    public MaxquantParser(String maxquantFilePath) {
        this.maxquantFilePath = maxquantFilePath;
        parseParameterFile();
        parseProteinGroupFile();
        parseMsMsFile();
    }

    private void parseParameterFile() {

        File paramFile = new File(new File(maxquantFilePath), "parameters.txt");
        if (!paramFile.exists()) {
            throw new ConverterException("Maxquant parameters.txt file not found in directory: " + maxquantFilePath);
        }

        try {
            //read all params
            BufferedReader in = new BufferedReader(new FileReader(paramFile));
            //skip header row
            String oneLine = in.readLine();
            while ((oneLine = in.readLine()) != null) {
                processParameterLine(oneLine);
            }
        } catch (Exception e) {
            throw new ConverterException("Error parsing parameters.txt file");
        }

    }

    private void processParameterLine(String line) {

        if (line != null && line.trim().length() > 0) {
            String[] tokens = line.split("\\t");
            if (tokens.length != 2) {
                logger.warn("Ignoring invalid parameter line: " + line);
            }
            String key = tokens[0];
            String value = tokens[1];
            parameters.put(key, value);
        }

    }

    private void parseProteinGroupFile() {
        File proteinGroupFile = new File(new File(maxquantFilePath), "proteinGroups.txt");
        if (!proteinGroupFile.exists()) {
            throw new ConverterException("Maxquant proteinGroups.txt file not found in directory: " + maxquantFilePath);
        }

        try {
            //read all params
            BufferedReader in = new BufferedReader(new FileReader(proteinGroupFile));
            //skip header row
            String oneLine = in.readLine();
            while ((oneLine = in.readLine()) != null) {
                processProteinGroupLine(oneLine);
            }
        } catch (Exception e) {
            throw new ConverterException("Error parsing parameters.txt file");
        }
    }

    private void processProteinGroupLine(String line) {

        Identification identification = new Identification();
        identification.setUniqueIdentifier();
        identification.setAccession();
        identification.setDatabase(getSearchDatabaseName());
        identification.setScore();
        identification.setSequenceCoverage();
        identification.setDatabaseVersion(getSearchDatabaseVersion());

    }

    public Param getProcessingMethod() {
        Param param = new Param();
        for (String processingKey : parameterKeys) {
            if (parameters.containsKey(processingKey)) {
                param.getUserParam().add(new UserParam(processingKey, parameters.get(processingKey)));
            }
        }
        return param;
    }

    public String getSearchDatabaseVersion() {
        return "Unknown";
    }

    public String getSearchDatabaseName() {
        String dbName = parameters.get(DATABASE_PARAM);
        if (dbName == null) {
            dbName = "Unknown";
        }
        return dbName;
    }

    public String getVersion() {
        String version = parameters.get(VERSION_PARAM);
        if (version == null) {
            version = "unknown";
        }
        return version;
    }

    public Collection<PTM> getPTMs() {
        //andromeda lookups
        return Collections.emptyList();
    }

    public int getSpectrumReferenceForPeptideUID(String peptideUID) {
        String msID = peptideUID.substring(peptideUID.indexOf(SEPARATOR) + 1);
        return msIdtoScanNumber.get(msID);
    }

    public Identification getIdentificationByUID(String identificationUID) {
        return identifications.get(identificationUID);
    }

    public Iterator<Identification> getIdentificationIterator(boolean prescanMode) {
        return identifications.values().iterator();
    }

}
