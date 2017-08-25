package cn.littlelory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by littlelory on 2017/8/25.
 */
public class FingerprintTest {
    @Test
    public void generate() {
        String value = "JitBeanName";

        String expect = "8fc6588f3e1d02875766ece925051422d7bf41bf";
        String actual = Fingerprint.generate(value.getBytes());

        assertEquals(expect, actual);
    }

    @Test(expected = NullPointerException.class)
    public void input_is_null() {
        Fingerprint.generate(null);
    }
}
