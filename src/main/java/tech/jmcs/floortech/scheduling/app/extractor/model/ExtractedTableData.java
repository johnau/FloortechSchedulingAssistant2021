package tech.jmcs.floortech.scheduling.app.extractor.model;

import java.util.HashMap;
import java.util.Map;

public class ExtractedTableData<T> {

    private String name;
    private Map<Long, T> data;

    public ExtractedTableData(String name) {
        this.name = name;
        this.data = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Adds a value to the map and returns the assigned id.
     * @param value
     * @return
     */
    public long addData(T value) {
        long id = (long) this.data.size();
        this.data.put(id, value);
        return id;
    }

    public Map<Long, T> getData() {
        return data;
    }
}
