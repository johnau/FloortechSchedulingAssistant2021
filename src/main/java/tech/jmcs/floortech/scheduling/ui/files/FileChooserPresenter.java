package tech.jmcs.floortech.scheduling.ui.files;

import com.sun.xml.internal.ws.util.StringUtils;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.datasource.extractor.*;
import tech.jmcs.floortech.scheduling.app.datasource.model.BeamData;
import tech.jmcs.floortech.scheduling.app.datasource.model.ExtractedTableData;
import tech.jmcs.floortech.scheduling.app.datasource.model.SlabData;
import tech.jmcs.floortech.scheduling.app.datasource.model.TrussData;
import tech.jmcs.floortech.scheduling.app.exception.DataExtractorException;
import tech.jmcs.floortech.scheduling.app.settings.SettingsHolder;
import tech.jmcs.floortech.scheduling.app.types.DataSourceExtractorType;
import tech.jmcs.floortech.scheduling.app.types.FileType;
import tech.jmcs.floortech.scheduling.app.util.ExcelCellAddress;
import tech.jmcs.floortech.scheduling.app.util.FloortechHelper;
import tech.jmcs.floortech.scheduling.app.util.PathUtilities;
import tech.jmcs.floortech.scheduling.app.util.XLSHelper;
import tech.jmcs.floortech.scheduling.ui.data.ObservableExtractedData;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

public class FileChooserPresenter implements Initializable {
    protected static final Logger LOG = LoggerFactory.getLogger(FileChooserPresenter.class);

    @Inject
    private SettingsHolder settingsHolder;

    @Inject
    private ObservableExtractedData extractedDataHolder;

    @FXML
    private TextField jobNumberTextfield;

    @FXML
    private Button autoFillAllButton;

    @FXML
    private TextField schedulingFilePathTextfield;

    @FXML
    private Button schedulingFilePathBrowseButton;

    @FXML
    private TextField detailingFolderPathTextfield;

    @FXML
    private Button detailingFolderPathBrowseButton;

    @FXML
    private Button addDataSourceButton; // add data sources for extraction (file_path field, browse button, data source type choicebox [populated from settings] )

    @FXML
    private HBox addDataSourceButtonHbox; // to use the whole area as a button trigger, fire same command as button

    @FXML
    private VBox dataSourceVbox; // where data source rows are added

//    private List<DataExtractorDescriptor> dataSourceList;
//    private Map<String, DataExtractorDescriptor> dataSourceMap;
//    private Map<DataSourceExtractorType, VBox> builtInExtractorVBoxMap;

    private Map<String, DataExtractorDescriptor> dataSourceMap;
    private Map<String, Boolean> enabledDataSources;

    private Path lastJobDetailingFolderChooserPath;
    private Path lastJobSchedulingFileChooserPath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.debug("FileChooserPresenter initialized");
//        this.dataSourceList = new ArrayList<>();
        this.dataSourceMap = new HashMap<>();
        this.enabledDataSources = new HashMap<>();
//        this.builtInExtractorVBoxMap = new HashMap<>();
        setupJobNumberListener();
        setupBindingToSettings();
        installBuiltInExtractorComponents();
        toggleBuiltInExtractors();
    }

    @FXML
    public void handleExtractDataButtonAction(ActionEvent event) {
        // clear the extracted data holder
        this.extractedDataHolder.clear();

        // process any built in data extractors present
        this.enabledDataSources.forEach( (n, v) -> {
            LOG.debug("Data source: {} is enabled: {}", n, v);
            DataExtractorDescriptor descriptor = this.dataSourceMap.get(n);

            if (v) {
                String path = descriptor.getFilePathText();
                if (path == null || path.isEmpty()) {
                    LOG.warn("No file provided for '{}'", n);
                } else {
                    LOG.debug("Extracting '{}' from file '{}'", n, path);
                    boolean success = processDataSourceExtraction(descriptor);
                    if (!success) {
                        LOG.debug("Could not process data for: '{}' from: '{}'", n, path);
                    }
                }
            }
        });
    }

    private boolean processDataSourceExtraction(DataExtractorDescriptor descriptor) {
        switch (descriptor.getType()) {
            case BEAM: return beamExtract(descriptor);
            case SHEET: return sheetExtract(descriptor);
            case SLAB: return slabExtract(descriptor);
            case TRUSS: return trussExtract(descriptor);
            default: return false;
        }
    }

    private boolean beamExtract(DataExtractorDescriptor descriptor) {
        LOG.debug("Beam extractor: {} {} {}", descriptor.getName(), descriptor.getType(), descriptor.getFilePathText());
        String filePathStr = descriptor.getFilePathText();
        if (filePathStr == null || filePathStr.isEmpty()) {
            LOG.debug("No file to extract");
            return false;
        }
        Path excelFile = Paths.get(filePathStr);
        BeamListExtractor extractor = DataExtractorFactory.openExcelFileAsBeamList(excelFile);
        if (extractor == null) {
            LOG.warn("Could not create extractor, is the file in open in excel? does it exist?");
            return false;
        }

        try {
            extractor.extract();
        } catch (DataExtractorException e) {
            LOG.warn("Data Extractor Exception thrown: [%s] %s \n",  e.getDataSourceName(), e.getMessage());
            return false;
        }

        ExtractedTableData<BeamData> data = extractor.getData();
        if (data == null) {
            LOG.warn("Extraction was not be completed");
            return false;
        }

        Map<Long, BeamData> dataMap = data.getData();
        this.extractedDataHolder.setBeamDataMap(dataMap);

        LOG.info("Extracted Beam Data with Built In Beam Extractor");

        return true;
    }

    private boolean sheetExtract(DataExtractorDescriptor descriptor) {
        LOG.debug("Sheet extractor: {} {} {}", descriptor.getName(), descriptor.getType(), descriptor.getFilePathText());
        String filePathStr = descriptor.getFilePathText();
        if (filePathStr == null || filePathStr.isEmpty()) {
            LOG.debug("No file to extract");
            return false;
        }
        Path excelPath = Paths.get(filePathStr);
        // using Generic Extractor for now

        /**
         * START of Setting up for use of Generic Extractor
         */
        Map<ExcelCellAddress, String> tableLayout = new HashMap<>();
        tableLayout.put(new ExcelCellAddress(0, 2), ExcelDataSourceExtractor.DATA_START);
        tableLayout.put(new ExcelCellAddress(0, 0), "Sheets");
        tableLayout.put(new ExcelCellAddress(0, 1), "Length");
        tableLayout.put(new ExcelCellAddress(1, 1), "ID");
        tableLayout.put(new ExcelCellAddress(2, 1), "Qty");

        String lenColName = "Length";
        String idColName = "ID";
        String qtyColName = "Quantity";

        Map<Integer, GenericExtractorColumnDescription> columnMap = new HashMap<>();
        GenericExtractorColumnDescription lenCol = new GenericExtractorColumnDescription(lenColName, 0, GenericExtractorColumnDataType.NUMERIC);
        GenericExtractorColumnDescription idCol = new GenericExtractorColumnDescription(idColName, 1, GenericExtractorColumnDataType.TEXT);
        GenericExtractorColumnDescription qtyCol = new GenericExtractorColumnDescription(qtyColName, 2, GenericExtractorColumnDataType.NUMERIC);

        columnMap.put(0, lenCol);
        columnMap.put(1, idCol);
        columnMap.put(2, qtyCol);

        List<Integer> validRowDataList = Arrays.asList(1, 2);

        List<Function<Row, String>> recordValidationFunctions = new ArrayList();
        Function<Row, String> recordValidator = (row) -> {
            Cell idCell = XLSHelper.getCellByColumnIndex(row, 1);
            if (idCell == null) return "The ID Cell did not exist";
            if (idCell.getCellType().equals(CellType.STRING)) {
                String cVal = idCell.getStringCellValue().toLowerCase();
                if (cVal.contains("z")) {
                    return null;
                }
            }
            return "The ID Cell did not contain the letter 'Z'. Sheet ID's start with 'Z'";
        };
        recordValidationFunctions.add(recordValidator);
        /**
         * END of Setting up for use of Generic Extractor
         */

        GenericExcelHorizontalTableDataExtractor extractor = DataExtractorFactory.openExcelFileAsGenericList(excelPath, tableLayout, columnMap, validRowDataList, null, recordValidationFunctions);

        try {
            extractor.extract();
        } catch (DataExtractorException e) {
            System.out.printf("Extraction error: %s \n", e.getMessage());
            return false;
        }

        ExtractedTableData<Map<String, Object>> data = extractor.getData();
        if (data == null) {
            System.out.println("Table data was null");
            return false;
        }
        Map<Long, Map<String, Object>> dataMap = data.getData();
        this.extractedDataHolder.addCustomData(dataMap); // custom data always stored as Map<Long, Map<String, Object>>...

        LOG.info("Extracted Sheet Data with Built In (Hardcoded Generic) Sheet Extractor");

        return true;
    }

    private boolean slabExtract(DataExtractorDescriptor descriptor) {
        LOG.debug("Slab extractor: {} {} {}", descriptor.getName(), descriptor.getType(), descriptor.getFilePathText());
        String filePathStr = descriptor.getFilePathText();
        if (filePathStr == null || filePathStr.isEmpty()) {
            LOG.debug("No file to extract");
            return false;
        }
        Path pdfPath = Paths.get(filePathStr);
        SlabListExtractor extractor = DataExtractorFactory.openPdfAsSlabList(pdfPath);
        if (extractor == null) {
            LOG.debug("Could not create extractor, is the file in open in excel? does it exist?");
            return false;
        }

        try {
            extractor.extract();
        } catch (DataExtractorException e) {
            LOG.debug("Could not complete extraction of slab data");
            return false;
        }

        ExtractedTableData<SlabData> data = extractor.getData();
        if (data == null) {
            LOG.warn("Extraction was not be completed");
            return false;
        }

        Map<Long, SlabData> dataMap = data.getData();
        this.extractedDataHolder.setSlabDataMap(dataMap);

        LOG.info("Extracted Slab Data with Built In Slab Extractor");

        return true;
    }

    private boolean trussExtract(DataExtractorDescriptor descriptor) {
        LOG.debug("Truss extractor: {} {} {}", descriptor.getName(), descriptor.getType(), descriptor.getFilePathText());
        String filePathStr = descriptor.getFilePathText();
        if (filePathStr == null || filePathStr.isEmpty()) {
            LOG.debug("No file to extract");
            return false;
        }
        Path excelFile = Paths.get(filePathStr);
        TrussListExtractor extractor = DataExtractorFactory.openExcelFileAsTrussList(excelFile);
        if (extractor == null) {
            LOG.debug("Could not create extractor, is the file in open in excel? does it exist?");
            return false;
        }

        try {
            extractor.extract();
        } catch (DataExtractorException e) {
            LOG.debug("Could not extract Truss List Data from file: {}", descriptor.getFilePathText());
            return false;
        }

        ExtractedTableData<TrussData> data = extractor.getData();
        if (data == null) {
            LOG.warn("Extraction was not be completed");
            return false;
        }

        Map<Long, TrussData> dataMap = data.getData();
        this.extractedDataHolder.setTrussDataMap(dataMap);

        LOG.info("Extracted Truss Data with Built In Truss Extractor");

        return true;
    }

    @FXML
    public void handleAutoFillButtonAction(ActionEvent event) {
        String jobNumberStr = this.jobNumberTextfield.getText();
        boolean valid = FloortechHelper.isValidJobNumber(jobNumberStr);
        if (!valid) {
            LOG.debug("Could not auto-fill, job number was invalid");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invalid Job Number");
            alert.setHeaderText("Invalid Job Number!");
            alert.setContentText("Can not Auto-fill data with invalid job number.  A valid job number is 5 or 6 digits long");
            alert.showAndWait();

            return;
        }

        LOG.warn("Auto fill not yet implemented");
        // lookup scheduling file based on job number
        // populate text field
        // lookup job folder based on job number
        // populate text field
        // process files in folder and assign to enabled data source extractors.
        // populate text fields
    }

    @FXML
    public void handleBrowseSchedulingFileButtonAction(ActionEvent event) {
        FileChooser chooser = new FileChooser();

        FileChooser.ExtensionFilter extFilterXls = new FileChooser.ExtensionFilter("Excel File", FileType.fileTypesMap.get("EXCEL"));
        FileChooser.ExtensionFilter extFilterAll = new FileChooser.ExtensionFilter("All Files", "*.*");

        Path schedulingRoot = this.settingsHolder.getJobFilesSchedulingRootPath();

        if (this.lastJobSchedulingFileChooserPath != null) {
            chooser.setInitialDirectory(PathUtilities.getNearestDirectory(this.lastJobSchedulingFileChooserPath));
        } else if (schedulingRoot != null && !schedulingRoot.toString().isEmpty()) {
            chooser.setInitialDirectory(PathUtilities.getNearestDirectory(schedulingRoot));
        }

        chooser.setTitle("Choose Excel Scheduling file target...");
        chooser.getExtensionFilters().add(extFilterAll);
        chooser.getExtensionFilters().add(extFilterXls);
        chooser.setSelectedExtensionFilter(extFilterXls);
//        chooser.setInitialFileName();

        File fileChosen = chooser.showOpenDialog(this.dataSourceVbox.getScene().getWindow());
        if (fileChosen != null) {
            this.schedulingFilePathTextfield.setText(fileChosen.getPath());
            this.lastJobSchedulingFileChooserPath = fileChosen.toPath();
        }
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
     * Public accessor to Job Number TextField
     * @return Job Number as String
     */
    public String getJobNumberFromField() {
        return this.jobNumberTextfield.getText();
    }

    /**
     * Public accessor to Scheduling File Path TextField
     * @return Path as String
     */
    public String getSchedulingFilePathFromField() {
        return this.schedulingFilePathTextfield.getText();
    }

    private void toggleBuiltInExtractors() {
        Boolean beamExtractorEnabled = this.settingsHolder.isBuiltInBeamExtractorEnabled();
        Boolean sheetExtractorEnabled = this.settingsHolder.isBuiltInSheetExtractorEnabled();
        Boolean slabExtractorEnabled = this.settingsHolder.isBuiltInSlabExtractorEnabled();
        Boolean trussExtractorEnabled = this.settingsHolder.isBuiltInTrussExtractorEnabled();

        VBox beamVbox = this.dataSourceMap.get(DataSourceExtractorType.BEAM.getName()).getExtractorVbox();
        VBox sheetVbox = this.dataSourceMap.get(DataSourceExtractorType.SHEET.getName()).getExtractorVbox();
        VBox slabVbox = this.dataSourceMap.get(DataSourceExtractorType.SLAB.getName()).getExtractorVbox();
        VBox trussVbox = this.dataSourceMap.get(DataSourceExtractorType.TRUSS.getName()).getExtractorVbox();

        if (beamExtractorEnabled) {// add the extractor to the view if not present
            Optional<Node> existing = this.dataSourceVbox.getChildren().stream().filter(f -> f.equals(beamVbox)).findFirst();
            if (!existing.isPresent()) this.dataSourceVbox.getChildren().add(beamVbox);

            this.enabledDataSources.computeIfPresent(DataSourceExtractorType.BEAM.getName(), (s, b) -> true);
        } else { // remove the extractor from the view
            this.dataSourceVbox.getChildren().remove(beamVbox);

            this.enabledDataSources.computeIfPresent(DataSourceExtractorType.BEAM.getName(), (s, b) -> false);
        }

        if (sheetExtractorEnabled) {
            Optional<Node> existing = this.dataSourceVbox.getChildren().stream().filter(f -> f.equals(sheetVbox)).findFirst();
            if (!existing.isPresent()) this.dataSourceVbox.getChildren().add(sheetVbox);

            this.enabledDataSources.computeIfPresent(DataSourceExtractorType.SHEET.getName(), (s, b) -> true);
        } else {
            this.dataSourceVbox.getChildren().remove(sheetVbox);

            this.enabledDataSources.computeIfPresent(DataSourceExtractorType.SHEET.getName(), (s, b) -> false);
        }

        if (slabExtractorEnabled) {
            Optional<Node> existing = this.dataSourceVbox.getChildren().stream().filter(f -> f.equals(slabVbox)).findFirst();
            if (!existing.isPresent()) this.dataSourceVbox.getChildren().add(slabVbox);

            this.enabledDataSources.computeIfPresent(DataSourceExtractorType.SLAB.getName(), (s, b) -> true);
        } else {
            this.dataSourceVbox.getChildren().remove(slabVbox);

            this.enabledDataSources.computeIfPresent(DataSourceExtractorType.SLAB.getName(), (s, b) -> false);
        }

        if (trussExtractorEnabled) {
            Optional<Node> existing = this.dataSourceVbox.getChildren().stream().filter(f -> f.equals(trussVbox)).findFirst();
            if (!existing.isPresent()) this.dataSourceVbox.getChildren().add(trussVbox);

            this.enabledDataSources.computeIfPresent(DataSourceExtractorType.TRUSS.getName(), (s, b) -> true);
        } else {
            this.dataSourceVbox.getChildren().remove(trussVbox);

            this.enabledDataSources.computeIfPresent(DataSourceExtractorType.TRUSS.getName(), (s, b) -> false);
        }
    }

    private void installBuiltInExtractorComponents() {
        this.installBuiltinExtractorComponent(DataSourceExtractorType.BEAM);
        this.installBuiltinExtractorComponent(DataSourceExtractorType.SLAB);
        this.installBuiltinExtractorComponent(DataSourceExtractorType.SHEET);
        this.installBuiltinExtractorComponent(DataSourceExtractorType.TRUSS);
    }

    private void installBuiltinExtractorComponent(DataSourceExtractorType type) {
        DataExtractorDescriptor extractorDescriptor = new DataExtractorDescriptor();

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
        pathField.setPromptText("eg. C:\\..\\jobs\\19000\\" + FileType.fileTypesMap.get(type.getFileType()));
        pathField.getStyleClass().add("field");

        Button browseButton = new Button();
        browseButton.setText("Browse...");
        browseButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            if (lastJobDetailingFolderChooserPath == null) {
                Path detailingRoot = settingsHolder.getJobFoldersDetailingRootPath();
                if (detailingRoot != null && !detailingRoot.toString().isEmpty()) {
                    fileChooser.setInitialDirectory(PathUtilities.getNearestDirectory(detailingRoot));
                }
            } else {
                fileChooser.setInitialDirectory(PathUtilities.getNearestDirectory(lastJobDetailingFolderChooserPath));
            }

//          fileChooser.setInitialFileName(); // TODO: Generate expected filenames and set
            FileChooser.ExtensionFilter extFilterXls = new FileChooser.ExtensionFilter(StringUtils.capitalize(type.getFileType()) + " Files", FileType.fileTypesMap.get(type.getFileType()));
            FileChooser.ExtensionFilter extFilterAll = new FileChooser.ExtensionFilter("All Files", FileType.fileTypesMap.get("ALL"));
            fileChooser.getExtensionFilters().add(extFilterXls);
            fileChooser.getExtensionFilters().add(extFilterAll);
            fileChooser.setSelectedExtensionFilter(extFilterXls);
            fileChooser.setTitle(type.getName() + " | Choose Data File for Extractor...");
            File fileChosen = fileChooser.showOpenDialog(dataSourceVbox.getScene().getWindow());
            if (fileChosen != null) {
                String filePath = fileChosen.getPath();
                pathField.setText(filePath);
                lastJobDetailingFolderChooserPath = Paths.get(filePath);
            }
        });

        hbox.getChildren().addAll(fieldLabel, pathField, browseButton);

        Separator separator = new Separator();
        separator.setOrientation(Orientation.HORIZONTAL);

        vbox.getChildren().addAll(headerLabel, hbox, separator);

        extractorDescriptor.setName(type.getName());
        extractorDescriptor.setType(type);
        extractorDescriptor.setExtractorVbox(vbox);
        extractorDescriptor.setFilePathTextProperty(pathField.textProperty());
        extractorDescriptor.setCustomExtractorDetails(null);

        this.dataSourceMap.put(extractorDescriptor.getName(), extractorDescriptor);
        this.enabledDataSources.put(extractorDescriptor.getName(), true);
    }

    /**
     * Private method to add a change listener to the SettingsHolder's lastUpdated property.
     * This allows this view to react to changes in settings.
     */
    private void setupBindingToSettings() {
        ObjectProperty<Date> lastUpdatedObservable = settingsHolder.getLastUpdatedProperty();
        lastUpdatedObservable.addListener((observable, oldDate, newDate) -> {
            if (newDate.after(oldDate)) {
                LOG.debug("Settings updated! Checking for changes");
                this.toggleBuiltInExtractors();
                // see if anything in this layout needs to change based on settings (eg. built in extractors)
            }
        });
    }

    /**
     * Private method to setup listener on the Job Number TextField for automatic lookup.
     */
    private void setupJobNumberListener() {
        jobNumberTextfield.textProperty().addListener((observable, oldValue, newValue) -> {
            if (FloortechHelper.isValidJobNumber(newValue)) {
                LOG.debug("Lookup job number: {}", newValue);
                // TODO: Implement as a notification that files and folders have been located (async lookup)
            } else {
                LOG.debug("Job number entered is not valid: {}", newValue);
            }
        });
    }
}