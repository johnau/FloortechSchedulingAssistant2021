package tech.jmcs.floortech.scheduling.app.datasource.extractor;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;
import java.util.function.Function;

public class TableValidationFunctionProducer extends GenericExtractorValidationFunctionProducer<Sheet> {

    @Override
    public Function<Sheet, String> getFunction() {
        if (this.targetColumn == null || this.targetRow == null || this.filterType == null) {
            if (this.targetColumn == null) {

            }
            if (this.targetRow == null) {

            }
            if (this.filterType == null) {

            }
            // log : missing properties
            return null;
        }

        Function<Sheet, String> f = (sheet) -> {

            int col = this.targetColumn;
            int row = this.targetRow;
            boolean negative = this.negativeFilter == null ? false : this.negativeFilter;
            List<GenericExtractorValidationFunctionFilterCondition> conditions = this.additionalConditions;
            GenericExtractorValidationFunctionFilterType filtType = this.filterType;

            boolean isStringTypeFilter = false;

            if (isStringTypeFilter) {
                // process String filter type filters
            } else {
                // process Enum filter type filers
            }

            return "";
        };

        return null;
    }

}
