package tech.jmcs.floortech.scheduling.app.extractor;

import tech.jmcs.floortech.scheduling.app.extractor.DataExtractorException;

public class BeamListDataException extends DataExtractorException {

    public BeamListDataException(String message) {
        super(message, "BEAM LISTING");
    }

}
