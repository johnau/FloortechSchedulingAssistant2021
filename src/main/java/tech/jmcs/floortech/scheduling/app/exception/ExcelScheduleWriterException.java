package tech.jmcs.floortech.scheduling.app.exception;

public class ExcelScheduleWriterException extends DataWriterException {

    public ExcelScheduleWriterException(String message) {
        super(message, "EXCEL SCHEDULE");
    }

}
