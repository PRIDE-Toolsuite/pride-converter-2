package uk.ac.ebi.pride.tools.converter.dao.handler.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import uk.ac.ebi.pride.mztab_java.MzTabFile;
import uk.ac.ebi.pride.mztab_java.MzTabParsingException;
import uk.ac.ebi.pride.mztab_java.model.Param.ParamType;
import uk.ac.ebi.pride.mztab_java.model.Protein;
import uk.ac.ebi.pride.mztab_java.model.Subsample;
import uk.ac.ebi.pride.mztab_java.model.Unit;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.dao.Utils;
import uk.ac.ebi.pride.tools.converter.dao.handler.ExternalHandler;
import uk.ac.ebi.pride.tools.converter.dao.handler.QuantitationCvParams;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.GelBasedData;
import uk.ac.ebi.pride.tools.converter.report.model.Identification;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.Peptide;
import uk.ac.ebi.pride.tools.converter.report.model.Point;
import uk.ac.ebi.pride.tools.converter.report.model.SimpleGel;
import uk.ac.ebi.pride.tools.converter.report.model.UserParam;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.utils.AccessionResolver;

/**
 * Extracts quantitative and gel-based data
 * from the passed mzTab file and updates
 * the given Identification objects accordingly.
 *
 * @author jg
 */
public class MzTabHandler implements ExternalHandler {
    private enum MZTAB_GELCOLUMN {
        SPOT_IDENTIFIER("opt_gel_spotidentifier"),
        GEL_IDENTIFIER("opt_gel_identifier"),
        URL("opt_gel_url"),
        YCOORD("opt_ycoord_pixel"),
        XCOORD("opt_xcoord_pixel"),
        MW("opt_mw"),
        PI("opt_pi");

        private String columnName;

        private MZTAB_GELCOLUMN(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnName() {
            return columnName;
        }
    }
    
    public static final String EMPAI_COLUMN = "opt_empai";
    public static final String TIC_COLUMN = "opt_tic";

    /**
     * The logger to use.
     */
    private static Logger logger = Logger.getLogger(MzTabHandler.class);
    /**
     * The MzTabFile parser used to parse the
     * given mzTab file.
     */
    private MzTabFile mzTabFile;
    /**
     * The unit containing the metadata of the
     * experiment in the mzTab file.
     */
    private Unit unit;

    /**
     * Creates a new MzTabHandler
     * based on the given mzTab File.
     * The mzTab file must only contain information
     * from one unit.
     *
     * @param mzTabFilePath Path to an mzTab file.
     */
    public MzTabHandler(String mzTabFilePath) {
        // create a file object
        File inputFile = new File(mzTabFilePath);

        // make sure the file exists
        if (!inputFile.isFile() || !inputFile.exists())
            throw new ConverterException("MzTab file '" + mzTabFilePath + "' could not be found.");
        if (!inputFile.canRead())
            throw new ConverterException("Cannot read passed MzTab file '" + mzTabFilePath + "'.");

        // create the mzTabFile object
        try {
            mzTabFile = new MzTabFile(inputFile);
            
            // make sure the mzTab File contains only one unit
            if (mzTabFile.getUnitMetadata().size() < 1)
            	throw new ConverterException("The mzTab file passed for additional data does not contain any data.");
            if (mzTabFile.getUnitMetadata().size() != 1)
                throw new ConverterException("The mzTab file passed for additional data must only contain one unit.");

            // save the first and only unit metadata
            unit = mzTabFile.getUnitMetadata().iterator().next();
        } catch (MzTabParsingException e) {
            throw new ConverterException("Failed to parse mzTab file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Returns the parameters that were used to generate the mzTab
     * file in case it was generated using PRIDE Converter. In case
     * no DAO Configuration is found in the mzTab file NULL is returned.
     * @return The DAO configuration or NULL in case the mzTab file was not created using PRIDE Converter.
     */
    public Properties getDaoConfiguration() {
    	Properties configuration = new Properties();
    	List<uk.ac.ebi.pride.mztab_java.model.Param> parameters = unit.getCustomParams();
    	boolean prideConverterGenerated = false;
    	
    	for (uk.ac.ebi.pride.mztab_java.model.Param param : parameters) {
    		if ("MzTab generation software".equals(param.getName()) && "PRIDE Converter".equals(param.getValue())) {
    			prideConverterGenerated = true;
    			continue;
    		}
    		
    		// make sure it's a DAO configuration
    		if (!param.getName().startsWith("pride_converter_dao_"))
    			continue;
    		
    		String name = param.getName().substring(20);
    		configuration.setProperty(name, param.getValue());
    	}
    	
    	if (!prideConverterGenerated)
    		return null;
    	
    	return configuration;
    }

    @Override
    public Identification updateIdentification(Identification identification) {
    	// ignore decoy hits
    	if (isDecoyHit(identification))
    		return identification;
    	
        // get the proteins information
        Protein mzTabProtein = mzTabFile.getProtein(identification.getAccession(), unit.getUnitId());

        // if the protein doesn't exist, try the curated accession
        if (mzTabProtein == null)
            mzTabProtein = mzTabFile.getProtein(identification.getCuratedAccession(), unit.getUnitId());

        // if the protein still doesn't exist, try the indistinguishable accessions
        if (mzTabProtein == null) {
            Collection<String> indistinguishableAccessions = Utils.getIndistinguishableAccessions(identification);

            for (String acc : indistinguishableAccessions) {
                AccessionResolver resolver = new AccessionResolver(acc, null, identification.getDatabase());
                String altAcc = resolver.getAccession();

                // try with the "normal" accession
                mzTabProtein = mzTabFile.getProtein(acc, unit.getUnitId());

                if (mzTabProtein == null) {
                    // try with the resolved accession
                    mzTabProtein = mzTabFile.getProtein(acc, unit.getUnitId());
                }

                // if it was successful change the primary accession
                if (mzTabProtein != null) {
                    identification = Utils.changePrimaryAccession(identification, acc, altAcc);
                    break;
                }
            }
        }

        // if the protein is defined, update the protein's information
        if (mzTabProtein != null) {
            identification = updateProteinInformation(mzTabProtein, identification);
        }

        // add the peptide info
        ArrayList<Peptide> updatedPeptides = new ArrayList<Peptide>(identification.getPeptide().size());

        for (Peptide peptide : identification.getPeptide()) {
            updatedPeptides.add(updatePeptide(peptide, identification.getAccession(), unit.getUnitId()));
        }

        // replace the peptides with the updated ones
        identification.getPeptide().clear();
        identification.getPeptide().addAll(updatedPeptides);

        return identification;
    }
    
    /**
     * Checks whether the passed identification was labeled
     * as a decoy hit.
     * 
     * @param identification The identification object to check.
     * @return Boolean indicating whether it's a decoy hit.
     */
    private boolean isDecoyHit(Identification identification) {
    	// check if the decoy cvParam is present
    	for (CvParam param : identification.getAdditional().getCvParam()) {
    		if (param.getAccession().equals(DAOCvParams.DECOY_HIT.getAccession()))
				return true;
    	}
    	
    	// check if the (older) userParam is present
    	for (UserParam param : identification.getAdditional().getUserParam()) {
    		if ("Decoy Hit".equals(param.getName()))
    			return true;
    	}
    	
    	return false;
    }

    /**
     * Adds the quantitative information from the mzTab
     * file to the passed peptide and returns the updated
     * object. The peptide is identified through the protein's
     * accessions, its sequence and score. If no peptide is found
     * the unaltered peptide object is returned.
     *
     * @param peptide
     * @param accession
     * @param unitId
     * @return
     */
    private Peptide updatePeptide(Peptide peptide, String accession, String unitId) {
        // get the peptide from the mzTab file
        Collection<uk.ac.ebi.pride.mztab_java.model.Peptide> mzTabPeptides = mzTabFile.getProteinPeptides(accession, unitId);

        if (mzTabPeptides == null) {
        	logger.warn("The peptide '" + peptide.getSequence() + "' in " + accession + " was not found in the mzTab file.");
        	return peptide;
        }
        
        // get the peptide scores
        Map<String, Double> peptideScores = Utils.extractPeptideScores(peptide);

        // find the peptide in the collection
        uk.ac.ebi.pride.mztab_java.model.Peptide mzTabPeptide = null;

        for (uk.ac.ebi.pride.mztab_java.model.Peptide p : mzTabPeptides) {
            // check the scores
            if (p.getSearchEngineScore() != null) {
                for (uk.ac.ebi.pride.mztab_java.model.Param scoreParam : p.getSearchEngineScore()) {
                    // check if the value is the same as in the peptide object
                    if (peptideScores.containsKey(scoreParam.getAccession()) &&
                            peptideScores.get(scoreParam.getAccession()).toString().equals(scoreParam.getValue())) {
                        mzTabPeptide = p;
                        break;
                    }
                }
            }

            // leave the loop as soon as the peptide was found
            if (mzTabPeptide != null)
                break;
        }

        // if the peptide wasn't found in the mzTabFile log the problem and return
        if (mzTabPeptide == null) {
            logger.warn("The peptide '" + peptide.getSequence() + "' in " + accession + " was not found in the mzTab file ("+ peptideScores.toString() +").");
            return peptide;
        }

        // update the peptide with the quant info
        for (Integer subsampleIndex : mzTabPeptide.getSubsampleIndexes()) {
            Double abundance = mzTabPeptide.getAbundance(subsampleIndex);
            Double std = mzTabPeptide.getAbundanceStdDev(subsampleIndex);
            Double err = mzTabPeptide.getAbundanceStdErr(subsampleIndex);

            if (abundance != null)
                peptide.getAdditional().getCvParam().add(
                        QuantitationCvParams.getPeptideIntensityParam(subsampleIndex, abundance.toString()));
            if (std != null)
                peptide.getAdditional().getCvParam().add(
                        QuantitationCvParams.getPeptideIntensityStdParam(subsampleIndex, std.toString()));
            if (err != null)
                peptide.getAdditional().getCvParam().add(
                        QuantitationCvParams.getPeptideIntensityStdErrParam(subsampleIndex, err.toString()));
            
            // make sure the unit and method are set
            if (abundance != null && unit.getQuantificationMethod() == null)
            	throw new ConverterException("Peptide quantification values reported without set quantification method.");
            if (abundance != null && unit.getPeptideQuantificationUnit() == null)
            	throw new ConverterException("Peptide quantification values reported without set quantification unit.");
        }

        // add the info how the peptide was quantified - if it was quantified
        if (unit.getQuantificationMethod() != null)
            peptide.getAdditional().getCvParam().add(convertCvParam(unit.getQuantificationMethod()));
        if (unit.getPeptideQuantificationUnit() != null)
        	peptide.getAdditional().getCvParam().add(convertCvParam(unit.getPeptideQuantificationUnit()));
        
        // add the TIC if available
        if (mzTabPeptide.getCustom().containsKey(TIC_COLUMN))
        	peptide.getAdditional().getCvParam().add(QuantitationCvParams.TIC_VALUE.getParam(mzTabPeptide.getCustom().get(TIC_COLUMN)));

        return peptide;
    }

    /**
     * Adds the quantitative information retrieved from the mzTab
     * Protein object and adds the respective cvParams to the
     * identification.
     *
     * @param mzTabProtein
     * @param identification
     * @return
     */
    private Identification updateProteinInformation(Protein mzTabProtein, Identification identification) {
    	// make sure the additional param exists 
    	if (identification.getAdditional() == null)
             identification.setAdditional(new Param());
    	
        // get the quant information
        for (Integer subsampleIndex : mzTabProtein.getSubsampleIndexes()) {
            Double abundance = mzTabProtein.getAbundance(subsampleIndex);
            Double std = mzTabProtein.getAbundanceStdDev(subsampleIndex);
            Double err = mzTabProtein.getAbundanceStdErr(subsampleIndex);

            if (abundance != null)
                identification.getAdditional().getCvParam().add(
                        QuantitationCvParams.getProteinIntensityParam(subsampleIndex, abundance.toString()));
            if (std != null)
                identification.getAdditional().getCvParam().add(
                        QuantitationCvParams.getProteinIntensityStdParam(subsampleIndex, std.toString()));
            if (err != null)
                identification.getAdditional().getCvParam().add(
                        QuantitationCvParams.getProteinIntensityStdErrParam(subsampleIndex, err.toString()));
            
            // make sure the unit and method are set
            if (abundance != null && unit.getQuantificationMethod() == null)
            	throw new ConverterException("Protein quantification values reported without set quantification method.");
            if (abundance != null && unit.getProteinQuantificationUnit() == null)
            	throw new ConverterException("Protein quantification values reported without set quantification unit.");
        }

        // add the info how the protein was quantified - if it was quantified
        if (unit.getQuantificationMethod() != null)
            identification.getAdditional().getCvParam().add(convertCvParam(unit.getQuantificationMethod()));
        // add the quant unit - if it was set
        if (unit.getProteinQuantificationUnit() != null)
        	identification.getAdditional().getCvParam().add(convertCvParam(unit.getProteinQuantificationUnit()));

        // add gel based data - if available
        Map<String, String> customColumns = mzTabProtein.getCustom();

        identification = updateIdentificationGelData(identification, customColumns);
        
        // add the emPAI if available
        if (customColumns.containsKey(EMPAI_COLUMN))
        	identification.getAdditional().getCvParam().add(QuantitationCvParams.EMPAI_VALUE.getParam(customColumns.get(EMPAI_COLUMN)));
        // add the TIC if available
        if (customColumns.containsKey(TIC_COLUMN))
        	identification.getAdditional().getCvParam().add(QuantitationCvParams.TIC_VALUE.getParam(customColumns.get(TIC_COLUMN)));

        return identification;
    }

    /**
     * Update the passed identification with the gel-based
     * data. If no gel-based data is available the unaltered
     * identification object is returned.
     *
     * @param identification
     * @param customColumns
     * @return
     */
    private Identification updateIdentificationGelData(
            Identification identification, Map<String, String> customColumns) {
        // get the spot and gel identifier
        String spotIdentifier = customColumns.get(MZTAB_GELCOLUMN.SPOT_IDENTIFIER.getColumnName());
        String gelIdentifier = customColumns.get(MZTAB_GELCOLUMN.GEL_IDENTIFIER.getColumnName());

        // check if the required info is available
        if (spotIdentifier == null || gelIdentifier == null)
            return identification;

        // set the two spot identifier and gel identifier
        identification.getAdditional().getCvParam().add(DAOCvParams.GEL_SPOT_IDENTIFIER.getParam(spotIdentifier));
        identification.getAdditional().getCvParam().add(DAOCvParams.GEL_IDENTIFIER.getParam(gelIdentifier));

        // if there's enough information, add a gel object
        String ycoord = customColumns.get(MZTAB_GELCOLUMN.YCOORD.getColumnName());
        String xcoord = customColumns.get(MZTAB_GELCOLUMN.XCOORD.getColumnName());
        String url = customColumns.get(MZTAB_GELCOLUMN.URL.getColumnName());
        String mw = customColumns.get(MZTAB_GELCOLUMN.MW.getColumnName());
        String pi = customColumns.get(MZTAB_GELCOLUMN.PI.getColumnName());

        if (url != null && ycoord != null && xcoord != null) {
            // create the gel object
            SimpleGel gel = new SimpleGel();
            gel.setGelLink(url);

            // add the position information to the identification
            GelBasedData gelBasedData = new GelBasedData();
            gelBasedData.setGel(gel);

            Point point = new Point();
            point.setXCoordinate(Double.parseDouble(xcoord));
            point.setYCoordinate(Double.parseDouble(ycoord));
            gelBasedData.setGelLocation(point);

            if (mw != null)
                gelBasedData.setMolecularWeight(Double.parseDouble(mw));
            if (pi != null)
                gelBasedData.setPI(Double.parseDouble(pi));

            identification.setGelBasedData(gelBasedData);
        }

        return identification;
    }
    
    @Override
    public Param getSampleDescriptionParams() {
        Param param = new Param();

        // indicate that there are multiple subsamples
        if (unit.getSubsamples() != null)
            param.getCvParam().add(QuantitationCvParams.CONTAINS_MULTIPLE_SUBSAMPLES.getParam());
        else
        	return param;

        // process the subsamples
        int subsampleIndex = 1;

        // initialize the available protein and peptide fields
        String proteinFields = "";
        String peptideFields = "";

        for (Subsample subsample : unit.getSubsamples()) {
            // create the subsample identifier
            String subsampleIdentifier = String.format("subsample%d", subsampleIndex);

            // add the description
            if (subsample.getDescription() != null)
                param.getCvParam().add(QuantitationCvParams.getSubsampleDescription(subsampleIndex, subsample.getDescription()));

            // add the reagent
            uk.ac.ebi.pride.mztab_java.model.Param reagentParam = subsample.getQuantificationReagent();
            // make sure the reagent is set and a cvParam
            if (reagentParam != null && reagentParam.getType() == ParamType.CV_PARAM) {
                CvParam reportParam = convertCvParam(reagentParam);
                reportParam.setValue(subsampleIdentifier);
                param.getCvParam().add(reportParam);
            }

            // add the species params
            if (subsample.getSpecies() != null) {
                param.getCvParam().addAll(convertCvParams(subsample.getSpecies(), subsampleIdentifier));
            }
            // add the tissue param
            if (subsample.getTissue() != null) {
                param.getCvParam().addAll(convertCvParams(subsample.getTissue(), subsampleIdentifier));
            }
            // add the cell type param
            if (subsample.getCellType() != null) {
                param.getCvParam().addAll(convertCvParams(subsample.getCellType(), subsampleIdentifier));
            }
            // add the disease param
            if (subsample.getDisease() != null) {
                param.getCvParam().addAll(convertCvParams(subsample.getDisease(), subsampleIdentifier));
            }

            // add the available protein and peptide fields (basically one intensity per subsample)
            if (unit.getQuantificationMethod() != null) {
                proteinFields += (proteinFields.length() > 1 ? "," : "") +
                        QuantitationCvParams.getProteinIntensityParam(subsampleIndex, "").getAccession();
                peptideFields += (peptideFields.length() > 1 ? "," : "") +
                        QuantitationCvParams.getPeptideIntensityParam(subsampleIndex, "").getAccession();
            }

            subsampleIndex++;
        }

        // add the available protein and peptide field params
        param.getUserParam().add(new UserParam("Available protein quantitation fields", proteinFields));
        param.getUserParam().add(new UserParam("Available peptide quantitation fields", peptideFields));

        return param;
    }

    @Override
    public Param getExperimentParams() {
        // just return the quantification method param
        Param param = new Param();

        // check if there's quantitation data available
        if (unit.getQuantificationMethod() != null)
            param.getCvParam().add(convertCvParam(unit.getQuantificationMethod()));
        
        // indicates whether TIC values were detected
        boolean ticDetected = false;
        
        // check if there's gel data (by checking the first protein) and semi quant methods
        if (mzTabFile.getProteins().size() > 0) {
            Protein protein = mzTabFile.getProteins().iterator().next();

            if (protein != null &&
                    protein.getCustom().containsKey(MZTAB_GELCOLUMN.GEL_IDENTIFIER.getColumnName()) &&
                    protein.getCustom().containsKey(MZTAB_GELCOLUMN.SPOT_IDENTIFIER.getColumnName())) {
                param.getCvParam().add(DAOCvParams.GEL_BASED_EXPERIMENT.getParam());
            }
            
            // add emPAI and TIC labels
            if (protein != null && protein.getCustom().containsKey(EMPAI_COLUMN))
            	param.getCvParam().add(QuantitationCvParams.EMPAI_QUANTIFIED.getParam());
            if (protein != null && protein.getCustom().containsKey(TIC_COLUMN)) {
            	param.getCvParam().add(QuantitationCvParams.TIC_QUANTIFIED.getParam());
            	ticDetected = true;
            }
        }
        
        // if TIC values weren't detected at the protein level check the peptide
        // level
        if (!ticDetected && mzTabFile.getPeptides().size() > 0) {
        	uk.ac.ebi.pride.mztab_java.model.Peptide peptide = mzTabFile.getPeptides().iterator().next();
        	
        	if (peptide != null && peptide.getCustom().containsKey(TIC_COLUMN)) {
        		param.getCvParam().add(QuantitationCvParams.TIC_QUANTIFIED.getParam());
            	ticDetected = true;
        	}
        }

        return param;
    }

    /**
     * Converts an mzTab Param to a PRIDE report
     * file CvParam.
     *
     * @param param
     * @return
     */
    private CvParam convertCvParam(uk.ac.ebi.pride.mztab_java.model.Param param) {
        return new CvParam(param.getCvLabel(), param.getAccession(), param.getName(), param.getValue());
    }

    /**
     * Converts all CV_PARAMS in the given param Collection
     * into report file CvParams and returns them in a new
     * collection.
     *
     * @param params
     * @param value  If not null this value replaces all the CvParams values.
     * @return
     */
    private Collection<CvParam> convertCvParams(Collection<uk.ac.ebi.pride.mztab_java.model.Param> params, String value) {
        ArrayList<CvParam> convertedParams = new ArrayList<CvParam>(params.size());

        for (uk.ac.ebi.pride.mztab_java.model.Param param : params) {
            if (param.getType() == ParamType.CV_PARAM) {
                CvParam reportParam = convertCvParam(param);
                if (value != null)
                    reportParam.setValue(value);
                convertedParams.add(reportParam);
            }
        }

        return convertedParams;
    }
}
