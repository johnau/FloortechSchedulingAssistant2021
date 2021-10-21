package tech.jmcs.floortech.scheduling.ui.dataframe;

import javafx.beans.Observable;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.datasource.model.BeamData;
import tech.jmcs.floortech.scheduling.app.datasource.model.SlabData;
import tech.jmcs.floortech.scheduling.app.datasource.model.TrussData;
import tech.jmcs.floortech.scheduling.app.types.BeamTreatment;
import tech.jmcs.floortech.scheduling.ui.AutoFillManagerFX;
import tech.jmcs.floortech.scheduling.ui.ExtractedDataHolderFX;
import tech.jmcs.floortech.scheduling.ui.dataframe.table.*;

import javax.inject.Inject;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class DataFramePresenter implements Initializable {
    protected static final Logger LOG = LoggerFactory.getLogger(DataFramePresenter.class);

    @Inject private ExtractedDataHolderFX extractedDataHolderFX;
    @Inject private AutoFillManagerFX autoFillManagerFX;

    @FXML
    private AnchorPane mainPagePane;

    @FXML
    private TabPane mainTabPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.debug("DataFramePresenter initializing...");
        setupListenerToExtractedData();
        mainTabPane.getTabs().clear();
        AnchorPane.setLeftAnchor(this.mainPagePane, 0d);
        AnchorPane.setRightAnchor(this.mainPagePane, 0d);
        AnchorPane.setTopAnchor(this.mainPagePane, 0d);
        AnchorPane.setBottomAnchor(this.mainPagePane, 0d);

        setTabPaneFullSize();
    }

    private void setTabPaneFullSize() {
        AnchorPane.setLeftAnchor(this.mainTabPane, 0d);
        AnchorPane.setRightAnchor(this.mainTabPane, 0d);
        AnchorPane.setTopAnchor(this.mainTabPane, 0d);
        AnchorPane.setBottomAnchor(this.mainTabPane, 0d);
    }

    private void setTabPaneForEditWindow(Double bottomGap) {
        AnchorPane.setLeftAnchor(this.mainTabPane, 0d);
        AnchorPane.setRightAnchor(this.mainTabPane, 0d);
        AnchorPane.setTopAnchor(this.mainTabPane, 0d);
        AnchorPane.setBottomAnchor(this.mainTabPane, bottomGap);
    }

    private void setupListenerToExtractedData() {
        this.extractedDataHolderFX.lastUpdatedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.after(oldValue)) {
                LOG.debug("Updating the data frame view for changed extracted data content");

                this.mainTabPane.getTabs().clear(); // TODO: Implement reuse of tables and tabs to prevent excess memory usage?

                if (this.extractedDataHolderFX.getBeamDataMap() != null && !this.extractedDataHolderFX.getBeamDataMap().isEmpty()) {
                    LOG.debug("Built In Beam Data Present");
                    installBeamDataTable();
                }
                if (this.extractedDataHolderFX.getTrussDataMap() != null && !this.extractedDataHolderFX.getTrussDataMap().isEmpty()) {
                    LOG.debug("Built In Truss Data Present");
                    installTrussDataTable();
                }
                if (this.extractedDataHolderFX.getSlabDataMap() != null && !this.extractedDataHolderFX.getSlabDataMap().isEmpty()) {
                    LOG.debug("Built In Slab Data Present");
                    installSlabDataTable();
                }
                if (this.extractedDataHolderFX.getCustomData() != null && !this.extractedDataHolderFX.getCustomData().isEmpty()) {
                    LOG.debug("Custom Data Present");
                    installCustomDataTables();
                } else {
                    LOG.debug("No custom data present!");
                }
            }
        });
    }

    private void installCustomDataTables() {
        Map<String, Map<Long, Map<String, Object>>> customData = this.extractedDataHolderFX.getCustomData();
        // Long is Id, Map<String, Object> is name:value

        // iterate each custom data collection
        for (Map.Entry<String, Map<Long, Map<String, Object>>> entry: customData.entrySet()) {
            LOG.debug("Processing a custom extracted data source");
            Map<Long, Map<String, Object>> dataMap = entry.getValue();
            String name = entry.getKey();
            if (dataMap.isEmpty()) {
                LOG.warn("The data map was empty");
                continue;
            }

            Map<Integer, String> colNames = new HashMap<>();
            Map<Integer, Class> colTypes = new HashMap<>();
            populateColumnNamesAndTypes(dataMap, colNames, colTypes);
            TableView<GenericDataObservable> table = createCustomTable();
            addColumnsToTable(table, colNames,colTypes);
            ObservableList<GenericDataObservable> tableData = collectItems(dataMap);
            table.setItems(tableData);

            // TODO: Name of table needs to get through to here.
            addTableToView(table, name);
        }
    }

    /**
     * BEGIN TEMPORARY METHODS
     * These methods should be moved to another class for portability and cleanup (Custom Table Manager Class)
     */

    /**
     *
     * @param dataMap
     * @param colNames
     * @param colTypes
     */
    private void populateColumnNamesAndTypes(Map<Long, Map<String, Object>> dataMap, Map<Integer, String> colNames, Map<Integer, Class> colTypes) {
        // Get the first record, populate column names and types to build columns
        Map<String, Object> recordMap = dataMap.entrySet().iterator().next().getValue();
        int idx = 0;
        for (Map.Entry<String, Object> recordProperty : recordMap.entrySet()) {
            idx++;
            String name = recordProperty.getKey();
            colNames.put(idx, name);

            Object value = recordProperty.getValue();
            colTypes.put(idx, value.getClass());
        }
    }

    private TableView<GenericDataObservable> createCustomTable() {
        // create table
        TableView<GenericDataObservable> table = new TableView();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefWidth(TableView.USE_COMPUTED_SIZE);
        table.setPrefHeight(TableView.USE_COMPUTED_SIZE);
        VBox.setVgrow(table, Priority.ALWAYS);

        return table;
    }

    private void addColumnsToTable(TableView<GenericDataObservable> table, Map<Integer, String> colNames, Map<Integer, Class> colTypes) {
        // create columns
        for (int i = 1; i <= colNames.size(); i++) {
            String name = colNames.get(i);
            Class type = colTypes.get(i); // TODO: Factor in type
            TableColumn<GenericDataObservable, String> column = new TableColumn<>();
            column.setText(name);
            int colNum = i-1;
            column.setCellValueFactory(data -> data.getValue().getValueByColumnId(colNum));
            table.getColumns().add(column);
        }
    }

    private ObservableList<GenericDataObservable> collectItems(Map<Long, Map<String, Object>> dataMap) {
        // collect items
        ObservableList<GenericDataObservable> tableData = FXCollections.observableArrayList();
        for (Map.Entry<Long, Map<String, Object>> entry : dataMap.entrySet()) {
            Map<String, Object> recordData = entry.getValue();

//            List<ObservableValue> row = new ArrayList<>();
            GenericDataObservable row = new GenericDataObservable();
            int colIdx = -1;
            for (Map.Entry<String, Object> e : recordData.entrySet()) {
                colIdx++;
                Object value = e.getValue();
                if (value == null) {
                    row.addValue(colIdx, null);
                    continue;
                }

                if (value.getClass().equals(String.class)) {
                    row.addValue(colIdx, new SimpleStringProperty((String) value));
                } else if (value.getClass().equals(Double.class)) {
                    row.addValue(colIdx, new SimpleDoubleProperty((Double) value));
                } else if (value.getClass().equals(Long.class)) {
                    row.addValue(colIdx, new SimpleLongProperty((Long) value));
                }
            }
            tableData.add(row);
        }
        return tableData;
    }

    /**
     * END TEMPORARY METHODS
     */

    private void installBeamDataTable() {
        Map<Long, BeamData> beamData = this.extractedDataHolderFX.getBeamDataMap();

        List<BeamDataObservable> beamDataList = beamData.entrySet().stream()
                .map(m -> ObservableDataConverter.convert(m.getValue()))
                .collect(Collectors.toList());

        ObservableList<BeamDataObservable> beamDataObservables =  FXCollections.observableArrayList(
                beamData1 -> new Observable[] { beamData1.treatmentProperty() }
        );

        beamDataObservables.addAll(beamDataList);

        TableView<BeamDataObservable> beamTable = BuiltInTableFactory.createBeamTable();
        beamTable.setItems(beamDataObservables);
        beamDataObservables.addListener((ListChangeListener<BeamDataObservable>) c -> {
            while (c.next()) {
                LOG.trace("List was changed: added: {} permut: {} rem: {} repl: {} updt: {}", c.wasAdded(), c.wasPermutated(), c.wasRemoved(), c.wasReplaced(), c.wasUpdated());
                if (c.wasUpdated()) {
                    this.extractedDataHolderFX.clearBeamDataMap();
                    for (BeamDataObservable b : c.getList()) {
                        LOG.trace("In list: {} {} {} {} {}", b.getBeamId(), b.getBeamType(), b.getLength(), b.getQuantity(), b.getTreatment());
                        // just push all across for now
                        this.extractedDataHolderFX.addBeamData(b.getBeamId(), b.getBeamType(), b.getLength(), b.getQuantity(), BeamTreatment.valueOf(b.getTreatment()));
                    }
                }
            }
        });

        HBox toolbar = new HBox();
        toolbar.setAlignment(Pos.CENTER_RIGHT);
        toolbar.setStyle("-fx-background-color: #fff;");
        toolbar.setSpacing(5d);
        toolbar.setPadding(new Insets(5, 5, 5,5));

        Label l = new Label("Beam Treatment: ");

        ComboBox<String> treatmentsCombo = new ComboBox<>();
        ObservableList<String> treatmentsList = FXCollections.observableArrayList();
        for (BeamTreatment value : BeamTreatment.values()) {
            treatmentsList.add(value.toString());
        }
        treatmentsCombo.setItems(treatmentsList);

        Button setAllButton = new Button("Set All");
        setAllButton.setOnAction(event -> {
            String value = treatmentsCombo.getValue();
            if (value == null || value.isEmpty()) {
                return;
            }
            beamTable.getItems().forEach(bd -> {
                if (!bd.isTreatmentLocked()) {
                    bd.setTreatment(value);
                }
            });
        });

        toolbar.getChildren().addAll(l, treatmentsCombo, setAllButton);

        // add table to the view
        addTableToViewWithToolbar(beamTable, "Beam", toolbar);

//        FlowPane editView = BuiltInTableFactory.createBeamTableEditView();
    }

    private void installTrussDataTable() {
        Map<Long, TrussData> trussData = this.extractedDataHolderFX.getTrussDataMap();

        List<TrussDataObservable> trussDataList = trussData.entrySet().stream()
                .map(m -> ObservableDataConverter.convert(m.getValue()))
                .collect(Collectors.toList());
        ObservableList<TrussDataObservable> trussDataObservables = FXCollections.observableArrayList();
        trussDataObservables.addAll(trussDataList);

        TableView<TrussDataObservable> trussTable = BuiltInTableFactory.createTrussTable();
        trussTable.setItems(trussDataObservables);

        addTableToView(trussTable, "Truss");
    }

    private void installSlabDataTable() {
        Map<Long, SlabData> slabData = this .extractedDataHolderFX.getSlabDataMap();

        List<SlabDataObservable> slabDataList = slabData.entrySet().stream()
                .map(m -> ObservableDataConverter.convert(m.getValue()))
                .collect(Collectors.toList());
        ObservableList<SlabDataObservable> slabDataObservables = FXCollections.observableArrayList();
        slabDataObservables.addAll(slabDataList);

        TableView<SlabDataObservable> slabTable = BuiltInTableFactory.createSlabTable();
        slabTable.setItems(slabDataObservables);

        addTableToView(slabTable, "Slab");
    }

    /**
     * Method that puts a Table inside a VBox inside a Tab and added to the Tab Pane on the page.
     * @param table
     * @param name
     */
    private void addTableToView(TableView table, String name) {
        addTableToViewWithToolbar(table, name, null);
    }

    private void addTableToViewWithToolbar(TableView table, String name, Pane toolbar) {
        VBox vbox = new VBox();
        vbox.setFillWidth(true);
        vbox.setPrefWidth(VBox.USE_COMPUTED_SIZE);
        vbox.setPrefHeight(VBox.USE_COMPUTED_SIZE);
        VBox.setVgrow(vbox, Priority.ALWAYS);

//        vbox.setMaxWidth();
        vbox.setBackground(new Background(new BackgroundFill(Paint.valueOf("#cccc77"), CornerRadii.EMPTY, Insets.EMPTY)));

        if (toolbar != null) {
            vbox.getChildren().add(toolbar);
        }
        vbox.getChildren().add(table);

        Tab tab = new Tab();
        tab.setText(name + " Data");
        tab.setContent(vbox);

        mainTabPane.getTabs().add(tab);
    }
}