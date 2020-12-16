package tech.jmcs.floortech.scheduling.app.types;

public enum DataSourceExtractorType {
    TRUSS ("Truss", "EXCEL"),
    SLAB ("Slab", "PDF"),
    BEAM ("Beam", "EXCEL"),
    SHEET ("Sheet", "EXCEL"),
    GENERIC_SIMPLE ("Generic", "*");

    private final String name;
    private final String fileType;

    DataSourceExtractorType(String niceName, String fileType) {

        this.name = niceName;
        this.fileType = fileType;
    }

    public String getName() {
        return name;
    }

    public String getFileType() {
        return fileType;
    }
}
