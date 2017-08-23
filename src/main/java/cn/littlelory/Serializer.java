package cn.littlelory;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by littlelory on 2017/8/23.
 */
class Serializer {
    private static final Serializer INSTANCE = new Serializer();
    private static final int CURRENT_SERIALIZE_VERSION = 1;

    private ReflectUtil reflectUtil = ReflectUtil.getINSTANCE();

    public void setReflectUtil(ReflectUtil reflectUtil) {
        this.reflectUtil = reflectUtil;
    }

    Serializer getInstance() {
        return Serializer.INSTANCE;
    }

    <T> byte[] encode(T t) {
        ByteBuffer buffer = ByteBuffer.allocate(2048);

        buffer.put(encodeInt(CURRENT_SERIALIZE_VERSION));

        Class clz = t.getClass();
        JitBean jitBean = reflectUtil.getTypeAnnotation(clz, JitBean.class);
        byte[] beanName = encodeStr(jitBean.name());
        fillBuffer(buffer, beanName);

        Field keyField = reflectUtil.getFieldsByAnnotation(clz, JitKey.class).get(0);
        byte[] key = encodeField(t, keyField);
        fillBuffer(buffer, key);

        List<Field> fields = reflectUtil.getFieldsByAnnotation(clz, JitField.class);
        fields.sort((o1, o2) -> {
            JitField anno1 = o1.getDeclaredAnnotation(JitField.class);
            JitField anno2 = o2.getDeclaredAnnotation(JitField.class);
            return anno1.sort() - anno2.sort();
        });

        fields.forEach(field -> fillBuffer(buffer, encodeField(t, field)));

        buffer.rewind();
        return buffer.array();
    }

    <T> T decode(Class<T> clz, byte[] bytes) {
        //todo so tired, to write the logic of decoding tomorrow...
        return null;
    }

    private void fillBuffer(ByteBuffer buffer, byte[] bytes) {
        int size = bytes.length;
        buffer.put(encodeInt(size));
        buffer.put(bytes);
    }

    private static final int byteLen = 8;

    private byte[] encodeField(Object obj, Field field) {
        try {
            field.setAccessible(true);
            Class type = field.getType();
            if (type == Integer.TYPE || type == Integer.class)
                return encodeInt(field.getInt(obj));
            else if (type == Long.TYPE || type == Long.class)
                return encodeLong(field.getLong(obj));
            else if (type == String.class)
                return encodeStr((String) field.get(obj));
            else
                throw new TypeNotSupportException("not support field type[" + type + "] now.");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] encodeInt(Integer value) {
        byte[] result = new byte[4];
        for (int i = 0; i < 4; i++)
            result[i] = (byte) (value >> (3 - i) * byteLen);
        return result;
    }

    private byte[] encodeLong(Long value) {
        byte[] result = new byte[8];
        for (int i = 0; i < 8; i++)
            result[i] = (byte) (value >> (7 - i) * byteLen);
        return result;
    }

    private byte[] encodeStr(String value) {
        return value.getBytes(Charset.forName("utf8"));
    }
}
