package tech.jmcs.floortech.scheduling.ui.customextractorwizard.recordvalidation;

public enum ValidationType {
    EQUALS ("="),
    NOT_EQUALS ("not ="),
    GREATER_THAN (">"),
    LESS_THAN ("<"),
    GREATER_THAN_OR_EQUAL_TO (">="),
    LESS_THAN_OR_EQUAL_TO ("<="),
    NOT_EMPTY ("Not Empty"),
    EMPTY ("Empty"),
    IS_TYPE ("is Type"),
    CONTAINS ("contains"),
    STARTS_WITH ("starts with"),
    ENDS_WITH ("ends with");

    private final String stringValue;

    ValidationType(String stringValue) {
        this.stringValue = stringValue;
    }

    public static ValidationType fromStringValue(String string) throws Exception {
        for (ValidationType value : ValidationType.values()) {
            if (value.getStringValue().equals(string)) {
                return value;
            }
        }
        throw new Exception("Validation Type did not exist");
    }

    public final String getStringValue() {
        return stringValue;
    }
}
