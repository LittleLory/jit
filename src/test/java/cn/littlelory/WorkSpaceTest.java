package cn.littlelory;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WorkSpaceTest {
    private WorkSpace workSpace;

    @Before
    public void init() {
        String basePath = TestUtil.resourcesPath() + "data/scan";
        workSpace = new WorkSpace(basePath);
    }

    @Test
    public void list() throws Exception {
        List<FileEntry> result = workSpace.list();
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("a.txt", result.get(0).getPathname());
        assertEquals("dir/b.txt", result.get(1).getPathname());
        assertEquals("dir2/dir3/c.txt", result.get(2).getPathname());
    }

    @Test
    public void search() {
        TestUtil.assertBytesEquals(new byte[]{0x61, 0x62, 0x63}, workSpace.search("a.txt"));
    }

    @Test
    public void flush() throws IOException {
        String basePath = TestUtil.resourcesPath() + "data/flush";
        workSpace = new WorkSpace(basePath);

        List<JitObject> objects = new ArrayList<>();
        objects.add(new JitObject("a.txt", "a".getBytes()));
        objects.add(new JitObject("b.txt", "b".getBytes()));
        objects.add(new JitObject("dir/b.txt", "b".getBytes()));

        workSpace.flush(objects);

        assertTrue(Files.exists(Paths.get(basePath + "/a.txt")));
        TestUtil.assertBytesEquals("a".getBytes(), Files.readAllBytes(Paths.get(basePath + "/a.txt")));
        assertTrue(Files.exists(Paths.get(basePath + "/b.txt")));
        TestUtil.assertBytesEquals("b".getBytes(), Files.readAllBytes(Paths.get(basePath + "/b.txt")));
        assertTrue(Files.exists(Paths.get(basePath + "/dir/b.txt")));
        TestUtil.assertBytesEquals("b".getBytes(), Files.readAllBytes(Paths.get(basePath + "/dir/b.txt")));
    }
}