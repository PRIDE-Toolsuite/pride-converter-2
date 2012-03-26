package uk.ac.ebi.pride.tools.converter.dao_spectrast_txt.parsers;

import uk.ac.ebi.pride.tools.converter.report.model.PTM;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses a Crux txt parameter file consisting of comments or parameter assignments (and also blank lines if desired)
 *
 * About building PTMs from the parameter file:
 * The PTM object should at least contain the SearchEnginePTMLabel, whether they are fixed or variable modifications,
 * the deltas and the Residues. The SearchEnginePTMLabel is used by the DAO to identify the given modification in this
 * function and when reporting modifications for peptides. It is by the converter framework to merge user annotated
 * information about the modification with the one reported by the DAO. The SearchEnginePTMLabel will not be written
 * to the final PRIDE XML file.  The Residues string specifies the amino acids as single-letter code on which the given
 * modification was observed (f.e. "CM" for cysteine and methionine). The N-terminus should be reported as "0" and the
 * C-terminus as "1".
 *
 * About variable modifications: they are expressed in the parameter file as
 * <mass change>:<aa list>:<max per peptide>:<prevents cleavage>:<prevents cross-link>
 * It seems that not all of them are mandatory. We assume just the two first ones to be mandatory
 *
 * @author Jose A. Dianes
 * @version $Id$
 */
public class CruxTxtParamsParser {

    /**
     * General expressions
     */
    private static final String doubleRegex = "-?\\d+\\.\\d+";
    private static final String aminoacidRegex = "[A-Z]";
    private static final String terminalModification = doubleRegex+":(-1|\\d+)";
    
    /**
     * fixed modifications regex
     */
    private static final String fixedPTMRegex = "^"+aminoacidRegex+"="+doubleRegex;
    private static Pattern fixedPTMPattern = Pattern.compile(fixedPTMRegex);

    /**
     * variable modifications regex
     */
    private static final String variablePTMRegex = "^mod="+doubleRegex+":"+aminoacidRegex+"+:\\d+";
    private static Pattern variablePTMPattern = Pattern.compile(variablePTMRegex);

    /**
     * nterminal variable modifications regex
     */
    private static final String nterminalPTMRegex = "^nmod="+terminalModification;
    private static Pattern nterminalPTMPattern = Pattern.compile(nterminalPTMRegex);

    /**
     * cterminal variable modifications regex
     */
    private static final String cterminalPTMRegex = "^cmod="+terminalModification;
    private static Pattern cterminalPTMPattern = Pattern.compile(cterminalPTMRegex);

    /**
     * cterminal fixed modifications regex
     */
    private static final String cterminalFixedPTMRegex = "^cmod-fixed="+terminalModification;
    private static Pattern cterminalFixedPTMPattern = Pattern.compile(cterminalFixedPTMRegex);

    /**
     * nterminal fixed modifications regex
     */
    private static final String nterminalFixedPTMRegex = "^nmod-fixed="+terminalModification;
    private static Pattern nterminalFixedPTMPattern = Pattern.compile(nterminalFixedPTMRegex);

    /**
     * fragment-mass
     */
    private static final String fragmentMassRegex = "fragment-mass=[average|mono]";
    private static Pattern fragmentMassPattern = Pattern.compile(fragmentMassRegex);

    /**
     * The main parsing method
     * @param file
     * @return Returns a Parameters data structure build up from the file
     * @throws ConverterException
     */
    public static CruxParametersParserResult parse(File file) throws ConverterException {

        if (file == null)
            throw new ConverterException("Input properties file was not set.");
        try {
            BufferedReader br = new BufferedReader( new InputStreamReader( new FileInputStream( file ) ) );
            CruxParametersParserResult result = new CruxParametersParserResult();
            result.aaToPtm = new HashMap<String, PTM>();
            result.aaToFixedPtm = new HashMap<String, PTM>();
            result.ptms = new LinkedList<PTM>();   // Since we are not accessing this by index and we don't know the size 
            result.properties = new Properties();  // in advance, it makes no sense to use ArraysLists for PTMs (need resizing)            

            String line;
            // scan the file looking for properties
            while ( (line = br.readLine()) != null ) { // while not eof
                if (!line.startsWith("#") && line.length()>0 ) { // if not a comment, its a parameter

                    String[] parameter =  line.split("=");

                    // Check if its a fixed modification
                    // <aa>=<mass>
                    Matcher m = fixedPTMPattern.matcher(line);
                    if (m.matches() && ((Double.parseDouble(parameter[1]) > 0) || (Double.parseDouble(parameter[1]) < 0)) ) {
                        // Create the PTM
                        // Set fixed, searchEngineLabel = aminoacid, residues = <aminoacid>, monodelta=mass
                        PTM newPtm = new PTM();
                        newPtm.setFixedModification(true);
                        newPtm.setResidues(parameter[0]);
                        newPtm.setSearchEnginePTMLabel(parameter[0]);
                        newPtm.getModMonoDelta().add(parameter[1]);
                        result.aaToFixedPtm.put(parameter[0],newPtm);
                        result.ptms.add(newPtm);
                        continue;
                    }

                    // Check if its a variable modification
                    m = variablePTMPattern.matcher(line);
                    if (m.find()) {
                        // Create the PTM
                        // <mass change>:<aa list>:<max per peptide>:<prevents cleavage>:<prevents cross-link>
                        // We just use mass change and aa list. Check that max per peptide is not 0
                        String[] modParams = parameter[1].split(":");
                        // Check max per peptide is not 0
                        if (modParams[2].equals("0")) throw new ConverterException("Variable modification specified as fixed in: "+line);
                        // Create the PTM
                        PTM newPtm = new PTM();
                        newPtm.setFixedModification(false);
                        newPtm.setResidues(modParams[1]);
                        newPtm.setSearchEnginePTMLabel(modParams[0]+"@"+modParams[1]);
                        newPtm.getModMonoDelta().add(modParams[0]);
                        // Add the PTM to the amino-PTM map and to the PTMs list
                        String[] tempAss = modParams[1].split("");
                        String[] aas = Arrays.copyOfRange(tempAss, 1, tempAss.length);
                        for (String aa: aas) {
                            result.aaToPtm.put(aa+"["+modParams[0]+"]",newPtm);
                        }
                        result.ptms.add(newPtm);
                        continue;
                    }

                    // Check if its a nterminal variable modification
                    // nterminal modifications has "0" as a residue value
                    m = nterminalPTMPattern.matcher(line);
                    if (m.find()) {
                        // Create the PTM
                        // <mass change>:<max distance from protein n-term (-1 for no max)>
                        String[] modParams = parameter[1].split(":");
                        PTM newPtm = new PTM();
                        newPtm.setFixedModification(false);
                        newPtm.setResidues("0");
                        newPtm.setSearchEnginePTMLabel(modParams[0] + "@nterm");
                        if (result.ntermPTM != null) throw new ConverterException("Duplicated nterm modification found");
                        else result.ntermPTM = newPtm;
                        result.ptms.add(newPtm);
                        continue;
                    }

                    // Check if its a cterminal variable modification
                    // cterminal modifications has "1" as a residue value
                    m = cterminalPTMPattern.matcher(line);
                    if (m.find()) {
                        // Create the PTM
                        // <mass change>:<max distance from protein c-term (-1 for no max)>
                        String[] modParams = parameter[1].split(":");
                        PTM newPtm = new PTM();
                        newPtm.setResidues("1");
                        newPtm.setFixedModification(false);
                        newPtm.setSearchEnginePTMLabel(modParams[0] + "@cterm");
                        if (result.ctermPTM != null) throw new ConverterException("Duplicated cterm modification found");
                        else result.ctermPTM = newPtm;
                        result.ptms.add(newPtm);
                        continue;
                    }

                    // Check if its a nterminal fixed modification
                    // nterminal modifications has "0" as a residue value
                    // nterm-fixed=<mass>
                    m = nterminalFixedPTMPattern.matcher(line);
                    if (m.find()) {
                        // Create the PTM
                        String[] modParams = parameter[1].split(":");
                        PTM newPtm = new PTM();
                        newPtm.setResidues("0");
                        newPtm.setFixedModification(true);
                        newPtm.setSearchEnginePTMLabel(modParams[0]+"@nterm-fixed");
                        if (result.ntermFixedPTM != null) throw new ConverterException("Duplicated nterm fixed modification found");
                        else result.ntermFixedPTM = newPtm;
                        result.ptms.add(newPtm);
                        continue;
                    }

                    // Check if its a cterminal fixed modification
                    // cterminal modifications has "1" as a residue value
                    // cterm-fixed=<mass>
                    m = cterminalFixedPTMPattern.matcher(line);
                    if (m.find()) {
                        // Create the PTM
                        String[] modParams = parameter[1].split(":");
                        PTM newPtm = new PTM();
                        newPtm.setResidues("1");
                        newPtm.setFixedModification(true);
                        newPtm.setSearchEnginePTMLabel(modParams[0]+"@cterm-fixed");
                        if (result.ctermFixedPTM != null) throw new ConverterException("Duplicated cterm fixed modification found");
                        else result.ctermFixedPTM = newPtm;
                        result.ptms.add(newPtm);
                        continue;
                    }



                    // check if its fragment-mass=average|mono (average not supported)
                    m = fragmentMassPattern.matcher(line);
                    if (m.find()) {
                        // check it is not average
                        if ("average".equals(parameter[1])) {
                            throw new ConverterException("fragment-mass=average not supported");
                        }
                    }
                    
                    // Then is a "regular" parameter
                    result.properties.setProperty(parameter[0],parameter[1]);

                }
            }

            br.close();

            return result;

        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }
}
