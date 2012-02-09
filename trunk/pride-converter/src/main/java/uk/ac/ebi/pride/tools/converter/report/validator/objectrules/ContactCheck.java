package uk.ac.ebi.pride.tools.converter.report.validator.objectrules;

import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.validator.Context;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import psidev.psi.tools.validator.rules.codedrule.ObjectRule;
import uk.ac.ebi.pride.tools.converter.report.model.Admin;
import uk.ac.ebi.pride.tools.converter.report.model.Contact;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Created by IntelliJ IDEA.
 * User: attilacsordas
 * Date: Aug 20, 2010
 * Time: 11:57:32 AM
 * <p/>
 * <p/>
 * ObjectRule to check if the PRIDE XML file contains a valid email address in the
 * ../mzData/description/admin/contact/contactinfo section of the document.
 * Note: Only objects of certain classes are supported by this rule (see #canCheck)
 */

public class ContactCheck<ReportObject> extends ObjectRule<ReportObject> {

    private static final Context context = new Context("contact element");

    public ContactCheck(OntologyManager ontologyManager) {
        super(ontologyManager);
        this.setName("ContactCheck");
    }

    /**
     * @return a String that should uniquely identify the rule
     */
    @Override
    public String getId() {
        return "CONTACT";
    }

    /**
     * This rule can only check objects of the following types (as they wrap the contactInfo):
     * - Description
     * - Admin
     * - Contact
     * <p/>
     * NOTE: higher level objects (like MzData or Experiment) are not taken into account
     * as these are usually too big and the validator is supposed to break them into parts.
     *
     * @param t the object to check.
     * @return true if this rule can check the provided object.
     */
    public boolean canCheck(Object t) {
        return (t instanceof Admin || t instanceof Contact);
    }

    public Collection<ValidatorMessage> check(ReportObject reportObject) throws ValidatorException {
        Collection<ValidatorMessage> msgs = new ArrayList<ValidatorMessage>();

        if (reportObject instanceof Admin) {
            msgs = checkAdmin((Admin) reportObject);
        }
        if (reportObject instanceof Contact) {
            msgs = checkContact((Contact) reportObject);
        } else {
            // we got a unexpected object, create a appropriate ValidatorMessage
            msgs.add(new ValidatorMessage("ContactCheck: Could not check the presented object '" + reportObject.getClass()
                    + "', as it is not of a supported type (Admin|Contact)!",
                    MessageLevel.ERROR, context, this));
        }

        return msgs;
    }


    private Collection<ValidatorMessage> checkAdmin(Admin admin) {
        Collection<ValidatorMessage> msgs = new ArrayList<ValidatorMessage>();
        if (admin == null) {
            msgs.add(new ValidatorMessage("ContactCheck: Could not check 'admin' section, as the object was null!",
                    MessageLevel.ERROR, context, this));
        } else {
            for (Contact contact : admin.getContact()) {
                Collection<ValidatorMessage> contactMessages = checkContact(contact);
                msgs.addAll(contactMessages);
            }
        }
        return msgs;

    }

    private Collection<ValidatorMessage> checkContact(Contact contact) {
        Collection<ValidatorMessage> msgs = new ArrayList<ValidatorMessage>();
        if (contact == null) {
            msgs.add(new ValidatorMessage("ContactCheck: Could not check 'contact' section, as the object was null!",
                    MessageLevel.ERROR, context, this));
        } else {
            // in PRIDE XSD contactinfo is optional <xsd:element name="contactInfo" type="xsd:string" minOccurs="0">
            //<contact>
            //  <name>{1,1}</name>
            //  <institution>{1,1}</institution>
            //  <contactInfo>{0,1}</contactInfo>
            //</contact>
            String email = contact.getContactInfo();
            String name = contact.getName();
            String institution = contact.getInstitution();
            if (name == null || "".equals(name.trim())) {
                msgs.add(new ValidatorMessage("Could not find a 'name' element in the contact. ",
                        MessageLevel.ERROR, context, this));
            }
            if (institution == null || "".equals(institution.trim())) {
                msgs.add(new ValidatorMessage("Could not find a 'institution' element in the contact: " + contact.getName(),
                        MessageLevel.ERROR, context, this));
            }
            if (email == null || "".equals(email.trim())) {
                msgs.add(new ValidatorMessage("Could not find a 'contactInfo' element in the contact: " + contact.getName(),
                        MessageLevel.ERROR, context, this));
            } else {
                if (!email.contains("@")) {
                    msgs.add(new ValidatorMessage("Could not find a valid email address in 'contactInfo' element!)",
                            MessageLevel.ERROR, context, this));
                }
            }
        }
        // return the result messages
        return msgs;
    }
}

