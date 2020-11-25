package tech.jmcs.floortech.scheduling.app.extractor;

public enum GenericExtractorValidationFunctionFilterType {
    STARTS_WITH ("Starts with"),
    ENDS_WIDTH ("Ends with"),
    CONTAINS ("Contains"),
    EQUALS ("Equals"),
    IS_DATA_TYPE("Is data type"),
    HAS_SEQUENTIAL_IDS ("Has sequential IDs");


    private final String name;

    private GenericExtractorValidationFunctionFilterType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
