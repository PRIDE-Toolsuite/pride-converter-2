package uk.ac.ebi.pride.tools.converter.dao_spectrast_xls.parsers;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.dao.Utils;
import uk.ac.ebi.pride.tools.converter.dao_spectrast_xls.model.SpectraSTPeptide;
import uk.ac.ebi.pride.tools.converter.dao_spectrast_xls.model.SpectraSTProtein;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;

import java.io.*;
import java.util.*;

/**
 * Parses Crux TXT file.
 * @author Jose A. Dianes
 * @version $Id$
 */
public class SpectraSTXlsIdentificationsParser {
    /**
     * Logger used by this class
     */
    private static final Logger logger = Logger.getLogger(SpectraSTXlsIdentificationsParser.class);

    /**
     * Header names
     */
    public static final String QUERY_NAME_HEADER = "### Query";
    public static final String RANK_HEADER = "Rk";
    public static final String ID_HEADER = "ID";
    public static final String DOT_HEADER = "Dot";
    public static final String DELTA_HEADER = "Delta";
    public static final String DELTA_RANK_HEADER = "DelRk";
    public static final String DOT_BIAS_HEADER = "DBias";
    public static final String PRECURSOR_MZ_DIFF_HEADER = "MzDiff";
    public static final String NUM_CAND_HEADER = "#Cand";
    public static final String MEAN_DOT_HEADER = "MeanDot";
    public static final String SD_DOT_HEADER = "SDDot";
    public static final String FVAL_HEADER = "Fval";
    public static final String STATUS_HEADER = "Status";
    public static final String INST_HEADER = "Inst";
    public static final String SPECTRUM_TYPE_HEADER = "Spec";
    public static final String NUM_PROTEINS_HEADER = "#Pr";
    public static final String PROTEINS_HEADER = "Proteins";
    public static final String LIB_FILE_OFFSET_HEADER = "LibFileOffset";
    public static final String PROTEIN_SEPARATOR = ";";

    /**
     * Reader
     */
    private static BufferedReader br;

    /**
     * Parses just the header
     * @param targetFile
     * @return A map containing the header keys and their positions (column number)
     * @throws ConverterException
     */
    public static Map<String, Integer> parseHeader(File targetFile) throws ConverterException {

        if (targetFile == null)
            throw new ConverterException("Input target file was not set.");

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(targetFile)));
            String line;
            Map<String, Integer> header = null;

            // read the header
            if ((line = readLine(br)) != null) {
                header = parseHeader(line);
            }

            br.close();

            return header;

        } catch (FileNotFoundException e) {
            logger.error("Failed to open input file: " + e.getMessage());
            throw new ConverterException("Could not find input file.", e);
        } catch (IOException e) {
            logger.error("Failed to read from input file: " + e.getMessage());
            throw new ConverterException("Failed to read from input file.", e);
        }
    }
    
    /**
     * Parses the whole file.
     * @param identificationsFile
     * @return A Map of proteins to peptides (1 to n) obtained from the file
     * @throws ConverterException
     */
    public static SpectraSTIdentificationsParserResult parse(File identificationsFile) throws ConverterException {
        if (identificationsFile == null)
            throw new ConverterException("Input identifications file was not set.");

        SpectraSTIdentificationsParserResult res = new SpectraSTIdentificationsParserResult();
        res.proteins = new LinkedHashMap<String, SpectraSTProtein>();
        res.identifiedSpectraTitles = new LinkedList<String>();
        res.fileIndex = new ArrayList<String>();
        res.ptms = new HashMap<String, PTM>();
        res.peptideCount = 0;

        try {

            br = new BufferedReader(new InputStreamReader(new FileInputStream(identificationsFile)));
            String line;

            // read the header (will throw exception if not found)
            if ( (line = readLine(br)) != null ) {
                res.header = parseHeader(line);
            }

            // read the identifications
            while ( (line = readLine(br)) != null ) {
                String[] fields = line.split("\t");

                // check the number of columns
                if ( fields.length !=  res.header.size() ) throw new ConverterException("Identification line doesn't match header columns");

                // we may have several proteins in the PROTEINS_HEADER field, comma?? separated - todo: check protein accession separator
                String[] proteinIds = fields[res.header.get(PROTEINS_HEADER)].split(PROTEIN_SEPARATOR);

                addPTMs(fields[res.header.get(ID_HEADER)].split("/")[0], res.ptms);
                
                // for each protein accession (key), create an entry and add the peptide string to the peptide list (value)
                for (String accession: proteinIds) {
                    // if the protein doesn't exist add it for the first time
                    if (!res.proteins.containsKey(accession))
                        res.proteins.put(accession, new SpectraSTProtein(accession));

                    // finally associate the peptide string index
                    res.fileIndex.add(res.peptideCount, line); // add the line to the index
                    res.proteins.get(accession).addPeptide(res.peptideCount); // reference the line from the protein

                }

                // Add the identified spectra if not done already   TODO: CHECK THIS CONDITION CAREFULLY
                String scanTitle = fields[res.header.get(QUERY_NAME_HEADER)];
                if ( !( (res.identifiedSpectraTitles.size() > 0) && (scanTitle.equals(res.identifiedSpectraTitles.get(res.identifiedSpectraTitles.size() - 1))) ) ) {
                    res.identifiedSpectraTitles.add(scanTitle);
                }

                res.peptideCount++;

            }

            br.close();
            return res;

        } catch (FileNotFoundException e) {
            logger.error("Failed to open input file: " + e.getMessage());
            throw new ConverterException("Could not find input file.", e);
        } catch (IOException e) {
            logger.error("Failed to read from input file: " + e.getMessage());
            throw new ConverterException("Failed to read from input file.", e);
        }

    }

    private static void addPTMs(String sequence, Map<String, PTM> allPtms) {
        // get all PTMs associated with the sequence
        Map<Integer, String> mods = Utils.getModifications(sequence);

        // Put them in the main PTMs list
        for (Map.Entry<Integer, String> mod: mods.entrySet()) {
            String[] modDelta = mod.getValue().split("[\\[\\]]");
            if (!allPtms.containsKey(modDelta[1]+"@"+modDelta[0])) {
                PTM ptm = new PTM();
                ptm.setFixedModification(false);
                ptm.setResidues(modDelta[0]);
                ptm.setSearchEnginePTMLabel(modDelta[1]+"@"+modDelta[0]);
                ptm.getModMonoDelta().add(modDelta[1]);
                allPtms.put(ptm.getSearchEnginePTMLabel(), ptm);
            }
        }

        
    }

    /**
     * Just adds blank line skipping to BufferedReader.readLine call
     * @param br
     * @return the next line that is not a comment or an empty line
     */
    private static String readLine(BufferedReader br) throws IOException {
        String newLine = br.readLine();
        while ( newLine != null &&
                (newLine.compareTo("") == 0) ) {
            newLine = br.readLine();
        }
        return newLine;
    }

    /**
     * This is the private method where the real header parsing is done. Will throw a ConverterException if the header
     * doesn't contain certain "mandatory" fields.
     * @param line
     * @return A map representing the header keys mapped to their column numbers
     */
    private static Map<String, Integer> parseHeader(String line) {
        String[] fields = line.split("\t");

        Map<String, Integer> header = new HashMap<String, Integer>();
        for (int i = 0; i < fields.length; i++)
            header.put(fields[i], i);

        // Check for "mandatory" column names
        if ( !header.containsKey(QUERY_NAME_HEADER) || !header.containsKey(ID_HEADER) )
            throw new ConverterException("Header file not present or mandatory fields are missing");

        return header;
    }


    /**
     * Creates a new SpectraSTPeptide object from the passed
     * fields and header.
     * @param fields The fields of the line representing the peptide.
     * @param header A Map mapping a given column name to its 0-based index.
     * @return The SpectraSTPeptide object representing the line.
     */
    public static SpectraSTPeptide createSpectraSTPeptide(String[] fields,
                                                          Map<String, Integer> header) {
        // we may have several proteins in the protein_id field, semicolon-separated
        String[] proteinIds = fields[header.get(SpectraSTXlsIdentificationsParser.PROTEINS_HEADER)].split(";");

        String [] parsed = fields[header.get(ID_HEADER)].split("/");
        String sequence = parsed[0];
        int charge = Integer.parseInt(parsed[1]);
        int delRk = Integer.parseInt(fields[header.get(DELTA_RANK_HEADER)].substring(
                1,
                fields[header.get(DELTA_RANK_HEADER)].length() - 1)
        );
        SpectraSTPeptide peptide = new SpectraSTPeptide(
                fields[header.get(QUERY_NAME_HEADER)],
                Integer.parseInt(fields[header.get(RANK_HEADER)]),
                sequence,
                charge,
                Double.parseDouble(fields[header.get(DOT_HEADER)]),
                Double.parseDouble(fields[header.get(DELTA_HEADER)]),
                delRk,
                Double.parseDouble(fields[header.get(DOT_BIAS_HEADER)]),
                Double.parseDouble(fields[header.get(PRECURSOR_MZ_DIFF_HEADER)]),
                Integer.parseInt(fields[header.get(NUM_CAND_HEADER)]),
                Double.parseDouble(fields[header.get(MEAN_DOT_HEADER)]),
                Double.parseDouble(fields[header.get(SD_DOT_HEADER)]),
                Double.parseDouble(fields[header.get(FVAL_HEADER)]),
                fields[header.get(STATUS_HEADER)],
                fields[header.get(INST_HEADER)],
                fields[header.get(SPECTRUM_TYPE_HEADER)],
                Integer.parseInt(fields[header.get(NUM_PROTEINS_HEADER)]),
                Integer.parseInt(fields[header.get(LIB_FILE_OFFSET_HEADER)])
        );

        return peptide;
    }


}
