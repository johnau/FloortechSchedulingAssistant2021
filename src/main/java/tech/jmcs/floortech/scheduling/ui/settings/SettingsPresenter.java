package tech.jmcs.floortech.scheduling.ui.settings;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.schedulewriter.ExcelScheduleScanner;
import tech.jmcs.floortech.scheduling.app.settings.SettingsLoader;
import tech.jmcs.floortech.scheduling.app.types.FileType;
import tech.jmcs.floortech.scheduling.app.settings.SettingsHolder;
import tech.jmcs.floortech.scheduling.app.settings.SettingsWriter;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class SettingsPresenter implements Initializable {
    protected static final Logger LOG = LoggerFactory.getLogger(SettingsPresenter.class);

    @Inject private SettingsHolder settingsHolder;
    @Inject private SettingsWriter settingsWriter;
    @Inject private SettingsLoader settingsLoader;

    @FXML private AnchorPane mainAnchorPane;
    @FXML private TextField detailingFoldersRootTextfield;
    @FXML private TextField schedulingFolderRootTextfield;

    @FXML private TextField excelScheduleSheetNameTextField;
    @FXML private ListView<String> scheduleSectionsList;

    @FXML private CheckBox trussExtractorEnabledCheckbox;
    @FXML private CheckBox beamExtractorEnabledCheckbox;
    @FXML private CheckBox slabExtractorEnabledCheckbox;
    @FXML private CheckBox sheetExtractorEnabledCheckbox;

    @FXML private ChoiceBox<String> trussScheduleSectionChoiceBox;
    @FXML private ChoiceBox<String> beamScheduleSectionChoiceBox;
    @FXML private ChoiceBox<String> slabScheduleSectionChoiceBox;
    @FXML private ChoiceBox<String> sheetScheduleSectionChoiceBox;

    //<editor-fold desc="Truss Data Schedule Entries">
    @FXML private TextField cw260EntryField;
    @FXML private TextField stdEndCapCw260EntryField;
    @FXML private TextField conEndCapCw260EntryField;
    @FXML private TextField acPenoCw260EntryField;
    @FXML private TextField adjEndCapCw260EntryField;

    @FXML private TextField cw346EntryField;
    @FXML private TextField stdEndCapCw346EntryField;
    @FXML private TextField conEndCapCw346EntryField;
//    @FXML private TextField acPenoCw346EntryField; // no air-con peno in 346 truss
    @FXML private TextField adjEndCapCw346EntryField;

    @FXML private TextField hj200QuantityEntryField;
    @FXML private TextField hj200EntryField;
    @FXML private TextField stdEndCapHj200EntryField;
    @FXML private TextField conEndCapHj200EntryField;
    @FXML private TextField adjEndCapHj200EntryField;

    @FXML private TextField hj300QuantityEntryField;
    @FXML private TextField hj300EntryField;
    @FXML private TextField stdEndCapHj300EntryField;
    @FXML private TextField conEndCapHj300EntryField;
    @FXML private TextField adjEndCapHj300EntryField;
    //</editor-fold>

    //<editor-fold desc="beam fields">
    @FXML private TextField blackKeywordField;
    @FXML private TextField galvKeywordField;
    @FXML private TextField dimetKeywordField;
    @FXML private TextField epoxyKeywordField;
    //</editor-fold>

    //<editor-fold desc="slab fields">
    @FXML private TextField int83mmSlabEntryField;
    @FXML private TextField balc2cRhsSlabEntryField;
    @FXML private TextField balc3cRhsSlabEntryField;
    @FXML private TextField balc2cInsituSlabEntryField;
    @FXML private TextField balc3cInsituSlabEntryField;
    @FXML private TextField balc4cInsituSlabEntryField;
    @FXML private TextField thickAngleEntryField;
    @FXML private TextField thinAngleEntryField;
    //</editor-fold>

    //<editor-fold desc="sheet fields">
    @FXML private TextField sheetKeywordField;
    //</editor-fold>

    @FXML private Button saveAndCloseButton;
    private List<Consumer<Boolean>> onSaveConsumers;

    private ChangeListener<Scene> exitListener = (observable, oldScene, newScene) -> {
        if (oldScene == null && newScene != null) {
            // scene is set for the first time. Now its the time to listen stage changes.
            newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                if (oldWindow == null && newWindow != null) {
                    // stage is set. now is the right time to do whatever we need to the stage in the controller.
                    ((Stage) newWindow).setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            event.consume();
                        }
                    });
                }
            });
        }
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.mainAnchorPane.sceneProperty().addListener(exitListener);
        LOG.debug("SettingsPresenter initializing...");
        this.onSaveConsumers = new ArrayList<>();
        this.setupScheduleSectionsList();
        this.setupScheduleSectionChoiceBoxes();

        this.updateSettingsFromMemory();



    }

    public void addListenerOnSave(Consumer<Boolean> consumer) {
        this.onSaveConsumers.add(consumer);
    }

    public void update() {
        this.updateSettingsFromMemory();
    }

    @FXML
    public void handleRestoreDefaultSettings(ActionEvent event) {
        String quotePath = this.schedulingFolderRootTextfield.getText();
        String draftPath = this.detailingFoldersRootTextfield.getText();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Restore default settings?");
        alert.setContentText("Current settings will be overwritten");
        if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.NO) {
            return;
        }
        this.settingsLoader.loadDefaultSettingsToMemory();
        this.updateSettingsFromMemory();

        this.schedulingFolderRootTextfield.setText(quotePath);
        this.detailingFoldersRootTextfield.setText(draftPath);

        saveSettingsToMemory();
        try {
            this.settingsWriter.writeSettingsFile();
        } catch (IOException e) {
        }
    }

    @FXML
    public void handleChooseJobFoldersPathAction(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Detailing Folder Root...");
//        directoryChooser.setInitialDirectory();
        File chosen = directoryChooser.showDialog(this.mainAnchorPane.getScene().getWindow());
        if (chosen != null && chosen.exists()) {
            this.detailingFoldersRootTextfield.setText(chosen.toPath().toString());
        }
    }

    @FXML
    public void handleChooseSchedulingFoldersPathAction(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Scheduling  Folder Root...");
//        directoryChooser.setInitialDirectory();
        File chosen = directoryChooser.showDialog(this.mainAnchorPane.getScene().getWindow());
        if (chosen != null && chosen.exists()) {
            this.schedulingFolderRootTextfield.setText(chosen.toPath().toString());
        }
    }

    @FXML
    public void handleAddScheduleSectionButtonAction(ActionEvent event) {
        String result = this.showAddScheduleSectionInput();
        if (result != null) {
            if (!this.scheduleSectionsList.getItems().contains(result)) {
                this.scheduleSectionsList.getItems().add(result);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Already exists");
                alert.setHeaderText("Item already exists!");
                alert.setContentText("The Schedule Section list already contains: " + result);
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void handleRemoveScheduleSectionButtonAction(ActionEvent event) {
        int selIdx = this.scheduleSectionsList.getSelectionModel().getSelectedIndex();
        String selected = this.scheduleSectionsList.getSelectionModel().getSelectedItem();
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm remove");
        confirmAlert.setHeaderText(String.format("Remove: '%s'?", selected));
        confirmAlert.setContentText("Do you want to remove the entry?");
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get().equals(ButtonType.OK)) {
            LOG.debug("Removing item {} : {}", selIdx, selected);
            this.scheduleSectionsList.getItems().remove(selIdx);
        }
    }

    @FXML
    public void handleTestScheduleSectionsButtonAction(ActionEvent event) {
        ObservableList<String> itemsList = this.scheduleSectionsList.getItems();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Excel Scheduling File for the test...");

        FileChooser.ExtensionFilter excelFilter = new FileChooser.ExtensionFilter("Excel files", FileType.fileTypesMap.get("EXCEL"));
        FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All files", FileType.fileTypesMap.get("ALL"));

        fileChooser.getExtensionFilters().add(excelFilter);
        fileChooser.getExtensionFilters().add(allFilter);
        fileChooser.setSelectedExtensionFilter(excelFilter);

        File chosen = fileChooser.showOpenDialog(this.mainAnchorPane.getScene().getWindow());
        if (chosen != null) {
            try {
                ExcelScheduleScanner scanner = new ExcelScheduleScanner(chosen.toPath(), this.settingsHolder);
                List<String> notFound = scanner.checkScheduleContainsAll(itemsList);
                Alert alert;
                if (!notFound.isEmpty()) {
                    alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Entries not found");
                    alert.setHeaderText("Some of the entries were not found");
                    StringBuilder builder = new StringBuilder();
                    builder.append("Could not find the following Schedule sections: \n");
                    notFound.forEach( s -> {
                        builder.append(s);
                        builder.append(", \n");
                    });
                    builder.append("please check the list or the schedule file.");
                    alert.setContentText(builder.toString());
                } else {
                    alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Test Success");
                    alert.setHeaderText("The Schedule Sections Test passed successfully");
                    alert.setContentText("All Schedule Sections were found in the selected Excel Schedule file");
                }
                alert.showAndWait();
            } catch (FileNotFoundException e) {
                LOG.warn("Could not find file just selected");
            }
        }
    }

    @FXML
    public void handleSaveSettingsAndCloseButtonAction(ActionEvent event) {
        List<String> dupes = checkForDuplicateScheduleEntryValues();
        if (!dupes.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.getButtonTypes().clear();
            ButtonType yes = ButtonType.YES;
            ButtonType no = ButtonType.NO;
            alert.getButtonTypes().addAll(yes, no);
            alert.setTitle("Continue?");
            alert.setHeaderText("Do you want to continue?");
            StringBuilder sb = new StringBuilder();
            sb.append("Some schedule entry settings are the same, some values may be overwritten when writing to schedule\n");
            sb.append("The following fields should be checked:\n");
            dupes.forEach(f -> {
                sb.append(f);
                sb.append("\n");
            });
            alert.setContentText(sb.toString());
            if (alert.showAndWait().orElse(no) == no) {
                return;
            }
        }

        saveSettingsToMemory();

        try {
            this.settingsWriter.writeSettingsFile();
        } catch (IOException e) {
            LOG.debug("Could not write settings file: {}", e.getMessage());
        }

        for (Consumer<Boolean> onSaveConsumer : this.onSaveConsumers) {
            onSaveConsumer.accept(true);
        }

        Stage stage = (Stage) this.mainAnchorPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleDiscardSettingsAndCloseButtonAction(ActionEvent event) {
        Stage stage = (Stage) this.mainAnchorPane.getScene().getWindow();
        stage.close();
    }

    private void setupScheduleSectionsList() {
        ObservableList<String> tableData = FXCollections.observableArrayList();
        this.scheduleSectionsList.setItems(tableData);
    }

    private void setupScheduleSectionChoiceBoxes() {
        this.trussScheduleSectionChoiceBox.setItems(this.scheduleSectionsList.getItems());
        this.beamScheduleSectionChoiceBox.setItems(this.scheduleSectionsList.getItems());
        this.slabScheduleSectionChoiceBox.setItems(this.scheduleSectionsList.getItems());
        this.sheetScheduleSectionChoiceBox.setItems(this.scheduleSectionsList.getItems());
    }

    private String showAddScheduleSectionInput() {
        TextInputDialog td = new TextInputDialog();
        td.setHeaderText("Enter a new Schedule Section Name (exact)");
        td.setTitle("Add Schedule Section...");
        Optional<String> result = td.showAndWait();
        if (result.isPresent()) {
            String entry = result.get();
            LOG.debug("Got entry name: {}", entry);
            return entry;
        }
        return null;
    }

    private void saveSettingsToMemory() {
        this.settingsHolder.setMainSettings(
                Paths.get(this.schedulingFolderRootTextfield.getText()),
                Paths.get(this.detailingFoldersRootTextfield.getText()),
                this.beamExtractorEnabledCheckbox.isSelected(),
                this.beamScheduleSectionChoiceBox.getSelectionModel().getSelectedItem(),
                this.sheetExtractorEnabledCheckbox.isSelected(),
                this.sheetScheduleSectionChoiceBox.getSelectionModel().getSelectedItem(),
                this.slabExtractorEnabledCheckbox.isSelected(),
                this.slabScheduleSectionChoiceBox.getSelectionModel().getSelectedItem(),
                this.trussExtractorEnabledCheckbox.isSelected(),
                this.trussScheduleSectionChoiceBox.getSelectionModel().getSelectedItem(),
                this.excelScheduleSheetNameTextField.getText(),
                this.scheduleSectionsList.getItems() // ok to pass the observable list?...
        );

        this.settingsHolder.setScheduleEntryCw260Truss(this.cw260EntryField.getText());
        this.settingsHolder.setScheduleEntryStandardEndcapsCw260(this.stdEndCapCw260EntryField.getText());
        this.settingsHolder.setScheduleEntryConnectionEndcapsCw260(this.conEndCapCw260EntryField.getText());
        this.settingsHolder.setScheduleEntryTrussAirConPenoCw260(this.acPenoCw260EntryField.getText());
        this.settingsHolder.setScheduleEntryAdjustableEndcapsCw260(this.adjEndCapCw260EntryField.getText());

        this.settingsHolder.setScheduleEntryCw346Truss(this.cw346EntryField.getText());
        this.settingsHolder.setScheduleEntryStandardEndcapsCw346(this.stdEndCapCw346EntryField.getText());
        this.settingsHolder.setScheduleEntryConnectionEndcapsCw346(this.conEndCapCw346EntryField.getText());
        this.settingsHolder.setScheduleEntryAdjustableEndcapsCw346(this.adjEndCapCw346EntryField.getText());

        this.settingsHolder.setScheduleEntryHj200TrussCount(this.hj200QuantityEntryField.getText());
        this.settingsHolder.setScheduleEntryHj200Truss(this.hj200EntryField.getText());
        this.settingsHolder.setScheduleEntryStandardEndcapsHj200(this.stdEndCapHj200EntryField.getText());
        this.settingsHolder.setScheduleEntryConnectionEndcapsHj200(this.conEndCapHj200EntryField.getText());
        // don't set hj200 or hj300 adjustable field just yet. using cw260 and cw346 only

        this.settingsHolder.setScheduleEntryHj300TrussCount(this.hj300QuantityEntryField.getText());
        this.settingsHolder.setScheduleEntryHj300Truss(this.hj300EntryField.getText());
        this.settingsHolder.setScheduleEntryStandardEndcapsHj300(this.stdEndCapHj300EntryField.getText());
        this.settingsHolder.setScheduleEntryConnectionEndcapsHj300(this.conEndCapHj300EntryField.getText());

        this.settingsHolder.setScheduleEntrySteelBlackKeyword(this.blackKeywordField.getText());
        this.settingsHolder.setScheduleEntrySteelGalvanisedKeyword(this.galvKeywordField.getText());
        this.settingsHolder.setScheduleEntrySteelDimetKeyword(this.dimetKeywordField.getText());
        this.settingsHolder.setScheduleEntrySteelEpoxyKeyword(this.epoxyKeywordField.getText());
        this.settingsHolder.setScheduleEntrySlabInternal(this.int83mmSlabEntryField.getText());
        this.settingsHolder.setScheduleEntrySlab2cRhs(this.balc2cRhsSlabEntryField.getText());
        this.settingsHolder.setScheduleEntrySlab3cRhs(this.balc3cRhsSlabEntryField.getText());
        this.settingsHolder.setScheduleEntrySlab2cInsitu(this.balc2cInsituSlabEntryField.getText());
        this.settingsHolder.setScheduleEntrySlab3cInsitu(this.balc3cInsituSlabEntryField.getText());
        this.settingsHolder.setScheduleEntrySlab4cInsitu(this.balc4cInsituSlabEntryField.getText());
        this.settingsHolder.setScheduleEntrySlabThickAngle(this.thickAngleEntryField.getText());
        this.settingsHolder.setScheduleEntrySlabThinAngle(this.thinAngleEntryField.getText());
        this.settingsHolder.setScheduleEntrySheetSuffix(this.sheetKeywordField.getText());

    }

    private void updateSettingsFromMemory() {
        Path detailingFoldersRootPath = this.settingsHolder.getJobFoldersDetailingRootPath();
        Path schedulingFilesRootPath = this.settingsHolder.getJobFilesSchedulingRootPath();

        Boolean trussExtractorEnabled = this.settingsHolder.isBuiltInTrussExtractorEnabled();
        String trussScheduleSectionName = this.settingsHolder.getTrussScheduleSectionName();
        Boolean slabExtractorEnabled = this.settingsHolder.isBuiltInSlabExtractorEnabled();
        String slabScheduleSectionName = this.settingsHolder.getSlabScheduleSectionName();
        Boolean sheetExtractorEnabled = this.settingsHolder.isBuiltInSheetExtractorEnabled();
        String sheetScheduleSectionName = this.settingsHolder.getSheetScheduleSectionName();
        Boolean beamExtractorEnabled = this.settingsHolder.isBuiltInBeamExtractorEnabled();
        String beamScheduleSectionName = this.settingsHolder.getBeamScheduleSectionName();

        String excelScheduleSheetName = this.settingsHolder.getExcelScheduleSheetName();
        List<String> scheduleSections = this.settingsHolder.getExcelScheduleFileSections();

        String cw260Truss = this.settingsHolder.getScheduleEntryCw260Truss();
        String cw260StdEndCap = this.settingsHolder.getScheduleEntryStandardEndcapsCw260();
        String cw260ConEndCap = this.settingsHolder.getScheduleEntryConnectionEndcapsCw260();
        String cw260AcPeno = this.settingsHolder.getScheduleEntryTrussAirConPenoCw260();
        String cw346Truss = this.settingsHolder.getScheduleEntryCw346Truss();
        String cw346StdEndCap = this.settingsHolder.getScheduleEntryStandardEndcapsCw346();
        String cw346ConEndCap = this.settingsHolder.getScheduleEntryConnectionEndcapsCw346();

        String hj200TrussCount = this.settingsHolder.getScheduleEntryHj200TrussCount();
        String hj200Truss = this.settingsHolder.getScheduleEntryHj200Truss();
        String hj200StdEndCap = this.settingsHolder.getScheduleEntryStandardEndcapsHj200();
        String hj200ConEndCap = this.settingsHolder.getScheduleEntryConnectionEndcapsHj200();

        String hj300TrussCount = this.settingsHolder.getScheduleEntryHj300TrussCount();
        String hj300Truss = this.settingsHolder.getScheduleEntryHj300Truss();
        String hj300StdEndCap = this.settingsHolder.getScheduleEntryStandardEndcapsHj300();
        String hj300ConEndCap = this.settingsHolder.getScheduleEntryConnectionEndcapsHj300();

        String cw260AdjEndCap = this.settingsHolder.getScheduleEntryAdjustableEndcapsCw260();
        String cw346AdjEndCap = this.settingsHolder.getScheduleEntryAdjustableEndcapsCw346();

        String blackKwd = this.settingsHolder.getScheduleEntrySteelBlackKeyword();
        String galvKwd = this.settingsHolder.getScheduleEntrySteelGalvanisedKeyword();
        String dimetKwd = this.settingsHolder.getScheduleEntrySteelDimetKeyword();
        String epoxyKwd = this.settingsHolder.getScheduleEntrySteelEpoxyKeyword();
        String int83slab = this.settingsHolder.getScheduleEntrySlabInternal();
        String balc2cRhsSlab = this.settingsHolder.getScheduleEntrySlab2cRhs();
        String balc3cRhsSlab = this.settingsHolder.getScheduleEntrySlab3cRhs();
        String balc2cInsituSlab = this.settingsHolder.getScheduleEntrySlab2cInsitu();
        String balc3cInsituSlab = this.settingsHolder.getScheduleEntrySlab3cInsitu();
        String balc4cInsituSlab = this.settingsHolder.getScheduleEntrySlab4cInsitu();
        String thickAngle = this.settingsHolder.getScheduleEntrySlabThickAngle();
        String thinAngle = this.settingsHolder.getScheduleEntrySlabThinAngle();
        String sheetSuffix = this.settingsHolder.getScheduleEntrySheetSuffix();

        this.cw260EntryField.setText(cw260Truss == null ? "" : cw260Truss);
        this.stdEndCapCw260EntryField.setText(cw260StdEndCap == null ? "" : cw260StdEndCap);
        this.conEndCapCw260EntryField.setText(cw260ConEndCap == null ? "" : cw260ConEndCap);
        this.acPenoCw260EntryField.setText(cw260AcPeno == null ? "" : cw260AcPeno);
        this.cw346EntryField.setText(cw346Truss == null ? "" : cw346Truss);
        this.stdEndCapCw346EntryField.setText(cw346StdEndCap == null ? "" : cw346StdEndCap);
        this.conEndCapCw346EntryField.setText(cw346ConEndCap == null ? "" : cw346ConEndCap);

        this.hj200QuantityEntryField.setText(hj200TrussCount == null ? "" : hj200TrussCount);
        this.hj200EntryField.setText(hj200Truss == null ? "" : hj200Truss);
        this.stdEndCapHj200EntryField.setText(hj200StdEndCap == null ? "" : hj200StdEndCap);
        this.conEndCapHj200EntryField.setText(hj200ConEndCap == null ? "" : hj200ConEndCap);

        this.hj300QuantityEntryField.setText(hj300TrussCount == null ? "" : hj300TrussCount);
        this.hj300EntryField.setText(hj300Truss == null ? "" : hj300Truss);
        this.stdEndCapHj300EntryField.setText(hj300StdEndCap == null ? "" : hj300StdEndCap);
        this.conEndCapHj300EntryField.setText(hj300ConEndCap == null ? "" : hj300ConEndCap);

        this.adjEndCapCw260EntryField.setText(cw260AdjEndCap == null ? "" : cw260AdjEndCap);
        this.adjEndCapHj200EntryField.setText(cw260AdjEndCap == null ? "" : cw260AdjEndCap);
        this.adjEndCapCw346EntryField.setText(cw346AdjEndCap == null ? "" : cw346AdjEndCap);
        this.adjEndCapHj300EntryField.setText(cw346AdjEndCap == null ? "" : cw346AdjEndCap);

        this.blackKeywordField.setText(blackKwd == null ? "" : blackKwd);
        this.galvKeywordField.setText(galvKwd == null ? "" : galvKwd);
        this.dimetKeywordField.setText(dimetKwd == null ? "" : dimetKwd);
        this.epoxyKeywordField.setText(epoxyKwd == null ? "" : epoxyKwd);
        this.int83mmSlabEntryField.setText(int83slab == null ? "" : int83slab);
        this.balc2cRhsSlabEntryField.setText(balc2cRhsSlab == null ? "" : balc2cRhsSlab);
        this.balc3cRhsSlabEntryField.setText(balc3cRhsSlab == null ? "" : balc3cRhsSlab);
        this.balc2cInsituSlabEntryField.setText(balc2cInsituSlab == null ? "" : balc2cInsituSlab);
        this.balc3cInsituSlabEntryField.setText(balc3cInsituSlab == null ? "" : balc3cInsituSlab);
        this.balc4cInsituSlabEntryField.setText(balc4cInsituSlab == null ? "" : balc4cInsituSlab);
        this.thickAngleEntryField.setText(thickAngle == null ? "" : thickAngle);
        this.thinAngleEntryField.setText(thinAngle == null ? "" : thinAngle);
        this.sheetKeywordField.setText(sheetSuffix == null ? "" : sheetSuffix);

        this.excelScheduleSheetNameTextField.setText(excelScheduleSheetName);
        this.scheduleSectionsList.getItems().clear();
        this.scheduleSectionsList.getItems().addAll(scheduleSections); // MUST BE SET BEFORE EXTRACTOR SCHEDULE CHOICE BOXES

        this.detailingFoldersRootTextfield.setText(detailingFoldersRootPath == null ? "" : detailingFoldersRootPath.toString());
        this.schedulingFolderRootTextfield.setText(schedulingFilesRootPath == null ? "" : schedulingFilesRootPath.toString());

        this.trussExtractorEnabledCheckbox.setSelected(trussExtractorEnabled == null || trussExtractorEnabled);
        this.trussScheduleSectionChoiceBox.setValue(trussScheduleSectionName == null ? "" : trussScheduleSectionName);
        this.slabExtractorEnabledCheckbox.setSelected(slabExtractorEnabled == null || slabExtractorEnabled);
        this.slabScheduleSectionChoiceBox.setValue(slabScheduleSectionName == null ? "" : slabScheduleSectionName);
        this.sheetExtractorEnabledCheckbox.setSelected(sheetExtractorEnabled == null || sheetExtractorEnabled);
        this.sheetScheduleSectionChoiceBox.setValue(sheetScheduleSectionName == null ? "" : sheetScheduleSectionName);
        StringBuilder b = new StringBuilder();
        this.beamScheduleSectionChoiceBox.getItems().forEach(c -> {
            b.append(c);
            b.append(", ");
        });
        LOG.trace("Beam extractor values {} + setting {}", b.toString(), beamScheduleSectionName);
        this.beamExtractorEnabledCheckbox.setSelected(beamExtractorEnabled == null || beamExtractorEnabled);
        this.beamScheduleSectionChoiceBox.setValue(beamScheduleSectionName == null ? "" : beamScheduleSectionName);


    }

    /**
     *
     * @return return a list of setting names that have duplicates
     */
    private List<String> checkForDuplicateScheduleEntryValues() {
        List<String> dupes = new ArrayList<>();

        LOG.debug("Chcek for duplicate setting schedule entries not yet implemented");

        return dupes;
    }
}
