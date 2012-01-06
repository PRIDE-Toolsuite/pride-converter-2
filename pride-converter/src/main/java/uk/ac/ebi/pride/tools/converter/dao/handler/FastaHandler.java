package uk.ac.ebi.pride.tools.converter.dao.handler;

import uk.ac.ebi.pride.tools.converter.report.model.Identification;
import uk.ac.ebi.pride.tools.converter.report.model.Sequence;

import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 06-Dec-2010
 * Time: 16:04:15
 * <p/>
 * Note to implementers: FastaHandlers need to be able to handle one or several FASTA files
 * and the iterator will return the sequences from all files. If there are several files, or even
 * if there is only one file, the FastaHandler needs to do a prescan of the file to ensure that
 * all protein identifiers are unique. Otherwise, throw IllegalStateException.
 */
public interface FastaHandler {

    /**
     * If the FastaHandler can locate the sequence corresponding to the accession of the
     * identification, correctly set the FastaSequenceReference of the identification. If no
     * corresponding sequence can be found for the accession, throw a ConverterException.
     *
     * @param identification - the identification object to be updated
     * @return - the updated identification object
     */
    public Identification updateFastaSequenceInformation(Identification identification);

    /**
     * Return an iterator over the fasta sequences. If the onlyIdentified flag is set to true,
     * return an iterator over only the sequences that have been involved in previous calls of
     * updateFastaSequenceInformation(). If getIterator(true) is called and no previous calls
     * of updateFastaSequenceInformation() have been done, this method should return an iterator
     * over an empty collection. If getIterator(false) is called, iterate over all sequences.
     *
     * @param onlyIdentified
     * @return
     */
    public Iterator<Sequence> getIterator(boolean onlyIdentified);

}
