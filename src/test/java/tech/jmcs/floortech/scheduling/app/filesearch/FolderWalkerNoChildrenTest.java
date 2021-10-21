package tech.jmcs.floortech.scheduling.app.filesearch;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FolderWalkerNoChildrenTest {

    @Test
    void getFoldersLimited() {
        FolderWalkerNoChildren fw = new FolderWalkerNoChildren();
        List<Path> folders = fw.getFoldersLimited("E:\\appdev\\floortech_env\\Floortech\\1 FLOORTECH JOBS");
        folders.forEach( f->{
            System.out.println(f);
        });

    }
}