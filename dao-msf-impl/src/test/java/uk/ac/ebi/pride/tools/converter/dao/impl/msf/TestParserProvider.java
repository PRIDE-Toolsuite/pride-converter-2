/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.pride.tools.converter.dao.impl.msf;

import com.compomics.thermo_msf_parser.Parser;
import java.io.File;
import java.net.URL;
import java.sql.SQLException;

/**
 *
 * @author toorn101
 */
public class TestParserProvider {
    private static Parser instance = null;
    
    public static Parser getParser() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            URL dbFileURL = Parser.class.getResource("/protmix.msf");
            File dbFile = new File(dbFileURL.getFile());
            instance = new Parser(dbFile.getAbsolutePath(), true);
        }
        return instance;
    }
}
