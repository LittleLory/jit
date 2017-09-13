package cn.littlelory;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by littlelory on 07/09/2017.
 */
public class LocalSpaceTest {
    private LocalSpace localSpace;
    private static final String OBJECT_DIR = TestUtil.resourcesPath() + "data/manager/local/.jit/objects";
    private static final String HEAD_PATH = TestUtil.resourcesPath() + "data/manager/local/.jit/HEAD";
    private static final String HEAD_LOG_PATH = TestUtil.resourcesPath() + "data/manager/local/.jit/logs/HEAD";

    @Before
    public void init() {
        String path1 = "object1";
        JitObject object1 = new JitObject(path1, new byte[]{0x0, 0x0, 0x0, 0x1});
        byte[] bytes1 = object1.encode();
        String fingerprint1 = Fingerprint.generate(bytes1);
        System.out.println("object1-" + fingerprint1);

        String path2 = "dir/object3";
        JitObject object3 = new JitObject(path2, new byte[]{0x0, 0x0, 0x0, 0x3});
        byte[] bytes2 = object3.encode();
        String fingerprint2 = Fingerprint.generate(bytes2);
        System.out.println("dir/object3-" + fingerprint2);

        String path3 = "dir";
        JitTree tree = new JitTree(path3);
        tree.addChild(JitBlobType.OBJECT, path2, fingerprint2);
        byte[] bytes3 = tree.encode();
        String fingerprint3 = Fingerprint.generate(bytes3);

        JitTree root = new JitTree("");
        root.addChild(JitBlobType.OBJECT, path1, fingerprint1);
        root.addChild(JitBlobType.TREE, path3, fingerprint3);
        byte[] bytes4 = root.encode();
        String fingerprint4 = Fingerprint.generate(bytes4);

        FileUtil.writeStr(HEAD_PATH, fingerprint4);

        FileUtil.mkdirIfNotExist(OBJECT_DIR + "/" + fingerprint1.substring(0,2));
        FileUtil.writeBytes(OBJECT_DIR + "/" + fingerprint1.substring(0,2) + "/" + fingerprint1.substring(2), bytes1);
        FileUtil.mkdirIfNotExist(OBJECT_DIR + "/" + fingerprint2.substring(0,2));
        FileUtil.writeBytes(OBJECT_DIR + "/" + fingerprint2.substring(0,2) + "/" + fingerprint2.substring(2), bytes2);
        FileUtil.mkdirIfNotExist(OBJECT_DIR + "/" + fingerprint3.substring(0,2));
        FileUtil.writeBytes(OBJECT_DIR + "/" + fingerprint3.substring(0,2) + "/" + fingerprint3.substring(2), bytes3);
        FileUtil.mkdirIfNotExist(OBJECT_DIR + "/" + fingerprint4.substring(0,2));
        FileUtil.writeBytes(OBJECT_DIR + "/" + fingerprint4.substring(0,2) + "/" + fingerprint4.substring(2), bytes4);

        localSpace = new LocalSpace(OBJECT_DIR, HEAD_PATH, HEAD_LOG_PATH);
    }

    @Test
    public void list() {
        List<FileEntry> list = localSpace.list();
        assertEquals(2, list.size());
        assertEquals("dir/object3", list.get(0).getPathname());
        assertEquals("a6e25e9d8effb5ae3b5e2839d062309197c191c9", list.get(0).getFingerprint());
        assertEquals("object1", list.get(1).getPathname());
        assertEquals("9dff22578761e09c7fcaad5186f563df1b3365fb", list.get(1).getFingerprint());
    }

    @Test
    public void map() {
        Map<String, FileEntry> map = localSpace.map();
        assertTrue(map.containsKey("dir/object3"));
        assertEquals(new FileEntry("dir/object3", "a6e25e9d8effb5ae3b5e2839d062309197c191c9"), map.get("dir/object3"));
        assertTrue(map.containsKey("object1"));
        assertEquals(new FileEntry("object1", "9dff22578761e09c7fcaad5186f563df1b3365fb"), map.get("object1"));
    }
}