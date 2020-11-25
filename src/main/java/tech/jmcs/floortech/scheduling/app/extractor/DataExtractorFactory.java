package tech.jmcs.floortech.scheduling.app.extractor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.util.ExcelCellAddress;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class DataExtractorFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DataExtractorFactory.class);

    public static BeamListExtractor openExcelFileAsBeamList(Path excelFile) {
        try {
            return new BeamListExtractor(excelFile);
        } catch (IOException e) {
            LOG.debug("IOException: {}", e.getMessage());
            // rethrow
            return null;
        }
    }

    public static TrussListExtractor openExcelFileAsTrussList(Path excelFile) {
        try {
            return new TrussListExtractor(excelFile);
        } catch (IOException e) {
            LOG.debug("IOException: {}", e.getMessage());
            // rethrow
            return null;
        }
    }

    public static TrussListExtractor2 openExcelFileAsTrussList2(Path excelFile) {
        try {
            return new TrussListExtractor2(excelFile);
        } catch (IOException e) {
            LOG.debug("IOException: {}", e.getMessage());
            // rethrow
            return null;
        }
    }

    public static GenericExcelHorizontalTableDataExtractor openExcelFileAsGenericList(Path excelFile, Map<ExcelCellAddress, String> tableLayout, Map<Integer, String> columnMap, List<Integer> validRowDataList) {
        try {
            return new GenericExcelHorizontalTableDataExtractor(excelFile, tableLayout, columnMap, validRowDataList);
        } catch (IOException e) {
            LOG.debug("IOException: {}", e.getMessage());
            // rethrow
            return null;
        }
    }

    public static GenericExcelHorizontalTableDataExtractor openExcelFileAsGenericList(Path excelFile, Map<ExcelCellAddress, String> tableLayout, Map<Integer, String> columnMap, List<Integer> validRowDataList, Integer sheetNumber) {
        try {
            return new GenericExcelHorizontalTableDataExtractor(excelFile, tableLayout, columnMap, validRowDataList, sheetNumber);
        } catch (IOException e) {
            LOG.debug("IOException: {}", e.getMessage());
            // rethrow
            return null;
        }
    }


    public static SlabListExtractor openPdfAsSlabList(Path pdfPath) {
        try {
            return new SlabListExtractor(pdfPath);
        } catch (IOException e) {
            LOG.debug("IOException: {}", e.getMessage());
            // rethrow
            return null;
        }
    }
}
