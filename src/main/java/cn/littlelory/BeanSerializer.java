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

    private ReflectUtil reflectUtil = ReflectUtil.getINSTANCE();
    private SerializeUtil serializeUtil = SerializeUtil.getINSTANCE();

    public void setReflectUtil(ReflectUtil reflectUtil) {
        this.reflectUtil = reflectUtil;
    }

    public static BeanSerializer getInstance() {
        return BeanSerializer.INSTANCE;
    }

    <T> byte[] encode(T t) {
        ByteBuffer buffer = ByteBuffer.allocate(2048);

        buffer.put(serializeUtil.encodeInt(SERIALIZE_VERSION));

        Class clz = t.getClass();
        JitBean jitBean = reflectUtil.getTypeAnnotation(clz, JitBean.class);
        byte[] beanName = serializeUtil.encodeStr(jitBean.name());
        fillBuffer(buffer, beanName);

        Field keyField = reflectUtil.getFieldByAnnotation(clz, JitKey.class);
        byte[] key = encodeField(t, keyField);
        fillBuffer(buffer, key);

        List<Field> fields = reflectUtil.getFieldsByAnnotation(clz, JitField.class);
        orderByJitFieldSortValue(fields);

        buffer.put(serializeUtil.encodeInt(fields.size()));

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
        String beanName = serializeUtil.decodeStr(beanNameBytes);

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
            String key = serializeUtil.decodeStr(keyBytes);
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
        buffer.put(serializeUtil.encodeInt(size));
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
                return serializeUtil.encodeInt(field.getInt(obj));
            else if (type == Long.TYPE || type == Long.class)
                return serializeUtil.encodeLong(field.getLong(obj));
            else if (type == String.class)
                return serializeUtil.encodeStr((String) field.get(obj));
            else
                throw new TypeNotSupportException("not support field type[" + type + "] now.");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    Object decodeField(Field field, byte[] bytes) {
        Class type = field.getType();
        if (type == Integer.TYPE || type == Integer.class)
            return serializeUtil.decodeInt(bytes);
        else if (type == Long.TYPE || type == Long.class)
            return serializeUtil.decodeLong(bytes);
        else if (type == String.class)
            return serializeUtil.decodeStr(bytes);
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
