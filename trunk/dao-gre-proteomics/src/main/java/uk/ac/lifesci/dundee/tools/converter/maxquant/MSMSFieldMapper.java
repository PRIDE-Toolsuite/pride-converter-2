package uk.ac.lifesci.dundee.tools.converter.maxquant;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 23/01/13
 * Time: 17:35
 */
public class MSMSFieldMapper extends BaseFieldMapper {

    private int idColumn = -1;
    private int scanNoColumn = -1;

    public MSMSFieldMapper(String headerLine) {
        super(headerLine);
        idColumn = columnIndex(new String [] {"id"});
        scanNoColumn = columnIndex(new String [] {"Scan number"});
    }

	public int getIdColumn() {
		return idColumn;
	}

	public int getScanNoColumn() {
		return scanNoColumn;
	}
   
}
