package tech.jmcs.floortech.scheduling.app.types;

public enum BeamTreatment {

    BLACK ("Black"),
    GALVANISED ("Galvanised"),
    DIMET ("Dimet"),
    EPOXY ("Epoxy"),
    DURAGAL ("Duragal");

    private String name;

    BeamTreatment(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
