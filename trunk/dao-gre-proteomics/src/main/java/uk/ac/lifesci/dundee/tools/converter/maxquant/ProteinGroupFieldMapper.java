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
//        * ['PEP'],

    private String headerLine;
    private int proteinIdColumn = -1;
    private int sequenceCoverageColumn = -1;
    private int pepScoreColumn = -1;


    public ProteinGroupFieldMapper(String headerLine) {
        this.headerLine = headerLine;
        String[] headers = headerLine.split("\\t");
        for (int i = 0; i < headers.length; i++) {
            if ("id".equals(headers[i])) {
                proteinIdColumn = i;
            } else if ("Sequence Coverage [%]".equals(headers[i])) {
                sequenceCoverageColumn = i;
            } else if ("PEP".equals(headers[i])) {
                pepScoreColumn = i;
            }
        }
    }

    public int getProteinIdColumn() {
        return proteinIdColumn;
    }

    public int getSequenceCoverageColumn() {
        return sequenceCoverageColumn;
    }

    public int getPepScoreColumn() {
        return pepScoreColumn;
    }

}
