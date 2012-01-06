package uk.ac.ebi.pride.tools.converter.report.validator.objectrules;

import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.validator.Context;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import psidev.psi.tools.validator.rules.codedrule.ObjectRule;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.Protocol;

import java.util.ArrayList;
import java.util.Collection;


/**
 * ObjectRule to check if a source/analyzer/detector annotation has been provided.
 * <p/>
 * NOTE: there are also cv-mapping rules to check these elements, but they may not
 * result in an error if the rule could not be applied because of missing elements.
 *
 * @author Florian Reisinger
 * @since 0.1
 */
public class ProtocolCheck<ReportObject> extends ObjectRule<ReportObject> {

    private static final Context context = new Context("Protocol element");

    public ProtocolCheck(OntologyManager ontologyManager) {
        super(ontologyManager);
        setName("ProtocolCheck");
    }

    /**
     * @return a String that should uniquely identify the rule
     */
    @Override
    public String getId() {
        return "PROTOCOL";
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
        return (t instanceof Protocol);
    }

    public Collection<ValidatorMessage> check(ReportObject reportObject) throws ValidatorException {
        Collection<ValidatorMessage> msgs = new ArrayList<ValidatorMessage>();

        if (reportObject instanceof Protocol) {

            Protocol protocol = (Protocol) reportObject;

            if (protocol == null) {
                msgs.add(new ValidatorMessage("Protocol must not be null!", MessageLevel.ERROR, context, this));
            } else {
                if (protocol.getProtocolName() == null || "".equals(protocol.getProtocolName().trim())) {
                    msgs.add(new ValidatorMessage("Protocol name must not be null",
                            MessageLevel.ERROR, context, this));
                }
                if (protocol.getProtocolSteps() == null) {
                    msgs.add(new ValidatorMessage("Protocol must have at least one step defined",
                            MessageLevel.ERROR, context, this));
                } else {
                    if (protocol.getProtocolSteps().getStepDescription().isEmpty()) {
                        msgs.add(new ValidatorMessage("Protocol must have at least one step defined",
                                MessageLevel.ERROR, context, this));
                    } else {
                        for (Param p : protocol.getProtocolSteps().getStepDescription()) {
                            if (p.getCvParam().isEmpty() && p.getUserParam().isEmpty()) {
                                msgs.add(new ValidatorMessage("Protocol steps must have at least one cvParam or userParam defined",
                                        MessageLevel.ERROR, context, this));
                            }
                        }
                    }
                }
            }

        } else {
            // we got a unexpected object, create a appropriate ValidatorMessage
            msgs.add(new ValidatorMessage("ProtocolCheck: Could not check the presented object '" + reportObject.getClass()
                    + "', as it is not of a supported type (Protocol)!",
                    MessageLevel.ERROR, context, this));
        }

        return msgs;
    }

}
