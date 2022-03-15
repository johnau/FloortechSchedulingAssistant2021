package tech.jmcs.floortech.scheduling.ui.customextractorwizard.tablelayout;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.CustomExtractorData;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.TriggeredFXMLView;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class TableLayoutPresenter implements Initializable, TriggeredFXMLView {
    protected static final Logger LOG = LoggerFactory.getLogger(TableLayoutPresenter.class);

    @Inject private CustomExtractorData customExtractorData;

    @FXML private TextField tableTitle;
    @FXML private TextField tableTitleRow;
    @FXML private TextField columnHeadersRow;
    @FXML private TextField firstDataRow;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            return text.matches("\\d?") ? change : null;
        };
        this.tableTitleRow.setTextFormatter(new TextFormatter<String>(filter));
        this.columnHeadersRow.setTextFormatter(new TextFormatter<String>(filter));
        this.firstDataRow.setTextFormatter(new TextFormatter<String>(filter));
    }

    @Override
    public void trigger() {
        String _tableTitle = this.tableTitle.getText();
        String _tableTitleRow = this.tableTitleRow.getText();
        String _columnHeadersRow = this.columnHeadersRow.getText();
        String _firstDataRow = this.firstDataRow.getText();

        boolean defaultsUsed = false;
        if (_tableTitle.trim().isEmpty()) {
            // table title can be empty for now
        }
        if (_tableTitleRow.trim().isEmpty()
        || _columnHeadersRow.trim().isEmpty()
        || _firstDataRow.trim().isEmpty()) {
            // Set all the values if one is missing, enforce default table structure for now
            _tableTitleRow = "1";
            _columnHeadersRow = "2";
            _firstDataRow = "3";
            defaultsUsed = true;
        }

        if (defaultsUsed) {
            showDefaultsUsedAlert();
        }

        LOG.debug("Data from this form has been saved:");
        // convert and set values
        this.customExtractorData.getCurrent().setTableTitle(_tableTitle.trim()); // NOTE: Must compare to trimmed titles on the excel doc
        LOG.debug("Table title: {}", _tableTitle.trim());
        try {
            Integer _ttr = Integer.parseInt(_tableTitleRow);
            this.customExtractorData.getCurrent().setTableTitleRow(_ttr);
            LOG.debug("Table Title Row: {}", _ttr);
        } catch (NumberFormatException nex) {}
        try {
            Integer _chr = Integer.parseInt(_columnHeadersRow);
            this.customExtractorData.getCurrent().setColumnHeadersRow(_chr);
            LOG.debug("Column Headers Row: {}", _chr);
        } catch (NumberFormatException nex) {}
        try {
            Integer _fdr = Integer.parseInt(_firstDataRow);
            this.customExtractorData.getCurrent().setFirstDataRow(_fdr);
            LOG.debug("First Data Row: {}", _fdr);
        } catch (NumberFormatException nex) {}
    }

    private void showDefaultsUsedAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Defaults used");
        alert.setTitle("Defaults used");
        alert.setContentText("Some values could not be left empty and have been set to default values");
        alert.showAndWait();
    }

    @Override
    public void reset() {
        this.tableTitle.setText("");
        this.tableTitleRow.setText("1");
        this.columnHeadersRow.setText("2");
        this.firstDataRow.setText("3");
    }

    @Override
    public void update() {
        // update method not required for this presenter
    }
}
