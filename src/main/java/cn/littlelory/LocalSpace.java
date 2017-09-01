package cn.littlelory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class LocalSpace {
    private final String objectsDirPath;
    private final String headPath;

    private String head;
    private Map<String, JitBlob> blobs;

    LocalSpace(String objectsDirPath, String headPath) {
        this.objectsDirPath = objectsDirPath;
        this.headPath = headPath;

        if (FileUtil.exist(headPath)) {
            this.head = FileUtil.readStr(headPath);
            blobs = new HashMap<>();
            walk(head, JitBlobType.TREE);
        }
    }

    private void walk(String fingerprint, JitBlobType type) {
        String blobPath = objectsDirPath + "/" + fingerprint.substring(0, 2) + "/" + fingerprint.substring(2);
        if (type == JitBlobType.OBJECT) {
            JitObject object = new JitObject();
            object.decode(FileUtil.readBytes(blobPath));
            blobs.put(fingerprint, object);
        } else if (type == JitBlobType.TREE) {
            JitTree tree = new JitTree();
            tree.decode(FileUtil.readBytes(blobPath));
            tree.getChildren().forEach(child -> walk(child.getFingerprint(), child.getType()));
        } else {
            System.out.println("skip: fingerprint[" + fingerprint + "], type[" + type + "].");
        }
    }

    List<FileEntry> list() {
        return blobs.entrySet().stream()
                .filter(entry -> entry.getValue().getType() == JitBlobType.OBJECT)
                .map(entry -> new FileEntry(entry.getValue().getPathname(), entry.getKey()))
                .sorted()
                .collect(Collectors.toList());
    }

    Map<String, FileEntry> map() {
        return list().stream().collect(Collectors.toMap(FileEntry::getPathname, (fileEntry -> fileEntry)));
    }
}
