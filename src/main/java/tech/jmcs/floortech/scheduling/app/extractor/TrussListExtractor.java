package tech.jmcs.floortech.scheduling.app.extractor;

import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.EndCapCW260;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Supports the original Truss List
 */
public class TrussListExtractor extends ExcelDataSourceExtractor<TrussData> {
    private static final Logger LOG = LoggerFactory.getLogger(TrussListExtractor.class);

    protected static final String TRUSS_SCHEDULE_TITLE_PT1_260 = "CW260";
    protected static final String TRUSS_SCHEDULE_TITLE_PT1_346 = "CW346";
    protected static final String TRUSS_SCHEDULE_TITLE_PT2 = " Joist Schedule";
    protected static final String TRUSS_SCHEDULE_TITLE_260 = TRUSS_SCHEDULE_TITLE_PT1_260 + TRUSS_SCHEDULE_TITLE_PT2;
    protected static final String TRUSS_SCHEDULE_TITLE_346 = TRUSS_SCHEDULE_TITLE_PT1_346 + TRUSS_SCHEDULE_TITLE_PT2;
    protected static final String COL_A_TITLE = "ID";
    protected static final String COL_B_TITLE = "No.";
    protected static final String COL_C_TITLE = "Truss Length";
    protected static final String COL_D_TITLE = "Type";
    protected static final String COL_E_TITLE = "Left End Cap";
    protected static final String COL_F_TITLE = "Right End Cap";
    protected static final String COL_G_TITLE = "NECs";
    protected static final String COL_H_TITLE = "STDs";
    protected static final String COL_I_TITLE = "";
    protected static final String COL_J_TITLE = "p.HAS PENO";
    protected static final String COL_K_TITLE = "p.CUT WEBS (count from: Left End Cap)";

    protected static final int TRUSS_DATA_ROW = 0;
    protected static final int TRUSS_TOTALS_ROW = 1;
    protected static final int LAST_ROW = 2;

    protected final Map<Integer, String> columnNames;

    protected TrussListExtractor(Path excelFile) throws IOException {
        super(excelFile);

        this.columnNames = new HashMap<>();
        this.columnNames.put(0, COL_A_TITLE);
        this.columnNames.put(1, COL_B_TITLE);
        this.columnNames.put(2, COL_C_TITLE);
        this.columnNames.put(3, COL_D_TITLE);
        this.columnNames.put(4, COL_E_TITLE);
        this.columnNames.put(5, COL_F_TITLE);
        this.columnNames.put(6, COL_G_TITLE);
        this.columnNames.put(7, COL_H_TITLE);
        this.columnNames.put(8, COL_I_TITLE);
        this.columnNames.put(9, COL_J_TITLE);
        this.columnNames.put(10, COL_K_TITLE);

    }

    @Override
    public Boolean isValid() {
        // check the truss listing xls layout and cell contents for expected layout

        Sheet firstSheet = xls.getSheetByNumber(0);

        DataFormatter dataFormatter = new DataFormatter();

        int c = -1;
        List<String> errors = new ArrayList();

        String lastTrussId = "XX00";
        for (Row row : firstSheet) {
            c += 1;
            if (c == 0) {
                if (!firstRowOk(row)) errors.add("Row 1 (Title) was not as expected. (Expected 'CWxxx Joist Schedule'");
            } else if (c == 1) {
                if (!secondRowOk(row)) errors.add("Row 2 (Column Headers) was not as expected.(Expected columns 'ID, 'No.', etc')");
            } else if (c == firstSheet.getLastRowNum()) {
                if (!lastRowOk(row)) errors.add("Last row (Grand Total) was not as expected. (Expected grand total row)");
            } else {
                /** Check all other rows: Truss Data Rows */
                if (c % 2 == 0) {
                    /** Even Rows Will be Truss Rows */

                    if (!trussIdOk(row, lastTrussId)) errors.add("Missing or Duplicated Truss ID found (Expected sequential ID's starting from 01)");
                    String trussId = dataFormatter.formatCellValue(row.getCell(0));
                    lastTrussId = trussId;
                    LOG.debug("Just found a new truss id: {}", lastTrussId);

                } else { // odd row, check is a total row
                    /** Odd Rows Will be Total Rows */
                    LOG.trace("No processing done on odd rows at the moment");
                }
            }

            if (!errors.isEmpty()) {
                // TODO: Finish error collection to display to user
                // For now just exit and report invalid file.
                return false;
            }
        }

        return true;
    }

    @Override
    public void extract() throws DataExtractorException {
        if (!isValid()) {
            LOG.warn("The Truss List XLS file was not valid.");
            // throw exception
            return;
        }

        if (!this.xls.isOpen()) {
            // log
            return;
        }

        ExtractedDataObject<TrussData> data = new ExtractedDataObject(DataSourceName.BEAM_LISTING.toString());

        Sheet sheet = this.xls.getSheetByNumber(0);
        int c = -1;
        TrussData lastTrussData = null;

        DataFormatter dataFormatter = new DataFormatter();
        int lastRowNum = sheet.getLastRowNum();
        for (Row row : sheet) {
            c += 1;
            if (c > 1) { // skip first two rows
                TrussData trussData = null;

                int rowType = getRowType(row, dataFormatter, lastRowNum);
                if (rowType == TRUSS_DATA_ROW) {
                    trussData = processTrussData(row, dataFormatter);

                } else if (rowType == TRUSS_TOTALS_ROW) {
                    LOG.debug("Total rows are not processed yet");

                } else if (rowType == LAST_ROW) {
                    LOG.debug("Grand Total row is not processed yet");
                }

                if (trussData != null) {
                    Long id = data.addData(trussData);
                }
            }
        }

        this.dataObject = data;
    }

    @Override
    public ExtractedDataObject<TrussData> getDataAndFinish() {
        this.closeFile();
        return this.dataObject;
    }

    /**
     * Checks that if the current row has a truss id, it is the expected id (sequential)
     * @param row
     * @param lastTrussId
     * @return
     */
    protected boolean trussIdOk(Row row, String lastTrussId) {
        Cell cellA = row.getCell(0);
        if (cellA.getCellType().equals(CellType.STRING)) {
            String valA = cellA.getStringCellValue();

            if (!valA.isEmpty()) {
                Long lastId = extractTrussId(lastTrussId);
                Long currId = extractTrussId(valA);
                LOG.trace("Truss ID comparison: Last {} | Current {}", lastId, currId);
                if (!currId.equals((lastId + 1L))) {
                    return false;
                }
            }
        }
        return true;
    }

    protected Long extractTrussId(String trussId) {
        Pattern rx = Pattern.compile("[a-zA-Z]{2}");
        Matcher matcher = rx.matcher(trussId);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "");
        }
        matcher.appendTail(sb);
        try {
            return Long.parseLong(sb.toString());
        } catch (NumberFormatException nex) {
            return null;
        }
    }

    /**
     * Check the last row is the grand total row
     * @param row
     * @return
     */
    protected boolean lastRowOk(Row row) {
        return true;
    }

    /**
     * Check the second row contains the expected column headings.
     * Strict check, extra columns at the end will also throw an error.
     * Another class to handle different layouts, eg. the new 'Truss Packing Group' column
     * @param row
     * @return
     */
    protected boolean secondRowOk(Row row) {

        int cellCount = row.getPhysicalNumberOfCells();
        if (cellCount != this.columnNames.size()) {
            LOG.debug("Cell count ({}) did not match expected {}", cellCount, this.columnNames.size());
            return false;
        }

        int n = -1;
        for (Cell cell : row) {
            n += 1;

            String colName = this.columnNames.get(n).toLowerCase().trim();

            if (cell.getCellType().equals(CellType.STRING)) {
                String strVal = cell.getStringCellValue().toLowerCase().trim().replaceAll(" +", " ");

                if (!colName.equals(strVal)) {
                    LOG.debug("Expected column: {}, got: {}", colName, strVal);
                    return false;
                }
            }
        }

        LOG.debug("Truss list column headers matched expected layout");
        return true;
    }

    /**
     * Check the first row contains an expected title
     * @param row
     * @return
     */
    protected boolean firstRowOk(Row row) {
        Cell cellA = row.getCell(0);
        if (cellA.getCellType().equals(CellType.STRING)) {
            String aLwr = cellA.getStringCellValue().toLowerCase();

            if (aLwr.equals(TRUSS_SCHEDULE_TITLE_260.toLowerCase())
                    || aLwr.equals(TRUSS_SCHEDULE_TITLE_346.toLowerCase())) {
                LOG.debug("Truss list title matched expected Strings");
                return true;
            }

            Pattern rx = Pattern.compile("([cC][wWxX])([a-zA-Z0-9]{3})([\\s]*)(Joist Schedule)", Pattern.CASE_INSENSITIVE);
            Matcher m = rx.matcher(aLwr);
            if (m.find()) {
                LOG.debug("Truss list title matched expected pattern");
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the row type
     * 0 = Truss Data Row
     * 1 = Truss Total Row
     * @param row
     * @param dataFormatter
     * @param lastRowNum
     * @return
     */
    protected int getRowType(Row row, DataFormatter dataFormatter, int lastRowNum) {
        if (lastRowNum == row.getRowNum()) {
            return LAST_ROW;
        }
        if (row.getRowNum() % 2 == 0) {
            return TRUSS_DATA_ROW;
        } else {
            return TRUSS_TOTALS_ROW;
        }
    }

    protected TrussData processTrussData(Row row, DataFormatter dataFormatter) {
        TrussData trussData = new TrussData();

        Cell a = row.getCell(0);
        Cell b = row.getCell(1);
        Cell c = row.getCell(2);
        Cell d = row.getCell(3);
        Cell e = row.getCell(4);
        Cell f = row.getCell(5);
        Cell g = row.getCell(6);
        Cell h = row.getCell(7);
        Cell i = row.getCell(8);
        Cell j = row.getCell(9);
        Cell k = row.getCell(10);

        /**
         * Get Data from cells
         */

        String id = dataFormatter.formatCellValue(a);
        String type = dataFormatter.formatCellValue(d);
        String leftEc = dataFormatter.formatCellValue(e);
        String rightEc = dataFormatter.formatCellValue(f);
        String cutWebs = dataFormatter.formatCellValue(k);

        Long numberOf = 0l;
        if (b.getCellType().equals(CellType.NUMERIC)) {
            long valB = (long) b.getNumericCellValue();
            numberOf = valB;
        }

        Long length = 0l;
        if (c.getCellType().equals(CellType.NUMERIC)) {
            long valC = (long) c.getNumericCellValue();
            length = valC;
        }

        Boolean hasPeno = false;
        if (j.getCellType().equals(CellType.BOOLEAN)) {
            hasPeno = c.getBooleanCellValue();
        }

        /**
         * Convert Data
         */

        Pattern ptnCutWeb = Pattern.compile("([0-9])[\\s]*[+][\\s]*([0-9])"); // matches pattern like 1+2 or 1 + 2
        Matcher matcherCutWeb = ptnCutWeb.matcher(cutWebs);
        Integer cutWeb1 = 0;
        Integer cutWeb2 = 0;
        while (matcherCutWeb.find()) {
            try {
                cutWeb1 = Integer.parseInt(matcherCutWeb.group(0));
                cutWeb2 = Integer.parseInt(matcherCutWeb.group(1));
            } catch (NumberFormatException nex) {
                LOG.warn("Could not interpret webs to cut");
            }
        }

        EndCapCW260 leftEndcap = EndCapCW260.fromName(leftEc);
        EndCapCW260 rightEndcap = EndCapCW260.fromName(rightEc);

        /**
         * Set Data to Truss Data Object
         */

        trussData.setTrussId(id);
        trussData.setType(type);
        trussData.setLeftEndcap(leftEndcap);
        trussData.setRightEndcap(rightEndcap);
        trussData.getPenetrationWebCuts().add(cutWeb1);
        trussData.getPenetrationWebCuts().add(cutWeb2);
        trussData.setQty(numberOf);
        trussData.setLength(length);
        trussData.setHasAirconPenetration(hasPeno);

        return trussData;
    }
}
