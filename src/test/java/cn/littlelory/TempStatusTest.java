package cn.littlelory;

import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
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
public class TempStatusTest {
    private BlobManager blobManager;

    @Before
    public void init() throws FileNotFoundException {
        blobManager = new BlobManager("");
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

        replay(workSpace, tempSpace);

        List<StatusInfo> expect = new ArrayList<>();
        expect.add(new StatusInfo("a.txt", StatusInfo.Status.UNTRACKED));

        List<StatusInfo> actual = blobManager.tempStatus();

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

        replay(workSpace, tempSpace);

        List<StatusInfo> expect = new ArrayList<>();
        expect.add(new StatusInfo("a.txt", StatusInfo.Status.MODIFITED));

        List<StatusInfo> actual = blobManager.tempStatus();

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

        replay(workSpace, tempSpace);

        List<StatusInfo> expect = new ArrayList<>();

        List<StatusInfo> actual = blobManager.tempStatus();

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

        replay(workSpace, tempSpace);

        List<StatusInfo> expect = new ArrayList<>();
        expect.add(new StatusInfo("a.txt", StatusInfo.Status.DELETE));

        List<StatusInfo> actual = blobManager.tempStatus();

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

        replay(workSpace, tempSpace);

        List<StatusInfo> expect = new ArrayList<>();
        List<StatusInfo> actual = blobManager.tempStatus();

        assertEquals(expect, actual);
    }
}
