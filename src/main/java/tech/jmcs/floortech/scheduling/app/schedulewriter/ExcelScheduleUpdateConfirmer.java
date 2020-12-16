package tech.jmcs.floortech.scheduling.app.schedulewriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ExcelScheduleUpdateConfirmer implements ScheduleUpdaterConfirmer {
    protected static final Logger LOG = LoggerFactory.getLogger(ExcelScheduleUpdateConfirmer.class);
    protected final ExcelScheduleUpdater updater;

    //    protected final XLSUtility xls;
//    protected final Integer targetSheetNumber;
    protected List<String> updateErrors;
    protected Map<String, Object> dataConflictProblems;
    protected Map<String, Object> noMatchFoundProblems;

    protected ExcelScheduleUpdateConfirmer(ExcelScheduleUpdater updater) {
        this.updater = updater;
//        this.xls = xls;
//        this.targetSheetNumber = targetSheetNumber;
        this.updateErrors = new ArrayList();
        this.dataConflictProblems = new HashMap<>();
        this.noMatchFoundProblems = new HashMap<>();
    }

    @Override
    public List<String> getUpdateErrors() {
        return this.updateErrors;
    }

    @Override
    public Map<String, Object> getConflictProblems() {
        return this.dataConflictProblems;
    }

    @Override
    public Map<String, Object> getNotFoundProblems() {
        return this.noMatchFoundProblems;
    }

    @Override
    public void addUpdateError(String error) {
        this.updateErrors.add(error);
    }
    @Override
    public void addConflictProblem(String name, Object existingValue) {
        this.dataConflictProblems.put(name, existingValue);
    }
    @Override
    public void addNotFoundProblem(String name, Object value) {
        this.noMatchFoundProblems.put(name, value);
    }
}
