package tech.jmcs.floortech.scheduling.ui.dataframe.table;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.HashMap;

public class GenericDataObservable {

    private StringProperty dataName = new SimpleStringProperty("", "dataName");
    private ObservableMap<Integer, ObservableValue> valuesMap = FXCollections.observableMap(new HashMap()); // stored by column id

    public GenericDataObservable() {
    }

    public String getDataName() {
        return dataName.get();
    }

    public StringProperty dataNameProperty() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName.set(dataName);
    }

    public ObservableMap<Integer, ObservableValue> getValuesMap() {
        return valuesMap;
    }

    public void addValue(Integer columnId, ObservableValue value) {
        this.valuesMap.put(columnId, value);
    }

    public void setValuesMap(ObservableMap<Integer, ObservableValue> valuesMap) {
        this.valuesMap = valuesMap;
    }

    public ObservableValue getValueByColumnId(Integer columnId) {
        return this.valuesMap.get(columnId);
    }
}
