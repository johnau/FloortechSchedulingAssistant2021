package tech.jmcs.floortech.scheduling.ui;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.filesearch.FileWalkerNoChildren;
import tech.jmcs.floortech.scheduling.app.filesearch.FolderWalkerNoChildren;
import tech.jmcs.floortech.scheduling.app.settings.SettingsHolder;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class AutoFillManagerFX {
    protected static final Logger LOG = LoggerFactory.getLogger(AutoFillManagerFX.class);

//    @Inject private ExtractedDataHolderFX extractedDataHolder;

    private SettingsHolder settingsHolder;
//    @Inject private ExtractorManagerFX extractorManagerFX;

    private StringProperty jobNumber;
    private StringProperty detailingJobFolderPath;
    private Consumer<Boolean> autoFillExtractors;
    private StringProperty dataTargetPath;
    private ExtractorComponentHolderFX extractorHolder;
    private ExtractedDataHolderFX extractorDataHolder;

    private Button extractButton;
    private Button commitButton;
    private TextField jobNumberField;
    private TabPane dataFrameTabPane;
    private Consumer<Boolean> clearExtractedData;

    public AutoFillManagerFX(SettingsHolder settingsHolder) {
        this.settingsHolder = settingsHolder;
        this.detailingJobFolderPath = new SimpleStringProperty(this, "detailingJobFolderPath");
        this.dataTargetPath = new SimpleStringProperty(this, "dataTargetPath");
    }

    public void setDataFrameTabPane(TabPane dataFrameTabPane) {
        this.dataFrameTabPane = dataFrameTabPane;
    }

    public void setJobNumber(StringProperty jobNumber) {
        LOG.trace("{} | Setting job number stringproperty: {}", this.hashCode(), jobNumber);
        this.jobNumber = jobNumber;
    }

//    public void setDetailingJobFolderPath(StringProperty detailingJobFolderPath) {
//        this.detailingJobFolderPath = detailingJobFolderPath;
//        LOG.debug("{} | Set detailing job folder path string property: {} : {}", this.hashCode(), this.detailingJobFolderPath, this.detailingJobFolderPath.hashCode());
//    }

    public StringProperty getDetailingJobFolderPathProperty() {
        return detailingJobFolderPath;
    }

    public void setAutoFillExtractors(Consumer<Boolean> autoFillExtractors) {
        LOG.trace("{} | Setting auto fill extractors consumer: {}", this.hashCode(), autoFillExtractors);
        this.autoFillExtractors = autoFillExtractors;
    }

    public StringProperty getDataTargetPathProperty() {
        return dataTargetPath;
    }

    //    public void setDataTargetPath(StringProperty dataTargetPath) {
//        LOG.debug("{} | Setting data target path stringproperty: {}", this.hashCode(), dataTargetPath);
//        this.dataTargetPath = dataTargetPath;
//    }

    public void setExtractButton(Button extractButton) {
        LOG.trace("{} | Setting extract button: {}", this.hashCode(), extractButton);
        this.extractButton = extractButton;
    }

    public void setCommitButton(Button commitButton) {
        LOG.trace("{} | Setting commit button: {}", this.hashCode(), commitButton);
        this.commitButton = commitButton;
    }

    public void setExtractorHolder(ExtractorComponentHolderFX extractorHolder) {
        this.extractorHolder = extractorHolder;
    }

    public void setExtractorDataHolder(ExtractedDataHolderFX extractorDataHolder) {
        this.extractorDataHolder = extractorDataHolder;
    }

    public void setJobNumberField(TextField jobNumberField) {
        this.jobNumberField = jobNumberField;
    }

    public boolean doAutoFill() {
        if (this.clearExtractedData != null) {
            this.clearExtractedData.accept(true);
        }

        FolderWalkerNoChildren folderWalker = new FolderWalkerNoChildren();
        FileWalkerNoChildren fileWalker = new FileWalkerNoChildren();

        // look up detaling job folder path with job number
        if (this.jobNumber == null) {
            LOG.debug("No job number");
            return false;
        }

        String jobNumber = this.jobNumber.getValue();
        if (jobNumber == null || jobNumber.isEmpty()) {
            LOG.debug("No job number");
            return false;
        }

        Path jobFolder = null;
        Path detailingRoot = this.settingsHolder.getJobFoldersDetailingRootPath();
        List<Path> yearFolders = folderWalker.getFoldersLimited(detailingRoot.toString());
        Collections.reverse(yearFolders);
        for (Path yearFolder : yearFolders) {
            LOG.debug("Checking folder: {}", yearFolder.toString());
            Path jfp = Paths.get(yearFolder.toString(), this.jobNumber.getValue());
            File jff = jfp.toFile();
            if (jff.exists() && jff.isDirectory()) {
                LOG.debug("Found job folder: {}", jfp.toString());
                jobFolder = jfp;
                break;
            }
        }

        // set to detailing job folder path
        if (jobFolder != null && this.detailingJobFolderPath != null) {
            this.detailingJobFolderPath.setValue(jobFolder.toString());
        } else {
            LOG.debug("Couldn't set DetailingJobFolderPath field '{}' '{}'", jobFolder, this.detailingJobFolderPath);
            return false;
        }

        System.out.println("Found job folder: " + jobFolder.toString());

        // run the auto fill extractors consumer
        if (this.autoFillExtractors != null) {
            this.autoFillExtractors.accept(true);
        } else {
            LOG.debug("AutoFill consumer is null");
            return false;
        }

        // lookup the quote file
        Path quoteFilePath = null;
        Path quoteRoot = this.settingsHolder.getJobFilesSchedulingRootPath();
        List<Path> quoteYearFolders = folderWalker.getFoldersLimited(quoteRoot.toString());
        Collections.reverse(quoteYearFolders);
        for (Path yearFolder : quoteYearFolders) {
            if (quoteFilePath != null) {
                LOG.debug("Found quoteFilePath: {}", quoteFilePath.toString());
                break;
            }
            List<Path> builderFolders = folderWalker.getFoldersLimited(yearFolder.toString());
            for (Path builderFolder : builderFolders) {
                if (quoteFilePath != null) {
                    break;
                }
                List<Path> quoteFiles = fileWalker.getFilesLimited(builderFolder.toString(), Arrays.asList("xls", "xlsx")).get("paths");
                if (quoteFiles != null) {
//                    quoteFiles.forEach(x -> LOG.trace("Quote file: {}", x));
                    quoteFilePath = quoteFiles.stream()
                            .filter(f -> f.getFileName().toString().toLowerCase().contains(jobNumber) && !f.toString().isEmpty())
                            .findFirst().orElse(null);
                }
            }
        }

        // set the data target path
        if (quoteFilePath != null && this.dataTargetPath != null) {
            this.dataTargetPath.setValue(quoteFilePath.toString());
        } else {
            LOG.debug("Could not set qutoe file (data target) path");
            return false;
        }

        System.out.println("Found quote file: " + quoteFilePath.toString());

        // set focus to the extract button
        if (this.extractButton != null) {
            Platform.runLater(() -> this.extractButton.requestFocus());
        } else {
            LOG.debug("Extract button is not available");
        }
        return true;
    }

    public void setFocusToCommit() {
        if (this.commitButton != null) {
            Platform.runLater(() -> this.commitButton.requestFocus());
        }
    }

    public void resetAutoFill() {
        if (this.extractorHolder != null) {
            this.extractorHolder.getActiveExtractors().forEach((n, e) -> {
                Platform.runLater( () -> {
                    e.setFilePathText("");
                });
            });
        }

        if (this.dataTargetPath != null) {
            this.dataTargetPath.setValue("");
        }

        if (this.detailingJobFolderPath != null) {
            this.detailingJobFolderPath.setValue("");
        }

//        // don;t clear extracted data after commit, might want to look at it.
//        if (this.clearExtractedData != null) {
//            this.clearExtractedData.accept(true);
//        }

        if (this.jobNumberField != null) {
            this.jobNumber.setValue("");
            Platform.runLater( ()-> {
                this.jobNumberField.requestFocus();
            });
        }

//        clearExtractedData();

    }

//    public void clearExtractedData() {
//        if (this.dataFrameTabPane != null) {
//            this.extractorDataHolder.clear();
////            Platform.runLater(() -> {
////                this.dataFrameTabPane.getTabs().clear(); // need to clear the ExtractorManager too
////            });
//        }
//    }

    public void setClearExtractedData(Consumer<Boolean> clearExtractedData) {
        this.clearExtractedData = clearExtractedData;
    }
}
