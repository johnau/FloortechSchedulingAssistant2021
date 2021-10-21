package tech.jmcs.floortech.scheduling.app.exception;

public class DataExtractorException extends Exception {
    private final String dataSourceName;

    public DataExtractorException(String message, String dataSourceName) {
        super(message);
        this.dataSourceName = dataSourceName;
        System.out.println(String.format("Error! '%s' data extraction failed: '%s'", dataSourceName, message));
    }

    public String getDataSourceName() {
        return dataSourceName;
    }
}
