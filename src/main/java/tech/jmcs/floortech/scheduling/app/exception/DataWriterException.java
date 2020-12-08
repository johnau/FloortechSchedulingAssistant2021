package tech.jmcs.floortech.scheduling.app.exception;

public class DataWriterException extends Exception {
    private final String dataTargetName;

    public DataWriterException(String message, String dataTargetName) {
        super(message);
        this.dataTargetName = dataTargetName;
    }

    public String getDataTargetName() {
        return dataTargetName;
    }
}