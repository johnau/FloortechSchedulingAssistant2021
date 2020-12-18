package tech.jmcs.floortech.scheduling.app.settings;

public enum DefaultScheduleSections {

    BEAMS_N_BRACKETS ("BEAMS / BRACKETS", 1),
    ANGLE_N_FLASHING ("EDGE ANGLES & FLASHINGS", 2),
    FORM_SHEETS ("FORM SHEETS", 3),
    STRUCTURAL_STEEL ("STRUCTURAL STEEL", 4),
    CRANE_N_WELDING ("CRANE HIRE & SITE WELDING", 5),
    REO ("REINFORCEMENT", 6),
    STAIR_FORM ("STAIRCASE FORMWORK", 7),
    LABOUR ("LABOUR", 8),
    MISC ("MISCELLANEOUS", 9);

    private final String entryName;
    private final int id;

    DefaultScheduleSections(String entryName, int id) {
        this.entryName = entryName;
        this.id = id;
    }

    public String getEntryName() {
        return entryName;
    }

    public int getId() {
        return id;
    }
}
