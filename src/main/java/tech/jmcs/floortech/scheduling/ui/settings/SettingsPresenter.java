package tech.jmcs.floortech.scheduling.ui.settings;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.settings.SettingsHolder;
import tech.jmcs.floortech.scheduling.app.settings.SettingsWriter;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class SettingsPresenter implements Initializable {
    protected static final Logger LOG = LoggerFactory.getLogger(SettingsPresenter.class);

    @FXML
    private AnchorPane mainAnchorPane;

    @FXML
    private TextField detailingFoldersRootTextfield;

    @FXML
    private TextField schedulingFolderRootTextfield;

    @FXML
    private CheckBox trussExtractorEnabledCheckbox;

    @FXML
    private CheckBox beamExtractorEnabledCheckbox;

    @FXML
    private CheckBox slabExtractorEnabledCheckbox;

    @FXML
    private CheckBox sheetExtractorEnabledCheckbox;

    @FXML
    private VBox dataSourcesVbox;

    @FXML
    private Button saveSettingsAndCloseButton;

    @FXML
    private Button discardSettingsAndCloseButton;

    @Inject
    private SettingsHolder settingsHolder;

    @Inject
    private SettingsWriter settingsWriter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.debug("SettingsPresenter initialized");
        this.updateSettingsFromMemory();
    }

    public void update() {
        this.updateSettingsFromMemory();
    }

    private void updateSettingsFromMemory() {
        Path detailingFoldersRootPath = this.settingsHolder.getJobFoldersDetailingRootPath();
        Path schedulingFilesRootPath = this.settingsHolder.getJobFilesSchedulingRootPath();
        Boolean trussExtractorEnabled = this.settingsHolder.isBuiltInTrussExtractorEnabled();
        Boolean slabExtractorEnabled = this.settingsHolder.isBuiltInSlabExtractorEnabled();
        Boolean sheetExtractorEnabled = this.settingsHolder.isBuiltInSheetExtractorEnabled();
        Boolean beamExtractorEnabled = this.settingsHolder.isBuiltInBeamExtractorEnabled();

        this.detailingFoldersRootTextfield.setText(detailingFoldersRootPath == null ? "" : detailingFoldersRootPath.toString());
        this.schedulingFolderRootTextfield.setText(schedulingFilesRootPath == null ? "" : schedulingFilesRootPath.toString());
        this.trussExtractorEnabledCheckbox.setSelected(trussExtractorEnabled == null || trussExtractorEnabled);
        this.slabExtractorEnabledCheckbox.setSelected(slabExtractorEnabled == null || slabExtractorEnabled);
        this.sheetExtractorEnabledCheckbox.setSelected(sheetExtractorEnabled == null || sheetExtractorEnabled);
        this.beamExtractorEnabledCheckbox.setSelected(beamExtractorEnabled == null || beamExtractorEnabled);
    }

    @FXML
    public void handleSaveSettingsAndCloseButtonAction(ActionEvent event) {
        this.settingsHolder.setAllSettings(
                Paths.get(this.schedulingFolderRootTextfield.getText()),
                Paths.get(this.detailingFoldersRootTextfield.getText()),
                this.beamExtractorEnabledCheckbox.isSelected(),
                this.sheetExtractorEnabledCheckbox.isSelected(),
                this.slabExtractorEnabledCheckbox.isSelected(),
                this.trussExtractorEnabledCheckbox.isSelected()
        );

        try {
            this.settingsWriter.writeSettingsFile();
        } catch (IOException e) {
            LOG.debug("Could not write settings file: {}", e.getMessage());
        }

        Stage stage = (Stage) this.mainAnchorPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleDiscardSettingsAndCloseButtonAction(ActionEvent event) {
        Stage stage = (Stage) this.mainAnchorPane.getScene().getWindow();
        stage.close();
    }
}
