package tech.jmcs.floortech.scheduling.app.filesearch;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class FolderWalkerNoChildren {

    public List<Path> getFoldersLimited(String pathString) {
        Path folder = Paths.get(pathString);
        ArrayList<Path> folderPaths = new ArrayList();

        try {
            Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                        throws IOException {

                    if (dir.toString().equals(pathString)) {
                        return FileVisitResult.CONTINUE;
                    }
                    folderPaths.add(dir);

                    return FileVisitResult.SKIP_SUBTREE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException
                {
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                {
                    if (dir.toAbsolutePath().toString().equals(pathString)) {
                        return FileVisitResult.TERMINATE;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return folderPaths;
    }
}
