package cn.littlelory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by littlelory on 2017/8/25.
 */
class FileUtil {

    static void write(String pathname, byte[] bytes) throws IOException {
        if (exist(pathname))
            throw new IOException("the file[" + pathname + "] is exist.");

        Files.write(path(pathname), bytes);
    }

    static byte[] read(String pathname) throws IOException {
        return Files.readAllBytes(path(pathname));
    }

    static boolean exist(String pathname) {
        return Files.exists(path(pathname));
    }

    static void mkdir(String pathname) throws IOException {
        Files.createDirectory(path(pathname));
    }

    private static Path path(String pathname) {
        return Paths.get(pathname);
    }

    private static String pathname(Path path) {
        return path.toString();
    }
}
