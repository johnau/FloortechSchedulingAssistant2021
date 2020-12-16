package tech.jmcs.floortech.scheduling.app.datasource.extractor;

public class GenericExtractorColumnDescription {

    private String name;
    private Integer columnId; // column position in source
    private Integer columnDisplayPosition; // column position in table view ?
    private GenericExtractorColumnDataType dataType;

    public GenericExtractorColumnDescription() {
    }

    public GenericExtractorColumnDescription(String name, Integer columnId, GenericExtractorColumnDataType dataType) {
        this.name = name;
        this.columnId = columnId;
        this.dataType = dataType;
        this.columnDisplayPosition = columnId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getColumnId() {
        return columnId;
    }

    public void setColumnId(Integer columnId) {
        this.columnId = columnId;
    }

    public Integer getColumnDisplayPosition() {
        if (columnDisplayPosition == null) {
            return columnId;
        }
        return columnDisplayPosition;
    }

    public void setColumnDisplayPosition(Integer columnDisplayPosition) {
        this.columnDisplayPosition = columnDisplayPosition;
    }

    public GenericExtractorColumnDataType getDataType() {
        return dataType;
    }

    public void setDataType(GenericExtractorColumnDataType dataType) {
        this.dataType = dataType;
    }
}
