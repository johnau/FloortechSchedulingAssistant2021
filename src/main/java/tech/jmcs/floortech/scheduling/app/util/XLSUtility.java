package tech.jmcs.floortech.scheduling.app.util;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

public class XLSUtility {

    private final Path excelFile;

    private Workbook workbook;
    private Iterator<Sheet> sheetIterator;

    public XLSUtility(Path excelFile) {
        if (excelFile == null) {
            // log
        }
        this.excelFile = excelFile;
    }

    public Boolean isOpen() {
        if (this.workbook == null) return false;

        Iterator<Sheet> it = this.workbook.sheetIterator();
        if (it == null) return false;

        return true;
    }

    public void loadFile() throws IOException {

        File f = excelFile.toFile();
        if (!f.exists()) {
            throw new FileNotFoundException(excelFile.toString());
        }

        try {
            workbook = WorkbookFactory.create(f);
            sheetIterator = workbook.sheetIterator();

        } catch (IOException e) {
            throw e;
        }

    }

    public Sheet getSheetByNumber(Integer num) {
        Sheet sheet = workbook.getSheetAt(num);
        return sheet;
    }

    public Sheet getNextSheet() {
        if (sheetIterator.hasNext()) {
            return sheetIterator.next();
        } else {
            return null;
        }
    }

    public void closeFile() throws IOException {
        if (this.workbook != null) {
            try {
                this.workbook.close();
                this.workbook = null;
            } catch (IOException e) {
                throw e;
            }
        }
    }

}
