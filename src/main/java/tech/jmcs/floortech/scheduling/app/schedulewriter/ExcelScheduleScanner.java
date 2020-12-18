package tech.jmcs.floortech.scheduling.app.schedulewriter;

import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jmcs.floortech.scheduling.app.settings.DefaultScheduleSections;
import tech.jmcs.floortech.scheduling.app.settings.SettingsHolder;
import tech.jmcs.floortech.scheduling.app.util.ExcelCellAddress;
import tech.jmcs.floortech.scheduling.app.util.ExcelCellRange;
import tech.jmcs.floortech.scheduling.app.util.XLSHelper;
import tech.jmcs.floortech.scheduling.app.util.XLSUtility;
import tech.jmcs.floortech.scheduling.ui.settings.SettingsPresenter;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.*;

/**
 * Class to process Excel Schedule to detect data structures + data contained
 */
public class ExcelScheduleScanner {
    protected static final Logger LOG = LoggerFactory.getLogger(ExcelScheduleScanner.class);

    private SettingsHolder settingsHolder;
    private Path scheduleFilePath;
    private final XLSUtility xls;

    public ExcelScheduleScanner(Path excelPath, SettingsHolder settingsHolder) throws FileNotFoundException {
        LOG.info("ExcelScheduleScanner constructing...");
        this.settingsHolder = settingsHolder;
        this.scheduleFilePath = excelPath;
        try {
            this.xls = new XLSUtility(excelPath);
        } catch (FileNotFoundException e) {
            LOG.warn("File not found: {}", excelPath);
            throw e;
        }
    }

    /**
     * Checks excel schedule for all items in check list and returns any not found
     * @param checkList
     * @return List of not matched / not found
     */
    public List<String> checkScheduleContainsAll(List<String> checkList) {
        List<String> notFound = new ArrayList<>();

//        Map<Integer, Sheet> sheets = this.xls.getSheetsMatchingName(this.settingsHolder.getExcelScheduleSheetName(), true, true);
        Sheet sheet;
        try {
            sheet = this.xls.getSheet(0);
        } catch (Exception e) {
            // sheet didnt exist?
            return null;
        }
//        if (sheets.size() == 1) {
//            Sheet sheet = sheets.get(0);
        DataFormatter dataFormatter = new DataFormatter();

        checkList.forEach( name -> {
            ExcelCellAddress cellAddress = XLSHelper.findCellByName(sheet, this.settingsHolder.getExcelScheduleDataNameCol(), name, dataFormatter);
            if (cellAddress == null) {
                notFound.add(name);
            }
        });
//        } else {
//            //  TODO: Handle other cases
//        }

        return notFound;
    }

    /**
     * Scans an Excel Schedule file for a section title and tries to detect the range of that section.
     * @param sectionName
     * @return
     */
    public ExcelCellRange getRangeForSection(String sectionName) {

        return null;
    }

}
