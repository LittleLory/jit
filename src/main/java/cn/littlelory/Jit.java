package cn.littlelory;

import java.io.IOException;
import java.util.List;

/**
 * Created by littlelory on 2017/8/23.
 */
public class Jit {
    private final String jitBaseDirPath;
    private BlobManager blobManager;
    private BeanManager beanManager;

    public Jit(String jitBaseDirPath) {
        this.jitBaseDirPath = jitBaseDirPath;
        this.blobManager = new BlobManager(jitBaseDirPath);
        this.beanManager = new BeanManager(jitBaseDirPath);
    }

    public void init() throws IOException {
        blobManager.init();
    }

    public <T> void save(T t) {
        beanManager.save(t);
    }

    public <T> T delete(Class<T> clz, String key) throws BeanNotExistException {
        return beanManager.delete(clz, key);
    }

    public <T> T get(Class<T> clz, String key) {
        return beanManager.get(clz, key);
    }

    public <T> List<T> getList(Class<T> clz) {
        return beanManager.getList(clz);
    }

    public Status status() {
        return new Status(blobManager.tempStatus(), blobManager.localStatus());
    }

    public String add(String pathname) {
        return blobManager.add(pathname);
    }

    public String commit() {
        return blobManager.commit();
    }

    public void reset(String head) {
        blobManager.reset(head);
    }

    public void checkout() {
        blobManager.checkout();
    }

    public String head() {
        return blobManager.head();
    }

    public static final class Status {
        private final List<StatusInfo> added;
        private final List<StatusInfo> unAdd;

        public Status(List<StatusInfo> unAdd,List<StatusInfo> added) {
            this.added = added;
            this.unAdd = unAdd;
        }

        public List<StatusInfo> getAdded() {
            return added;
        }

        public List<StatusInfo> getUnAdd() {
            return unAdd;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("status not add:").append("\n");
            if (unAdd != null)
                unAdd.forEach(statusInfo -> builder.append(statusInfo.toString()).append("\n"));
            builder.append("status added:").append("\n");
            if (added != null)
                added.forEach(statusInfo -> builder.append(statusInfo.toString()).append("\n"));
            return builder.toString();
        }
    }
}
