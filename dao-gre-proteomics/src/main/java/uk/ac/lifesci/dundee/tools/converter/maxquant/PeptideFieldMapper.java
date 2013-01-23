package uk.ac.lifesci.dundee.tools.converter.maxquant;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 23/01/13
 * Time: 17:18
 */
public class PeptideFieldMapper {
//        * ['id'],
//        * ['Sequence'],
//        * ['Unique', 'Unique (Groups)'],
//        * ['Protein Group IDs'],
//        * ['Contaminant'],
//        * ['PEP'],
//        * ['Reverse']

    private String headerLine;
    private int peptideIdColumn;
    private int sequenceColumn;
    private int proteinGroupsColumn;
    private int contaminantColumn;
    private int pepScoreColumn;
    private int reverseColumn;
    private int uniqueColumn;

    public int getSequenceColumn() {
        return sequenceColumn;
    }

    public int getProteinGroupsColumn() {
        return proteinGroupsColumn;
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

    public int getUniqueColumn() {
        return uniqueColumn;
    }

    public int getPeptideIdColumn() {
        return peptideIdColumn;
    }

    public PeptideFieldMapper(String headerLine) {
        this.headerLine = headerLine;
        String[] headers = headerLine.split("\\t");
        for (int i = 0; i < headers.length; i++) {
            if ("Sequence".equals(headers[i])) {
                sequenceColumn = i;
            } else if ("id".equals(headers[i])) {
                peptideIdColumn = i;
            } else if ("Unique".equals(headers[i])) {
                uniqueColumn = i;
            } else if ("Unique (Groups)".equals(headers[i])) {
                uniqueColumn = i;
            } else if ("Protein Group IDs".equals(headers[i])) {
                proteinGroupsColumn = i;
            } else if ("Contaminant".equals(headers[i])) {
                contaminantColumn = i;
            } else if ("PEP".equals(headers[i])) {
                pepScoreColumn = i;
            } else if ("Reverse".equals(headers[i])) {
                reverseColumn = i;
            }

        }
    }

}
