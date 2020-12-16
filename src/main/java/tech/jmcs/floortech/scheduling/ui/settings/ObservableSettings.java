package tech.jmcs.floortech.scheduling.ui.settings;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.settings.SettingsHolder;

import java.nio.file.Path;

@Deprecated
public class ObservableSettings {

    protected static final Logger LOG = LoggerFactory.getLogger(SettingsHolder.class);

    private SimpleObjectProperty<Path> jobFilesSchedulingRootPath;
    private SimpleObjectProperty<Path> jobFoldersDetailingRootPath;
    private SimpleBooleanProperty builtInTrussExtractorEnabled;
    private SimpleBooleanProperty builtInBeamExtractorEnabled;
    private SimpleBooleanProperty builtInSheetExtractorEnabled;
    private SimpleBooleanProperty builtInSlabExtractorEnabled;

    public ObservableSettings() {
        LOG.debug("Observable Settings instantiated");
    }

    public Path getJobFilesSchedulingRootPath() {
        return jobFilesSchedulingRootPathProperty().get();
    }

    public SimpleObjectProperty<Path> jobFilesSchedulingRootPathProperty() {
        if (this.jobFilesSchedulingRootPath == null) {
            this.jobFilesSchedulingRootPath = new SimpleObjectProperty<>();
        }
        return jobFilesSchedulingRootPath;
    }

    public void setJobFilesSchedulingRootPath(Path jobFilesSchedulingRootPath) {
        this.jobFilesSchedulingRootPathProperty().set(jobFilesSchedulingRootPath);
    }

    public Path getJobFoldersDetailingRootPath() {
        return jobFoldersDetailingRootPathProperty().get();
    }

    public SimpleObjectProperty<Path> jobFoldersDetailingRootPathProperty() {
        if (this.jobFoldersDetailingRootPath == null) {
            this.jobFoldersDetailingRootPath = new SimpleObjectProperty<>();
        }
        return jobFoldersDetailingRootPath;
    }

    public void setJobFoldersDetailingRootPath(Path jobFoldersDetailingRootPath) {
        this.jobFoldersDetailingRootPathProperty().set(jobFoldersDetailingRootPath);
    }

    public boolean isBuiltInTrussExtractorEnabled() {
        return builtInTrussExtractorEnabledProperty().get();
    }

    public SimpleBooleanProperty builtInTrussExtractorEnabledProperty() {
        if (this.builtInTrussExtractorEnabled == null) {
            this.builtInTrussExtractorEnabled = new SimpleBooleanProperty();
        }
        return builtInTrussExtractorEnabled;
    }

    public void setBuiltInTrussExtractorEnabled(boolean builtInTrussExtractorEnabled) {
        this.builtInTrussExtractorEnabledProperty().set(builtInTrussExtractorEnabled);
    }

    public boolean isBuiltInBeamExtractorEnabled() {
        return builtInBeamExtractorEnabledProperty().get();
    }

    public SimpleBooleanProperty builtInBeamExtractorEnabledProperty() {
        if (this.builtInBeamExtractorEnabled == null) {
            this.builtInBeamExtractorEnabled = new SimpleBooleanProperty();
        }
        return builtInBeamExtractorEnabled;
    }

    public void setBuiltInBeamExtractorEnabled(boolean builtInBeamExtractorEnabled) {
        this.builtInBeamExtractorEnabledProperty().set(builtInBeamExtractorEnabled);
    }

    public boolean isBuiltInSheetExtractorEnabled() {
        return builtInSheetExtractorEnabledProperty().get();
    }

    public SimpleBooleanProperty builtInSheetExtractorEnabledProperty() {
        if (this.builtInSheetExtractorEnabled == null) {
            this.builtInSheetExtractorEnabled = new SimpleBooleanProperty();
        }
        return builtInSheetExtractorEnabled;
    }

    public void setBuiltInSheetExtractorEnabled(boolean builtInSheetExtractorEnabled) {
        this.builtInSheetExtractorEnabledProperty().set(builtInSheetExtractorEnabled);
    }

    public boolean isBuiltInSlabExtractorEnabled() {
        return builtInSlabExtractorEnabledProperty().get();
    }

    public SimpleBooleanProperty builtInSlabExtractorEnabledProperty() {
        if (this.builtInSlabExtractorEnabled == null) {
            this.builtInSlabExtractorEnabled = new SimpleBooleanProperty();
        }
        return builtInSlabExtractorEnabled;
    }

    public void setBuiltInSlabExtractorEnabled(boolean builtInSlabExtractorEnabled) {
        this.builtInSlabExtractorEnabledProperty().set(builtInSlabExtractorEnabled);
    }
}
