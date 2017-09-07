package cn.littlelory;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by littlelory on 2017/8/28.
 */
class JitTree extends JitBlob {

    private List<Child> children;

    JitTree() {

    }

    JitTree(String pathname) {
        super(pathname, JitBlobType.TREE);
        this.children = new ArrayList<>();
    }

    void addChild(JitBlobType type, String pathname, String fingerprint) {
        this.children.add(new Child(type, pathname, fingerprint));
    }

    List<Child> getChildren() {
        List<Child> result = Arrays.asList(new Child[children.size()]);
        Collections.copy(result, children);
        return result;
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
    protected void _decode(byte[] bytesOfBody) {
        ByteBuffer buffer = ByteBuffer.wrap(bytesOfBody);
        int childrenCount = buffer.getInt();
        this.children = IntStream.range(0, childrenCount)
                .mapToObj(i -> {
                    byte[] bytesOfChild = ByteBufferUtil.getBuffer(buffer);
                    Child child = new Child();
                    child.decode(bytesOfChild);
                    return child;
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JitTree tree = (JitTree) o;
        return Objects.equals(children, tree.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(children);
    }

    @Override
    public String toString() {
        return "JitTree{" +
                "type=" + type +
                ", pathname='" + pathname + '\'' +
                ", children=" + children +
                '}';
    }

    static class Child {
        JitBlobType type;
        String pathname;
        String fingerprint;

        Child() {
        }

        Child(JitBlobType type, String pathname, String fingerprint) {
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

        void decode(byte[] bytes) {
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            this.type = JitBlobType.getTypeById(buffer.getInt());
            this.pathname = SerializeUtil.decodeStr(ByteBufferUtil.getBuffer(buffer));
            this.fingerprint = SerializeUtil.decodeStr(ByteBufferUtil.getBuffer(buffer));
        }

        public JitBlobType getType() {
            return type;
        }

        public String getPathname() {
            return pathname;
        }

        public String getFingerprint() {
            return fingerprint;
        }
    }
}
