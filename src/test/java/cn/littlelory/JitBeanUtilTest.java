package cn.littlelory;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by littlelory on 2017/8/23.
 */
public class JitBeanUtilTest {
    private JitBeanUtil beanUtil = JitBeanUtil.getINSTANCE();

    @Test(expected = NoBeanAnntationFoundException.class)
    public void have_no_bean_annotation() {
        beanUtil.assertHaveBeanAnnotation(NoJitBeanAnnotationBean.class);
    }

    @Test
    public void have_bean_annotation() {
        beanUtil.assertHaveBeanAnnotation(NormalJitBean.class);
    }

    @Test(expected = NoKeyFoundException.class)
    public void have_no_key_annotation() {
        beanUtil.assertKeyAnnotationLegal(NoJitKeyBean.class);
    }

    @Test
    public void have_one_key_annotation() {
        beanUtil.assertKeyAnnotationLegal(NormalJitBean.class);
    }

    @Test(expected = MultiKeyException.class)
    public void have_more_than_one_key_annotation() {
        beanUtil.assertKeyAnnotationLegal(MultiKeyBean.class);
    }

    @Test(expected = MultiSortException.class)
    public void field_sort_repeat() {
        beanUtil.assertFieldAnnotationLegal(FieldSortRepeatBean.class);
    }

    @Test
    public void no_jit_field() {
        beanUtil.assertFieldAnnotationLegal(NormalJitBean.class);
    }

    @Test
    public void get_bean_name() {
        String actual = beanUtil.getBeanName(NormalJitBean.class);
        String expect = "NormalJitBean";
        assertEquals(expect, actual);
    }

    @Test
    public void no_bean_name() {
        String actual = beanUtil.getBeanName(NoJitBeanAnnotationBean.class);
        assertNull(actual);
    }

    @Test
    public void get_key() {
        String expect = "key";
        NormalJitBean bean = new NormalJitBean();
        bean.setKey("key");

        String actual = beanUtil.getKey(bean);
        assertEquals(expect, actual);
    }

    @Test
    public void get_key_but_null() {
        NormalJitBean bean = new NormalJitBean();
        String actual = beanUtil.getKey(bean);
        assertNull(actual);
    }
}
