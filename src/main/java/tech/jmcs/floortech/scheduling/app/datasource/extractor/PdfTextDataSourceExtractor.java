package tech.jmcs.floortech.scheduling.app.datasource.extractor;

import tech.jmcs.floortech.scheduling.app.util.PDFUtilities;

import java.io.IOException;
import java.nio.file.Path;

public abstract class PdfTextDataSourceExtractor<T> extends FileDataSourceExtractor<T> {

    protected final String pdfText;

    protected PdfTextDataSourceExtractor(Path pdfPath) throws IOException {
        super(pdfPath);

        this.pdfText = PDFUtilities.readPdfAsText(pdfPath);
    }

}
