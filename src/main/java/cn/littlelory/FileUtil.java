package cn.littlelory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Created by littlelory on 2017/8/25.
 */
class FileUtil {

    static void writeBytes(String pathname, byte[] bytes) throws IOException {
        Files.write(path(pathname), bytes);
    }

    static void writeStr(String pathname, String str) throws IOException {
        byte[] bytes = str.getBytes();
        writeBytes(pathname, bytes);
    }

    static void appendBytes(String pathname, byte[] bytes) throws IOException {
        Files.write(path(pathname), bytes, StandardOpenOption.APPEND);
    }

    static byte[] readBytes(String pathname) throws IOException {
        return Files.readAllBytes(path(pathname));
    }


    static String readStr(String pathname) throws IOException {
        byte[] bytes = readBytes(pathname);
        return new String(bytes);
    }

    static void appendStr(String pathname, String str) throws IOException {
        byte[] bytes = str.getBytes();
        appendBytes(pathname, bytes);
    }

    static boolean exist(String pathname) {
        return Files.exists(path(pathname));
    }

    static void mkdir(String pathname) throws IOException {
        Files.createDirectory(path(pathname));
    }

    static void mkdirIfNotExist(String pathname) throws IOException {
        if (!exist(pathname))
            mkdir(pathname);
    }

    private static void assertNotExist(String pathname) throws IOException {
        if (exist(pathname))
            throw new IOException("the file[" + pathname + "] is exist.");
    }

    private static Path path(String pathname) {
        return Paths.get(pathname);
    }

    private static String pathname(Path path) {
        return path.toString();
    }
}
