package uk.ac.ebi.pride.tools.converter.dao_omssa_txt.parsers;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.model.OmssaPTM;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.model.OmssaPeptide;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.model.OmssaProtein;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;

import java.io.*;
import java.util.*;

/**
 * Parses Omssa TXT file.
 * Note: Omssa text file reports multiple hits of one peptide identification and different proteins repeating the
 * identification line with a different DEFINE_HEADER value (the protein accession column)
 *
 * @author Jose A. Dianes
 * @version $Id$
 */
public class OmssaIdentificationsParser {
    /**
     * Logger used by this class
     */
    private static final Logger logger = Logger.getLogger(OmssaIdentificationsParser.class);

    /**
     * Header names
     */
    public static final String SPECTRUM_NUMBER_HEADER = "Spectrum number";
    public static final String FILENAME_ID_HEADER = "Filename/id";
    public static final String PEPTIDE_HEADER = "Peptide";
    public static final String E_VALUE_HEADER = "E-value";
    public static final String MASS_HEADER = "Mass";
    public static final String GI_HEADER = "gi";
    public static final String ACCESSION_HEADER = "Accession";
    public static final String START_HEADER = "Start";
    public static final String STOP_HEADER = "Stop";
    public static final String DEFLINE_HEADER = "Defline";
    public static final String MODS_HEADER = "Mods";
    public static final String CHARGE_HEADER = "Charge";
    public static final String THEO_MASS_HEADER = "Theo Mass";
    public static final String P_VALUE_HEADER = "P-value";
    public static final String NIST_SCORE_HEADER = "NIST score";


    private File identificationsFile;
    private Map<String, OmssaPTM> definedOmssaPTMs;
    private Map<String, PTM> fixedPtms;


    public OmssaIdentificationsParser(File identificationsFile, File modsFile, File usermodsFile, Map<Character, Double> fixedPtms) {
        if (identificationsFile == null)
            throw new ConverterException("Input identifications file was not set.");

        if (modsFile == null)
            throw new ConverterException("OMSSA ptm file was not set.");

        this.identificationsFile = identificationsFile;
        definedOmssaPTMs = new OmssaPTMFileParser(modsFile).getOmssaPTMs();
        if (usermodsFile != null) {
            definedOmssaPTMs.putAll(new OmssaPTMFileParser(usermodsFile).getOmssaPTMs());
        }

        this.fixedPtms = OmssaPTMFileParser.createFixedPTMs(fixedPtms);

    }

    /**
     * Parses the whole file.
     *
     * @return A Map of proteins to peptides (1 to n) obtained from the file
     * @throws ConverterException
     */
    public OmssaIdentificationsParserResult parse() throws ConverterException {

        OmssaIdentificationsParserResult res = new OmssaIdentificationsParserResult();
        res.setProteins(new LinkedHashMap<String, OmssaProtein>());
        res.setIdentifiedSpectraTitles(new LinkedList<Integer>());
        res.setFileIndex(new ArrayList<String[]>());
        res.setPtms(new HashMap<String, PTM>());
        res.setPeptideCount(0);

        try {

            CSVReader br = new CSVReader(new InputStreamReader(new FileInputStream(identificationsFile)));
            String[] line;

            // read the header (will throw exception if not found)
            if ((line = br.readNext()) != null) {
                res.setHeader(parseHeader(line));
            }

            // read the identifications
            // Omssa text file reports multiple hits of one peptide identification and different proteins repeating the
            // identification line with a different DEFINE_HEADER value (the protein accession column)
            while ((line = br.readNext()) != null) {

                // check the number of columns
                if (line.length != res.getHeader().size())
                    throw new ConverterException("Identification line doesn't match header columns");

                addPTMs(res.getPtms(), line[res.getHeader().get(MODS_HEADER)], line[res.getHeader().get(PEPTIDE_HEADER)]);

                // for the protein accession (key), create an entry and add the peptide string to the peptide list (value)
                // if the protein doesn't exist add it for the first time
                String accession = line[res.getHeader().get(DEFLINE_HEADER)];
                if (!res.getProteins().containsKey(accession)) {
                    res.getProteins().put(accession, new OmssaProtein(accession));
                }

                // finally associate the peptide string index
                res.getFileIndex().add(res.getPeptideCount(), line); // add the line to the index
                res.getProteins().get(accession).addPeptide(res.getPeptideCount()); // reference the line from the protein

                // Add the identified spectra if not done already
                int scanTitle = Integer.parseInt(line[res.getHeader().get(SPECTRUM_NUMBER_HEADER)]);
                if (!((res.getIdentifiedSpectraTitles().size() > 0) && (scanTitle == (res.getIdentifiedSpectraTitles().get(res.getIdentifiedSpectraTitles().size() - 1))))) {
                    res.getIdentifiedSpectraTitles().add(scanTitle);
                }

                //update peptide count
                res.setPeptideCount(res.getPeptideCount() + 1);

                //update result to include fixed PTMs
                res.getPtms().putAll(this.fixedPtms);

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

    private void addPTMs(Map<String, PTM> observedPTMs, String modString, String peptide) {

        //check for variable mods
        if (modString != null && !"".equals(modString.trim())) {

            //oxidation of M:1 ,oxidation of M:10
            //phosphorylation of Y:7

            String[] mods = modString.split(",");
            for (String modStr : mods) {

                String modName = modStr.substring(0, modStr.indexOf(":")).trim();
                String modPositionStr = modStr.substring(modStr.indexOf(":") + 1).trim();
                int modPosition = -1;
                if (!"".equals(modPositionStr)) {
                    modPosition = Integer.parseInt(modPositionStr);
                }

                //look to see if mod is defined
                OmssaPTM ptm = definedOmssaPTMs.get(modName);
                if (ptm != null) {
                    //create a new PTM and add it to the observed map if not already present
                    if (!observedPTMs.containsKey(modName)) {

                        //create PTM
                        PTM reportPTM = new PTM();
                        reportPTM.setFixedModification(false);
                        reportPTM.setSearchEnginePTMLabel(modName);
                        if (ptm.getPsiModName() != null) {
                            reportPTM.setModName(ptm.getPsiModName());
                        }
                        if (ptm.getUnimodAc() != null) {
                            reportPTM.getAdditional().getCvParam().add(new CvParam("Unimod", ptm.getUnimodAc(), modName, null));
                        }
                        if (ptm.getModMonoMass() != null) {
                            reportPTM.getModMonoDelta().add("" + ptm.getModMonoMass());
                        }

                        //add
                        observedPTMs.put(modName, reportPTM);

                    }

                    //update residue information for identified PTM
                    if (modPosition > 0 && peptide != null) {
                        PTM reportPTM = observedPTMs.get(modName);
                        reportPTM.setResidues(updatePTMResidues(reportPTM.getResidues(), "" + peptide.charAt(modPosition - 1)));
                    }
                }

            }

        }

    }

    private String updatePTMResidues(String residues, String c) {

        //alpha sort
        TreeSet<String> observedResidues = new TreeSet<String>();
        if (residues != null) {
            residues = residues.toUpperCase();
            for (int i = 0; i < residues.length(); i++) {
                observedResidues.add("" + residues.charAt(i));
            }
        }
        observedResidues.add(c.toUpperCase());

        //return string
        StringBuilder sb = new StringBuilder();
        for (String s : observedResidues) {
            sb.append(s);
        }

        return sb.toString().toUpperCase();

    }

    /**
     * This is the private method where the real header parsing is done. Will throw a ConverterException if the header
     * doesn't contain certain "mandatory" fields.
     *
     * @param line
     * @return A map representing the header keys mapped to their column numbers
     */
    private Map<String, Integer> parseHeader(String[] line) {

        Map<String, Integer> header = new HashMap<String, Integer>();
        for (int i = 0; i < line.length; i++)
            header.put(line[i].trim(), i);

        // Check for "mandatory" column names
        if (!header.containsKey(SPECTRUM_NUMBER_HEADER) || !header.containsKey(PEPTIDE_HEADER))
            throw new ConverterException("Header file not present or mandatory fields are missing");

        return header;
    }


    /**
     * Creates a new OmssaPeptide object from the passed
     * fields and header.
     *
     * @param fields The fields of the line representing the peptide.
     * @param header A Map mapping a given column name to its 0-based index.
     * @return The OmssaPeptidecr object representing the line.
     */
    public static OmssaPeptide createOmssaPeptide(String[] fields,
                                                  Map<String, Integer> header) {

        OmssaPeptide peptide = new OmssaPeptide(
                Integer.parseInt(fields[header.get(SPECTRUM_NUMBER_HEADER)]),
                fields[header.get(FILENAME_ID_HEADER)],
                fields[header.get(PEPTIDE_HEADER)],
                Double.parseDouble(fields[header.get(E_VALUE_HEADER)]),
                Double.parseDouble(fields[header.get(MASS_HEADER)]),
                Double.parseDouble(fields[header.get(GI_HEADER)]),
                fields[header.get(ACCESSION_HEADER)],
                Integer.parseInt(fields[header.get(START_HEADER)]),
                Integer.parseInt(fields[header.get(STOP_HEADER)]),
                fields[header.get(DEFLINE_HEADER)],
                fields[header.get(MODS_HEADER)],
                Integer.parseInt(fields[header.get(CHARGE_HEADER)]),
                Double.parseDouble(fields[header.get(THEO_MASS_HEADER)]),
                Double.parseDouble(fields[header.get(P_VALUE_HEADER)]),
                Double.parseDouble(fields[header.get(NIST_SCORE_HEADER)])
        );

        return peptide;

    }

}
