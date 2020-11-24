package tech.jmcs.floortech.scheduling.app.extractor;

import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.util.XLSUtility;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * BEAM LIST IS CURRENTLY STRUCTURED AS:
 * ROW 1: A= "BEAM LISTING"
 * ROW 2: A= "Beam", B= "Qty",C= "ID", D= "Length"
 * ROW 3: A= First Beam Group Name
 * ROW 4-(n-1): Data Rows [B,C & D = Data cells] OR Beam Group Name [A= Beam Group Name]
 * ROW n: Grand Total Row (Currently Ignored!)
 */
public class BeamListExtractor extends ExcelDataSourceExtractor<BeamData> {
    private static final Logger LOG = LoggerFactory.getLogger(BeamListExtractor.class);

    protected static final String BEAM_SCHEDULE_TITLE = "BEAM SCHEDULE";
    protected static final String COL_A_TITLE = "Beam";
    protected static final String COL_B_TITLE = "Qty";
    protected static final String COL_C_TITLE = "ID";
    protected static final String COL_D_TITLE = "Length";

    protected static final int ROW_TYPE_BEAM_GROUP = 0;
    protected static final int ROW_TYPE_BEAM_DATA = 1;
    protected static final int ROW_TYPE_BEAM_GROUP_TOTAL = 2;
    protected static final int ROW_TYPE_GRAND_TOTAL = 3;

    protected BeamListExtractor(Path excelFile) throws IOException {
        super(excelFile);
    }

    /**
     * Check the provided data source (Excel File) is valid
     * @return
     */
    @Override
    public Boolean isValid() {
        // check the beam listing xls layout and cell contents for expected layout

        Sheet firstSheet = xls.getSheetByNumber(0);

        int c = 0;
        List<String> errors = new ArrayList();
        for (Row row : firstSheet) {
            if (c == 0) {
                if (!firstRowOk(row)) errors.add("Row 1 (Title) was not as expected. (Expected 'BEAM LISTING')");
            } else if (c == 1) {
                if (!secondRowOk(row)) errors.add("Row 2 (Column headers) was not as expected. (Expected columns: 'Beam', 'Qty', 'ID', 'Length')");
            } else if (c == 2) {
                if (!thirdRowNotEmpty(row)) errors.add("Row 3 (First Beam Group Row) was not as expected. (Expected data, is the table empty?)");
            } else if (c == firstSheet.getLastRowNum()) {
                if (!lastRowOk(row)) errors.add("Last Row (Totals) was not as expected. (Expected a cell with 'mm' length measurement");
            }

            if (!errors.isEmpty()) {
                // TODO: Finish error collection to display to user
                return false;
            }
            c += 1;
        }

        // possibly check the list has contents and not an empty list

        return true;
    }

    /**
     * Extract data source into ExtractedDataObject
     * @return
     */
    @Override
    public void extract() throws DataExtractorException {
        if (!isValid()) {
            LOG.warn("The Beam List XLS file was not valid.");
            // throw exception
            return;
        }

        if (!this.xls.isOpen()) {
            // log
            return;
        }

        ExtractedDataObject<BeamData> data = new ExtractedDataObject(DataSourceName.BEAM_LISTING.toString());

        Sheet sheet = this.xls.getSheetByNumber(0);

        int c = -1;
        String currentBeamGroup = "";
        List<Long> beamsInGroup = new ArrayList();
        DataFormatter dataFormatter = new DataFormatter();
        int lastRowNum = sheet.getLastRowNum();
        for (Row row : sheet) {
            c += 1;
            if (c > 1) { // skip first two rows
                int rowType = getRowType(row, dataFormatter, lastRowNum);
                BeamData beamData = null;

                if (rowType == ROW_TYPE_BEAM_GROUP) {

                    /** Set the current beam group name and reset the beam group ids list */

                    Cell cellA = row.getCell(0);
                    String aStrVal = dataFormatter.formatCellValue(cellA);
                    if (!aStrVal.isEmpty()) {
                        currentBeamGroup = aStrVal;
                        beamsInGroup = new ArrayList<>();
                    }

                } else if (rowType == ROW_TYPE_BEAM_DATA) {
                    /** Populate new BeamData object */

                    beamData = this.processBeamData(row, currentBeamGroup);

                } else if (rowType == ROW_TYPE_BEAM_GROUP_TOTAL) {
                    /** Add up Totals and Compare */

                    long expLength = 0L;
                    long expQty = 0L;
                    for(Map.Entry<Long, BeamData> e : data.getData().entrySet()) {
                        Long id = e.getKey();
                        if (beamsInGroup.contains(id)) {
                            BeamData bd = e.getValue();

                            Long qty = bd.getQuantity();
                            expQty += qty;

                            Long bLen = bd.getLength();
                            if (qty == 1L) {
                                expLength += bLen;
                            } else {
                                expLength += bLen * qty;
                            }
                        }
                    }

                    if (!this.totalRowAndDataMatched(row, dataFormatter, expQty, expLength)) {
                        LOG.warn(" !!! Malformed Excel Table: Totals did not match for {}", currentBeamGroup);
                        throw new BeamListDataException("There was an issue with the data for: " + currentBeamGroup);
                    } else {
                        LOG.debug("{} Matched OK! {} Beams in group, exp len: {}, exp qty: {} ", currentBeamGroup,beamsInGroup.size(), expLength, expQty);
                    }
                } else if (rowType == ROW_TYPE_GRAND_TOTAL) {

                }

                if (beamData != null) {
                    Long id = data.addData(beamData);
                    beamsInGroup.add(id);
                }
            }
        }

        this.dataObject = data;
    }

    @Override
    public ExtractedDataObject<BeamData> getDataAndFinish() {
        this.closeFile();
        return dataObject;
    }

    /**
     * Check the first cell of the first row matches the title (eg. "BEAM SCHEDULE")
     * @param row
     * @return
     */
    protected Boolean firstRowOk(Row row) {
        Cell cell = row.getCell(0);
        if (cell.getCellType().equals(CellType.STRING)) {
            String val = cell.getStringCellValue();
            if (val.toUpperCase().trim().equals(BEAM_SCHEDULE_TITLE)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check the second row has the expected column layout:
     * (eg. A: Beam, B: Qty, C: ID, D: Length)
     * @param row
     * @return
     */
    protected Boolean secondRowOk(Row row) {
        Cell cellA = row.getCell(0);
        Cell cellB = row.getCell(1);
        Cell cellC = row.getCell(2);
        Cell cellD = row.getCell(3);

        if (cellA.getCellType().equals(CellType.STRING)
                && cellB.getCellType().equals(CellType.STRING)
                && cellC.getCellType().equals(CellType.STRING)
                && cellD.getCellType().equals(CellType.STRING)) {

            String valA_lwr = cellA.getStringCellValue().toLowerCase();
            String valB_lwr = cellB.getStringCellValue().toLowerCase();
            String valC_lwr = cellC.getStringCellValue().toLowerCase();
            String valD_lwr = cellD.getStringCellValue().toLowerCase();

            if (valA_lwr.equals(COL_A_TITLE.toLowerCase())
                    && valB_lwr.equals(COL_B_TITLE.toLowerCase())
                    && valC_lwr.equals(COL_C_TITLE.toLowerCase())
                    && valD_lwr.equals(COL_D_TITLE.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check the first cell of the third row is not empty (empty table - no data)
     * @param row
     * @return
     */
    protected Boolean thirdRowNotEmpty(Row row) {
        Cell cellA = row.getCell(0);
        if (cellA.getCellType().equals(CellType.STRING)) {
            String valA = cellA.getStringCellValue();
            if (!valA.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check the last cell (cell 'D') of the last row has " mm" in it
     * @param row
     * @return
     */
    protected Boolean lastRowOk(Row row) {
        Cell cellD = row.getCell(3);
        if (cellD.getCellType().equals(CellType.STRING)) {
            String valD = cellD.getStringCellValue();
            if (valD.toLowerCase().contains(" mm")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Detects the type of row of beam list.
     * 0 = Beam Group Row (eg 250 UB 26)
     * 1 = Beam Data Row (qty, id, length)
     * 2 = Beam Group Totals Row
     * 3 = Grand Total Row
     * @param row
     * @param dataFormatter
     * @return
     */
    protected int getRowType(Row row, DataFormatter dataFormatter, int lastRowNum) {
        if (row.getRowNum() == lastRowNum) {
            return ROW_TYPE_GRAND_TOTAL;
        }

        Cell cellA = row.getCell(0);
        String aStrVal = dataFormatter.formatCellValue(cellA);
        if (!aStrVal.isEmpty()) {
            return ROW_TYPE_BEAM_GROUP;
        }

        Cell cellC = row.getCell(2);
        Cell cellD = row.getCell(3);
        String cStrVal = dataFormatter.formatCellValue(cellC);
        String dStrVal = dataFormatter.formatCellValue(cellD).toLowerCase();
        if (cStrVal.isEmpty() && dStrVal.contains(" mm")) {
            return ROW_TYPE_BEAM_GROUP_TOTAL;
        }

        return ROW_TYPE_BEAM_DATA;
    }

    protected boolean totalRowAndDataMatched(Row row, DataFormatter dataFormatter, Long expQtyTotal, Long expLenTotal) {
        // qty total
        Cell cellB = row.getCell(1);
        if (cellB.getCellType().equals(CellType.NUMERIC)) {
            Long valB = (long) cellB.getNumericCellValue();
            if (!valB.equals(expQtyTotal)) {
                LOG.debug("Expected Qty: {} did not match value read from table: {}", expQtyTotal, valB);
                return false;
            }
        }

        // len total
        Cell cellD = row.getCell(3);
        String dStrVal = dataFormatter.formatCellValue(cellD).toLowerCase();

        dStrVal = dStrVal.replace(" mm", "");
        dStrVal = dStrVal.replace(",", "");
        dStrVal.replace(" ", "");

        try {
            Long lenTotal = Long.parseLong(dStrVal);
            if (!lenTotal.equals(expLenTotal)) {
                LOG.debug("Expected Length: {} did not match value read from table: {}", expLenTotal, lenTotal);
                return false;
            }
        } catch (NumberFormatException nex) {
            LOG.debug("Number format exception on Length Total Row");
            return false;
        }

        return true;
    }

    /**
     * Process a row in the beam listing table.  Assumes a data row is passed in (not Title or Column Header row)
     * @param row
     * @param currentBeamGroup
     * @return
     */
    protected BeamData processBeamData(Row row, String currentBeamGroup) {
        BeamData beamData = new BeamData(currentBeamGroup);
        Cell cellB = row.getCell(1);
        Cell cellC = row.getCell(2);
        Cell cellD = row.getCell(3);

        Long qty = 0L;
        String id = "";
        Long length = 0L;

        if (cellB.getCellType().equals(CellType.NUMERIC)) {
            qty = (long) cellB.getNumericCellValue();
        }

        if (cellC.getCellType().equals(CellType.STRING)) {
            id = cellC.getStringCellValue();
        }

        if (cellD.getCellType().equals(CellType.NUMERIC)) {
            length = (long) cellD.getNumericCellValue();
        }

        beamData.setBeamId(id);
        beamData.setLength(length);
        beamData.setQuantity(qty);

        return beamData;
    }

}
