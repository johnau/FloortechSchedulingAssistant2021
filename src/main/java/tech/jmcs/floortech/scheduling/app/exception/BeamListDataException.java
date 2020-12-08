package tech.jmcs.floortech.scheduling.app.exception;

public class BeamListDataException extends DataExtractorException {

    public BeamListDataException(String message) {
        super(message, "BEAM LISTING");
    }

}
