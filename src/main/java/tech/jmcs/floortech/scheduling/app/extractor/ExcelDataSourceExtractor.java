package tech.jmcs.floortech.scheduling.app.extractor;

import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.util.XLSUtility;

import java.io.IOException;
import java.nio.file.Path;

public abstract class ExcelDataSourceExtractor<T> extends FileDataSourceExtractor<T> {
    private static final Logger LOG = LoggerFactory.getLogger(ExcelDataSourceExtractor.class);

    private final XLSUtility xls;

    protected Sheet targetSheet;

    protected Integer targetSheetNumber;

    protected ExcelDataSourceExtractor(Path excelFile) throws IOException {
        super(excelFile);

        xls = new XLSUtility(excelFile);
        this.targetSheetNumber = 0;
    }

    protected Sheet getTargetSheet() {
        if (targetSheet == null) {
            try {
                if (targetSheetNumber == null || targetSheetNumber < 0) {
                    this.targetSheetNumber = 0;
                }
                targetSheet = this.xls.getSheet(this.targetSheetNumber);
            } catch (Exception e) {
                // TODO: Handle
            }
        }
        return this.targetSheet;
    }

    protected void setTargetSheetNumber(Integer n) {
        this.targetSheetNumber = n;
    }

}
