package tech.jmcs.floortech.scheduling.app.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

class PDFUtilitiesTest {

    @Test
    public void testReadPdfAsText() {
        Path resourceDirectory = Paths.get("src","test","resources");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        Path pdfPath = Paths.get(absolutePath, "19383", "19383.pdf");
        String t = null;
        try {
            t = PDFUtilities.readPdfAsText(pdfPath);
        } catch (IOException e) {
            System.out.println("Could not read pdf file: " + pdfPath.toString());
            return;
        }
        System.out.println(t);
    }

    @Test
    public void testReadPdfAsText2() {
        Path resourceDirectory = Paths.get("src","test","resources");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        Path pdfPath = Paths.get(absolutePath, "TestJob.pdf");
        String t = null;
        try {
            t = PDFUtilities.readPdfAsText(pdfPath);
        } catch (IOException e) {
            System.out.println("Could not read pdf file: " + pdfPath.toString());
            return;
        }
        System.out.println(t);
    }
}