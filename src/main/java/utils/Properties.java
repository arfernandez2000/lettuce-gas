package utils;

public class Properties {

    private Integer D;

    private Integer N;

    private String outFileName;

    private boolean printDensity;

    private Integer runs;

    public Integer getD() {
        return D;
    }

    public void setD(Integer d) {
        D = d;
    }

    public Integer getN() {
        return N;
    }

    public boolean isPrintDensity() {
        return printDensity;
    }

    public void setPrintDensity(boolean printDensity) {
        this.printDensity = printDensity;
    }

    public Integer getRuns() {
        return runs;
    }

    public void setRuns(Integer runs) {
        this.runs = runs;
    }

    public String getOutFileName() {
        return outFileName;
    }

    public void setOutFileName(String outFileName) {
        this.outFileName = outFileName;
    }

    public void setN(Integer n) {
        N = n;
    }

    public Properties() { }
}
