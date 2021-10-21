package tech.jmcs.floortech.scheduling.ui.datatarget;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.settings.SettingsHolder;
import tech.jmcs.floortech.scheduling.app.types.FileType;
import tech.jmcs.floortech.scheduling.app.util.PathUtilities;
import tech.jmcs.floortech.scheduling.ui.AutoFillManagerFX;
import tech.jmcs.floortech.scheduling.ui.ExtractedDataHolderFX;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class DataTargetPresenter implements Initializable {
    protected static final Logger LOG = LoggerFactory.getLogger(DataTargetPresenter.class);

    @Inject private SettingsHolder settingsHolder;
    @Inject AutoFillManagerFX autoFillManagerFX;
    @Inject ExtractedDataHolderFX extractedDataHolderFX;

    @FXML private TextField schedulingFilePathTextField;

    private Path lastJobSchedulingFileChooserPath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.info("DataTargetPresenter initializing...");
        LOG.trace("Setting data target path string property '{}'", this.schedulingFilePathTextField.textProperty());
//        this.autoFillManagerFX.setDataTargetPath(this.schedulingFilePathTextField.textProperty());
        extractedDataHolderFX.setDataTarget(this.schedulingFilePathTextField.textProperty());
        this.schedulingFilePathTextField.textProperty().bindBidirectional(this.autoFillManagerFX.getDataTargetPathProperty());
    }

    @FXML
    public void handleBrowseSchedulingFileButtonAction(ActionEvent event) {
        FileChooser chooser = new FileChooser();

        FileChooser.ExtensionFilter extFilterXls = new FileChooser.ExtensionFilter("Excel File", FileType.fileTypesMap.get("EXCEL"));
        FileChooser.ExtensionFilter extFilterAll = new FileChooser.ExtensionFilter("All Files", FileType.fileTypesMap.get("ALL"));

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

        File fileChosen = chooser.showOpenDialog(this.schedulingFilePathTextField.getScene().getWindow());
        if (fileChosen != null) {
            this.schedulingFilePathTextField.setText(fileChosen.getPath());
            this.lastJobSchedulingFileChooserPath = fileChosen.toPath();
        }
    }
}
