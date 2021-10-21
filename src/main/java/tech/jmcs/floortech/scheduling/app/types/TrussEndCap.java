package tech.jmcs.floortech.scheduling.app.types;

public enum TrussEndCap {
    TYPE_A ("Type A"),
    TYPE_B ("Type B"),
    TYPE_C ("Type C"),
    TYPE_D ("Type D"),
    TYPE_E ("Type E"),
    TYPE_F ("Type F"),
    TYPE_G ("Type G"),
    TYPE_H ("Type H"),
    TYPE_I ("Type I"),
    TYPE_J ("Type J"),
    TYPE_K ("Type K"),
    SPECIAL ("Special"),
    ADJUSTABLE ("Adjustable"),
    STANDARD ("Standard");

    private final String name;

    private TrussEndCap(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static TrussEndCap fromName(String name) {
        for (TrussEndCap value : TrussEndCap.values()) {
            String n = value.getName();
            if (name.toLowerCase().equals(n.toLowerCase())) {
                return value;
            }
        }
        return null;
    }
}
