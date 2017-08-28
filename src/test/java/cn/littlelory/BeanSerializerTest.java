package cn.littlelory;

import org.junit.Test;

import java.lang.reflect.Field;

import static cn.littlelory.TestUtil.assertBytesEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by littlelory on 2017/8/24.
 */
public class BeanSerializerTest {
    private BeanSerializer beanSerializer = BeanSerializer.getInstance();

    @Test
    public void encode_int_field() throws NoSuchFieldException {
        NormalJitBean bean = new NormalJitBean();
        bean.setF1(Integer.MAX_VALUE);

        Class clz = NormalJitBean.class;
        Field field = clz.getDeclaredField("f1");

        byte[] actual = beanSerializer.encodeField(bean, field);

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

        byte[] actual = beanSerializer.encodeField(bean, field);

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

        byte[] actual = beanSerializer.encodeField(bean, field);

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

        Object result = beanSerializer.decodeField(field, bytes);
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

        Object result = beanSerializer.decodeField(field, bytes);
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

        Object result = beanSerializer.decodeField(field, bytes);
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

        byte[] actual = beanSerializer.encode(bean);

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
        NormalJitBean bean = beanSerializer.decode(clz, bytes);
        assertNotNull(bean);
        assertEquals("abc", bean.getKey());
        assertEquals(1024, bean.getF1());
        assertEquals(2048, bean.getF2());
    }
}
