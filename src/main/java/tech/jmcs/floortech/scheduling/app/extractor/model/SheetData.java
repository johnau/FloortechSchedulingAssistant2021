package tech.jmcs.floortech.scheduling.app.extractor.model;

public class SheetData {

    private String zoneId;

    private Long length;

    private Long qty;

    public SheetData() {
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }
}
