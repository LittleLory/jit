package cn.littlelory;

import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

/**
 * Created by littlelory on 22/09/2017.
 */
public class LocalStatusTest {

    private BlobManager blobManager;

    @Before
    public void init() throws FileNotFoundException {
        blobManager = new BlobManager("");
    }


    @Test
    public void status_modified() {
        TempSpace tempSpace = createMock(TempSpace.class);
        List<FileEntry> tempList = new ArrayList<>();
        tempList.add(new FileEntry("a.txt", "b"));
        expect(tempSpace.list()).andReturn(tempList);
        blobManager.setTempSpace(tempSpace);

        LocalSpace localSpace = createMock(LocalSpace.class);
        List<FileEntry> localList = new ArrayList<>();
        localList.add(new FileEntry("a.txt", "a"));
        expect(localSpace.list()).andReturn(localList);
        blobManager.setLocalSpace(localSpace);

        replay(tempSpace, localSpace);

        List<StatusInfo> expect = new ArrayList<>();
        expect.add(new StatusInfo("a.txt", StatusInfo.Status.MODIFITED));

        List<StatusInfo> actual = blobManager.localStatus();

        assertEquals(expect, actual);
    }

    @Test
    public void status_added() {
        TempSpace tempSpace = createMock(TempSpace.class);
        List<FileEntry> tempList = new ArrayList<>();
        tempList.add(new FileEntry("a.txt", "b"));
        expect(tempSpace.list()).andReturn(tempList);
        blobManager.setTempSpace(tempSpace);

        LocalSpace localSpace = createMock(LocalSpace.class);
        List<FileEntry> localList = new ArrayList<>();
        expect(localSpace.list()).andReturn(localList);
        blobManager.setLocalSpace(localSpace);

        replay(tempSpace, localSpace);

        List<StatusInfo> expect = new ArrayList<>();
        expect.add(new StatusInfo("a.txt", StatusInfo.Status.ADDED));

        List<StatusInfo> actual = blobManager.localStatus();

        assertEquals(expect, actual);
    }

    @Test
    public void status_delete() {
        TempSpace tempSpace = createMock(TempSpace.class);
        List<FileEntry> tempList = new ArrayList<>();
        expect(tempSpace.list()).andReturn(tempList);
        blobManager.setTempSpace(tempSpace);

        LocalSpace localSpace = createMock(LocalSpace.class);
        List<FileEntry> localList = new ArrayList<>();
        localList.add(new FileEntry("a.txt", "a"));
        expect(localSpace.list()).andReturn(localList);
        blobManager.setLocalSpace(localSpace);

        replay(tempSpace, localSpace);

        List<StatusInfo> expect = new ArrayList<>();
        expect.add(new StatusInfo("a.txt", StatusInfo.Status.DELETE));

        List<StatusInfo> actual = blobManager.localStatus();

        assertEquals(expect, actual);
    }
}
