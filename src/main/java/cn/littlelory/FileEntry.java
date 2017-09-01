package cn.littlelory;

import java.nio.ByteBuffer;

public class FileEntry implements Comparable {
    private String pathname;
    private String fingerprint;

//    FileEntry() {
//    }

    FileEntry(String pathname, String fingerprint) {
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

    static FileEntry decode(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        String fingerprint = new String(ByteBufferUtil.getBuffer(buffer));
        String pathname = new String(ByteBufferUtil.getBuffer(buffer));
        return new FileEntry(pathname, fingerprint);
    }

    @Override
    public int compareTo(Object o) {
        FileEntry another = (FileEntry) o;
        return this.pathname.compareTo(another.getPathname());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileEntry fileEntry = (FileEntry) o;

        if (!pathname.equals(fileEntry.pathname)) return false;
        return fingerprint.equals(fileEntry.fingerprint);
    }

    @Override
    public int hashCode() {
        int result = pathname.hashCode();
        result = 31 * result + fingerprint.hashCode();
        return result;
    }
}
