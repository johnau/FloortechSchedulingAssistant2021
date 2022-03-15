package tech.jmcs.floortech.scheduling.ui.customextractorwizard;

import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.tablelayout.TableLayoutPresenter;

import java.util.List;

public class ColumnStringConverter extends StringConverter<ColumnValidationData> {
    protected static final Logger LOG = LoggerFactory.getLogger(ColumnStringConverter.class);

    private CustomExtractorData customExtractorData;

    public ColumnStringConverter(CustomExtractorData customExtractorData) {
        this.customExtractorData = customExtractorData;
    }

    @Override
    public String toString(ColumnValidationData object) {
        if (object == null) return null;
        return object.getColumnTitle();
    }

    @Override
    public ColumnValidationData fromString(String string) {
        List<ColumnValidationData> colDesc = this.customExtractorData.getCurrent().getColumnDescriptors();
        if (colDesc == null || colDesc.isEmpty()) {
            return null;
        }

        ColumnValidationData col = colDesc.stream()
                .filter(c -> c.getColumnTitle().equals(string))
                .findFirst().orElse(null);
        return col;
    }
}
