package tech.jmcs.floortech.scheduling.app.util;

import java.io.Serializable;
import java.util.Objects;

public class ExcelCellAddress implements Serializable {

    private Integer col = -1;
    private Integer row = -1;

    public ExcelCellAddress(Integer col, Integer row) {
        this.col = col;
        this.row = row;
    }

    public Integer getCol() {
        return col;
    }

    public void setCol(Integer col) {
        this.col = col;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExcelCellAddress that = (ExcelCellAddress) o;
        return Objects.equals(col, that.col) &&
                Objects.equals(row, that.row);
    }

    @Override
    public int hashCode() {
        return Objects.hash(col, row);
    }

    @Override
    public String toString() {
        return this.col+", "+this.row;
    }

    public static ExcelCellAddress fromString(String s) throws Exception {

        String[] parts = s.split(",");
        if (parts.length > 2) {
            throw new Exception("Bad formatted string");
        }

        String _col = parts[0].trim();
        String _row = parts[1].trim();

        int col = 0;
        int row = 0;
        try {
            col = Integer.parseInt(_col);
            row = Integer.parseInt(_row);
            ExcelCellAddress eca = new ExcelCellAddress(col, row);
            return eca;
        } catch (NumberFormatException nex) {
            throw new Exception("Bad formatted string");
        }
    }
}
