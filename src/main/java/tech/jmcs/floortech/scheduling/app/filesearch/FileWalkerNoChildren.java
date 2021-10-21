package tech.jmcs.floortech.scheduling.app.filesearch;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class FileWalkerNoChildren {
    public HashMap<String, List> getFilesLimited(String pathString, String extension) {
        return getFilesLimited(pathString, Arrays.asList(extension));
    }

    public HashMap<String, List> getFilesLimited(String pathString, List<String> extensions) {
        Path folder = Paths.get(pathString);
        ArrayList<Path> files_paths = new ArrayList();
        ArrayList<BasicFileAttributes> files_attributes = new ArrayList();
        HashMap<String, List> files = new HashMap<>();

        try {
            Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                        throws IOException {
                    if (dir.toString().equals(pathString)) {
                        return FileVisitResult.CONTINUE;
                    }
                    return FileVisitResult.SKIP_SUBTREE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException
                {
                    List<PathMatcher> matchers = extensions.stream().map( ext -> FileSystems.getDefault().getPathMatcher("glob:**/*." + ext)).collect(Collectors.toList());

                    if (matchers.stream().filter( f -> f.matches(file)).findAny().isPresent()) {
                        files_paths.add(file);
                        files_attributes.add(attrs);
                    }
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

        files.put("paths", files_paths);
        files.put("attributes", files_attributes);

        return files;
    }
}
