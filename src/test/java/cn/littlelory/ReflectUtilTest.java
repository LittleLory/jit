package cn.littlelory;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by littlelory on 2017/8/23.
 */
public class ReflectUtilTest {
    private ReflectUtil reflectUtil = ReflectUtil.getINSTANCE();

    @Test
    public void get_fields_by_annotation_hit() {
        List<Field> fields = reflectUtil.getFieldsByAnnotation(NormalJitBean.class, JitKey.class);
        assertNotNull(fields);
        assertEquals(1, fields.size());

        Field field = fields.get(0);
        assertEquals("key", field.getName());
    }

    @Test
    public void get_fields_by_annotation_not_hit() {
        List<Field> fields = reflectUtil.getFieldsByAnnotation(NormalJitBean.class, Test.class);
        assertNotNull(fields);
        assertEquals(0, fields.size());
    }

    @Test
    public void class_have_target_annotation() {
        assertTrue(reflectUtil.hasTypeAnnotation(NormalJitBean.class, JitBean.class));
    }

    @Test
    public void class_has_no_target_annotation() {
        assertFalse(reflectUtil.hasTypeAnnotation(NormalJitBean.class, Test.class));
    }

    @Test
    public void get_type_annotation() {
        JitBean bean = reflectUtil.getTypeAnnotation(NormalJitBean.class, JitBean.class);
        assertNotNull(bean);
        assertEquals("NormalJitBean", bean.name());
    }
}
