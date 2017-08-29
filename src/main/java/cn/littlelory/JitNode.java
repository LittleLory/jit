package cn.littlelory;

import java.util.Arrays;

/**
 * Created by littlelory on 2017/8/28.
 */
class JitNode extends JitObject {
    private byte[] data;

    public JitNode() {
    }

    JitNode(String pathname, byte[] data) {
        super(pathname, JitObjectType.NODE);
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
        JitNode node = (JitNode) o;
        return Arrays.equals(data, node.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    @Override
    public String toString() {
        return "JitNode{" +
                "data=" + Arrays.toString(data) +
                ", type=" + type +
                ", pathname='" + pathname + '\'' +
                '}';
    }
}
