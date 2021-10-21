package tech.jmcs.floortech.scheduling.app.schedulewriter;

import org.apache.poi.ss.format.CellFormatter;
import org.apache.poi.ss.usermodel.*;
import tech.jmcs.floortech.scheduling.app.util.ExcelCellAddress;
import tech.jmcs.floortech.scheduling.app.util.ExcelHelper;
import tech.jmcs.floortech.scheduling.app.util.XLSHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BasicExcelScheduleUpdateConfirmer extends ExcelScheduleUpdateConfirmer {

    protected BasicExcelScheduleUpdateConfirmer(ExcelScheduleUpdater updater) {
        super(updater);
    }

    @Override
    public void forceOverwrite_addToCurrentValue(String name, Object value) {
        DataFormatter dataFormatter = new DataFormatter();
        Cell targetCell = getTargetValueCell(name, value, dataFormatter);

        // check target address is NOT empty
        Object adjustedValue = null;
        if (XLSHelper.cellIsEmpty(targetCell, dataFormatter)) {
            adjustedValue = value;
        } else {
            // get cell value, add to Object value.
            LOG.debug("Cell type: {} and value type: {}", targetCell.getCellType(), value.getClass().getName());
            if (targetCell.getCellType().equals(CellType.STRING)
                    && value.getClass().equals(String.class)) {
                String strCellValue = targetCell.getStringCellValue();
                String strNewValue = (String) value;
                adjustedValue = strCellValue + ";" + strNewValue;
            } else if (targetCell.getCellType().equals(CellType.NUMERIC)
                    && value.getClass().equals(Double.class)) {
                Double dblCellValue = targetCell.getNumericCellValue();
                Double dblNewValue = (Double) value;
                adjustedValue = dblCellValue + dblNewValue;
            }
        }

        LOG.debug("Added cell value to new value, sum: {}", adjustedValue);
        writeValueToTargetAddress(targetCell, adjustedValue);
    }

    @Override
    public void forceOverwrite_addNewCell(String name, Object value, ExcelCellAddress belowCell) {
        Sheet tSheet = getTargetSheet();

        int rowCount = tSheet.getLastRowNum();

        int targetRow = belowCell.getRow() + 1;

        tSheet.shiftRows(targetRow, rowCount, 1, true, false);

        Row newRow = tSheet.createRow(targetRow);

        Workbook wb = this.updater.getXlsUtil().getWorkbook();

        // create cell style and font style for newly added cells
        Font fontItalic = wb.createFont();
        fontItalic.setFontHeightInPoints((short)12);
        fontItalic.setFontName("Arial");
        fontItalic.setBold(true);
        fontItalic.setItalic(true);

        Font fontNormal = wb.createFont();
        fontNormal.setFontHeightInPoints((short)12);
        fontNormal.setFontName("Arial");
        fontNormal.setBold(true);

        CellStyle styleItalic = wb.createCellStyle();
        styleItalic.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
        styleItalic.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleItalic.setFont(fontItalic);

        CellStyle styleNormal = wb.createCellStyle();
        styleNormal.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
        styleNormal.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleNormal.setFont(fontNormal);

        // TODO: Cells should also be created for UNIT, RATE, and TOTAL and should be filled with ?'s to force user to fill in the data.  Later this can be done through the app

        Cell dataNameCell = newRow.createCell(this.updater.getDataNameColumn());
        dataNameCell.setCellStyle(styleItalic);
        Cell dataValueCell = newRow.createCell(this.updater.getDataValueColumn());
        dataValueCell.setCellStyle(styleNormal);

        dataNameCell.setCellValue(name.toUpperCase());

        if (value.getClass().equals(Double.class)) {
            dataValueCell.setCellValue((Double) value);
        } else if (value.getClass().equals(String.class)) {
            dataValueCell.setCellValue((String) value);
        } else {
            LOG.debug("Adding new value cell; value type not handled! (No data added to cell)");
        }
    }

    @Override
    public void forceOverwrite_replaceCurrentValue(String name, Object value) {
        DataFormatter dataFormatter = new DataFormatter();
        Cell targetCell = getTargetValueCell(name, value, dataFormatter);

        writeValueToTargetAddress(targetCell, value);
    }

    @Override
    public void forceOverwrite_replaceCell(String name, Object value, ExcelCellAddress targetRow) {
        Sheet tSheet = getTargetSheet();
        Row tRow = tSheet.getRow(targetRow.getRow());
        Cell dataNameCell = XLSHelper.getCellByColumnIndex(tRow, this.updater.getDataNameColumn());
        Cell dataValueCell = XLSHelper.getCellByColumnIndex(tRow, this.updater.getDataValueColumn());
        if (dataNameCell == null || dataValueCell == null) {
            LOG.debug("One or more of the target cells could not be found.");
            // TODO: Throw an exception.  (Should not create new cells here, expecting that the cell exist here)
            return;
        }

        dataNameCell.setCellValue(name.toUpperCase());

        if (value instanceof Double || value instanceof Integer || value instanceof Long) {
            dataValueCell.setCellValue((Double) value);
        } else if (value instanceof String) {
            dataValueCell.setCellValue((String) value);
        } else {
            LOG.debug("Type not handled!");
        }

    }

    @Override
    public List<String> getAllScheduleEntryNames() {
        Iterator<Row> it = this.updater.getTargetSheet().rowIterator();
        List<String> entryNames = new ArrayList<>();
        while (it.hasNext()) {
            Row r = it.next();
            Integer rowNum = r.getRowNum();
            Integer colNum = this.updater.getDataNameColumn();
            ExcelCellAddress eca = new ExcelCellAddress(colNum, rowNum);

            Cell c = r.getCell(colNum);
            DataFormatter df = new DataFormatter();
            String s = df.formatCellValue(c);
            entryNames.add(String.format("%s | %s", eca.toString(), s));
        }
        return entryNames;
    }

    protected Sheet getTargetSheet() {
        return this.updater.getTargetSheet();
//        try {
//            return this.updater.getXlsUtil().getSheet(this.updater.getTargetSheetNumber(), true);
//        } catch (Exception e) {
//            LOG.debug("Sheet out of range exception...");
//            return null;
//        }
    }

    protected boolean writeValueToTargetAddress(Cell targetCell, Object value) {
        if (value != null) {
            LOG.debug("Updating target cell {} with value: {}", targetCell, value);
            XLSHelper.updateCellValue(targetCell, value);
            return true;
        } else {
            LOG.debug("Did not update cell, value was null");
            return false;
        }
    }

    /**
     * Gets a cell based on a name lookup to find the Row and value column property (int) to find Column.
     * May return null.
     * @param name
     * @param value
     * @param dataFormatter
     * @return Cell or null
     */
    protected Cell getTargetValueCell(String name, Object value, DataFormatter dataFormatter) {
        Sheet tSheet = getTargetSheet();

        ExcelCellAddress itemNameAddress = XLSHelper.findCellByName(tSheet, this.updater.getDataNameColumn(), name, dataFormatter);
        if (itemNameAddress == null) {
            LOG.debug("Could not find a place for [{} : {}]", name, value);
            return null;
        }

        ExcelCellAddress valueAddress = new ExcelCellAddress(this.updater.getDataValueColumn(), itemNameAddress.getRow());
        LOG.debug("Value address is : {}", ExcelHelper.convertAddressToHumanReadable(valueAddress.getCol(), valueAddress.getRow()));
        Row targetRow = tSheet.getRow(valueAddress.getRow());
        return XLSHelper.getCellByColumnIndex(targetRow, valueAddress.getCol());
    }
}
