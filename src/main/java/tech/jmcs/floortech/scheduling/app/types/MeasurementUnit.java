package tech.jmcs.floortech.scheduling.app.types;

public enum MeasurementUnit {
    LM ("Linear Meter"),
    M2 ("Square Meter");

    private final String name;

    MeasurementUnit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
