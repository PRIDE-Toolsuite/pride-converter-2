package uk.ac.ebi.pride.tools.converter.report.validator.objectrules;

import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.validator.Context;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import psidev.psi.tools.validator.rules.codedrule.ObjectRule;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;
import uk.ac.ebi.pride.tools.converter.report.model.InstrumentDescription;
import uk.ac.ebi.pride.tools.converter.report.model.Param;
import uk.ac.ebi.pride.tools.converter.report.model.UserParam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * ObjectRule to check if a source/analyzer/detector annotation has been provided.
 * <p/>
 * NOTE: there are also cv-mapping rules to check these elements, but they may not
 * result in an error if the rule could not be applied because of missing elements.
 *
 * @author Florian Reisinger
 * @since 0.1
 */
public class InstrumentCheck<ReportObject> extends ObjectRule<ReportObject> {

    private static final Context context = new Context("mzData instrument element");

    public InstrumentCheck(OntologyManager ontologyManager) {
        super(ontologyManager);
        setName("InstrumentCheck");
    }

    /**
     * @return a String that should uniquely identify the rule
     */
    @Override
    public String getId() {
        return "INSTRUMENT";
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
        return (t instanceof InstrumentDescription);
    }

    public Collection<ValidatorMessage> check(ReportObject ReportObject) throws ValidatorException {
        Collection<ValidatorMessage> msgs = new ArrayList<ValidatorMessage>();

        if (ReportObject instanceof InstrumentDescription) {
            msgs = checkInstrument((InstrumentDescription) ReportObject);
        } else {
            // we got a unexpected object, create a appropriate ValidatorMessage
            msgs.add(new ValidatorMessage("InstrumentCheck: Could not check the presented object '" + ReportObject.getClass()
                    + "', as it is not of a supported type (InstrumentDescription)!",
                    MessageLevel.ERROR, context, this));
        }

        return msgs;
    }

    private Collection<ValidatorMessage> checkInstrument(InstrumentDescription instrument) {
        Collection<ValidatorMessage> msgs = new ArrayList<ValidatorMessage>();
        if (instrument == null) {
            // this is only relevant if a schema validation did not take place, as this is a required element
            msgs.add(new ValidatorMessage("InstrumentCheck: Could not find 'instrument' element, please check against the PRIDE schema!",
                    MessageLevel.ERROR, context, this));
        } else {
            // first check the source and detector (as they are single elements
            checkComponent(instrument.getSource(), msgs);
            checkComponent(instrument.getDetector(), msgs);
            // then check the list of analyzers
            InstrumentDescription.AnalyzerList analyzers = instrument.getAnalyzerList();
            if (analyzers == null || analyzers.getAnalyzer() == null || analyzers.getAnalyzer().size() < 1) {
                msgs.add(new ValidatorMessage("InstrumentCheck: Could not find 'analyzer' elements, please check against the PRIDE schema!",
                        MessageLevel.ERROR, context, this));
            } else {
                // if we have at least one analyzer, check it for content
                for (Param analyser : analyzers.getAnalyzer()) {
                    checkComponent(analyser, msgs);
                }
            }
        }
        // return the result messages
        return msgs;
    }

    private void checkComponent(Param component, Collection<ValidatorMessage> msgs) {
        if (component == null) {
            // this is only relevant if a schema validation did not take place, as this is a required element
            msgs.add(new ValidatorMessage("InstrumentCheck: Could not find required component element, please check against the PRIDE schema!",
                    MessageLevel.ERROR, context, this));
        } else {
            // this should be the regular case, and we check that an element is there and contains some data
            boolean foundContent = false;
            List<CvParam> cvParamList = component.getCvParam();
            for (CvParam cvParam : cvParamList) {
                if (cvParam.getAccession() != null && cvParam.getAccession().length() > 1) {
                    foundContent = true;
                }
            }
            // in case we did not find any CvParams, there could also be only user defined params
            if (!foundContent) {
                List<UserParam> userParamList = component.getUserParam();
                for (UserParam userParam : userParamList) {
                    if (userParam.getName() != null && userParam.getName().length() > 1) {
                        foundContent = true;
                    }
                }
            }
            // if we have not found any annotation, we report an error
            if (!foundContent) {
                msgs.add(new ValidatorMessage("InstrumentCheck: Component (source, analyzer or detector) element did not contain any annotation!",
                        MessageLevel.ERROR, context, this));
            }
        }
    }

}
