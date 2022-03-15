package tech.jmcs.floortech.scheduling.ui.customextractorwizard.columndescriptorspage;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.CustomExtractorData;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.TriggeredFXMLView;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.recordvalidation.ColumnDataType;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.ColumnValidationData;

import javax.inject.Inject;
import java.net.URL;
import java.util.*;

public class ColumnDescriptorsPresenter implements Initializable, TriggeredFXMLView {
    protected static final Logger LOG = LoggerFactory.getLogger(ColumnDescriptorsPresenter.class);

    @Inject private CustomExtractorData customExtractorData;

    @FXML private VBox columnContainer;

//    private Integer columnCount = 0;

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String COL_LETTER = "column_letter";
    private static final String COLUMN_TITLE = "column_title";
    private static final String DATA_TYPE = "data_type";
    private static final String EMPTY_ROW = "empty_row";
    private static final String DELETE_BUTTON = "delete_row";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.columnContainer.getChildren().addListener((ListChangeListener<Node>) c -> {
            // when the children are changed, rename all the labels
            int i = 0;
            for (Node node : c.getList()) {
                if (node instanceof HBox) {
                    HBox hb = (HBox) node;
                    Node colLetter = hb.getChildren().stream()
                            .filter(f -> f.getId().equalsIgnoreCase(COL_LETTER))
                            .findFirst()
                            .orElse(null);
                    if (colLetter != null) {
                        Label _cl = (Label) colLetter;
                        _cl.setText(ALPHABET.charAt(i++) + "");
                    }
                }
            }
        });
        reset();

    }

    @FXML
    public void addColumnAction(ActionEvent event) {
        if (this.columnContainer.getChildren().size() <= 25) {
            this.columnContainer.getChildren().add(createColumnEntry());
        } else {
            showMaxColumnsAlert();
        }
    }

    private void showMaxColumnsAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Too many columns");
        alert.setHeaderText("26 column table max supported");
        alert.setContentText("Tables with more than 26 columns are not supported.");
        alert.showAndWait();
    }

    private HBox createColumnEntry() {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER);
        row.setSpacing(10d);

        Label colLetter = new Label("A");
        colLetter.setId(COL_LETTER);
        colLetter.setStyle("-fx-font-weight: bold; -fx-font-size: 24;");

        Separator s1 = new Separator(Orientation.VERTICAL);

        Label colTitle = new Label("Column Title");

        TextField colTitleField = new TextField();
        colTitleField.setId(COLUMN_TITLE);

        Separator s2 = new Separator(Orientation.VERTICAL);

        Label dataType = new Label("Data Type");

        ChoiceBox<String> dataTypeChoice = new ChoiceBox<>();
        dataTypeChoice.setId(DATA_TYPE);
        dataTypeChoice.getItems().addAll(ColumnDataType.NUMBER.toString(),
                ColumnDataType.TEXT.toString(),
                ColumnDataType.TRUE_FALSE.toString());
        dataTypeChoice.setValue(ColumnDataType.NUMBER.toString());

        Separator s3 = new Separator(Orientation.VERTICAL);

        CheckBox emptyCheck = new CheckBox("Empty");
        emptyCheck.setId(EMPTY_ROW);
        emptyCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                row.getChildren().forEach(n -> {
                    if (n.getId() == null ||
                            (!n.getId().equals(EMPTY_ROW) && !n.getId().equals(DELETE_BUTTON))) {
                        n.setDisable(true);
                    }
                });
            } else {
                row.getChildren().forEach(n -> {
                    n.setDisable(false);
                });
            }
        });

        Button delete = new Button("x");
        delete.setId(DELETE_BUTTON);
        delete.setStyle("-fx-font-weight: bold;");
        delete.setOnAction(event -> {
            Parent parent = row.getParent();
            VBox v = (VBox) parent;
            v.getChildren().remove(row);
        });

        row.getChildren().addAll(colLetter, s1, colTitle, colTitleField, s2,
                dataType, dataTypeChoice, s3, emptyCheck, delete);

        return row;
    }

    @Override
    public void trigger() throws Exception {
        if (this.columnContainer.getChildren().isEmpty()) {
            throw new Exception("You must provide column descriptors for the columns in the data table");
        }

        // check to see if all the column names are blanks
        long emptyCount = this.columnContainer.getChildren().stream()
                .filter(n -> {
                    if (n instanceof HBox) {
                        // check to see if all the column names are blanks
                        Optional<Node> empty = ((HBox) n).getChildren().stream()
                                .filter(c -> c.getId() != null
                                        && c.getId().equals(COLUMN_TITLE)
                                        && c instanceof TextField
                                        && !c.isDisabled()) // check disabled for the empty row check
                                .filter(c -> ((TextField) c).getText().isEmpty())
                                .findAny();
                        if (empty.isPresent()) {
                            return true;
                        }
                    }
                    return false;
                })
                .count();
        if (emptyCount == this.columnContainer.getChildren().size()) {
            throw new Exception("Some of the columns must have column titles");
        }

        // check there are no duplicate column names, if so rename them and alert user later
        boolean duplicateColumns = false;
        Set<String> usedColumnNames = new HashSet<>();

        // iterate the rows and extract the data into the model
        List<ColumnValidationData> columns = new ArrayList<>();

        int pos = 0;
        for (Node child : this.columnContainer.getChildren()) {
            ColumnValidationData data = new ColumnValidationData();
            data.setColumnPosition(pos++);
            HBox b = (HBox) child;

            Node _colTitle = b.getChildren().stream()
                    .filter(f -> f.getId() != null && f.getId().equalsIgnoreCase(COLUMN_TITLE))
                    .findFirst()
                    .orElse(null);
            Node _dataType = b.getChildren().stream()
                    .filter(f -> f.getId() != null && f.getId().equalsIgnoreCase(DATA_TYPE))
                    .findFirst()
                    .orElse(null);
            Node _emptyRow = b.getChildren().stream()
                    .filter(f -> f.getId() != null && f.getId().equalsIgnoreCase(EMPTY_ROW))
                    .findFirst()
                    .orElse(null);

            boolean empty = false;
            if (_emptyRow != null) {
                Boolean _empty = ((CheckBox) _emptyRow).isSelected();
                data.setEmpty(_empty);
                empty = _empty;
            }

            if (_colTitle != null) {
                String colTitle = ((TextField) _colTitle).getText();
                if (!usedColumnNames.contains(colTitle) && !empty) {
                    usedColumnNames.add(colTitle);
                } else if (empty) {
                    // ignore empty columns for title check
                } else {
                    throw new Exception("There is more than one column with the same name");
                }
                data.setColumnTitle(colTitle);
            }

            if (_dataType != null) {
                String dataType = ((ChoiceBox<String>) _dataType).getValue();
                data.setDataType(dataType);
            }

            columns.add(data);
        }

        customExtractorData.getCurrent().setColumnDescriptors(columns);

        LOG.debug("Data from this form has been saved:");
        columns.forEach(c -> {
            LOG.debug("#{}: '{}' ({}) | Empty: {}, required {}",
                    c.getColumnPosition(), c.getColumnTitle(), c.getDataType(), c.getEmpty(),
                    c.getRequiredForRecord());
        });
    }

    @Override
    public void reset() {
        this.columnContainer.getChildren().clear();
//        this.columnCount = 0;
        HBox defaultRow = createColumnEntry();
        this.columnContainer.getChildren().add(defaultRow);
    }

    @Override
    public void update() {
        // update method not required for this presenter
    }
}
