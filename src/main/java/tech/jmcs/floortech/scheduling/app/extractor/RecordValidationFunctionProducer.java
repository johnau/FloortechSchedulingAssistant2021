package tech.jmcs.floortech.scheduling.app.extractor;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.function.Function;

public class RecordValidationFunctionProducer extends GenericExtractorValidationFunctionProducer<Row> {

    @Override
    public Function<Row, String> getFunction() {
        return null;
    }

}
