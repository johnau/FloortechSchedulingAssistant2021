package tech.jmcs.floortech.scheduling.ui.customextractorwizard.namepage;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.CustomExtractorData;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.TriggeredFXMLView;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class NamePagePresenter implements Initializable, TriggeredFXMLView {
    protected static final Logger LOG = LoggerFactory.getLogger(NamePagePresenter.class);

    @Inject
    private CustomExtractorData customExtractorData;

    @FXML private TextField nameField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.debug("Instance id for CustomExtractorData: " + this.customExtractorData.hashCode());
    }

    @Override
    public void trigger() {
        String extractorName = this.nameField.getText();
        if (extractorName.trim().isEmpty()) {
            extractorName = "New Custom Extractor";

            int i = 1;
            while (!nameIsOk(extractorName)) {
                extractorName = "New Custom Extractor" + i++;
            }

            this.nameField.setText(extractorName); // set the field so the user can see what name was chosen
            showDefaultNameUsedAlert(); // show the alert so the user knows
        } else {
            String _e = extractorName;
            int i = 1;
            while (!nameIsOk(extractorName)) {
                extractorName = _e + i++;
            }
        }

        this.customExtractorData.getCurrent().setExtratorName(extractorName);

        LOG.debug("Data from this form has been saved: \n " +
                "Extractor name: {}", extractorName);
    }

    @Override
    public void reset() {
        this.nameField.setText("");
    }

    @Override
    public void update() {
        // update method not required for this presenter
    }

    private boolean nameIsOk(String name) {
        // check the database of custom extractors for a name match
        LOG.warn("Not yet implemented name check for custom extractor");

        return true;
    }

    private void showDefaultNameUsedAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Using defaults");
        alert.setHeaderText("No name provided...");
        alert.setContentText("Using a default name for this custom extractor");
        alert.showAndWait();
    }
}
