# Introduction #

mzXML is the peak list format used by the Trans Proteomics Pipeline (TPP). Detailed information about the mzXML format can be found at http://tools.proteomecenter.org/wiki/index.php?title=Formats:mzXML.

The **MzXML DAO** is based on the mzxml-parser developed as part of the [jmzReader library](http://jmzreader.googlecode.com).

# Parameters #

The **MzXML DAO** does not support any parameters.

# Details #

mzXML files can contain nested spectra - run objects as children of run objects. The mzxml-parser does support these nested spectra. For the conversion to PRIDE XML only one level of runs is supported. In cases where this feature is used this generally refers to MS 2 spectra. Thus, currently any run objects that are children of children of run objects are being ignored by the **MzXML DAO**. Or in other words: the **MzXML DAO** is expected to only support (nested) spectra up to MS 2.