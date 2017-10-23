package cn.littlelory;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * Created by littlelory on 13/09/2017.
 */
public class TempBuildTest {
    private String baseDirPath;
    private String objectDirPath;
    private String indexPath;
    private TempSpace tempSpace;

    @Before
    public void init() throws IOException {
        this.baseDirPath = TestUtil.resourcesPath() + "data/temp";
        this.objectDirPath = TestUtil.resourcesPath() + "data/temp/.jit/objects";
        this.indexPath = TestUtil.resourcesPath() + "data/temp/.jit/index";

        byte[] bytes = new byte[]{
                0x0, 0x0, 0x0, 0x35,//first entry size
                0x0, 0x0, 0x0, 0x28,//fingerprint length = 40
                0x66, 0x38, 0x62, 0x61, 0x62, 0x65, 0x32, 0x61, 0x65, 0x31, 0x62, 0x62, 0x35, 0x39, 0x63, 0x34, 0x35,
                0x61, 0x32, 0x38, 0x33, 0x30, 0x37, 0x37, 0x66, 0x64, 0x34, 0x63, 0x62, 0x34, 0x37, 0x62, 0x62, 0x39,
                0x63, 0x31, 0x30, 0x36, 0x61, 0x37,//fingerprint = 8baef1b4abc478178b004d62031cf7fe6db6f903
                0x0, 0x0, 0x0, 0x5,//pathname length = 5
                0x61, 0x2E, 0x74, 0x78, 0x74,//pathname =  a.txt

                0x0, 0x0, 0x0, 0x39,//second entry size
                0x0, 0x0, 0x0, 0x28,//fingerprint length = 40
                0x38, 0x36, 0x35, 0x37, 0x35, 0x64, 0x64, 0x34, 0x37, 0x39, 0x36, 0x62, 0x31, 0x37, 0x30, 0x65, 0x36,
                0x66, 0x66, 0x33, 0x31, 0x63, 0x36, 0x39, 0x30, 0x35, 0x64, 0x66, 0x66, 0x64, 0x38, 0x64, 0x37, 0x36,
                0x32, 0x37, 0x64, 0x64, 0x36, 0x65,//fingerprint = 72943a16fb2c8f38f9dde202b7a70ccc19c52f34
                0x0, 0x0, 0x0, 0x9,//pathname length = 9
                0x64, 0x69, 0x72, 0x2F, 0x61, 0x2E, 0x74, 0x78, 0x74,//pathname =  dir/a.txt
        };

        Files.write(Paths.get(indexPath), bytes);

        TestUtil.deleteChildrenIfExists(Paths.get(TestUtil.resourcesPath() + "data/temp/.jit/objects"));

        tempSpace = new TempSpace(baseDirPath, objectDirPath, indexPath);
    }

    @Test
    public void build() throws IOException {
        tempSpace.build();

        JitObject jitObject1 = new JitObject("a.txt", Files.readAllBytes(Paths.get(TestUtil.resourcesPath() + "data/temp/a.txt")));
        String fingerprint1 = Fingerprint.generate(jitObject1.encode());
        assertTrue(Files.exists(Paths.get(objectDirPath + "/" + fingerprint1.substring(0, 2))));
        assertTrue(Files.isDirectory(Paths.get(objectDirPath + "/" + fingerprint1.substring(0, 2))));
        assertTrue(Files.exists(Paths.get(objectDirPath + "/" + fingerprint1.substring(0, 2) + "/" + fingerprint1.substring(2))));
        assertFalse(Files.isDirectory(Paths.get(objectDirPath + "/" + fingerprint1.substring(0, 2) + "/" + fingerprint1.substring(2))));

        JitObject jitObject2 = new JitObject("dir/a.txt", Files.readAllBytes(Paths.get(TestUtil.resourcesPath() + "data/temp/dir/a.txt")));
        String fingerprint2 = Fingerprint.generate(jitObject2.encode());
        assertTrue(Files.exists(Paths.get(objectDirPath + "/" + fingerprint2.substring(0,2))));
        assertTrue(Files.isDirectory(Paths.get(objectDirPath + "/" + fingerprint2.substring(0,2))));
        assertTrue(Files.exists(Paths.get(objectDirPath + "/"+fingerprint2.substring(0,2)+"/" + fingerprint2.substring(2))));
        assertFalse(Files.isDirectory(Paths.get(objectDirPath + "/"+fingerprint2.substring(0,2)+"/" + fingerprint2.substring(2))));

        JitTree tree = new JitTree("dir/");
        tree.addChild(JitBlobType.OBJECT, "dir/a.txt", fingerprint2);
        String fingerprint3 = Fingerprint.generate(tree.encode());
        assertTrue(Files.exists(Paths.get(objectDirPath + "/" + fingerprint3.substring(0,2))));
        assertTrue(Files.isDirectory(Paths.get(objectDirPath + "/" + fingerprint3.substring(0,2))));
        assertTrue(Files.exists(Paths.get(objectDirPath + "/"+fingerprint3.substring(0,2)+"/" + fingerprint3.substring(2))));
        assertFalse(Files.isDirectory(Paths.get(objectDirPath + "/"+fingerprint3.substring(0,2)+"/" + fingerprint3.substring(2))));

        JitTree root = new JitTree("");
        root.addChild(JitBlobType.OBJECT, "a.txt",fingerprint1);
        root.addChild(JitBlobType.TREE, "dir/", fingerprint3);
        String fingerprint4 = Fingerprint.generate(root.encode());
        assertTrue(Files.exists(Paths.get(objectDirPath + "/" + fingerprint4.substring(0,2))));
        assertTrue(Files.isDirectory(Paths.get(objectDirPath + "/" + fingerprint4.substring(0,2))));
        assertTrue(Files.exists(Paths.get(objectDirPath + "/"+fingerprint4.substring(0,2)+"/" + fingerprint4.substring(2))));
        assertFalse(Files.isDirectory(Paths.get(objectDirPath + "/"+fingerprint4.substring(0,2)+"/" + fingerprint4.substring(2))));
    }

    @Test
    public void test() throws IOException {
        System.out.println();
        System.out.println(Fingerprint.generate(new JitObject("dir/a.txt", Files.readAllBytes(Paths.get(TestUtil.resourcesPath() + "data/temp/dir/a.txt"))).encode()));
    }

}
