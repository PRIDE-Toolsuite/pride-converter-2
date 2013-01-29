package uk.ac.lifesci.dundee.tools.converter.maxquant;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 23/01/13
 * Time: 17:17
 */
public class ProteinGroupFieldMapper extends BaseFieldMapper{

//        * ['id'],
//        * ['Sequence Coverage [%]'],
//        * ['PEP'],

    private int proteinIdColumn = -1;
    private int sequenceCoverageColumn = -1;
    private int pepScoreColumn = -1;


    public ProteinGroupFieldMapper(String headerLine) {
    	super(headerLine);
    	proteinIdColumn = columnIndex(new String [] {"id"});
    	sequenceCoverageColumn = columnIndex(new String [] {"Sequence Coverage [%]"});
    	pepScoreColumn = columnIndex(new String [] {"PEP"});
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
