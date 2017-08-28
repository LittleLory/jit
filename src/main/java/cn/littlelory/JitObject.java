package cn.littlelory;

import java.nio.ByteBuffer;

/**
 * Created by littlelory on 2017/8/28.
 */
abstract class JitObject {

    JitObjectType type;

    String pathname;

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

    public String getPathname() {
        return pathname;
    }

    public JitObjectType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "JitObject{" +
                "type=" + type +
                ", pathname='" + pathname + '\'' +
                '}';
    }
}
