package tech.jmcs.floortech.scheduling.app.settings;

public enum SettingsName {
    JOB_FILES_SCHEDULING_PATH ("job_files_scheduling_path"),
    JOB_FOLDERS_DETAILING_PATH ("job_folders_detailing_path"),
    BUILT_IN_TRUSS_EXTRACTOR_ENABLED ("built_in_truss_extractor_enabled"),
    BUILT_IN_BEAM_EXTRACTOR_ENABLED ("built_in_beam_extractor_enabled"),
    BUILT_IN_SHEET_EXTRACTOR_ENABLED ("built_in_sheet_extractor_enabled"),
    BUILT_IN_SLAB_EXTRACTOR_ENABLED ("built_in_slab_extractor_enabled");

    private final String settingsFilePropertyName;

    private SettingsName(String settingsFilePropertyName) {
        this.settingsFilePropertyName = settingsFilePropertyName;
    }

    public String getSettingsFilePropertyName() {
        return settingsFilePropertyName;
    }
}
