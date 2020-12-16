package tech.jmcs.floortech.scheduling.app.datasource.extractor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Support for the newly added 'Truss Packing Group' column
 */
public class TrussListExtractor2 extends TrussListExtractor {
    private static final Logger LOG = LoggerFactory.getLogger(TrussListExtractor2.class);

    protected static final String COL_L_TITLE = "Truss Packing Group";

    protected TrussListExtractor2(Path excelFile) throws IOException {
        super(excelFile);

        this.columnNames.put(11, COL_L_TITLE);
    }

    @Override
    public void extract() {

    }

}
