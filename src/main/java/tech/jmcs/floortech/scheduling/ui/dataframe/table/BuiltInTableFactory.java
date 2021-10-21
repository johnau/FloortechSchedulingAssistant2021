package tech.jmcs.floortech.scheduling.ui.dataframe.table;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import tech.jmcs.floortech.scheduling.app.datasource.model.SheetData;
import tech.jmcs.floortech.scheduling.app.types.BeamTreatment;

import java.util.Arrays;
import java.util.stream.Collectors;

public class BuiltInTableFactory {

    private static <T> TableView<T> createTableView() {
        TableView<T> table = new TableView();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefWidth(TableView.USE_COMPUTED_SIZE);
        table.setPrefHeight(TableView.USE_COMPUTED_SIZE);
        VBox.setVgrow(table, Priority.ALWAYS);

        return table;
    }

    public static TableView<BeamDataObservable> createBeamTable() {
        TableView<BeamDataObservable> table = createTableView();
        table.setEditable(true);

        TableColumn<BeamDataObservable, String> beamIdCol = new TableColumn<>();
//        beamIdCol.setCellValueFactory(new PropertyValueFactory<>("beamId"));
        beamIdCol.setCellValueFactory(cellData -> cellData.getValue().beamIdProperty());
        beamIdCol.setText("Beam Id");

        TableColumn<BeamDataObservable, String> beamTypeCol = new TableColumn<>();
//        beamTypeCol.setCellValueFactory(new PropertyValueFactory<>("beamType"));
        beamTypeCol.setCellValueFactory(cellData -> cellData.getValue().beamTypeProperty());
        beamTypeCol.setText("Beam Type");

        TableColumn<BeamDataObservable, Long> quantity = new TableColumn<>();
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
//        quantity.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
        quantity.setText("Quantity");

        TableColumn<BeamDataObservable, Long> length = new TableColumn<>();
        length.setCellValueFactory(new PropertyValueFactory<>("length"));
//        length.setCellValueFactory(cellData -> cellData.getValue().lengthProperty());
        length.setText("Length");

        TableColumn<BeamDataObservable, String> treatment = new TableColumn<>();
//        treatment.setCellValueFactory(new PropertyValueFactory<>("treatment"));
        treatment.setCellValueFactory(cellData -> cellData.getValue().treatmentProperty());
        String[] bt = Arrays.asList(BeamTreatment.values()).stream().map(m -> m.toString()).toArray(String[]::new);
        treatment.setCellFactory(ComboBoxTableCell.forTableColumn(bt));
        treatment.setText("Treatment");

        // treatment colum needs to have a custom cell factory taht displays a drop down menu when the row is selected.
        // this will be populated from the BeamTreatment enum and use a StringConverter to bring it to and from the enum
        // When the value is changed it has to update the item.

        table.getColumns().addAll(beamTypeCol, beamIdCol, quantity, length, treatment);

        return table;
    }

    public static FlowPane createBeamTableEditView() {
        FlowPane flowPane = new FlowPane();

        // length
        VBox lengthVbox = new VBox();

        TextField beamLengthField = new TextField();

        Label lengthFieldLabel = new Label();
        lengthFieldLabel.setText("Length: ");
        lengthFieldLabel.setLabelFor(beamLengthField);

        lengthVbox.getChildren().addAll(lengthFieldLabel, beamLengthField);

        // treatment
        VBox treatmentVbox = new VBox();

        ChoiceBox beamTreatmentChoice = new ChoiceBox<BeamTreatment>();
        Label treatmentFieldLabel = new Label();
        treatmentFieldLabel.setText("Treatment: ");
        treatmentFieldLabel.setLabelFor(beamTreatmentChoice);

        treatmentVbox.getChildren().addAll(treatmentFieldLabel, beamTreatmentChoice);

        flowPane.getChildren().addAll(lengthVbox, treatmentVbox);

        return flowPane;
    }

    // TODO: Change TableView data type to observable class?
    public static TableView<TrussDataObservable> createTrussTable() {
        TableView<TrussDataObservable> table = createTableView();

        TableColumn<TrussDataObservable, String> trussIdCol = new TableColumn<>("Truss Id");
        trussIdCol.setCellValueFactory(cellData -> cellData.getValue().trussIdProperty());
//        trussIdCol.setCellValueFactory(new PropertyValueFactory<>("trussId"));


        TableColumn<TrussDataObservable, Long> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("qty"));

        TableColumn<TrussDataObservable, Long> lengthCol = new TableColumn<>("Length");
        lengthCol.setCellValueFactory(new PropertyValueFactory<>("length"));

        TableColumn<TrussDataObservable, String> typeCol = new TableColumn<>("Truss Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<TrussDataObservable, String> leftEndcapCol = new TableColumn<>("Left E/C");
        leftEndcapCol.setCellValueFactory(new PropertyValueFactory<>("leftEndcap"));

        TableColumn<TrussDataObservable, String> rightEndcapCol = new TableColumn<>("Right E/C");
        rightEndcapCol.setCellValueFactory(new PropertyValueFactory<>("rightEndcap"));

        TableColumn<TrussDataObservable, String> airConPenoCol = new TableColumn<>("Has A/C Peno");
        airConPenoCol.setCellValueFactory(cellData -> {
            Boolean acp = cellData.getValue().isAirConPeno();
            String acps = "-";
            if (acp) {
                acps = "Yes";
            }
            return new SimpleStringProperty(acps);
        });

        TableColumn<TrussDataObservable, String> penetrationWebCutsCol = new TableColumn<>("Peno Web Cuts");
        penetrationWebCutsCol.setCellValueFactory(cellData -> {
            ObservableList<Integer> cuts = cellData.getValue().getPenetrationWebCuts();
            String s = "";
            if (cuts != null && !cuts.isEmpty()) {
                s += cuts.get(0) + "+" + cuts.get(1);
            }
            return new SimpleStringProperty(s);
        });

        TableColumn<TrussDataObservable, String> packingGroupCol = new TableColumn<>("Packing Group");
        packingGroupCol.setCellValueFactory(new PropertyValueFactory<>("packingGroup"));

        table.getColumns().addAll(typeCol, trussIdCol, qtyCol, lengthCol, leftEndcapCol,rightEndcapCol, airConPenoCol, penetrationWebCutsCol, packingGroupCol);

        return table;
    }

    // TODO: Change TableView data type to observable class?
    public static TableView<SlabDataObservable> createSlabTable() {
        TableView<SlabDataObservable> table = createTableView();

        TableColumn<SlabDataObservable, String> nameCol = new TableColumn<>();
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setText("Item Name");

        TableColumn<SlabDataObservable, String> sizeCol = new TableColumn<>();
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("size"));
        sizeCol.setText("Size");

        TableColumn<SlabDataObservable, String> unitCol = new TableColumn<>();
        unitCol.setCellValueFactory(new PropertyValueFactory<>("measurementUnit"));
        unitCol.setText("Unit");

        table.getColumns().addAll(nameCol, sizeCol, unitCol);

        return table;
    }

    // TODO: Change TableView data type to observable class?
    public static TableView<SheetData> createSheetTable(SheetData sheetData) {
        return null;
    }

}
