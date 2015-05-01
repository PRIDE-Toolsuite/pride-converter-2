# Introduction #

The PRIDE Converter 2 **Mascot DAO** is build based on the _Mascot Parser_ from Matrix Science (http://www.matrixscience.com/msparser.html). A complete documentation of the _Mascot Parser_ library can be found at http://www.matrixscience.com/parser_support.html.

# Parameters #

The parameters taken by the **Mascot DAO** are very similar / identical to the ones used in the actual Mascot search.

**Min Probability**: Specifies a cut-off point for peptide scores, a cut-off for an integrated error tolerant search and a threshold for calculating MudPIT scores. This value represents a probability threshold. All peptides below this set score are labelled _non-significant results_.

**Ignore Below Ion Score**: Peptides with a lower expect ratio (of being false positives) will be ignored completely. Set to 1 to deactivate.

**Decoy Accession Prefix**: An accession prefix that identifies decoy hits. Every protein with an accession starting with this prefix will be flagged as decoy hit. Furthermore, any decoy hit who's accession does not start with this prefix will be altered accordingly.

**Use MudPIT Scoring**: Indicates whether MudPIT or normal scoring should be used.

**Only significant**: Indicates whether only significant peptides / (in PMF searches) proteins should be included in the generated PRIDE file. Significant identifications are determined using the set **Min Probability** on the _Identity Threshold_. There is no clear suggestion whether only the _Identity Threshold_ or also the _Homology Threshold_ should be used.

**Remove duplicate same query**: Indicates whether duplicate peptides having the same sequence and coming from the same query (= spectrum) should be removed. These peptides may have different modifications reported.

**Remove duplicate different query**: Indicates whether duplicate peptides having the same sequence (but maybe different modifications) coming from different queries (= spectra) should be removed.

**Compatibility mode**: If set to true (default) the precuror charge will also be reported at the spectrum level using the best ranked peptide's charge state. This might lead to wrong precursor charges being reported. The correct charge state is always additionally reported at the peptide level.

**Include error tolerant**: Indicates whether integrated error tolerant search results should be included in the PRIDE XML support. These results are not included in the protein scores by Mascot.

**Enable protein grouping**: Indicates whether the grouping mode (Occam's Razor, see Mascot documentation) should be enabled. This is the default behaviour for Mascot. This mode is not equivalent to the protein clustering introduced in Mascot 2.3.

**Remove empty spectra**: If set to true (default) spectra without any peaks are ignored and not reported in the PRIDE XML file.

# Details #

To convert Mascot results into PRIDE XML files several compromises have to be taken:
  * **Ion Series**: Mascot supports the possibility to query spectra where the ion series (b, y, rest) are separated beforehand. These cases are currently not supported by the **Mascot DAO** and only ionSeries 1 (as recommende in the _Mascot Parser_ documentation) is taken into consideration. This should work fine for 99% of cases.
  * **Precursor Charge States**: There is currently only one precursor supported per spectrum. Furthermore, as Mascot can report multiple peptides per spectrum the **Mascot DAO** only reports charge states at the peptide level and NOT at the precursor level.
  * **Unsupported PRIDE XML objects**: The following objects are currently not supported (and thus not returned) by the **Mascot DAO**:
    * Activation parameter
    * Spectrum acquisition parameters
  * **Error tolerant searches**: The **Mascot DAO** currently only supports integrated error tolerant searches. Separate error tolerant searches are not recommended by Matrix Science and thus are not supported.
  * **Quantitation Methods**: Quantitation methods are not supported by the **Mascot DAO** directly. As defined in the [PRIDE Converter 2 introduction](PrideConverter2.md) it is possible to report quantitative values using additional mzTab files.
  * **PMF**: In PMF searches all queries are reported as 1 MS1 spectrum. In case no intensities are supplied to the search all peak intensities are set to 1. The spectra then all point to the same one spectrum. Additionally, every peptide contains one additional m/z parameter storing the m/z value the peptide was identified from.
  * **Protein families**: Protein families (available from Mascot version >= 2.3) cannot be reported in PRIDE XML files. Therefore, the here presented results correspond to the results seen in the older "peptide summary" view.
  * **Protein scores in MudPIT experiments**: For several reasons when using MudPIT scoring, proteins with only one peptide can have a lower score than the threshold while still being deemed significant identifications. this is caused by the fact that the protein thresholds have to be determined by using the average peptide threshold in the file (as recommended in the _Mascot Parser_ documentation).