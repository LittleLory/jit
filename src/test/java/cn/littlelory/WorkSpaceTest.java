package cn.littlelory;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

public class WorkSpaceTest {
    private WorkSpace workSpace;

    @Before
    public void init() {
        String basePath = TestUtil.resourcesPath() + "data/scan";
        workSpace = new WorkSpace(basePath);
    }

    @Test
    public void allFiles() throws Exception {
        List<FileEntry> result = workSpace.list();
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("a.txt", result.get(0).getPathname());
        assertEquals("dir/b.txt", result.get(1).getPathname());
        assertEquals("dir2/dir3/c.txt", result.get(2).getPathname());
    }

}