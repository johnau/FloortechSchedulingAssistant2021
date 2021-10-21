package tech.jmcs.floortech.scheduling.ui;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.datasource.extractor.*;
import tech.jmcs.floortech.scheduling.app.datasource.model.BeamData;
import tech.jmcs.floortech.scheduling.app.datasource.model.ExtractedTableData;
import tech.jmcs.floortech.scheduling.app.datasource.model.SlabData;
import tech.jmcs.floortech.scheduling.app.datasource.model.TrussData;
import tech.jmcs.floortech.scheduling.app.exception.DataExtractorException;
import tech.jmcs.floortech.scheduling.app.types.DataSourceExtractorType;
import tech.jmcs.floortech.scheduling.app.util.ExcelCellAddress;
import tech.jmcs.floortech.scheduling.app.util.XLSHelper;

import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

/**
 * Class to handle extraction of data sources
 *
 * Also handles the FX Components related to extraction
 *
 * Data is extracted into the extractedDataHolder
 *
 */
public class ExtractorManagerFX {
    protected static final Logger LOG = LoggerFactory.getLogger(ExtractorManagerFX.class);

    @Inject private ExtractorComponentHolderFX extractorComponentHolderFX;
    @Inject private ExtractedDataHolderFX extractedDataHolderFX;

    public ExtractorManagerFX() {

    }

    public void processActiveExtractors() throws DataExtractorException {
        clearExtractedData();
        for (Map.Entry<String, DataExtractorDescriptorFX> entry : this.extractorComponentHolderFX.getActiveExtractors().entrySet()) {
            String name = entry.getKey();

            DataExtractorDescriptorFX descriptor = entry.getValue();
            DataSourceExtractorType type = descriptor.getType();
            String name2 = descriptor.getName();
            String filePathText = descriptor.getFilePathText();
            if (type.equals(DataSourceExtractorType.BEAM)
                    || type.equals(DataSourceExtractorType.SHEET)
                    || type.equals(DataSourceExtractorType.SLAB)
                    || type.equals(DataSourceExtractorType.TRUSS_COLDWRIGHT)
                    || type.equals(DataSourceExtractorType.TRUSS_HOPLEY)) {
                boolean success = this.processBuiltInDataSourceExtraction(name2, type, filePathText);
                if (success) {
                    LOG.debug("Successfully extracted: {}", name2);
                } else {
                    LOG.warn("Failed to extract: {}", name2);
                }
            } else if (type.equals(DataSourceExtractorType.GENERIC_SIMPLE)) {
                // process generic simple data sources
                LOG.debug("Processing of generic data sources not yet implemented");
            }
        }
    }

    public void clearExtractedData() {
        if (this.extractedDataHolderFX != null) {
            this.extractedDataHolderFX.clear();
        }
    }

    private boolean processBuiltInDataSourceExtraction(String name, DataSourceExtractorType type, String filePath) throws DataExtractorException {
        switch (type) {
            case BEAM:
                return beamExtract(name, type, filePath);
            case SHEET:
                return sheetExtract(name, type, filePath);
            case SLAB:
                return slabExtract(name, type, filePath);
            case TRUSS_COLDWRIGHT:
            case TRUSS_HOPLEY:
                return trussExtract(name, type, filePath);
            default:
                return false;
        }
    }

    private boolean isEmpty(String path, DataSourceExtractorType type) {
        if (path == null || path.isEmpty()) {
            LOG.debug("No file to extract");
            System.out.println(String.format("No '%s' %s data provided", type.getName(), type.getFileType()));
            return true;
        }
        System.out.println(String.format("Processing '%s' %s data...", type.getName(), type.getFileType()));
        return false;
    }

    private boolean beamExtract(String name, DataSourceExtractorType type, String filePath) throws DataExtractorException {
        LOG.debug("Beam extractor: {} {} {}", name, type, filePath);
        if (isEmpty(filePath, type)) return false;

        Path excelFile = Paths.get(filePath);
        BeamListExtractor extractor = DataExtractorFactory.openExcelFileAsBeamList(excelFile);
        if (extractor == null) {
            LOG.warn("Could not create extractor, is the file in open in excel? does it exist?");
            return false;
        }

        try {
            extractor.extract();
        } catch (DataExtractorException e) {
            LOG.warn("Data Extractor Exception thrown: [%s] %s \n",  e.getDataSourceName(), e.getMessage());

            System.out.println(String.format("Error! '%s' data extraction failed: '%s'", e.getDataSourceName(), e.getMessage()));

            throw e;
        }

        ExtractedTableData<BeamData> data = extractor.getData();
        if (data == null) {
            LOG.warn("Extraction was not be completed");
            return false;
        }

        Map<Long, BeamData> dataMap = data.getData();
        this.extractedDataHolderFX.addBeamData(dataMap);

        LOG.info("Extracted Beam Data with Built In Beam Extractor");

        System.out.println(String.format("Found %s Beam items", dataMap.size()));

        return true;
    }

    private boolean sheetExtract(String name, DataSourceExtractorType type, String filePath) throws DataExtractorException {
        if (isEmpty(filePath, type)) return false;

        LOG.debug("Sheet extractor: {} {} {}", name, type, filePath);
        Path excelPath = Paths.get(filePath);
        // using Generic Extractor for now

        String lenColName = "Length";
        String idColName = "ID";
        String qtyColName = "Qty";

        /**
         * START of Setting up for use of Generic Extractor
         */
        Map<ExcelCellAddress, String> tableLayout = new HashMap<>();
        tableLayout.put(new ExcelCellAddress(0, 2), ExcelDataSourceExtractor.DATA_START);
        tableLayout.put(new ExcelCellAddress(0, 0), "Sheets");
        tableLayout.put(new ExcelCellAddress(0, 1), lenColName);
        tableLayout.put(new ExcelCellAddress(1, 1), idColName);
        tableLayout.put(new ExcelCellAddress(2, 1), qtyColName);

        Map<Integer, GenericExtractorColumnDescription> columnMap = new HashMap<>();
        GenericExtractorColumnDescription lenCol = new GenericExtractorColumnDescription(lenColName, 0, GenericExtractorColumnDataType.NUMERIC);
        GenericExtractorColumnDescription idCol = new GenericExtractorColumnDescription(idColName, 1, GenericExtractorColumnDataType.TEXT);
        GenericExtractorColumnDescription qtyCol = new GenericExtractorColumnDescription(qtyColName, 2, GenericExtractorColumnDataType.NUMERIC);

        columnMap.put(0, lenCol);
        columnMap.put(1, idCol);
        columnMap.put(2, qtyCol);

        List<Integer> validRowDataList = Arrays.asList(1, 2);

        List<Function<Row, String>> recordValidationFunctions = new ArrayList();
        Function<Row, String> recordValidator = (row) -> {
            Cell idCell = XLSHelper.getCellByColumnIndex(row, 1);
            if (idCell == null) return "The ID Cell did not exist";
            if (idCell.getCellType().equals(CellType.STRING)) {
                String cVal = idCell.getStringCellValue().toLowerCase();
                if (cVal.contains("z")) {
                    return null;
                }
            }
            return "The ID Cell did not contain the letter 'Z'. Sheet ID's start with 'Z'";
        };
        recordValidationFunctions.add(recordValidator);
        /**
         * END of Setting up for use of Generic Extractor
         */

        GenericExcelHorizontalTableDataExtractor extractor = DataExtractorFactory.openExcelFileAsGenericList(excelPath, tableLayout, columnMap, validRowDataList, null, recordValidationFunctions);
        if (extractor == null) {
            LOG.warn("Could not create the extractor, did the file exist");
            return false;
        }
        try {
            extractor.extract();
        } catch (DataExtractorException e) {
            throw e;
        }

        ExtractedTableData<Map<String, Object>> data = extractor.getData();
        if (data == null) {
            System.out.println("Table data was null");
            return false;
        }
        Map<Long, Map<String, Object>> dataMap = data.getData();
        this.extractedDataHolderFX.addCustomData("Sheet", dataMap); // custom data always stored as Map<Long, Map<String, Object>>...

        LOG.info("Extracted Sheet Data with Built In (Hardcoded Generic) Sheet Extractor");

        System.out.println(String.format("Found %s Sheet items", dataMap.size()));

        return true;
    }

    private boolean slabExtract(String name, DataSourceExtractorType type, String filePath) throws DataExtractorException {
        if (isEmpty(filePath, type)) return false;

        LOG.debug("Slab extractor: {} {} {}", name, type, filePath);
        Path pdfPath = Paths.get(filePath);
        SlabListExtractor extractor = DataExtractorFactory.openPdfAsSlabList(pdfPath);
        if (extractor == null) {
            LOG.debug("Could not create extractor, is the file in open in excel? does it exist?");
            return false;
        }

        try {
            extractor.extract();
        } catch (DataExtractorException e) {
            LOG.debug("Could not complete extraction of slab data");
            throw e;
        }

        ExtractedTableData<SlabData> data = extractor.getData();
        if (data == null) {
            LOG.warn("Extraction was not be completed");
            return false;
        }

        Map<Long, SlabData> dataMap = data.getData();
        this.extractedDataHolderFX.addSlabData(dataMap);

        LOG.info("Extracted Slab Data with Built In Slab Extractor");

        System.out.println(String.format("Found %s Slab items", dataMap.size()));

        return true;
    }

    private boolean trussExtract(String name, DataSourceExtractorType type, String filePath) throws DataExtractorException {
        if (isEmpty(filePath, type)) return false;

        LOG.debug("Truss extractor: {} {} {}", name, type, filePath);

        Path excelFile = Paths.get(filePath);
        TrussListExtractor extractor = DataExtractorFactory.openExcelFileAsTrussList(excelFile);
        if (extractor == null) {
            LOG.debug("Could not create extractor, is the file in open in excel? does it exist?");
            return false;
        }

        try {
            extractor.extract();
        } catch (DataExtractorException e) {
            throw e;
        }

        ExtractedTableData<TrussData> data = extractor.getData();
        if (data == null) {
            LOG.warn("Extraction was not be completed");
            return false;
        }

        Map<Long, TrussData> dataMap = data.getData();
        this.extractedDataHolderFX.addTrussData(dataMap);

        LOG.info("Extracted Truss Data with Built In Truss Extractor");

        System.out.println(String.format("Found %s Truss items", dataMap.size()));

        return true;
    }

}
