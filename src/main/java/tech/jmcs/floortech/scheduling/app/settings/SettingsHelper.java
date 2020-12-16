package tech.jmcs.floortech.scheduling.app.settings;

import java.nio.file.Path;
import java.util.Properties;

public class SettingsHelper {

    public static Properties settingsToPropertiesObject(SettingsHolder settingsHolder) {
        Properties properties = new Properties();
        Boolean builtInBeamExtractorEnabled = settingsHolder.isBuiltInBeamExtractorEnabled();
        Boolean builtInSlabExtractorEnabled = settingsHolder.isBuiltInSlabExtractorEnabled();
        Boolean builtInSheetExtractorEnabled = settingsHolder.isBuiltInSheetExtractorEnabled();
        Boolean builtInTrussExtractorEnabled = settingsHolder.isBuiltInTrussExtractorEnabled();
        Path jobFilesSchedulingRootPath = settingsHolder.getJobFilesSchedulingRootPath();
        Path jobFoldersDetailingRootPath = settingsHolder.getJobFoldersDetailingRootPath();

        properties.setProperty(SettingsName.BUILT_IN_BEAM_EXTRACTOR_ENABLED.getSettingsFilePropertyName(), builtInBeamExtractorEnabled == null ? "true" : builtInBeamExtractorEnabled.toString());
        properties.setProperty(SettingsName.BUILT_IN_SLAB_EXTRACTOR_ENABLED.getSettingsFilePropertyName(), builtInSlabExtractorEnabled == null ? "true" : builtInSlabExtractorEnabled.toString());
        properties.setProperty(SettingsName.BUILT_IN_SHEET_EXTRACTOR_ENABLED.getSettingsFilePropertyName(), builtInSheetExtractorEnabled == null ? "true" : builtInSheetExtractorEnabled.toString());
        properties.setProperty(SettingsName.BUILT_IN_TRUSS_EXTRACTOR_ENABLED.getSettingsFilePropertyName(), builtInTrussExtractorEnabled == null ? "true" : builtInTrussExtractorEnabled.toString());
        properties.setProperty(SettingsName.JOB_FILES_SCHEDULING_PATH.getSettingsFilePropertyName(), jobFilesSchedulingRootPath == null ? "" : jobFilesSchedulingRootPath.toString());
        properties.setProperty(SettingsName.JOB_FOLDERS_DETAILING_PATH.getSettingsFilePropertyName(), jobFoldersDetailingRootPath == null ? "" : jobFoldersDetailingRootPath.toString());

        return properties;
    }

}
