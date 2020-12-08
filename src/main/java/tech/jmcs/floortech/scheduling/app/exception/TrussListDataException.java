package tech.jmcs.floortech.scheduling.app.exception;

public class TrussListDataException extends DataExtractorException {

    public TrussListDataException(String message) {
        super(message, "TRUSS LISTING");
    }

}
