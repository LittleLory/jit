package cn.littlelory;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by littlelory on 2017/8/28.
 */
public enum JitObjectType {
    TREE(1, JitTree.class),
    NODE(2, JitNode.class);

    private int id;
    private Class<? extends JitObject> clz;

    JitObjectType(int id, Class<? extends JitObject> clz) {
        this.id = id;
        this.clz = clz;
    }

    public int getId() {
        return id;
    }

    public Class<?> getClz() {
        return clz;
    }

    public static JitObjectType getTypeById(int id) {
        Optional<JitObjectType> result = Arrays.stream(JitObjectType.values()).filter(type -> type.id == id).findFirst();
        if (!result.isPresent())
            throw new RuntimeException("typeId[" + id + "] is not exist.");
        return result.get();
    }
}
