package uk.ac.lifesci.dundee.tools.converter.maxquant;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.report.model.*;
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


/*
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
        parsePeptideFile();
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
            ProteinGroupFieldMapper mapper = new ProteinGroupFieldMapper(oneLine);
            while ((oneLine = in.readLine()) != null) {
                processProteinGroupLine(oneLine, mapper);
            }
        } catch (Exception e) {
            throw new ConverterException("Error parsing parameters.txt file");
        }
    }

    private void processProteinGroupLine(String line, ProteinGroupFieldMapper mapper) {

        try {
            String[] tokens = line.split("\\t");
            Identification identification = new Identification();
            identification.setUniqueIdentifier(tokens[mapper.getProteinIdColumn()]);
            identification.setAccession(tokens[mapper.getUniprotColumn()]);
            //check to see if there are more than 1 uniprot accesions
            int ndx = identification.getAccession().indexOf(";");
            if (ndx > 0) {
                String uniprotAc = identification.getAccession();
                identification.setCuratedAccession(uniprotAc.substring(0, ndx));
            }
            identification.setDatabase(getSearchDatabaseName());
            identification.setScore(Double.valueOf(tokens[mapper.getPepScoreColumn()]));
            identification.setSequenceCoverage(Double.valueOf(mapper.getSequenceCoverageColumn()));
            identification.setDatabaseVersion(getSearchDatabaseVersion());
            Param param = new Param();
            param.getCvParam().add(new CvParam("PRIDE", "PRIDE:0000063", "Protein description line", tokens[mapper.getProteinDescriptionColumn()]));
            identification.setAdditional(param);
            identifications.put(identification.getUniqueIdentifier(), identification);
        } catch (Exception e) {
            throw new ConverterException("Improperly formatted proteinGroup line: " + line);
        }

    }

    private void parsePeptideFile() {
        File peptideFile = new File(new File(maxquantFilePath), "peptides.txt");
        if (!peptideFile.exists()) {
            throw new ConverterException("Maxquant peptides.txt file not found in directory: " + maxquantFilePath);
        }

        try {
            //read all params
            BufferedReader in = new BufferedReader(new FileReader(peptideFile));
            //skip header row
            String oneLine = in.readLine();
            PeptideFieldMapper mapper = new PeptideFieldMapper(oneLine);
            while ((oneLine = in.readLine()) != null) {
                processPeptideLine(oneLine, mapper);
            }
        } catch (Exception e) {
            throw new ConverterException("Error parsing parameters.txt file");
        }

    }

    private void processPeptideLine(String line, PeptideFieldMapper mapper) {

        String[] tokens = line.split("\\t");

        String proteinGroupIds = tokens[mapper.getProteinGroupsColumn()];
        String[] proteinGroups = proteinGroupIds.split(";");
        for (String proteinGroup : proteinGroups) {

            Peptide peptide = new Peptide();
            peptide.setUniqueIdentifier(proteinGroup + SEPARATOR + tokens[mapper.getPeptideIdColumn()]);
            //will be done later
            //peptide.setSpectrumReference();
            //todo
            peptide.setStart(-1);
            //todo
            peptide.setEnd(-1);
            peptide.setIsSpecific("yes".equals(tokens[mapper.getUniqueColumn()]));
            peptide.setSequence(tokens[mapper.getSequenceColumn()]);
            peptide.setCuratedSequence(tokens[mapper.getSequenceColumn()]);
            Param param = new Param();
            param.getCvParam().add(new CvParam("MS", "MS:1001901", "MaxQuant:PEP", tokens[mapper.getPepScoreColumn()]));
            peptide.setAdditional(param);

            //store peptide
            Identification identification = identifications.get(proteinGroup);
            if (identification != null) {
                identification.getPeptide().add(peptide);
            } else {
                throw new ConverterException("Peptide assigned to unknown protein group. Peptide ID is: " + peptide.getUniqueIdentifier());
            }

        }

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
        //todo - andromeda lookups
        return Collections.emptyList();
    }

    public int getSpectrumReferenceForPeptideUID(String peptideUID) {
        String msID = peptideUID.substring(peptideUID.lastIndexOf(SEPARATOR) + 1);
        return msIdtoScanNumber.get(msID);
    }

    public Identification getIdentificationByUID(String identificationUID) {
        return identifications.get(identificationUID);
    }

    public Iterator<Identification> getIdentificationIterator(boolean prescanMode) {
        return identifications.values().iterator();
    }

}
