package cn.littlelory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by littlelory on 31/08/2017.
 */
final class Index {
    private final String indexPathname;
    private List<Entry> entries;

    Index(String indexPathname) {
        this.indexPathname = indexPathname;
        readIndex();
    }

    String search(String pathname) {
        return entries.stream().filter(entry -> entry.getPathname().equals(pathname)).map(Entry::getFingerprint).findFirst().orElse(null);
    }

    void add(String pathname, String fingerprint) {
        assertNotExist(pathname);
        entries.add(new Entry(pathname, fingerprint));
        Collections.sort(entries);
        writeIndex();
    }

    void update(String pathname, String fingerprint) {
        assertExist(pathname);
        Optional<Entry> optional = entries.stream().filter(entry -> entry.getPathname().equals(pathname)).findFirst();
        Entry target = optional.orElseThrow(() -> new NoSuchIndexException("no such index of path[" + pathname + "]."));
        entries.remove(target);
        entries.add(new Entry(pathname, fingerprint));
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
        try {
            if (FileUtil.exist(indexPathname))
                bytes = FileUtil.readBytes(indexPathname);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        entries = new ArrayList<>();
        if (bytes != null && bytes.length > 0) {
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            int length = buffer.getInt();
            IntStream.range(0, length).forEach(i -> {
                Entry entry = Entry.decode(ByteBufferUtil.getBuffer(buffer));
                entries.add(i, entry);
            });
        }
    }

    private void writeIndex() {
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        buffer.putInt(entries.size());
        entries.stream().sorted().forEach(entry -> ByteBufferUtil.fillBuffer(buffer, entry.encode()));
        byte[] result = ByteBufferUtil.getAll(buffer);
        try {
            FileUtil.writeBytes(indexPathname, result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final class Entry implements Comparable {
        private String pathname;
        private String fingerprint;

        private Entry() {
        }

        private Entry(String pathname, String fingerprint) {
            this.pathname = pathname;
            this.fingerprint = fingerprint;
        }

        String getPathname() {
            return pathname;
        }

        String getFingerprint() {
            return fingerprint;
        }

        byte[] encode() {
            ByteBuffer buffer = ByteBuffer.allocate(2048);
            ByteBufferUtil.fillBuffer(buffer, fingerprint.getBytes());
            ByteBufferUtil.fillBuffer(buffer, pathname.getBytes());
            return ByteBufferUtil.getAll(buffer);
        }

        static Entry decode(byte[] bytes) {
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            Entry entry = new Entry();
            entry.fingerprint = new String(ByteBufferUtil.getBuffer(buffer));
            entry.pathname = new String(ByteBufferUtil.getBuffer(buffer));
            return entry;
        }

        @Override
        public int compareTo(Object o) {
            Entry another = (Entry) o;
            return this.pathname.compareTo(another.getPathname());
        }
    }
}
