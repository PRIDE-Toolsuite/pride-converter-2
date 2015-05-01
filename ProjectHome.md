# The PRIDE Converter 2 tool suite is a composed of 4 independent applications #

  * The **PRIDE Converter 2** application will convert MS search result files containing identification and spectra into PRIDE XML.
  * The **PRIDE mzTab Generator** will produce skeleton mzTab files from MS search results files. These skeleton files require either manual or scripted editing to add quantitation and/or gel information.
  * The **PRIDE XML Filter** will remove identifications or spectra from PRIDE XML files based on a series of configurable filters.
  * The **PRIDE XML Merger** will combine several PRIDE XML files into a single one.

All tools have both a Graphical User Interface (GUI) and a Command-Line Interface (CLI). The GUIs have been designed to provide a rich, user-friendly interface while the CLIs have been developed mainly for tool and pipeline developers to be able to integrate the PRIDE Converter 2 tools in their own software to provide an efficient way to generate PRIDE XML from their own resources.

# Publications #

When you use PRIDE Converter 2, please cite the following publication:

  * [Cote RG & Griss J, et al., Mol Cell Proteomics. 2012 Sep 4.](http://www.mcponline.org/content/early/2012/09/03/mcp.O112.021543.abstract) [PDF File.](http://www.mcponline.org/content/early/2012/09/03/mcp.O112.021543.full.pdf) [PubMed Record.](http://www.ncbi.nlm.nih.gov/pubmed/22949509)


---


# Downloads #

The latest PRIDE Converter 2 package will always be available from our [FTP site](ftp://ftp.pride.ebi.ac.uk/pride/resources/tools/converter-2/latest/pride-converter-2.zip).


---


# System Requirements #
  * Java: 64-bit, JRE 1.5+
  * CPU: 1 gigahertz (GHz) or faster 64-bit processor.
  * Memory: 1+ GB RAM.
  * Hard Disk: 55 MB available for installation, plus more for file conversions.
  * Platform: 64-bit, Windows / Max / Linux.
  * An established Internet connection.


---




# Change Log #
**v2.0.21**
  * Templates are now saved properly.

**v2.0.20**
  * Improved ProteomeDiscoverer MSF support

**v2.0.19**
  * Updated bootstrap of applications to deal with new windows-specific problem of Runtime.exec() parsing introduced in java 1.7

**v2.0.18**
  * Updated documentation in converter.properties file. Also added graphical warning from Application selection panel if the Java executable is not found where expected.

**v2.0.17**
  * Fixed issue where a pre-existing, schematically invalid (or possibly corrupt) report file could break the conversion process.

**v2.0.16**
  * Improved ProteomeDiscoverer MSF support

**v2.0.15**
  * minor code cleanup

**v2.0.14**
  * Added mandatory BTO annotation for sample
  * Fixed auto-completion display bug in CvParam dialog search box

**v2.0.13**
  * Added option to turn off automatic protein accession resolver in cases of non-standard protein databases
  * Improved exception reporting for mzIdentML DAO

**v2.0.12**
  * Fixed bug in PRIDE XML Writer. Spectra with empty or null mz/intensity arrays will have dummy values inserted (an array with a single double value, 0.0). This is to keep the spectrum count attribute in the mzData head element consistent. The PRIDE XML Filter too has been updated to deal with such spectra should they require filtering.

**v2.0.11**
  * Fixed UI error where the wrong table row was updated in certain cases

**v2.0.10**
  * Fixed default memory settings

**v2.0.9**
  * Fixed OMSSA PTM parsing error

**v2.0.8**
  * Updated low-level XTandem parsing library to latest version

**v2.0.7**
  * Fixed parsing error in OMSSA DAO

**v2.0.6**
  * Improved logging/debugging of bootstrap process

**v2.0.5**
  * Documentation updates
  * Fixed bug in MGF parser

**v2.0.4**
  * Fixed bug in PRIDE XML Writer. Proteins with no supporting peptides will be filtered out automatically.

**v2.0.3**
  * Bugfix with the windows bootstrap launcher.

**v2.0.2**
  * Added possibility to enable verbose debug logging to GUI application selector

**v2.0.1**
  * Fixed single file vs directory selection bug for external spectra in selected DAOs

**v2.0**
  * Initial Release


---


# Documentation #

| PRIDE Converter 2 GUI user guide | [PDF](http://pride-converter-2.googlecode.com/files/PRIDE%20Converter%202%20GUI%20User%20Manual.pdf) |
|:---------------------------------|:-----------------------------------------------------------------------------------------------------|
| PRIDE Converter 2 CLI user guide | [PDF](http://pride-converter-2.googlecode.com/files/PRIDE%20Converter%202%20CLI%20User%20Manual.pdf) |
| PRIDE Converter 2 Developer  guide | [PDF](http://pride-converter-2.googlecode.com/files/PRIDE%20Converter%202%20Developer%20Guide.pdf) |


---


# Troubleshooting #

Please check the converter.properties file with respect to the memory settings for Java (see below). The default configuration used very limited memory, which may cause Java Heap Space errors if larger files are converted. To resolve these errors increase the memory configuration (example: jvm.args=-Xms512M -Xmx2024M), but please make sure your computer has enough memory.

## The Application Selector window appears but no tools start when a button is clicked ##

When PRIDE Converter 2 starts in the GUI mode, the first screen that is displayed is the application selector. In rare occasions, it is possible that nothing happens when the users click on a button to start a specific application (Converter, Filter, Merger, mzTab generator). This is generally a problem caused with improper Java configuration. Please ensure that JAVA\_HOME is correctly set in your environment and that the java runtime executable file is in your path. Please refer to the Java documentation on how to properly install and configure the Java Runtime Environment (http://java.com/en/download/help/download_options.xml).

When a button is clicked from the application selector, a new process is launched based on the settings found in the converter.properties file that is located in the same directory as the PRIDE Converter 2 jar file.

```
###############################################################
# Pride Converter GUI Bootstrap configuration
###############################################################

# set the exact path of the java executable if not already in the path
# do not include the executable itself. Do not include the trailing slash.

# eg java.home=/home/rcote/dev/jdk1.6.0_13/bin
# and not
# eg java.home=/home/rcote/dev/jdk1.6.0_13/bin/java
# or not
# eg java.home=/home/rcote/dev/jdk1.6.0_13/bin/
#
# NOTE - Windows users will need to use double-backslashes
# c:\\Program Files\\java\\jre7\\bin
#
# Note - Max OS users will need to point to the correct folder
# /System/Library/Frameworks/JavaVM.framework/Versions/Current/Commands
java.home=

# any JVM argument listed here will be passed verbatim to the JVM
jvm.args=-Xms64M -Xmx1024M

# IF YOU GET OutOfMemoryErrors and your system has more RAM available
# change the jvm.args settings to increase the amount of memory
# allocated to the Converter
#jvm.args=-Xms128M -Xmx2G

# uncomment and set accordingly to configure PROXY settings
#http.proxyHost=webcache.mydomain.com
#http.proxyPort=8080
#http.proxyUser=
#http.proxyPassword=
#http.proxySet=true
```

If JAVA\_HOME isn’t properly set, the new process might not properly start. One way to circumvent this problem is to update the java.home setting in the properties file. You need to include the full path to the java executable, without the trailing slash

```
java.home=/home/rcote/dev/jdk1.6.0_13/bin              - correct for linux and Mac OS X
java.home=c:\\Program Files\\jdk1.6.0_13\\bin             - correct for Windows
```

If this still does not work correctly, try starting PRIDE Converter 2 from the command line, as useful debugging information is logged to the console window.

```
rcote@bobble: target$ java -jar pride-converter-2.0-SNAPSHOT.jar 

Reading properties file: ./converter.properties
Bootstrap command: /usr/lib/jvm/java-6-sun-1.6.0.26/jre/bin/java -Xms128M -Xmx4000M \
  -cp ./pride-converter-2.0-SNAPSHOT.jar \
  uk.ac.ebi.pride.tools.converter.gui.ConverterApplicationSelector$ConverterLauncher

Name of the OS: Linux
Version of the OS: 2.6.32-41-generic
Architecture of The OS: amd64
Java Home: /usr/lib/jvm/java-6-sun-1.6.0.26/jre
Java Version: 1.6.0_26 (Sun Microsystems Inc.)
Number of processors: 2
Memory Usage: 122MB used, 114MB free, 3555MB Max
Validator Loaded
```

This information can be communicated with the PRIDE helpdesk to help fix the problem.

If nothing still seems to be happening, please look in the logs. You will probably find something like this:
```
2012-09-10 15:32:43,006 ERROR ConverterApplicationSelector - The JVM exited with an error code. This probably means that your application did not start correctly.
2012-09-10 15:32:43,006 ERROR ConverterApplicationSelector - Please select the 'Show Debug Information' from the help menu or try running the boostrap command from a console.
```

If you see these errors, please start the converter GUI and turn on the 'Show Debug Information' from the 'Help' menu in the upper right corner. This will write more information to the log file and can help solve the problem.

A common source of this error happens if the default memory settings are not appropriate for your configuration, you might get an error message similar to this:
```
Error occurred during initialization of VM
Could not reserve enough space for object heap
Error: Could not create the Java Virtual Machine.
Error: A fatal exception has occurred. Program will exit.
```

In this case, lower the default memory settings in the `jvm.args` property and restart the converter.

## A progress window appears but the application seems to hang ##

If you are converting a large number of files or files that are very big or contain a large number of spectra, the conversion process can take anywhere from minutes to hours. This is normal and the time required will be in direct proportion to the number, size and complexity of the source files. The GUI will always show message dialogs if exceptions occur. If the application still seems to hang but no error message is shown, users should look in the log file that is in the log directory where the PRIDE Converter 2 was installed.

## The application runs out of memory ##

The PRIDE Converter 2 tool suite was written to be as memory-efficient as possible. However, if converting a large number of big files, it is always possible to run out of memory. If this should happen, please update the _jvm.args_ setting in the converter.properties file.

## The application cannot use the OLS ##

The PRIDE Converter 2 tool suite requires a working internet connection to connect to the OLS web service to perform CV term related queries. If you are on a computer that requires a proxy configuration to access external network resources, you will need to uncomment and update the five _http.proxy_ settings in the converter.properties file.