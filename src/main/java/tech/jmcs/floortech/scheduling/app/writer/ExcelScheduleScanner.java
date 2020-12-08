package tech.jmcs.floortech.scheduling.app.writer;

import tech.jmcs.floortech.scheduling.app.util.ExcelCellRange;

/**
 * Class to process Excel Schedule to detect data structures + data contained
 */
public class ExcelScheduleScanner {

    public static final String BEAMS_N_BRACKETS = "BEAMS / BRACKETS";
    public static final String ANGLE_N_FLASHING = "EDGE ANGLES & FLASHINGS";
    public static final String FORM_SHEETS = "FORM SHEETS";
    public static final String STRUCTURAL_STEEL = "STRUCTURAL STEEL";
    public static final String CRANE_N_WELDING = "CRANE HIRE & SITE WELDING";
    public static final String REO = "REINFORCEMENT";
    public static final String STAIR_FORM = "STAIRCASE FORMWORK";
    public static final String LABOUR = "LABOUR";
    public static final String MISC = "MISCELLANEOUS";

    /**
     * Scans an Excel Schedule file for a section title and tries to detect the range of that section.
     * @param sectionName
     * @return
     */
    public ExcelCellRange getRangeForSection(String sectionName) {

        return null;
    }

}
