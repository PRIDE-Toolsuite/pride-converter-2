package uk.ac.ebi.pride.tools.converter.dao.handler.impl;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.tools.converter.dao.handler.FastaHandler;
import uk.ac.ebi.pride.tools.converter.report.model.Sequence;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 06-Dec-2010
 * Time: 16:32:27
 */
public abstract class AbstractFastaHandlerImpl implements FastaHandler, Iterator<Sequence> {

    private static final Logger logger = Logger.getLogger(AbstractFastaHandlerImpl.class);

    private File fastaFile = null;
    private LinkedHashMap<String, Long> offsets = new LinkedHashMap<String, Long>();
    private Iterator<String> sequenceIt = null;
    private LinkedHashMap<String, Long> ids = new LinkedHashMap<String, Long>();
    private HashSet<String> identifiedIds = new HashSet<String>();

    public AbstractFastaHandlerImpl(String fastaFilePath) {
        if (fastaFilePath == null) {
            throw new ConverterException("Please provide a non-null file path to the fasta file to use");
        }

        fastaFile = new File(fastaFilePath);
        if (fastaFile.exists()) {
            logger.warn("Processing " + fastaFile.getAbsolutePath());
            initOffsets();
        } else {
            throw new ConverterException("Error loading fasta file: " + fastaFile);
        }
    }

    private void initOffsets() {

        long now = System.currentTimeMillis();
        RandomAccessFile in = null;
        try {

            in = new RandomAccessFile(fastaFile, "r");
            byte[] bytes = new byte[1];
            long offset = 0;
            long currentOffset = -1;
            long currentId = 1;
            StringBuilder currentHeader = null;
            boolean inHeader = false;
            while (in.read(bytes) > 0) {

                char c = (char) bytes[0];
                //check to see if we have a header
                if (c == '>') {

                    //are we already in a header???
                    if (!inHeader) {
                        currentOffset = offset;
                        currentHeader = new StringBuilder();
                        inHeader = true;
                    } else {
                        currentHeader.append(c);
                    }

                } else {

                    //check to see if we have reached EOL
                    if (c == '\n' || c == '\r') {

                        //store info to map
                        if (inHeader) {
                            //store header and offset in offset map
                            offsets.put(currentHeader.toString().trim(), new Long(currentOffset));
                            //store header and sequence id in id map
                            ids.put(currentHeader.toString().trim(), currentId++);
                            if (logger.isDebugEnabled()) {
                                logger.debug("Found fasta header: " + currentHeader.toString().trim() + " at offset " + currentOffset);
                            }
                            //reaching EOL means that the header is done and next line should begin with sequence
                            inHeader = false;
                        }


                    } else {
                        //not EOL
                        if (inHeader) {
                            //store current char in header string
                            currentHeader.append(c);
                        }
                    }

                }

                //increment offset index
                offset++;

            }

        } catch (IOException e) {
            throw new ConverterException("Error initializing fasta file: " + e.getMessage(), e);
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    /* no op */
                }
        }

        logger.warn(offsets.size() + " fasta sequences parsed in " + (System.currentTimeMillis() - now) + " ms");
    }

    protected LinkedHashMap<String, Long> getOffsets() {
        return offsets;
    }

    protected Long getSequenceRefForAccession(String accession) {
        Long seqId = ids.get(accession);
        if (seqId != null) {
            identifiedIds.add(accession);
        }
        return seqId;
    }

    @Override
    public Iterator<Sequence> getIterator(boolean onlyIdentified) {
        if (onlyIdentified) {
            sequenceIt = identifiedIds.iterator();
        } else {
            sequenceIt = offsets.keySet().iterator();
        }
        return this;
    }

    @Override
    public boolean hasNext() {
        return sequenceIt.hasNext();
    }

    protected Sequence getNextSequence() {
        String id = sequenceIt.next();
        return getSequence(id);
    }

    protected Sequence getSequence(String sequenceIdentifier) {

        RandomAccessFile in = null;

        try {

            in = new RandomAccessFile(fastaFile, "r");
            byte[] bytes = new byte[1];
            StringBuilder header = new StringBuilder();
            StringBuilder sequence = new StringBuilder();
            boolean inHeader = false;

            //go to proper offset position in file
            Long offset = offsets.get(sequenceIdentifier);
            in.seek(offset);

            while (in.read(bytes) > 0) {

                char c = (char) bytes[0];

                //check to see if we have a header
                if (c == '>') {

                    if (!inHeader) {
                        inHeader = true;
                        //this will happen once we have processed the desired sequence and have reached the
                        //header of the next one - break loop
                        if (header.length() > 0) {
                            break;
                        }
                    } else {
                        header.append(c);
                    }

                } else {

                    //check to see if we have reached EOL
                    if (c == '\n' || c == '\r') {

                        if (inHeader) {
                            //reaching EOL means that the header is done and next line should begin with sequence
                            inHeader = false;
                        }

                    } else {
                        //not EOL
                        if (inHeader) {
                            //store current char in header string
                            header.append(c);
                        } else {
                            //store sequence char
                            sequence.append(c);
                        }
                    }

                }

            }

            if (header.length() > 0 && sequence.length() > 0) {
                //create new sequence
                Sequence seq = new Sequence();
                //set accession
                seq.setAccession(header.toString().trim());
                //set sequence
                seq.setValue(sequence.toString().trim());
                //set id - get from id map
                seq.setId(ids.get(header.toString().trim()));
                return seq;
            } else {
                throw new ConverterException("Improperly formatted fasta file for ");
            }

        } catch (IOException e) {
            throw new ConverterException("Error initializing FastA file: " + e.getMessage(), e);
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    /* no op */
                }
        }

    }

    public void remove() {
        throw new UnsupportedOperationException("Method remove is not supported in this iterator");
    }

}
