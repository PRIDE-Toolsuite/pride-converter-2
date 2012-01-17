package uk.ac.ebi.pride.tools.converter.report.validator.objectrules;

import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;
import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.validator.Context;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import psidev.psi.tools.validator.rules.codedrule.ObjectRule;
import uk.ac.ebi.pride.tools.converter.dao.handler.QuantitationCvParams;
import uk.ac.ebi.pride.tools.converter.report.model.Admin;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.Description;

import java.text.MessageFormat;
import java.util.*;


/**
 * ObjectRule to check if the PRIDE XML file contains a NEWT taxonomy annotation in the
 * ../mzData/description/admin/sampleDescription section of the document.
 * Note: Only objects of certain classes are supported by this rule (see #canCheck) and
 * it is only checked if a cvParam with the 'NEWT' cvLable is present.
 *
 * @author Florian Reisinger
 * @since 0.1
 */
public class SubsampleCheck<ReportObject> extends ObjectRule<ReportObject> {

    private static final Context context = new Context("sampleDescription element");

    public SubsampleCheck(OntologyManager ontologyManager) {
        super(ontologyManager);
        setName("SubsampleCheck");
    }

    /**
     * @return a String that should uniquely identify the rule
     */
    @Override
    public String getId() {
        return "SUBSAMPLES";
    }

    /**
     * This rule can only check objects of the following types (as they wrap the CvParam
     * that should define a species):
     * - Description
     * - Admin
     * - SampleDescription
     * <p/>
     * NOTE: higher level objects (like MzData or Experiment) are not taken into account
     * as these are usually too big and the validator is supposed to break tem into parts.
     *
     * @param t the object to check.
     * @return true if this rule can check the provided object.
     */
    public boolean canCheck(Object t) {
        return (t instanceof Admin || t instanceof Description);
    }

    public Collection<ValidatorMessage> check(ReportObject reportObject) throws ValidatorException {
        Collection<ValidatorMessage> msgs = new ArrayList<ValidatorMessage>();

        if (reportObject instanceof Admin) {
            msgs = checkAdmin((Admin) reportObject);
        }
        if (reportObject instanceof Description) {
            msgs = checkSampleDescription((Description) reportObject);
        } else {
            // we got a unexpected object, create a appropriate ValidatorMessage
            msgs.add(new ValidatorMessage("SubsampleCheck: Could not check the presented object '" + reportObject.getClass()
                    + "', as it is not of a supported type (Admin|Description)!",
                    MessageLevel.ERROR, context, this));
        }

        return msgs;
    }


    private Collection<ValidatorMessage> checkAdmin(Admin admin) {
        if (admin == null) {
            Collection<ValidatorMessage> msgs = new ArrayList<ValidatorMessage>();
            msgs.add(new ValidatorMessage("SubsampleCheck: Could not check 'admin' section, as the object was null!",
                    MessageLevel.ERROR, context, this));
            return msgs;
        } else {
            return checkSampleDescription(admin.getSampleDescription());
        }

    }

    private Collection<ValidatorMessage> checkSampleDescription(Description sampleDesc) {
        Collection<ValidatorMessage> msgs = new ArrayList<ValidatorMessage>();
        if (sampleDesc == null) {
            msgs.add(new ValidatorMessage("SubsampleCheck: Could not check 'sampleDescription' section, as the object was null!",
                    MessageLevel.ERROR, context, this));
        } else {

            //check to see if we have subsample params
            boolean hasQuantParams = false;
            for (CvParam cv : sampleDesc.getCvParam()) {
                if (QuantitationCvParams.isAQuantificationParam(cv.getAccession())) {
                    hasQuantParams = true;
                    break;
                }
            }

            if (hasQuantParams) {

                //how many subsamples
                Map<String, String> subsamples = new HashMap<String, String>();
                for (CvParam cv : sampleDesc.getCvParam()) {
                    if (QuantitationCvParams.isQuantificationReagent(cv.getAccession())) {
                        //quant reagent, subsample id
                        subsamples.put(cv.getName(), cv.getValue());
                    }
                }
                if (subsamples.size() < 2) {
                    msgs.add(new ValidatorMessage("SubsampleCheck: At least 2 quantification reagent params need to be given.",
                            MessageLevel.ERROR, context, this));
                }
                //sort all non-quant params per cv label
                MultiMap<String, CvParam> params = new MultiHashMap<String, CvParam>();
                for (CvParam cv : sampleDesc.getCvParam()) {
                    if (!QuantitationCvParams.isAQuantificationParam(cv.getAccession())) {
                        params.put(cv.getCvLabel(), cv);
                    }
                }
                //check to see that all other non-quant params are all equally numbered
                for (String cvLabel : params.keySet()) {
                    Collection<CvParam> cvParamsForLabel = params.get(cvLabel);
                    if (cvParamsForLabel.size() != subsamples.size()) {
                        msgs.add(new ValidatorMessage(MessageFormat.format("SubsampleCheck: There are {0} defined subsamples but {1} cv params defined for label {2}", subsamples.size(), cvParamsForLabel.size(), cvLabel),
                                MessageLevel.ERROR, context, this));

                    }

                    //check that all params have a valid subsample id
                    Set<String> observedSubsampleIds = new HashSet<String>();
                    for (CvParam cv : cvParamsForLabel) {
                        if (!observedSubsampleIds.add(cv.getValue())) {
                            msgs.add(new ValidatorMessage(MessageFormat.format("SubsampleCheck: There are multiple values for subsample {0} for cv label {1}", cv.getValue(), cvLabel),
                                    MessageLevel.ERROR, context, this));
                        }
                        if (!subsamples.keySet().contains(cv.getValue())) {
                            msgs.add(new ValidatorMessage(MessageFormat.format("SubsampleCheck: {0} is not a valid subsample for cv label {1}", cv.getValue(), cvLabel),
                                    MessageLevel.ERROR, context, this));
                        }
                    }
                    Set<String> allSubsampleIds = new HashSet<String>();
                    allSubsampleIds.addAll(subsamples.keySet());
                    allSubsampleIds.removeAll(observedSubsampleIds);
                    if (allSubsampleIds.size() > 0) {
                        msgs.add(new ValidatorMessage(MessageFormat.format("SubsampleCheck: There is no values for subsample(s) {0} for cv label {1}", allSubsampleIds.toString(), cvLabel),
                                MessageLevel.ERROR, context, this));
                    }

                }

            }

        }
        // return the result messages
        return msgs;
    }
}
