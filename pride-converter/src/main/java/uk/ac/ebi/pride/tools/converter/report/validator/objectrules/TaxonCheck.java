package uk.ac.ebi.pride.tools.converter.report.validator.objectrules;

import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.validator.Context;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import psidev.psi.tools.validator.rules.codedrule.ObjectRule;
import uk.ac.ebi.pride.tools.converter.report.model.Admin;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.Description;

import java.util.ArrayList;
import java.util.Collection;


/**
 * ObjectRule to check if the PRIDE XML file contains a NEWT taxonomy annotation in the
 * ../mzData/description/admin/sampleDescription section of the document.
 * Note: Only objects of certain classes are supported by this rule (see #canCheck) and
 * it is only checked if a cvParam with the 'NEWT' cvLable is present.
 *
 * @author Florian Reisinger
 * @since 0.1
 */
public class TaxonCheck<ReportObject> extends ObjectRule<ReportObject> {

    private static final Context context = new Context("sampleDescription element");

    public TaxonCheck(OntologyManager ontologyManager) {
        super(ontologyManager);
        setName("TaxonCheck");
    }

    /**
     * @return a String that should uniquely identify the rule
     */
    @Override
    public String getId() {
        return "TAXON";
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
            msgs.add(new ValidatorMessage("TaxonCheck: Could not check the presented object '" + reportObject.getClass()
                    + "', as it is not of a supported type (Admin|Description)!",
                    MessageLevel.ERROR, context, this));
        }

        return msgs;
    }


    private Collection<ValidatorMessage> checkAdmin(Admin admin) {
        if (admin == null) {
            Collection<ValidatorMessage> msgs = new ArrayList<ValidatorMessage>();
            msgs.add(new ValidatorMessage("TaxonCheck: Could not check 'admin' section, as the object was null!",
                    MessageLevel.ERROR, context, this));
            return msgs;
        } else {
            return checkSampleDescription(admin.getSampleDescription());
        }

    }

    private Collection<ValidatorMessage> checkSampleDescription(Description sampleDesc) {
        Collection<ValidatorMessage> msgs = new ArrayList<ValidatorMessage>();
        if (sampleDesc == null) {
            msgs.add(new ValidatorMessage("TaxonCheck: Could not check 'sampleDescription' section, as the object was null!",
                    MessageLevel.ERROR, context, this));
        } else {
            boolean taxonFound = false;
            for (CvParam cvParam : sampleDesc.getCvParam()) {
                if (cvParam.getCvLabel().equalsIgnoreCase("NEWT")) {
                    taxonFound = true;
                    break;
                }
            }
            // create a ValidatorMessage only if we did not find a taxonomy annotation
            if (!taxonFound) {
                msgs.add(new ValidatorMessage("Could not find a NEWT taxonomy annotation in 'sampleDescription' element!",
                        MessageLevel.ERROR, context, this));
            }
        }
        // return the result messages
        return msgs;
    }
}
