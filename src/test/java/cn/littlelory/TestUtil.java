package cn.littlelory;

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
}
