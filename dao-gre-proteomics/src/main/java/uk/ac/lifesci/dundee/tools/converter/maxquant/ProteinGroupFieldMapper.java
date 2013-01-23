package uk.ac.lifesci.dundee.tools.converter.maxquant;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 23/01/13
 * Time: 17:17
 */
public class ProteinGroupFieldMapper {

//        * ['id'],
//        * ['Sequence Coverage [%]'],
//        * ['Uniprot'],
//        * ['Protein Descriptions','Fasta headers'],
//        * ['Contaminant'],
//        * ['PEP'],
//        * ['Reverse']

    private String headerLine;
    private int proteinIdColumn = -1;
    private int sequenceCoverageColumn = -1;
    private int uniprotColumn = -1;
    private int proteinDescriptionColumn = -1;
    private int contaminantColumn = -1;
    private int pepScoreColumn = -1;
    private int reverseColumn = -1;


    public ProteinGroupFieldMapper(String headerLine) {
        this.headerLine = headerLine;
        String[] headers = headerLine.split("\\t");
        for (int i = 0; i < headers.length; i++) {
            if ("id".equals(headers[i])) {
                proteinIdColumn = i;
            } else if ("Sequence Coverage [%]".equals(headers[i])) {
                sequenceCoverageColumn = i;
            } else if ("Protein Descriptions".equals(headers[i])) {
                proteinDescriptionColumn = i;
            } else if ("Fasta headers".equals(headers[i])) {
                proteinDescriptionColumn = i;
            } else if ("Uniprot".equals(headers[i])) {
                uniprotColumn = i;
            } else if ("Contaminant".equals(headers[i])) {
                contaminantColumn = i;
            } else if ("PEP".equals(headers[i])) {
                pepScoreColumn = i;
            } else if ("Reverse".equals(headers[i])) {
                reverseColumn = i;
            }
        }
    }

    public int getProteinIdColumn() {
        return proteinIdColumn;
    }

    public int getSequenceCoverageColumn() {
        return sequenceCoverageColumn;
    }

    public int getUniprotColumn() {
        return uniprotColumn;
    }

    public int getProteinDescriptionColumn() {
        return proteinDescriptionColumn;
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
}
