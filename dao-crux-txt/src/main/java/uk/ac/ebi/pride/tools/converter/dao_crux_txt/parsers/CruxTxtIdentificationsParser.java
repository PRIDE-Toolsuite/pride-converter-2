package uk.ac.ebi.pride.tools.converter.dao_crux_txt.parsers;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.model.CruxPeptide;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.model.CruxProtein;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.results.CruxParserResults;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Parses Crux TXT file.
 * @author Jose A. Dianes
 * @version $Id$
 */
public class CruxTxtIdentificationsParser {
    /**
     * Logger used by this class
     */
    private static final Logger logger = Logger.getLogger(CruxTxtIdentificationsParser.class);

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
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(targetFile)));
            String line;
            Map<String, Integer> header = null;

            // read the header
            if ((line = readLine(br)) != null) {
                header = parseHeader(line);
            }

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
    public static CruxParserResults parse(File identificationsFile) throws ConverterException {
        return parse(identificationsFile, "");
    }
    
    /**
     * Parses the whole file.
     * @param identificationsFile
     * @return A Map of proteins to peptides (1 to n) obtained from the file
     * @throws ConverterException
     */
    public static CruxParserResults parse(File identificationsFile, String prefix) throws ConverterException {
        if (identificationsFile == null)
            throw new ConverterException("Input identifications file was not set.");

        CruxParserResults res = new CruxParserResults();
        res.proteins = new LinkedHashMap<String, CruxProtein>();
        res.identifiedSpecIds = new LinkedList<Integer>();
        res.peptideCount = 0;

        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(identificationsFile)));
            String line;
            Map<String, Integer> header = null;

            // read the header (will throw exception if not found)
            if ( (line = readLine(br)) != null ) {
                header = parseHeader(line);
            }

            // read the identifications
            while ( (line = readLine(br)) != null ) {
                String[] fields = line.split("\t");

                // check the number of columns
                if ( fields.length !=  header.size() ) throw new ConverterException("Identification line doesn't match header columns");

                // process the peptide, it has to be associated with each protein
                CruxPeptide peptide = createCruxPeptide(fields, header);

                // we may have several proteins in the protein_id field, comma separated
                String[] proteinIds = fields[header.get("protein id")].split(",");

                // for each protein accession (key), create an entry and add the peptide to the peptide list (value)
                for (String accession: proteinIds) {
                    accession = prefix + accession;
                    // if the protein doesn't exist add it for the first time
                    if (!res.proteins.containsKey(accession))
                        res.proteins.put(accession, new CruxProtein(accession));

                    // finally associate the peptide
                    res.proteins.get(accession).addPeptide(peptide);

                }

            }

            return res;

        } catch (FileNotFoundException e) {
            logger.error("Failed to open input file: " + e.getMessage());
            throw new ConverterException("Could not find input file.", e);
        } catch (IOException e) {
            logger.error("Failed to read from input file: " + e.getMessage());
            throw new ConverterException("Failed to read from input file.", e);
        }

    }

    /**
     * Just adds blank line and comment skipping to BufferedReader.readLine call
     * @param br
     * @return the next line that is not a comment or an empty line
     */
    private static String readLine(BufferedReader br) throws IOException {
        String newLine = br.readLine();
        while ( newLine != null &&
                ((newLine.compareTo("") == 0) || newLine.startsWith("#")) ) {
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
        if ( !header.containsKey("scan") || !header.containsKey("charge") || !header.containsKey("sequence") )
            throw new ConverterException("Header file not present or mandatory fields are missing");

        return header;
    }

    /**
     * Creates a new CruxPeptide object from the passed
     * fields and header.
     * @param fields The fields of the line representing the peptide.
     * @param header A Map mapping a given column name to its 0-based index.
     * @return The CruxPeptide object representing the line.
     */
    private static CruxPeptide createCruxPeptide(String[] fields,
                                                 Map<String, Integer> header) {

        CruxPeptide peptide = new CruxPeptide(
                Integer.parseInt(fields[header.get("scan")]),
                Integer.parseInt(fields[header.get("charge")]),
                Double.parseDouble(fields[header.get("spectrum precursor m/z")]),
                Double.parseDouble(fields[header.get("spectrum neutral mass")]),
                Double.parseDouble(fields[header.get("peptide mass")]),
                Double.parseDouble(fields[header.get("delta_cn")]),
                Double.parseDouble(fields[header.get("xcorr score")]),
                Integer.parseInt(fields[header.get("xcorr rank")]),
                Integer.parseInt(fields[header.get("matches/spectrum")]),
                fields[header.get("sequence")],
                fields[header.get("cleavage type")]
        );

        return peptide;
    }

}
