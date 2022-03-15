package tech.jmcs.floortech.scheduling.ui.customextractorwizard.recordvalidation;

import javafx.util.StringConverter;

public class ValidationTypeConverter extends StringConverter<ValidationType> {
    @Override
    public String toString(ValidationType object) {
        return object.getStringValue();
    }

    @Override
    public ValidationType fromString(String string) {
        try {
            return ValidationType.fromStringValue(string);
        } catch (Exception e) {
            return null;
        }
    }
}
