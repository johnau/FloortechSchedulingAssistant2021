package tech.jmcs.floortech.scheduling.ui.extractbutton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.ui.ExtractorManagerFX;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class ExtractButtonPresenter implements Initializable {
    protected static final Logger LOG = LoggerFactory.getLogger(ExtractButtonPresenter.class);

    @Inject private ExtractorManagerFX extractorManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.info("ExtractButtonPresenter initializing...");
    }

    @FXML
    public void handleExtractDataButtonAction(ActionEvent event) {
        extractorManager.processActiveExtractors();
    }
}
