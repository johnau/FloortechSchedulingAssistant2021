package tech.jmcs.floortech.scheduling.ui.extractors;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.filesearch.FileWalkerNoChildren;
import tech.jmcs.floortech.scheduling.app.settings.SettingsHolder;
import tech.jmcs.floortech.scheduling.app.types.DataSourceExtractorType;
import tech.jmcs.floortech.scheduling.app.util.PathUtilities;
import tech.jmcs.floortech.scheduling.ui.AutoFillManagerFX;
import tech.jmcs.floortech.scheduling.ui.DataExtractorDescriptorFX;
import tech.jmcs.floortech.scheduling.ui.ExtractedDataHolderFX;
import tech.jmcs.floortech.scheduling.ui.ExtractorComponentHolderFX;
import tech.jmcs.floortech.scheduling.ui.settings.SettingsPresenter;
import tech.jmcs.floortech.scheduling.ui.settings.SettingsView;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

public class ExtractorsPresenter implements Initializable {
    protected static final Logger LOG = LoggerFactory.getLogger(ExtractorsPresenter.class);

    @Inject private SettingsHolder settingsHolder;
    @Inject private ExtractedDataHolderFX extractedDataHolderFX;
    @Inject private ExtractorComponentHolderFX extractorComponentHolderFX;
    @Inject private SettingsView settingsView;
    @Inject AutoFillManagerFX autoFillManagerFX;

    @FXML private TextField detailingFolderPathTextfield;
    @FXML private VBox dataSourceVbox; // where data source rows are added

    private Path lastJobDetailingFolderChooserPath;

    private Consumer<Boolean> fillExtractors = aBoolean -> Platform.runLater(() -> autoFillExtractorsFromJobFolderLocation());
    private Consumer<Boolean> clearExtractedData = aBoolean -> this.extractedDataHolderFX.clear();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.debug("ExtractorsPresenter initializing... (autoFillManager: {})", this.autoFillManagerFX.hashCode());
        setupBindingToSettings();
//        setupJobNumberListener();
        toggleBuiltInExtractors();

        this.autoFillManagerFX.setClearExtractedData(this.clearExtractedData);
        this.autoFillManagerFX.setAutoFillExtractors(this.fillExtractors);
        this.autoFillManagerFX.setExtractorHolder(this.extractorComponentHolderFX);
        this.autoFillManagerFX.setExtractorDataHolder(this.extractedDataHolderFX);
//        this.autoFillManagerFX.setDetailingJobFolderPath(this.detailingFolderPathTextfield.textProperty());
//        LOG.debug("Detailing Folder Path Text Field Hash Code: {}", this.detailingFolderPathTextfield.textProperty().hashCode());
        this.detailingFolderPathTextfield.textProperty().bindBidirectional(this.autoFillManagerFX.getDetailingJobFolderPathProperty());
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

    @FXML
    public void handleAutoFillAction(ActionEvent event) {
        this.clearExtractedData.accept(true);
        autoFillExtractorsFromJobFolderLocation();
        LOG.debug("Detailing Folder Path Text Field hashcode {}", this.detailingFolderPathTextfield.textProperty().hashCode());
    }

    private void autoFillExtractorsFromJobFolderLocation() {
        this.dataSourceVbox.getScene().getRoot().setDisable(true);// disable the entire scene
        if (this.detailingFolderPathTextfield.getText().isEmpty()) {
            informUser("No folder entered", "Enter a folder path into the field and try again");
            return;
        }
        Path d = Paths.get(this.detailingFolderPathTextfield.getText());
        if (!Files.exists(d, LinkOption.NOFOLLOW_LINKS) || !Files.isDirectory(d)) {
            informUser("Folder does not exist", "The folder path entered in the field was not a valid directory/path. Please try another path and try again");
            return;
        }
        // get all files in the folder (single level / no recurse)
        List<Path> existingFiles = checkFolderForExistingFiles(d);
        for (Path path : existingFiles) {
            LOG.debug("File found in detailng folder: {}", path.toString());
            String ext = FilenameUtils.getExtension(path.getFileName().toString());
            if (ext.equalsIgnoreCase("xls") || ext.equalsIgnoreCase("xlsx")) {
                String ucaseFileName = path.getFileName().toString().toUpperCase();
                if (ucaseFileName.contains("BEAM") && ucaseFileName.contains("LIST")) {
                    System.out.println("Found beam list: " + path.toString());
                    Map.Entry<String, DataExtractorDescriptorFX> r = this.extractorComponentHolderFX.getActiveExtractors()
                            .entrySet()
                            .stream()
                            .filter(f -> f.getKey().equalsIgnoreCase(DataSourceExtractorType.BEAM.getName()))
                            .findFirst().orElse(null);
                    if (r != null) {
                        r.getValue().setFilePathText(path.toString());
                    }
                } else if (ucaseFileName.contains("SHEET") && ucaseFileName.contains("LIST")) {
                    System.out.println("Found sheet list: " + path.toString());
                    Map.Entry<String, DataExtractorDescriptorFX> r = this.extractorComponentHolderFX.getActiveExtractors()
                            .entrySet()
                            .stream()
                            .filter(f -> f.getKey().equalsIgnoreCase(DataSourceExtractorType.SHEET.getName()))
                            .findFirst().orElse(null);
                    if (r != null) {
                        r.getValue().setFilePathText(path.toString());
                    }
                } else if (ucaseFileName.contains("LIST") &&
                        (ucaseFileName.contains("HOPLEY") || ucaseFileName.contains("HJ300") || ucaseFileName.contains("HJ200"))) {
                    System.out.println("Found Hopley truss list: " + path.toString());
                    Map.Entry<String, DataExtractorDescriptorFX> r = this.extractorComponentHolderFX.getActiveExtractors()
                            .entrySet()
                            .stream()
                            .filter(f -> f.getKey().equalsIgnoreCase(DataSourceExtractorType.TRUSS_HOPLEY.getName()))
                            .findFirst().orElse(null);
                    if (r != null) {
                        r.getValue().setFilePathText(path.toString());
                    }
                } else if (ucaseFileName.contains("LIST") &&
                        (ucaseFileName.contains("COLDWRIGHT") || ucaseFileName.contains("CW260") || ucaseFileName.contains("CW346") || ucaseFileName.contains("CX346"))) {
                    System.out.println("Found Coldwright truss list: " + path.toString());
                    Map.Entry<String, DataExtractorDescriptorFX> r = this.extractorComponentHolderFX.getActiveExtractors()
                            .entrySet()
                            .stream()
                            .filter(f -> f.getKey().equalsIgnoreCase(DataSourceExtractorType.TRUSS_COLDWRIGHT.getName()))
                            .findFirst().orElse(null);
                    if (r != null) {
                        r.getValue().setFilePathText(path.toString());
                    }
                } else { // assume its the cw260/cw346 listing for now...
                    System.out.println("Found (assumed) Coldwright truss list: " + path.toString());
                    Map.Entry<String, DataExtractorDescriptorFX> r = this.extractorComponentHolderFX.getActiveExtractors()
                            .entrySet()
                            .stream()
                            .filter(f -> f.getKey().equalsIgnoreCase(DataSourceExtractorType.TRUSS_COLDWRIGHT.getName()))
                            .findFirst().orElse(null);
                    if (r != null) {
                        r.getValue().setFilePathText(path.toString());
                    }
                }
            } else if (ext.equalsIgnoreCase("pdf")) {
                System.out.println("Found Floortech job PDF: " + path.toString());
                Path parentFolder = path.getParent().getFileName();
                Path fileName = path.getFileName();
                String fn = FilenameUtils.removeExtension(fileName.toString()).trim();
                String pfn = parentFolder.toString().trim();
                if (fn.equalsIgnoreCase(pfn)) {
                    Map.Entry<String, DataExtractorDescriptorFX> r = this.extractorComponentHolderFX.getActiveExtractors()
                            .entrySet()
                            .stream()
                            .filter(f -> f.getKey().equalsIgnoreCase(DataSourceExtractorType.SLAB.getName()))
                            .findFirst().orElse(null);
                    if (r != null) {
                        r.getValue().setFilePathText(path.toString());
                    }
                }
            }
        }
        this.dataSourceVbox.getScene().getRoot().setDisable(false); // enable the entire scene
    }

    private List<Path> checkFolderForExistingFiles(Path dirToCheck) {
        if (dirToCheck == null) {
            LOG.warn("Null path to check for existing files");
            return null;
        }

        FileWalkerNoChildren filewalker = new FileWalkerNoChildren();
        HashMap<String, List> res = filewalker.getFilesLimited(dirToCheck.toString(), Arrays.asList("*"));
        List<Path> paths = res.get("paths");
        if ( !paths.isEmpty() ) {
            paths.forEach( (path) -> {
                LOG.info("Processing existing .{} file in folder -- '{}' '{}'", Arrays.asList("*"), path.getFileName(), path);

            });
        }
        return paths;
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
        // use trussextractor enabled for both coldwright and hopley

        VBox beamVbox = this.extractorComponentHolderFX.getExtractorMap().get(DataSourceExtractorType.BEAM.getName()).getExtractorVbox();
        VBox sheetVbox = this.extractorComponentHolderFX.getExtractorMap().get(DataSourceExtractorType.SHEET.getName()).getExtractorVbox();
        VBox slabVbox = this.extractorComponentHolderFX.getExtractorMap().get(DataSourceExtractorType.SLAB.getName()).getExtractorVbox();
        VBox trussColdwrightVbox = this.extractorComponentHolderFX.getExtractorMap().get(DataSourceExtractorType.TRUSS_COLDWRIGHT.getName()).getExtractorVbox();
        VBox trussHopleyVbox = this.extractorComponentHolderFX.getExtractorMap().get(DataSourceExtractorType.TRUSS_HOPLEY.getName()).getExtractorVbox();

        if (beamExtractorEnabled) {// add the extractor to the view if not present
            Optional<Node> existing = this.dataSourceVbox.getChildren().stream().filter(f -> f.equals(beamVbox)).findFirst();
            if (!existing.isPresent()) this.dataSourceVbox.getChildren().add(beamVbox);
            this.extractorComponentHolderFX.setExtractorEnabled(DataSourceExtractorType.BEAM.getName(), true);
        } else { // remove the extractor from the view
            this.dataSourceVbox.getChildren().remove(beamVbox);
            this.extractorComponentHolderFX.setExtractorEnabled(DataSourceExtractorType.BEAM.getName(), false);
        }

        if (sheetExtractorEnabled) {
            Optional<Node> existing = this.dataSourceVbox.getChildren().stream().filter(f -> f.equals(sheetVbox)).findFirst();
            if (!existing.isPresent()) this.dataSourceVbox.getChildren().add(sheetVbox);
            this.extractorComponentHolderFX.setExtractorEnabled(DataSourceExtractorType.SHEET.getName(), true);
        } else {
            this.dataSourceVbox.getChildren().remove(sheetVbox);
            this.extractorComponentHolderFX.setExtractorEnabled(DataSourceExtractorType.SHEET.getName(), false);
        }

        if (slabExtractorEnabled) {
            Optional<Node> existing = this.dataSourceVbox.getChildren().stream().filter(f -> f.equals(slabVbox)).findFirst();
            if (!existing.isPresent()) this.dataSourceVbox.getChildren().add(slabVbox);
            this.extractorComponentHolderFX.setExtractorEnabled(DataSourceExtractorType.SLAB.getName(), true);
        } else {
            this.dataSourceVbox.getChildren().remove(slabVbox);
            this.extractorComponentHolderFX.setExtractorEnabled(DataSourceExtractorType.SLAB.getName(), false);
        }

        if (trussExtractorEnabled) {
            Node ec = this.dataSourceVbox.getChildren().stream().filter(f -> f.equals(trussColdwrightVbox)).findFirst().orElse(null);
            if (ec == null) {
                this.dataSourceVbox.getChildren().add(trussColdwrightVbox);
            }
            this.extractorComponentHolderFX.setExtractorEnabled(DataSourceExtractorType.TRUSS_COLDWRIGHT.getName(), true);

            Node eh = this.dataSourceVbox.getChildren().stream().filter(f -> f.equals(trussHopleyVbox)).findFirst().orElse(null);
            if (eh == null) {
                this.dataSourceVbox.getChildren().add(trussHopleyVbox);
            }
            this.extractorComponentHolderFX.setExtractorEnabled(DataSourceExtractorType.TRUSS_HOPLEY.getName(), true);
        } else {
            this.dataSourceVbox.getChildren().remove(trussColdwrightVbox);
            this.extractorComponentHolderFX.setExtractorEnabled(DataSourceExtractorType.TRUSS_COLDWRIGHT.getName(), false);

            this.dataSourceVbox.getChildren().remove(trussHopleyVbox);
            this.extractorComponentHolderFX.setExtractorEnabled(DataSourceExtractorType.TRUSS_HOPLEY.getName(), false);
        }
    }

    private void informUser(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(header);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
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