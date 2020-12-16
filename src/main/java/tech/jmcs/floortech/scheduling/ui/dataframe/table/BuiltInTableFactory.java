package tech.jmcs.floortech.scheduling.ui.dataframe.table;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import tech.jmcs.floortech.scheduling.app.datasource.model.SheetData;
import tech.jmcs.floortech.scheduling.app.types.BeamTreatment;

public class BuiltInTableFactory {

    private static <T> TableView<T> createTableView() {
        TableView<T> table = new TableView();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefWidth(TableView.USE_COMPUTED_SIZE);
        table.setPrefHeight(TableView.USE_COMPUTED_SIZE);
        VBox.setVgrow(table, Priority.ALWAYS);

        return table;
    }

    // TODO: Change TableView data type to observable class?
    public static TableView<BeamDataObservable> createBeamTable() {
        TableView<BeamDataObservable> table = createTableView();

        TableColumn<BeamDataObservable, String> beamIdCol = new TableColumn<>();
        beamIdCol.setCellValueFactory(new PropertyValueFactory<>("beamId"));
        beamIdCol.setText("Beam Id");

        TableColumn<BeamDataObservable, String> beamTypeCol = new TableColumn<>();
        beamTypeCol.setCellValueFactory(new PropertyValueFactory<>("beamType"));
        beamTypeCol.setText("Beam Type");

        TableColumn<BeamDataObservable, Long> quantity = new TableColumn<>();
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantity.setText("Quantity");

        TableColumn<BeamDataObservable, Long> length = new TableColumn<>();
        length.setCellValueFactory(new PropertyValueFactory<>("length"));
        length.setText("Length");

        TableColumn<BeamDataObservable, String> treatment = new TableColumn<>();
        treatment.setCellValueFactory(new PropertyValueFactory<>("treatment"));
        treatment.setText("Treatment");

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

        TableColumn<TrussDataObservable, String> trussIdCol = new TableColumn<>();
        trussIdCol.setCellValueFactory(new PropertyValueFactory<>("trussId"));
        trussIdCol.setText("Truss Id");

        TableColumn<TrussDataObservable, Long> qtyCol = new TableColumn<>();
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("qty"));
        qtyCol.setText("Quantity");

        TableColumn<TrussDataObservable, Long> lengthCol = new TableColumn<>();
        lengthCol.setCellValueFactory(new PropertyValueFactory<>("length"));
        lengthCol.setText("Length");

        TableColumn<TrussDataObservable, String> typeCol = new TableColumn<>();
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setText("Truss Type");

        TableColumn<TrussDataObservable, String> leftEndcapCol = new TableColumn<>();
        leftEndcapCol.setCellValueFactory(new PropertyValueFactory<>("leftEndcap"));
        leftEndcapCol.setText("Left E/C");

        TableColumn<TrussDataObservable, String> rightEndcapCol = new TableColumn<>();
        rightEndcapCol.setCellValueFactory(new PropertyValueFactory<>("rightEndcap"));
        rightEndcapCol.setText("Right E/C");

        TableColumn<TrussDataObservable, String> airConPenoCol = new TableColumn<>();
        airConPenoCol.setCellValueFactory(new PropertyValueFactory<>("airConPeno"));
        airConPenoCol.setText("Has Air-Con Peno");

        TableColumn<TrussDataObservable, String> penetrationWebCutsCol = new TableColumn<>();
//        airConPenoCol.setCellValueFactory(new PropertyValueFactory<>("airConPeno"));
        penetrationWebCutsCol.setText("A/C Peno Web Cuts");

        TableColumn<TrussDataObservable, String> packingGroupCol = new TableColumn<>();
        packingGroupCol.setCellValueFactory(new PropertyValueFactory<>("packingGroup"));
        packingGroupCol.setText("Packing Group");

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
