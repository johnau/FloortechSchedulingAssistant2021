package tech.jmcs.floortech.scheduling.app.exception;

public class SlabDataException extends DataExtractorException {
    public SlabDataException(String message) {
        super(message, "SLAB DATA");
    }
}
