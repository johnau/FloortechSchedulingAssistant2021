package tech.jmcs.floortech.scheduling.ui.dataframe.table;

import javafx.beans.property.*;
import javafx.beans.value.ObservableLongValue;
import javafx.beans.value.ObservableValue;
import tech.jmcs.floortech.scheduling.app.types.BeamTreatment;

public class BeamDataObservable {
    private StringProperty beamType = new SimpleStringProperty("", "beamType");
    private LongProperty quantity = new SimpleLongProperty(0L, "quantity");
    private StringProperty beamId = new SimpleStringProperty("", "beamId");
    private LongProperty length = new SimpleLongProperty(0L, "length");
    private StringProperty treatment = new SimpleStringProperty(BeamTreatment.BLACK.toString(), "treatment");
    private BooleanProperty treatmentLocked = new SimpleBooleanProperty(false, "treatmentLocked");

    public BeamDataObservable() {
    }

    public String getBeamType() {
        return beamType.get();
    }

    public StringProperty beamTypeProperty() {
        return beamType;
    }

    public void setBeamType(String beamType) {
        this.beamType.set(beamType);
    }

    public long getQuantity() {
        return quantity.get();
    }

    public LongProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity.set(quantity);
    }

    public String getBeamId() {
        return beamId.get();
    }

    public StringProperty beamIdProperty() {
        return beamId;
    }

    public void setBeamId(String beamId) {
        this.beamId.set(beamId);
    }

    public long getLength() {
        return length.get();
    }

    public LongProperty lengthProperty() {
        return length;
    }

    public void setLength(long length) {
        this.length.set(length);
    }

    public String getTreatment() {
        return treatment.get();
    }

    public StringProperty treatmentProperty() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment.set(treatment);
    }

    public boolean isTreatmentLocked() {
        return treatmentLocked.get();
    }

    public BooleanProperty treatmentLockedProperty() {
        return treatmentLocked;
    }

    public void setTreatmentLocked(boolean treatmentLocked) {
        this.treatmentLocked.set(treatmentLocked);
    }
}
