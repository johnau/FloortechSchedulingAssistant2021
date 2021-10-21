package tech.jmcs.floortech.scheduling.ui.extractbutton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.exception.DataExtractorException;
import tech.jmcs.floortech.scheduling.ui.AutoFillManagerFX;
import tech.jmcs.floortech.scheduling.ui.ExtractedDataHolderFX;
import tech.jmcs.floortech.scheduling.ui.ExtractorManagerFX;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class ExtractButtonPresenter implements Initializable {
    protected static final Logger LOG = LoggerFactory.getLogger(ExtractButtonPresenter.class);

    @Inject private ExtractorManagerFX extractorManagerFX;
    @Inject AutoFillManagerFX autoFillManagerFX;
    @Inject ExtractedDataHolderFX extractedDataHolderFX;

    @FXML private Button extractButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.info("ExtractButtonPresenter initializing...");
        LOG.trace("Setting extract button '{}'", this.extractButton);
        this.autoFillManagerFX.setExtractButton(this.extractButton);
        this.extractButton.defaultButtonProperty().bind(this.extractButton.focusedProperty());
    }

    @FXML
    public void handleExtractDataButtonAction(ActionEvent event) {
//        this.autoFillManagerFX.clearExtractedData();
        this.extractedDataHolderFX.clear();
        System.out.println("Begin data extraction...");
        try {
            this.extractorManagerFX.processActiveExtractors();
        } catch (DataExtractorException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Extraction issues");
            alert.setHeaderText(String.format("There were some issues during data extraction (%s)", e.getDataSourceName()));
            alert.setContentText(e.getMessage());
            alert.initOwner(this.extractButton.getScene().getWindow());
            alert.showAndWait();
        }
        this.autoFillManagerFX.setFocusToCommit();
    }

}
