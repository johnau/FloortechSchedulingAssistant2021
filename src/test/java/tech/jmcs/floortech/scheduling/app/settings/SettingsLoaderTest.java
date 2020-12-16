package tech.jmcs.floortech.scheduling.app.settings;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class SettingsLoaderTest {

    @Test
    void testLoadSettings() {

        SettingsHolder holder = new SettingsHolder();

        SettingsWriter writer = new SettingsWriter(holder);

        SettingsLoader loader = new SettingsLoader(holder, writer);
        try {
            loader.load();
        } catch (IOException e) {
            System.out.printf("FAIL: %s", e.getMessage());
            fail();
        }

        Properties settings = loader.getSettingsFromMemory();
        settings.forEach( (name, value) -> {

            System.out.printf("Setting found: %s = %s \n", name, value);

        });

        Boolean beam = holder.isBuiltInBeamExtractorEnabled();
        Boolean sheet = holder.isBuiltInSheetExtractorEnabled();
        Boolean slab = holder.isBuiltInSlabExtractorEnabled();
        Boolean truss = holder.isBuiltInTrussExtractorEnabled();
        Path jobFilesScheduling = holder.getJobFilesSchedulingRootPath();
        Path jobFoldersDetailing = holder.getJobFoldersDetailingRootPath();

        System.out.printf("Settings found: %s %s %s %s %s %s \n", beam, sheet, slab, truss, jobFilesScheduling, jobFoldersDetailing);

    }
}