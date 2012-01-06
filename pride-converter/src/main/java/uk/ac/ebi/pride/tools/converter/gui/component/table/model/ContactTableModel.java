package uk.ac.ebi.pride.tools.converter.gui.component.table.model;

import uk.ac.ebi.pride.tools.converter.report.model.Contact;

/**
 * Created by IntelliJ IDEA.
 * User: melih
 * Date: 15/03/2011
 * Time: 14:59
 */
public class ContactTableModel extends BaseTableModel<Contact> {

    public ContactTableModel() {

        String name = resourceBundle.getString("ContactTable.name.text");
        String email = resourceBundle.getString("ContactTable.email.text");
        String institution = resourceBundle.getString("ContactTable.institution.text");

        /**
         * column name, editable
         * 0 [ row number, false ]
         * 1 [ name, false ]
         * 2 [ email, false ]
         * 3 [ institution, false ]
         * 4 [ delete action, true ]
         * 5 [ data, false]
         */
        columnNames = new String[]{"", name, email, institution, "", ""};
        columnEditable = new boolean[]{false, false, false, false, true, false};
        columnTypes = new Class<?>[]{
                String.class, String.class, String.class, String.class, String.class, Contact.class
        };
        dataColumnIndex = 5;

    }

    public Object[] getRowObjectArray(Contact contact) {
        if (contact == null) return null;
        //this might happen if the DAO returns a null contact collection and the reportwriter puts in an empty contact
        if (contact.getName() == null || "".equals(contact.getName())) return null;
        return new Object[]{"", contact.getName(), contact.getContactInfo(), contact.getInstitution(), "", contact};
    }

}


