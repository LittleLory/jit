package cn.littlelory;

/**
 * Created by littlelory on 2017/8/23.
 */
public class FieldSortRepeatBean {
    @JitField(sort = 1)
    private int f1;
    @JitField(sort = 1)
    private int f2;

    public int getF1() {
        return f1;
    }

    public void setF1(int f1) {
        this.f1 = f1;
    }

    public int getF2() {
        return f2;
    }

    public void setF2(int f2) {
        this.f2 = f2;
    }

}
