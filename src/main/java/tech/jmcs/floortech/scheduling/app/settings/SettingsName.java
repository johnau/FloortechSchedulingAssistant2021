package tech.jmcs.floortech.scheduling.app.settings;

public enum SettingsName {
    JOB_FILES_SCHEDULING_PATH ("job_files_scheduling_path"),
    JOB_FOLDERS_DETAILING_PATH ("job_folders_detailing_path"),
    BUILT_IN_TRUSS_EXTRACTOR_ENABLED ("built_in_truss_extractor_enabled"),
    TRUSS_SCHEDULE_SECTION_NAME ("truss_schedule_section_name"),
    BUILT_IN_BEAM_EXTRACTOR_ENABLED ("built_in_beam_extractor_enabled"),
    BEAM_SCHEDULE_SECTION_NAME ("beam_schedule_section_name"),
    BUILT_IN_SHEET_EXTRACTOR_ENABLED ("built_in_sheet_extractor_enabled"),
    SHEET_SCHEDULE_SECTION_NAME ("sheet_schedule_section_name"),
    BUILT_IN_SLAB_EXTRACTOR_ENABLED ("built_in_slab_extractor_enabled"),
    SLAB_SCHEDULE_SECTION_NAME ("slab_schedule_section_name"),
    EXCEL_SCHEDULE_SECTION_NAMES ("excel_schedule_section_names"),
    EXCEL_SCHEDULE_SHEET_NAME ("excel_schedule_sheet_name"),
    EXCEL_SCHEDULE_DATA_NAME_COL ("excel_schedule_data_name_col"),
    EXCEL_SCHEDULE_DATA_VALUE_COL ("excel_schedule_data_value_col"),
    EXCEL_SCHEDULE_DATA_UNIT_COL ("excel_schedule_data_unit_col"),
    EXCEL_SCHEDULE_DATA_RATE_COL ("excel_schedule_data_rate_col"),
    EXCEL_SCHEDULE_DATA_TOTAL_COL ("excel_schedule_data_total_col"),
    EXCEL_SCHEDULE_DATA_COMMENT_COL ("excel_schedule_data_comment_col");

    private final String settingsFilePropertyName;

    private SettingsName(String settingsFilePropertyName) {
        this.settingsFilePropertyName = settingsFilePropertyName;
    }

    public String getSettingsFilePropertyName() {
        return settingsFilePropertyName;
    }
}
