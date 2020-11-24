package tech.jmcs.floortech.scheduling.app.extractor;

import org.apache.poi.ss.usermodel.Sheet;
import tech.jmcs.floortech.scheduling.app.util.XLSUtility;

import java.nio.file.Path;

public abstract class FileDataSourceExtractorBase<T> implements DataSourceExtractor<T> {

    private final Path sourceFilePath;
    protected ExtractedDataObject<T> dataObject;

    protected FileDataSourceExtractorBase(Path sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }

    public Path getSourceFilePath() {
        return sourceFilePath;
    }

}
