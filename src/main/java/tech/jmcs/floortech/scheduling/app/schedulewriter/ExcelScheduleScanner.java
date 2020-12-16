package tech.jmcs.floortech.scheduling.app.schedulewriter;

import tech.jmcs.floortech.scheduling.app.util.ExcelCellRange;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to process Excel Schedule to detect data structures + data contained
 */
public class ExcelScheduleScanner {

    public static final int SECT_TRUSS = 1;
    public static final int SECT_ANGLE = 2;
    public static final int SECT_SHEET = 3;
    public static final int SECT_STEEL = 4;
    public static final int SECT_WELD = 5;
    public static final int SECT_REO = 6;
    public static final int SECT_STAIR = 7;
    public static final int SECT_LABOUR = 8;
    public static final int SECT_MISC = 9;

    public static final String BEAMS_N_BRACKETS = "BEAMS / BRACKETS";
    public static final String ANGLE_N_FLASHING = "EDGE ANGLES & FLASHINGS";
    public static final String FORM_SHEETS = "FORM SHEETS";
    public static final String STRUCTURAL_STEEL = "STRUCTURAL STEEL";
    public static final String CRANE_N_WELDING = "CRANE HIRE & SITE WELDING";
    public static final String REO = "REINFORCEMENT";
    public static final String STAIR_FORM = "STAIRCASE FORMWORK";
    public static final String LABOUR = "LABOUR";
    public static final String MISC = "MISCELLANEOUS";

    private Map<Integer, String> sectionMap;

    public ExcelScheduleScanner() {
        this.sectionMap = new HashMap<>();
        this.sectionMap.put(SECT_TRUSS, BEAMS_N_BRACKETS);
        this.sectionMap.put(SECT_ANGLE, ANGLE_N_FLASHING);
        this.sectionMap.put(SECT_SHEET, FORM_SHEETS);
        this.sectionMap.put(SECT_STEEL, STRUCTURAL_STEEL);
        this.sectionMap.put(SECT_WELD, CRANE_N_WELDING);
        this.sectionMap.put(SECT_REO, REO);
        this.sectionMap.put(SECT_STAIR, STAIR_FORM);
        this.sectionMap.put(SECT_LABOUR, LABOUR);
        this.sectionMap.put(SECT_MISC, MISC);
    }

    private void updateSectionMapWithAvailableSettings() {

    }

    /**
     * Scans an Excel Schedule file for a section title and tries to detect the range of that section.
     * @param sectionName
     * @return
     */
    public ExcelCellRange getRangeForSection(String sectionName) {

        return null;
    }

}
