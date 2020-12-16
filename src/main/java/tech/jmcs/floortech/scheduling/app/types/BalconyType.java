package tech.jmcs.floortech.scheduling.app.types;

public enum BalconyType {

    NONE ("NONE"),
    RHS_2C ("2C RHS"),
    RHS_3C ("3C RHS"),
    INSITU_2C ("2C INSITU"),
    INSITU_3C ("3C INSITU"),
    INSITU_4C ("4C INSITU");

    private final String name;

    private BalconyType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    /**
     * Returns null if cant find a match
     * @param name
     * @return
     */
    public static BalconyType fromName(String name) {
        for (BalconyType t : BalconyType.values()) {
            if (t.getName().equals(name)) {
                return t;
            }

            String n1 = name.toUpperCase().trim().replaceAll(" +", "");
            String n2 = t.getName().toUpperCase().trim().replaceAll(" +", "");
            if (n1.equals(n2)) {
                return t;
            }
        }
        return null;
    }
}
