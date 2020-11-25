package tech.jmcs.floortech.scheduling.app.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

class PDFUtilitiesTest {

    @Test
    public void testReadPdfAsText() {
        Path pdfPath = Paths.get("D:\\appdev\\floortech_env\\test_data\\example jobs\\19383\\19383.pdf");
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
        Path pdfPath = Paths.get("D:\\appdev\\floortech_env\\test_data\\example jobs\\ForTestingSchedulingApp.pdf");
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