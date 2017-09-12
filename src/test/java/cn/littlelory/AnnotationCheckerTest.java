package cn.littlelory;

import org.junit.Test;

/**
 * Created by littlelory on 2017/8/23.
 */
public class AnnotationCheckerTest {
    private AnnotationChecker checker = AnnotationChecker.getINSTANCE();

    @Test(expected = NoBeanAnntationFoundException.class)
    public void have_no_bean_annotation() {
        checker.assertHaveBeanAnnotation(NoJitBeanAnnotationBean.class);
    }

    @Test
    public void have_bean_annotation() {
        checker.assertHaveBeanAnnotation(NormalJitBean.class);
    }

    @Test(expected = NoKeyFoundException.class)
    public void have_no_key_annotation() {
        checker.assertKeyAnnotationLegal(NoJitKeyBean.class);
    }

    @Test
    public void have_one_key_annotation() {
        checker.assertKeyAnnotationLegal(NormalJitBean.class);
    }

    @Test(expected = MultiKeyException.class)
    public void have_more_than_one_key_annotation() {
        checker.assertKeyAnnotationLegal(MultiKeyBean.class);
    }

    @Test(expected = MultiSortException.class)
    public void field_sort_repeat() {
        checker.assertFieldAnnotationLegal(FieldSortRepeatBean.class);
    }

    @Test
    public void no_jit_field() {
        checker.assertFieldAnnotationLegal(NormalJitBean.class);
    }
}
