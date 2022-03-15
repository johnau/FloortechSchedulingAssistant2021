package tech.jmcs.floortech.scheduling.ui.customextractorwizard.requiredrecorddata;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.ColumnStringConverter;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.CustomExtractorData;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.TriggeredFXMLView;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.ColumnValidationData;

import javax.inject.Inject;
import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class RequiredRecordDataPresenter implements Initializable, TriggeredFXMLView {
    protected static final Logger LOG = LoggerFactory.getLogger(RequiredRecordDataPresenter.class);

    @Inject private CustomExtractorData customExtractorData;

    @FXML private FlowPane checkboxFlowpane;

    private final ObservableList<ColumnValidationData> selected = FXCollections.observableArrayList();

//    private Map<CheckBox, ChangeListener<Boolean>> listenerMap = new HashMap<>();
    private List<String> requiredColumns = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.debug("Custom extractor data: {}", customExtractorData);
        populateCheckboxes();
    }

    private void populateCheckboxes() {
        this.customExtractorData.getCurrent().getColumnDescriptors().forEach(c -> {
            LOG.debug("Column: {} is required: {}", c.getColumnTitle(), c.getRequiredForRecord());
        });

//        this.listenerMap.forEach( (cb, cl) -> {
//            cb.selectedProperty().removeListener(cl);
//            this.checkboxFlowpane.getChildren().remove(cb);
//        });
//        this.listenerMap.clear();
        this.checkboxFlowpane.getChildren().clear();

        this.customExtractorData.getCurrent().getColumnDescriptors().forEach(c -> {
            CheckBox cb = new CheckBox(c.getColumnTitle());
            if (this.requiredColumns.contains(c.getColumnTitle())) {
                cb.setSelected(true);
            }
            ChangeListener<Boolean> cl = (observable, oldValue, newValue) -> {
                c.setRequiredForRecord(newValue);
                if (newValue) {
                    if (!requiredColumns.contains(c.getColumnTitle())) {
                        requiredColumns.add(c.getColumnTitle());
                    }
                } else {
                    requiredColumns.remove(c.getColumnTitle());
                }
            };
            cb.selectedProperty().addListener(cl);
            this.checkboxFlowpane.getChildren().add(cb);
//            this.listenerMap.put(cb, cl);
        });
    }

    @Override
    public void trigger() {
//        // check if there are no required record data, if not, add the default ID and move on
        ObservableList<Node> checkboxes = this.checkboxFlowpane.getChildren();
        List<String> required = checkboxes.stream()
                .filter(l -> {
                    if (l instanceof CheckBox) {
                        CheckBox cb = (CheckBox) l;
                        return cb.isSelected();
                    }
                    return false;
                })
                .map(l -> {
                    CheckBox cb = (CheckBox) l;
                    return cb.getText();
                })
                .collect(Collectors.toList());

//        this.customExtractorData.getCurrent().setRequiredRecordDataColumns(requiredColumns);
        LOG.debug("Data from this form has been saved:");
        StringBuilder sb = new StringBuilder();
        required.forEach(c -> {
            sb.append(c);
            sb.append(", ");
        });
        this.customExtractorData.getCurrent().getColumnDescriptors().forEach(c -> {
            if (required.contains(c.getColumnTitle())) {
                c.setRequiredForRecord(true);
            } else {
                c.setRequiredForRecord(false);
            }
        });
        LOG.debug("Required columns for valid record: {}", sb.toString());
    }

    @Override
    public void reset() {
        this.checkboxFlowpane.getChildren().clear();
        this.requiredColumns.clear();
    }

    @Override
    public void update() {
        populateCheckboxes();
    }
}
