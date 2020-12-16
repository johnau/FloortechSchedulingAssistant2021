package tech.jmcs.floortech.scheduling.ui.quicklookup;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.util.FloortechHelper;

import java.net.URL;
import java.util.ResourceBundle;

public class QuickLookupPresenter implements Initializable {
    protected static final Logger LOG = LoggerFactory.getLogger(QuickLookupPresenter.class);

    @FXML private TextField jobNumberTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.info("QuickLookupPresenter initializing...");
        setupJobNumberListener();
    }

    @FXML
    public void handleAutoFillButtonAction(ActionEvent event) {
        String jobNumberStr = this.jobNumberTextField.getText();
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

    /**
     * Private method to setup listener on the Job Number TextField for automatic lookup.
     */
    private void setupJobNumberListener() {
        jobNumberTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (FloortechHelper.isValidJobNumber(newValue)) {
                LOG.debug("Lookup job number: {}", newValue);
                // TODO: Implement as a notification that files and folders have been located (async lookup)
            } else {
                LOG.debug("Job number entered is not valid: {}", newValue);
            }
        });
    }
}
