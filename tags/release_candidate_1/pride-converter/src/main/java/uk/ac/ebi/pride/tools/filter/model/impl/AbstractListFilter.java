package uk.ac.ebi.pride.tools.filter.model.impl;

import uk.ac.ebi.pride.jaxb.model.Identification;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.tools.filter.model.Filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 25/07/11
 * Time: 17:29
 */
public abstract class AbstractListFilter implements Filter<Identification> {

    protected Set<String> accessionsToFilter;

    public AbstractListFilter(Set<String> accessionsToFilter) {
        this.accessionsToFilter = accessionsToFilter;
    }

    public AbstractListFilter(String filePath) {

        accessionsToFilter = new HashSet<String>();

        if (filePath == null) {
            throw new ConverterException("Null file path given.");
        }

        File inFile = new File(filePath);
        if (!inFile.exists()) {
            throw new ConverterException("File not found: " + filePath);
        }

        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(inFile));
            String accession;
            while ((accession = in.readLine()) != null) {
                accessionsToFilter.add(accession.trim());
            }
        } catch (IOException e) {
            throw new ConverterException("Error while reading file: " + e.getMessage(), e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                /* no op */
            }
        }
    }

}
