package tech.jmcs.floortech.scheduling.ui.customextractorwizard;

/**
 * Data model for custom extractor data used to create a new custom extractor
 */
public class CustomExtractorData {

    private CustomExtractor current;

    public CustomExtractorData() {
        current = new CustomExtractor();
    }

    public CustomExtractor getCurrent() {
        return current;
    }

    public void finalize() {
        this.current = new CustomExtractor();
    }

}
