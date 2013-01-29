package uk.ac.lifesci.dundee.tools.converter.maxquant;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 23/01/13
 * Time: 17:35
 */
public class EvidenceFieldMapper extends BaseFieldMapper {

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
        super(headerLine);
        peptideIdColumn = columnIndex(new String [] {"Peptide ID"});
        msIDColumn = columnIndex(new String [] {"MS/MS IDs"});
        proteinGroupIdColumn = columnIndex(new String [] {"Protein Group IDs"});
        modificationsColumn = columnIndex(new String [] {"Modifications"});
        modifiedSequenceColumn = columnIndex(new String [] {"Modified Sequence"});
        contaminantColumn = columnIndex(new String [] {"Contaminant"});
        pepScoreColumn = columnIndex(new String [] {"PEP"});
        reverseColumn = columnIndex(new String [] {"Reverse"});
        sequenceColumn = columnIndex(new String [] {"Sequence"});
        proteinDescriptionColumn = columnIndex(new String [] {"Protein Descriptions", "Fasta headers"});
        uniprotColumn = columnIndex(new String [] {"Uniprot"});
        rawFileColumn = columnIndex(new String [] {"Raw File"});
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
