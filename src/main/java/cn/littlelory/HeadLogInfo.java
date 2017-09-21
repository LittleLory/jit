package cn.littlelory;

/**
 * Created by littlelory on 19/09/2017.
 */
public class HeadLogInfo {
    private final String oldHead;
    private final String newHead;
    private final long timestamp;

    public HeadLogInfo(String oldHead, String newHead, long timestamp) {
        this.oldHead = oldHead;
        this.newHead = newHead;
        this.timestamp = timestamp;
    }

    public HeadLogInfo(String logInfoStr) {
        String[] s = logInfoStr.split("\t");
        this.oldHead = s[0];
        this.newHead = s[1];
        this.timestamp = Long.parseLong(s[2]);
    }

    public String getOldHead() {
        return oldHead;
    }

    public String getNewHead() {
        return newHead;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return oldHead + "\t" + newHead + "\t" + timestamp;
    }
}
