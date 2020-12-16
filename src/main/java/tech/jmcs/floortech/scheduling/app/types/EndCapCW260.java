package tech.jmcs.floortech.scheduling.app.types;

public enum EndCapCW260 {
    TYPE_A ("Type A"),
    TYPE_B ("Type B"),
    TYPE_C ("Type C"),
    TYPE_D ("Type D"),
    TYPE_E ("Type E"),
    TYPE_F ("Type F"),
    SPECIAL ("Special"),
    ADJUSTABLE ("Adjustable"),
    STANDARD ("Standard");

    private final String name;

    private EndCapCW260(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static EndCapCW260 fromName(String name) {
        for (EndCapCW260 value : EndCapCW260.values()) {
            String n = value.getName();
            if (name.toLowerCase().equals(n.toLowerCase())) {
                return value;
            }
        }
        return null;
    }
}
