package uk.ac.ebi.pride.tools.converter.report.validator;

import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import psidev.psi.tools.validator.*;
import psidev.psi.tools.validator.rules.cvmapping.CvRule;
import uk.ac.ebi.pride.tools.converter.report.model.ModelConstants;
import uk.ac.ebi.pride.tools.converter.report.model.ReportObject;

import java.io.InputStream;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 17/08/11
 * Time: 14:43
 */
public class ReportObjectValidator extends Validator {

    public ReportObjectValidator(InputStream ontoConfig, InputStream cvRuleConfig, InputStream objectRuleConfig) throws ValidatorException, OntologyLoaderException {
        super(ontoConfig, cvRuleConfig, objectRuleConfig);

        // Validate CV Mapping Rules
        Collection<ValidatorMessage> msgs = this.checkCvMappingRules();
        //See if the mapping makes sense.
        if (msgs.size() != 0) {

            StringBuilder errorMsg = new StringBuilder("There were errors processing the CV mapping configuration file:").append('\n');
            for (ValidatorMessage msg : msgs) {
                if (msg.getLevel().isHigher(MessageLevel.WARN)) {
                    errorMsg.append(msg).append('\n');
                }
            }
            System.out.println("errorMsg = " + errorMsg);

        }

    }

    public ReportObjectValidator(InputStream ontoConfig) throws ValidatorException, OntologyLoaderException {
        super(ontoConfig);
    }

    public void setSharedOntologyManager(OntologyManager ontologyManager) {
        super.setOntologyManager(ontologyManager);
    }

    public Collection<ValidatorMessage> validateObject(ReportObject objToValidate) throws ValidatorException {

        // reset the WhiteListHack (hack to find terms that are not covered by the CvMapping)
        ValidatorCvContext.getInstance().resetRecognised();
        ValidatorCvContext.getInstance().resetNotRecognised();


        ArrayList<ValidatorMessage> msgs = new ArrayList<ValidatorMessage>();
        if (objToValidate != null) {

            String xpath = ModelConstants.getxPathForClass(objToValidate.getClass());

            //run cvRules
            if (xpath != null) {
                msgs.addAll(checkCvMapping(objToValidate, xpath));
                Set<String> terms = ValidatorCvContext.getInstance().getNotRecognisedTerms(xpath);
                if (terms != null) {
                    for (String trm : terms) {
                        msgs.add(new ValidatorMessage("Unrecognized term: " + trm, MessageLevel.INFO));
                    }
                }
            }

            //run object rules
            msgs.addAll(validate(objToValidate));
        }

        return msgs;

    }

    public boolean hasError(ReportObject objToValidate) throws ValidatorException {

        Collection<ValidatorMessage> msgs = validate(objToValidate);
        boolean errorFound = false;
        for (ValidatorMessage msg : msgs) {
            if (msg.getLevel().isHigher(MessageLevel.WARN)) {
                errorFound = true;
                break;
            }
        }
        return errorFound;

    }


    public Map<String, String> getStatistics() {

        HashMap<String, String> stats = new HashMap<String, String>();
        if (getCvRuleManager() != null && getReport() != null) {
            stats.put("CvMappingRule total count", "" + getCvRuleManager().getCvRules().size());
            stats.put("CvMappingRule not run", "" + getReport().getCvRulesNotChecked().size());

            StringBuilder msg = new StringBuilder(getReport().getCvRulesInvalidXpath().size()).append(" [");
            for (CvRule rule : getReport().getCvRulesInvalidXpath()) {
                msg.append(rule.getId()).append(",");
            }
            if (msg.lastIndexOf(",") > 0) {
                msg.replace(msg.lastIndexOf(","), msg.length(), "]");
            } else {
                msg.append("]");
            }
            stats.put("CvMappingRules with invalid Xpath", msg.toString());

            msg = new StringBuilder(getReport().getCvRulesValidXpath().size()).append(" [");
            for (CvRule rule : getReport().getCvRulesValidXpath()) {
                msg.append(rule.getId()).append(",");
            }
            if (msg.lastIndexOf(",") > 0) {
                msg.replace(msg.lastIndexOf(","), msg.length(), "]");
            } else {
                msg.append("]");
            }
            stats.put("CvMappingRules valid Xpath, but no hit", msg.toString());

            msg = new StringBuilder(getReport().getCvRulesValid().size()).append(" [");
            for (CvRule rule : getReport().getCvRulesValid()) {
                msg.append(rule.getId()).append(",");
            }
            if (msg.lastIndexOf(",") > 0) {
                msg.replace(msg.lastIndexOf(","), msg.length(), "]");
            } else {
                msg.append("]");
            }
            stats.put("CvMappingRules run & valid", msg.toString());

        }
        return stats;
    }


}
