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

        for (Cell cell : row) {
            int cci = cell.getColumnIndex();
            if (cci == colIndex) {
                return cell;
            }
        }

        LOG.trace("Could not find a cell for col index: {} in this row", colIndex);
        return null;
    }

    /**
     * Searches a column for a matching string (name).  Returns an ExcelCellAddress with the matched row (and col).
     * If not found, returns null.
     * @param name
     * @return ExcelCellAddress or Null
     * @throws ExcelScheduleWriterException
     */
    public static ExcelCellAddress findCellByName(Sheet schSheet, Integer column, String name, DataFormatter dataFormatter) {
        for (Row row : schSheet) {
            Cell cell = getCellByColumnIndex(row, column);
            if (cell == null) {
                LOG.trace("Cell was null while trying to find name at row: {}", row.getRowNum());
                continue;
            }

            String cellValue = dataFormatter.formatCellValue(cell);
            LOG.trace("Found cell value: {}, trying to find {}", cellValue, name);

            String cellV_ucase = cellValue.toUpperCase().trim().replaceAll(" +", " ");
            String name_ucase = name.toUpperCase().trim().replaceAll(" +", " ");

            if (cellV_ucase.equals(name_ucase)) {
                return new ExcelCellAddress(cell.getColumnIndex(), row.getRowNum());
            }
        }

        LOG.debug("Could not find target cell '{}'", name);
        return null;
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
        if (!cellValue.isEmpty()) {
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
        if (value == null) {
            targetCell.setBlank();
            return;
        }
        // write value to target address
        Class valueClass = value.getClass();
        if (valueClass.equals(String.class)) {
            targetCell.setCellValue((String) value);
        } else if (valueClass.equals(Double.class)) {
            targetCell.setCellValue((Double) value);
        } else if (valueClass.equals(Boolean.class)) {
            targetCell.setCellValue((Boolean) value);
        } else if (valueClass.equals(Date.class)) {
            targetCell.setCellValue((Date) value);
        } else {
            LOG.debug("Data type {} is not handled and was not written to the excel file", valueClass.getName());
        }
    }

}
