package tech.jmcs.floortech.scheduling.app.settings;

import com.sun.org.apache.xml.internal.security.Init;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.Initializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Settings Memory class
 * Holds settings to be accessed throughout the app (Class is container managed and injected, should not be instantiated
 * Will be instantiated when a Settings Loader is used.
 */
public class SettingsHolder {
    protected static final Logger LOG = LoggerFactory.getLogger(SettingsHolder.class);

    private Path jobFilesSchedulingRootPath;
    private Path jobFoldersDetailingRootPath;
    private Boolean builtInTrussExtractorEnabled;
    private Boolean builtInBeamExtractorEnabled;
    private Boolean builtInSheetExtractorEnabled;
    private Boolean builtInSlabExtractorEnabled;

    private List<String> dataSourceFileNamesMap;

    private ObjectProperty<Date> lastUpdatedProperty;

    /**
     * Constructor
     */
    public SettingsHolder() {
        LOG.debug("Settings Holder instantiated (Empty)");
        this.dataSourceFileNamesMap = new ArrayList<>();

        this.lastUpdatedProperty = new SimpleObjectProperty<>();
        this.lastUpdatedProperty.set(new Date());
    }

    // Last Updated FX Property
    public ObjectProperty<Date> getLastUpdatedProperty() {
        return lastUpdatedProperty;
    }

    public void setUpdatedProperty() {
        this.lastUpdatedProperty.set(new Date());
    }

    // Set all method
    public void setAllSettings(Path jobFilesSchedulingRootPath, Path jobFoldersDetailingRootPath,
                               Boolean builtInBeamExtractorEnabled,
                               Boolean builtInSheetExtractorEnabled,
                               Boolean builtInSlabExtractorEnabled,
                               Boolean builtInTrussExtractorEnabled) {
        this.jobFilesSchedulingRootPath = jobFilesSchedulingRootPath;
        this.jobFoldersDetailingRootPath = jobFoldersDetailingRootPath;
        this.builtInBeamExtractorEnabled = builtInBeamExtractorEnabled;
        this.builtInSheetExtractorEnabled = builtInSheetExtractorEnabled;
        this.builtInSlabExtractorEnabled = builtInSlabExtractorEnabled;
        this.builtInTrussExtractorEnabled = builtInTrussExtractorEnabled;
        LOG.debug("Set: js path: {} jd path{} beam {} sheet {} slab {} truss {} ", jobFilesSchedulingRootPath, jobFoldersDetailingRootPath, builtInBeamExtractorEnabled, builtInSheetExtractorEnabled, builtInSlabExtractorEnabled, builtInTrussExtractorEnabled);

        setUpdatedProperty();
    }

    // Getters & Setters
    public Path getJobFilesSchedulingRootPath() {
        return jobFilesSchedulingRootPath;
    }

    public void setJobFilesSchedulingRootPath(Path jobFilesSchedulingRootPath) throws FileNotFoundException {
        if (jobFilesSchedulingRootPath == null) {
            this.jobFilesSchedulingRootPath = null;
            setUpdatedProperty();
            return;
        }
        File f = jobFilesSchedulingRootPath.toFile();
        if (!f.exists() || !f.isDirectory()) {
            LOG.debug("The folder did not exist");
            throw new FileNotFoundException("Folder not found");
        }
        this.jobFilesSchedulingRootPath = jobFilesSchedulingRootPath;
        setUpdatedProperty();
    }

    public Path getJobFoldersDetailingRootPath() {
        return jobFoldersDetailingRootPath;
    }

    public void setJobFoldersDetailingRootPath(Path jobFoldersDetailingRootPath) throws FileNotFoundException {
        if (jobFoldersDetailingRootPath == null) {
            this.jobFoldersDetailingRootPath = null;
            setUpdatedProperty();
            return;
        }
        File f = jobFoldersDetailingRootPath.toFile();
        if (!f.exists() || !f.isDirectory()) {
            LOG.debug("The folder did not exist");
            throw new FileNotFoundException("Folder not found");
        }
        this.jobFoldersDetailingRootPath = jobFoldersDetailingRootPath;
        setUpdatedProperty();
    }

    public Boolean isBuiltInTrussExtractorEnabled() {
        return builtInTrussExtractorEnabled;
    }

    public void setBuiltInTrussExtractorEnabled(Boolean builtInTrussExtractorEnabled) {
        if (this.builtInTrussExtractorEnabled != builtInTrussExtractorEnabled) {
            this.builtInTrussExtractorEnabled = builtInTrussExtractorEnabled;
            setUpdatedProperty();
        }
    }

    public Boolean isBuiltInBeamExtractorEnabled() {
        return builtInBeamExtractorEnabled;
    }

    public void setBuiltInBeamExtractorEnabled(Boolean builtInBeamExtractorEnabled) {
        if (this.builtInBeamExtractorEnabled != builtInBeamExtractorEnabled) {
            this.builtInBeamExtractorEnabled = builtInBeamExtractorEnabled;
            setUpdatedProperty();
        }
    }

    public Boolean isBuiltInSheetExtractorEnabled() {
        return builtInSheetExtractorEnabled;
    }

    public void setBuiltInSheetExtractorEnabled(Boolean builtInSheetExtractorEnabled) {
        if (this.builtInSheetExtractorEnabled != builtInSheetExtractorEnabled) {
            this.builtInSheetExtractorEnabled = builtInSheetExtractorEnabled;
            setUpdatedProperty();
        }
    }

    public Boolean isBuiltInSlabExtractorEnabled() {
        return builtInSlabExtractorEnabled;
    }

    public void setBuiltInSlabExtractorEnabled(Boolean builtInSlabExtractorEnabled) {
        if (this.builtInSlabExtractorEnabled != builtInSlabExtractorEnabled) {
            this.builtInSlabExtractorEnabled = builtInSlabExtractorEnabled;
            setUpdatedProperty();
        }
    }

}
