package tech.jmcs.floortech.scheduling.app.converter;

import tech.jmcs.floortech.scheduling.app.extractor.model.ExtractedTableData;

import java.util.Map;

// TODO: Implement Generic Data Converter
public class GenericDataConverter extends DataFormatConverter<Map<String, Object>> {

    @Override
    public Map<String, Object> convert(ExtractedTableData<Map<String, Object>> d) {
        return null;
    }

}
