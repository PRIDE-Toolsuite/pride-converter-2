package uk.ac.ebi.pride.tools.converter.dao_crux_txt.parsers;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.dao_crux_txt.model.CruxProtein;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;

import java.io.*;
import java.util.*;

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
     * headers as the appear in the file
     */
    private static final String PROTEIN_ID_HEADER = "protein id";
    private static final String SCAN_HEADER = "scan";
    private static final String CHARGE_HEADER = "charge";
    private static final String SEQUENCE_HEADER = "sequence";

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
    public static CruxIdentificationsParserResult parse(File identificationsFile) throws ConverterException {
        if (identificationsFile == null)
            throw new ConverterException("Input identifications file was not set.");

        CruxIdentificationsParserResult res = new CruxIdentificationsParserResult();
        res.proteins = new LinkedHashMap<String, CruxProtein>();
        res.identifiedSpecIds = new LinkedList<Integer>();
        res.fileIndex = new ArrayList<String>();
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

                // we may have several proteins in the protein_id field, comma separated
                String[] proteinIds = fields[res.header.get(PROTEIN_ID_HEADER)].split(",");

                // for each protein accession (key), create an entry and add the peptide string to the peptide list (value)
                for (String accession: proteinIds) {
                    // if the protein doesn't exist add it for the first time
                    if (!res.proteins.containsKey(accession))
                        res.proteins.put(accession, new CruxProtein(accession));

                    // finally associate the peptide string index
                    res.fileIndex.add(res.peptideCount, line); // add the line to the index
                    res.proteins.get(accession).addPeptide(res.peptideCount); // reference the line from the protein

                }

                // Add the identified spectra if not done already
                int scan = Integer.parseInt(fields[res.header.get(SCAN_HEADER)]);
                if ( !( (res.identifiedSpecIds.size() > 0) && (res.identifiedSpecIds.get(res.identifiedSpecIds.size() - 1) == scan) ) ) {
                    res.identifiedSpecIds.add(scan);
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
        if ( !header.containsKey(SCAN_HEADER) || !header.containsKey(CHARGE_HEADER) || !header.containsKey(SEQUENCE_HEADER) )
            throw new ConverterException("Header file not present or mandatory fields are missing");

        return header;
    }



}
