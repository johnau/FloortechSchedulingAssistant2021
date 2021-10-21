package tech.jmcs.floortech.scheduling.ui.commitbutton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.ExtractedDataToScheduleConverter;
import tech.jmcs.floortech.scheduling.app.exception.ExcelScheduleWriterException;
import tech.jmcs.floortech.scheduling.app.schedulewriter.ExcelScheduleUpdateConfirmer;
import tech.jmcs.floortech.scheduling.app.schedulewriter.BasicExcelScheduleUpdater;
import tech.jmcs.floortech.scheduling.app.settings.SettingsHolder;
import tech.jmcs.floortech.scheduling.ui.AutoFillManagerFX;
import tech.jmcs.floortech.scheduling.ui.CommitErrors;
import tech.jmcs.floortech.scheduling.ui.ExtractedDataHolderFX;
import tech.jmcs.floortech.scheduling.ui.collisions.CollisionsView;

import javax.inject.Inject;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.ResourceBundle;

public class CommitButtonPresenter implements Initializable {
    protected static final Logger LOG = LoggerFactory.getLogger(CommitButtonPresenter.class);

    @Inject private ExtractedDataHolderFX extractedDataHolderFX;
    @Inject private ExtractedDataToScheduleConverter extractedDataToScheduleConverter;
    @Inject private SettingsHolder settingsHolder;
    @Inject private AutoFillManagerFX autoFillManagerFX;
    @Inject private CommitErrors commitErrors;

    @FXML private Button commitButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.info("CommitButtonPresenter initializing...");
        LOG.trace("Setting commit button '{}'", this.commitButton);
        this.autoFillManagerFX.setCommitButton(this.commitButton);
        this.commitButton.defaultButtonProperty().bind(this.commitButton.focusedProperty());
    }

    @FXML
    public void handleCommitButtonAction(ActionEvent event) {
        this.extractedDataHolderFX.jsonifyData();

        Map<String, Object> scheduleReadyDataMap = this.extractedDataHolderFX.convertToScheduleMap();
        scheduleReadyDataMap.forEach((name, val) -> {
            LOG.trace("Converted: {} : {}", name, val);
        });

        Path scheduleFile = Paths.get(this.extractedDataHolderFX.getDataTarget());

        System.out.println(String.format("Committing data to schedule file: %s", scheduleFile.toString()));

        BasicExcelScheduleUpdater updater = null;

        try {
            updater = new BasicExcelScheduleUpdater(scheduleFile);
            ExcelScheduleUpdateConfirmer confirmer = updater.updateSchedule(scheduleReadyDataMap);

            if (confirmer != null) {
                Map<String, Object> cp = confirmer.getConflictProblems();
                cp.forEach((name, val) -> {
                    LOG.trace("Conflict: {} : {}", name, val);
                });
            }

            commitErrors.setConfirmer(confirmer);
            if (confirmer == null) {
                System.out.println(String.format("Schedule file was updated without any conflicts or errors!"));
            } else {
                System.out.println(String.format("Schedule file was updated with %s errors, %s conflicts, %s missing",
                        confirmer.getUpdateErrors().size(), confirmer.getConflictProblems().size(), confirmer.getNotFoundProblems().size()));
            }

            if (updater.fileAccessible()) {
                showCollisionsView();
                updater.completeUpdate(); // this is when the file is accessed (write)
            } else {
                informUser("File access error", "Could not access the scheduling file", "The file located at " + scheduleFile.toString() + " could not be accessed.");
                return;
            }

        } catch (IOException e) {
            informUser("File access error", "Could not access the scheduling file", "The file located at " + scheduleFile.toString() + " could not be accessed.");
            return;
        } catch (ExcelScheduleWriterException e) {
            informUser("Excel Schedule Error", "Issue with Excel Scheduling File", e.getMessage());
            return;
        }

        System.out.println(String.format("Commit complete, opening quote file: %s", scheduleFile.toString()));

        try {
            Desktop.getDesktop().open(scheduleFile.toFile());
        } catch (IOException e) {
        }

        this.autoFillManagerFX.resetAutoFill();
    }

    private void informUser(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showCollisionsView() {
        CollisionsView collisionsView = new CollisionsView();
        Scene scene = new Scene(collisionsView.getView());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initOwner(this.commitButton.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.showAndWait();
    }

}
