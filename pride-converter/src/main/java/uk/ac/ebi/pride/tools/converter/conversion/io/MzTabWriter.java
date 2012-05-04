package uk.ac.ebi.pride.tools.converter.conversion.io;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import uk.ac.ebi.pride.jmztab.MzTabFile;
import uk.ac.ebi.pride.jmztab.MzTabParsingException;
import uk.ac.ebi.pride.jmztab.model.Param;
import uk.ac.ebi.pride.jmztab.model.ParamList;
import uk.ac.ebi.pride.jmztab.model.Protein;
import uk.ac.ebi.pride.jmztab.model.Subsample;
import uk.ac.ebi.pride.jmztab.model.Unit;
import uk.ac.ebi.pride.tools.converter.dao.DAO;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.dao.Utils;
import uk.ac.ebi.pride.tools.converter.dao.impl.MascotDAO;
import uk.ac.ebi.pride.tools.converter.dao.impl.XTandemDAO;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.Identification;
import uk.ac.ebi.pride.tools.converter.report.model.Peptide;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;

/**
 * Writes the a DAO's prescan "output" into a
 * mzTab file. As this is done before any user
 * annotation (before the report file is being
 * generated) several fields are not available:
 * modifications, used search engine (apart from
 * Mascot and X!Tandem).
 * @author jg
 *
 */
public class MzTabWriter {
    private static final Logger logger = Logger.getLogger(MzTabWriter.class);

    private MzTabFile mztabFile;
    
    public enum OptionalColumn {
    	GEL_IDENTIFIER("opt_gel_identifier"),
    	SPOT_IDENTIFIER("opt_gel_spotidentifier");
    	
    	private String columnHeader;
    	
    	private OptionalColumn(String columnHeader) {
    		this.columnHeader = columnHeader;
    	}
    	
    	public String getColumnHeader() {
    		return columnHeader;
    	}
    }
    /**
     * The DAO to use to get the data.
     */
    private DAO dao;
    /**
     * The unit holding all the metadata
     * from the PRIDE XML file.
     */
    private Unit unit;
    /**
     * If set (not null) this string is used
     * as gel identifier for all generated
     * protein entries.
     */
    private String gelIdentifier;
    /**
     * If set (not null) this string is used
     * as spot identifier for all generated
     * protein entries.
     */
    private String spotId;
    /**
     * The number of subsamples to automaticall generate
     * (empty) annotations for.
     */
    private int subsamples = 0;

    public MzTabWriter(DAO dao) throws InvalidFormatException {
    	this(dao, 0, (String) null, (String) null);
    }
    
    /**
     * @param dao
     */
    public MzTabWriter(DAO dao, int subsamplesToGenerate, String gelIdentifier, String spotIdentifier) throws InvalidFormatException {
        // create the mztab file object
        mztabFile = new MzTabFile();
        // set the dao
        this.dao = dao;
        
        this.subsamples = subsamplesToGenerate;
        this.gelIdentifier = gelIdentifier;
        this.spotId = spotIdentifier;

        processData();
    }
    
    /**
     * Creates a new mzTab writer. The spot
     * identifier is retrieved by parsing the
     * sourcefile's name (not path) using the respective
     * regex.
     * @param dao The DAO to use to retrieve the data.
     * @param subsamplesToGenerate The number of subsamples empty quantitative fields should be generated for. If set to 0 no quantitative fields will be added.
     * @param gelId The gel identifier to be used for all proteins generated.
     * @param spotRegex The regex to be used on the filename to retrieve the spot identifier.
     * @throws InvalidFormatException
     */
    public MzTabWriter(DAO dao, int subsamplesToGenerate, String gelId, Pattern spotRegex) throws InvalidFormatException {
        // get the spot identifier
    	File sourcefile = new File (dao.getSourceFile().getPathToFile());
    	
    	String filename = sourcefile.getName();
    	
    	Matcher matcher = spotRegex.matcher(filename);
    	
    	if (!matcher.find())
    		throw new InvalidFormatException("Regular expression passed to retrieve the gel spot identifier does not match the passed sourcefile's name \"" + filename + "\"");
    	if (matcher.groupCount() < 1)
    		throw new InvalidFormatException("Regular expression passed to retrieve the gel spot identifier does not contain any matching groups.");
    	
    	// create the mztab file object
        mztabFile = new MzTabFile();
        
        // set the dao
        this.dao = dao;
        
        this.gelIdentifier = gelId;
        this.spotId = matcher.group(1);
        this.subsamples = subsamplesToGenerate;

        processData();
    }

    private void processData() throws InvalidFormatException {
        // process the metadata
        processMetadata();

        // process the proteins
        processProteins();
    }

    private void processMetadata() throws InvalidFormatException {
        // create a unit to hold the metadata
    	try {
	        unit = new Unit();
	
	        unit.setUnitId("TMP_" + System.currentTimeMillis());
	
	        // title
	        if (dao.getExperimentTitle() != null && dao.getExperimentTitle().length() > 0)
	            unit.setTitle(dao.getExperimentTitle());
	
	        // add quantitative fields if subsamples are defined
	        if (subsamples > 0) {
	        	unit.setQuantificationMethod(new Param("PRIDE", "PRIDE:0000391", "Quantification parameter", "TODO: Replace with quantification method parameter (Children of PRIDE:0000307)"));
	        	unit.setPeptideQuantificationUnit(new Param("PRIDE", "PRIDE:0000391", "Quantification parameter", "TODO: Replace with peptide quantification unit parameter (Children of PRIDE:0000392)"));
	        	unit.setProteinQuantificationUnit(new Param("PRIDE", "PRIDE:0000391", "Quantification parameter", "TODO: Replace with protein quantification unit parameter (Children of PRIDE:0000392)"));
	        	
	        	for (int subsampleIndex = 1; subsampleIndex <= subsamples; subsampleIndex++) {
	        		Subsample subsample = new Subsample(unit.getUnitId(), subsampleIndex);
	        		subsample.setDescription("TODO: Set the description for this subsamples.");
	        		subsample.setQuantificationReagent(new Param("PRIDE", "PRIDE:0000391", "Quantification parameter", "TODO: Replace with parameter for the used quantification reagent (Children of PRIDE:0000324)"));
	        		
	        		unit.setSubsample(subsample);
	        	}
	        }
	        
	        // add the used DAO options
	        Properties properties = dao.getConfiguration();
	        List<Param> customParams = new ArrayList<Param>();
	        
	        customParams.add(new Param("MzTab generation software", "PRIDE Converter"));
	        
	        for (String name : properties.stringPropertyNames()) {
	        	String value = properties.getProperty(name);
	        	Param propertyParam = new Param("pride_converter_dao_" + name, value);
	        	customParams.add(propertyParam);
	        }
	        
	        unit.setCustomParams(customParams);
	        
	        // set the unit
	        mztabFile.setUnit(unit);
    	}
    	catch (MzTabParsingException e) {
    		throw new InvalidFormatException(e);
    	}
    }

    private void processProteins() throws InvalidFormatException {
    	try {
	        // save the unit id
	        String unitId = unit.getUnitId();
	
	        // iterate over the proteins
	        Iterator<Identification> it = dao.getIdentificationIterator(true);
	
	        while (it.hasNext()) {
	            Identification identification = it.next();
	
	            // make sure it's not null
	            if (identification == null)
	                continue;
	
	            // create a new protein object
	            Protein protein = new Protein();
	
	            protein.setAccession(identification.getAccession());
	            protein.setUnitId(unitId);
	            // set the description if available
	            String descripion = getFirstParamValue(identification.getAdditional().getCvParam(), DAOCvParams.PROTEIN_NAME.getAccession());
	            protein.setDescription(descripion);
	            // check if there's a species param
	            List<CvParam> speciesParams = getParamForCvLabel(identification.getAdditional().getCvParam(), "NEWT");
	
	            if (speciesParams.size() == 1) {
	                protein.setTaxid(speciesParams.get(0).getAccession());
	                protein.setSpecies(speciesParams.get(0).getValue());
	            }
	
	            // database and database version
	            protein.setDatabase(identification.getDatabase());
	            protein.setDatabaseVersion(identification.getDatabaseVersion());
	            
	            // check if it's a .dat input file
	            if (dao instanceof MascotDAO) {
	            	ParamList searchEngineList = new ParamList(1);
	            	searchEngineList.add(new Param("MS", "MS:1001207", "Mascot", ""));
	            	protein.setSearchEngine(searchEngineList);
	            }
	            else if (dao instanceof XTandemDAO) {
	            	ParamList searchEngineList = new ParamList(1);
	            	searchEngineList.add(new Param("MS", "MS:1001476", "X!Tandem", ""));
	            	protein.setSearchEngine(searchEngineList);
	            }
	
	            // get the number of peptides
	            protein.setNumPeptides(identification.getPeptide().size());
	            HashSet<String> peptideSequences = new HashSet<String>();
	            for (Peptide p : identification.getPeptide()) {
	                peptideSequences.add(p.getSequence() + p.getPTM().toString());
	            }
	            protein.setNumPeptidesDistinct(peptideSequences.size());
	
	            // add the indistinguishable accessions to the ambiguitiy members
	            List<String> indistinguishableAccessions = getParam(identification.getAdditional().getCvParam(), DAOCvParams.INDISTINGUISHABLE_ACCESSION.getAccession());
	            protein.setAmbiguityMembers(indistinguishableAccessions);
	
	            // set the modifications
	            protein.setModifications(null);
	            
	            // set potential (empty) quant fields
	            for (int subsampleIndex = 1; subsampleIndex <= subsamples; subsampleIndex++) {
	            	protein.setAbundance(subsampleIndex, null, null, null);
	            }
	            
	            // set the optional columns
	            try {
		            if (gelIdentifier != null && gelIdentifier.length() > 0) {
		            	protein.setCustomColumn(OptionalColumn.GEL_IDENTIFIER.getColumnHeader(), 
		            			gelIdentifier);
		            }
		            if (spotId != null && spotId.length() > 0) {
		            	protein.setCustomColumn(OptionalColumn.SPOT_IDENTIFIER.getColumnHeader(), 
		            			spotId);
		            }
	            } catch (MzTabParsingException e) {
					throw new ConverterException("Failed to generate mzTab file: " + e.getMessage());
				}
	
	            // process the protein's peptides
	            processIdentificationPeptides(identification);
	
	            // add the protein
	            try {
	                mztabFile.addProtein(protein);
	            } catch (MzTabParsingException e) {
	                logger.error(e.getMessage());
	            }
	        }
    	}
    	catch (MzTabParsingException e) {
    		throw new InvalidFormatException(e);
    	}
    }

    private void processIdentificationPeptides(Identification identification) throws MzTabParsingException {
        // iterate over all the peptides
        for (Peptide p : identification.getPeptide()) {
            // initialize the new peptide
            uk.ac.ebi.pride.jmztab.model.Peptide peptide = new uk.ac.ebi.pride.jmztab.model.Peptide();

            // sequence
            peptide.setSequence(p.getSequence());
            // accession
            peptide.setAccession(identification.getAccession());
            // unit id
            peptide.setUnitId(unit.getUnitId());
            // unique can't be set - check with the mascot DAO

            // database
            peptide.setDatabase(identification.getDatabase());
            // database version
            peptide.setDatabaseVersion(identification.getDatabaseVersion());
            // search engine can currently not be set - requires translation to cvParam

            // get the search engine scores
            peptide.setSearchEngineScore(getPeptideSearchEngineScores(p));
            // the reliability cannot be determined

            // convert the modifications
            peptide.setModification(null);
            
            // add the quantitation fields
            for (int subsampleIndex = 1; subsampleIndex <= subsamples; subsampleIndex++)
            	peptide.setAbundance(subsampleIndex, null, null, null);

            // add the peptide
            mztabFile.addPeptide(peptide);
        }

    }

/**
 * Reporting modifications was disabled from the current version
 * of the MzTabWriter as the mztab export is done before the
 * user annotates the report file. Therefore, there will be
 * cases where a given modification's PSI-MOD accession is
 * not available.
 */
//    /**
//     * Converts a peptide's PeptidePTMs into a List
//     * of mzTab Modifications.
//     *
//     * @param p
//     * @return
//     */
//    private List<Modification> getPeptideModifications(Peptide p) {
//        ArrayList<Modification> modifications = new ArrayList<Modification>();
//
//        for (PeptidePTM ptm : p.getPTM()) {
//            String modAccession = (ptm.getModAccession() != null) ? ptm.getModAccession() : ptm.getSearchEnginePTMLabel();
//
//            Modification mod = new Modification(modAccession, (int) ptm.getModLocation());
//
//            modifications.add(mod);
//        }
//        return modifications;
//    }

    /**
     * Extracts all search engine score related parameters
     * from a peptide object and returns them in a list of
     * mzTab ParamS.
     *
     * @param peptide
     * @return
     */
    private ParamList getPeptideSearchEngineScores(Peptide peptide) throws MzTabParsingException {
        ParamList scoreParams = new ParamList();

        for (CvParam param : peptide.getAdditional().getCvParam()) {
            if (Utils.PEPTIDE_SCORE_PARAM.isScoreAccession(param.getAccession())) {
                Param scoreParam = new Param(param.getCvLabel(), param.getAccession(), param.getName(), param.getValue());

                scoreParams.add(scoreParam);
            }
        }

        return scoreParams;
    }

/**
 * Reporting modifications was disabled from the current version
 * of the MzTabWriter as the mztab export is done before the
 * user annotates the report file. Therefore, there will be
 * cases where a given modification's PSI-MOD accession is
 * not available.
 */
//    /**
//     * Processes an identification's peptides'
//     * modifications, corrects the position and
//     * returns them as a list.
//     *
//     * @param identification
//     * @return
//     */
//    private List<Modification> getIdentificationModifications(
//            Identification identification) {
//        HashSet<Modification> modifications = new HashSet<Modification>();
//
//        for (Peptide p : identification.getPeptide()) {
//            for (PeptidePTM ptm : p.getPTM()) {
//                String accession = null;
//
//                if (ptm.getModAccession() != null)
//                    accession = ptm.getModAccession();
//                else
//                    accession = ptm.getSearchEnginePTMLabel();
//
//                Integer position = (int) (p.getStart() + ptm.getModLocation() - 1);
//
//                Modification mod = new Modification(accession, position);
//
//                modifications.add(mod);
//            }
//        }
//
//        return new ArrayList<Modification>(modifications);
//    }

    /**
     * Returns the values of all parameters with the given accession from the
     * passed List of CvParams. In case there is not parameter
     * with such an accession an empty list is returned.
     *
     * @param params
     * @param accession
     * @return
     */
    private List<String> getParam(List<CvParam> params, String accession) {
        ArrayList<String> paramValues = new ArrayList<String>();

        for (CvParam p : params) {
            if (accession.equals(p.getAccession()))
                paramValues.add(p.getValue());
        }

        return paramValues;
    }

    /**
     * Returns the values of all parameters with the given cvLabel from the
     * passed List of CvParams. In case there is not parameter
     * with such a cvLabel an empty list is returned.
     *
     * @param params
     * @param cvLabel
     * @return
     */
    private List<CvParam> getParamForCvLabel(List<CvParam> params, String cvLabel) {
        ArrayList<CvParam> paramValues = new ArrayList<CvParam>();

        for (CvParam p : params) {
            if (cvLabel.equals(p.getCvLabel()))
                paramValues.add(p);
        }

        return paramValues;
    }

    /**
     * Returns the value of the first parameter
     * with the given accession or null in case
     * the parameter does not exist.
     *
     * @param params
     * @param accession
     * @return
     */
    private String getFirstParamValue(List<CvParam> params, String accession) {
        for (CvParam p : params) {
            if (accession.equals(p.getAccession()))
                return p.getValue();
        }

        return null;
    }

    /**
     * Writes the mztab output to the given file.
     * In case the file already exists it will
     * be overwritten.
     *
     * @param outputFile
     */
    public void writeMzTabFile(File outputFile) throws Exception {
        if (outputFile.exists() && !outputFile.canWrite())
            throw new Exception(outputFile.getName() + " can not be written.");

        // create a file writer
        FileWriter writer = new FileWriter(outputFile);

        // write the file
        writer.write(mztabFile.toMzTab());

        writer.close();
    }
}
