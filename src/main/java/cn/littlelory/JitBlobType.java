package cn.littlelory;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by littlelory on 2017/8/28.
 */
public enum JitBlobType {
    TREE(1, JitTree.class),
    OBJECT(2, JitObject.class);

    private int id;
    private Class<? extends JitBlob> clz;

    JitBlobType(int id, Class<? extends JitBlob> clz) {
        this.id = id;
        this.clz = clz;
    }

    public int getId() {
        return id;
    }

    public Class<?> getClz() {
        return clz;
    }

    public static JitBlobType getTypeById(int id) {
        Optional<JitBlobType> result = Arrays.stream(JitBlobType.values()).filter(type -> type.id == id).findFirst();
        if (!result.isPresent())
            throw new RuntimeException("typeId[" + id + "] is not exist.");
        return result.get();
    }
}
