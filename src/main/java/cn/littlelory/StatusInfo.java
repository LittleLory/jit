package cn.littlelory;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatusInfo that = (StatusInfo) o;
        return Objects.equals(path, that.path) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, status);
    }
}
