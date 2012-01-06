package uk.ac.ebi.pride.tools.converter.utils.xml.validation;


import org.apache.log4j.Logger;
import psidev.psi.tools.ontology_manager.OntologyManager;
import uk.ac.ebi.pride.tools.converter.report.validator.ReportObjectValidator;
import uk.ac.ebi.pride.tools.converter.utils.ConverterException;
import uk.ac.ebi.pride.validator.PrideXmlValidator;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 17/08/11
 * Time: 14:32
 */
public class ValidatorFactory {

    private static final Logger logger = Logger.getLogger(ValidatorFactory.class);

    private static ValidatorFactory instance = new ValidatorFactory();

    public static ValidatorFactory getInstance() {
        return instance;
    }

    private OntologyManager ontoManager;
    private PrideXmlValidator prideValidator;
    private ReportObjectValidator reportValidator;

    private ValidatorFactory() {

        //create full ontology manager
        initializeOntologyManager();

        //initialize PRIDE validator
        initializePrideValidator();

        //initialize ReportObject validator
        initializeReportValidator();

    }

    private synchronized void initializeOntologyManager() {

        if (ontoManager != null) {
            return;
        }
        try {
            URL ontoURL = getClass().getClassLoader().getResource("all-ontologies.xml");
            ontoManager = new OntologyManager(ontoURL.openStream());
        } catch (Exception e) {
            throw new ConverterException("Error initializing ontology manager: " + e.getMessage(), e);
        }

    }

    private synchronized void initializeReportValidator() {

        if (reportValidator != null) {
            return;
        }

        if (ontoManager == null) {
            initializeOntologyManager();
        }

        //  empty ontology file
        URL ontoURL = getClass().getClassLoader().getResource("empty-ontologies.xml");

        if (reportValidator == null) {
            try {
                //  these are stored in the pride validator jar
                URL cvURL = getClass().getClassLoader().getResource("report-cv-mapping.xml");
                URL objURL = getClass().getClassLoader().getResource("report-object-rules.xml");
                reportValidator = new ReportObjectValidator(ontoURL.openStream());
                reportValidator.setSharedOntologyManager(ontoManager);
                reportValidator.setCvMappingRules(cvURL.openStream());
                reportValidator.setObjectRules(objURL.openStream());
            } catch (Exception e) {
                throw new ConverterException("Could not load validator configuration files", e);
            }
        }

    }

    private synchronized void initializePrideValidator() {

        if (prideValidator != null) {
            return;
        }

        if (ontoManager == null) {
            initializeOntologyManager();
        }

        //  empty ontology file
        URL ontoURL = getClass().getClassLoader().getResource("empty-ontologies.xml");

        if (prideValidator == null) {
            try {
                //  these are stored in the pride validator jar
                URL cvURL = getClass().getClassLoader().getResource("cv-mapping.xml");
                URL objURL = getClass().getClassLoader().getResource("ObjectRules.xml");
                prideValidator = new PrideXmlValidator(ontoURL.openStream());
                prideValidator.setSharedOntologyManager(ontoManager);
                prideValidator.setCvMappingRules(cvURL.openStream());
                prideValidator.setObjectRules(objURL.openStream());
            } catch (Exception e) {
                throw new ConverterException("Could not load validator configuration files", e);
            }
        }

    }

    public PrideXmlValidator getPrideXmlValidator() {
        if (prideValidator == null) {
            initializePrideValidator();
        }
        return prideValidator;
    }

    public ReportObjectValidator getReportValidator() {
        if (reportValidator == null) {
            initializeReportValidator();
        }
        return reportValidator;
    }


}
