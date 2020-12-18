package tech.jmcs.floortech.scheduling.app.settings;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class SettingsHelper {

    public static Properties settingsToPropertiesObject(SettingsHolder settingsHolder) {
        Properties properties = new Properties();

        Path jobFilesSchedulingRootPath = settingsHolder.getJobFilesSchedulingRootPath();
        Path jobFoldersDetailingRootPath = settingsHolder.getJobFoldersDetailingRootPath();

        Boolean builtInBeamExtractorEnabled = settingsHolder.isBuiltInBeamExtractorEnabled();
        String beamScheduleSectionName = settingsHolder.getBeamScheduleSectionName();
        Boolean builtInSlabExtractorEnabled = settingsHolder.isBuiltInSlabExtractorEnabled();
        String slabScheduleSectionName = settingsHolder.getSlabScheduleSectionName();
        Boolean builtInSheetExtractorEnabled = settingsHolder.isBuiltInSheetExtractorEnabled();
        String sheetScheduleSectionName = settingsHolder.getSheetScheduleSectionName();
        Boolean builtInTrussExtractorEnabled = settingsHolder.isBuiltInTrussExtractorEnabled();
        String trussScheduleSectionName = settingsHolder.getTrussScheduleSectionName();

        String scheduleSheetName = settingsHolder.getExcelScheduleSheetName();
        List<String> scheduleSections = settingsHolder.getExcelScheduleFileSections();
        Integer dataNameCol = settingsHolder.getExcelScheduleDataNameCol();
        Integer dataValueCol = settingsHolder.getExcelScheduleDataValueCol();
        Integer dataUnitCol = settingsHolder.getExcelScheduleDataUnitCol();
        Integer dataRateCol = settingsHolder.getExcelScheduleDataRateCol();
        Integer dataTotalCol = settingsHolder.getExcelScheduleDataTotalCol();
        Integer dataCommentCol = settingsHolder.getExcelScheduleDataCommentCol();

        properties.setProperty(SettingsName.JOB_FILES_SCHEDULING_PATH.getSettingsFilePropertyName(), jobFilesSchedulingRootPath == null ? "" : jobFilesSchedulingRootPath.toString());
        properties.setProperty(SettingsName.JOB_FOLDERS_DETAILING_PATH.getSettingsFilePropertyName(), jobFoldersDetailingRootPath == null ? "" : jobFoldersDetailingRootPath.toString());

        properties.setProperty(SettingsName.BUILT_IN_BEAM_EXTRACTOR_ENABLED.getSettingsFilePropertyName(), builtInBeamExtractorEnabled == null ? "true" : builtInBeamExtractorEnabled.toString());
        properties.setProperty(SettingsName.BEAM_SCHEDULE_SECTION_NAME.getSettingsFilePropertyName(), beamScheduleSectionName == null ? "" : beamScheduleSectionName);
        properties.setProperty(SettingsName.BUILT_IN_SLAB_EXTRACTOR_ENABLED.getSettingsFilePropertyName(), builtInSlabExtractorEnabled == null ? "true" : builtInSlabExtractorEnabled.toString());
        properties.setProperty(SettingsName.SLAB_SCHEDULE_SECTION_NAME.getSettingsFilePropertyName(), slabScheduleSectionName == null ? "" : slabScheduleSectionName);
        properties.setProperty(SettingsName.BUILT_IN_SHEET_EXTRACTOR_ENABLED.getSettingsFilePropertyName(), builtInSheetExtractorEnabled == null ? "true" : builtInSheetExtractorEnabled.toString());
        properties.setProperty(SettingsName.SHEET_SCHEDULE_SECTION_NAME.getSettingsFilePropertyName(), sheetScheduleSectionName == null ? "" : sheetScheduleSectionName);
        properties.setProperty(SettingsName.BUILT_IN_TRUSS_EXTRACTOR_ENABLED.getSettingsFilePropertyName(), builtInTrussExtractorEnabled == null ? "true" : builtInTrussExtractorEnabled.toString());
        properties.setProperty(SettingsName.TRUSS_SCHEDULE_SECTION_NAME.getSettingsFilePropertyName(), trussScheduleSectionName == null ? "" : trussScheduleSectionName);

        properties.setProperty(SettingsName.EXCEL_SCHEDULE_SHEET_NAME.getSettingsFilePropertyName(), scheduleSheetName == null ? "" : scheduleSheetName);
        properties.setProperty(SettingsName.EXCEL_SCHEDULE_SECTION_NAMES.getSettingsFilePropertyName(), scheduleSections == null || scheduleSections.isEmpty() ? "" : String.join(";", scheduleSections));
        properties.setProperty(SettingsName.EXCEL_SCHEDULE_DATA_NAME_COL.getSettingsFilePropertyName(), String.valueOf(dataNameCol == null ? 1 : dataNameCol));
        properties.setProperty(SettingsName.EXCEL_SCHEDULE_DATA_VALUE_COL.getSettingsFilePropertyName(), String.valueOf(dataValueCol == null ? 2 : dataValueCol));
        properties.setProperty(SettingsName.EXCEL_SCHEDULE_DATA_UNIT_COL.getSettingsFilePropertyName(), String.valueOf(dataUnitCol == null ? 3 : dataUnitCol));
        properties.setProperty(SettingsName.EXCEL_SCHEDULE_DATA_RATE_COL.getSettingsFilePropertyName(), String.valueOf(dataRateCol == null ? 4 : dataRateCol));
        properties.setProperty(SettingsName.EXCEL_SCHEDULE_DATA_TOTAL_COL.getSettingsFilePropertyName(), String.valueOf(dataTotalCol == null ? 5 : dataTotalCol));
        properties.setProperty(SettingsName.EXCEL_SCHEDULE_DATA_COMMENT_COL.getSettingsFilePropertyName(), String.valueOf(dataCommentCol == null ? 6 : dataCommentCol));

        return properties;
    }

    public static void propertiesObjectToSettings(Properties properties, SettingsHolder settingsHolder) {
        // get from properties
        Path jobFilesSchedulingPath = Paths.get(properties.getProperty(SettingsName.JOB_FILES_SCHEDULING_PATH.getSettingsFilePropertyName()));
        Path jobFoldersDetailingPath = Paths.get(properties.getProperty(SettingsName.JOB_FOLDERS_DETAILING_PATH.getSettingsFilePropertyName()));

        Boolean builtInBeamExtractorEnabled = Boolean.parseBoolean(properties.getProperty(SettingsName.BUILT_IN_BEAM_EXTRACTOR_ENABLED.getSettingsFilePropertyName()));
        String beamScheduleSectionName = properties.getProperty(SettingsName.BEAM_SCHEDULE_SECTION_NAME.getSettingsFilePropertyName());
        Boolean builtInSheetExtractorEnabled = Boolean.parseBoolean(properties.getProperty(SettingsName.BUILT_IN_SHEET_EXTRACTOR_ENABLED.getSettingsFilePropertyName()));
        String sheetScheduleSectionName = properties.getProperty(SettingsName.SHEET_SCHEDULE_SECTION_NAME.getSettingsFilePropertyName());
        Boolean builtInSlabExtractorEnabled = Boolean.parseBoolean(properties.getProperty(SettingsName.BUILT_IN_SLAB_EXTRACTOR_ENABLED.getSettingsFilePropertyName()));
        String slabScheduleSectionName = properties.getProperty(SettingsName.SLAB_SCHEDULE_SECTION_NAME.getSettingsFilePropertyName());
        Boolean builtInTrussExtractorEnabled = Boolean.parseBoolean(properties.getProperty(SettingsName.BUILT_IN_TRUSS_EXTRACTOR_ENABLED.getSettingsFilePropertyName()));
        String trussScheduleSectionName = properties.getProperty(SettingsName.TRUSS_SCHEDULE_SECTION_NAME.getSettingsFilePropertyName());

        Integer dataNameCol = 0;
        Integer dataValueCol = 0;
        Integer dataUnitCol = 0;
        Integer dataRateCol = 0;
        Integer dataTotalCol = 0;
        Integer dataCommentCol = 0;
        try {
            dataNameCol = Integer.parseInt(properties.getProperty(SettingsName.EXCEL_SCHEDULE_DATA_NAME_COL.getSettingsFilePropertyName()));
            dataValueCol = Integer.parseInt(properties.getProperty(SettingsName.EXCEL_SCHEDULE_DATA_VALUE_COL.getSettingsFilePropertyName()));
            dataUnitCol = Integer.parseInt(properties.getProperty(SettingsName.EXCEL_SCHEDULE_DATA_UNIT_COL.getSettingsFilePropertyName()));
            dataRateCol = Integer.parseInt(properties.getProperty(SettingsName.EXCEL_SCHEDULE_DATA_RATE_COL.getSettingsFilePropertyName()));
            dataTotalCol = Integer.parseInt(properties.getProperty(SettingsName.EXCEL_SCHEDULE_DATA_TOTAL_COL.getSettingsFilePropertyName()));
            dataCommentCol = Integer.parseInt(properties.getProperty(SettingsName.EXCEL_SCHEDULE_DATA_COMMENT_COL.getSettingsFilePropertyName()));
        } catch (NumberFormatException nex) {
            // others following fail when one fails...
        }

        String scheduleSheetName = properties.getProperty(SettingsName.EXCEL_SCHEDULE_SHEET_NAME.getSettingsFilePropertyName());

        String rawScheduleSections = properties.getProperty(SettingsName.EXCEL_SCHEDULE_SECTION_NAMES.getSettingsFilePropertyName());
        List<String> scheduleSections = new ArrayList<>();
        if (rawScheduleSections != null && !rawScheduleSections.isEmpty() && rawScheduleSections.contains(";")) {
            scheduleSections = Arrays.asList(rawScheduleSections.split(";"));
        }

        // set to settings holder
        settingsHolder.setBuiltInBeamExtractorEnabled(builtInBeamExtractorEnabled);
        settingsHolder.setBeamScheduleSectionName(beamScheduleSectionName);
        settingsHolder.setBuiltInSheetExtractorEnabled(builtInSheetExtractorEnabled);
        settingsHolder.setSheetScheduleSectionName(sheetScheduleSectionName);
        settingsHolder.setBuiltInSlabExtractorEnabled(builtInSlabExtractorEnabled);
        settingsHolder.setSlabScheduleSectionName(slabScheduleSectionName);
        settingsHolder.setBuiltInTrussExtractorEnabled(builtInTrussExtractorEnabled);
        settingsHolder.setTrussScheduleSectionName(trussScheduleSectionName);
        try {
            settingsHolder.setJobFilesSchedulingRootPath(jobFilesSchedulingPath);
        } catch (FileNotFoundException e) {
            // silently fail to set paths that dont exist
        }
        try {
            settingsHolder.setJobFoldersDetailingRootPath(jobFoldersDetailingPath);
        } catch (FileNotFoundException e) {
            // silently fail to set paths that dont exist
        }
        settingsHolder.setExcelScheduleFileSections(scheduleSections);
        settingsHolder.setExcelScheduleSheetName(scheduleSheetName);
        settingsHolder.setExcelScheduleDataNameCol(dataNameCol);
        settingsHolder.setExcelScheduleDataValueCol(dataValueCol);
        settingsHolder.setExcelScheduleDataUnitCol(dataUnitCol);
        settingsHolder.setExcelScheduleDataRateCol(dataRateCol);
        settingsHolder.setExcelScheduleDataTotalCol(dataTotalCol);
        settingsHolder.setExcelScheduleDataCommentCol(dataCommentCol);

    }
}
