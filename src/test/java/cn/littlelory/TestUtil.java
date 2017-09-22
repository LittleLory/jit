package cn.littlelory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

/**
 * Created by littlelory on 2017/8/28.
 */
class TestUtil {
    static void assertBytesEquals(byte[] expect, byte[] actual) {
        for (int i = 0; i < expect.length; i++)
            try {
                assertEquals(expect[i], actual[i]);
            } catch (AssertionError e) {
                throw new AssertionError(e.getMessage() + "\nindex\t:" + i + "\n", e);
            }
    }

    static String resourcesPath() {
        return TestUtil.class.getResource("/").getPath();
    }

    static void deleteIfExists(Path path) {
        try {
            if (!Files.exists(path))
                return;

            if (Files.isDirectory(path))
                Files.list(path).forEach(TestUtil::deleteIfExists);

            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void deleteChildrenIfExists(Path path) {
        try {
            if (!Files.exists(path))
                return;

            if (Files.isDirectory(path))
                Files.list(path).forEach(TestUtil::deleteIfExists);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
