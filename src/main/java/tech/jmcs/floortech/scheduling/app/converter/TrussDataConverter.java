package tech.jmcs.floortech.scheduling.app.converter;

import tech.jmcs.floortech.scheduling.app.extractor.model.ExtractedTableData;
import tech.jmcs.floortech.scheduling.app.extractor.model.TrussData;

import java.util.Map;

public class TrussDataConverter extends DataFormatConverter<TrussData> {

    @Override
    public Map<String, Object> convert(ExtractedTableData<TrussData> d) {
        return null;
    }
}
