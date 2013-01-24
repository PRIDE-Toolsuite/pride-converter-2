package uk.ac.lifesci.dundee.tools.converter.maxquant;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 23/01/13
 * Time: 17:18
 */
public class PeptideFieldMapper {
//        * ['id'],
//        * ['Unique', 'Unique (Groups)'],

    private String headerLine;
    private int peptideIdColumn;
    private int uniqueColumn;

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
            if ("id".equals(headers[i])) {
                peptideIdColumn = i;
            } else if ("Unique".equals(headers[i])) {
                uniqueColumn = i;
            } else if ("Unique (Groups)".equals(headers[i])) {
                uniqueColumn = i;
            }

        }
    }

}
