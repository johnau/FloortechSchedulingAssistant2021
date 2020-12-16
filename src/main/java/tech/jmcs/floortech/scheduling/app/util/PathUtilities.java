package tech.jmcs.floortech.scheduling.app.util;

import java.io.File;
import java.nio.file.Path;

public class PathUtilities {

    public static File getNearestDirectory(Path path) {
        File f = path.toFile();
        if (f.isDirectory()) {
            return f;
        } else {
            f = f.getParentFile();
            if (f.isDirectory()) {
                return f;
            }
        }
        return null;
    }

}
