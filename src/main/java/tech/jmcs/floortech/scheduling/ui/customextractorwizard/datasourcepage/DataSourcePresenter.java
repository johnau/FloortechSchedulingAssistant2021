package tech.jmcs.floortech.scheduling.ui.customextractorwizard.datasourcepage;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.CustomExtractorData;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.StringPosition;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.TriggeredFXMLView;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class DataSourcePresenter implements Initializable, TriggeredFXMLView {

    @Inject private CustomExtractorData customExtractorData;

    @FXML private ChoiceBox<StringPosition> stringPosChoice;
    @FXML private TextField fileNamePatternField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.stringPosChoice.getItems().addAll(StringPosition.values());
    }

    @Override
    public void trigger() throws Exception {
        if (this.fileNamePatternField.getText().isEmpty()) {
            throw new Exception("Please enter a value for the filename pattern, for example 'SHEETS' for a Sheet list");
        }

        if (this.stringPosChoice.getValue() == null) {
            throw new Exception("Please choose an option for the position of the filename pattern, for example: " +
                    "\nIf the file is SHEETS LIST 19999.xls, you could set 'Starts With' & 'SHEETS'");
        }

        this.customExtractorData.getCurrent().setDataSourceFileNamePattern(this.fileNamePatternField.getText());
        this.customExtractorData.getCurrent().setDataSourceFileNamePatternPos(this.stringPosChoice.getValue());
    }

    @Override
    public void reset() {

    }

    @Override
    public void update() {

    }
}
