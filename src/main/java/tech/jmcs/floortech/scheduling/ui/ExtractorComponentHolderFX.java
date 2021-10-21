package tech.jmcs.floortech.scheduling.ui;

import com.sun.xml.internal.ws.util.StringUtils;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.settings.SettingsHolder;
import tech.jmcs.floortech.scheduling.app.types.DataSourceExtractorType;
import tech.jmcs.floortech.scheduling.app.types.FileType;
import tech.jmcs.floortech.scheduling.app.util.PathUtilities;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExtractorComponentHolderFX {
    protected static final Logger LOG = LoggerFactory.getLogger(ExtractorComponentHolderFX.class);

    private SettingsHolder settingsHolder;

    private Map<String, DataExtractorDescriptorFX> dataExtractorMapFX;

    private Path lastPathChosen;

    public ExtractorComponentHolderFX(SettingsHolder settingsHolder) {
        LOG.info("ExtractorComponentHolderFX...");
        this.settingsHolder = settingsHolder;
        this.dataExtractorMapFX = new HashMap<>();
    }

    /**
     * Get the Map of Extractor Descriptors
     * @return Map of Extractor Descriptors
     */
    public Map<String, DataExtractorDescriptorFX> getExtractorMap() {
        return dataExtractorMapFX;
    }

    /**
     * Get the Map of Active Extractor Descriptors
     * @return Map of Active Extractor Descriptors
     */
    public Map<String, DataExtractorDescriptorFX> getActiveExtractors() {
        return this.dataExtractorMapFX.entrySet().stream()
                .filter(f -> f.getValue().getEnabled())
                .collect(Collectors.toMap(
                        m -> m.getKey(),
                        m -> m.getValue())
                );
    }

    /**
     * Enable or disable an extractor.
     * @param name
     * @param enabled
     */
    public void setExtractorEnabled(String name, Boolean enabled) {
        Optional<DataExtractorDescriptorFX> extractor = dataExtractorMapFX.entrySet().stream()
                .filter(f -> f.getKey().toUpperCase().equals(name.toUpperCase()))
                .map(m -> m.getValue())
                .findFirst();
        if (extractor.isPresent()) {
            extractor.get().setEnabled(enabled);
        }
    }

    /**
     * Build All Extractors (present in Settings)
     */
    public void buildAll() {
        buildAllBuiltIns();
    }

    private void buildAllBuiltIns() {
        this.buildBuiltInExtractor(DataSourceExtractorType.BEAM);
        this.buildBuiltInExtractor(DataSourceExtractorType.SLAB);
        this.buildBuiltInExtractor(DataSourceExtractorType.SHEET);
        this.buildBuiltInExtractor(DataSourceExtractorType.TRUSS_COLDWRIGHT);
        this.buildBuiltInExtractor(DataSourceExtractorType.TRUSS_HOPLEY);
    }

    private void buildBuiltInExtractor(DataSourceExtractorType type) {
        DataExtractorDescriptorFX extractorDescriptor = new DataExtractorDescriptorFX();

        VBox vbox = new VBox();
        vbox.setPrefHeight(VBox.USE_COMPUTED_SIZE);
        vbox.setPrefWidth(VBox.USE_COMPUTED_SIZE);
        vbox.getStyleClass().add("small-padded");

        Label headerLabel = new Label();
        headerLabel.setText(type.getName() + " Extractor (Built-in)");
        headerLabel.getStyleClass().add("sub-header");

        HBox hbox = new HBox();
        hbox.setPrefHeight(HBox.USE_COMPUTED_SIZE);
        hbox.setPrefWidth(HBox.USE_COMPUTED_SIZE);
        hbox.getStyleClass().add("row-container");

        Label fieldLabel = new Label();
        fieldLabel.setText("File path: ");
        fieldLabel.getStyleClass().add("field-label");

        TextField pathField = new TextField();
        HBox.setHgrow(pathField, Priority.ALWAYS);
        pathField.setPromptText("eg. C:\\..\\jobs\\19000\\" + FileType.fileTypesMap.get(type.getFileType()));
        pathField.getStyleClass().add("field");

        Button browseButton = new Button();
        browseButton.setText("Browse...");
        browseButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            if (lastPathChosen == null) {
                Path detailingRoot = settingsHolder.getJobFoldersDetailingRootPath();
                if (detailingRoot != null && !detailingRoot.toString().isEmpty()) {
                    fileChooser.setInitialDirectory(PathUtilities.getNearestDirectory(detailingRoot));
                }
            } else {
                fileChooser.setInitialDirectory(PathUtilities.getNearestDirectory(lastPathChosen));
            }

//          fileChooser.setInitialFileName(); // TODO: Generate expected filenames and set
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(StringUtils.capitalize(type.getFileType()) + " Files", FileType.fileTypesMap.get(type.getFileType()));
            FileChooser.ExtensionFilter extFilterAll = new FileChooser.ExtensionFilter("All Files", FileType.fileTypesMap.get("ALL"));
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.getExtensionFilters().add(extFilterAll);
            fileChooser.setSelectedExtensionFilter(extFilter);
            fileChooser.setTitle(type.getName() + " | Choose Data File for Extractor...");

            File fileChosen = fileChooser.showOpenDialog(vbox.getScene().getWindow());
            if (fileChosen != null) {
                String filePath = fileChosen.getPath();
                pathField.setText(filePath);
                lastPathChosen = Paths.get(filePath);
            }
        });

        hbox.getChildren().addAll(fieldLabel, pathField, browseButton);

        Separator separator = new Separator();
        separator.setOrientation(Orientation.HORIZONTAL);

        HBox fileNameContainer = new HBox();
        HBox.setHgrow(fileNameContainer, Priority.ALWAYS);
        HBox filler = new HBox();
        filler.setPrefWidth(100D);
        Label fileNameLabel = new Label("");
        fileNameLabel.setAlignment(Pos.TOP_CENTER);
        fileNameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #0099ff;");
        HBox.setHgrow(fileNameLabel, Priority.ALWAYS);
        fileNameContainer.getChildren().addAll(filler, fileNameLabel);

        pathField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                fileNameLabel.setText("");
                return;
            }
            Path p = Paths.get(newValue);
            String cf = p.getFileName().toString();
            fileNameLabel.setText(cf);
        });

        vbox.getChildren().addAll(headerLabel, hbox, fileNameContainer, separator);

        extractorDescriptor.setName(type.getName());
        extractorDescriptor.setType(type);
        extractorDescriptor.setExtractorVbox(vbox);
        extractorDescriptor.setFilePathTextProperty(pathField.textProperty());
//        extractorDescriptor.setTextField(pathField);
        extractorDescriptor.setCustomExtractorDetails(null);
        extractorDescriptor.setEnabled(true);

        this.dataExtractorMapFX.put(extractorDescriptor.getName(), extractorDescriptor);
//        this.enabledDataSources.put(extractorDescriptor.getName(), true);
    }
}
