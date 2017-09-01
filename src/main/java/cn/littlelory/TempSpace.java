package cn.littlelory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by littlelory on 31/08/2017.
 */
final class TempSpace {
    private final String indexPathname;
    private List<FileEntry> entries;

    List<FileEntry> list() {
        return entries;
    }

    Map<String, FileEntry> map() {
        return list().stream().collect(Collectors.toMap(FileEntry::getPathname, (fileEntry -> fileEntry)));
    }

    TempSpace(String indexPathname) {
        this.indexPathname = indexPathname;
        readIndex();
    }

    String search(String pathname) {
        return entries.stream().filter(entry -> entry.getPathname().equals(pathname)).map(FileEntry::getFingerprint).findFirst().orElse(null);
    }

    void add(String pathname, String fingerprint) {
        assertNotExist(pathname);
        entries.add(new FileEntry(pathname, fingerprint));
        Collections.sort(entries);
        writeIndex();
    }

    void update(String pathname, String fingerprint) {
        assertExist(pathname);
        Optional<FileEntry> optional = entries.stream().filter(entry -> entry.getPathname().equals(pathname)).findFirst();
        FileEntry target = optional.orElseThrow(() -> new NoSuchIndexException("no such index of path[" + pathname + "]."));
        entries.remove(target);
        entries.add(new FileEntry(pathname, fingerprint));
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
            int length = buffer.getInt();
            IntStream.range(0, length).forEach(i -> {
                FileEntry entry = FileEntry.decode(ByteBufferUtil.getBuffer(buffer));
                entries.add(i, entry);
            });
        }
    }

    private void writeIndex() {
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        buffer.putInt(entries.size());
        entries.stream().sorted().forEach(entry -> ByteBufferUtil.fillBuffer(buffer, entry.encode()));
        byte[] result = ByteBufferUtil.getAll(buffer);
        FileUtil.writeBytes(indexPathname, result);
    }
}
