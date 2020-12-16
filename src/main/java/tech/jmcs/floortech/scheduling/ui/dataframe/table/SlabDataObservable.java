package tech.jmcs.floortech.scheduling.ui.dataframe.table;

import javafx.beans.property.*;
import tech.jmcs.floortech.scheduling.app.types.MeasurementUnit;

public class SlabDataObservable {

    private StringProperty name = new SimpleStringProperty("", "name");
    private DoubleProperty size = new SimpleDoubleProperty(0d, "size");
    private ObjectProperty<MeasurementUnit> measurementUnit = new SimpleObjectProperty<>(null, "measurementUnit");

    public SlabDataObservable() {
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public double getSize() {
        return size.get();
    }

    public DoubleProperty sizeProperty() {
        return size;
    }

    public void setSize(double size) {
        this.size.set(size);
    }

    public MeasurementUnit getMeasurementUnit() {
        return measurementUnit.get();
    }

    public ObjectProperty<MeasurementUnit> measurementUnitProperty() {
        return measurementUnit;
    }

    public void setMeasurementUnit(MeasurementUnit measurementUnit) {
        this.measurementUnit.set(measurementUnit);
    }
}
