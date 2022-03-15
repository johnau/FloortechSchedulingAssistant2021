package tech.jmcs.floortech.scheduling.ui.customextractorwizard;

public class ColumnValidationData {

    private Integer columnPosition = 0;
    private String columnTitle = "";
    private String dataType = "";
    private Boolean empty = false;
    private Boolean requiredForRecord = false;

    public ColumnValidationData() {
    }

    public Integer getColumnPosition() {
        return columnPosition;
    }

    public void setColumnPosition(Integer columnPosition) {
        this.columnPosition = columnPosition;
    }

    public String getColumnTitle() {
        return columnTitle;
    }

    public void setColumnTitle(String columnTitle) {
        this.columnTitle = columnTitle;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Boolean getEmpty() {
        return empty;
    }

    public void setEmpty(Boolean empty) {
        this.empty = empty;
    }

    public Boolean getRequiredForRecord() {
        return requiredForRecord;
    }

    public void setRequiredForRecord(Boolean requiredForRecord) {
        this.requiredForRecord = requiredForRecord;
    }
}
