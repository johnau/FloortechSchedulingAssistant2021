package tech.jmcs.floortech.scheduling.ui.data.table;

import javafx.beans.property.*;
import tech.jmcs.floortech.scheduling.app.types.BeamTreatment;

public class BeamDataObservable {
    private StringProperty beamType = new SimpleStringProperty("", "beamType");

    private LongProperty quantity = new SimpleLongProperty(0L, "quantity");

    private StringProperty beamId = new SimpleStringProperty("", "beamId");

    private LongProperty length = new SimpleLongProperty(0L, "length");

    private ObjectProperty<BeamTreatment> treatment = new SimpleObjectProperty<>(BeamTreatment.BLACK, "treatment");

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

    public BeamTreatment getTreatment() {
        return treatment.get();
    }

    public ObjectProperty<BeamTreatment> treatmentProperty() {
        return treatment;
    }

    public void setTreatment(BeamTreatment treatment) {
        this.treatment.set(treatment);
    }
}
