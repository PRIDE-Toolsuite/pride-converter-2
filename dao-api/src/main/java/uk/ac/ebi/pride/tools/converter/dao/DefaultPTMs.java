package uk.ac.ebi.pride.tools.converter.dao;

/**
 * This enum holds the default database, accession and name
 * for the most commonly used PTMs and should be used by
 * the DAOs to report these.
 *
 * @author jg
 */
public enum DefaultPTMs {
    // Modification		PreferredName							Accession		Database NameDatabase Version	AVG		MONO
    // -------------------------------------------------------------------------------------
    CARBAMIDOMETHYL("iodoacetamide derivatized residue", "MOD:00397", "MOD", "1.2", 57.05, 57.021464),
    CARBOXYMETHYL("iodoacetic acid derivatized residue", "MOD:00399", "MOD", "1.2", 58.04, 58.005479),
    PHOSPHORYLATION("phosphorylated residue", "MOD:00696", "MOD", "1.2", 79.98, 79.966331),
    OXIDATION("monohydroxylated residue", "MOD:00425", "MOD", "1.2", 16.0, 15.994915),
    ACETYLATION("acetylated residue", "MOD:00394", "MOD", "1.2", 42.0367, 42.010565),
    AMIDATION("amidated residue", "MOD:00674", "MOD", "1.2", -0.98, -0.984016),
    CARBAMOYLATION("carbamoylated residue", "MOD:00398", "MOD", "1.2", 43.02, 43.005814),
    DEAMIDATION("deamidated resiude", "MOD:00400", "MOD", "1.2", 0.98, 0.984016),
    HOMOSERIN("homoserine", "MOD:00403", "MOD", "1.2", -30.09, -29.992806),
    HOMOSERIN_LACTONE("homoserine lactone", "MOD:00404", "MOD", "1.2", -48.1, -48.003371),
    NIPCAM("S-(N-isopropylcarboxamidomethyl)-L-cysteine", "MOD:00410", "MOD", "1.2", 99.13, 99.068414),
    DEHYDRATION("dehydrated residue", "MOD:00704", "MOD", "1.2", -18.02, -18.010565),
    PROPIONAMIDE("S-carboxamidoethyl-L-cysteine", "MOD:00417", "MOD", "1.2", 71.08, 71.037114),
    PYRO_CARBAMIDOMETHYL("(R)-5-oxo-1,4-tetrahydrothiazine-3-carboxylic acid", "MOD:00419", "MOD", "1.2", 40.02, 39.994915),
    PYRO_GLU_E("2-pyrrolidone-5-carboxylic acid (Glu)", "MOD:00420", "MOD", "1.2", -18.02, -18.010565),
    PYRO_GLU_Q("2-pyrrolidone-5-carboxylic acid (Gln)", "MOD:00040", "MOD", "1.2", -17.03, -17.026549),
    BIOTIN("N6-biotinyl-L-lysine", "MOD:00126", "MOD", "1.2", 226.29, 226.077599),
    METHYLATION("monomethylated residue", "MOD:00599", "MOD", "1.2", 14.03, 14.01565),
    METHYLTHIOLATION("methylthiolated residue", "MOD:01153", "MOD", "1.2", 46.09, 45.987721),
    SULFATION("sulfated residue", "MOD:00695", "MOD", "1.2", 81.06, 80.96464),
    FORMYLATION("formylated residue", "MOD:00493", "MOD", "1.2", 28.01, 27.994915),
    DIHYDROXYLATION("dihydroxylated resiude", "MOD:00428", "MOD", "1.2", 32.0, 31.989829),
    PYRIDYLETHYL("S-pyridylethyl-L-cysteine", "MOD:00424", "MOD", "1.2", 105.14, 105.057849),
    AMONIA_LOSS("deaminated residue", "MOD:01160", "MOD", "1.2", -17.03, -17.026549),
    ITRAQ_4PLEX("iTRAQ4plex-116 reporter+balance reagent acylated residue", "MOD:01499", "MOD", "1.2", 144.1, 144.102062),
    ITRAQ_8PLEX("iTRAQ8plex-113 reporter+balance reagent acylated residue", "MOD:01528", "MOD", "1.2", 304.21, 304.205359),
    TMT_6PLEX("TMT6plex-126 reporter+balance reagent acylated residue", "MOD:01720", "MOD", "1.2", 229.16, 229.162932);


    private String preferredName;
    private String accession;
    private String database;
    private String databaseVersion;
    private Double avgDelta;
    private Double monoDelta;

    private DefaultPTMs(String preferredName, String accession, String database, String databaseVersion, Double avgDelta, Double monoDelta) {
        this.preferredName = preferredName;
        this.accession = accession;
        this.database = database;
        this.databaseVersion = databaseVersion;
        this.avgDelta = avgDelta;
        this.monoDelta = monoDelta;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public String getAccession() {
        return accession;
    }

    public String getDatabase() {
        return database;
    }

    public String getDatabaseVersion() {
        return databaseVersion;
    }

    public Double getAvgDelta() {
        return avgDelta;
    }

    public Double getMonoDelta() {
        return monoDelta;
    }
}
