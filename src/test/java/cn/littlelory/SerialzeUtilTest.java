package cn.littlelory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by littlelory on 2017/8/28.
 */
public class SerialzeUtilTest {
    @Test
    public void encode_positive_int_value() {
        int value = Integer.MAX_VALUE;

        byte[] expect = new byte[4];
        expect[3] = (byte) 0xff;
        expect[2] = (byte) 0xff;
        expect[1] = (byte) 0xff;
        expect[0] = (byte) 0x7f;

        byte[] actual = SerializeUtil.encodeInt(value);

        assertEquals(expect.length, actual.length);
        assertBytesEquals(expect, actual);
    }

    @Test
    public void encode_negative_int_value() {
        int value = Integer.MIN_VALUE;

        byte[] expect = new byte[4];
        expect[3] = (byte) 0x00;
        expect[2] = (byte) 0x00;
        expect[1] = (byte) 0x00;
        expect[0] = (byte) 0x80;

        byte[] actual = SerializeUtil.encodeInt(value);

        assertEquals(expect.length, actual.length);
        assertBytesEquals(expect, actual);
    }

    @Test
    public void decode_positive_int_value() {
        byte[] bytes = new byte[4];
        bytes[3] = (byte) 0xff;
        bytes[2] = (byte) 0xff;
        bytes[1] = (byte) 0xff;
        bytes[0] = (byte) 0x7f;

        assertEquals(Integer.MAX_VALUE, SerializeUtil.decodeInt(bytes));
    }

    @Test
    public void decode_negative_int_value() {
        byte[] bytes = new byte[4];
        bytes[3] = (byte) 0x00;
        bytes[2] = (byte) 0x00;
        bytes[1] = (byte) 0x00;
        bytes[0] = (byte) 0x80;

        assertEquals(Integer.MIN_VALUE, SerializeUtil.decodeInt(bytes));
    }

    @Test
    public void encode_positive_long_value() {
        long value = Long.MAX_VALUE;

        byte[] expect = new byte[8];
        expect[7] = (byte) 0xff;
        expect[6] = (byte) 0xff;
        expect[5] = (byte) 0xff;
        expect[4] = (byte) 0xff;
        expect[3] = (byte) 0xff;
        expect[2] = (byte) 0xff;
        expect[1] = (byte) 0xff;
        expect[0] = (byte) 0x7f;

        byte[] actual = SerializeUtil.encodeLong(value);

        assertEquals(expect.length, actual.length);
        assertBytesEquals(expect, actual);
    }

    @Test
    public void encode_negative_long_value() {
        long value = Long.MIN_VALUE;

        byte[] expect = new byte[8];
        expect[7] = (byte) 0x00;
        expect[6] = (byte) 0x00;
        expect[5] = (byte) 0x00;
        expect[4] = (byte) 0x00;
        expect[3] = (byte) 0x00;
        expect[2] = (byte) 0x00;
        expect[1] = (byte) 0x00;
        expect[0] = (byte) 0x80;

        byte[] actual = SerializeUtil.encodeLong(value);

        assertEquals(expect.length, actual.length);
        assertBytesEquals(expect, actual);
    }

    @Test
    public void decode_positive_long_value() {
        byte[] bytes = new byte[8];
        bytes[7] = (byte) 0xff;
        bytes[6] = (byte) 0xff;
        bytes[5] = (byte) 0xff;
        bytes[4] = (byte) 0xff;
        bytes[3] = (byte) 0xff;
        bytes[2] = (byte) 0xff;
        bytes[1] = (byte) 0xff;
        bytes[0] = (byte) 0x7f;

        assertEquals(Long.MAX_VALUE, SerializeUtil.decodeLong(bytes));
    }

    @Test
    public void decode_negative_long_value() {
        byte[] bytes = new byte[8];
        bytes[7] = (byte) 0x00;
        bytes[6] = (byte) 0x00;
        bytes[5] = (byte) 0x00;
        bytes[4] = (byte) 0x00;
        bytes[3] = (byte) 0x00;
        bytes[2] = (byte) 0x00;
        bytes[1] = (byte) 0x00;
        bytes[0] = (byte) 0x80;

        assertEquals(Long.MIN_VALUE, SerializeUtil.decodeLong(bytes));
    }

    @Test
    public void encode_str() {
        String value = "abc";

        byte[] expect = new byte[3];
        expect[0] = 'a';
        expect[1] = 'b';
        expect[2] = 'c';

        byte[] actual = SerializeUtil.encodeStr(value);

        assertEquals(expect.length, actual.length);
        assertBytesEquals(expect, actual);
    }

    @Test
    public void encode_null_str() {
        String value = null;
        byte[] actual = SerializeUtil.encodeStr(value);
        assertEquals(0, actual.length);
    }

    @Test
    public void encode_blank_str() {
        String value = "";
        byte[] actual = SerializeUtil.encodeStr(value);
        assertEquals(0, actual.length);
    }

    private void assertBytesEquals(byte[] expect, byte[] actual) {
        for (int i = 0; i < expect.length; i++)
            assertEquals(expect[i], actual[i]);
    }
}
