package cn.littlelory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by littlelory on 2017/8/25.
 */
public class FingerprintTest {
    @Test
    public void generate_from_str() {
        String value = "JitBeanName";

        String expect = "8fc6588f3e1d02875766ece925051422d7bf41bf";
        String actual = Fingerprint.generate(value.getBytes());

        assertEquals(expect, actual);
    }

    @Test
    public void generate_from_bytes() {
        byte[] bytes = new byte[4];
        bytes[3] = (byte) 0x00;
        bytes[2] = (byte) 0x00;
        bytes[1] = (byte) 0x00;
        bytes[0] = (byte) 0x80;

        String expect = "e1798367d1b1ed54425e99f790a986c1fe939414";
        String actual = Fingerprint.generate(bytes);

        assertEquals(expect, actual);
    }

    @Test(expected = NullPointerException.class)
    public void input_is_null() {
        Fingerprint.generate(null);
    }
}
