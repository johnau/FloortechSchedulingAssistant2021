package tech.jmcs.floortech.scheduling.app.datasource.extractor;

public enum GenericExtractorValidationFunctionFilterCondition {

    CASE_INSENSITIVE ("Case Insensitive"),
    CASE_SENSITIVE ("Case Sensitive"),
    IGNORE_SPACES ("Ignore Spaces"),
    TRIM_SPACES ("Trim Spaces");

    private final String name;

    GenericExtractorValidationFunctionFilterCondition(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
