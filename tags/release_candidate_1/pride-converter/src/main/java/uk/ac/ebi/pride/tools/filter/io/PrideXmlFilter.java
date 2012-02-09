package uk.ac.ebi.pride.tools.filter.io;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.jaxb.model.CvParam;
import uk.ac.ebi.pride.jaxb.model.Identification;
import uk.ac.ebi.pride.jaxb.model.PrideXmlObject;
import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.jaxb.xml.PrideXmlReader;
import uk.ac.ebi.pride.jaxb.xml.marshaller.PrideXmlMarshaller;
import uk.ac.ebi.pride.jaxb.xml.marshaller.PrideXmlMarshallerFactory;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.FileUtils;
import uk.ac.ebi.pride.tools.converter.utils.memory.MemoryUsage;
import uk.ac.ebi.pride.tools.filter.model.FDRCalculator;
import uk.ac.ebi.pride.tools.filter.model.Filter;
import uk.ac.ebi.pride.tools.filter.model.UpdatingFilter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 11/03/11
 * Time: 15:51
 */
public class PrideXmlFilter {

    private static final Logger logger = Logger.getLogger(PrideXmlFilter.class);
    private String outputFilePath;
    private String inputFilePath;
    private PrideXmlMarshaller prideJaxbMarshaller;
    private boolean outputGzipCompressed = false;
    private boolean inputGzipCompressed = false;

    private List<Filter<Spectrum>> spectrumFilters = null;
    private List<Filter<Identification>> identificationFilters = null;
    private List<UpdatingFilter<Identification>> identificationUpdatingFilters = null;
    private boolean filterUnidentifiedSpectra = false;

    private boolean computeFDR = false;

    public PrideXmlFilter(String outputFilePath, String inputFilePath, boolean inputGzipCompressed, boolean outputGzipCompressed) {

        this.inputGzipCompressed = inputGzipCompressed;
        this.outputGzipCompressed = outputGzipCompressed;

        prideJaxbMarshaller = PrideXmlMarshallerFactory.getInstance().initializeMarshaller();
        identificationFilters = new ArrayList<Filter<Identification>>();
        spectrumFilters = new ArrayList<Filter<Spectrum>>();
        identificationUpdatingFilters = new ArrayList<UpdatingFilter<Identification>>();

        //sanity check on compression and filename suffix
        if (outputGzipCompressed) {
            //check to see if outputFilePath ends in .gz if gzip turned on
            if (!outputFilePath.endsWith(FileUtils.gz)) {
                //update file name
                outputFilePath += FileUtils.gz;
            }
        } else {
            //check to see if outputFilePath ends if .gz with gzip turned off
            if (outputFilePath.toLowerCase().endsWith(FileUtils.gz)) {
                //automatically turn on compression
                this.outputGzipCompressed = true;
            }
        }

        //sanity check on compression and filename suffix
        if (inputGzipCompressed) {
            //check to see if inputFilePath ends in .gz if gzip turned on
            if (!inputFilePath.endsWith(FileUtils.gz)) {
                //update file name
                this.inputGzipCompressed = false;
            }
        } else {
            //check to see if inputFilePath ends if .gz with gzip turned off
            if (inputFilePath.toLowerCase().endsWith(FileUtils.gz)) {
                //automatically turn on compression
                this.inputGzipCompressed = true;
            }
        }

        //sanity check to make sure we're not overwriting the file we're reading
        //check to see if the DAO is a ReportReaderDAO and make sure
        //that we're not trying to overwrite a file we're reading
        File outputFile = new File(outputFilePath);
        File inputFile = new File(inputFilePath);

        if (logger.isDebugEnabled()) {
            logger.debug("inputFile.getAbsolutePath() = " + inputFile.getAbsolutePath());
            logger.debug("outputFile.getAbsolutePath() = " + outputFile.getAbsolutePath());
        }

        if (outputFile.getAbsolutePath().equals(inputFile.getAbsolutePath())) {
            logger.info("Overwriting file detected. Will update output file name.");
            //write tmp output file and rename afterwards
            int ndx = outputFilePath.toLowerCase().indexOf(FileUtils.xml);
            if (ndx > 0) {
                outputFilePath = outputFilePath.substring(0, ndx) + "-filtered.xml";
                if (this.outputGzipCompressed) {
                    outputFilePath += FileUtils.gz;
                }
            }
        }

        //set paths for later use
        this.outputFilePath = outputFilePath;
        this.inputFilePath = inputFilePath;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public void registerIdentificationFilter(Filter<Identification> filter) {
        if (filter != null) {
            identificationFilters.add(filter);
        }
    }

    public void registerIdentificationUpdatingFilter(UpdatingFilter<Identification> filter) {
        if (filter != null) {
            identificationUpdatingFilters.add(filter);
        }
    }

    public void registerSpectrumFilter(Filter<Spectrum> filter) {
        if (filter != null) {
            spectrumFilters.add(filter);
        }
    }

    public boolean isFilterUnidentifiedSpectra() {
        return filterUnidentifiedSpectra;
    }

    public void setFilterUnidentifiedSpectra(boolean filterUnidentifiedSpectra) {
        this.filterUnidentifiedSpectra = filterUnidentifiedSpectra;
    }

    /**
     * Main logic method. If gzipCompress is set to true, the resulting file will be gzipped. Note that if
     * gzipCompress is set to true, the outputFilePath will automatically be appended with ".gz" if not already set.
     * Also, if the outputFilePath ends in ".gz", gzipCompress is automatically set to true.
     *
     * @throws uk.ac.ebi.pride.tools.converter.utils.ConverterException
     *
     */
    public void writeXml() throws ConverterException {

        PrintWriter out = null;
        try {

            //create xml file
            File xmlFile = new File(outputFilePath);

            if (outputGzipCompressed) {
                FileOutputStream fos = new FileOutputStream(xmlFile);
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fos);
                OutputStreamWriter outWriter = new OutputStreamWriter(gzipOutputStream);
                out = new PrintWriter(outWriter);
            } else {
                out = new PrintWriter(new FileWriter(xmlFile));
            }

            File inputFile;
            if (inputGzipCompressed) {
                inputFile = FileUtils.extractTempFile(new File(inputFilePath));
            } else {
                inputFile = new File(inputFilePath);
            }
            PrideXmlReader reader = new PrideXmlReader(inputFile);

            logger.warn("Writing file : " + xmlFile.getAbsolutePath());

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
            out.println();
            out.println("<description>");
            marshallPrideObject(out, reader.getAdmin());
            out.println();
            marshallPrideObject(out, reader.getInstrument());
            out.println();
            marshallPrideObject(out, reader.getDataProcessing());
            out.println();
            out.println("</description>");

            //spectrumlist - need to prescan all spectra before we actually write them
            List<String> filteredSpectrumIds = filterSpectra(reader);
            out.println("<spectrumList count=\"" + filteredSpectrumIds.size() + "\">");
            int spectrumCount = 0;
            for (String id : filteredSpectrumIds) {
                marshallPrideObject(out, reader.getSpectrumById(id));
                out.println();
                spectrumCount++;
                if (spectrumCount % 1000 == 0) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Marshalled " + spectrumCount + " spectra");
                        logger.info(MemoryUsage.getMessage());
                    }
                }
            }
            //close spectrumlist
            out.println("</spectrumList>");

            logger.warn("Total number of spectra: " + reader.getSpectrumIds().size());
            logger.warn("Filtered number of spectra: " + filteredSpectrumIds.size());

            //close mzdata
            out.println("</mzData>");

            //identifications
            int identCount = 0;
            int filteredIdentCount = 0;
            for (String id : reader.getIdentIds()) {
                identCount++;
                Identification ident = reader.getIdentById(id);
                //filterObject will return true if object passes all filters
                //if one filter returns false - i.e object to be removed - filterObject will return false
                if (filterObject(ident, identificationFilters)) {

                    ident = updateObject(ident, identificationUpdatingFilters);
                    if (ident != null) {
                        marshallPrideObject(out, ident);
                        filteredIdentCount++;
                        out.println();
                        if (identCount % 1000 == 0) {
                            //log memory usage
                            if (logger.isInfoEnabled()) {
                                logger.info("Marshalled " + identCount + " identifications");
                                logger.info(MemoryUsage.getMessage());
                            }
                        }
                    }
                }
            }

            logger.warn("Total number of identifications: " + identCount);
            logger.warn("Filtered number of identifications: " + filteredIdentCount);

            //close experiment
            marshallPrideObject(out, reader.getAdditionalParams());
            //write FDR, if required
            for (UpdatingFilter filter : identificationUpdatingFilters) {
                if (filter instanceof FDRCalculator) {
                    marshallFDRParam(out, (FDRCalculator) filter);
                }
            }
            for (Filter<Identification> filter : identificationFilters) {
                if (filter instanceof FDRCalculator) {
                    marshallFDRParam(out, (FDRCalculator) filter);
                }
            }
            out.println("</Experiment>");

            //close experimentcollection
            out.println("</ExperimentCollection>");

        } catch (IOException e) {

            logger.error("Error writing outputfile: " + e.getMessage(), e);
            throw new ConverterException("Error writing output file: " + e.getMessage(), e);

        } finally {
            if (out != null) {
                out.close();
            }
        }

    }

    private void marshallFDRParam(PrintWriter out, FDRCalculator fdrCalculator) throws IOException {
        switch (fdrCalculator.getFDRType()) {
            case PROTEIN:
                if (fdrCalculator.getProteinFalseDiscoveryRate() > 0) {
                    //write protein FDR param
                    CvParam cv = new CvParam();
                    cv.setCvLabel("MS");
                    cv.setAccession("MS:1001214");
                    cv.setName("prot:global FDR");
                    cv.setValue("" + fdrCalculator.getProteinFalseDiscoveryRate());
                    marshallPrideObject(out, cv);
                }
                break;
            case PEPTIDE:
                //write peptide FDR param
                CvParam cv = new CvParam();
                cv.setCvLabel("MS");
                cv.setAccession("MS:1001364");
                cv.setName("pep:global FDR");
                cv.setValue("" + fdrCalculator.getPeptideFalseDiscoveryRate());
                marshallPrideObject(out, cv);
                break;
            case BOTH:
                //write protein FDR param
                CvParam prot = new CvParam();
                prot.setCvLabel("MS");
                prot.setAccession("MS:1001214");
                prot.setName("prot:global FDR");
                prot.setValue("" + fdrCalculator.getProteinFalseDiscoveryRate());
                marshallPrideObject(out, prot);
                //write peptide FDR param
                CvParam pep = new CvParam();
                pep.setCvLabel("MS");
                pep.setAccession("MS:1001364");
                pep.setName("pep:global FDR");
                pep.setValue("" + fdrCalculator.getPeptideFalseDiscoveryRate());
                marshallPrideObject(out, pep);
                break;
        }

    }

    private <T extends PrideXmlObject> T updateObject(T objectToUpdate, List<UpdatingFilter<T>> updatingFilters) {

        if (objectToUpdate == null || updatingFilters == null) {
            return objectToUpdate;
        }

        T tmpObject = objectToUpdate;
        for (UpdatingFilter<T> filter : updatingFilters) {
            tmpObject = filter.update(tmpObject);
            //come filters can nullify object, so stop processing
            if (tmpObject == null) {
                break;
            }
        }

        return tmpObject;

    }

    private List<String> filterSpectra(PrideXmlReader reader) {

        ArrayList<String> retval = new ArrayList<String>();
        for (String id : reader.getSpectrumIds()) {

            //only run the filters if
            //  - we're filtering out the unidentified spectra and the spectrum is identified
            //  - we're not filtering out unidentified spectra

            if (isFilterUnidentifiedSpectra() && reader.isIdentifiedSpectrum(id)) {
                //filterObject will return true if object passes all filters
                //if one filter returns false - i.e object to be removed - filterObject will return false
                if (filterObject(reader.getSpectrumById(id), spectrumFilters)) {
                    retval.add(id);
                }
            }
            if (!isFilterUnidentifiedSpectra()) {
                //filterObject will return true if object passes all filters
                //if one filter returns false - i.e object to be removed - filterObject will return false
                if (filterObject(reader.getSpectrumById(id), spectrumFilters)) {
                    retval.add(id);
                }
            }

        }
        return retval;
    }

    /**
     * This method will pass the object to be filtered to each filter within the list of filters.
     * If one filter returns true - i.e. the object is to be filtered/removed, this method will
     * return false. This method will only return true if the object passes each filter and is
     * therefore valid
     *
     * @param obj     - object to be filtered
     * @param filters - a list of registered filters
     * @return true if the object is valid and should therefore be part of the final xml file
     */
    private <T extends PrideXmlObject> boolean filterObject(T obj, List<Filter<T>> filters) {

        boolean retval = true;
        //iterate over each registered filter for this object type. if it fails one filter,
        //break looping and return false; only return true if object passes all filters.
        for (Filter<T> filter : filters) {

            /**
             * Filter an object based on the underlying implementation requirements. This method
             * will return true if an object is to be filtered (i.e. excluded from a given task) and
             * false if it is not to be excluded (i.e. it is a valid object, based on the implementation
             * requirements).
             *
             * @param objectToFilter
             * @return
             */
            if (filter.filter(obj)) {
                retval = false;
                break;
            }
        }
        return retval;
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
            out.println();
        }

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
        } catch (Exception e) {
            throw new IOException("Error marshalling: " + xmlObject.getClass().getName(), e);
        }

    }

}
