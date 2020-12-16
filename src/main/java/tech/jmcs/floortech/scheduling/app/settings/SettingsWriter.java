package tech.jmcs.floortech.scheduling.app.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.Path;
import java.util.Properties;

public class SettingsWriter {
    protected static final Logger LOG = LoggerFactory.getLogger(SettingsWriter.class);

    @Inject
    private SettingsHolder settingsHolder;

    public SettingsWriter() {
    }

    /**
     * Protected constructor for testing purposes
     * @param settingsHolder
     */
    protected SettingsWriter(SettingsHolder settingsHolder) {
        this.settingsHolder = settingsHolder;
    }

    public Path writeSettingsFile() throws IOException {
        Path settingsFilePath = SettingsConstants.SETTINGS_FILE_PATH;
        File settingsFile = settingsFilePath.toFile();
        if (!settingsFile.exists()) {
            settingsFile.getParentFile().mkdirs();
            settingsFile.createNewFile();
        }
        Properties settings = SettingsHelper.settingsToPropertiesObject(this.settingsHolder);
        try (OutputStream fos = new FileOutputStream(settingsFile)) {
            settings.store(fos, "");
        } catch (FileNotFoundException e) {
            LOG.error("File not found exception: {}", e.getMessage());
            return null;
        } catch (IOException e) {
            LOG.error("IO exception (file open?): {}", e.getMessage());
            throw e;
        }

        return settingsFilePath;
    }
}
