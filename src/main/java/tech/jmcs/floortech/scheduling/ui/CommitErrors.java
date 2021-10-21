package tech.jmcs.floortech.scheduling.ui;

import tech.jmcs.floortech.scheduling.app.schedulewriter.ExcelScheduleUpdateConfirmer;

public class CommitErrors {

    private ExcelScheduleUpdateConfirmer confirmer;

    public CommitErrors() {
    }

    public ExcelScheduleUpdateConfirmer getConfirmer() {
        return confirmer;
    }

    public void setConfirmer(ExcelScheduleUpdateConfirmer confirmer) {
        this.confirmer = confirmer;
    }

    public void clear() {
        this.confirmer = null;
    }
}
