package uk.ac.ebi.pride.tools.cellzome;

import uk.ac.ebi.pride.jmztab.MzTabFile;
import uk.ac.ebi.pride.jmztab.MzTabParsingException;
import uk.ac.ebi.pride.jmztab.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 05/10/11
 * Time: 20:05
 */
public class MzTabReader {

    private static Map<String, Double[]> proteinAbundance = new HashMap<String, Double[]>();
    private static Map<String, String[]> sampleDescriptions = new HashMap<String, String[]>();

    private static String accession = "23399";
    private static String inputTabFile = "/home/rcote/Desktop/cellzome/23399_001.dat-mztab-rc.txt";
    private static String quantFile = "/home/rcote/Desktop/cellzome/23399.txt";
    private static boolean itraq = false;
    private static int subSampleNb = 6;
    private static boolean itraq_odd = false;

    public static void main(String[] args) {

        if (args.length > 0) {
            accession = args[0];
            inputTabFile = args[1];
            quantFile = args[2];
            if (args.length > 4) {
                if ("itraq".equals(args[3])) {
                    itraq = true;
                    subSampleNb = 2;
                }
                if ("odd".equals(args[4])) {
                    itraq_odd = true;
                } else {
                    itraq_odd = false;
                }
            }
        }

        try {
            File inputFile = new File(inputTabFile);

            // by creating the MzTabFile object the mzTab file
            // is automatically parsed.
            MzTabFile mzTabFile = new MzTabFile(inputFile);
            //need to create new tab file to store modified objects
            //todo - why???
            MzTabFile outTabFile = new MzTabFile();

            //add sample metadata
            if (mzTabFile.getUnitIds().size() > 1) {
                throw new IllegalStateException("More than 1 unit defined in mztab file");
            }
            String unitID = mzTabFile.getUnitIds().iterator().next();
            Unit unit = mzTabFile.getUnitMetadata(unitID);

            //create subsamples
            Param[] params = new Param[subSampleNb];
            if (!itraq) {
                params[0] = new Param("PRIDE", "PRIDE:0000285", "TMT reagent 126", null);
                params[1] = new Param("PRIDE", "PRIDE:0000286", "TMT reagent 127", null);
                params[2] = new Param("PRIDE", "PRIDE:0000287", "TMT reagent 128", null);
                params[3] = new Param("PRIDE", "PRIDE:0000288", "TMT reagent 129", null);
                params[4] = new Param("PRIDE", "PRIDE:0000289", "TMT reagent 130", null);
                params[5] = new Param("PRIDE", "PRIDE:0000290", "TMT reagent 131", null);
            } else {
                if (itraq_odd) {
                    params[0] = new Param("PRIDE", "PRIDE:0000115", "iTRAQ reagent 115", null);
                    params[1] = new Param("PRIDE", "PRIDE:0000117", "iTRAQ reagent 117", null);
                } else {
                    params[0] = new Param("PRIDE", "PRIDE:0000114", "iTRAQ reagent 114", null);
                    params[1] = new Param("PRIDE", "PRIDE:0000116", "iTRAQ reagent 116", null);
                }
            }

            Collection<Subsample> ss = new ArrayList<Subsample>();
            for (int i = 0; i < subSampleNb; i++) {
                Subsample s = new Subsample(unit.getUnitId(), i + 1); //+1 because ssamples start from 1
                s.setQuantificationReagent(params[i]);
                s.setDescription(getDescription(accession, i));
                ss.add(s);
            }
            unit.setSubsamples(ss);
            if (!itraq) {
                unit.setQuantificationMethod(new Param("PRIDE", "PRIDE:0000314", "TMT", null));
            } else {
                unit.setQuantificationMethod(new Param("PRIDE", "PRIDE:0000313", "iTRAQ", null));
            }
            unit.setProteinQuantificationUnit(new Param("PRIDE", "PRIDE:0000394", "Absolute quantification unit", null));
            outTabFile.setUnit(unit);

            //add quantitation values
            Collection<Protein> proteins = mzTabFile.getProteins();
            int noQuant = 0;
            for (Protein prot : proteins) {
                String ac = prot.getAccession();
                int ndx = ac.indexOf(".");
                if (ndx > 0) {
                    ac = ac.substring(0, ndx);
                }
                Double[] abundance = getProteinAbundance(ac);
                if (abundance != null) {
                    for (int i = 0; i < subSampleNb; i++) {
                        prot.setAbundance(i + 1, abundance[i], null, null); //+1 because ssamples start from 1
                    }
                } else {
                    System.out.println("No protein abundance found for " + ac);
                    noQuant++;
                }
                //todo - need to do this because otherwise quant data created but not exported to file
                //todo - this is badness
                outTabFile.addProtein(prot);
            }

            Collection<Peptide> peptides = mzTabFile.getPeptides();
            for (Peptide pep : peptides) {
                outTabFile.addPeptide(pep);
            }

            //write mztab file
            PrintWriter out = new PrintWriter(new FileWriter(inputFile.getAbsolutePath() + "-out"));
            out.println(outTabFile.toMzTab());
            out.close();

            System.out.println("proteins.size() = " + proteins.size());
            System.out.println("noQuant = " + noQuant);

            //test to see if file is valid
            MzTabFile testFile = new MzTabFile(new File(inputFile.getAbsolutePath() + "-out"));
            System.out.println("testFile.getProteins().size() = " + testFile.getProteins().size());

        } catch (MzTabParsingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getDescription(String accession, int i) {
        initSampleDescriptions();
        if (sampleDescriptions.get(accession) != null) {
            return sampleDescriptions.get(accession)[i];
        } else {
            throw new IllegalStateException("No Sample Description for Experiment " + accession);
        }
    }

    private static void initSampleDescriptions() {

        if (!sampleDescriptions.isEmpty()) return;

        //read experimentDescription.txt file
/*
#expname	label no	label reagent	sample desciption
19703	1	TMT6-126	Mouse kidney - CZC00054252 (3.0 uM) - la-S7
19703	2	TMT6-127	Mouse kidney - CZC00054252 (0.75 uM) - la-S7
19703	3	TMT6-128	Mouse kidney - CZC00054252 (0.188 uM) - la-S7
19703	4	TMT6-129	Mouse kidney - CZC00054252 (0.047 uM) - la-S7
19703	5	TMT6-130	Mouse kidney - CZC00054252 (0.012 uM) - la-S7
19703	6	TMT6-131	Mouse kidney - la-S7
 */
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader("experimentdetails.txt"));
            String line;
            while ((line = in.readLine()) != null) {

                if (line.startsWith("#")) continue;

                String[] tokens = line.split("\t");
                String ac = tokens[0];
                String ndx = tokens[1];
                String description = tokens[3];

                String[] descriptionArray = sampleDescriptions.get(ac);
                if (descriptionArray == null) {
                    descriptionArray = new String[6];
                }
                descriptionArray[Integer.parseInt(ndx) - 1] = description;

                sampleDescriptions.put(ac, descriptionArray);

            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static Double[] getProteinAbundance(String accession) {
        initProteinAbundance();
        return proteinAbundance.get(accession);
    }

    private static void initProteinAbundance() {

        if (!proteinAbundance.isEmpty()) return;

        //read corresponding cellzome file to genreate protein abundance arrays
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(quantFile));

            //read header
            String line = in.readLine();

            if (!itraq) {
                //experiment code	IPI acc. no.	Protein NAME	Mascot Score	SSM	UPM	SSM used for quantification	126	127	128	129	130	131
                while ((line = in.readLine()) != null) {
                    String[] tokens = line.split("\t");
                    if (tokens.length < 13) {
                        System.out.println("improperly formatted line: " + line);
                        continue;
                    }
                    String ac = tokens[1];
                    Double[] values = new Double[6];
                    values[0] = makeDouble(tokens[7]);
                    values[1] = makeDouble(tokens[8]);
                    values[2] = makeDouble(tokens[9]);
                    values[3] = makeDouble(tokens[10]);
                    values[4] = makeDouble(tokens[11]);
                    values[5] = makeDouble(tokens[12]);
                    proteinAbundance.put(ac, values);
                }
            } else {
                //experiment code	IPI acc. no.	Protein NAME	Mascot Score	SSM	UPM	SSM used for quantification	114	115	116	117
                while ((line = in.readLine()) != null) {
                    String[] tokens = line.split("\t");
                    if (tokens.length < 11) {
                        System.out.println("improperly formatted line: " + line);
                        continue;
                    }
                    String ac = tokens[1];
                    Double[] values = new Double[2];
                    if (itraq_odd) {
                        values[0] = makeDouble(tokens[8]);
                        values[1] = makeDouble(tokens[10]);
                    } else {
                        values[0] = makeDouble(tokens[7]);
                        values[1] = makeDouble(tokens[9]);
                    }
                    proteinAbundance.put(ac, values);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                /* no op*/
            }
        }
    }

    private static Double makeDouble(String token) {
        if (token != null && !"".equals(token.trim())) {
            return Double.valueOf(token);
        } else {
            return null;
        }

    }

}
