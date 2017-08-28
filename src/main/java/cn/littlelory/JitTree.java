package cn.littlelory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by littlelory on 2017/8/28.
 */
class JitTree extends JitObject {

    private List<Child> children;

    JitTree(String pathname) {
        super(pathname, JitObjectType.TREE);
        this.children = new ArrayList<>();
    }

    void addChild(JitObjectType type, String pathname, String fingerprint) {
        this.children.add(new Child(type, pathname, fingerprint));
    }

    @Override
    protected byte[] _encode() {
        List<byte[]> list = children.stream().map(Child::encode).collect(Collectors.toList());
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        buffer.put(SerializeUtil.encodeInt(list.size()));
        list.forEach(bytes -> ByteBufferUtil.fillBuffer(buffer, bytes));

        return ByteBufferUtil.getAll(buffer);
    }

    @Override
    public String toString() {
        return "JitTree{" +
                "type=" + type +
                ", pathname='" + pathname + '\'' +
                ", children=" + children +
                '}';
    }

    private static class Child {
        JitObjectType type;
        String pathname;
        String fingerprint;

        Child(JitObjectType type, String pathname, String fingerprint) {
            this.type = type;
            this.pathname = pathname;
            this.fingerprint = fingerprint;
        }

        byte[] encode() {
            ByteBuffer buffer = ByteBuffer.allocate(2048);
            buffer.put(SerializeUtil.encodeInt(type.getId()));
            ByteBufferUtil.fillBuffer(buffer, SerializeUtil.encodeStr(pathname));
            ByteBufferUtil.fillBuffer(buffer, SerializeUtil.encodeStr(fingerprint));
            return ByteBufferUtil.getAll(buffer);
        }
    }
}
