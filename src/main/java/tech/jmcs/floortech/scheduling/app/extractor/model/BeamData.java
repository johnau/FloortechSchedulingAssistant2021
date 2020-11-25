package tech.jmcs.floortech.scheduling.app.extractor.model;

/**
 * Data object for storing values from Beam Listing
 * ** Note on adding fields:
 * ! Default values should be provided for all fields for older truss lists that do not supply that data.
 */
public class BeamData {

    private String beamType = "NA";

    private Long quantity = 0l;

    private String beamId = "NA";

    private Long length = 0l;

    public BeamData() {
    }

    public BeamData(String beamType) {
        this();
        this.beamType = beamType;
    }

    public String getBeamType() {
        return beamType;
    }

    public void setBeamType(String beamType) {
        this.beamType = beamType;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getBeamId() {
        return beamId;
    }

    public void setBeamId(String beamId) {
        this.beamId = beamId;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }
}
