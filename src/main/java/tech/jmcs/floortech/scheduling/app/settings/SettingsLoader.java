package tech.jmcs.floortech.scheduling.app.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SettingsLoader {
    protected static final Logger LOG = LoggerFactory.getLogger(SettingsLoader.class);

    @Inject private SettingsHolder settingsHolder;
    @Inject private SettingsWriter settingsWriter;

    public SettingsLoader() {
    }

    /**
     * Protected Constructor for Testing Purposes
     * @param settingsHolder
     */
    protected SettingsLoader(SettingsHolder settingsHolder, SettingsWriter settingsWriter) {
        LOG.info("SettingsLoader constructing...");
        this.settingsHolder = settingsHolder;
        this.settingsWriter = settingsWriter;
    }

    public void load() throws IOException {
        File settingsFile = SettingsConstants.SETTINGS_FILE_PATH.toFile();

        Properties defaultSettings = new Properties();
        copyDefaultSettings(defaultSettings);

        Properties settings = null;

        if (settingsFile.exists() && settingsFile.isFile()) {
            try {
                settings = this.loadSettings(settingsFile);
                LOG.debug("Loaded settings from file");

                // TODO: Check if any settings are present in the defaultsettings.properties file and not in loaded settings (new app version could have new settings) and add them
                for (Map.Entry<Object, Object> entry : defaultSettings.entrySet()) {
                    String n = (String) entry.getKey();
                    String v = (String) entry.getValue();
                    if (!settings.containsKey(n)) {
                        settings.setProperty(n, v);
                        LOG.debug("Added a new property that was not found in the settings file on drive {} : {}", n, v);
                    }
                }
            } catch (IOException e) {
                throw new IOException("Could not read the settings file");
            }
        }

        // if file does not exist, needs to be written
        boolean writeFile = false;
        if (settings == null) {
            settings = defaultSettings;

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
        SettingsHelper.propertiesObjectToSettings(settings, this.settingsHolder);
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
