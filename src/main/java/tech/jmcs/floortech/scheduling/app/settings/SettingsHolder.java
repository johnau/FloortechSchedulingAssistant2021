package tech.jmcs.floortech.scheduling.app.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Settings Memory class
 * Holds settings to be accessed throughout the app (Class is container managed and injected, should not be instantiated
 * Will be instantiated when a Settings Loader is used.
 */
public class SettingsHolder {
    protected static final Logger LOG = LoggerFactory.getLogger(SettingsHolder.class);

    private Path jobFilesSchedulingRootPath;
    private Path jobFoldersDetailingRootPath;

    private String excelScheduleSheetName;
    private Integer excelScheduleDataNameCol;
    private Integer excelScheduleDataValueCol;
    private Integer excelScheduleDataUnitCol;
    private Integer excelScheduleDataRateCol;
    private Integer excelScheduleDataTotalCol;
    private Integer excelScheduleDataCommentCol;

    private List<String> excelScheduleFileSections;

    private Boolean builtInTrussExtractorEnabled;
    private String trussScheduleSectionName;
    private Boolean builtInBeamExtractorEnabled;
    private String beamScheduleSectionName;
    private Boolean builtInSheetExtractorEnabled;
    private String sheetScheduleSectionName;
    private Boolean builtInSlabExtractorEnabled;
    private String slabScheduleSectionName;

    private List<String> dataSourceFileNamesMap;

    /**
     * Constructor
     */
    public SettingsHolder() {
        LOG.debug("Settings Holder instantiated (Empty)");
        this.dataSourceFileNamesMap = new ArrayList<>();
        this.excelScheduleFileSections = new ArrayList<>();
    }

    /**
     * Set all method
     * @param jobFilesSchedulingRootPath
     * @param jobFoldersDetailingRootPath
     * @param builtInBeamExtractorEnabled
     * @param builtInSheetExtractorEnabled
     * @param builtInSlabExtractorEnabled
     * @param builtInTrussExtractorEnabled
     */
    public void setAllSettings(Path jobFilesSchedulingRootPath, Path jobFoldersDetailingRootPath,
                               Boolean builtInBeamExtractorEnabled,
                               String beamScheduleSection,
                               Boolean builtInSheetExtractorEnabled,
                               String sheetScheduleSection,
                               Boolean builtInSlabExtractorEnabled,
                               String slabScheduleSection,
                               Boolean builtInTrussExtractorEnabled,
                               String trussScheduleSection,
                               String scheduleSheetName,
                               List<String> excelScheduleSections) {
        try {
            this.setJobFilesSchedulingRootPath(jobFilesSchedulingRootPath);
        } catch (FileNotFoundException e) {
        }
        try {
            this.setJobFoldersDetailingRootPath(jobFoldersDetailingRootPath);
        } catch (FileNotFoundException e) {
        }

        this.setBuiltInBeamExtractorEnabled(builtInBeamExtractorEnabled);
        this.setBeamScheduleSectionName(beamScheduleSection);
        this.setBuiltInSheetExtractorEnabled(builtInSheetExtractorEnabled);
        this.setSheetScheduleSectionName(sheetScheduleSection);
        this.setBuiltInSlabExtractorEnabled(builtInSlabExtractorEnabled);
        this.setSlabScheduleSectionName(slabScheduleSection);
        this.setBuiltInTrussExtractorEnabled(builtInTrussExtractorEnabled);
        this.setTrussScheduleSectionName(trussScheduleSection);

        this.setExcelScheduleSheetName(scheduleSheetName);
        this.setExcelScheduleFileSections(excelScheduleSections);
//        LOG.debug("Set: js path: {} jd path{} beam {} sheet {} slab {} truss {} ", jobFilesSchedulingRootPath, jobFoldersDetailingRootPath, builtInBeamExtractorEnabled, builtInSheetExtractorEnabled, builtInSlabExtractorEnabled, builtInTrussExtractorEnabled);
    }

    // Getters & Setters
    public Path getJobFilesSchedulingRootPath() {
        return jobFilesSchedulingRootPath;
    }

    public void setJobFilesSchedulingRootPath(Path jobFilesSchedulingRootPath) throws FileNotFoundException {
        if (jobFilesSchedulingRootPath == Paths.get("")) {
            this.jobFilesSchedulingRootPath = null;
            return;
        }
        if (this.jobFilesSchedulingRootPath != null && this.jobFilesSchedulingRootPath.equals(jobFilesSchedulingRootPath)) {
            return;
        }
        File f = jobFilesSchedulingRootPath.toFile();
        if (!f.exists() || !f.isDirectory()) {
            LOG.debug("The folder did not exist");
            throw new FileNotFoundException("Folder not found");
        }
        this.jobFilesSchedulingRootPath = jobFilesSchedulingRootPath;
    }

    public Path getJobFoldersDetailingRootPath() {
        return jobFoldersDetailingRootPath;
    }

    public void setJobFoldersDetailingRootPath(Path jobFoldersDetailingRootPath) throws FileNotFoundException {
        if (jobFoldersDetailingRootPath == null) {
            this.jobFoldersDetailingRootPath = Paths.get("");
            return;
        }
        if (this.jobFoldersDetailingRootPath != null && this.jobFoldersDetailingRootPath.equals(jobFoldersDetailingRootPath)) {
            return;
        }
        File f = jobFoldersDetailingRootPath.toFile();
        if (!f.exists() || !f.isDirectory()) {
            LOG.debug("The folder did not exist");
            throw new FileNotFoundException("Folder not found");
        }
        this.jobFoldersDetailingRootPath = jobFoldersDetailingRootPath;
    }

    public Boolean isBuiltInTrussExtractorEnabled() {
        return builtInTrussExtractorEnabled;
    }

    public void setBuiltInTrussExtractorEnabled(Boolean builtInTrussExtractorEnabled) {
        if (builtInTrussExtractorEnabled == null) {
            return;
        }
        if (this.builtInTrussExtractorEnabled != builtInTrussExtractorEnabled) {
            this.builtInTrussExtractorEnabled = builtInTrussExtractorEnabled;
        }
    }

    public Boolean isBuiltInBeamExtractorEnabled() {
        return builtInBeamExtractorEnabled;
    }

    public void setBuiltInBeamExtractorEnabled(Boolean builtInBeamExtractorEnabled) {
        if (builtInBeamExtractorEnabled == null) {
            return;
        }
        if (this.builtInBeamExtractorEnabled != builtInBeamExtractorEnabled) {
            this.builtInBeamExtractorEnabled = builtInBeamExtractorEnabled;
        }
    }

    public Boolean isBuiltInSheetExtractorEnabled() {
        return builtInSheetExtractorEnabled;
    }

    public void setBuiltInSheetExtractorEnabled(Boolean builtInSheetExtractorEnabled) {
        if (builtInSheetExtractorEnabled == null) {
            return;
        }
        if (this.builtInSheetExtractorEnabled != builtInSheetExtractorEnabled) {
            this.builtInSheetExtractorEnabled = builtInSheetExtractorEnabled;
        }
    }

    public Boolean isBuiltInSlabExtractorEnabled() {
        return builtInSlabExtractorEnabled;
    }

    public void setBuiltInSlabExtractorEnabled(Boolean builtInSlabExtractorEnabled) {
        if (builtInSlabExtractorEnabled == null) {
            return;
        }
        if (this.builtInSlabExtractorEnabled != builtInSlabExtractorEnabled) {
            this.builtInSlabExtractorEnabled = builtInSlabExtractorEnabled;
        }
    }

    public String getTrussScheduleSectionName() {
        return trussScheduleSectionName;
    }

    public void setTrussScheduleSectionName(String trussScheduleSectionName) {
        if (trussScheduleSectionName == null) {
            return;
        }
        this.trussScheduleSectionName = trussScheduleSectionName;
    }

    public String getBeamScheduleSectionName() {
        return beamScheduleSectionName;
    }

    public void setBeamScheduleSectionName(String beamScheduleSectionName) {
        if (beamScheduleSectionName == null) {
            return;
        }
        this.beamScheduleSectionName = beamScheduleSectionName;
    }

    public String getSheetScheduleSectionName() {
        return sheetScheduleSectionName;
    }

    public void setSheetScheduleSectionName(String sheetScheduleSectionName) {
        if (sheetScheduleSectionName == null) {
            return;
        }
        this.sheetScheduleSectionName = sheetScheduleSectionName;
    }

    public String getSlabScheduleSectionName() {
        return slabScheduleSectionName;
    }

    public void setSlabScheduleSectionName(String slabScheduleSectionName) {
        if (slabScheduleSectionName == null) {
            return;
        }
        this.slabScheduleSectionName = slabScheduleSectionName;
    }

    public String getExcelScheduleSheetName() {
        return excelScheduleSheetName;
    }

    public void setExcelScheduleSheetName(String excelScheduleSheetName) {
        if (excelScheduleSheetName == null) {
            return;
        }
        this.excelScheduleSheetName = excelScheduleSheetName;
    }

    public Integer getExcelScheduleDataNameCol() {
        return excelScheduleDataNameCol;
    }

    public void setExcelScheduleDataNameCol(Integer excelScheduleDataNameCol) {
        if (excelScheduleDataNameCol == null){
            return;
        }
        this.excelScheduleDataNameCol = excelScheduleDataNameCol;
    }

    public Integer getExcelScheduleDataValueCol() {
        return excelScheduleDataValueCol;
    }

    public void setExcelScheduleDataValueCol(Integer excelScheduleDataValueCol) {
        if (excelScheduleDataValueCol == null) {
            return;
        }
        this.excelScheduleDataValueCol = excelScheduleDataValueCol;
    }

    public Integer getExcelScheduleDataUnitCol() {
        return excelScheduleDataUnitCol;
    }

    public void setExcelScheduleDataUnitCol(Integer excelScheduleDataUnitCol) {
        if (excelScheduleDataUnitCol == null) {
            return;
        }
        this.excelScheduleDataUnitCol = excelScheduleDataUnitCol;
    }

    public Integer getExcelScheduleDataRateCol() {
        return excelScheduleDataRateCol;
    }

    public void setExcelScheduleDataRateCol(Integer excelScheduleDataRateCol) {
        if (excelScheduleDataRateCol == null) {
            return;
        }
        this.excelScheduleDataRateCol = excelScheduleDataRateCol;
    }

    public Integer getExcelScheduleDataTotalCol() {
        return excelScheduleDataTotalCol;
    }

    public void setExcelScheduleDataTotalCol(Integer excelScheduleDataTotalCol) {
        if (excelScheduleDataTotalCol == null) {
            return;
        }
        this.excelScheduleDataTotalCol = excelScheduleDataTotalCol;
    }

    public Integer getExcelScheduleDataCommentCol() {
        return excelScheduleDataCommentCol;
    }

    public void setExcelScheduleDataCommentCol(Integer excelScheduleDataCommentCol) {
        if (excelScheduleDataCommentCol == null) {
            return;
        }
        this.excelScheduleDataCommentCol = excelScheduleDataCommentCol;
    }

    public List<String> getExcelScheduleFileSections() {
        return excelScheduleFileSections;
    }

    public void addExcelScheduleFileSections(String section) {
        if (section == null) {
            return;
        }
        if (!this.excelScheduleFileSections.contains(section.toUpperCase())) {
            this.excelScheduleFileSections.add(section.toUpperCase());
        }
    }

    public void setExcelScheduleFileSections(List<String> excelScheduleFileSections) {
        if (excelScheduleFileSections != null) {
            List<String> upperCaseList = excelScheduleFileSections.stream().map(m -> m.toUpperCase()).collect(Collectors.toList());
            this.excelScheduleFileSections = upperCaseList;
        } else {
            this.excelScheduleFileSections = new ArrayList();
        }
    }
}
