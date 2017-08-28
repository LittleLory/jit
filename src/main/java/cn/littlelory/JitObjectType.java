package cn.littlelory;

/**
 * Created by littlelory on 2017/8/28.
 */
public enum JitObjectType {
    TREE(1),
    NODE(2);

    private int id;

    JitObjectType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
