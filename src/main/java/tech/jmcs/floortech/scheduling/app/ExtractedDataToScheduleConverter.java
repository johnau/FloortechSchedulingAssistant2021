package tech.jmcs.floortech.scheduling.app;

import tech.jmcs.floortech.scheduling.app.datasource.converter.BeamDataConverter;
import tech.jmcs.floortech.scheduling.app.datasource.converter.SheetsDataConverter;
import tech.jmcs.floortech.scheduling.app.datasource.converter.SlabDataConverter;
import tech.jmcs.floortech.scheduling.app.datasource.converter.TrussDataConverter;
import tech.jmcs.floortech.scheduling.app.datasource.model.*;

import java.util.Map;

/**
 * Class to aggregate the converters for easier use.
 * Could be static right now, but leave as an injected object so that storage can be added?
 */
public class ExtractedDataToScheduleConverter {

    /**
     * Converts ExtractedData object to Map
     * @return Map of Values stored by schedule entry name
     */
    public Map<String, Object> convertBeamDataForSchedule(ExtractedTableData<BeamData> data) {
        BeamDataConverter converter = new BeamDataConverter();
        return converter.convert(data);
    }

    public Map<String, Object> convertTrussDataForSchedule(ExtractedTableData<TrussData> data) {
        TrussDataConverter converter = new TrussDataConverter();
        return converter.convert(data);
    }

    public Map<String, Object> convertSlabDataForSchedule(ExtractedTableData<SlabData> data) {
        SlabDataConverter converter = new SlabDataConverter();
        return converter.convert(data);
    }

    public Map<String, Object> convertSheetDataForSchedule(ExtractedTableData<SheetData> data) {
        SheetsDataConverter converter = new SheetsDataConverter();
        return converter.convert(data);
    }

}
