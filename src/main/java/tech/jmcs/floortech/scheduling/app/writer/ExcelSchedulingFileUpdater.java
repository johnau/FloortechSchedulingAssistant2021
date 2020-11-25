package tech.jmcs.floortech.scheduling.app.writer;

import tech.jmcs.floortech.scheduling.app.util.XLSUtility;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class ExcelSchedulingFileUpdater implements ScheduleUpdater {

    protected Path excelSchedulePath;

    protected XLSUtility xls;

    public ExcelSchedulingFileUpdater(Path excelSchedulePath) throws IOException {
        this.excelSchedulePath = excelSchedulePath;

        this.xls = new XLSUtility(excelSchedulePath);
        this.xls.loadFile();
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public void updateSchedule(Map<String, Object> values) {
        // TODO: Implement

        // confirm correct sheet (by Name of sheet and other markers)

        // consider how to check for all sheets that match and allow user to interface with which one is used.
        // default to the first occurrence of a match in the book.


    }
}
