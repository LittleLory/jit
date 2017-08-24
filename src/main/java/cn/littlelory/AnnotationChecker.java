package cn.littlelory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by littlelory on 2017/8/23.
 */
class AnnotationChecker {
    private static final AnnotationChecker INSTANCE = new AnnotationChecker();

    static AnnotationChecker getINSTANCE() {
        return INSTANCE;
    }

    private ReflectUtil reflectUtil = ReflectUtil.getINSTANCE();

    public void setReflectUtil(ReflectUtil reflectUtil) {
        this.reflectUtil = reflectUtil;
    }

    <T> void check(T t) {
        Class clz = t.getClass();
        assertHaveBeanAnnotation(clz);
        assertKeyAnnotationLegal(clz);
        assertFieldAnnotationLegal(clz);
    }

    void assertHaveBeanAnnotation(Class<?> clz) {
        if (!reflectUtil.hasTypeAnnotation(clz, JitBean.class))
            throw new NoBeanAnntationFoundException("not found the annotation[" + JitBean.class + "] from the Class[" + clz + "].");
    }

    void assertKeyAnnotationLegal(Class<?> clz) {
        List<Field> fields = reflectUtil.getFieldsByAnnotation(clz, JitKey.class);
        if (fields.size() == 0)
            throw new NoKeyFoundException("not found the field associated with JitKey in the Class[" + clz + "].");

        if (fields.size() > 1)
            throw new MultiKeyException("found multiple fields in the Class[" + clz + "].");
    }

    void assertFieldAnnotationLegal(Class<?> clz) {
        List<Field> fields = reflectUtil.getFieldsByAnnotation(clz, JitField.class);
        Map<Integer, Long> sortCountMap = fields.stream().map(field -> field.getDeclaredAnnotation(JitField.class)).filter(Objects::nonNull).collect(Collectors.groupingBy(JitField::sort, Collectors.counting()));
        List<Long> multiSorts = sortCountMap.entrySet().stream().filter(entry -> entry.getValue() > 1).map(Map.Entry::getValue).collect(Collectors.toList());
        if (multiSorts.size() > 0)
            throw new MultiSortException("found multiple field sort value in the Class[" + clz + "].");
    }
}
