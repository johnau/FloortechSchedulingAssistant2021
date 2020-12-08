package tech.jmcs.floortech.scheduling.app.extractor;

import tech.jmcs.floortech.scheduling.app.extractor.model.ExtractedTableData;

import java.nio.file.Path;

public abstract class FileDataSourceExtractor<T> implements DataSourceExtractor<T> {

    private final Path sourceFilePath;
    protected ExtractedTableData<T> dataObject;

    protected FileDataSourceExtractor(Path sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }

    public Path getSourceFilePath() {
        return sourceFilePath;
    }

}