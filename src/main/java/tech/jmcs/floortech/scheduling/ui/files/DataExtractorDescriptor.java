package tech.jmcs.floortech.scheduling.ui.files;

import javafx.beans.property.StringProperty;
import javafx.scene.layout.VBox;
import tech.jmcs.floortech.scheduling.app.types.DataSourceExtractorType;

import java.nio.file.Path;
import java.util.Objects;

public class DataExtractorDescriptor {

    private String name;

    private DataSourceExtractorType type;

    private Object customExtractorDetails;

    private StringProperty filePathText;

    private VBox extractorVbox;

    public DataExtractorDescriptor() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataSourceExtractorType getType() {
        return type;
    }

    public void setType(DataSourceExtractorType type) {
        this.type = type;
    }

    public Object getCustomExtractorDetails() {
        return customExtractorDetails;
    }

    public void setCustomExtractorDetails(Object customExtractorDetails) {
        this.customExtractorDetails = customExtractorDetails;
    }

    public String getFilePathText() {
        if (filePathText != null) {
            return filePathText.get();
        } else {
            return "";
        }
    }

    public StringProperty filePathTextProperty() {
        return filePathText;
    }

    public void setFilePathTextProperty(StringProperty filePathText) {
        this.filePathText = filePathText;
    }

    public VBox getExtractorVbox() {
        return extractorVbox;
    }

    public void setExtractorVbox(VBox extractorVbox) {
        this.extractorVbox = extractorVbox;
    }

}
