package cn.littlelory;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by littlelory on 31/08/2017.
 */
class TempSpace {
    private final String baseDirPath;
    private final String objectDirPath;
    private final String indexPathname;
    private List<FileEntry> entries;

    TempSpace(String baseDirPath, String objectDirPath, String indexPathname) {
        this.baseDirPath = baseDirPath;
        this.objectDirPath = objectDirPath;
        this.indexPathname = indexPathname;
        readIndex();
    }

    List<FileEntry> list() {
        return entries;
    }

    Map<String, FileEntry> map() {
        return list().stream().collect(Collectors.toMap(FileEntry::getPathname, (fileEntry -> fileEntry)));
    }


    String search(String pathname) {
        return entries.stream().filter(entry -> entry.getPathname().equals(pathname)).map(FileEntry::getFingerprint).findFirst().orElse(null);
    }

    String add(String pathname) {
        assertNotExist(pathname);
        byte[] data = FileUtil.readBytes(baseDirPath + "/" + pathname);
        JitObject object = new JitObject(pathname, data);
        String fingerprint = BlobUtil.writeOBlob(objectDirPath, object);
        entries.add(new FileEntry(pathname, fingerprint));
        Collections.sort(entries);
        writeIndex();
        return fingerprint;
    }

    void update(String pathname) {
        assertExist(pathname);
        Optional<FileEntry> optional = entries.stream().filter(entry -> entry.getPathname().equals(pathname)).findFirst();
        FileEntry target = optional.orElseThrow(() -> new NoSuchIndexException("no such index of path[" + pathname + "]."));
        byte[] data = FileUtil.readBytes(baseDirPath + "/" + pathname);
        JitObject object = new JitObject(pathname, data);
        String fingerprint = BlobUtil.writeOBlob(objectDirPath, object);
        entries.remove(target);
        entries.add(new FileEntry(pathname, fingerprint));
        Collections.sort(entries);
        writeIndex();
    }

    void remove(String pathname) {
        assertExist(pathname);
        entries = entries.stream().filter(entry -> !entry.getPathname().equals(pathname)).collect(Collectors.toList());
        writeIndex();
    }

    private void assertExist(String pathname) {
        if (search(pathname) == null)
            throw new NoSuchIndexException("no such index of path[" + pathname + "].");
    }

    private void assertNotExist(String pathname) {
        if (search(pathname) != null)
            throw new IndexAlreadyExistExpetion("the index of path[" + pathname + "] is already exist.");
    }

    private void readIndex() {
        byte[] bytes = null;
        if (FileUtil.exist(indexPathname))
            bytes = FileUtil.readBytes(indexPathname);

        entries = new ArrayList<>();
        if (bytes != null && bytes.length > 0) {
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            while (buffer.remaining() > 0) {
                FileEntry entry = FileEntry.decode(ByteBufferUtil.getBuffer(buffer));
                entries.add(entry);
            }
        }
    }

    private void writeIndex() {
        FileUtil.writeBytes(indexPathname, new byte[]{});
        entries.stream().sorted().forEach(entry -> {
            ByteBuffer buffer = ByteBuffer.allocate(2048);
            ByteBufferUtil.fillBuffer(buffer, entry.encode());
            FileUtil.appendBytes(indexPathname, ByteBufferUtil.getAll(buffer));
        });
    }

    String build() {
        Node root = new Node("", NodeType.DIR);
        for (FileEntry fileEntry : entries) {
            List<String> paths = Arrays.asList(fileEntry.getPathname().split("/"));
            StringBuilder pathname = new StringBuilder();
            Node current = root;
            for (int i = 0; i < paths.size() - 1; i++) {
                String path = paths.get(i);
                pathname.append(path).append("/");
                Node dir = current.search(path);
                if (dir == null)
                    dir = new Node(pathname.toString(), NodeType.DIR);

                current.addChild(dir);
                current = dir;
            }
            pathname.append(paths.get(paths.size() - 1));
            Node file = new Node(pathname.toString(), NodeType.FILE);
            current.addChild(file);
        }

        walkAndWrite(root);
        return root.getFingerprint();
    }

    List<JitObject> load() {
        List<JitObject> objects = new ArrayList<>();
        for (FileEntry entry : entries)
            objects.add((JitObject) BlobUtil.loadBlob(objectDirPath, entry.getFingerprint(), JitBlobType.OBJECT));
        return objects;
    }

    void loadToIndex(String head) {
        this.entries = loadObjects(head);
        writeIndex();
    }

    private List<FileEntry> loadObjects(String head) {
        List<FileEntry> objects = new LinkedList<>();
        loadTree(objects, head);
        return objects;
    }

    private void loadTree(List<FileEntry> objects, String fingerprint) {
        JitTree tree = (JitTree) BlobUtil.loadBlob(objectDirPath, fingerprint, JitBlobType.TREE);
        for (JitTree.Child child : tree.getChildren()) {
            JitBlobType type = child.getType();
            if (type == JitBlobType.OBJECT)
                objects.add(new FileEntry(child.getPathname(), child.getFingerprint()));
            else if (type == JitBlobType.TREE)
                loadTree(objects, child.getFingerprint());
            else
                throw new RuntimeException("wrong jit blob type[" + type + "].");
        }
    }

    private void walkAndWrite(Node root) {
        if (root.getType() == NodeType.FILE) {
            JitBlob blob = new JitObject(root.getName(), FileUtil.readBytes(baseDirPath + "/" + root.getName()));
            String fingerprint = BlobUtil.writeOBlob(objectDirPath, blob);
            root.setBlob(blob);
            root.setFingerprint(fingerprint);
        } else {
            for (Node child : root.getChildren())
                walkAndWrite(child);
            JitTree jitTree = new JitTree(root.getName());
            for (Node child : root.getChildren())
                jitTree.addChild(child.getBlob().getType(), child.getName(), child.getFingerprint());
            String fingerprint = BlobUtil.writeOBlob(objectDirPath, jitTree);
            root.setBlob(jitTree);
            root.setFingerprint(fingerprint);
        }
    }

    private static final class Node {
        private String name;
        private String fingerprint;
        private NodeType type;
        private JitBlob blob;
        private List<Node> children;

        private Node(String name, NodeType type) {
            this.name = name;
            this.type = type;
            this.children = new ArrayList<>();
        }

        void addChild(Node node) {
            this.children.add(node);
        }

        Node search(String path) {
            return children.stream().filter(child -> child.getName().equals(path)).findFirst().orElse(null);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public NodeType getType() {
            return type;
        }

        public void setType(NodeType type) {
            this.type = type;
        }

        public JitBlob getBlob() {
            return blob;
        }

        public void setBlob(JitBlob blob) {
            this.blob = blob;
        }

        public List<Node> getChildren() {
            return children;
        }

        public void setChildren(List<Node> children) {
            this.children = children;
        }

        public String getFingerprint() {
            return fingerprint;
        }

        public void setFingerprint(String fingerprint) {
            this.fingerprint = fingerprint;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "name='" + name + '\'' +
                    ", fingerprint='" + fingerprint + '\'' +
                    ", type=" + type +
                    ", blob=" + blob +
                    ", children=" + children +
                    '}';
        }
    }

    private enum NodeType {
        DIR, FILE;
    }
}
