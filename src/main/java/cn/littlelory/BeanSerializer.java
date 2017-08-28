package cn.littlelory;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by littlelory on 2017/8/23.
 */
class BeanSerializer {
    private static final BeanSerializer INSTANCE = new BeanSerializer();
    static final int SERIALIZE_VERSION = 1;

    public static BeanSerializer getInstance() {
        return BeanSerializer.INSTANCE;
    }

    <T> byte[] encode(T t) {
        ByteBuffer buffer = ByteBuffer.allocate(2048);

        buffer.put(SerializeUtil.encodeInt(SERIALIZE_VERSION));

        Class clz = t.getClass();
        JitBean jitBean = ReflectUtil.getTypeAnnotation(clz, JitBean.class);
        byte[] beanName = SerializeUtil.encodeStr(jitBean.name());
        ByteBufferUtil.fillBuffer(buffer, beanName);

        Field keyField = ReflectUtil.getFieldByAnnotation(clz, JitKey.class);
        byte[] key = encodeField(t, keyField);
        ByteBufferUtil.fillBuffer(buffer, key);

        List<Field> fields = ReflectUtil.getFieldsByAnnotation(clz, JitField.class);
        orderByJitFieldSortValue(fields);

        buffer.put(SerializeUtil.encodeInt(fields.size()));

        fields.forEach(field -> ByteBufferUtil.fillBuffer(buffer, encodeField(t, field)));

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

        byte[] beanNameBytes = ByteBufferUtil.getBuffer(buffer);
        String beanName = SerializeUtil.decodeStr(beanNameBytes);

        JitBean jitBean = ReflectUtil.getTypeAnnotation(clz, JitBean.class);
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
            byte[] keyBytes = ByteBufferUtil.getBuffer(buffer);
            String key = SerializeUtil.decodeStr(keyBytes);
            Field keyField = ReflectUtil.getFieldByAnnotation(clz, JitKey.class);
            accessField(keyField);
            keyField.set(entity, key);

            int fieldCount = buffer.getInt();
            List<Field> fields = ReflectUtil.getFieldsByAnnotation(clz, JitField.class);
            orderByJitFieldSortValue(fields);
            fields = fields.subList(0, fieldCount);

            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                byte[] valueBytes = ByteBufferUtil.getBuffer(buffer);
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

    private static final int byteLen = 8;

    byte[] encodeField(Object obj, Field field) {
        try {
            accessField(field);
            Class type = field.getType();
            if (type == Integer.TYPE || type == Integer.class)
                return SerializeUtil.encodeInt(field.getInt(obj));
            else if (type == Long.TYPE || type == Long.class)
                return SerializeUtil.encodeLong(field.getLong(obj));
            else if (type == String.class)
                return SerializeUtil.encodeStr((String) field.get(obj));
            else
                throw new TypeNotSupportException("not support field type[" + type + "] now.");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    Object decodeField(Field field, byte[] bytes) {
        Class type = field.getType();
        if (type == Integer.TYPE || type == Integer.class)
            return SerializeUtil.decodeInt(bytes);
        else if (type == Long.TYPE || type == Long.class)
            return SerializeUtil.decodeLong(bytes);
        else if (type == String.class)
            return SerializeUtil.decodeStr(bytes);
        else
            throw new TypeNotSupportException("not support field type[" + type + "] now.");
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

}
