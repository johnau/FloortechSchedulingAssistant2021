package tech.jmcs.floortech.scheduling.ui.customextractorwizard;

public interface TriggeredFXMLView {

    /**
     * The trigger method is called when then next button is clicked, triggering a save
     * on the current page data
     */
    public void trigger() throws Exception;

    /**
     * The reset method is called when the finish button is triggered
     */
    public void reset();

    /**
     * The update method is called when a page is loaded (after clicking the next button)
     */
    public void update();
}
