package cn.littlelory;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by littlelory on 2017/8/25.
 */
class FileUtil {

    static void writeBytes(String pathname, byte[] bytes) {
        try {
            Files.write(path(pathname), bytes);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    static void writeStr(String pathname, String str) {
        byte[] bytes = str.getBytes();
        writeBytes(pathname, bytes);
    }

    static void appendBytes(String pathname, byte[] bytes) {
        try {
            Files.write(path(pathname), bytes, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    static byte[] readBytes(String pathname) {
        try {
            return Files.readAllBytes(path(pathname));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    static String readStr(String pathname) {
        byte[] bytes = readBytes(pathname);
        return new String(bytes);
    }

    static void appendStr(String pathname, String str) {
        byte[] bytes = str.getBytes();
        appendBytes(pathname, bytes);
    }

    static boolean exist(String pathname) {
        return Files.exists(path(pathname));
    }

    static void mkdir(String pathname) {
        try {
            Files.createDirectory(path(pathname));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    static void mkdirIfNotExist(String pathname) {
        if (!exist(pathname))
            mkdir(pathname);
    }

    static void remove(String pathname) {
        try {
            Files.delete(Paths.get(pathname));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static List<String> list(String dirPath) {
        try {
            return Files.list(Paths.get(dirPath)).map(path -> path.getFileName().toString()).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
