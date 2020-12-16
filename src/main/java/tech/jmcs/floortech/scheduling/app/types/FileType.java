package tech.jmcs.floortech.scheduling.app.types;

import java.util.HashMap;
import java.util.Map;

public class FileType {

    public static final Map<String, String> fileTypesMap;
    static {
        fileTypesMap = new HashMap<>();
        fileTypesMap.put("EXCEL", "*.xls; *.xlsx");
        fileTypesMap.put("PDF", "*.pdf");
        fileTypesMap.put("ALL", "*.*");

    };

}
