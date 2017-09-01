package cn.littlelory;

public class StatusInfo {
    private String path;
    private Status status;

    public StatusInfo(String path, Status status) {
        this.path = path;
        this.status = status;
    }

    public enum Status {
        UNTRACKED, MODIFITED, ADDED, DELETE;
    }
}
