package cn.littlelory;

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
    List<StatusInfo> tempStatus() {
        List<FileEntry> entriesOfWork = workSpace.list();
        List<FileEntry> entriesOfTemp = tempSpace.list();
//        Map<String, FileEntry> entriesOfLocal = localSpace.map();

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
                }
                indexOfWork += 1;
                indexOfTemp += 1;
            }
        }

        return result;
    }
    
    List<StatusInfo> localStatus() {
        List<FileEntry> entriesOfTemp = tempSpace.list();
        List<FileEntry> entriesOfLocal = localSpace.list();

        List<StatusInfo> result = new ArrayList<>();

        int indexOfLocal = 0;
        int indexOfTemp = 0;
        while (true) {
            if (indexOfLocal == entriesOfLocal.size()) {
                IntStream.range(indexOfTemp, entriesOfTemp.size()).
                        mapToObj(entriesOfTemp::get)
                        .forEach(entry -> result.add(new StatusInfo(entry.getPathname(), StatusInfo.Status.ADDED)));
                break;
            } else if (indexOfTemp == entriesOfTemp.size()) {
                IntStream.range(indexOfLocal, entriesOfLocal.size()).
                        mapToObj(entriesOfLocal::get)
                        .forEach(entry -> result.add(new StatusInfo(entry.getPathname(), StatusInfo.Status.DELETE)));
                break;
            }

            FileEntry entryOfLocal = entriesOfLocal.get(indexOfLocal);
            FileEntry entryOfTemp = entriesOfTemp.get(indexOfTemp);
            if (entryOfLocal.getPathname().compareTo(entryOfTemp.getPathname()) < 0) { //local space have, but temp space not.
                result.add(new StatusInfo(entryOfLocal.getPathname(), StatusInfo.Status.ADDED));
                indexOfLocal += 1;
            } else if (entryOfLocal.getPathname().compareTo(entryOfTemp.getPathname()) > 0) { //local space don't have, but temp space have.
                result.add(new StatusInfo(entryOfLocal.getPathname(), StatusInfo.Status.DELETE));
                indexOfTemp += 1;
            } else { //local space have, and temp space have too.
                if (!entryOfLocal.getFingerprint().equals(entryOfTemp.getFingerprint())) { //local space's fingerprint != temp space fingerprint.
                    result.add(new StatusInfo(entryOfLocal.getPathname(), StatusInfo.Status.MODIFITED));
                }
                indexOfLocal += 1;
                indexOfTemp += 1;
            }
        }

        return result;
    }

    //todo how to process the condition that the pathname is not in untracked status?
    String add(String pathname) {
        byte[] dataBytes = workSpace.search(pathname);
        if (dataBytes != null) {
            String fingerprint = tempSpace.search(pathname);
            if (fingerprint == null)
                tempSpace.add(pathname);
            else
                tempSpace.update(pathname);
        } else
            tempSpace.remove(pathname);
        return null;
    }

    String commit() {
        String head = tempSpace.build();
        localSpace.rebuild(head);
        return head;
    }

    void reset(String fingerprint) {
        tempSpace.loadToIndex(fingerprint);
        localSpace.rebuild(fingerprint);
    }

    void checkout() {
        List<JitObject> objects = tempSpace.load();
        workSpace.flush(objects);
    }

    String head() {
        return localSpace.head();
    }

    List<HeadLogInfo> log() {
        return null;
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
