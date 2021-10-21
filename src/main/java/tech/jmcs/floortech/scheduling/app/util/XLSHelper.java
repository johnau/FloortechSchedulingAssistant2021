package tech.jmcs.floortech.scheduling.app.util;

import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.exception.ExcelScheduleWriterException;

import java.util.Date;

public class XLSHelper {
    private static final Logger LOG = LoggerFactory.getLogger(XLSHelper.class);

    /**
     * Gets a cell from a row based on the column index.
     * Returns a Apache POI Cell object or Null
     * @param row
     * @param colIndex
     * @return Cell or null
     */
    public static Cell getCellByColumnIndex(Row row, Integer colIndex) {
        if (row == null) {
            LOG.warn("Row was null");
            return null;
        }

        if (colIndex < 0) {
            LOG.warn("colIndex was less than 0 (negative) : {}", colIndex);
            return null;
        }

        return row.getCell(colIndex);

//        for (Cell cell : row) {
//            int cci = cell.getColumnIndex();
//            if (cci == colIndex) {
//                return cell;
//            }
//        }

//        LOG.trace("Could not find a cell for col index: {} in this row", colIndex);
//        return null;
    }

    /**
     * Searches a column for a matching string (name).  Returns an ExcelCellAddress with the matched row (and col).
     * If not found, returns null.
     * @param name
     * @return ExcelCellAddress or Null
     * @throws ExcelScheduleWriterException
     */
    public static ExcelCellAddress findCellByName(Sheet schSheet, Integer column, String name, DataFormatter dataFormatter) {
        LOG.debug("Searching for cell value: {} (in column {})", name, column);
        if (name == null || column == null) {
            return null;
        }
        for (Row row : schSheet) {
            Cell cell = getCellByColumnIndex(row, column);
            if (cell == null) {
                LOG.trace("Cell was null while trying to find name at row: {}", row.getRowNum());
                continue;
            }
            ExcelCellAddress address = new ExcelCellAddress(cell.getColumnIndex(), row.getRowNum());
            String cellValue = dataFormatter.formatCellValue(cell);
            LOG.trace("At cell value: {} (trying to find {})", cellValue, name);

            String cellValue_ucase = cleanUpSpaces(cellValue.toUpperCase());
            String name_ucase = cleanUpSpaces(name.toUpperCase());

            if (cellValue_ucase.equals(name_ucase)) {
                LOG.debug("Matched (uppercase match)");
                return address;
            }

            String cellValue_stripped = cellValue_ucase.replaceAll("[^A-Za-z0-9]", "");
            String name_stripped = name_ucase.replaceAll("[^A-Za-z0-9]", "");

            if (cellValue_stripped.equals(name_stripped)) {
                LOG.debug("Matched (stripped match) {} = {} ({} = {})", cellValue_stripped, name_stripped, cellValue, name);
                return address;
            }

            if (cellValue_stripped.contains(name_stripped) && cellValue_stripped.startsWith(name_stripped)) {
                LOG.debug("Matched (stripped contained match) {} = {} ({} = {})", cellValue_stripped, name_stripped, cellValue, name);
                return address;
            }
        }

        LOG.debug("Could not find target cell '{}'", name);
        return null;
    }

    private static String cleanUpSpaces(String s) {
        return s.trim().replaceAll("[\\s]+", " ");
    }

    /**
     * Check if a cell is empty
     * @param cell
     * @param dataFormatter
     * @return
     */
    public static boolean cellIsEmpty(Cell cell, DataFormatter dataFormatter) {
        if (cell == null) {
            LOG.debug("Cell Empty check: cell was null");
            return true;
        }
        // check cell value by string
        String cellValue = dataFormatter.formatCellValue(cell);
        if (!cellValue.isEmpty() && !cellValue.equalsIgnoreCase("0")) {
            LOG.debug("Cell was not empty, contents: {}", cellValue);
            return false;
        }

        // check cell value by formula (currently will never be run? Checking by string will catch a formula)
        if (cell.getCellType().equals(CellType.FORMULA)) {
            String formula = cell.getCellFormula();
            LOG.debug("Cell contained formula: {}", formula);
            // TODO: Work with cell formula and allow retention in some cases (not in this method)
            return false;
        }

        return true;
    }

    public static void updateCellValue(Cell targetCell, Object value) {
        targetCell.setBlank();
        if (value == null) {
            return;
        }
        // write value to target address
        if (value instanceof String) {
            targetCell.setCellValue((String) value);
        } else if (value instanceof Double || value instanceof Long || value instanceof Integer) {
            targetCell.setCellValue((Double) value);
        } else if (value instanceof Boolean) {
            targetCell.setCellValue((Boolean) value);
        } else if (value instanceof Date) {
            targetCell.setCellValue((Date) value);
        } else {
            LOG.debug("Data type {} is not handled and was not written to the excel file", value.getClass().getSimpleName());
        }
    }

}
