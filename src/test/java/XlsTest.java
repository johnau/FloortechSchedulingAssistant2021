import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import tech.jmcs.floortech.scheduling.app.util.XLSHelper;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class XlsTest {

    @Test
    public void test2() {
//        List<String> a = new ArrayList<>();
//        a.add(null);
//        System.out.println(a.size());
//        for (String s : a) {
//            System.out.println(s);
//        }
//
//        Long length = 28888700l;
//        String sheetSuffix = "m length sheet";
//        Double ld = length / 1000d;
//        String result = new DecimalFormat("#.#").format(ld) + sheetSuffix;
//        System.out.println(result);
//
//        String s = "1, 2 | alskdjflksdjfd";
//        String[] p = s.split("[|]");
//        for (String s1 : p) {
//            System.out.printf("'%s'\n", s1.trim());
//        }

        double a = 13.3d;
        double b = 13.8d;



        System.out.println(round45(a));
        System.out.println(round45(b));

//        Pattern ptnCutWeb = Pattern.compile("([0-9]+)[\\s]*[+][\\s]*([0-9]+)"); // matches pattern like 1+2 or 1 + 2
//        Matcher matcherCutWeb = ptnCutWeb.matcher("6+7");
//        Integer cutWeb1 = 0;
//        Integer cutWeb2 = 0;
//        while (matcherCutWeb.find()) {
//            try {
//                System.out.printf("Got web cuts at : %s + %s\n", matcherCutWeb.group(1), matcherCutWeb.group(2));
//                cutWeb1 = Integer.parseInt(matcherCutWeb.group(1));
//                cutWeb2 = Integer.parseInt(matcherCutWeb.group(2));
//                System.out.printf("Got web cuts at : %s + %s\n", cutWeb1, cutWeb2);
//                break;
//            } catch (NumberFormatException nex) {
//                System.out.printf("Could not interpret webs to cut\n");
//            }
//        }
    }

    private double round45(double x) {
        double f = Math.floor(x / 4.5) + 1;
        return f*4.5;
    }

    @Test
    public void testNewExcelReader() {
        InputStream is = null;
        try {
            is = new FileInputStream(new File("E:\\appdev\\floortech_env\\test_data\\Construction Worksheet1.xlsm"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (is == null) {
            System.out.println("Could not open file");
            return;
        }
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(5)    // number of rows to keep in memory (defaults to 10)
                .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                .open(is);

        Map<Integer, String> columnHeaders = new HashMap();
        for (Sheet sheet : workbook){
            System.out.println("Processing workbook");
            if (sheet.getSheetName().equalsIgnoreCase("jobs list")) {
                for (Row r : sheet) {
                    System.out.println("Processing sheet: " + sheet.getSheetName());
                    if (r.getRowNum() == 0) {
                        for (Cell c : r) {
                            columnHeaders.put(c.getColumnIndex(), c.getStringCellValue());
                        }
                        break;
                    }
                }
                break;
            }
        }
        columnHeaders.forEach((col, s) -> {

            System.out.printf("COL %s = '%s'\n", col, s);

        });
    }

    @Test
    public void tetstes() {
//        String s = "sldkjf     lskdjf  sldkfj";
//        s = s.toLowerCase().trim(); // lowercase and trim
//        s = s.replaceAll("[\\s]+", " "); // remove excess spaces
//        System.out.println(s);

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("hh:mm:ss dd-MM-yy");
        System.out.println(df.format(date));
    }

    @Test
    void testXlsRead() {
        Path excelFile = Paths.get("D:\\appdev\\floortech_env\\test_data\\SHEET LISTING.xls");
        File f = excelFile.toFile();
        if (!f.exists()) {
            System.out.println("Could not find file");
            return;
        }

        Workbook workbook;
        Iterator<Sheet> sheetIterator;
        Sheet firstSheet;

        try {
            workbook = WorkbookFactory.create(f);
            workbook.close();
        } catch (IOException e) {
            System.out.println("Could not get workbook");
            fail();
            return;
        }

        sheetIterator = workbook.sheetIterator();
        try {
            firstSheet = workbook.getSheetAt(0);
        } catch (IllegalArgumentException aex) {
            fail("Sheet out of range?");
            return;
        }

        String name = firstSheet.getSheetName();
        System.out.printf("Found sheet: %s \n", name);

        Row firstRow = firstSheet.getRow(0);
        Cell firstCell = XLSHelper.getCellByColumnIndex(firstRow, 0);
        if (firstCell == null) fail("Could not get the first cell");

        DataFormatter dataFormatter = new DataFormatter();
        String title = dataFormatter.formatCellValue(firstCell);
        System.out.printf("Table title: %s \n", title);

        int sc = 0;
        while (sheetIterator.hasNext()) {
            Sheet nSheet = sheetIterator.next();
            System.out.printf("At sheet %d: '%s'", ++sc, nSheet.getSheetName());
        }

    }

    @Test
    void randomTest() {
        try {
            Long l = Long.parseLong("");
        } catch (NumberFormatException nex) {
            System.out.println("Could not convert empty string to long");
        }
    }

    @Test
    void testAddCellsAndWrite() {
        // open existing excel file
        Path resourceDirectory = Paths.get("src","test","resources");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        Path excelFile = Paths.get(absolutePath, "ADDROW.xls");
        File f = excelFile.toFile();
        if (!f.exists()) {
            System.out.println("Could not find file");
            fail();
        }

        FileInputStream fis;
        Workbook workbook;
        Iterator<Sheet> sheetIterator;
        Sheet firstSheet;

        try {
            fis = new FileInputStream(f);
            workbook = WorkbookFactory.create(fis);
        } catch (FileNotFoundException e) {
            fail();
            return;
        } catch (IOException e) {
            System.out.println("Could not get workbook");
            fail();
            return;
        }

        sheetIterator = workbook.sheetIterator();
        try {
            firstSheet = workbook.getSheetAt(0);
        } catch (IllegalArgumentException aex) {
            fail("Sheet out of range?");
            return;
        }

        int rowCount = firstSheet.getLastRowNum();
        System.out.printf("Row count is: %s", rowCount);

        // insert three rows
        firstSheet.shiftRows(120, rowCount, 3);

        // add three consecutive rows with data

        for (int i = 0; i < 3; i++) {
            Row newRow = firstSheet.createRow(120 + i);
            Cell c1 = newRow.createCell(1);
            Cell c2 = newRow.createCell(2);
            c1.setCellValue(new Random().nextLong());
            c2.setCellValue(new Random().nextLong());
        }

        rowCount = firstSheet.getLastRowNum();
        System.out.printf("Row count is: %s", rowCount);

        // insert row
        firstSheet.shiftRows(115, rowCount, 1);

        // create row contents
        Row newRow = firstSheet.createRow(115);
        Cell c1 = newRow.createCell(1);
        Cell c2 = newRow.createCell(2);
        c1.setCellValue(new Random().nextLong());
        c2.setCellValue(new Random().nextLong());

        try {
            fis.close();
        } catch (IOException e) {
            fail();
            return;
        }

        // write
        if (workbook != null) {
            try (FileOutputStream fos = new FileOutputStream(f)) {
                workbook.write(fos);
                workbook.close();
            } catch (FileNotFoundException e) {
                System.out.printf("Could not find workbook: %s \n", e.getMessage());
                fail();
            } catch (IOException e) {
                System.out.printf("Could not access workbook: %s \n", e.getMessage());
            }
        }
    }
}
