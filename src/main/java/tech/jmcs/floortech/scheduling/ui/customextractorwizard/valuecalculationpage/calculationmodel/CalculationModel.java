package tech.jmcs.floortech.scheduling.ui.customextractorwizard.valuecalculationpage.calculationmodel;

import javafx.scene.Node;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.ColumnValidationData;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.CustomExtractorData;

import java.util.Map;

public abstract class CalculationModel {

    private CustomExtractorData customExtractorData;

    public CalculationModel(CustomExtractorData customExtractorData) {
        this.customExtractorData = customExtractorData;
    }

    public abstract void load();

    public abstract Map<Integer, Node> getModelLayout();

//    public abstract Map<Integer, ColumnValidationData> getColumns();

    public abstract Map<Integer, Object> getComponents();

    public abstract void unload();
}
