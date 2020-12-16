package tech.jmcs.floortech.scheduling.app.settings;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SettingsConstants {
    public static String SETTINGS_FILE_NAME = "FloortechScheduling_mainSettings.dat";
    public static String APP_GROUP_DIR_NAME = "Floortech";
    public static String APP_DIR_NAME = "Scheduling";
    public static Path SETTINGS_PARENT_FOLDER_PATH = Paths.get(System.getProperty("user.home"), APP_GROUP_DIR_NAME, APP_DIR_NAME);
    public static Path SETTINGS_FILE_PATH = Paths.get(SETTINGS_PARENT_FOLDER_PATH.toString(), SETTINGS_FILE_NAME);
}
