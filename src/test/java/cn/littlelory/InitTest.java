package cn.littlelory;

import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Created by littlelory on 22/09/2017.
 */
public class InitTest {
    private BlobManager blobManager;
    private static final String DATADIR = TestUtil.resourcesPath() + "/data/manager/dir1";

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
}
