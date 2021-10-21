package tech.jmcs.floortech.scheduling.app.types;

public enum BeamTreatment {

    BLACK ("Black", 0),
    GALVANISED ("Galvanised", 2),
    DURAGALV("Duragalv", 4),
    DIMET ("Dimet", 8),
    EPOXY ("Epoxy", 16);


    private String nameForSchedule;
    private int rank;

    BeamTreatment(String nameForSchedule, int rank) {
        this.nameForSchedule = nameForSchedule;
        this.rank = rank;
    }

    public String getNameForSchedule() {
        return nameForSchedule;
    }

    public int getRank() {
        return rank;
    }
}
