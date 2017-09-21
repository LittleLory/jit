package cn.littlelory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class LocalSpace {
    private final String objectsDirPath;
    private final String headPath;
    private final String headLogPath;

    private String head;
    private Map<String, JitBlob> blobs;

    LocalSpace(String objectsDirPath, String headPath, String headLogPath) {
        this.objectsDirPath = objectsDirPath;
        this.headPath = headPath;
        blobs = new HashMap<>();

        if (FileUtil.exist(headPath)) {
            this.head = FileUtil.readStr(headPath);
            walk(head, JitBlobType.TREE);
        }

        this.headLogPath = headLogPath;
    }

    private void walk(String fingerprint, JitBlobType type) {
        JitBlob blob = BlobUtil.loadBlob(objectsDirPath, fingerprint, type);
        if (type == JitBlobType.OBJECT) {
            blobs.put(fingerprint, blob);
        } else if (type == JitBlobType.TREE) {
            JitTree tree = (JitTree) blob;
            tree.getChildren().forEach(child -> walk(child.getFingerprint(), child.getType()));
        } else {
            System.out.println("skip: fingerprint[" + fingerprint + "], type[" + type + "].");
        }
    }

    void rebuild(String head) {
        this.head = head;
        walk(head, JitBlobType.TREE);
        String headBefore = updateHEAD(head);
        updateHEADLog(headBefore, head);
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

    List<HeadLogInfo> headLogInfos() {
        if (FileUtil.exist(headLogPath)) {
            String lines = FileUtil.readStr(headLogPath);
            if (lines.length() > 0)
                return Stream.of(lines.split("\n")).map(line -> line.split("\t")).map(arr -> new HeadLogInfo(arr[0], arr[1], Long.parseLong(arr[2]))).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }



    private String updateHEAD(String fingerprint) {
        String headBefore = FileUtil.exist(headPath) ? FileUtil.readStr(headPath) : "0000000000000000000000000000000000000000";
        FileUtil.writeStr(headPath, fingerprint);
        return headBefore;
    }

    private void updateHEADLog(String oldHead, String newHead) {
        HeadLogInfo logInfo = new HeadLogInfo(oldHead, newHead, System.currentTimeMillis());
        if (FileUtil.exist(headLogPath))
            FileUtil.appendStr(headLogPath, logInfo.toString());
        else
            FileUtil.writeStr(headLogPath, logInfo.toString());
    }
}
