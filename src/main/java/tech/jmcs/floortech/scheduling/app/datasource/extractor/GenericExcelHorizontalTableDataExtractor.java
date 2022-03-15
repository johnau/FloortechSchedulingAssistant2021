package tech.jmcs.floortech.scheduling.app.datasource.extractor;

import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.exception.DataExtractorException;
import tech.jmcs.floortech.scheduling.app.exception.GenericExcelDataException;
import tech.jmcs.floortech.scheduling.app.datasource.model.ExtractedTableData;
import tech.jmcs.floortech.scheduling.app.types.DataSourceExtractorType;
import tech.jmcs.floortech.scheduling.app.util.ExcelCellAddress;
import tech.jmcs.floortech.scheduling.app.util.ExcelHelper;
import tech.jmcs.floortech.scheduling.app.util.XLSHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

/**
 * Extracts Table Data using table layout map <Cell Address, Contents (String)> and a column layout map
 * <Column Number (Integer): Name (String)> and a columns list <Integer> for row validation (cells that must contain
 * data) and extracts record data into a map stored by column name.
 *
 * Use the addAdditionalValidation(Function<Sheet, String> f) method to add extra validators for the table structure.
 * ** Note: The validation Functions should return an error string if validation failed, or empty string / null if there
 * are no errors (validation success).
 *
 * Note: Sheet numbers start at 0
 *
 * Tables used with this must adhere to the following rules:
 * - Horizontal (Records in rows).
 * - If a header row is used, header data must be in Column A.
 * - A valid data row can not contain a single value in Column A.
 * - A totals row can not have totals for all columns that also match a valid data row.
 */
public class GenericExcelHorizontalTableDataExtractor extends ExcelDataSourceExtractor<Map<String, Object>> {
    private static final Logger LOG = LoggerFactory.getLogger(GenericExcelHorizontalTableDataExtractor.class);

    private final Map<ExcelCellAddress, String> tableLayout;
    private final Map<Integer, GenericExtractorColumnDescription> columnMap;
    private final List<Integer> validRowDataList;

    private final Integer sheetNumber;
    private final ArrayList<Function<Sheet, String>> tableValidationFunctions;
    private final ArrayList<Function<Row, String>> recordValidationFunctions;

    /**
     * Constructor
     * Sheet Numbers start at 0
     * @param excelFile
     * @param tableLayout
     * @param columnMap  // TODO: Needs to change to an object with the column name and data type
     * @param sheetNumber (Starts at 0)
     * @throws IOException
     */
    protected GenericExcelHorizontalTableDataExtractor(Path excelFile,
                                                       Map<ExcelCellAddress, String> tableLayout,
                                                       Map<Integer, GenericExtractorColumnDescription> columnMap,
                                                       List<Integer> validRowDataList,
                                                       Integer sheetNumber) throws IOException {
        super(excelFile);

        this.setTargetSheetNumber(sheetNumber);

        this.tableLayout = tableLayout;
        this.columnMap = columnMap;
        this.sheetNumber = sheetNumber;
        this.validRowDataList = validRowDataList;
        this.tableValidationFunctions = new ArrayList<>();
        this.recordValidationFunctions = new ArrayList<>();


    }

    /**
     * Overload Constructor
     * Sheet Numbers start at 0
     * @param excelFile
     * @param tableLayout
     * @param columnMap
     * @param validRowDataList
     * @throws IOException
     */
    protected GenericExcelHorizontalTableDataExtractor(Path excelFile,
                                                       Map<ExcelCellAddress, String> tableLayout,
                                                       Map<Integer, GenericExtractorColumnDescription> columnMap,
                                                       List<Integer> validRowDataList)
            throws IOException {
        this(excelFile, tableLayout, columnMap, validRowDataList, 0);
    }

    public void addAdditionalTableValidation(Function<Sheet, String> validatorFunction) {
        this.tableValidationFunctions.add(validatorFunction);
    }

    public void addAdditionalRecordValidation(Function<Row, String> validatorFunction) {
        this.recordValidationFunctions.add(validatorFunction);
    }

    @Override
    public Boolean isValid() throws DataExtractorException {
        Sheet sheet = this.getTargetSheet();
        if (sheet == null) {
            throw new DataExtractorException("The file may be in use!", "Generic List");
        }

        DataFormatter dataFormatter = new DataFormatter();

        List<String> errors = new ArrayList<>();

        /** Check is valid based on provided TableLayout */
        this.tableLayout.forEach( (cellAddress, expValue) -> {

            if (cellAddress.getRow() < 0 || cellAddress.getCol() < 0) {
                LOG.debug("Skipping this item due to negative out of bounds cell address");
                return;
            }

            if (expValue.equals(DATA_START)) {
                /** data start value - skip processing */
                return;
            }

            Row row = sheet.getRow(cellAddress.getRow());
            Cell cell = XLSHelper.getCellByColumnIndex(row, cellAddress.getCol());
            if (cell == null) {
                LOG.debug("Validation may fail, cell was null for col: {}", cellAddress.getCol());
            }
            String cellValue = dataFormatter.formatCellValue(cell);

            // direct comparison and skip to next
            if (cellValue.equals(expValue)) {
                LOG.debug("Cell {} was exactly as expected {}", ExcelHelper.convertAddressToHumanReadable(cellAddress.getCol(), cellAddress.getRow()), expValue);
                return;
            }

            String cellValue_clean = cleanStringForComparison(cellValue);
            String expValue_clean = cleanStringForComparison(expValue);
            if (!cellValue_clean.equals(expValue_clean) && !cellValue_clean.contains(expValue_clean)) {
                String cellAddressStr = ExcelHelper.convertAddressToHumanReadable(cellAddress.getCol(), cellAddress.getRow());

                // if failed both direct comparison and cleaned comparison then add an error
                errors.add(String.format("Cell %s did not match or contain the expected content: '%s'.  Actual cellvalue was '%s'", cellAddressStr, expValue, cellValue));
            }
        });

        /** Check is valid with additional validation functions */
        for (Function<Sheet, String> validationFunction : this.tableValidationFunctions) {
            String error = validationFunction.apply(sheet);
            if (error == null || error.isEmpty()) {
                continue;
            }

            LOG.debug("Validation failed, reason: {}", error);
            errors.add(error);
        }

        /** Check for errors and return false */
        if (!errors.isEmpty()) {
            LOG.warn("There were {} errors while validating this table with Generic Excel Extractor", errors.size());
            StringBuilder sb = new StringBuilder();
            for (String error : errors) {
                sb.append(error);
                sb.append("\n");
            }
            throw new DataExtractorException(sb.toString(), "Generic Source");

        }

        /** Return true if no errors */
        return true;
    }

    @Override
    public void extract() throws DataExtractorException {
        if (!isValid()) {
            LOG.warn("The Truss List XLS file was not valid.");
            // throw exception
            return;
        }

        Sheet sheet = this.getTargetSheet();
        if (sheet == null) {
            LOG.debug("Sheet did not exist");
            throw new GenericExcelDataException("The requested sheet did not exist");

        }

        Optional<ExcelCellAddress> dsResult = this.tableLayout.entrySet().stream()
                .filter(e -> e.getValue().equals(DATA_START) )
                .map(e -> e.getKey() )
                .findFirst();
        if (!dsResult.isPresent()) {
            throw new GenericExcelDataException("No Data Start address provided ('" + DATA_START + "')");
        }

        ExcelCellAddress dataStart = dsResult.get();
        Integer startRow = dataStart.getRow();
        Integer startCol = dataStart.getCol();
        DataFormatter dataFormatter = new DataFormatter();

        this.dataObject = this.processSheet(sheet, startRow, startCol, dataFormatter);
    }

    private ExtractedTableData<Map<String, Object>> processSheet(Sheet sheet, Integer startRow, Integer startCol, DataFormatter dataFormatter) {
        ExtractedTableData<Map<String, Object>> tableData = new ExtractedTableData<>(DataSourceExtractorType.GENERIC_SIMPLE.getName());
        boolean hasHeaderRows = false;

        String headerValue = "";
        for (Row row : sheet) {
            Map<String, Object> rowData = new HashMap<>();
            if (row.getRowNum() >= startRow) {
                boolean skip = false;
                for (Cell cell : row) {
                    if (skip) continue;

                    int colIdx = cell.getColumnIndex();
                    if (colIdx >= startCol) {
                        GenericExtractorColumnDescription colDesc = this.columnMap.get(colIdx);
                        if (colDesc == null) {
                            LOG.debug("Tried to fetch a column descriptor that did not exist: Column #{}", colIdx);
                            continue;
                        }

                        LOG.trace("Working with column: {} {} {} ", colDesc.getName(), colDesc.getColumnId(), colDesc.getDataType());
                        String colName = colDesc.getName();

                        GenericExtractorColumnDataType colType = colDesc.getDataType();
                        tableData.addColumnType(colName, colType);

                        boolean isHeaderRow = isHeaderRow(row, dataFormatter);
                        if (isHeaderRow) {
                            hasHeaderRows = true;
                            headerValue = dataFormatter.formatCellValue(cell); // hit on first loop then the row is skipped.
                            skip = true;
                            LOG.trace("Encountered header row: {}", headerValue);
                            continue;
                        }

                        if (hasHeaderRows && colIdx == startCol) {
                            LOG.trace("Using header ({}) for first column ({})", headerValue, colName);
                            rowData.put(colName, headerValue);
                            continue;
                        }

                        if (cell.getCellType().equals(CellType.STRING)) {
                            String cellStr = cell.getStringCellValue();
                            LOG.trace("@ Col {}, found data: {} (Col Name: {})", colIdx, cellStr, colName);
                            rowData.put(colName, cellStr);
                        } else if (cell.getCellType().equals(CellType.NUMERIC)) {
                            Double cellNum = cell.getNumericCellValue();
                            LOG.trace("@ Col {}, found data: {} (Col Name: {})", colIdx, cellNum, colName);
                            rowData.put(colName, cellNum);
                        } else if (cell.getCellType().equals(CellType.BOOLEAN)) {
                            Boolean cellBool = cell.getBooleanCellValue();
                            LOG.trace("@ Col {}, found data: {} (Col Name: {})", colIdx, cellBool, colName);
                            rowData.put(colName, cellBool);
                        } else if (cell.getCellType().equals(CellType.BLANK)) {
                            rowData.put(colName, null);
                            LOG.trace("@ Col {}, found no data: EMPTY (Col Name: {})", colIdx, colName);
                        } else {
                            LOG.warn("Cell type not handled: {}", cell.getCellType().name());
                        }
                    }


                }
                skip = false;
            }

            if (!rowData.isEmpty() && rowDataOk(row, rowData)) {
                tableData.addData(rowData);
            } else {
                LOG.trace("This rows data was not complete");
            }
        }
        return tableData;
    }

    private boolean rowDataOk(Row row, Map<String, Object> rowData) {
        /** Removed below code
         * Check columns match in count - removed because not all columns need to be filled, and
         * extra columns in a table is not a problem as long as expected columns are in expected
         * locations.
         * TODO: Notify user if there are extra columns in the table.  User may not know extra data being ignored.
          */
//        if (rowData.size() != this.columnMap.size()) {
//            LOG.debug("Row data size did not match column number size");
//            return false;
//        }

        for (Map.Entry<Integer, GenericExtractorColumnDescription> colEntry : this.columnMap.entrySet()) {
            Integer colNum = colEntry.getKey();
            GenericExtractorColumnDescription colDesc = colEntry.getValue();
            String colName = colDesc.getName();

            if (!this.validRowDataList.contains(colNum)) {
                LOG.debug("Row Data Check: Not checking column: {} ({}) for data, not in validation list", colNum, colName);
                continue;
            }

            Object value = rowData.get(colName);

            if (value instanceof Double) {
                Double d = (Double) value;
                if (d == null || d.isNaN() || d.isInfinite()) { // TODO: Take another look at this check against invalid Double values.
                    LOG.debug("Row Data Check Fail: Value was empty for: {}", colName);
                    return false;
                } else {
                    LOG.debug("Row data Check pass for col: '{}' (value: '{}')", colName, d);
                }
            } else if (value instanceof String) {
                String s = (String) value;
                if (s.isEmpty()) {
                    LOG.debug("Row Data Check Fail: Value was empty for: {}", colName);
                    return false;
                } else {
                    LOG.debug("Row data Check pass for col: '{}' (value: '{}')", colName, s);
                }
            } else {
                LOG.debug("Unexpected data type: {}", value.getClass().getName());
            }
        }

        for (Function<Row, String> recordValidationFunction : recordValidationFunctions) {
            String error = recordValidationFunction.apply(row);
            if (error == null || error.isEmpty()) {
                continue;
            }

            LOG.debug("Validation failed, reason: {}", error);
//            errors.add(error);
            return false;
        }

        return true;
    }

    private boolean isHeaderRow(Row row, DataFormatter dataFormatter) {
        boolean firstCellPopulated = false;
        int col = -1;
        int blankCount = 0;
        for (Cell cell : row) {
            col += 1;
            if (col == 0) {
                /** Check first column for content */
                String content = dataFormatter.formatCellValue(cell);
                if (!content.isEmpty()) {
                    firstCellPopulated = true;
                }
            } else {
                /** Check other columns for blanks */
                if (cell.getCellType().equals(CellType.BLANK)) {
                    blankCount++;
                }
            }
        }
        if (firstCellPopulated && blankCount == row.getLastCellNum() - 1) {
            // checking first cell is populated and the rest are blanks
            return true;
        }
        return false;
    }

    @Override
    public ExtractedTableData<Map<String, Object>> getData() {
//        try {
//            this.closeFile();
//        } catch (Exception e) {
//
//        }
        return this.dataObject;
    }

    private String cleanStringForComparison(String s) {
        s = s.toLowerCase().trim(); // lowercase and trim
        s = s.replaceAll("[\\s]+", " "); // remove excess spaces
        s = s.replace(",", ""); // remove commas

        return s;
    }
}
