package tech.jmcs.floortech.scheduling.app.extractor;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.Test;
import tech.jmcs.floortech.scheduling.app.extractor.exception.DataExtractorException;
import tech.jmcs.floortech.scheduling.app.extractor.model.ExtractedTableData;
import tech.jmcs.floortech.scheduling.app.util.ExcelCellAddress;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class GenericExcelHorizontalTableDataExtractorTest {

    @Test
    void testValidateGeneric() {
        Path excelFile = Paths.get("D:\\appdev\\floortech_env\\test_data\\example jobs\\19383\\Beam Listing 19383.xls");
        Map<ExcelCellAddress, String> tableLayout = new HashMap<>();
        tableLayout.put(new ExcelCellAddress(0, 2), "$data_start$");
        tableLayout.put(new ExcelCellAddress(0, 0), "BEAM SCHEDULE");
        tableLayout.put(new ExcelCellAddress(0, 1), "Beam");
        tableLayout.put(new ExcelCellAddress(1, 1), "Qty");
        tableLayout.put(new ExcelCellAddress(2, 1), "ID");
        tableLayout.put(new ExcelCellAddress(3, 1), "Length");
        Map<Integer, String> columnMap = new HashMap<>();
        columnMap.put(0, "Beam Type");
        columnMap.put(1, "Quantity");
        columnMap.put(2, "ID");
        columnMap.put(3, "Length");

        List<Integer> validRowDataList = Arrays.asList(1, 2, 3);

        GenericExcelHorizontalTableDataExtractor extractor = DataExtractorFactory.openExcelFileAsGenericList(excelFile, tableLayout, columnMap, validRowDataList);
        if (extractor == null) {
            System.out.println("Could not create generic extractor");
            return;
        }

        Boolean isValid = extractor.isValid();
        System.out.printf("Generic table is valid: %s", isValid);

        extractor.closeFile();
    }

    /**
     * TODO: Use the generic file extract test methods for documentation
     */
    @Test
    void testExtractGeneric() {
        extractGenericFileOne(); // beam list
        extractGenericFileTwo(); // truss list
        extractGenericFileThree_xls(); // sheets list xls
        extractGenericFileThree_xlsx(); // sheets list xlsx
    }

    private void extractGenericFileOne() {
        Path excelPath = Paths.get("D:\\appdev\\floortech_env\\test_data\\example jobs\\19383\\Beam Listing 19383.xls");
        Map<ExcelCellAddress, String> tableLayout = new HashMap<>();

        tableLayout.put(new ExcelCellAddress(0, 2), "$data_start$");
        tableLayout.put(new ExcelCellAddress(0, 0), "BEAM SCHEDULE");
        tableLayout.put(new ExcelCellAddress(0, 1), "Beam");
        tableLayout.put(new ExcelCellAddress(1, 1), "Qty");
        tableLayout.put(new ExcelCellAddress(2, 1), "ID");
        tableLayout.put(new ExcelCellAddress(3, 1), "Length");

        String beamColName = "Beam Type";
        String qtyColName = "Quantity";
        String idColName = "ID";
        String lenColName = "Length";

        Map<Integer, String> columnMap = new HashMap<>();
        columnMap.put(0, beamColName);
        columnMap.put(1, qtyColName);
        columnMap.put(2, idColName);
        columnMap.put(3, lenColName);

        List<Integer> validRowDataList = Arrays.asList(1, 2, 3);

        Map<Long, Map<String, Object>> dataMap = extractGeneric(excelPath, tableLayout, columnMap, validRowDataList);

        dataMap.forEach((id, map) -> {

            String beam = "";
            Double qty = 0d;
            String bid = "";
            Double len = 0d;

            try {
                beam = (String) map.get(beamColName);
            } catch (IllegalFormatConversionException | ClassCastException ex) {
            }
            try {
                qty = (Double) map.get(qtyColName);
            } catch (IllegalFormatConversionException | ClassCastException ex) {
            }
            try {
                bid = (String) map.get(idColName);
            } catch (IllegalFormatConversionException | ClassCastException ex) {
            }
            try {
                len = (Double) map.get(lenColName);
            } catch (IllegalFormatConversionException | ClassCastException ex) {
            }

            System.out.printf("Got record: %s | QTY: %s | ID: %s | LEN: %s \n", beam, qty, bid, len);

        });
    }

    private void extractGenericFileTwo() {
        Path excelPath = Paths.get("D:\\appdev\\floortech_env\\test_data\\example jobs\\19383\\LOT 5017 JOSEPH BANK BOULEVARD, BANKSIA GROVE - 19383.xls");
        Map<ExcelCellAddress, String> tableLayout = new HashMap<>();

        tableLayout.put(new ExcelCellAddress(0, 2), "$data_start$");
        tableLayout.put(new ExcelCellAddress(0, 0), "Joist Schedule");
        tableLayout.put(new ExcelCellAddress(0, 1), "ID");
        tableLayout.put(new ExcelCellAddress(3, 1), "Type");
        tableLayout.put(new ExcelCellAddress(7, 1), "STDs");

        String idColName = "ID";
        String numOfColName = "Number Of";
        String lengthColName = "Truss Length";
        String typeColName = "Type";
        String lEcColName = "Left EndCap";
        String rEcColName = "Right EndCap";
        String necColName = "NECs";
        String stdColName = "STDs";
        String hasPenoColName = "Has Peno";
        String webCutsColName = "Peno Web Cuts";

        Map<Integer, String> columnMap = new HashMap<>();
        columnMap.put(0, idColName);
        columnMap.put(1, numOfColName);
        columnMap.put(2, lengthColName);
        columnMap.put(3, typeColName);
        columnMap.put(4, lEcColName);
        columnMap.put(5, rEcColName);
        columnMap.put(6, necColName);
        columnMap.put(7, stdColName);
        // column 8 is gap
        columnMap.put(9, hasPenoColName);
        columnMap.put(10, webCutsColName);

        List<Integer> validRowDataList = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);

        List<Function<Sheet, String>> validationFunctions = new ArrayList<>();
        /**
         * Sequential Truss Id Validation Function
         */
        Function<Sheet, String> validationFunction_sequentialTrussId = sheet -> {
            String lastTrussId = "XX00";
            for (Row row : sheet) {
                if (row.getRowNum() <= 1) {
                    // skip first two rows.
                    continue;
                }

                Cell cellA = row.getCell(0);

                if (cellA.getCellType().equals(CellType.STRING)) {
                    String valA = cellA.getStringCellValue();
                    if (valA.isEmpty()) {
                        // skip empty
                        continue;
                    }

                    if (!valA.isEmpty()) {
                        Long lastId = extractTrussId(lastTrussId);
                        Long currId = extractTrussId(valA);
                        System.out.printf("LastID %s, Current ID %s \n", lastId, currId);

                        if (!currId.equals((lastId + 1L))) {
                            return "The Truss IDs were not sequential";
                        }

                        lastTrussId = valA;
                    }
                } else {
                    System.out.println("Ignoring row with non-string cell in column 0");
                }
            }
            return null; // empty string or null is no error
        };

        validationFunctions.add(validationFunction_sequentialTrussId);

        Map<Long, Map<String, Object>> dataMap = extractGeneric(excelPath, tableLayout, columnMap, validRowDataList, validationFunctions);

        dataMap.forEach((id, map) -> {

            String trussId = "";
            try {
                trussId = (String) map.get(idColName);
            } catch (IllegalFormatConversionException | ClassCastException ex) {
            }
            Double numOf = 0d;
            try {
                numOf = (Double) map.get(numOfColName);
            } catch (IllegalFormatConversionException | ClassCastException ex) {
            }
            Double len = 0d;
            try {
                len = (Double) map.get(lengthColName);
            } catch (IllegalFormatConversionException | ClassCastException ex) {
            }
            String trussType = "";
            try {
                trussType = (String) map.get(typeColName);
            } catch (IllegalFormatConversionException | ClassCastException ex) {
            }
            String lec = "";
            try {
                lec = (String) map.get(lEcColName);
            } catch (IllegalFormatConversionException | ClassCastException ex) {
            }
            String rec = "";
            try {
                rec = (String) map.get(rEcColName);
            } catch (IllegalFormatConversionException | ClassCastException ex) {
            }
            Double necCount = 0d;
            try {
                necCount = (Double) map.get(necColName);
            } catch (IllegalFormatConversionException | ClassCastException ex) {
            }
            Double stdCount = 0d;
            try {
                stdCount = (Double) map.get(stdColName);
            } catch (IllegalFormatConversionException | ClassCastException ex) {
            }
            Boolean hasPeno = false;
            try {
                hasPeno = (Boolean) map.get(hasPenoColName);
            } catch (IllegalFormatConversionException | ClassCastException ex) {
            }
            String cutWebs = "";
            try {
                cutWebs = (String) map.get(webCutsColName);
                if (cutWebs.isEmpty()) cutWebs = "None";
            } catch (IllegalFormatConversionException | ClassCastException ex) {
            }

            System.out.printf("Got record: %s | No.: %s | LEN: %s | Type: %s | LEC: %s | REC: %s | NECs: %s | STDs %s | Has Peno: %s | Cut Webs: %s \n", trussId, numOf, len, trussType, lec, rec, necCount, stdCount, hasPeno, cutWebs);
        });
    }

    private void extractGenericFileThree_xls() {
        Path excelPath = Paths.get("D:\\appdev\\floortech_env\\test_data\\SHEET LISTING.xls");
        this.extractGenericFileThree(excelPath);
    }

    private void extractGenericFileThree_xlsx() {
        Path excelPath = Paths.get("D:\\appdev\\floortech_env\\test_data\\SHEET LISTING.xlsx");
        this.extractGenericFileThree(excelPath);
    }

    private void extractGenericFileThree(Path excelPath) {

        Map<ExcelCellAddress, String> tableLayout = new HashMap<>();

        tableLayout.put(new ExcelCellAddress(0, 2), "$data_start$");
        tableLayout.put(new ExcelCellAddress(0, 0), "Sheets");
        tableLayout.put(new ExcelCellAddress(0, 1), "Length");
        tableLayout.put(new ExcelCellAddress(1, 1), "ID");
        tableLayout.put(new ExcelCellAddress(2, 1), "Qty");

        String lenColName = "Length";
        String idColName = "ID";
        String qtyColName = "Quantity";

        Map<Integer, String> columnMap = new HashMap<>();
        columnMap.put(0, lenColName);
        columnMap.put(1, idColName);
        columnMap.put(2, qtyColName);

        List<Integer> validRowDataList = Arrays.asList(1, 2);

        List<Function<Row, String>> recordValidationFunctions = new ArrayList();
        Function<Row, String> recordValidator = (row) -> {
            Cell idCell = row.getCell(1);
            if (idCell.getCellType().equals(CellType.STRING)) {
                String cVal = idCell.getStringCellValue().toLowerCase();
                if (cVal.contains("z")) {
                    return null;
                }
            }
            return "The ID Cell did not contain the letter 'Z'. Sheet ID's start with 'Z'";
        };
        recordValidationFunctions.add(recordValidator);

        Map<Long, Map<String, Object>> dataMap = extractGeneric(excelPath, tableLayout, columnMap, validRowDataList, null, recordValidationFunctions);

        dataMap.forEach((id, map) -> {

            String zoneId = "";
            try {
                zoneId = (String) map.get(idColName);
            } catch (IllegalFormatConversionException | ClassCastException ex) {
            }
            Double length = 0d;
            try {
                String lenStr = (String) map.get(lenColName);
                try {
                    length = Double.parseDouble(lenStr);
                } catch (NumberFormatException nex) {
                    System.out.println("Could not interpret length");
                }
            } catch (IllegalFormatConversionException | ClassCastException ex) {
            }
            Double qty = 0d;
            try {
                qty = (Double) map.get(qtyColName);
            } catch (IllegalFormatConversionException | ClassCastException ex) {
            }

            System.out.printf("Zone %s : %s x %smm \n", zoneId, qty, length);
        });
    }

    private Map<Long, Map<String, Object>> extractGeneric(Path excelPath, Map<ExcelCellAddress, String> tableLayout, Map<Integer, String> columnMap, List<Integer> validRowDataList) {
        return extractGeneric(excelPath, tableLayout, columnMap, validRowDataList, null);
    }

    private Map<Long, Map<String, Object>> extractGeneric(Path excelPath, Map<ExcelCellAddress, String> tableLayout, Map<Integer, String> columnMap, List<Integer> validRowDataList, List<Function<Sheet, String>> tableValidationFunctions) {
        return extractGeneric(excelPath, tableLayout, columnMap, validRowDataList, tableValidationFunctions, null);
    }

    private Map<Long, Map<String, Object>> extractGeneric(Path excelPath,
                                                          Map<ExcelCellAddress, String> tableLayout,
                                                          Map<Integer, String> columnMap,
                                                          List<Integer> validRowDataList,
                                                          List<Function<Sheet, String>> tableValidationFunctions,
                                                          List<Function<Row, String>> recordValidationFunctions) {
        GenericExcelHorizontalTableDataExtractor extractor = DataExtractorFactory.openExcelFileAsGenericList(excelPath, tableLayout, columnMap, validRowDataList);
        if (extractor == null) {
            System.out.println("Could not create generic extractor");
            fail("Check file name, exists, in use, etc....");
        }

        if (recordValidationFunctions != null) {
            recordValidationFunctions.forEach(f -> extractor.addAdditionalRecordValidation(f));
        }

        if (tableValidationFunctions != null) {
            tableValidationFunctions.forEach(f -> extractor.addAdditionalTableValidation(f));
        }

        try {
            extractor.extract();
        } catch (DataExtractorException e) {
            System.out.printf("Extraction error: %s \n", e.getMessage());
        }

        ExtractedTableData<Map<String, Object>> tableData = extractor.getDataAndFinish();
        if (tableData == null) {
            System.out.println("Table data was null");
            fail();
        }
        Map<Long, Map<String, Object>> dataMap = tableData.getData();

        return dataMap;
    }

    private Long extractTrussId(String trussId) {
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
}