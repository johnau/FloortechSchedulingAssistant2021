package tech.jmcs.floortech.scheduling.ui.customextractorwizard.recordvalidation;

public class RecordValidator {

    private Integer targetColumn;
    private ValidationType validationType;
    private String validationValueString;
    private Double validationValueDouble;

    public RecordValidator() {
    }

    public Integer getTargetColumn() {
        return targetColumn;
    }

    public void setTargetColumn(Integer targetColumn) {
        this.targetColumn = targetColumn;
    }

    public ValidationType getValidationType() {
        return validationType;
    }

    public void setValidationType(ValidationType validationType) {
        this.validationType = validationType;
    }

    public String getValidationValueString() {
        return validationValueString;
    }

    public void setValidationValueString(String validationValueString) {
        this.validationValueString = validationValueString;
    }

    public Double getValidationValueDouble() {
        return validationValueDouble;
    }

    public void setValidationValueDouble(Double validationValueDouble) {
        this.validationValueDouble = validationValueDouble;
    }

    public boolean isOk() {
        boolean valueOk = false;
        if (validationValueString == null && validationValueDouble != null) {
            valueOk = true;
        } else if (validationValueString != null && validationValueDouble == null) {
            valueOk = true;
        }

        if (targetColumn > -1 && validationType != null && valueOk) {
            return true;
        } else {
            return false;
        }
    }
}
