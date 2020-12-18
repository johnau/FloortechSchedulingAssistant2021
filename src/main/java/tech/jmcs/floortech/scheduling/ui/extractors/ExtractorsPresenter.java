package tech.jmcs.floortech.scheduling.ui.extractors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.settings.SettingsHolder;
import tech.jmcs.floortech.scheduling.app.types.DataSourceExtractorType;
import tech.jmcs.floortech.scheduling.app.util.PathUtilities;
import tech.jmcs.floortech.scheduling.ui.ExtractedDataHolderFX;
import tech.jmcs.floortech.scheduling.ui.ExtractorComponentHolderFX;
import tech.jmcs.floortech.scheduling.ui.settings.SettingsPresenter;
import tech.jmcs.floortech.scheduling.ui.settings.SettingsView;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public class ExtractorsPresenter implements Initializable {
    protected static final Logger LOG = LoggerFactory.getLogger(ExtractorsPresenter.class);

    @Inject private SettingsHolder settingsHolder;
    @Inject private ExtractedDataHolderFX extractedDataHolder;
    @Inject private ExtractorComponentHolderFX extractorHolder;
    @Inject private SettingsView settingsView;

    @FXML private TextField detailingFolderPathTextfield;
    @FXML private VBox dataSourceVbox; // where data source rows are added

    private Path lastJobDetailingFolderChooserPath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.debug("ExtractorsPresenter initializing...");
        setupBindingToSettings();
        setupJobNumberListener();
        toggleBuiltInExtractors();
    }

    @FXML
    public void handleAddExtractorButtonAction(ActionEvent event) {
        LOG.info("Handle Add Data Source Button Action Not Yet Implemented");
    }

    @FXML
    public void handleBrowseJobFolderButtonAction(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();

        chooser.setTitle("Choose Job Detailing Folder...");

        Path detailingRoot = this.settingsHolder.getJobFoldersDetailingRootPath();

        if (this.lastJobDetailingFolderChooserPath != null) { // set the last path used if exists
            chooser.setInitialDirectory(PathUtilities.getNearestDirectory(this.lastJobDetailingFolderChooserPath));
        } else if (detailingRoot != null && !detailingRoot.toString().isEmpty()) { // else set from settings
            chooser.setInitialDirectory(PathUtilities.getNearestDirectory(detailingRoot));
        }

        File dirChosen = chooser.showDialog(this.dataSourceVbox.getScene().getWindow());
        if (dirChosen != null) {
            this.detailingFolderPathTextfield.setText(dirChosen.getPath());
            this.lastJobDetailingFolderChooserPath = dirChosen.toPath();
        }
    }

    /**
     * Private method to add a change listener to the SettingsHolder's lastUpdated property.
     * This allows this view to react to changes in settings.
     */
    private void setupBindingToSettings() {
        SettingsPresenter settingsPresenter = (SettingsPresenter) settingsView.getPresenter();
        Consumer<Boolean> onSettingsSaveConsumer = aBoolean -> {
            toggleBuiltInExtractors();
        };
        settingsPresenter.addListenerOnSave(onSettingsSaveConsumer);
//        settingsHolder.getLastUpdatedProperty().addListener((observable, oldDate, newDate) -> {
//            if (newDate.after(oldDate)) toggleBuiltInExtractors();
//        });
    }

    /**
     * Toggles activated extractors based on settings
     */
    private void toggleBuiltInExtractors() {
        Boolean beamExtractorEnabled = this.settingsHolder.isBuiltInBeamExtractorEnabled();
        Boolean sheetExtractorEnabled = this.settingsHolder.isBuiltInSheetExtractorEnabled();
        Boolean slabExtractorEnabled = this.settingsHolder.isBuiltInSlabExtractorEnabled();
        Boolean trussExtractorEnabled = this.settingsHolder.isBuiltInTrussExtractorEnabled();

        VBox beamVbox = this.extractorHolder.getExtractorMap().get(DataSourceExtractorType.BEAM.getName()).getExtractorVbox();
        VBox sheetVbox = this.extractorHolder.getExtractorMap().get(DataSourceExtractorType.SHEET.getName()).getExtractorVbox();
        VBox slabVbox = this.extractorHolder.getExtractorMap().get(DataSourceExtractorType.SLAB.getName()).getExtractorVbox();
        VBox trussVbox = this.extractorHolder.getExtractorMap().get(DataSourceExtractorType.TRUSS.getName()).getExtractorVbox();

        if (beamExtractorEnabled) {// add the extractor to the view if not present
            Optional<Node> existing = this.dataSourceVbox.getChildren().stream().filter(f -> f.equals(beamVbox)).findFirst();
            if (!existing.isPresent()) this.dataSourceVbox.getChildren().add(beamVbox);
            this.extractorHolder.setExtractorEnabled(DataSourceExtractorType.BEAM.getName(), true);
        } else { // remove the extractor from the view
            this.dataSourceVbox.getChildren().remove(beamVbox);
            this.extractorHolder.setExtractorEnabled(DataSourceExtractorType.BEAM.getName(), false);
        }

        if (sheetExtractorEnabled) {
            Optional<Node> existing = this.dataSourceVbox.getChildren().stream().filter(f -> f.equals(sheetVbox)).findFirst();
            if (!existing.isPresent()) this.dataSourceVbox.getChildren().add(sheetVbox);
            this.extractorHolder.setExtractorEnabled(DataSourceExtractorType.SHEET.getName(), true);
        } else {
            this.dataSourceVbox.getChildren().remove(sheetVbox);
            this.extractorHolder.setExtractorEnabled(DataSourceExtractorType.SHEET.getName(), false);
        }

        if (slabExtractorEnabled) {
            Optional<Node> existing = this.dataSourceVbox.getChildren().stream().filter(f -> f.equals(slabVbox)).findFirst();
            if (!existing.isPresent()) this.dataSourceVbox.getChildren().add(slabVbox);
            this.extractorHolder.setExtractorEnabled(DataSourceExtractorType.SLAB.getName(), true);
        } else {
            this.dataSourceVbox.getChildren().remove(slabVbox);
            this.extractorHolder.setExtractorEnabled(DataSourceExtractorType.SLAB.getName(), false);
        }

        if (trussExtractorEnabled) {
            Optional<Node> existing = this.dataSourceVbox.getChildren().stream().filter(f -> f.equals(trussVbox)).findFirst();
            if (!existing.isPresent()) this.dataSourceVbox.getChildren().add(trussVbox);
            this.extractorHolder.setExtractorEnabled(DataSourceExtractorType.TRUSS.getName(), true);
        } else {
            this.dataSourceVbox.getChildren().remove(trussVbox);
            this.extractorHolder.setExtractorEnabled(DataSourceExtractorType.TRUSS.getName(), false);
        }
    }

    /**
     * Private method to setup listener on the Job Number TextField for automatic lookup.
     * Change an icon when job number is valid and ready for lookup.
     */
    private void setupJobNumberListener() {
//        jobNumberTextfield.textProperty().addListener((observable, oldValue, newValue) -> {
//            if (FloortechHelper.isValidJobNumber(newValue)) {
//                LOG.debug("Lookup job number: {}", newValue);
//                // TODO: Implement as a notification that files and folders have been located (async lookup)
//            } else {
//                LOG.debug("Job number entered is not valid: {}", newValue);
//            }
//        });
    }
}