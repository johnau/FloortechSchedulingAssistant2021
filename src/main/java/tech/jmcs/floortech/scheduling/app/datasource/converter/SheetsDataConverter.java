package tech.jmcs.floortech.scheduling.app.datasource.converter;

import tech.jmcs.floortech.scheduling.app.datasource.model.ExtractedTableData;
import tech.jmcs.floortech.scheduling.app.datasource.model.SheetData;

import java.util.Map;

public class SheetsDataConverter extends DataFormatConverter<SheetData> {

    @Override
    public Map<String, Object> convert(ExtractedTableData<SheetData> d) {
        return null;
    }
}
