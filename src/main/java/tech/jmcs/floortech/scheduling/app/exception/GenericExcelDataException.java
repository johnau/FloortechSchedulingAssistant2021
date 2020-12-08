package tech.jmcs.floortech.scheduling.app.exception;

public class GenericExcelDataException extends DataExtractorException {

    public GenericExcelDataException(String message) {
        super(message, "Generic");
    }

}
