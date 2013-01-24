package uk.ac.lifesci.dundee.tools.converter.maxquant;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 23/01/13
 * Time: 17:35
 */
public class EvidenceFieldMapper {

    private String headerLine;
    private int peptideIdColumn = -1;
    private int msIDColumn = -1;
    private int proteinGroupIdColumn = -1;
    private int modificationsColumn = -1;
    private int modifiedSequenceColumn = -1;
    private int contaminantColumn = -1;
    private int pepScoreColumn = -1;
    private int reverseColumn = -1;
    private int sequenceColumn = -1;
    private int proteinDescriptionColumn = -1;
    private int uniprotColumn = -1;
    private int rawFileColumn = -1;

    public EvidenceFieldMapper(String headerLine) {
        this.headerLine = headerLine;
        String[] headers = headerLine.split("\\t");
        for (int i = 0; i < headers.length; i++) {
            if ("Peptide ID".equals(headers[i])) {
                peptideIdColumn = i;
            } else if ("MS/MS IDs".equals(headers[i])) {
                msIDColumn = i;
            } else if ("Protein Group IDs".equals(headers[i])) {
                proteinGroupIdColumn = i;
            } else if ("Modifications".equals(headers[i])) {
                modificationsColumn = i;
            } else if ("Modified Sequence".equals(headers[i])) {
                modifiedSequenceColumn = i;
            } else if ("Sequence".equals(headers[i])) {
                sequenceColumn = i;
            } else if ("Contaminant".equals(headers[i])) {
                contaminantColumn = i;
            } else if ("PEP".equals(headers[i])) {
                pepScoreColumn = i;
            } else if ("Reverse".equals(headers[i])) {
                reverseColumn = i;
            } else if ("Protein Descriptions".equals(headers[i])) {
                proteinDescriptionColumn = i;
            } else if ("Fasta headers".equals(headers[i])) {
                proteinDescriptionColumn = i;
            } else if ("Uniprot".equals(headers[i])) {
                uniprotColumn = i;
            } else if ("Raw File".equals(headers[i])) {
                rawFileColumn = i;
            }
            
        }

    }

    public int getPeptideIdColumn() {
        return peptideIdColumn;
    }

    public int getMsIDColumn() {
        return msIDColumn;
    }

    public int getProteinGroupIdColumn() {
        return proteinGroupIdColumn;
    }

    public int getModificationsColumn() {
        return modificationsColumn;
    }

    public int getModifiedSequenceColumn() {
        return modifiedSequenceColumn;
    }

    public int getContaminantColumn() {
        return contaminantColumn;
    }

    public int getPepScoreColumn() {
        return pepScoreColumn;
    }

    public int getReverseColumn() {
        return reverseColumn;
    }

    public int getSequenceColumn() {
        return sequenceColumn;
    }

    public int getProteinDescriptionColumn() {
        return proteinDescriptionColumn;
    }

    public int getUniprotColumn() {
        return uniprotColumn;
    }
    
    public int getRawFileColumn() {
        return rawFileColumn;
    }
    
    
}
