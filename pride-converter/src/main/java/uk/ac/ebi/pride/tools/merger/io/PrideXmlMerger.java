package uk.ac.ebi.pride.tools.merger.io;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.jaxb.model.*;
import uk.ac.ebi.pride.jaxb.xml.PrideXmlReader;
import uk.ac.ebi.pride.jaxb.xml.marshaller.PrideXmlMarshaller;
import uk.ac.ebi.pride.jaxb.xml.marshaller.PrideXmlMarshallerFactory;
import uk.ac.ebi.pride.tools.converter.gui.NavigationPanel;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.memory.MemoryUsage;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 18/03/11
 * Time: 16:28
 */
public class PrideXmlMerger {

    private static final Logger logger = Logger.getLogger(PrideXmlMerger.class);

    private List<String> filesToMerge;
    private Map<String, Map<String, Integer>> localRefsPerFile = new LinkedHashMap<String, Map<String, Integer>>();
    private int currentSpectrumId = 1;
    private String outputFilePath;
    private PrideXmlMarshaller prideJaxbMarshaller;
    private boolean gzipCompress = false;
    private boolean updateGUI = false;

    public PrideXmlMerger(List<String> filesToMerge, String outputFilePath, boolean gzipCompress, boolean updateGUI) {

        this.filesToMerge = filesToMerge;
        this.gzipCompress = gzipCompress;
        this.outputFilePath = outputFilePath;
        this.updateGUI = updateGUI;

        prideJaxbMarshaller = PrideXmlMarshallerFactory.getInstance().initializeMarshaller();

        //sanity check on compression and filename suffix
        if (gzipCompress) {
            //check to see if outputFilePath ends in .gz if gzip turned on
            if (!outputFilePath.endsWith(".gz")) {
                //update file name
                this.outputFilePath += ".gz";
            }
        } else {
            //check to see if outputFilePath ends if .gz with gzip turned off
            if (outputFilePath.toLowerCase().endsWith(".gz")) {
                //automatically turn on compression
                this.gzipCompress = true;
            }
        }

        //prescan ids
        for (String file : filesToMerge) {
            //for each file, remap spectrumIDs to new values
            localRefsPerFile.put(file, scanRef(file));
        }

    }

    private Map<String, Integer> scanRef(String file) {

        logger.info("Prescanning " + file);
        PrideXmlReader reader = new PrideXmlReader(new File(file));
        Map<String, Integer> retval = new LinkedHashMap<String, Integer>();
        //iterate over all spectrumIds in the file, assigning them a new one
        List<String> spectrumIds = reader.getSpectrumIds();
        for (String id : spectrumIds) {
            Integer oldId = retval.put(id, currentSpectrumId++);
            if (oldId != null) {
                throw new ConverterException("Spectrum ID " + id + " seen more than once in file: " + file);
            }
        }
        return retval;

    }

    /**
     * Main logic method. If gzipCompress is set to true, the resulting file will be gzipped. Note that if
     * gzipCompress is set to true, the outputFilePath will automatically be appended with ".gz" if not already set.
     * Also, if the outputFilePath ends in ".gz", gzipCompress is automatically set to true.
     *
     * @throws ConverterException - on error
     */
    public String mergeXml() throws ConverterException {

        PrintWriter out = null;
        try {

            //create xml file
            File xmlFile = new File(outputFilePath);

            if (gzipCompress) {
                FileOutputStream fos = new FileOutputStream(xmlFile);
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fos);
                OutputStreamWriter outWriter = new OutputStreamWriter(gzipOutputStream);
                out = new PrintWriter(outWriter);
            } else {
                out = new PrintWriter(new FileWriter(xmlFile));
            }

            logger.warn("Writing file : " + xmlFile.getAbsolutePath());
            if (updateGUI) {
                NavigationPanel.getInstance().setWorkingMessage("Writing master file: " + xmlFile.getAbsolutePath());
            }

            //use metadata from first file in list
            String masterFile = filesToMerge.get(0);
            logger.warn("Using metadata from master file: " + masterFile);
            PrideXmlReader reader = new PrideXmlReader(new File(masterFile));

            //write header
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

            //experiment
            out.println("<ExperimentCollection version=\"2.1\">");
            out.println("<Experiment>");
            String ac = (reader.getExpAccession() == null) ? "" : reader.getExpAccession();
            out.println("<ExperimentAccession>" + ac + "</ExperimentAccession>");
            out.println("<Title>" + reader.getExpTitle() + "</Title>");

            marshallPrideObject(out, reader.getReferences());
            out.println("<ShortLabel>" + reader.getExpShortLabel() + "</ShortLabel>");

            //protocol
            marshallPrideObject(out, reader.getProtocol());
            out.println();

            //mzdata
            out.println("<mzData version=\"1.05\" accessionNumber=\"" + ac + "\">");
            marshallPrideObject(out, reader.getCvLookups());
            marshallPrideObject(out, reader.getDescription());

            //spectrumlist - use currentSpectrumId - 1 (first spectrum is 1)
            out.println("<spectrumList count=\"" + (currentSpectrumId - 1) + "\">");

            //merge spectra for all files
            int spectrumCount = 0;
            for (String file : localRefsPerFile.keySet()) {
                //get reader
                logger.info("Merging spectra from file: " + file);
                if (updateGUI) {
                    NavigationPanel.getInstance().setWorkingMessage("Merging spectra from file: " + file);
                }
                Map<String, Integer> idMappings = localRefsPerFile.get(file);
                PrideXmlReader localReader = new PrideXmlReader(new File(file));
                for (String oldSpectrumId : idMappings.keySet()) {
                    //update spectrum ID
                    Spectrum sp = localReader.getSpectrumById(oldSpectrumId);
                    sp.setId(idMappings.get(oldSpectrumId));
                    //update precursor
                    if (sp.getSpectrumDesc() != null && sp.getSpectrumDesc().getPrecursorList() != null) {
                        for (Precursor prec : sp.getSpectrumDesc().getPrecursorList().getPrecursor()) {

                            Integer mappedId = idMappings.get("" + prec.getSpectrum().getId());
                            if (mappedId == null) {
                                logger.warn("Referenced Spectrum ID " + prec.getSpectrum().getId() + " not provided in MzData SpectrumList. Set to -1!");
                                mappedId = -1;
                            }

                            //update spectrum reference
                            Spectrum precSp = new Spectrum();
                            precSp.setId(mappedId);
                            prec.setSpectrum(sp);
                            if (logger.isDebugEnabled()) {
                                logger.debug("replaced spectrum ID " + prec.getSpectrum().getId() + " with " + mappedId);
                            }
                        }
                    }

                    marshallPrideObject(out, sp);
                    spectrumCount++;
                    if (spectrumCount % 1000 == 0) {
                        if (logger.isInfoEnabled()) {
                            logger.info("Marshalled " + spectrumCount + " spectra");
                            logger.info(MemoryUsage.getMessage());
                        }
                    }
                }
            }

            //close spectrumlist
            out.println("</spectrumList>");

            //close mzdata
            out.println("</mzData>");

            //identifications
            //merge spectra for all files
            int identCount = 0;
            for (String file : localRefsPerFile.keySet()) {
                //get reader
                logger.info("Merging identifications from file: " + file);
                if (updateGUI) {
                    NavigationPanel.getInstance().setWorkingMessage("Merging identifications from file: " + file);
                }
                Map<String, Integer> idMappings = localRefsPerFile.get(file);
                PrideXmlReader localReader = new PrideXmlReader(new File(file));
                for (String identId : localReader.getIdentIds()) {

                    Identification ident = localReader.getIdentById(identId);

                    //check for PMF
                    if (ident.getSpectrum() != null) {

                        Integer mappedId = idMappings.get("" + ident.getSpectrum().getId());
                        if (mappedId != null) {

                            Spectrum sp = new Spectrum();
                            //update spectrum reference
                            sp.setId(mappedId);
                            ident.setSpectrum(sp);
                            if (logger.isDebugEnabled()) {
                                logger.debug("replaced spectrum ID " + ident.getSpectrum().getId() + " with " + mappedId);
                            }

                        } else {
                            logger.warn("Referenced Spectrum ID " + ident.getSpectrum().getId() + " not provided in MzData SpectrumList. Ignored!");
                            ident.setSpectrum(null);
                        }

                    }

                    //update peptides
                    for (PeptideItem p : ident.getPeptideItem()) {
                        Spectrum oldSpectrum = p.getSpectrum();
                        if (oldSpectrum != null) {

                            Integer mappedId = idMappings.get("" + oldSpectrum.getId());
                            if (mappedId != null) {

                                //update spectrum ref
                                Spectrum sp = new Spectrum();
                                sp.setId(idMappings.get("" + oldSpectrum.getId()));
                                p.setSpectrum(sp);
                                if (logger.isDebugEnabled()) {
                                    logger.debug("replaced spectrum ID " + oldSpectrum.getId() + " with " + mappedId);
                                }

                            } else {
                                logger.warn("Referenced Spectrum ID " + oldSpectrum.getId() + " not provided in MzData SpectrumList. Ignored!");
                                p.setSpectrum(null);
                            }

                        }
                    }

                    marshallPrideObject(out, ident);
                    identCount++;
                    if (identCount % 1000 == 0) {
                        //log memory usage
                        if (logger.isInfoEnabled()) {
                            logger.info("Marshalled " + identCount + " identifications");
                            logger.info(MemoryUsage.getMessage());
                        }
                    }
                }
            }

            //close experiment
            marshallPrideObject(out, reader.getAdditionalParams());
            out.println("</Experiment>");

            //close experimentcollection
            out.println("</ExperimentCollection>");

            return xmlFile.getAbsolutePath();

        } catch (IOException e) {

            logger.error("Error writing outputfile: " + e.getMessage(), e);
            throw new ConverterException("Error writing output file: " + e.getMessage(), e);

        } finally {
            if (out != null) {
                out.close();
            }
        }

    }

    /**
     * helper method to marshall out a collection of PRIDE JAXB model objects
     *
     * @param out
     * @param xmlObject
     * @param <T>
     * @throws IOException
     */
    private <T extends PrideXmlObject> void marshallPrideObject(PrintWriter out, List<T> xmlObject) throws IOException {
        if (xmlObject == null) {
            throw new ConverterException("Attempting to marshall out null list");
        }

        //iterate and marshall individual objects
        for (T obj : xmlObject) {
            marshallPrideObject(out, obj);
        }
        out.println();

    }

    /**
     * helper method to marshall out a single PRIDE JAXB model objects
     *
     * @param out
     * @param xmlObject
     * @param <T>
     * @throws IOException
     */
    private <T extends PrideXmlObject> void marshallPrideObject(PrintWriter out, T xmlObject) throws IOException {

        if (xmlObject == null) {
            throw new ConverterException("Attempting to marshall out null object");
        }

        try {
            prideJaxbMarshaller.marshall(xmlObject, out);
            out.println();
        } catch (Exception e) {
            throw new IOException("Error marshalling: " + xmlObject.getClass().getName(), e);
        }

    }

}
