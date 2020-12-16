package tech.jmcs.floortech.scheduling.app.datasource.model;

import tech.jmcs.floortech.scheduling.app.datasource.extractor.GenericExtractorColumnDataType;

import java.util.HashMap;
import java.util.Map;

public class ExtractedTableData<T> {

    private String name;
    private Map<String, GenericExtractorColumnDataType> columnTypes;
    private Map<Long, T> data;

    public ExtractedTableData(String name) {
        this.name = name;
        this.data = new HashMap<>();
        this.columnTypes = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Adds a value to the map and returns the assigned id.
     * @param value
     * @return
     */
    public long addData(T value) {
        long id = (long) this.data.size();
        this.data.put(id, value);
        return id;
    }

    public Map<Long, T> getData() {
        return data;
    }

    public Map<String, GenericExtractorColumnDataType> getColumnTypes() {
        return columnTypes;
    }

    public void addColumnType(String columnName, GenericExtractorColumnDataType type) {
        this.columnTypes.putIfAbsent(columnName, type);
    }

    public void setColumnTypes(Map<String, GenericExtractorColumnDataType> columnTypes) {
        this.columnTypes = columnTypes;
    }
}
