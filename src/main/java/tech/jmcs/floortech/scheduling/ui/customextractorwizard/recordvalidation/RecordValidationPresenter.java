package tech.jmcs.floortech.scheduling.ui.customextractorwizard.recordvalidation;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.ColumnStringConverter;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.ColumnValidationData;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.CustomExtractorData;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.TriggeredFXMLView;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class RecordValidationPresenter implements Initializable, TriggeredFXMLView {
    protected static final Logger LOG = LoggerFactory.getLogger(RecordValidationPresenter.class);

    private static final String INDEX = "index";
    private static final String COLUMN_CHOICE = "column_choice";
    private static final String VALIDATION_TYPES_CHOICE = "validation_types_choice";
    private static final String VALUE_FIELD  = "value_field";
    private static final String VALUE_FIELD_HOLDER = "value_field_holder";

    @Inject private CustomExtractorData customExtractorData;

    @FXML private VBox rowContainer;

    private Map<Integer, ColumnValidationData> validatorColumnMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.rowContainer.getChildren().clear();
        this.rowContainer.getChildren().add(createNewValidatorRow());
        this.rowContainer.getChildren().addListener((ListChangeListener<Node>) c -> {
            int i = 0;
            for (Node node : c.getList()) {
                if (node instanceof HBox) {
                    Label indexLabel = ((HBox) node).getChildren().stream()
                            .filter(n -> n.getId().equalsIgnoreCase(INDEX)
                            && n instanceof Label)
                            .map(n -> (Label)n)
                            .findFirst().orElse(null);
                    indexLabel.setText(++i + "");
                }
                node.setId(i + "");
            }
        });
    }


    @FXML public void handleAddRecordValidationAction(ActionEvent event) {
        HBox row = createNewValidatorRow();
        this.rowContainer.getChildren().add(row);
        try {
            Integer id = Integer.parseInt(row.getId());
            this.validatorColumnMap.put(id, null);
        } catch (NumberFormatException nex) {}
    }

    @Override
    public void trigger() throws Exception {
        List<HBox> rows = this.rowContainer.getChildren().stream()
                .filter(h -> h instanceof HBox)
                .map(h -> (HBox) h)
                .collect(Collectors.toList());

        List<RecordValidator> validators = new ArrayList<>();

        rows.forEach(r -> {
            Node columnChoice = r.getChildren().stream()
                    .filter(n -> n.getId() != null && n.getId().equals(COLUMN_CHOICE))
                    .findFirst().orElse(null);

            Node validationTypeChoice = r.getChildren().stream()
                    .filter(n -> n.getId() != null && n.getId().equals(VALIDATION_TYPES_CHOICE))
                    .findFirst().orElse(null);

            Node valueFieldHolder = r.getChildren().stream()
                    .filter(n -> n.getId() != null && n.getId().equals(VALUE_FIELD_HOLDER))
                    .findFirst().orElse(null);

            Node valueField = ((HBox) valueFieldHolder).getChildren().stream()
                    .filter(n -> n.getId() != null && n.getId().equals(VALUE_FIELD))
                    .findFirst().orElse(null);

            // this field is contained in an hbox at the moment :|

            LOG.debug("Found in this row: {} {}, {} {}, {} {}", columnChoice, columnChoice.getClass(),
                    validationTypeChoice, validationTypeChoice.getClass(),
                    valueField, valueField.getClass());

            ColumnValidationData validationColumn = null;
            ValidationType validationType = null;
            String validationValue = null;
            if (columnChoice instanceof ChoiceBox) {
                ChoiceBox<ColumnValidationData> cc = (ChoiceBox) columnChoice;
                validationColumn = cc.getValue();
            }
            if (validationTypeChoice instanceof ChoiceBox) {
                ChoiceBox<ValidationType> vtc = (ChoiceBox<ValidationType>) validationTypeChoice;
                validationType = vtc.getValue();
//                try {
//                    validationType = ValidationType.fromStringValue(vtc.getValue());
//                } catch (Exception e) {
//                    LOG.debug("Could not convert validation type: {}", e.getMessage());
//                }
            }
            if (valueField instanceof TextField) {
                TextField vf = (TextField) valueField;
                validationValue = vf.getText();
                LOG.debug("Validation Value: '{}'", validationValue);
            }

            RecordValidator validator = new RecordValidator();

            validator.setTargetColumn(validationColumn.getColumnPosition());
            validator.setValidationType(validationType);

            try {
                DecimalFormat format = new DecimalFormat("\u00A4#,##0.##");
                format.setParseBigDecimal(true);
                BigDecimal result = (BigDecimal) format.parse(validationValue);
//                Double vd = Double.parseDouble(validationValue);


                validator.setValidationValueDouble(result.doubleValue());
            } catch (NumberFormatException | ParseException ex) {
                validator.setValidationValueString(validationValue);
            }

            if (validator.isOk()) {
                validators.add(validator);
            }
        });

        LOG.debug("Data from this form has been saved: \n Record Validators:");
        DecimalFormat df = new DecimalFormat("0.00");
        validators.forEach(v -> {
            String vv = "";

            if (v.getValidationValueDouble() != null) {
                vv = df.format(v.getValidationValueDouble()) + " (D)";
            }
            else if (v.getValidationValueString() != null) {
                vv = v.getValidationValueString();
            }
            LOG.debug("\n\n{} {} {}", v.getTargetColumn(), v.getValidationType().getStringValue(), vv);
        });
    }

    @Override
    public void reset() {
        this.rowContainer.getChildren().clear();
        this.rowContainer.getChildren().add(createNewValidatorRow());
    }

    @Override
    public void update() {
        // update the column choices
        List<ColumnValidationData> columns = this.customExtractorData.getCurrent().getColumnDescriptors();

        this.rowContainer.getChildren().forEach(n -> {
            if (n instanceof HBox) {
                Node columnChoice = ((HBox) n).getChildren().stream()
                        .filter(node -> node.getId() != null && node.getId().equalsIgnoreCase(COLUMN_CHOICE))
                        .findFirst().orElse(null);
                if (columnChoice instanceof ChoiceBox) {
                    ChoiceBox<ColumnValidationData> cc = (ChoiceBox<ColumnValidationData>) columnChoice;
                    cc.getItems().clear();
                    cc.getItems().addAll(columns);
                }
            }
        });
    }

    private HBox createNewValidatorRow() {
        HBox row = new HBox();

        row.setAlignment(Pos.CENTER);
        row.setSpacing(10d);

        Label colLabel = new Label("1");
        colLabel.setId(INDEX);
        colLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 24;");

        Separator s1 = new Separator(Orientation.VERTICAL);

        ChoiceBox<ColumnValidationData> columnChoice = new ChoiceBox<>();
        columnChoice.setConverter(new ColumnStringConverter(this.customExtractorData));
        columnChoice.getItems().clear();
        columnChoice.setId(COLUMN_CHOICE);
        ObservableList<ColumnValidationData> columns = this.customExtractorData.getCurrent().getColumnDescriptorsObservable();

        columnChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
            try {
                Integer id = Integer.parseInt(columnChoice.getParent().getId());
                this.validatorColumnMap.put(id, newValue);
            } catch (NumberFormatException nex) {}
        });

//        LOG.debug("There are {} columns", columns.size());
//        columns.forEach(c -> {
//            LOG.debug("Col: {} {} {} {} ", c.getColumnPosition(), c.getColumnTitle(), c.getDataType(), c.getEmpty());
//        });
        if (columns != null && !columns.isEmpty()) {
            columnChoice.getItems().addAll(columns);
        }

        ChoiceBox<ValidationType> validationTypes = new ChoiceBox<>();
        validationTypes.setId(VALIDATION_TYPES_CHOICE);
        validationTypes.getItems().addAll(ValidationType.values());
        validationTypes.setConverter(new ValidationTypeConverter());

        HBox validationValueHolder = new HBox();
        validationValueHolder.setId(VALUE_FIELD_HOLDER);
        validationValueHolder.setAlignment(Pos.CENTER_LEFT);
        validationValueHolder.setFillHeight(true);
        validationValueHolder.setMinWidth(250);

        validationTypes.valueProperty().addListener((observable, oldValue, newValue) -> {
            LOG.debug("value changed to : {} - {}", newValue, newValue.getStringValue());

            validationValueHolder.getChildren().clear();
            switch (newValue) {
                case EMPTY:
                case NOT_EMPTY:
                    break;

                case STARTS_WITH:
                case ENDS_WITH:
                case CONTAINS:
                    TextField stringTf = new TextField();
                    stringTf.setId(VALUE_FIELD);
                    stringTf.setPromptText("Value...");
                    validationValueHolder.getChildren().add(stringTf);
                    break;
                case GREATER_THAN:
                case LESS_THAN:
                case GREATER_THAN_OR_EQUAL_TO:
                case LESS_THAN_OR_EQUAL_TO:
                case EQUALS:
                case NOT_EQUALS:
                    TextField numberTf = new TextField();
                    numberTf.setId(VALUE_FIELD);
                    UnaryOperator<TextFormatter.Change> filter = change -> {
                        String text = change.getText();
                        return text.matches("[\\d.]?") ? change : null;
                    };
                    numberTf.setTextFormatter(new TextFormatter<String>(filter));
                    numberTf.setPromptText("0.00");
                    validationValueHolder.getChildren().add(numberTf);
                    break;
                default:
            }
        });

        row.getChildren().addAll(colLabel, s1, columnChoice, validationTypes, validationValueHolder);

        return row;
    }
}
