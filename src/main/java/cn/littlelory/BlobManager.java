package cn.littlelory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Created by littlelory on 29/08/2017.
 */
class BlobManager {
    private final String baseDirPath;
    private final String libDirPath;
    private final String logDirPath;
    private final String objectsDirPath;

    private final String headPath;
    private final String indexPath;
    private final String headLogPath;

    private static final String LIB_DIR = ".jit";

    private WorkSpace workSpace;
    private TempSpace tempSpace;
    private LocalSpace localSpace;

    BlobManager(String baseDirPath) {
        this.baseDirPath = baseDirPath;
        if (!FileUtil.exist(baseDirPath))
            throw new RuntimeException("directory[" + baseDirPath + "] is not found.");

        this.libDirPath = baseDirPath + "/" + LIB_DIR;
        this.headPath = this.libDirPath + "/HEAD";
        this.indexPath = this.libDirPath + "/tempSpace";
        this.logDirPath = this.libDirPath + "/logs";
        this.headLogPath = this.logDirPath + "/HEAD";
        this.objectsDirPath = this.libDirPath + "/objects";

        tempSpace = new TempSpace(this.baseDirPath, this.objectsDirPath, this.indexPath);
        workSpace = new WorkSpace(this.baseDirPath);
        localSpace = new LocalSpace(this.objectsDirPath, this.headPath, this.headLogPath);
    }

    void init() throws IOException {
        if (FileUtil.exist(libDirPath))
            throw new FileAlreadyExistsException("library directory[" + libDirPath + "] is already exist.");

        FileUtil.mkdir(libDirPath);
        FileUtil.mkdir(logDirPath);
        FileUtil.mkdir(objectsDirPath);
    }

    //todo rewrite this shit...
    List<StatusInfo> status() {
        List<FileEntry> entriesOfWork = workSpace.list();
        List<FileEntry> entriesOfTemp = tempSpace.list();
        Map<String, FileEntry> entriesOfLocal = localSpace.map();

        List<StatusInfo> result = new ArrayList<>();

        int indexOfWork = 0;
        int indexOfTemp = 0;
        while (true) {
            if (indexOfWork == entriesOfWork.size()) {
                IntStream.range(indexOfTemp, entriesOfTemp.size()).
                        mapToObj(entriesOfTemp::get)
                        .forEach(entry -> result.add(new StatusInfo(entry.getPathname(), StatusInfo.Status.DELETE)));
                break;
            } else if (indexOfTemp == entriesOfTemp.size()) {
                IntStream.range(indexOfWork, entriesOfWork.size()).
                        mapToObj(entriesOfWork::get)
                        .forEach(entry -> result.add(new StatusInfo(entry.getPathname(), StatusInfo.Status.UNTRACKED)));
                break;
            }

            FileEntry entryOfWork = entriesOfWork.get(indexOfWork);
            FileEntry entryOfTemp = entriesOfTemp.get(indexOfTemp);
            if (entryOfWork.getPathname().compareTo(entryOfTemp.getPathname()) < 0) { //work space have, but temp space not.
                result.add(new StatusInfo(entryOfWork.getPathname(), StatusInfo.Status.UNTRACKED));
                indexOfWork += 1;
            } else if (entryOfWork.getPathname().compareTo(entryOfTemp.getPathname()) > 0) { //work space don't have, but temp space have.
                result.add(new StatusInfo(entryOfWork.getPathname(), StatusInfo.Status.DELETE));
                indexOfTemp += 1;
            } else { //work space have, and temp space have too.
                if (!entryOfWork.getFingerprint().equals(entryOfTemp.getFingerprint())) { //work space's fingerprint != temp space fingerprint.
                    result.add(new StatusInfo(entryOfWork.getPathname(), StatusInfo.Status.MODIFITED));
                } else if (!entriesOfLocal.containsKey(entryOfTemp.getPathname()) || !entriesOfLocal.get(entryOfTemp.getPathname()).getFingerprint().equals(entryOfTemp.getFingerprint())) {
                    result.add(new StatusInfo(entryOfWork.getPathname(), StatusInfo.Status.ADDED));
                }
                indexOfWork += 1;
                indexOfTemp += 1;
            }
        }

        return result;
    }

    //todo how to process the condition that the pathname is not in untracked status?
    String add(String pathname) {
        byte[] dataBytes = workSpace.search(pathname);
        if (dataBytes != null) {
            return tempSpace.add(pathname);
        }
        return null;
    }

    String commit() {
        String head = tempSpace.build();
        localSpace.rebuild(head);
        return head;
    }

    void setWorkSpace(WorkSpace workSpace) {
        this.workSpace = workSpace;
    }

    void setTempSpace(TempSpace tempSpace) {
        this.tempSpace = tempSpace;
    }

    void setLocalSpace(LocalSpace localSpace) {
        this.localSpace = localSpace;
    }
}
