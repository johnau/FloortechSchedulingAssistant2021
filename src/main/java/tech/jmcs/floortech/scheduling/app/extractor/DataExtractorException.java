package tech.jmcs.floortech.scheduling.app.extractor;

public class DataExtractorException extends Exception {
    private final String dataSourceName;

    public DataExtractorException(String message, String dataSourceName) {
        super(message);
        this.dataSourceName = dataSourceName;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }
}
