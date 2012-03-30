package uk.ac.ebi.pride.tools.converter.dao;

import uk.ac.ebi.pride.tools.converter.report.model.CvParam;

public enum DAOCvParams {
    SEARCH_DATABASE_PROTEIN_SEQUENCE("PRIDE", "PRIDE:0000041", "Search database protein sequence", "", "PRIDE:0000004"),
    SUBMITTED_PROTEIN_ACCESSION("PRIDE", "PRIDE:0000299", "Submitted Protein Accession", "", "PRIDE:0000004"),
    XML_GENERATION_SOFTWARE("PRIDE", "PRIDE:0000175", "XML generation software", "", "PRIDE:0000006"),
    PRIDE_PROJECT("PRIDE", "PRIDE:0000097", "Project", "", "PRIDE:0000006"),
    PRECURSOR_INTENSITY("MS", "MS:1000042", "peak intensity", "", "MS:1000455"),
    PRECURSOR_MZ("MS", "MS:1000744", "selected ion m/z", "", "MS:1000455"),
    UNIT_MZ("MS", "MS:1000040", "m/z", "", "MS:1000460"),
    PRECURSOR_MH("PRIDE", "PRIDE:0000051", "(M+H)+", "", "PRIDE:0000049"),
    CHARGE_STATE("MS", "MS:1000041", "charge state", "", "MS:1000455"),
    POSSIBLE_CHARGE_STATE("MS", "MS:1000633", "possible charge state", "", "MS:1000455"),
    RETENTION_TIME("MS", "MS:1000894", "retention time", "", "MS:1000887"),
    PEAK_LIST_SCANS("MS", "MS:1000797", "peak list scans", "", "MS:1000499"),
    DATE_OF_SEARCH("PRIDE", "PRIDE:0000219", "Date of search", "", "PRIDE:0000006"),
    ORIGINAL_MS_FORMAT("PRIDE", "PRIDE:0000218", "Original MS data file format", "", "PRIDE:0000006"),
    PEPTIDE_FDR("MS", "MS:1001364", "pep:global FDR", "", "MS:1001405"),
    PROTEIN_NAME("PRIDE", "PRIDE:0000063", "Protein description line", "", "PRIDE:0000004"),
    DECOY_HIT("PRIDE", "PRIDE:0000303", "Decoy hit", "", "PRIDE:0000004"),
    NON_SIGNIFICANT_PROTEIN("PRIDE", "PRIDE:0000301", "Non-significant protein identification", "", "PRIDE:0000004"),
    ALTERNATIVE_ACCESSION("PRIDE", "PRIDE:0000064", "Secondary accession", "", "PRIDE:0000004"),
    INDISTINGUISHABLE_ACCESSION("PRIDE", "PRIDE:0000098", "Indistinguishable alternative protein accession", "", "PRIDE:0000004"),
    MS_MS_IDENTIFICATION("PRIDE", "PRIDE:0000113", "Identified by peptide fragmentation", "", "PRIDE:0000003"),
    PMF_IDENTIFICATION("PRIDE", "PRIDE:0000112", "Identified by peptide mass fingerprint", "", "PRIDE:0000003"),
    MASCOT_SCORE("PRIDE", "PRIDE:0000069", "Mascot Score", "", "PRIDE:0000003"),
    XTANDEM_EXPECT("MS", "MS:1001330", "X!Tandem:expect", "", "MS:1001153"),
    XTANDEM_HYPERSCORE("MS", "MS:1001331", "X!Tandem:hyperscore", "", "MS:1001153"),
    XTANDEM_DELTASCORE("PRIDE", "PRIDE:0000180", "delta", "", "PRIDE:0000047"),
    PEPTIDE_RANK("PRIDE", "PRIDE:0000091", "Rank", "", "PRIDE:0000003"),
    UPSTREAM_FLANKING_SEQUENCE("PRIDE", "PRIDE:0000065", "Upstream flanking sequence", "", "PRIDE:0000003"),
    DOWNSTREAM_FLANKING_SEQUENCE("PRIDE", "PRIDE:0000066", "Downstream flanking sequence", "", "PRIDE:0000003"),
    NON_SIGNIFICANT_PEPTIDE("PRIDE", "PRIDE:0000302", "Non-significant peptide identification", "", "PRIDE:0000003"),
    NEUTRAL_LOSS("MS", "MS:1000336", "neutral loss", "", "MS:1001055"),
    PRODUCT_ION_CHARGE("PRIDE", "PRIDE:0000204", "product ion charge", "", "PRIDE:0000187"),
    PRODUCT_ION_INTENSITY("PRIDE", "PRIDE:0000189", "product ion intensity", "", "PRIDE:0000187"),
    PRODUCT_ION_MZ("PRIDE", "PRIDE:0000188", "product ion m/z", "", "PRIDE:0000187"),
    PRODUCT_ION_MASS_ERROR("PRIDE", "PRIDE:0000190", "product ion mass error", "", "PRIDE:0000187"),
    PRODUCT_ION_DELTA("PRIDE", "PRIDE:0000188", "product ion m/z delta", "", "PRIDE:0000187"),
    GEL_BASED_EXPERIMENT("PRIDE", "PRIDE:0000305", "Gel-based experiment", "", "PRIDE:0000006"),
    GEL_SPOT_IDENTIFIER("PRIDE", "PRIDE:0000300", "Gel spot identifier", "", "PRIDE:0000004"),
    GEL_IDENTIFIER("PRIDE", "PRIDE:0000304", "Gel identifier", "", "PRIDE:0000004"),
    DEISOTOPING("MS", "MS:1000033", "deisotoping", "", "MS:1000543"),
    CHARGE_DECONVOLUTION("MS", "MS:1000034", "charge deconvolution", "", "MS:1000543"),
    CENTROIDED_SPECTRUM("MS", "MS:1000127", "centroided spectrum", "", "MS:1000525"),
    EXPERIMENT_DESCRIPTION("PRIDE", "PRIDE:0000040", "Experiment description", "", "PRIDE:0000006"),
    REFERENCE_DOI("PRIDE", "PRIDE:0000042", "DOI", "", "PRIDE:0000028"),
    REFERENCE_PUBMED("PRIDE", "PRIDE:0000029", "DOI", "", "PRIDE:0000028"),
    PMF_SEARCH("MS", "MS:1001081", "pmf search", "", "MS:1001080"),
    MS_MS_SEARCH("MS", "MS:1001083", "ms/ms search", "", "MS:1001080"),
    TAG_SEARCH("MS", "MS:1001082", "tag search", "", "MS:1001080"),
    SEARCH_SETTING_FRAGMENT_MASS_TOLERANCE("PRIDE", "PRIDE:0000161", "Fragment mass tolerance setting", "", "PRIDE:0000071"),
    SEARCH_SETTING_PARENT_MASS_TOLERANCE("PRIDE", "PRIDE:0000078", "Peptide mass tolerance setting", "", "PRIDE:0000071"),
    SEARCH_SETTING_MISSED_CLEAVAGES("PRIDE", "PRIDE:0000162", "Allowed missed cleavages", "", "PRIDE:0000071"),
    SEARCH_SETTING_TOLERANCE_MINUS_VALUE("MS", "MS:1001413", "search tolerance minus value", "", "MS:1001411"),
    SEARCH_SETTING_TOLERANCE_PLUS_VALUE("MS", "MS:1001412", "search tolerance plus value", "", "MS:1001411"),
    MASCOT_SIGNIFICANCE_THRESHOLD("MS", "MS:1001316", "Mascot:SigThreshold", "", "MS:1001302"),
    MASCOT_SIGNIFICANCE_THRESHOLD_TYPE("MS", "MS:1001758", "Mascot:SigThresholdType", "", "MS:1001302"),
    PEPTIDE_P_VALUE("MS", "MS:1001870", "p-value for peptides", "", "MS:1001092"),
    SEQUEST_DELTA_CN("MS", "MS:1001156", "Sequest:deltacn", "", "MS:1001153"),
    SEQUEST_XCORR("MS", "MS:1001155", "Sequest:xcorr", "", "MS:1001153"),
    SUBMIT_TO_INTACT("PRIDE", "PRIDE:0000405", "Submit PRIDE Experiment to IntAct", "", ""),
    SPECTRAST_DOT("MS", "MS:1001417", "SpectraST:dot", "", "MS:1001153"),
    SPECTRAST_DOT_BIAS("MS", "MS:1001418", "SpectraST:dot_bias", "", "MS:1001153"),
    SPECTRAST_FVAL("MS", "MS:1001419", "SpectraST:discriminant score F", "", "MS:1001153"),
    SPECTRAST_DELTA("MS", "MS:1001420", "SpectraST:delta", "", "MS:1001153"),
    MZ_DIFF("MS", "MS:1001975", "m/z difference", "", "") // TODO complete info here

    ;

    // TODO: Add all the different ions - wait for new MS version

    private String cv;
    private String accession;
    private String name;
    private String value;
    private String parentAccession;

    private DAOCvParams(String cv, String accession, String name, String value,
                        String parentAccession) {
        this.cv = cv;
        this.accession = accession;
        this.name = name;
        this.value = value;
        this.parentAccession = parentAccession;
    }

    public CvParam getParam() {
        return new CvParam(cv, accession, name, value);
    }

    public CvParam getParam(Object theValue) {
        return new CvParam(cv, accession, name, theValue.toString());
    }

    public uk.ac.ebi.pride.jaxb.model.CvParam getJaxbParam() {
        uk.ac.ebi.pride.jaxb.model.CvParam param = new uk.ac.ebi.pride.jaxb.model.CvParam();
        param.setAccession(accession);
        param.setCvLabel(cv);
        param.setName(name);
        param.setValue(value);

        return param;
    }

    public uk.ac.ebi.pride.jaxb.model.CvParam getJaxbParam(Object theValue) {
        uk.ac.ebi.pride.jaxb.model.CvParam param = new uk.ac.ebi.pride.jaxb.model.CvParam();
        param.setAccession(accession);
        param.setCvLabel(cv);
        param.setName(name);
        param.setValue(theValue.toString());

        return param;
    }

    public String getCv() {
        return cv;
    }

    public void setCv(String cv) {
        this.cv = cv;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getParentAccession() {
        return parentAccession;
    }

    public void setParentAccession(String parentAccession) {
        this.parentAccession = parentAccession;
    }
}
