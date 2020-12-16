package tech.jmcs.floortech.scheduling.app.settings;

import javafx.fxml.Initializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.ResourceBundle;

public class SettingsLoader {
    protected static final Logger LOG = LoggerFactory.getLogger(SettingsLoader.class);

    @Inject
    private SettingsHolder settingsHolder;

    @Inject
    private SettingsWriter settingsWriter;

    public SettingsLoader() {
    }

    /**
     * Protected Constructor for Testing Purposes
     * @param settingsHolder
     */
    protected SettingsLoader(SettingsHolder settingsHolder, SettingsWriter settingsWriter) {
        this.settingsHolder = settingsHolder;
        this.settingsWriter = settingsWriter;
    }

    public void load() throws IOException {
        // TODO: Load settings from expected location
        File settingsFile = SettingsConstants.SETTINGS_FILE_PATH.toFile();

        Properties settings = null;

        if (settingsFile.exists() && settingsFile.isFile()) {
            try {
                settings = this.loadSettings(settingsFile);
                LOG.debug("Loaded settings from file");
            } catch (IOException e) {
                throw new IOException("Could not read the settings file");
            }
        }

        // if file does not exist, needs to be written
        boolean writeFile = false;
        if (settings == null) {
            settings = new Properties();
            // set default settings from memory.
            copyDefaultSettings(settings);

            LOG.debug("Copied default settings");

            writeFile = true;
        }

        copySettingsToMemory(settings);
        LOG.debug("Copied settings to memory");

        if (writeFile) {
            try {
                Path filePathWritten = this.settingsWriter.writeSettingsFile();
                if (filePathWritten == null) {
                    LOG.debug("Could not write the settings file");
                } else {
                    LOG.debug("Wrote the settings file");
                }
            } catch (IOException ioex) {
                LOG.debug("Could not access the settings file to write");
                throw ioex;
            }
        }

    }

    private void copySettingsToMemory(Properties settings) {
        Boolean builtInBeamExtractorEnabled = Boolean.parseBoolean(settings.getProperty(SettingsName.BUILT_IN_BEAM_EXTRACTOR_ENABLED.getSettingsFilePropertyName()));
        Boolean builtInSheetExtractorEnabled = Boolean.parseBoolean(settings.getProperty(SettingsName.BUILT_IN_SHEET_EXTRACTOR_ENABLED.getSettingsFilePropertyName()));
        Boolean builtInSlabExtractorEnabled = Boolean.parseBoolean(settings.getProperty(SettingsName.BUILT_IN_SLAB_EXTRACTOR_ENABLED.getSettingsFilePropertyName()));
        Boolean builtInTrussExtractorEnabled = Boolean.parseBoolean(settings.getProperty(SettingsName.BUILT_IN_TRUSS_EXTRACTOR_ENABLED.getSettingsFilePropertyName()));
        Path jobFilesSchedulingPath = Paths.get(settings.getProperty(SettingsName.JOB_FILES_SCHEDULING_PATH.getSettingsFilePropertyName()));
        Path jobFoldersDetailingPath = Paths.get(settings.getProperty(SettingsName.JOB_FOLDERS_DETAILING_PATH.getSettingsFilePropertyName()));

        LOG.debug("Converted settings: {} {} {} {} {} {}", builtInBeamExtractorEnabled, builtInSheetExtractorEnabled, builtInSlabExtractorEnabled, builtInTrussExtractorEnabled, jobFilesSchedulingPath, jobFoldersDetailingPath);

        this.settingsHolder.setBuiltInBeamExtractorEnabled(builtInBeamExtractorEnabled);
        this.settingsHolder.setBuiltInSheetExtractorEnabled(builtInSheetExtractorEnabled);
        this.settingsHolder.setBuiltInSlabExtractorEnabled(builtInSlabExtractorEnabled);
        this.settingsHolder.setBuiltInTrussExtractorEnabled(builtInTrussExtractorEnabled);
        try {
            this.settingsHolder.setJobFilesSchedulingRootPath(jobFilesSchedulingPath);
        } catch (FileNotFoundException e) {
            LOG.warn("Job Files scheduling Root Path in Settings can not be found");
        }
        try {
            this.settingsHolder.setJobFoldersDetailingRootPath(jobFoldersDetailingPath);
        } catch (FileNotFoundException e) {
            LOG.warn("Job Folders Detailing Root Path in Settings can not be found");
        }
    }

    private void copyDefaultSettings(Properties settings) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("defaultsettings.properties")) {
            if (is == null) {
                LOG.error("Could not read settings from resources folder");
                return;
            }
            settings.load(is);
            LOG.debug("Loaded default settings");
        } catch (IOException e) {
            LOG.warn("Could not read settings forom resource folder");
        }
    }

    public Properties getSettingsFromMemory() {
        return SettingsHelper.settingsToPropertiesObject(this.settingsHolder);
    }

    /**
     * Loads settings file from the user folder
     */
    private Properties loadSettings(File settingsFile) throws IOException {
        Properties settings = new Properties();
        try (FileInputStream fis = new FileInputStream(settingsFile)){
            settings.load(fis);
        } catch (FileNotFoundException e) {
            LOG.warn("Could not find a settings file");
            return null;
        } catch (IOException e) {
            LOG.warn("Could not access settings file");
            throw e;
        }

        settings.forEach((s, v) -> {

            LOG.debug("Setting found {}={}", s, v);

        });

        return settings;
    }


}
