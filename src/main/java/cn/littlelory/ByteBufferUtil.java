package cn.littlelory;

import java.nio.ByteBuffer;

/**
 * Created by littlelory on 2017/8/28.
 */
class ByteBufferUtil {
    static void fillBuffer(ByteBuffer buffer, byte[] bytes) {
        int size = bytes.length;
        buffer.put(SerializeUtil.encodeInt(size));
        buffer.put(bytes);
    }

    static byte[] getBuffer(ByteBuffer buffer) {
        int size = buffer.getInt();
        byte[] bytes = new byte[size];
        buffer.get(bytes);
        return bytes;
    }

    static byte[] getAll(ByteBuffer buffer) {
        int position = buffer.position();
        buffer.rewind();
        byte[] result = new byte[position];
        buffer.get(result, 0, position);
        return result;
    }
}
