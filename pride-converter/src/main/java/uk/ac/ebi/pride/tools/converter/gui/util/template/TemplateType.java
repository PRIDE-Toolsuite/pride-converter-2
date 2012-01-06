package uk.ac.ebi.pride.tools.converter.gui.util.template;

import uk.ac.ebi.pride.tools.converter.report.model.Contact;
import uk.ac.ebi.pride.tools.converter.report.model.InstrumentDescription;
import uk.ac.ebi.pride.tools.converter.report.model.Protocol;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 07/11/11
 * Time: 12:24
 */
public enum TemplateType {
    INSTRUMENT("instrument", InstrumentDescription.class),
    PROTOCOL("protocol", Protocol.class),
    CONTACT("contact", Contact.class);

    private String templatePath;
    private Class objectClass;

    TemplateType(String templatePath, Class objectClass) {
        this.templatePath = templatePath;
        this.objectClass = objectClass;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public Class getObjectClass() {
        return objectClass;
    }
}
