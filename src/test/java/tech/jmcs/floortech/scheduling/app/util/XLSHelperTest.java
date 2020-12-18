package tech.jmcs.floortech.scheduling.app.util;

import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XLSHelperTest {
    private String getResourcePath() {
        Path resourceDirectory = Paths.get("src","test","resources");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        return absolutePath;
    }

    @Test
    void findCellByName() {
        List<String> testCells = new ArrayList<>();
        testCells.add("BEAMS / BRACKETS");
        testCells.add("EDGE ANGLES & FLASHINGS");
        testCells.add("FORM SHEETS");
        testCells.add("CRANE HIRE & SITE WELDING");
        testCells.add("LABOUR");
        try {
            XLSUtility xls = new XLSUtility(Paths.get(getResourcePath(), "1FLOORTECH-JOB TEMPLATE- 17-NOV-20.xls"));
            Sheet sheet = xls.getSheet(0);
            DataFormatter formatter = new DataFormatter();

            testCells.forEach( c -> {
                ExcelCellAddress cellAdd = XLSHelper.findCellByName(sheet, 1, c, formatter);
                if (cellAdd == null) {
                    fail();
                }
                System.out.printf("Found: %s @ %s:%s\n", c, cellAdd.getCol(), cellAdd.getRow());
            });
        } catch (FileNotFoundException e) {
            fail();
        } catch (Exception e) {
            fail();
        }
    }
}