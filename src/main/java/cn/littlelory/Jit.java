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

    public List<StatusInfo> status() {
        return blobManager.status();
    }

    public String add(String pathname) {
        return blobManager.add(pathname);
    }

    public String commit() {
        return blobManager.commit();
    }
}
