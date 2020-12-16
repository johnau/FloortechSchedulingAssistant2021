package tech.jmcs.floortech.scheduling.app.schedulewriter;

import org.apache.poi.ss.usermodel.Sheet;
import tech.jmcs.floortech.scheduling.app.util.XLSUtility;

public abstract class ExcelScheduleUpdater implements ScheduleUpdater {
    public abstract XLSUtility getXlsUtil();
    public abstract Integer getTargetSheetNumber();

    public abstract Sheet getTargetSheet();

    public abstract Integer getDataNameColumn();
    public abstract Integer getDataValueColumn();
    public abstract Integer getDataUnitColumn();
    public abstract Integer getDataRateColumn();
    public abstract Integer getDataTotalColumn();
    public abstract Integer getDataCommentColumn();
}
