package tech.jmcs.floortech.scheduling.ui.customextractorwizard.finishpage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.datasource.extractor.*;
import tech.jmcs.floortech.scheduling.app.util.ExcelCellAddress;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.CustomExtractorData;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.StringPosition;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.TriggeredFXMLView;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.recordvalidation.ColumnDataType;
import tech.jmcs.floortech.scheduling.ui.customextractorwizard.ColumnValidationData;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class FinishPagePresenter implements Initializable, TriggeredFXMLView {
    protected static final Logger LOG = LoggerFactory.getLogger(FinishPagePresenter.class);

    @Inject private CustomExtractorData customExtractorData;

    @FXML private VBox resultsContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.debug("Instance id for CustomExtractorData: " + this.customExtractorData.hashCode());
    }

    @FXML public void handleTestCustomExtractor(ActionEvent event) {
        String filenamePattern = generateFilenamePattern();

        File testFile = chooseTestFile(filenamePattern);
        if (testFile == null) {
            showFileNotChosenDialog();
            return;
        }

        Map<ExcelCellAddress, String> tableLayout = generateTableLayoutMap();
        Map<Integer, GenericExtractorColumnDescription> columnMap = generateColumnMap();
//        List<Integer> requiredRecordDataColumns = this.customExtractorData.getCurrent().getRequiredRecordDataColumns();


//        GenericExcelHorizontalTableDataExtractor extractor = DataExtractorFactory.openExcelFileAsGenericList(
//                testFile.toPath(), tableLayout, columnMap, requiredRecordDataColumns, null, null);
    }

    private Map<ExcelCellAddress, String> generateTableLayoutMap() {
        Map<ExcelCellAddress, String> tableLayout = new HashMap<>();

        String tableTitle = this.customExtractorData.getCurrent().getTableTitle();
        Integer tableTitleRow = this.customExtractorData.getCurrent().getTableTitleRow();
        Integer columnHeadersRow = this.customExtractorData.getCurrent().getColumnHeadersRow();
        Integer firstDataRow = this.customExtractorData.getCurrent().getFirstDataRow();
        List<ColumnValidationData> colDescriptors = this.customExtractorData.getCurrent().getColumnDescriptors();


        tableLayout.put(new ExcelCellAddress(0, firstDataRow), ExcelDataSourceExtractor.DATA_START);
        tableLayout.put(new ExcelCellAddress(0, tableTitleRow), tableTitle);

        colDescriptors.forEach(colDescriptor -> {
            Integer pos = colDescriptor.getColumnPosition();
            String title = colDescriptor.getColumnTitle();
            tableLayout.put(new ExcelCellAddress(pos, columnHeadersRow), title);
        });

        return tableLayout;
    }

    private Map<Integer, GenericExtractorColumnDescription> generateColumnMap() {
        Map<Integer, GenericExtractorColumnDescription> columnMap = new HashMap<>();
        List<ColumnValidationData> colDescriptors = this.customExtractorData.getCurrent().getColumnDescriptors();

        int i = 0;
        for (ColumnValidationData colDescriptor : colDescriptors) {
            Integer pos = colDescriptor.getColumnPosition();
            String title = colDescriptor.getColumnTitle();
            String dataType = colDescriptor.getDataType();
            Boolean empty = colDescriptor.getEmpty();

            ColumnDataType eDataType = ColumnDataType.valueOf(dataType);

            GenericExtractorColumnDataType geDataType = null;
            switch (eDataType) {
                case TEXT:
                    geDataType = GenericExtractorColumnDataType.TEXT;
                case NUMBER:
                    geDataType = GenericExtractorColumnDataType.NUMERIC;
                case TRUE_FALSE:
                    geDataType = GenericExtractorColumnDataType.BOOLEAN;
            }

            GenericExtractorColumnDescription col = new GenericExtractorColumnDescription(
                    title, pos, geDataType);

            columnMap.put(i++, col);
        }

        return columnMap;
    }

    private void showFileNotChosenDialog() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Abort test");
        a.setHeaderText("Aborting test...");
        a.setContentText("An excel file was not selected, the test can not continue.");
        a.showAndWait();
    }

    private String generateFilenamePattern() {
        String filenamePattern = customExtractorData.getCurrent().getDataSourceFileNamePattern();
        StringPosition patternPosition = customExtractorData.getCurrent().getDataSourceFileNamePatternPos();
        switch (patternPosition) {
            case STARTS_WTIH:
                return filenamePattern + "*";
            case CONTAINS:
                return "*" + filenamePattern + "*";
            case ENDS_WITH:
                return "*" + filenamePattern;
            default:
                return "";
        }
    }

    private File chooseTestFile(String filenamePattern) {
        FileChooser fc = new FileChooser();
        fc.setInitialFileName(filenamePattern);
        fc.setTitle("Choose test Excel file");
        FileChooser.ExtensionFilter af = new FileChooser.ExtensionFilter("All files (*.*)", "*.*");
        FileChooser.ExtensionFilter ef = new FileChooser.ExtensionFilter("Excel files (*.xls,*.xlsx)", "*.xls;*.xlsx");
        fc.setSelectedExtensionFilter(ef);

        File result = fc.showOpenDialog(resultsContainer.getScene().getWindow());
        return result;
    }

    @Override
    public void trigger() {
        LOG.debug("No data to commit from the start page");
    }

    @Override
    public void reset() {

    }

    @Override
    public void update() {

    }
}
