package tech.jmcs.floortech.scheduling.app.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class PDFUtilities {
    private static final Logger LOG = LoggerFactory.getLogger(PDFUtilities.class);

    public static String readPdfAsText(Path pdfPath) throws IOException {

        String pdfText = null;
        try (PDDocument doc = PDDocument.load(pdfPath.toFile()))
        {
            pdfText = new PDFTextStripper().getText(doc);
        } catch (IOException e) {
            LOG.debug("Could not open pdf document");
            throw e;
        }

        return pdfText;

    }
}
