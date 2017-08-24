package cn.littlelory;

import org.junit.Test;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import static org.junit.Assert.*;

/**
 * Created by littlelory on 2017/8/24.
 */
public class SerializerTest {
    private Serializer serializer = Serializer.getInstance();

    @Test
    public void encode_positive_int_value() {
        int value = Integer.MAX_VALUE;

        byte[] expect = new byte[4];
        expect[3] = (byte) 0xff;
        expect[2] = (byte) 0xff;
        expect[1] = (byte) 0xff;
        expect[0] = (byte) 0x7f;

        byte[] actual = serializer.encodeInt(value);

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

        byte[] actual = serializer.encodeInt(value);

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

        assertEquals(Integer.MAX_VALUE, serializer.decodeInt(bytes));
    }

    @Test
    public void decode_negative_int_value() {
        byte[] bytes = new byte[4];
        bytes[3] = (byte) 0x00;
        bytes[2] = (byte) 0x00;
        bytes[1] = (byte) 0x00;
        bytes[0] = (byte) 0x80;

        assertEquals(Integer.MIN_VALUE, serializer.decodeInt(bytes));
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

        byte[] actual = serializer.encodeLong(value);

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

        byte[] actual = serializer.encodeLong(value);

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

        assertEquals(Long.MAX_VALUE, serializer.decodeLong(bytes));
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

        assertEquals(Long.MIN_VALUE, serializer.decodeLong(bytes));
    }

    @Test
    public void encode_str() {
        String value = "abc";

        byte[] expect = new byte[3];
        expect[0] = 'a';
        expect[1] = 'b';
        expect[2] = 'c';

        byte[] actual = serializer.encodeStr(value);

        assertEquals(expect.length, actual.length);
        assertBytesEquals(expect, actual);
    }

    @Test
    public void encode_null_str() {
        String value = null;
        byte[] actual = serializer.encodeStr(value);
        assertEquals(0, actual.length);
    }

    @Test
    public void encode_blank_str() {
        String value = "";
        byte[] actual = serializer.encodeStr(value);
        assertEquals(0, actual.length);
    }


    @Test
    public void encode_int_field() throws NoSuchFieldException {
        NormalJitBean bean = new NormalJitBean();
        bean.setF1(Integer.MAX_VALUE);

        Class clz = NormalJitBean.class;
        Field field = clz.getDeclaredField("f1");

        byte[] actual = serializer.encodeField(bean, field);

        byte[] expect = new byte[4];
        expect[3] = (byte) 0xff;
        expect[2] = (byte) 0xff;
        expect[1] = (byte) 0xff;
        expect[0] = (byte) 0x7f;

        assertBytesEquals(expect, actual);
    }

    @Test
    public void encode_long_field() throws NoSuchFieldException {
        NormalJitBean bean = new NormalJitBean();
        bean.setF2(Long.MAX_VALUE);

        Class clz = NormalJitBean.class;
        Field field = clz.getDeclaredField("f2");

        byte[] actual = serializer.encodeField(bean, field);

        byte[] expect = new byte[8];
        expect[7] = (byte) 0xff;
        expect[6] = (byte) 0xff;
        expect[5] = (byte) 0xff;
        expect[4] = (byte) 0xff;
        expect[3] = (byte) 0xff;
        expect[2] = (byte) 0xff;
        expect[1] = (byte) 0xff;
        expect[0] = (byte) 0x7f;

        assertBytesEquals(expect, actual);
    }

    @Test
    public void encode_str_field() throws NoSuchFieldException {
        NormalJitBean bean = new NormalJitBean();
        bean.setKey("abc");

        Class clz = NormalJitBean.class;
        Field field = clz.getDeclaredField("key");

        byte[] actual = serializer.encodeField(bean, field);

        byte[] expect = new byte[3];
        expect[0] = 'a';
        expect[1] = 'b';
        expect[2] = 'c';

        assertBytesEquals(expect, actual);
    }

    @Test
    public void decode_int_field() throws NoSuchFieldException {
        Class clz = NormalJitBean.class;
        Field field = clz.getDeclaredField("f1");

        byte[] bytes = new byte[4];
        bytes[3] = (byte) 0xff;
        bytes[2] = (byte) 0xff;
        bytes[1] = (byte) 0xff;
        bytes[0] = (byte) 0x7f;

        Object result = serializer.decodeField(field, bytes);
        assertEquals(Integer.class, result.getClass());
        assertEquals(Integer.MAX_VALUE, result);
    }

    @Test
    public void decode_long_field() throws NoSuchFieldException {
        Class clz = NormalJitBean.class;
        Field field = clz.getDeclaredField("f2");

        byte[] bytes = new byte[8];
        bytes[7] = (byte) 0xff;
        bytes[6] = (byte) 0xff;
        bytes[5] = (byte) 0xff;
        bytes[4] = (byte) 0xff;
        bytes[3] = (byte) 0xff;
        bytes[2] = (byte) 0xff;
        bytes[1] = (byte) 0xff;
        bytes[0] = (byte) 0x7f;

        Object result = serializer.decodeField(field, bytes);
        assertEquals(Long.class, result.getClass());
        assertEquals(Long.MAX_VALUE, result);
    }

    @Test
    public void decode_str_field() throws NoSuchFieldException {
        Class clz = NormalJitBean.class;
        Field field = clz.getDeclaredField("key");

        byte[] bytes = new byte[3];
        bytes[0] = 'a';
        bytes[1] = 'b';
        bytes[2] = 'c';

        Object result = serializer.decodeField(field, bytes);
        assertEquals(String.class, result.getClass());
        assertEquals("abc", result);
    }

    @Test
    public void encode() {
        NormalJitBean bean = new NormalJitBean();
        bean.setKey("abc");
        bean.setF1(1024);
        bean.setF2(2048L);

        byte[] expect = new byte[]{
                0x0, 0x0, 0x0, 0x1,//version
                0x0, 0x0, 0x0, 0xd,//bean name size
                0x4E, 0x6F, 0x72, 0x6D, 0x61, 0x6C, 0x4A, 0x69, 0x74, 0x42, 0x65, 0x61, 0x6E,//bean name
                0x0, 0x0, 0x0, 0x3,//key size
                0x61, 0x62, 0x63,//key
                0x0, 0x0, 0x0, 0x2,//field count
                0x0, 0x0, 0x0, 0x4,//f1 size
                0x0, 0x0, 0x4, 0x0,//f1 value
                0x0, 0x0, 0x0, 0x8,//f2 size
                0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x8, 0x0//f2 value
        };

        byte[] actual = serializer.encode(bean);

        assertBytesEquals(expect, actual);
    }

    @Test
    public void decode() {
        byte[] bytes = new byte[]{
                0x0, 0x0, 0x0, 0x1,//version
                0x0, 0x0, 0x0, 0xd,//bean name size
                0x4E, 0x6F, 0x72, 0x6D, 0x61, 0x6C, 0x4A, 0x69, 0x74, 0x42, 0x65, 0x61, 0x6E,//bean name
                0x0, 0x0, 0x0, 0x3,//key size
                0x61, 0x62, 0x63,//key
                0x0, 0x0, 0x0, 0x2,//field count
                0x0, 0x0, 0x0, 0x4,//f1 size
                0x0, 0x0, 0x4, 0x0,//f1 value
                0x0, 0x0, 0x0, 0x8,//f2 size
                0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x8, 0x0//f2 value
        };

        Class<NormalJitBean> clz = NormalJitBean.class;
        NormalJitBean bean = serializer.decode(clz, bytes);
        assertNotNull(bean);
        assertEquals("abc", bean.getKey());
        assertEquals(1024, bean.getF1());
        assertEquals(2048, bean.getF2());
    }

    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    public String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append("0x");
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
            r.append(",");
        }
        return r.toString();
    }

    private void assertBytesEquals(byte[] expect, byte[] actual) {
        for (int i = 0; i < expect.length; i++)
            assertEquals(expect[i], actual[i]);
    }
}
