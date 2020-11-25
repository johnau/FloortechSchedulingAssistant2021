package tech.jmcs.floortech.scheduling.app.extractor;

public enum GenericExtractorValidationFunctionFilterValue {
    EMPTY ("Empty");

    private final String name;

    private GenericExtractorValidationFunctionFilterValue(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
