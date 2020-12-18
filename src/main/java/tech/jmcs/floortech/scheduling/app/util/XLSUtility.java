package tech.jmcs.floortech.scheduling.app.util;

import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class XLSUtility {
    private static final Logger LOG = LoggerFactory.getLogger(XLSUtility.class);

    private final Path excelFile;

//    private FileInputStream fileInStream;
    private Workbook workbook;
    private Iterator<Sheet> sheetIterator;

    public XLSUtility(Path excelFile) throws FileNotFoundException {
        if (excelFile == null) {
            throw new FileNotFoundException("Path is empty");
        }
        if (!this.fileExists(excelFile)) {
            throw new FileNotFoundException("Can't find file: " + excelFile.toString());
        }
        this.excelFile = excelFile;
    }

    public Boolean isOpen() {
        if (this.workbook == null) return false;

        Iterator<Sheet> it = this.workbook.sheetIterator();
        if (it == null) return false;

        return true;
    }

    public Workbook getWorkbook() {
        return this.workbook;
    }

    /**
     * Loads XL file if needed, read sheet number requested, close excel file, and returns Sheet.
     * @return
     */
    public Sheet getSheet(int sheetNumber, boolean keepOpen) throws Exception {

        Sheet tSheet;

        // Open workbook if required
        if (this.workbook == null) {
            try (FileInputStream fis = new FileInputStream(this.excelFile.toFile()) ){
                this.workbook = WorkbookFactory.create(fis);
            } catch (FileNotFoundException e) {
                // ignore, already checked
                return null;
            } catch (IOException e) {
                throw e; // rethrow excel exception
            }
        }

        try {
            tSheet = this.workbook.getSheetAt(sheetNumber);
        } catch (IllegalArgumentException aex) {
            throw new Exception("Sheet number out of range, does not exist");
        }

        if (!keepOpen) {
            this.workbook.close();
            this.workbook = null;
        }

        return tSheet;
    }

    /**
     * Overload for getSheet(int sheetNumber, boolean keepOpen)
     * @param sheetNumber
     * @return
     * @throws Exception
     */
    public Sheet getSheet(int sheetNumber) throws Exception {
        return this.getSheet(sheetNumber, false);
    }

    /**
     * Get All Sheets that match or contain the search string. (Mapped by sheet index)
     * @param search
     * @param keepOpen
     * @return an HashMap, empty or populated. Key is sheet index
     */
    public Map<Integer, Sheet> getSheetsMatchingName(String search, boolean caseInsensitive, boolean ignoreSpecialCharacters, boolean keepOpen) {
        Map<Integer, Sheet> tSheets = new HashMap<>();

        LOG.warn("getSheetsMatchingName :: NOT YET IMPLEMENTED!!!");

        return null;
    }

    /**
     * Overload for getSheetsMatchingName(String search, boolean caseInsensitive, boolean keepOpen)
     * @param name
     * @param caseInsensitive
     * @return
     */
    public Map<Integer, Sheet> getSheetsMatchingName(String name, boolean caseInsensitive, boolean ignoreSpecialCharacters) {
        return this.getSheetsMatchingName(name, caseInsensitive, ignoreSpecialCharacters, false);
    }

    /**
     * Updates workbook: Workbook must be already open.  Workbook closed at end.
     */
    public void saveAndClose() throws FileNotFoundException, IOException {
        if (this.workbook != null) {
            try (FileOutputStream fos = new FileOutputStream(this.excelFile.toFile())) {
                this.workbook.write(fos);
                this.closeFile();
            } catch (FileNotFoundException e) {
                LOG.debug("Could not find workbook: {}", e.getMessage());
                throw e;
            } catch (IOException e) {
                throw e;
            }
        } else {
            LOG.debug("updateWorkbookAndClose(): Workbook was already closed");
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

    private boolean fileExists(Path excelFile) {
        File f = excelFile.toFile();
        return f.exists();
    }

    @Deprecated
    public void loadFile() throws IOException {

        // TODO: Use a FileInputStream
        // TODO: CloseFile() method replaced by opening the Workbook, getting the Sheet iterator and closing
        //  the Workbook immediately.

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

    @Deprecated
    public Sheet getSheetByNumber(Integer num) {
        Sheet sheet = workbook.getSheetAt(num);
        return sheet;
    }

    @Deprecated
    public Sheet getNextSheet() {
        if (sheetIterator.hasNext()) {
            return sheetIterator.next();
        } else {
            return null;
        }
    }

}
