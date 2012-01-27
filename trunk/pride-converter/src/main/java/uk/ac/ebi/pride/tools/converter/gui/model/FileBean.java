package uk.ac.ebi.pride.tools.converter.gui.model;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 27/01/12
 * Time: 13:50
 */
public class FileBean implements Comparable<FileBean> {

    private String inputFile;
    private String reportFile;
    private String outputFile;
    private String mzTabFile;

    public FileBean(String inputFile) {
        this.inputFile = inputFile;
    }

    public String getInputFile() {
        return inputFile;
    }

    public String getReportFile() {
        return reportFile;
    }

    public void setReportFile(String reportFile) {
        this.reportFile = reportFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getMzTabFile() {
        return mzTabFile;
    }

    public void setMzTabFile(String mzTabFile) {
        this.mzTabFile = mzTabFile;
    }

    @Override
    public int compareTo(FileBean o) {
        return this.inputFile.compareTo(o.getInputFile());
    }
}