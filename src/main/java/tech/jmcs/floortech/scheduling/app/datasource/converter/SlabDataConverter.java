package tech.jmcs.floortech.scheduling.app.datasource.converter;

import tech.jmcs.floortech.scheduling.app.datasource.model.ExtractedTableData;
import tech.jmcs.floortech.scheduling.app.datasource.model.SlabData;

import java.util.Map;

public class SlabDataConverter extends DataFormatConverter<SlabData> {

    @Override
    public Map<String, Object> convert(ExtractedTableData<SlabData> d) {
        /**
         * Need to collect:
         * - each type of slab data and map to value on schedule
         */

        return null;
    }
}
