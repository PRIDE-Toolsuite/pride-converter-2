/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.pride.tools.converter.dao.impl.msf.converters;

import com.compomics.thermo_msf_parser.Parser;
import com.compomics.thermo_msf_parser.msf.ProcessingNode;
import com.compomics.thermo_msf_parser.msf.ProcessingNodeParameter;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.dao.impl.msf.terms.MsfCvTermReference;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;

import java.util.ArrayList;
import java.util.List;


/**
 * @author toorn101
 */
public class NodeToCVParam {

    private static final Logger logger = Logger.getLogger(NodeToCVParam.class);

    public static List<CvParam> convert(ProcessingNode node, Parser parser) {
        List<CvParam> result = new ArrayList<CvParam>();
        String methodParameterPrefix = "";
        if (node.getNodeGUID() != null) {
            switch (node.getNodeGUID()) {
                case NODE_MASCOT:
                    methodParameterPrefix = "Mascot:";
                    result.add(new CvParam("MS", "MS:1001207", "Mascot", "VERSION")); //@TODO: extract version information
                    break;
                case NODE_SEQUEST:
                    methodParameterPrefix = "SEQUEST:";
                    result.add(new CvParam("MS", "MS:1001208", "Sequest", "VERSION")); //@TODO: extract version information
                    break;
                case NODE_NON_FRAGMENT_FILTER:
                    methodParameterPrefix = "Non-Fragment Filter:";
                    break;
                case NODE_PTM_SCORER:
                    methodParameterPrefix = "Percolator:";
                    result.add(new CvParam("MS", "MS:1001490", "Percolator", "VERSION")); //@TODO: extract version information
                    break;
                case NODE_SCAN_EVENT_FILTER:
                case NODE_SPECTRUM_FILES:
                case NODE_TOP_N_PEAKS_FILTER:
                case NODE_SPECTRUM_NORMALIZATION:
                case NODE_PEPTIDE_VALIDATOR:
                case NODE_PERCOLATOR:
                default:
            }
            result.addAll(convertParameters(node, methodParameterPrefix));
        } else {
            logger.warn("Could not map " + node.getNodeName());
        }
        return result;
    }

    private static List<CvParam> convertParameters(ProcessingNode node, String methodParameterPrefix) {
        ArrayList<CvParam> result = new ArrayList<CvParam>();
        for (ProcessingNodeParameter param : node.getProcessingNodeParameters()) {
            try {
                result.add(mapFriendlyNameToCvParam(methodParameterPrefix + param.getFriendlyName(), param.getParameterValue()));
            } catch (RuntimeException ex) {
                try {
                    result.add(mapFriendlyNameToCvParam(param.getFriendlyName(), param.getParameterValue()));
                } catch (RuntimeException ex2) {
                    logger.warn("Could not map " + param.getFriendlyName());
                }
            }
        }

        return result;
    }

    /**
     * @param friendlyName
     * @return
     */
    // The Cv terms for Proteome Discoverer are apparently generated from some MGF file, so the friendly name 
    // should be mappable to the CV param by simple matching. Let's hope the friendly names don't change
    private static CvParam mapFriendlyNameToCvParam(String friendlyName, String value) {
        CvParam result = null;
        for (MsfCvTermReference ref : MsfCvTermReference.values()) {
            if (ref.getName().contains(friendlyName)) {
                result = new CvParam(ref.getCvLabel(), ref.getAccession(), ref.getName().replaceAll("\\(|\\)", ""), value);
            }
        }
        if (result == null) {
            throw new ConverterException("Could not build param for:" + friendlyName);
        }
        return result;
    }
}
