package cn.littlelory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by littlelory on 15/09/2017.
 */
class BeanManager {
    private BeanSerializer serializer = BeanSerializer.getInstance();
    private JitBeanUtil jitBeanUtil = JitBeanUtil.getINSTANCE();
    private final String baseDirPath;

    BeanManager(String baseDirPath) {
        this.baseDirPath = baseDirPath;
    }

    <T> void save(T t) {
        Class<?> clz = t.getClass();
        checkClass(clz);
        String beanName = jitBeanUtil.getBeanName(clz);
        String beanDirPath = baseDirPath + "/" + beanName;
        FileUtil.mkdirIfNotExist(beanDirPath);
        String key = jitBeanUtil.getKey(t);
        String beanPath = beanDirPath + "/" + key;
        byte[] bytes = serializer.encode(t);
        FileUtil.writeBytes(beanPath, bytes);
    }

    <T> T delete(Class<T> clz, String key) throws BeanNotExistException {
        checkClass(clz);
        String beanName = jitBeanUtil.getBeanName(clz);
        String beanPath = beanPath(beanName, key);
        assertBeanExist(beanName, key);
        T bean = serializer.decode(clz, FileUtil.readBytes(beanPath));
        FileUtil.remove(beanPath);
        return bean;
    }

    <T> T get(Class<T> clz, String key) throws BeanNotExistException {
        checkClass(clz);
        String beanName = jitBeanUtil.getBeanName(clz);
        String beanPath = beanPath(beanName, key);
        assertBeanExist(beanName, key);
        return serializer.decode(clz, FileUtil.readBytes(beanPath));
    }

    <T> List<T> getList(Class<T> clz) {
        jitBeanUtil.check(clz);
        String beanName = jitBeanUtil.getBeanName(clz);
        List<String> fileNames = FileUtil.list(beanDirPath(beanName));
        return fileNames.stream()
                .map(fileName -> serializer.decode(clz, FileUtil.readBytes(beanPath(beanName, fileName))))
                .collect(Collectors.toList());
    }

    private void checkClass(Class<?> clz) {
        jitBeanUtil.check(clz);
    }

    private String beanDirPath(String beanName) {
        return baseDirPath + "/" + beanName;
    }

    private String beanPath(String beanName, String key) {
        return beanDirPath(beanName) + "/" + key;
    }

    private void assertBeanExist(String beanName, String key) throws BeanNotExistException {
        if (!FileUtil.exist(beanPath(beanName, key)))
            throw new BeanNotExistException("bean[" + beanName + "] have no entry with the key[" + key + "].");
    }
}
