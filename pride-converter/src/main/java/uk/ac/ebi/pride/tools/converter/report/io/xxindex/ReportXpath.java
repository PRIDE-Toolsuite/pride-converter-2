package uk.ac.ebi.pride.tools.converter.report.io.xxindex;

import uk.ac.ebi.pride.tools.converter.report.model.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Note: All classes added must be either null or extending ReportObject
 */
public enum ReportXpath {

    SEARCH_RESULT_IDENTIFIER_ELEMENT("/Report/SearchResultIdentifier", SearchResultIdentifier.class),
    METADATA_ELEMENT("/Report/Metadata", Metadata.class),
    IDENTIFICATION_ELEMENT("/Report/Identifications/Identification", Identification.class),
    FASTA_ELEMENT("/Report/Fasta", Fasta.class),
    SEQUENCE_ELEMENT("/Report/Fasta/Sequence", Sequence.class),
    PTM_ELEMENT("/Report/PTMs/PTM", PTM.class),
    DATABASE_MAPPING_ELEMENT("/Report/DatabaseMappings/DatabaseMapping", DatabaseMapping.class),
    CONFIGURATION_OPTIONS_ELEMENT("/Report/ConfigurationOptions", ConfigurationOptions.class);

    private final String xpath;

    private final Class type;

    private static final Set<String> xpaths;

    static {
        xpaths = new HashSet<String>();
        for (ReportXpath xpath : values()) {
            xpaths.add(xpath.getXpath());
        }
    }

    private ReportXpath(String xpath, Class clazz) {
        this.xpath = xpath;
        this.type = clazz;
    }

    public String getXpath() {
        return xpath;
    }

    public Class getClassType() {
        return type;
    }

    public static Set<String> getXpaths() {
        return xpaths;
    }
}
