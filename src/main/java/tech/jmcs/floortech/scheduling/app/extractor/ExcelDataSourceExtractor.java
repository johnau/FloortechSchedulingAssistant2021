package tech.jmcs.floortech.scheduling.app.extractor;

import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.util.XLSUtility;

import java.io.IOException;
import java.nio.file.Path;

public abstract class ExcelDataSourceExtractor<T> extends FileDataSourceExtractorBase<T> {
    private static final Logger LOG = LoggerFactory.getLogger(ExcelDataSourceExtractor.class);

    protected final XLSUtility xls;

    protected ExcelDataSourceExtractor(Path excelFile) throws IOException {
        super(excelFile);

        xls = new XLSUtility(excelFile);
        xls.loadFile();
    }

    public void closeFile() {
        try {
            xls.closeFile();
            LOG.debug("Closed excel file");
        } catch (IOException e) {
            // log
        }
    }

}
