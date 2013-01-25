package uk.ac.lifesci.dundee.tools.converter.maxquant;

public class BaseFieldMapper {
	
	public final static String COLUMN_DELIM="\\t";
	//protected String headerLine;
	protected String[] headers;
	
	
	public BaseFieldMapper(String headerLine) {
		//this.headerLine = headerLine;
        this.headers = headerLine.split(COLUMN_DELIM);
	}

	public String[] rowSections(String line) {
		return line.split(COLUMN_DELIM);
	}

	protected int columnIndex(String[] headers, String[] columnSearchNames) {
        for (int i = 0; i < headers.length; i++) {
            for (String name : columnSearchNames) {
                if (name.equalsIgnoreCase(headers[i]))
                    return i;
            }
        }
        return -1;
    }

}
