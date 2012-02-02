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
    private String sequenceFile;

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

    public String getSequenceFile() {
        return sequenceFile;
    }

    public void setSequenceFile(String sequenceFile) {
        this.sequenceFile = sequenceFile;
    }

    @Override
    public int compareTo(FileBean o) {
        return this.inputFile.compareTo(o.getInputFile());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileBean fileBean = (FileBean) o;

        if (inputFile != null ? !inputFile.equals(fileBean.inputFile) : fileBean.inputFile != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return inputFile != null ? inputFile.hashCode() : 0;
    }
}