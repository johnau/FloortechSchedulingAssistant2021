package tech.jmcs.floortech.scheduling.app.extractor;

import java.io.IOException;
import java.nio.file.Path;

public class DataExtractorFactory {

    public static BeamListExtractor openExcelFileAsBeamList(Path excelFile) {
        try {
            return new BeamListExtractor(excelFile);
        } catch (IOException e) {
            // log
            // rethrow
            return null;
        }
    }

    public static TrussListExtractor openExcelFileAsTrussList(Path excelFile) {
        try {
            return new TrussListExtractor(excelFile);
        } catch (IOException e) {
            // log
            // rethrow
            return null;
        }
    }

    public static TrussListExtractor2 openExcelFileAsTrussList2(Path excelFile) {
        try {
            return new TrussListExtractor2(excelFile);
        } catch (IOException e) {
            // log
            // rethrow
            return null;
        }
    }


}
