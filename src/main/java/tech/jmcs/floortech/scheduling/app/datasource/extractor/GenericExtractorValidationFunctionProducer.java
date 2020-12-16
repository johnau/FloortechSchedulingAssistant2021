package tech.jmcs.floortech.scheduling.app.datasource.extractor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Fluent API Function Producer intended for use with Sheet or Row for Table & Record Validation functions.
 * If producing function for use with Table Validation, use Sheet class
 * If producing function for use with Record Validation, use Row class
 * @param <T>
 */
public abstract class GenericExtractorValidationFunctionProducer<T> {

    protected Integer targetColumn;
    protected Integer targetRow;
    protected Boolean negativeFilter;
    protected GenericExtractorValidationFunctionFilterType filterType;
    protected String filterValue_str;
    protected GenericExtractorValidationFunctionFilterValue filterValue_enum;
    protected List<GenericExtractorValidationFunctionFilterCondition> additionalConditions;

    public GenericExtractorValidationFunctionProducer() {
        this.additionalConditions = new ArrayList<>();
    }

    public GenericExtractorValidationFunctionProducer setTargetColumn(Integer columnNumber) {
        this.targetColumn = columnNumber;
        return this;
    }

    public GenericExtractorValidationFunctionProducer setTargetRow(Integer rowNumber) {
        this.targetRow = rowNumber;
        return this;
    }

    public GenericExtractorValidationFunctionProducer setNegativeFilter(boolean negative) {
        this.negativeFilter = negative;
        return this;
    }

    public GenericExtractorValidationFunctionProducer setFilterType(GenericExtractorValidationFunctionFilterType fType) {
        this.filterType = fType;
        return this;
    }

    public GenericExtractorValidationFunctionProducer setFilterValue(String fVal) {
        // depending on filter type, this method should be blocked or allowed
        switch (this.filterType) {
            case CONTAINS:
            case STARTS_WITH:
            case ENDS_WIDTH:
            case EQUALS:
                break;
            case HAS_SEQUENTIAL_IDS:
            case IS_DATA_TYPE:
                // log, cant set for Enum filters
                return this;
        }

        this.filterValue_str = fVal;
        return this;
    }

    public GenericExtractorValidationFunctionProducer setFilterValue(GenericExtractorValidationFunctionFilterValue fVal) {
        // depending on filter type, this method should be blocked or allowed

        if (this.filterType == null) {
            // log, don't allow, UI should not display option to enter a value if filter type not set
            return this;
        }

        switch (this.filterType) {
            case CONTAINS:
            case STARTS_WITH:
            case ENDS_WIDTH:
            case EQUALS:
                // log, cant set for String filters
                return this;
            case HAS_SEQUENTIAL_IDS:
            case IS_DATA_TYPE:
                // these ok to set enum value
                break;
        }

        this.filterValue_enum = fVal;
        return this;
    }

    public GenericExtractorValidationFunctionProducer addAdditionalCondition(GenericExtractorValidationFunctionFilterCondition fCondition) {
        // can have multiple conditions
        if (!this.additionalConditions.contains(fCondition)) {
            this.additionalConditions.add(fCondition);
        }
        return this;
    }

    public GenericExtractorValidationFunctionProducer removeAdditionalCondition(GenericExtractorValidationFunctionFilterCondition fCondition) {
        if (this.additionalConditions.contains(fCondition)) {
            this.additionalConditions.remove(fCondition);
        }
        return this;
    }

    /**
     * Gets the Function<T, String> where the return value of the function is an error string.
     * If the function was successful, it should return Null or an empty String.
     * @return
     */
    public abstract Function<T, String> getFunction();

}
