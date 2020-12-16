package tech.jmcs.floortech.scheduling.app.datasource.extractor;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.util.ExcelCellAddress;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DataExtractorFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DataExtractorFactory.class);

    public static BeamListExtractor openExcelFileAsBeamList(Path excelFile) {
        try {
            LOG.debug("Creating beam list extractor for : {}", excelFile);
            return new BeamListExtractor(excelFile);
        } catch (IOException e) {
            LOG.debug("IOException: {}", e.getMessage());
            // rethrow
            return null;
        }
    }

    public static TrussListExtractor openExcelFileAsTrussList(Path excelFile) {
        try {
            LOG.debug("Creating truss list extractor for : {}", excelFile);
            return new TrussListExtractor(excelFile);
        } catch (IOException e) {
            LOG.debug("IOException: {}", e.getMessage());
            // rethrow
            return null;
        }
    }

    public static TrussListExtractor2 openExcelFileAsTrussList2(Path excelFile) {
        try {
            LOG.debug("Creating truss list 2 extractor for : {}", excelFile);
            return new TrussListExtractor2(excelFile);
        } catch (IOException e) {
            LOG.debug("IOException: {}", e.getMessage());
            // rethrow
            return null;
        }
    }

    public static GenericExcelHorizontalTableDataExtractor openExcelFileAsGenericList(Path excelFile,
                                                                                      Map<ExcelCellAddress, String> tableLayout,
                                                                                      Map<Integer, GenericExtractorColumnDescription> columnMap,
                                                                                      List<Integer> validRowDataList) {
        try {
            LOG.debug("Creating Generic extractor for : {}", excelFile);
            return new GenericExcelHorizontalTableDataExtractor(excelFile, tableLayout, columnMap, validRowDataList);
        } catch (IOException e) {
            LOG.debug("IOException: {}", e.getMessage());
            // rethrow
            return null;
        }
    }

    public static GenericExcelHorizontalTableDataExtractor openExcelFileAsGenericList(Path excelPath,
                                                                                      Map<ExcelCellAddress, String> tableLayout,
                                                                                      Map<Integer, GenericExtractorColumnDescription> columnMap,
                                                                                      List<Integer> validRowDataList,
                                                                                      List<Function<Sheet, String>> tableValidationFunctions,
                                                                                      List<Function<Row, String>> recordValidationFunctions) {
        GenericExcelHorizontalTableDataExtractor extractor = DataExtractorFactory.openExcelFileAsGenericList(excelPath, tableLayout, columnMap, validRowDataList);
        if (extractor == null) {
            System.out.println("Could not create generic extractor");
            return null;
        }

        if (recordValidationFunctions != null) {
            recordValidationFunctions.forEach(f -> extractor.addAdditionalRecordValidation(f));
        }

        if (tableValidationFunctions != null) {
            tableValidationFunctions.forEach(f -> extractor.addAdditionalTableValidation(f));
        }

        return extractor;
    }

    public static GenericExcelHorizontalTableDataExtractor openExcelFileAsGenericList(Path excelFile,
                                                                                      Map<ExcelCellAddress, String> tableLayout,
                                                                                      Map<Integer, GenericExtractorColumnDescription> columnMap,
                                                                                      List<Integer> validRowDataList,
                                                                                      Integer sheetNumber) {
        try {
            LOG.debug("Creating Generic extractor for : {}", excelFile);
            return new GenericExcelHorizontalTableDataExtractor(excelFile, tableLayout, columnMap, validRowDataList, sheetNumber);
        } catch (IOException e) {
            LOG.debug("IOException: {}", e.getMessage());
            // rethrow
            return null;
        }
    }


    public static SlabListExtractor openPdfAsSlabList(Path pdfPath) {
        try {
            LOG.debug("Creating Slab pdf extractor for : {}", pdfPath);
            return new SlabListExtractor(pdfPath);
        } catch (IOException e) {
            LOG.debug("IOException: {}", e.getMessage());
            // rethrow
            return null;
        }
    }


}
