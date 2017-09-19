package cn.littlelory;

import java.util.Objects;

/**
 * Created by littlelory on 2017/8/23.
 */
@JitBean(name = "NormalJitBean")
public class NormalJitBean {
    @JitKey
    private String key;
    @JitField(sort = 1)
    private int f1;
    @JitField(sort = 2)
    private long f2;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
        return "NormalJitBean{" +
                "key='" + key + '\'' +
                ", f1=" + f1 +
                ", f2=" + f2 +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NormalJitBean bean = (NormalJitBean) o;
        return f1 == bean.f1 &&
                f2 == bean.f2 &&
                Objects.equals(key, bean.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, f1, f2);
    }
}
