package tech.jmcs.floortech.scheduling.app.schedulewriter;

import tech.jmcs.floortech.scheduling.app.exception.ExcelScheduleWriterException;

import java.io.IOException;
import java.util.Map;

public interface ScheduleUpdater {

    public boolean isValid() throws ExcelScheduleWriterException;

    /**
     * Updates the Scheduling sheet with values by name.
     * Returns a Map (Key: Problem Type, Value: Map of values by name)
     * @param values
     * @throws ExcelScheduleWriterException
     * @return
     */
    public ExcelScheduleUpdateConfirmer updateSchedule(Map<String, Object> values) throws ExcelScheduleWriterException;

//    public List<String> getErrors();
//    public Map<String, Object> getNoMatchProblems();
//    public Map<String, Object> getConflictingDataProblems();

//    public void updateSingleWithForceOverwrite(String name, Object value, ForceUpdateType type) throws ExcelScheduleWriterException;


    /**
     * @throws IOException
     */
    public void completeUpdate() throws IOException;

}
