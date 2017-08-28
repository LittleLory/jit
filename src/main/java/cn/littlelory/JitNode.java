package cn.littlelory;

import java.util.Arrays;

/**
 * Created by littlelory on 2017/8/28.
 */
class JitNode extends JitObject {
    private byte[] data;

    JitNode(String pathname, byte[] data) {
        super(pathname, JitObjectType.NODE);
        this.data = data;
    }

    @Override
    protected byte[] _encode() {
        return data;
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
