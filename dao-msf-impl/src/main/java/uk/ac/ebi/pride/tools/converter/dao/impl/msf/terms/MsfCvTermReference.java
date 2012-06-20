package uk.ac.ebi.pride.tools.converter.dao.impl.msf.terms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import uk.ac.ebi.pride.tools.converter.report.model.CvParam;

/**
 * A direct copy of the Pride CvTermReference, can be merged if felt appropriate
 *
 * @author toorn101
 */
public enum MsfCvTermReference {

    // Activation methods
    ACTIVATION_ETD("MS", "MS:1000598", "electron transfer dissociation", "MS:1000044"),
    ACTIVATION_CID("MS", "MS:1000133", "collision-induced dissociation", "MS:1000044"),
    ACTIVATION_HCD("MS", "MS:1000422", "high-energy collision-induced dissociation", "MS:1000044"),
    ACTIVATION_PQD("MS", "MS:1000599", "pulsed q dissociation", "MS:1000044"),
    ACTIVATION_ECD("MS", "MS:1000250", "electron capture dissociation", "MS:1000044"),
    ACTIVATION_MPD("MS", "MS:1000262", "infrared multiphoton dissociation", "MS:1000044"),
    
    // Ionization
    IONIZATION_UNKNOWN("MS", "MS:1000008", "ionization dissociation", "MS:1000008"),
    IONIZATION_ELECTROSPRAY("MS", "MS:1000073", "electrospray", "MS:1000008"),
    IONIZATION_NANOSPRAY("MS", "MS:1000398", "nanoelectrospray", "MS:1000073"),
    IONIZATION_THERMOSPRAY("MS", "MS:1000598", "electron transfer dissociation", "MS:1000008"),
    IONIZATION_ELECTRONIMPACT("MS", "MS:1000389", "electron ionization", "MS:1000008"),
    IONIZATION_APCI("MS", "MS:1000070", "atmospheric pressure chemical ionization", "MS:1000008"),
    IONIZATION_MALDI("MS", "MS:1000075", "matrix-assisted laser desorption ionization", "MS:1000247"),
    IONIZATION_CHEMICAL_IONIZATION("MS", "MS:1000071", "chemical ionization", "MS:1000008"),
    IONIZATION_FAST_ATOM_BOMBARDMENT("MS", "MS:1000074", "fast atom bombardment ionization", "MS:1000008"),
    IONIZATION_FIELD_DESORPTION("MS", "MS:1000257", "field desorption", "MS:1000247"),
    IONIZATION_GLOW_DISCHARGE("MS", "MS:1000259", "glow discharge ionization", "MS:1000008"),
    
    // Mass Analyzers
    ANALYZER_UNKNOWN("MS","MS:1000443", "mass analyzer", "MS:1000451"),
    ANALYZER_ION_TRAP("MS","MS:1000264", "ion trap", "MS:1000443"),
    ANALYZER_FOURIER_TRANSFORM("MS", "MS:1000079", "fourier transform ion cyclotron resonance mass spectrometer", "MS:1000443" ),
    ANALYZER_TIME_OF_FLIGHT("MS", "MS:1000287", "time-of-flight mass spectrometer", "MS:1000443"),
    ANALYZER_SINGLE_QUAD("MS", "MS:1000081", "quadropole", "MS:1000443" ),
    ANALYZER_TRIPLE_QUAD("MS", "MS:1000081", "quadropole", "MS:1000443" ),
    ANALYZER_SECTOR_FIELD("MS", "MS:1000080", "magnetic sector", "MS:1000443" ),
    
    
    // PSI terms for selected ions
    PSI_CHARGE_STATE("PSI", "PSI:1000041", "Charge State", "PSI:1000455"),
    PSI_INTENSITY("PSI", "PSI:1000042", "Intensity", "PSI:1000455"),
    PSI_INTENSITY_UNIT("PSI", "PSI:1000043", "Intensity Unit", "PSI:1000455"),
    PSI_INTENSITY_UNIT_NUMBER_OF_COUNTS("PSI", "PSI:1000131", "Number of Counts", "PSI:1000043"),
    PSI_INTENSITY_UNIT_PERCENT_OF_BASE_PEAK("PSI", "PSI:1000132", "Percent of Base Peak", "PSI:1000043"),
    PSI_MZ_RATIO("PSI", "PSI:1000040", "Mass To Charge Ratio", "PSI:1000455"),
    
    // Protein Group Membership
    PRIDE_GROUP_MEMBER("PRIDE", "PRIDE:0000418", "Other member of protein ambiguity group", "PRIDE:0000004"),
    
    // PD parameters, currently not used.
    PD_PARAM_1_DYNAMIC_MODIFICATION("MS", "MS:1001720", "ProteomeDiscoverer:1. Dynamic Modification", "MS:1001302"),
    PD_PARAM_2_DYNAMIC_MODIFICATION("MS", "MS:1001721", "ProteomeDiscoverer:2. Dynamic Modification", "MS:1001302"),
    PD_PARAM_3_DYNAMIC_MODIFICATION("MS", "MS:1001722", "ProteomeDiscoverer:3. Dynamic Modification", "MS:1001302"),
    PD_PARAM_4_DYNAMIC_MODIFICATION("MS", "MS:1001723", "ProteomeDiscoverer:4. Dynamic Modification", "MS:1001302"),
    PD_PARAM_ABSOLUTE_XCORR_THRESHOLD("MS", "MS:1001668", "ProteomeDiscoverer:Absolute XCorr Threshold", "MS:1001302"),
    PD_PARAM_ACTIVATION_TYPE("MS", "MS:1001604", "ProteomeDiscoverer:Activation Type", "MS:1001302"),
    PD_PARAM_DYNAMIC_MODIFICATIONS("MS", "MS:1001644", "ProteomeDiscoverer:Dynamic Modifications", "MS:1001302"),
    PD_PARAM_ENZYME_NAME("MS", "MS:1001654", "ProteomeDiscoverer:Enzyme Name", "MS:1001302"),
    PD_PARAM_FRAGMENT_MASS_TOLERANCE("MS", "MS:1001655", "ProteomeDiscoverer:Fragment Mass Tolerance", "MS:1001302"),
    PD_PARAM_HIGHEST_CHARGE_STATE("MS", "MS:1001642", "ProteomeDiscoverer:Highest Charge State", "MS:1001302"),
    PD_PARAM_INITIAL_MINIMAL_PEPTIDE_PROBABILITY("MS", "MS:1001725", "ProteomeDiscoverer:Initial minimal peptide probability", "MS:1001302"),
    PD_PARAM_LOWEST_CHARGE_STATE("MS", "MS:1001641", "ProteomeDiscoverer:Lowest Charge State", "MS:1001302"),
    PD_PARAM_MASCOT_DECOY_SEARCH("MS", "MS:1001646", "ProteomeDiscoverer:Mascot:Decoy Search", "MS:1001302"),
    PD_PARAM_MASCOT_ERROR_TOLERANT_SEARCH("MS", "MS:1001647", "ProteomeDiscoverer:Mascot:Error tolerant Search", "MS:1001302"),
    PD_PARAM_MASCOT_MASCOT_SERVER_URL("MS", "MS:1001649", "ProteomeDiscoverer:Mascot:Mascot Server URL", "MS:1001302"),
    PD_PARAM_MASCOT_MAX_MGF_FILE_SIZE("MS", "MS:1001648", "ProteomeDiscoverer:Mascot:Max MGF File Size", "MS:1001302"),
    PD_PARAM_MASCOT_NUMBER_OF_ATTEMPTS_TO_SUBMIT_THE_SEARCH("MS", "MS:1001650", "ProteomeDiscoverer:Mascot:Number of attempts to submit the search", "MS:1001302"),
    PD_PARAM_MASCOT_PEPTIDE_CUTOFF_SCORE("MS", "MS:1001658", "ProteomeDiscoverer:Mascot:Peptide CutOff Score", "MS:1001302"),
    PD_PARAM_MASCOT_PLEASE_DO_NOT_TOUCH_THIS("MS", "MS:1001754", "ProteomeDiscoverer:Mascot:Please Do not Touch this", "MS:1001302"),
    PD_PARAM_MASCOT_PROTEIN_CUTOFF_SCORE("MS", "MS:1001660", "ProteomeDiscoverer:Mascot:Protein CutOff Score", "MS:1001302"),
    PD_PARAM_MASCOT_PROTEIN_RELEVANCE_FACTOR("MS", "MS:1001662", "ProteomeDiscoverer:Mascot:Protein Relevance Factor", "MS:1001302"),
    PD_PARAM_MASCOT_TAXONOMY("MS", "MS:1001665", "ProteomeDiscoverer:Mascot:Taxonomy", "MS:1001302"),
    PD_PARAM_MASCOT_TIME_INTERVAL_BETWEEN_ATTEMPTS_TO_SUBMIT_A_SEARCH("MS", "MS:1001653", "ProteomeDiscoverer:Mascot:Time interval between attempts to submit a search", "MS:1001302"),
    PD_PARAM_MASCOT_USER_NAME("MS", "MS:1001652", "ProteomeDiscoverer:Mascot:User Name", "MS:1001302"),
    PD_PARAM_MASCOT_WEIGHT_OF_A_IONS("MS", "MS:1001743", "ProteomeDiscoverer:Mascot:Weight of A Ions", "MS:1001302"),
    PD_PARAM_MASCOT_WEIGHT_OF_B_IONS("MS", "MS:1001744", "ProteomeDiscoverer:Mascot:Weight of B Ions", "MS:1001302"),
    PD_PARAM_MASCOT_WEIGHT_OF_C_IONS("MS", "MS:1001745", "ProteomeDiscoverer:Mascot:Weight of C Ions", "MS:1001302"),
    PD_PARAM_MASCOT_WEIGHT_OF_D_IONS("MS", "MS:1001746", "ProteomeDiscoverer:Mascot:Weight of D Ions", "MS:1001302"),
    PD_PARAM_MASCOT_WEIGHT_OF_V_IONS("MS", "MS:1001747", "ProteomeDiscoverer:Mascot:Weight of V Ions", "MS:1001302"),
    PD_PARAM_MASCOT_WEIGHT_OF_W_IONS("MS", "MS:1001748", "ProteomeDiscoverer:Mascot:Weight of W Ions", "MS:1001302"),
    PD_PARAM_MASCOT_WEIGHT_OF_X_IONS("MS", "MS:1001749", "ProteomeDiscoverer:Mascot:Weight of X Ions", "MS:1001302"),
    PD_PARAM_MASCOT_WEIGHT_OF_Y_IONS("MS", "MS:1001750", "ProteomeDiscoverer:Mascot:Weight of Y Ions", "MS:1001302"),
    PD_PARAM_MASCOT_WEIGHT_OF_Z_IONS("MS", "MS:1001751", "ProteomeDiscoverer:Mascot:Weight of Z Ions", "MS:1001302"),
    PD_PARAM_MASCOT_X_STATIC_MODIFICATION("MS", "MS:1001651", "ProteomeDiscoverer:Mascot:X Static Modification", "MS:1001302"),
    PD_PARAM_MASS_ANALYZER("MS", "MS:1001606", "ProteomeDiscoverer:Mass Analyzer", "MS:1001302"),
    PD_PARAM_MAXIMUM_MISSED_CLEAVAGE_SITES("MS", "MS:1001657", "ProteomeDiscoverer:Maximum Missed Cleavage Sites", "MS:1001302"),
    PD_PARAM_MAXIMUM_PEPTIDES_OUTPUT("MS", "MS:1001675", "ProteomeDiscoverer:Maximum Peptides Output", "MS:1001302"),
    PD_PARAM_MAXIMUM_PROTEIN_REFERENCES_PER_PEPTIDE("MS", "MS:1001676", "ProteomeDiscoverer:Maximum Protein References Per Peptide", "MS:1001302"),
    PD_PARAM_MAX_MODIFICATIONS_PER_PEPTIDE("MS", "MS:1001673", "ProteomeDiscoverer:Max Modifications Per Peptide", "MS:1001302"),
    PD_PARAM_MAX_PRECURSOR_MASS("MS", "MS:1001607", "ProteomeDiscoverer:Max Precursor Mass", "MS:1001302"),
    PD_PARAM_MINIMAL_PEPTIDE_PROBABILITY("MS", "MS:1001726", "ProteomeDiscoverer:Minimal peptide probability", "MS:1001302"),
    PD_PARAM_MINIMAL_PEPTIDE_WEIGHT("MS", "MS:1001727", "ProteomeDiscoverer:Minimal peptide weight", "MS:1001302"),
    PD_PARAM_MINIMUM_PEAK_COUNT("MS", "MS:1001609", "ProteomeDiscoverer:Minimum Peak Count", "MS:1001302"),
    PD_PARAM_MIN_PRECURSOR_MASS("MS", "MS:1001608", "ProteomeDiscoverer:Min Precursor Mass", "MS:1001302"),
    PD_PARAM_MS_ORDER("MS", "MS:1001610", "ProteomeDiscoverer:MS Order", "MS:1001302"),
    PD_PARAM_NON_FRAGMENT_FILTER_MASS_WINDOW_OFFSET("MS", "MS:1001622", "ProteomeDiscoverer:Non-Fragment Filter:Mass Window Offset", "MS:1001302"),
    PD_PARAM_NON_FRAGMENT_FILTER_MAXIMUM_NEUTRAL_LOSS_MASS("MS", "MS:1001623", "ProteomeDiscoverer:Non-Fragment Filter:Maximum Neutral Loss Mass", "MS:1001302"),
    PD_PARAM_NON_FRAGMENT_FILTER_REMOVE_CHARGE_REDUCED_PRECURSOR("MS", "MS:1001624", "ProteomeDiscoverer:Non-Fragment Filter:Remove Charge Reduced Precursor", "MS:1001302"),
    PD_PARAM_NON_FRAGMENT_FILTER_REMOVE_NEUTRAL_LOSS_PEAKS("MS", "MS:1001625", "ProteomeDiscoverer:Non-Fragment Filter:Remove Neutral Loss Peaks", "MS:1001302"),
    PD_PARAM_NON_FRAGMENT_FILTER_REMOVE_ONLY_KNOWN_MASSES("MS", "MS:1001626", "ProteomeDiscoverer:Non-Fragment Filter:Remove Only Known Masses", "MS:1001302"),
    PD_PARAM_NON_FRAGMENT_FILTER_REMOVE_PRECURSOR_OVERTONES("MS", "MS:1001627", "ProteomeDiscoverer:Non-Fragment Filter:Remove Precursor Overtones", "MS:1001302"),
    PD_PARAM_NON_FRAGMENT_FILTER_REMOVE_PRECURSOR_PEAK("MS", "MS:1001628", "ProteomeDiscoverer:Non-Fragment Filter:Remove Precursor Peak", "MS:1001302"),
    PD_PARAM_NUMBER_OF_INPUT1_SPECTRA("MS", "MS:1001728", "ProteomeDiscoverer:Number of input1 spectra", "MS:1001302"),
    PD_PARAM_NUMBER_OF_INPUT2_SPECTRA("MS", "MS:1001729", "ProteomeDiscoverer:Number of input2 spectra", "MS:1001302"),
    PD_PARAM_NUMBER_OF_INPUT3_SPECTRA("MS", "MS:1001730", "ProteomeDiscoverer:Number of input3 spectra", "MS:1001302"),
    PD_PARAM_NUMBER_OF_INPUT4_SPECTRA("MS", "MS:1001731", "ProteomeDiscoverer:Number of input4 spectra", "MS:1001302"),
    PD_PARAM_NUMBER_OF_INPUT5_SPECTRA("MS", "MS:1001732", "ProteomeDiscoverer:Number of input5 spectra", "MS:1001302"),
    PD_PARAM_NUMBER_OF_PREDICTED_CORRECT_PROTEINS("MS", "MS:1001733", "ProteomeDiscoverer:Number of predicted correct proteins", "MS:1001302"),
    PD_PARAM_ORGANISM("MS", "MS:1001734", "ProteomeDiscoverer:Organism", "MS:1001302"),
    PD_PARAM_PEPTIDE_CTERMINUS("MS", "MS:1001678", "ProteomeDiscoverer:Peptide CTerminus", "MS:1001302"),
    PD_PARAM_PEPTIDE_NTERMINUS("MS", "MS:1001679", "ProteomeDiscoverer:Peptide NTerminus", "MS:1001302"),
    PD_PARAM_POLARITY_MODE("MS", "MS:1001611", "ProteomeDiscoverer:Polarity Mode", "MS:1001302"),
    PD_PARAM_PRECURSOR_MASS_TOLERANCE("MS", "MS:1001659", "ProteomeDiscoverer:Precursor Mass Tolerance", "MS:1001302"),
    PD_PARAM_PROTEIN_DATABASE("MS", "MS:1001661", "ProteomeDiscoverer:Protein Database", "MS:1001302"),
    PD_PARAM_PROTEIN_RELEVANCE_THRESHOLD("MS", "MS:1001681", "ProteomeDiscoverer:Protein Relevance Threshold", "MS:1001302"),
    PD_PARAM_REFERENCE_DATABASE("MS", "MS:1001735", "ProteomeDiscoverer:Reference Database", "MS:1001302"),
    PD_PARAM_REPORTER_IONS_QUANTIZER_INTEGRATION_METHOD("MS", "MS:1001698", "ProteomeDiscoverer:Reporter Ions Quantizer:Integration Method", "MS:1001302"),
    PD_PARAM_REPORTER_IONS_QUANTIZER_INTEGRATION_WINDOW_TOLERANCE("MS", "MS:1001699", "ProteomeDiscoverer:Reporter Ions Quantizer:Integration Window Tolerance", "MS:1001302"),
    PD_PARAM_REPORTER_IONS_QUANTIZER_QUANTITATION_METHOD("MS", "MS:1001700", "ProteomeDiscoverer:Reporter Ions Quantizer:Quantitation Method", "MS:1001302"),
    PD_PARAM_RESIDUE_SUBSTITUTION_LIST("MS", "MS:1001736", "ProteomeDiscoverer:Residue substitution list", "MS:1001302"),
    PD_PARAM_SCAN_TYPE("MS", "MS:1001614", "ProteomeDiscoverer:Scan Type", "MS:1001302"),
    PD_PARAM_SEARCH_AGAINST_DECOY_DATABASE("MS", "MS:1001682", "ProteomeDiscoverer:Search Against Decoy Database", "MS:1001302"),
    PD_PARAM_SEARCH_MODIFICATIONS_ONLY_FOR_IDENTIFIED_PROTEINS("MS", "MS:1001703", "ProteomeDiscoverer:Search Modifications Only For Identified Proteins", "MS:1001302"),
    PD_PARAM_SEQUEST_CALCULATE_PROBABILITY_SCORE("MS", "MS:1001669", "ProteomeDiscoverer:SEQUEST:Calculate Probability Score", "MS:1001302"),
    PD_PARAM_SEQUEST_CTERMINAL_MODIFICATION("MS", "MS:1001670", "ProteomeDiscoverer:SEQUEST:CTerminal Modification", "MS:1001302"),
    PD_PARAM_SEQUEST_FRAGMENT_ION_CUTOFF_PERCENTAGE("MS", "MS:1001671", "ProteomeDiscoverer:SEQUEST:Fragment Ion Cutoff Percentage", "MS:1001302"),
    PD_PARAM_SEQUEST_FT_HIGH_CONFIDENCE_XCORR_CHARGE1("MS", "MS:1001712", "ProteomeDiscoverer:SEQUEST:FT High Confidence XCorr Charge1", "MS:1001302"),
    PD_PARAM_SEQUEST_FT_HIGH_CONFIDENCE_XCORR_CHARGE2("MS", "MS:1001713", "ProteomeDiscoverer:SEQUEST:FT High Confidence XCorr Charge2", "MS:1001302"),
    PD_PARAM_SEQUEST_FT_HIGH_CONFIDENCE_XCORR_CHARGE3("MS", "MS:1001714", "ProteomeDiscoverer:SEQUEST:FT High Confidence XCorr Charge3", "MS:1001302"),
    PD_PARAM_SEQUEST_FT_HIGH_CONFIDENCE_XCORR_CHARGE4("MS", "MS:1001715", "ProteomeDiscoverer:SEQUEST:FT High Confidence XCorr Charge4", "MS:1001302"),
    PD_PARAM_SEQUEST_FT_MEDIUM_CONFIDENCE_XCORR_CHARGE1("MS", "MS:1001716", "ProteomeDiscoverer:SEQUEST:FT Medium Confidence XCorr Charge1", "MS:1001302"),
    PD_PARAM_SEQUEST_FT_MEDIUM_CONFIDENCE_XCORR_CHARGE2("MS", "MS:1001717", "ProteomeDiscoverer:SEQUEST:FT Medium Confidence XCorr Charge2", "MS:1001302"),
    PD_PARAM_SEQUEST_FT_MEDIUM_CONFIDENCE_XCORR_CHARGE3("MS", "MS:1001718", "ProteomeDiscoverer:SEQUEST:FT Medium Confidence XCorr Charge3", "MS:1001302"),
    PD_PARAM_SEQUEST_FT_MEDIUM_CONFIDENCE_XCORR_CHARGE4("MS", "MS:1001719", "ProteomeDiscoverer:SEQUEST:FT Medium Confidence XCorr Charge4", "MS:1001302"),
    PD_PARAM_SEQUEST_MAXIMUM_PEPTIDES_CONSIDERED("MS", "MS:1001674", "ProteomeDiscoverer:SEQUEST:Maximum Peptides Considered", "MS:1001302"),
    PD_PARAM_SEQUEST_MAX_IDENTICAL_MODIFICATIONS_PER_PEPTIDE("MS", "MS:1001672", "ProteomeDiscoverer:SEQUEST:Max Identical Modifications Per Peptide", "MS:1001302"),
    PD_PARAM_SEQUEST_NTERMINAL_MODIFICATION("MS", "MS:1001677", "ProteomeDiscoverer:SEQUEST:NTerminal Modification", "MS:1001302"),
    PD_PARAM_SEQUEST_PEPTIDE_RELEVANCE_FACTOR("MS", "MS:1001680", "ProteomeDiscoverer:SEQUEST:Peptide Relevance Factor", "MS:1001302"),
    PD_PARAM_SEQUEST_STD_HIGH_CONFIDENCE_XCORR_CHARGE1("MS", "MS:1001704", "ProteomeDiscoverer:SEQUEST:Std High Confidence XCorr Charge1", "MS:1001302"),
    PD_PARAM_SEQUEST_STD_HIGH_CONFIDENCE_XCORR_CHARGE2("MS", "MS:1001705", "ProteomeDiscoverer:SEQUEST:Std High Confidence XCorr Charge2", "MS:1001302"),
    PD_PARAM_SEQUEST_STD_HIGH_CONFIDENCE_XCORR_CHARGE3("MS", "MS:1001706", "ProteomeDiscoverer:SEQUEST:Std High Confidence XCorr Charge3", "MS:1001302"),
    PD_PARAM_SEQUEST_STD_HIGH_CONFIDENCE_XCORR_CHARGE4("MS", "MS:1001707", "ProteomeDiscoverer:SEQUEST:Std High Confidence XCorr Charge4", "MS:1001302"),
    PD_PARAM_SEQUEST_STD_MEDIUM_CONFIDENCE_XCORR_CHARGE1("MS", "MS:1001708", "ProteomeDiscoverer:SEQUEST:Std Medium Confidence XCorr Charge1", "MS:1001302"),
    PD_PARAM_SEQUEST_STD_MEDIUM_CONFIDENCE_XCORR_CHARGE2("MS", "MS:1001709", "ProteomeDiscoverer:SEQUEST:Std Medium Confidence XCorr Charge2", "MS:1001302"),
    PD_PARAM_SEQUEST_STD_MEDIUM_CONFIDENCE_XCORR_CHARGE3("MS", "MS:1001710", "ProteomeDiscoverer:SEQUEST:Std Medium Confidence XCorr Charge3", "MS:1001302"),
    PD_PARAM_SEQUEST_STD_MEDIUM_CONFIDENCE_XCORR_CHARGE4("MS", "MS:1001711", "ProteomeDiscoverer:SEQUEST:Std Medium Confidence XCorr Charge4", "MS:1001302"),
    PD_PARAM_SEQUEST_USE_AVERAGE_FRAGMENT_MASSES("MS", "MS:1001683", "ProteomeDiscoverer:SEQUEST:Use Average Fragment Masses", "MS:1001302"),
    PD_PARAM_SEQUEST_WEIGHT_OF_A_IONS("MS", "MS:1001688", "ProteomeDiscoverer:SEQUEST:Weight of a Ions", "MS:1001302"),
    PD_PARAM_SEQUEST_WEIGHT_OF_B_IONS("MS", "MS:1001689", "ProteomeDiscoverer:SEQUEST:Weight of b Ions", "MS:1001302"),
    PD_PARAM_SEQUEST_WEIGHT_OF_C_IONS("MS", "MS:1001690", "ProteomeDiscoverer:SEQUEST:Weight of c Ions", "MS:1001302"),
    PD_PARAM_SEQUEST_WEIGHT_OF_D_IONS("MS", "MS:1001691", "ProteomeDiscoverer:SEQUEST:Weight of d Ions", "MS:1001302"),
    PD_PARAM_SEQUEST_WEIGHT_OF_V_IONS("MS", "MS:1001692", "ProteomeDiscoverer:SEQUEST:Weight of v Ions", "MS:1001302"),
    PD_PARAM_SEQUEST_WEIGHT_OF_W_IONS("MS", "MS:1001693", "ProteomeDiscoverer:SEQUEST:Weight of w Ions", "MS:1001302"),
    PD_PARAM_SEQUEST_WEIGHT_OF_X_IONS("MS", "MS:1001694", "ProteomeDiscoverer:SEQUEST:Weight of x Ions", "MS:1001302"),
    PD_PARAM_SEQUEST_WEIGHT_OF_Y_IONS("MS", "MS:1001695", "ProteomeDiscoverer:SEQUEST:Weight of y Ions", "MS:1001302"),
    PD_PARAM_SEQUEST_WEIGHT_OF_Z_IONS("MS", "MS:1001696", "ProteomeDiscoverer:SEQUEST:Weight of z Ions", "MS:1001302"),
    PD_PARAM_SN_THRESHOLD("MS", "MS:1001613", "ProteomeDiscoverer:SN Threshold", "MS:1001302"),
    PD_PARAM_SOURCE_FILES("MS", "MS:1001738", "ProteomeDiscoverer:Source Files", "MS:1001302"),
    PD_PARAM_SOURCE_FILES_OLD("MS", "MS:1001739", "ProteomeDiscoverer:Source Files old", "MS:1001302"),
    PD_PARAM_SOURCE_FILE_EXTENSION("MS", "MS:1001737", "ProteomeDiscoverer:Source file extension", "MS:1001302"),
    PD_PARAM_SPECTRUM_EXPORTER_EXPORT_FORMAT("MS", "MS:1001701", "ProteomeDiscoverer:Spectrum Exporter:Export Format", "MS:1001302"),
    PD_PARAM_SPECTRUM_EXPORTER_FILE_NAME("MS", "MS:1001702", "ProteomeDiscoverer:Spectrum Exporter:File name", "MS:1001302"),
    PD_PARAM_SPECTRUM_FILES_RAW_FILE_NAMES("MS", "MS:1001601", "ProteomeDiscoverer:Spectrum Files:Raw File names", "MS:1001302"),
    PD_PARAM_SPECTRUM_GROUPER_ALLOW_MASS_ANALYZER_MISMATCH("MS", "MS:1001629", "ProteomeDiscoverer:Spectrum Grouper:Allow Mass Analyzer Mismatch", "MS:1001302"),
    PD_PARAM_SPECTRUM_GROUPER_ALLOW_MS_ORDER_MISMATCH("MS", "MS:1001630", "ProteomeDiscoverer:Spectrum Grouper:Allow MS Order Mismatch", "MS:1001302"),
    PD_PARAM_SPECTRUM_GROUPER_MAX_RT_DIFFERENCE("MS", "MS:1001631", "ProteomeDiscoverer:Spectrum Grouper:Max RT Difference", "MS:1001302"),
    PD_PARAM_SPECTRUM_GROUPER_PRECURSOR_MASS_CRITERION("MS", "MS:1001632", "ProteomeDiscoverer:Spectrum Grouper:Precursor Mass Criterion", "MS:1001302"),
    PD_PARAM_SPECTRUM_SCORE_FILTER_LET_PASS_ABOVE_SCORES("MS", "MS:1001643", "ProteomeDiscoverer:Spectrum Score Filter:Let Pass Above Scores", "MS:1001302"),
    PD_PARAM_SPECTRUM_SELECTOR_IONIZATION_SOURCE("MS", "MS:1001603", "ProteomeDiscoverer:Spectrum Selector:Ionization Source", "MS:1001302"),
    PD_PARAM_SPECTRUM_SELECTOR_LOWER_RT_LIMIT("MS", "MS:1001605", "ProteomeDiscoverer:Spectrum Selector:Lower RT Limit", "MS:1001302"),
    PD_PARAM_SPECTRUM_SELECTOR_PRECURSOR_SELECTION("MS", "MS:1001612", "ProteomeDiscoverer:Spectrum Selector:Precursor Selection", "MS:1001302"),
    PD_PARAM_SPECTRUM_SELECTOR_SN_THRESHOLD_FTONLY("MS", "MS:1001753", "ProteomeDiscoverer:Spectrum Selector:SN Threshold FTonly", "MS:1001302"),
    PD_PARAM_SPECTRUM_SELECTOR_UNRECOGNIZED_ACTIVATION_TYPE_REPLACEMENTS("MS", "MS:1001616", "ProteomeDiscoverer:Spectrum Selector:Unrecognized Activation Type Replacements", "MS:1001302"),
    PD_PARAM_SPECTRUM_SELECTOR_UNRECOGNIZED_CHARGE_REPLACEMENTS("MS", "MS:1001617", "ProteomeDiscoverer:Spectrum Selector:Unrecognized Charge Replacements", "MS:1001302"),
    PD_PARAM_SPECTRUM_SELECTOR_UNRECOGNIZED_MASS_ANALYZER_REPLACEMENTS("MS", "MS:1001618", "ProteomeDiscoverer:Spectrum Selector:Unrecognized Mass Analyzer Replacements", "MS:1001302"),
    PD_PARAM_SPECTRUM_SELECTOR_UNRECOGNIZED_MS_ORDER_REPLACEMENTS("MS", "MS:1001619", "ProteomeDiscoverer:Spectrum Selector:Unrecognized MS Order Replacements", "MS:1001302"),
    PD_PARAM_SPECTRUM_SELECTOR_UNRECOGNIZED_POLARITY_REPLACEMENTS("MS", "MS:1001620", "ProteomeDiscoverer:Spectrum Selector:Unrecognized Polarity Replacements", "MS:1001302"),
    PD_PARAM_SPECTRUM_SELECTOR_UPPER_RT_LIMIT("MS", "MS:1001621", "ProteomeDiscoverer:Spectrum Selector:Upper RT Limit", "MS:1001302"),
    PD_PARAM_SPECTRUM_SELECTOR_USE_NEW_PRECURSOR_REEVALUATION("MS", "MS:1001752", "ProteomeDiscoverer:Spectrum Selector:Use New Precursor Reevaluation", "MS:1001302"),
    PD_PARAM_SRF_FILE_SELECTOR_SRF_FILE_PATH("MS", "MS:1001602", "ProteomeDiscoverer:SRF File Selector:SRF File Path", "MS:1001302"),
    PD_PARAM_STATIC_MODIFICATIONS("MS", "MS:1001645", "ProteomeDiscoverer:Static Modifications", "MS:1001302"),
    PD_PARAM_STATIC_MODIFICATION_FOR_X("MS", "MS:1001724", "ProteomeDiscoverer:Static Modification for X", "MS:1001302"),
    PD_PARAM_TARGET_FDR_RELAXED("MS", "MS:1001663", "ProteomeDiscoverer:Target FDR Relaxed", "MS:1001302"),
    PD_PARAM_TARGET_FDR_STRICT("MS", "MS:1001664", "ProteomeDiscoverer:Target FDR Strict", "MS:1001302"),
    PD_PARAM_TOTAL_INTENSITY_THRESHOLD("MS", "MS:1001615", "ProteomeDiscoverer:Total Intensity Threshold", "MS:1001302"),
    PD_PARAM_USE_AVERAGE_PRECURSOR_MASS("MS", "MS:1001666", "ProteomeDiscoverer:Use Average Precursor Mass", "MS:1001302"),
    PD_PARAM_USE_NEUTRAL_LOSS_A_IONS("MS", "MS:1001684", "ProteomeDiscoverer:Use Neutral Loss a Ions", "MS:1001302"),
    PD_PARAM_USE_NEUTRAL_LOSS_B_IONS("MS", "MS:1001685", "ProteomeDiscoverer:Use Neutral Loss b Ions", "MS:1001302"),
    PD_PARAM_USE_NEUTRAL_LOSS_Y_IONS("MS", "MS:1001686", "ProteomeDiscoverer:Use Neutral Loss y Ions", "MS:1001302"),
    PD_PARAM_USE_NEUTRAL_LOSS_Z_IONS("MS", "MS:1001687", "ProteomeDiscoverer:Use Neutral Loss z Ions", "MS:1001302"),
    PD_PARAM_WINCYG_REFERENCE_DATABASE("MS", "MS:1001740", "ProteomeDiscoverer:WinCyg reference database", "MS:1001302"),
    PD_PARAM_WINCYG_SOURCE_FILES("MS", "MS:1001741", "ProteomeDiscoverer:WinCyg source files", "MS:1001302"),
    PD_PARAM_XTRACT_HIGHEST_CHARGE("MS", "MS:1001633", "ProteomeDiscoverer:Xtract:Highest Charge", "MS:1001302"),
    PD_PARAM_XTRACT_HIGHEST_MZ("MS", "MS:1001634", "ProteomeDiscoverer:Xtract:Highest MZ", "MS:1001302"),
    PD_PARAM_XTRACT_LOWEST_CHARGE("MS", "MS:1001635", "ProteomeDiscoverer:Xtract:Lowest Charge", "MS:1001302"),
    PD_PARAM_XTRACT_LOWEST_MZ("MS", "MS:1001636", "ProteomeDiscoverer:Xtract:Lowest MZ", "MS:1001302"),
    PD_PARAM_XTRACT_MONOISOTOPIC_MASS_ONLY("MS", "MS:1001637", "ProteomeDiscoverer:Xtract:Monoisotopic Mass Only", "MS:1001302"),
    PD_PARAM_XTRACT_OVERLAPPING_REMAINDER("MS", "MS:1001638", "ProteomeDiscoverer:Xtract:Overlapping Remainder", "MS:1001302"),
    PD_PARAM_XTRACT_REQUIRED_FITTING_ACCURACY("MS", "MS:1001639", "ProteomeDiscoverer:Xtract:Required Fitting Accuracy", "MS:1001302"),
    PD_PARAM_XTRACT_RESOLUTION_AT_400("MS", "MS:1001640", "ProteomeDiscoverer:Xtract:Resolution At 400", "MS:1001302"),
    PD_PARAM_ZCORE_PROTEIN_SCORE_CUTOFF("MS", "MS:1001697", "ProteomeDiscoverer:ZCore:Protein Score Cutoff", "MS:1001302"),
    PD_PHOSPHORS_SCORE("MS", "MS:1001969", "ProteomeDiscoverer:phosphoRS score", "MS:1001968"),
    PD_PHOSPHORS_SEQUENCE_PROBABILITY("MS", "MS:1001970", "ProteomeDiscoverer:phosphoRS sequence probability", "MS:1001968"),
    PD_PHOSPHORS_SITE_PROBABILITY("MS", "MS:1001971", "ProteomeDiscoverer:phosphoRS site probability", "MS:1001968"),
    RAW_DATA_FILE("MS", "MS:1000577", "raw data file", "MS:1001458");
    
    private final String cvLabel;
    private final String accession;
    private final String name;
    private final String parentAccession;

    private MsfCvTermReference(String cvLabel, String accession, String name, String parentAccession) {
        this.cvLabel = cvLabel;
        this.accession = accession;
        this.name = name;
        this.parentAccession = parentAccession;
    }

    public String getCvLabel() {
        return cvLabel;
    }

    public String getAccession() {
        return accession;
    }

    public String getName() {
        return name;
    }

    public Collection<String> getChildAccessions() {
        Collection<String> results = new ArrayList<String>();
        MsfCvTermReference[] cvTerms = values();
        for (MsfCvTermReference cv : cvTerms) {
            if (cv.getParentAccessions().contains(accession)) {
                results.add(cv.getAccession());
            }
        }
        return results;
    }
    
    public CvParam getCvParam(String value) {
        CvParam param = new CvParam();
        
        param.setAccession(accession);
        param.setCvLabel(cvLabel);
        param.setName(name);
        param.setValue(value);
        
        return param;
    }
    
    public uk.ac.ebi.pride.jaxb.model.CvParam getJaxbParam(String value) {
        return reportToJaxb(this.getCvParam(value));
    }
    
    public Collection<String> getParentAccessions() {
        return Arrays.asList(parentAccession.split(";"));
    }

    /**
     * Get Cv term by accession.
     *
     * @param accession controlled vocabulary accession.
     * @return CvTermReference Cv term.
     */
    public static MsfCvTermReference getCvRefByAccession(String accession) {
        MsfCvTermReference cvTerm = null;

        MsfCvTermReference[] cvTerms = MsfCvTermReference.values();
        for (MsfCvTermReference cv : cvTerms) {
            if (cv.getAccession().equals(accession)) {
                cvTerm = cv;
            }
        }

        return cvTerm;
    }

    
    /**
     * Check whether the accession exists in the enum.
     *
     * @param accession controlled vocabulary accession
     * @return boolean true if exists
     */
    public static boolean hasAccession(String accession) {
        boolean result = false;

        MsfCvTermReference[] cvTerms = MsfCvTermReference.values();
        for (MsfCvTermReference cv : cvTerms) {
            if (cv.getAccession().equals(accession)) {
                result = true;
            }
        }

        return result;
    }
    
    /**
     * Check whether two accessions are parent-child relationship.
     *
     * @param parentAcc parent accession.
     * @param childAcc child accession.
     * @return boolean true if it is parent-child relationship.
     */
    public static boolean isChild(String parentAcc, String childAcc) {
        boolean isChild = false;
        MsfCvTermReference childCvTerm = getCvRefByAccession(childAcc);
        if (childCvTerm != null && childCvTerm.getParentAccessions().contains(parentAcc)) {
            isChild = true;
        }
        return isChild;
    }
    
    /**
     * Convert from cvParam types from report type to jaxb type
     * @param reportCvParam
     * @return 
     */
    private static uk.ac.ebi.pride.jaxb.model.CvParam reportToJaxb(CvParam reportCvParam) {
        uk.ac.ebi.pride.jaxb.model.CvParam convertedParam = new uk.ac.ebi.pride.jaxb.model.CvParam();
        convertedParam.setAccession(reportCvParam.getAccession());
        convertedParam.setCvLabel(reportCvParam.getCvLabel());
        convertedParam.setName(reportCvParam.getName());
        convertedParam.setValue(reportCvParam.getValue());
        
        return convertedParam;
    }



}
