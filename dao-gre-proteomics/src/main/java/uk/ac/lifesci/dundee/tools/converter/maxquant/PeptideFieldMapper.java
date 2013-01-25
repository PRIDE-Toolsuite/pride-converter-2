package uk.ac.lifesci.dundee.tools.converter.maxquant;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 23/01/13
 * Time: 17:18
 */
public class PeptideFieldMapper extends BaseFieldMapper{
//        * ['id'],
//        * ['Unique', 'Unique (Groups)'],

    private int peptideIdColumn;
    private int uniqueColumn;

    public int getUniqueColumn() {
        return uniqueColumn;
    }

    public int getPeptideIdColumn() {
        return peptideIdColumn;
    }

    public PeptideFieldMapper(String headerLine) {
    	super(headerLine);
        peptideIdColumn = columnIndex(headers, new String [] {"id"});
        uniqueColumn = columnIndex(headers, new String [] {"Unique", "Unique (Groups)"});
    }

}
