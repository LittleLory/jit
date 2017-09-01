package cn.littlelory;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import static cn.littlelory.TestUtil.assertBytesEquals;
import static java.nio.file.Paths.*;
import static org.junit.Assert.*;

/**
 * Created by littlelory on 2017/8/25.
 */
public class FileUtilTest {
    private static final String DATADIR = TestUtil.resourcesPath() + "/data/file/";
    
    @Test
    public void file_exist() {
        assertTrue(FileUtil.exist(DATADIR + "file1.txt"));
    }

    @Test
    public void file_not_exist() {
        assertFalse(FileUtil.exist(DATADIR + "notExist.txt"));
    }

    @Test
    public void directory_exist() {
        assertTrue(FileUtil.exist(TestUtil.resourcesPath() + "data/file"));
    }

    @Test
    public void directory_not_exist() {
        assertFalse(FileUtil.exist(TestUtil.resourcesPath() + "data/notExist"));
    }

    @Test
    public void write_bytes() throws IOException {
        String pathname = DATADIR + "fileWriteBytesResult.txt";
        Path path = get(pathname);
        Files.deleteIfExists(path);

        byte[] bytes = "abc".getBytes();
        FileUtil.writeBytes(pathname, bytes);

        assertTrue(Files.exists(path));
        byte[] bytesInFile = Files.readAllBytes(path);
        assertBytesEquals(bytes, bytesInFile);
    }

    @Test
    public void write_str() throws IOException {
        String pathname = DATADIR + "fileWriteStrResult.txt";
        Path path = get(pathname);
        Files.deleteIfExists(path);

        String str = "test";
        FileUtil.writeStr(pathname, str);

        assertTrue(Files.exists(path));
        String strInFile = Files.readAllLines(path).get(0);
        assertEquals(str, strInFile);
    }

    @Test
    public void read_bytes() {
        byte[] expect = new byte[]{0x66,0x69,0x6C,0x65,0x33};
        byte[] actual = FileUtil.readBytes(DATADIR + "file3.txt");
        assertBytesEquals(expect, actual);
    }

    @Test
    public void read_str() {
        String expect = "test";
        String actual = FileUtil.readStr(DATADIR + "file4.txt");
        assertEquals(expect, actual);
    }

    @Test(expected = RuntimeException.class)
    public void read_bytes_and_file_not_exist() {
        FileUtil.readBytes(DATADIR + "notExist.txt");
    }

    @Test(expected = RuntimeException.class)
    public void read_a_directory() {
        FileUtil.readBytes(TestUtil.resourcesPath());
    }

    @Test
    public void mkdir() throws IOException {
        String pathname = DATADIR + "dir1";
        Path path = get(pathname);
        Files.deleteIfExists(path);

        FileUtil.mkdir(pathname);

        assertTrue(Files.exists(path));
    }

    @Test(expected = RuntimeException.class)
    public void mkdir_and_directory_exist() {
        String pathname = DATADIR + "dir2";
        FileUtil.mkdir(pathname);
    }

    @Test
    public void mkdir_if_not_exist_and_not_exist_actual() throws IOException {
        String pathname = DATADIR + "dir3";
        Path path = get(pathname);
        Files.deleteIfExists(path);

        FileUtil.mkdirIfNotExist(pathname);

        assertTrue(Files.exists(path));
    }

    @Test
    public void mkdir_if_not_exist_and_exist_actual() throws IOException {
        String pathname = DATADIR + "dir4";
        Path path = get(pathname);
        Files.deleteIfExists(path);

        FileUtil.mkdirIfNotExist(pathname);

        assertTrue(Files.exists(path));
    }
}
