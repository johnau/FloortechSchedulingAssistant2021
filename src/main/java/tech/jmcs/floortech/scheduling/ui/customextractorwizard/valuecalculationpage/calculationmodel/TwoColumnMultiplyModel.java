package tech.jmcs.floortech.scheduling.ui.customextractorwizard.valuecalculationpage.calculationmodel;

import javafx.scene.Node;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.CustomExtractorData;

import java.util.Map;

/**
 * This model supports multiplying two columns together and adding up the values
 * This is targeted at Quantity x Length structured tables
 */
public class TwoColumnMultiplyModel extends CalculationModel {

    public TwoColumnMultiplyModel(CustomExtractorData customExtractorData) {
        super(customExtractorData);
    }

    @Override
    public void load() {

    }

    @Override
    public Map<Integer, Node> getModelLayout() {
        // choice box (columns)
        // choice box (multiplication)
        // choice box (columns)
        return null;
    }

    @Override
    public Map<Integer, Object> getComponents() {
        return null;
    }

    @Override
    public void unload() {

    }

}
