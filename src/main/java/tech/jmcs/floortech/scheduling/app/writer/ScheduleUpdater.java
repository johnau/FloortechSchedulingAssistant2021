package tech.jmcs.floortech.scheduling.app.writer;

import java.util.Map;

public interface ScheduleUpdater {

    public boolean isValid();

    public void updateSchedule(Map<String, Object> values);

}
