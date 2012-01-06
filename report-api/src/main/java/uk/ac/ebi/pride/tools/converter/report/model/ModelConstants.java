package uk.ac.ebi.pride.tools.converter.report.model;

import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 13-Aug-2010
 * Time: 14:17:05
 * To change this template use File | Settings | File Templates.
 */
public class ModelConstants {

    public static final String MODEL_PKG = "uk.ac.ebi.pride.tools.converter.report.model";
    public static final String REPORT_NAMESPACE = "";

    private static Map<Class, QName> modelQNames = new HashMap<Class, QName>();
    private static Map<Class, String> modelXpaths = new HashMap<Class, String>();

    static {

        modelQNames.put(Admin.class, new QName(REPORT_NAMESPACE, "admin"));
        modelQNames.put(ConfigurationOptions.class, new QName(REPORT_NAMESPACE, "ConfigurationOptions"));
        modelQNames.put(Contact.class, new QName(REPORT_NAMESPACE, "contact"));
        modelQNames.put(CV.class, new QName(REPORT_NAMESPACE, "cvLookup"));
        modelQNames.put(CvParam.class, new QName(REPORT_NAMESPACE, "cvParam"));
        modelQNames.put(DataProcessing.class, new QName(REPORT_NAMESPACE, "dataProcessing"));
        modelQNames.put(Description.class, new QName(REPORT_NAMESPACE, "description"));
        modelQNames.put(FragmentIon.class, new QName(REPORT_NAMESPACE, "FragmentIon"));
        modelQNames.put(Gel.class, new QName(REPORT_NAMESPACE, "Gel"));
        modelQNames.put(GelBasedData.class, new QName(REPORT_NAMESPACE, "GelBasedData"));
        modelQNames.put(Identification.class, new QName(REPORT_NAMESPACE, "Identification"));
        modelQNames.put(InstrumentDescription.class, new QName(REPORT_NAMESPACE, "instrument"));
        modelQNames.put(Metadata.class, new QName(REPORT_NAMESPACE, "Metadata"));
        modelQNames.put(Option.class, new QName(REPORT_NAMESPACE, "Option"));
        modelQNames.put(Param.class, new QName(REPORT_NAMESPACE, "additional"));
        modelQNames.put(Peptide.class, new QName(REPORT_NAMESPACE, "Peptide"));
        modelQNames.put(PeptidePTM.class, new QName(REPORT_NAMESPACE, "PTM"));
        modelQNames.put(Point.class, new QName(REPORT_NAMESPACE, "GelLocation"));
        modelQNames.put(Protocol.class, new QName(REPORT_NAMESPACE, "Protocol"));
        modelQNames.put(PTM.class, new QName(REPORT_NAMESPACE, "PTM"));
        modelQNames.put(Reference.class, new QName(REPORT_NAMESPACE, "Reference"));
        modelQNames.put(SearchResultIdentifier.class, new QName(REPORT_NAMESPACE, "SearchResultIdentifier"));
        modelQNames.put(Sequence.class, new QName(REPORT_NAMESPACE, "Sequence"));
        modelQNames.put(SimpleGel.class, new QName(REPORT_NAMESPACE, "Gel"));
        modelQNames.put(Software.class, new QName(REPORT_NAMESPACE, "software"));
        modelQNames.put(SourceFile.class, new QName(REPORT_NAMESPACE, "sourceFile"));
        modelQNames.put(UserParam.class, new QName(REPORT_NAMESPACE, "userParam"));
        modelQNames.put(DatabaseMapping.class, new QName(REPORT_NAMESPACE, "DatabaseMapping"));

        //now make set unmodifiable
        modelQNames = Collections.unmodifiableMap(modelQNames);

        modelXpaths.put(Admin.class, "/Report/Metadata/MzDataDescription/admin");
        modelXpaths.put(ConfigurationOptions.class, "/Report/ConfigurationOptions");
        modelXpaths.put(DataProcessing.class, "/Report/Metadata/MzDataDescription/dataProcessing");
        modelXpaths.put(Identification.class, "/Report/Identifications/Identification");
        modelXpaths.put(InstrumentDescription.class, "/Report/Metadata/MzDataDescription/instrument");
        modelXpaths.put(Metadata.class, "/Report/Metadata");
        modelXpaths.put(Protocol.class, "/Report/Metadata/Protocol");
        modelXpaths.put(PTM.class, "/Report/PTMs/PTM");
        modelXpaths.put(DatabaseMapping.class, "/Report/DatabaseMappings/DatabaseMapping");
        modelXpaths.put(Reference.class, "/Report/Metadata/Reference");
        modelXpaths.put(SearchResultIdentifier.class, "/Report/SearchResultIdentifier");

        //now make set unmodifiable
        modelXpaths = Collections.unmodifiableMap(modelXpaths);

    }


    public static boolean isRegisteredQNameClass(Class cls) {
        return modelQNames.containsKey(cls);
    }

    public static boolean isRegisteredXpathClass(Class cls) {
        return modelXpaths.containsKey(cls);
    }

    public static QName getQNameForClass(Class cls) {
        if (isRegisteredQNameClass(cls)) {
            return modelQNames.get(cls);
        } else {
            throw new IllegalStateException("No QName registered for class: " + cls);
        }
    }

    public static String getxPathForClass(Class cls) {
        if (isRegisteredXpathClass(cls)) {
            return modelXpaths.get(cls);
        } else {
            throw new IllegalStateException("No Xpath registered for class: " + cls);
        }
    }

    public static String getElementNameForClass(Class cls) {
        if (isRegisteredQNameClass(cls)) {
            return modelQNames.get(cls).getLocalPart();
        } else {
            throw new IllegalStateException("No QName registered for class: " + cls);
        }
    }

    public static Class getClassForElementName(String name) {
        for (Map.Entry<Class, QName> entry : modelQNames.entrySet()) {
            if (entry.getValue().getLocalPart().equals(name)) {
                return entry.getKey();
            }
        }
        return null;
    }


}
