# Introduction #

mzIdentML is the current PSI standard for reporting proteomics results. Detailed information about mzIdentML can be found at the PSI's mzIdentML page http://www.psidev.info/index.php?q=node/453.

The **MzIdentML DAO** is based on jmzidentml. More information on jmzidentml can be found at http://code.google.com/p/jmzidentml/.

# Parameters #

  * **allow identifications only**: Allows the conversion of mzIdentML files without spectra data. Can be set to "True" or "False". The default value is "False".
  * **use weighted scoring**: As protein scores are not directly reported in mzIdentML files two different methods can be used to calculate protein from peptide scores: Either a weighted approach (default) or just adding up the peptide scores. In the weighted approach a peptide's score is evenly split across all proteins the peptide is assigned to.
  * **report all spectrum identifications**: By default only ranked one spectrum identifications are reported. If this option is set to true all spectrum identifications irrespective of their rank are reported. Mascot, for example, reports 10 peptide identifications per spectrum by default.
  * **decoy accession precursor**: If set any identification with the specified precursor in its identification will be flagged as decoy identification.
  * **peptide score accession**: Specifies the accession of the peptide score CV parameter to use to construct the protein scores. If this parameter is not set one of the available peptide scores will be randomly chosen.

# Details #

mzIdentML is considerably more complex than PRIDE XML. Therefore, several compromises have to be taken during the conversion.

## Objects converted ##

The **MzIdentML DAO** only uses object found at `/MzIdentML/SequenceCollection`. Thus, all available proteins and peptides as well as all possible assignments between the two are reported. Any protein inference information stored at `/MzIdentML/DataCollection/AnalysisData` is ignored.

## Meta-data ##

mzIdentML supports different kind of (more detailed) meta-data than PRIDE XML. Any meta-data not supported by PRIDE XML is currently being ignored and nothing is being reported by the DAO. Thus, users have to be extra careful when annotating report files generated through the **MzIdentML DAO**.

## Spectra Data ##

mzIdentML files do not contain spectra data but only refer to spectra in external peak list files. Currently, the **MzIdentML DAO** supports spectra data in the following formats:
  * ms2
  * mgf
  * pkl
  * dta
  * mzML
  * mzXML
The spectra files must not be renamed but have to have the exact same name that is being reported in the mzIdentML file. The **MzIdentML DAO** expects peak lists file to be in
  * The path specified in the mzIdentML file
  * The current working directory
  * The same directory the mzIdentML is currently in