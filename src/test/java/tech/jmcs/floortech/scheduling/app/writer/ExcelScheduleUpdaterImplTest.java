package tech.jmcs.floortech.scheduling.app.writer;

import org.junit.jupiter.api.Test;
import tech.jmcs.floortech.scheduling.app.exception.ExcelScheduleWriterException;
import tech.jmcs.floortech.scheduling.app.util.ExcelCellAddress;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExcelScheduleUpdaterImplTest {

    @Test
    void testUpdateExcelSchedule() {
        Path scheduleFilePath = Paths.get("D:\\appdev\\floortech_env\\test_data\\1FLOORTECH-JOB TEMPLATE- 17-NOV-20.xls");
        try {
            ExcelScheduleUpdaterImpl updater = new ExcelScheduleUpdaterImpl(scheduleFilePath);
            updater.setTargetSheetNumber(0);

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("200 UB 18 Galvanised", 1.1d);
            dataMap.put("250 UB 37 Galvanised", 1.2d);
            dataMap.put("250 PFC Galvanised", 1.3d);
            dataMap.put("200 UB 18 Black", 1.4d);
            dataMap.put("250 UB 26 Black", 1.5d);
            dataMap.put("410 UB 54 Black", 1.6d);
            // test does not currently work with these:
            dataMap.put("500 UC 00 Black", 10000.01d); // unplaced value
            dataMap.put("501 UC 00 Black", 22.01d); // unplaced value
            dataMap.put("502 UC 00 Black", 33.01d); // unplaced value

            ExcelScheduleUpdateConfirmer confirmer = updater.updateSchedule(dataMap);
            if (confirmer == null) {
                System.out.println("No issues during update");
            } else {
                System.out.println("Some issues during update");
            }

            Map<String, Object> noMatchProblems = confirmer.getNotFoundProblems();
            int count1 = 0;
            for (Map.Entry<String, Object> mapEntry : noMatchProblems.entrySet()) {
                String key = mapEntry.getKey();
                Object v = mapEntry.getValue();
                System.out.printf("NO MATCH | '%s' : '%s' \n", key, v);
                // User will be prompted about the data, can choose a place to locate it (by group?)
                // probably easiest by group, allow user to choose group and then have a method to find
                // the end of that group.
                Object realValue = dataMap.get(key);

                if (++count1 % 2 == 1) {
                    System.out.printf("Add New Cell: %s, actual: %s for [%s]\n", v, realValue, key);
                    confirmer.forceOverwrite_addNewCell(key, realValue, new ExcelCellAddress(2, 140));
                } else {
                    System.out.printf("Replace cell: %s, actual: %s for [%s]\n", v, realValue, key);
                    confirmer.forceOverwrite_replaceCell(key, realValue, new ExcelCellAddress(2, 140));
                }
            }
            Map<String, Object> dataConflictProblems = confirmer.getConflictProblems();
            int count2 = 0;
            for (Map.Entry<String, Object> entry : dataConflictProblems.entrySet()) {
                String name = entry.getKey();
                Object value = entry.getValue(); // show to user what current cell value is (string)
                Object realValue = dataMap.get(name);
                System.out.printf("DATA CONFLICT | '%s' : Existing: '%s', New: '%s' \n", name, value, realValue);

                if (++count2 % 2 == 0) {
                    System.out.printf("Adding: %s + %s for [%s]\n", value, realValue, name);
                    confirmer.forceOverwrite_addToCurrentValue(name, realValue);
                } else {
                    System.out.printf("Replacing: %s with %s for [%s] \n", value, realValue, name);
                    confirmer.forceOverwrite_replaceCurrentValue(name, realValue);
                }
            }
            List<String> errors = confirmer.getUpdateErrors();
            errors.forEach(e -> {
                System.out.printf("GENERAL | %s \n", e);
            });

            // user would be prompted with the issues, choose what to do (new place, replace, add, ignore) each problem
            // as needed and then the sheet would be re-updated...

            System.out.println("Completing update (with unresolved problems)");
            updater.completeUpdate();

        } catch (FileNotFoundException e) {
            System.out.println("Could not find excel file, does it exist or is the file in use?");
            fail();
        } catch (IOException e) {
            System.out.println("Could not open excel file, is the file in use?");
            fail();
        } catch (ExcelScheduleWriterException e) {
            System.out.printf("Could not write data: %s | %s \n", e.getDataTargetName(), e.getMessage());
            fail();
        }



    }
}