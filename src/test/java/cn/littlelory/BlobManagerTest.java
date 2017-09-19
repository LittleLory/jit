package cn.littlelory;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 * Created by littlelory on 29/08/2017.
 */
public class BlobManagerTest {
    private BlobManager blobManager;
    private static final String DATADIR = TestUtil.resourcesPath() + "/data/manager/dir1";
    private static final String DATADIR2 = TestUtil.resourcesPath() + "/data/manager/dir2";

    @Before
    public void init() throws FileNotFoundException {
        blobManager = new BlobManager(DATADIR);
    }

    @Test
    public void initLibDir() throws IOException {
        TestUtil.deleteIfExists(Paths.get(DATADIR + "/.jit"));

        blobManager.init();

        assertTrue(Files.exists(Paths.get(DATADIR + "/.jit")));
        assertTrue(Files.exists(Paths.get(DATADIR + "/.jit/logs")));
        assertTrue(Files.exists(Paths.get(DATADIR + "/.jit/objects")));
    }

    @Test
    public void status_untracked() {
        WorkSpace workSpace = createMock(WorkSpace.class);
        List<FileEntry> workList = new ArrayList<>();
        workList.add(new FileEntry("a.txt", "a"));
        expect(workSpace.list()).andReturn(workList);
        blobManager.setWorkSpace(workSpace);

        TempSpace tempSpace = createMock(TempSpace.class);
        List<FileEntry> tempList = new ArrayList<>();
        expect(tempSpace.list()).andReturn(tempList);
        blobManager.setTempSpace(tempSpace);

        LocalSpace localSpace = createMock(LocalSpace.class);
        Map<String, FileEntry> localMap = new HashMap<>();
        expect(localSpace.map()).andReturn(localMap);
        blobManager.setLocalSpace(localSpace);

        replay(workSpace, tempSpace, localSpace);

        List<StatusInfo> expect = new ArrayList<>();
        expect.add(new StatusInfo("a.txt", StatusInfo.Status.UNTRACKED));

        List<StatusInfo> actual = blobManager.status();

        assertEquals(expect, actual);
    }

    @Test
    public void status_modified() {
        WorkSpace workSpace = createMock(WorkSpace.class);
        List<FileEntry> workList = new ArrayList<>();
        workList.add(new FileEntry("a.txt", "a"));
        expect(workSpace.list()).andReturn(workList);
        blobManager.setWorkSpace(workSpace);

        TempSpace tempSpace = createMock(TempSpace.class);
        List<FileEntry> tempList = new ArrayList<>();
        tempList.add(new FileEntry("a.txt", "b"));
        expect(tempSpace.list()).andReturn(tempList);
        blobManager.setTempSpace(tempSpace);

        LocalSpace localSpace = createMock(LocalSpace.class);
        Map<String, FileEntry> localMap = new HashMap<>();
        expect(localSpace.map()).andReturn(localMap);
        blobManager.setLocalSpace(localSpace);

        replay(workSpace, tempSpace, localSpace);

        List<StatusInfo> expect = new ArrayList<>();
        expect.add(new StatusInfo("a.txt", StatusInfo.Status.MODIFITED));

        List<StatusInfo> actual = blobManager.status();

        assertEquals(expect, actual);
    }

    @Test
    public void status_added() {
        WorkSpace workSpace = createMock(WorkSpace.class);
        List<FileEntry> workList = new ArrayList<>();
        workList.add(new FileEntry("a.txt", "a"));
        expect(workSpace.list()).andReturn(workList);
        blobManager.setWorkSpace(workSpace);

        TempSpace tempSpace = createMock(TempSpace.class);
        List<FileEntry> tempList = new ArrayList<>();
        tempList.add(new FileEntry("a.txt", "a"));
        expect(tempSpace.list()).andReturn(tempList);
        blobManager.setTempSpace(tempSpace);

        LocalSpace localSpace = createMock(LocalSpace.class);
        Map<String, FileEntry> localMap = new HashMap<>();
        expect(localSpace.map()).andReturn(localMap);
        blobManager.setLocalSpace(localSpace);

        replay(workSpace, tempSpace, localSpace);

        List<StatusInfo> expect = new ArrayList<>();
        expect.add(new StatusInfo("a.txt", StatusInfo.Status.ADDED));

        List<StatusInfo> actual = blobManager.status();

        assertEquals(expect, actual);
    }

    @Test
    public void status_delete() {
        WorkSpace workSpace = createMock(WorkSpace.class);
        List<FileEntry> workList = new ArrayList<>();
        expect(workSpace.list()).andReturn(workList);
        blobManager.setWorkSpace(workSpace);

        TempSpace tempSpace = createMock(TempSpace.class);
        List<FileEntry> tempList = new ArrayList<>();
        tempList.add(new FileEntry("a.txt", "a"));
        expect(tempSpace.list()).andReturn(tempList);
        blobManager.setTempSpace(tempSpace);

        LocalSpace localSpace = createMock(LocalSpace.class);
        Map<String, FileEntry> localMap = new HashMap<>();
        expect(localSpace.map()).andReturn(localMap);
        blobManager.setLocalSpace(localSpace);

        replay(workSpace, tempSpace, localSpace);

        List<StatusInfo> expect = new ArrayList<>();
        expect.add(new StatusInfo("a.txt", StatusInfo.Status.DELETE));

        List<StatusInfo> actual = blobManager.status();

        assertEquals(expect, actual);
    }

    @Test
    public void status_nothing() {
        WorkSpace workSpace = createMock(WorkSpace.class);
        List<FileEntry> workList = new ArrayList<>();
        expect(workSpace.list()).andReturn(workList);
        blobManager.setWorkSpace(workSpace);

        TempSpace tempSpace = createMock(TempSpace.class);
        List<FileEntry> tempList = new ArrayList<>();
        expect(tempSpace.list()).andReturn(tempList);
        blobManager.setTempSpace(tempSpace);

        LocalSpace localSpace = createMock(LocalSpace.class);
        Map<String, FileEntry> localMap = new HashMap<>();
        expect(localSpace.map()).andReturn(localMap);
        blobManager.setLocalSpace(localSpace);

        replay(workSpace, tempSpace, localSpace);

        List<StatusInfo> expect = new ArrayList<>();
        List<StatusInfo> actual = blobManager.status();

        assertEquals(expect, actual);
    }
}
