package cn.littlelory;

import java.util.Arrays;

/**
 * Created by littlelory on 2017/8/28.
 */
class JitObject extends JitBlob {
    private byte[] data;

    public JitObject() {
    }

    JitObject(String pathname, byte[] data) {
        super(pathname, JitBlobType.OBJECT);
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    protected byte[] _encode() {
        return data;
    }

    @Override
    protected void _decode(byte[] bytesOfBody) {
        this.data = bytesOfBody;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JitObject node = (JitObject) o;
        return Arrays.equals(data, node.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    @Override
    public String toString() {
        return "JitObject{" +
                "data=" + Arrays.toString(data) +
                ", type=" + type +
                ", pathname='" + pathname + '\'' +
                '}';
    }
}
