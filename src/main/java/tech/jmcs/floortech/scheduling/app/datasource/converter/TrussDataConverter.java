package tech.jmcs.floortech.scheduling.app.datasource.converter;

import tech.jmcs.floortech.scheduling.app.datasource.model.ExtractedTableData;
import tech.jmcs.floortech.scheduling.app.datasource.model.TrussData;

import java.util.Map;

public class TrussDataConverter extends DataFormatConverter<TrussData> {

    @Override
    public Map<String, Object> convert(ExtractedTableData<TrussData> d) {

        /**
         * Need to collect:
         * - NEC Count
         * - STD Count
         * - Total meters truss
         * - Air con penos
         * -
         */

        return null;
    }
}
