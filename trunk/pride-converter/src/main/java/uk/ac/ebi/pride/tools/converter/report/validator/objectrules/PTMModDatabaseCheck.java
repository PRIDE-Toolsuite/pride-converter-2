package uk.ac.ebi.pride.tools.converter.report.validator.objectrules;

import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.validator.Context;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import psidev.psi.tools.validator.rules.codedrule.ObjectRule;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;

import java.util.ArrayList;
import java.util.Collection;


/**
 * ObjectRule to check if a PTM annotation has been provided.
 * <p/>
 * NOTE: there are also cv-mapping rules to check these elements, but they may not
 * result in an error if the rule could not be applied because of missing elements.
 *
 * @author Florian Reisinger
 * @since 0.1
 */
public class PTMModDatabaseCheck<ReportObject> extends ObjectRule<ReportObject> {

    private static final Context context = new Context("PTM element");

    public PTMModDatabaseCheck(OntologyManager ontologyManager) {
        super(ontologyManager);
        setName("PTMModDatabaseCheck");
    }

    /**
     * @return a String that should uniquely identify the rule
     */
    @Override
    public String getId() {
        return "PTM-MOD-DB";
    }

    /**
     * This rule can only check objects of the following types (as they wrap
     * the instrument element):
     * - Description
     * - Instrument
     * <p/>
     * NOTE: higher level objects (like MzData or Experiment) are not taken into account
     * as these are usually too big and the validator is supposed to break them into parts.
     *
     * @param t the object to check.
     * @return true if this rule can check the provided object.
     */
    public boolean canCheck(Object t) {
        return (t instanceof PTM);
    }

    public Collection<ValidatorMessage> check(ReportObject reportObject) throws ValidatorException {
        Collection<ValidatorMessage> msgs = new ArrayList<ValidatorMessage>();

        if (reportObject instanceof PTM) {

            PTM ptm = (PTM) reportObject;

            if (ptm == null) {
                msgs.add(new ValidatorMessage("PTM must not be null!", MessageLevel.ERROR, context, this));
            } else {
                if (ptm.getModDatabase() == null || "".equals(ptm.getModDatabase().trim())) {
                    msgs.add(new ValidatorMessage("PTM modification database name must not be null: " + ptm.getSearchEnginePTMLabel(),
                            MessageLevel.ERROR, context, this));
                }
            }

        } else {
            // we got a unexpected object, create a appropriate ValidatorMessage
            msgs.add(new ValidatorMessage("PTMModDatabaseCheck: Could not check the presented object '" + reportObject.getClass()
                    + "', as it is not of a supported type (PTM)!",
                    MessageLevel.ERROR, context, this));
        }

        return msgs;
    }

}
