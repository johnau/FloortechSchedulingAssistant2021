package tech.jmcs.floortech.scheduling.app.converter;

import tech.jmcs.floortech.scheduling.app.extractor.model.ExtractedTableData;
import tech.jmcs.floortech.scheduling.app.extractor.model.SlabData;

import java.util.Map;

public class SlabDataConverter extends DataFormatConverter<SlabData> {

    @Override
    public Map<String, Object> convert(ExtractedTableData<SlabData> d) {
        return null;
    }
}
