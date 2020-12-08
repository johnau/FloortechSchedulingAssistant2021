package tech.jmcs.floortech.scheduling.app.util;

public class ExcelCellRange {

    private ExcelCellAddress start;
    private ExcelCellAddress end;

    public ExcelCellRange() {
    }

    public ExcelCellAddress getStart() {
        return start;
    }

    public void setStart(ExcelCellAddress start) {
        this.start = start;
    }

    public ExcelCellAddress getEnd() {
        return end;
    }

    public void setEnd(ExcelCellAddress end) {
        this.end = end;
    }
}
