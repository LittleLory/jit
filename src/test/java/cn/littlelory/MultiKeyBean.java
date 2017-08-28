package cn.littlelory;

/**
 * Created by littlelory on 2017/8/28.
 */
@JitBean(name = "MultiKeyBean")
public class MultiKeyBean {
    @JitKey
    private String key1;
    @JitKey
    private String key2;
    @JitField(sort = 1)
    private int f1;
    @JitField(sort = 2)
    private long f2;

    public String getKey1() {
        return key1;
    }

    public void setKey1(String key1) {
        this.key1 = key1;
    }

    public String getKey2() {
        return key2;
    }

    public void setKey2(String key2) {
        this.key2 = key2;
    }

    public int getF1() {
        return f1;
    }

    public void setF1(int f1) {
        this.f1 = f1;
    }

    public long getF2() {
        return f2;
    }

    public void setF2(long f2) {
        this.f2 = f2;
    }

    @Override
    public String toString() {
        return "MultiKeyBean{" +
                "key1='" + key1 + '\'' +
                ", key2='" + key2 + '\'' +
                ", f1=" + f1 +
                ", f2=" + f2 +
                '}';
    }
}
