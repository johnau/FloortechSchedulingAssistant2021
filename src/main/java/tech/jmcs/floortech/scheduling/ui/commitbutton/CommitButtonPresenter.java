package tech.jmcs.floortech.scheduling.ui.commitbutton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.ExtractedDataToScheduleConverter;
import tech.jmcs.floortech.scheduling.app.settings.SettingsHolder;
import tech.jmcs.floortech.scheduling.ui.ExtractedDataHolderFX;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class CommitButtonPresenter implements Initializable {
    protected static final Logger LOG = LoggerFactory.getLogger(CommitButtonPresenter.class);

    @Inject private ExtractedDataHolderFX extractedDataHolder;
    @Inject private ExtractedDataToScheduleConverter dataConverter;
    @Inject private SettingsHolder settingsHolder;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.info("CommitButtonPresenter initializing...");
    }

    @FXML
    public void handleCommitButtonAction(ActionEvent event) {

    }

    private void preCheckScheduleFile() {

    }

    private void showPreCheckFailedMessage() {

    }

    private void updateScheduleFile() {

    }

    private void showPostUpdateMessage() {

    }

    private void showUpdateErrorMessages() {

    }

    private void openExcelFile() {

    }

}
