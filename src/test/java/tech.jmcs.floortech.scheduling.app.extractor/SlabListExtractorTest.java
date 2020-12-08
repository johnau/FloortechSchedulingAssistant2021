package tech.jmcs.floortech.scheduling.app.extractor;

import org.junit.jupiter.api.Test;
import tech.jmcs.floortech.scheduling.app.exception.DataExtractorException;
import tech.jmcs.floortech.scheduling.app.extractor.model.ExtractedTableData;
import tech.jmcs.floortech.scheduling.app.extractor.model.SlabData;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SlabListExtractorTest {

    @Test
    void testValidationSlabData() {
        Path pdfPath = Paths.get("D:\\appdev\\floortech_env\\test_data\\example jobs\\19383\\19383.pdf");
        SlabListExtractor extractor = DataExtractorFactory.openPdfAsSlabList(pdfPath);
        if (extractor == null) {
            System.out.println("Could not create extractor, is the file in open in excel? does it exist?");
            return;
        }

        Boolean valid = extractor.isValid();
        System.out.printf("File is valid: %s \n", valid.toString());
    }

    @Test
    void testVExtractSlabData() {
        Path pdfPath = Paths.get("D:\\appdev\\floortech_env\\test_data\\example jobs\\19383\\19383.pdf");
        Path pdfPath1 = Paths.get("D:\\appdev\\floortech_env\\test_data\\example jobs\\ForTestingSchedulingApp.pdf");

        extractPdfSlabData(pdfPath);
        extractPdfSlabData(pdfPath1);
    }

    private void extractPdfSlabData(Path pdfPath) {
        SlabListExtractor extractor = DataExtractorFactory.openPdfAsSlabList(pdfPath);
        if (extractor == null) {
            System.out.println("Could not create extractor, is the file in open in excel? does it exist?");
            fail();
        }

        try {
            extractor.extract();
        } catch (DataExtractorException e) {
            System.out.println("ERROR!");
            fail();
        }

        ExtractedTableData<SlabData> data = extractor.getData();
        Map<Long, SlabData> dataMap = data.getData();

        dataMap.forEach( (id, sd) -> {

            Double fa = sd.getFloorArea();
            Double br2 = sd.getBalcony2cRhsArea();
            Double br3 = sd.getBalcony3cRhsArea();
            Double bi2 = sd.getBalcony2cInsituArea();
            Double bi3 = sd.getBalcony3cInsituArea();
            Double bi4 = sd.getBalcony4cInsituArea();
            Double ka = sd.getThickAngle();
            Double na = sd.getThinAngle();

            System.out.printf("Floor Area: %s \n" +
                    "| Balcony 2c Rhs: %s \n" +
                    "| Balcony 3c Rhs: %s \n" +
                    "| Balcony 2c Insitu: %s \n" +
                    "| Balcony 3c Insitu: %s \n" +
                    "| Balcony 4c Insitu: %s \n" +
                    "| Thick Angle: %s \n" +
                    "| Thin Angle: %s \n",
                    fa, br2, br3, bi2, bi3, bi4, ka, na);
        });
    }
}