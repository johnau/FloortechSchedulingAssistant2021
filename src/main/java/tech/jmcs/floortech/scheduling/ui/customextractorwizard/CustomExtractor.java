package tech.jmcs.floortech.scheduling.ui.customextractorwizard;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class CustomExtractor {

    private String extratorName;
    private String tableTitle;
    private Integer tableTitleRow;
    private Integer columnHeadersRow;
    private Integer firstDataRow;
    private List<ColumnValidationData> columnDescriptors;
    private ObservableList<ColumnValidationData> columnDescriptorsObservable;

//    private List<Integer> requiredRecordDataColumns;

    private String dataSourceFileNamePattern; // the string part of the pattern
    private StringPosition dataSourceFileNamePatternPos; // the position part of the pattern (1. starts with, 2. contains, 3. ends with, )



    public CustomExtractor() {
        this.columnDescriptorsObservable = FXCollections.observableArrayList();

        this.tableTitleRow = 1;
        this.columnHeadersRow = 2;
        this.firstDataRow = 3;
    }

    public String getExtratorName() {
        return extratorName;
    }

    public void setExtratorName(String extratorName) {
        this.extratorName = extratorName;
    }

    public String getTableTitle() {
        return tableTitle;
    }

    public void setTableTitle(String tableTitle) {
        this.tableTitle = tableTitle;
    }

    public Integer getTableTitleRow() {
        return tableTitleRow;
    }

    public void setTableTitleRow(Integer tableTitleRow) {
        this.tableTitleRow = tableTitleRow;
    }

    public Integer getColumnHeadersRow() {
        return columnHeadersRow;
    }

    public void setColumnHeadersRow(Integer columnHeadersRow) {
        this.columnHeadersRow = columnHeadersRow;
    }

    public Integer getFirstDataRow() {
        return firstDataRow;
    }

    public void setFirstDataRow(Integer firstDataRow) {
        this.firstDataRow = firstDataRow;
    }

    public List<ColumnValidationData> getColumnDescriptors() {
        return columnDescriptors;
    }

    public void setColumnDescriptors(List<ColumnValidationData> columnDescriptors) {
        this.columnDescriptors = columnDescriptors;
        this.columnDescriptorsObservable.clear();
        this.columnDescriptorsObservable.addAll(columnDescriptors);
    }

    public ObservableList<ColumnValidationData> getColumnDescriptorsObservable() {
        return columnDescriptorsObservable;
    }

    //    public List<Integer> getRequiredRecordDataColumns() {
//        return requiredRecordDataColumns;
//    }
//
//    public void setRequiredRecordDataColumns(List<Integer> requiredRecordDataColumns) {
//        this.requiredRecordDataColumns = requiredRecordDataColumns;
//    }

    public String getDataSourceFileNamePattern() {
        return dataSourceFileNamePattern;
    }

    public void setDataSourceFileNamePattern(String dataSourceFileNamePattern) {
        this.dataSourceFileNamePattern = dataSourceFileNamePattern;
    }

    public StringPosition getDataSourceFileNamePatternPos() {
        return dataSourceFileNamePatternPos;
    }

    /**
     * Use value 1 for Starts With
     * Use value 2 for Contains
     * Use value 3 for Ends With
     * @param dataSourceFileNamePatternPos
     */
    public void setDataSourceFileNamePatternPos(StringPosition dataSourceFileNamePatternPos) {
        this.dataSourceFileNamePatternPos = dataSourceFileNamePatternPos;
    }


}
