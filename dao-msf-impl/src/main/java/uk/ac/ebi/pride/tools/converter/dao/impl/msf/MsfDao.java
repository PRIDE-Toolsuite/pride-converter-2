/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.pride.tools.converter.dao.impl.msf;

import com.compomics.thermo_msf_parser.gui.MsfFile;
import com.compomics.thermo_msf_parser.msf.FastaLowMemController;
import com.compomics.thermo_msf_parser.msf.Modification;
import com.compomics.thermo_msf_parser.msf.ModificationLowMemController;
import com.compomics.thermo_msf_parser.msf.PeptideLowMem;
import com.compomics.thermo_msf_parser.msf.PeptideLowMemController;
import com.compomics.thermo_msf_parser.msf.ProcessingNode;
import com.compomics.thermo_msf_parser.msf.ProcessingNodeLowMemController;
import com.compomics.thermo_msf_parser.msf.ProcessingNodeParameter;
import com.compomics.thermo_msf_parser.msf.ProteinLowMemController;
import com.compomics.thermo_msf_parser.msf.RawFileLowMemController;
import com.compomics.thermo_msf_parser.msf.SpectrumLowMemController;
import com.compomics.thermo_msf_parser.msf.WorkFlowLowMemController;
import com.compomics.thermo_msf_parser.msf.ProteinLowMem;
import com.compomics.thermo_msf_parser.msf.SpectrumLowMem;
import com.compomics.thermo_msf_parser.msf.WorkflowInfo;
import com.compomics.thermo_msf_parser.msf.util.Joiner;
import uk.ac.ebi.pride.jaxb.model.Spectrum;
import uk.ac.ebi.pride.tools.converter.dao.DAO;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.dao.DAOProperty;
import uk.ac.ebi.pride.tools.converter.dao.impl.AbstractDAOImpl;
import uk.ac.ebi.pride.tools.converter.dao.impl.msf.converters.IdentificationConverter;
import uk.ac.ebi.pride.tools.converter.dao.impl.msf.converters.PTMConverter;
import uk.ac.ebi.pride.tools.converter.dao.impl.msf.converters.SpectrumConverter;
import uk.ac.ebi.pride.tools.converter.report.model.*;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.FileUtils;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author toorn101
 */
public class MsfDao extends AbstractDAOImpl implements DAO {

    private Properties configuration;
    private Integer confidenceLevel = 1;
    public static String MSF_FILE_STRING = "Proteome Discoverer .msf file";
    private static MsfFile msfFile;
    private static WorkFlowLowMemController workflowController = new WorkFlowLowMemController();
    private static ProcessingNodeLowMemController processingNodeController = new ProcessingNodeLowMemController();
    private static FastaLowMemController fasta = new FastaLowMemController();
    private static ModificationLowMemController mods = new ModificationLowMemController();
    private static PeptideLowMemController peptides = new PeptideLowMemController();
    private static SpectrumLowMemController spectra = new SpectrumLowMemController();
    private static ProteinLowMemController proteins = new ProteinLowMemController();
    private static RawFileLowMemController rawFiles = new RawFileLowMemController();
    
    
    /**
     * formatter to be used in several parts of the DAO
     */
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public MsfDao(File source) {
        try {
            // TODO create connection to file
            msfFile = new MsfFile(source);
            //parser = new Parser(source.getAbsolutePath(), true); 
        } catch (Exception e) {
            throw new ConverterException("Errors while opening msf files.", e);
        }
    }

    public static Collection<DAOProperty> getSupportedProperties() {
        Collection<DAOProperty> properties = new ArrayList<DAOProperty>();

        DAOProperty<Integer> confidenceLevel = new DAOProperty<Integer>("confidence_level", 3, 1, 3);

        confidenceLevel.setUnit("level");
        confidenceLevel.setAdvanced(false);
        confidenceLevel.setDescription("Allow peptides at a certain confidence level: 1=low confidence, 2=intermediate confidence, 3=high confidence");
        confidenceLevel.setShortDescription("Minimum confidence level: 1=low confidence, 2=intermediate confidence, 3=high confidence");

        properties.add(confidenceLevel);
        return properties;
    }

    public void setConfiguration(Properties properties) {
        configuration = properties;
        confidenceLevel = Integer.parseInt(configuration.getProperty("confidence_level"));
    }

    public Properties getConfiguration() {
        return configuration;
    }

    public String getExperimentTitle() throws InvalidFormatException {
        WorkflowInfo temp = workflowController.getWorkFlowInfo(msfFile.getConnection());
        String experimentTitle = temp.getWorkflowDescription();
        return experimentTitle;

        //return parser.getWorkFlowInfo().getWorkflowDescription();
    }

    public String getExperimentShortLabel() {
        return workflowController.getWorkFlowInfo(msfFile.getConnection()).getWorkflowName();
        //return parser.getWorkFlowInfo().getWorkflowName();
    }

    public Param getExperimentParams() {
        Param experimentParam = new Param();
        File inputFile = msfFile.getMsfFile();

        // date of search
        experimentParam.getCvParam().add(DAOCvParams.DATE_OF_SEARCH.getParam(formatter.format(inputFile.lastModified()).toString()));
        //System.out.println(inputFile.lastModified());
        // original MS format param
        experimentParam.getCvParam().add(DAOCvParams.ORIGINAL_MS_FORMAT.getParam(MSF_FILE_STRING));


        return experimentParam;
    }

    public String getSampleName() {
        return null; // Let users fill it in themselves
    }

    public String getSampleComment() {
        return null;
    }

    public Param getSampleParams() {
        return new Param();
    }

    public SourceFile getSourceFile() {
        SourceFile source = new SourceFile();
        source.setFileType(MSF_FILE_STRING);
        source.setNameOfFile(msfFile.getMsfFile().getName());
        source.setPathToFile(msfFile.getMsfFile().getAbsolutePath());

        return source;
    }

    public Collection<Contact> getContacts() {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        return contacts;
    }

    public InstrumentDescription getInstrument() {
        InstrumentDescription i = new InstrumentDescription();
        // We can't find the machine name from the msf
        return i;
    }

    /**
     * Software
     *
     * @return Proteome Discoverer, with version.
     * @throws InvalidFormatException
     */
    public Software getSoftware() {
        Software s = new Software();
        s.setName("Proteome Discoverer");
        s.setVersion(workflowController.getWorkFlowInfo(msfFile.getConnection()).getMsfVersionInfo().getSoftwareVersion());
        s.setCompletionTime(formatter.format(workflowController.getWorkFlowInfo(msfFile.getConnection()).getWorkflowMessages().lastElement().getUnixTime() * 1000));

        return s;
    }

    /**
     * The computational methods used to process the data PD uses a graph based
     * processing pipeline, with nodes doing the processing I attempt to convert
     * the parameters for the nodes into cvTerms
     *
     * @return
     */
    public Param getProcessingMethod() {
        Param result = new Param(); //TODO: go from the processingnodes to a sensible conversion


        // Convert the node parameters into user params

        StringBuilder dotEdges = new StringBuilder();
        ArrayList<ProcessingNode> allProcessingNodes = processingNodeController.getAllProcessingNodes(msfFile.getConnection(), msfFile.getVersion());
        for (ProcessingNode node : allProcessingNodes) {
            for (ProcessingNodeParameter nodeParameter : node.getProcessingNodeParameters()) {
                UserParam param = new UserParam();
                param.setName(node.getProcessingNodeNumber() + ":" + node.getNodeName() + "(" + node.getNodeGUIDString() + "):" + nodeParameter.getParameterName());
                param.setValue(nodeParameter.getParameterValue());
                result.getUserParam().add(param);
            }

            // Meanwhile, create 'dot' digraph
            String parentNumbers = node.getProcessingNodeParentNumber();
            if (!parentNumbers.equals("")) {
                for (String parentNumber : parentNumbers.split(";")) {
                    ProcessingNode parentNode = processingNodeController.getProcessingNodeByNumber(msfFile.getConnection(), Integer.parseInt(parentNumber), msfFile.getVersion());
                    dotEdges.append(parentNode.getNodeName()).append("_").append(parentNode.getProcessingNodeNumber()).append("->").append(node.getNodeName()).append("_").append(node.getProcessingNodeNumber()).append(";");
                }
            }
        }

        UserParam dotGraph = new UserParam();
        dotGraph.setName("Workflow");
        dotGraph.setValue("digraph workflow {" + dotEdges.toString() + "}");
        result.getUserParam().add(dotGraph);

        return result;
    }

    /**
     * Wet lab protocol, can't be found in this file
     *
     * @return
     */
    public Protocol getProtocol() {
        return null; // User has to supply
    }

    /**
     * Publication references for this study. Not known from the file.
     *
     * @return
     */
    public Collection<Reference> getReferences() {
        return null; // User has to supply
    }

    /**
     * Return the name of the searched database Here, I assume the same database
     * is used in all of the searches that are combined in this file.
     *
     * @return
     * @throws InvalidFormatException
     */
    public String getSearchDatabaseName() {
        String fastafiles = Joiner.join(fasta.getFastaFileNames(msfFile.getConnection()), ",");
        return fastafiles;
    }

    /**
     * The version of the searched database This information is usually not
     * contained in a uniform way, so we return null
     *
     * @return
     * @throws InvalidFormatException
     */
    public String getSearchDatabaseVersion() {
        return getSearchDatabaseName();
    }

    /**
     * Return the PTMs for this file
     *
     * @return
     * @throws InvalidFormatException
     */
    public Collection<PTM> getPTMs() {
        Collection<PTM> ptms = new ArrayList<PTM>();
        ArrayList<Modification> allmods = mods.getAllModifications(msfFile.getConnection(), msfFile.getVersion(), msfFile.getAminoAcids());
        for (Modification mod : allmods) {
            ptms.add(PTMConverter.convert(mod));
        }
        return ptms;
    }

    /**
     * @return @throws InvalidFormatException
     */
    public Collection<DatabaseMapping> getDatabaseMappings() {
        Collection<DatabaseMapping> result = new ArrayList<DatabaseMapping>();
        ArrayList<String> allFastaFileNames = fasta.getFastaFileNames(msfFile.getConnection());
        for (String fastafile : allFastaFileNames) {
            DatabaseMapping mapping = new DatabaseMapping();
            mapping.setSearchEngineDatabaseName(fastafile);
            mapping.setSearchEngineDatabaseVersion(fastafile);

            result.add(mapping);
        }
        return result;
    }

    /**
     * Try to unequivocally identify the search result, based on source file
     * path, creation time (here implemented as 'last modified') and a hash
     * value, here an MD5 hash of the source file.
     *
     * @return
     * @throws InvalidFormatException
     */
    public SearchResultIdentifier getSearchResultIdentifier() {
        SearchResultIdentifier identifier = new SearchResultIdentifier();
        File inputFile = new File(msfFile.getMsfFile().getAbsolutePath());

        identifier.setSourceFilePath(inputFile.getAbsolutePath());
        identifier.setTimeCreated(formatter.format(inputFile.lastModified()).toString());
        identifier.setHash(FileUtils.MD5Hash(inputFile.getAbsolutePath()));
        //System.out.println("Identifier: " + identifier);
        return identifier;
    }

    public Collection<CV> getCvLookup() {
        ArrayList<CV> cvs = new ArrayList<CV>();

        cvs.add(new CV("MS", "PSI Mass Spectrometry Ontology", "3.20.0", "http://psidev.cvs.sourceforge.net/viewvc/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo"));
        cvs.add(new CV("PRIDE", "PRIDE Controlled Vocabulary", "1.101", "http://ebi-pride.googlecode.com/svn/trunk/pride-core/schema/pride_cv.obo"));

        return cvs;
    }

    // Get the number of spectra
    public int getSpectrumCount(boolean identifiedOnly) {
        int result;
        if (identifiedOnly) {
            result = peptides.returnNumberOfPeptides(msfFile.getConnection());
        } else {
            result = spectra.getNumberOfSpectra(msfFile.getConnection());
        }
        return result;
    }

    /**
     * Spectrum iterator only for the identified spectra
     */
    private class IdentifiedOnlySpectrumIterator implements Iterator<Spectrum> {

        private Set<Integer> spectrumIds = new HashSet<Integer>();
        private Iterator<Integer> spectrumIdIterator = null;
        private Vector<PeptideLowMem> peptidesForProtein;

        public IdentifiedOnlySpectrumIterator(Iterator<com.compomics.thermo_msf_parser.msf.ProteinLowMem> proteinIterator) {
            while (proteinIterator.hasNext()) {
                peptidesForProtein = peptides.getPeptidesForProtein(proteinIterator.next(), msfFile.getVersion(), msfFile.getAminoAcids());
                for (PeptideLowMem peptide : peptidesForProtein) {
                    spectrumIds.add(peptide.getSpectrumId());
                }
            }
        }

        public boolean hasNext() {
            if (spectrumIdIterator == null) {
                spectrumIdIterator = spectrumIds.iterator();
            }
            return spectrumIdIterator.hasNext();
        }

        public Spectrum next() {
            SpectrumLowMem spectrum = spectra.getSpectrumForSpectrumID(spectrumIdIterator.next(), msfFile.getConnection());
            Spectrum result;
            try {
                result = SpectrumConverter.convert(spectrum,msfFile.getConnection());
            } catch (Exception ex) {
                throw new ConverterException("While converting spectrum " + rawFiles.getRawFileNameForFileID(spectrum.getFileId(), msfFile.getConnection()) + " (id=" + spectrum.getSpectrumId() + ")", ex);
            }
            return result;
        }

        public void remove() {
            throw new UnsupportedOperationException("Don't call remove...");
        }
    }

    public Iterator<Spectrum> getSpectrumIterator(final boolean identifiedOnly) {
        Iterator<Spectrum> i;

        if (identifiedOnly) {
            i = new IdentifiedOnlySpectrumIterator(proteins.getAllProteins(msfFile.getConnection()));
        } else {
            final Iterator<com.compomics.thermo_msf_parser.msf.SpectrumLowMem> spectraIter = spectra.getAllSpectra(msfFile.getConnection()).iterator();
            i = new Iterator<Spectrum>() {
                public boolean hasNext() {
                    return spectraIter.hasNext();
                }

                public Spectrum next() {
                    Spectrum result = null;
                    com.compomics.thermo_msf_parser.msf.SpectrumLowMem msfSpectrum = spectraIter.next();
                    try {
                        result = SpectrumConverter.convert(msfSpectrum, msfFile.getConnection());
                    } catch (Exception ex) {
                        throw new RuntimeException("While converting spectrum " + rawFiles.getRawFileNameForFileID(msfSpectrum.getFileId(), msfFile.getConnection()), ex);
                    }
                    return result;
                }

                public void remove() {
                    throw new UnsupportedOperationException("Don't call remove..");
                }
            };
        }

        return i;
    }

    public int getSpectrumReferenceForPeptideUID(String peptideUID) {
        return spectra.getSpectrumForPeptideID(Integer.parseInt(peptideUID), msfFile.getConnection()).getSpectrumId();
    }

    /**
     * Return a protein identification by UID
     *
     * @param identificationUID
     * @return
     * @throws InvalidFormatException
     */
    public Identification getIdentificationByUID(String identificationUID) {
        ProteinLowMem p = proteins.getProteinForProteinId(msfFile.getConnection(), Integer.parseInt(identificationUID));
        return IdentificationConverter.convert(p, getSearchDatabaseName(), getSearchDatabaseVersion(), true, confidenceLevel, msfFile);
    }

    /**
     * @param preScanMode
     * @return
     * @throws InvalidFormatException
     */
    public Iterator<Identification> getIdentificationIterator(final boolean preScanMode) {
        IdentificationIterator it = new IdentificationIterator(msfFile, getSearchDatabaseName(), getSearchDatabaseVersion(), preScanMode, confidenceLevel);
        return it;
    }

    public void setExternalSpectrumFile(String filename) {
        throw new UnsupportedOperationException("Not supported.");
    }
}
