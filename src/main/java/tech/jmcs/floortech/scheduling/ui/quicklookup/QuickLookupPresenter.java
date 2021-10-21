package tech.jmcs.floortech.scheduling.ui.quicklookup;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.settings.SettingsHolder;
import tech.jmcs.floortech.scheduling.app.util.FloortechHelper;
import tech.jmcs.floortech.scheduling.ui.AutoFillManagerFX;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class QuickLookupPresenter implements Initializable {
    protected static final Logger LOG = LoggerFactory.getLogger(QuickLookupPresenter.class);

    @Inject AutoFillManagerFX autoFillManagerFX;
    @Inject SettingsHolder settingsHolder;

    @FXML private TextField jobNumberTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.info("QuickLookupPresenter initializing...");
        setupJobNumberListener();
        LOG.trace("Setting job number string property {}", this.jobNumberTextField.textProperty());
        this.autoFillManagerFX.setJobNumber(this.jobNumberTextField.textProperty());
        this.autoFillManagerFX.setJobNumberField(this.jobNumberTextField);
    }

    @FXML
    public void handleAutoFillButtonAction(ActionEvent event) {
        autoFill();
    }

    @FXML
    public void handleKeyPressJobNumberField(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            autoFill();
        }
    }

    private void autoFill() {
        String jobNumberStr = this.jobNumberTextField.getText();
        boolean valid = FloortechHelper.isValidJobNumber(jobNumberStr);
        if (!valid) {
            LOG.debug("Could not auto-fill, job number was invalid");
            System.out.println(String.format("'%s' is not a valid job number!", jobNumberStr));
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invalid Job Number");
            alert.setHeaderText("Invalid Job Number!");
            alert.setContentText("Can not Auto-fill data with invalid job number.  A valid job number is 5 or 6 digits long");
            alert.showAndWait();

            return;
        }
        System.out.println(String.format("Looking up files for job: '%s'", jobNumberStr));
        boolean success = this.autoFillManagerFX.doAutoFill();
        if (!success) {
            System.out.println(String.format("Could not find job location for job number '%s'!", jobNumberStr));
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invalid number");
            alert.setHeaderText("The job number provided could not be found in the system");
            alert.setContentText(String.format("The detailing folder path: '%s' was searched for a job folder for job: %s without results", this.settingsHolder.getJobFoldersDetailingRootPath(), jobNumberStr));
            alert.showAndWait();
        } else {

        }
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
