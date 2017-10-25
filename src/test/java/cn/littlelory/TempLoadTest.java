package cn.littlelory;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by littlelory on 22/09/2017.
 */
public class TempLoadTest {
    private TempSpace tempSpace;
    private String baseDirPath;
    private String objectDirPath;
    private String indexPath;

    @Before
    public void init() throws IOException {
        baseDirPath = TestUtil.resourcesPath() + "data/load";
        objectDirPath = baseDirPath + "/.jit/objects";
        indexPath = baseDirPath + "/.jit/index";

        TestUtil.deleteChildrenIfExists(Paths.get(objectDirPath));
        Files.write(Paths.get(indexPath), new byte[0]);

        tempSpace = new TempSpace(baseDirPath, objectDirPath, indexPath);
    }

    @Test
    public void load() throws IOException {
        String path1 = "a.txt";
        byte[] bytes1=  "a".getBytes();
        JitObject jitObject1 = new JitObject(path1, bytes1);
        String path2 = "dir/b.txt";
        byte[] bytes2 = "dir/b.txt".getBytes();
        JitObject jitObject2 = new JitObject(path2, bytes2);

        Files.createDirectories(Paths.get(baseDirPath + "/dir"));
        Files.write(Paths.get(baseDirPath + "/" + path1), bytes1);
        Files.write(Paths.get(baseDirPath + "/" + path2), bytes2);

//        BlobUtil.writeBlob(objectDirPath, jitObject1);
//        BlobUtil.writeBlob(objectDirPath, jitObject2);

        tempSpace.add(path1);
        tempSpace.add(path2);

        List<JitObject> expect = new ArrayList<>();
        expect.add(jitObject1);
        expect.add(jitObject2);

        List<JitObject> actual = tempSpace.load();

        assertEquals(2, actual.size());
        assertEquals(jitObject1, actual.get(0));
        assertEquals(jitObject2, actual.get(1));
    }
}
