package cn.littlelory;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by littlelory on 2017/8/28.
 */
abstract class JitObject {

    JitObjectType type;

    String pathname;

    JitObject() {
    }

    JitObject(String pathname, JitObjectType type) {
        assert pathname != null && pathname.length() > 0 && pathname.length() < 128;
        this.pathname = pathname;
        this.type = type;
    }

    protected abstract byte[] _encode();

    byte[] encode() {
        byte[] bytesOfType = SerializeUtil.encodeInt(type.getId());
        byte[] bytesOfPath = SerializeUtil.encodeStr(pathname);
        byte[] bytesOfBody = _encode();

        //todo which performance is higher between using byte array and ByteBuffer?
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        buffer.put(bytesOfType);
        ByteBufferUtil.fillBuffer(buffer, bytesOfPath);
        ByteBufferUtil.fillBuffer(buffer, bytesOfBody);

        return ByteBufferUtil.getAll(buffer);
    }

    protected abstract void _decode(byte[] bytesOfBody);

    void decode(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int typeId = buffer.getInt();
        JitObjectType type = JitObjectType.getTypeById(typeId);
        byte[] bytesOfPathname = ByteBufferUtil.getBuffer(buffer);
        String pathname = SerializeUtil.decodeStr(bytesOfPathname);
        byte[] bytesOfBody = ByteBufferUtil.getBuffer(buffer);

        this.type = type;
        this.pathname = pathname;
        _decode(bytesOfBody);
    }

    static JitObjectType typeOf(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int typeId = buffer.getInt();
        return JitObjectType.getTypeById(typeId);
    }

    String getPathname() {
        return pathname;
    }

    JitObjectType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JitObject object = (JitObject) o;
        return type == object.type &&
                Objects.equals(pathname, object.pathname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, pathname);
    }

    @Override
    public String toString() {
        return "JitObject{" +
                "type=" + type +
                ", pathname='" + pathname + '\'' +
                '}';
    }
}
