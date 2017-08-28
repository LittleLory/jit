package cn.littlelory;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static cn.littlelory.TestUtil.assertBytesEquals;
import static org.junit.Assert.*;

/**
 * Created by littlelory on 2017/8/25.
 */
public class FileUtilTest {
    private FileUtil fileUtil = FileUtil.getInstance();

    @Test
    public void file_exist() {
        assertTrue(fileUtil.exist(resourcesPath() + "data/file/file1.txt"));
    }

    @Test
    public void file_not_exist() {
        assertFalse(fileUtil.exist(resourcesPath() + "data/file/notExist.txt"));
    }

    @Test
    public void directory_exist() {
        assertTrue(fileUtil.exist(resourcesPath() + "data/file"));
    }

    @Test
    public void directory_not_exist() {
        assertFalse(fileUtil.exist(resourcesPath() + "data/notExist"));
    }

    @Test
    public void write_file_and_not_exist_before() throws IOException {
        String pathname = resourcesPath() + "data/file/fileWriteResult.txt";
        Path path = Paths.get(pathname);
        Files.deleteIfExists(path);

        byte[] bytes = "abc".getBytes();
        fileUtil.write(pathname, bytes);

        assertTrue(Files.exists(path));
        byte[] bytesInFile = Files.readAllBytes(path);
        assertBytesEquals(bytes, bytesInFile);
    }

    @Test(expected = IOException.class)
    public void write_file_and_exist_before() throws IOException {
        byte[] bytes = "abc".getBytes();
        String pathname = resourcesPath() + "data/file/file2.txt";
        fileUtil.write(pathname, bytes);
    }

    @Test
    public void read() throws IOException {
        byte[] expect = new byte[]{0x66,0x69,0x6C,0x65,0x33};
        byte[] actual = fileUtil.read(resourcesPath() + "data/file/file3.txt");
        assertBytesEquals(expect, actual);
    }

    @Test(expected = IOException.class)
    public void read_not_exist_file() throws IOException {
        fileUtil.read(resourcesPath() + "data/file/notExist.txt");
    }

    @Test(expected = IOException.class)
    public void read_a_directory() throws IOException {
        fileUtil.read(resourcesPath());
    }

    @Test
    public void mkdir() throws IOException {
        String pathname = resourcesPath() + "data/file/dir1";
        Path path = Paths.get(pathname);
        Files.deleteIfExists(path);

        fileUtil.mkdir(pathname);

        assertTrue(Files.exists(path));
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void mkdir_and_directory_exist() throws IOException {
        String pathname = resourcesPath() + "data/file/dir2";
        fileUtil.mkdir(pathname);
    }



    private String resourcesPath() {
        return this.getClass().getResource("/").getPath();
    }

}
