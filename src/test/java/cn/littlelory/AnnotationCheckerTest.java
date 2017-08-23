package cn.littlelory;

import org.easymock.EasyMock;
import org.easymock.MockType;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by littlelory on 2017/8/23.
 */
public class AnnotationCheckerTest {
    private AnnotationChecker checker = AnnotationChecker.getINSTANCE();
    private ReflectUtil reflectUtil;

    @Before
    public void before() {
        reflectUtil = EasyMock.mock(MockType.NICE, ReflectUtil.class);
    }

    @Test(expected = NoBeanAnntationFoundException.class)
    public void have_no_bean_annotation() {
        EasyMock.expect(reflectUtil.hasAnnotation(NormalJitBean.class, JitBean.class)).andReturn(false);
        checker.setReflectUtil(reflectUtil);
        EasyMock.replay(reflectUtil);

        checker.assertHaveBeanAnnotation(NormalJitBean.class);
    }

    @Test
    public void have_bean_annotation() {
        EasyMock.expect(reflectUtil.hasAnnotation(NormalJitBean.class, JitBean.class)).andReturn(true);
        checker.setReflectUtil(reflectUtil);
        EasyMock.replay(reflectUtil);

        checker.assertHaveBeanAnnotation(NormalJitBean.class);
    }

    @Test(expected = NoKeyFoundException.class)
    public void have_no_key_annotation() {
        EasyMock.expect(reflectUtil.getFieldsByAnnotation(NormalJitBean.class, JitKey.class)).andReturn(Collections.emptyList());
        checker.setReflectUtil(reflectUtil);
        EasyMock.replay(reflectUtil);

        checker.assertKeyAnnotationLegal(NormalJitBean.class);
    }

    @Test
    public void have_one_key_annotation() {
        EasyMock.expect(reflectUtil.getFieldsByAnnotation(NormalJitBean.class, JitKey.class)).andReturn(Collections.singletonList(NormalJitBean.class.getDeclaredFields()[0]));
        checker.setReflectUtil(reflectUtil);
        EasyMock.replay(reflectUtil);

        checker.assertKeyAnnotationLegal(NormalJitBean.class);
    }

    @Test(expected = MultiKeyException.class)
    public void have_more_than_one_key_annotation() {
        EasyMock.expect(reflectUtil.getFieldsByAnnotation(NormalJitBean.class, JitKey.class)).andReturn(Arrays.asList(NormalJitBean.class.getDeclaredFields()));
        checker.setReflectUtil(reflectUtil);
        EasyMock.replay(reflectUtil);

        checker.assertKeyAnnotationLegal(NormalJitBean.class);
    }

    @Test(expected = MultiSortException.class)
    public void field_sort_repeat() {
        EasyMock.expect(reflectUtil.getFieldsByAnnotation(FieldSortRepeatBean.class, JitField.class)).andReturn(Arrays.asList(FieldSortRepeatBean.class.getDeclaredFields()));
        checker.setReflectUtil(reflectUtil);
        EasyMock.replay(reflectUtil);

        checker.assertFieldAnnotationLegal(FieldSortRepeatBean.class);
    }

    @Test
    public void no_jit_field() {
        EasyMock.expect(reflectUtil.getFieldsByAnnotation(NormalJitBean.class, JitField.class)).andReturn(Collections.emptyList());
        checker.setReflectUtil(reflectUtil);
        EasyMock.replay(reflectUtil);

        checker.assertFieldAnnotationLegal(NormalJitBean.class);
    }
}
