package tech.jmcs.floortech.scheduling;

import org.junit.jupiter.api.Test;
import tech.jmcs.floortech.scheduling.app.exception.ExcelScheduleWriterException;
import tech.jmcs.floortech.scheduling.app.schedulewriter.ExcelScheduleUpdateConfirmer;
import tech.jmcs.floortech.scheduling.app.schedulewriter.BasicExcelScheduleUpdater;
import tech.jmcs.floortech.scheduling.app.settings.SettingsHolder;
import tech.jmcs.floortech.scheduling.app.settings.SettingsLoader;
import tech.jmcs.floortech.scheduling.ui.ExtractedDataHolderFX;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;

public class CommitDataTest {

    @Test
    public void testCommitData() {
        SettingsHolder settings = new SettingsHolder();
        SettingsLoader loader = new SettingsLoader(settings, null);
        try {
            loader.load();
        } catch (IOException e) {
            return;
        }


        ExtractedDataHolderFX dataHolder = new ExtractedDataHolderFX(settings);

        Map<String, String> jsonDatas = new HashMap<>();
        jsonDatas.put("beam", beamJson());
        jsonDatas.put("truss", trussData());
        dataHolder.importJsonData(jsonDatas);

        Path excelSchedulePath = Paths.get("E:\\appdev\\floortech_env\\test_data\\testquotefile.xls");
        if (!excelSchedulePath.toFile().exists()) {
            System.out.println("Test not run, file missing");
            return;
        }

        Map<String, Object> scheduleData = dataHolder.convertToScheduleMap();

        try {
            BasicExcelScheduleUpdater updater = new BasicExcelScheduleUpdater(excelSchedulePath);
            ExcelScheduleUpdateConfirmer confirm = updater.updateSchedule(scheduleData);
            Map<String, Object> errorsConflict = confirm.getConflictProblems();
            Map<String, Object> errorsNotFound = confirm.getNotFoundProblems();
            List<String> errorsUpdate = confirm.getUpdateErrors();
            updater.completeUpdate();

            errorsConflict.forEach((n, v) -> System.out.printf("Error (Conflict): %s : %s\n", n, v));
            errorsNotFound.forEach((n, v) -> System.out.printf("Error (Not found): %s : %s\n", n, v));
            errorsUpdate.forEach(s -> System.out.printf("Error (Not found): %s\n", s));

        } catch (IOException | ExcelScheduleWriterException e) {
            System.out.println("Could not completed quote sheet update: " + e.getMessage());
        }

    }

    private String beamJson() {
        return "{\n" +
                "  \"0\" : {\n" +
                "    \"beamType\" : \"150 PFC\",\n" +
                "    \"quantity\" : 1,\n" +
                "    \"beamId\" : \"P01\",\n" +
                "    \"length\" : 440,\n" +
                "    \"treatment\" : \"BLACK\"\n" +
                "  },\n" +
                "  \"1\" : {\n" +
                "    \"beamType\" : \"200 UB 18\",\n" +
                "    \"quantity\" : 1,\n" +
                "    \"beamId\" : \"B12\",\n" +
                "    \"length\" : 1010,\n" +
                "    \"treatment\" : \"BLACK\"\n" +
                "  },\n" +
                "  \"2\" : {\n" +
                "    \"beamType\" : \"200 UB 18\",\n" +
                "    \"quantity\" : 1,\n" +
                "    \"beamId\" : \"B15\",\n" +
                "    \"length\" : 1620,\n" +
                "    \"treatment\" : \"BLACK\"\n" +
                "  },\n" +
                "  \"3\" : {\n" +
                "    \"beamType\" : \"250 UB 26\",\n" +
                "    \"quantity\" : 1,\n" +
                "    \"beamId\" : \"B01\",\n" +
                "    \"length\" : 3155,\n" +
                "    \"treatment\" : \"BLACK\"\n" +
                "  },\n" +
                "  \"4\" : {\n" +
                "    \"beamType\" : \"250 UB 26\",\n" +
                "    \"quantity\" : 1,\n" +
                "    \"beamId\" : \"B05\",\n" +
                "    \"length\" : 2649,\n" +
                "    \"treatment\" : \"BLACK\"\n" +
                "  },\n" +
                "  \"5\" : {\n" +
                "    \"beamType\" : \"250 UB 26\",\n" +
                "    \"quantity\" : 1,\n" +
                "    \"beamId\" : \"B06\",\n" +
                "    \"length\" : 3245,\n" +
                "    \"treatment\" : \"BLACK\"\n" +
                "  },\n" +
                "  \"6\" : {\n" +
                "    \"beamType\" : \"250 UB 26\",\n" +
                "    \"quantity\" : 1,\n" +
                "    \"beamId\" : \"B09\",\n" +
                "    \"length\" : 3600,\n" +
                "    \"treatment\" : \"BLACK\"\n" +
                "  },\n" +
                "  \"7\" : {\n" +
                "    \"beamType\" : \"250 UB 26\",\n" +
                "    \"quantity\" : 1,\n" +
                "    \"beamId\" : \"B13\",\n" +
                "    \"length\" : 1990,\n" +
                "    \"treatment\" : \"BLACK\"\n" +
                "  },\n" +
                "  \"8\" : {\n" +
                "    \"beamType\" : \"250 UB 26\",\n" +
                "    \"quantity\" : 1,\n" +
                "    \"beamId\" : \"B14\",\n" +
                "    \"length\" : 3116,\n" +
                "    \"treatment\" : \"BLACK\"\n" +
                "  },\n" +
                "  \"9\" : {\n" +
                "    \"beamType\" : \"250 UB 26\",\n" +
                "    \"quantity\" : 1,\n" +
                "    \"beamId\" : \"B16\",\n" +
                "    \"length\" : 2770,\n" +
                "    \"treatment\" : \"BLACK\"\n" +
                "  },\n" +
                "  \"10\" : {\n" +
                "    \"beamType\" : \"250 UB 37\",\n" +
                "    \"quantity\" : 1,\n" +
                "    \"beamId\" : \"B08\",\n" +
                "    \"length\" : 4718,\n" +
                "    \"treatment\" : \"BLACK\"\n" +
                "  },\n" +
                "  \"11\" : {\n" +
                "    \"beamType\" : \"250 UB 37\",\n" +
                "    \"quantity\" : 1,\n" +
                "    \"beamId\" : \"B10\",\n" +
                "    \"length\" : 4690,\n" +
                "    \"treatment\" : \"BLACK\"\n" +
                "  },\n" +
                "  \"12\" : {\n" +
                "    \"beamType\" : \"250 UC 73\",\n" +
                "    \"quantity\" : 1,\n" +
                "    \"beamId\" : \"B02\",\n" +
                "    \"length\" : 4900,\n" +
                "    \"treatment\" : \"BLACK\"\n" +
                "  },\n" +
                "  \"13\" : {\n" +
                "    \"beamType\" : \"250 UC 73\",\n" +
                "    \"quantity\" : 1,\n" +
                "    \"beamId\" : \"B03\",\n" +
                "    \"length\" : 5951,\n" +
                "    \"treatment\" : \"BLACK\"\n" +
                "  },\n" +
                "  \"14\" : {\n" +
                "    \"beamType\" : \"250 UC 73\",\n" +
                "    \"quantity\" : 1,\n" +
                "    \"beamId\" : \"B04\",\n" +
                "    \"length\" : 5870,\n" +
                "    \"treatment\" : \"BLACK\"\n" +
                "  },\n" +
                "  \"15\" : {\n" +
                "    \"beamType\" : \"250 UC 73\",\n" +
                "    \"quantity\" : 1,\n" +
                "    \"beamId\" : \"B07\",\n" +
                "    \"length\" : 5910,\n" +
                "    \"treatment\" : \"BLACK\"\n" +
                "  },\n" +
                "  \"16\" : {\n" +
                "    \"beamType\" : \"250 UC 89\",\n" +
                "    \"quantity\" : 1,\n" +
                "    \"beamId\" : \"B11\",\n" +
                "    \"length\" : 6550,\n" +
                "    \"treatment\" : \"BLACK\"\n" +
                "  },\n" +
                "  \"17\" : {\n" +
                "    \"beamType\" : \"RHS 75x50x1.6\",\n" +
                "    \"quantity\" : 1,\n" +
                "    \"beamId\" : \"BB01\",\n" +
                "    \"length\" : 2380,\n" +
                "    \"treatment\" : \"BLACK\"\n" +
                "  },\n" +
                "  \"18\" : {\n" +
                "    \"beamType\" : \"RHS 75x50x1.6\",\n" +
                "    \"quantity\" : 5,\n" +
                "    \"beamId\" : \"BB03\",\n" +
                "    \"length\" : 2740,\n" +
                "    \"treatment\" : \"BLACK\"\n" +
                "  },\n" +
                "  \"19\" : {\n" +
                "    \"beamType\" : \"RHS 75x50x1.6\",\n" +
                "    \"quantity\" : 8,\n" +
                "    \"beamId\" : \"BB02\",\n" +
                "    \"length\" : 2755,\n" +
                "    \"treatment\" : \"BLACK\"\n" +
                "  },\n" +
                "  \"20\" : {\n" +
                "    \"beamType\" : \"T-Bar 180x10 / 150x10\",\n" +
                "    \"quantity\" : 1,\n" +
                "    \"beamId\" : \"T04\",\n" +
                "    \"length\" : 2435,\n" +
                "    \"treatment\" : \"BLACK\"\n" +
                "  },\n" +
                "  \"21\" : {\n" +
                "    \"beamType\" : \"T-Bar 180x10 / 200x12\",\n" +
                "    \"quantity\" : 1,\n" +
                "    \"beamId\" : \"T03\",\n" +
                "    \"length\" : 6380,\n" +
                "    \"treatment\" : \"BLACK\"\n" +
                "  },\n" +
                "  \"22\" : {\n" +
                "    \"beamType\" : \"T-Bar 180x10 / 250x16 / 50x25\",\n" +
                "    \"quantity\" : 1,\n" +
                "    \"beamId\" : \"T02\",\n" +
                "    \"length\" : 3433,\n" +
                "    \"treatment\" : \"BLACK\"\n" +
                "  },\n" +
                "  \"23\" : {\n" +
                "    \"beamType\" : \"T-Bar 200x10 / 150x10\",\n" +
                "    \"quantity\" : 1,\n" +
                "    \"beamId\" : \"T01\",\n" +
                "    \"length\" : 427,\n" +
                "    \"treatment\" : \"BLACK\"\n" +
                "  },\n" +
                "  \"24\" : {\n" +
                "    \"beamType\" : \"T-Bar 200x10 / 150x10\",\n" +
                "    \"quantity\" : 1,\n" +
                "    \"beamId\" : \"T05\",\n" +
                "    \"length\" : 2440,\n" +
                "    \"treatment\" : \"BLACK\"\n" +
                "  }\n" +
                "}\n";
    }

    private String trussData() {
        return "{\n" +
                "  \"0\" : {\n" +
                "    \"trussId\" : \"CW01\",\n" +
                "    \"qty\" : 5,\n" +
                "    \"length\" : 285,\n" +
                "    \"type\" : \"CW346\",\n" +
                "    \"leftEndcap\" : \"SPECIAL\",\n" +
                "    \"rightEndcap\" : \"TYPE_D\",\n" +
                "    \"penetrationWebCuts\" : [ 0, 0 ],\n" +
                "    \"packingGroup\" : 1\n" +
                "  },\n" +
                "  \"1\" : {\n" +
                "    \"trussId\" : \"CW02\",\n" +
                "    \"qty\" : 2,\n" +
                "    \"length\" : 337,\n" +
                "    \"type\" : \"CW260\",\n" +
                "    \"leftEndcap\" : \"STANDARD\",\n" +
                "    \"rightEndcap\" : \"TYPE_D\",\n" +
                "    \"penetrationWebCuts\" : [ 0, 0 ],\n" +
                "    \"packingGroup\" : 1\n" +
                "  },\n" +
                "  \"2\" : {\n" +
                "    \"trussId\" : \"CW03\",\n" +
                "    \"qty\" : 6,\n" +
                "    \"length\" : 1893,\n" +
                "    \"type\" : \"CW260\",\n" +
                "    \"leftEndcap\" : \"STANDARD\",\n" +
                "    \"rightEndcap\" : \"SPECIAL\",\n" +
                "    \"penetrationWebCuts\" : [ 0, 0 ],\n" +
                "    \"packingGroup\" : 1\n" +
                "  },\n" +
                "  \"3\" : {\n" +
                "    \"trussId\" : \"CW04\",\n" +
                "    \"qty\" : 1,\n" +
                "    \"length\" : 2150,\n" +
                "    \"type\" : \"CW260\",\n" +
                "    \"leftEndcap\" : \"STANDARD\",\n" +
                "    \"rightEndcap\" : \"STANDARD\",\n" +
                "    \"penetrationWebCuts\" : [ 0, 0 ],\n" +
                "    \"packingGroup\" : 1\n" +
                "  },\n" +
                "  \"4\" : {\n" +
                "    \"trussId\" : \"CW05\",\n" +
                "    \"qty\" : 1,\n" +
                "    \"length\" : 2159,\n" +
                "    \"type\" : \"CW260\",\n" +
                "    \"leftEndcap\" : \"TYPE_E\",\n" +
                "    \"rightEndcap\" : \"STANDARD\",\n" +
                "    \"penetrationWebCuts\" : [ 0, 0 ],\n" +
                "    \"packingGroup\" : 1\n" +
                "  },\n" +
                "  \"5\" : {\n" +
                "    \"trussId\" : \"CW06\",\n" +
                "    \"qty\" : 1,\n" +
                "    \"length\" : 2262,\n" +
                "    \"type\" : \"CW260\",\n" +
                "    \"leftEndcap\" : \"SPECIAL\",\n" +
                "    \"rightEndcap\" : \"TYPE_E\",\n" +
                "    \"penetrationWebCuts\" : [ 0, 0 ],\n" +
                "    \"packingGroup\" : 1\n" +
                "  },\n" +
                "  \"6\" : {\n" +
                "    \"trussId\" : \"CW07\",\n" +
                "    \"qty\" : 3,\n" +
                "    \"length\" : 2358,\n" +
                "    \"type\" : \"CW260\",\n" +
                "    \"leftEndcap\" : \"TYPE_A\",\n" +
                "    \"rightEndcap\" : \"TYPE_E\",\n" +
                "    \"penetrationWebCuts\" : [ 0, 0 ],\n" +
                "    \"packingGroup\" : 1\n" +
                "  },\n" +
                "  \"7\" : {\n" +
                "    \"trussId\" : \"CW08\",\n" +
                "    \"qty\" : 1,\n" +
                "    \"length\" : 2408,\n" +
                "    \"type\" : \"CW260\",\n" +
                "    \"leftEndcap\" : \"TYPE_D\",\n" +
                "    \"rightEndcap\" : \"STANDARD\",\n" +
                "    \"penetrationWebCuts\" : [ 0, 0 ],\n" +
                "    \"packingGroup\" : 1\n" +
                "  },\n" +
                "  \"8\" : {\n" +
                "    \"trussId\" : \"CW09\",\n" +
                "    \"qty\" : 1,\n" +
                "    \"length\" : 2459,\n" +
                "    \"type\" : \"CW260\",\n" +
                "    \"leftEndcap\" : \"STANDARD\",\n" +
                "    \"rightEndcap\" : \"TYPE_E\",\n" +
                "    \"penetrationWebCuts\" : [ 0, 0 ],\n" +
                "    \"packingGroup\" : 1\n" +
                "  },\n" +
                "  \"9\" : {\n" +
                "    \"trussId\" : \"CW10\",\n" +
                "    \"qty\" : 1,\n" +
                "    \"length\" : 2460,\n" +
                "    \"type\" : \"CW260\",\n" +
                "    \"leftEndcap\" : \"STANDARD\",\n" +
                "    \"rightEndcap\" : \"TYPE_E\",\n" +
                "    \"penetrationWebCuts\" : [ 0, 0 ],\n" +
                "    \"packingGroup\" : 1\n" +
                "  },\n" +
                "  \"10\" : {\n" +
                "    \"trussId\" : \"CW11\",\n" +
                "    \"qty\" : 1,\n" +
                "    \"length\" : 2582,\n" +
                "    \"type\" : \"CW260\",\n" +
                "    \"leftEndcap\" : \"SPECIAL\",\n" +
                "    \"rightEndcap\" : \"STANDARD\",\n" +
                "    \"penetrationWebCuts\" : [ 0, 0 ],\n" +
                "    \"packingGroup\" : 1\n" +
                "  },\n" +
                "  \"11\" : {\n" +
                "    \"trussId\" : \"CW12\",\n" +
                "    \"qty\" : 5,\n" +
                "    \"length\" : 2677,\n" +
                "    \"type\" : \"CW260\",\n" +
                "    \"leftEndcap\" : \"STANDARD\",\n" +
                "    \"rightEndcap\" : \"SPECIAL\",\n" +
                "    \"penetrationWebCuts\" : [ 0, 0 ],\n" +
                "    \"packingGroup\" : 1\n" +
                "  },\n" +
                "  \"12\" : {\n" +
                "    \"trussId\" : \"CW13\",\n" +
                "    \"qty\" : 9,\n" +
                "    \"length\" : 2759,\n" +
                "    \"type\" : \"CW260\",\n" +
                "    \"leftEndcap\" : \"TYPE_E\",\n" +
                "    \"rightEndcap\" : \"STANDARD\",\n" +
                "    \"penetrationWebCuts\" : [ 0, 0 ],\n" +
                "    \"packingGroup\" : 1\n" +
                "  },\n" +
                "  \"13\" : {\n" +
                "    \"trussId\" : \"CW14\",\n" +
                "    \"qty\" : 3,\n" +
                "    \"length\" : 2940,\n" +
                "    \"type\" : \"CW260\",\n" +
                "    \"leftEndcap\" : \"STANDARD\",\n" +
                "    \"rightEndcap\" : \"STANDARD\",\n" +
                "    \"penetrationWebCuts\" : [ 0, 0 ],\n" +
                "    \"packingGroup\" : 1\n" +
                "  },\n" +
                "  \"14\" : {\n" +
                "    \"trussId\" : \"CW15\",\n" +
                "    \"qty\" : 13,\n" +
                "    \"length\" : 2975,\n" +
                "    \"type\" : \"CW260\",\n" +
                "    \"leftEndcap\" : \"STANDARD\",\n" +
                "    \"rightEndcap\" : \"SPECIAL\",\n" +
                "    \"penetrationWebCuts\" : [ 0, 0 ],\n" +
                "    \"packingGroup\" : 1\n" +
                "  },\n" +
                "  \"15\" : {\n" +
                "    \"trussId\" : \"CW16\",\n" +
                "    \"qty\" : 14,\n" +
                "    \"length\" : 2985,\n" +
                "    \"type\" : \"CW260\",\n" +
                "    \"leftEndcap\" : \"SPECIAL\",\n" +
                "    \"rightEndcap\" : \"SPECIAL\",\n" +
                "    \"penetrationWebCuts\" : [ 0, 0 ],\n" +
                "    \"packingGroup\" : 1\n" +
                "  },\n" +
                "  \"16\" : {\n" +
                "    \"trussId\" : \"CW17\",\n" +
                "    \"qty\" : 6,\n" +
                "    \"length\" : 3004,\n" +
                "    \"type\" : \"CW260\",\n" +
                "    \"leftEndcap\" : \"SPECIAL\",\n" +
                "    \"rightEndcap\" : \"STANDARD\",\n" +
                "    \"penetrationWebCuts\" : [ 0, 0 ],\n" +
                "    \"packingGroup\" : 1\n" +
                "  },\n" +
                "  \"17\" : {\n" +
                "    \"trussId\" : \"CW18\",\n" +
                "    \"qty\" : 6,\n" +
                "    \"length\" : 3082,\n" +
                "    \"type\" : \"CW260\",\n" +
                "    \"leftEndcap\" : \"SPECIAL\",\n" +
                "    \"rightEndcap\" : \"STANDARD\",\n" +
                "    \"penetrationWebCuts\" : [ 0, 0 ],\n" +
                "    \"packingGroup\" : 1\n" +
                "  },\n" +
                "  \"18\" : {\n" +
                "    \"trussId\" : \"CW19\",\n" +
                "    \"qty\" : 5,\n" +
                "    \"length\" : 3389,\n" +
                "    \"type\" : \"CW260\",\n" +
                "    \"leftEndcap\" : \"STANDARD\",\n" +
                "    \"rightEndcap\" : \"SPECIAL\",\n" +
                "    \"penetrationWebCuts\" : [ 0, 0 ],\n" +
                "    \"packingGroup\" : 1\n" +
                "  },\n" +
                "  \"19\" : {\n" +
                "    \"trussId\" : \"CW20\",\n" +
                "    \"qty\" : 6,\n" +
                "    \"length\" : 3586,\n" +
                "    \"type\" : \"CW260\",\n" +
                "    \"leftEndcap\" : \"SPECIAL\",\n" +
                "    \"rightEndcap\" : \"TYPE_A\",\n" +
                "    \"penetrationWebCuts\" : [ 0, 0 ],\n" +
                "    \"packingGroup\" : 1\n" +
                "  },\n" +
                "  \"20\" : {\n" +
                "    \"trussId\" : \"CW21\",\n" +
                "    \"qty\" : 1,\n" +
                "    \"length\" : 4000,\n" +
                "    \"type\" : \"CW260\",\n" +
                "    \"leftEndcap\" : \"TYPE_D\",\n" +
                "    \"rightEndcap\" : \"SPECIAL\",\n" +
                "    \"penetrationWebCuts\" : [ 0, 0 ],\n" +
                "    \"packingGroup\" : 1\n" +
                "  },\n" +
                "  \"21\" : {\n" +
                "    \"trussId\" : \"CW22\",\n" +
                "    \"qty\" : 2,\n" +
                "    \"length\" : 4207,\n" +
                "    \"type\" : \"CW260\",\n" +
                "    \"leftEndcap\" : \"SPECIAL\",\n" +
                "    \"rightEndcap\" : \"SPECIAL\",\n" +
                "    \"penetrationWebCuts\" : [ 0, 0 ],\n" +
                "    \"packingGroup\" : 1\n" +
                "  },\n" +
                "  \"22\" : {\n" +
                "    \"trussId\" : \"CW23\",\n" +
                "    \"qty\" : 6,\n" +
                "    \"length\" : 4521,\n" +
                "    \"type\" : \"CW260\",\n" +
                "    \"leftEndcap\" : \"SPECIAL\",\n" +
                "    \"rightEndcap\" : \"SPECIAL\",\n" +
                "    \"penetrationWebCuts\" : [ 0, 0 ],\n" +
                "    \"packingGroup\" : 1\n" +
                "  },\n" +
                "  \"23\" : {\n" +
                "    \"trussId\" : \"CW24\",\n" +
                "    \"qty\" : 4,\n" +
                "    \"length\" : 5274,\n" +
                "    \"type\" : \"CW260\",\n" +
                "    \"leftEndcap\" : \"SPECIAL\",\n" +
                "    \"rightEndcap\" : \"TYPE_E\",\n" +
                "    \"penetrationWebCuts\" : [ 0, 0 ],\n" +
                "    \"packingGroup\" : 1\n" +
                "  }\n" +
                "}\n";
    }
}
