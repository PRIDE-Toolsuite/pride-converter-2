# Introduction #

PRIDE Converter 2 is able to include gel-based data in PRIDE XML files. Currently, two additional pieces of information can be stored per protein in a PRIDE XML file: the **gel identifier** and the **gel spot identifier**. Both of these fields should resemble the names used in the corresponding publication (f.e. "Gel 1", "Spot A", etc.).

Protein identifications are linked to given gel spots through mzTab files. A detailed documentation about the mzTab format can be found [here](http://mztab.googelcode.com).

PRIDE Converter 2 is able to automatically generate the required mzTab files in most cases.

# Generating mzTab files #

## Using the Command Line ##

Given the following example where Mascot was used and a single spot is represented through one result file:
```
Filename            Gel      Spot
---------------------------------------
GelA_Spot1.dat      A         1
GelA_Spot2.dat      A         2
```

Open a command line and navigate to the directory where the result files are stored. Then execute PRIDE Converter 2 with the following options:
```
java -jar /path/to/pride_converter.jar -converter -engine mascot -sourcefile GelA_Spot1.dat -mode mztab -gel_identifier "Gel A" -gel_spot_regex "Gel[AB]_Spot([0-9]+).*"

Alterantive:
java -jar /path/to/pride_converter.jar -converter -engine mascot -sourcefile GelA_Spot1.dat -mode mztab -gel_identifier "Gel A" -gel_spot_identifier "Spot 1"
```

This command will create an mzTab file containing all protein identifications found in the supplied Mascot .dat file and saves it under "GelA\_Spot1.dat-mztab".

The used options are as follows:
  * **-engine**: The search engine used. Supported values are "mascot", "xtandem", "mzidentml", "msgf".
  * **-mode**: Defines the mode PRIDE Converter should run in. To generate mzTab files this has to be set to "mztab".
  * **-gel\_identifier**: Defines the (static) gel identifier to be used during this conversion
  * **-gel\_spot\_regex**: A regular expression used to extract the gel spot's identifier from the input filename. As an alternative it is also possible to supply a static identifier using **-gel\_spot\_identifier** (works in the same way as **-gel\_identifier**).

## Creating mzTab files using a batch process (Linux) ##

On Linux it is possible to use "find" to launch the mzTab generation for all result files using a single command. Based on the previous example the same could be achieved for all (in the example both) .dat files with the following command:

```
find /path/to/dat/file/directory -name "*.dat" -exec java -jar /path/to/pride_converter.jar -converter -engine mascot -mode mztab -gel_identifier "Gel A" -gel_spot_regex "Gel[AB]_Spot([0-9]+).*" -sourcefile '{}' \;
```

In this case the **-gel\_spot\_regex** option must be used as otherwise all files would be attributed to a single gel spot.

## Using the Graphical User Interface (GUI) ##

mzTab files can also be generated using PRIDE Converter's GUI. Simply select "Launch PRIDE mzTab Generator" in the first window. In the second step you need to select the input file type. In the third step you can set the above mentioned options to add the gel specific information automatically to the generated mzTab files. For detailed information about the options please see the above list.

# Running PRIDE Converter #

When running PRIDE Converter simply add the generated mzTab files containing the required gel specific annotation to the conversion process. This information will then automatically be included in the generated PRIDE XML file.