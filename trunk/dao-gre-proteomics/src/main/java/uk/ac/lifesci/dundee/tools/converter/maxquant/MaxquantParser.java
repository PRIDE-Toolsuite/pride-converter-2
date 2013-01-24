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
    public static final String COLUMN_DELIM = "\\t";
    public static final String UNMODIFIED_PEPTIDE = "Unmodified";

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

    //keep track of peptideID uniqueness
    private LinkedHashMap<String, String> peptideUniqueness = new LinkedHashMap<String, String>();

    //keep track of proteinGroup sequence coverage
    private LinkedHashMap<String, String> proteinGroupSequenceCoverage = new LinkedHashMap<String, String>();

    //keep track of proteinGroup PEP score
    private LinkedHashMap<String, String> proteinGroupPEPScore = new LinkedHashMap<String, String>();

    private String maxquantFilePath;

    public MaxquantParser(String maxquantFilePath) {
        this.maxquantFilePath = maxquantFilePath;
        logger.warn("Parsing parameter file");
        parseParameterFile();
        logger.warn("Parsing proteingroup file");
        parseProteinGroupFile();
        logger.warn("Parsing peptide file");
        parsePeptideFile();
        logger.warn("Parsing evidence file");
        parseEvidenceFile();
        logger.warn("Parsing ms/ms file");
        parseMsMsFile();
    }

    private void parseParameterFile() {

        File paramFile = new File(new File(maxquantFilePath), "parameters.txt");
        if (!paramFile.exists()) {
            throw new ConverterException("Maxquant parameters.txt file not found in directory: " + maxquantFilePath);
        }

        int lineCount = 1;
        try {
            //read all params
            BufferedReader in = new BufferedReader(new FileReader(paramFile));
            //skip header row
            String oneLine = in.readLine();
            while ((oneLine = in.readLine()) != null) {
                processParameterLine(oneLine);
                lineCount++;
            }
        } catch (Exception e) {
            throw new ConverterException("Error parsing parameters.txt file at line " + lineCount, e);
        }

    }

    //store key/values for the parameters
    private void processParameterLine(String line) {

        if (line != null && line.trim().length() > 0) {
            String[] tokens = line.split(COLUMN_DELIM);
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

        int lineCount = 1;
        try {
            //read all params
            BufferedReader in = new BufferedReader(new FileReader(proteinGroupFile));
            //skip header row
            String oneLine = in.readLine();
            ProteinGroupFieldMapper mapper = new ProteinGroupFieldMapper(oneLine);
            while ((oneLine = in.readLine()) != null) {
                processProteinGroupLine(oneLine, mapper);
                lineCount++;
            }
        } catch (Exception e) {
            throw new ConverterException("Error parsing proteinGroups.txt file at line " + lineCount, e);
        }
    }

    //create identifications
    private void processProteinGroupLine(String line, ProteinGroupFieldMapper mapper) {

        try {

            String[] tokens = line.split(COLUMN_DELIM);
            String peptideGroupId = tokens[mapper.getProteinIdColumn()];
            String pepScore = tokens[mapper.getPepScoreColumn()];
            String coverage = tokens[mapper.getSequenceCoverageColumn()];
            proteinGroupPEPScore.put(peptideGroupId, pepScore);
            proteinGroupSequenceCoverage.put(peptideGroupId, coverage);

        } catch (Exception e) {
            throw new ConverterException("Improperly formatted proteinGroup line: " + line, e);
        }

    }

    private void parsePeptideFile() {
        File peptideFile = new File(new File(maxquantFilePath), "peptides.txt");
        if (!peptideFile.exists()) {
            throw new ConverterException("Maxquant peptides.txt file not found in directory: " + maxquantFilePath);
        }

        int lineCount = 1;
        try {
            //read all params
            BufferedReader in = new BufferedReader(new FileReader(peptideFile));
            //skip header row
            String oneLine = in.readLine();
            PeptideFieldMapper mapper = new PeptideFieldMapper(oneLine);
            while ((oneLine = in.readLine()) != null) {
                processPeptideLine(oneLine, mapper);
                lineCount++;
            }
        } catch (Exception e) {
            throw new ConverterException("Error parsing peptide.txt file at line " + lineCount, e);
        }

    }

    //update exisiting identifications and create peptides for them
    private void processPeptideLine(String line, PeptideFieldMapper mapper) {

        String[] tokens = line.split(COLUMN_DELIM);

        String peptideId = tokens[mapper.getPeptideIdColumn()];
        String unique = tokens[mapper.getUniqueColumn()];
        peptideUniqueness.put(peptideId, unique);

    }


    private void parseEvidenceFile() {
        File evidence = new File(new File(maxquantFilePath), "evidence.txt");
        if (!evidence.exists()) {
            throw new ConverterException("Maxquant evidence.txt file not found in directory: " + maxquantFilePath);
        }

        int lineCount = 1;
        try {
            //read all params
            BufferedReader in = new BufferedReader(new FileReader(evidence));
            //skip header row
            String oneLine = in.readLine();
            EvidenceFieldMapper mapper = new EvidenceFieldMapper(oneLine);
            while ((oneLine = in.readLine()) != null) {
                processEvidenceLine(oneLine, mapper);
                lineCount++;
            }
        } catch (Exception e) {
            throw new ConverterException("Error parsing evidence.txt file at line " + lineCount, e);
        }
    }

    //the evidence file will provide the msmsID as well as all of the PTM information
    private void processEvidenceLine(String line, EvidenceFieldMapper mapper) {

        String[] tokens = line.split(COLUMN_DELIM);

        String proteinGroupIds = tokens[mapper.getProteinGroupIdColumn()];
        String peptideId = tokens[mapper.getPeptideIdColumn()];
        String msmsIds = tokens[mapper.getMsIDColumn()];
        String modifications = tokens[mapper.getModificationsColumn()];
        String modifiedSequence = tokens[mapper.getModifiedSequenceColumn()];
        String sequence = tokens[mapper.getSequenceColumn()];

        //check sample raw file
        boolean ok = true;
        //todo logic goes here
        if (!ok) {
            return;
        }


        String[] proteinGroups = proteinGroupIds.split(";");
        for (String proteinGroupId : proteinGroups) {

            //find or create protein group
            Identification identification = identifications.get(proteinGroupId);
            if (identification == null) {

                //create identification
                identification = new Identification();
                identification.setUniqueIdentifier(proteinGroupId);
                identification.setAccession(tokens[mapper.getUniprotColumn()]);
                //check to see if there are more than 1 uniprot accesions
                int ndx = identification.getAccession().indexOf(";");
                if (ndx > 0) {
                    String uniprotAc = identification.getAccession();
                    identification.setCuratedAccession(uniprotAc.substring(0, ndx));
                }
                identification.setDatabase(getSearchDatabaseName());
                identification.setScore(Double.valueOf(proteinGroupPEPScore.get(proteinGroupId)));
                identification.setSequenceCoverage(Double.valueOf(proteinGroupSequenceCoverage.get(proteinGroupId)));
                identification.setDatabaseVersion(getSearchDatabaseVersion());
                Param param = new Param();
                param.getCvParam().add(new CvParam("PRIDE", "PRIDE:0000063", "Protein description line", tokens[mapper.getProteinDescriptionColumn()]));
                identification.setAdditional(param);
                identifications.put(identification.getUniqueIdentifier(), identification);

            }

            String[] msIDs = msmsIds.split(";");
            for (String msId : msIDs) {

                Peptide peptide = new Peptide();
                peptide.setUniqueIdentifier(makePeptideUID(proteinGroupId, peptideId, msId));
                //will be done later
                //peptide.setSpectrumReference();
                //todo
                peptide.setStart(-1);
                //todo
                peptide.setEnd(-1);
                peptide.setIsSpecific("yes".equals(peptideUniqueness.get(peptideId)));
                peptide.setSequence(sequence);
                peptide.setCuratedSequence(modifiedSequence);
                Param param = new Param();
                param.getCvParam().add(new CvParam("MS", "MS:1001901", "MaxQuant:PEP", tokens[mapper.getPepScoreColumn()]));
                peptide.setAdditional(param);

                //now we need to update the peptide with the msms scan ID
                //as well as any modifications
                if (!UNMODIFIED_PEPTIDE.equals(modifications)) {
                    //there are modifications
                    peptide.getPTM().addAll(createModifications(modifications, modifiedSequence));
                    //set modified sequence, remove __ characters at either end
                    peptide.setSequence(modifiedSequence.replace('_', ' ').trim());
                }

                identification.getPeptide().add(peptide);
            }

        }

    }

    private Collection<? extends PeptidePTM> createModifications(String modifications, String modifiedSequence) {
        //parse the modifications to create the PTMs
        //parse the modified sequence to assign positions
        //modifications are abbreviated with (XX), where XX is the lowercase first 2 letters of the modification name
        //modificatino positions are 1-based from the N-terminus of the peptide. N-Term mods are 0. C-term mods are
        //length+1;
        //todo - THIS NEEDS TO BE DONE ONCE THE PTMs ARE PROPERLY PARSED FROM THE ANDROMEDA FILE
        return Collections.emptyList();
    }

    // MSMS SECTION
    private void parseMsMsFile() {
        File msmsFile = new File(new File(maxquantFilePath), "msms.txt");
        if (!msmsFile.exists()) {
            throw new ConverterException("Maxquant msms.txt file not found in directory: " + maxquantFilePath);
        }

        try {
            BufferedReader in = new BufferedReader(new FileReader(msmsFile));
            String[] headers = in.readLine().split(COLUMN_DELIM);
            int colIndexRowId = columnIndex(headers, new String[]{"id"});
            int colIndexScanNo = columnIndex(headers, new String[]{"Scan number"});

            String line;
            while ((line = in.readLine()) != null) {
                String[] rowSections = line.split(COLUMN_DELIM);
                String rowId = rowSections[colIndexRowId];
                int scanNo = Integer.parseInt(rowSections[colIndexScanNo]);
                msIdtoScanNumber.put(rowId, scanNo);
            }
        } catch (Exception e) {
            throw new ConverterException("Error parsing msms.txt file", e);
        }
    }

    private int columnIndex(String[] headers, String[] columnSearchNames) {
        for (int i = 0; i < headers.length; i++) {
            for (String name : columnSearchNames) {
                if (name.equalsIgnoreCase(headers[i]))
                    return i;
            }
        }
        return -1;
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

    //craete peptideUIDs based on what step of the parsing we're on
    private String makePeptideUID(String proteinGroup, String peptideId) {
        return new StringBuilder().append(proteinGroup).append(SEPARATOR).append(peptideId).toString();
    }

    private String makePeptideUID(String proteinGroupId, String peptideId, String msID) {
        return new StringBuilder().append(proteinGroupId).append(SEPARATOR).append(peptideId).append(SEPARATOR).append(msID).toString();
    }

}
