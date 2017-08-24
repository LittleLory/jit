package cn.littlelory;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by littlelory on 2017/8/23.
 */
class Serializer {
    private static final Serializer INSTANCE = new Serializer();
    static final int SERIALIZE_VERSION = 1;

    private ReflectUtil reflectUtil = ReflectUtil.getINSTANCE();

    public void setReflectUtil(ReflectUtil reflectUtil) {
        this.reflectUtil = reflectUtil;
    }

    public static Serializer getInstance() {
        return Serializer.INSTANCE;
    }

    <T> byte[] encode(T t) {
        ByteBuffer buffer = ByteBuffer.allocate(2048);

        buffer.put(encodeInt(SERIALIZE_VERSION));

        Class clz = t.getClass();
        JitBean jitBean = reflectUtil.getTypeAnnotation(clz, JitBean.class);
        byte[] beanName = encodeStr(jitBean.name());
        fillBuffer(buffer, beanName);

        Field keyField = reflectUtil.getFieldByAnnotation(clz, JitKey.class);
        byte[] key = encodeField(t, keyField);
        fillBuffer(buffer, key);

        List<Field> fields = reflectUtil.getFieldsByAnnotation(clz, JitField.class);
        orderByJitFieldSortValue(fields);

        buffer.put(encodeInt(fields.size()));

        fields.forEach(field -> fillBuffer(buffer, encodeField(t, field)));

        int position = buffer.position();
        buffer.rewind();
        byte[] result = new byte[position];
        buffer.get(result, 0, position);
        return result;
    }

    <T> T decode(Class<T> clz, byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        //todo version compatible
        int serializeVersion = buffer.getInt();

        byte[] beanNameBytes = getBuffer(buffer);
        String beanName = decodeStr(beanNameBytes);

        JitBean jitBean = reflectUtil.getTypeAnnotation(clz, JitBean.class);
        String actualName = jitBean.name();
        if (!beanName.equals(actualName))
            throw new BeanNotMatchException("try to decode the entity of bean[" + beanName + "], but byte data is a entity of class[" + actualName + "].");

        T entity;
        try {
            entity = clz.newInstance();
        } catch (InstantiationException e) {
            throw new ConstructionException("an exception occurred when execute the construction of Class["+clz+"].", e);
        } catch (IllegalAccessException e) {
            throw new ConstructionException("the construction of Class["+clz+"] is not accessible.");
        }

        try {
            byte[] keyBytes = getBuffer(buffer);
            String key = decodeStr(keyBytes);
            Field keyField = reflectUtil.getFieldByAnnotation(clz, JitKey.class);
            accessField(keyField);
            keyField.set(entity, key);

            int fieldCount = buffer.getInt();
            List<Field> fields = reflectUtil.getFieldsByAnnotation(clz, JitField.class);
            orderByJitFieldSortValue(fields);
            fields = fields.subList(0, fieldCount);

            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                byte[] valueBytes = getBuffer(buffer);
                Object value = decodeField(field, valueBytes);
                accessField(field);
                field.set(entity, value);
            }
        } catch (IllegalAccessException e) {
            //todo how to process on this condition？
            throw new RuntimeException(e);
        }

        return entity;
    }

    private void fillBuffer(ByteBuffer buffer, byte[] bytes) {
        int size = bytes.length;
        buffer.put(encodeInt(size));
        buffer.put(bytes);
    }

    private byte[] getBuffer(ByteBuffer buffer) {
        int size = buffer.getInt();
        byte[] bytes = new byte[size];
        buffer.get(bytes);
        return bytes;
    }

    private static final int byteLen = 8;

    byte[] encodeField(Object obj, Field field) {
        try {
            accessField(field);
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

    Object decodeField(Field field, byte[] bytes) {
        Class type = field.getType();
        if (type == Integer.TYPE || type == Integer.class)
            return decodeInt(bytes);
        else if (type == Long.TYPE || type == Long.class)
            return decodeLong(bytes);
        else if (type == String.class)
            return decodeStr(bytes);
        else
            throw new TypeNotSupportException("not support field type[" + type + "] now.");
    }

    private static final int INT_LEN = 4;
    byte[] encodeInt(Integer value) {
        byte[] result = new byte[INT_LEN];
        for (int i = 0; i < INT_LEN; i++)
            result[i] = (byte) ((value >> (INT_LEN - i - 1) * byteLen) & 0xff);
        return result;
    }

    int decodeInt(byte[] bytes) {
        int result = 0;
        for (int i = 0; i < bytes.length; i++) {
            result <<= byteLen;
            result |= (bytes[i]&0x0ff);
        }
        return result;
    }

    private static final int LONG_LEN = 8;
    byte[] encodeLong(Long value) {
        byte[] result = new byte[LONG_LEN];
        for (int i = 0; i < LONG_LEN; i++)
            result[i] = (byte) ((value >> (LONG_LEN - i - 1) * byteLen) & 0xff);
        return result;
    }

    long decodeLong(byte[] bytes) {
        long result = 0L;
        for (int i = 0; i < bytes.length; i++) {
            result <<= byteLen;
            result |= bytes[i]&0x0ff;
        }
        return result;
    }

    byte[] encodeStr(String value) {
        if (value == null || value.length() == 0)
            return new byte[0];
        return value.getBytes(charset());
    }

    String decodeStr(byte[] bytes) {
        return new String(bytes, charset());
    }

    private void accessField(Field field) {
        field.setAccessible(true);
    }

    private void orderByJitFieldSortValue(List<Field> fields) {
        fields.sort((o1, o2) -> {
            JitField anno1 = o1.getDeclaredAnnotation(JitField.class);
            JitField anno2 = o2.getDeclaredAnnotation(JitField.class);
            return anno1.sort() - anno2.sort();
        });
    }

    private Charset charset() {
        return Charset.forName("utf8");
    }
}
