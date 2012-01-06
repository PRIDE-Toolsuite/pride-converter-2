package uk.ac.ebi.pride.tools.converter.dao;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.dao.handler.FastaHandler;
import uk.ac.ebi.pride.tools.converter.dao.handler.HandlerFactory;
import uk.ac.ebi.pride.tools.converter.report.model.Identification;

import java.io.File;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 14/01/11
 * Time: 14:56
 * To change this template use File | Settings | File Templates.
 */
public class FastaHandlerTest extends TestCase {

    private static final Logger logger = Logger.getLogger(FastaHandlerTest.class);

    private static final String FULL_STRING = "sw|P16401|H15_HUMAN RecName: Full=Histone H1.5;AltName: Full=Histone H1a;";
    private static final String ID = "H15_HUMAN";
    private static final String ACCESSION = "P16401";

    public void testUniprotAcFastaHandler() throws Exception {

        URL fasta = getClass().getClassLoader().getResource("swissprot-human.fasta");
        assertNotNull("Could not find fasta file", fasta);
        File f = new File(fasta.toURI());
        FastaHandler fh = HandlerFactory.getInstance().getFastaHandler(f.getAbsolutePath(), "uniprot_match_ac");

        Identification id = new Identification();
        id.setAccession(ACCESSION);

        id = fh.updateFastaSequenceInformation(id);

        assertNotNull("NO FASTA SEQUENCE FOUND", id.getFastaSequenceReference());
        assertNull("CURATED ACCESSION FOUND", id.getCuratedAccession());

    }

    public void testUniprotIdFastaHandler() throws Exception {

        URL fasta = getClass().getClassLoader().getResource("swissprot-human.fasta");
        assertNotNull("Could not find fasta file", fasta);
        File f = new File(fasta.toURI());
        FastaHandler fh = HandlerFactory.getInstance().getFastaHandler(f.getAbsolutePath(), "uniprot_match_id");

        Identification id = new Identification();
        id.setAccession(ID);

        id = fh.updateFastaSequenceInformation(id);

        assertNotNull("NO FASTA SEQUENCE FOUND", id.getFastaSequenceReference());
        assertNotNull("NO CURATED ACCESSION FOUND", id.getCuratedAccession());

    }

    public void testFullFastaHandler() throws Exception {

        URL fasta = getClass().getClassLoader().getResource("swissprot-human.fasta");
        assertNotNull("Could not find fasta file", fasta);
        File f = new File(fasta.toURI());
        FastaHandler fh = HandlerFactory.getInstance().getFastaHandler(f.getAbsolutePath(), "full");

        Identification id = new Identification();
        id.setAccession(FULL_STRING);

        id = fh.updateFastaSequenceInformation(id);

        assertNotNull("NO FASTA SEQUENCE FOUND", id.getFastaSequenceReference());
        assertNull("CURATED ACCESSION FOUND", id.getCuratedAccession());

    }

    public void testFirstWordFastaHandler() throws Exception {

        URL fasta = getClass().getClassLoader().getResource("small.fasta");
        assertNotNull("Could not find fasta file", fasta);
        File f = new File(fasta.toURI());
        FastaHandler fh = HandlerFactory.getInstance().getFastaHandler(f.getAbsolutePath(), "first_word");

        Identification id = new Identification();
        id.setAccession(ACCESSION);

        id = fh.updateFastaSequenceInformation(id);

        assertNotNull("NO FASTA SEQUENCE FOUND", id.getFastaSequenceReference());
        assertNull("CURATED ACCESSION FOUND", id.getCuratedAccession());

    }


}
