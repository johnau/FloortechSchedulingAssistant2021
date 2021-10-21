package tech.jmcs.floortech.scheduling.app.schedulewriter;

import tech.jmcs.floortech.scheduling.app.util.ExcelCellAddress;

import java.util.List;
import java.util.Map;

public interface ScheduleUpdaterConfirmer {

    public List<String> getUpdateErrors();
    public void addUpdateError(String error);
    public Map<String, Object> getConflictProblems();
    public Map<String, Object> getConflictValues();
    public void addConflictProblem(String name, Object existingValue, Object newValue);
    public Map<String, Object> getNotFoundProblems();
    public void addNotFoundProblem(String name, Object value);

    public void forceOverwrite_addToCurrentValue(String name, Object value);
    public void forceOverwrite_addNewCell(String name, Object value, ExcelCellAddress belowCell);
    public void forceOverwrite_replaceCurrentValue(String name, Object value);
    public void forceOverwrite_replaceCell(String name, Object value, ExcelCellAddress targetRow);
    public List<String> getAllScheduleEntryNames();

}
